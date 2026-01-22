-- V11__Smart_Quiz_System.sql
-- Smart Quiz System - User question tracking and exam attempts
-- Generated: 2026-01-16

-- ========================================
-- Users Table
-- ========================================

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ========================================
-- Quiz Attempts Table
-- ========================================

CREATE TABLE quiz_attempts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    quiz_type VARCHAR(50) DEFAULT 'PRACTICE',
    total_questions INT NOT NULL,
    correct_answers INT NOT NULL DEFAULT 0,
    wrong_answers INT NOT NULL DEFAULT 0,
    score_percentage DECIMAL(5,2) NOT NULL,
    passed BOOLEAN NOT NULL DEFAULT FALSE,
    time_taken_seconds INT,
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user (user_id),
    INDEX idx_completed (completed_at),
    INDEX idx_user_date (user_id, completed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ========================================
-- User Quiz Answers Table
-- ========================================

CREATE TABLE quiz_user_answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    attempt_id BIGINT NOT NULL,
    question_type ENUM('PRACTICE', 'EXAM') NOT NULL,
    question_ref_id BIGINT NOT NULL,
    selected_option INT NOT NULL,
    is_correct BOOLEAN NOT NULL,
    time_taken_seconds INT,
    answered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (attempt_id) REFERENCES quiz_attempts(id) ON DELETE CASCADE,
    INDEX idx_attempt (attempt_id),
    INDEX idx_question_ref (question_type, question_ref_id),
    INDEX idx_user_question (attempt_id, question_type, question_ref_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ========================================
-- User Question History - Prevent repetition within 24 hours
-- ========================================

CREATE TABLE user_question_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    last_shown_type ENUM('PRACTICE', 'EXAM') NOT NULL,
    question_ref_id BIGINT NOT NULL,
    last_shown_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    times_shown INT NOT NULL DEFAULT 1,
    times_correct INT NOT NULL DEFAULT 0,
    times_wrong INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_question (user_id, last_shown_type, question_ref_id),
    INDEX idx_user_recent (user_id, last_shown_at),
    INDEX idx_question_stats (last_shown_type, question_ref_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ========================================
-- Error Pattern Analysis Table
-- ========================================

CREATE TABLE user_error_patterns (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    error_type ENUM(
        'SIGN_CONFUSION',
        'SUPPLEMENTARY_IGNORED',
        'PRIORITY_MISUNDERSTANDING',
        'SPEED_LIMIT_ERROR',
        'ZONE_CONFUSION',
        'RULE_OVERGENERALIZATION',
        'OTHER'
    ) NOT NULL,
    question_type ENUM('PRACTICE', 'EXAM') NOT NULL,
    question_ref_id BIGINT NOT NULL,
    traffic_sign_id BIGINT,
    category_id BIGINT,
    occurred_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (traffic_sign_id) REFERENCES traffic_signs(id) ON DELETE SET NULL,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    INDEX idx_user_errors (user_id, error_type),
    INDEX idx_user_date (user_id, occurred_at),
    INDEX idx_error_type (error_type),
    INDEX idx_question_ref (question_type, question_ref_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ========================================
-- User Weak Areas Table
-- ========================================

CREATE TABLE user_weak_areas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category_id BIGINT,
    traffic_sign_id BIGINT,
    total_questions INT NOT NULL DEFAULT 0,
    correct_answers INT NOT NULL DEFAULT 0,
    wrong_answers INT NOT NULL DEFAULT 0,
    accuracy_percentage DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    FOREIGN KEY (traffic_sign_id) REFERENCES traffic_signs(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_category (user_id, category_id),
    INDEX idx_user_weak (user_id, accuracy_percentage),
    INDEX idx_category_stats (category_id, accuracy_percentage)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ========================================
-- Add smart context fields to exam_questions
-- ========================================

ALTER TABLE exam_questions
    ADD COLUMN typical_error_type ENUM(
        'SIGN_CONFUSION',
        'SUPPLEMENTARY_IGNORED',
        'PRIORITY_MISUNDERSTANDING',
        'SPEED_LIMIT_ERROR',
        'ZONE_CONFUSION',
        'RULE_OVERGENERALIZATION',
        'OTHER'
    ) AFTER explanation_fr,
    ADD COLUMN context_specific BOOLEAN DEFAULT TRUE AFTER typical_error_type;

-- ========================================
-- Add smart context fields to practice_questions
-- ========================================

ALTER TABLE practice_questions
    ADD COLUMN typical_error_type ENUM(
        'SIGN_CONFUSION',
        'SUPPLEMENTARY_IGNORED',
        'PRIORITY_MISUNDERSTANDING',
        'SPEED_LIMIT_ERROR',
        'ZONE_CONFUSION',
        'RULE_OVERGENERALIZATION',
        'OTHER'
    ) AFTER explanation_fr,
    ADD COLUMN context_specific BOOLEAN DEFAULT TRUE AFTER typical_error_type;
