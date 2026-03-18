-- V172: Reset G Additional Signs (category code='G')
-- Delete all existing G signs and related data, then insert 9 clean signs.
-- Uses image_url (not image_path) so the frontend can resolve images correctly.

-- Step 1: Remove from sign_exam_questions
DELETE FROM sign_exam_questions
WHERE question_id IN (
    SELECT id FROM sign_questions
    WHERE sign_id IN (
        SELECT id FROM traffic_signs
        WHERE category_id = (SELECT id FROM categories WHERE code = 'G')
    )
);

-- Step 2: Remove from sign_choices
DELETE FROM sign_choices
WHERE question_id IN (
    SELECT id FROM sign_questions
    WHERE sign_id IN (
        SELECT id FROM traffic_signs
        WHERE category_id = (SELECT id FROM categories WHERE code = 'G')
    )
);

-- Step 3: Remove from sign_questions
DELETE FROM sign_questions
WHERE sign_id IN (
    SELECT id FROM traffic_signs
    WHERE category_id = (SELECT id FROM categories WHERE code = 'G')
);

-- Step 4: Remove all G traffic signs
DELETE FROM traffic_signs
WHERE category_id = (SELECT id FROM categories WHERE code = 'G');

-- Step 5: Insert 9 clean G additional signs (image_url set directly)
INSERT INTO traffic_signs (
    category_id, sign_code, normalized_sign_code,
    name_nl, name_ar, name_en, name_fr,
    image_url, is_active, created_at, updated_at
)
VALUES
(
    (SELECT id FROM categories WHERE code = 'G'),
    'GIa', 'gia',
    'Aanduiding van een afstand',
    'إشارة مسافة',
    'Distance indication',
    'Indication de distance',
    'images/signs/additional_signs/GIa Aanduiding van een afstand.png',
    1, NOW(), NOW()
),
(
    (SELECT id FROM categories WHERE code = 'G'),
    'GIb', 'gib',
    'Aanduiding van een afstand',
    'إشارة مسافة',
    'Distance indication',
    'Indication de distance',
    'images/signs/additional_signs/GIb Aanduiding van een afstand.png',
    1, NOW(), NOW()
),
(
    (SELECT id FROM categories WHERE code = 'G'),
    'GIII-', 'giii-',
    'Opgepast kans op aquaplaning',
    'احتمال الانزلاق المائي (aquaplaning)',
    'Risk of aquaplaning',
    'Risque d''aquaplanage',
    'images/signs/additional_signs/GIII- Opgepast kans op aquaplaning.png',
    1, NOW(), NOW()
),
(
    (SELECT id FROM categories WHERE code = 'G'),
    'GIII', 'giii',
    'Opgepast kans op ijzel',
    'احتمال الجليد (ijzel)',
    'Risk of black ice',
    'Risque de verglas',
    'images/signs/additional_signs/GIII Opgepast kans op ijzel.png',
    1, NOW(), NOW()
),
(
    (SELECT id FROM categories WHERE code = 'G'),
    'GVIIa', 'gviia',
    'Aanvulling van de verkeersborden voor parkeren',
    'تفاصيل إضافية لعلامات الركن',
    'Parking sign supplement',
    'Complément des panneaux de stationnement',
    'images/signs/additional_signs/GVIIa Aanvulling van de verkeersborden voor parkeren.png',
    1, NOW(), NOW()
),
(
    (SELECT id FROM categories WHERE code = 'G'),
    'GVIIb', 'gviib',
    'Aanvulling van de verkeersborden voor parkeren',
    'تفاصيل إضافية لعلامات الركن',
    'Parking sign supplement',
    'Complément des panneaux de stationnement',
    'images/signs/additional_signs/GVIIb Aanvulling van de verkeersborden voor parkeren.png',
    1, NOW(), NOW()
),
(
    (SELECT id FROM categories WHERE code = 'G'),
    'GVIId', 'gviid',
    'Aanvulling van de verkeersborden voor parkeren',
    'تفاصيل إضافية لعلامات الركن',
    'Parking sign supplement',
    'Complément des panneaux de stationnement',
    'images/signs/additional_signs/GVIId Aanvulling van de verkeersborden voor parkeren.png',
    1, NOW(), NOW()
),
(
    (SELECT id FROM categories WHERE code = 'G'),
    'GVIII', 'gviii',
    'Voorrangs aanduiding',
    'إشارة أولوية',
    'Priority indication',
    'Indication de priorité',
    'images/signs/additional_signs/GVIII Voorrangs aanduiding.png',
    1, NOW(), NOW()
),
(
    (SELECT id FROM categories WHERE code = 'G'),
    'GXI', 'gxi',
    'Afrit',
    'مخرج',
    'Exit',
    'Sortie',
    'images/signs/additional_signs/GXI Afrit.png',
    1, NOW(), NOW()
);
