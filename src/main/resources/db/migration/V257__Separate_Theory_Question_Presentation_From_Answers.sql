-- Phase 3A portability migration. No historical timestamps are fabricated.

ALTER TABLE exam_simulation_questions
    ADD COLUMN presented_at DATETIME(6) NULL;

ALTER TABLE user_question_history
    ADD COLUMN last_presented_at DATETIME(6) NULL,
    ADD COLUMN times_presented INT NOT NULL DEFAULT 0,
    MODIFY COLUMN answered_at DATETIME(6) NULL DEFAULT NULL,
    MODIFY COLUMN last_shown_at DATETIME(6) NULL DEFAULT NULL,
    MODIFY COLUMN times_shown INT NOT NULL DEFAULT 0;
