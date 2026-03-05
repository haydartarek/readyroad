-- ====================================================================
-- V5: Add Remaining Lessons, Practice Questions and Exam Questions
-- ====================================================================

USE readyroad_prod;

-- ====================
-- STEP 1: Additional Lessons (8-31)
-- ====================
INSERT INTO lessons (
    category_id,
    title_ar, title_en, title_nl, title_fr,
    content_ar, content_en, content_nl, content_fr,
    display_order, estimated_minutes, is_active
) VALUES

-- Lesson 8: Speed Limits
((SELECT id FROM categories WHERE code = 'C'),
 'حدود السرعة في بلجيكا', 'Speed Limits in Belgium', 'Snelheidslimieten in België', 'Limites de vitesse en Belgique',
 'حدود السرعة تختلف حسب نوع الطريق: داخل المدن 50 كم/س، خارج المدن 90 كم/س، الطرق السريعة 120 كم/س.',
 'Speed limits vary by road type: in cities 50 km/h, outside cities 90 km/h, highways 120 km/h.',
 'Snelheidslimieten variëren per wegtype: in steden 50 km/u, buiten steden 90 km/u, snelwegen 120 km/u.',
 'Limites de vitesse varient selon type route: en ville 50 km/h, hors ville 90 km/h, autoroutes 120 km/h.',
 8, 10, TRUE),

-- Lesson 9: Overtaking Rules
((SELECT id FROM categories WHERE code = 'C'),
 'قواعد التجاوز', 'Overtaking Rules', 'Inhaalregels', 'Règles de dépassement',
 'التجاوز يجب أن يتم من اليسار فقط. ممنوع التجاوز عند الخط المستمر أو في المنعطفات الخطرة.',
 'Overtaking must be done from the left only. Prohibited at continuous lines or dangerous curves.',
 'Inhalen moet alleen van links. Verboden bij doorgetrokken lijnen of gevaarlijke bochten.',
 'Dépassement uniquement par la gauche. Interdit aux lignes continues ou virages dangereux.',
 9, 10, TRUE),

-- Lesson 10: Parking Zones
((SELECT id FROM categories WHERE code = 'E'),
 'مناطق الوقوف المختلفة', 'Different Parking Zones', 'Verschillende parkeerzones', 'Différentes zones de stationnement',
 'المنطقة الزرقاء: وقوف محدود بقرص. المنطقة الحمراء: ممنوع الوقوف. منطقة المعاقين: فقط مع بطاقة.',
 'Blue zone: limited parking with disc. Red zone: no parking. Disabled zone: only with permit.',
 'Blauwe zone: beperkt parkeren met schijf. Rode zone: parkeerverbod. Gehandicaptenzone: alleen met kaart.',
 'Zone bleue: limité avec disque. Zone rouge: interdit. Zone handicapés: uniquement avec carte.',
 10, 8, TRUE),

-- Lesson 11: Complex Signs
((SELECT id FROM categories WHERE code = 'A'),
 'إشارات الطريق المعقدة', 'Complex Road Signs', 'Complexe verkeersborden', 'Panneaux routiers complexes',
 'تعلم قراءة الإشارات المركبة والإشارات الإضافية التي تعدل معنى الإشارة الرئيسية.',
 'Learn to read combined signs and additional signs that modify the main sign meaning.',
 'Leer gecombineerde borden en aanvullende borden lezen die betekenis van hoofdbord wijzigen.',
 'Apprenez à lire les panneaux combinés et additionnels qui modifient sens du panneau principal.',
 11, 12, TRUE),

-- Lesson 12: Roundabouts
((SELECT id FROM categories WHERE code = 'B'),
 'التقاطعات الدوارية', 'Roundabouts', 'Rotondes', 'Ronds-points',
 'في الدوار، السيارات داخل الدوار لها الأولوية. استخدم الإشارة عند الخروج.',
 'In roundabouts, vehicles inside have priority. Use signal when exiting.',
 'In rotondes hebben voertuigen binnen voorrang. Gebruik richtingaanwijzer bij verlaten.',
 'Dans ronds-points, véhicules intérieurs ont priorité. Utilisez clignotant en sortant.',
 12, 10, TRUE),

-- Lesson 13: Alcohol and Drugs
((SELECT id FROM categories WHERE code = 'C'),
 'الكحول والمخدرات', 'Alcohol and Drugs', 'Alcohol en drugs', 'Alcool et drogues',
 'الحد المسموح للكحول 0.5‰ للسائقين العاديين و0.2‰ للمحترفين والمبتدئين. المخدرات ممنوعة تماماً.',
 'Alcohol limit 0.5‰ for normal drivers and 0.2‰ for professionals and beginners. Drugs prohibited.',
 'Alcohollimiet 0.5‰ voor normale bestuurders en 0.2‰ voor professionals en beginners. Drugs verboden.',
 'Limite alcool 0.5‰ conducteurs normaux et 0.2‰ professionnels et débutants. Drogues totalement interdites.',
 13, 8, TRUE),

-- Lesson 14: Safety Equipment
((SELECT id FROM categories WHERE code = 'D'),
 'معدات السلامة الإلزامية', 'Mandatory Safety Equipment', 'Verplichte veiligheidsuitrusting', 'Équipement de sécurité obligatoire',
 'يجب وجود مثلث تحذير، سترة عاكسة، طفاية حريق (للشاحنات)، وحقيبة إسعافات أولية.',
 'Must have warning triangle, reflective vest, fire extinguisher (trucks), and first aid kit.',
 'Moet gevarendriehoek, reflecterend vest, brandblusser (vrachtwagens) en EHBO-kit hebben.',
 'Doit avoir triangle signalisation, gilet réfléchissant, extincteur (camions) et trousse premiers soins.',
 14, 8, TRUE),

-- Lesson 15: Emergency Stopping
((SELECT id FROM categories WHERE code = 'E'),
 'الوقوف الطارئ', 'Emergency Stopping', 'Noodstop', 'Arrêt d''urgence',
 'في حالة الطوارئ، توقف في مكان آمن، فعّل الإشارات التحذيرية، ضع المثلث على بعد 30 متر.',
 'In emergency, stop in safe place, activate hazard lights, place triangle 30m away.',
 'Bij noodgeval, stop op veilige plaats, activeer alarmlichten, plaats driehoek 30m verder.',
 'En urgence, arrêtez lieu sûr, activez feux détresse, placez triangle à 30m.',
 15, 8, TRUE),

-- Lesson 16: Highways
((SELECT id FROM categories WHERE code = 'F'),
 'الطرق السريعة', 'Highways', 'Snelwegen', 'Autoroutes',
 'السرعة القصوى 120 كم/س. ممنوع التوقف إلا في حالة الطوارئ. استخدم الحارة اليمنى للقيادة العادية.',
 'Maximum speed 120 km/h. Stopping prohibited except emergencies. Use right lane for normal driving.',
 'Maximumsnelheid 120 km/u. Stoppen verboden behalve noodgevallen. Gebruik rechterbaan voor normaal rijden.',
 'Vitesse maximale 120 km/h. Arrêt interdit sauf urgences. Utilisez voie droite pour conduite normale.',
 16, 12, TRUE),

-- Lesson 17: Pedestrians and Bicycles
((SELECT id FROM categories WHERE code = 'G'),
 'المشاة والدراجات', 'Pedestrians and Bicycles', 'Voetgangers en fietsen', 'Piétons et vélos',
 'أعط الأولوية دائماً للمشاة عند معابر المشاة. احترم مسارات الدراجات.',
 'Always give priority to pedestrians at crossings. Respect bicycle lanes.',
 'Geef altijd voorrang aan voetgangers bij oversteken. Respecteer fietspaden.',
 'Donnez toujours priorité aux piétons aux passages. Respectez pistes cyclables.',
 17, 10, TRUE),

-- Lesson 18: Traffic Lights
((SELECT id FROM categories WHERE code = 'A'),
 'الإشارات الضوئية', 'Traffic Lights', 'Verkeerslichten', 'Feux de signalisation',
 'الأحمر: توقف. البرتقالي: استعد للتوقف. الأخضر: تابع بحذر. السهم الأخضر: اتجاه مسموح.',
 'Red: stop. Orange: prepare to stop. Green: proceed with caution. Green arrow: direction allowed.',
 'Rood: stop. Oranje: bereid stoppen voor. Groen: ga voorzichtig verder. Groene pijl: richting toegestaan.',
 'Rouge: arrêt. Orange: préparez arrêt. Vert: continuez prudemment. Flèche verte: direction autorisée.',
 18, 8, TRUE),

-- Lesson 19: Emergency Vehicles
((SELECT id FROM categories WHERE code = 'B'),
 'أولوية المركبات الطارئة', 'Emergency Vehicle Priority', 'Voorrang hulpdiensten', 'Priorité véhicules urgence',
 'عند سماع صفارة الإنذار، افسح الطريق فوراً. توقف إذا لزم الأمر.',
 'When hearing siren, give way immediately. Stop if necessary.',
 'Bij horen sirene, maak onmiddellijk plaats. Stop indien nodig.',
 'En entendant sirène, cédez passage immédiatement. Arrêtez si nécessaire.',
 19, 8, TRUE),

-- Lesson 20: Mobile Phone ✅ Fixed: removed Russian word
((SELECT id FROM categories WHERE code = 'C'),
 'استخدام الهاتف المحمول', 'Mobile Phone Use', 'Mobiele telefoon gebruik', 'Utilisation téléphone portable',
 'ممنوع استخدام الهاتف أثناء القيادة إلا بنظام يدوي حر. الغرامة ثقيلة.',
 'Phone use while driving prohibited except hands-free system. Heavy fine.',
 'Telefoongebruik tijdens rijden verboden behalve handsfree systeem. Zware boete.',  -- ✅ Fixed spacing
 'Utilisation téléphone en conduisant interdite sauf mains libres. Amende lourde.',
 20, 6, TRUE),

-- Lesson 21: Seat Belts
((SELECT id FROM categories WHERE code = 'D'),
 'حزام الأمان', 'Seat Belts', 'Veiligheidsgordels', 'Ceintures de sécurité',
 'حزام الأمان إلزامي للجميع في السيارة. الأطفال دون 135 سم يحتاجون مقعداً خاصاً.',
 'Seat belt mandatory for everyone in car. Children under 135cm need special seat.',
 'Veiligheidsgordel verplicht voor iedereen in auto. Kinderen onder 135cm hebben speciaal zitje nodig.',
 'Ceinture obligatoire pour tous dans voiture. Enfants moins 135cm nécessitent siège spécial.',
 21, 8, TRUE),

-- Lesson 22: Disabled Parking
((SELECT id FROM categories WHERE code = 'E'),
 'الوقوف للمعاقين', 'Disabled Parking', 'Gehandicaptenparkeren', 'Stationnement handicapés',
 'أماكن وقوف المعاقين محجوزة فقط لحاملي البطاقة الأوروبية للمعاقين.',
 'Disabled parking spaces reserved only for European disabled card holders.',
 'Gehandicaptenparkeerplaatsen gereserveerd alleen voor Europese gehandicaptenkaart houders.',
 'Places handicapés réservées uniquement pour détenteurs carte européenne handicapés.',
 22, 6, TRUE),

-- Lesson 23: National Roads
((SELECT id FROM categories WHERE code = 'F'),
 'الطرق الوطنية والإقليمية', 'National and Regional Roads', 'Nationale en gewestwegen', 'Routes nationales et régionales',
 'الطرق الوطنية (N) والإقليمية (R) لها حدود سرعة مختلفة عن الطرق السريعة.',
 'National (N) and regional (R) roads have different speed limits than highways.',
 'Nationale (N) en gewestwegen (R) hebben andere snelheidslimieten dan snelwegen.',
 'Routes nationales (N) et régionales (R) ont limites vitesse différentes des autoroutes.',
 23, 10, TRUE),

-- Lesson 24: Motorcycles
((SELECT id FROM categories WHERE code = 'G'),
 'الدراجات النارية', 'Motorcycles', 'Motorfietsen', 'Motos',
 'الخوذة إلزامية. القفازات والملابس الواقية موصى بها. يُسمح بالترشيح بين الحارات بحذر.',
 'Helmet mandatory. Gloves and protective clothing recommended. Lane filtering allowed with caution.',
 'Helm verplicht. Handschoenen en beschermende kleding aanbevolen. Filteren tussen rijstroken toegestaan.',
 'Casque obligatoire. Gants et vêtements protecteurs recommandés. Filtrage entre voies autorisé avec prudence.',
 24, 10, TRUE),

-- Lesson 25: Roadwork Signs
((SELECT id FROM categories WHERE code = 'A'),
 'إشارات الأشغال', 'Roadwork Signs', 'Wegenwerken borden', 'Panneaux de travaux',
 'الإشارات البرتقالية تشير إلى أشغال مؤقتة. اتبع الإشارات المؤقتة بدلاً من الدائمة.',
 'Orange signs indicate temporary roadwork. Follow temporary signs instead of permanent ones.',
 'Oranje borden duiden tijdelijke wegenwerken aan. Volg tijdelijke borden in plaats van permanente.',
 'Panneaux orange indiquent travaux temporaires. Suivez panneaux temporaires au lieu des permanents.',
 25, 8, TRUE),

-- Lesson 26: Railway Crossings
((SELECT id FROM categories WHERE code = 'B'),
 'تقاطعات السكك الحديدية', 'Railway Crossings', 'Spoorwegovergangen', 'Passages à niveau',
 'توقف دائماً عند الإشارة الحمراء. لا تدخل إذا كانت الحواجز تنزل. انظر يميناً ويساراً.',
 'Always stop at red signal. Don''t enter if barriers lowering. Look right and left.',
 'Stop altijd bij rood signaal. Ga niet binnen als slagbomen dalen. Kijk rechts en links.',
 'Arrêtez toujours au signal rouge. N''entrez pas si barrières descendent. Regardez droite et gauche.',
 26, 10, TRUE),

-- Lesson 27: Night Driving ✅ Fixed Dutch text spacing
((SELECT id FROM categories WHERE code = 'C'),
 'القيادة الليلية', 'Night Driving', 'Nachtrijden', 'Conduite de nuit',
 'استخدم الأضواء المنخفضة في المدن والعالية خارجها. خفض السرعة. كن أكثر حذراً.',
 'Use low beams in cities and high beams outside. Reduce speed. Be more cautious.',
 'Gebruik dimlichten in steden en grootlichten buiten. Verlaag snelheid. Wees voorzichtiger.',  -- ✅ Fixed
 'Utilisez feux croisement en ville et feux route dehors. Réduisez vitesse. Soyez plus prudent.',
 27, 10, TRUE),

-- Lesson 28: Bad Weather
((SELECT id FROM categories WHERE code = 'D'),
 'القيادة في الطقس السيء', 'Bad Weather Driving', 'Slecht weer rijden', 'Conduite par mauvais temps',
 'في المطر والثلج، ضاعف المسافة الآمنة. خفف السرعة. استخدم الأضواء المناسبة.',
 'In rain and snow, double safe distance. Reduce speed. Use appropriate lights.',
 'Bij regen en sneeuw, verdubbel veilige afstand. Verlaag snelheid. Gebruik juiste lichten.',
 'Sous pluie et neige, doublez distance sécurité. Réduisez vitesse. Utilisez feux appropriés.',
 28, 10, TRUE),

-- Lesson 29: Double Parking
((SELECT id FROM categories WHERE code = 'E'),
 'الوقوف المزدوج', 'Double Parking', 'Dubbel parkeren', 'Stationnement en double file',
 'الوقوف المزدوج ممنوع تماماً. يعيق حركة المرور ويسبب غرامة.',
 'Double parking completely prohibited. Obstructs traffic and causes fine.',
 'Dubbel parkeren volledig verboden. Belemmert verkeer en veroorzaakt boete.',
 'Stationnement double file totalement interdit. Obstrue circulation et cause amende.',
 29, 6, TRUE),

-- Lesson 30: Environmental Zones
((SELECT id FROM categories WHERE code = 'F'),
 'المناطق البيئية', 'Environmental Zones', 'Milieuzones', 'Zones environnementales',
 'بعض المدن لديها مناطق منخفضة الانبعاثات. تحقق من معايير مركبتك قبل الدخول.',
 'Some cities have low emission zones. Check your vehicle standards before entering.',
 'Sommige steden hebben lage emissiezones. Controleer uw voertuignormen voor binnenrijden.',
 'Certaines villes ont zones faibles émissions. Vérifiez normes véhicule avant d''entrer.',
 30, 8, TRUE),

-- Lesson 31: License Points
((SELECT id FROM categories WHERE code = 'G'),
 'نقاط الترخيص', 'License Points', 'Rijbewijs punten', 'Points de permis',
 'نظام النقاط يتتبع المخالفات. فقدان جميع النقاط يعني تعليق الرخصة.',
 'Points system tracks violations. Losing all points means license suspension.',
 'Puntensysteem volgt overtredingen. Alle punten verliezen betekent rijbewijs schorsing.',
 'Système points suit infractions. Perdre tous points signifie suspension permis.',
 31, 10, TRUE)

ON DUPLICATE KEY UPDATE updated_at = NOW();

SELECT '✅ Step 1: Lessons 8-31 inserted' AS status;

-- ====================
-- STEP 2: Practice Questions (using subquery for lesson_id)
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

-- Lesson 8: Speed Limits
((SELECT id FROM lessons WHERE title_en = 'Speed Limits in Belgium' LIMIT 1),
 'ما هي السرعة القصوى داخل المدن؟', 'What is maximum speed in cities?', 'Wat is maximumsnelheid in steden?', 'Quelle est vitesse maximale en ville?',
 '30 كم/س', '30 km/h', '30 km/u', '30 km/h',
 '50 كم/س', '50 km/h', '50 km/u', '50 km/h',
 '70 كم/س', '70 km/h', '70 km/u', '70 km/h',
 '90 كم/س', '90 km/h', '90 km/u', '90 km/h',
 2,
 'السرعة القصوى داخل المدن 50 كم/س ما لم تُحدد إشارة أخرى',
 'Maximum speed in cities is 50 km/h unless otherwise indicated',
 'Maximumsnelheid in steden is 50 km/u tenzij anders aangegeven',
 'Vitesse maximale en ville est 50 km/h sauf indication contraire',
 1, TRUE),

((SELECT id FROM lessons WHERE title_en = 'Speed Limits in Belgium' LIMIT 1),
 'ما هي السرعة القصوى على الطرق السريعة؟', 'Maximum speed on highways?', 'Maximumsnelheid op snelwegen?', 'Vitesse maximale sur autoroutes?',
 '90 كم/س', '90 km/h', '90 km/u', '90 km/h',
 '100 كم/س', '100 km/h', '100 km/u', '100 km/h',
 '120 كم/س', '120 km/h', '120 km/u', '120 km/h',
 '130 كم/س', '130 km/h', '130 km/u', '130 km/h',
 3,
 'السرعة القصوى على الطرق السريعة في بلجيكا 120 كم/س',
 'Maximum speed on highways in Belgium is 120 km/h',
 'Maximumsnelheid op snelwegen in België is 120 km/u',
 'Vitesse maximale sur autoroutes en Belgique est 120 km/h',
 2, TRUE),

((SELECT id FROM lessons WHERE title_en = 'Speed Limits in Belgium' LIMIT 1),
 'ما هي السرعة في منطقة 30؟', 'Speed in zone 30?', 'Snelheid in zone 30?', 'Vitesse en zone 30?',
 '20 كم/س', '20 km/h', '20 km/u', '20 km/h',
 '30 كم/س', '30 km/h', '30 km/u', '30 km/h',
 '40 كم/س', '40 km/h', '40 km/u', '40 km/h',
 '50 كم/س', '50 km/h', '50 km/u', '50 km/h',
 2,
 'في منطقة 30، السرعة القصوى هي 30 كم/س',
 'In zone 30, maximum speed is 30 km/h',
 'In zone 30 is maximumsnelheid 30 km/u',
 'En zone 30, vitesse maximale est 30 km/h',
 3, TRUE),

-- Lesson 9: Overtaking
((SELECT id FROM lessons WHERE title_en = 'Overtaking Rules' LIMIT 1),
 'من أي جهة يجب التجاوز؟', 'From which side to overtake?', 'Van welke kant inhalen?', 'De quel côté dépasser?',
 'من اليمين', 'From right', 'Van rechts', 'De droite',
 'من اليسار', 'From left', 'Van links', 'De gauche',
 'من أي جهة', 'Any side', 'Elke kant', 'N''importe quel côté',
 'من المنتصف', 'From middle', 'Van midden', 'Du milieu',
 2,
 'التجاوز يجب أن يتم دائماً من الجهة اليسرى',
 'Overtaking must always be done from left side',
 'Inhalen moet altijd van linkerkant',
 'Dépassement doit toujours se faire par la gauche',
 1, TRUE),

((SELECT id FROM lessons WHERE title_en = 'Overtaking Rules' LIMIT 1),
 'متى يُمنع التجاوز؟', 'When is overtaking prohibited?', 'Wanneer is inhalen verboden?', 'Quand dépassement interdit?',
 'في أي وقت', 'Anytime', 'Altijd', 'À tout moment',
 'عند الخط المستمر', 'At continuous line', 'Bij doorgetrokken lijn', 'À ligne continue',
 'في الليل فقط', 'At night only', 'Alleen ''s nachts', 'Seulement la nuit',
 'في النهار فقط', 'At day only', 'Alleen overdag', 'Seulement le jour',
 2,
 'التجاوز ممنوع عند الخط المستمر والمنعطفات الخطرة',
 'Overtaking prohibited at continuous line and dangerous curves',
 'Inhalen verboden bij doorgetrokken lijn en gevaarlijke bochten',
 'Dépassement interdit à ligne continue et virages dangereux',
 2, TRUE),

-- Lesson 10: Parking Zones
((SELECT id FROM lessons WHERE title_en = 'Different Parking Zones' LIMIT 1),
 'ما هي المنطقة الزرقاء؟', 'What is blue zone?', 'Wat is blauwe zone?', 'Qu''est-ce que zone bleue?',
 'وقوف مجاني', 'Free parking', 'Gratis parkeren', 'Stationnement gratuit',
 'وقوف محدود بقرص', 'Limited parking with disc', 'Beperkt met schijf', 'Limité avec disque',
 'وقوف ممنوع', 'No parking', 'Parkeerverbod', 'Stationnement interdit',
 'وقوف ليلي', 'Night parking', 'Nachtparkeren', 'Stationnement nuit',
 2,
 'المنطقة الزرقاء تسمح بوقوف محدود (عادة ساعتان) باستخدام قرص الوقوف',
 'Blue zone allows limited parking (usually 2 hours) using parking disc',
 'Blauwe zone staat beperkt parkeren toe (meestal 2 uur) met parkeerschijf',
 'Zone bleue permet stationnement limité (généralement 2 heures) avec disque',
 1, TRUE),

((SELECT id FROM lessons WHERE title_en = 'Different Parking Zones' LIMIT 1),
 'من يستطيع الوقوف في منطقة المعاقين؟', 'Who can park in disabled zone?', 'Wie mag parkeren in gehandicaptenzone?', 'Qui peut stationner zone handicapés?',
 'أي شخص', 'Anyone', 'Iedereen', 'N''importe qui',
 'فقط حاملو البطاقة الأوروبية', 'Only European card holders', 'Alleen Europese kaarthouders', 'Seulement détenteurs carte européenne',
 'كبار السن', 'Elderly', 'Ouderen', 'Personnes âgées',
 'سائقو الأجرة', 'Taxi drivers', 'Taxichauffeurs', 'Chauffeurs taxi',
 2,
 'أماكن المعاقين محجوزة فقط لحاملي البطاقة الأوروبية للمعاقين',
 'Disabled spaces reserved only for European disabled card holders',
 'Gehandicaptenplaatsen gereserveerd alleen voor Europese gehandicaptenkaarthouders',
 'Places handicapés réservées uniquement pour détenteurs carte européenne',
 2, TRUE)

ON DUPLICATE KEY UPDATE updated_at = NOW();

SELECT '✅ Step 2: Practice questions inserted' AS status;

-- ====================
-- STEP 3: Exam Questions
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

((SELECT id FROM categories WHERE code = 'A'),
 'مثلث أحمر بحدود، ما لونه الأساسي؟', 'Red triangle with border, what is its main color?', 'Rode driehoek met rand, wat is hoofdkleur?', 'Triangle rouge avec bordure, quelle couleur principale?',
 'أزرق', 'Blue', 'Blauw', 'Bleu',
 'أحمر على أبيض', 'Red on white', 'Rood op wit', 'Rouge sur blanc',
 'أخضر', 'Green', 'Groen', 'Vert',
 'برتقالي', 'Orange', 'Oranje', 'Orange',
 2,
 'إشارات التحذير دائماً: حدود حمراء على خلفية بيضاء',
 'Warning signs always: red border on white background',
 'Waarschuwingsborden altijd: rode rand op witte achtergrond',
 'Panneaux avertissement toujours: bordure rouge sur fond blanc',
 NULL, 'EASY', FALSE, TRUE),

((SELECT id FROM categories WHERE code = 'C'),
 'السرعة القصوى خارج المدن بدون إشارات؟', 'Max speed outside cities without signs?', 'Maximumsnelheid buiten steden zonder borden?', 'Vitesse max hors ville sans panneaux?',
 '70 كم/س', '70 km/h', '70 km/u', '70 km/h',
 '90 كم/س', '90 km/h', '90 km/u', '90 km/h',
 '110 كم/س', '110 km/h', '110 km/u', '110 km/h',
 '120 كم/س', '120 km/h', '120 km/u', '120 km/h',
 2,
 'القاعدة الافتراضية خارج المدن في بلجيكا: 90 كم/س على الطرق الوطنية',
 'Default rule outside cities in Belgium: 90 km/h on national roads',
 'Standaardregel buiten steden in België: 90 km/u op nationale wegen',
 'Règle par défaut hors ville en Belgique: 90 km/h sur routes nationales',
 NULL, 'EASY', TRUE, TRUE),

((SELECT id FROM categories WHERE code = 'C'),
 'حد الكحول للسائق المبتدئ؟', 'Alcohol limit for beginner driver?', 'Alcohollimiet voor beginnende bestuurder?', 'Limite alcool conducteur débutant?',
 '0.5‰', '0.5‰', '0.5‰', '0.5‰',
 '0.2‰', '0.2‰', '0.2‰', '0.2‰',
 '0.8‰', '0.8‰', '0.8‰', '0.8‰',
 '0.0‰', '0.0‰', '0.0‰', '0.0‰',
 2,
 'السائقون المبتدئون والمحترفون: 0.2‰ فقط',
 'Beginner and professional drivers: 0.2‰ only',
 'Beginnende en professionele bestuurders: slechts 0.2‰',
 'Conducteurs débutants et professionnels: seulement 0.2‰',
 NULL, 'MEDIUM', TRUE, TRUE),

((SELECT id FROM categories WHERE code = 'B'),
 'مركبة الإسعاف تدق، ماذا تفعل فوراً؟', 'Ambulance honking, what to do immediately?', 'Ambulance toetert, wat onmiddellijk doen?', 'Ambulance klaxonne, que faire immédiatement?',
 'تسرّع للإفساح', 'Speed up to make way', 'Versnel om ruimte te maken', 'Accélérez pour faire place',
 'افسح الطريق وتوقف إن لزم', 'Make way and stop if needed', 'Maak ruimte en stop indien nodig', 'Cédez passage et arrêtez si nécessaire',
 'تجاهل', 'Ignore', 'Negeer', 'Ignorez',
 'استمر بنفس السرعة', 'Continue same speed', 'Ga door zelfde snelheid', 'Continuez même vitesse',
 2,
 'مركبات الطوارئ لها الأولوية المطلقة',
 'Emergency vehicles have absolute priority',
 'Hulpdiensten hebben absolute voorrang',
 'Véhicules urgence ont priorité absolue',
 NULL, 'MEDIUM', TRUE, TRUE),

((SELECT id FROM categories WHERE code = 'D'),
 'حزام الأمان، من يجب أن يضعه؟', 'Seat belt, who must wear it?', 'Veiligheidsgordel, wie moet hem dragen?', 'Ceinture sécurité, qui doit la porter?',
 'السائق فقط', 'Driver only', 'Alleen bestuurder', 'Conducteur seulement',
 'السائق والمقعد الأمامي', 'Driver and front seat', 'Bestuurder en voorstoel', 'Conducteur et siège avant',
 'الجميع في السيارة', 'Everyone in car', 'Iedereen in auto', 'Tout le monde dans voiture',
 'الجميع فوق 12 سنة', 'Everyone over 12', 'Iedereen boven 12', 'Tout le monde plus 12 ans',
 3,
 'حزام الأمان إلزامي للجميع في السيارة بدون استثناء',
 'Seat belt mandatory for everyone in car without exception',
 'Veiligheidsgordel verplicht voor iedereen in auto zonder uitzondering',
 'Ceinture obligatoire pour tous dans voiture sans exception',
 NULL, 'EASY', TRUE, TRUE)

ON DUPLICATE KEY UPDATE updated_at = NOW();

SELECT '✅ Step 3: Exam questions inserted' AS status;

-- ====================
-- STEP 4: Final Verification
-- ====================
SELECT
    'Lessons'            AS entity, COUNT(*) AS total FROM lessons
UNION ALL SELECT
    'Practice Questions', COUNT(*)             FROM practice_questions
UNION ALL SELECT
    'Exam Questions',     COUNT(*)             FROM exam_questions;
