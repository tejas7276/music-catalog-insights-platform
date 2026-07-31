-- Optional sample data for local development / demos.
-- Password for demo@example.com is: Password123 (bcrypt hash below)

INSERT INTO users (name, email, password, created_at, updated_at)
VALUES ('Demo User', 'demo@example.com',
        '$2a$12$CwTycUXWue0Thq9StjUM0uJ8Q9WoxfNXVFvW8U0AJXcxAWdEHY0Iu',
        now(), now())
ON CONFLICT (email) DO NOTHING;

INSERT INTO saved_songs (apple_catalog_id, title, artist_name, genre, release_date,
                          duration_millis, artwork_url, user_rating, user_notes,
                          created_at, updated_at, user_id)
SELECT 1440806041, 'Yellow', 'Coldplay', 'Alternative', '2000-07-10',
       267000, 'https://is1-ssl.mzstatic.com/image/thumb/example/100x100bb.jpg', 5,
       'One of my all-time favorites', now(), now(), u.id
FROM users u WHERE u.email = 'demo@example.com'
ON CONFLICT DO NOTHING;

INSERT INTO saved_songs (apple_catalog_id, title, artist_name, genre, release_date,
                          duration_millis, artwork_url, user_rating, user_notes,
                          created_at, updated_at, user_id)
SELECT 1193701392, 'Shape of You', 'Ed Sheeran', 'Pop', '2017-01-06',
       233000, 'https://is1-ssl.mzstatic.com/image/thumb/example/100x100bb.jpg', 4,
       NULL, now(), now(), u.id
FROM users u WHERE u.email = 'demo@example.com'
ON CONFLICT DO NOTHING;
