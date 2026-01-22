-- Phase 2: Quiz Service Restoration - Create/Update quiz tables
-- Date: 2026-01-18
-- Strategy: Production-safe - ALTER existing tables, CREATE new ones

-- Create quiz_questions table (new)
CREATE TABLE IF NOT EXISTS quiz_questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_ar TEXT NOT NULL,
    question_en TEXT NOT NULL,
    question_nl TEXT NOT NULL,
    question_fr TEXT NOT NULL,
    question_type ENUM('MULTIPLE_CHOICE', 'TRUE_FALSE', 'IMAGE_BASED') NOT NULL,
    difficulty_level ENUM('EASY', 'MEDIUM', 'HARD') NOT NULL,
    category_id BIGINT,
    traffic_sign_id BIGINT,
    content_image_url TEXT,
    explanation_ar TEXT,
    explanation_en TEXT,
    explanation_nl TEXT,
    explanation_fr TEXT,
    error_explanation_ar TEXT,
    error_explanation_en TEXT,
    error_explanation_nl TEXT,
    error_explanation_fr TEXT,
    typical_error_type ENUM('SIGN_CONFUSION', 'SUPPLEMENTARY_IGNORED', 'PRIORITY_MISUNDERSTANDING', 'SPEED_LIMIT_ERROR', 'ZONE_CONFUSION', 'RULE_OVERGENERALIZATION', 'OTHER'),
    context_specific BOOLEAN DEFAULT TRUE,
    requires_sign_image BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    FOREIGN KEY (traffic_sign_id) REFERENCES traffic_signs(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create quiz_answer_options table (new)
CREATE TABLE IF NOT EXISTS quiz_answer_options (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL,
    option_text_ar TEXT NOT NULL,
    option_text_en TEXT NOT NULL,
    option_text_nl TEXT NOT NULL,
    option_text_fr TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    display_order INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES quiz_questions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Update quiz_user_answers table (existing - add missing columns if table exists)
-- Note: Table may exist from previous migrations

-- Check if table exists and create if not
CREATE TABLE IF NOT EXISTS quiz_user_answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    attempt_id BIGINT NOT NULL,
    question_id BIGINT,
    selected_option_id BIGINT,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    time_taken_seconds INT,
    answered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (attempt_id) REFERENCES quiz_attempts(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES quiz_questions(id),
    FOREIGN KEY (selected_option_id) REFERENCES quiz_answer_options(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

