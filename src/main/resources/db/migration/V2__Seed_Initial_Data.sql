-- ====================================================================
-- Phase 0: Foundation Seed Data
-- ====================================================================

USE readyroad_prod;

-- ====================
-- STEP 1: Pre-check
-- ====================
SELECT COUNT(*) AS existing_categories FROM categories;
SELECT COUNT(*) AS existing_signs      FROM traffic_signs;

-- ====================
-- STEP 2: Insert Categories (idempotent)
-- ====================
INSERT INTO categories (
    code,
    name_ar, name_en, name_nl, name_fr,
    description_ar, description_en, description_nl, description_fr,
    display_order, is_active, created_at, updated_at
) VALUES
('A', 'إشارات التحذير',       'Warning Signs',     'Waarschuwingsborden', 'Signaux d''avertissement',
 'إشارات تحذيرية للسائقين',   'Warning signs for drivers',
 'Waarschuwingsborden voor bestuurders', 'Panneaux d''avertissement pour les conducteurs',
 1, TRUE, NOW(), NOW()),

('B', 'إشارات الأولوية',      'Priority Signs',    'Voorrangsborden',     'Signaux de priorité',
 'إشارات الأولوية في الطريق', 'Priority road signs',
 'Voorrangsborden op de weg',            'Panneaux de priorité sur la route',
 2, TRUE, NOW(), NOW()),

('C', 'إشارات المنع',         'Prohibition Signs', 'Verbodsborden',       'Signaux d''interdiction',
 'إشارات المنع والتقييد',     'Prohibition and restriction signs',
 'Verbods- en beperkingsborden',         'Panneaux d''interdiction et de restriction',
 3, TRUE, NOW(), NOW()),

('D', 'إشارات الإلزام',       'Mandatory Signs',   'Gebodsborden',        'Signaux d''obligation',
 'إشارات إلزامية للسائقين',   'Mandatory signs for drivers',
 'Verplichte borden voor bestuurders',   'Panneaux obligatoires pour les conducteurs',
 4, TRUE, NOW(), NOW()),

('E', 'إشارات الوقوف والانتظار', 'Parking Signs',  'Parkeren en stilstaan', 'Signaux de stationnement',
 'إشارات الوقوف والانتظار',   'Parking and stopping signs',
 'Parkeer- en stopborden',               'Panneaux de stationnement et d''arrêt',
 5, TRUE, NOW(), NOW()),

('F', 'إشارات الإرشاد',       'Direction Signs',   'Richtingsborden',     'Signaux de direction',
 'إشارات إرشادية للطرق',      'Direction and guidance signs',
 'Richting- en geleidingsborden',        'Panneaux de direction et d''orientation',
 6, TRUE, NOW(), NOW()),

('G', 'إشارات إضافية',        'Additional Signs',  'Onderborden',         'Signaux additionnels',
 'إشارات إضافية توضيحية',     'Additional information signs',
 'Aanvullende informatieborden',         'Panneaux d''information supplémentaires',
 7, TRUE, NOW(), NOW()),

('Z', 'إشارات المناطق',       'Zone Signs',        'Zoneborden',          'Signaux de zone',
 'إشارات المناطق الخاصة',     'Special zone signs',
 'Speciale zoneborden',                  'Panneaux de zone spéciaux',
 8, TRUE, NOW(), NOW()),

('M', 'علامات الطريق',        'Road Markings',     'Wegmarkeringen',      'Marquages routiers',
 'علامات الطريق الأرضية',     'Road surface markings',
 'Wegmarkeringen op het wegdek',         'Marquages au sol',
 9, TRUE, NOW(), NOW())

-- ✅ Idempotent: skip if code already exists
ON DUPLICATE KEY UPDATE
    display_order = VALUES(display_order),
    updated_at    = NOW();

SELECT '✅ Step 2: Categories inserted/updated' AS status;

-- ====================
-- STEP 3: Insert Sample Traffic Signs
-- ====================
-- ✅ Using subquery for category_id instead of hardcoded numbers
INSERT INTO traffic_signs (
    category_id,
    sign_code,
    name_ar, name_en, name_nl, name_fr,
    description_ar, description_en, description_nl, description_fr,
    image_url, is_active, created_at, updated_at
) VALUES

-- Category A: Warning Signs
((SELECT id FROM categories WHERE code = 'A'), 'A1',
 'منعطف خطير لليمين', 'Dangerous bend to the right', 'Gevaarlijke bocht naar rechts', 'Virage dangereux à droite',
 'يحذر من وجود منعطف خطير لليمين', 'Warns of a dangerous right bend',
 'Waarschuwt voor een gevaarlijke bocht naar rechts', 'Avertit d''un virage dangereux à droite',
 NULL, TRUE, NOW(), NOW()),

((SELECT id FROM categories WHERE code = 'A'), 'A2',
 'منعطف خطير لليسار', 'Dangerous bend to the left', 'Gevaarlijke bocht naar links', 'Virage dangereux à gauche',
 'يحذر من وجود منعطف خطير لليسار', 'Warns of a dangerous left bend',
 'Waarschuwt voor een gevaarlijke bocht naar links', 'Avertit d''un virage dangereux à gauche',
 NULL, TRUE, NOW(), NOW()),

-- Category B: Priority Signs
((SELECT id FROM categories WHERE code = 'B'), 'B1',
 'طريق ذو أولوية', 'Priority road', 'Voorrangsweg', 'Route prioritaire',
 'يشير إلى أن الطريق له أولوية المرور', 'Indicates priority road',
 'Geeft aan dat de weg voorrang heeft', 'Indique une route prioritaire',
 NULL, TRUE, NOW(), NOW()),

((SELECT id FROM categories WHERE code = 'B'), 'B2',
 'أعط الأولوية', 'Give way', 'Voorrang verlenen', 'Céder le passage',
 'يجب إعطاء الأولوية للمركبات الأخرى', 'Must give way to other vehicles',
 'Moet voorrang verlenen aan ander verkeer', 'Doit céder le passage aux autres véhicules',
 NULL, TRUE, NOW(), NOW()),

-- Category C: Prohibition Signs
((SELECT id FROM categories WHERE code = 'C'), 'C1',
 'ممنوع الدخول', 'No entry', 'Verboden toegang', 'Accès interdit',
 'ممنوع دخول المركبات', 'No entry for vehicles',
 'Geen toegang voor voertuigen', 'Accès interdit aux véhicules',
 NULL, TRUE, NOW(), NOW()),

((SELECT id FROM categories WHERE code = 'C'), 'C2',
 'ممنوع الانعطاف لليسار', 'No left turn', 'Verboden links af te slaan', 'Interdiction de tourner à gauche',
 'ممنوع الانعطاف لليسار', 'No left turn allowed',
 'Verboden om naar links af te slaan', 'Interdiction de tourner à gauche',
 NULL, TRUE, NOW(), NOW()),

-- Category D: Mandatory Signs
((SELECT id FROM categories WHERE code = 'D'), 'D1',
 'الاتجاه الإلزامي لليمين', 'Compulsory direction right', 'Verplichte rijrichting rechts', 'Direction obligatoire à droite',
 'يجب الاتجاه لليمين', 'Must go right',
 'Verplicht naar rechts', 'Obligation de tourner à droite',
 NULL, TRUE, NOW(), NOW()),

((SELECT id FROM categories WHERE code = 'D'), 'D2',
 'الاتجاه الإلزامي لليسار', 'Compulsory direction left', 'Verplichte rijrichting links', 'Direction obligatoire à gauche',
 'يجب الاتجاه لليسار', 'Must go left',
 'Verplicht naar links', 'Obligation de tourner à gauche',
 NULL, TRUE, NOW(), NOW()),

-- Category E: Parking Signs ✅ Fixed descriptions to match Belgian traffic law
((SELECT id FROM categories WHERE code = 'E'), 'E1',
 'ممنوع الوقوف', 'No parking', 'Parkeerverbod', 'Interdiction de stationnement',
 'ممنوع الوقوف لكن يسمح بالتوقف المؤقت', 'Parking prohibited, temporary stopping allowed',
 'Parkeren verboden, tijdelijk stilstaan toegestaan', 'Stationnement interdit, arrêt temporaire autorisé',
 NULL, TRUE, NOW(), NOW()),

((SELECT id FROM categories WHERE code = 'E'), 'E3',  -- ✅ Fixed: E2 doesn't exist in Belgian law, E3 is correct
 'ممنوع التوقف والوقوف', 'No stopping and parking', 'Stilstaan en parkeren verboden', 'Arrêt et stationnement interdits',
 'ممنوع التوقف والوقوف تماماً', 'Stopping and parking completely prohibited',
 'Stilstaan en parkeren volledig verboden', 'Arrêt et stationnement totalement interdits',
 NULL, TRUE, NOW(), NOW()),

-- Category F: Direction Signs
((SELECT id FROM categories WHERE code = 'F'), 'F1',
 'اتجاه الطريق السريع', 'Highway direction', 'Richting snelweg', 'Direction autoroute',
 'يشير إلى اتجاه الطريق السريع', 'Indicates highway direction',
 'Geeft richting naar de snelweg aan', 'Indique la direction de l''autoroute',
 NULL, TRUE, NOW(), NOW()),

((SELECT id FROM categories WHERE code = 'F'), 'F2',
 'اتجاه المدينة', 'City direction', 'Richting stad', 'Direction ville',
 'يشير إلى اتجاه المدينة', 'Indicates city direction',
 'Geeft richting naar de stad aan', 'Indique la direction de la ville',
 NULL, TRUE, NOW(), NOW()),

-- Category G: Additional Signs
((SELECT id FROM categories WHERE code = 'G'), 'G1',
 'مسافة السريان', 'Distance', 'Afstand', 'Distance',
 'يوضح مسافة سريان الإشارة', 'Indicates distance of sign validity',
 'Geeft afstand van geldigheid aan', 'Indique la distance de validité du panneau',
 NULL, TRUE, NOW(), NOW()),

((SELECT id FROM categories WHERE code = 'G'), 'G2',
 'باستثناء', 'Except', 'Uitgezonderd', 'Excepté',
 'يوضح استثناء من الإشارة الرئيسية', 'Indicates exception to main sign',
 'Geeft uitzondering op hoofdbord aan', 'Indique une exception au panneau principal',
 NULL, TRUE, NOW(), NOW()),

-- Category Z: Zone Signs
((SELECT id FROM categories WHERE code = 'Z'), 'Z1',
 'بداية منطقة السكن', 'Start of residential zone', 'Begin woongebied', 'Début de zone résidentielle',
 'بداية منطقة سكنية بقواعد خاصة', 'Start of residential area with special rules',
 'Begin van woongebied met bijzondere regels', 'Début de zone résidentielle avec règles spéciales',
 NULL, TRUE, NOW(), NOW()),

((SELECT id FROM categories WHERE code = 'Z'), 'Z2',
 'نهاية منطقة السكن', 'End of residential zone', 'Einde woongebied', 'Fin de zone résidentielle',
 'نهاية منطقة السكن', 'End of residential zone',
 'Einde van woongebied', 'Fin de zone résidentielle',
 NULL, TRUE, NOW(), NOW()),

-- Category M: Road Markings
((SELECT id FROM categories WHERE code = 'M'), 'M1',
 'خط أبيض متواصل', 'Continuous white line', 'Doorgetrokken witte streep', 'Ligne blanche continue',
 'خط أبيض متواصل لا يجوز عبوره', 'Continuous white line that cannot be crossed',
 'Doorgetrokken witte streep die niet overschreden mag worden', 'Ligne blanche continue qui ne peut être franchie',
 NULL, TRUE, NOW(), NOW()),

((SELECT id FROM categories WHERE code = 'M'), 'M2',
 'خط أبيض منقطع', 'Broken white line', 'Onderbroken witte streep', 'Ligne blanche discontinue',
 'خط أبيض منقطع يجوز عبوره بحذر', 'Broken white line that can be crossed carefully',
 'Onderbroken witte streep die voorzichtig overschreden mag worden', 'Ligne blanche discontinue qui peut être franchie prudemment',
 NULL, TRUE, NOW(), NOW())

-- ✅ Idempotent: update if sign_code already exists
ON DUPLICATE KEY UPDATE
    name_ar      = VALUES(name_ar),
    name_en      = VALUES(name_en),
    updated_at   = NOW();

SELECT '✅ Step 3: Traffic signs inserted/updated' AS status;

-- ====================
-- STEP 4: Verification
-- ====================
SELECT
    c.code,
    c.name_en                AS category,
    COUNT(ts.id)             AS sign_count
FROM categories c
LEFT JOIN traffic_signs ts ON ts.category_id = c.id
GROUP BY c.id, c.code, c.name_en
ORDER BY c.display_order;
