-- =============================================================================
-- V146: Align D-series mandatory signs with disk canonical set (signs_import)
-- Canonical D codes on disk:
-- D10, D11, D13, D1a, D1b-links, D1b-rechts, D1c, D1d, D1e, D1f,
-- D3a, D3b, D4-links, D4-rechtdoor, D4-rechts, D5, D7, D9a
-- =============================================================================

SET NAMES utf8mb4;

-- -----------------------------------------------------------------------------
-- 1) traffic_signs: keep only D codes present in the disk allowlist
-- -----------------------------------------------------------------------------
DELETE FROM traffic_signs
WHERE sign_code LIKE 'D%'
  AND sign_code NOT IN (
    'D10','D11','D13','D1a','D1b-links','D1b-rechts','D1c','D1d','D1e','D1f',
    'D3a','D3b','D4-links','D4-rechtdoor','D4-rechts','D5','D7','D9a'
  );

-- Enforce canonical NL names and canonical image paths for allowed D rows
UPDATE traffic_signs
SET name_nl = CASE sign_code
    WHEN 'D10' THEN 'Deel van de weg voorbehouden voor voetgangers en fietsers'
    WHEN 'D11' THEN 'Verplichte weg voor voetgangers'
    WHEN 'D13' THEN 'Verplichte weg voor ruiters'
    WHEN 'D1a' THEN 'Verplichting rechtdoor'
    WHEN 'D1b-links' THEN 'Verplichting links afslaan'
    WHEN 'D1b-rechts' THEN 'Verplichting rechts afslaan'
    WHEN 'D1c' THEN 'Verplichting links aanhouden'
    WHEN 'D1d' THEN 'Verplichting rechts aanhouden'
    WHEN 'D1e' THEN 'Verplicht links afslaan'
    WHEN 'D1f' THEN 'Verplicht rechts afslaan'
    WHEN 'D3a' THEN 'Verplicht één van de pijlen te volgen'
    WHEN 'D3b' THEN 'Verplicht één van de pijlen te volgen'
    WHEN 'D4-links' THEN 'Verplicht links afslaan gevaarlijke goederen'
    WHEN 'D4-rechtdoor' THEN 'Verplicht rechtdoor gevaarlijke goederen'
    WHEN 'D4-rechts' THEN 'Verplicht rechts afslaan gevaarlijke goederen'
    WHEN 'D5' THEN 'Verplicht rondgaand verkeer'
    WHEN 'D7' THEN 'Verplicht fietspad'
    WHEN 'D9a' THEN 'Deel van de weg voorbehouden voor voetgangers en fietsers'
    ELSE name_nl
  END,
  image_url = CASE sign_code
    WHEN 'D10' THEN 'images/signs/mandatory_signs/D10 Deel van de weg voorbehouden voor voetgangers en fietsers.png'
    WHEN 'D11' THEN 'images/signs/mandatory_signs/D11 Verplichte weg voor voetgangers.png'
    WHEN 'D13' THEN 'images/signs/mandatory_signs/D13 Verplichte weg voor ruiters.png'
    WHEN 'D1a' THEN 'images/signs/mandatory_signs/D1a Verplichting rechtdoor.png'
    WHEN 'D1b-links' THEN 'images/signs/mandatory_signs/D1b Verplichting links afslaan.png'
    WHEN 'D1b-rechts' THEN 'images/signs/mandatory_signs/D1b Verplichting rechts afslaan.png'
    WHEN 'D1c' THEN 'images/signs/mandatory_signs/D1c Verplichting links aanhouden.png'
    WHEN 'D1d' THEN 'images/signs/mandatory_signs/D1d Verplichting rechts aanhouden.png'
    WHEN 'D1e' THEN 'images/signs/mandatory_signs/D1e Verplicht links afslaan.png'
    WHEN 'D1f' THEN 'images/signs/mandatory_signs/D1f Verplicht rechts afslaan.png'
    WHEN 'D3a' THEN 'images/signs/mandatory_signs/D3a Verplicht één van de pijlen te volgen.png'
    WHEN 'D3b' THEN 'images/signs/mandatory_signs/D3b Verplicht één van de pijlen te volgen.png'
    WHEN 'D4-links' THEN 'images/signs/mandatory_signs/D4 Verplicht links afslaan gevaarlijke goederen.png'
    WHEN 'D4-rechtdoor' THEN 'images/signs/mandatory_signs/D4 Verplicht rechtdoor gevaarlijke goederen.png'
    WHEN 'D4-rechts' THEN 'images/signs/mandatory_signs/D4 Verplicht rechts afslaan gevaarlijke goederen.png'
    WHEN 'D5' THEN 'images/signs/mandatory_signs/D5 Verplicht rondgaand verkeer.png'
    WHEN 'D7' THEN 'images/signs/mandatory_signs/D7 Verplicht fietspad.png'
    WHEN 'D9a' THEN 'images/signs/mandatory_signs/D9a Deel van de weg voorbehouden voor voetgangers en fietsers.png'
    ELSE image_url
  END,
  image_path = CASE sign_code
    WHEN 'D10' THEN 'images/signs/mandatory_signs/D10 Deel van de weg voorbehouden voor voetgangers en fietsers.png'
    WHEN 'D11' THEN 'images/signs/mandatory_signs/D11 Verplichte weg voor voetgangers.png'
    WHEN 'D13' THEN 'images/signs/mandatory_signs/D13 Verplichte weg voor ruiters.png'
    WHEN 'D1a' THEN 'images/signs/mandatory_signs/D1a Verplichting rechtdoor.png'
    WHEN 'D1b-links' THEN 'images/signs/mandatory_signs/D1b Verplichting links afslaan.png'
    WHEN 'D1b-rechts' THEN 'images/signs/mandatory_signs/D1b Verplichting rechts afslaan.png'
    WHEN 'D1c' THEN 'images/signs/mandatory_signs/D1c Verplichting links aanhouden.png'
    WHEN 'D1d' THEN 'images/signs/mandatory_signs/D1d Verplichting rechts aanhouden.png'
    WHEN 'D1e' THEN 'images/signs/mandatory_signs/D1e Verplicht links afslaan.png'
    WHEN 'D1f' THEN 'images/signs/mandatory_signs/D1f Verplicht rechts afslaan.png'
    WHEN 'D3a' THEN 'images/signs/mandatory_signs/D3a Verplicht één van de pijlen te volgen.png'
    WHEN 'D3b' THEN 'images/signs/mandatory_signs/D3b Verplicht één van de pijlen te volgen.png'
    WHEN 'D4-links' THEN 'images/signs/mandatory_signs/D4 Verplicht links afslaan gevaarlijke goederen.png'
    WHEN 'D4-rechtdoor' THEN 'images/signs/mandatory_signs/D4 Verplicht rechtdoor gevaarlijke goederen.png'
    WHEN 'D4-rechts' THEN 'images/signs/mandatory_signs/D4 Verplicht rechts afslaan gevaarlijke goederen.png'
    WHEN 'D5' THEN 'images/signs/mandatory_signs/D5 Verplicht rondgaand verkeer.png'
    WHEN 'D7' THEN 'images/signs/mandatory_signs/D7 Verplicht fietspad.png'
    WHEN 'D9a' THEN 'images/signs/mandatory_signs/D9a Deel van de weg voorbehouden voor voetgangers en fietsers.png'
    ELSE image_path
  END,
  normalized_sign_code = LOWER(sign_code),
  updated_at = NOW()
WHERE sign_code IN (
  'D10','D11','D13','D1a','D1b-links','D1b-rechts','D1c','D1d','D1e','D1f',
  'D3a','D3b','D4-links','D4-rechtdoor','D4-rechts','D5','D7','D9a'
);

-- Ensure D5/D7/D9a exist in traffic_signs (historically missing in some datasets)
INSERT INTO traffic_signs (
  category_id, sign_code, normalized_sign_code,
  name_nl, name_en, name_fr, name_ar,
  description_nl, description_en, description_fr, description_ar,
  image_url, image_path, is_active, created_at, updated_at
)
SELECT (SELECT id FROM categories WHERE code = 'D'),
       src.sign_code,
       LOWER(src.sign_code),
       src.name_nl,
       src.name_en,
       src.name_fr,
       src.name_ar,
       src.description_nl,
       src.description_en,
       src.description_fr,
       src.description_ar,
       src.image_path,
       src.image_path,
       1,
       NOW(),
       NOW()
FROM (
  SELECT 'D5' AS sign_code,
         'Verplicht rondgaand verkeer' AS name_nl,
         'Mandatory roundabout' AS name_en,
         'Circulation giratoire obligatoire' AS name_fr,
         'دوران إلزامي' AS name_ar,
         'Verplicht rondgaand verkeer.' AS description_nl,
         'Mandatory roundabout.' AS description_en,
         'Circulation giratoire obligatoire.' AS description_fr,
         'دوران إلزامي.' AS description_ar,
         'images/signs/mandatory_signs/D5 Verplicht rondgaand verkeer.png' AS image_path
  UNION ALL
  SELECT 'D7',
         'Verplicht fietspad',
         'Mandatory cycle path',
         'Piste cyclable obligatoire',
         'مسار دراجات إلزامي',
         'Verplicht fietspad.',
         'Mandatory cycle path.',
         'Piste cyclable obligatoire.',
         'مسار دراجات إلزامي.',
         'images/signs/mandatory_signs/D7 Verplicht fietspad.png'
  UNION ALL
  SELECT 'D9a',
         'Deel van de weg voorbehouden voor voetgangers en fietsers',
         'Part of the road reserved for pedestrians and cyclists',
         'Partie de la voie réservée aux piétons et aux cyclistes',
         'جزء من الطريق مخصص للمشاة والدراجين',
         'Deel van de weg voorbehouden voor voetgangers en fietsers.',
         'Part of the road reserved for pedestrians and cyclists.',
         'Partie de la voie réservée aux piétons et aux cyclistes.',
         'جزء من الطريق مخصص للمشاة والدراجين.',
         'images/signs/mandatory_signs/D9a Deel van de weg voorbehouden voor voetgangers en fietsers.png'
) src
WHERE NOT EXISTS (
  SELECT 1 FROM traffic_signs t WHERE t.sign_code = src.sign_code
);

-- -----------------------------------------------------------------------------
-- 2) road_signs: same disk allowlist cleanup as traffic_signs
-- -----------------------------------------------------------------------------
DELETE FROM road_signs
WHERE sign_code LIKE 'D%'
  AND sign_code NOT IN (
    'D10','D11','D13','D1a','D1b-links','D1b-rechts','D1c','D1d','D1e','D1f',
    'D3a','D3b','D4-links','D4-rechtdoor','D4-rechts','D5','D7','D9a'
  );

UPDATE road_signs
SET category = 'MANDATORY',
    serious_violation = 0,
    name_nl = CASE sign_code
      WHEN 'D10' THEN 'Deel van de weg voorbehouden voor voetgangers en fietsers'
      WHEN 'D11' THEN 'Verplichte weg voor voetgangers'
      WHEN 'D13' THEN 'Verplichte weg voor ruiters'
      WHEN 'D1a' THEN 'Verplichting rechtdoor'
      WHEN 'D1b-links' THEN 'Verplichting links afslaan'
      WHEN 'D1b-rechts' THEN 'Verplichting rechts afslaan'
      WHEN 'D1c' THEN 'Verplichting links aanhouden'
      WHEN 'D1d' THEN 'Verplichting rechts aanhouden'
      WHEN 'D1e' THEN 'Verplicht links afslaan'
      WHEN 'D1f' THEN 'Verplicht rechts afslaan'
      WHEN 'D3a' THEN 'Verplicht één van de pijlen te volgen'
      WHEN 'D3b' THEN 'Verplicht één van de pijlen te volgen'
      WHEN 'D4-links' THEN 'Verplicht links afslaan gevaarlijke goederen'
      WHEN 'D4-rechtdoor' THEN 'Verplicht rechtdoor gevaarlijke goederen'
      WHEN 'D4-rechts' THEN 'Verplicht rechts afslaan gevaarlijke goederen'
      WHEN 'D5' THEN 'Verplicht rondgaand verkeer'
      WHEN 'D7' THEN 'Verplicht fietspad'
      WHEN 'D9a' THEN 'Deel van de weg voorbehouden voor voetgangers en fietsers'
      ELSE name_nl
    END,
    image_path = CASE sign_code
      WHEN 'D10' THEN 'images/signs/mandatory_signs/D10 Deel van de weg voorbehouden voor voetgangers en fietsers.png'
      WHEN 'D11' THEN 'images/signs/mandatory_signs/D11 Verplichte weg voor voetgangers.png'
      WHEN 'D13' THEN 'images/signs/mandatory_signs/D13 Verplichte weg voor ruiters.png'
      WHEN 'D1a' THEN 'images/signs/mandatory_signs/D1a Verplichting rechtdoor.png'
      WHEN 'D1b-links' THEN 'images/signs/mandatory_signs/D1b Verplichting links afslaan.png'
      WHEN 'D1b-rechts' THEN 'images/signs/mandatory_signs/D1b Verplichting rechts afslaan.png'
      WHEN 'D1c' THEN 'images/signs/mandatory_signs/D1c Verplichting links aanhouden.png'
      WHEN 'D1d' THEN 'images/signs/mandatory_signs/D1d Verplichting rechts aanhouden.png'
      WHEN 'D1e' THEN 'images/signs/mandatory_signs/D1e Verplicht links afslaan.png'
      WHEN 'D1f' THEN 'images/signs/mandatory_signs/D1f Verplicht rechts afslaan.png'
      WHEN 'D3a' THEN 'images/signs/mandatory_signs/D3a Verplicht één van de pijlen te volgen.png'
      WHEN 'D3b' THEN 'images/signs/mandatory_signs/D3b Verplicht één van de pijlen te volgen.png'
      WHEN 'D4-links' THEN 'images/signs/mandatory_signs/D4 Verplicht links afslaan gevaarlijke goederen.png'
      WHEN 'D4-rechtdoor' THEN 'images/signs/mandatory_signs/D4 Verplicht rechtdoor gevaarlijke goederen.png'
      WHEN 'D4-rechts' THEN 'images/signs/mandatory_signs/D4 Verplicht rechts afslaan gevaarlijke goederen.png'
      WHEN 'D5' THEN 'images/signs/mandatory_signs/D5 Verplicht rondgaand verkeer.png'
      WHEN 'D7' THEN 'images/signs/mandatory_signs/D7 Verplicht fietspad.png'
      WHEN 'D9a' THEN 'images/signs/mandatory_signs/D9a Deel van de weg voorbehouden voor voetgangers en fietsers.png'
      ELSE image_path
    END,
    normalized_sign_code = LOWER(sign_code),
    updated_at = NOW()
WHERE sign_code IN (
  'D10','D11','D13','D1a','D1b-links','D1b-rechts','D1c','D1d','D1e','D1f',
  'D3a','D3b','D4-links','D4-rechtdoor','D4-rechts','D5','D7','D9a'
);

-- Backfill missing road_signs rows for canonical D allowlist from traffic_signs
INSERT INTO road_signs (
  sign_code, normalized_sign_code, category,
  image_path, serious_violation,
  name_nl, name_en, name_fr, name_ar,
  description_nl, description_en, description_fr, description_ar,
  is_active
)
SELECT ts.sign_code,
       LOWER(ts.sign_code),
       'MANDATORY',
       COALESCE(NULLIF(ts.image_path, ''), COALESCE(ts.image_url, '')),
       0,
       ts.name_nl,
       ts.name_en,
       ts.name_fr,
       ts.name_ar,
       ts.description_nl,
       ts.description_en,
       ts.description_fr,
       ts.description_ar,
       ts.is_active
FROM traffic_signs ts
WHERE ts.sign_code IN (
  'D10','D11','D13','D1a','D1b-links','D1b-rechts','D1c','D1d','D1e','D1f',
  'D3a','D3b','D4-links','D4-rechtdoor','D4-rechts','D5','D7','D9a'
)
AND NOT EXISTS (
  SELECT 1
  FROM road_signs rs
  WHERE rs.sign_code = ts.sign_code
);
