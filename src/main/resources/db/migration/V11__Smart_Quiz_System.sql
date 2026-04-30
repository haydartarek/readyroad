-- V11__Smart_Quiz_System.sql
-- Smart Quiz System - User question tracking and exam attempts
-- Fixed: FKs, safe ALTERs, DECIMAL(4,2), USE statement, question_ref_id mapping
-- Generated: 2026-02-27 (Fixed version)

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- Quiz Attempts Table
-- ========================================
CREATE TABLE IF NOT EXISTS quiz_attempts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    quiz_type ENUM('PRACTICE', 'EXAM') NOT NULL DEFAULT 'PRACTICE',
    total_questions INT NOT NULL,
    correct_answers INT NOT NULL DEFAULT 0,
    wrong_answers INT NOT NULL DEFAULT 0,
    score_percentage DECIMAL(4,2) NOT NULL, -- 99.99% max
    passed BOOLEAN NOT NULL DEFAULT FALSE,
    time_taken_seconds INT,
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user (user_id),
    INDEX idx_completed (completed_at),
    INDEX idx_user_date (user_id, completed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- User Quiz Answers Table
-- ========================================
CREATE TABLE IF NOT EXISTS quiz_user_answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    attempt_id BIGINT NOT NULL,
    question_type ENUM('RULE', 'SIGN') NOT NULL, -- RULE=traffic_rules.id, SIGN=traffic_signs.sign_code
    question_ref_id BIGINT NOT NULL, -- traffic_rules.id OR traffic_signs.id
    selected_option INT NOT NULL,
    is_correct BOOLEAN NOT NULL,
    time_taken_seconds INT,
    answered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (attempt_id) REFERENCES quiz_attempts(id) ON DELETE CASCADE,
    INDEX idx_attempt (attempt_id),
    INDEX idx_question (question_type, question_ref_id),
    INDEX idx_user_question (attempt_id, question_type, question_ref_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- User Question History - Prevent repetition within 24h
-- ========================================
CREATE TABLE IF NOT EXISTS user_question_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    question_type ENUM('RULE', 'SIGN') NOT NULL,
    question_ref_id BIGINT NOT NULL,
    last_shown_type ENUM('PRACTICE', 'EXAM') NOT NULL,
    last_shown_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    times_shown INT NOT NULL DEFAULT 1,
    times_correct INT NOT NULL DEFAULT 0,
    times_wrong INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_question (user_id, question_type, question_ref_id, last_shown_type),
    INDEX idx_user_recent (user_id, last_shown_at),
    INDEX idx_question_stats (question_type, question_ref_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- Error Pattern Analysis Table (Fixed FKs)
-- ========================================
CREATE TABLE IF NOT EXISTS user_error_patterns (
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
    question_ref_type ENUM('RULE', 'SIGN') NOT NULL,
    question_ref_id BIGINT NOT NULL,
    traffic_sign_code VARCHAR(10), -- sign_code instead of id
    rule_category VARCHAR(50),     -- traffic_rules.category instead of categories.id
    occurred_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_errors (user_id, error_type),
    INDEX idx_user_date (user_id, occurred_at),
    INDEX idx_error_type (error_type),
    INDEX idx_question_ref (question_type, question_ref_type, question_ref_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- User Weak Areas Table (Fixed FKs)
-- ========================================
CREATE TABLE IF NOT EXISTS user_weak_areas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category VARCHAR(50),           -- traffic_rules.category OR 'SIGNS_*'
    traffic_sign_code VARCHAR(10),  -- traffic_signs.sign_code
    total_questions INT NOT NULL DEFAULT 0,
    correct_answers INT NOT NULL DEFAULT 0,
    wrong_answers INT NOT NULL DEFAULT 0,
    accuracy_percentage DECIMAL(4,2) NOT NULL DEFAULT 0.00,
    last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_category (user_id, category),
    UNIQUE KEY uk_user_sign (user_id, traffic_sign_code),
    INDEX idx_user_weak (user_id, accuracy_percentage),
    INDEX idx_category_stats (category, accuracy_percentage)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- Safe ALTERs for existing questions tables
-- ========================================
-- exam_questions
SET @sql = '
ALTER TABLE exam_questions 
    ADD COLUMN typical_error_type ENUM(
        ''SIGN_CONFUSION'',
        ''SUPPLEMENTARY_IGNORED'', 
        ''PRIORITY_MISUNDERSTANDING'',
        ''SPEED_LIMIT_ERROR'',
        ''ZONE_CONFUSION'',
        ''RULE_OVERGENERALIZATION'',
        ''OTHER''
    ) AFTER explanation_fr,
    ADD COLUMN context_specific BOOLEAN DEFAULT TRUE AFTER typical_error_type;
';
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- practice_questions  
SET @sql = '
ALTER TABLE practice_questions 
    ADD COLUMN typical_error_type ENUM(
        ''SIGN_CONFUSION'',
        ''SUPPLEMENTARY_IGNORED'',
        ''PRIORITY_MISUNDERSTANDING'', 
        ''SPEED_LIMIT_ERROR'',
        ''ZONE_CONFUSION'',
        ''RULE_OVERGENERALIZATION'',
        ''OTHER''
    ) AFTER explanation_fr,
    ADD COLUMN context_specific BOOLEAN DEFAULT TRUE AFTER typical_error_type;
';
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ========================================
-- Performance indexes for existing tables
-- ========================================
-- Note: idx_exam_sign_code and idx_practice_sign_code removed (sign_code column
-- does not exist in those tables at this migration version; added later)
CREATE INDEX idx_traffic_rules_category ON traffic_rules(category_id);

-- ========================================
-- Default admin user
-- ========================================
-- Default admin creation is handled by DefaultAdminInitializer using
-- ADMIN_DEFAULT_PASSWORD / readyroad.admin.default-password.
-- Do not seed production credentials from Flyway.

-- ========================================
-- Views for analytics dashboard
-- ========================================
CREATE OR REPLACE VIEW user_quiz_stats AS
SELECT 
    u.id as user_id,
    u.email,
    u.full_name,
    COUNT(qa.id) as total_attempts,
    AVG(qa.score_percentage) as avg_score,
    SUM(CASE WHEN qa.passed THEN 1 ELSE 0 END) as passed_exams,
    MAX(qa.completed_at) as last_attempt
FROM users u 
LEFT JOIN quiz_attempts qa ON u.id = qa.user_id 
WHERE u.is_active = TRUE
GROUP BY u.id, u.email, u.full_name;

CREATE OR REPLACE VIEW weak_areas_summary AS
SELECT 
    uwa.user_id,
    uwa.category,
    AVG(uwa.accuracy_percentage) as avg_accuracy,
    COUNT(*) as question_count
FROM user_weak_areas uwa
WHERE uwa.accuracy_percentage < 80.00
GROUP BY uwa.user_id, uwa.category
HAVING COUNT(*) >= 3;

-- ========================================
-- Sample data for testing
-- ========================================
INSERT IGNORE INTO quiz_attempts (user_id, quiz_type, total_questions, correct_answers, wrong_answers, score_percentage, passed)
SELECT 1, 'PRACTICE', 10, 8, 2, 80.00, FALSE
WHERE EXISTS (SELECT 1 FROM users WHERE id = 1);

-- End of V11
