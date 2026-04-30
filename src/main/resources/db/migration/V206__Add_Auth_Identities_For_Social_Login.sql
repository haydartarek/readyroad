CREATE TABLE auth_identities (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    user_id BIGINT NOT NULL,
    provider VARCHAR(20) NOT NULL,
    provider_user_id VARCHAR(191) NOT NULL,
    provider_email VARCHAR(100) NOT NULL,
    email_verified BIT(1) NOT NULL DEFAULT b'0',
    PRIMARY KEY (id),
    CONSTRAINT uk_auth_identities_provider_user UNIQUE (provider, provider_user_id),
    CONSTRAINT fk_auth_identities_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_auth_identities_user_provider ON auth_identities (user_id, provider);
CREATE INDEX idx_auth_identities_provider_email ON auth_identities (provider, provider_email);
