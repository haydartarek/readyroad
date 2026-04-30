-- ====================================================================
-- ReadyRoad - Refresh Traffic Sign Catalog
-- ====================================================================
-- Purpose:
--   Clear traffic_signs and prepare a fresh reload from the canonical
--   signs source used by the application startup/import pipeline.
--
-- WARNING:
--   This will DELETE all traffic signs data permanently.
--   Run only in a controlled maintenance context.
-- ====================================================================

USE readyroad_prod;

-- ====================
-- STEP 1: Pre-check
-- ====================

-- Check how many rows will be deleted
SELECT 
    COUNT(*)                            AS total_signs,
    SUM(CASE WHEN is_active = 1 THEN 1 ELSE 0 END) AS active_signs,
    COUNT(DISTINCT category_id)         AS categories_affected
FROM traffic_signs;

-- Check for dependent tables BEFORE truncating
SELECT COUNT(*) AS dependent_questions 
FROM questions 
WHERE traffic_sign_id IS NOT NULL;

-- ====================
-- STEP 2: Disable FK & Truncate
-- ====================

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE traffic_signs;

SET FOREIGN_KEY_CHECKS = 1;

SELECT '✅ traffic_signs cleared successfully' AS status;

-- ====================
-- STEP 3: Verify
-- ====================

SELECT 
    COUNT(*) AS remaining_rows,
    CASE 
        WHEN COUNT(*) = 0 THEN '✅ Table is empty - ready for reload'
        ELSE '❌ Table still has data - check manually'
    END AS verification
FROM traffic_signs;

SELECT 'Restart Spring Boot to reload from signs.json via DataInitializer' AS next_step;
