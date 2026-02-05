-- V74: Add Additional 50 Questions for Exam Simulation
-- Modified to match current schema (removed: is_active, status, context_specific, requires_sign_image)

-- Speed Limit Questions
INSERT INTO quiz_questions (question_ar, question_en, question_nl, question_fr, question_type, difficulty_level, category_id, created_at, updated_at)
VALUES
('ما هي السرعة القصوى داخل المدن في بلجيكا؟', 'What is the maximum speed limit inside cities in Belgium?', 'Wat is de maximumsnelheid binnen steden in België?', 'Quelle est la vitesse maximale dans les villes en Belgique?', 'MULTIPLE_CHOICE', 'EASY', 3, NOW(), NOW()),
('ما السرعة القصوى على الطرق السريعة في الطقس الجاف؟', 'What is the maximum speed on highways in dry weather?', 'Wat is de maximumsnelheid op snelwegen bij droog weer?', 'Quelle est la vitesse maximale sur les autoroutes par temps sec?', 'MULTIPLE_CHOICE', 'EASY', 3, NOW(), NOW()),
('كيف تؤثر الأمطار على السرعة الآمنة؟', 'How does rain affect safe speed?', 'Hoe beïnvloedt regen de veilige snelheid?', 'Comment la pluie affecte-t-elle la vitesse de sécurité?', 'MULTIPLE_CHOICE', 'MEDIUM', 3, NOW(), NOW()),
('ما السرعة المناسبة بالقرب من المدارس؟', 'What is the appropriate speed near schools?', 'Wat is de geschikte snelheid bij scholen?', 'Quelle est la vitesse appropriée près des écoles?', 'MULTIPLE_CHOICE', 'EASY', 3, NOW(), NOW()),
('متى يجب تخفيض السرعة إلى أقل من الحد الأقصى؟', 'When should you reduce speed below the limit?', 'Wanneer moet je de snelheid verlagen onder de limiet?', 'Quand devez-vous réduire la vitesse en dessous de la limite?', 'MULTIPLE_CHOICE', 'MEDIUM', 3, NOW(), NOW()),

-- Traffic Sign Questions
('ماذا يعني اللون الأزرق في إشارات المرور؟', 'What does the blue color mean in traffic signs?', 'Wat betekent de blauwe kleur bij verkeersborden?', 'Que signifie la couleur bleue dans les panneaux de circulation?', 'MULTIPLE_CHOICE', 'EASY', 1, NOW(), NOW()),
('ما معنى الإشارة المثلثية المقلوبة؟', 'What does an inverted triangular sign mean?', 'Wat betekent een omgekeerd driehoekig bord?', 'Que signifie un panneau triangulaire inversé?', 'MULTIPLE_CHOICE', 'EASY', 1, NOW(), NOW()),
('ماذا تعني الإشارة الدائرية الحمراء مع خط أبيض؟', 'What does a red circular sign with white bar mean?', 'Wat betekent een rood rond bord met witte balk?', 'Que signifie un panneau circulaire rouge avec barre blanche?', 'MULTIPLE_CHOICE', 'EASY', 1, NOW(), NOW()),
('كيف تتعرف على إشارات الطريق السريع؟', 'How do you recognize highway signs?', 'Hoe herken je snelwegborden?', 'Comment reconnaissez-vous les panneaux d''autoroute?', 'MULTIPLE_CHOICE', 'EASY', 1, NOW(), NOW()),
('ما الفرق بين الإشارة التحذيرية والإلزامية؟', 'What is the difference between warning and mandatory signs?', 'Wat is het verschil tussen waarschuwings- en verplichte borden?', 'Quelle est la différence entre les panneaux d''avertissement et obligatoires?', 'MULTIPLE_CHOICE', 'MEDIUM', 1, NOW(), NOW()),
('ماذا تعني الأسهم على الطريق؟', 'What do arrows on the road mean?', 'Wat betekenen pijlen op de weg?', 'Que signifient les flèches sur la route?', 'MULTIPLE_CHOICE', 'EASY', 1, NOW(), NOW()),
('كيف تتصرف عند رؤية إشارة الأشغال؟', 'How do you behave when seeing road work signs?', 'Hoe gedraag je je bij het zien van werkzaamheidsborden?', 'Comment vous comportez-vous en voyant des panneaux de travaux?', 'MULTIPLE_CHOICE', 'MEDIUM', 1, NOW(), NOW()),

-- Priority Rules Questions
('من له الأولوية في المواقف غير المحددة؟', 'Who has priority in undefined situations?', 'Wie heeft voorrang in ongedefinieerde situaties?', 'Qui a la priorité dans les situations non définies?', 'MULTIPLE_CHOICE', 'HARD', 2, NOW(), NOW()),
('كيف تعطي الأولوية للحافلات؟', 'How do you give priority to buses?', 'Hoe geef je voorrang aan bussen?', 'Comment donnez-vous la priorité aux bus?', 'MULTIPLE_CHOICE', 'MEDIUM', 2, NOW(), NOW()),
('ما قواعد الأولوية عند اندماج الحارات؟', 'What are priority rules when lanes merge?', 'Wat zijn de voorrangsregels bij samenvoegen van rijstroken?', 'Quelles sont les règles de priorité lors de la fusion des voies?', 'MULTIPLE_CHOICE', 'MEDIUM', 2, NOW(), NOW()),
('متى تعطي الأولوية للدراجات؟', 'When do you give priority to bicycles?', 'Wanneer geef je voorrang aan fietsen?', 'Quand donnez-vous la priorité aux vélos?', 'MULTIPLE_CHOICE', 'MEDIUM', 2, NOW(), NOW()),
('كيف تتعامل مع سيارات الطوارئ؟', 'How do you deal with emergency vehicles?', 'Hoe ga je om met hulpdiensten?', 'Comment gérez-vous les véhicules d''urgence?', 'MULTIPLE_CHOICE', 'EASY', 2, NOW(), NOW()),
('ما معنى الخطوط البيضاء المتقطعة؟', 'What do broken white lines mean?', 'Wat betekenen onderbroken witte lijnen?', 'Que signifient les lignes blanches discontinues?', 'MULTIPLE_CHOICE', 'EASY', 2, NOW(), NOW()),
('كيف تتصرف في تقاطع بأربعة اتجاهات؟', 'How do you behave at a four-way intersection?', 'Hoe gedraag je je op een kruispunt met vier richtingen?', 'Comment vous comportez-vous à un carrefour à quatre directions?', 'MULTIPLE_CHOICE', 'MEDIUM', 2, NOW(), NOW()),

-- Parking Questions
('ما المسافة القانونية للوقوف من الزاوية؟', 'What is the legal distance to park from a corner?', 'Wat is de wettelijke afstand om vanaf een hoek te parkeren?', 'Quelle est la distance légale pour se garer d''un coin?', 'MULTIPLE_CHOICE', 'MEDIUM', 4, NOW(), NOW()),
('كيف توقف سيارتك بشكل موازي؟', 'How do you parallel park?', 'Hoe parkeer je parallel?', 'Comment vous garez-vous en parallèle?', 'MULTIPLE_CHOICE', 'MEDIUM', 4, NOW(), NOW()),
('متى يُسمح بالوقوف على الرصيف؟', 'When is parking on the sidewalk allowed?', 'Wanneer is parkeren op de stoep toegestaan?', 'Quand le stationnement sur le trottoir est-il autorisé?', 'MULTIPLE_CHOICE', 'MEDIUM', 4, NOW(), NOW()),
('ما معنى الخطوط الصفراء على جانب الطريق؟', 'What do yellow lines on the roadside mean?', 'Wat betekenen gele lijnen aan de kant van de weg?', 'Que signifient les lignes jaunes sur le bord de la route?', 'MULTIPLE_CHOICE', 'EASY', 4, NOW(), NOW()),
('كيف تستخدم فرامل الانتظار بشكل صحيح؟', 'How do you use the parking brake correctly?', 'Hoe gebruik je de handrem correct?', 'Comment utilisez-vous le frein de stationnement correctement?', 'MULTIPLE_CHOICE', 'EASY', 4, NOW(), NOW()),
('أين يُمنع الوقوف بشكل مطلق؟', 'Where is parking absolutely prohibited?', 'Waar is parkeren absoluut verboden?', 'Où le stationnement est-il absolument interdit?', 'MULTIPLE_CHOICE', 'EASY', 4, NOW(), NOW()),
('ما الفرق بين الوقوف والتوقف؟', 'What is the difference between parking and stopping?', 'Wat is het verschil tussen parkeren en stoppen?', 'Quelle est la différence entre stationner et s''arrêter?', 'MULTIPLE_CHOICE', 'MEDIUM', 4, NOW(), NOW()),

-- Vehicle Equipment Questions
('ما هو الحد الأدنى لعمق مداس الإطار؟', 'What is the minimum tire tread depth?', 'Wat is de minimale profieldiepte van banden?', 'Quelle est la profondeur minimale de la bande de roulement?', 'MULTIPLE_CHOICE', 'MEDIUM', 5, NOW(), NOW()),
('متى يجب استبدال مساحات الزجاج؟', 'When should windshield wipers be replaced?', 'Wanneer moeten ruitenwissers worden vervangen?', 'Quand faut-il remplacer les essuie-glaces?', 'MULTIPLE_CHOICE', 'EASY', 5, NOW(), NOW()),
('كيف تتحقق من مستوى زيت المحرك؟', 'How do you check engine oil level?', 'Hoe controleer je het motorolieniveau?', 'Comment vérifiez-vous le niveau d''huile moteur?', 'MULTIPLE_CHOICE', 'EASY', 5, NOW(), NOW()),
('ما أهمية فحص الأضواء قبل القيادة؟', 'What is the importance of checking lights before driving?', 'Wat is het belang van het controleren van lichten voor het rijden?', 'Quelle est l''importance de vérifier les feux avant de conduire?', 'MULTIPLE_CHOICE', 'EASY', 5, NOW(), NOW()),
('متى يجب استخدام المثلث التحذيري؟', 'When should the warning triangle be used?', 'Wanneer moet de waarschuwingsdriehoek worden gebruikt?', 'Quand le triangle de signalisation doit-il être utilisé?', 'MULTIPLE_CHOICE', 'EASY', 5, NOW(), NOW()),
('ما وظيفة نظام ABS في السيارة؟', 'What is the function of ABS system in a car?', 'Wat is de functie van het ABS-systeem in een auto?', 'Quelle est la fonction du système ABS dans une voiture?', 'MULTIPLE_CHOICE', 'MEDIUM', 5, NOW(), NOW()),
('كيف تعرف أن الفرامل تحتاج صيانة؟', 'How do you know brakes need maintenance?', 'Hoe weet je dat remmen onderhoud nodig hebben?', 'Comment savez-vous que les freins ont besoin d''entretien?', 'MULTIPLE_CHOICE', 'MEDIUM', 5, NOW(), NOW()),
('ما أهمية ضبط المرايا بشكل صحيح؟', 'What is the importance of adjusting mirrors correctly?', 'Wat is het belang van het correct afstellen van spiegels?', 'Quelle est l''importance d''ajuster correctement les rétroviseurs?', 'MULTIPLE_CHOICE', 'EASY', 5, NOW(), NOW()),

-- Environmental Questions
('كيف تقلل انبعاثات العادم؟', 'How do you reduce exhaust emissions?', 'Hoe verminder je uitlaatgassen?', 'Comment réduisez-vous les émissions d''échappement?', 'MULTIPLE_CHOICE', 'MEDIUM', 6, NOW(), NOW()),
('ما تأثير الإطارات غير المنفوخة على البيئة؟', 'What is the impact of underinflated tires on environment?', 'Wat is de impact van slecht opgepompte banden op het milieu?', 'Quel est l''impact des pneus sous-gonflés sur l''environnement?', 'MULTIPLE_CHOICE', 'MEDIUM', 6, NOW(), NOW()),
('كيف تمارس القيادة الاقتصادية؟', 'How do you practice economical driving?', 'Hoe oefen je economisch rijden?', 'Comment pratiquez-vous la conduite économique?', 'MULTIPLE_CHOICE', 'MEDIUM', 6, NOW(), NOW()),
('ما فائدة إيقاف المحرك أثناء الانتظار الطويل؟', 'What is the benefit of stopping engine during long wait?', 'Wat is het voordeel van het stoppen van de motor tijdens lang wachten?', 'Quel est l''avantage d''arrêter le moteur pendant une longue attente?', 'MULTIPLE_CHOICE', 'EASY', 6, NOW(), NOW()),
('كيف تختار وقود صديق للبيئة؟', 'How do you choose eco-friendly fuel?', 'Hoe kies je milieuvriendelijke brandstof?', 'Comment choisissez-vous un carburant écologique?', 'MULTIPLE_CHOICE', 'MEDIUM', 6, NOW(), NOW()),
('ما تأثير القيادة العدوانية على البيئة؟', 'What is the impact of aggressive driving on environment?', 'Wat is de impact van agressief rijden op het milieu?', 'Quel est l''impact de la conduite agressive sur l''environnement?', 'MULTIPLE_CHOICE', 'MEDIUM', 6, NOW(), NOW()),
('كيف تساهم في تقليل الازدحام المروري؟', 'How do you contribute to reducing traffic congestion?', 'Hoe draag je bij aan het verminderen van verkeersopstoppingen?', 'Comment contribuez-vous à réduire les embouteillages?', 'MULTIPLE_CHOICE', 'MEDIUM', 6, NOW(), NOW()),

-- Additional Situational Questions
('كيف تتعامل مع الانزلاق المائي؟', 'How do you handle hydroplaning?', 'Hoe ga je om met aquaplaning?', 'Comment gérez-vous l''aquaplanage?', 'MULTIPLE_CHOICE', 'HARD', 1, NOW(), NOW()),
('ما الإجراءات عند وقوع حادث؟', 'What are the procedures when an accident occurs?', 'Wat zijn de procedures bij een ongeval?', 'Quelles sont les procédures lors d''un accident?', 'MULTIPLE_CHOICE', 'MEDIUM', 5, NOW(), NOW()),
('كيف تقود بأمان في الضباب الكثيف؟', 'How do you drive safely in dense fog?', 'Hoe rijd je veilig in dichte mist?', 'Comment conduire en toute sécurité dans un brouillard dense?', 'MULTIPLE_CHOICE', 'HARD', 1, NOW(), NOW()),
('متى يجب استخدام الإشارات الضوئية؟', 'When should you use turn signals?', 'Wanneer moet je richtingaanwijzers gebruiken?', 'Quand devez-vous utiliser les clignotants?', 'MULTIPLE_CHOICE', 'EASY', 1, NOW(), NOW()),
('كيف تتصرف عند فشل الفرامل؟', 'How do you behave when brakes fail?', 'Hoe gedraag je je bij remfalen?', 'Comment vous comportez-vous en cas de défaillance des freins?', 'MULTIPLE_CHOICE', 'HARD', 5, NOW(), NOW()),
('ما المسافة الآمنة في الطقس الممطر؟', 'What is the safe distance in rainy weather?', 'Wat is de veilige afstand bij regenachtig weer?', 'Quelle est la distance de sécurité par temps pluvieux?', 'MULTIPLE_CHOICE', 'MEDIUM', 3, NOW(), NOW()),
('كيف تستخدم ناقل الحركة بشكل صحيح؟', 'How do you use the gearbox correctly?', 'Hoe gebruik je de versnellingsbak correct?', 'Comment utilisez-vous la boîte de vitesses correctement?', 'MULTIPLE_CHOICE', 'MEDIUM', 5, NOW(), NOW());
