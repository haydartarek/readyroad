-- ========================================
-- V30: Fix score_percentage column type in quiz_attempts
-- ========================================
-- Date: 2026-01-18 | Revised: 2026-02-27
-- Description: Change score_percentage from DECIMAL to DOUBLE (FLOAT 53)
-- Issue: Hibernate expects FLOAT(53) but DB has DECIMAL
-- ========================================

USE readyroad_prod;

-- Fix score_percentage column type (safe migration)
ALTER TABLE quiz_attempts
MODIFY COLUMN IF EXISTS score_percentage DOUBLE NULL;

-- Update comment with precise description
ALTER TABLE quiz_attempts
COMMENT = 'Quiz attempts table with FLOAT(53) score_percentage for Hibernate compatibility';
