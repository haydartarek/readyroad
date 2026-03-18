-- V168: Add E9g (Mandatory parking on the roadway)
-- This sign was removed in V160 but its image exists on disk.

INSERT IGNORE INTO traffic_signs (
    category_id, sign_code, normalized_sign_code,
    name_ar, name_en, name_nl, name_fr,
    description_ar, description_en, description_nl, description_fr,
    image_url, is_active, created_at, updated_at
) VALUES (
    5, 'E9g', 'E9g',
    'وقوف السيارات الإلزامي على الطريق',
    'Mandatory parking on the roadway',
    'Verplicht parkeren op de rijbaan',
    'Stationnement obligatoire sur la chaussée',
    'يُلزم بوقوف السيارات على الطريق.',
    'Parking on the roadway is mandatory here.',
    'Parkeren op de rijbaan is hier verplicht.',
    'Le stationnement sur la chaussée est obligatoire ici.',
    'images/signs/parking_signs/E9g Verplicht parkeren op de rijbaan.png',
    1, NOW(), NOW()
);
