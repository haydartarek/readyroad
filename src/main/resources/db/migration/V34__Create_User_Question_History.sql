-- V34: Create user_question_history for 24h cooldown (Phase 3)
-- Created: January 18, 2026
-- Purpose: Track when users see questions to prevent repetition within 24 hours
-- Design: Generic table name (content-agnostic), optimized indexes

-- Create user_question_history table
CREATE TABLE IF NOT EXISTS user_question_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    answered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign keys
    CONSTRAINT fk_user_question_history_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_question_history_question
        FOREIGN KEY (question_id) REFERENCES quiz_questions(id) ON DELETE CASCADE,

    -- Indexes for performance
    INDEX idx_user_question_history_user_answered (user_id, answered_at),
    INDEX idx_user_question_history_question_answered (question_id, answered_at),
    INDEX idx_user_question_history_lookup (user_id, question_id, answered_at)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Migration complete
