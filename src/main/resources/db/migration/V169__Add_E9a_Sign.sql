-- V169: Add E9a plain (Parking allowed / Parkeren toegelaten)
-- This sign was removed in V160 but its image exists on disk.
-- The sub-variants (E9a-electric, E9a-disc, E9a-disabled) are separate entries.

INSERT IGNORE INTO traffic_signs (
    category_id, sign_code, normalized_sign_code,
    name_ar, name_en, name_nl, name_fr,
    description_ar, description_en, description_nl, description_fr,
    image_url, is_active, created_at, updated_at
) VALUES (
    5, 'E9a', 'E9a',
    'وقوف السيارات مسموح',
    'Parking allowed',
    'Parkeren toegelaten',
    'Stationnement autorisé',
    'يُسمح بوقوف السيارات هنا.',
    'Parking is allowed here.',
    'Parkeren is hier toegestaan.',
    'Le stationnement est autorisé ici.',
    'images/signs/parking_signs/E9a Parkeren toegelaten.png',
    1, NOW(), NOW()
);
