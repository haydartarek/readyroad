-- Phase 3A: distinguish a question that was actually presented from one answered later.
-- Existing history remains untouched because its true presentation time cannot be inferred.

ALTER TABLE exam_simulation_questions
    ADD COLUMN presented_at TIMESTAMP(6) WITHOUT TIME ZONE;

ALTER TABLE user_question_history
    ADD COLUMN last_presented_at TIMESTAMP(6) WITHOUT TIME ZONE,
    ADD COLUMN times_presented INTEGER NOT NULL DEFAULT 0,
    ALTER COLUMN last_shown_at DROP NOT NULL,
    ALTER COLUMN last_shown_at DROP DEFAULT,
    ALTER COLUMN times_shown SET DEFAULT 0,
    ADD CONSTRAINT chk_uqh_times_presented CHECK (times_presented >= 0);

COMMENT ON COLUMN exam_simulation_questions.presented_at IS
    'First time this question was actually presented in this persisted exam attempt';
COMMENT ON COLUMN user_question_history.last_shown_at IS
    'Legacy compatibility timestamp; new selection logic uses last_presented_at';
COMMENT ON COLUMN user_question_history.times_shown IS
    'Legacy compatibility counter; new analytics uses times_presented';
COMMENT ON COLUMN user_question_history.last_presented_at IS
    'Latest verified presentation time; historical values are not inferred';
COMMENT ON COLUMN user_question_history.times_presented IS
    'Verified presentation count recorded after Phase 3A';
