-- ============================================================
-- V243__Sync_Road_Signs_With_Disk.sql
-- Sync road_signs table with actual image files on disk.
-- This migration is intentionally idempotent because several of
-- these signs may already exist in databases seeded by earlier
-- migrations.
-- ============================================================

-- 1. G SIGNS - delete 9 signs not on disk.
DELETE FROM road_signs
WHERE sign_code IN ('GIa','GIb','GIII','GIII-','GVIIa','GVIIb','GVIId','GVIII','GXI');

-- 2. C SIGNS.
DELETE FROM road_signs
WHERE sign_code IN ('C11a','C11b','C28a','C43','C43_70','C43_90');

UPDATE road_signs
SET image_path = '/images/signs/prohibition_signs/C13 Verboden toegang voor bestuurders van gespannen.png'
WHERE sign_code = 'C13';

INSERT INTO road_signs (sign_code, normalized_sign_code, category, image_path, serious_violation, is_active) VALUES
('C5',    'c5',    'PROHIBITION', '/images/signs/prohibition_signs/C5 Verboden toegang voor bestuurders van motorvoertuigen met meer dan twee wielen.png', 0, 1),
('C5-C7', 'c5_c7', 'PROHIBITION', '/images/signs/prohibition_signs/C5-C7 Verboden toegang voor motorvoertuigen en motorfietsen.png', 0, 1),
('C7',    'c7',    'PROHIBITION', '/images/signs/prohibition_signs/C7 Verboden toegang voor bestuurders van motorfietsen.png', 0, 1),
('C9-C11','c9_c11','PROHIBITION', '/images/signs/prohibition_signs/C9-C11 Verboden toegang voor bestuurders van bromfietsen en fietsen.png', 0, 1) AS new_values
ON DUPLICATE KEY UPDATE
    normalized_sign_code = new_values.normalized_sign_code,
    category = new_values.category,
    image_path = new_values.image_path,
    serious_violation = new_values.serious_violation,
    is_active = new_values.is_active;

-- 3. D SIGNS.
DELETE FROM road_signs WHERE sign_code = 'D9b';

-- 4. E SIGNS.
DELETE FROM road_signs WHERE sign_code IN ('E11', 'E9i');

INSERT INTO road_signs (sign_code, normalized_sign_code, category, image_path, serious_violation, is_active) VALUES
('E9b', 'e9b', 'PARKING', '/images/signs/parking_signs/E9b Parkeren uitsluitend voor autos.png', 0, 1) AS new_values
ON DUPLICATE KEY UPDATE
    normalized_sign_code = new_values.normalized_sign_code,
    category = new_values.category,
    image_path = new_values.image_path,
    serious_violation = new_values.serious_violation,
    is_active = new_values.is_active;

-- 5. F SIGNS.
DELETE FROM road_signs
WHERE sign_code IN ('F103','F105','F117','F118','F45b','F4a','F4b','F87','ZONE-F111','ZONE-F113');

INSERT INTO road_signs (sign_code, normalized_sign_code, category, image_path, serious_violation, is_active) VALUES
('F50bis-fietsers', 'f50bis_fietsers', 'INFORMATION', '/images/signs/information_signs/F50bis Opgepast als je van richting veranderd, fietsers.png', 0, 1) AS new_values
ON DUPLICATE KEY UPDATE
    normalized_sign_code = new_values.normalized_sign_code,
    category = new_values.category,
    image_path = new_values.image_path,
    serious_violation = new_values.serious_violation,
    is_active = new_values.is_active;

-- 6. Z SIGNS.
UPDATE road_signs SET image_path = '/images/signs/zone_signs/ZE9a Zone parkeren uitsluitend voor auto''s.png' WHERE sign_code = 'ZE9a';
UPDATE road_signs SET image_path = '/images/signs/zone_signs/ZE9aT Zone parkeren uitsluitend voor auto''s.png' WHERE sign_code = 'ZE9aT';
UPDATE road_signs SET image_path = '/images/signs/zone_signs/ZE9a-Einde zone parkeren uitsluitend voor auto''s.png' WHERE sign_code = 'ZE9a-Einde';
UPDATE road_signs SET image_path = '/images/signs/zone_signs/ZE9aT-Einde zone parkeren uitsluitend voor auto''s.png' WHERE sign_code = 'ZE9aT-Einde';

UPDATE road_signs SET sign_code = 'ZC21-Einde',  normalized_sign_code = 'zc21_einde'  WHERE sign_code = 'ZC21-';
UPDATE road_signs SET sign_code = 'ZC35-Einde',  normalized_sign_code = 'zc35_einde'  WHERE sign_code = 'ZC35-';
UPDATE road_signs SET sign_code = 'ZC45-Einde',  normalized_sign_code = 'zc45_einde'  WHERE sign_code = 'ZC45';
UPDATE road_signs SET sign_code = 'ZC5-Einde',   normalized_sign_code = 'zc5_einde'   WHERE sign_code = 'ZC5-';
UPDATE road_signs SET sign_code = 'ZE1-Einde',   normalized_sign_code = 'ze1_einde'   WHERE sign_code = 'ZE1-';
UPDATE road_signs SET sign_code = 'ZE9a-Einde',  normalized_sign_code = 'ze9a_einde'  WHERE sign_code = 'ZE9a-';
UPDATE road_signs SET sign_code = 'ZE9aT-Einde', normalized_sign_code = 'ze9at_einde' WHERE sign_code = 'ZE9aT-';

INSERT INTO road_signs (sign_code, normalized_sign_code, category, image_path, serious_violation, is_active) VALUES
('Zone-F103',       'zone_f103',       'ZONE', '/images/signs/zone_signs/Zone-F103 Begin van een voetgangerszone.png', 0, 1),
('Zone-F105-Einde', 'zone_f105_einde', 'ZONE', '/images/signs/zone_signs/Zone-F105-Einde zone van een voetgangerszone.png', 0, 1),
('Zone-F111',       'zone_f111',       'ZONE', '/images/signs/zone_signs/Zone-F111 Zone Fietsstraat.png', 0, 1),
('Zone-F111-Einde', 'zone_f111_einde', 'ZONE', '/images/signs/zone_signs/Zone-F111-Eind zone Fietsstraat.png', 0, 1),
('Zone-F117',       'zone_f117',       'ZONE', '/images/signs/zone_signs/Zone-F117 Begin van lage emissiezone.png', 0, 1),
('Zone-F118-Einde', 'zone_f118_einde', 'ZONE', '/images/signs/zone_signs/Zone-F118-Einde van lage emissiezone.png', 0, 1),
('Zone-F4a',        'zone_f4a',        'ZONE', '/images/signs/zone_signs/Zone-F4a Zone 30 km.png', 0, 1),
('Zone-F4b-Einde',  'zone_f4b_einde',  'ZONE', '/images/signs/zone_signs/Zone-F4b-Einde zone 30 km.png', 0, 1) AS new_values
ON DUPLICATE KEY UPDATE
    normalized_sign_code = new_values.normalized_sign_code,
    category = new_values.category,
    image_path = new_values.image_path,
    serious_violation = new_values.serious_violation,
    is_active = new_values.is_active;

-- Result: 186 road signs, all matching disk files
