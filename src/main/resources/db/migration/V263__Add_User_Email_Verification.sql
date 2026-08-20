ALTER TABLE users
    ADD COLUMN email_verified BIT(1) NOT NULL DEFAULT b'0';

UPDATE users
SET email_verified = b'1'
WHERE username = 'admin'
   OR EXISTS (
        SELECT 1
        FROM auth_identities identity
        WHERE identity.user_id = users.id
          AND identity.email_verified = b'1'
   );
