-- ====================================================================
-- ReadyRoad — Create Test Users (All Environments)
-- ====================================================================
-- Purpose : Idempotent script that creates / recreates both QA test
--           accounts in one run.  Safe to re-run: users are deleted
--           first (LIMIT 1 guard) before re-insert.
--
-- ⚠️  For DEVELOPMENT / QA only — never run in production.
--
-- Accounts created:
--   1.  test       / test@readyroad.com       password: Test123!@#
--   2.  testuser   / testuser@readyroad.be    password: Test123!
--
-- NOTE:
--   ReadyRoad now uses USER as the standard learner role. This script
--   stays aligned with the current application enum and avoids the
--   older legacy learner-role wording.
--
-- BCrypt hashes below were generated at cost-factor 10.
-- To regenerate: use PasswordHashGenerator.java or
--   echo -n "Test123!@#" | htpasswd -nbBC 10 "" | tail -c +3
-- ====================================================================

USE readyroad_prod;

-- ============================================================
-- STEP 1 — Remove stale test accounts (safe, idempotent)
-- ============================================================

DELETE FROM users
WHERE email IN ('test@readyroad.com', 'testuser@readyroad.be')
LIMIT 2;

SELECT CONCAT('✅ Removed ', ROW_COUNT(), ' existing test user(s)') AS status;

-- ============================================================
-- STEP 2 — Insert test accounts
-- ============================================================

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
) VALUES
-- Account 1: used by integration tests & Postman collection
(
    'test',
    'test@readyroad.com',
    '$2a$10$rV8YqGZ6H.xQX5fR7gZ9XeJVQ8LJ6kZkY3r0nF5K3H.pL0WqVnM2G',  -- Test123!@#
    'Test User (readyroad.com)',
    'USER',
    TRUE,
    FALSE,
    NOW(),
    NOW()
),
-- Account 2: used by SmartQuiz / auth integration tests
(
    'testuser',
    'testuser@readyroad.be',
    '$2a$10$afbMILokv43Q85zLSLy0qOAdSOrQ91BK9quWa6.Vxz89pbkqJXDH6',  -- Test123!
    'Test User (readyroad.be)',
    'USER',
    TRUE,
    FALSE,
    NOW(),
    NOW()
);

SELECT CONCAT('✅ Inserted ', ROW_COUNT(), ' test user(s)') AS status;

-- ============================================================
-- STEP 3 — Verify
-- ============================================================

SELECT
    id,
    username,
    email,
    role,
    is_active,
    is_locked,
    created_at
FROM users
WHERE email IN ('test@readyroad.com', 'testuser@readyroad.be')
ORDER BY email;
