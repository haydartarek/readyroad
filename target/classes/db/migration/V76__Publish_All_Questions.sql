-- V76: Publish All Questions
-- Update all questions to be active and published so they can be used in exams

UPDATE quiz_questions
SET
    is_active = true,
    status = 'PUBLISHED',
    context_specific = false,
    requires_sign_image = false,
    published_at = NOW()
WHERE status IS NULL OR status = 'DRAFT' OR is_active IS NULL;
