CREATE TABLE rooms (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid (),
    name varchar(100) NOT NULL,
    capacity integer NOT NULL,
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT rooms_name_uq UNIQUE (name),
    CONSTRAINT rooms_name_not_blank CHECK (length(btrim(name)) > 0),
    CONSTRAINT rooms_capacity_range CHECK (capacity BETWEEN 1 AND 500)
);