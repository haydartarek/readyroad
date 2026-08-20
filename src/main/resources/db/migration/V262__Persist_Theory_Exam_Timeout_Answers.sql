-- Portable forward migration for local/MySQL-compatible deployments.

ALTER TABLE exam_simulation_answers
    MODIFY COLUMN selected_option_id BIGINT NULL,
    ADD COLUMN answer_state VARCHAR(20) NOT NULL DEFAULT 'ANSWERED',
    ADD COLUMN timed_out_at DATETIME(6) NULL,
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
