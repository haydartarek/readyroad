-- ============================================================================
-- V126: Fix E-series (Parkeren en stilstaan) sign image paths and names
-- ============================================================================
-- Problems fixed:
--   1. All E-series signs in traffic_signs used 'images/signs/parkeren/' folder
--      which does not exist on disk. Correct folder is 'images/signs/parking_signs/'.
--   2. E9b had a typo 'imagges/' in V90 seed. Fixed here.
--   3. E9a was set to 'images/signs/parkeren/' by V115. Fixed here.
--   4. Unofficial names fixed: E9a-v10 and E9g-v1 in traffic_signs.
--   5. road_signs updated with corrected image_path values matching sign.json.
--      Note: road_signs uses codes E9a-v4/v5/v6 vs traffic_signs E9a-v6/v7/v10.
-- ============================================================================

-- ============================================================================
-- PART 1: Fix traffic_signs image_url (parkeren/ -> parking_signs/)
-- ============================================================================

UPDATE traffic_signs
SET image_url  = 'images/signs/parking_signs/E1 Parkeerverbod.png',
    updated_at = NOW()
WHERE sign_code = 'E1';

UPDATE traffic_signs
SET image_url  = 'images/signs/parking_signs/E3 Stilstaan en parkeren verboden.png',
    updated_at = NOW()
WHERE sign_code = 'E3';

UPDATE traffic_signs
SET image_url  = 'images/signs/parking_signs/E5 Parkeerverbod van de 1e tot de 15e van de maand.png',
    updated_at = NOW()
WHERE sign_code = 'E5';

UPDATE traffic_signs
SET image_url  = 'images/signs/parking_signs/E7 Parkeerverbod van de 16e tot het einde van de maand.png',
    updated_at = NOW()
WHERE sign_code = 'E7';

-- E9a: fix path set by V115 (still used parkeren/ folder)
UPDATE traffic_signs
SET image_url  = 'images/signs/parking_signs/E9a elektrisch laden Parkeerplaats voorbehouden voor het elektrisch opladen van je wagen.png',
    updated_at = NOW()
WHERE sign_code = 'E9a';

-- E9b: fix both the 'imagges' typo from V90 and the wrong folder
UPDATE traffic_signs
SET image_url  = 'images/signs/parking_signs/E9b Parkeren uitsluitend voor auto''s.png',
    updated_at = NOW()
WHERE sign_code = 'E9b';

UPDATE traffic_signs
SET image_url  = 'images/signs/parking_signs/E9c Parkeren uitsluitend voorvrachtwagens.png',
    updated_at = NOW()
WHERE sign_code = 'E9c';

UPDATE traffic_signs
SET image_url  = 'images/signs/parking_signs/E9d Parkeren uitsluitend voor autocars.png',
    updated_at = NOW()
WHERE sign_code = 'E9d';

UPDATE traffic_signs
SET image_url  = 'images/signs/parking_signs/E9e Verplicht parkeren op de berm of op het trottoir.png',
    updated_at = NOW()
WHERE sign_code = 'E9e';

UPDATE traffic_signs
SET image_url  = 'images/signs/parking_signs/E9f Verplicht parkeren deels op de berm of op het trottoir.png',
    updated_at = NOW()
WHERE sign_code = 'E9f';

UPDATE traffic_signs
SET image_url  = 'images/signs/parking_signs/E9g - Verplicht parkeren op de rijbaan.png',
    updated_at = NOW()
WHERE sign_code = 'E9g';

UPDATE traffic_signs
SET image_url  = 'images/signs/parking_signs/E9h Parkeren uitsluitend voor kampeerauto''s.png',
    updated_at = NOW()
WHERE sign_code = 'E9h';

UPDATE traffic_signs
SET image_url  = 'images/signs/parking_signs/E9i Parkeren uitsluitend voor motorfietsen.png',
    updated_at = NOW()
WHERE sign_code = 'E9i';

UPDATE traffic_signs
SET image_url  = 'images/signs/parking_signs/E11 Halfmaandelijks parkeren in gans de bebouwde kom.png',
    updated_at = NOW()
WHERE sign_code = 'E11';

-- ============================================================================
-- PART 2: Fix unofficial names in traffic_signs
-- ============================================================================

-- E9a-v10: was "E9a - Parkeerzone speciale bestemming" (not an official name)
UPDATE traffic_signs
SET name_nl    = 'Parkeerplaats',
    name_en    = 'Parking place',
    name_fr    = 'Emplacement de stationnement',
    name_ar    = 'مكان لوقوف السيارات',
    updated_at = NOW()
WHERE sign_code = 'E9a-v10';

-- E9g-v1: was "E9g - Verplicht parkeren op de rijbaan variant 2" (unofficial)
UPDATE traffic_signs
SET name_nl    = 'Verplicht parkeren op de rijbaan',
    name_en    = 'Mandatory parking on the roadway',
    name_fr    = 'Stationnement obligatoire sur la chaussée',
    name_ar    = 'وقوف إلزامي على الطريق',
    updated_at = NOW()
WHERE sign_code = 'E9g-v1';

-- ============================================================================
-- PART 3: Fix road_signs image_path (all E-series codes from sign.json)
-- ============================================================================

UPDATE road_signs
SET image_path = 'images/signs/parking_signs/E1 Parkeerverbod.png',
    updated_at = NOW()
WHERE sign_code = 'E1';

UPDATE road_signs
SET image_path = 'images/signs/parking_signs/E3 Stilstaan en parkeren verboden.png',
    updated_at = NOW()
WHERE sign_code = 'E3';

UPDATE road_signs
SET image_path = 'images/signs/parking_signs/E5 Parkeerverbod van de 1e tot de 15e van de maand.png',
    updated_at = NOW()
WHERE sign_code = 'E5';

UPDATE road_signs
SET image_path = 'images/signs/parking_signs/E7 Parkeerverbod van de 16e tot het einde van de maand.png',
    updated_at = NOW()
WHERE sign_code = 'E7';

UPDATE road_signs
SET image_path = 'images/signs/parking_signs/E9a elektrisch laden Parkeerplaats voorbehouden voor het elektrisch opladen van je wagen.png',
    updated_at = NOW()
WHERE sign_code = 'E9a';

-- E9a-v2 (road_signs) = basic "Parkeren toegelaten" sign
UPDATE road_signs
SET image_path = 'images/signs/parking_signs/E9a - Parkeren toegelaten.png',
    updated_at = NOW()
WHERE sign_code = 'E9a-v2';

-- E9a-v3 (road_signs) = electric charging variant
UPDATE road_signs
SET image_path = 'images/signs/parking_signs/E9a Elektrisch opladen.png',
    updated_at = NOW()
WHERE sign_code = 'E9a-v3';

-- E9a-v4 (road_signs) = parking disc / time-limited
UPDATE road_signs
SET image_path = 'images/signs/parking_signs/E9a parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.png',
    updated_at = NOW()
WHERE sign_code = 'E9a-v4';

-- E9a-v5 (road_signs) = disabled persons parking
UPDATE road_signs
SET image_path = 'images/signs/parking_signs/E9a mindervaliden Parkeren enkel toegelaten voor mindervaliden.png',
    updated_at = NOW()
WHERE sign_code = 'E9a-v5';

-- E9a-v6 (road_signs) = general parking (fixes path + name)
UPDATE road_signs
SET image_path = NULL, -- image removed
    name_nl    = 'Parkeerplaats',
    name_en    = 'Parking place',
    name_fr    = 'Emplacement de stationnement',
    name_ar    = 'مكان لوقوف السيارات',
    updated_at = NOW()
WHERE sign_code = 'E9a-v6';

UPDATE road_signs
SET image_path = 'images/signs/parking_signs/E9b Parkeren uitsluitend voor auto''s.png',
    updated_at = NOW()
WHERE sign_code = 'E9b';

UPDATE road_signs
SET image_path = 'images/signs/parking_signs/E9c Parkeren uitsluitend voorvrachtwagens.png',
    updated_at = NOW()
WHERE sign_code = 'E9c';

UPDATE road_signs
SET image_path = 'images/signs/parking_signs/E9d Parkeren uitsluitend voor autocars.png',
    updated_at = NOW()
WHERE sign_code = 'E9d';

UPDATE road_signs
SET image_path = 'images/signs/parking_signs/E9e Verplicht parkeren op de berm of op het trottoir.png',
    updated_at = NOW()
WHERE sign_code = 'E9e';

UPDATE road_signs
SET image_path = 'images/signs/parking_signs/E9f Verplicht parkeren deels op de berm of op het trottoir.png',
    updated_at = NOW()
WHERE sign_code = 'E9f';

UPDATE road_signs
SET image_path = 'images/signs/parking_signs/E9g - Verplicht parkeren op de rijbaan.png',
    updated_at = NOW()
WHERE sign_code = 'E9g';

-- E9g-v1 (road_signs) = fixes path + name
UPDATE road_signs
SET image_path = NULL, -- image removed
    name_nl    = 'Verplicht parkeren op de rijbaan',
    name_en    = 'Mandatory parking on the roadway',
    name_fr    = 'Stationnement obligatoire sur la chaussée',
    name_ar    = 'وقوف إلزامي على الطريق',
    updated_at = NOW()
WHERE sign_code = 'E9g-v1';

UPDATE road_signs
SET image_path = 'images/signs/parking_signs/E9h Parkeren uitsluitend voor kampeerauto''s.png',
    updated_at = NOW()
WHERE sign_code = 'E9h';

UPDATE road_signs
SET image_path = 'images/signs/parking_signs/E9i Parkeren uitsluitend voor motorfietsen.png',
    updated_at = NOW()
WHERE sign_code = 'E9i';

UPDATE road_signs
SET image_path = 'images/signs/parking_signs/E11 Halfmaandelijks parkeren in gans de bebouwde kom.png',
    updated_at = NOW()
WHERE sign_code = 'E11';
