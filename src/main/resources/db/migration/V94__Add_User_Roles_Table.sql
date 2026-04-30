-- ========================================
-- V94: Add user_roles table and missing indexes
-- ========================================
-- Completes what V28 originally missed in the live DB:
--   • idx_users_role index
--   • user_roles reference table + seed data
--   • user_roles seed data
-- ========================================

-- Role index (speeds up Spring Security role lookups)
SET @idx_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='users' AND INDEX_NAME='idx_users_role');
SET @sql_idx = IF(@idx_exists = 0, 'CREATE INDEX idx_users_role ON users(role)', 'SELECT 1');
PREPARE _stmt FROM @sql_idx; EXECUTE _stmt; DEALLOCATE PREPARE _stmt;

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

-- Default admin creation is handled by DefaultAdminInitializer using
-- ADMIN_DEFAULT_PASSWORD / readyroad.admin.default-password.
-- Do not seed production credentials from Flyway.
