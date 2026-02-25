-- ============================================================================
-- V89: Seed canonical long descriptions for F47, G-series, and M-series signs
-- ============================================================================
-- Canonical source: readyroad/data/signs.json
-- Signs: F47, GIb, GIII-ijzel, GIV, GIX, M2, M7, M12-richtingen,
--         M12-fiets-brom, M13, M15
-- ============================================================================

-- F47
UPDATE traffic_signs SET
  long_description_en = 'End van de Road works.',
  long_description_nl = 'Einde van de werken.',
  long_description_fr = 'Fin van de Travaux.',
  long_description_ar = 'أعمال نهاية فان دي رود.',
  updated_at = NOW()
WHERE sign_code = 'F47';

-- GIb
UPDATE traffic_signs SET
  long_description_en = 'Distance indication',
  long_description_nl = 'Aanduiding van een afstand',
  long_description_fr = 'Indication de distance',
  long_description_ar = 'إشارة المسافة',
  updated_at = NOW()
WHERE sign_code = 'GIb';

-- GIII-ijzel
UPDATE traffic_signs SET
  long_description_en = 'Caution risk of ice',
  long_description_nl = 'Opgepast kans op ijzel',
  long_description_fr = 'Attention risque de verglas',
  long_description_ar = 'انتباه خطر الجليد',
  updated_at = NOW()
WHERE sign_code = 'GIII-ijzel';

-- GIV
UPDATE traffic_signs SET
  long_description_en = 'Loading and unloading only',
  long_description_nl = 'Enkel laden en lossen',
  long_description_fr = 'Chargement et déchargement uniquement',
  long_description_ar = 'التحميل والتفريغ فقط',
  updated_at = NOW()
WHERE sign_code = 'GIV';

-- GIX
UPDATE traffic_signs SET
  long_description_en = 'Lane narrowing',
  long_description_nl = 'Versmalling van een rijstrook',
  long_description_fr = 'Rétrécissement de voie',
  long_description_ar = 'تضييق المسار',
  updated_at = NOW()
WHERE sign_code = 'GIX';

-- M2
UPDATE traffic_signs SET
  long_description_en = 'Except bicycles',
  long_description_nl = 'Uitgezonderd fiets',
  long_description_fr = 'Sauf vélos',
  long_description_ar = 'باستثناء الدراجات',
  updated_at = NOW()
WHERE sign_code = 'M2';

-- M7
UPDATE traffic_signs SET
  long_description_en = 'Moped class B prohibited',
  long_description_nl = 'Verbod voor bromfietsen klasse B',
  long_description_fr = 'Cyclomoteurs classe B interdits',
  long_description_ar = 'الدراجة البخارية من الفئة B محظورة',
  updated_at = NOW()
WHERE sign_code = 'M7';

-- M12-richtingen
UPDATE traffic_signs SET
  long_description_en = 'Bicycle directions left right',
  long_description_nl = 'Fietsrichtingen links rechts',
  long_description_fr = 'Directions vélos gauche droite',
  long_description_ar = 'اتجاهات الدراجات يسار يمين',
  updated_at = NOW()
WHERE sign_code = 'M12-richtingen';

-- M12-fiets-brom
UPDATE traffic_signs SET
  long_description_en = 'Except bicycles and moped class A',
  long_description_nl = 'Uitgezonderd fiets en bromfiets A',
  long_description_fr = 'Sauf vélos et cyclomoteurs classe A',
  long_description_ar = 'باستثناء الدراجات والدراجات البخارية الصغيرة من الفئة أ',
  updated_at = NOW()
WHERE sign_code = 'M12-fiets-brom';

-- M13
UPDATE traffic_signs SET
  long_description_en = 'Obligation for speed pedelecs',
  long_description_nl = 'Verplichting voor speed pedelecs',
  long_description_fr = 'Obligation pour speed pedelecs',
  long_description_ar = 'إلزامي للدراجات الكهربائية السريعة',
  updated_at = NOW()
WHERE sign_code = 'M13';

-- M15
UPDATE traffic_signs SET
  long_description_en = 'Speed pedelecs prohibited',
  long_description_nl = 'Verbod voor speed pedelecs',
  long_description_fr = 'Speed pedelecs interdits',
  long_description_ar = 'ممنوع للدراجات الكهربائية السريعة',
  updated_at = NOW()
WHERE sign_code = 'M15';
