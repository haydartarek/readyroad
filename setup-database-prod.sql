-- ============================================
-- ReadyRoad PRODUCTION Database Setup
-- ============================================
-- Purpose: Create production database and grant permissions
-- User: haydar
-- Password: Hh06101987@
-- Database: readyroad_prod
-- ============================================

-- 1. Create production database if not exists
CREATE DATABASE IF NOT EXISTS readyroad_prod
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- 2. Grant all privileges on readyroad_prod database to user 'haydar'
GRANT ALL PRIVILEGES ON readyroad_prod.* TO 'haydar'@'localhost';

-- 3. Apply changes
FLUSH PRIVILEGES;

-- 4. Verify grants
SHOW GRANTS FOR 'haydar'@'localhost';

-- 5. Switch to readyroad_prod database
USE readyroad_prod;

-- 6. Show existing tables (if any)
SHOW TABLES;

-- 7. Show database character set
SELECT
    SCHEMA_NAME as 'Database',
    DEFAULT_CHARACTER_SET_NAME as 'Charset',
    DEFAULT_COLLATION_NAME as 'Collation'
FROM INFORMATION_SCHEMA.SCHEMATA
WHERE SCHEMA_NAME = 'readyroad_prod';

-- ============================================
-- Expected Output:
-- ============================================
-- Database created: readyroad_prod
-- Character set: utf8mb4
-- Collation: utf8mb4_unicode_ci
-- Grants for haydar@localhost:
--   - GRANT ALL PRIVILEGES ON `readyroad_prod`.* TO `haydar`@`localhost`
-- ============================================

-- ============================================
-- Usage:
-- ============================================
-- From command line:
-- mysql -u root -p < setup-database-prod.sql
--
-- Or from MySQL CLI:
-- mysql -u root -p
-- source setup-database-prod.sql
-- ============================================
