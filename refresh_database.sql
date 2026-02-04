-- Refresh Database Script
-- This will clear old data and force DataInitializer to reload from JSON

USE readyroad;

-- Clear all traffic signs data
TRUNCATE TABLE traffic_signs;

-- Clear related tables if needed
-- TRUNCATE TABLE lessons;
-- TRUNCATE TABLE categories;

SELECT 'Database cleared successfully. Restart Spring Boot to reload from signs.json' AS message;
