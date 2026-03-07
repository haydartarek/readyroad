-- V107 — Remove placeholder quiz questions that have "Option A/B/C" answer text.
-- These were seeded by old migrations as templates (not real questions).
-- Real questions have specific answer text like "50 km/h", "Priority road", etc.

-- Step 1: Delete answer options linked to placeholder questions.
-- Wrap in derived table to avoid MySQL "can't specify target table in FROM clause" error.
DELETE FROM quiz_answer_options
WHERE question_id IN (
    SELECT qid FROM (
        SELECT DISTINCT question_id AS qid
        FROM quiz_answer_options
        WHERE option_text_en LIKE 'Option %'
    ) AS placeholder_ids
);

-- Step 2: Delete quiz_questions that now have no remaining answer options.
-- After step 1, only the placeholder questions are left without options.
DELETE FROM quiz_questions
WHERE id IN (
    SELECT qid FROM (
        SELECT q.id AS qid
        FROM quiz_questions q
        LEFT JOIN quiz_answer_options qao ON qao.question_id = q.id
        WHERE qao.id IS NULL
    ) AS orphaned_questions
);
