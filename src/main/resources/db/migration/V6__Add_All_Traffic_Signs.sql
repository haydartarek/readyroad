-- V6__Add_All_Traffic_Signs.sql
-- إضافة جميع العلامات المرورية البلجيكية
-- Belgian Traffic Signs - Complete Database
-- Generated: 2026-01-14 15:02:22
-- Total Signs: 202

-- ========================================
-- إدراج فئات العلامات المرورية
-- Traffic Signs Categories
-- ========================================

-- Update existing categories or insert if not exists
INSERT INTO categories (code, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, display_order, is_active, created_at, updated_at)
VALUES ('A', 'علامات الخطر', 'Danger Signs', 'Gevaar', 'Danger', 
        'علامات تحذيرية للإشارة إلى المخاطر على الطريق', 'Warning signs indicating road hazards', 'Waarschuwingsborden voor gevaren op de weg', 'Panneaux d''avertissement des dangers sur la route', 1, TRUE, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
    name_ar = VALUES(name_ar), name_en = VALUES(name_en), name_nl = VALUES(name_nl), name_fr = VALUES(name_fr),
    description_ar = VALUES(description_ar), description_en = VALUES(description_en), description_nl = VALUES(description_nl), description_fr = VALUES(description_fr);

INSERT INTO categories (code, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, display_order, is_active, created_at, updated_at)
VALUES ('B', 'علامات الأولوية', 'Priority Signs', 'Voorrang', 'Priorité', 
        'علامات تحدد حق الأولوية على الطريق', 'Signs determining priority on the road', 'Borden die voorrang op de weg bepalen', 'Panneaux déterminant la priorité sur la route', 2, TRUE, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
    name_ar = VALUES(name_ar), name_en = VALUES(name_en), name_nl = VALUES(name_nl), name_fr = VALUES(name_fr),
    description_ar = VALUES(description_ar), description_en = VALUES(description_en), description_nl = VALUES(description_nl), description_fr = VALUES(description_fr);

INSERT INTO categories (code, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, display_order, is_active, created_at, updated_at)
VALUES ('C', 'علامات المنع', 'Prohibition Signs', 'Verbod', 'Interdiction', 
        'علامات تمنع أو تحظر إجراءات معينة', 'Signs prohibiting certain actions', 'Borden die bepaalde handelingen verbieden', 'Panneaux interdisant certaines actions', 3, TRUE, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
    name_ar = VALUES(name_ar), name_en = VALUES(name_en), name_nl = VALUES(name_nl), name_fr = VALUES(name_fr),
    description_ar = VALUES(description_ar), description_en = VALUES(description_en), description_nl = VALUES(description_nl), description_fr = VALUES(description_fr);

INSERT INTO categories (code, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, display_order, is_active, created_at, updated_at)
VALUES ('D', 'علامات الإلزام', 'Mandatory Signs', 'Gebod', 'Obligation', 
        'علامات تفرض سلوكاً معيناً', 'Signs imposing specific behavior', 'Borden die specifiek gedrag opleggen', 'Panneaux imposant un comportement spécifique', 4, TRUE, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
    name_ar = VALUES(name_ar), name_en = VALUES(name_en), name_nl = VALUES(name_nl), name_fr = VALUES(name_fr),
    description_ar = VALUES(description_ar), description_en = VALUES(description_en), description_nl = VALUES(description_nl), description_fr = VALUES(description_fr);

INSERT INTO categories (code, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, display_order, is_active, created_at, updated_at)
VALUES ('E', 'علامات الوقوف والانتظار', 'Parking Signs', 'Stilstaan en parkeren', 'Stationnement', 
        'علامات تنظم الوقوف والانتظار', 'Signs regulating stopping and parking', 'Borden die stilstaan en parkeren regelen', 'Panneaux réglementant l''arrêt et le stationnement', 5, TRUE, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
    name_ar = VALUES(name_ar), name_en = VALUES(name_en), name_nl = VALUES(name_nl), name_fr = VALUES(name_fr),
    description_ar = VALUES(description_ar), description_en = VALUES(description_en), description_nl = VALUES(description_nl), description_fr = VALUES(description_fr);

INSERT INTO categories (code, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, display_order, is_active, created_at, updated_at)
VALUES ('F', 'علامات إرشادية', 'Information Signs', 'Aanwijzing', 'Indication', 
        'علامات توفر معلومات ودلالات', 'Signs providing information and directions', 'Borden die informatie en aanwijzingen geven', 'Panneaux fournissant des informations et des indications', 6, TRUE, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
    name_ar = VALUES(name_ar), name_en = VALUES(name_en), name_nl = VALUES(name_nl), name_fr = VALUES(name_fr),
    description_ar = VALUES(description_ar), description_en = VALUES(description_en), description_nl = VALUES(description_nl), description_fr = VALUES(description_fr);

INSERT INTO categories (code, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, display_order, is_active, created_at, updated_at)
VALUES ('M', 'لوحات الدراجات', 'Bicycle Signs', 'Onderborden betreffende fietsen', 'Panneaux vélos', 
        'لوحات خاصة بالدراجات والدراجات النارية', 'Signs specific to bicycles and mopeds', 'Borden specifiek voor fietsen en bromfietsen', 'Panneaux spécifiques aux vélos et cyclomoteurs', 7, TRUE, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
    name_ar = VALUES(name_ar), name_en = VALUES(name_en), name_nl = VALUES(name_nl), name_fr = VALUES(name_fr),
    description_ar = VALUES(description_ar), description_en = VALUES(description_en), description_nl = VALUES(description_nl), description_fr = VALUES(description_fr);


-- ========================================
-- إدراج جميع العلامات المرورية
-- Insert ALL Traffic Signs
-- ========================================


-- Category A: علامات الخطر

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'A11', 
  (SELECT id FROM categories WHERE code = 'A'),
  'طريق يؤدي إلى رصيف أو شاطئ',
  'Road leads to quay or waterside',
  'Uitweg op kaai of oever.',
  'Route menant au quai ou à la rive',
  'طريق يؤدي إلى رصيف أو شاطئ',
  'Road leads to quay or waterside',
  'Uitweg op kaai of oever.',
  'Route menant au quai ou à la rive',
  'assets/traffic_signs/danger_signs/A11.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'A13', 
  (SELECT id FROM categories WHERE code = 'A'),
  'حفرة عرضية أو مطب',
  'Transverse depression or hump',
  'Dwarse uitholling of ezelsrug.',
  'Dépression transversale ou dos d''âne',
  'حفرة عرضية أو مطب',
  'Transverse depression or hump',
  'Dwarse uitholling of ezelsrug.',
  'Dépression transversale ou dos d''''âne',
  'assets/traffic_signs/danger_signs/A13.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'A14', 
  (SELECT id FROM categories WHERE code = 'A'),
  'علامة A14',
  'Verhoogde inrichting.',
  'Verhoogde inrichting.',
  'Verhoogde inrichting.',
  'علامة A14',
  'Verhoogde inrichting.',
  'Verhoogde inrichting.',
  'Verhoogde inrichting.',
  'assets/traffic_signs/danger_signs/A14.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'A15', 
  (SELECT id FROM categories WHERE code = 'A'),
  'علامة A15',
  'Gladde rijbaan - Slipgevaar.',
  'Gladde rijbaan - Slipgevaar.',
  'Gladde rijbaan - Slipgevaar.',
  'علامة A15',
  'Gladde rijbaan - Slipgevaar.',
  'Gladde rijbaan - Slipgevaar.',
  'Gladde rijbaan - Slipgevaar.',
  'assets/traffic_signs/danger_signs/A15.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'A17', 
  (SELECT id FROM categories WHERE code = 'A'),
  'علامة A17',
  'Kiezelprojectie',
  'Kiezelprojectie',
  'Kiezelprojectie',
  'علامة A17',
  'Kiezelprojectie',
  'Kiezelprojectie',
  'Kiezelprojectie',
  'assets/traffic_signs/danger_signs/A17.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'A19', 
  (SELECT id FROM categories WHERE code = 'A'),
  'علامة A19',
  'Vallende stenen.',
  'Vallende stenen.',
  'Vallende stenen.',
  'علامة A19',
  'Vallende stenen.',
  'Vallende stenen.',
  'Vallende stenen.',
  'assets/traffic_signs/danger_signs/A19.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'A1a', 
  (SELECT id FROM categories WHERE code = 'A'),
  'منعطف خطر لليسار',
  'Dangerous bend to the left',
  'Gevaarlijke bocht naar links.',
  'Virage dangereux à gauche',
  'منعطف خطر لليسار',
  'Dangerous bend to the left',
  'Gevaarlijke bocht naar links.',
  'Virage dangereux à gauche',
  'assets/traffic_signs/danger_signs/A1a.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'A1b', 
  (SELECT id FROM categories WHERE code = 'A'),
  'منعطف خطر لليمين',
  'Dangerous bend to the right',
  'Gevaarlijke bocht naar rechts.',
  'Virage dangereux à droite',
  'منعطف خطر لليمين',
  'Dangerous bend to the right',
  'Gevaarlijke bocht naar rechts.',
  'Virage dangereux à droite',
  'assets/traffic_signs/danger_signs/A1b.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'A1c', 
  (SELECT id FROM categories WHERE code = 'A'),
  'منعطفات خطرة، الأول لليسار',
  'Dangerous double or multiple bends, first to the left',
  'Gevaarlijke dubbele of meer dan twee bochten, de eerste naar links.',
  'Virages dangereux, le premier à gauche',
  'منعطفات خطرة، الأول لليسار',
  'Dangerous double or multiple bends, first to the left',
  'Gevaarlijke dubbele of meer dan twee bochten, de eerste naar links.',
  'Virages dangereux, le premier à gauche',
  'assets/traffic_signs/danger_signs/A1c.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'A1d', 
  (SELECT id FROM categories WHERE code = 'A'),
  'منعطفات خطرة، الأول لليمين',
  'Dangerous double or multiple bends, first to the right',
  'Gevaarlijke dubbele of meer dan twee bochten, de eerste naar rechts.',
  'Virages dangereux, le premier à droite',
  'منعطفات خطرة، الأول لليمين',
  'Dangerous double or multiple bends, first to the right',
  'Gevaarlijke dubbele of meer dan twee bochten, de eerste naar rechts.',
  'Virages dangereux, le premier à droite',
  'assets/traffic_signs/danger_signs/A1d.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'A21', 
  (SELECT id FROM categories WHERE code = 'A'),
  'معبر للمشاة',
  'Pedestrian crossing',
  'Oversteekplaats voor voetgangers.',
  'Passage pour piétons',
  'معبر للمشاة',
  'Pedestrian crossing',
  'Oversteekplaats voor voetgangers.',
  'Passage pour piétons',
  'assets/traffic_signs/danger_signs/A21.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'A23', 
  (SELECT id FROM categories WHERE code = 'A'),
  'أطفال',
  'Children',
  'Opgelet kinderen.',
  'Enfants',
  'أطفال',
  'Children',
  'Opgelet kinderen.',
  'Enfants',
  'assets/traffic_signs/danger_signs/A23.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'A25', 
  (SELECT id FROM categories WHERE code = 'A'),
  'دراجات ودراجات نارية',
  'Cyclists and moped riders',
  'Oversteekplaats voor fietsers en bromfietsers.',
  'Cyclistes et cyclomotoristes',
  'دراجات ودراجات نارية',
  'Cyclists and moped riders',
  'Oversteekplaats voor fietsers en bromfietsers.',
  'Cyclistes et cyclomotoristes',
  'assets/traffic_signs/danger_signs/A25.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'A27', 
  (SELECT id FROM categories WHERE code = 'A'),
  'علامة A27',
  'Overstekend groot wild.',
  'Overstekend groot wild.',
  'Overstekend groot wild.',
  'علامة A27',
  'Overstekend groot wild.',
  'Overstekend groot wild.',
  'Overstekend groot wild.',
  'assets/traffic_signs/danger_signs/A27.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'A29', 
  (SELECT id FROM categories WHERE code = 'A'),
  'علامة A29',
  'Overstekend vee.',
  'Overstekend vee.',
  'Overstekend vee.',
  'علامة A29',
  'Overstekend vee.',
  'Overstekend vee.',
  'Overstekend vee.',
  'assets/traffic_signs/danger_signs/A29.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'A3', 
  (SELECT id FROM categories WHERE code = 'A'),
  'انحدار خطر',
  'Dangerous descent',
  'Gevaarlijke daling.',
  'Descente dangereuse',
  'انحدار خطر',
  'Dangerous descent',
  'Gevaarlijke daling.',
  'Descente dangereuse',
  'assets/traffic_signs/danger_signs/A3.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'A31', 
  (SELECT id FROM categories WHERE code = 'A'),
  'علامة A31',
  'Werken.',
  'Werken.',
  'Werken.',
  'علامة A31',
  'Werken.',
  'Werken.',
  'Werken.',
  'assets/traffic_signs/danger_signs/A31.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'A33', 
  (SELECT id FROM categories WHERE code = 'A'),
  'علامة A33',
  'Verkeerslichten.',
  'Verkeerslichten.',
  'Verkeerslichten.',
  'علامة A33',
  'Verkeerslichten.',
  'Verkeerslichten.',
  'Verkeerslichten.',
  'assets/traffic_signs/danger_signs/A33.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'A35', 
  (SELECT id FROM categories WHERE code = 'A'),
  'علامة A35',
  'Vliegtuigen op geringe hoogte.',
  'Vliegtuigen op geringe hoogte.',
  'Vliegtuigen op geringe hoogte.',
  'علامة A35',
  'Vliegtuigen op geringe hoogte.',
  'Vliegtuigen op geringe hoogte.',
  'Vliegtuigen op geringe hoogte.',
  'assets/traffic_signs/danger_signs/A35.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'A37', 
  (SELECT id FROM categories WHERE code = 'A'),
  'علامة A37',
  'Zijwind.',
  'Zijwind.',
  'Zijwind.',
  'علامة A37',
  'Zijwind.',
  'Zijwind.',
  'Zijwind.',
  'assets/traffic_signs/danger_signs/A37.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'A39', 
  (SELECT id FROM categories WHERE code = 'A'),
  'علامة A39',
  'Twee richtingsverkeer toegelaten na een stuk éénrichtingsverkeer.',
  'Twee richtingsverkeer toegelaten na een stuk éénrichtingsverkeer.',
  'Twee richtingsverkeer toegelaten na een stuk éénrichtingsverkeer.',
  'علامة A39',
  'Twee richtingsverkeer toegelaten na een stuk éénrichtingsverkeer.',
  'Twee richtingsverkeer toegelaten na een stuk éénrichtingsverkeer.',
  'Twee richtingsverkeer toegelaten na een stuk éénrichtingsverkeer.',
  'assets/traffic_signs/danger_signs/A39.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'A41', 
  (SELECT id FROM categories WHERE code = 'A'),
  'علامة A41',
  'Overweg met slagbomen.',
  'Overweg met slagbomen.',
  'Overweg met slagbomen.',
  'علامة A41',
  'Overweg met slagbomen.',
  'Overweg met slagbomen.',
  'Overweg met slagbomen.',
  'assets/traffic_signs/danger_signs/A41.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'A43', 
  (SELECT id FROM categories WHERE code = 'A'),
  'علامة A43',
  'Overweg zonder slagbomen.',
  'Overweg zonder slagbomen.',
  'Overweg zonder slagbomen.',
  'علامة A43',
  'Overweg zonder slagbomen.',
  'Overweg zonder slagbomen.',
  'Overweg zonder slagbomen.',
  'assets/traffic_signs/danger_signs/A43.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'A49', 
  (SELECT id FROM categories WHERE code = 'A'),
  'علامة A49',
  'Openbare weg kruist met een of meer in de rijbaan aangelegde sporen.',
  'Openbare weg kruist met een of meer in de rijbaan aangelegde sporen.',
  'Openbare weg kruist met een of meer in de rijbaan aangelegde sporen.',
  'علامة A49',
  'Openbare weg kruist met een of meer in de rijbaan aangelegde sporen.',
  'Openbare weg kruist met een of meer in de rijbaan aangelegde sporen.',
  'Openbare weg kruist met een of meer in de rijbaan aangelegde sporen.',
  'assets/traffic_signs/danger_signs/A49.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'A5', 
  (SELECT id FROM categories WHERE code = 'A'),
  'صعود خطر',
  'Dangerous ascent',
  'Gevaarlijke helling.',
  'Montée dangereuse',
  'صعود خطر',
  'Dangerous ascent',
  'Gevaarlijke helling.',
  'Montée dangereuse',
  'assets/traffic_signs/danger_signs/A5.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'A50', 
  (SELECT id FROM categories WHERE code = 'A'),
  'علامة A50',
  'Opgelet file',
  'Opgelet file',
  'Opgelet file',
  'علامة A50',
  'Opgelet file',
  'Opgelet file',
  'Opgelet file',
  'assets/traffic_signs/danger_signs/A50.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'A51', 
  (SELECT id FROM categories WHERE code = 'A'),
  'علامة A51',
  'Gevaar dat niet door een speciaal symbool wordt bepaald.',
  'Gevaar dat niet door een speciaal symbool wordt bepaald.',
  'Gevaar dat niet door een speciaal symbool wordt bepaald.',
  'علامة A51',
  'Gevaar dat niet door een speciaal symbool wordt bepaald.',
  'Gevaar dat niet door een speciaal symbool wordt bepaald.',
  'Gevaar dat niet door een speciaal symbool wordt bepaald.',
  'assets/traffic_signs/danger_signs/A51.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'A7a', 
  (SELECT id FROM categories WHERE code = 'A'),
  'تضييق الطريق',
  'Road narrowing',
  'Rijbaanversmalling',
  'Rétrécissement de chaussée',
  'تضييق الطريق',
  'Road narrowing',
  'Rijbaanversmalling',
  'Rétrécissement de chaussée',
  'assets/traffic_signs/danger_signs/A7a.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'A7b', 
  (SELECT id FROM categories WHERE code = 'A'),
  'تضييق الطريق',
  'Road narrowing',
  'Rijbaanversmalling links',
  'Rétrécissement de chaussée',
  'تضييق الطريق',
  'Road narrowing',
  'Rijbaanversmalling links',
  'Rétrécissement de chaussée',
  'assets/traffic_signs/danger_signs/A7b.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'A7c', 
  (SELECT id FROM categories WHERE code = 'A'),
  'تضييق الطريق',
  'Road narrowing',
  'Rijbaanversmalling rechts',
  'Rétrécissement de chaussée',
  'تضييق الطريق',
  'Road narrowing',
  'Rijbaanversmalling rechts',
  'Rétrécissement de chaussée',
  'assets/traffic_signs/danger_signs/A7c.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'A9', 
  (SELECT id FROM categories WHERE code = 'A'),
  'جسر متحرك',
  'Movable bridge',
  'Beweegbare brug.',
  'Pont mobile',
  'جسر متحرك',
  'Movable bridge',
  'Beweegbare brug.',
  'Pont mobile',
  'assets/traffic_signs/danger_signs/A9.png',
  TRUE,
  NOW(),
  NOW()
);


-- Category B: علامات الأولوية

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'B1', 
  (SELECT id FROM categories WHERE code = 'B'),
  'أعط الأولوية',
  'Give way',
  'Voorrang verlenen',
  'Cédez le passage',
  'أعط الأولوية',
  'Give way',
  'Voorrang verlenen',
  'Cédez le passage',
  'assets/traffic_signs/priority_signs/B1.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'B11', 
  (SELECT id FROM categories WHERE code = 'B'),
  'طريق ذو أولوية',
  'Priority road',
  'Einde voorrangsweg',
  'Route prioritaire',
  'طريق ذو أولوية',
  'Priority road',
  'Einde voorrangsweg',
  'Route prioritaire',
  'assets/traffic_signs/priority_signs/B11.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'B15a', 
  (SELECT id FROM categories WHERE code = 'B'),
  'علامة B15a',
  'Voorrang op de kruisende zijwegen',
  'Voorrang op de kruisende zijwegen',
  'Voorrang op de kruisende zijwegen',
  'علامة B15a',
  'Voorrang op de kruisende zijwegen',
  'Voorrang op de kruisende zijwegen',
  'Voorrang op de kruisende zijwegen',
  'assets/traffic_signs/priority_signs/B15a.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'B15b', 
  (SELECT id FROM categories WHERE code = 'B'),
  'علامة B15b',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'علامة B15b',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'assets/traffic_signs/priority_signs/B15b.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'B15c', 
  (SELECT id FROM categories WHERE code = 'B'),
  'علامة B15c',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'علامة B15c',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'assets/traffic_signs/priority_signs/B15c.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'B15d', 
  (SELECT id FROM categories WHERE code = 'B'),
  'علامة B15d',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'علامة B15d',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'assets/traffic_signs/priority_signs/B15d.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'B15e', 
  (SELECT id FROM categories WHERE code = 'B'),
  'علامة B15e',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'علامة B15e',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'assets/traffic_signs/priority_signs/B15e.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'B15f', 
  (SELECT id FROM categories WHERE code = 'B'),
  'علامة B15f',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'علامة B15f',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'assets/traffic_signs/priority_signs/B15f.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'B15g', 
  (SELECT id FROM categories WHERE code = 'B'),
  'علامة B15g',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'علامة B15g',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'assets/traffic_signs/priority_signs/B15g.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'B17', 
  (SELECT id FROM categories WHERE code = 'B'),
  'علامة B17',
  'Kruispunt waar de voorrang van rechts geldt',
  'Kruispunt waar de voorrang van rechts geldt',
  'Kruispunt waar de voorrang van rechts geldt',
  'علامة B17',
  'Kruispunt waar de voorrang van rechts geldt',
  'Kruispunt waar de voorrang van rechts geldt',
  'Kruispunt waar de voorrang van rechts geldt',
  'assets/traffic_signs/priority_signs/B17.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'B19', 
  (SELECT id FROM categories WHERE code = 'B'),
  'أعط الأولوية',
  'Give way',
  'Smalle doorgang voorrang verlenen aan de bestuurders die uit de tegenovergestelde richting komen',
  'Cédez le passage',
  'أعط الأولوية',
  'Give way',
  'Smalle doorgang voorrang verlenen aan de bestuurders die uit de tegenovergestelde richting komen',
  'Cédez le passage',
  'assets/traffic_signs/priority_signs/B19.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'B21', 
  (SELECT id FROM categories WHERE code = 'B'),
  'علامة B21',
  'Smalle doorgang voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen',
  'Smalle doorgang voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen',
  'Smalle doorgang voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen',
  'علامة B21',
  'Smalle doorgang voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen',
  'Smalle doorgang voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen',
  'Smalle doorgang voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen',
  'assets/traffic_signs/priority_signs/B21.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'B22', 
  (SELECT id FROM categories WHERE code = 'B'),
  'علامة B22',
  'Fietsers en speed pedelecs mogen rechtsaf slaan en de verkeerslichten voorbijrijden',
  'Fietsers en speed pedelecs mogen rechtsaf slaan en de verkeerslichten voorbijrijden',
  'Fietsers en speed pedelecs mogen rechtsaf slaan en de verkeerslichten voorbijrijden',
  'علامة B22',
  'Fietsers en speed pedelecs mogen rechtsaf slaan en de verkeerslichten voorbijrijden',
  'Fietsers en speed pedelecs mogen rechtsaf slaan en de verkeerslichten voorbijrijden',
  'Fietsers en speed pedelecs mogen rechtsaf slaan en de verkeerslichten voorbijrijden',
  'assets/traffic_signs/priority_signs/B22.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'B23', 
  (SELECT id FROM categories WHERE code = 'B'),
  'علامة B23',
  'Fietsers en speed pedelecs mogen rechtdoor rijden en de verkeerslichten voorbijrijden',
  'Fietsers en speed pedelecs mogen rechtdoor rijden en de verkeerslichten voorbijrijden',
  'Fietsers en speed pedelecs mogen rechtdoor rijden en de verkeerslichten voorbijrijden',
  'علامة B23',
  'Fietsers en speed pedelecs mogen rechtdoor rijden en de verkeerslichten voorbijrijden',
  'Fietsers en speed pedelecs mogen rechtdoor rijden en de verkeerslichten voorbijrijden',
  'Fietsers en speed pedelecs mogen rechtdoor rijden en de verkeerslichten voorbijrijden',
  'assets/traffic_signs/priority_signs/B23.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'B5', 
  (SELECT id FROM categories WHERE code = 'B'),
  'قف',
  'Stop',
  'Stoppen en voorrang verlenen',
  'Stop',
  'قف',
  'Stop',
  'Stoppen en voorrang verlenen',
  'Stop',
  'assets/traffic_signs/priority_signs/B5.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'B9', 
  (SELECT id FROM categories WHERE code = 'B'),
  'طريق ذو أولوية',
  'Priority road',
  'Voorrangsweg',
  'Route prioritaire',
  'طريق ذو أولوية',
  'Priority road',
  'Voorrangsweg',
  'Route prioritaire',
  'assets/traffic_signs/priority_signs/B9.png',
  TRUE,
  NOW(),
  NOW()
);


-- Category C: علامات المنع

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'C1', 
  (SELECT id FROM categories WHERE code = 'C'),
  'اتجاه ممنوع',
  'Direction prohibited',
  'Verboden richting voor iedere bestuurder',
  'Direction interdite',
  'اتجاه ممنوع',
  'Direction prohibited',
  'Verboden richting voor iedere bestuurder',
  'Direction interdite',
  'assets/traffic_signs/prohibition_signs/C1.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'C11', 
  (SELECT id FROM categories WHERE code = 'C'),
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van rijwielen.',
  'Accès interdit',
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van rijwielen.',
  'Accès interdit',
  'assets/traffic_signs/prohibition_signs/C11.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'C13', 
  (SELECT id FROM categories WHERE code = 'C'),
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van gespannen.',
  'Accès interdit',
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van gespannen.',
  'Accès interdit',
  'assets/traffic_signs/prohibition_signs/C13.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'C15', 
  (SELECT id FROM categories WHERE code = 'C'),
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor ruiters.',
  'Accès interdit',
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor ruiters.',
  'Accès interdit',
  'assets/traffic_signs/prohibition_signs/C15.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'C17', 
  (SELECT id FROM categories WHERE code = 'C'),
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van handkarren.',
  'Accès interdit',
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van handkarren.',
  'Accès interdit',
  'assets/traffic_signs/prohibition_signs/C17.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'C19', 
  (SELECT id FROM categories WHERE code = 'C'),
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor voetgangers.',
  'Accès interdit',
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor voetgangers.',
  'Accès interdit',
  'assets/traffic_signs/prohibition_signs/C19.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'C21', 
  (SELECT id FROM categories WHERE code = 'C'),
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van voertuigen waarvan de massa in beladen toestand hoger is dan de aangeduide massa.',
  'Accès interdit',
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van voertuigen waarvan de massa in beladen toestand hoger is dan de aangeduide massa.',
  'Accès interdit',
  'assets/traffic_signs/prohibition_signs/C21.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'C22', 
  (SELECT id FROM categories WHERE code = 'C'),
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van autocars.',
  'Accès interdit',
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van autocars.',
  'Accès interdit',
  'assets/traffic_signs/prohibition_signs/C22.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'C23', 
  (SELECT id FROM categories WHERE code = 'C'),
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van voertuigen bestemd of gebruikt voor het vervoer van zaken.',
  'Accès interdit',
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van voertuigen bestemd of gebruikt voor het vervoer van zaken.',
  'Accès interdit',
  'assets/traffic_signs/prohibition_signs/C23.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'C24a', 
  (SELECT id FROM categories WHERE code = 'C'),
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van voertuigen die gevaarlijke goederen vervoeren.',
  'Accès interdit',
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van voertuigen die gevaarlijke goederen vervoeren.',
  'Accès interdit',
  'assets/traffic_signs/prohibition_signs/C24a.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'C24b', 
  (SELECT id FROM categories WHERE code = 'C'),
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van voertuigen die gevaarlijke ontvlambare of ontplofbare stoffen vervoeren.',
  'Accès interdit',
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van voertuigen die gevaarlijke ontvlambare of ontplofbare stoffen vervoeren.',
  'Accès interdit',
  'assets/traffic_signs/prohibition_signs/C24b.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'C24c', 
  (SELECT id FROM categories WHERE code = 'C'),
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van voertuigen die gevaarlijke verontreinigende stoffen vervoeren.',
  'Accès interdit',
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van voertuigen die gevaarlijke verontreinigende stoffen vervoeren.',
  'Accès interdit',
  'assets/traffic_signs/prohibition_signs/C24c.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'C25', 
  (SELECT id FROM categories WHERE code = 'C'),
  'علامة C25',
  'Verboden voor voertuigen langer dan het aangeduide',
  'Verboden voor voertuigen langer dan het aangeduide',
  'Verboden voor voertuigen langer dan het aangeduide',
  'علامة C25',
  'Verboden voor voertuigen langer dan het aangeduide',
  'Verboden voor voertuigen langer dan het aangeduide',
  'Verboden voor voertuigen langer dan het aangeduide',
  'assets/traffic_signs/prohibition_signs/C25.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'C27', 
  (SELECT id FROM categories WHERE code = 'C'),
  'علامة C27',
  'Verboden voor voertuigen breder dan het aangeduide.',
  'Verboden voor voertuigen breder dan het aangeduide.',
  'Verboden voor voertuigen breder dan het aangeduide.',
  'علامة C27',
  'Verboden voor voertuigen breder dan het aangeduide.',
  'Verboden voor voertuigen breder dan het aangeduide.',
  'Verboden voor voertuigen breder dan het aangeduide.',
  'assets/traffic_signs/prohibition_signs/C27.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'C29', 
  (SELECT id FROM categories WHERE code = 'C'),
  'علامة C29',
  'Verboden voor voertuigen hoger dan het aangeduide.',
  'Verboden voor voertuigen hoger dan het aangeduide.',
  'Verboden voor voertuigen hoger dan het aangeduide.',
  'علامة C29',
  'Verboden voor voertuigen hoger dan het aangeduide.',
  'Verboden voor voertuigen hoger dan het aangeduide.',
  'Verboden voor voertuigen hoger dan het aangeduide.',
  'assets/traffic_signs/prohibition_signs/C29.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'C3', 
  (SELECT id FROM categories WHERE code = 'C'),
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang, in beide richtingen, voor iedere bestuurder.',
  'Accès interdit',
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang, in beide richtingen, voor iedere bestuurder.',
  'Accès interdit',
  'assets/traffic_signs/prohibition_signs/C3.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'C31a', 
  (SELECT id FROM categories WHERE code = 'C'),
  'علامة C31a',
  'Verbod om links af te slaan.',
  'Verbod om links af te slaan.',
  'Verbod om links af te slaan.',
  'علامة C31a',
  'Verbod om links af te slaan.',
  'Verbod om links af te slaan.',
  'Verbod om links af te slaan.',
  'assets/traffic_signs/prohibition_signs/C31a.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'C31b', 
  (SELECT id FROM categories WHERE code = 'C'),
  'علامة C31b',
  'Verbod rechts af te slaan.',
  'Verbod rechts af te slaan.',
  'Verbod rechts af te slaan.',
  'علامة C31b',
  'Verbod rechts af te slaan.',
  'Verbod rechts af te slaan.',
  'Verbod rechts af te slaan.',
  'assets/traffic_signs/prohibition_signs/C31b.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'C33', 
  (SELECT id FROM categories WHERE code = 'C'),
  'علامة C33',
  'Verbod om te keren.',
  'Verbod om te keren.',
  'Verbod om te keren.',
  'علامة C33',
  'Verbod om te keren.',
  'Verbod om te keren.',
  'Verbod om te keren.',
  'assets/traffic_signs/prohibition_signs/C33.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'C35', 
  (SELECT id FROM categories WHERE code = 'C'),
  'علامة C35',
  'Verbod een voertuig links in te halen.',
  'Verbod een voertuig links in te halen.',
  'Verbod een voertuig links in te halen.',
  'علامة C35',
  'Verbod een voertuig links in te halen.',
  'Verbod een voertuig links in te halen.',
  'Verbod een voertuig links in te halen.',
  'assets/traffic_signs/prohibition_signs/C35.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'C37', 
  (SELECT id FROM categories WHERE code = 'C'),
  'علامة C37',
  'Einde verbod opgelegd door het verkeersbord C35',
  'Einde verbod opgelegd door het verkeersbord C35',
  'Einde verbod opgelegd door het verkeersbord C35',
  'علامة C37',
  'Einde verbod opgelegd door het verkeersbord C35',
  'Einde verbod opgelegd door het verkeersbord C35',
  'Einde verbod opgelegd door het verkeersbord C35',
  'assets/traffic_signs/prohibition_signs/C37.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'C39', 
  (SELECT id FROM categories WHERE code = 'C'),
  'علامة C39',
  'Verbod voertuigen met toegelaten massa > 3500 kg in te halen',
  'Verbod voertuigen met toegelaten massa > 3500 kg in te halen',
  'Verbod voertuigen met toegelaten massa > 3500 kg in te halen',
  'علامة C39',
  'Verbod voertuigen met toegelaten massa > 3500 kg in te halen',
  'Verbod voertuigen met toegelaten massa > 3500 kg in te halen',
  'Verbod voertuigen met toegelaten massa > 3500 kg in te halen',
  'assets/traffic_signs/prohibition_signs/C39.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'C41', 
  (SELECT id FROM categories WHERE code = 'C'),
  'علامة C41',
  'Einde van het verbod opgelegd door het verkeersbord C39.',
  'Einde van het verbod opgelegd door het verkeersbord C39.',
  'Einde van het verbod opgelegd door het verkeersbord C39.',
  'علامة C41',
  'Einde van het verbod opgelegd door het verkeersbord C39.',
  'Einde van het verbod opgelegd door het verkeersbord C39.',
  'Einde van het verbod opgelegd door het verkeersbord C39.',
  'assets/traffic_signs/prohibition_signs/C41.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'C43', 
  (SELECT id FROM categories WHERE code = 'C'),
  'علامة C43',
  'Verbod te rijden met een grotere snelheid dan is aangeduid.',
  'Verbod te rijden met een grotere snelheid dan is aangeduid.',
  'Verbod te rijden met een grotere snelheid dan is aangeduid.',
  'علامة C43',
  'Verbod te rijden met een grotere snelheid dan is aangeduid.',
  'Verbod te rijden met een grotere snelheid dan is aangeduid.',
  'Verbod te rijden met een grotere snelheid dan is aangeduid.',
  'assets/traffic_signs/prohibition_signs/C43.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'C45', 
  (SELECT id FROM categories WHERE code = 'C'),
  'علامة C45',
  'Einde van de snelheidsbeperking opgelegd door het verkeersbord C43.',
  'Einde van de snelheidsbeperking opgelegd door het verkeersbord C43.',
  'Einde van de snelheidsbeperking opgelegd door het verkeersbord C43.',
  'علامة C45',
  'Einde van de snelheidsbeperking opgelegd door het verkeersbord C43.',
  'Einde van de snelheidsbeperking opgelegd door het verkeersbord C43.',
  'Einde van de snelheidsbeperking opgelegd door het verkeersbord C43.',
  'assets/traffic_signs/prohibition_signs/C45.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'C46', 
  (SELECT id FROM categories WHERE code = 'C'),
  'علامة C46',
  'Einde van alle plaatselijke verbodsbepalingen opgelegd aan de voertuigen in beweging.',
  'Einde van alle plaatselijke verbodsbepalingen opgelegd aan de voertuigen in beweging.',
  'Einde van alle plaatselijke verbodsbepalingen opgelegd aan de voertuigen in beweging.',
  'علامة C46',
  'Einde van alle plaatselijke verbodsbepalingen opgelegd aan de voertuigen in beweging.',
  'Einde van alle plaatselijke verbodsbepalingen opgelegd aan de voertuigen in beweging.',
  'Einde van alle plaatselijke verbodsbepalingen opgelegd aan de voertuigen in beweging.',
  'assets/traffic_signs/prohibition_signs/C46.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'C47', 
  (SELECT id FROM categories WHERE code = 'C'),
  'قف',
  'Stop',
  'Tolpost. Verbod voorbij te rijden zonder te stoppen.',
  'Stop',
  'قف',
  'Stop',
  'Tolpost. Verbod voorbij te rijden zonder te stoppen.',
  'Stop',
  'assets/traffic_signs/prohibition_signs/C47.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'C5', 
  (SELECT id FROM categories WHERE code = 'C'),
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van motorvoertuigen en motorfietsen.',
  'Accès interdit',
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van motorvoertuigen en motorfietsen.',
  'Accès interdit',
  'assets/traffic_signs/prohibition_signs/C5.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'C7', 
  (SELECT id FROM categories WHERE code = 'C'),
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van motorfietsen.',
  'Accès interdit',
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van motorfietsen.',
  'Accès interdit',
  'assets/traffic_signs/prohibition_signs/C7.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'C9', 
  (SELECT id FROM categories WHERE code = 'C'),
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van bromfietsen en fietsen.',
  'Accès interdit',
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van bromfietsen en fietsen.',
  'Accès interdit',
  'assets/traffic_signs/prohibition_signs/C9.png',
  TRUE,
  NOW(),
  NOW()
);


-- Category D: علامات الإلزام

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'D10', 
  (SELECT id FROM categories WHERE code = 'D'),
  'علامة D10',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'علامة D10',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'assets/traffic_signs/mandatory_signs/D10.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'D11', 
  (SELECT id FROM categories WHERE code = 'D'),
  'علامة D11',
  'Verplichte weg voor voetgangers.',
  'Verplichte weg voor voetgangers.',
  'Verplichte weg voor voetgangers.',
  'علامة D11',
  'Verplichte weg voor voetgangers.',
  'Verplichte weg voor voetgangers.',
  'Verplichte weg voor voetgangers.',
  'assets/traffic_signs/mandatory_signs/D11.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'D13', 
  (SELECT id FROM categories WHERE code = 'D'),
  'علامة D13',
  'Verplichte weg voor ruiters.',
  'Verplichte weg voor ruiters.',
  'Verplichte weg voor ruiters.',
  'علامة D13',
  'Verplichte weg voor ruiters.',
  'Verplichte weg voor ruiters.',
  'Verplichte weg voor ruiters.',
  'assets/traffic_signs/mandatory_signs/D13.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'D1a', 
  (SELECT id FROM categories WHERE code = 'D'),
  'علامة D1a',
  'Verplichting rechtdoor.',
  'Verplichting rechtdoor.',
  'Verplichting rechtdoor.',
  'علامة D1a',
  'Verplichting rechtdoor.',
  'Verplichting rechtdoor.',
  'Verplichting rechtdoor.',
  'assets/traffic_signs/mandatory_signs/D1a.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'D1b', 
  (SELECT id FROM categories WHERE code = 'D'),
  'علامة D1b',
  'Verplichting rechts afslaan.',
  'Verplichting rechts afslaan.',
  'Verplichting rechts afslaan.',
  'علامة D1b',
  'Verplichting rechts afslaan.',
  'Verplichting rechts afslaan.',
  'Verplichting rechts afslaan.',
  'assets/traffic_signs/mandatory_signs/D1b.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'D1c', 
  (SELECT id FROM categories WHERE code = 'D'),
  'علامة D1c',
  'Verplichting links aanhouden.',
  'Verplichting links aanhouden.',
  'Verplichting links aanhouden.',
  'علامة D1c',
  'Verplichting links aanhouden.',
  'Verplichting links aanhouden.',
  'Verplichting links aanhouden.',
  'assets/traffic_signs/mandatory_signs/D1c.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'D1d', 
  (SELECT id FROM categories WHERE code = 'D'),
  'علامة D1d',
  'Verplichting rechts aanhouden.',
  'Verplichting rechts aanhouden.',
  'Verplichting rechts aanhouden.',
  'علامة D1d',
  'Verplichting rechts aanhouden.',
  'Verplichting rechts aanhouden.',
  'Verplichting rechts aanhouden.',
  'assets/traffic_signs/mandatory_signs/D1d.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'D1e', 
  (SELECT id FROM categories WHERE code = 'D'),
  'علامة D1e',
  'Verplicht de aangeduide richting te volgen (linksaf)',
  'Verplicht de aangeduide richting te volgen (linksaf)',
  'Verplicht de aangeduide richting te volgen (linksaf)',
  'علامة D1e',
  'Verplicht de aangeduide richting te volgen (linksaf)',
  'Verplicht de aangeduide richting te volgen (linksaf)',
  'Verplicht de aangeduide richting te volgen (linksaf)',
  'assets/traffic_signs/mandatory_signs/D1e.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'D1f', 
  (SELECT id FROM categories WHERE code = 'D'),
  'علامة D1f',
  'Verplicht de aangeduide richting te volgen (rechtsaf)',
  'Verplicht de aangeduide richting te volgen (rechtsaf)',
  'Verplicht de aangeduide richting te volgen (rechtsaf)',
  'علامة D1f',
  'Verplicht de aangeduide richting te volgen (rechtsaf)',
  'Verplicht de aangeduide richting te volgen (rechtsaf)',
  'Verplicht de aangeduide richting te volgen (rechtsaf)',
  'assets/traffic_signs/mandatory_signs/D1f.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'D3a', 
  (SELECT id FROM categories WHERE code = 'D'),
  'علامة D3a',
  'Verplicht één van de pijlen te volgen.',
  'Verplicht één van de pijlen te volgen.',
  'Verplicht één van de pijlen te volgen.',
  'علامة D3a',
  'Verplicht één van de pijlen te volgen.',
  'Verplicht één van de pijlen te volgen.',
  'Verplicht één van de pijlen te volgen.',
  'assets/traffic_signs/mandatory_signs/D3a.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'D3b', 
  (SELECT id FROM categories WHERE code = 'D'),
  'علامة D3b',
  'Verplicht één van de pijlen te volgen.',
  'Verplicht één van de pijlen te volgen.',
  'Verplicht één van de pijlen te volgen.',
  'علامة D3b',
  'Verplicht één van de pijlen te volgen.',
  'Verplicht één van de pijlen te volgen.',
  'Verplicht één van de pijlen te volgen.',
  'assets/traffic_signs/mandatory_signs/D3b.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'D4', 
  (SELECT id FROM categories WHERE code = 'D'),
  'علامة D4',
  'Verplicht rechts voor voertuigen die gevaarlijke goederen vervoeren.',
  'Verplicht rechts voor voertuigen die gevaarlijke goederen vervoeren.',
  'Verplicht rechts voor voertuigen die gevaarlijke goederen vervoeren.',
  'علامة D4',
  'Verplicht rechts voor voertuigen die gevaarlijke goederen vervoeren.',
  'Verplicht rechts voor voertuigen die gevaarlijke goederen vervoeren.',
  'Verplicht rechts voor voertuigen die gevaarlijke goederen vervoeren.',
  'assets/traffic_signs/mandatory_signs/D4.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'D5', 
  (SELECT id FROM categories WHERE code = 'D'),
  'علامة D5',
  'Verplicht rondgaand verkeer.',
  'Verplicht rondgaand verkeer.',
  'Verplicht rondgaand verkeer.',
  'علامة D5',
  'Verplicht rondgaand verkeer.',
  'Verplicht rondgaand verkeer.',
  'Verplicht rondgaand verkeer.',
  'assets/traffic_signs/mandatory_signs/D5.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'D7', 
  (SELECT id FROM categories WHERE code = 'D'),
  'ممر دراجات إلزامي',
  'Compulsory cycle path',
  'Verplicht fietspad.',
  'Piste cyclable obligatoire',
  'ممر دراجات إلزامي',
  'Compulsory cycle path',
  'Verplicht fietspad.',
  'Piste cyclable obligatoire',
  'assets/traffic_signs/mandatory_signs/D7.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'D9a', 
  (SELECT id FROM categories WHERE code = 'D'),
  'علامة D9a',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'علامة D9a',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'assets/traffic_signs/mandatory_signs/D9a.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'D9b', 
  (SELECT id FROM categories WHERE code = 'D'),
  'علامة D9b',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'علامة D9b',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'assets/traffic_signs/mandatory_signs/D9b.png',
  TRUE,
  NOW(),
  NOW()
);


-- Category E: علامات الوقوف والانتظار

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'E1', 
  (SELECT id FROM categories WHERE code = 'E'),
  'ممنوع الانتظار',
  'Parking prohibited',
  'Parkeerverbod.',
  'Interdiction de stationner',
  'ممنوع الانتظار',
  'Parking prohibited',
  'Parkeerverbod.',
  'Interdiction de stationner',
  'assets/traffic_signs/parking_signs/E1.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'E11', 
  (SELECT id FROM categories WHERE code = 'E'),
  'علامة E11',
  'Halfmaandelijks parkeren in gans de bebouwde kom.',
  'Halfmaandelijks parkeren in gans de bebouwde kom.',
  'Halfmaandelijks parkeren in gans de bebouwde kom.',
  'علامة E11',
  'Halfmaandelijks parkeren in gans de bebouwde kom.',
  'Halfmaandelijks parkeren in gans de bebouwde kom.',
  'Halfmaandelijks parkeren in gans de bebouwde kom.',
  'assets/traffic_signs/parking_signs/E11.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'E3', 
  (SELECT id FROM categories WHERE code = 'E'),
  'ممنوع التوقف والانتظار',
  'No stopping or parking',
  'Stilstaan en parkeren verboden.',
  'Arrêt et stationnement interdits',
  'ممنوع التوقف والانتظار',
  'No stopping or parking',
  'Stilstaan en parkeren verboden.',
  'Arrêt et stationnement interdits',
  'assets/traffic_signs/parking_signs/E3.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'E5', 
  (SELECT id FROM categories WHERE code = 'E'),
  'ممنوع الانتظار',
  'Parking prohibited',
  'Parkeerverbod van de 1e tot de 15e van de maand.',
  'Interdiction de stationner',
  'ممنوع الانتظار',
  'Parking prohibited',
  'Parkeerverbod van de 1e tot de 15e van de maand.',
  'Interdiction de stationner',
  'assets/traffic_signs/parking_signs/E5.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'E7', 
  (SELECT id FROM categories WHERE code = 'E'),
  'ممنوع الانتظار',
  'Parking prohibited',
  'Parkeerverbod van de 16e tot het einde van de maand.',
  'Interdiction de stationner',
  'ممنوع الانتظار',
  'Parking prohibited',
  'Parkeerverbod van de 16e tot het einde van de maand.',
  'Interdiction de stationner',
  'assets/traffic_signs/parking_signs/E7.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'E9a', 
  (SELECT id FROM categories WHERE code = 'E'),
  'علامة E9a',
  'parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.',
  'parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.',
  'parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.',
  'علامة E9a',
  'parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.',
  'parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.',
  'parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.',
  'assets/traffic_signs/parking_signs/E9a.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'E9b', 
  (SELECT id FROM categories WHERE code = 'E'),
  'علامة E9b',
  'Parkeren uitsluitend voor auto''s.',
  'Parkeren uitsluitend voor auto''s.',
  'Parkeren uitsluitend voor auto''s.',
  'علامة E9b',
  'Parkeren uitsluitend voor auto''''s.',
  'Parkeren uitsluitend voor auto''''s.',
  'Parkeren uitsluitend voor auto''''s.',
  'assets/traffic_signs/parking_signs/E9b.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'E9c', 
  (SELECT id FROM categories WHERE code = 'E'),
  'علامة E9c',
  'Parkeren uitsluitend voorvrachtwagens.',
  'Parkeren uitsluitend voorvrachtwagens.',
  'Parkeren uitsluitend voorvrachtwagens.',
  'علامة E9c',
  'Parkeren uitsluitend voorvrachtwagens.',
  'Parkeren uitsluitend voorvrachtwagens.',
  'Parkeren uitsluitend voorvrachtwagens.',
  'assets/traffic_signs/parking_signs/E9c.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'E9d', 
  (SELECT id FROM categories WHERE code = 'E'),
  'علامة E9d',
  'Parkeren uitsluitend voor autocars.',
  'Parkeren uitsluitend voor autocars.',
  'Parkeren uitsluitend voor autocars.',
  'علامة E9d',
  'Parkeren uitsluitend voor autocars.',
  'Parkeren uitsluitend voor autocars.',
  'Parkeren uitsluitend voor autocars.',
  'assets/traffic_signs/parking_signs/E9d.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'E9e', 
  (SELECT id FROM categories WHERE code = 'E'),
  'علامة E9e',
  'Verplicht parkeren op de berm of op het trottoir.',
  'Verplicht parkeren op de berm of op het trottoir.',
  'Verplicht parkeren op de berm of op het trottoir.',
  'علامة E9e',
  'Verplicht parkeren op de berm of op het trottoir.',
  'Verplicht parkeren op de berm of op het trottoir.',
  'Verplicht parkeren op de berm of op het trottoir.',
  'assets/traffic_signs/parking_signs/E9e.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'E9f', 
  (SELECT id FROM categories WHERE code = 'E'),
  'علامة E9f',
  'Verplicht parkeren deels op de berm of op het trottoir.',
  'Verplicht parkeren deels op de berm of op het trottoir.',
  'Verplicht parkeren deels op de berm of op het trottoir.',
  'علامة E9f',
  'Verplicht parkeren deels op de berm of op het trottoir.',
  'Verplicht parkeren deels op de berm of op het trottoir.',
  'Verplicht parkeren deels op de berm of op het trottoir.',
  'assets/traffic_signs/parking_signs/E9f.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'E9g', 
  (SELECT id FROM categories WHERE code = 'E'),
  'علامة E9g',
  'Verplicht parkeren op de rijbaan.',
  'Verplicht parkeren op de rijbaan.',
  'Verplicht parkeren op de rijbaan.',
  'علامة E9g',
  'Verplicht parkeren op de rijbaan.',
  'Verplicht parkeren op de rijbaan.',
  'Verplicht parkeren op de rijbaan.',
  'assets/traffic_signs/parking_signs/E9g.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'E9h', 
  (SELECT id FROM categories WHERE code = 'E'),
  'علامة E9h',
  'Parkeren uitsluitend voor kampeerauto''s.',
  'Parkeren uitsluitend voor kampeerauto''s.',
  'Parkeren uitsluitend voor kampeerauto''s.',
  'علامة E9h',
  'Parkeren uitsluitend voor kampeerauto''''s.',
  'Parkeren uitsluitend voor kampeerauto''''s.',
  'Parkeren uitsluitend voor kampeerauto''''s.',
  'assets/traffic_signs/parking_signs/E9h.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'E9i', 
  (SELECT id FROM categories WHERE code = 'E'),
  'علامة E9i',
  'Parkeren uitsluitend voor motorfietsen.',
  'Parkeren uitsluitend voor motorfietsen.',
  'Parkeren uitsluitend voor motorfietsen.',
  'علامة E9i',
  'Parkeren uitsluitend voor motorfietsen.',
  'Parkeren uitsluitend voor motorfietsen.',
  'Parkeren uitsluitend voor motorfietsen.',
  'assets/traffic_signs/parking_signs/E9i.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'E9j', 
  (SELECT id FROM categories WHERE code = 'E'),
  'علامة E9j',
  'wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en auto’s',
  'wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en auto’s',
  'wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en auto’s',
  'علامة E9j',
  'wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en auto’s',
  'wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en auto’s',
  'wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en auto’s',
  'assets/traffic_signs/parking_signs/E9j.png',
  TRUE,
  NOW(),
  NOW()
);


-- Category F: علامات إرشادية

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F101a', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F101a',
  'Einde voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Einde voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Einde voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'علامة F101a',
  'Einde voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Einde voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Einde voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'assets/traffic_signs/information_signs/F101a.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F101b', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F101b',
  'Einde deel van de openbare weg voorbehouden voor fietsers en voetgangers.',
  'Einde deel van de openbare weg voorbehouden voor fietsers en voetgangers.',
  'Einde deel van de openbare weg voorbehouden voor fietsers en voetgangers.',
  'علامة F101b',
  'Einde deel van de openbare weg voorbehouden voor fietsers en voetgangers.',
  'Einde deel van de openbare weg voorbehouden voor fietsers en voetgangers.',
  'Einde deel van de openbare weg voorbehouden voor fietsers en voetgangers.',
  'assets/traffic_signs/information_signs/F101b.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F101c', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F101c',
  'Einde voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Einde voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Einde voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'علامة F101c',
  'Einde voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Einde voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Einde voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'assets/traffic_signs/information_signs/F101c.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F103', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F103',
  'Begin van een voetgangerszone',
  'Begin van een voetgangerszone',
  'Begin van een voetgangerszone',
  'علامة F103',
  'Begin van een voetgangerszone',
  'Begin van een voetgangerszone',
  'Begin van een voetgangerszone',
  'assets/traffic_signs/information_signs/F103.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F105', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F105',
  'Einde van een voetgangerszone',
  'Einde van een voetgangerszone',
  'Einde van een voetgangerszone',
  'علامة F105',
  'Einde van een voetgangerszone',
  'Einde van een voetgangerszone',
  'Einde van een voetgangerszone',
  'assets/traffic_signs/information_signs/F105.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F11', 
  (SELECT id FROM categories WHERE code = 'F'),
  'طريق سيارات',
  'Expressway',
  'Einde van de autoweg.',
  'Route pour automobiles',
  'طريق سيارات',
  'Expressway',
  'Einde van de autoweg.',
  'Route pour automobiles',
  'assets/traffic_signs/information_signs/F11.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F111', 
  (SELECT id FROM categories WHERE code = 'F'),
  'شارع الدراجات',
  'Cycle street',
  'Fietsstraat.',
  'Rue cyclable',
  'شارع الدراجات',
  'Cycle street',
  'Fietsstraat.',
  'Rue cyclable',
  'assets/traffic_signs/information_signs/F111.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F113', 
  (SELECT id FROM categories WHERE code = 'F'),
  'شارع الدراجات',
  'Cycle street',
  'Einde fietsstraat.',
  'Rue cyclable',
  'شارع الدراجات',
  'Cycle street',
  'Einde fietsstraat.',
  'Rue cyclable',
  'assets/traffic_signs/information_signs/F113.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F117', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F117',
  'Begin van een lage emissiezone',
  'Begin van een lage emissiezone',
  'Begin van een lage emissiezone',
  'علامة F117',
  'Begin van een lage emissiezone',
  'Begin van een lage emissiezone',
  'Begin van een lage emissiezone',
  'assets/traffic_signs/information_signs/F117.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F118', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F118',
  'Einde van een lage emissiezone',
  'Einde van een lage emissiezone',
  'Einde van een lage emissiezone',
  'علامة F118',
  'Einde van een lage emissiezone',
  'Einde van een lage emissiezone',
  'Einde van een lage emissiezone',
  'assets/traffic_signs/information_signs/F118.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F12a', 
  (SELECT id FROM categories WHERE code = 'F'),
  'منطقة سكنية',
  'Residential zone',
  'Begin van een woonerf of van een erf.',
  'Zone résidentielle',
  'منطقة سكنية',
  'Residential zone',
  'Begin van een woonerf of van een erf.',
  'Zone résidentielle',
  'assets/traffic_signs/information_signs/F12a.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F12b', 
  (SELECT id FROM categories WHERE code = 'F'),
  'منطقة سكنية',
  'Residential zone',
  'Einde van een woonerf of van een erf.',
  'Zone résidentielle',
  'منطقة سكنية',
  'Residential zone',
  'Einde van een woonerf of van een erf.',
  'Zone résidentielle',
  'assets/traffic_signs/information_signs/F12b.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F13', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F13',
  'Rijstrook keuze.',
  'Rijstrook keuze.',
  'Rijstrook keuze.',
  'علامة F13',
  'Rijstrook keuze.',
  'Rijstrook keuze.',
  'Rijstrook keuze.',
  'assets/traffic_signs/information_signs/F13.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F14', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F14',
  'Opstelvak voor fietsers en bromfietsen.',
  'Opstelvak voor fietsers en bromfietsen.',
  'Opstelvak voor fietsers en bromfietsen.',
  'علامة F14',
  'Opstelvak voor fietsers en bromfietsen.',
  'Opstelvak voor fietsers en bromfietsen.',
  'Opstelvak voor fietsers en bromfietsen.',
  'assets/traffic_signs/information_signs/F14.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F17', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F17',
  'Rijstrook aanduiding voorbehouden voor autobussen.',
  'Rijstrook aanduiding voorbehouden voor autobussen.',
  'Rijstrook aanduiding voorbehouden voor autobussen.',
  'علامة F17',
  'Rijstrook aanduiding voorbehouden voor autobussen.',
  'Rijstrook aanduiding voorbehouden voor autobussen.',
  'Rijstrook aanduiding voorbehouden voor autobussen.',
  'assets/traffic_signs/information_signs/F17.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F18', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F18',
  'Bijzondere overrijdbare bedding.',
  'Bijzondere overrijdbare bedding.',
  'Bijzondere overrijdbare bedding.',
  'علامة F18',
  'Bijzondere overrijdbare bedding.',
  'Bijzondere overrijdbare bedding.',
  'Bijzondere overrijdbare bedding.',
  'assets/traffic_signs/information_signs/F18.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F19', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F19',
  'Eenrichtingsverkeer.',
  'Eenrichtingsverkeer.',
  'Eenrichtingsverkeer.',
  'علامة F19',
  'Eenrichtingsverkeer.',
  'Eenrichtingsverkeer.',
  'Eenrichtingsverkeer.',
  'assets/traffic_signs/information_signs/F19.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F1a', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F1a',
  'Begin van een bebouwde kom.',
  'Begin van een bebouwde kom.',
  'Begin van een bebouwde kom.',
  'علامة F1a',
  'Begin van een bebouwde kom.',
  'Begin van een bebouwde kom.',
  'Begin van een bebouwde kom.',
  'assets/traffic_signs/information_signs/F1a.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F1b', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F1b',
  'Begin van een bebouwde kom.',
  'Begin van een bebouwde kom.',
  'Begin van een bebouwde kom.',
  'علامة F1b',
  'Begin van een bebouwde kom.',
  'Begin van een bebouwde kom.',
  'Begin van een bebouwde kom.',
  'assets/traffic_signs/information_signs/F1b.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F21', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F21',
  'Rechts of links voorbijrijden toegelaten.',
  'Rechts of links voorbijrijden toegelaten.',
  'Rechts of links voorbijrijden toegelaten.',
  'علامة F21',
  'Rechts of links voorbijrijden toegelaten.',
  'Rechts of links voorbijrijden toegelaten.',
  'Rechts of links voorbijrijden toegelaten.',
  'assets/traffic_signs/information_signs/F21.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F23a', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F23a',
  'Nummer van een gewone weg.',
  'Nummer van een gewone weg.',
  'Nummer van een gewone weg.',
  'علامة F23a',
  'Nummer van een gewone weg.',
  'Nummer van een gewone weg.',
  'Nummer van een gewone weg.',
  'assets/traffic_signs/information_signs/F23a.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F23b', 
  (SELECT id FROM categories WHERE code = 'F'),
  'طريق سريع',
  'Motorway',
  'Nummer van een autosnelweg.',
  'Autoroute',
  'طريق سريع',
  'Motorway',
  'Nummer van een autosnelweg.',
  'Autoroute',
  'assets/traffic_signs/information_signs/F23b.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F23c', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F23c',
  'Nummer van een internationale weg.',
  'Nummer van een internationale weg.',
  'Nummer van een internationale weg.',
  'علامة F23c',
  'Nummer van een internationale weg.',
  'Nummer van een internationale weg.',
  'Nummer van een internationale weg.',
  'assets/traffic_signs/information_signs/F23c.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F23d', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F23d',
  'Nummer van een ringweg.',
  'Nummer van een ringweg.',
  'Nummer van een ringweg.',
  'علامة F23d',
  'Nummer van een ringweg.',
  'Nummer van een ringweg.',
  'Nummer van een ringweg.',
  'assets/traffic_signs/information_signs/F23d.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F29', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F29',
  'Wegwijzer',
  'Wegwijzer',
  'Wegwijzer',
  'علامة F29',
  'Wegwijzer',
  'Wegwijzer',
  'Wegwijzer',
  'assets/traffic_signs/information_signs/F29.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F31', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F31',
  'Wegwijzer autostrade',
  'Wegwijzer autostrade',
  'Wegwijzer autostrade',
  'علامة F31',
  'Wegwijzer autostrade',
  'Wegwijzer autostrade',
  'Wegwijzer autostrade',
  'assets/traffic_signs/information_signs/F31.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F33a', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F33a',
  'Bewegwijzeringsbord op afstand',
  'Bewegwijzeringsbord op afstand',
  'Bewegwijzeringsbord op afstand',
  'علامة F33a',
  'Bewegwijzeringsbord op afstand',
  'Bewegwijzeringsbord op afstand',
  'Bewegwijzeringsbord op afstand',
  'assets/traffic_signs/information_signs/F33a.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F33c', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F33c',
  'Bewegwijzeringsbord op afstand',
  'Bewegwijzeringsbord op afstand',
  'Bewegwijzeringsbord op afstand',
  'علامة F33c',
  'Bewegwijzeringsbord op afstand',
  'Bewegwijzeringsbord op afstand',
  'Bewegwijzeringsbord op afstand',
  'assets/traffic_signs/information_signs/F33c.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F34a', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F34a',
  'Nabijheid van inrichting die van openbaar of algemeen belang is.',
  'Nabijheid van inrichting die van openbaar of algemeen belang is.',
  'Nabijheid van inrichting die van openbaar of algemeen belang is.',
  'علامة F34a',
  'Nabijheid van inrichting die van openbaar of algemeen belang is.',
  'Nabijheid van inrichting die van openbaar of algemeen belang is.',
  'Nabijheid van inrichting die van openbaar of algemeen belang is.',
  'assets/traffic_signs/information_signs/F34a.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F34b', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F34b',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  'علامة F34b',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  'assets/traffic_signs/information_signs/F34b.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F34c', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F34c',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  'علامة F34c',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  'assets/traffic_signs/information_signs/F34c.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F35', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F35',
  'Plaats voor toerisme of ontspanning.',
  'Plaats voor toerisme of ontspanning.',
  'Plaats voor toerisme of ontspanning.',
  'علامة F35',
  'Plaats voor toerisme of ontspanning.',
  'Plaats voor toerisme of ontspanning.',
  'Plaats voor toerisme of ontspanning.',
  'assets/traffic_signs/information_signs/F35.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F37', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F37',
  'Wegwijzer naar hotels, campings, restaurant.',
  'Wegwijzer naar hotels, campings, restaurant.',
  'Wegwijzer naar hotels, campings, restaurant.',
  'علامة F37',
  'Wegwijzer naar hotels, campings, restaurant.',
  'Wegwijzer naar hotels, campings, restaurant.',
  'Wegwijzer naar hotels, campings, restaurant.',
  'assets/traffic_signs/information_signs/F37.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F39', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F39',
  'Aankondiging van een omleiding.',
  'Aankondiging van een omleiding.',
  'Aankondiging van een omleiding.',
  'علامة F39',
  'Aankondiging van een omleiding.',
  'Aankondiging van een omleiding.',
  'Aankondiging van een omleiding.',
  'assets/traffic_signs/information_signs/F39.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F3a', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F3a',
  'Einde van een bebouwde kom.',
  'Einde van een bebouwde kom.',
  'Einde van een bebouwde kom.',
  'علامة F3a',
  'Einde van een bebouwde kom.',
  'Einde van een bebouwde kom.',
  'Einde van een bebouwde kom.',
  'assets/traffic_signs/information_signs/F3a.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F3b', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F3b',
  'Einde van een bebouwde kom.',
  'Einde van een bebouwde kom.',
  'Einde van een bebouwde kom.',
  'علامة F3b',
  'Einde van een bebouwde kom.',
  'Einde van een bebouwde kom.',
  'Einde van een bebouwde kom.',
  'assets/traffic_signs/information_signs/F3b.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F41', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F41',
  'Wegwijzer omleidingsweg',
  'Wegwijzer omleidingsweg',
  'Wegwijzer omleidingsweg',
  'علامة F41',
  'Wegwijzer omleidingsweg',
  'Wegwijzer omleidingsweg',
  'Wegwijzer omleidingsweg',
  'assets/traffic_signs/information_signs/F41.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F43', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F43',
  'Gemeentegrens',
  'Gemeentegrens',
  'Gemeentegrens',
  'علامة F43',
  'Gemeentegrens',
  'Gemeentegrens',
  'Gemeentegrens',
  'assets/traffic_signs/information_signs/F43.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F45', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F45',
  'Doodlopende weg, rechtse doorgang.',
  'Doodlopende weg, rechtse doorgang.',
  'Doodlopende weg, rechtse doorgang.',
  'علامة F45',
  'Doodlopende weg, rechtse doorgang.',
  'Doodlopende weg, rechtse doorgang.',
  'Doodlopende weg, rechtse doorgang.',
  'assets/traffic_signs/information_signs/F45.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F45b', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F45b',
  'Doodlopende weg, uitgezonderd voetgangers en fietsers.',
  'Doodlopende weg, uitgezonderd voetgangers en fietsers.',
  'Doodlopende weg, uitgezonderd voetgangers en fietsers.',
  'علامة F45b',
  'Doodlopende weg, uitgezonderd voetgangers en fietsers.',
  'Doodlopende weg, uitgezonderd voetgangers en fietsers.',
  'Doodlopende weg, uitgezonderd voetgangers en fietsers.',
  'assets/traffic_signs/information_signs/F45b.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F47', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F47',
  'Einde van de werken.',
  'Einde van de werken.',
  'Einde van de werken.',
  'علامة F47',
  'Einde van de werken.',
  'Einde van de werken.',
  'Einde van de werken.',
  'assets/traffic_signs/information_signs/F47.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F49', 
  (SELECT id FROM categories WHERE code = 'F'),
  'معبر للمشاة',
  'Pedestrian crossing',
  'Oversteekplaats voor voetgangers.',
  'Passage pour piétons',
  'معبر للمشاة',
  'Pedestrian crossing',
  'Oversteekplaats voor voetgangers.',
  'Passage pour piétons',
  'assets/traffic_signs/information_signs/F49.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F4a', 
  (SELECT id FROM categories WHERE code = 'F'),
  'منطقة 30',
  'Zone 30',
  'Zone 30 km/u.',
  'Zone 30',
  'منطقة 30',
  'Zone 30',
  'Zone 30 km/u.',
  'Zone 30',
  'assets/traffic_signs/information_signs/F4a.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F4b', 
  (SELECT id FROM categories WHERE code = 'F'),
  'منطقة 30',
  'Zone 30',
  'Einde zone 30 km/u.',
  'Zone 30',
  'منطقة 30',
  'Zone 30',
  'Einde zone 30 km/u.',
  'Zone 30',
  'assets/traffic_signs/information_signs/F4b.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F5', 
  (SELECT id FROM categories WHERE code = 'F'),
  'طريق سريع',
  'Motorway',
  'Autosnelweg.',
  'Autoroute',
  'طريق سريع',
  'Motorway',
  'Autosnelweg.',
  'Autoroute',
  'assets/traffic_signs/information_signs/F5.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F50', 
  (SELECT id FROM categories WHERE code = 'F'),
  'دراجات ودراجات نارية',
  'Cyclists and moped riders',
  'Oversteekplaats voor fietsers en bromfietsers.',
  'Cyclistes et cyclomotoristes',
  'دراجات ودراجات نارية',
  'Cyclists and moped riders',
  'Oversteekplaats voor fietsers en bromfietsers.',
  'Cyclistes et cyclomotoristes',
  'assets/traffic_signs/information_signs/F50.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F50b', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F50b',
  'Opgepast als je van richting veranderd, fietsers.',
  'Opgepast als je van richting veranderd, fietsers.',
  'Opgepast als je van richting veranderd, fietsers.',
  'علامة F50b',
  'Opgepast als je van richting veranderd, fietsers.',
  'Opgepast als je van richting veranderd, fietsers.',
  'Opgepast als je van richting veranderd, fietsers.',
  'assets/traffic_signs/information_signs/F50b.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F53', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F53',
  'Verplegingsinrichting.',
  'Verplegingsinrichting.',
  'Verplegingsinrichting.',
  'علامة F53',
  'Verplegingsinrichting.',
  'Verplegingsinrichting.',
  'Verplegingsinrichting.',
  'assets/traffic_signs/information_signs/F53.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F55', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F55',
  'Hulppost.',
  'Hulppost.',
  'Hulppost.',
  'علامة F55',
  'Hulppost.',
  'Hulppost.',
  'Hulppost.',
  'assets/traffic_signs/information_signs/F55.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F56', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F56',
  'Brandblusapparaat.',
  'Brandblusapparaat.',
  'Brandblusapparaat.',
  'علامة F56',
  'Brandblusapparaat.',
  'Brandblusapparaat.',
  'Brandblusapparaat.',
  'assets/traffic_signs/information_signs/F56.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F59', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F59',
  'Aankondiging van een parking.',
  'Aankondiging van een parking.',
  'Aankondiging van een parking.',
  'علامة F59',
  'Aankondiging van een parking.',
  'Aankondiging van een parking.',
  'Aankondiging van een parking.',
  'assets/traffic_signs/information_signs/F59.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F59a', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F59a',
  'Aankondiging van een parking.',
  'Aankondiging van een parking.',
  'Aankondiging van een parking.',
  'علامة F59a',
  'Aankondiging van een parking.',
  'Aankondiging van een parking.',
  'Aankondiging van een parking.',
  'assets/traffic_signs/information_signs/F59a.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F59b', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F59b',
  'Aankondiging van een fietsparking.',
  'Aankondiging van een fietsparking.',
  'Aankondiging van een fietsparking.',
  'علامة F59b',
  'Aankondiging van een fietsparking.',
  'Aankondiging van een fietsparking.',
  'Aankondiging van een fietsparking.',
  'assets/traffic_signs/information_signs/F59b.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F60', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F60',
  'Overdekte parking.',
  'Overdekte parking.',
  'Overdekte parking.',
  'علامة F60',
  'Overdekte parking.',
  'Overdekte parking.',
  'Overdekte parking.',
  'assets/traffic_signs/information_signs/F60.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F61', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F61',
  'Telefoon.',
  'Telefoon.',
  'Telefoon.',
  'علامة F61',
  'Telefoon.',
  'Telefoon.',
  'Telefoon.',
  'assets/traffic_signs/information_signs/F61.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F62', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F62',
  'Noodtelefoon.',
  'Noodtelefoon.',
  'Noodtelefoon.',
  'علامة F62',
  'Noodtelefoon.',
  'Noodtelefoon.',
  'Noodtelefoon.',
  'assets/traffic_signs/information_signs/F62.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F63', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F63',
  '- Specifieke brandstof Tankstation met een specifieke brandstof.',
  '- Specifieke brandstof Tankstation met een specifieke brandstof.',
  '- Specifieke brandstof Tankstation met een specifieke brandstof.',
  'علامة F63',
  '- Specifieke brandstof Tankstation met een specifieke brandstof.',
  '- Specifieke brandstof Tankstation met een specifieke brandstof.',
  '- Specifieke brandstof Tankstation met een specifieke brandstof.',
  'assets/traffic_signs/information_signs/F63.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F65', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F65',
  'Hotel of motel.',
  'Hotel of motel.',
  'Hotel of motel.',
  'علامة F65',
  'Hotel of motel.',
  'Hotel of motel.',
  'Hotel of motel.',
  'assets/traffic_signs/information_signs/F65.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F67', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F67',
  'Restaurant.',
  'Restaurant.',
  'Restaurant.',
  'علامة F67',
  'Restaurant.',
  'Restaurant.',
  'Restaurant.',
  'assets/traffic_signs/information_signs/F67.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F69', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F69',
  'Drankgelegenheid.',
  'Drankgelegenheid.',
  'Drankgelegenheid.',
  'علامة F69',
  'Drankgelegenheid.',
  'Drankgelegenheid.',
  'Drankgelegenheid.',
  'assets/traffic_signs/information_signs/F69.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F7', 
  (SELECT id FROM categories WHERE code = 'F'),
  'طريق سريع',
  'Motorway',
  'Einde autosnelweg.',
  'Autoroute',
  'طريق سريع',
  'Motorway',
  'Einde autosnelweg.',
  'Autoroute',
  'assets/traffic_signs/information_signs/F7.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F71', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F71',
  'Kampeerterrein.',
  'Kampeerterrein.',
  'Kampeerterrein.',
  'علامة F71',
  'Kampeerterrein.',
  'Kampeerterrein.',
  'Kampeerterrein.',
  'assets/traffic_signs/information_signs/F71.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F73', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F73',
  'Caravanterrein.',
  'Caravanterrein.',
  'Caravanterrein.',
  'علامة F73',
  'Caravanterrein.',
  'Caravanterrein.',
  'Caravanterrein.',
  'assets/traffic_signs/information_signs/F73.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F75', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F75',
  'Jeugdherberg.',
  'Jeugdherberg.',
  'Jeugdherberg.',
  'علامة F75',
  'Jeugdherberg.',
  'Jeugdherberg.',
  'Jeugdherberg.',
  'assets/traffic_signs/information_signs/F75.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F77', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F77',
  'Toeristische informatie.',
  'Toeristische informatie.',
  'Toeristische informatie.',
  'علامة F77',
  'Toeristische informatie.',
  'Toeristische informatie.',
  'Toeristische informatie.',
  'assets/traffic_signs/information_signs/F77.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F8', 
  (SELECT id FROM categories WHERE code = 'F'),
  'نفق',
  'Tunnel',
  'Tunnel.',
  'Tunnel',
  'نفق',
  'Tunnel',
  'Tunnel.',
  'Tunnel',
  'assets/traffic_signs/information_signs/F8.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F87', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F87',
  'Verhoogde inrichting (vluchtheuvel).',
  'Verhoogde inrichting (vluchtheuvel).',
  'Verhoogde inrichting (vluchtheuvel).',
  'علامة F87',
  'Verhoogde inrichting (vluchtheuvel).',
  'Verhoogde inrichting (vluchtheuvel).',
  'Verhoogde inrichting (vluchtheuvel).',
  'assets/traffic_signs/information_signs/F87.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F9', 
  (SELECT id FROM categories WHERE code = 'F'),
  'طريق سيارات',
  'Expressway',
  'Autoweg.',
  'Route pour automobiles',
  'طريق سيارات',
  'Expressway',
  'Autoweg.',
  'Route pour automobiles',
  'assets/traffic_signs/information_signs/F9.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F97', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F97',
  'Rijstrook versmalling.',
  'Rijstrook versmalling.',
  'Rijstrook versmalling.',
  'علامة F97',
  'Rijstrook versmalling.',
  'Rijstrook versmalling.',
  'Rijstrook versmalling.',
  'assets/traffic_signs/information_signs/F97.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F99a', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F99a',
  'Voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'علامة F99a',
  'Voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'assets/traffic_signs/information_signs/F99a.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F99b', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F99b',
  'Deel van de openbare weg voorbehouden voor fietsers en voetgangers',
  'Deel van de openbare weg voorbehouden voor fietsers en voetgangers',
  'Deel van de openbare weg voorbehouden voor fietsers en voetgangers',
  'علامة F99b',
  'Deel van de openbare weg voorbehouden voor fietsers en voetgangers',
  'Deel van de openbare weg voorbehouden voor fietsers en voetgangers',
  'Deel van de openbare weg voorbehouden voor fietsers en voetgangers',
  'assets/traffic_signs/information_signs/F99b.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'F99c', 
  (SELECT id FROM categories WHERE code = 'F'),
  'علامة F99c',
  'Voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'علامة F99c',
  'Voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'assets/traffic_signs/information_signs/F99c.png',
  TRUE,
  NOW(),
  NOW()
);


-- Category M: لوحات الدراجات

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'M1', 
  (SELECT id FROM categories WHERE code = 'M'),
  'علامة M1',
  'Enkel voor fietsers.',
  'Enkel voor fietsers.',
  'Enkel voor fietsers.',
  'علامة M1',
  'Enkel voor fietsers.',
  'Enkel voor fietsers.',
  'Enkel voor fietsers.',
  'assets/traffic_signs/bicycle_signs/M1.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'M10', 
  (SELECT id FROM categories WHERE code = 'M'),
  'علامة M10',
  'Fietsers en bromfietser in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  'Fietsers en bromfietser in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  'Fietsers en bromfietser in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  'علامة M10',
  'Fietsers en bromfietser in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  'Fietsers en bromfietser in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  'Fietsers en bromfietser in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  'assets/traffic_signs/bicycle_signs/M10.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'M11', 
  (SELECT id FROM categories WHERE code = 'M'),
  'علامة M11',
  'Uitgezonderd fietsers en speed pedelecs.',
  'Uitgezonderd fietsers en speed pedelecs.',
  'Uitgezonderd fietsers en speed pedelecs.',
  'علامة M11',
  'Uitgezonderd fietsers en speed pedelecs.',
  'Uitgezonderd fietsers en speed pedelecs.',
  'Uitgezonderd fietsers en speed pedelecs.',
  'assets/traffic_signs/bicycle_signs/M11.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'M12', 
  (SELECT id FROM categories WHERE code = 'M'),
  'علامة M12',
  'Uitgezonderd fietsers, bromfietsers klasse A en speed pedelecs.',
  'Uitgezonderd fietsers, bromfietsers klasse A en speed pedelecs.',
  'Uitgezonderd fietsers, bromfietsers klasse A en speed pedelecs.',
  'علامة M12',
  'Uitgezonderd fietsers, bromfietsers klasse A en speed pedelecs.',
  'Uitgezonderd fietsers, bromfietsers klasse A en speed pedelecs.',
  'Uitgezonderd fietsers, bromfietsers klasse A en speed pedelecs.',
  'assets/traffic_signs/bicycle_signs/M12.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'M13', 
  (SELECT id FROM categories WHERE code = 'M'),
  'علامة M13',
  'Verplichting voor speed pedelecs.',
  'Verplichting voor speed pedelecs.',
  'Verplichting voor speed pedelecs.',
  'علامة M13',
  'Verplichting voor speed pedelecs.',
  'Verplichting voor speed pedelecs.',
  'Verplichting voor speed pedelecs.',
  'assets/traffic_signs/bicycle_signs/M13.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'M14', 
  (SELECT id FROM categories WHERE code = 'M'),
  'علامة M14',
  'Verplichting voor bromfietsen klasse B en Speed pedelecs.',
  'Verplichting voor bromfietsen klasse B en Speed pedelecs.',
  'Verplichting voor bromfietsen klasse B en Speed pedelecs.',
  'علامة M14',
  'Verplichting voor bromfietsen klasse B en Speed pedelecs.',
  'Verplichting voor bromfietsen klasse B en Speed pedelecs.',
  'Verplichting voor bromfietsen klasse B en Speed pedelecs.',
  'assets/traffic_signs/bicycle_signs/M14.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'M15', 
  (SELECT id FROM categories WHERE code = 'M'),
  'علامة M15',
  'Verbod voor speed pedelecs.',
  'Verbod voor speed pedelecs.',
  'Verbod voor speed pedelecs.',
  'علامة M15',
  'Verbod voor speed pedelecs.',
  'Verbod voor speed pedelecs.',
  'Verbod voor speed pedelecs.',
  'assets/traffic_signs/bicycle_signs/M15.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'M16', 
  (SELECT id FROM categories WHERE code = 'M'),
  'علامة M16',
  'Verbod voor bromfietsen klasse B en speed pedelecs.',
  'Verbod voor bromfietsen klasse B en speed pedelecs.',
  'Verbod voor bromfietsen klasse B en speed pedelecs.',
  'علامة M16',
  'Verbod voor bromfietsen klasse B en speed pedelecs.',
  'Verbod voor bromfietsen klasse B en speed pedelecs.',
  'Verbod voor bromfietsen klasse B en speed pedelecs.',
  'assets/traffic_signs/bicycle_signs/M16.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'M17', 
  (SELECT id FROM categories WHERE code = 'M'),
  'علامة M17',
  'Fietsers en speed pedelecs mogen in 2 richtingen.',
  'Fietsers en speed pedelecs mogen in 2 richtingen.',
  'Fietsers en speed pedelecs mogen in 2 richtingen.',
  'علامة M17',
  'Fietsers en speed pedelecs mogen in 2 richtingen.',
  'Fietsers en speed pedelecs mogen in 2 richtingen.',
  'Fietsers en speed pedelecs mogen in 2 richtingen.',
  'assets/traffic_signs/bicycle_signs/M17.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'M18', 
  (SELECT id FROM categories WHERE code = 'M'),
  'علامة M18',
  'Fietsers, bromfietsen klasse A en speed pedelecs mogen in 2 richtingen.',
  'Fietsers, bromfietsen klasse A en speed pedelecs mogen in 2 richtingen.',
  'Fietsers, bromfietsen klasse A en speed pedelecs mogen in 2 richtingen.',
  'علامة M18',
  'Fietsers, bromfietsen klasse A en speed pedelecs mogen in 2 richtingen.',
  'Fietsers, bromfietsen klasse A en speed pedelecs mogen in 2 richtingen.',
  'Fietsers, bromfietsen klasse A en speed pedelecs mogen in 2 richtingen.',
  'assets/traffic_signs/bicycle_signs/M18.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'M19', 
  (SELECT id FROM categories WHERE code = 'M'),
  'علامة M19',
  'Enkel voor speed pedelecs.',
  'Enkel voor speed pedelecs.',
  'Enkel voor speed pedelecs.',
  'علامة M19',
  'Enkel voor speed pedelecs.',
  'Enkel voor speed pedelecs.',
  'Enkel voor speed pedelecs.',
  'assets/traffic_signs/bicycle_signs/M19.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'M2', 
  (SELECT id FROM categories WHERE code = 'M'),
  'علامة M2',
  'Uitgezonderd fietsers.',
  'Uitgezonderd fietsers.',
  'Uitgezonderd fietsers.',
  'علامة M2',
  'Uitgezonderd fietsers.',
  'Uitgezonderd fietsers.',
  'Uitgezonderd fietsers.',
  'assets/traffic_signs/bicycle_signs/M2.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'M20', 
  (SELECT id FROM categories WHERE code = 'M'),
  'علامة M20',
  'Enkel voor fietsers en speed pedelecs.',
  'Enkel voor fietsers en speed pedelecs.',
  'Enkel voor fietsers en speed pedelecs.',
  'علامة M20',
  'Enkel voor fietsers en speed pedelecs.',
  'Enkel voor fietsers en speed pedelecs.',
  'Enkel voor fietsers en speed pedelecs.',
  'assets/traffic_signs/bicycle_signs/M20.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'M3', 
  (SELECT id FROM categories WHERE code = 'M'),
  'دراجات ودراجات نارية',
  'Cyclists and moped riders',
  'Uitgezonderd fietsers en bromfietsers klasse A.',
  'Cyclistes et cyclomotoristes',
  'دراجات ودراجات نارية',
  'Cyclists and moped riders',
  'Uitgezonderd fietsers en bromfietsers klasse A.',
  'Cyclistes et cyclomotoristes',
  'assets/traffic_signs/bicycle_signs/M3.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'M3b', 
  (SELECT id FROM categories WHERE code = 'M'),
  'دراجات ودراجات نارية',
  'Cyclists and moped riders',
  'Uitgezonderd fietsers en bromfietsers.',
  'Cyclistes et cyclomotoristes',
  'دراجات ودراجات نارية',
  'Cyclists and moped riders',
  'Uitgezonderd fietsers en bromfietsers.',
  'Cyclistes et cyclomotoristes',
  'assets/traffic_signs/bicycle_signs/M3b.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'M4', 
  (SELECT id FROM categories WHERE code = 'M'),
  'علامة M4',
  'Fietsers mogen in 2 richtingen.',
  'Fietsers mogen in 2 richtingen.',
  'Fietsers mogen in 2 richtingen.',
  'علامة M4',
  'Fietsers mogen in 2 richtingen.',
  'Fietsers mogen in 2 richtingen.',
  'Fietsers mogen in 2 richtingen.',
  'assets/traffic_signs/bicycle_signs/M4.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'M5', 
  (SELECT id FROM categories WHERE code = 'M'),
  'دراجات ودراجات نارية',
  'Cyclists and moped riders',
  'Fietsers en bromfietsers Klasse A mogen in 2 richtingen.',
  'Cyclistes et cyclomotoristes',
  'دراجات ودراجات نارية',
  'Cyclists and moped riders',
  'Fietsers en bromfietsers Klasse A mogen in 2 richtingen.',
  'Cyclistes et cyclomotoristes',
  'assets/traffic_signs/bicycle_signs/M5.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'M5b', 
  (SELECT id FROM categories WHERE code = 'M'),
  'علامة M5b',
  'fietsers, bromfietsers klasse A, B en speed pedelecs mogen in 2 richtingen.',
  'fietsers, bromfietsers klasse A, B en speed pedelecs mogen in 2 richtingen.',
  'fietsers, bromfietsers klasse A, B en speed pedelecs mogen in 2 richtingen.',
  'علامة M5b',
  'fietsers, bromfietsers klasse A, B en speed pedelecs mogen in 2 richtingen.',
  'fietsers, bromfietsers klasse A, B en speed pedelecs mogen in 2 richtingen.',
  'fietsers, bromfietsers klasse A, B en speed pedelecs mogen in 2 richtingen.',
  'assets/traffic_signs/bicycle_signs/M5b.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'M6', 
  (SELECT id FROM categories WHERE code = 'M'),
  'علامة M6',
  'Verplichting voor bromfietsen klasse B.',
  'Verplichting voor bromfietsen klasse B.',
  'Verplichting voor bromfietsen klasse B.',
  'علامة M6',
  'Verplichting voor bromfietsen klasse B.',
  'Verplichting voor bromfietsen klasse B.',
  'Verplichting voor bromfietsen klasse B.',
  'assets/traffic_signs/bicycle_signs/M6.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'M7', 
  (SELECT id FROM categories WHERE code = 'M'),
  'علامة M7',
  'Verbod voor bromfietsen klasse B.',
  'Verbod voor bromfietsen klasse B.',
  'Verbod voor bromfietsen klasse B.',
  'علامة M7',
  'Verbod voor bromfietsen klasse B.',
  'Verbod voor bromfietsen klasse B.',
  'Verbod voor bromfietsen klasse B.',
  'assets/traffic_signs/bicycle_signs/M7.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'M8', 
  (SELECT id FROM categories WHERE code = 'M'),
  'دراجات ودراجات نارية',
  'Cyclists and moped riders',
  'Enkel voor fietsers en bromfietsers.',
  'Cyclistes et cyclomotoristes',
  'دراجات ودراجات نارية',
  'Cyclists and moped riders',
  'Enkel voor fietsers en bromfietsers.',
  'Cyclistes et cyclomotoristes',
  'assets/traffic_signs/bicycle_signs/M8.png',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO traffic_signs (sign_code, category_id, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, image_url, is_active, created_at, updated_at) VALUES (
  'M9', 
  (SELECT id FROM categories WHERE code = 'M'),
  'علامة M9',
  'Fietsers in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  'Fietsers in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  'Fietsers in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  'علامة M9',
  'Fietsers in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  'Fietsers in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  'Fietsers in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  'assets/traffic_signs/bicycle_signs/M9.png',
  TRUE,
  NOW(),
  NOW()
);




