-- Canonical subject areas for theoretical-exam classification and analytics.
-- Existing sign questions keep their valid BOTH category; only clearly general
-- questions are reassigned below.

INSERT INTO categories (
    code, name_ar, name_en, name_nl, name_fr,
    description_ar, description_en, description_nl, description_fr,
    display_order, is_active, content_scope, created_at, updated_at
) VALUES
    ('TH_PRI', 'الأولوية والتقاطعات', 'Priority and intersections', 'Voorrang en kruispunten', 'Priorité et carrefours', NULL, NULL, NULL, NULL, 101, TRUE, 'THEORETICAL_EXAM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('TH_SPEED', 'السرعة', 'Speed', 'Snelheid', 'Vitesse', NULL, NULL, NULL, NULL, 102, TRUE, 'THEORETICAL_EXAM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('TH_PARK', 'الوقوف والتوقف', 'Parking and stopping', 'Parkeren en stilstaan', 'Stationnement et arrêt', NULL, NULL, NULL, NULL, 103, TRUE, 'THEORETICAL_EXAM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('TH_RULES', 'قواعد السير', 'Traffic rules', 'Verkeersregels', 'Règles de circulation', NULL, NULL, NULL, NULL, 104, TRUE, 'THEORETICAL_EXAM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('TH_POS', 'الموقع على الطريق', 'Road position', 'Plaats op de weg', 'Position sur la chaussée', NULL, NULL, NULL, NULL, 105, TRUE, 'THEORETICAL_EXAM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('TH_OVTK', 'التجاوز', 'Overtaking', 'Inhalen', 'Dépassement', NULL, NULL, NULL, NULL, 106, TRUE, 'THEORETICAL_EXAM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('TH_VRU', 'مستخدمو الطريق الأكثر عرضة للخطر', 'Vulnerable road users', 'Kwetsbare weggebruikers', 'Usagers vulnérables', NULL, NULL, NULL, NULL, 107, TRUE, 'THEORETICAL_EXAM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('TH_BEHAV', 'سلوك السائق', 'Driver behaviour', 'Rijgedrag', 'Comportement du conducteur', NULL, NULL, NULL, NULL, 108, TRUE, 'THEORETICAL_EXAM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('TH_VEH', 'قواعد المركبة', 'Vehicle rules', 'Voertuigregels', 'Règles relatives au véhicule', NULL, NULL, NULL, NULL, 109, TRUE, 'THEORETICAL_EXAM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('TH_SAFE', 'السلامة', 'Safety', 'Veiligheid', 'Sécurité', NULL, NULL, NULL, NULL, 110, TRUE, 'THEORETICAL_EXAM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('TH_SIGNS', 'العلامات المرورية', 'Traffic signs', 'Verkeersborden', 'Panneaux de signalisation', NULL, NULL, NULL, NULL, 111, TRUE, 'THEORETICAL_EXAM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('TH_ECO', 'القيادة البيئية', 'Environment and eco-driving', 'Milieu en ecologisch rijden', 'Environnement et conduite écologique', NULL, NULL, NULL, NULL, 112, TRUE, 'THEORETICAL_EXAM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('TH_MWAY', 'الطريق السيار وطريق السيارات', 'Motorway and express road', 'Autosnelweg en autoweg', 'Autoroute et route pour automobiles', NULL, NULL, NULL, NULL, 113, TRUE, 'THEORETICAL_EXAM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('TH_LEGAL', 'حالات قانونية عملية', 'Practical legal situations', 'Praktische juridische situaties', 'Situations juridiques pratiques', NULL, NULL, NULL, NULL, 114, TRUE, 'THEORETICAL_EXAM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (code) DO UPDATE SET
    name_ar = EXCLUDED.name_ar,
    name_en = EXCLUDED.name_en,
    name_nl = EXCLUDED.name_nl,
    name_fr = EXCLUDED.name_fr,
    display_order = EXCLUDED.display_order,
    is_active = TRUE,
    content_scope = 'THEORETICAL_EXAM',
    updated_at = CURRENT_TIMESTAMP;

UPDATE quiz_questions
SET category_id = (SELECT id FROM categories WHERE code = 'TH_SPEED'),
    updated_at = CURRENT_TIMESTAMP
WHERE question_en IN (
    'What is the speed limit in residential areas?',
    'What is the maximum speed limit inside cities in Belgium?',
    'What is the maximum speed on highways in dry weather?'
);

UPDATE quiz_questions
SET category_id = (SELECT id FROM categories WHERE code = 'TH_RULES'),
    updated_at = CURRENT_TIMESTAMP
WHERE question_en = 'When must you stop at a red light?';
