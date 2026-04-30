-- V177: Restore variant signs that were deleted or lost in previous migrations
-- Restores: B15b, B15c, D1c, D1d,
--           D1b-rechts, D4-links, D4-rechtdoor
-- Net: +7 signs

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
    'B15b', 'b15a_variant_rechts',
    'Voorrang op de kruisende zijwegen (variant schuine rechts)',
    'Right of way on crossing side roads (right diagonal variant)',
    'Priorité sur les voies transversales (variante diagonale droite)',
    'الأولوية على الطرق الجانبية المتقاطعة (نوع منحرف يمين)',
    'Geeft voorrang op de kruisende zijwegen, de zijweg sluit schuin rechts aan.',
    'Indicates right of way on crossing side roads with a diagonal junction on the right.',
    'Indique la priorité sur les voies transversales, jonction diagonale à droite.',
    'تمنح الأولوية على الطرق الجانبية المتقاطعة، مع وجود تقاطع منحرف على اليمين.',
    'images/signs/priority_signs/B15b Voorrang op het eerstvolgende kruispunt - variant schuine zijweg rechts.png',
    'images/signs/priority_signs/B15b Voorrang op het eerstvolgende kruispunt - variant schuine zijweg rechts.png',
    1, NOW(), NOW()
),
(
    (SELECT id FROM categories WHERE code = 'B'),
    'B15c', 'b15a_versmalling',
    'Voorrang op het eerstvolgende kruispunt.',
    'Priority at the next junction.',
    'Priorité au prochain carrefour.',
    'الأولوية عند التقاطع القادم.',
    'Dit bord geeft aan dat u voorrang heeft op het eerstvolgende kruispunt.',
    'This sign indicates that you have priority at the next junction.',
    'Ce panneau indique que vous avez la priorité au prochain carrefour.',
    'تشير هذه العلامة إلى أن لك الأولوية عند التقاطع القادم.',
    'images/signs/priority_signs/B15c Voorrang op het eerstvolgende kruispunt.png',
    'images/signs/priority_signs/B15c Voorrang op het eerstvolgende kruispunt.png',
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
    'D1c', 'd1c',
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
    'D1d', 'd1d',
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
    'images/signs/mandatory_signs/D4 Verplicht links afslaan gevaarlijke goederen.png',
    'images/signs/mandatory_signs/D4 Verplicht links afslaan gevaarlijke goederen.png',
    1, NOW(), NOW()
),
(
    (SELECT id FROM categories WHERE code = 'D'),
    'D4-rechtdoor', 'd4-rechtdoor',
    'Verplicht rechtdoor voor voertuigen die gevaarlijke goederen vervoeren',
    'Mandatory straight ahead for vehicles carrying dangerous goods',
    'Obligation d''aller tout droit pour les véhicules transportant des marchandises dangereuses',
    'إلزامي الاستمرار للأمام للمركبات التي تنقل بضائع خطرة',
    'Gebod voor voertuigen die gevaarlijke goederen vervoeren om rechtdoor te rijden.',
    'Obligation for vehicles carrying dangerous goods to go straight ahead.',
    'Obligation pour les véhicules transportant des marchandises dangereuses d''aller tout droit.',
    'إلزام المركبات التي تنقل بضائع خطرة بالاستمرار للأمام.',
    'images/signs/mandatory_signs/D4 Verplicht rechtdoor gevaarlijke goederen.png',
    'images/signs/mandatory_signs/D4 Verplicht rechtdoor gevaarlijke goederen.png',
    1, NOW(), NOW()
);

