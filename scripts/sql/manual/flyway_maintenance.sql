-- ====================================================================
-- ReadyRoad — Flyway Maintenance
-- ====================================================================
-- Purpose:
--   1. Inspect Flyway history
--   2. Remove a FAILED migration record safely so Flyway can retry it
--
-- Usage:
--   - Run against readyroad_prod as an admin user.
--   - Never delete a SUCCESS record from flyway_schema_history.
-- ====================================================================

USE readyroad_prod;

-- ============================================================
-- SECTION 1 — Inspect recent Flyway history
-- ============================================================

SELECT
    installed_rank,
    version,
    description,
    type,
    script,
    installed_by,
    installed_on,
    execution_time,
    CASE WHEN success = 1 THEN 'SUCCESS' ELSE 'FAILED' END AS status
FROM flyway_schema_history
ORDER BY installed_rank DESC
LIMIT 10;

SELECT
    CASE WHEN success = 1 THEN 'SUCCESS' ELSE 'FAILED' END AS status,
    COUNT(*) AS count
FROM flyway_schema_history
GROUP BY success
ORDER BY success DESC;

-- ============================================================
-- SECTION 2 — Repair a failed Flyway record
-- ============================================================
-- Set only the failed version number below, then run the repair
-- block. Leave it unchanged if you only want the history queries.

SET @failed_version = '33';

SELECT
    installed_rank,
    version,
    description,
    script,
    installed_by,
    installed_on,
    execution_time,
    CASE WHEN success = 1 THEN 'SUCCESS' ELSE 'FAILED' END AS result
FROM flyway_schema_history
WHERE version = @failed_version;

DELETE FROM flyway_schema_history
WHERE version = @failed_version
  AND success = 0
LIMIT 1;

SELECT
    CASE
        WHEN NOT EXISTS (
            SELECT 1
            FROM flyway_schema_history
            WHERE version = @failed_version
              AND success = 0
        )
        THEN CONCAT('OK: failed record for V', @failed_version, ' removed')
        ELSE CONCAT('CHECK: failed record for V', @failed_version, ' still exists')
    END AS verification;
