-- V185: Add missing C43_10 (10 km/h speed limit) sign
-- Image file exists on disk but sign was never seeded into the DB.
-- Follows the same pattern as C43_50 and C43_70 in V177.

INSERT IGNORE INTO traffic_signs
    (category_id, sign_code, normalized_sign_code,
     name_nl, name_en, name_fr, name_ar,
     description_nl, description_en, description_fr, description_ar,
     image_url, image_path, is_active, created_at, updated_at)
VALUES (
    (SELECT id FROM categories WHERE code = 'C'),
    'C43_10', 'c43_10',
    'Verbod te rijden met een grotere snelheid dan 10 km/u',
    'Speed limit 10 km/h',
    'Limitation de vitesse à 10 km/h',
    'حظر السير بسرعة تتجاوز 10 كم/س',
    'Verbod om te rijden met een snelheid hoger dan 10 km/u.',
    'Prohibition to drive at a speed exceeding 10 km/h.',
    'Interdiction de circuler à une vitesse supérieure à 10 km/h.',
    'حظر السير بسرعة تتجاوز 10 كيلومتراً في الساعة.',
    'images/signs/prohibition_signs/C43 Verbod te rijden met een grotere snelheid dan 10 km.png',
    'images/signs/prohibition_signs/C43 Verbod te rijden met een grotere snelheid dan 10 km.png',
    1, NOW(), NOW()
);
