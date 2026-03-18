-- Fix E9a: correct name + image path
UPDATE traffic_signs
SET name_nl        = 'Parkeren toegelaten',
    name_en        = 'Parking allowed',
    name_fr        = 'Stationnement autorisé',
    name_ar        = 'وقوف السيارات مسموح',
    description_nl = 'Parkeren is hier toegestaan.',
    description_en = 'Parking is allowed here.',
    description_fr = 'Le stationnement est autorisé ici.',
    description_ar = 'يُسمح بوقوف السيارات هنا.',
    image_url      = 'images/signs/parking_signs/E9a Parkeren toegelaten.png',
    updated_at     = NOW()
WHERE sign_code = 'E9a';

-- Fix E9g: remove leading slash + correct folder
UPDATE traffic_signs
SET image_url  = 'images/signs/parking_signs/E9g Verplicht parkeren op de rijbaan.png',
    updated_at = NOW()
WHERE sign_code = 'E9g';

-- Add E9a-electric (INSERT IGNORE: skip if already exists)
INSERT IGNORE INTO traffic_signs (category_id, sign_code, name_nl, name_en, name_fr, name_ar, description_nl, description_en, description_fr, description_ar, image_url, is_active, created_at, updated_at)
VALUES (5, 'E9a-electric',
    'Parkeerplaats voor elektrisch laden',
    'Parking reserved for electric charging',
    'Parking réservé à la recharge électrique',
    'موقف مخصص لشحن السيارات الكهربائية',
    'Parkeerplaats voorbehouden voor het elektrisch opladen van voertuigen.',
    'Parking space reserved for electric vehicle charging.',
    'Place de stationnement réservée à la recharge des véhicules électriques.',
    'موقف مخصص لشحن المركبات الكهربائية.',
    'images/signs/parking_signs/E9a elektrisch laden Parkeerplaats voorbehouden voor het elektrisch opladen van je wagen.png',
    1, NOW(), NOW());

-- Add E9a-disabled (INSERT IGNORE: skip if already exists)
INSERT IGNORE INTO traffic_signs (category_id, sign_code, name_nl, name_en, name_fr, name_ar, description_nl, description_en, description_fr, description_ar, image_url, is_active, created_at, updated_at)
VALUES (5, 'E9a-disabled',
    'Parkeren enkel voor mindervaliden',
    'Parking only for disabled persons',
    'Stationnement uniquement pour personnes handicapées',
    'موقف مخصص لذوي الاحتياجات الخاصة',
    'Parkeren is enkel toegelaten voor mindervaliden met een geldige parkeerkaart.',
    'Parking is only allowed for disabled persons with a valid parking card.',
    'Le stationnement est uniquement autorisé pour les personnes handicapées avec une carte de stationnement valide.',
    'يُسمح بالوقوف فقط لذوي الاحتياجات الخاصة الحاملين لبطاقة وقوف سارية.',
    'images/signs/parking_signs/E9a mindervaliden Parkeren enkel toegelaten voor mindervaliden.png',
    1, NOW(), NOW());

-- Add E9a-disc (INSERT IGNORE: skip if already exists)
INSERT IGNORE INTO traffic_signs (category_id, sign_code, name_nl, name_en, name_fr, name_ar, description_nl, description_en, description_fr, description_ar, image_url, is_active, created_at, updated_at)
VALUES (5, 'E9a-disc',
    'Parkeren beperkt in tijd, parkeerschijf verplicht',
    'Time-limited parking, parking disc required',
    'Stationnement limité dans le temps, disque de stationnement obligatoire',
    'وقوف محدود بالوقت، قرص الوقوف إلزامي',
    'Parkeren is beperkt in tijd. Het gebruik van een parkeerschijf is verplicht.',
    'Parking is time-limited. Use of a parking disc is mandatory.',
    'Le stationnement est limité dans le temps. L''utilisation d''un disque de stationnement est obligatoire.',
    'الوقوف محدود بالوقت. استخدام قرص الوقوف إلزامي.',
    'images/signs/parking_signs/E9a parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.png',
    1, NOW(), NOW());

-- Add E9j (INSERT IGNORE: skip if already exists)
INSERT IGNORE INTO traffic_signs (category_id, sign_code, name_nl, name_en, name_fr, name_ar, description_nl, description_en, description_fr, description_ar, image_url, is_active, created_at, updated_at)
VALUES (5, 'E9j',
    'Wisselend parkeren voor fietsers en auto''s',
    'Alternating parking for cyclists and cars',
    'Stationnement alterné pour cyclistes et voitures',
    'ركن متناوب للدراجات والسيارات',
    'Parkeerplaats voorzien voor wisselend parkeren tussen fietsers en auto''s.',
    'Parking space for alternating use by cyclists and cars.',
    'Place de stationnement prévue pour un usage alterné entre cyclistes et voitures.',
    'مكان ركن مخصص للاستخدام المتناوب بين الدراجات والسيارات.',
    'images/signs/parking_signs/E9j wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en autos.png',
    1, NOW(), NOW());
