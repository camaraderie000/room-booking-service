CREATE TABLE booking_notifications (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid (),
    booking_id uuid NOT NULL,
    user_id uuid NOT NULL,
    kind varchar(20) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    sent_at timestamptz,
    CONSTRAINT booking_notifications_booking_fk FOREIGN KEY (booking_id) REFERENCES bookings (id) ON DELETE CASCADE,
    CONSTRAINT booking_notifications_kind_check CHECK (kind IN ('REMINDER_5M')),
    CONSTRAINT booking_notifications_once_uq UNIQUE (booking_id, kind)
);