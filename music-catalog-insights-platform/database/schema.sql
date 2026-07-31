-- Music Catalog Insights Platform - PostgreSQL schema
CREATE TABLE IF NOT EXISTS users (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(120)  NOT NULL,
    email           VARCHAR(180)  NOT NULL,
    password        VARCHAR(255)  NOT NULL,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS saved_songs (
    id                BIGSERIAL PRIMARY KEY,
    apple_catalog_id  BIGINT        NOT NULL,
    title             VARCHAR(255)  NOT NULL,
    artist_name       VARCHAR(255)  NOT NULL,
    genre             VARCHAR(120),
    release_date      DATE,
    duration_millis   INTEGER,
    artwork_url       VARCHAR(500),
    user_rating       INTEGER       CHECK (user_rating BETWEEN 1 AND 5),
    user_notes        VARCHAR(2000),
    created_at        TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ   NOT NULL DEFAULT now(),
    user_id           BIGINT        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uk_user_apple_catalog_id UNIQUE (user_id, apple_catalog_id)
);

CREATE INDEX IF NOT EXISTS idx_saved_songs_user_id ON saved_songs(user_id);
CREATE INDEX IF NOT EXISTS idx_saved_songs_genre ON saved_songs(genre);
CREATE INDEX IF NOT EXISTS idx_saved_songs_artist_name ON saved_songs(artist_name);
CREATE INDEX IF NOT EXISTS idx_saved_songs_created_at ON saved_songs(created_at);
