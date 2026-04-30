ALTER TABLE auth_identities
    ADD CONSTRAINT uk_auth_identities_user_provider UNIQUE (user_id, provider);
