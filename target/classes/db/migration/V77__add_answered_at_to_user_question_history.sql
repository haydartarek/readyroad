-- ============================================================
-- Migration: V77 (SAFE - Idempotent)
-- Description: Add answered_at column to user_question_history
-- Author: ReadyRoad Team
-- Date: 2026-02-06
-- BDD: Must not fail if column already exists
-- ============================================================

-- Check if answered_at column exists
SET @column_exists = (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'user_question_history'
    AND COLUMN_NAME = 'answered_at'
);

-- Add column only if it doesn't exist
SET @sql = IF(
  @column_exists = 0,
  'ALTER TABLE user_question_history ADD COLUMN answered_at DATETIME(6) NULL COMMENT ''Timestamp when question was answered by user''',
  'SELECT ''✅ Column answered_at already exists - skipping'' AS migration_status'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Check if index exists
SET @index_exists = (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'user_question_history'
    AND INDEX_NAME = 'idx_answered_at'
);

-- Add index only if it doesn't exist
SET @sql_index = IF(
  @index_exists = 0,
  'CREATE INDEX idx_answered_at ON user_question_history(answered_at)',
  'SELECT ''✅ Index idx_answered_at already exists - skipping'' AS migration_status'
);

PREPARE stmt_index FROM @sql_index;
EXECUTE stmt_index;
DEALLOCATE PREPARE stmt_index;

-- Log success
SELECT CONCAT(
  'V77 Migration completed: ',
  IF(@column_exists = 0, 'Column added', 'Column already existed'),
  ', ',
  IF(@index_exists = 0, 'Index added', 'Index already existed')
) AS result;
