-- V124__Fix_C_Series_Official_Names_And_Images.sql
-- Fixes C-series (Verbod / Prohibition) sign data in BOTH tables:
--   • traffic_signs  — legacy table seeded by SQL migrations
--   • road_signs     — Sign Quiz system table seeded from sign.json import
--
-- Issues corrected:
--   1. C11   — align with the curated bicycle-only sign
--   2. C43   — align with the curated 50 km/h canonical sign
-- ============================================================================

-- ─────────────────────────────────────────────────────────────────────────────
-- 1. Fix C11 — Verboden toegang voor bestuurders van rijwielen
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE traffic_signs
SET name_nl   = 'Verboden toegang voor bestuurders van rijwielen',
    name_en   = 'No entry for riders of bicycles',
    name_fr   = 'Accès interdit aux conducteurs de bicyclettes',
    name_ar   = 'ممنوع دخول راكبي الدراجات الهوائية',
    image_url = 'images/signs/prohibition_signs/C11 Verboden toegang voor bestuurders van rijwielen.png',
    updated_at = NOW()
WHERE sign_code = 'C11';

UPDATE road_signs
SET name_nl    = 'Verboden toegang voor bestuurders van rijwielen',
    name_en    = 'No entry for riders of bicycles',
    name_fr    = 'Accès interdit aux conducteurs de bicyclettes',
    name_ar    = 'ممنوع دخول راكبي الدراجات الهوائية',
    image_path = 'images/signs/prohibition_signs/C11 Verboden toegang voor bestuurders van rijwielen.png',
    updated_at = NOW()
WHERE sign_code = 'C11';

-- ─────────────────────────────────────────────────────────────────────────────
-- 2. Fix C43 — Use the curated 50 km/h image
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE traffic_signs
SET image_url = 'images/signs/prohibition_signs/C43 Verbod te rijden met een grotere snelheid dan 50 km.png',
    updated_at = NOW()
WHERE sign_code = 'C43';

UPDATE road_signs
SET image_path = 'images/signs/prohibition_signs/C43 Verbod te rijden met een grotere snelheid dan 50 km.png',
    updated_at = NOW()
WHERE sign_code = 'C43';
