-- V158__Fix_E_Parking_Signs_Whitelist.sql
--
-- V153 renamed traffic_signs sign_code 'E9a' → 'E9a-electric' but kept the base
-- "Parkeren toegelaten" image on that row (E9a-electric should show the elektrisch-
-- laden image). V155/V156/V157 restored the canonical E9a row, so by the time this
-- migration runs the situation is:
--   • E9a          — Parkeren toegelaten (correct, from V157)
--   • E9a-electric — WRONG image (still points to base E9a image, V153 oversight)
--   • E9a-disc     — correct (V153 renamed + V138 fixed prefix)
--   • E9a-disabled — correct (V153 renamed + V138 fixed prefix)
--   • E9g          — correct (V153 fixed dash in filename)
--   • E9j          — correct (V153 inserted with right path)
--
-- This migration only corrects what V153 left incomplete.

-- ─── Fix E9a-electric in traffic_signs ───────────────────────────────────────

UPDATE traffic_signs
SET   name_nl    = 'Parkeerplaats voorbehouden voor het elektrisch opladen van je wagen',
      name_en    = 'Parking reserved for electric vehicle charging',
      name_fr    = 'Stationnement réservé à la recharge des véhicules électriques',
      name_ar    = 'موقف مخصص لشحن السيارات الكهربائية',
      image_url  = 'images/signs/parking_signs/E9a elektrisch laden Parkeerplaats voorbehouden voor het elektrisch opladen van je wagen.png',
      image_path = 'images/signs/parking_signs/E9a elektrisch laden Parkeerplaats voorbehouden voor het elektrisch opladen van je wagen.png',
      updated_at = NOW()
WHERE sign_code = 'E9a-electric';

-- ─── Fix E9a-electric in road_signs ─────────────────────────────────────────

UPDATE road_signs
SET   name_nl    = 'Parkeerplaats voorbehouden voor het elektrisch opladen van je wagen',
      name_en    = 'Parking reserved for electric vehicle charging',
      name_fr    = 'Stationnement réservé à la recharge des véhicules électriques',
      name_ar    = 'موقف مخصص لشحن السيارات الكهربائية',
      image_path = 'images/signs/parking_signs/E9a elektrisch laden Parkeerplaats voorbehouden voor het elektrisch opladen van je wagen.png',
      updated_at = NOW()
WHERE sign_code = 'E9a-electric';

-- ─── Fix E9b Dutch name (restore apostrophe) ─────────────────────────────────

UPDATE traffic_signs
SET   name_nl    = 'Parkeren uitsluitend voor auto''s',
      updated_at = NOW()
WHERE sign_code = 'E9b';

-- ─── Fix E9h Dutch name (restore apostrophe) ─────────────────────────────────

UPDATE traffic_signs
SET   name_nl    = 'Parkeren uitsluitend voor kampeerauto''s',
      updated_at = NOW()
WHERE sign_code = 'E9h';
