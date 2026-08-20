-- Keep the local MySQL learning-history schema aligned with the existing
-- PostgreSQL V29 migration. Historical rows remain nullable and are not
-- assigned an invented locale.

ALTER TABLE exam_simulations
    ADD COLUMN language_code VARCHAR(2) NULL;

ALTER TABLE sign_exam_results
    ADD COLUMN language_code VARCHAR(2) NULL,
    ADD COLUMN submission_key VARCHAR(64) NULL;

ALTER TABLE sign_practice_sessions
    ADD COLUMN language_code VARCHAR(2) NULL;

ALTER TABLE sign_random_practice_sessions
    ADD COLUMN language_code VARCHAR(2) NULL;

ALTER TABLE user_lesson_progress
    ADD COLUMN language_code VARCHAR(2) NULL;

ALTER TABLE exam_simulations
    ADD CONSTRAINT chk_exam_simulations_language
        CHECK (language_code IS NULL OR language_code IN ('en', 'nl', 'fr', 'ar'));

ALTER TABLE sign_exam_results
    ADD CONSTRAINT chk_sign_exam_results_language
        CHECK (language_code IS NULL OR language_code IN ('en', 'nl', 'fr', 'ar')),
    ADD CONSTRAINT uq_sign_exam_result_submission
        UNIQUE (user_id, submission_key);

ALTER TABLE sign_practice_sessions
    ADD CONSTRAINT chk_sign_practice_sessions_language
        CHECK (language_code IS NULL OR language_code IN ('en', 'nl', 'fr', 'ar'));

ALTER TABLE sign_random_practice_sessions
    ADD CONSTRAINT chk_sign_random_practice_sessions_language
        CHECK (language_code IS NULL OR language_code IN ('en', 'nl', 'fr', 'ar'));

ALTER TABLE user_lesson_progress
    ADD CONSTRAINT chk_user_lesson_progress_language
        CHECK (language_code IS NULL OR language_code IN ('en', 'nl', 'fr', 'ar'));

CREATE INDEX idx_sign_exam_results_user_completed
    ON sign_exam_results (user_id, completed_at DESC);

CREATE INDEX idx_sign_practice_sessions_user_completed
    ON sign_practice_sessions (user_id, status, completed_at DESC);
