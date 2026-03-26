-- ====================================================================
-- ReadyRoad Database Setup — Universal
-- ====================================================================
-- Purpose : Create readyroad_prod database and grant privileges
--           to the haydar app user for BOTH Docker and bare-metal.
--
-- Usage:
--   Run as MySQL root / admin user ONLY — never as the app user.
--
--   Docker/Remote host : the '@'%'' entry covers all hosts.
--   Bare-metal / local  : the '@'localhost'' entry is required.
--   Both entries are created unconditionally so one script works
--   in all environments.
--
-- ⚠️  Replace STRONG_PASSWORD_HERE with the real password before
--     running, or pass it via an environment variable / secrets
--     manager.  Never commit plaintext credentials.
-- ====================================================================

-- ============================================================
-- STEP 1 — Create Application Database
-- ============================================================

CREATE DATABASE IF NOT EXISTS readyroad_prod
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

SELECT '✅ Step 1: Database readyroad_prod created (or already exists)' AS status;

-- ============================================================
-- STEP 2 — Create App User (Docker / remote connections — '@'%'')
-- ============================================================

CREATE USER IF NOT EXISTS 'haydar'@'%'
    IDENTIFIED BY 'STRONG_PASSWORD_HERE';

SELECT '✅ Step 2a: User haydar@% created (or already exists)' AS status;

-- ============================================================
-- STEP 3 — Create App User (bare-metal / localhost)
-- ============================================================

CREATE USER IF NOT EXISTS 'haydar'@'localhost'
    IDENTIFIED BY 'STRONG_PASSWORD_HERE';

SELECT '✅ Step 3a: User haydar@localhost created (or already exists)' AS status;

-- ============================================================
-- STEP 4 — Grant Least-Privilege Access
-- ============================================================
-- Grants cover what Spring Boot + Flyway need:
--   SELECT/INSERT/UPDATE/DELETE  — normal app DML
--   CREATE/ALTER/DROP            — Flyway schema migrations
--   INDEX/REFERENCES             — foreign keys & index creation

GRANT SELECT, INSERT, UPDATE, DELETE,
      CREATE, ALTER, DROP,
      INDEX, REFERENCES
    ON readyroad_prod.* TO 'haydar'@'%';

GRANT SELECT, INSERT, UPDATE, DELETE,
      CREATE, ALTER, DROP,
      INDEX, REFERENCES
    ON readyroad_prod.* TO 'haydar'@'localhost';

FLUSH PRIVILEGES;

SELECT '✅ Step 4: Privileges granted to haydar@% and haydar@localhost' AS status;

-- ============================================================
-- STEP 5 — Verify
-- ============================================================

SHOW GRANTS FOR 'haydar'@'%';
SHOW GRANTS FOR 'haydar'@'localhost';

-- Database metadata check
SELECT
    SCHEMA_NAME                  AS `Database`,
    DEFAULT_CHARACTER_SET_NAME   AS `Charset`,
    DEFAULT_COLLATION_NAME       AS `Collation`
FROM information_schema.SCHEMATA
WHERE SCHEMA_NAME = 'readyroad_prod';

USE readyroad_prod;
SHOW TABLES;
