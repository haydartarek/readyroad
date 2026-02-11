-- ============================================================
-- Migration: V78
-- Description: Add all missing columns to user_question_history
-- Author: ReadyRoad Team
-- Date: 2026-02-06
-- Issue: Multiple schema validation errors
-- ============================================================

-- Add last_answer_correct column (if not exists)
SET @column_exists_lac = (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'user_question_history'
    AND COLUMN_NAME = 'last_answer_correct'
);

SET @sql_lac = IF(
  @column_exists_lac = 0,
  'ALTER TABLE user_question_history ADD COLUMN last_answer_correct TINYINT(1) NULL COMMENT ''Whether the last answer was correct''',
  'SELECT ''✅ Column last_answer_correct already exists'' AS status'
);

PREPARE stmt_lac FROM @sql_lac;
EXECUTE stmt_lac;
DEALLOCATE PREPARE stmt_lac;

-- Verify answered_at exists (from V77)
SET @column_exists_aa = (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'user_question_history'
    AND COLUMN_NAME = 'answered_at'
);

SET @sql_aa = IF(
  @column_exists_aa = 0,
  'ALTER TABLE user_question_history ADD COLUMN answered_at DATETIME(6) NULL COMMENT ''Timestamp when question was answered''',
  'SELECT ''✅ Column answered_at already exists'' AS status'
);

PREPARE stmt_aa FROM @sql_aa;
EXECUTE stmt_aa;
DEALLOCATE PREPARE stmt_aa;

-- Add any other potentially missing columns
-- (Add more columns here if schema validation reveals them)

-- Summary
SELECT CONCAT(
  'V78 Migration completed. ',
  'last_answer_correct: ', IF(@column_exists_lac = 0, 'ADDED', 'existed'), ', ',
  'answered_at: ', IF(@column_exists_aa = 0, 'ADDED', 'existed')
) AS migration_result;
