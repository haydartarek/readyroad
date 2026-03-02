-- ============================================================
-- Migration: V92
-- Description: Seed quiz questions + answer options for
--              categories G (7), Z (8), M (9), H (17)
--              that previously had 0 deliverable questions.
-- Quality gate: 2-3 options, exactly 1 correct, multilingual.
-- Author: ReadyRoad Team
-- Date: 2026-02-27
-- ============================================================

-- ══════════════════════════════════════════════════════════
-- CATEGORY G (id=7) – Supplementary Signs
-- ══════════════════════════════════════════════════════════

INSERT INTO quiz_questions (question_ar, question_en, question_nl, question_fr, question_type, difficulty_level, category_id, is_active, status, created_at, updated_at)
VALUES ('ماذا يعني اللوح التكميلي تحت علامة التوقف؟', 'What does a supplementary panel below a STOP sign mean?', 'Wat betekent een onderbord onder een STOPbord?', 'Que signifie un panneau additionnel sous un panneau STOP?', 'MULTIPLE_CHOICE', 'EASY', 7, true, 'PUBLISHED', NOW(), NOW());
SET @q1 = LAST_INSERT_ID();
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order, created_at) VALUES
(@q1, 'يضيف شرطاً إضافياً للعلامة الرئيسية', 'It adds an additional condition to the main sign', 'Het voegt een bijkomende voorwaarde aan het hoofdbord toe', 'Il ajoute une condition supplémentaire au panneau principal', true,  1, NOW()),
(@q1, 'يلغي العلامة الرئيسية', 'It cancels the main sign', 'Het annuleert het hoofdbord', 'Il annule le panneau principal', false, 2, NOW()),
(@q1, 'يُغير لون العلامة الرئيسية', 'It changes the colour of the main sign', 'Het verandert de kleur van het hoofdbord', 'Il change la couleur du panneau principal', false, 3, NOW());

INSERT INTO quiz_questions (question_ar, question_en, question_nl, question_fr, question_type, difficulty_level, category_id, is_active, status, created_at, updated_at)
VALUES ('ماذا يعني لوح تكميلي يحمل رسم سهم إلى اليمين؟', 'What does a supplementary panel with a right-pointing arrow mean?', 'Wat betekent een onderbord met een pijl naar rechts?', 'Que signifie un panneau additionnel avec une flèche vers la droite?', 'MULTIPLE_CHOICE', 'EASY', 7, true, 'PUBLISHED', NOW(), NOW());
SET @q2 = LAST_INSERT_ID();
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order, created_at) VALUES
(@q2, 'يشير إلى أن التطبيق يكون في الاتجاه الذي يشير إليه السهم', 'It indicates the sign applies in the direction the arrow points', 'Het geeft aan dat het bord geldt in de pijlrichting', 'Il indique que le panneau s''applique dans la direction indiquée par la flèche', true,  1, NOW()),
(@q2, 'يشير إلى مسار هروب', 'It marks an escape route', 'Het markeert een vluchtroute', 'Il marque une route d''évacuation', false, 2, NOW());

INSERT INTO quiz_questions (question_ar, question_en, question_nl, question_fr, question_type, difficulty_level, category_id, is_active, status, created_at, updated_at)
VALUES ('ما وظيفة اللوحات التكميلية بشكل عام؟', 'What is the general purpose of supplementary panels?', 'Wat is het algemene doel van onderborden?', 'Quel est le but général des panneaux additionnels?', 'MULTIPLE_CHOICE', 'MEDIUM', 7, true, 'PUBLISHED', NOW(), NOW());
SET @q3 = LAST_INSERT_ID();
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order, created_at) VALUES
(@q3, 'تحديد أو تقييد نطاق تطبيق العلامة الرئيسية', 'To define or restrict the scope of the main sign', 'De reikwijdte van het hoofdbord afbakenen of beperken', 'Définir ou restreindre la portée du panneau principal', true,  1, NOW()),
(@q3, 'استبدال العلامة الرئيسية', 'To replace the main sign', 'Het hoofdbord vervangen', 'Remplacer le panneau principal', false, 2, NOW());

INSERT INTO quiz_questions (question_ar, question_en, question_nl, question_fr, question_type, difficulty_level, category_id, is_active, status, created_at, updated_at)
VALUES ('ما الشكل المعتاد للوح التكميلي البلجيكي؟', 'What is the typical shape of a Belgian supplementary panel?', 'Wat is de gebruikelijke vorm van een Belgisch onderbord?', 'Quelle est la forme habituelle d''un panneau additionnel belge?', 'MULTIPLE_CHOICE', 'EASY', 7, true, 'PUBLISHED', NOW(), NOW());
SET @q4 = LAST_INSERT_ID();
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order, created_at) VALUES
(@q4, 'مستطيل أبيض بإطار أحمر', 'White rectangle with red border', 'Witte rechthoek met rode rand', 'Rectangle blanc avec bordure rouge', true,  1, NOW()),
(@q4, 'دائرة زرقاء', 'Blue circle', 'Blauwe cirkel', 'Cercle bleu', false, 2, NOW()),
(@q4, 'مثلث أصفر', 'Yellow triangle', 'Gele driehoek', 'Triangle jaune', false, 3, NOW());

INSERT INTO quiz_questions (question_ar, question_en, question_nl, question_fr, question_type, difficulty_level, category_id, is_active, status, created_at, updated_at)
VALUES ('ماذا يعني لوح تكميلي يحمل الكلمة UITGEZONDERD / SAUF؟', 'What does a supplementary panel reading UITGEZONDERD / SAUF mean?', 'Wat betekent een onderbord met de tekst UITGEZONDERD?', 'Que signifie un panneau additionnel portant la mention SAUF?', 'MULTIPLE_CHOICE', 'MEDIUM', 7, true, 'PUBLISHED', NOW(), NOW());
SET @q5 = LAST_INSERT_ID();
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order, created_at) VALUES
(@q5, 'استثناء لفئة معينة من تطبيق العلامة الرئيسية', 'An exception for a specific category from the main sign', 'Een uitzondering voor een bepaalde categorie van het hoofdbord', 'Une exception pour une catégorie spécifique du panneau principal', true,  1, NOW()),
(@q5, 'يمنع جميع المركبات', 'It prohibits all vehicles', 'Het verbiedt alle voertuigen', 'Il interdit tous les véhicules', false, 2, NOW()),
(@q5, 'مرحلة تجريبية', 'Pilot phase indicator', 'Pilootfase indicator', 'Indicateur de phase pilote', false, 3, NOW());

INSERT INTO quiz_questions (question_ar, question_en, question_nl, question_fr, question_type, difficulty_level, category_id, is_active, status, created_at, updated_at)
VALUES ('هل يلزم احترام اللوحات التكميلية مثل العلامات الرئيسية؟', 'Must supplementary panels be respected like main signs?', 'Moeten onderborden worden nageleefd zoals hoofdborden?', 'Les panneaux additionnels doivent-ils être respectés comme les panneaux principaux?', 'MULTIPLE_CHOICE', 'EASY', 7, true, 'PUBLISHED', NOW(), NOW());
SET @q6 = LAST_INSERT_ID();
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order, created_at) VALUES
(@q6, 'نعم، لها قوة قانونية ملزمة', 'Yes, they have legally binding force', 'Ja, ze hebben wettelijk bindende kracht', 'Oui, ils ont une force juridiquement contraignante', true,  1, NOW()),
(@q6, 'لا، هي مجرد توصيات', 'No, they are merely recommendations', 'Nee, het zijn slechts aanbevelingen', 'Non, ce ne sont que des recommandations', false, 2, NOW());

INSERT INTO quiz_questions (question_ar, question_en, question_nl, question_fr, question_type, difficulty_level, category_id, is_active, status, created_at, updated_at)
VALUES ('كيف تتميز اللوحة التكميلية التي تحدد فترة زمنية؟', 'How is a supplementary panel indicating a time period distinguished?', 'Hoe onderscheidt u een onderbord dat een tijdperiode aangeeft?', 'Comment distingue-t-on un panneau additionnel indiquant une période horaire?', 'MULTIPLE_CHOICE', 'HARD', 7, true, 'PUBLISHED', NOW(), NOW());
SET @q7 = LAST_INSERT_ID();
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order, created_at) VALUES
(@q7, 'تحمل أوقاتاً أو أياماً مكتوبة عليها', 'It shows times or days written on it', 'Het toont tijden of dagen erop geschreven', 'Il affiche des heures ou des jours indiqués dessus', true,  1, NOW()),
(@q7, 'تكون مضاءة باللون الأخضر', 'It is lit in green', 'Het is verlicht in het groen', 'Il est éclairé en vert', false, 2, NOW()),
(@q7, 'تحمل رمز ساعة', 'It carries a clock symbol', 'Het draagt een kloksymbool', 'Il porte un symbole d''horloge', false, 3, NOW());

-- ══════════════════════════════════════════════════════════
-- CATEGORY Z (id=8) – Zone Signs
-- ══════════════════════════════════════════════════════════

INSERT INTO quiz_questions (question_ar, question_en, question_nl, question_fr, question_type, difficulty_level, category_id, is_active, status, created_at, updated_at)
VALUES ('ماذا تعني علامة منطقة السرعة 30 كم/ساعة؟', 'What does a Zone 30 sign mean?', 'Wat betekent een Zone 30-bord?', 'Que signifie un panneau Zone 30?', 'MULTIPLE_CHOICE', 'EASY', 8, true, 'PUBLISHED', NOW(), NOW());
SET @q8 = LAST_INSERT_ID();
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order, created_at) VALUES
(@q8, 'السرعة القصوى في المنطقة كاملها 30 كم/ساعة', 'Maximum speed throughout the entire zone is 30 km/h', 'Maximumsnelheid door de gehele zone is 30 km/u', 'La vitesse maximale dans toute la zone est de 30 km/h', true,  1, NOW()),
(@q8, 'السرعة القصوى 30 كم/ساعة في هذه النقطة فقط', 'Maximum speed is 30 km/h at this point only', 'Maximumsnelheid is 30 km/u alleen op dit punt', 'La vitesse maximale est de 30 km/h à ce point seulement', false, 2, NOW()),
(@q8, 'لا يوجد حد سرعة', 'There is no speed limit', 'Er is geen snelheidslimiet', 'Il n''y a pas de limitation de vitesse', false, 3, NOW());

INSERT INTO quiz_questions (question_ar, question_en, question_nl, question_fr, question_type, difficulty_level, category_id, is_active, status, created_at, updated_at)
VALUES ('كيف يتم إنهاء منطقة Zone 30؟', 'How is a Zone 30 terminated?', 'Hoe wordt een Zone 30 beëindigd?', 'Comment est terminée une Zone 30?', 'MULTIPLE_CHOICE', 'EASY', 8, true, 'PUBLISHED', NOW(), NOW());
SET @q9 = LAST_INSERT_ID();
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order, created_at) VALUES
(@q9, 'علامة نهاية Zone 30', 'An End Zone 30 sign', 'Een Einde Zone 30-bord', 'Un panneau Fin Zone 30', true,  1, NOW()),
(@q9, 'خط أبيض على الأرض', 'A white line on the ground', 'Een witte lijn op de grond', 'Une ligne blanche sur le sol', false, 2, NOW());

INSERT INTO quiz_questions (question_ar, question_en, question_nl, question_fr, question_type, difficulty_level, category_id, is_active, status, created_at, updated_at)
VALUES ('ماذا يعني لافتة منطقة سكنية Woonerf / Zone résidentielle؟', 'What does a Woonerf / Residential Zone sign mean?', 'Wat betekent een Woonerf-bord?', 'Que signifie un panneau Zone résidentielle?', 'MULTIPLE_CHOICE', 'MEDIUM', 8, true, 'PUBLISHED', NOW(), NOW());
SET @q10 = LAST_INSERT_ID();
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order, created_at) VALUES
(@q10, 'منطقة مشتركة بين المشاة والمركبات بقواعد خاصة', 'A shared area for pedestrians and vehicles with special rules', 'Een gedeeld gebied voor voetgangers en voertuigen met speciale regels', 'Une zone partagée entre piétons et véhicules avec des règles spéciales', true,  1, NOW()),
(@q10, 'منطقة مخصصة للمركبات فقط', 'A zone reserved for vehicles only', 'Een zone voorbehouden aan voertuigen', 'Une zone réservée aux véhicules uniquement', false, 2, NOW()),
(@q10, 'طريق سريع محلي', 'A local express road', 'Een lokale snelweg', 'Une voie express locale', false, 3, NOW());

INSERT INTO quiz_questions (question_ar, question_en, question_nl, question_fr, question_type, difficulty_level, category_id, is_active, status, created_at, updated_at)
VALUES ('ما الحد الأقصى للسرعة في منطقة سكنية Woonerf؟', 'What is the maximum speed in a Woonerf residential zone?', 'Wat is de maximumsnelheid in een Woonerf?', 'Quelle est la vitesse maximale dans une zone résidentielle Woonerf?', 'MULTIPLE_CHOICE', 'EASY', 8, true, 'PUBLISHED', NOW(), NOW());
SET @q11 = LAST_INSERT_ID();
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order, created_at) VALUES
(@q11, '20 كم/ساعة', '20 km/h', '20 km/u', '20 km/h', true,  1, NOW()),
(@q11, '30 كم/ساعة', '30 km/h', '30 km/u', '30 km/h', false, 2, NOW()),
(@q11, '50 كم/ساعة', '50 km/h', '50 km/u', '50 km/h', false, 3, NOW());

INSERT INTO quiz_questions (question_ar, question_en, question_nl, question_fr, question_type, difficulty_level, category_id, is_active, status, created_at, updated_at)
VALUES ('ما الفرق بين Zone 30 وعلامة السرعة القصوى 30 العادية؟', 'What is the difference between Zone 30 and a regular 30 km/h speed limit sign?', 'Wat is het verschil tussen Zone 30 en een gewoon 30 km/u-bord?', 'Quelle est la différence entre Zone 30 et un panneau de vitesse 30 km/h classique?', 'MULTIPLE_CHOICE', 'MEDIUM', 8, true, 'PUBLISHED', NOW(), NOW());
SET @q12 = LAST_INSERT_ID();
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order, created_at) VALUES
(@q12, 'Zone 30 تنطبق على الشوارع داخل المنطقة بأكملها، بينما الأخرى تنطبق فقط من نقطة الوضع', 'Zone 30 applies to all streets within the zone, while the other applies only from where it is placed', 'Zone 30 geldt voor alle straten in de zone, terwijl het andere enkel geldt vanaf de plaatsing', 'Zone 30 s''applique à toutes les rues de la zone, l''autre seulement depuis son emplacement', true,  1, NOW()),
(@q12, 'لا يوجد فرق', 'There is no difference', 'Er is geen verschil', 'Il n''y a pas de différence', false, 2, NOW());

INSERT INTO quiz_questions (question_ar, question_en, question_nl, question_fr, question_type, difficulty_level, category_id, is_active, status, created_at, updated_at)
VALUES ('ماذا يعني لافتة منطقة المشاة؟', 'What does a pedestrian zone sign mean?', 'Wat betekent een voetgangerszone-bord?', 'Que signifie un panneau zone piétonne?', 'MULTIPLE_CHOICE', 'EASY', 8, true, 'PUBLISHED', NOW(), NOW());
SET @q13 = LAST_INSERT_ID();
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order, created_at) VALUES
(@q13, 'المنطقة مخصصة للمشاة، المركبات ممنوعة إلا بإذن', 'The zone is for pedestrians only, vehicles are prohibited unless authorised', 'De zone is voorbehouden voor voetgangers, voertuigen zijn verboden tenzij gemachtigd', 'La zone est réservée aux piétons, les véhicules sont interdits sauf autorisation', true,  1, NOW()),
(@q13, 'المشاة فقط ممنوعون من الدخول', 'Only pedestrians are prohibited from entering', 'Alleen voetgangers mogen niet binnenkomen', 'Seuls les piétons sont interdits d''entrée', false, 2, NOW()),
(@q13, 'السرعة القصوى 10 كم/ساعة', 'Maximum speed is 10 km/h', 'Maximumsnelheid is 10 km/u', 'La vitesse maximale est de 10 km/h', false, 3, NOW());

INSERT INTO quiz_questions (question_ar, question_en, question_nl, question_fr, question_type, difficulty_level, category_id, is_active, status, created_at, updated_at)
VALUES ('هل يمكن للسائق دخول منطقة مشاة لتحميل البضائع؟', 'Can a driver enter a pedestrian zone for loading goods?', 'Kan een bestuurder een voetgangerszone betreden voor het laden van goederen?', 'Un conducteur peut-il entrer dans une zone piétonne pour charger des marchandises?', 'MULTIPLE_CHOICE', 'MEDIUM', 8, true, 'PUBLISHED', NOW(), NOW());
SET @q14 = LAST_INSERT_ID();
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order, created_at) VALUES
(@q14, 'فقط في حال كان هناك لوح تكميلي يسمح بذلك', 'Only if there is a supplementary panel authorising it', 'Alleen als er een onderbord is dat dit toestaat', 'Seulement s''il y a un panneau additionnel l''autorisant', true,  1, NOW()),
(@q14, 'نعم دائماً', 'Yes always', 'Ja altijd', 'Oui toujours', false, 2, NOW()),
(@q14, 'لا أبداً', 'Never', 'Nooit', 'Jamais', false, 3, NOW());

-- ══════════════════════════════════════════════════════════
-- CATEGORY M (id=9) – Delineation Signs
-- ══════════════════════════════════════════════════════════

INSERT INTO quiz_questions (question_ar, question_en, question_nl, question_fr, question_type, difficulty_level, category_id, is_active, status, created_at, updated_at)
VALUES ('ما وظيفة علامات التحديد الجانبية على الطريق؟', 'What is the function of lateral delineation signs on the road?', 'Wat is de functie van zijdelingse afbakeningsborden op de weg?', 'Quelle est la fonction des panneaux de délimitation latérale sur la route?', 'MULTIPLE_CHOICE', 'EASY', 9, true, 'PUBLISHED', NOW(), NOW());
SET @q15 = LAST_INSERT_ID();
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order, created_at) VALUES
(@q15, 'إرشاد السائقين لحافة الطريق أو العوائق', 'To guide drivers to the road edge or obstacles', 'Bestuurders naar de rijbaanrand of obstakels leiden', 'Guider les conducteurs vers le bord de la chaussée ou les obstacles', true,  1, NOW()),
(@q15, 'تحديد الحد الأقصى للسرعة', 'To indicate the maximum speed', 'De maximumsnelheid aanduiden', 'Indiquer la vitesse maximale', false, 2, NOW()),
(@q15, 'منع الوقوف', 'To prohibit parking', 'Parkeren verbieden', 'Interdire le stationnement', false, 3, NOW());

INSERT INTO quiz_questions (question_ar, question_en, question_nl, question_fr, question_type, difficulty_level, category_id, is_active, status, created_at, updated_at)
VALUES ('ما اللون المستخدم في علامات تحديد الطريق M1؟', 'What colour is used in M1 road delineation markers?', 'Welke kleur wordt gebruikt bij M1 wegafbakeningsborden?', 'Quelle couleur est utilisée dans les balises de délimitation M1?', 'MULTIPLE_CHOICE', 'EASY', 9, true, 'PUBLISHED', NOW(), NOW());
SET @q16 = LAST_INSERT_ID();
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order, created_at) VALUES
(@q16, 'أبيض مع شريط عاكس أحمر وأبيض', 'White with red and white reflective strip', 'Wit met rood-witte reflecterende strook', 'Blanc avec bande réfléchissante rouge et blanche', true,  1, NOW()),
(@q16, 'أصفر تماماً', 'Entirely yellow', 'Volledig geel', 'Entièrement jaune', false, 2, NOW()),
(@q16, 'أزرق مع نجمة بيضاء', 'Blue with a white star', 'Blauw met een witte ster', 'Bleu avec une étoile blanche', false, 3, NOW());

INSERT INTO quiz_questions (question_ar, question_en, question_nl, question_fr, question_type, difficulty_level, category_id, is_active, status, created_at, updated_at)
VALUES ('ما الفرق بين M2 و M3 في علامات التحديد البلجيكية؟', 'What is the difference between M2 and M3 Belgian delineation markers?', 'Wat is het verschil tussen M2 en M3 bij Belgische afbakeningsborden?', 'Quelle est la différence entre M2 et M3 dans les balises belges?', 'MULTIPLE_CHOICE', 'HARD', 9, true, 'PUBLISHED', NOW(), NOW());
SET @q17 = LAST_INSERT_ID();
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order, created_at) VALUES
(@q17, 'M2 لليمين (أحمر-أبيض) و M3 لليسار (أبيض)', 'M2 marks the right side (red-white) and M3 marks the left side (white)', 'M2 markeert de rechterkant (rood-wit) en M3 de linkerkant (wit)', 'M2 marque le côté droit (rouge-blanc) et M3 le côté gauche (blanc)', true,  1, NOW()),
(@q17, 'M2 للطرق السريعة و M3 للطرق المحلية', 'M2 is for highways and M3 for local roads', 'M2 is voor snelwegen en M3 voor lokale wegen', 'M2 est pour les autoroutes et M3 pour les routes locales', false, 2, NOW()),
(@q17, 'لا يوجد فرق', 'There is no difference', 'Er is geen verschil', 'Il n''y a pas de différence', false, 3, NOW());

INSERT INTO quiz_questions (question_ar, question_en, question_nl, question_fr, question_type, difficulty_level, category_id, is_active, status, created_at, updated_at)
VALUES ('لماذا يجب الانتباه لعلامات التحديد في الليل؟', 'Why should you pay attention to delineation markers at night?', 'Waarom moet u ''s nachts op afbakeningsborden letten?', 'Pourquoi faut-il prêter attention aux balises de délimitation la nuit?', 'MULTIPLE_CHOICE', 'MEDIUM', 9, true, 'PUBLISHED', NOW(), NOW());
SET @q18 = LAST_INSERT_ID();
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order, created_at) VALUES
(@q18, 'لأنها عاكسة وتحدد حافة الطريق في الظلام', 'Because they are reflective and mark the road edge in the dark', 'Omdat ze reflecterend zijn en de rijbaanrand in het donker markeren', 'Parce qu''elles sont réfléchissantes et marquent le bord de la route dans l''obscurité', true,  1, NOW()),
(@q18, 'لأنها مضاءة بشكل نشط', 'Because they are actively illuminated', 'Omdat ze actief verlicht zijn', 'Parce qu''elles sont activement éclairées', false, 2, NOW());

INSERT INTO quiz_questions (question_ar, question_en, question_nl, question_fr, question_type, difficulty_level, category_id, is_active, status, created_at, updated_at)
VALUES ('ما علامة التحديد التي تشير إلى خطر العبور فوق جسر أو حافة؟', 'Which delineation sign warns of a bridge or edge crossing hazard?', 'Welk afbakeningsbord waarschuwt voor een oversteekgevaar bij een brug of rand?', 'Quel panneau de délimitation avertit d''un danger de franchissement de pont ou de bord?', 'MULTIPLE_CHOICE', 'HARD', 9, true, 'PUBLISHED', NOW(), NOW());
SET @q19 = LAST_INSERT_ID();
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order, created_at) VALUES
(@q19, 'علامة التحديد M4 ذات الألوان الحمراء والبيضاء المتناوبة', 'M4 delineation marker with alternate red and white colours', 'M4 afbakeningsbord met afwisselend rood en wit', 'Balise de délimitation M4 avec couleurs rouges et blanches alternées', true,  1, NOW()),
(@q19, 'علامة التحديد M1 الصغيرة', 'Small M1 delineation marker', 'Klein M1-afbakeningsbord', 'Petite balise de délimitation M1', false, 2, NOW()),
(@q19, 'علامة توقف حمراء', 'Red STOP sign', 'Rood STOPbord', 'Panneau STOP rouge', false, 3, NOW());

INSERT INTO quiz_questions (question_ar, question_en, question_nl, question_fr, question_type, difficulty_level, category_id, is_active, status, created_at, updated_at)
VALUES ('هل علامات التحديد إلزامية على الطرق الخارجية في بلجيكا؟', 'Are delineation markers mandatory on rural roads in Belgium?', 'Zijn afbakeningsborden verplicht op landelijke wegen in België?', 'Les balises de délimitation sont-elles obligatoires sur les routes rurales en Belgique?', 'MULTIPLE_CHOICE', 'MEDIUM', 9, true, 'PUBLISHED', NOW(), NOW());
SET @q20 = LAST_INSERT_ID();
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order, created_at) VALUES
(@q20, 'نعم، تُنصب حيث يوجد خطر أو حافة غير محمية', 'Yes, installed where there is a hazard or unprotected edge', 'Ja, geplaatst waar een gevaar of onbeschermde rand bestaat', 'Oui, installées là où il y a un danger ou un bord non protégé', true,  1, NOW()),
(@q20, 'لا، هي اختيارية تماماً', 'No, they are completely optional', 'Nee, ze zijn volledig optioneel', 'Non, elles sont entièrement facultatives', false, 2, NOW());

-- ══════════════════════════════════════════════════════════
-- CATEGORY H (id=17) – Information and Temporary Traffic Signs
-- ══════════════════════════════════════════════════════════

INSERT INTO quiz_questions (question_ar, question_en, question_nl, question_fr, question_type, difficulty_level, category_id, is_active, status, created_at, updated_at)
VALUES ('ماذا تعني علامة الطريق المقطوع أو المغلق مؤقتاً؟', 'What does a temporary road closed sign mean?', 'Wat betekent een tijdelijk wegafsluiting-bord?', 'Que signifie un panneau de route temporairement fermée?', 'MULTIPLE_CHOICE', 'EASY', 17, true, 'PUBLISHED', NOW(), NOW());
SET @q21 = LAST_INSERT_ID();
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order, created_at) VALUES
(@q21, 'يجب على السائق إيجاد طريق بديل', 'The driver must find an alternative route', 'De bestuurder moet een alternatieve route zoeken', 'Le conducteur doit trouver un itinéraire alternatif', true,  1, NOW()),
(@q21, 'يمكن المرور ببطء', 'You may pass slowly', 'U mag langzaam passeren', 'Vous pouvez passer lentement', false, 2, NOW()),
(@q21, 'الطريق مغلق للمشاة فقط', 'The road is closed for pedestrians only', 'De weg is alleen afgesloten voor voetgangers', 'La route est fermée pour les piétons uniquement', false, 3, NOW());

INSERT INTO quiz_questions (question_ar, question_en, question_nl, question_fr, question_type, difficulty_level, category_id, is_active, status, created_at, updated_at)
VALUES ('ما الفرق بين اللافتات الدائمة والمؤقتة لأشغال الطريق؟', 'What is the difference between permanent and temporary road work signs?', 'Wat is het verschil tussen permanente en tijdelijke wegenwerken-borden?', 'Quelle est la différence entre les panneaux permanents et temporaires de travaux routiers?', 'MULTIPLE_CHOICE', 'MEDIUM', 17, true, 'PUBLISHED', NOW(), NOW());
SET @q22 = LAST_INSERT_ID();
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order, created_at) VALUES
(@q22, 'اللافتات المؤقتة لها خلفية صفراء في بلجيكا', 'Temporary signs have a yellow background in Belgium', 'Tijdelijke borden hebben een gele achtergrond in België', 'Les panneaux temporaires ont un fond jaune en Belgique', true,  1, NOW()),
(@q22, 'اللافتات المؤقتة أصغر حجماً', 'Temporary signs are smaller in size', 'Tijdelijke borden zijn kleiner van formaat', 'Les panneaux temporaires sont plus petits', false, 2, NOW());

INSERT INTO quiz_questions (question_ar, question_en, question_nl, question_fr, question_type, difficulty_level, category_id, is_active, status, created_at, updated_at)
VALUES ('ما السلوك الصحيح عند اقتراب منطقة أشغال؟', 'What is the correct behaviour when approaching a road work zone?', 'Wat is het correcte gedrag bij het naderen van een werkenzone?', 'Quel est le comportement correct en approchant d''une zone de travaux?', 'MULTIPLE_CHOICE', 'EASY', 17, true, 'PUBLISHED', NOW(), NOW());
SET @q23 = LAST_INSERT_ID();
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order, created_at) VALUES
(@q23, 'خفض السرعة والانتباه للعمال والعلامات المؤقتة', 'Reduce speed and watch out for workers and temporary signs', 'Snelheid verminderen en letten op arbeiders en tijdelijke borden', 'Réduire la vitesse et faire attention aux ouvriers et aux panneaux temporaires', true,  1, NOW()),
(@q23, 'تسريع للمرور بسرعة', 'Accelerate to pass quickly', 'Versnellen om snel voorbij te komen', 'Accélérer pour passer rapidement', false, 2, NOW()),
(@q23, 'تشغيل الأضواء الخلفية', 'Turn on rear lights', 'Achterlichten inschakelen', 'Allumer les feux arrière', false, 3, NOW());

INSERT INTO quiz_questions (question_ar, question_en, question_nl, question_fr, question_type, difficulty_level, category_id, is_active, status, created_at, updated_at)
VALUES ('ما معنى علامة تحويل المرور المؤقتة؟', 'What does a temporary traffic diversion sign mean?', 'Wat betekent een tijdelijk verkeersomleiding-bord?', 'Que signifie un panneau de déviation de circulation temporaire?', 'MULTIPLE_CHOICE', 'EASY', 17, true, 'PUBLISHED', NOW(), NOW());
SET @q24 = LAST_INSERT_ID();
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order, created_at) VALUES
(@q24, 'يجب اتباع مسار التحويل المشار إليه', 'You must follow the indicated diversion route', 'U moet de aangegeven omleiding volgen', 'Vous devez suivre l''itinéraire de déviation indiqué', true,  1, NOW()),
(@q24, 'التحويل اختياري', 'The diversion is optional', 'De omleiding is optioneel', 'La déviation est facultative', false, 2, NOW()),
(@q24, 'الطريق مفتوح للجميع', 'The road is open to everyone', 'De weg is voor iedereen open', 'La route est ouverte à tous', false, 3, NOW());

INSERT INTO quiz_questions (question_ar, question_en, question_nl, question_fr, question_type, difficulty_level, category_id, is_active, status, created_at, updated_at)
VALUES ('هل تتمتع العلامات المؤقتة (خلفية صفراء) بنفس قوة العلامات الدائمة؟', 'Do temporary signs (yellow background) have the same force as permanent signs?', 'Hebben tijdelijke borden (gele achtergrond) dezelfde kracht als permanente borden?', 'Les panneaux temporaires (fond jaune) ont-ils la même force que les panneaux permanents?', 'MULTIPLE_CHOICE', 'MEDIUM', 17, true, 'PUBLISHED', NOW(), NOW());
SET @q25 = LAST_INSERT_ID();
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order, created_at) VALUES
(@q25, 'نعم، يجب احترامها بنفس القدر', 'Yes, they must be respected equally', 'Ja, ze moeten even goed worden nageleefd', 'Oui, ils doivent être respectés de la même manière', true,  1, NOW()),
(@q25, 'لا، يمكن تجاهلها', 'No, they can be ignored', 'Nee, ze mogen worden genegeerd', 'Non, ils peuvent être ignorés', false, 2, NOW());

INSERT INTO quiz_questions (question_ar, question_en, question_nl, question_fr, question_type, difficulty_level, category_id, is_active, status, created_at, updated_at)
VALUES ('عند تعارض علامة مرور مؤقتة مع علامة دائمة، أيهما يُطاع؟', 'When a temporary sign conflicts with a permanent sign, which one takes precedence?', 'Als een tijdelijk bord in conflict is met een permanent bord, welk bord heeft dan voorrang?', 'En cas de conflit entre un panneau temporaire et un panneau permanent, lequel prime?', 'MULTIPLE_CHOICE', 'HARD', 17, true, 'PUBLISHED', NOW(), NOW());
SET @q26 = LAST_INSERT_ID();
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order, created_at) VALUES
(@q26, 'العلامة المؤقتة لها الأولوية', 'The temporary sign takes precedence', 'Het tijdelijke bord heeft voorrang', 'Le panneau temporaire a la priorité', true,  1, NOW()),
(@q26, 'العلامة الدائمة لها الأولوية', 'The permanent sign takes precedence', 'Het permanente bord heeft voorrang', 'Le panneau permanent a la priorité', false, 2, NOW()),
(@q26, 'يختار السائق', 'The driver chooses', 'De bestuurder kiest', 'Le conducteur choisit', false, 3, NOW());

INSERT INTO quiz_questions (question_ar, question_en, question_nl, question_fr, question_type, difficulty_level, category_id, is_active, status, created_at, updated_at)
VALUES ('ما لون خلفية لافتات أشغال الطريق المؤقتة في بلجيكا؟', 'What background colour do temporary road work signs have in Belgium?', 'Welke achtergrondkleur hebben tijdelijke wegenwerken-borden in België?', 'De quelle couleur est le fond des panneaux de travaux temporaires en Belgique?', 'MULTIPLE_CHOICE', 'EASY', 17, true, 'PUBLISHED', NOW(), NOW());
SET @q27 = LAST_INSERT_ID();
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order, created_at) VALUES
(@q27, 'أصفر', 'Yellow', 'Geel', 'Jaune', true,  1, NOW()),
(@q27, 'أبيض', 'White', 'Wit', 'Blanc', false, 2, NOW()),
(@q27, 'برتقالي', 'Orange', 'Oranje', 'Orange', false, 3, NOW());

-- V92 migration complete: questions seeded for categories G(7), Z(8), M(9), H(17)
