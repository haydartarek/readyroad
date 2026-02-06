-- ============================================
-- ReadyRoad Database Setup Script
-- ============================================
-- Purpose: Create database and grant permissions
-- User: haydar
-- Password: Hh06101987@
-- ============================================

-- 1. Create database if not exists
CREATE DATABASE IF NOT EXISTS readyroad
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- 2. Grant all privileges on readyroad database to user 'haydar'
GRANT ALL PRIVILEGES ON readyroad.* TO 'haydar'@'localhost';

-- 3. If you want to grant privileges to any host (not recommended for production):
-- GRANT ALL PRIVILEGES ON readyroad.* TO 'haydar'@'%';

-- 4. Apply changes
FLUSH PRIVILEGES;

-- 5. Verify grants
SHOW GRANTS FOR 'haydar'@'localhost';

-- 6. Switch to readyroad database
USE readyroad;

-- 7. Show existing tables (if any)
SHOW TABLES;

-- ============================================
-- Expected Output:
-- ============================================
-- Database created: readyroad
-- Grants for haydar@localhost:
--   - GRANT ALL PRIVILEGES ON `readyroad`.* TO `haydar`@`localhost`
-- ============================================
