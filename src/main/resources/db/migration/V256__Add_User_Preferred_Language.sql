ALTER TABLE users
    ADD COLUMN preferred_language VARCHAR(2) NULL;

ALTER TABLE users
    ADD CONSTRAINT chk_users_preferred_language
        CHECK (preferred_language IS NULL OR preferred_language IN ('en', 'nl', 'fr', 'ar'));
