-- =============================================================================
-- V200__Create_Sign_Random_Practice_System.sql
-- =============================================================================
-- Persists the mixed random traffic-sign exam shown at /practice/random.
--
-- Rules implemented by the application layer:
--   - 50 questions per session
--   - 20 EASY + 20 MEDIUM + 10 HARD
--   - questions are sourced only from sign_questions / sign_choices
--   - once a question is shown to a user, it cannot be assigned again in
--     another random session for 24 hours
-- =============================================================================

CREATE TABLE IF NOT EXISTS sign_random_practice_sessions (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    user_id          BIGINT       NOT NULL,
    total_questions  INT          NOT NULL DEFAULT 50,
    answered_count   INT          NOT NULL DEFAULT 0,
    correct_count    INT          NOT NULL DEFAULT 0,
    passing_score    INT          NOT NULL DEFAULT 41,
    score_pct        DECIMAL(5,2) NULL,
    passed           TINYINT(1)   NULL,
    status           VARCHAR(20)  NOT NULL DEFAULT 'IN_PROGRESS',
    started_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at       TIMESTAMP    NOT NULL,
    completed_at     TIMESTAMP    NULL,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT fk_srps_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_srps_user_status    (user_id, status),
    INDEX idx_srps_user_started   (user_id, started_at DESC),
    INDEX idx_srps_status_started (status, started_at DESC),
    INDEX idx_srps_completed_at   (completed_at DESC)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Persistent mixed traffic-sign random exam sessions for /practice/random.';

CREATE TABLE IF NOT EXISTS sign_random_practice_questions (
    id                 BIGINT      NOT NULL AUTO_INCREMENT,
    session_id         BIGINT      NOT NULL,
    question_id        BIGINT      NOT NULL,
    question_order     INT         NOT NULL,
    selected_choice_id BIGINT      NULL,
    is_correct         TINYINT(1)  NULL,
    was_timeout        TINYINT(1)  NOT NULL DEFAULT 0,
    answered_at        TIMESTAMP   NULL,
    created_at         TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT fk_srpq_session  FOREIGN KEY (session_id) REFERENCES sign_random_practice_sessions (id) ON DELETE CASCADE,
    CONSTRAINT fk_srpq_question FOREIGN KEY (question_id) REFERENCES sign_questions (id) ON DELETE CASCADE,
    CONSTRAINT fk_srpq_choice   FOREIGN KEY (selected_choice_id) REFERENCES sign_choices (id) ON DELETE SET NULL,
    UNIQUE KEY uq_srpq_session_order    (session_id, question_order),
    UNIQUE KEY uq_srpq_session_question (session_id, question_id),
    INDEX idx_srpq_session   (session_id),
    INDEX idx_srpq_question  (question_id),
    INDEX idx_srpq_answered  (answered_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Assigned questions and persisted answers for /practice/random sessions.';
