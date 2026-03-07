-- =============================================================================
-- V106__Sign_Exam_Results.sql
-- =============================================================================
-- Creates the sign_exam_results table to track:
--   - which user completed which exam (1 or 2) for which sign
--   - score, pass/fail, timestamp
--
-- Business rule enforced at application level:
--   Exam 2 is LOCKED until the user has at least one completed Exam 1 record
--   for the same sign.  This table is the source of truth for that check.
-- =============================================================================

CREATE TABLE IF NOT EXISTS sign_exam_results (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    user_id         BIGINT          NOT NULL,
    sign_id         BIGINT          NOT NULL,
    sign_code       VARCHAR(50)     NOT NULL,
    exam_number     TINYINT(1)      NOT NULL COMMENT '1 or 2',
    total_questions INT             NOT NULL DEFAULT 0,
    answered_count  INT             NOT NULL DEFAULT 0,
    correct_count   INT             NOT NULL DEFAULT 0,
    required_to_pass INT            NOT NULL DEFAULT 0,
    score_pct       DECIMAL(5,2)    NOT NULL DEFAULT 0.00,
    passed          TINYINT(1)      NOT NULL DEFAULT 0,
    completed_at    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT fk_ser_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_ser_sign FOREIGN KEY (sign_id) REFERENCES road_signs (id) ON DELETE CASCADE,
    INDEX idx_ser_user_sign       (user_id, sign_id),
    INDEX idx_ser_user_sign_exam  (user_id, sign_id, exam_number),
    INDEX idx_ser_passed          (passed),
    INDEX idx_ser_completed_at    (completed_at DESC)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Records every sign exam submission per user. Used to enforce Exam 2 lock until Exam 1 is completed.';
