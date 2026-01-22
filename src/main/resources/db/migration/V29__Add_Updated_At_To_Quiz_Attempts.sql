-- ========================================
-- V29: Add missing updated_at column to quiz_attempts
-- ========================================
-- Date: 2026-01-18
-- Description: Fix schema validation error - add updated_at column
-- Issue: Hibernate expects updated_at but column doesn't exist in DB
-- ========================================

ALTER TABLE quiz_attempts
ADD COLUMN updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
AFTER created_at;

-- Add comment
ALTER TABLE quiz_attempts COMMENT = 'Quiz attempts table with tracking timestamps';
