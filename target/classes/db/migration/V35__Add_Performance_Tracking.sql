-- V35: Add Performance Tracking to User Question History
-- Created: January 19, 2026
-- Updated: January 20, 2026 - Made idempotent for MySQL 8.0
-- Purpose: Enable adaptive difficulty (Law #2) by tracking answer correctness and timing
-- Phase: 4 (Adaptive Difficulty)

-- Check and add is_correct column if not exists
SET @sql := IF (
    NOT EXISTS(
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = DATABASE()
        AND table_name = 'user_question_history'
        AND column_name = 'is_correct'
    ),
    'ALTER TABLE user_question_history ADD COLUMN is_correct BOOLEAN NULL COMMENT ''Was the answer correct? NULL = not answered yet''',
    'SELECT ''Column is_correct already exists'' AS msg'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Check and add time_taken_seconds column if not exists
SET @sql := IF (
    NOT EXISTS(
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = DATABASE()
        AND table_name = 'user_question_history'
        AND column_name = 'time_taken_seconds'
    ),
    'ALTER TABLE user_question_history ADD COLUMN time_taken_seconds INT NULL COMMENT ''Time taken to answer in seconds''',
    'SELECT ''Column time_taken_seconds already exists'' AS msg'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Check and add composite index if not exists
SET @sql := IF (
    NOT EXISTS(
        SELECT 1 FROM information_schema.statistics
        WHERE table_schema = DATABASE()
        AND table_name = 'user_question_history'
        AND index_name = 'idx_user_question_history_perf'
    ),
    'CREATE INDEX idx_user_question_history_perf ON user_question_history(user_id, last_shown_at, is_correct)',
    'SELECT ''Index idx_user_question_history_perf already exists'' AS msg'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Migration complete
-- This migration is now idempotent and safe to run multiple times
