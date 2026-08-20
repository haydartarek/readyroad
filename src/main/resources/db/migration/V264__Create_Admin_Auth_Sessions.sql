-- Keep the local MySQL schema aligned with the existing PostgreSQL
-- admin-session persistence used for revocable ADMIN JWTs.

CREATE TABLE admin_auth_sessions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    session_id BINARY(16) NOT NULL,
    user_id BIGINT NOT NULL,
    expires_at DATETIME(6) NOT NULL,
    revoked_at DATETIME(6) NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uq_admin_auth_session_id UNIQUE (session_id),
    CONSTRAINT fk_admin_auth_session_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT chk_admin_auth_session_expiry CHECK (expires_at > created_at),
    INDEX idx_admin_auth_sessions_user (user_id, expires_at DESC),
    INDEX idx_admin_auth_sessions_active (session_id, revoked_at, expires_at)
);
