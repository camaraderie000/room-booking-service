CREATE TABLE room_schedules (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid (),
    room_id uuid NOT NULL,
    day_of_week integer NOT NULL,
    start_minute integer NOT NULL,
    end_minute integer NOT NULL,
    CONSTRAINT room_schedules_room_fk FOREIGN KEY (room_id) REFERENCES rooms (id) ON DELETE CASCADE,
    CONSTRAINT room_schedules_dow_check CHECK (day_of_week BETWEEN 1 AND 7),
    CONSTRAINT room_schedules_minutes_range CHECK (
        start_minute >= 0
        AND end_minute <= 1440
        AND start_minute < end_minute
    ),
    CONSTRAINT room_schedules_slot_aligned CHECK (
        start_minute % 30 = 0
        AND end_minute % 30 = 0
    ),
    CONSTRAINT room_schedules_no_overlap EXCLUDE USING gist (
        room_id
        WITH
            =,
            day_of_week
        WITH
            =,
            int4range (start_minute, end_minute)
        WITH
            &&
    )
);