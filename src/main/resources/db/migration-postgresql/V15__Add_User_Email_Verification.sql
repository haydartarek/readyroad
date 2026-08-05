ALTER TABLE users
    ADD COLUMN email_verified BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE users
SET email_verified = TRUE
WHERE username = 'admin'
   OR EXISTS (
        SELECT 1
        FROM auth_identities identity
        WHERE identity.user_id = users.id
          AND identity.email_verified = TRUE
   );
