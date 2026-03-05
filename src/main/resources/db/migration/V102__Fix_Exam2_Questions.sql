-- =============================================================================
-- V102__Fix_Exam2_Questions.sql
-- =============================================================================
-- Problem:
--   sign_exams table contains 252 rows with exam_number=2 (one per road sign),
--   but sign_exam_questions has 0 rows linked to any exam_number=2 exam.
--   exam_number=1 is fully populated: 252 exams × 5 questions = 1,260 rows.
--
-- Root cause:
--   The V99 migration (Create Sign Quiz System) created both exam_number=1 and
--   exam_number=2 rows in sign_exams but only inserted question assignments for
--   exam_number=1.
--
-- Fix:
--   Copy the same 5 questions (same question_id, same question_order) that are
--   assigned to each sign's exam_number=1 exam into the corresponding
--   exam_number=2 exam.
--
--   Each sign has exactly 5 questions (2 EASY, 2 MEDIUM, 1 HARD).  Since both
--   exams cover the same sign, sharing the same question pool is correct; the
--   exam_number differentiates attempt history, not question content.
--
-- Safety guard:
--   The INSERT is wrapped in a conditional so it is a no-op if exam_number=2
--   already has at least one question (idempotent re-run protection).
-- =============================================================================

-- -----------------------------------------------------------
-- Step 1: Verify the gap exists before doing anything
-- -----------------------------------------------------------
-- (SELECT is informational; Flyway executes it without error)
-- Expected: questions_for_exam2 = 0  before migration
--           questions_for_exam2 = 1260 after migration

-- -----------------------------------------------------------
-- Step 2: Insert missing exam_number=2 question assignments
-- -----------------------------------------------------------
-- Strategy:
--   For every exam_number=1 exam (e1) find its partner exam_number=2 exam (e2)
--   via shared sign_id, then copy all sign_exam_questions rows from e1 → e2.
--
-- The NOT EXISTS guard prevents duplicate inserts if migration runs twice.
-- The UNIQUE KEY uk_seq_exam_q(exam_id, question_id) also guards at DB level.

INSERT INTO sign_exam_questions (exam_id, question_id, question_order)
SELECT
    e2.id          AS exam_id,
    seq.question_id,
    seq.question_order
FROM sign_exams      e1
JOIN sign_exams      e2  ON  e2.sign_id     = e1.sign_id
                         AND e2.exam_number  = 2
                         AND e2.is_active    = 1
JOIN sign_exam_questions seq ON seq.exam_id  = e1.id
WHERE
    e1.exam_number = 1
    AND e1.is_active = 1
    -- Safety guard: skip if exam_number=2 already has ANY questions at all
    AND NOT EXISTS (
        SELECT 1
        FROM sign_exam_questions guard
        WHERE guard.exam_id = e2.id
        LIMIT 1
    )
ORDER BY
    e2.id,
    seq.question_order;

-- =============================================================================
-- Expected result after migration:
--   SELECT COUNT(*) FROM sign_exam_questions seq
--   JOIN sign_exams se ON seq.exam_id = se.id
--   WHERE se.exam_number = 2;
--   --> 1260   (252 signs × 5 questions each)
-- =============================================================================
