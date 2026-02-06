-- ============================================
-- ReadyRoad Complete Database Setup
-- ============================================
-- Purpose: Setup ALL required databases with permissions
-- User: haydar
-- Password: Hh06101987@
-- ============================================

-- 1. Create development database (readyroad)
CREATE DATABASE IF NOT EXISTS readyroad
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- 2. Create production database (readyroad_prod)
CREATE DATABASE IF NOT EXISTS readyroad_prod
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- 3. Grant all privileges on BOTH databases to haydar
GRANT ALL PRIVILEGES ON readyroad.* TO 'haydar'@'localhost';
GRANT ALL PRIVILEGES ON readyroad_prod.* TO 'haydar'@'localhost';

-- 4. Apply changes
FLUSH PRIVILEGES;

-- 5. Verify grants
SHOW GRANTS FOR 'haydar'@'localhost';

-- 6. Show created databases
SELECT
    SCHEMA_NAME as 'Database',
    DEFAULT_CHARACTER_SET_NAME as 'Charset',
    DEFAULT_COLLATION_NAME as 'Collation'
FROM INFORMATION_SCHEMA.SCHEMATA
WHERE SCHEMA_NAME IN ('readyroad', 'readyroad_prod')
ORDER BY SCHEMA_NAME;

-- ============================================
-- Expected Output:
-- ============================================
-- Databases created:
--   - readyroad (development)
--   - readyroad_prod (production)
--
-- Character set: utf8mb4
-- Collation: utf8mb4_unicode_ci
--
-- Grants for haydar@localhost:
--   - GRANT ALL PRIVILEGES ON `readyroad`.* TO `haydar`@`localhost`
--   - GRANT ALL PRIVILEGES ON `readyroad_prod`.* TO `haydar`@`localhost`
-- ============================================
