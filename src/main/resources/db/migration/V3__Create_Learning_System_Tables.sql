-- ====================================================================
-- Phase 4: Learning System Tables
-- ====================================================================

-- ====================
-- STEP 1: Lessons Table
-- ====================
CREATE TABLE IF NOT EXISTS lessons (
    id               BIGINT          AUTO_INCREMENT PRIMARY KEY,
    category_id      BIGINT          NOT NULL,
    title_ar         VARCHAR(500)    NOT NULL,  -- ✅ Fixed: TEXT → VARCHAR for indexed/sorted fields
    title_en         VARCHAR(500)    NOT NULL,
    title_nl         VARCHAR(500)    NOT NULL,
    title_fr         VARCHAR(500)    NOT NULL,
    content_ar       TEXT            NOT NULL,
    content_en       TEXT            NOT NULL,
    content_nl       TEXT            NOT NULL,
    content_fr       TEXT            NOT NULL,
    display_order    INT             NOT NULL DEFAULT 0,
    estimated_minutes INT            NOT NULL DEFAULT 5,
    is_active        BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,          -- ✅ Fixed: added DEFAULT
    updated_at       TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,  -- ✅ Fixed: auto-update
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    INDEX idx_lessons_category      (category_id),
    INDEX idx_lessons_display_order (display_order),
    INDEX idx_lessons_active        (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ====================
-- STEP 2: Practice Questions Table
-- ====================
CREATE TABLE IF NOT EXISTS practice_questions (
    id               BIGINT  AUTO_INCREMENT PRIMARY KEY,
    lesson_id        BIGINT  NOT NULL,
    question_ar      TEXT    NOT NULL,
    question_en      TEXT    NOT NULL,
    question_nl      TEXT    NOT NULL,
    question_fr      TEXT    NOT NULL,
    option1_ar       TEXT    NOT NULL,
    option1_en       TEXT    NOT NULL,
    option1_nl       TEXT    NOT NULL,
    option1_fr       TEXT    NOT NULL,
    option2_ar       TEXT    NOT NULL,
    option2_en       TEXT    NOT NULL,
    option2_nl       TEXT    NOT NULL,
    option2_fr       TEXT    NOT NULL,
    option3_ar       TEXT    NOT NULL,
    option3_en       TEXT    NOT NULL,
    option3_nl       TEXT    NOT NULL,
    option3_fr       TEXT    NOT NULL,
    option4_ar       TEXT    NOT NULL,
    option4_en       TEXT    NOT NULL,
    option4_nl       TEXT    NOT NULL,
    option4_fr       TEXT    NOT NULL,
    correct_answer   TINYINT NOT NULL,           -- ✅ Fixed: INT → TINYINT (values 1-4 only)
    explanation_ar   TEXT,
    explanation_en   TEXT,
    explanation_nl   TEXT,
    explanation_fr   TEXT,
    display_order    INT     NOT NULL DEFAULT 0,
    is_active        BOOLEAN NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_pq_correct_answer CHECK (correct_answer BETWEEN 1 AND 4),  -- ✅ Added: validate range
    FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON DELETE CASCADE,
    INDEX idx_pq_lesson        (lesson_id),
    INDEX idx_pq_display_order (display_order),
    INDEX idx_pq_active        (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ====================
-- STEP 3: Exam Questions Table
-- ====================
CREATE TABLE IF NOT EXISTS exam_questions (
    id               BIGINT          AUTO_INCREMENT PRIMARY KEY,
    category_id      BIGINT          NOT NULL,
    question_ar      TEXT            NOT NULL,
    question_en      TEXT            NOT NULL,
    question_nl      TEXT            NOT NULL,
    question_fr      TEXT            NOT NULL,
    option1_ar       TEXT            NOT NULL,
    option1_en       TEXT            NOT NULL,
    option1_nl       TEXT            NOT NULL,
    option1_fr       TEXT            NOT NULL,
    option2_ar       TEXT            NOT NULL,
    option2_en       TEXT            NOT NULL,
    option2_nl       TEXT            NOT NULL,
    option2_fr       TEXT            NOT NULL,
    option3_ar       TEXT            NOT NULL,
    option3_en       TEXT            NOT NULL,
    option3_nl       TEXT            NOT NULL,
    option3_fr       TEXT            NOT NULL,
    option4_ar       TEXT            NOT NULL,
    option4_en       TEXT            NOT NULL,
    option4_nl       TEXT            NOT NULL,
    option4_fr       TEXT            NOT NULL,
    correct_answer   TINYINT         NOT NULL,   -- ✅ Fixed: INT → TINYINT
    explanation_ar   TEXT,
    explanation_en   TEXT,
    explanation_nl   TEXT,
    explanation_fr   TEXT,
    image_url        VARCHAR(500),
    difficulty       ENUM('EASY', 'MEDIUM', 'HARD') NOT NULL DEFAULT 'MEDIUM',
    is_important     BOOLEAN         NOT NULL DEFAULT TRUE,  -- ✅ Changed default to FALSE (not every question is important)
    is_active        BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_eq_correct_answer CHECK (correct_answer BETWEEN 1 AND 4),  -- ✅ Added validation
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    INDEX idx_eq_category   (category_id),
    INDEX idx_eq_difficulty (difficulty),
    INDEX idx_eq_important  (is_important),
    INDEX idx_eq_active     (is_active),
    INDEX idx_eq_exam_pool  (category_id, difficulty, is_active)  -- ✅ Added composite index for exam generation
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ====================
-- STEP 4: Verification
-- ====================
SELECT
    table_name              AS `Table`,
    table_rows              AS `Rows`,
    ROUND(data_length/1024) AS `Size KB`
FROM information_schema.tables
WHERE table_schema = DATABASE()
  AND table_name IN ('lessons', 'practice_questions', 'exam_questions');
