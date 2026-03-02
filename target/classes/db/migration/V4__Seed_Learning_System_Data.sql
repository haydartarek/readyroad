-- ====================================================================
-- Phase 4: Learning System - Test Data
-- ====================================================================

USE readyroad;

-- ====================
-- STEP 1: Insert Lessons (using subquery for category_id)
-- ====================
INSERT INTO lessons (
    category_id,
    title_ar, title_en, title_nl, title_fr,
    content_ar, content_en, content_nl, content_fr,
    display_order, estimated_minutes, is_active
    -- ✅ No created_at/updated_at needed — DEFAULT CURRENT_TIMESTAMP handles it
) VALUES

((SELECT id FROM categories WHERE code = 'A'),
 'إشارات التحذير الأساسية', 'Basic Warning Signs', 'Basis waarschuwingsborden', 'Signaux d''avertissement de base',
 'إشارات التحذير تنبه السائقين للمخاطر المحتملة على الطريق. يجب الانتباه لها والتصرف بحذر.',
 'Warning signs alert drivers to potential hazards on the road. Pay attention and act cautiously.',
 'Waarschuwingsborden waarschuwen bestuurders voor mogelijke gevaren op de weg. Let op en handel voorzichtig.',
 'Les panneaux d''avertissement alertent les conducteurs des dangers potentiels sur la route. Soyez attentif et agissez prudemment.',
 1, 5, TRUE),

((SELECT id FROM categories WHERE code = 'B'),
 'إشارات الأولوية', 'Priority Signs', 'Voorrangsborden', 'Signaux de priorité',
 'إشارات الأولوية تحدد من له حق المرور أولاً عند التقاطعات. احترامها ضروري للسلامة.',
 'Priority signs determine who has the right of way at intersections. Respecting them is essential for safety.',
 'Voorrangsborden bepalen wie voorrang heeft bij kruispunten. Het respecteren ervan is essentieel voor de veiligheid.',
 'Les panneaux de priorité déterminent qui a la priorité aux intersections. Les respecter est essentiel pour la sécurité.',
 2, 5, TRUE),

((SELECT id FROM categories WHERE code = 'C'),
 'إشارات المنع', 'Prohibition Signs', 'Verbodsborden', 'Signaux d''interdiction',
 'إشارات المنع تمنع تصرفات معينة على الطريق. يجب الالتزام بها لتجنب المخالفات والحوادث.',
 'Prohibition signs forbid certain actions on the road. They must be obeyed to avoid violations and accidents.',
 'Verbodsborden verbieden bepaalde handelingen op de weg. Ze moeten worden gehoorzaamd om overtredingen en ongevallen te voorkomen.',
 'Les panneaux d''interdiction interdisent certaines actions sur la route. Ils doivent être respectés pour éviter les infractions et les accidents.',
 3, 5, TRUE),

((SELECT id FROM categories WHERE code = 'D'),
 'إشارات الإلزام', 'Mandatory Signs', 'Gebodsborden', 'Signaux d''obligation',
 'إشارات الإلزام تفرض تصرفات محددة يجب على السائق اتباعها. مثل الاتجاه الإلزامي.',
 'Mandatory signs impose specific actions that the driver must follow, such as compulsory direction.',
 'Gebodsborden leggen specifieke acties op die de bestuurder moet volgen, zoals verplichte rijrichting.',
 'Les panneaux obligatoires imposent des actions spécifiques que le conducteur doit suivre, comme la direction obligatoire.',
 4, 5, TRUE),

((SELECT id FROM categories WHERE code = 'E'),
 'إشارات الوقوف والانتظار', 'Parking and Stopping Signs', 'Parkeer- en stopborden', 'Signaux de stationnement',
 'إشارات الوقوف تنظم أماكن وأوقات الانتظار والوقوف. الالتزام بها يمنع الازدحام المروري.',
 'Parking signs regulate where and when parking and stopping are allowed. Following them prevents traffic congestion.',
 'Parkeer- en stopborden regelen waar en wanneer parkeren en stoppen is toegestaan. Het volgen ervan voorkomt verkeersopstoppingen.',
 'Les panneaux de stationnement régulent où et quand le stationnement et l''arrêt sont autorisés. Les suivre évite les embouteillages.',
 5, 5, TRUE),

((SELECT id FROM categories WHERE code = 'F'),
 'إشارات الإرشاد والطرق', 'Direction and Road Signs', 'Richtings- en wegborden', 'Signaux de direction',
 'إشارات الإرشاد توجه السائقين للمدن والطرق السريعة. تساعد في التنقل بسهولة.',
 'Direction signs guide drivers to cities and highways. They help navigate easily.',
 'Richtingsborden leiden bestuurders naar steden en snelwegen. Ze helpen gemakkelijk navigeren.',
 'Les panneaux de direction guident les conducteurs vers les villes et les autoroutes. Ils aident à naviguer facilement.',
 6, 5, TRUE),

((SELECT id FROM categories WHERE code = 'G'),
 'إشارات إضافية', 'Additional Information Signs', 'Aanvullende informatieborden', 'Signaux additionnels',
 'الإشارات الإضافية تكمل الإشارات الرئيسية بمعلومات إضافية مثل المسافة أو الاستثناءات.',
 'Additional signs complement main signs with extra information such as distance or exceptions.',
 'Aanvullende borden vullen hoofdborden aan met extra informatie zoals afstand of uitzonderingen.',
 'Les panneaux additionnels complètent les panneaux principaux avec des informations supplémentaires telles que la distance ou les exceptions.',
 7, 5, TRUE)

ON DUPLICATE KEY UPDATE updated_at = NOW();

SELECT '✅ Step 1: Lessons inserted' AS status;

-- ====================
-- STEP 2: Insert Practice Questions
-- ====================
INSERT INTO practice_questions (
    lesson_id,
    question_ar, question_en, question_nl, question_fr,
    option1_ar, option1_en, option1_nl, option1_fr,
    option2_ar, option2_en, option2_nl, option2_fr,
    option3_ar, option3_en, option3_nl, option3_fr,
    option4_ar, option4_en, option4_nl, option4_fr,
    correct_answer,
    explanation_ar, explanation_en, explanation_nl, explanation_fr,
    display_order, is_active
) VALUES

-- Lesson A: Warning Signs
((SELECT id FROM lessons WHERE title_en = 'Basic Warning Signs' LIMIT 1),
 'ماذا تعني إشارة التحذير المثلثية الحمراء؟', 'What does a red triangular warning sign mean?', 'Wat betekent een rood driehoekig waarschuwingsbord?', 'Que signifie un panneau triangulaire rouge?',
 'خطر محتمل في الطريق', 'Potential danger on the road', 'Mogelijk gevaar op de weg', 'Danger potentiel sur la route',
 'ممنوع المرور', 'No entry', 'Geen toegang', 'Accès interdit',
 'طريق آمن', 'Safe road', 'Veilige weg', 'Route sûre',
 'سرعة قصوى', 'Maximum speed', 'Maximumsnelheid', 'Vitesse maximale',
 1,
 'الإشارات المثلثية الحمراء هي إشارات تحذير',
 'Red triangular signs are warning signs',
 'Rode driehoekige borden zijn waarschuwingsborden',
 'Les panneaux triangulaires rouges sont des panneaux d''avertissement',
 1, TRUE),

((SELECT id FROM lessons WHERE title_en = 'Basic Warning Signs' LIMIT 1),
 'متى يجب أن تبطئ سرعتك عند رؤية إشارة تحذير؟', 'When should you slow down after seeing a warning sign?', 'Wanneer moet je vertragen na het zien van een waarschuwingsbord?', 'Quand devez-vous ralentir après avoir vu un panneau d''avertissement?',
 'فوراً', 'Immediately', 'Onmiddellijk', 'Immédiatement',
 'بعد 500 متر', 'After 500 meters', 'Na 500 meter', 'Après 500 mètres',
 'عند التقاطع التالي', 'At next intersection', 'Bij volgende kruispunt', 'Au prochain carrefour',
 'ليس ضرورياً', 'Not necessary', 'Niet nodig', 'Pas nécessaire',
 1,
 'يجب الاستعداد فوراً عند رؤية إشارة التحذير',
 'You must prepare immediately when seeing a warning sign',
 'Je moet je onmiddellijk voorbereiden bij het zien van een waarschuwingsbord',
 'Vous devez vous préparer immédiatement en voyant un panneau d''avertissement',
 2, TRUE),

-- Lesson B: Priority Signs
((SELECT id FROM lessons WHERE title_en = 'Priority Signs' LIMIT 1),
 'ماذا يعني إشارة المثلث المقلوب؟', 'What does an inverted triangle sign mean?', 'Wat betekent een omgekeerd driehoekig bord?', 'Que signifie un panneau triangulaire inversé?',
 'أعط الأولوية', 'Give way', 'Voorrang verlenen', 'Céder le passage',
 'توقف', 'Stop', 'Stop', 'Arrêt',
 'ممنوع الدخول', 'No entry', 'Geen toegang', 'Accès interdit',
 'طريق ذو أولوية', 'Priority road', 'Voorrangsweg', 'Route prioritaire',
 1,
 'المثلث المقلوب يعني يجب إعطاء الأولوية',
 'Inverted triangle means give way',
 'Omgekeerde driehoek betekent voorrang verlenen',
 'Triangle inversé signifie céder le passage',
 1, TRUE),

((SELECT id FROM lessons WHERE title_en = 'Priority Signs' LIMIT 1),
 'من له الأولوية في تقاطع بدون إشارات؟', 'Who has priority at an intersection without signs?', 'Wie heeft voorrang bij een kruispunt zonder borden?', 'Qui a la priorité à un carrefour sans panneaux?',
 'المركبة من اليمين', 'Vehicle from the right', 'Voertuig van rechts', 'Véhicule de droite',
 'المركبة من اليسار', 'Vehicle from the left', 'Voertuig van links', 'Véhicule de gauche',
 'المركبة الأسرع', 'Faster vehicle', 'Snelste voertuig', 'Véhicule le plus rapide',
 'المركبة الأكبر', 'Larger vehicle', 'Groter voertuig', 'Véhicule plus grand',
 1,
 'قاعدة اليمين: من يأتي من اليمين له الأولوية',
 'Right-hand rule: who comes from right has priority',
 'Rechts-regel: wie van rechts komt heeft voorrang',
 'Règle de droite: qui vient de droite a la priorité',
 2, TRUE),

-- Lesson E: Parking Signs ✅ Fixed Q5 logic
((SELECT id FROM lessons WHERE title_en = 'Parking and Stopping Signs' LIMIT 1),
 'ما الفرق بين E1 وE3؟', 'What is the difference between E1 and E3?',
 'Wat is het verschil tussen E1 en E3?', 'Quelle est la différence entre E1 et E3?',
 'E1 ممنوع الوقوف فقط، E3 ممنوع التوقف والوقوف',
 'E1 no parking only, E3 no stopping and parking',
 'E1 alleen parkeerverbod, E3 stilstaan en parkeren verboden',
 'E1 stationnement interdit, E3 arrêt et stationnement interdits',
 'E1 ممنوع تماماً، E3 مسموح الوقوف', 'E1 completely forbidden, E3 parking allowed', 'E1 volledig verboden, E3 parkeren toegestaan', 'E1 complètement interdit, E3 stationnement autorisé',
 'E1 وE3 نفس المعنى', 'E1 and E3 same meaning', 'E1 en E3 zelfde betekenis', 'E1 et E3 même signification',
 'E3 ممنوع الوقوف فقط، E1 ممنوع التوقف', 'E3 no parking, E1 no stopping', 'E3 parkeerverbod, E1 stilstaanverbod', 'E3 stationnement interdit, E1 arrêt interdit',
 1,
 'E1 = ممنوع الوقوف لكن يُسمح التوقف المؤقت. E3 = ممنوع التوقف والوقوف تماماً',
 'E1 = no parking but brief stopping allowed. E3 = no stopping or parking at all',
 'E1 = parkeerverbod maar even stoppen toegestaan. E3 = stilstaan en parkeren volledig verboden',
 'E1 = stationnement interdit mais arrêt bref autorisé. E3 = arrêt et stationnement totalement interdits',
 1, TRUE)

ON DUPLICATE KEY UPDATE updated_at = NOW();

SELECT '✅ Step 2: Practice questions inserted' AS status;

-- ====================
-- STEP 3: Insert Exam Questions
-- ====================
INSERT INTO exam_questions (
    category_id,
    question_ar, question_en, question_nl, question_fr,
    option1_ar, option1_en, option1_nl, option1_fr,
    option2_ar, option2_en, option2_nl, option2_fr,
    option3_ar, option3_en, option3_nl, option3_fr,
    option4_ar, option4_en, option4_nl, option4_fr,
    correct_answer,
    explanation_ar, explanation_en, explanation_nl, explanation_fr,
    image_url, difficulty, is_important, is_active
) VALUES

-- Q1: Warning Signs - MEDIUM
((SELECT id FROM categories WHERE code = 'A'),
 'إشارة تحذير بمنعطف، ماذا تفعل؟', 'Warning sign for bend, what do you do?', 'Waarschuwingsbord voor bocht, wat doet u?', 'Panneau avertissement virage, que faites-vous?',
 'خفف السرعة', 'Reduce speed', 'Verminder snelheid', 'Réduisez vitesse',
 'أسرع للمرور', 'Speed up to pass', 'Versnel om te passeren', 'Accélérez pour passer',
 'توقف تماماً', 'Stop completely', 'Stop volledig', 'Arrêtez complètement',
 'تابع بنفس السرعة', 'Continue same speed', 'Ga door zelfde snelheid', 'Continuez même vitesse',
 1,
 'إشارة التحذير = خفف السرعة فوراً',
 'Warning sign = reduce speed immediately',
 'Waarschuwingsbord = verminder snelheid onmiddellijk',
 'Panneau avertissement = réduisez vitesse immédiatement',
 NULL, 'MEDIUM', FALSE, TRUE),

-- Q2: Priority Signs - EASY ✅ Fixed: moved to category B
((SELECT id FROM categories WHERE code = 'B'),
 'مثلث أحمر مقلوب فارغ، ماذا يعني؟', 'Empty inverted red triangle, what does it mean?', 'Lege omgekeerde rode driehoek, wat betekent?', 'Triangle rouge inversé vide, que signifie?',
 'أعط الأولوية', 'Give way', 'Voorrang verlenen', 'Cédez passage',
 'توقف', 'Stop', 'Stop', 'Arrêt',
 'ممنوع الدخول', 'No entry', 'Geen toegang', 'Accès interdit',
 'تحذير', 'Warning', 'Waarschuwing', 'Avertissement',
 1,
 'مثلث مقلوب = أعط الأولوية (B1)',
 'Inverted triangle = give way (B1)',
 'Omgekeerde driehoek = voorrang verlenen (B1)',
 'Triangle inversé = cédez passage (B1)',
 NULL, 'EASY', FALSE, TRUE),

-- Q3: Prohibition - HARD
((SELECT id FROM categories WHERE code = 'C'),
 'متى يُسمح تجاوز السرعة القصوى؟', 'When is exceeding speed limit allowed?', 'Wanneer is snelheidslimiet overschrijden toegestaan?', 'Quand dépasser limite vitesse est autorisé?',
 'أبداً', 'Never', 'Nooit', 'Jamais',
 'في حالات الطوارئ', 'In emergencies', 'Bij noodgevallen', 'En urgence',
 'على طريق فارغ', 'On empty road', 'Op lege weg', 'Sur route vide',
 'في الليل', 'At night', '\'s Nachts', 'La nuit',
 1,
 'حدود السرعة يجب احترامها دائماً بدون استثناء',
 'Speed limits must always be respected without exception',
 'Snelheidslimieten moeten altijd zonder uitzondering worden gerespecteerd',
 'Les limites de vitesse doivent toujours être respectées sans exception',
 NULL, 'HARD', TRUE, TRUE),

-- Q8: Cities speed - EASY
((SELECT id FROM categories WHERE code = 'C'),
 'السرعة القصوى داخل المدن بدون إشارات؟', 'Maximum speed in cities without signs?', 'Maximumsnelheid in steden zonder borden?', 'Vitesse maximale en ville sans panneaux?',
 '50 كم/س', '50 km/h', '50 km/u', '50 km/h',
 '30 كم/س', '30 km/h', '30 km/u', '30 km/h',
 '70 كم/س', '70 km/h', '70 km/u', '70 km/h',
 '90 كم/س', '90 km/h', '90 km/u', '90 km/h',
 1,
 'القاعدة الافتراضية في بلجيكا: 50 كم/س داخل المدن',
 'Default rule in Belgium: 50 km/h in cities',
 'Standaardregel in België: 50 km/u in steden',
 'Règle par défaut en Belgique: 50 km/h en ville',
 NULL, 'EASY', TRUE, TRUE),

-- Q9: Roundabout - MEDIUM
((SELECT id FROM categories WHERE code = 'B'),
 'دوار بدون إشارات، من يمر أولاً؟', 'Roundabout without signs, who goes first?', 'Rotonde zonder borden, wie gaat eerst?', 'Rond-point sans panneaux, qui passe en premier?',
 'من داخل الدوار', 'Who is inside', 'Wie binnen is', 'Qui est dedans',
 'من يدخل', 'Who enters', 'Wie binnenrijdt', 'Qui entre',
 'من اليمين', 'From right', 'Van rechts', 'De droite',
 'المركبة الأكبر', 'Larger vehicle', 'Groter voertuig', 'Plus grand véhicule',
 1,
 'الدوار: من بالداخل له الأولوية على من يدخل',
 'Roundabout: vehicle inside has priority over entering vehicle',
 'Rotonde: voertuig binnen heeft voorrang op binnenkomend voertuig',
 'Rond-point: véhicule à l''intérieur a priorité sur celui qui entre',
 NULL, 'MEDIUM', TRUE, TRUE)

ON DUPLICATE KEY UPDATE updated_at = NOW();

SELECT '✅ Step 3: Exam questions inserted' AS status;

-- ====================
-- STEP 4: Verification
-- ====================
SELECT
    'Lessons'            AS entity, COUNT(*) AS count FROM lessons
UNION ALL SELECT
    'Practice Questions' AS entity, COUNT(*) AS count FROM practice_questions
UNION ALL SELECT
    'Exam Questions'     AS entity, COUNT(*) AS count FROM exam_questions;
