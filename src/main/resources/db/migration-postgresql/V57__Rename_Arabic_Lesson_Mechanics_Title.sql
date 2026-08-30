-- Use the approved Arabic title for the vehicle mechanics lesson.
-- Scope is deliberately limited to LES-4; no other localized lesson content changes.
UPDATE lessons
SET title_ar = 'أساسيات ميكانيك السيارة',
    updated_at = CURRENT_TIMESTAMP
WHERE lesson_code = 'LES-4'
  AND title_ar = 'أساسيات تكنولوجيا السيارة';
