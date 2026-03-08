-- ============================================================
-- V111: Password Reset Tokens Table
-- ============================================================
-- Stores short-lived tokens used for the forgot-password flow.
-- Each token is a UUID, expires in 30 minutes, and is single-use.
-- ============================================================

CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    token        VARCHAR(36)  NOT NULL UNIQUE,
    user_id      BIGINT       NOT NULL,
    expires_at   DATETIME     NOT NULL,
    used         BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_prt_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_prt_token   ON password_reset_tokens (token);
CREATE INDEX idx_prt_user_id ON password_reset_tokens (user_id);
