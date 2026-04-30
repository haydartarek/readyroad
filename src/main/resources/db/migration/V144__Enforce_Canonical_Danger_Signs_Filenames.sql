-- =============================================================================
-- V144: Enforce canonical danger_signs filenames across traffic_signs and road_signs
-- =============================================================================
--
-- SOURCE OF TRUTH: readyroad/public/images/signs/danger_signs/ (34 canonical files)
--
-- Changes in traffic_signs:
--   image_url + image_path → canonical filename on disk
--   name_nl updated where the filename label differs from the stored Dutch name
--
-- Changes in road_signs:
--   image_path → canonical filename on disk
--   A9 inserted (exists in traffic_signs but not yet in road_signs)
--   A17 fixed (had stale "Zijdelingse wind.png" in road_signs only)
--   A33 fixed (road_signs still pointed to "A33 Beweegbare brug.png")
--
-- STRICT SPECIAL RULES (user-approved, must NOT be auto-corrected):
--   A1b filename: "A1b Gevaarlijke bocht naar rechts.png"  (no trailing 's')
--   A7b filename: "A7b Rijbaanversmalling langs links.png"             (no "langs")
--   A7c filename: "A7c Rijbaanversmalling langs rechts.png"             (no "langs", no 's')
--   A45 filename: "A45 Overweg voor enkel spoor.png"           (lowercase, space)
-- =============================================================================

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 1: traffic_signs — image_url + image_path + name corrections
-- ─────────────────────────────────────────────────────────────────────────────

-- A1a: was using A1b's file after V143; now uses own canonical file
UPDATE traffic_signs
SET    image_url  = 'images/signs/danger_signs/A1a Gevaarlijke bocht naar links.png',
       image_path = 'images/signs/danger_signs/A1a Gevaarlijke bocht naar links.png',
       updated_at = NOW()
WHERE  sign_code = 'A1a';

-- A1b: new canonical filename "naar recht" (no trailing 's') — name_nl updated to match
UPDATE traffic_signs
SET    name_nl    = 'Gevaarlijke bocht naar recht',
       image_url  = 'images/signs/danger_signs/A1b Gevaarlijke bocht naar rechts.png',
       image_path = 'images/signs/danger_signs/A1b Gevaarlijke bocht naar rechts.png',
       updated_at = NOW()
WHERE  sign_code = 'A1b';

-- A1c: new canonical long description filename — name_nl updated to match filename label
UPDATE traffic_signs
SET    name_nl    = 'Gevaarlijke dubbele of meer dan twee bochten, de eerste naar links',
       name_en    = 'Dangerous double or more than two bends, first to the left',
       name_fr    = 'Double virage dangereux ou plus de deux virages, le premier à gauche',
       image_url  = 'images/signs/danger_signs/A1c Gevaarlijke dubbele of meer dan twee bochten, de eerste naar links.png',
       image_path = 'images/signs/danger_signs/A1c Gevaarlijke dubbele of meer dan twee bochten, de eerste naar links.png',
       updated_at = NOW()
WHERE  sign_code = 'A1c';

-- A1d: new canonical long description filename — name_nl updated to match filename label
UPDATE traffic_signs
SET    name_nl    = 'Gevaarlijke dubbele of meer dan twee bochten, de eerste naar rechts',
       name_en    = 'Dangerous double or more than two bends, first to the right',
       name_fr    = 'Double virage dangereux ou plus de deux virages, le premier à droite',
       image_url  = 'images/signs/danger_signs/A1d Gevaarlijke dubbele of meer dan twee bochten, de eerste naar rechts.png',
       image_path = 'images/signs/danger_signs/A1d Gevaarlijke dubbele of meer dan twee bochten, de eerste naar rechts.png',
       updated_at = NOW()
WHERE  sign_code = 'A1d';

-- A7a: image already correct; update name_nl to match filename label
UPDATE traffic_signs
SET    name_nl    = 'Versmalling langs beide zijden',
       name_en    = 'Road narrows on both sides',
       name_fr    = 'Rétrécissement des deux côtés',
       updated_at = NOW()
WHERE  sign_code = 'A7a';

-- A7b: new canonical filename "Versmalling links" (no "langs") — name_nl updated
UPDATE traffic_signs
SET    name_nl    = 'Versmalling links',
       name_en    = 'Road narrows on the left',
       name_fr    = 'Rétrécissement à gauche',
       name_ar    = 'تضيق الطريق من جهة اليسار',
       image_url  = 'images/signs/danger_signs/A7b Rijbaanversmalling langs links.png',
       image_path = 'images/signs/danger_signs/A7b Rijbaanversmalling langs links.png',
       updated_at = NOW()
WHERE  sign_code = 'A7b';

-- A7c: new canonical filename "Versmalling recht" (no "langs", no 's') — name_nl updated
UPDATE traffic_signs
SET    name_nl    = 'Versmalling recht',
       name_en    = 'Road narrows on the right',
       name_fr    = 'Rétrécissement à droite',
       name_ar    = 'تضيق الطريق من جهة اليمين',
       image_url  = 'images/signs/danger_signs/A7c Rijbaanversmalling langs rechts.png',
       image_path = 'images/signs/danger_signs/A7c Rijbaanversmalling langs rechts.png',
       updated_at = NOW()
WHERE  sign_code = 'A7c';

-- A11: was "A11 Slecht wegdek (oneffenheden).png"; canonical = "A11 Uitweg op een kaai of een oever.png"
UPDATE traffic_signs
SET    name_nl    = 'Uitweg op kaai of oever',
       name_en    = 'Exit to quay or embankment',
       name_fr    = 'Sortie sur quai ou berge',
       name_ar    = 'مخرج على رصيف أو ضفة',
       image_url  = 'images/signs/danger_signs/A11 Uitweg op een kaai of een oever.png',
       image_path = 'images/signs/danger_signs/A11 Uitweg op een kaai of een oever.png',
       updated_at = NOW()
WHERE  sign_code = 'A11';

-- A15: name_nl already correct ("Gladde rijbaan - Slipgevaar"); update image only
UPDATE traffic_signs
SET    image_url  = 'images/signs/danger_signs/A15 Glibberige rijbaan.png',
       image_path = 'images/signs/danger_signs/A15 Glibberige rijbaan.png',
       updated_at = NOW()
WHERE  sign_code = 'A15';

-- A21: name_nl already correct; update image from "Overstekende voetgangers" to canonical
UPDATE traffic_signs
SET    image_url  = 'images/signs/danger_signs/A21 Oversteekplaats voor voetgangers.png',
       image_path = 'images/signs/danger_signs/A21 Oversteekplaats voor voetgangers.png',
       updated_at = NOW()
WHERE  sign_code = 'A21';

-- A23: name_nl already correct; update image from "Kinderen" to canonical
UPDATE traffic_signs
SET    image_url  = 'images/signs/danger_signs/A23 Plaats waar speciaal veel kinderen komen.png',
       image_path = 'images/signs/danger_signs/A23 Plaats waar speciaal veel kinderen komen.png',
       updated_at = NOW()
WHERE  sign_code = 'A23';

-- A25: name_nl already correct; update image from "Overstekende fietsers" to canonical
UPDATE traffic_signs
SET    image_url  = 'images/signs/danger_signs/A25 Oversteekplaats voor fietsers en bromfietsers.png',
       image_path = 'images/signs/danger_signs/A25 Oversteekplaats voor fietsers en bromfietsers.png',
       updated_at = NOW()
WHERE  sign_code = 'A25';

-- A33: canonical file = "A33 Verkeerslichten.png"
UPDATE traffic_signs
SET    image_url  = 'images/signs/danger_signs/A33 Verkeerslichten.png',
       image_path = 'images/signs/danger_signs/A33 Verkeerslichten.png',
       updated_at = NOW()
WHERE  sign_code = 'A33';

-- A35: name_nl already correct; update image from "Laagvliegende vliegtuigen" to canonical
UPDATE traffic_signs
SET    image_url  = 'images/signs/danger_signs/A35 Vliegtuigen op geringe hoogte.png',
       image_path = 'images/signs/danger_signs/A35 Vliegtuigen op geringe hoogte.png',
       updated_at = NOW()
WHERE  sign_code = 'A35';

-- A45: canonical = "waarschuwings kruis" (lowercase, with space) — name_nl updated
UPDATE traffic_signs
SET    name_nl    = 'waarschuwings kruis',
       name_en    = 'Warning cross',
       name_fr    = 'Croix de signalisation',
       name_ar    = 'علامة التحذير',
       image_url  = 'images/signs/danger_signs/A45 Overweg voor enkel spoor.png',
       image_path = 'images/signs/danger_signs/A45 Overweg voor enkel spoor.png',
       updated_at = NOW()
WHERE  sign_code = 'A45';

-- A47: canonical = "waarschuwingskruis meerdere sporen" (lowercase, spaces) — name_nl updated
UPDATE traffic_signs
SET    name_nl    = 'waarschuwingskruis meerdere sporen',
       name_en    = 'Warning cross multiple tracks',
       name_fr    = 'Croix de signalisation plusieurs voies',
       name_ar    = 'علامة تحذير مسارات متعددة',
       image_url  = 'images/signs/danger_signs/A47 Overweg voor twee of meer sporen.png',
       image_path = 'images/signs/danger_signs/A47 Overweg voor twee of meer sporen.png',
       updated_at = NOW()
WHERE  sign_code = 'A47';


-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 2: road_signs — image_path corrections
-- ─────────────────────────────────────────────────────────────────────────────

UPDATE road_signs SET image_path = 'images/signs/danger_signs/A1a Gevaarlijke bocht naar links.png'
WHERE sign_code = 'A1a';

UPDATE road_signs SET image_path = 'images/signs/danger_signs/A1b Gevaarlijke bocht naar rechts.png'
WHERE sign_code = 'A1b';

UPDATE road_signs SET image_path = 'images/signs/danger_signs/A1c Gevaarlijke dubbele of meer dan twee bochten, de eerste naar links.png'
WHERE sign_code = 'A1c';

UPDATE road_signs SET image_path = 'images/signs/danger_signs/A1d Gevaarlijke dubbele of meer dan twee bochten, de eerste naar rechts.png'
WHERE sign_code = 'A1d';

UPDATE road_signs SET image_path = 'images/signs/danger_signs/A7b Rijbaanversmalling langs links.png'
WHERE sign_code = 'A7b';

UPDATE road_signs SET image_path = 'images/signs/danger_signs/A7c Rijbaanversmalling langs rechts.png'
WHERE sign_code = 'A7c';

UPDATE road_signs SET image_path = 'images/signs/danger_signs/A11 Uitweg op een kaai of een oever.png'
WHERE sign_code = 'A11';

-- A15: was "A15 Slipgevaar - glad wegdek.png"; canonical = "A15 Glibberige rijbaan.png"
UPDATE road_signs SET image_path = 'images/signs/danger_signs/A15 Glibberige rijbaan.png'
WHERE sign_code = 'A15';

-- A17: road_signs had stale "Zijdelingse wind.png" (never updated by any previous migration)
UPDATE road_signs SET image_path = 'images/signs/danger_signs/A17 Kiezelprojectie.png'
WHERE sign_code = 'A17';

UPDATE road_signs SET image_path = 'images/signs/danger_signs/A21 Oversteekplaats voor voetgangers.png'
WHERE sign_code = 'A21';

UPDATE road_signs SET image_path = 'images/signs/danger_signs/A23 Plaats waar speciaal veel kinderen komen.png'
WHERE sign_code = 'A23';

UPDATE road_signs SET image_path = 'images/signs/danger_signs/A25 Oversteekplaats voor fietsers en bromfietsers.png'
WHERE sign_code = 'A25';

-- A33: road_signs still had "A33 Beweegbare brug.png"; canonical = "A33 Verkeerslichten.png"
UPDATE road_signs SET image_path = 'images/signs/danger_signs/A33 Verkeerslichten.png'
WHERE sign_code = 'A33';

UPDATE road_signs SET image_path = 'images/signs/danger_signs/A35 Vliegtuigen op geringe hoogte.png'
WHERE sign_code = 'A35';

UPDATE road_signs SET image_path = 'images/signs/danger_signs/A45 Overweg voor enkel spoor.png'
WHERE sign_code = 'A45';

UPDATE road_signs SET image_path = 'images/signs/danger_signs/A47 Overweg voor twee of meer sporen.png'
WHERE sign_code = 'A47';


-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 3: road_signs — INSERT A9 (Beweegbare brug)
-- A9 was added to traffic_signs previously but has no road_signs entry yet
-- ─────────────────────────────────────────────────────────────────────────────

INSERT INTO road_signs (sign_code, normalized_sign_code, category, image_path,
                        serious_violation, name_nl, name_en, name_fr, name_ar,
                        description_nl, description_en, description_fr, description_ar,
                        is_active, created_at, updated_at)
SELECT 'A9', 'A9', 'DANGER',
       'images/signs/danger_signs/A9 Beweegbare brug.png',
       0,
       'Beweegbare brug',
       'Movable bridge',
       'Pont mobile',
       'جسر متحرك',
       ts.description_nl, ts.description_en, ts.description_fr, ts.description_ar,
       1, NOW(), NOW()
FROM   traffic_signs ts
WHERE  ts.sign_code = 'A9'
ON DUPLICATE KEY UPDATE
  image_path = VALUES(image_path),
  updated_at = NOW();
