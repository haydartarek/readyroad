-- V37: Create User Category Progress Table
-- Phase 5: Story B1, B2, B3 - Progress tracking per category
-- Date: 2026-01-20

CREATE TABLE user_category_progress (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    questions_attempted INT NOT NULL DEFAULT 0,
    correct_answers INT NOT NULL DEFAULT 0,
    accuracy_rate DECIMAL(5,2) NULL,
    last_practiced TIMESTAMP NULL,
    mastery_level VARCHAR(20) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_ucp_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ucp_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    CONSTRAINT chk_ucp_attempted CHECK (questions_attempted >= 0),
    CONSTRAINT chk_ucp_correct CHECK (correct_answers >= 0 AND correct_answers <= questions_attempted),
    CONSTRAINT chk_ucp_accuracy CHECK (accuracy_rate IS NULL OR (accuracy_rate >= 0 AND accuracy_rate <= 100)),
    CONSTRAINT chk_ucp_mastery CHECK (mastery_level IS NULL OR mastery_level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED')),
    CONSTRAINT uq_user_category UNIQUE (user_id, category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Optimize progress queries
CREATE INDEX idx_user_cat_accuracy ON user_category_progress(user_id, accuracy_rate ASC);
CREATE INDEX idx_user_cat_mastery ON user_category_progress(user_id, mastery_level);
CREATE INDEX idx_user_cat_practiced ON user_category_progress(user_id, last_practiced DESC);
CREATE INDEX idx_category_users ON user_category_progress(category_id, accuracy_rate DESC);
