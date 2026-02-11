-- Create test@readyroad.com user
-- Password: Test123!@#

USE readyroad_prod;

-- Delete if exists
DELETE FROM users WHERE email = 'test@readyroad.com';

-- Insert new user
INSERT INTO users (
    username,
    email,
    password_hash,
    full_name,
    role,
    is_active,
    is_locked,
    created_at,
    updated_at
) VALUES (
    'test',
    'test@readyroad.com',
    '$2a$10$rV8YqGZ6H.xQX5fR7gZ9XeJVQ8LJ6kZkY3r0nF5K3H.pL0WqVnM2G',
    'Test User',
    'STUDENT',
    1,
    0,
    NOW(),
    NOW()
);

-- Verify
SELECT id, username, email, role FROM users WHERE email = 'test@readyroad.com';
