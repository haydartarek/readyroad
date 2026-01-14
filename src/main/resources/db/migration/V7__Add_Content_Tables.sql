-- V7__Add_Content_Tables.sql
-- إضافة جداول المحتوى الإضافي
-- Rules, Instructions, Quiz Questions
-- Generated: 2026-01-14

-- ========================================
-- جدول القواعد والتعليمات المرورية
-- Traffic Rules and Instructions Table
-- ========================================

CREATE TABLE traffic_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_code VARCHAR(20) NOT NULL UNIQUE,
    title_ar TEXT NOT NULL,
    title_en TEXT NOT NULL,
    title_nl TEXT NOT NULL,
    title_fr TEXT NOT NULL,
    content_ar TEXT,
    content_en TEXT,
    content_nl TEXT,
    content_fr TEXT,
    category VARCHAR(50),
    importance_level ENUM('HIGH', 'MEDIUM', 'LOW') DEFAULT 'MEDIUM',
    applies_to VARCHAR(100), -- 'ALL', 'CAR', 'MOTORCYCLE', 'BICYCLE', etc.
    penalty_info_ar TEXT,
    penalty_info_en TEXT,
    penalty_info_nl TEXT,
    penalty_info_fr TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    INDEX idx_category (category),
    INDEX idx_importance (importance_level),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- جدول ربط القواعد بالعلامات المرورية
-- Traffic Sign Rules Mapping Table
-- ========================================

CREATE TABLE traffic_sign_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    traffic_sign_id BIGINT NOT NULL,
    traffic_rule_id BIGINT NOT NULL,
    relationship_type ENUM('REQUIRED', 'RELATED', 'EXCEPTION') DEFAULT 'RELATED',
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (traffic_sign_id) REFERENCES traffic_signs(id) ON DELETE CASCADE,
    FOREIGN KEY (traffic_rule_id) REFERENCES traffic_rules(id) ON DELETE CASCADE,
    INDEX idx_sign (traffic_sign_id),
    INDEX idx_rule (traffic_rule_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- جدول أسئلة الاختبار
-- Quiz Questions Table
-- ========================================

CREATE TABLE quiz_questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_ar TEXT NOT NULL,
    question_en TEXT NOT NULL,
    question_nl TEXT NOT NULL,
    question_fr TEXT NOT NULL,
    question_type ENUM('MULTIPLE_CHOICE', 'TRUE_FALSE', 'IMAGE_RECOGNITION') NOT NULL,
    difficulty_level ENUM('EASY', 'MEDIUM', 'HARD') DEFAULT 'MEDIUM',
    category_id BIGINT,
    traffic_sign_id BIGINT,
    traffic_rule_id BIGINT,
    explanation_ar TEXT,
    explanation_en TEXT,
    explanation_nl TEXT,
    explanation_fr TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    FOREIGN KEY (traffic_sign_id) REFERENCES traffic_signs(id) ON DELETE SET NULL,
    FOREIGN KEY (traffic_rule_id) REFERENCES traffic_rules(id) ON DELETE SET NULL,
    INDEX idx_type (question_type),
    INDEX idx_difficulty (difficulty_level),
    INDEX idx_category (category_id),
    INDEX idx_sign (traffic_sign_id),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- جدول خيارات الإجابة
-- Quiz Answer Options Table
-- ========================================

CREATE TABLE quiz_answer_options (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL,
    option_text_ar TEXT NOT NULL,
    option_text_en TEXT NOT NULL,
    option_text_nl TEXT NOT NULL,
    option_text_fr TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (question_id) REFERENCES quiz_questions(id) ON DELETE CASCADE,
    INDEX idx_question (question_id),
    INDEX idx_correct (is_correct)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- جدول الأوصاف المفصلة للعلامات
-- Traffic Sign Detailed Descriptions Table
-- ========================================

CREATE TABLE traffic_sign_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    traffic_sign_id BIGINT NOT NULL UNIQUE,
    detailed_description_ar TEXT,
    detailed_description_en TEXT,
    detailed_description_nl TEXT,
    detailed_description_fr TEXT,
    when_to_use_ar TEXT,
    when_to_use_en TEXT,
    when_to_use_nl TEXT,
    when_to_use_fr TEXT,
    common_mistakes_ar TEXT,
    common_mistakes_en TEXT,
    common_mistakes_nl TEXT,
    common_mistakes_fr TEXT,
    tips_ar TEXT,
    tips_en TEXT,
    tips_nl TEXT,
    tips_fr TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (traffic_sign_id) REFERENCES traffic_signs(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- جدول إحصائيات أداء المستخدمين
-- User Performance Statistics Table
-- ========================================

CREATE TABLE user_quiz_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    question_id BIGINT NOT NULL,
    selected_option_id BIGINT,
    is_correct BOOLEAN NOT NULL,
    time_spent_seconds INT,
    attempted_at TIMESTAMP NOT NULL,
    FOREIGN KEY (question_id) REFERENCES quiz_questions(id) ON DELETE CASCADE,
    FOREIGN KEY (selected_option_id) REFERENCES quiz_answer_options(id) ON DELETE SET NULL,
    INDEX idx_user (user_id),
    INDEX idx_question (question_id),
    INDEX idx_correct (is_correct),
    INDEX idx_attempted (attempted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
