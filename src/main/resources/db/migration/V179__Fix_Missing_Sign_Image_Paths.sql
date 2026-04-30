-- =============================================================================
-- V179: Fix missing sign image paths causing 404 errors
-- =============================================================================
--
-- Problem: Five sign image URLs in the DB pointed to files that do not exist
-- in the frontend's public/images/signs folder, causing 404 errors:
--
--   DANGER SIGNS (images now added to frontend public folder):
--     A11 Uitweg op een kaai of een oever.png              (file was missing from frontend)
--     A15 Glibberige rijbaan.png          (file was missing from frontend)
--     A21 Oversteekplaats voor voetgangers.png     (file was missing from frontend)
--     A25 Oversteekplaats voor fietsers en bromfietsers.png  (file was missing from frontend)
--
--   INFORMATION SIGNS (DB path had wrong spelling):
--     F50bis: DB had "veranderd, voetgangers.png" (with 'd' and comma)
--             Actual file is "verandert voetgangers.png" (no 'd' at end, no comma)
--
-- Fix for A11/A15/A21/A25: image files copied from backend public folder to
-- frontend web_app/public/images/signs/danger_signs/ — no DB change needed.
--
-- Fix for F50bis: update DB path to match the actual file on disk.
-- =============================================================================

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 1: traffic_signs
-- ─────────────────────────────────────────────────────────────────────────────

UPDATE traffic_signs
SET    image_url  = 'images/signs/information_signs/F50bis Opgepast als je van richting verandert voetgangers.png',
       image_path = 'images/signs/information_signs/F50bis Opgepast als je van richting verandert voetgangers.png',
       updated_at = NOW()
WHERE  sign_code = 'F50bis';

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 2: road_signs
-- ─────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    image_path = 'images/signs/information_signs/F50bis Opgepast als je van richting verandert voetgangers.png',
       updated_at = NOW()
WHERE  sign_code = 'F50bis';
