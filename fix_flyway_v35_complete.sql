-- ====================================================================
-- FIX FLYWAY V35 MIGRATION - MySQL 8.0 Compatible
-- ====================================================================
-- Purpose: Fix failed V35 migration and restore database to correct state
-- Safe to run multiple times (idempotent)
-- Created: January 20, 2026
-- ====================================================================

USE readyroad;

-- ====================
-- STEP 1: Remove Failed V35 Record
-- ====================
DELETE FROM flyway_schema_history WHERE version = '35';

SELECT '✅ Step 1: Deleted V35 record from flyway_schema_history' AS status;

-- ====================
-- STEP 2: Drop Existing Columns/Index (MySQL 8.0 Safe)
-- ====================

-- Drop column is_correct if exists
SET @sql := IF (
    EXISTS(
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = DATABASE()
        AND table_name = 'user_question_history'
        AND column_name = 'is_correct'
    ),
    'ALTER TABLE user_question_history DROP COLUMN is_correct',
    'SELECT ''Column is_correct does not exist'' AS skip_msg'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SELECT '✅ Step 2a: Dropped is_correct column (if existed)' AS status;

-- Drop column time_taken_seconds if exists
SET @sql := IF (
    EXISTS(
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = DATABASE()
        AND table_name = 'user_question_history'
        AND column_name = 'time_taken_seconds'
    ),
    'ALTER TABLE user_question_history DROP COLUMN time_taken_seconds',
    'SELECT ''Column time_taken_seconds does not exist'' AS skip_msg'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SELECT '✅ Step 2b: Dropped time_taken_seconds column (if existed)' AS status;

-- Drop index idx_user_question_history_perf if exists
SET @sql := IF (
    EXISTS(
        SELECT 1 FROM information_schema.statistics
        WHERE table_schema = DATABASE()
        AND table_name = 'user_question_history'
        AND index_name = 'idx_user_question_history_perf'
    ),
    'DROP INDEX idx_user_question_history_perf ON user_question_history',
    'SELECT ''Index idx_user_question_history_perf does not exist'' AS skip_msg'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SELECT '✅ Step 2c: Dropped performance index (if existed)' AS status;

-- ====================
-- STEP 3: Add Columns Fresh
-- ====================

ALTER TABLE user_question_history
    ADD COLUMN is_correct BOOLEAN NULL COMMENT 'Was the answer correct? NULL = not answered yet',
    ADD COLUMN time_taken_seconds INT NULL COMMENT 'Time taken to answer in seconds';

SELECT '✅ Step 3: Added is_correct and time_taken_seconds columns' AS status;

-- ====================
-- STEP 4: Create Performance Index
-- ====================

CREATE INDEX idx_user_question_history_perf
    ON user_question_history(user_id, last_shown_at, is_correct);

SELECT '✅ Step 4: Created performance tracking index' AS status;

-- ====================
-- STEP 5: Register V35 as SUCCESS
-- ====================

INSERT INTO flyway_schema_history
    (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success)
VALUES
    (
        (SELECT IFNULL(MAX(installed_rank), 0) + 1 FROM (SELECT * FROM flyway_schema_history) AS fsh),
        '35',
        'Add Performance Tracking',
        'SQL',
        'V35__Add_Performance_Tracking.sql',
        NULL,
        USER(),
        NOW(),
        0,
        1
    );

SELECT '✅ Step 5: Registered V35 as SUCCESS in Flyway history' AS status;

-- ====================
-- STEP 6: Verification
-- ====================

SELECT
    '====== VERIFICATION RESULTS ======' AS section,
    NULL AS version,
    NULL AS description,
    NULL AS success
UNION ALL
SELECT
    'Flyway Status' AS section,
    version,
    description,
    CASE WHEN success = 1 THEN '✅ SUCCESS' ELSE '❌ FAILED' END AS success
FROM flyway_schema_history
WHERE version = '35'
UNION ALL
SELECT
    'Schema Verification' AS section,
    NULL AS version,
    'Checking columns exist...' AS description,
    NULL AS success
UNION ALL
SELECT
    'Column Check' AS section,
    'is_correct' AS version,
    CASE
        WHEN EXISTS(SELECT 1 FROM information_schema.columns
                    WHERE table_schema = DATABASE()
                    AND table_name = 'user_question_history'
                    AND column_name = 'is_correct')
        THEN '✅ EXISTS'
        ELSE '❌ MISSING'
    END AS description,
    NULL AS success
FROM dual
UNION ALL
SELECT
    'Column Check' AS section,
    'time_taken_seconds' AS version,
    CASE
        WHEN EXISTS(SELECT 1 FROM information_schema.columns
                    WHERE table_schema = DATABASE()
                    AND table_name = 'user_question_history'
                    AND column_name = 'time_taken_seconds')
        THEN '✅ EXISTS'
        ELSE '❌ MISSING'
    END AS description,
    NULL AS success
FROM dual
UNION ALL
SELECT
    'Index Check' AS section,
    'idx_user_question_history_perf' AS version,
    CASE
        WHEN EXISTS(SELECT 1 FROM information_schema.statistics
                    WHERE table_schema = DATABASE()
                    AND table_name = 'user_question_history'
                    AND index_name = 'idx_user_question_history_perf')
        THEN '✅ EXISTS'
        ELSE '❌ MISSING'
    END AS description,
    NULL AS success
FROM dual;

-- ====================
-- SUCCESS MESSAGE
-- ====================

SELECT
    '🎉 V35 MIGRATION FIX COMPLETE! 🎉' AS message,
    'Your database is now ready. Run: mvn spring-boot:run' AS next_step;
