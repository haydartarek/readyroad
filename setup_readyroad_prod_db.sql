-- ========================================
-- Setup readyroad_prod Database
-- ========================================
-- Run this script as MySQL root user to create the database and grant permissions

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS readyroad_prod
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- Grant all privileges to haydar user
GRANT ALL PRIVILEGES ON readyroad_prod.* TO 'haydar'@'localhost';

-- Flush privileges to apply changes
FLUSH PRIVILEGES;

-- Verify grants
SHOW GRANTS FOR 'haydar'@'localhost';

-- Switch to the new database
USE readyroad_prod;

-- Show tables (should show existing tables after Flyway migration)
SHOW TABLES;
