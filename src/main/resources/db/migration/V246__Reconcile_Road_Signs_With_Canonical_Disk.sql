-- Reconcile road_signs with the disk-backed canonical catalog.
--
-- V243/V245 repaired many image paths, but some legacy rows were renamed
-- away from the current data/signs.json route codes. This migration merges
-- those aliases back into the canonical codes while preserving child rows.

-- End-zone signs: keep the row that may already own questions/exams, then
-- move it back to the canonical dash-suffixed route code.
UPDATE sign_questions q JOIN road_signs src ON src.sign_code = 'ZC21-' JOIN road_signs dst ON dst.sign_code = 'ZC21-Einde' SET q.sign_id = dst.id WHERE q.sign_id = src.id;
UPDATE sign_exams e JOIN road_signs src ON src.sign_code = 'ZC21-' JOIN road_signs dst ON dst.sign_code = 'ZC21-Einde' SET e.sign_id = dst.id WHERE e.sign_id = src.id;
UPDATE quiz_questions q JOIN road_signs src ON src.sign_code = 'ZC21-' JOIN road_signs dst ON dst.sign_code = 'ZC21-Einde' SET q.road_sign_id = dst.id WHERE q.road_sign_id = src.id;
UPDATE sign_exam_results r JOIN road_signs src ON src.sign_code = 'ZC21-' JOIN road_signs dst ON dst.sign_code = 'ZC21-Einde' SET r.sign_id = dst.id WHERE r.sign_id = src.id;
UPDATE sign_practice_sessions ps JOIN road_signs src ON src.sign_code = 'ZC21-' JOIN road_signs dst ON dst.sign_code = 'ZC21-Einde' SET ps.sign_id = dst.id WHERE ps.sign_id = src.id;
UPDATE user_weak_areas uwa JOIN road_signs src ON src.sign_code = 'ZC21-' JOIN road_signs dst ON dst.sign_code = 'ZC21-Einde' SET uwa.road_sign_id = dst.id WHERE uwa.road_sign_id = src.id;
DELETE src FROM road_signs src JOIN road_signs dst ON dst.sign_code = 'ZC21-Einde' WHERE src.sign_code = 'ZC21-';
UPDATE road_signs SET sign_code = 'ZC21-', normalized_sign_code = 'zc21_', category = 'ZONE', image_path = '/images/signs/zone_signs/ZC21-Einde zone verboden toegang voor bestuurders van voertuigen waarvan de massa hoger dan 3500 kg.png', is_active = 1 WHERE sign_code = 'ZC21-Einde';

UPDATE sign_questions q JOIN road_signs src ON src.sign_code = 'ZC35-' JOIN road_signs dst ON dst.sign_code = 'ZC35-Einde' SET q.sign_id = dst.id WHERE q.sign_id = src.id;
UPDATE sign_exams e JOIN road_signs src ON src.sign_code = 'ZC35-' JOIN road_signs dst ON dst.sign_code = 'ZC35-Einde' SET e.sign_id = dst.id WHERE e.sign_id = src.id;
UPDATE quiz_questions q JOIN road_signs src ON src.sign_code = 'ZC35-' JOIN road_signs dst ON dst.sign_code = 'ZC35-Einde' SET q.road_sign_id = dst.id WHERE q.road_sign_id = src.id;
UPDATE sign_exam_results r JOIN road_signs src ON src.sign_code = 'ZC35-' JOIN road_signs dst ON dst.sign_code = 'ZC35-Einde' SET r.sign_id = dst.id WHERE r.sign_id = src.id;
UPDATE sign_practice_sessions ps JOIN road_signs src ON src.sign_code = 'ZC35-' JOIN road_signs dst ON dst.sign_code = 'ZC35-Einde' SET ps.sign_id = dst.id WHERE ps.sign_id = src.id;
UPDATE user_weak_areas uwa JOIN road_signs src ON src.sign_code = 'ZC35-' JOIN road_signs dst ON dst.sign_code = 'ZC35-Einde' SET uwa.road_sign_id = dst.id WHERE uwa.road_sign_id = src.id;
DELETE src FROM road_signs src JOIN road_signs dst ON dst.sign_code = 'ZC35-Einde' WHERE src.sign_code = 'ZC35-';
UPDATE road_signs SET sign_code = 'ZC35-', normalized_sign_code = 'zc35_', category = 'ZONE', image_path = '/images/signs/zone_signs/ZC35-Einde zone verboden inhalen.png', is_active = 1 WHERE sign_code = 'ZC35-Einde';

UPDATE sign_questions q JOIN road_signs src ON src.sign_code = 'ZC5-' JOIN road_signs dst ON dst.sign_code = 'ZC5-Einde' SET q.sign_id = dst.id WHERE q.sign_id = src.id;
UPDATE sign_exams e JOIN road_signs src ON src.sign_code = 'ZC5-' JOIN road_signs dst ON dst.sign_code = 'ZC5-Einde' SET e.sign_id = dst.id WHERE e.sign_id = src.id;
UPDATE quiz_questions q JOIN road_signs src ON src.sign_code = 'ZC5-' JOIN road_signs dst ON dst.sign_code = 'ZC5-Einde' SET q.road_sign_id = dst.id WHERE q.road_sign_id = src.id;
UPDATE sign_exam_results r JOIN road_signs src ON src.sign_code = 'ZC5-' JOIN road_signs dst ON dst.sign_code = 'ZC5-Einde' SET r.sign_id = dst.id WHERE r.sign_id = src.id;
UPDATE sign_practice_sessions ps JOIN road_signs src ON src.sign_code = 'ZC5-' JOIN road_signs dst ON dst.sign_code = 'ZC5-Einde' SET ps.sign_id = dst.id WHERE ps.sign_id = src.id;
UPDATE user_weak_areas uwa JOIN road_signs src ON src.sign_code = 'ZC5-' JOIN road_signs dst ON dst.sign_code = 'ZC5-Einde' SET uwa.road_sign_id = dst.id WHERE uwa.road_sign_id = src.id;
DELETE src FROM road_signs src JOIN road_signs dst ON dst.sign_code = 'ZC5-Einde' WHERE src.sign_code = 'ZC5-';
UPDATE road_signs SET sign_code = 'ZC5-', normalized_sign_code = 'zc5_', category = 'ZONE', image_path = '/images/signs/zone_signs/ZC5-Einde zone verboden toegang voor motorvoertuigen met meer dan 2 wielen.png', is_active = 1 WHERE sign_code = 'ZC5-Einde';

UPDATE sign_questions q JOIN road_signs src ON src.sign_code = 'ZE1-' JOIN road_signs dst ON dst.sign_code = 'ZE1-Einde' SET q.sign_id = dst.id WHERE q.sign_id = src.id;
UPDATE sign_exams e JOIN road_signs src ON src.sign_code = 'ZE1-' JOIN road_signs dst ON dst.sign_code = 'ZE1-Einde' SET e.sign_id = dst.id WHERE e.sign_id = src.id;
UPDATE quiz_questions q JOIN road_signs src ON src.sign_code = 'ZE1-' JOIN road_signs dst ON dst.sign_code = 'ZE1-Einde' SET q.road_sign_id = dst.id WHERE q.road_sign_id = src.id;
UPDATE sign_exam_results r JOIN road_signs src ON src.sign_code = 'ZE1-' JOIN road_signs dst ON dst.sign_code = 'ZE1-Einde' SET r.sign_id = dst.id WHERE r.sign_id = src.id;
UPDATE sign_practice_sessions ps JOIN road_signs src ON src.sign_code = 'ZE1-' JOIN road_signs dst ON dst.sign_code = 'ZE1-Einde' SET ps.sign_id = dst.id WHERE ps.sign_id = src.id;
UPDATE user_weak_areas uwa JOIN road_signs src ON src.sign_code = 'ZE1-' JOIN road_signs dst ON dst.sign_code = 'ZE1-Einde' SET uwa.road_sign_id = dst.id WHERE uwa.road_sign_id = src.id;
DELETE src FROM road_signs src JOIN road_signs dst ON dst.sign_code = 'ZE1-Einde' WHERE src.sign_code = 'ZE1-';
UPDATE road_signs SET sign_code = 'ZE1-', normalized_sign_code = 'ze1_', category = 'ZONE', image_path = '/images/signs/zone_signs/ZE1-Einde zone parkeerverbod.png', is_active = 1 WHERE sign_code = 'ZE1-Einde';

UPDATE sign_questions q JOIN road_signs src ON src.sign_code = 'ZE9a-' JOIN road_signs dst ON dst.sign_code = 'ZE9a-Einde' SET q.sign_id = dst.id WHERE q.sign_id = src.id;
UPDATE sign_exams e JOIN road_signs src ON src.sign_code = 'ZE9a-' JOIN road_signs dst ON dst.sign_code = 'ZE9a-Einde' SET e.sign_id = dst.id WHERE e.sign_id = src.id;
UPDATE quiz_questions q JOIN road_signs src ON src.sign_code = 'ZE9a-' JOIN road_signs dst ON dst.sign_code = 'ZE9a-Einde' SET q.road_sign_id = dst.id WHERE q.road_sign_id = src.id;
UPDATE sign_exam_results r JOIN road_signs src ON src.sign_code = 'ZE9a-' JOIN road_signs dst ON dst.sign_code = 'ZE9a-Einde' SET r.sign_id = dst.id WHERE r.sign_id = src.id;
UPDATE sign_practice_sessions ps JOIN road_signs src ON src.sign_code = 'ZE9a-' JOIN road_signs dst ON dst.sign_code = 'ZE9a-Einde' SET ps.sign_id = dst.id WHERE ps.sign_id = src.id;
UPDATE user_weak_areas uwa JOIN road_signs src ON src.sign_code = 'ZE9a-' JOIN road_signs dst ON dst.sign_code = 'ZE9a-Einde' SET uwa.road_sign_id = dst.id WHERE uwa.road_sign_id = src.id;
DELETE src FROM road_signs src JOIN road_signs dst ON dst.sign_code = 'ZE9a-Einde' WHERE src.sign_code = 'ZE9a-';
UPDATE road_signs SET sign_code = 'ZE9a-', normalized_sign_code = 'ze9a_', category = 'ZONE', image_path = '/images/signs/zone_signs/ZE9a-Einde zone parkeren uitsluitend voor auto''s.png', is_active = 1 WHERE sign_code = 'ZE9a-Einde';

UPDATE sign_questions q JOIN road_signs src ON src.sign_code = 'ZE9aT-' JOIN road_signs dst ON dst.sign_code = 'ZE9aT-Einde' SET q.sign_id = dst.id WHERE q.sign_id = src.id;
UPDATE sign_exams e JOIN road_signs src ON src.sign_code = 'ZE9aT-' JOIN road_signs dst ON dst.sign_code = 'ZE9aT-Einde' SET e.sign_id = dst.id WHERE e.sign_id = src.id;
UPDATE quiz_questions q JOIN road_signs src ON src.sign_code = 'ZE9aT-' JOIN road_signs dst ON dst.sign_code = 'ZE9aT-Einde' SET q.road_sign_id = dst.id WHERE q.road_sign_id = src.id;
UPDATE sign_exam_results r JOIN road_signs src ON src.sign_code = 'ZE9aT-' JOIN road_signs dst ON dst.sign_code = 'ZE9aT-Einde' SET r.sign_id = dst.id WHERE r.sign_id = src.id;
UPDATE sign_practice_sessions ps JOIN road_signs src ON src.sign_code = 'ZE9aT-' JOIN road_signs dst ON dst.sign_code = 'ZE9aT-Einde' SET ps.sign_id = dst.id WHERE ps.sign_id = src.id;
UPDATE user_weak_areas uwa JOIN road_signs src ON src.sign_code = 'ZE9aT-' JOIN road_signs dst ON dst.sign_code = 'ZE9aT-Einde' SET uwa.road_sign_id = dst.id WHERE uwa.road_sign_id = src.id;
DELETE src FROM road_signs src JOIN road_signs dst ON dst.sign_code = 'ZE9aT-Einde' WHERE src.sign_code = 'ZE9aT-';
UPDATE road_signs SET sign_code = 'ZE9aT-', normalized_sign_code = 'ze9at_', category = 'ZONE', image_path = '/images/signs/zone_signs/ZE9aT-Einde zone parkeren uitsluitend voor auto''s.png', is_active = 1 WHERE sign_code = 'ZE9aT-Einde';

-- Zone rows that should use the route codes present in data/signs.json.
UPDATE road_signs SET sign_code = 'F103', normalized_sign_code = 'f103', category = 'INFORMATION', image_path = '/images/signs/zone_signs/Zone-F103 Begin van een voetgangerszone.png', is_active = 1 WHERE sign_code = 'Zone-F103' AND NOT EXISTS (SELECT 1 FROM (SELECT id FROM road_signs WHERE sign_code = 'F103') existing);
DELETE alias FROM road_signs alias JOIN road_signs canonical ON canonical.sign_code = 'F103' WHERE alias.sign_code = 'Zone-F103';

UPDATE road_signs SET sign_code = 'F105', normalized_sign_code = 'f105', category = 'INFORMATION', image_path = '/images/signs/zone_signs/Zone-F105-Einde zone van een voetgangerszone.png', is_active = 1 WHERE sign_code = 'Zone-F105-Einde' AND NOT EXISTS (SELECT 1 FROM (SELECT id FROM road_signs WHERE sign_code = 'F105') existing);
DELETE alias FROM road_signs alias JOIN road_signs canonical ON canonical.sign_code = 'F105' WHERE alias.sign_code = 'Zone-F105-Einde';

UPDATE road_signs SET sign_code = 'F117', normalized_sign_code = 'f117', category = 'INFORMATION', image_path = '/images/signs/zone_signs/Zone-F117 Begin van lage emissiezone.png', is_active = 1 WHERE sign_code = 'Zone-F117' AND NOT EXISTS (SELECT 1 FROM (SELECT id FROM road_signs WHERE sign_code = 'F117') existing);
DELETE alias FROM road_signs alias JOIN road_signs canonical ON canonical.sign_code = 'F117' WHERE alias.sign_code = 'Zone-F117';

UPDATE road_signs SET sign_code = 'F118', normalized_sign_code = 'f118', category = 'INFORMATION', image_path = '/images/signs/zone_signs/Zone-F118-Einde van lage emissiezone.png', is_active = 1 WHERE sign_code = 'Zone-F118-Einde' AND NOT EXISTS (SELECT 1 FROM (SELECT id FROM road_signs WHERE sign_code = 'F118') existing);
DELETE alias FROM road_signs alias JOIN road_signs canonical ON canonical.sign_code = 'F118' WHERE alias.sign_code = 'Zone-F118-Einde';

UPDATE road_signs SET sign_code = 'F4a', normalized_sign_code = 'f4a', category = 'INFORMATION', image_path = '/images/signs/zone_signs/Zone-F4a Zone 30 km.png', is_active = 1 WHERE sign_code = 'Zone-F4a' AND NOT EXISTS (SELECT 1 FROM (SELECT id FROM road_signs WHERE sign_code = 'F4a') existing);
DELETE alias FROM road_signs alias JOIN road_signs canonical ON canonical.sign_code = 'F4a' WHERE alias.sign_code = 'Zone-F4a';

UPDATE road_signs SET sign_code = 'F4b', normalized_sign_code = 'f4b', category = 'INFORMATION', image_path = '/images/signs/zone_signs/Zone-F4b-Einde zone 30 km.png', is_active = 1 WHERE sign_code = 'Zone-F4b-Einde' AND NOT EXISTS (SELECT 1 FROM (SELECT id FROM road_signs WHERE sign_code = 'F4b') existing);
DELETE alias FROM road_signs alias JOIN road_signs canonical ON canonical.sign_code = 'F4b' WHERE alias.sign_code = 'Zone-F4b-Einde';

UPDATE road_signs SET sign_code = 'ZONE-F111', normalized_sign_code = 'zone_f111', category = 'ZONE', image_path = '/images/signs/zone_signs/Zone-F111 Zone Fietsstraat.png', is_active = 1 WHERE sign_code = 'Zone-F111' AND NOT EXISTS (SELECT 1 FROM (SELECT id FROM road_signs WHERE sign_code = 'ZONE-F111') existing);
DELETE alias FROM road_signs alias JOIN road_signs canonical ON canonical.sign_code = 'ZONE-F111' WHERE alias.sign_code = 'Zone-F111';

UPDATE road_signs SET sign_code = 'ZONE-F113', normalized_sign_code = 'zone_f113', category = 'ZONE', image_path = '/images/signs/zone_signs/Zone-F111-Eind zone Fietsstraat.png', is_active = 1 WHERE sign_code = 'Zone-F111-Einde' AND NOT EXISTS (SELECT 1 FROM (SELECT id FROM road_signs WHERE sign_code = 'ZONE-F113') existing);
DELETE alias FROM road_signs alias JOIN road_signs canonical ON canonical.sign_code = 'ZONE-F113' WHERE alias.sign_code = 'Zone-F111-Einde';

-- Ensure the three canonical signs that were skipped by corrupted image paths exist.
INSERT INTO road_signs (sign_code, normalized_sign_code, category, image_path, serious_violation, is_active) VALUES
('A39', 'a39', 'DANGER', '/images/signs/danger_signs/A39 Twee richtingsverkeer toegelaten na een stuk éénrichtingsverkeer.png', 0, 1),
('D3a', 'd3a', 'MANDATORY', '/images/signs/mandatory_signs/D3a Verplicht één van de pijlen te volgen.png', 0, 1),
('D3b', 'd3b', 'MANDATORY', '/images/signs/mandatory_signs/D3b Verplicht één van de pijlen te volgen.png', 0, 1)
ON DUPLICATE KEY UPDATE
    normalized_sign_code = VALUES(normalized_sign_code),
    category = VALUES(category),
    image_path = VALUES(image_path),
    serious_violation = VALUES(serious_violation),
    is_active = VALUES(is_active);
