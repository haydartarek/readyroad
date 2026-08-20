-- Forward-only support for theory questions finalized by the 15-second timer.
-- Existing rows all have a selected option and remain factual ANSWERED records.

ALTER TABLE exam_simulation_answers
    DROP CONSTRAINT fk_esa_selected_option_question;

ALTER TABLE exam_simulation_answers
    ALTER COLUMN selected_option_id DROP NOT NULL,
    ADD COLUMN answer_state VARCHAR(20) NOT NULL DEFAULT 'ANSWERED',
    ADD COLUMN timed_out_at TIMESTAMP(6) WITHOUT TIME ZONE;

ALTER TABLE exam_simulation_answers
    ADD CONSTRAINT fk_esa_selected_option_question
        FOREIGN KEY (question_id, selected_option_id)
        REFERENCES quiz_answer_options (question_id, id)
        ON DELETE RESTRICT,
    ADD CONSTRAINT chk_exam_answer_state
        CHECK (
            (answer_state = 'ANSWERED'
                AND selected_option_id IS NOT NULL
                AND timed_out_at IS NULL)
            OR
            (answer_state = 'TIMED_OUT'
                AND selected_option_id IS NULL
                AND is_correct = FALSE
                AND timed_out_at IS NOT NULL)
        );

COMMENT ON COLUMN exam_simulation_answers.answer_state IS
    'ANSWERED has a selected option; TIMED_OUT records a finalized unanswered question.';

COMMENT ON COLUMN exam_simulation_answers.timed_out_at IS
    'Timestamp of the 15-second timeout; NULL for selected answers and all legacy rows.';
