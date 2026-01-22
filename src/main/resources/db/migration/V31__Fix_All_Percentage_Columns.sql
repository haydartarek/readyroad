-- ========================================
-- V31: Fix all DECIMAL to DOUBLE type mismatches
-- ========================================
-- Date: 2026-01-18
-- Description: Convert all percentage columns from DECIMAL to DOUBLE
-- Issue: Hibernate expects FLOAT(53)/DOUBLE but DB has DECIMAL
-- ========================================

-- Fix user_weak_areas table
ALTER TABLE user_weak_areas
MODIFY COLUMN accuracy_percentage DOUBLE NULL;

-- Fix any other percentage columns that might have the same issue
-- (Add more ALTER statements here if needed based on future errors)

-- Add comment
ALTER TABLE user_weak_areas COMMENT = 'User weak areas tracking with correct column types';
