-- V43: Create test user for development/QA
-- Password: Test123! (BCrypt encoded by PasswordHashGenerator.java)
-- This migration ensures a known test user always exists

INSERT INTO users (username, email, password_hash, full_name, role, is_active, is_locked, created_at, updated_at)
SELECT 'testuser', 'testuser@readyroad.be', '$2a$10$afbMILokv43Q85zLSLy0qOAdSOrQ91BK9quWa6.Vxz89pbkqJXDH6', 'Test User', 'USER', 1, 0, NOW(), NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'testuser');
