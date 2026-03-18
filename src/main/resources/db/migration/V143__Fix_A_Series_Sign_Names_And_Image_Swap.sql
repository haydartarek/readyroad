-- =============================================================================
-- V143: Fix A-series sign names, descriptions, and image URLs per Belgian KB
-- =============================================================================
--
-- ROOT CAUSE:
--   V130 reversed name_nl (and other language) fields for A1a/A1b, A1c/A1d,
--   A7b/A7c from the Belgian statutory order (KB 1998) convention.
--   V135 then re-aligned image_url to match the (now wrong) reversed names.
--   Result: names AND images are mutually consistent but BOTH contradict the
--   Belgian statutory sign code assignment.
--
-- WHAT THIS MIGRATION RESTORES (Belgian KB 1998):
--   A1a = Gevaarlijke bocht naar LINKS   (sign A-1a, first left-curve)
--   A1b = Gevaarlijke bocht naar RECHTS  (sign A-1b, first right-curve)
--   A1c = Gevaarlijke dubbele bocht (LINKS-rechts)  — first bend to LEFT
--   A1d = Gevaarlijke dubbele bocht (RECHTS-links)  — first bend to RIGHT
--   A7b = Versmalling langs LINKS   (sign A-7b, right side stays open)
--   A7c = Versmalling langs RECHTS  (sign A-7c, left side stays open)
--
-- IMAGE URL STRATEGY:
--   Physical disk files are named after the sign codes but their content
--   was swapped during original asset creation:
--     disk "A1a...naar rechts.png"  → shows a RIGHT-bend arrow
--     disk "A1b...naar links.png"   → shows a LEFT-bend arrow
--   We cross-assign so each sign_code gets the file with matching CONTENT:
--     A1a (= links) → points to "A1b Gevaarlijke bocht naar links.png"
--     A1b (= rechts) → points to "A1a Gevaarlijke bocht naar rechts.png"
--     (same logic for A1c/A1d and A7b/A7c pairs)
--
-- D1c NOTE (not changed here):
--   D1c (Verplichting rechts afslaan = mandatory right turn) has no correct
--   image file on disk. The file "D1c Verplichting links aanhouden.png" shows
--   a KEEP-LEFT sign, not a right-turn arrow. A correct D1c image must be
--   created (visual mirror of D1b) and placed at:
--     public/images/signs/mandatory_signs/D1c Verplichting rechts afslaan.png
--   Only then should image_url/image_path for D1c be updated.
--
-- BOTH TABLES UPDATED: traffic_signs (image_url + image_path) and road_signs (image_path)
-- =============================================================================

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 1: Fix traffic_signs
-- ─────────────────────────────────────────────────────────────────────────────

-- ── A1a: Belgian KB = dangerous curve to the LEFT ────────────────────────────
UPDATE traffic_signs
SET    name_ar         = 'منحنى خطير إلى اليسار',
       name_nl         = 'Gevaarlijke bocht naar links',
       name_en         = 'Dangerous curve to the left',
       name_fr         = 'Virage dangereux à gauche',
       description_ar  = 'منعطف خطير إلى اليسار.',
       description_nl  = 'Waarschuwing voor een gevaarlijke bocht naar links.',
       description_en  = 'Dangerous bend to the left.',
       description_fr  = 'Virage dangereux à gauche.',
       image_url       = 'images/signs/danger_signs/A1b Gevaarlijke bocht naar links.png',
       image_path      = 'images/signs/danger_signs/A1b Gevaarlijke bocht naar links.png',
       updated_at      = NOW()
WHERE  sign_code = 'A1a';

-- ── A1b: Belgian KB = dangerous curve to the RIGHT ───────────────────────────
UPDATE traffic_signs
SET    name_ar         = 'منحنى خطير إلى اليمين',
       name_nl         = 'Gevaarlijke bocht naar rechts',
       name_en         = 'Dangerous curve to the right',
       name_fr         = 'Virage dangereux à droite',
       description_ar  = 'منعطف خطير إلى اليمين.',
       description_nl  = 'Waarschuwing voor een gevaarlijke bocht naar rechts.',
       description_en  = 'Dangerous bend to the right.',
       description_fr  = 'Virage dangereux à droite.',
       image_url       = 'images/signs/danger_signs/A1a Gevaarlijke bocht naar rechts.png',
       image_path      = 'images/signs/danger_signs/A1a Gevaarlijke bocht naar rechts.png',
       updated_at      = NOW()
WHERE  sign_code = 'A1b';

-- ── A1c: Belgian KB = double curve, first bend to the LEFT (links-rechts) ────
UPDATE traffic_signs
SET    name_ar         = 'منحنى مزدوج خطير (يسار-يمين)',
       name_nl         = 'Gevaarlijke dubbele bocht (links-rechts)',
       name_en         = 'Dangerous double curve (left-right)',
       name_fr         = 'Double virage dangereux (gauche-droite)',
       description_ar  = 'منعطف مزدوج خطير، الأول إلى اليسار.',
       description_nl  = 'Gevaarlijke dubbele bochten, de eerste naar links.',
       description_en  = 'Dangerous bend. First bend to the left.',
       description_fr  = 'Virages dangereux, le premier à gauche.',
       image_url       = 'images/signs/danger_signs/A1d Gevaarlijke dubbele bocht (links-rechts).png',
       image_path      = 'images/signs/danger_signs/A1d Gevaarlijke dubbele bocht (links-rechts).png',
       updated_at      = NOW()
WHERE  sign_code = 'A1c';

-- ── A1d: Belgian KB = double curve, first bend to the RIGHT (rechts-links) ───
UPDATE traffic_signs
SET    name_ar         = 'منحنى مزدوج خطير (يمين-يسار)',
       name_nl         = 'Gevaarlijke dubbele bocht (rechts-links)',
       name_en         = 'Dangerous double curve (right-left)',
       name_fr         = 'Double virage dangereux (droite-gauche)',
       description_ar  = 'منعطف مزدوج خطير، الأول إلى اليمين.',
       description_nl  = 'Gevaarlijke dubbele bochten, de eerste naar rechts.',
       description_en  = 'Dangerous bend. First bend to the right.',
       description_fr  = 'Virages dangereux, le premier à droite.',
       image_url       = 'images/signs/danger_signs/A1c Gevaarlijke dubbele bocht (rechts-links).png',
       image_path      = 'images/signs/danger_signs/A1c Gevaarlijke dubbele bocht (rechts-links).png',
       updated_at      = NOW()
WHERE  sign_code = 'A1d';

-- ── A7b: Belgian KB = road narrows on the LEFT (right lane stays clear) ──────
UPDATE traffic_signs
SET    name_ar         = 'تضيق الطريق من جهة اليسار',
       name_nl         = 'Versmalling langs links',
       name_en         = 'Road narrows on the left',
       name_fr         = 'Rétrécissement à gauche',
       description_ar  = 'تضييق الطريق على اليسار.',
       description_nl  = 'Rijbaanversmalling langs links.',
       description_en  = 'Road narrowing on the left.',
       description_fr  = 'Rétrécissement de la chaussée à gauche.',
       image_url       = 'images/signs/danger_signs/A7c Versmalling langs links.png',
       image_path      = 'images/signs/danger_signs/A7c Versmalling langs links.png',
       updated_at      = NOW()
WHERE  sign_code = 'A7b';

-- ── A7c: Belgian KB = road narrows on the RIGHT (left lane stays clear) ──────
UPDATE traffic_signs
SET    name_ar         = 'تضيق الطريق من جهة اليمين',
       name_nl         = 'Versmalling langs rechts',
       name_en         = 'Road narrows on the right',
       name_fr         = 'Rétrécissement à droite',
       description_ar  = 'تضييق الطريق على اليمين.',
       description_nl  = 'Rijbaanversmalling langs rechts.',
       description_en  = 'Road narrowing on the right.',
       description_fr  = 'Rétrécissement de la chaussée à droite.',
       image_url       = 'images/signs/danger_signs/A7b Versmalling langs rechts.png',
       image_path      = 'images/signs/danger_signs/A7b Versmalling langs rechts.png',
       updated_at      = NOW()
WHERE  sign_code = 'A7c';


-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 2: Sync road_signs image_path for the same signs
-- ─────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    image_path = 'images/signs/danger_signs/A1b Gevaarlijke bocht naar links.png'
WHERE  sign_code  = 'A1a';

UPDATE road_signs
SET    image_path = 'images/signs/danger_signs/A1a Gevaarlijke bocht naar rechts.png'
WHERE  sign_code  = 'A1b';

UPDATE road_signs
SET    image_path = 'images/signs/danger_signs/A1d Gevaarlijke dubbele bocht (links-rechts).png'
WHERE  sign_code  = 'A1c';

UPDATE road_signs
SET    image_path = 'images/signs/danger_signs/A1c Gevaarlijke dubbele bocht (rechts-links).png'
WHERE  sign_code  = 'A1d';

UPDATE road_signs
SET    image_path = 'images/signs/danger_signs/A7c Versmalling langs links.png'
WHERE  sign_code  = 'A7b';

UPDATE road_signs
SET    image_path = 'images/signs/danger_signs/A7b Versmalling langs rechts.png'
WHERE  sign_code  = 'A7c';
