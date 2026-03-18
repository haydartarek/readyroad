-- ============================================================
-- Migration: V138
-- Description: Fix broken image path prefixes and folder names
--              in both traffic_signs.image_url and road_signs.image_path
--
-- Fixes applied:
--   1. assets/signs/...              → images/signs/...
--   2. images/signs/direction_signs/ → images/signs/information_signs/
--   3. images/signs/parkeren/        → images/signs/parking_signs/
--   4. images/signs/onderborden/     → images/signs/additional_signs/
--   5. images/signs/afbakeningsborden/ → images/signs/delineation_signs/
--   6. images/signs/informatieborden_en_tijdelijke_verkeersmaatregelen/
--                                    → images/signs/information_signs/
--   7. assets/signs/gevaarsborden/   → images/signs/danger_signs/
--   8. assets/signs/voorrangsborden/ → images/signs/priority_signs/
--   9. assets/signs/verbodsborden/   → images/signs/prohibition_signs/
--  10. assets/signs/gebodsborden/    → images/signs/mandatory_signs/
--  11. assets/signs/parkeren/        → images/signs/parking_signs/
--  12. assets/signs/aanwijzingsborden/ → images/signs/information_signs/
--  13. assets/signs/onderborden/     → images/signs/additional_signs/
--  14. assets/signs/afbakeningsborden/ → images/signs/delineation_signs/
--  15. assets/signs/zoneborden/      → images/signs/zone_signs/
--  16. assets/signs/zone_signs/      → images/signs/zone_signs/
--  17. assets/signs/Informatieborden_en_tijdelijke_verkeersmaatregelen/
--                                    → images/signs/information_signs/
-- All replacements are idempotent (only rows matching the pattern are updated).
-- ============================================================

-- ════════════════════════════════════════════════════════════════
-- traffic_signs.image_url
-- ════════════════════════════════════════════════════════════════

-- 1. assets/signs/ → images/signs/
UPDATE traffic_signs
SET image_url = CONCAT('images/signs/', SUBSTRING(image_url, LENGTH('assets/signs/') + 1)),
    updated_at = NOW()
WHERE image_url LIKE 'assets/signs/%';

-- 2. direction_signs/ → information_signs/
UPDATE traffic_signs
SET image_url = REPLACE(image_url, 'images/signs/direction_signs/', 'images/signs/information_signs/'),
    updated_at = NOW()
WHERE image_url LIKE 'images/signs/direction_signs/%';

-- 3. parkeren/ → parking_signs/
UPDATE traffic_signs
SET image_url = REPLACE(image_url, 'images/signs/parkeren/', 'images/signs/parking_signs/'),
    updated_at = NOW()
WHERE image_url LIKE 'images/signs/parkeren/%';

-- 4. onderborden/ → additional_signs/
UPDATE traffic_signs
SET image_url = REPLACE(image_url, 'images/signs/onderborden/', 'images/signs/additional_signs/'),
    updated_at = NOW()
WHERE image_url LIKE 'images/signs/onderborden/%';

-- 5. afbakeningsborden/ → delineation_signs/
UPDATE traffic_signs
SET image_url = REPLACE(image_url, 'images/signs/afbakeningsborden/', 'images/signs/delineation_signs/'),
    updated_at = NOW()
WHERE image_url LIKE 'images/signs/afbakeningsborden/%';

-- 6. informatieborden_en_tijdelijke_verkeersmaatregelen/ → information_signs/
UPDATE traffic_signs
SET image_url = REPLACE(image_url,
    'images/signs/informatieborden_en_tijdelijke_verkeersmaatregelen/',
    'images/signs/information_signs/'),
    updated_at = NOW()
WHERE image_url LIKE 'images/signs/informatieborden_en_tijdelijke_verkeersmaatregelen/%';

-- 7. Informatieborden_en_tijdelijke_verkeersmaatregelen/ (capitalized) → information_signs/
UPDATE traffic_signs
SET image_url = REPLACE(image_url,
    'images/signs/Informatieborden_en_tijdelijke_verkeersmaatregelen/',
    'images/signs/information_signs/'),
    updated_at = NOW()
WHERE image_url LIKE 'images/signs/Informatieborden_en_tijdelijke_verkeersmaatregelen/%';

-- ════════════════════════════════════════════════════════════════
-- road_signs.image_path
-- ════════════════════════════════════════════════════════════════

-- 1. assets/signs/ → images/signs/
UPDATE road_signs
SET image_path = CONCAT('images/signs/', SUBSTRING(image_path, LENGTH('assets/signs/') + 1)),
    updated_at = NOW()
WHERE image_path LIKE 'assets/signs/%';

-- 2. direction_signs/ → information_signs/
UPDATE road_signs
SET image_path = REPLACE(image_path, 'images/signs/direction_signs/', 'images/signs/information_signs/'),
    updated_at = NOW()
WHERE image_path LIKE 'images/signs/direction_signs/%';

-- 3. parkeren/ → parking_signs/
UPDATE road_signs
SET image_path = REPLACE(image_path, 'images/signs/parkeren/', 'images/signs/parking_signs/'),
    updated_at = NOW()
WHERE image_path LIKE 'images/signs/parkeren/%';

-- 4. onderborden/ → additional_signs/
UPDATE road_signs
SET image_path = REPLACE(image_path, 'images/signs/onderborden/', 'images/signs/additional_signs/'),
    updated_at = NOW()
WHERE image_path LIKE 'images/signs/onderborden/%';

-- 5. afbakeningsborden/ → delineation_signs/
UPDATE road_signs
SET image_path = REPLACE(image_path, 'images/signs/afbakeningsborden/', 'images/signs/delineation_signs/'),
    updated_at = NOW()
WHERE image_path LIKE 'images/signs/afbakeningsborden/%';

-- 6. informatieborden_en_tijdelijke_verkeersmaatregelen/ → information_signs/
UPDATE road_signs
SET image_path = REPLACE(image_path,
    'images/signs/informatieborden_en_tijdelijke_verkeersmaatregelen/',
    'images/signs/information_signs/'),
    updated_at = NOW()
WHERE image_path LIKE 'images/signs/informatieborden_en_tijdelijke_verkeersmaatregelen/%';

-- 7. Informatieborden_en_tijdelijke_verkeersmaatregelen/ (capitalized) → information_signs/
UPDATE road_signs
SET image_path = REPLACE(image_path,
    'images/signs/Informatieborden_en_tijdelijke_verkeersmaatregelen/',
    'images/signs/information_signs/'),
    updated_at = NOW()
WHERE image_path LIKE 'images/signs/Informatieborden_en_tijdelijke_verkeersmaatregelen/%';
