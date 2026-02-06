-- ============================================================
-- Migration: V79
-- Description: Add times_wrong column (idempotent)
-- Author: ReadyRoad Team
-- Date: 2026-02-06
-- Critical: This was added manually - now making it official
-- ============================================================

-- Add times_wrong column if it doesn't exist
-- (Production already has it, but new environments need it)

SET @column_exists = (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'user_question_history'
    AND COLUMN_NAME = 'times_wrong'
);

SET @sql = IF(
  @column_exists = 0,
  'ALTER TABLE user_question_history ADD COLUMN times_wrong INT NOT NULL DEFAULT 0 COMMENT ''Times answered incorrectly (separate from times_incorrect for historical tracking)''',
  'SELECT ''✅ Column times_wrong already exists - skipping'' AS migration_status'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add index for performance (if needed)
SET @index_exists = (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'user_question_history'
    AND INDEX_NAME = 'idx_times_wrong'
);

SET @sql_index = IF(
  @index_exists = 0,
  'CREATE INDEX idx_times_wrong ON user_question_history(times_wrong)',
  'SELECT ''✅ Index idx_times_wrong already exists - skipping'' AS migration_status'
);

PREPARE stmt_index FROM @sql_index;
EXECUTE stmt_index;
DEALLOCATE PREPARE stmt_index;

-- Log completion
SELECT CONCAT(
  'V79: times_wrong column ',
  IF(@column_exists = 0, 'ADDED', 'verified (already existed)'),
  ', index ',
  IF(@index_exists = 0, 'ADDED', 'verified')
) AS result;
