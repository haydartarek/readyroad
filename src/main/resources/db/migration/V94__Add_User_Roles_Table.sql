-- ========================================
-- V94: Add user_roles table and missing indexes
-- ========================================
-- Completes what V28 originally missed in the live DB:
--   • idx_users_role index
--   • user_roles reference table + seed data
--   • admin@readyroad.be default admin user
-- ========================================

USE readyroad_prod;

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

-- Ensure admin@readyroad.be exists (default password: 'password')
INSERT IGNORE INTO users (email, username, full_name, password_hash, role, is_active, is_locked, created_at)
VALUES (
    'admin@readyroad.be', 'admin_be', 'ReadyRoad Admin',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
    'ADMIN', TRUE, FALSE, NOW()
);
