-- V130: Fix A-serie (Gevaarsborden / Danger Signs) — name errors + missing A9 + A33 image
--
-- Root cause: V103 re-seeded all A-series after V51-V61 cleared the table.
-- V103 had systematic left/right name swaps for A1a, A1b, A1c, A1d, A7b, A7c,
-- a completely wrong name for A11, and omitted A9 entirely.
-- V110 encoding-fix confirmed the V103 errors for A1a instead of correcting them.
-- V103 + V118 also left A33 pointing to the movable-bridge image (A9 content).

-- ─────────────────────────────────────────────────────────────
-- PART 1 : Fix left/right swap for A1a (naar links → naar rechts)
--          Official Belgian Road Code: A1a = gevaarlijke bocht naar RECHTS
-- ─────────────────────────────────────────────────────────────
UPDATE traffic_signs
SET    name_nl            = 'Gevaarlijke bocht naar rechts',
       name_en            = 'Dangerous curve to the right',
       name_fr            = 'Virage dangereux à droite',
       name_ar            = 'منحنى خطير إلى اليمين',
       description_nl     = 'Waarschuwing voor een gevaarlijke bocht naar rechts.',
       description_en     = 'Dangerous bend to the right.',
       description_fr     = 'Virage dangereux à droite.',
       description_ar     = 'منعطف خطير إلى اليمين.',
       updated_at         = NOW()
WHERE  sign_code = 'A1a';

-- ─────────────────────────────────────────────────────────────
-- PART 2 : Fix left/right swap for A1b (naar rechts → naar links)
--          Official Belgian Road Code: A1b = gevaarlijke bocht naar LINKS
-- ─────────────────────────────────────────────────────────────
UPDATE traffic_signs
SET    name_nl            = 'Gevaarlijke bocht naar links',
       name_en            = 'Dangerous curve to the left',
       name_fr            = 'Virage dangereux à gauche',
       name_ar            = 'منحنى خطير إلى اليسار',
       description_nl     = 'Waarschuwing voor een gevaarlijke bocht naar links.',
       description_en     = 'Dangerous bend to the left.',
       description_fr     = 'Virage dangereux à gauche.',
       description_ar     = 'منعطف خطير إلى اليسار.',
       updated_at         = NOW()
WHERE  sign_code = 'A1b';

-- ─────────────────────────────────────────────────────────────
-- PART 3 : Fix sequence label for A1c (links-rechts → rechts-links)
--          Official Belgian Road Code: A1c = eerste naar RECHTS → (rechts-links)
-- ─────────────────────────────────────────────────────────────
UPDATE traffic_signs
SET    name_nl            = 'Gevaarlijke dubbele bocht (rechts-links)',
       name_en            = 'Dangerous double curve (right-left)',
       name_fr            = 'Double virage dangereux (droite-gauche)',
       name_ar            = 'منحنى مزدوج خطير (يمين-يسار)',
       description_nl     = 'Gevaarlijke dubbele bochten, de eerste naar rechts.',
       description_en     = 'Dangerous bend. First bend to the right.',
       description_fr     = 'Virages dangereux, le premier à droite.',
       description_ar     = 'منعطف مزدوج خطير، الأول إلى اليمين.',
       updated_at         = NOW()
WHERE  sign_code = 'A1c';

-- ─────────────────────────────────────────────────────────────
-- PART 4 : Fix sequence label for A1d (rechts-links → links-rechts)
--          Official Belgian Road Code: A1d = eerste naar LINKS → (links-rechts)
-- ─────────────────────────────────────────────────────────────
UPDATE traffic_signs
SET    name_nl            = 'Gevaarlijke dubbele bocht (links-rechts)',
       name_en            = 'Dangerous double curve (left-right)',
       name_fr            = 'Double virage dangereux (gauche-droite)',
       name_ar            = 'منحنى مزدوج خطير (يسار-يمين)',
       description_nl     = 'Gevaarlijke dubbele bochten, de eerste naar links.',
       description_en     = 'Dangerous bend. First bend to the left.',
       description_fr     = 'Virages dangereux, le premier à gauche.',
       description_ar     = 'منعطف مزدوج خطير، الأول إلى اليسار.',
       updated_at         = NOW()
WHERE  sign_code = 'A1d';

-- ─────────────────────────────────────────────────────────────
-- PART 5 : Fix A7b (langs links → langs rechts)
--          Official Belgian Road Code: A7b = rijbaanversmalling langs RECHTS
-- ─────────────────────────────────────────────────────────────
UPDATE traffic_signs
SET    name_nl            = 'Versmalling langs rechts',
       name_en            = 'Road narrows on the right',
       name_fr            = 'Rétrécissement à droite',
       name_ar            = 'تضيق الطريق من جهة اليمين',
       description_nl     = 'Rijbaanversmalling langs rechts.',
       description_en     = 'Road narrowing on the right.',
       description_fr     = 'Rétrécissement de la chaussée à droite.',
       description_ar     = 'تضييق الطريق على اليمين.',
       updated_at         = NOW()
WHERE  sign_code = 'A7b';

-- ─────────────────────────────────────────────────────────────
-- PART 6 : Fix A7c (langs rechts → langs links)
--          Official Belgian Road Code: A7c = rijbaanversmalling langs LINKS
-- ─────────────────────────────────────────────────────────────
UPDATE traffic_signs
SET    name_nl            = 'Versmalling langs links',
       name_en            = 'Road narrows on the left',
       name_fr            = 'Rétrécissement à gauche',
       name_ar            = 'تضيق الطريق من جهة اليسار',
       description_nl     = 'Rijbaanversmalling langs links.',
       description_en     = 'Road narrowing on the left.',
       description_fr     = 'Rétrécissement de la chaussée à gauche.',
       description_ar     = 'تضييق الطريق على اليسار.',
       updated_at         = NOW()
WHERE  sign_code = 'A7c';

-- ─────────────────────────────────────────────────────────────
-- PART 7 : Fix A11 (completely wrong "kaai/oever" name → correct Slecht wegdek)
--          Official Belgian Road Code: A11 = Slecht wegdek (oneffenheden)
-- ─────────────────────────────────────────────────────────────
UPDATE traffic_signs
SET    name_nl            = 'Slecht wegdek',
       name_en            = 'Bumpy or uneven road',
       name_fr            = 'Mauvais état de la chaussée',
       name_ar            = 'طريق سيء أو وعر',
       description_nl     = 'Oneffenheden in de rijbaan.',
       description_en     = 'Uneven road surface.',
       description_fr     = 'Chaussée dégradée ou inégale.',
       description_ar     = 'سطح طريق غير مستوٍ.',
       updated_at         = NOW()
WHERE  sign_code = 'A11';

-- ─────────────────────────────────────────────────────────────
-- PART 8 : Fix A33 image_url
--          V103 pointed A33 to 'A33 Beweegbare brug.png' (A9 content).
--          That file has been renamed on disk to 'A9 Beweegbare brug.png'.
--          A33 (Verkeerslichten) should use the already-existing A33-v1 image.
-- ─────────────────────────────────────────────────────────────
UPDATE traffic_signs
SET    image_url  = 'images/signs/danger_signs/A33-v1 Verkeerslichten.png',
       updated_at = NOW()
WHERE  sign_code = 'A33';

-- ─────────────────────────────────────────────────────────────
-- PART 9 : Fix A17 image_url
--          Disk file was mislabeled 'A17 Zijdelingse wind.png'.
--          Renamed on disk to 'A17 Kiezelprojectie.png' (official A17 name).
-- ─────────────────────────────────────────────────────────────
UPDATE traffic_signs
SET    image_url  = 'images/signs/danger_signs/A17 Kiezelprojectie.png',
       updated_at = NOW()
WHERE  sign_code = 'A17';

-- ─────────────────────────────────────────────────────────────
-- PART 10: Insert missing A9 (Beweegbare brug / Movable bridge)
--          A9 was in V6 but cleared by V51-V61 and omitted from V103.
--          Disk file 'A33 Beweegbare brug.png' was renamed to
--          'A9 Beweegbare brug.png' to match the correct sign code.
-- ─────────────────────────────────────────────────────────────
INSERT INTO traffic_signs (
    sign_code, category_id,
    name_nl, name_en, name_fr, name_ar,
    description_nl, description_en, description_fr, description_ar,
    long_description_nl, long_description_en, long_description_fr, long_description_ar,
    image_url, is_active, created_at, updated_at
)
VALUES (
    'A9',
    (SELECT id FROM categories WHERE code = 'A'),
    'Beweegbare brug',
    'Movable bridge',
    'Pont mobile',
    'جسر متحرك',
    'Beweegbare brug in de nabijheid.',
    'Movable bridge ahead.',
    'Pont mobile à proximité.',
    'جسر متحرك أمامك.',
    'Dit bord waarschuwt voor een beweegbare brug. De brug kan worden geopend om schepen door te laten. Verminder uw snelheid en wees bereid te stoppen.',
    'This sign warns of a movable bridge ahead. The bridge may open to allow vessels to pass. Reduce your speed and be prepared to stop.',
    'Ce panneau avertit d''un pont mobile. Le pont peut s''ouvrir pour laisser passer des bateaux. Réduisez votre vitesse et soyez prêt à vous arrêter.',
    'تحذر هذه العلامة من وجود جسر متحرك أمامك. قد ينفتح الجسر للسماح بمرور السفن. خفف سرعتك وكن مستعداً للتوقف.',
    'images/signs/danger_signs/A9 Beweegbare brug.png',
    TRUE,
    NOW(),
    NOW()
)
ON DUPLICATE KEY UPDATE
    name_nl        = VALUES(name_nl),
    name_en        = VALUES(name_en),
    name_fr        = VALUES(name_fr),
    name_ar        = VALUES(name_ar),
    description_nl = VALUES(description_nl),
    description_en = VALUES(description_en),
    image_url      = VALUES(image_url),
    updated_at     = NOW();
