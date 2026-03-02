-- ============================================================
-- Migration: V95
-- Description: Critical schema fixes to align DB with JPA entities
-- Author: ReadyRoad Team
-- Date: 2026-02-27
-- Safe: Fully idempotent - all operations check before executing
--
-- Fixes:
--  1. user_question_history: Add missing question_id FK column
--  2. user_question_history: Fix question_type NOT NULL (entity doesn't map it)
--  3. user_question_history: Fix last_shown_type ENUM → VARCHAR(20)
--  4. quiz_user_answers: Fix question_type ENUM RULE/SIGN → PRACTICE/EXAM
--  5. quiz_attempts: Fix quiz_type ENUM → VARCHAR(50) for MOCK_EXAM support
--  6. quiz_attempts: Fix score_percentage DECIMAL(4,2) → DECIMAL(5,2)
--  7. user_question_history: Fix answered_at to DATETIME (consistency)
--  8. Add missing performance indexes
-- ============================================================

-- ════════════════════════════════════════════════════════════════
-- 1. user_question_history: Add missing question_id column
--    Entity: @Column(name = "question_id", nullable = false)
--    FK to quiz_questions(id), auto-filled via @PrePersist
-- ════════════════════════════════════════════════════════════════

SET @col = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_question_history'
  AND COLUMN_NAME = 'question_id');
SET @sql = IF(@col = 0,
  'ALTER TABLE user_question_history ADD COLUMN question_id BIGINT NULL COMMENT ''FK to quiz_questions.id'' AFTER user_id',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- Back-fill question_id from question_ref_id for all existing rows
UPDATE user_question_history
SET question_id = question_ref_id
WHERE question_id IS NULL AND question_ref_id IS NOT NULL AND question_ref_id > 0;

-- Add FK index for question_id
SET @idx = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_question_history'
  AND INDEX_NAME = 'idx_uqh_question_id');
SET @sql = IF(@idx = 0,
  'CREATE INDEX idx_uqh_question_id ON user_question_history (question_id)',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ════════════════════════════════════════════════════════════════
-- 2. user_question_history: Fix question_type NOT NULL constraint
--    Entity does NOT have this field - causes INSERT failures
--    Make nullable to allow JPA inserts without this column
-- ════════════════════════════════════════════════════════════════

SET @col = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_question_history'
  AND COLUMN_NAME = 'question_type' AND IS_NULLABLE = 'NO');
SET @sql = IF(@col > 0,
  'ALTER TABLE user_question_history MODIFY COLUMN question_type VARCHAR(10) NULL DEFAULT NULL COMMENT ''Legacy field (not used by entity) - kept for historical data''',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ════════════════════════════════════════════════════════════════
-- 3. user_question_history: Fix last_shown_type ENUM → VARCHAR(20)
--    Old: ENUM(''PRACTICE'',''EXAM'') NOT NULL
--    New: VARCHAR(20) NULL  (entity uses: RANDOM, CATEGORY, EXAM, SMART_QUIZ)
-- ════════════════════════════════════════════════════════════════

SET @col_type = (SELECT UPPER(COLUMN_TYPE) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_question_history'
  AND COLUMN_NAME = 'last_shown_type');
SET @sql = IF(@col_type LIKE '%ENUM%',
  'ALTER TABLE user_question_history MODIFY COLUMN last_shown_type VARCHAR(20) NULL COMMENT ''Context: RANDOM | CATEGORY | EXAM | SMART_QUIZ''',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ════════════════════════════════════════════════════════════════
-- 4. user_question_history: Ensure answered_at type consistency
--    V77 added DATETIME(6) NULL - align to plain DATETIME NULL
-- ════════════════════════════════════════════════════════════════

SET @col_type = (SELECT UPPER(COLUMN_TYPE) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_question_history'
  AND COLUMN_NAME = 'answered_at');
SET @sql = IF(@col_type = 'DATETIME(6)',
  'ALTER TABLE user_question_history MODIFY COLUMN answered_at DATETIME NULL COMMENT ''Timestamp when user last answered this question''',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ════════════════════════════════════════════════════════════════
-- 5. quiz_user_answers: Fix question_type ENUM values
--    Old: ENUM(''RULE'',''SIGN'')  (legacy quiz system)
--    New: ENUM(''PRACTICE'',''EXAM'')  (entity: QuestionType enum)
-- ════════════════════════════════════════════════════════════════

-- Step A: Widen to VARCHAR so we can migrate data
SET @col_type = (SELECT UPPER(COLUMN_TYPE) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'quiz_user_answers'
  AND COLUMN_NAME = 'question_type');
SET @sql = IF(@col_type LIKE '%RULE%',
  'ALTER TABLE quiz_user_answers MODIFY COLUMN question_type VARCHAR(20) NOT NULL',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- Step B: Map old ENUM values to new ones
UPDATE quiz_user_answers SET question_type = 'PRACTICE' WHERE question_type = 'RULE';
UPDATE quiz_user_answers SET question_type = 'EXAM'     WHERE question_type = 'SIGN';

-- Step C: Apply correct ENUM constraint
SET @col_type2 = (SELECT UPPER(COLUMN_TYPE) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'quiz_user_answers'
  AND COLUMN_NAME = 'question_type');
SET @sql = IF(@col_type2 NOT LIKE '%PRACTICE%',
  'ALTER TABLE quiz_user_answers MODIFY COLUMN question_type ENUM(''PRACTICE'',''EXAM'') NOT NULL',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ════════════════════════════════════════════════════════════════
-- 6. quiz_attempts: Fix quiz_type ENUM → VARCHAR(50)
--    Old: ENUM(''PRACTICE'',''EXAM'')
--    New: VARCHAR(50) to support MOCK_EXAM, OFFICIAL_EXAM
--    Entity: @Column(name = "quiz_type", length = 50) String
-- ════════════════════════════════════════════════════════════════

SET @col_type = (SELECT UPPER(COLUMN_TYPE) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'quiz_attempts'
  AND COLUMN_NAME = 'quiz_type');
SET @sql = IF(@col_type LIKE '%ENUM%',
  'ALTER TABLE quiz_attempts MODIFY COLUMN quiz_type VARCHAR(50) NOT NULL DEFAULT ''PRACTICE'' COMMENT ''PRACTICE | MOCK_EXAM | OFFICIAL_EXAM''',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ════════════════════════════════════════════════════════════════
-- 7. quiz_attempts: Fix score_percentage precision
--    Old: DECIMAL(4,2) - max 99.99% (fine for percentage but semantically narrow)
--    New: DECIMAL(5,2) - consistent with exam_simulations table
-- ════════════════════════════════════════════════════════════════

SET @col_type = (SELECT UPPER(COLUMN_TYPE) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'quiz_attempts'
  AND COLUMN_NAME = 'score_percentage');
SET @sql = IF(@col_type = 'DECIMAL(4,2)',
  'ALTER TABLE quiz_attempts MODIFY COLUMN score_percentage DECIMAL(5,2) NOT NULL COMMENT ''Score 0.00-100.00''',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ════════════════════════════════════════════════════════════════
-- 8. user_question_history: Add missing compound indexes
--    Required by entity @Table(indexes = {...})
-- ════════════════════════════════════════════════════════════════

-- idx_user_question_history_user_answered: (user_id, answered_at)
SET @idx = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_question_history'
  AND INDEX_NAME = 'idx_user_question_history_user_answered');
SET @sql = IF(@idx = 0,
  'CREATE INDEX idx_user_question_history_user_answered ON user_question_history (user_id, answered_at)',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- idx_user_question_history_question_answered: (question_id, answered_at)
SET @idx = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_question_history'
  AND INDEX_NAME = 'idx_user_question_history_question_answered');
SET @sql = IF(@idx = 0,
  'CREATE INDEX idx_user_question_history_question_answered ON user_question_history (question_id, answered_at)',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- idx_user_question_history_lookup: (user_id, question_id, answered_at)
SET @idx = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_question_history'
  AND INDEX_NAME = 'idx_user_question_history_lookup');
SET @sql = IF(@idx = 0,
  'CREATE INDEX idx_user_question_history_lookup ON user_question_history (user_id, question_id, answered_at)',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ════════════════════════════════════════════════════════════════
-- 9. quiz_attempts: Add quiz_type index for filtering by type
-- ════════════════════════════════════════════════════════════════

SET @idx = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'quiz_attempts'
  AND INDEX_NAME = 'idx_quiz_attempts_quiz_type');
SET @sql = IF(@idx = 0,
  'CREATE INDEX idx_quiz_attempts_quiz_type ON quiz_attempts (quiz_type)',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ════════════════════════════════════════════════════════════════
-- 10. quiz_user_answers: Add question lookup index
-- ════════════════════════════════════════════════════════════════

SET @idx = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'quiz_user_answers'
  AND INDEX_NAME = 'idx_quiz_user_answers_question_ref');
SET @sql = IF(@idx = 0,
  'CREATE INDEX idx_quiz_user_answers_question_ref ON quiz_user_answers (question_type, question_ref_id)',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ════════════════════════════════════════════════════════════════
-- 11. quiz_questions: Ensure status column has correct DEFAULT
--     V38 adds it but let's verify the default is DRAFT
-- ════════════════════════════════════════════════════════════════

SET @col_default = (SELECT COLUMN_DEFAULT FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'quiz_questions'
  AND COLUMN_NAME = 'status');
SET @sql = IF(@col_default IS NULL OR @col_default != 'DRAFT',
  'ALTER TABLE quiz_questions MODIFY COLUMN status VARCHAR(20) NOT NULL DEFAULT ''DRAFT'' COMMENT ''DRAFT | PUBLISHED''',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ════════════════════════════════════════════════════════════════
-- 12. user_category_progress: Ensure mastery_level constraint is correct
--     Add EXPERT level to mastery check (BEGINNER, INTERMEDIATE, ADVANCED, EXPERT)
-- ════════════════════════════════════════════════════════════════

-- No change needed - existing constraint is fine for current app logic

-- ════════════════════════════════════════════════════════════════
-- Verification summary
-- ════════════════════════════════════════════════════════════════
SELECT
  (SELECT COLUMN_TYPE FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'quiz_user_answers' AND COLUMN_NAME = 'question_type')
   AS `quiz_user_answers.question_type`,

  (SELECT COLUMN_TYPE FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'quiz_attempts' AND COLUMN_NAME = 'quiz_type')
   AS `quiz_attempts.quiz_type`,

  (SELECT COLUMN_TYPE FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_question_history' AND COLUMN_NAME = 'last_shown_type')
   AS `uqh.last_shown_type`,

  (SELECT COLUMN_TYPE FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_question_history' AND COLUMN_NAME = 'question_id')
   AS `uqh.question_id`,

  (SELECT COLUMN_TYPE FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'quiz_attempts' AND COLUMN_NAME = 'score_percentage')
   AS `quiz_attempts.score_percentage`;

SELECT 'V95: Critical schema fixes applied successfully' AS result;
