-- V153__Fix_E_Parking_Signs_Canonical_Names.sql
-- Enforce 18 canonical E-series parking sign codes across traffic_signs and road_signs.
-- Renames: E9a→E9a-electric, E9a-v6→E9a-disc, E9a-v7→E9a-disabled, E9a-v10→E9a-default
--          road E9a-v4→E9a-disc, E9a-v5→E9a-disabled, E9a-v6→E9a-default
-- Fixes:   E9g image_path (remove dash from filename)
-- Deletes: E9a-v2, E9a-v3, E9g-v1 (non-canonical, reference non-existent image files)
-- Inserts: E9j (wisselend parkeren – new canonical sign)

-- ============================================================
-- STEP 1: Rename sign_codes in traffic_signs (FKs on id, so safe)
-- ============================================================
UPDATE traffic_signs SET sign_code = 'E9a-electric', normalized_sign_code = 'E9a-electric'
  WHERE sign_code = 'E9a';

UPDATE traffic_signs SET sign_code = 'E9a-default', normalized_sign_code = 'E9a-default'
  WHERE sign_code = 'E9a-v10';

UPDATE traffic_signs SET sign_code = 'E9a-disc', normalized_sign_code = 'E9a-disc'
  WHERE sign_code = 'E9a-v6';

UPDATE traffic_signs SET sign_code = 'E9a-disabled', normalized_sign_code = 'E9a-disabled'
  WHERE sign_code = 'E9a-v7';

-- Fix E9g image path (remove erroneous dash)
UPDATE traffic_signs
  SET image_url  = NULL, -- image removed
      image_path = NULL
  WHERE sign_code = 'E9g';

-- ============================================================
-- STEP 2: Rename sign_codes in road_signs (FKs on id, so safe)
-- ============================================================
UPDATE road_signs SET sign_code = 'E9a-electric', normalized_sign_code = 'E9a-electric'
  WHERE sign_code = 'E9a';

UPDATE road_signs SET sign_code = 'E9a-default', normalized_sign_code = 'E9a-default'
  WHERE sign_code = 'E9a-v6';

UPDATE road_signs SET sign_code = 'E9a-disc', normalized_sign_code = 'E9a-disc'
  WHERE sign_code = 'E9a-v4';

UPDATE road_signs SET sign_code = 'E9a-disabled', normalized_sign_code = 'E9a-disabled'
  WHERE sign_code = 'E9a-v5';

-- Fix E9g image path (remove erroneous dash)
UPDATE road_signs
  SET image_path = NULL -- image removed
  WHERE sign_code = 'E9g';

-- ============================================================
-- STEP 3: Delete non-canonical signs
-- quiz_questions → traffic_signs is NO ACTION; others CASCADE.
-- ============================================================

-- Pre-delete quiz_questions for non-canonical traffic_signs
DELETE FROM quiz_questions
  WHERE traffic_sign_id IN (
    SELECT id FROM traffic_signs WHERE sign_code IN ('E9a-v2', 'E9a-v3', 'E9g-v1')
  );

-- Delete non-canonical traffic_signs rows (traffic_sign_details + traffic_sign_rules CASCADE)
DELETE FROM traffic_signs WHERE sign_code IN ('E9a-v2', 'E9a-v3', 'E9g-v1');

-- Delete non-canonical road_signs rows (sign_questions, sign_exams, sign_exam_results,
-- sign_practice_sessions all CASCADE)
DELETE FROM road_signs WHERE sign_code IN ('E9a-v2', 'E9a-v3', 'E9g-v1');

-- ============================================================
-- STEP 4: Insert E9j (new canonical parking sign)
-- ============================================================
INSERT INTO traffic_signs (
  category_id, sign_code, normalized_sign_code,
  name_nl, name_en, name_fr, name_ar,
  description_nl, description_en, description_fr, description_ar,
  long_description_nl, long_description_en, long_description_fr, long_description_ar,
  image_url, image_path, is_active, created_at, updated_at
) VALUES (
  5, 'E9j', 'E9j',
  'Wisselend parkeren voor fietsers en auto''s',
  'Alternating parking for cyclists and cars',
  'Stationnement alternant pour cyclistes et voitures',
  'ركن متناوب للدراجات والسيارات',
  'Parkeerplaats voorzien voor wisselend parkeren fietsers en auto''s.',
  'Parking space for alternating use by cyclists and cars.',
  'Place de stationnement pour usage alterné par les cyclistes et les voitures.',
  'مكان لوقوف السيارات للاستخدام المتناوب من قبل الدراجات والسيارات.',
  'Dit verkeersbord geeft aan dat de parkeerplaats is bedoeld voor wisselend gebruik door fietsers en auto''s.',
  'This traffic sign indicates that the parking space is intended for alternating use by cyclists and cars.',
  'Ce panneau indique que la place de stationnement est destinée à un usage alterné par les cyclistes et les voitures.',
  'تشير هذه الإشارة المرورية إلى أن مكان وقوف السيارات مخصص للاستخدام المتناوب من قبل الدراجات والسيارات.',
  'images/signs/parking_signs/E9j wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en auto''s.png',
  'images/signs/parking_signs/E9j wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en auto''s.png',
  1, NOW(), NOW()
);

INSERT INTO road_signs (
  sign_code, normalized_sign_code, category,
  name_nl, name_en, name_fr, name_ar,
  description_nl, description_en, description_fr, description_ar,
  image_path, serious_violation, is_active
) VALUES (
  'E9j', 'E9j', 'PARKING',
  'Wisselend parkeren voor fietsers en auto''s',
  'Alternating parking for cyclists and cars',
  'Stationnement alternant pour cyclistes et voitures',
  'ركن متناوب للدراجات والسيارات',
  'Parkeerplaats voorzien voor wisselend parkeren fietsers en auto''s.',
  'Parking space for alternating use by cyclists and cars.',
  'Place de stationnement pour usage alterné par les cyclistes et les voitures.',
  'مكان لوقوف السيارات للاستخدام المتناوب من قبل الدراجات والسيارات.',
  'images/signs/parking_signs/E9j wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en auto''s.png',
  0, 1
);
