ALTER TABLE exam_simulation_answers
    ADD COLUMN IF NOT EXISTS correct_option_id BIGINT;

-- A correct historical selection proves the exact correct option.
UPDATE exam_simulation_answers
SET correct_option_id = selected_option_id
WHERE correct_option_id IS NULL
  AND is_correct = TRUE;

-- Older incorrect answers did not snapshot the correct option. Backfill them
-- deterministically from the question's current canonical correct option.
UPDATE exam_simulation_answers answer
SET correct_option_id = (
    SELECT option.id
    FROM quiz_answer_options option
    WHERE option.question_id = answer.question_id
      AND option.is_correct = TRUE
    ORDER BY
      CASE WHEN option.is_active = TRUE THEN 0 ELSE 1 END,
      option.display_order,
      option.id
    LIMIT 1
)
WHERE answer.correct_option_id IS NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_esa_correct_option'
    ) THEN
        ALTER TABLE exam_simulation_answers
            ADD CONSTRAINT fk_esa_correct_option
            FOREIGN KEY (correct_option_id)
            REFERENCES quiz_answer_options (id)
            ON DELETE SET NULL;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_exam_answers_correct_option
    ON exam_simulation_answers (correct_option_id);
