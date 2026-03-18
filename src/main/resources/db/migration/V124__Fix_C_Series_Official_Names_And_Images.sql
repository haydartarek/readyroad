-- V124__Fix_C_Series_Official_Names_And_Images.sql
-- Fixes C-series (Verbod / Prohibition) sign data in BOTH tables:
--   • traffic_signs  — legacy table seeded by SQL migrations
--   • road_signs     — Sign Quiz system table seeded from sign.json import
--
-- Issues corrected:
--   1. C11   — wrong name + wrong image (pointed to C11b instead of C11)
--   2. C28a  — wrong name ("zonder zijspan") → correct: width restriction incl. trailer
--   3. C43   — image_url used 30 km specific image → correct: generic speed-limit image
--   4. C43_30 — INSERT missing sign (never properly added to traffic_signs)
-- ============================================================================

-- ─────────────────────────────────────────────────────────────────────────────
-- 1. Fix C11 — Verboden toegang voor bestuurders van rijwielen met motor
--    (previously pointing to C11b image/name by mistake)
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE traffic_signs
SET name_nl   = 'Verboden toegang voor bestuurders van rijwielen met motor',
    name_en   = 'No motorized bicycles',
    name_fr   = 'Accès interdit aux conducteurs de cyclomoteurs',
    name_ar   = 'ممنوع دخول الدراجات النارية الصغيرة',
    image_url = 'images/signs/prohibition_signs/C11 Verboden toegang voor bestuurders van rijwielen met motor.png',
    updated_at = NOW()
WHERE sign_code = 'C11';

UPDATE road_signs
SET name_nl    = 'Verboden toegang voor bestuurders van rijwielen met motor',
    name_en    = 'No motorized bicycles',
    name_fr    = 'Accès interdit aux conducteurs de cyclomoteurs',
    name_ar    = 'ممنوع دخول الدراجات النارية الصغيرة',
    image_path = 'images/signs/prohibition_signs/C11 Verboden toegang voor bestuurders van rijwielen met motor.png',
    updated_at = NOW()
WHERE sign_code = 'C11';

-- ─────────────────────────────────────────────────────────────────────────────
-- 2. Fix C28a — Width restriction incl. trailer
--    (previously: "auto's en motorfietsen zonder zijspan" — completely wrong)
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE traffic_signs
SET name_nl   = 'Verboden toegang voor voertuigen of sleep breder dan het aangeduide',
    name_en   = 'No vehicles or trailers exceeding indicated width',
    name_fr   = 'Accès interdit aux véhicules ou remorques dépassant la largeur indiquée',
    name_ar   = 'ممنوع دخول المركبات أو المقطورات التي يزيد عرضها عن العرض المحدد',
    updated_at = NOW()
WHERE sign_code = 'C28a';

UPDATE road_signs
SET name_nl    = 'Verboden toegang voor voertuigen of sleep breder dan het aangeduide',
    name_en    = 'No vehicles or trailers exceeding indicated width',
    name_fr    = 'Accès interdit aux véhicules ou remorques dépassant la largeur indiquée',
    name_ar    = 'ممنوع دخول المركبات أو المقطورات التي يزيد عرضها عن العرض المحدد',
    updated_at = NOW()
WHERE sign_code = 'C28a';

-- ─────────────────────────────────────────────────────────────────────────────
-- 3. Fix C43 — Use generic speed-limit image (not the 30 km variant)
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE traffic_signs
SET image_url = 'images/signs/prohibition_signs/C43 Verbod te rijden met een grotere snelheid dan is aangeduid.png',
    updated_at = NOW()
WHERE sign_code = 'C43';

UPDATE road_signs
SET image_path = 'images/signs/prohibition_signs/C43 Verbod te rijden met een grotere snelheid dan is aangeduid.png',
    updated_at = NOW()
WHERE sign_code = 'C43';

-- ─────────────────────────────────────────────────────────────────────────────
-- 4. Insert C43_30 — was never properly added to traffic_signs
--    (V103 inserted a duplicate C43 row instead of C43_30; V108 deleted it)
-- ─────────────────────────────────────────────────────────────────────────────
INSERT INTO traffic_signs (
    category_id, sign_code,
    name_nl, name_en, name_fr, name_ar,
    description_nl, description_en, description_fr, description_ar,
    image_url, is_active, created_at, updated_at
)
SELECT
    (SELECT id FROM categories WHERE code = 'C'),
    'C43_30',
    'Verbod te rijden met een grotere snelheid dan 30 km/u',
    'Speed limit 30 km/h',
    'Limitation de vitesse 30 km/h',
    'ممنوع القيادة بسرعة تتجاوز 30 كم/س',
    'Maximumsnelheid 30 km/u die niet mag worden overschreden.',
    'Maximum speed of 30 km/h must not be exceeded.',
    'La vitesse maximale de 30 km/h ne doit pas être dépassée.',
    'الحد الأقصى للسرعة 30 كم/ساعة يجب عدم تجاوزه.',
    'images/signs/prohibition_signs/C43 - Verbod te rijden met een grotere snelheid dan is aangeduid 30 km.png',
    TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM traffic_signs WHERE sign_code = 'C43_30');

-- ─────────────────────────────────────────────────────────────────────────────
-- 5. Fix C43_30 image in road_signs (if it was imported with wrong assets/ path)
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE road_signs
SET name_nl    = 'Verbod te rijden met een grotere snelheid dan 30 km/u',
    name_en    = 'Speed limit 30 km/h',
    name_fr    = 'Limitation de vitesse 30 km/h',
    name_ar    = 'ممنوع القيادة بسرعة تتجاوز 30 كم/س',
    image_path = 'images/signs/prohibition_signs/C43 - Verbod te rijden met een grotere snelheid dan is aangeduid 30 km.png',
    updated_at = NOW()
WHERE sign_code = 'C43_30';

-- ─────────────────────────────────────────────────────────────────────────────
-- 6. Fix C43_50 and C43_70 in road_signs (assets/ prefix from old sign.json)
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE road_signs
SET image_path = 'images/signs/prohibition_signs/C43 - Verbod te rijden met een grotere snelheid dan is aangeduid 50 km.png',
    updated_at = NOW()
WHERE sign_code = 'C43_50'
  AND image_path LIKE 'assets/%';

UPDATE road_signs
SET image_path = 'images/signs/prohibition_signs/C43 - Verbod te rijden met een grotere snelheid dan is aangeduid 70 km.png',
    updated_at = NOW()
WHERE sign_code = 'C43_70'
  AND image_path LIKE 'assets/%';
