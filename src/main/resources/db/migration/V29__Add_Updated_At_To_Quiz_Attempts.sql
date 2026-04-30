-- V29__Add_Missing_updated_at_to_quiz_attempts.sql (Fixed)
-- Add missing updated_at column (safe migration)
ALTER TABLE quiz_attempts 
ADD COLUMN updated_at TIMESTAMP NULL 
DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP 
AFTER created_at;

-- Add table comment (safe)
ALTER TABLE quiz_attempts 
COMMENT = 'Quiz attempts table with tracking timestamps (started_at, completed_at, updated_at)';

-- Verify the change (optional - for migration log)
-- SELECT COLUMN_NAME, COLUMN_TYPE, EXTRA FROM INFORMATION_SCHEMA.COLUMNS 
-- WHERE TABLE_NAME = 'quiz_attempts' AND COLUMN_NAME = 'updated_at';
