-- =============================================================================
-- V135: Fix A-series image_url values after V130 name corrections
-- =============================================================================
-- Root cause: V122 set image_url values based on one convention (left/right),
-- then V130 reversed the name_nl fields back to match the actual disk files,
-- but V130 did NOT update the image_url fields.
-- Result: DB name_nl and image_url now contradict each other for 6 A-series signs.
-- Also: A11 and A19 have image_url pointing to non-existent filenames.
--
-- Fix: Align all image_url values with the actual filenames on disk.
-- =============================================================================

-- A1a: disk = 'A1a Gevaarlijke bocht naar rechts.png'
UPDATE traffic_signs
SET    image_url  = 'images/signs/danger_signs/A1a Gevaarlijke bocht naar rechts.png',
       updated_at = NOW()
WHERE  sign_code  = 'A1a';

-- A1b: disk = 'A1b Gevaarlijke bocht naar rechts.png'
UPDATE traffic_signs
SET    image_url  = 'images/signs/danger_signs/A1b Gevaarlijke bocht naar rechts.png',
       updated_at = NOW()
WHERE  sign_code  = 'A1b';

-- A1c: disk = 'A1c Gevaarlijke dubbele bocht (rechts-links).png'
UPDATE traffic_signs
SET    image_url  = 'images/signs/danger_signs/A1c Gevaarlijke dubbele bocht (rechts-links).png',
       updated_at = NOW()
WHERE  sign_code  = 'A1c';

-- A1d: disk = 'A1d Gevaarlijke dubbele of meer dan twee bochten, de eerste naar rechts.png'
UPDATE traffic_signs
SET    image_url  = 'images/signs/danger_signs/A1d Gevaarlijke dubbele of meer dan twee bochten, de eerste naar rechts.png',
       updated_at = NOW()
WHERE  sign_code  = 'A1d';

-- A7b: disk = 'A7b Versmalling langs rechts.png'
UPDATE traffic_signs
SET    image_url  = 'images/signs/danger_signs/A7b Versmalling langs rechts.png',
       updated_at = NOW()
WHERE  sign_code  = 'A7b';

-- A7c: disk = 'A7c Rijbaanversmalling langs rechts.png'
UPDATE traffic_signs
SET    image_url  = 'images/signs/danger_signs/A7c Rijbaanversmalling langs rechts.png',
       updated_at = NOW()
WHERE  sign_code  = 'A7c';

-- A11: disk = 'A11 Slecht wegdek (oneffenheden).png'
--      V122 set a non-existent 'Uitweg op een kaai of oever.png' filename.
--      V130 confirmed A11 = Slecht wegdek (official Belgian name).
UPDATE traffic_signs
SET    image_url  = 'images/signs/danger_signs/A11 Slecht wegdek (oneffenheden).png',
       updated_at = NOW()
WHERE  sign_code  = 'A11';

-- A19: disk = 'A19 Vallende stenen.png'
--      V122 set 'Vallende stenen.png' (without 'links') — file does not exist on disk.
UPDATE traffic_signs
SET    image_url  = 'images/signs/danger_signs/A19 Vallende stenen.png',
       updated_at = NOW()
WHERE  sign_code  = 'A19';

-- =============================================================================
-- road_signs: same fixes for the Sign Quiz system table.
-- road_signs.image_path is seeded from sign.json files which still use the
-- old V122 convention. Until sign.json files are updated these DB fixes are
-- the authoritative correction for the running application.
-- =============================================================================

UPDATE road_signs
SET image_path  = 'images/signs/danger_signs/A1a Gevaarlijke bocht naar rechts.png',
    name_nl     = 'Gevaarlijke bocht naar rechts',
    name_en     = 'Dangerous curve to the right',
    name_fr     = 'Virage dangereux à droite',
    name_ar     = 'منعطف خطير إلى اليمين',
    updated_at  = NOW()
WHERE sign_code = 'A1a';

UPDATE road_signs
SET image_path  = 'images/signs/danger_signs/A1b Gevaarlijke bocht naar rechts.png',
    name_nl     = 'Gevaarlijke bocht naar links',
    name_en     = 'Dangerous curve to the left',
    name_fr     = 'Virage dangereux à gauche',
    name_ar     = 'منعطف خطير إلى اليسار',
    updated_at  = NOW()
WHERE sign_code = 'A1b';

UPDATE road_signs
SET image_path  = 'images/signs/danger_signs/A1c Gevaarlijke dubbele bocht (rechts-links).png',
    name_nl     = 'Gevaarlijke dubbele bocht (rechts-links)',
    name_en     = 'Dangerous double curve (right-left)',
    name_fr     = 'Double virage dangereux (droite-gauche)',
    name_ar     = 'منعطف مزدوج خطير (يمين-يسار)',
    updated_at  = NOW()
WHERE sign_code = 'A1c';

UPDATE road_signs
SET image_path  = 'images/signs/danger_signs/A1d Gevaarlijke dubbele of meer dan twee bochten, de eerste naar rechts.png',
    name_nl     = 'Gevaarlijke dubbele bocht (links-rechts)',
    name_en     = 'Dangerous double curve (left-right)',
    name_fr     = 'Double virage dangereux (gauche-droite)',
    name_ar     = 'منعطف مزدوج خطير (يسار-يمين)',
    updated_at  = NOW()
WHERE sign_code = 'A1d';

UPDATE road_signs
SET image_path  = 'images/signs/danger_signs/A7b Versmalling langs rechts.png',
    name_nl     = 'Versmalling langs rechts',
    name_en     = 'Road narrows on the right',
    name_fr     = 'Rétrécissement à droite',
    name_ar     = 'تضيق الطريق من جهة اليمين',
    updated_at  = NOW()
WHERE sign_code = 'A7b';

UPDATE road_signs
SET image_path  = 'images/signs/danger_signs/A7c Rijbaanversmalling langs rechts.png',
    name_nl     = 'Versmalling langs links',
    name_en     = 'Road narrows on the left',
    name_fr     = 'Rétrécissement à gauche',
    name_ar     = 'تضيق الطريق من جهة اليسار',
    updated_at  = NOW()
WHERE sign_code = 'A7c';

UPDATE road_signs
SET image_path  = 'images/signs/danger_signs/A11 Slecht wegdek (oneffenheden).png',
    name_nl     = 'Slecht wegdek',
    name_en     = 'Bumpy or uneven road',
    name_fr     = 'Mauvais état de la chaussée',
    name_ar     = 'طريق سيء أو وعر',
    updated_at  = NOW()
WHERE sign_code = 'A11';

UPDATE road_signs
SET image_path  = 'images/signs/danger_signs/A19 Vallende stenen.png',
    updated_at  = NOW()
WHERE sign_code = 'A19';
