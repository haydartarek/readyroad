-- V125__Fix_D_Series_Official_Names_And_Images.sql
-- Fixes D-series (Gebod / Mandatory) sign data in BOTH tables:
--   • traffic_signs  — legacy table seeded by SQL migrations
--   • road_signs     — Sign Quiz system table seeded from sign.json import
--
-- Issues corrected:
--   1. D1c  — image_url points to "links aanhouden" (keep left) file
--             but sign is "Verplichting rechts afslaan" (mandatory right turn)
--   2. D4   — image_url already fixed in V118 for traffic_signs;
--             fix road_signs image_path (sign.json was pointing to D4-links image)
--   3. D1a-links  — road_signs: fix assets/ prefix → images/
--   4. D1a-rechts — road_signs: fix assets/ prefix → images/
--   5. D4-links   — road_signs: fix assets/ prefix → images/
--   6. D4-rechts  — road_signs: fix assets/ prefix → images/
--   7. D13  — deactivate in road_signs (image file missing; already removed
--             from traffic_signs in V116)
-- ============================================================================

-- ─────────────────────────────────────────────────────────────────────────────
-- 1. Fix D1c — wrong image (keep-left image used for mandatory-right-turn sign)
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE traffic_signs
SET image_url  = 'images/signs/mandatory_signs/D1b Verplichting rechts afslaan.png',
    name_nl    = 'Verplichting rechts afslaan',
    name_en    = 'Mandatory right turn',
    name_fr    = 'Obligation de tourner à droite',
    name_ar    = 'إلزامية القيادة باتجاه اليمين',
    updated_at = NOW()
WHERE sign_code = 'D1c';

UPDATE road_signs
SET image_path = 'images/signs/mandatory_signs/D1b Verplichting rechts afslaan.png',
    name_nl    = 'Verplichting rechts afslaan',
    name_en    = 'Mandatory right turn',
    name_fr    = 'Obligation de tourner à droite',
    name_ar    = 'إلزامية القيادة باتجاه اليمين',
    updated_at = NOW()
WHERE sign_code = 'D1c';

-- ─────────────────────────────────────────────────────────────────────────────
-- 2. Fix D4 — road_signs image_path (traffic_signs already fixed by V118)
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE road_signs
SET image_path = 'images/signs/mandatory_signs/D4 Verplicht rechtdoor voor voertuigen die gevaarlijke goederen vervoeren.png',
    updated_at = NOW()
WHERE sign_code = 'D4'
  AND (image_path LIKE '%linksaf%' OR image_path LIKE 'assets/%');

-- ─────────────────────────────────────────────────────────────────────────────
-- 3. Fix D1a-links — road_signs: assets/ prefix → images/
--    (traffic_signs already fixed by V118)
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE road_signs
SET image_path = 'images/signs/mandatory_signs/D1c Verplichting links aanhouden.png',
    updated_at = NOW()
WHERE sign_code = 'D1a-links'
  AND image_path LIKE 'assets/%';

-- ─────────────────────────────────────────────────────────────────────────────
-- 4. Fix D1a-rechts — road_signs: assets/ prefix → images/
--    (traffic_signs already fixed by V118)
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE road_signs
SET image_path = 'images/signs/mandatory_signs/D1d Verplichting rechts aanhouden.png',
    updated_at = NOW()
WHERE sign_code = 'D1a-rechts'
  AND image_path LIKE 'assets/%';

-- ─────────────────────────────────────────────────────────────────────────────
-- 5. Fix D4-links — road_signs: assets/ prefix → images/
--    (traffic_signs already fixed by V118)
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE road_signs
SET image_path = 'images/signs/mandatory_signs/D4 Verplicht linksaf voor voertuigen die gevaarlijke goederen vervoeren.png',
    updated_at = NOW()
WHERE sign_code = 'D4-links'
  AND image_path LIKE 'assets/%';

-- ─────────────────────────────────────────────────────────────────────────────
-- 6. Fix D4-rechts — road_signs: assets/ prefix → images/
--    (traffic_signs already fixed by V118)
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE road_signs
SET image_path = 'images/signs/mandatory_signs/D4 Verplicht rechts voor voertuigen die gevaarlijke goederen vervoeren.png',
    updated_at = NOW()
WHERE sign_code = 'D4-rechts'
  AND image_path LIKE 'assets/%';

-- ─────────────────────────────────────────────────────────────────────────────
-- 7. Deactivate D13 in road_signs — image file no longer exists
--    (already deleted from traffic_signs in V116)
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE road_signs
SET is_active  = FALSE,
    updated_at = NOW()
WHERE sign_code = 'D13';
