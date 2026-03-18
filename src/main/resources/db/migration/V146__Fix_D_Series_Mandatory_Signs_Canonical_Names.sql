-- =============================================================================
-- V146: Enforce canonical mandatory_signs filenames across all D-series signs
-- =============================================================================
-- Root cause:
--   1. D1c row holds "rechts afslaan" data but image was patched to D1c path
--      by V142. The sign_code and canonical image are both wrong for this row.
--   2. D1a-links / D1a-rechts use non-canonical sign_codes (should be D1c / D1d).
--   3. D3a/D3b names have extra parenthetical suffix not present in filenames.
--   4. D4, D4-links, D4-rechts use Dutch-suffix codes vs. canonical D4-straight,
--      D4-left, D4-right as required by the finalized filename set.
--   5. D1b sign_code must become D1b-left (left-turn variant).
--   6. D5, D7, D9a, D9b exist on disk but have no rows in either table.
-- =============================================================================
-- Disk canonical filenames (source of truth):
--   D1a   Verplichting rechtdoor.png
--   D1b   Verplichting links afslaan.png
--   D1b   Verplichting rechts afslaan.png
--   D1c   Verplichting links aanhouden.png
--   D1d   Verplichting rechts aanhouden.png
--   D1e   Verplicht de aangeduide richting te volgen (linksaf).png
--   D1f   Verplicht de aangeduide richting te volgen (rechtsaf).png
--   D3a   Verplicht een van de pijlen te volgen.png
--   D3b   Verplicht een van de pijlen te volgen.png
--   D4    Verplicht linksaf voor voertuigen die gevaarlijke goederen vervoeren.png
--   D4    Verplicht rechtdoor voor voertuigen die gevaarlijke goederen vervoeren.png
--   D4    Verplicht rechts voor voertuigen die gevaarlijke goederen vervoeren.png
--   D5    Verplicht rondgaand verkeer.png
--   D7    Verplicht fietspad.png
--   D9a   Deel van de weg voorbehouden voor voetgangers en fietsers.png
--   D9b   Deel van de weg voorbehouden voor voetgangers en fietsers.png
--   D10   Deel van de weg voorbehouden voor voetgangers en fietsers.png
--   D11   Verplichte weg voor voetgangers.png
-- =============================================================================

-- =============================================================================
-- SECTION 1: traffic_signs — rename sign_codes, fix images, fix names
-- Order matters to avoid unique-key conflicts during renames.
-- =============================================================================

-- Step 1a: D1c (confused row with wrong image) → D1b-right + fix image_path/url
UPDATE traffic_signs
SET   sign_code              = 'D1b-right',
      name_nl                = 'Verplichting rechts afslaan',
      name_en                = 'Mandatory right turn',
      name_fr                = 'Obligation de tourner à droite',
      image_url              = 'images/signs/mandatory_signs/D1b Verplichting rechts afslaan.png',
      image_path             = 'images/signs/mandatory_signs/D1b Verplichting rechts afslaan.png',
      updated_at             = NOW()
WHERE sign_code = 'D1c';

-- Step 1b: D1a-links → D1c (image was already correct: D1c Verplichting links aanhouden.png)
UPDATE traffic_signs
SET   sign_code   = 'D1c',
      name_nl     = 'Verplichting links aanhouden',
      name_en     = 'Mandatory keep left',
      name_fr     = 'Obligation de serrer à gauche',
      updated_at  = NOW()
WHERE sign_code = 'D1a-links';

-- Step 1c: D1a-rechts → D1d (image was already correct: D1d Verplichting rechts aanhouden.png)
UPDATE traffic_signs
SET   sign_code   = 'D1d',
      name_nl     = 'Verplichting rechts aanhouden',
      name_en     = 'Mandatory keep right',
      name_fr     = 'Obligation de serrer à droite',
      updated_at  = NOW()
WHERE sign_code = 'D1a-rechts';

-- Step 1d: D1b → D1b-left (left-turn canonical variant)
UPDATE traffic_signs
SET   sign_code  = 'D1b-left',
      updated_at = NOW()
WHERE sign_code = 'D1b';

-- Step 1e: D3a — remove extra parenthetical "(rechtdoor of linksaf)" from name_nl
UPDATE traffic_signs
SET   name_nl    = 'Verplicht één van de pijlen te volgen',
      name_en    = 'Mandatory to follow one of the arrows',
      name_fr    = 'Obligation de suivre une des flèches',
      updated_at = NOW()
WHERE sign_code = 'D3a';

-- Step 1f: D3b — remove extra parenthetical "(rechtdoor of rechtsaf)" from name_nl
UPDATE traffic_signs
SET   name_nl    = 'Verplicht één van de pijlen te volgen',
      name_en    = 'Mandatory to follow one of the arrows',
      name_fr    = 'Obligation de suivre une des flèches',
      updated_at = NOW()
WHERE sign_code = 'D3b';

-- Step 1g: D4 → D4-straight
UPDATE traffic_signs
SET   sign_code  = 'D4-straight',
      updated_at = NOW()
WHERE sign_code = 'D4';

-- Step 1h: D4-links → D4-left
UPDATE traffic_signs
SET   sign_code  = 'D4-left',
      updated_at = NOW()
WHERE sign_code = 'D4-links';

-- Step 1i: D4-rechts → D4-right
UPDATE traffic_signs
SET   sign_code  = 'D4-right',
      updated_at = NOW()
WHERE sign_code = 'D4-rechts';

-- Step 1j: INSERT missing D5, D7, D9a, D9b into traffic_signs
INSERT INTO traffic_signs
  (category_id, sign_code, normalized_sign_code, name_nl, name_en, name_fr, name_ar,
   description_nl, description_en, description_fr, description_ar,
   image_url, image_path, is_active, created_at, updated_at)
VALUES
  (4, 'D5', 'D5',
   'Verplicht rondgaand verkeer',
   'Mandatory roundabout',
   'Circulation giratoire obligatoire',
   'دوران إلزامي',
   'Verplicht rondgaand verkeer. Bestuurders in de rotonde hebben voorrang.',
   'Mandatory roundabout. Drivers already in the roundabout have priority.',
   'Circulation giratoire obligatoire. Les conducteurs dans le giratoire ont la priorité.',
   'دوران إلزامي. يتمتع السائقون في الدوار بحق الأولوية.',
   'images/signs/mandatory_signs/D5 Verplicht rondgaand verkeer.png',
   'images/signs/mandatory_signs/D5 Verplicht rondgaand verkeer.png',
   1, NOW(), NOW()),

  (4, 'D7', 'D7',
   'Verplicht fietspad',
   'Mandatory cycle path',
   'Piste cyclable obligatoire',
   'مسار دراجات إلزامي',
   'Verplicht fietspad. Alleen fietsen zijn toegelaten op dit pad.',
   'Mandatory cycle path. Only bicycles are allowed on this path.',
   'Piste cyclable obligatoire. Seuls les vélos sont autorisés sur cette piste.',
   'مسار الدراجات إلزامي. يُسمح فقط للدراجات على هذا المسار.',
   'images/signs/mandatory_signs/D7 Verplicht fietspad.png',
   'images/signs/mandatory_signs/D7 Verplicht fietspad.png',
   1, NOW(), NOW()),

  (4, 'D9a', 'D9a',
   'Deel van de weg voorbehouden voor voetgangers en fietsers',
   'Part of the road reserved for pedestrians and cyclists',
   'Partie de la voie réservée aux piétons et aux cyclistes',
   'جزء من الطريق مخصص للمشاة والدراجين',
   'Deel van de weg voorbehouden voor voetgangers (links) en fietsers (rechts).',
   'Part of the road reserved for pedestrians (left) and cyclists (right).',
   'Partie de la voie réservée aux piétons (gauche) et aux cyclistes (droite).',
   'جزء من الطريق مخصص للمشاة (يسار) والدراجين (يمين).',
   'images/signs/mandatory_signs/D9a Deel van de weg voorbehouden voor voetgangers en fietsers.png',
   'images/signs/mandatory_signs/D9a Deel van de weg voorbehouden voor voetgangers en fietsers.png',
   1, NOW(), NOW()),

  (4, 'D9b', 'D9b',
   'Deel van de weg voorbehouden voor voetgangers en fietsers',
   'Part of the road reserved for pedestrians and cyclists',
   'Partie de la voie réservée aux piétons et aux cyclistes',
   'جزء من الطريق مخصص للمشاة والدراجين',
   'Deel van de weg voorbehouden voor voetgangers en fietsers (fietser links).',
   'Part of the road reserved for pedestrians and cyclists (cyclist left).',
   'Partie de la voie réservée aux piétons et aux cyclistes (cycliste à gauche).',
   'جزء من الطريق مخصص للمشاة والدراجين (الدراجة على اليسار).',
   'images/signs/mandatory_signs/D9b Deel van de weg voorbehouden voor voetgangers en fietsers.png',
   'images/signs/mandatory_signs/D9b Deel van de weg voorbehouden voor voetgangers en fietsers.png',
   1, NOW(), NOW());

-- =============================================================================
-- SECTION 2: road_signs — same renames + normalized_sign_code must stay in sync
-- =============================================================================

-- Step 2a: D1c → D1b-right + fix image_path
UPDATE road_signs
SET   sign_code              = 'D1b-right',
      normalized_sign_code   = 'D1b-right',
      name_nl                = 'Verplichting rechts afslaan',
      name_en                = 'Mandatory right turn',
      name_fr                = 'Obligation de tourner à droite',
      image_path             = 'images/signs/mandatory_signs/D1b Verplichting rechts afslaan.png',
      updated_at             = NOW()
WHERE sign_code = 'D1c';

-- Step 2b: D1a-links → D1c
UPDATE road_signs
SET   sign_code              = 'D1c',
      normalized_sign_code   = 'D1c',
      name_nl                = 'Verplichting links aanhouden',
      name_en                = 'Mandatory keep left',
      name_fr                = 'Obligation de serrer à gauche',
      updated_at             = NOW()
WHERE sign_code = 'D1a-links';

-- Step 2c: D1a-rechts → D1d
UPDATE road_signs
SET   sign_code              = 'D1d',
      normalized_sign_code   = 'D1d',
      name_nl                = 'Verplichting rechts aanhouden',
      name_en                = 'Mandatory keep right',
      name_fr                = 'Obligation de serrer à droite',
      updated_at             = NOW()
WHERE sign_code = 'D1a-rechts';

-- Step 2d: D1b → D1b-left
UPDATE road_signs
SET   sign_code              = 'D1b-left',
      normalized_sign_code   = 'D1b-left',
      updated_at             = NOW()
WHERE sign_code = 'D1b';

-- Step 2e: D3a name fix
UPDATE road_signs
SET   name_nl    = 'Verplicht één van de pijlen te volgen',
      name_en    = 'Mandatory to follow one of the arrows',
      name_fr    = 'Obligation de suivre une des flèches',
      updated_at = NOW()
WHERE sign_code = 'D3a';

-- Step 2f: D3b name fix
UPDATE road_signs
SET   name_nl    = 'Verplicht één van de pijlen te volgen',
      name_en    = 'Mandatory to follow one of the arrows',
      name_fr    = 'Obligation de suivre une des flèches',
      updated_at = NOW()
WHERE sign_code = 'D3b';

-- Step 2g: D4 → D4-straight
UPDATE road_signs
SET   sign_code              = 'D4-straight',
      normalized_sign_code   = 'D4-straight',
      updated_at             = NOW()
WHERE sign_code = 'D4';

-- Step 2h: D4-links → D4-left
UPDATE road_signs
SET   sign_code              = 'D4-left',
      normalized_sign_code   = 'D4-left',
      updated_at             = NOW()
WHERE sign_code = 'D4-links';

-- Step 2i: D4-rechts → D4-right
UPDATE road_signs
SET   sign_code              = 'D4-right',
      normalized_sign_code   = 'D4-right',
      updated_at             = NOW()
WHERE sign_code = 'D4-rechts';

-- Step 2j: INSERT missing D5, D7, D9a, D9b into road_signs
INSERT INTO road_signs
  (sign_code, normalized_sign_code, category, image_path, serious_violation,
   name_nl, name_en, name_fr, name_ar,
   description_nl, description_en, description_fr, description_ar,
   is_active, created_at, updated_at)
VALUES
  ('D5', 'D5', 'MANDATORY',
   'images/signs/mandatory_signs/D5 Verplicht rondgaand verkeer.png', 0,
   'Verplicht rondgaand verkeer', 'Mandatory roundabout', 'Circulation giratoire obligatoire', 'دوران إلزامي',
   'Verplicht rondgaand verkeer. Bestuurders in de rotonde hebben voorrang.',
   'Mandatory roundabout. Drivers already in the roundabout have priority.',
   'Circulation giratoire obligatoire. Les conducteurs dans le giratoire ont la priorité.',
   'دوران إلزامي. يتمتع السائقون في الدوار بحق الأولوية.',
   1, NOW(), NOW()),

  ('D7', 'D7', 'MANDATORY',
   'images/signs/mandatory_signs/D7 Verplicht fietspad.png', 0,
   'Verplicht fietspad', 'Mandatory cycle path', 'Piste cyclable obligatoire', 'مسار دراجات إلزامي',
   'Verplicht fietspad. Alleen fietsen zijn toegelaten op dit pad.',
   'Mandatory cycle path. Only bicycles are allowed on this path.',
   'Piste cyclable obligatoire. Seuls les vélos sont autorisés sur cette piste.',
   'مسار الدراجات إلزامي. يُسمح فقط للدراجات على هذا المسار.',
   1, NOW(), NOW()),

  ('D9a', 'D9a', 'MANDATORY',
   'images/signs/mandatory_signs/D9a Deel van de weg voorbehouden voor voetgangers en fietsers.png', 0,
   'Deel van de weg voorbehouden voor voetgangers en fietsers',
   'Part of the road reserved for pedestrians and cyclists',
   'Partie de la voie réservée aux piétons et aux cyclistes',
   'جزء من الطريق مخصص للمشاة والدراجين',
   'Deel van de weg voorbehouden voor voetgangers (links) en fietsers (rechts).',
   'Part of the road reserved for pedestrians (left) and cyclists (right).',
   'Partie de la voie réservée aux piétons (gauche) et aux cyclistes (droite).',
   'جزء من الطريق مخصص للمشاة (يسار) والدراجين (يمين).',
   1, NOW(), NOW()),

  ('D9b', 'D9b', 'MANDATORY',
   'images/signs/mandatory_signs/D9b Deel van de weg voorbehouden voor voetgangers en fietsers.png', 0,
   'Deel van de weg voorbehouden voor voetgangers en fietsers',
   'Part of the road reserved for pedestrians and cyclists',
   'Partie de la voie réservée aux piétons et aux cyclistes',
   'جزء من الطريق مخصص للمشاة والدراجين',
   'Deel van de weg voorbehouden voor voetgangers en fietsers (fietser links).',
   'Part of the road reserved for pedestrians and cyclists (cyclist left).',
   'Partie de la voie réservée aux piétons et aux cyclistes (cycliste à gauche).',
   'جزء من الطريق مخصص للمشاة والدراجين (الدراجة على اليسار).',
   1, NOW(), NOW());
