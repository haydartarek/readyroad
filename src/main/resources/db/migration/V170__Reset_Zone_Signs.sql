-- V170: Reset Zone Signs (category_id=8)
-- Delete all existing zone signs and related data, then insert clean set

-- Step 1: Remove from sign_exam_questions
DELETE FROM sign_exam_questions
WHERE question_id IN (
    SELECT id FROM sign_questions
    WHERE sign_id IN (SELECT id FROM traffic_signs WHERE category_id = 8)
);

-- Step 2: Remove sign_choices
DELETE FROM sign_choices
WHERE question_id IN (
    SELECT id FROM sign_questions
    WHERE sign_id IN (SELECT id FROM traffic_signs WHERE category_id = 8)
);

-- Step 3: Remove sign_questions
DELETE FROM sign_questions
WHERE sign_id IN (SELECT id FROM traffic_signs WHERE category_id = 8);

-- Step 4: Remove traffic_signs zone
DELETE FROM traffic_signs WHERE category_id = 8;

-- Step 5: Insert 16 clean zone signs
INSERT INTO traffic_signs (category_id, sign_code, normalized_sign_code, name_nl, name_ar, name_en, name_fr, image_path, is_active, created_at, updated_at)
VALUES
(8, 'ZE1',      'ze1',      'Zone parkeerverbod',                                                                                  'منطقة ممنوع الوقوف',                                            'Zone no parking',                           'Zone interdiction de stationnement',              'images/signs/zone_signs/ZE1 Zone parkeerverbod.png',                                                                                        1, NOW(), NOW()),
(8, 'ZE1-',     'ze1-',     'Einde zone parkeerverbod',                                                                            'نهاية منطقة ممنوع الوقوف',                                      'End zone no parking',                       'Fin zone interdiction de stationnement',          'images/signs/zone_signs/ZE1- Einde zone parkeerverbod.png',                                                                                 1, NOW(), NOW()),
(8, 'ZE9a',     'ze9a',     'Zone parkeren uitsluitend voor auto''s',                                                              'منطقة للركن المخصص للسيارات فقط',                               'Zone parking for cars only',                'Zone stationnement uniquement pour voitures',     'images/signs/zone_signs/ZE9a Zone parkeren uitsluitend voor auto''s.png',                                                                   1, NOW(), NOW()),
(8, 'ZE9a-',    'ze9a-',    'Einde zone parkeren uitsluitend voor auto''s',                                                        'نهاية منطقة الركن المخصص للسيارات فقط',                         'End zone parking for cars only',            'Fin zone stationnement uniquement pour voitures', 'images/signs/zone_signs/ZE9a- Einde zone parkeren uitsluitend voor auto''s.png',                                                             1, NOW(), NOW()),
(8, 'ZE9aT',    'ze9at',    'Zone parkeren uitsluitend voor auto''s (met tijdsaanduiding)',                                        'منطقة للركن المخصص للسيارات فقط (مع إشارة وقت)',                'Zone parking for cars only (with time)',     'Zone stationnement voitures (avec indication)',   'images/signs/zone_signs/ZE9aT Zone parkeren uitsluitend voor auto''s.png',                                                                  1, NOW(), NOW()),
(8, 'ZE9aT-',   'ze9at-',   'Einde zone parkeren uitsluitend voor auto''s (met tijdsaanduiding)',                                  'نهاية منطقة الركن المخصص للسيارات فقط (مع إشارة وقت)',          'End zone parking for cars only (with time)', 'Fin zone stationnement voitures (avec indication)','images/signs/zone_signs/ZE9aT- Einde zone parkeren uitsluitend voor auto''s.png',                                                          1, NOW(), NOW()),
(8, 'ZC5',      'zc5',      'Zone verboden toegang voor motorvoertuigen met meer dan 2 wielen',                                    'منطقة ممنوع دخول المركبات ذات أكثر من عجلتين',                  'Zone no entry for motor vehicles >2 wheels','Zone accès interdit véhicules >2 roues',           'images/signs/zone_signs/ZC5 Zone verboden toegang voor motorvoertuigen met meer dan 2 wielen.png',                                          1, NOW(), NOW()),
(8, 'ZC5-',     'zc5-',     'Einde zone verboden toegang voor motorvoertuigen met meer dan 2 wielen',                             'نهاية منطقة ممنوع دخول المركبات ذات أكثر من عجلتين',            'End zone no entry motor vehicles >2 wheels','Fin zone accès interdit véhicules >2 roues',       'images/signs/zone_signs/ZC5- Einde zone verboden toegang voor motorvoertuigen met meer dan 2 wielen.png',                                   1, NOW(), NOW()),
(8, 'ZC21',     'zc21',     'Zone verboden toegang voor bestuurders van voertuigen waarvan de massa hoger dan 3500 kg',            'منطقة ممنوع دخول المركبات التي تزيد كتلتها عن 3500 كغ',         'Zone no entry vehicles over 3500 kg',       'Zone accès interdit véhicules >3500 kg',           'images/signs/zone_signs/ZC21 Zone verboden toegang voor bestuurders van voertuigen waarvan de massa hoger dan 3500 kg.png',                 1, NOW(), NOW()),
(8, 'ZC21-',    'zc21-',    'Einde zone verboden toegang voor bestuurders van voertuigen waarvan de massa hoger dan 3500 kg',      'نهاية منطقة ممنوع دخول المركبات التي تزيد كتلتها عن 3500 كغ',  'End zone no entry vehicles over 3500 kg',   'Fin zone accès interdit véhicules >3500 kg',       'images/signs/zone_signs/ZC21- Einde zone verboden toegang voor bestuurders van voertuigen waarvan de massa hoger dan 3500 kg.png',          1, NOW(), NOW()),
(8, 'ZC35',     'zc35',     'Zone verboden inhalen',                                                                               'منطقة ممنوع التجاوز',                                           'Zone no overtaking',                        'Zone interdiction de dépasser',                   'images/signs/zone_signs/ZC35 Zone verboden inhalen.png',                                                                                    1, NOW(), NOW()),
(8, 'ZC35-',    'zc35-',    'Einde zone verboden inhalen',                                                                        'نهاية منطقة ممنوع التجاوز',                                     'End zone no overtaking',                    'Fin zone interdiction de dépasser',               'images/signs/zone_signs/ZC35- Einde zone verboden inhalen.png',                                                                             1, NOW(), NOW()),
(8, 'ZC43',     'zc43',     'Zone met een snelheidsbeperking',                                                                    'منطقة تحديد السرعة',                                            'Zone with speed limit',                     'Zone avec limitation de vitesse',                 'images/signs/zone_signs/ZC43 Zone met een snelheidsbeperking.png',                                                                         1, NOW(), NOW()),
(8, 'ZC45',     'zc45',     'Einde zone met een snelheidsbeperking',                                                              'نهاية منطقة تحديد السرعة',                                      'End zone with speed limit',                 'Fin zone avec limitation de vitesse',             'images/signs/zone_signs/ZC45 Einde zone met een snelheidsbeperking.png',                                                                   1, NOW(), NOW()),
(8, 'ZONE-F111','zone-f111','ZONE Fietsstraat',                                                                                    'منطقة شارع الدراجات',                                           'ZONE Bicycle street',                       'ZONE Rue cyclable',                               'images/signs/zone_signs/ZONE F111- ZONE Fietsstraat.png',                                                                                  1, NOW(), NOW()),
(8, 'ZONE-F113','zone-f113','Einde ZONE Fietsstraat',                                                                             'نهاية منطقة شارع الدراجات',                                     'End ZONE Bicycle street',                   'Fin ZONE Rue cyclable',                           'images/signs/zone_signs/ZONE F113- Einde ZONE Fietsstraat.png',                                                                            1, NOW(), NOW());
