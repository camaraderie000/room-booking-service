CREATE TABLE users (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid (),
    email varchar(254) NOT NULL,
    password_hash varchar(100) NOT NULL,
    role varchar(10) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT users_email_uq UNIQUE (email),
    CONSTRAINT users_role_check CHECK (role IN ('ADMIN', 'USER')),
    CONSTRAINT users_email_format CHECK (
        email = lower(email)
        AND position('@' IN email) > 1
    )
);