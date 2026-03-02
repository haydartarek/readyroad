-- ─────────────────────────────────────────────────────────────────────────────
-- V101 — Sign Practice Sessions
--
-- 1. Adds traffic_sign_code VARCHAR(50) to user_weak_areas
--    (V11 uses category_id/traffic_sign_id FKs; Sign Quiz native queries
--     need a plain VARCHAR code column for ON DUPLICATE KEY upserts.)
-- 2. Creates sign_practice_sessions  — stateful practice session per sign per user
-- 3. Creates sign_practice_answers   — one row per question answered in a session
-- ─────────────────────────────────────────────────────────────────────────────

-- ── Add traffic_sign_code for Sign Quiz tracking ──────────────────────────────
-- Adds the column (no IF NOT EXISTS — column confirmed absent before this migration).
-- Also adds a UNIQUE KEY so ON DUPLICATE KEY UPDATE works correctly.
ALTER TABLE user_weak_areas
    ADD COLUMN traffic_sign_code VARCHAR(50) NULL,
    ADD UNIQUE KEY uk_uwa_user_sign_code (user_id, traffic_sign_code);

-- ── sign_practice_sessions ────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS sign_practice_sessions
(
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    user_id         BIGINT       NOT NULL,
    sign_id         BIGINT       NOT NULL,
    sign_code       VARCHAR(50)  NOT NULL,
    status          ENUM ('IN_PROGRESS', 'COMPLETED') NOT NULL DEFAULT 'IN_PROGRESS',
    total_questions INT          NOT NULL DEFAULT 0,
    correct_count   INT          NOT NULL DEFAULT 0,
    started_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at    TIMESTAMP    NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_sps_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_sps_sign FOREIGN KEY (sign_id) REFERENCES road_signs (id) ON DELETE CASCADE,
    INDEX idx_sps_user_sign (user_id, sign_id),
    INDEX idx_sps_user_status (user_id, status)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- ── sign_practice_answers ─────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS sign_practice_answers
(
    id                 BIGINT  NOT NULL AUTO_INCREMENT,
    session_id         BIGINT  NOT NULL,
    question_id        BIGINT  NOT NULL,
    choice_id          BIGINT  NOT NULL,
    is_correct         BOOLEAN NOT NULL DEFAULT FALSE,
    time_taken_seconds INT     NULL,
    answered_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT fk_spa_session  FOREIGN KEY (session_id)  REFERENCES sign_practice_sessions (id) ON DELETE CASCADE,
    CONSTRAINT fk_spa_question FOREIGN KEY (question_id) REFERENCES sign_questions (id),
    CONSTRAINT fk_spa_choice   FOREIGN KEY (choice_id)   REFERENCES sign_choices (id),
    UNIQUE KEY uk_spa_session_question (session_id, question_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
