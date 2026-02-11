-- ============================================
-- ReadyRoad Database Initialization
-- ============================================
-- This script runs automatically when MySQL container starts
-- ============================================

-- Ensure database exists with correct charset
CREATE DATABASE IF NOT EXISTS readyroad_prod
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Grant all privileges to haydar user
GRANT ALL PRIVILEGES ON readyroad_prod.* TO 'haydar'@'%';

-- Apply changes
FLUSH PRIVILEGES;

-- Switch to database
USE readyroad_prod;

-- Show database info
SELECT
    SCHEMA_NAME as 'Database',
    DEFAULT_CHARACTER_SET_NAME as 'Charset',
    DEFAULT_COLLATION_NAME as 'Collation'
FROM INFORMATION_SCHEMA.SCHEMATA
WHERE SCHEMA_NAME = 'readyroad_prod';

-- Log initialization
SELECT 'ReadyRoad database initialized successfully' as Status;
