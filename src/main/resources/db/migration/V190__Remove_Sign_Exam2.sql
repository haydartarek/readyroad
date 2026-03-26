-- =============================================================================
-- V190__Remove_Sign_Exam2.sql
-- =============================================================================
-- Removes all exam_number=2 data from the sign quiz system.
-- After this migration each sign has exactly one exam (exam_number=1).
-- Also drops the exam_number column from sign_exam_results since
-- there is now only one exam per sign.
-- =============================================================================

-- Step 1: Delete question assignments for exam_number=2 exams
DELETE seq
FROM sign_exam_questions seq
         INNER JOIN sign_exams se ON seq.exam_id = se.id
WHERE se.exam_number = 2;

-- Step 2: Delete exam_number=2 rows from sign_exams
DELETE FROM sign_exams WHERE exam_number = 2;

-- Step 3: Delete exam_number=2 rows from sign_exam_results
DELETE FROM sign_exam_results WHERE exam_number = 2;

-- Step 4: Drop the composite index that references exam_number
ALTER TABLE sign_exam_results DROP INDEX idx_ser_user_sign_exam;

-- Step 5: Drop the exam_number column (no longer needed — only exam_1 exists)
ALTER TABLE sign_exam_results DROP COLUMN exam_number;
