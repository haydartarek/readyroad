-- ==========================================================================
-- V193 — Enforce max 3 public choices for developer assessment questions
-- Keep exactly one correct choice and at most two incorrect choices per
-- question to match the project-wide UX rule: <= 3 choices, 1 correct answer.
-- ==========================================================================

DELETE c
FROM dev_exam_choices c
JOIN (
    SELECT id
    FROM (
        SELECT
            id,
            ROW_NUMBER() OVER (
                PARTITION BY question_id
                ORDER BY
                    CASE WHEN is_correct THEN 0 ELSE 1 END,
                    sort_order,
                    id
            ) AS keep_rank
        FROM dev_exam_choices
    ) ranked
    WHERE keep_rank > 3
) extra ON extra.id = c.id;

UPDATE dev_exam_choices c
JOIN (
    SELECT
        id,
        ROW_NUMBER() OVER (
            PARTITION BY question_id
            ORDER BY sort_order, id
        ) AS new_sort_order
    FROM dev_exam_choices
) ranked ON ranked.id = c.id
SET c.sort_order = ranked.new_sort_order;
