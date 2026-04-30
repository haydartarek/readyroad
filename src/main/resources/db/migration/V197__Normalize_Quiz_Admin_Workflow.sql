-- Align legacy quiz-question rows with the current admin workflow:
-- - no wrong-answer explanation payloads
-- - no sign-context-only flags
-- - active state is the source of truth for delivery

UPDATE quiz_questions
SET
    error_explanation_ar = NULL,
    error_explanation_en = NULL,
    error_explanation_nl = NULL,
    error_explanation_fr = NULL,
    context_specific = 0,
    requires_sign_image = 0,
    status = CASE
        WHEN COALESCE(is_active, 0) = 1 THEN 'PUBLISHED'
        ELSE 'DRAFT'
    END,
    published_at = CASE
        WHEN COALESCE(is_active, 0) = 1 THEN COALESCE(published_at, NOW())
        ELSE NULL
    END;
