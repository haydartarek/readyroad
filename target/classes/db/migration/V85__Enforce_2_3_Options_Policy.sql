-- ═══════════════════════════════════════════════════════════════════
-- V85: Enforce 2-3 Options Policy for MULTIPLE_CHOICE Questions
-- ═══════════════════════════════════════════════════════════════════
-- Belgian standard: each MULTIPLE_CHOICE question must have 2 or 3
-- answer options with exactly 1 marked correct.
--
-- This migration:
--   1. Reduces 4-option questions to 3 (removes the 4th by highest displayOrder,
--      preferring to drop a non-correct option)
--   2. Deactivates questions with 0 or >1 correct options
--   3. Deactivates questions with <2 or >3 options (after cleanup)
--   4. Normalizes displayOrder to 1..N for every question
--   5. Adds a UNIQUE constraint on (question_id, display_order)
-- ═══════════════════════════════════════════════════════════════════

-- ───────────────────────────────────────────────────────────────────
-- Step 1: For questions with exactly 4 options, delete the 4th option.
--         Prefer removing a non-correct option with the highest displayOrder.
-- ───────────────────────────────────────────────────────────────────

-- Delete the "worst" 4th option: non-correct with highest display_order first
DELETE qao FROM quiz_answer_options qao
INNER JOIN (
    SELECT qao_inner.id AS option_id
    FROM quiz_answer_options qao_inner
    INNER JOIN (
        -- Questions that have exactly 4 options
        SELECT question_id
        FROM quiz_answer_options
        GROUP BY question_id
        HAVING COUNT(*) = 4
    ) q4 ON qao_inner.question_id = q4.question_id
    WHERE qao_inner.id = (
        -- Pick the single option to remove: prefer non-correct with highest displayOrder
        SELECT id FROM (
            SELECT id
            FROM quiz_answer_options
            WHERE question_id = q4.question_id
            ORDER BY is_correct ASC, display_order DESC
            LIMIT 1
        ) subq
    )
) to_delete ON qao.id = to_delete.option_id;

-- ───────────────────────────────────────────────────────────────────
-- Step 2: Deactivate + unpublish questions with wrong correct-answer count
-- ───────────────────────────────────────────────────────────────────

-- Questions with 0 correct options
UPDATE quiz_questions qq
SET qq.is_active = FALSE, qq.status = 'DRAFT'
WHERE qq.id IN (
    SELECT question_id FROM (
        SELECT qao.question_id, SUM(qao.is_correct) AS correct_count
        FROM quiz_answer_options qao
        GROUP BY qao.question_id
        HAVING correct_count = 0 OR correct_count IS NULL
    ) bad_q
);

-- Questions with >1 correct options
UPDATE quiz_questions qq
SET qq.is_active = FALSE, qq.status = 'DRAFT'
WHERE qq.id IN (
    SELECT question_id FROM (
        SELECT qao.question_id, SUM(qao.is_correct) AS correct_count
        FROM quiz_answer_options qao
        GROUP BY qao.question_id
        HAVING correct_count > 1
    ) bad_q
);

-- ───────────────────────────────────────────────────────────────────
-- Step 3: Deactivate questions that still have wrong option counts
--         (<2 or >3 after step 1)
-- ───────────────────────────────────────────────────────────────────

UPDATE quiz_questions qq
SET qq.is_active = FALSE, qq.status = 'DRAFT'
WHERE qq.id IN (
    SELECT question_id FROM (
        SELECT qao.question_id, COUNT(*) AS opt_count
        FROM quiz_answer_options qao
        GROUP BY qao.question_id
        HAVING opt_count < 2 OR opt_count > 3
    ) bad_q
);

-- ───────────────────────────────────────────────────────────────────
-- Step 4: Normalize displayOrder to 1-based sequential for all questions
-- ───────────────────────────────────────────────────────────────────

-- Use a temporary table to calculate the correct positions
CREATE TEMPORARY TABLE tmp_display_order AS
SELECT qao.id AS option_id,
       ROW_NUMBER() OVER (PARTITION BY qao.question_id ORDER BY qao.display_order, qao.id) AS new_order
FROM quiz_answer_options qao;

UPDATE quiz_answer_options qao
INNER JOIN tmp_display_order t ON qao.id = t.option_id
SET qao.display_order = t.new_order;

DROP TEMPORARY TABLE tmp_display_order;

-- ───────────────────────────────────────────────────────────────────
-- Step 5: Ensure display_order is NOT NULL
-- ───────────────────────────────────────────────────────────────────

UPDATE quiz_answer_options SET display_order = 1 WHERE display_order IS NULL;

ALTER TABLE quiz_answer_options MODIFY display_order INT NOT NULL DEFAULT 1;

-- ───────────────────────────────────────────────────────────────────
-- Step 6: Add UNIQUE constraint on (question_id, display_order)
-- ───────────────────────────────────────────────────────────────────

ALTER TABLE quiz_answer_options
    ADD CONSTRAINT uq_question_display_order UNIQUE (question_id, display_order);
