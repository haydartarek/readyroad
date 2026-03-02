-- ========================================
-- V28: Add Authentication Support to Users Table
-- ========================================
-- Date: 2026-01-18 | Revised: 2026-02-27
-- Description: Add username, role, and lock fields for JWT authentication
-- ========================================

USE readyroad_prod;

-- Add username column (for authentication)
ALTER TABLE users
ADD COLUMN IF NOT EXISTS username VARCHAR(50) UNIQUE AFTER id;

-- Add role column
ALTER TABLE users
ADD COLUMN IF NOT EXISTS role VARCHAR(20) NOT NULL DEFAULT 'USER' AFTER password_hash;

-- Add is_locked column
ALTER TABLE users
ADD COLUMN IF NOT EXISTS is_locked BOOLEAN NOT NULL DEFAULT FALSE AFTER is_active;

-- Create indexes (idempotent using dynamic SQL)
SET @i1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='users' AND INDEX_NAME='idx_users_username');
SET @s1 = IF(@i1=0,'CREATE INDEX idx_users_username ON users(username)','SELECT 1'); PREPARE _s FROM @s1; EXECUTE _s; DEALLOCATE PREPARE _s;

SET @i2 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='users' AND INDEX_NAME='idx_users_email');
SET @s2 = IF(@i2=0,'CREATE INDEX idx_users_email ON users(email)','SELECT 1'); PREPARE _s FROM @s2; EXECUTE _s; DEALLOCATE PREPARE _s;

SET @i3 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='users' AND INDEX_NAME='idx_users_role');
SET @s3 = IF(@i3=0,'CREATE INDEX idx_users_role ON users(role)','SELECT 1'); PREPARE _s FROM @s3; EXECUTE _s; DEALLOCATE PREPARE _s;

-- Update existing users with username from email (safe)
UPDATE users
SET username  = SUBSTRING_INDEX(email, '@', 1),
    role      = 'USER',
    is_locked = FALSE
WHERE username IS NULL OR username = '';

-- Insert default admin user (safe - ignores duplicates)
INSERT IGNORE INTO users (email, username, full_name, password_hash, role, is_active, is_locked, created_at)
VALUES (
    'admin@readyroad.be', 'admin', 'ReadyRoad Admin',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
    'ADMIN', TRUE, FALSE, NOW()
);

-- Roles reference table
CREATE TABLE IF NOT EXISTS user_roles (
    id          TINYINT AUTO_INCREMENT PRIMARY KEY,
    role_name   VARCHAR(20)  NOT NULL UNIQUE,
    description VARCHAR(100),
    permissions JSON,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_role_name (role_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Populate standard roles
INSERT IGNORE INTO user_roles (role_name, description, permissions) VALUES
('USER',       'Standard learner user',    '{"quiz":true,"profile":true,"progress":true}'),
('ADMIN',      'Full system administrator','{"all":true}'),
('INSTRUCTOR', 'Lesson content manager',   '{"lessons":true,"questions":true,"users:read":true}');

-- Add table comment
ALTER TABLE users COMMENT = 'Users table with JWT authentication support (roles: USER, ADMIN, INSTRUCTOR)';
