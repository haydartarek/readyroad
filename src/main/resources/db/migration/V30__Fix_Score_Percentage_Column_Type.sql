-- ========================================
-- V30: Fix score_percentage column type in quiz_attempts
-- ========================================
-- Date: 2026-01-18
-- Description: Change score_percentage from DECIMAL to DOUBLE (FLOAT 53)
-- Issue: Hibernate expects FLOAT(53) but DB has DECIMAL
-- ========================================

ALTER TABLE quiz_attempts
MODIFY COLUMN score_percentage DOUBLE NULL;

-- Add comment
ALTER TABLE quiz_attempts COMMENT = 'Quiz attempts table with correct column types';
