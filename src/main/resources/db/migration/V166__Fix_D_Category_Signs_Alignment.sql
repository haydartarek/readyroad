-- ============================================================
-- V166__Fix_D_Category_Signs_Alignment.sql
-- 1. D1d already inserted manually — verify/skip if exists
-- 2. Add D1c and D1d if not exist
-- ============================================================

SET NAMES utf8mb4;

-- D1d: already inserted, update to ensure consistency
UPDATE traffic_signs SET
    name_ar = 'إلزام الانعطاف يميناً (الإمساك باليمين)',
    name_en = 'Mandatory keep right',
    name_nl = 'Verplichting rechts aanhouden',
    name_fr = 'Obligation de serrer à droite',
    image_url  = 'images/signs/mandatory_signs/D1d Verplichting rechts aanhouden.png',
    image_path = 'images/signs/mandatory_signs/D1d Verplichting rechts aanhouden.png',
    updated_at = NOW()
WHERE sign_code = 'D1d';

-- D1c: uses D1c image (correct by design)
INSERT INTO traffic_signs (
    category_id, sign_code, normalized_sign_code,
    name_ar, name_en, name_nl, name_fr,
    description_ar, description_en, description_nl, description_fr,
    image_url, image_path, is_active, created_at, updated_at
)
SELECT 4, 'D1c', 'd1c',
 'إلزام الإمساك باليسار',
 'Mandatory keep left',
 'Verplichting links aanhouden',
 'Obligation de serrer à gauche',
 'هذه العلامة تلزم السائقين بالإمساك بالجانب الأيسر.',
 'This sign requires drivers to keep to the left side of the road.',
 'Dit verkeersbord verplicht bestuurders om links aan te houden.',
 'Ce panneau oblige les conducteurs à serrer à gauche.',
 'images/signs/mandatory_signs/D1c Verplichting links aanhouden.png',
 'images/signs/mandatory_signs/D1c Verplichting links aanhouden.png',
 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM traffic_signs WHERE sign_code = 'D1c');

-- D1d: uses D1d image (correct by design)
INSERT INTO traffic_signs (
    category_id, sign_code, normalized_sign_code,
    name_ar, name_en, name_nl, name_fr,
    description_ar, description_en, description_nl, description_fr,
    image_url, image_path, is_active, created_at, updated_at
)
SELECT 4, 'D1d', 'd1d',
 'إلزام الإمساك باليمين',
 'Mandatory keep right',
 'Verplichting rechts aanhouden',
 'Obligation de serrer à droite',
 'هذه العلامة تلزم السائقين بالإمساك بالجانب الأيمن.',
 'This sign requires drivers to keep to the right side of the road.',
 'Dit verkeersbord verplicht bestuurders om rechts aan te houden.',
 'Ce panneau oblige les conducteurs à serrer à droite.',
 'images/signs/mandatory_signs/D1d Verplichting rechts aanhouden.png',
 'images/signs/mandatory_signs/D1d Verplichting rechts aanhouden.png',
 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM traffic_signs WHERE sign_code = 'D1d');
