-- V177: Restore variant signs that were deleted or lost in previous migrations
-- Restores: B15A-v1, B15A-v2, C43_50, C43_70, D1a-links, D1a-rechts,
--           D1b-rechts, D4-links, D4-straight
-- Net: +9 signs → brings total from 194 to 203

-- ============================================================
-- B Category: Priority Signs
-- ============================================================

INSERT IGNORE INTO traffic_signs
    (category_id, sign_code, normalized_sign_code,
     name_nl, name_en, name_fr, name_ar,
     description_nl, description_en, description_fr, description_ar,
     image_url, image_path, is_active, created_at, updated_at)
VALUES (
    (SELECT id FROM categories WHERE code = 'B'),
    'B15A-v1', 'b15a-v1',
    'Voorrang op de kruisende zijwegen (variant schuine rechts)',
    'Right of way on crossing side roads (right diagonal variant)',
    'Priorité sur les voies transversales (variante diagonale droite)',
    'الأولوية على الطرق الجانبية المتقاطعة (نوع منحرف يمين)',
    'Geeft voorrang op de kruisende zijwegen, de zijweg sluit schuin rechts aan.',
    'Indicates right of way on crossing side roads with a diagonal junction on the right.',
    'Indique la priorité sur les voies transversales, jonction diagonale à droite.',
    'تمنح الأولوية على الطرق الجانبية المتقاطعة، مع وجود تقاطع منحرف على اليمين.',
    'images/signs/priority_signs/B15A Variant schuine rechts.png',
    'images/signs/priority_signs/B15A Variant schuine rechts.png',
    1, NOW(), NOW()
),
(
    (SELECT id FROM categories WHERE code = 'B'),
    'B15A-v2', 'b15a-v2',
    'Voorrang op kruisende zijwegen (versmalling van rechts)',
    'Right of way on crossing side roads (narrowing from right)',
    'Priorité sur les voies transversales (rétrécissement par la droite)',
    'الأولوية على الطرق الجانبية المتقاطعة (تضييق من اليمين)',
    'Geeft voorrang op de kruisende zijwegen, de rijbaan versmalt aan de rechterzijde.',
    'Indicates right of way on crossing side roads where the lane narrows from the right.',
    'Indique la priorité sur les voies transversales avec rétrécissement par la droite.',
    'تمنح الأولوية على الطرق الجانبية المتقاطعة مع تضييق في الطريق من الجانب الأيمن.',
    'images/signs/priority_signs/B15A Versmalling van rechts.png',
    'images/signs/priority_signs/B15A Versmalling van rechts.png',
    1, NOW(), NOW()
);

-- ============================================================
-- C Category: Prohibition Signs
-- ============================================================

INSERT IGNORE INTO traffic_signs
    (category_id, sign_code, normalized_sign_code,
     name_nl, name_en, name_fr, name_ar,
     description_nl, description_en, description_fr, description_ar,
     image_url, image_path, is_active, created_at, updated_at)
VALUES (
    (SELECT id FROM categories WHERE code = 'C'),
    'C43_50', 'c43_50',
    'Verbod te rijden met een grotere snelheid dan 50 km/u',
    'Speed limit 50 km/h',
    'Limitation de vitesse à 50 km/h',
    'حظر السير بسرعة تتجاوز 50 كم/س',
    'Verbod om te rijden met een snelheid hoger dan 50 km/u.',
    'Prohibition to drive at a speed exceeding 50 km/h.',
    'Interdiction de circuler à une vitesse supérieure à 50 km/h.',
    'حظر السير بسرعة تتجاوز 50 كيلومتراً في الساعة.',
    'images/signs/prohibition_signs/C43 Verbod te rijden met een grotere snelheid dan 50 km.png',
    'images/signs/prohibition_signs/C43 Verbod te rijden met een grotere snelheid dan 50 km.png',
    1, NOW(), NOW()
),
(
    (SELECT id FROM categories WHERE code = 'C'),
    'C43_70', 'c43_70',
    'Verbod te rijden met een grotere snelheid dan 70 km/u',
    'Speed limit 70 km/h',
    'Limitation de vitesse à 70 km/h',
    'حظر السير بسرعة تتجاوز 70 كم/س',
    'Verbod om te rijden met een snelheid hoger dan 70 km/u.',
    'Prohibition to drive at a speed exceeding 70 km/h.',
    'Interdiction de circuler à une vitesse supérieure à 70 km/h.',
    'حظر السير بسرعة تتجاوز 70 كيلومتراً في الساعة.',
    'images/signs/prohibition_signs/C43 Verbod te rijden met een grotere snelheid dan 70 km.png',
    'images/signs/prohibition_signs/C43 Verbod te rijden met een grotere snelheid dan 70 km.png',
    1, NOW(), NOW()
);

-- ============================================================
-- D Category: Mandatory Signs
-- ============================================================

INSERT IGNORE INTO traffic_signs
    (category_id, sign_code, normalized_sign_code,
     name_nl, name_en, name_fr, name_ar,
     description_nl, description_en, description_fr, description_ar,
     image_url, image_path, is_active, created_at, updated_at)
VALUES (
    (SELECT id FROM categories WHERE code = 'D'),
    'D1a-links', 'd1a-links',
    'Verplichting links aanhouden',
    'Obligation to keep left',
    'Obligation de tenir la gauche',
    'إلزامية السير على اليسار',
    'Gebod om links te rijden op de rijbaan.',
    'Obligation to keep to the left side of the road.',
    'Obligation de se tenir sur la gauche de la chaussée.',
    'إلزام بالسير في الجانب الأيسر من الطريق.',
    'images/signs/mandatory_signs/D1c Verplichting links aanhouden.png',
    'images/signs/mandatory_signs/D1c Verplichting links aanhouden.png',
    1, NOW(), NOW()
),
(
    (SELECT id FROM categories WHERE code = 'D'),
    'D1a-rechts', 'd1a-rechts',
    'Verplichting rechts aanhouden',
    'Obligation to keep right',
    'Obligation de tenir la droite',
    'إلزامية السير على اليمين',
    'Gebod om rechts te rijden op de rijbaan.',
    'Obligation to keep to the right side of the road.',
    'Obligation de se tenir sur la droite de la chaussée.',
    'إلزام بالسير في الجانب الأيمن من الطريق.',
    'images/signs/mandatory_signs/D1d Verplichting rechts aanhouden.png',
    'images/signs/mandatory_signs/D1d Verplichting rechts aanhouden.png',
    1, NOW(), NOW()
),
(
    (SELECT id FROM categories WHERE code = 'D'),
    'D1b-rechts', 'd1b-rechts',
    'Verplichting rechts afslaan',
    'Mandatory right turn',
    'Obligation de tourner à droite',
    'إلزامية الانعطاف يميناً',
    'Verplichting de door de pijl aangeduide richting (rechts) te volgen.',
    'Obligation to follow the direction indicated by the arrow (right).',
    'Obligation de suivre la direction indiquée par la flèche (droite).',
    'وجوب اتباع الاتجاه المحدد بالسهم (يمين).',
    'images/signs/mandatory_signs/D1b Verplichting rechts afslaan.png',
    'images/signs/mandatory_signs/D1b Verplichting rechts afslaan.png',
    1, NOW(), NOW()
),
(
    (SELECT id FROM categories WHERE code = 'D'),
    'D4-links', 'd4-links',
    'Verplicht linksaf voor voertuigen die gevaarlijke goederen vervoeren',
    'Mandatory left turn for vehicles carrying dangerous goods',
    'Obligation de tourner à gauche pour les véhicules transportant des marchandises dangereuses',
    'إلزامي الانعطاف يساراً للمركبات التي تنقل بضائع خطرة',
    'Gebod voor voertuigen die gevaarlijke goederen vervoeren om linksaf te slaan.',
    'Obligation for vehicles carrying dangerous goods to turn left.',
    'Obligation pour les véhicules transportant des marchandises dangereuses de tourner à gauche.',
    'إلزام المركبات التي تنقل بضائع خطرة بالانعطاف يساراً.',
    'images/signs/mandatory_signs/D4 Verplicht linksaf voor voertuigen die gevaarlijke goederen vervoeren.png',
    'images/signs/mandatory_signs/D4 Verplicht linksaf voor voertuigen die gevaarlijke goederen vervoeren.png',
    1, NOW(), NOW()
),
(
    (SELECT id FROM categories WHERE code = 'D'),
    'D4-straight', 'd4-straight',
    'Verplicht rechtdoor voor voertuigen die gevaarlijke goederen vervoeren',
    'Mandatory straight ahead for vehicles carrying dangerous goods',
    'Obligation d''aller tout droit pour les véhicules transportant des marchandises dangereuses',
    'إلزامي الاستمرار للأمام للمركبات التي تنقل بضائع خطرة',
    'Gebod voor voertuigen die gevaarlijke goederen vervoeren om rechtdoor te rijden.',
    'Obligation for vehicles carrying dangerous goods to go straight ahead.',
    'Obligation pour les véhicules transportant des marchandises dangereuses d''aller tout droit.',
    'إلزام المركبات التي تنقل بضائع خطرة بالاستمرار للأمام.',
    'images/signs/mandatory_signs/D4 Verplicht rechtdoor voor voertuigen die gevaarlijke goederen vervoeren.png',
    'images/signs/mandatory_signs/D4 Verplicht rechtdoor voor voertuigen die gevaarlijke goederen vervoeren.png',
    1, NOW(), NOW()
);
