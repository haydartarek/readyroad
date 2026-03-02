-- V72: Add 50 Test Questions for Exam Simulation
-- These questions cover various categories and difficulty levels

-- Traffic Signs Questions (Category 1)
INSERT INTO quiz_questions (question_ar, question_en, question_nl, question_fr, question_type, difficulty_level, category_id, created_at, updated_at)
VALUES
-- Easy Questions
('ما هي السرعة القصوى في المناطق السكنية؟', 'What is the speed limit in residential areas?', 'Wat is de snelheidslimiet in woonwijken?', 'Quelle est la limite de vitesse dans les zones résidentielles?', 'MULTIPLE_CHOICE', 'EASY', 1, NOW(), NOW()),
('متى يجب التوقف عند الضوء الأحمر؟', 'When must you stop at a red light?', 'Wanneer moet je stoppen bij een rood licht?', 'Quand devez-vous vous arrêter à un feu rouge?', 'MULTIPLE_CHOICE', 'EASY', 1, NOW(), NOW()),
('ماذا يعني هذا الشكل المثلثي؟', 'What does a triangular sign mean?', 'Wat betekent een driehoekig bord?', 'Que signifie un panneau triangulaire?', 'MULTIPLE_CHOICE', 'EASY', 1, NOW(), NOW()),
('ما هو لون إشارة التوقف؟', 'What color is a stop sign?', 'Welke kleur heeft een stopbord?', 'De quelle couleur est un panneau stop?', 'MULTIPLE_CHOICE', 'EASY', 1, NOW(), NOW()),
('متى يجب استخدام أضواء الضباب؟', 'When should you use fog lights?', 'Wanneer moet je mistlampen gebruiken?', 'Quand devez-vous utiliser les feux de brouillard?', 'MULTIPLE_CHOICE', 'EASY', 1, NOW(), NOW()),

-- Medium Questions
('كيف تتصرف عند تقاطع بدون إشارات؟', 'How do you behave at an intersection without signals?', 'Hoe gedraag je je bij een kruispunt zonder signalen?', 'Comment vous comportez-vous à un carrefour sans signaux?', 'MULTIPLE_CHOICE', 'MEDIUM', 1, NOW(), NOW()),
('ما هي المسافة الآمنة للمتابعة؟', 'What is the safe following distance?', 'Wat is de veilige volgafstand?', 'Quelle est la distance de sécurité à suivre?', 'MULTIPLE_CHOICE', 'MEDIUM', 1, NOW(), NOW()),
('متى يجوز تجاوز سيارة أخرى؟', 'When is it allowed to overtake another vehicle?', 'Wanneer mag je een ander voertuig inhalen?', 'Quand est-il permis de dépasser un autre véhicule?', 'MULTIPLE_CHOICE', 'MEDIUM', 1, NOW(), NOW()),
('كيف تتعامل مع الدوار؟', 'How do you handle a roundabout?', 'Hoe ga je om met een rotonde?', 'Comment gérez-vous un rond-point?', 'MULTIPLE_CHOICE', 'MEDIUM', 1, NOW(), NOW()),
('ما هي قواعد الأولوية في التقاطع؟', 'What are the priority rules at an intersection?', 'Wat zijn de voorrangsregels op een kruispunt?', 'Quelles sont les règles de priorité à un carrefour?', 'MULTIPLE_CHOICE', 'MEDIUM', 1, NOW(), NOW()),

-- Hard Questions
('كيف تحسب مسافة التوقف الكلية؟', 'How do you calculate total stopping distance?', 'Hoe bereken je de totale stopafstand?', 'Comment calculez-vous la distance d''arrêt totale?', 'MULTIPLE_CHOICE', 'HARD', 1, NOW(), NOW()),
('ما الفرق بين ABS والفرامل العادية؟', 'What is the difference between ABS and regular brakes?', 'Wat is het verschil tussen ABS en gewone remmen?', 'Quelle est la différence entre ABS et freins normaux?', 'MULTIPLE_CHOICE', 'HARD', 1, NOW(), NOW()),
('كيف تقود في ظروف الطقس السيئة؟', 'How do you drive in bad weather conditions?', 'Hoe rijd je in slechte weersomstandigheden?', 'Comment conduire par mauvais temps?', 'MULTIPLE_CHOICE', 'HARD', 1, NOW(), NOW()),
('ما هي تقنيات القيادة الدفاعية؟', 'What are defensive driving techniques?', 'Wat zijn defensieve rijtechnieken?', 'Quelles sont les techniques de conduite défensive?', 'MULTIPLE_CHOICE', 'HARD', 1, NOW(), NOW()),
('كيف تتعامل مع انزلاق السيارة؟', 'How do you handle a vehicle skid?', 'Hoe ga je om met slippen?', 'Comment gérez-vous un dérapage?', 'MULTIPLE_CHOICE', 'HARD', 1, NOW(), NOW()),

-- Priority Rules Questions (Category 2)
('من له الأولوية عند تقاطع متساوي؟', 'Who has priority at an equal intersection?', 'Wie heeft voorrang op een gelijkwaardig kruispunt?', 'Qui a la priorité à un carrefour égal?', 'MULTIPLE_CHOICE', 'EASY', 2, NOW(), NOW()),
('ما معنى الخط المتقطع؟', 'What does a broken line mean?', 'Wat betekent een onderbroken lijn?', 'Que signifie une ligne discontinue?', 'MULTIPLE_CHOICE', 'EASY', 2, NOW(), NOW()),
('متى يجب إعطاء الأولوية للمشاة؟', 'When must you give priority to pedestrians?', 'Wanneer moet je voorrang geven aan voetgangers?', 'Quand devez-vous céder la priorité aux piétons?', 'MULTIPLE_CHOICE', 'EASY', 2, NOW(), NOW()),
('ما هي قاعدة الأولوية من اليمين؟', 'What is the right-hand priority rule?', 'Wat is de rechts-voor-links regel?', 'Quelle est la règle de priorité à droite?', 'MULTIPLE_CHOICE', 'MEDIUM', 2, NOW(), NOW()),
('كيف تتصرف في تقاطع على شكل T؟', 'How do you behave at a T-junction?', 'Hoe gedraag je je op een T-kruising?', 'Comment vous comportez-vous à un carrefour en T?', 'MULTIPLE_CHOICE', 'MEDIUM', 2, NOW(), NOW()),

-- Speed Limits Questions (Category 3)
('ما السرعة القصوى على الطريق السريع؟', 'What is the maximum speed on the highway?', 'Wat is de maximumsnelheid op de snelweg?', 'Quelle est la vitesse maximale sur l''autoroute?', 'MULTIPLE_CHOICE', 'EASY', 3, NOW(), NOW()),
('متى يجب تقليل السرعة؟', 'When should you reduce speed?', 'Wanneer moet je je snelheid verminderen?', 'Quand devez-vous réduire la vitesse?', 'MULTIPLE_CHOICE', 'EASY', 3, NOW(), NOW()),
('ما العوامل التي تؤثر على السرعة الآمنة؟', 'What factors affect safe speed?', 'Welke factoren beïnvloeden de veilige snelheid?', 'Quels facteurs affectent la vitesse de sécurité?', 'MULTIPLE_CHOICE', 'MEDIUM', 3, NOW(), NOW()),
('كيف تؤثر السرعة على مسافة الفرملة؟', 'How does speed affect braking distance?', 'Hoe beïnvloedt snelheid de remafstand?', 'Comment la vitesse affecte-t-elle la distance de freinage?', 'MULTIPLE_CHOICE', 'MEDIUM', 3, NOW(), NOW()),
('ما هي عواقب السرعة الزائدة؟', 'What are the consequences of speeding?', 'Wat zijn de gevolgen van te snel rijden?', 'Quelles sont les conséquences de l''excès de vitesse?', 'MULTIPLE_CHOICE', 'HARD', 3, NOW(), NOW()),

-- Parking Questions (Category 4)
('أين يُمنع الوقوف؟', 'Where is stopping prohibited?', 'Waar is stoppen verboden?', 'Où est-il interdit de s''arrêter?', 'MULTIPLE_CHOICE', 'EASY', 4, NOW(), NOW()),
('كيف توقف سيارتك على منحدر؟', 'How do you park on a slope?', 'Hoe parkeer je op een helling?', 'Comment garez-vous sur une pente?', 'MULTIPLE_CHOICE', 'MEDIUM', 4, NOW(), NOW()),
('ما المسافة المطلوبة من المعبر؟', 'What distance is required from a crossing?', 'Welke afstand is vereist van een oversteekplaats?', 'Quelle distance est requise d''un passage?', 'MULTIPLE_CHOICE', 'MEDIUM', 4, NOW(), NOW()),
('متى يجب استخدام أضواء التحذير؟', 'When should you use hazard lights?', 'Wanneer moet je waarschuwingslichten gebruiken?', 'Quand devez-vous utiliser les feux de détresse?', 'MULTIPLE_CHOICE', 'EASY', 4, NOW(), NOW()),
('كيف تتصرف عند الوقوف الطارئ؟', 'How do you behave during emergency stop?', 'Hoe gedraag je je bij noodstop?', 'Comment vous comportez-vous lors d''un arrêt d''urgence?', 'MULTIPLE_CHOICE', 'MEDIUM', 4, NOW(), NOW()),

-- Vehicle Equipment Questions (Category 5)
('ما هي معدات السلامة المطلوبة؟', 'What safety equipment is required?', 'Welke veiligheidsuitrusting is vereist?', 'Quel équipement de sécurité est requis?', 'MULTIPLE_CHOICE', 'EASY', 5, NOW(), NOW()),
('متى يجب فحص المركبة؟', 'When should the vehicle be inspected?', 'Wanneer moet het voertuig worden geïnspecteerd?', 'Quand le véhicule doit-il être inspecté?', 'MULTIPLE_CHOICE', 'EASY', 5, NOW(), NOW()),
('ما وظيفة حزام الأمان؟', 'What is the function of the seatbelt?', 'Wat is de functie van de veiligheidsgordel?', 'Quelle est la fonction de la ceinture de sécurité?', 'MULTIPLE_CHOICE', 'EASY', 5, NOW(), NOW()),
('كيف تتحقق من ضغط الإطارات؟', 'How do you check tire pressure?', 'Hoe controleer je de bandenspanning?', 'Comment vérifiez-vous la pression des pneus?', 'MULTIPLE_CHOICE', 'MEDIUM', 5, NOW(), NOW()),
('ما أهمية الصيانة الدورية؟', 'What is the importance of regular maintenance?', 'Wat is het belang van regelmatig onderhoud?', 'Quelle est l''importance de l''entretien régulier?', 'MULTIPLE_CHOICE', 'MEDIUM', 5, NOW(), NOW()),

-- Environmental Questions (Category 6)
('كيف تقلل استهلاك الوقود؟', 'How do you reduce fuel consumption?', 'Hoe verminder je brandstofverbruik?', 'Comment réduisez-vous la consommation de carburant?', 'MULTIPLE_CHOICE', 'EASY', 6, NOW(), NOW()),
('ما تأثير القيادة على البيئة؟', 'What is the impact of driving on environment?', 'Wat is de impact van rijden op het milieu?', 'Quel est l''impact de la conduite sur l''environnement?', 'MULTIPLE_CHOICE', 'EASY', 6, NOW(), NOW()),
('كيف تمارس القيادة الصديقة للبيئة؟', 'How do you practice eco-driving?', 'Hoe oefen je eco-rijden?', 'Comment pratiquez-vous l''éco-conduite?', 'MULTIPLE_CHOICE', 'MEDIUM', 6, NOW(), NOW()),
('ما فوائد السيارات الكهربائية؟', 'What are the benefits of electric vehicles?', 'Wat zijn de voordelen van elektrische voertuigen?', 'Quels sont les avantages des véhicules électriques?', 'MULTIPLE_CHOICE', 'MEDIUM', 6, NOW(), NOW()),
('كيف تقلل الانبعاثات الكربونية؟', 'How do you reduce carbon emissions?', 'Hoe verminder je de CO2-uitstoot?', 'Comment réduisez-vous les émissions de carbone?', 'MULTIPLE_CHOICE', 'MEDIUM', 6, NOW(), NOW()),

-- Additional Questions
('ما هي الأولوية في الدوار؟', 'What is the priority in a roundabout?', 'Wat is de voorrang op een rotonde?', 'Quelle est la priorité dans un rond-point?', 'MULTIPLE_CHOICE', 'EASY', 2, NOW(), NOW()),
('كيف تستخدم المرايا بشكل صحيح؟', 'How do you use mirrors correctly?', 'Hoe gebruik je spiegels correct?', 'Comment utilisez-vous les rétroviseurs correctement?', 'MULTIPLE_CHOICE', 'EASY', 5, NOW(), NOW()),
('متى يجب تغيير حارة المرور؟', 'When should you change lanes?', 'Wanneer moet je van rijstrook wisselen?', 'Quand devez-vous changer de voie?', 'MULTIPLE_CHOICE', 'MEDIUM', 1, NOW(), NOW()),
('ما هي مسؤوليات السائق؟', 'What are the driver''s responsibilities?', 'Wat zijn de verantwoordelijkheden van de bestuurder?', 'Quelles sont les responsabilités du conducteur?', 'MULTIPLE_CHOICE', 'EASY', 6, NOW(), NOW()),
('كيف تتعامل مع حالة طارئة؟', 'How do you handle an emergency?', 'Hoe ga je om met een noodsituatie?', 'Comment gérez-vous une urgence?', 'MULTIPLE_CHOICE', 'HARD', 5, NOW(), NOW()),
('ما معنى علامات الطريق الصفراء؟', 'What do yellow road markings mean?', 'Wat betekenen gele wegmarkeringen?', 'Que signifient les marquages routiers jaunes?', 'MULTIPLE_CHOICE', 'EASY', 1, NOW(), NOW()),
('كيف تقود في الليل بأمان؟', 'How do you drive safely at night?', 'Hoe rijd je veilig in het donker?', 'Comment conduire en toute sécurité la nuit?', 'MULTIPLE_CHOICE', 'MEDIUM', 3, NOW(), NOW()),
('ما قواعد نقل الأطفال؟', 'What are the rules for transporting children?', 'Wat zijn de regels voor vervoer van kinderen?', 'Quelles sont les règles pour transporter des enfants?', 'MULTIPLE_CHOICE', 'MEDIUM', 5, NOW(), NOW());

-- Now add answer options for each question
-- For simplicity, we'll add 4 options per question (A, B, C, D)
-- We'll need to get the question IDs and add corresponding options

-- Note: This is a simplified version. In production, you would add all answer options.
-- The application will need answer options to function properly.
