-- V176: Standardise sign codes — remove duplicate variants, rename and insert canonical codes
-- Deletes: B15A-v1, B15A-v2, C43_50, C43_70, D1a-links, D1a-rechts,
--          D1b-left, D1b-rechts, D1b-right, D4-left, D4-right, D4-straight
-- Updates: C22a -> C22, F50bis-pedestrians -> F50bis
-- Inserts: C41, D1b, D4, F97, F117, F118

-- ============================================================
-- 1. DELETE redundant variant signs (no quiz/detail dependencies)
-- ============================================================
DELETE FROM traffic_signs
WHERE sign_code IN (
    'B15A-v1',
    'B15A-v2',
    'C43_50',
    'C43_70',
    'D1a-links',
    'D1a-rechts',
    'D1b-left',
    'D1b-rechts',
    'D1b-right',
    'D4-left',
    'D4-right',
    'D4-straight'
);

-- ============================================================
-- 2. RENAME C22a -> C22 (autocars, not autobussen)
-- ============================================================
UPDATE traffic_signs
SET sign_code             = 'C22',
    normalized_sign_code  = 'c22',
    name_nl               = 'Verboden toegang voor bestuurders van autocars',
    name_en               = 'No entry for coaches',
    updated_at            = NOW()
WHERE sign_code = 'C22a';

-- ============================================================
-- 3. RENAME F50bis-pedestrians -> F50bis (keep existing image_url)
-- ============================================================
UPDATE traffic_signs
SET sign_code             = 'F50bis',
    normalized_sign_code  = 'f50bis',
    updated_at            = NOW()
WHERE sign_code = 'F50bis-pedestrians';

-- ============================================================
-- 4. INSERT new signs
-- ============================================================

-- C41: End of prohibition imposed by sign C39
INSERT IGNORE INTO traffic_signs
    (category_id, sign_code, normalized_sign_code,
     name_nl, name_en, name_fr, name_ar,
     description_nl, description_en, description_fr, description_ar,
     image_url, image_path, is_active, created_at, updated_at,
     long_description_nl, long_description_en, long_description_fr, long_description_ar)
VALUES (
    (SELECT id FROM categories WHERE code = 'C'),
    'C41', 'c41',
    'Einde van het verbod opgelegd door het verkeersbord C39',
    'End of prohibition imposed by sign C39',
    'Fin de l''interdiction imposee par le panneau C39',
    'نهاية الحظر المفروض بموجب العلامة C39',
    'Einde van het verbod om voertuigen met toegelaten massa groter dan 3500 kg in te halen.',
    'End of no overtaking zone for vehicles over 3,500 kg (C39).',
    'Fin de l''interdiction de depasser pour les vehicules de plus de 3 500 kg (C39).',
    'نهاية منطقة حظر تجاوز المركبات التي تتجاوز كتلتها 3500 كجم (C39).',
    'images/signs/prohibition_signs/C41 Einde van het verbod opgelegd door het verkeersbord C39.png',
    'images/signs/prohibition_signs/C41 Einde van het verbod opgelegd door het verkeersbord C39.png',
    1, NOW(), NOW(),
    'Dit verkeersbord geeft het einde aan van het inhaalverbod voor voertuigen met een toegelaten massa van meer dan 3500 kg dat werd opgelegd door bord C39. Vanaf hier mogen zware voertuigen weer inhalen, mits de verkeerssituatie dit veilig toelaat.',
    'This traffic sign indicates the end of the no overtaking prohibition for vehicles over 3,500 kg imposed by sign C39. From this point heavy vehicles may overtake again, provided the traffic situation allows this safely.',
    'Ce panneau indique la fin de l''interdiction de depasser pour les vehicules de plus de 3 500 kg imposee par le panneau C39. A partir de ce point les poids lourds peuvent a nouveau depasser, a condition que la situation de circulation le permette en toute securite.',
    'تشير هذه العلامة إلى نهاية حظر التجاوز للمركبات التي تتجاوز كتلتها 3500 كجم والمفروض بموجب العلامة C39. من هذه النقطة يُسمح للمركبات الثقيلة بالتجاوز مجدداً شريطة أن تسمح حالة المرور بذلك بأمان.'
);

-- D1b: Mandatory turn (left or right, indicated by arrow)
INSERT IGNORE INTO traffic_signs
    (category_id, sign_code, normalized_sign_code,
     name_nl, name_en, name_fr, name_ar,
     description_nl, description_en, description_fr, description_ar,
     image_url, image_path, is_active, created_at, updated_at,
     long_description_nl, long_description_en, long_description_fr, long_description_ar)
VALUES (
    (SELECT id FROM categories WHERE code = 'D'),
    'D1b', 'd1b',
    'Verplichting links afslaan',
    'Mandatory left turn',
    'Obligation de tourner a gauche',
    'إلزامية الانعطاف يساراً',
    'Verplichting de door de pijl aangeduide richting te volgen.',
    'Obligation to follow the direction indicated by the arrow.',
    'Obligation de suivre la direction indiquee par la fleche.',
    'وجوب اتباع الاتجاه المحدد بالسهم.',
    'images/signs/mandatory_signs/D1b Verplichting links afslaan.png',
    'images/signs/mandatory_signs/D1b Verplichting links afslaan.png',
    1, NOW(), NOW(),
    'Dit verkeersbord verplicht bestuurders om de door de pijl aangeduide richting te volgen. Het bord is bindend voor alle bestuurders die het passeren.',
    'This traffic sign requires drivers to follow the direction indicated by the arrow. The sign is binding for all drivers who pass it.',
    'Ce panneau oblige les conducteurs a suivre la direction indiquee par la fleche. Le panneau est contraignant pour tous les conducteurs qui le depassent.',
    'تلزم هذه العلامة المرورية السائقين باتباع الاتجاه المشار إليه بالسهم. العلامة ملزمة لجميع السائقين الذين يمرون بها.'
);

-- D4: Mandatory direction for vehicles carrying dangerous goods
INSERT IGNORE INTO traffic_signs
    (category_id, sign_code, normalized_sign_code,
     name_nl, name_en, name_fr, name_ar,
     description_nl, description_en, description_fr, description_ar,
     image_url, image_path, is_active, created_at, updated_at,
     long_description_nl, long_description_en, long_description_fr, long_description_ar)
VALUES (
    (SELECT id FROM categories WHERE code = 'D'),
    'D4', 'd4',
    'Verplicht rechts voor voertuigen die gevaarlijke goederen vervoeren',
    'Mandatory right turn for vehicles carrying dangerous goods',
    'Obligation de tourner a droite pour les vehicules transportant des marchandises dangereuses',
    'إلزامية الانعطاف يميناً للمركبات الناقلة للبضائع الخطرة',
    'Verplicht rechts voor voertuigen die gevaarlijke goederen vervoeren.',
    'Mandatory right turn for vehicles carrying dangerous goods.',
    'Obligation de tourner a droite pour les vehicules transportant des marchandises dangereuses.',
    'إلزامية الانعطاف يميناً للمركبات الناقلة للبضائع الخطرة.',
    'images/signs/mandatory_signs/D4 Verplicht rechts voor voertuigen die gevaarlijke goederen vervoeren.png',
    'images/signs/mandatory_signs/D4 Verplicht rechts voor voertuigen die gevaarlijke goederen vervoeren.png',
    1, NOW(), NOW(),
    'Dit verkeersbord verplicht voertuigen die gevaarlijke goederen vervoeren om de aangeduide richting te volgen. Het wordt gebruikt om voertuigen met gevaarlijke lading weg te leiden van tunnels, bewoonde gebieden of andere gevoelige locaties.',
    'This traffic sign requires vehicles carrying dangerous goods to follow the indicated direction. It is used to route vehicles with hazardous cargo away from tunnels, populated areas or other sensitive locations.',
    'Ce panneau oblige les vehicules transportant des marchandises dangereuses a suivre la direction indiquee. Il est utilise pour detourner les vehicules transportant des chargements dangereux des tunnels, des zones habitees ou d''autres endroits sensibles.',
    'تُلزم هذه العلامة المركبات الناقلة للبضائع الخطرة باتباع الاتجاه المشار إليه. وتُستخدم لتوجيه المركبات ذات الحمولات الخطرة بعيداً عن الأنفاق والمناطق السكنية والمواقع الحساسة الأخرى.'
);

-- F97: Lane narrowing
INSERT IGNORE INTO traffic_signs
    (category_id, sign_code, normalized_sign_code,
     name_nl, name_en, name_fr, name_ar,
     description_nl, description_en, description_fr, description_ar,
     image_url, image_path, is_active, created_at, updated_at,
     long_description_nl, long_description_en, long_description_fr, long_description_ar)
VALUES (
    (SELECT id FROM categories WHERE code = 'F'),
    'F97', 'f97',
    'Rijstrook versmalling',
    'Lane narrowing',
    'Retrecissement de voie',
    'تضييق مسار السير',
    'Aanduiding van een rijstrookversmalling.',
    'Indication of a lane narrowing.',
    'Indication d''un retrecissement de voie.',
    'إشارة إلى تضييق مسار السير.',
    'images/signs/information_signs/F97 Rijstrook versmalling.png',
    'images/signs/information_signs/F97 Rijstrook versmalling.png',
    1, NOW(), NOW(),
    'Dit verkeersbord duidt aan dat een rijstrook smaller wordt. Bestuurders moeten voorzichtig rijden en de rijstrookuitzetting respecteren.',
    'This traffic sign indicates that a lane is narrowing. Drivers must proceed with caution and respect the lane configuration.',
    'Ce panneau indique qu''une voie de circulation se retrecit. Les conducteurs doivent rouler avec prudence et respecter la configuration des voies.',
    'تشير هذه العلامة إلى تضييق مسار السير. يجب على السائقين توخي الحذر واحترام ترتيب المسارات.'
);

-- F117: Start of low emission zone
INSERT IGNORE INTO traffic_signs
    (category_id, sign_code, normalized_sign_code,
     name_nl, name_en, name_fr, name_ar,
     description_nl, description_en, description_fr, description_ar,
     image_url, image_path, is_active, created_at, updated_at,
     long_description_nl, long_description_en, long_description_fr, long_description_ar)
VALUES (
    (SELECT id FROM categories WHERE code = 'F'),
    'F117', 'f117',
    'Begin van lage emissiezone',
    'Start of low emission zone',
    'Debut de zone a basses emissions',
    'بداية منطقة الانبعاثات المنخفضة',
    'Aanduiding van het begin van een lage-emissiezone.',
    'Indication of the start of a low emission zone.',
    'Indication du debut d''une zone a basses emissions.',
    'إشارة إلى بداية منطقة الانبعاثات المنخفضة.',
    'images/signs/information_signs/F117 Begin van lage emissiezone.png',
    'images/signs/information_signs/F117 Begin van lage emissiezone.png',
    1, NOW(), NOW(),
    'Dit verkeersbord markeert het begin van een lage-emissiezone (LEZ). Voertuigen die niet voldoen aan de geldende emissienormen mogen deze zone niet binnenrijden zonder een geldige vergunning.',
    'This traffic sign marks the beginning of a low emission zone (LEZ). Vehicles that do not meet the applicable emission standards may not enter this zone without a valid permit.',
    'Ce panneau marque le debut d''une zone a basses emissions (ZBE). Les vehicules ne respectant pas les normes d''emission en vigueur ne sont pas autorises a entrer dans cette zone sans permis valide.',
    'تُحدد هذه العلامة بداية منطقة الانبعاثات المنخفضة. لا يُسمح للمركبات التي لا تستوفي معايير الانبعاثات المعمول بها بدخول هذه المنطقة دون تصريح ساري.'
);

-- F118: End of low emission zone
INSERT IGNORE INTO traffic_signs
    (category_id, sign_code, normalized_sign_code,
     name_nl, name_en, name_fr, name_ar,
     description_nl, description_en, description_fr, description_ar,
     image_url, image_path, is_active, created_at, updated_at,
     long_description_nl, long_description_en, long_description_fr, long_description_ar)
VALUES (
    (SELECT id FROM categories WHERE code = 'F'),
    'F118', 'f118',
    'Einde van lage emissiezone',
    'End of low emission zone',
    'Fin de zone a basses emissions',
    'نهاية منطقة الانبعاثات المنخفضة',
    'Aanduiding van het einde van een lage-emissiezone.',
    'Indication of the end of a low emission zone.',
    'Indication de la fin d''une zone a basses emissions.',
    'إشارة إلى نهاية منطقة الانبعاثات المنخفضة.',
    'images/signs/information_signs/F118 Einde van lage emissiezone.png',
    'images/signs/information_signs/F118 Einde van lage emissiezone.png',
    1, NOW(), NOW(),
    'Dit verkeersbord markeert het einde van een lage-emissiezone (LEZ). Vanaf dit punt zijn de emissiebeperkingen van de lage-emissiezone niet meer van toepassing.',
    'This traffic sign marks the end of a low emission zone (LEZ). From this point the emission restrictions of the low emission zone no longer apply.',
    'Ce panneau marque la fin d''une zone a basses emissions (ZBE). A partir de ce point les restrictions d''emissions de la zone a basses emissions ne s''appliquent plus.',
    'تُحدد هذه العلامة نهاية منطقة الانبعاثات المنخفضة. من هذه النقطة لا تسري قيود الانبعاثات الخاصة بمنطقة الانبعاثات المنخفضة بعد الآن.'
);
