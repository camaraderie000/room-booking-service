CREATE TABLE slots (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid (),
    room_id uuid NOT NULL,
    start_at timestamptz NOT NULL,
    end_at timestamptz NOT NULL,
    CONSTRAINT slots_room_fk FOREIGN KEY (room_id) REFERENCES rooms (id) ON DELETE CASCADE,
    CONSTRAINT slots_room_start_uq UNIQUE (room_id, start_at),
    CONSTRAINT slots_duration_30m CHECK (
        end_at = start_at + interval '30 minutes'
    )
);

CREATE INDEX slots_start_idx ON slots (start_at);