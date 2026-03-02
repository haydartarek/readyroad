-- ====================================================================
-- Check Flyway Migration History - ReadyRoad
-- ====================================================================

USE readyroad;

-- Show last 10 migrations with readable status
SELECT
    installed_rank,
    version,
    description,
    type,
    script,
    installed_by,
    installed_on,
    execution_time,
    CASE WHEN success = 1 THEN '✅ SUCCESS' ELSE '❌ FAILED' END AS status
FROM flyway_schema_history
ORDER BY installed_rank DESC
LIMIT 10;

-- Summary: count success vs failed
SELECT
    CASE WHEN success = 1 THEN '✅ SUCCESS' ELSE '❌ FAILED' END AS status,
    COUNT(*) AS count
FROM flyway_schema_history
GROUP BY success
ORDER BY success DESC;
