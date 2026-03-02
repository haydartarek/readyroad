USE readyroad_prod;

CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

CREATE TABLE IF NOT EXISTS user_roles (
    id          TINYINT AUTO_INCREMENT PRIMARY KEY,
    role_name   VARCHAR(20)  NOT NULL UNIQUE,
    description VARCHAR(100),
    permissions JSON,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_role_name (role_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO user_roles (role_name, description, permissions) VALUES
('USER',       'Standard learner user',    '{"quiz":true,"profile":true,"progress":true}'),
('ADMIN',      'Full system administrator','{"all":true}'),
('INSTRUCTOR', 'Lesson content manager',   '{"lessons":true,"questions":true,"users:read":true}');

INSERT IGNORE INTO users (email, username, full_name, password_hash, role, is_active, is_locked, created_at)
VALUES (
    'admin@readyroad.be', 'admin_be', 'ReadyRoad Admin',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
    'ADMIN', TRUE, FALSE, NOW()
);

SELECT 'V94 OK' as result;
