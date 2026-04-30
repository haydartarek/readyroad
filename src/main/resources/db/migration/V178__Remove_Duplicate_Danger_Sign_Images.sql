-- =============================================================================
-- V178: Remove duplicate danger sign image references
-- =============================================================================
--
-- These 7 duplicate image files have been deleted from disk:
--   A7c Rijbaanversmalling langs rechts.png
--   A1a Gevaarlijke bocht naar rechts.png
--   A1b Gevaarlijke bocht naar rechts.png
--   A1c Gevaarlijke dubbele bocht (rechts-links).png
--   A1d Gevaarlijke dubbele of meer dan twee bochten, de eerste naar rechts.png
--   A7b Versmalling langs rechts.png
--   A33 Verkeerslichten.png
--
-- Canonical files that remain on disk:
--   A7c Rijbaanversmalling langs rechts.png
--   A1a Gevaarlijke bocht naar links.png
--   A1b Gevaarlijke bocht naar rechts.png
--   A1c Gevaarlijke dubbele of meer dan twee bochten, de eerste naar links.png
--   A1d Gevaarlijke dubbele of meer dan twee bochten, de eerste naar rechts.png
--   A7b Rijbaanversmalling langs links.png
--   A33 Verkeerslichten.png
--
-- DB state before this migration (set by V143 + V144):
--   A1a -> A1a Gevaarlijke bocht naar links.png  (already correct, no change)
--   A1b -> A1b Gevaarlijke bocht naar rechts.png  (needs fix)
--   A1c -> A1c Gevaarlijke dubbele of meer... naar links.png  (already correct)
--   A1d -> A1d Gevaarlijke dubbele of meer... naar rechts.png  (needs fix)
--   A7b -> A7b Rijbaanversmalling langs links.png  (already correct, no change)
--   A7c -> A7c Rijbaanversmalling langs rechts.png  (needs fix)
--   A33 -> canonical file retained  (no action needed)
-- =============================================================================

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 1: traffic_signs
-- ─────────────────────────────────────────────────────────────────────────────

UPDATE traffic_signs
SET    image_url  = 'images/signs/danger_signs/A1b Gevaarlijke bocht naar rechts.png',
       image_path = 'images/signs/danger_signs/A1b Gevaarlijke bocht naar rechts.png',
       updated_at = NOW()
WHERE  sign_code = 'A1b';

UPDATE traffic_signs
SET    image_url  = 'images/signs/danger_signs/A1d Gevaarlijke dubbele of meer dan twee bochten, de eerste naar rechts.png',
       image_path = 'images/signs/danger_signs/A1d Gevaarlijke dubbele of meer dan twee bochten, de eerste naar rechts.png',
       updated_at = NOW()
WHERE  sign_code = 'A1d';

UPDATE traffic_signs
SET    image_url  = 'images/signs/danger_signs/A7c Rijbaanversmalling langs rechts.png',
       image_path = 'images/signs/danger_signs/A7c Rijbaanversmalling langs rechts.png',
       updated_at = NOW()
WHERE  sign_code = 'A7c';

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 2: road_signs
-- ─────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    image_path = 'images/signs/danger_signs/A1b Gevaarlijke bocht naar rechts.png',
       updated_at = NOW()
WHERE  sign_code = 'A1b';

UPDATE road_signs
SET    image_path = 'images/signs/danger_signs/A1d Gevaarlijke dubbele of meer dan twee bochten, de eerste naar rechts.png',
       updated_at = NOW()
WHERE  sign_code = 'A1d';

UPDATE road_signs
SET    image_path = 'images/signs/danger_signs/A7c Rijbaanversmalling langs rechts.png',
       updated_at = NOW()
WHERE  sign_code = 'A7c';
