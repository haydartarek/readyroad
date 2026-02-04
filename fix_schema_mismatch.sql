-- Fix Schema Mismatch Issues
-- Run this script to fix all schema validation errors

USE readyroad;

-- Fix exam_simulations table - Change DECIMAL to FLOAT
ALTER TABLE exam_simulations 
MODIFY COLUMN score_percentage FLOAT;

-- Disable foreign key checks temporarily
SET FOREIGN_KEY_CHECKS = 0;

-- Truncate traffic_signs to reload with correct image paths
TRUNCATE TABLE traffic_signs;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

SELECT 'Schema fixed successfully! Restart Spring Boot application.' AS message;
