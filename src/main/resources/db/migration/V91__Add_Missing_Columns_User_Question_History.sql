-- ============================================================
-- Migration: V91 (FIXED)
-- Description: Add all missing columns to user_question_history
--              so that authenticated smart-quiz sessions can
--              persist history without a 500 error.
-- Author: ReadyRoad Team
-- Date: 2026-02-27
-- Safe: idempotent (checks column existence before altering)
-- FIX: Removed broken UPDATE that referenced non-existent question_id column
-- ============================================================

-- ── question_ref_id (NOT NULL, used for unique constraint) ───────────────────
SET @col = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_question_history'
  AND COLUMN_NAME = 'question_ref_id');
SET @sql = IF(@col = 0,
  'ALTER TABLE user_question_history ADD COLUMN question_ref_id BIGINT NOT NULL DEFAULT 0 COMMENT ''Unique ref for (user, question) pair''',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ── Back-fill question_ref_id safely (only if question_id column exists) ─────
-- NOTE: question_id column is added in V95. If it exists, use it to back-fill.
SET @has_qid = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_question_history'
  AND COLUMN_NAME = 'question_id');
SET @sql = IF(@has_qid > 0,
  'UPDATE user_question_history SET question_ref_id = question_id WHERE question_ref_id = 0 AND question_id IS NOT NULL',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ── Unique constraint uk_user_question_ref ───────────────────────────────────
-- First drop old conflicting 4-column unique key if it exists
SET @old_idx = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_question_history'
  AND INDEX_NAME = 'uk_user_question');
SET @sql = IF(@old_idx > 0,
  'ALTER TABLE user_question_history DROP INDEX uk_user_question',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- Deduplicate rows before adding unique constraint (keep highest id per pair)
DELETE uqh FROM user_question_history uqh
INNER JOIN (
    SELECT user_id, question_ref_id, MAX(id) AS keep_id
    FROM user_question_history
    WHERE question_ref_id IS NOT NULL AND question_ref_id > 0
    GROUP BY user_id, question_ref_id
    HAVING COUNT(*) > 1
) dups ON uqh.user_id = dups.user_id
       AND uqh.question_ref_id = dups.question_ref_id
       AND uqh.id < dups.keep_id;

-- Add unique constraint
SET @con = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_question_history'
  AND CONSTRAINT_NAME = 'uk_user_question_ref');
SET @sql = IF(@con = 0,
  'ALTER TABLE user_question_history ADD CONSTRAINT uk_user_question_ref UNIQUE (user_id, question_ref_id)',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ── is_correct (nullable TINYINT) ────────────────────────────────────────────
SET @col = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_question_history'
  AND COLUMN_NAME = 'is_correct');
SET @sql = IF(@col = 0,
  'ALTER TABLE user_question_history ADD COLUMN is_correct TINYINT NULL COMMENT ''Correctness of the answer (null = not answered yet)''',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ── time_taken_seconds (nullable INT) ────────────────────────────────────────
SET @col = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_question_history'
  AND COLUMN_NAME = 'time_taken_seconds');
SET @sql = IF(@col = 0,
  'ALTER TABLE user_question_history ADD COLUMN time_taken_seconds INT NULL COMMENT ''Seconds taken to answer''',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ── last_shown_at (NOT NULL, default NOW()) ───────────────────────────────────
SET @col = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_question_history'
  AND COLUMN_NAME = 'last_shown_at');
SET @sql = IF(@col = 0,
  'ALTER TABLE user_question_history ADD COLUMN last_shown_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''When the question was last shown''',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ── last_shown_type (VARCHAR 20, nullable) ────────────────────────────────────
SET @col = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_question_history'
  AND COLUMN_NAME = 'last_shown_type');
SET @sql = IF(@col = 0,
  'ALTER TABLE user_question_history ADD COLUMN last_shown_type VARCHAR(20) NULL COMMENT ''Context: RANDOM | CATEGORY | EXAM | SMART_QUIZ''',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ── times_shown (NOT NULL, default 1) ────────────────────────────────────────
SET @col = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_question_history'
  AND COLUMN_NAME = 'times_shown');
SET @sql = IF(@col = 0,
  'ALTER TABLE user_question_history ADD COLUMN times_shown INT NOT NULL DEFAULT 1 COMMENT ''How many times shown''',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ── times_correct (NOT NULL, default 0) ──────────────────────────────────────
SET @col = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_question_history'
  AND COLUMN_NAME = 'times_correct');
SET @sql = IF(@col = 0,
  'ALTER TABLE user_question_history ADD COLUMN times_correct INT NOT NULL DEFAULT 0 COMMENT ''Correct answer count''',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ── times_wrong (NOT NULL, default 0) ────────────────────────────────────────
-- Entity field: timesIncorrect maps to column times_wrong
SET @col = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_question_history'
  AND COLUMN_NAME = 'times_wrong');
SET @sql = IF(@col = 0,
  'ALTER TABLE user_question_history ADD COLUMN times_wrong INT NOT NULL DEFAULT 0 COMMENT ''Incorrect answer count (entity: timesIncorrect)''',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ── created_at (NOT NULL, default NOW()) ─────────────────────────────────────
SET @col = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_question_history'
  AND COLUMN_NAME = 'created_at');
SET @sql = IF(@col = 0,
  'ALTER TABLE user_question_history ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''Record creation time''',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ── updated_at (nullable) ─────────────────────────────────────────────────────
SET @col = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_question_history'
  AND COLUMN_NAME = 'updated_at');
SET @sql = IF(@col = 0,
  'ALTER TABLE user_question_history ADD COLUMN updated_at DATETIME NULL COMMENT ''Last update time''',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ── Performance index on (user_id, answered_at, is_correct) ──────────────────
SET @idx = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_question_history'
  AND INDEX_NAME = 'idx_user_question_history_perf');
SET @sql = IF(@idx = 0,
  'CREATE INDEX idx_user_question_history_perf ON user_question_history (user_id, answered_at, is_correct)',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SELECT 'V91: user_question_history schema aligned (fixed UPDATE bug)' AS result;
