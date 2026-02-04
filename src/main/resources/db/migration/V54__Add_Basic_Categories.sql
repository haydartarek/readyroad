-- V54__Add_Basic_Categories.sql
-- Add basic categories for traffic signs

INSERT INTO categories (code, name_en, name_nl, name_fr, name_ar, description_en, display_order, is_active, created_at, updated_at)
VALUES
    ('A', 'Danger Signs', 'Gevaarsborden', 'Panneaux de danger', 'علامات الخطر', 'Warning signs for hazards ahead', 1, TRUE, NOW(), NOW()),
    ('B', 'Priority Signs', 'Voorrangsborden', 'Panneaux de priorité', 'علامات الأولوية', 'Signs indicating priority rules', 2, TRUE, NOW(), NOW()),
    ('C', 'Prohibition Signs', 'Verbodsborden', 'Panneaux d''interdiction', 'علامات المنع', 'Signs prohibiting certain actions', 3, TRUE, NOW(), NOW()),
    ('D', 'Mandatory Signs', 'Gebodsborden', 'Panneaux d''obligation', 'علامات الإلزام', 'Signs indicating mandatory actions', 4, TRUE, NOW(), NOW()),
    ('E', 'Parking Signs', 'Parkeerborden', 'Panneaux de stationnement', 'علامات الوقوف', 'Signs related to parking regulations', 5, TRUE, NOW(), NOW()),
    ('F', 'Information Signs', 'Aanwijzingsborden', 'Panneaux d''information', 'علامات المعلومات', 'Informational and guidance signs', 6, TRUE, NOW(), NOW()),
    ('G', 'Additional Signs', 'Onderborden', 'Panneaux additionnels', 'علامات إضافية', 'Supplementary information signs', 7, TRUE, NOW(), NOW()),
    ('Z', 'Zone Signs', 'Zoneborden', 'Panneaux de zone', 'علامات المنطقة', 'Zone entry and exit signs', 8, TRUE, NOW(), NOW()),
    ('M', 'Delineation Signs', 'Afbakeningsborden', 'Panneaux de balisage', 'علامات التحديد', 'Road delineation and guidance markers', 9, TRUE, NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();
