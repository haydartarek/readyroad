-- V171: Fix zone signs image_url (copy from image_path)
-- V170 inserted zone signs using image_path column only; image_url was left NULL.
-- The frontend reads image_url. This migration syncs image_url from image_path.

UPDATE traffic_signs
SET image_url  = image_path,
    updated_at = NOW()
WHERE category_id = 8
  AND image_path IS NOT NULL
  AND (image_url IS NULL OR image_url = '');
