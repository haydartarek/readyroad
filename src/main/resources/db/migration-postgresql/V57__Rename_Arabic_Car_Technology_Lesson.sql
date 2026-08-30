-- Keep the canonical lesson identity and all multilingual content unchanged.
-- Only correct the Arabic learner-facing title for the existing car technology lesson.
UPDATE lessons
SET title_ar = 'أساسيات ميكانيك السيارة',
    updated_at = CURRENT_TIMESTAMP
WHERE title_en = 'Basic Car Technology'
  AND title_ar = 'أساسيات تكنولوجيا السيارة';
