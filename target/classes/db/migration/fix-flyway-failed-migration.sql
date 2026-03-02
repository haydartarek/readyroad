-- ====================================================================
-- ReadyRoad — Fix Any Failed Flyway Migration (Template)
-- ====================================================================
-- Purpose : Remove a FAILED (success = 0) Flyway history record so
--           Spring Boot can retry the migration on next startup.
--
-- Usage:
--   1. Set @failed_version to the version you want to repair.
--   2. Run the script against readyroad_prod as admin/root.
--   3. Fix the SQL migration file itself (if the SQL was wrong).
--   4. Restart the Spring Boot backend — Flyway will retry.
--
-- ⚠️  NEVER delete a SUCCESS record (success = 1).
--     Doing so forces Flyway to re-run an already-applied migration
--     and will cause duplicate-data or schema errors.
--
-- Past uses of this script:
--   V33  — add_missing_columns  (duplicate column error)
--   V77  — seed_data            (syntax error in SQL)
--   V92  — seed_G_Z_M_H        (unescaped apostrophe)
-- ====================================================================

USE readyroad_prod;

-- ============================================================
-- ⚙️  CONFIGURATION — change only this line
-- ============================================================

SET @failed_version = '33';   -- ← replace with the failed version number

-- ============================================================
-- STEP 1 — Pre-check: confirm a failed record exists
-- ============================================================

SELECT
    installed_rank,
    version,
    description,
    script,
    installed_by,
    installed_on,
    execution_time,
    CASE WHEN success = 1 THEN '✅ SUCCESS' ELSE '❌ FAILED' END AS result
FROM flyway_schema_history
WHERE version = @failed_version;

-- ============================================================
-- STEP 2 — Delete the failed record (only if success = 0)
-- ============================================================

DELETE FROM flyway_schema_history
WHERE version     = @failed_version
  AND success     = 0
LIMIT 1;

-- ============================================================
-- STEP 3 — Verify removal
-- ============================================================

SELECT
    CASE
        WHEN NOT EXISTS (
            SELECT 1 FROM flyway_schema_history
            WHERE version = @failed_version AND success = 0
        )
        THEN CONCAT('✅ V', @failed_version, ' failed record removed — safe to restart backend')
        ELSE CONCAT('❌ V', @failed_version, ' still present — check manually')
    END AS verification;

-- ============================================================
-- STEP 4 — Show last 10 migrations for context
-- ============================================================

SELECT
    installed_rank,
    version,
    description,
    CASE WHEN success = 1 THEN '✅ OK' ELSE '❌ FAILED' END AS status,
    installed_on,
    execution_time
FROM flyway_schema_history
ORDER BY installed_rank DESC
LIMIT 10;
