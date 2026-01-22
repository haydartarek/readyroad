-- ========================================
-- V28: Add Authentication Support to Users Table
-- ========================================
-- Date: 2026-01-18
-- Description: Add username, role, and lock fields for JWT authentication
-- ========================================

-- Add username column (for authentication)
ALTER TABLE users
ADD COLUMN username VARCHAR(50) UNIQUE AFTER id;

-- Add role column
ALTER TABLE users
ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER' AFTER password_hash;

-- Add is_locked column
ALTER TABLE users
ADD COLUMN is_locked BOOLEAN NOT NULL DEFAULT FALSE AFTER is_active;

-- Create index on username for faster lookups
CREATE INDEX idx_users_username ON users(username);

-- Create index on email for faster lookups (if not already exists)
-- Check if index exists first, create only if needed
SET @sql = IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
     WHERE TABLE_SCHEMA = DATABASE()
     AND TABLE_NAME = 'users'
     AND INDEX_NAME = 'idx_users_email') = 0,
    'CREATE INDEX idx_users_email ON users(email)',
    'SELECT "Index idx_users_email already exists"'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Update existing users with username from email (if any exist)
UPDATE users
SET username = SUBSTRING_INDEX(email, '@', 1)
WHERE username IS NULL;

-- Add comment to table
ALTER TABLE users COMMENT = 'Users table with JWT authentication support';
