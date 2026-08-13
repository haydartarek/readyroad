-- Preserve the locale used for real learning activity and make stateless
-- traffic-sign exam submission idempotent without rewriting historical rows.

ALTER TABLE exam_simulations
    ADD COLUMN language_code VARCHAR(2);

ALTER TABLE sign_exam_results
    ADD COLUMN language_code VARCHAR(2),
    ADD COLUMN submission_key VARCHAR(64);

ALTER TABLE sign_practice_sessions
    ADD COLUMN language_code VARCHAR(2);

ALTER TABLE sign_random_practice_sessions
    ADD COLUMN language_code VARCHAR(2);

ALTER TABLE user_lesson_progress
    ADD COLUMN language_code VARCHAR(2);

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
    ON sign_practice_sessions (user_id, completed_at DESC)
    WHERE status = 'COMPLETED';
