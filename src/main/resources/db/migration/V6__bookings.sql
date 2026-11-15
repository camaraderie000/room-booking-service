CREATE TABLE bookings (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid (),
    room_id uuid NOT NULL,
    user_id uuid NOT NULL,
    start_at timestamptz NOT NULL,
    end_at timestamptz NOT NULL,
    status varchar(10) NOT NULL DEFAULT 'ACTIVE',
    conference_url varchar(500) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    cancelled_at timestamptz,
    CONSTRAINT bookings_room_fk FOREIGN KEY (room_id) REFERENCES rooms (id),
    CONSTRAINT bookings_user_fk FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT bookings_status_check CHECK (
        status IN ('ACTIVE', 'CANCELLED')
    ),
    CONSTRAINT bookings_time_order CHECK (end_at > start_at),
    CONSTRAINT bookings_aligned_30m CHECK (
        mod(
            extract(
                epoch
                FROM start_at
            )::bigint,
            1800
        ) = 0
        AND mod(
            extract(
                epoch
                FROM end_at
            )::bigint,
            1800
        ) = 0
    ),
    CONSTRAINT bookings_max_duration CHECK (
        end_at - start_at <= interval '4 hours'
    ),
    CONSTRAINT bookings_cancel_consistency CHECK (
        (status = 'CANCELLED') = (cancelled_at IS NOT NULL)
    ),
    CONSTRAINT bookings_cancel_before_start CHECK (
        cancelled_at IS NULL
        OR cancelled_at <= start_at
    ),
    CONSTRAINT bookings_no_overlap EXCLUDE USING gist (
        room_id
        WITH
            =,
            tstzrange (start_at, end_at, '[)')
        WITH
            &&
    )
    WHERE (status = 'ACTIVE')
);

CREATE INDEX bookings_user_idx ON bookings (user_id, start_at DESC);

CREATE INDEX bookings_reminder_idx ON bookings (start_at)
WHERE
    status = 'ACTIVE';

CREATE FUNCTION bookings_validate_insert() RETURNS trigger LANGUAGE plpgsql AS $$
DECLARE
    expected integer;
    actual   integer;
BEGIN
    IF NEW.start_at <= now() THEN
        RAISE EXCEPTION 'booking must start in the future'
            USING ERRCODE = 'check_violation', CONSTRAINT = 'bookings_must_be_future';
    END IF;

    expected := (extract(epoch FROM (NEW.end_at - NEW.start_at)) / 1800)::integer;
    SELECT count(*) INTO actual FROM slots
     WHERE room_id = NEW.room_id AND start_at >= NEW.start_at AND start_at < NEW.end_at;

    IF actual <> expected THEN
        RAISE EXCEPTION 'booking must be fully covered by available slots'
            USING ERRCODE = 'check_violation', CONSTRAINT = 'bookings_within_slots';
    END IF;
    RETURN NEW;
END $$;

CREATE TRIGGER bookings_validate_insert_trg
    BEFORE INSERT ON bookings
    FOR EACH ROW EXECUTE FUNCTION bookings_validate_insert();

CREATE FUNCTION bookings_guard_update() RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
    IF NEW.room_id <> OLD.room_id OR NEW.user_id <> OLD.user_id
       OR NEW.start_at <> OLD.start_at OR NEW.end_at <> OLD.end_at THEN
        RAISE EXCEPTION 'booking time, room and owner are immutable'
            USING ERRCODE = 'check_violation', CONSTRAINT = 'bookings_immutable_fields';
    END IF;
    IF OLD.status = 'CANCELLED' AND NEW.status <> 'CANCELLED' THEN
        RAISE EXCEPTION 'cancelled booking cannot be reactivated'
            USING ERRCODE = 'check_violation', CONSTRAINT = 'bookings_no_reactivation';
    END IF;
    RETURN NEW;
END $$;

CREATE TRIGGER bookings_guard_update_trg
    BEFORE UPDATE ON bookings
    FOR EACH ROW EXECUTE FUNCTION bookings_guard_update();