-- Preserve historical exam-answer references when an admin removes an option.
ALTER TABLE quiz_answer_options
    ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE quiz_answer_options
    DROP CONSTRAINT IF EXISTS uq_question_display_order;

CREATE UNIQUE INDEX uq_question_active_display_order
    ON quiz_answer_options (question_id, display_order)
    WHERE is_active = TRUE;

CREATE INDEX idx_quiz_answer_options_active_question
    ON quiz_answer_options (question_id, is_active);
