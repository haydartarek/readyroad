-- Fix Flyway failed migration
-- Run this in MySQL to clean up the failed migration

USE readyroad_prod;

-- Delete the failed migration record
DELETE FROM flyway_schema_history WHERE version = '77' AND success = 0;

-- Verify
SELECT * FROM flyway_schema_history ORDER BY installed_rank DESC LIMIT 5;
