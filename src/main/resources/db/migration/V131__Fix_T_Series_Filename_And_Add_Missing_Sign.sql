-- ============================================================
-- V131: Fix T-Series (Afbakeningsborden) — TYPE-VA filename
--       and insert missing TYPE-IIA sign
-- ============================================================
-- Category 'M' = Afbakeningsborden (Delineation Signs) — already correct.
-- V118 already fixed all image_urls to images/signs/delineation_signs/...
-- Remaining issues:
--   1. TYPE-VA image_url had missing space: "Type VaBebakening..." → "Type Va Bebakening..."
--   2. TYPE-IIA (Baken voor zijdelingse signalisatie, links) was never inserted.
-- ============================================================

-- =====================
-- 1. Fix TYPE-VA image_url (missing space between "Va" and "Bebakening")
-- =====================
UPDATE traffic_signs
SET image_url   = 'images/signs/delineation_signs/Type Va Bebakening van een bocht.png',
    updated_at  = NOW()
WHERE sign_code = 'TYPE-VA';

-- =====================
-- 2. Insert missing TYPE-IIA sign
--    Official name: Type IIa – Baken voor zijdelingse signalisatie, links
--    Category: M (Afbakeningsborden / Delineation Signs)
-- =====================
INSERT INTO traffic_signs (
    sign_code, category_id,
    name_nl, name_en, name_fr, name_ar,
    description_nl, description_en, description_fr, description_ar,
    long_description_nl, long_description_en, long_description_fr, long_description_ar,
    image_url, is_active, created_at, updated_at
)
VALUES (
    'TYPE-IIA',
    (SELECT id FROM categories WHERE code = 'M'),
    'Type IIa - Baken voor zijdelingse signalisatie, links',
    'Type IIa - Lateral signalization beacon, left',
    'Type IIa - Balise de signalisation latérale, gauche',
    'النوع IIa - منارة الإشارة الجانبية، على اليسار',
    'Type IIa - Baken voor zijdelingse signalisatie, links',
    'Type IIa - Lateral signalization beacon, left',
    'Type IIa - Balise de signalisation latérale, gauche',
    'النوع IIa - منارة الإشارة الجانبية، على اليسار',
    'Dit bord wijst op een obstakel of versmalling aan de zijkant van de weg, en de bestuurder moet aan de linkerkant passeren volgens de richting van de schuine lijnen. Het wordt vaak gebruikt bij wegwerkzaamheden of vaste obstakels aan de rechterzijde, en u moet afremmen en de aangegeven richting nauwkeurig volgen om een botsing te voorkomen.',
    'This traffic sign indicates an obstacle or narrowing on the side of the road, and the driver must pass on the left side according to the direction of the diagonal lines. It is often used at roadworks or fixed barriers on the right side, and you must slow down and follow the indicated direction precisely to avoid a collision.',
    'Ce panneau de signalisation indique un obstacle ou un rétrécissement sur le côté de la route, et le conducteur doit passer du côté gauche selon la direction des lignes diagonales. Il est souvent utilisé lors de travaux routiers ou d''obstacles fixes à droite, et vous devez ralentir et suivre la direction indiquée avec précision pour éviter une collision.',
    'تشير هذه العلامة المرورية إلى وجود عائق أو تضييق على جانب الطريق، ويجب على السائق المرور على الجانب الأيسر حسب اتجاه الخطوط القطرية. يتم استخدامه غالبًا في أعمال الطرق أو الحواجز الثابتة على الجانب الأيمن، ويجب عليك إبطاء السرعة واتباع الاتجاه المشار إليه بدقة لتجنب الاصطدام.',
    'images/signs/delineation_signs/Type IIa Baken voor zijdelingse signalisatie, links.png',
    TRUE,
    NOW(),
    NOW()
) AS new_values
ON DUPLICATE KEY UPDATE
    category_id         = new_values.category_id,
    name_nl             = new_values.name_nl,
    name_en             = new_values.name_en,
    name_fr             = new_values.name_fr,
    name_ar             = new_values.name_ar,
    description_nl      = new_values.description_nl,
    description_en      = new_values.description_en,
    description_fr      = new_values.description_fr,
    description_ar      = new_values.description_ar,
    long_description_nl = new_values.long_description_nl,
    long_description_en = new_values.long_description_en,
    long_description_fr = new_values.long_description_fr,
    long_description_ar = new_values.long_description_ar,
    image_url           = new_values.image_url,
    is_active           = TRUE,
    updated_at          = NOW();
