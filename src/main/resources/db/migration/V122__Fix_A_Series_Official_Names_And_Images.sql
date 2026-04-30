-- ============================================================================
-- V122: Fix A-series sign names and image paths to match official Belgian PDF
-- ============================================================================
-- Source: "Overzicht alle officiële Belgische verkeersborden" (official PDF)
-- All name_nl values corrected to official traffic sign names per Belgian law
-- (KB 1 december 1975 - Serie A, Gevaar/Danger signs)
-- ============================================================================

-- ---- A1a: image_url was swapped with A1b ----
UPDATE traffic_signs
SET image_url   = 'images/signs/danger_signs/A1a Gevaarlijke bocht naar links.png',
    updated_at  = NOW()
WHERE sign_code = 'A1a';

-- ---- A1b: image_url was swapped with A1a ----
UPDATE traffic_signs
SET image_url   = 'images/signs/danger_signs/A1b Gevaarlijke bocht naar rechts.png',
    updated_at  = NOW()
WHERE sign_code = 'A1b';

-- ---- A1c: full official name (PDF) + correct image_url ----
UPDATE traffic_signs
SET name_nl        = 'Gevaarlijke dubbele of meer dan twee bochten, de eerste naar links',
    name_en        = 'Dangerous double or multiple curves, first to the left',
    name_fr        = 'Double virage dangereux ou plusieurs virages, le premier à gauche',
    name_ar        = 'منعطف مزدوج خطير أو أكثر، الأول إلى اليسار',
    image_url      = 'images/signs/danger_signs/A1c Gevaarlijke dubbele bocht (links-rechts).png',
    updated_at     = NOW()
WHERE sign_code = 'A1c';

-- ---- A1d: full official name (PDF) + correct image_url ----
UPDATE traffic_signs
SET name_nl        = 'Gevaarlijke dubbele of meer dan twee bochten, de eerste naar rechts',
    name_en        = 'Dangerous double or multiple curves, first to the right',
    name_fr        = 'Double virage dangereux ou plusieurs virages, le premier à droite',
    name_ar        = 'منعطف مزدوج خطير أو أكثر، الأول إلى اليمين',
    image_url      = 'images/signs/danger_signs/A1d Gevaarlijke dubbele bocht (rechts-links).png',
    updated_at     = NOW()
WHERE sign_code = 'A1d';

-- ---- A7a: official name is simply "Rijbaanversmalling" ----
UPDATE traffic_signs
SET name_nl        = 'Rijbaanversmalling',
    name_en        = 'Road narrows',
    name_fr        = 'Rétrécissement de la chaussée',
    name_ar        = 'تضيق الطريق',
    updated_at     = NOW()
WHERE sign_code = 'A7a';

-- ---- A7b: "Rijbaanversmalling links" + correct image_url ----
UPDATE traffic_signs
SET name_nl        = 'Rijbaanversmalling links',
    name_en        = 'Road narrows on the left',
    name_fr        = 'Rétrécissement de la chaussée à gauche',
    name_ar        = 'تضيق الطريق من اليسار',
    image_url      = 'images/signs/danger_signs/A7b Versmalling langs links.png',
    updated_at     = NOW()
WHERE sign_code = 'A7b';

-- ---- A7c: "Rijbaanversmalling rechts" + correct image_url ----
UPDATE traffic_signs
SET name_nl        = 'Rijbaanversmalling rechts',
    name_en        = 'Road narrows on the right',
    name_fr        = 'Rétrécissement de la chaussée à droite',
    name_ar        = 'تضيق الطريق من اليمين',
    image_url      = 'images/signs/danger_signs/A7c Versmalling langs rechts.png',
    updated_at     = NOW()
WHERE sign_code = 'A7c';

-- ---- A9: INSERT missing sign (Beweegbare brug) ----
INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr,
    description_ar, description_en, description_nl, description_fr,
    image_url, is_active, created_at, updated_at)
SELECT
    'A9',
    (SELECT id FROM categories WHERE code = 'A'),
    'جسر متحرك',
    'Movable bridge',
    'Beweegbare brug',
    'Pont mobile',
    'جسر متحرك قد ينفتح للسماح بمرور القوارب.',
    'A movable bridge that may open to allow boat traffic.',
    'Een beweegbare brug die kan opengaan voor scheepvaart.',
    'Un pont mobile pouvant s''ouvrir pour la navigation.',
    'images/signs/danger_signs/A9 Beweegbare brug.png',
    TRUE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM traffic_signs WHERE sign_code = 'A9');

-- ---- A11: remove 'een' to match official PDF name ----
UPDATE traffic_signs
SET name_nl        = 'Uitweg op kaai of oever',
    name_en        = 'Exit to quay or riverbank',
    image_url      = 'images/signs/danger_signs/A11 Uitweg op een kaai of oever.png',
    updated_at     = NOW()
WHERE sign_code = 'A11';

-- ---- A15: official name "Gladde rijbaan - Slipgevaar" ----
UPDATE traffic_signs
SET name_nl        = 'Gladde rijbaan - Slipgevaar',
    name_en        = 'Slippery road - skid hazard',
    name_fr        = 'Chaussée glissante - Risque de dérapage',
    name_ar        = 'طريق زلق - خطر الانزلاق',
    updated_at     = NOW()
WHERE sign_code = 'A15';

-- ---- A17: official name "Kiezelprojectie" (not "Losliggende steenslag") ----
UPDATE traffic_signs
SET name_nl        = 'Kiezelprojectie',
    name_en        = 'Flying gravel',
    name_fr        = 'Projection de gravillons',
    name_ar        = 'تطاير الحصى',
    image_url      = 'images/signs/danger_signs/A17 Losliggende steenslag.png',
    updated_at     = NOW()
WHERE sign_code = 'A17';

-- ---- A19: remove 'links' from image filename ----
UPDATE traffic_signs
SET image_url      = 'images/signs/danger_signs/A19 Vallende stenen.png',
    updated_at     = NOW()
WHERE sign_code = 'A19';

-- ---- A21: "Oversteekplaats voor voetgangers" (not "Overstekende voetgangers") ----
UPDATE traffic_signs
SET name_nl        = 'Oversteekplaats voor voetgangers',
    image_url      = 'images/signs/danger_signs/A21 Overstekende voetgangers.png',
    updated_at     = NOW()
WHERE sign_code = 'A21';

-- ---- A23: "Opgelet kinderen" (not just "Kinderen") ----
UPDATE traffic_signs
SET name_nl        = 'Opgelet kinderen',
    name_en        = 'Warning: children',
    name_fr        = 'Attention: enfants',
    name_ar        = 'تحذير: أطفال',
    image_url      = 'images/signs/danger_signs/A23 Kinderen.png',
    updated_at     = NOW()
WHERE sign_code = 'A23';

-- ---- A25: "Oversteekplaats voor fietsers en bromfietsers" (bromfietsers added) ----
UPDATE traffic_signs
SET name_nl        = 'Oversteekplaats voor fietsers en bromfietsers',
    name_en        = 'Bicycle and moped crossing',
    name_fr        = 'Traversée de cyclistes et cyclomoteurs',
    name_ar        = 'منطقة عبور الدراجات والدراجات البخارية',
    image_url      = 'images/signs/danger_signs/A25 Overstekende fietsers.png',
    updated_at     = NOW()
WHERE sign_code = 'A25';

-- ---- A35: "Vliegtuigen op geringe hoogte" (not "Laagvliegende vliegtuigen") ----
UPDATE traffic_signs
SET name_nl        = 'Vliegtuigen op geringe hoogte',
    name_en        = 'Low-flying aircraft',
    name_fr        = 'Avions à basse altitude',
    updated_at     = NOW()
WHERE sign_code = 'A35';

-- ---- A39: full official name ----
UPDATE traffic_signs
SET name_nl        = 'Twee richtingsverkeer toegelaten na een stuk éénrichtingsverkeer',
    name_en        = 'Two-way traffic allowed after a one-way section',
    name_fr        = 'Trafic dans les deux sens autorisé après une section à sens unique',
    name_ar        = 'حركة مرور في اتجاهين بعد مقطع باتجاه واحد',
    updated_at     = NOW()
WHERE sign_code = 'A39';

-- ---- A49: full official name ----
UPDATE traffic_signs
SET name_nl        = 'Openbare weg kruist met een of meer in de rijbaan aangelegde sporen',
    name_en        = 'Public road crosses one or more tracks laid in the carriageway',
    name_fr        = 'Voie publique croisant un ou plusieurs rails dans la voie de circulation',
    name_ar        = 'طريق عام يتقاطع مع مسار واحد أو أكثر في الطريق',
    updated_at     = NOW()
WHERE sign_code = 'A49';

-- ---- A51: full official name (not "Algemeen gevaar") ----
UPDATE traffic_signs
SET name_nl        = 'Gevaar dat niet door een speciaal symbool wordt bepaald',
    name_en        = 'Danger not indicated by a special symbol',
    name_fr        = 'Danger non précisé par un symbole particulier',
    name_ar        = 'خطر غير محدد برمز خاص',
    updated_at     = NOW()
WHERE sign_code = 'A51';
