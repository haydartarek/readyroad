-- V157: Re-add E9a (Parkeren toegelaten) sign

-- Insert into traffic_signs
INSERT IGNORE INTO traffic_signs (
    category_id, sign_code, normalized_sign_code,
    name_nl, name_en, name_fr, name_ar,
    description_nl, description_en, description_fr, description_ar,
    image_url, image_path,
    long_description_nl, long_description_en, long_description_fr, long_description_ar,
    is_active, created_at, updated_at
) VALUES (
    5, 'E9a', 'E9a',
    'Parkeren toegelaten',
    'Parking allowed',
    'Stationnement autorisé',
    'مسموح بوقوف السيارات',
    'Parkeren toegelaten.',
    'Parking is allowed here.',
    'Le stationnement est autorisé ici.',
    'يُسمح بوقوف السيارات هنا.',
    NULL, -- image_url removed
    NULL, -- image_path removed
    'Parkeerbord: Parkeren toegelaten. Volg de aangegeven parkeerregels.',
    'Parking sign: follow the indicated parking rules.',
    'Panneau de stationnement : suivez les règles de stationnement indiquées.',
    'علامة وقوف السيارات: اتبع قواعد وقوف السيارات المشار إليها.',
    1, NOW(), NOW()
);

-- Insert into road_signs
INSERT IGNORE INTO road_signs (
    sign_code, normalized_sign_code, category, image_path, serious_violation,
    name_nl, name_en, name_fr, name_ar,
    description_nl, description_en, description_fr, description_ar,
    is_active
) VALUES (
    'E9a', 'E9a', 'PARKING',
    NULL, -- image_path removed
    0,
    'Parkeren toegelaten',
    'Parking allowed',
    'Stationnement autorisé',
    'مسموح بوقوف السيارات',
    'Parkeren toegelaten.',
    'Parking is allowed here.',
    'Le stationnement est autorisé ici.',
    'يُسمح بوقوف السيارات هنا.',
    1
);
