-- Keep legacy quiz-question compatibility columns aligned with the current
-- admin workflow so future inserts do not silently reintroduce stale flags.

ALTER TABLE quiz_questions
    MODIFY COLUMN context_specific BOOLEAN DEFAULT FALSE,
    MODIFY COLUMN requires_sign_image BOOLEAN DEFAULT FALSE;
