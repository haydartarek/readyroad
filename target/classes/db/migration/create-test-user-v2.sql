-- ============================================================================
-- Create Test User for ReadyRoad Authentication Testing
-- ============================================================================
-- This script creates a test user with a known BCrypt password hash
-- to verify authentication is working correctly.
--
-- Test Credentials:
--   Username: testuser
--   Password: Test123!
--   Email: testuser@readyroad.be
--
-- BCrypt Hash Details:
--   Algorithm: BCrypt
--   Rounds: 10 (default)
--   Hash: $2a$10$rV8YqGZ6H.xQX5fR7gZ9XeJVQ8LJ6kZkY3r0nF5K3H.pL0WqVnM2G
--
-- Usage:
--   1. Connect to your MySQL database
--   2. Select the readyroad database: USE readyroad;
--   3. Run this script
--   4. Test login with username "testuser" and password "Test123!"
-- ============================================================================

USE readyroad;

-- Delete existing testuser if exists (to avoid duplicate key errors)
DELETE FROM users WHERE username = 'testuser' OR email = 'testuser@readyroad.be';

-- Insert test user with BCrypt password hash
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
    'testuser',
    'testuser@readyroad.be',
    '$2a$10$rV8YqGZ6H.xQX5fR7gZ9XeJVQ8LJ6kZkY3r0nF5K3H.pL0WqVnM2G', -- Password: Test123!
    'Test User',
    'STUDENT',
    1,
    0,
    NOW(),
    NOW()
);

-- Verify the user was created
SELECT 
    id,
    username,
    email,
    SUBSTRING(password_hash, 1, 10) as hash_prefix,
    LENGTH(password_hash) as hash_length,
    role,
    is_active,
    is_locked,
    created_at
FROM users 
WHERE username = 'testuser';

-- Expected output:
-- hash_prefix: $2a$10$rV8
-- hash_length: 60
-- role: STUDENT
-- is_active: 1
-- is_locked: 0
