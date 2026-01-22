-- Phase 4: Learning System - Test Data (Limited)
-- Minimal data to test the learning system functionality

-- Insert 7 Test Lessons (one per category)
INSERT INTO lessons (category_id, title_ar, title_en, title_nl, title_fr, content_ar, content_en, content_nl, content_fr, display_order, estimated_minutes, is_active, created_at, updated_at)
VALUES
    -- Lesson 1: Warning Signs (Category A)
    (1, 'إشارات التحذير الأساسية', 'Basic Warning Signs', 'Basis waarschuwingsborden', 'Signaux d''avertissement de base',
     'إشارات التحذير تنبه السائقين للمخاطر المحتملة على الطريق. يجب الانتباه لها والتصرف بحذر.', 
     'Warning signs alert drivers to potential hazards on the road. Pay attention and act cautiously.',
     'Waarschuwingsborden waarschuwen bestuurders voor mogelijke gevaren op de weg. Let op en handel voorzichtig.',
     'Les panneaux d''avertissement alertent les conducteurs des dangers potentiels sur la route. Soyez attentif et agissez prudemment.',
     1, 5, TRUE, NOW(), NOW()),
     
    -- Lesson 2: Priority Signs (Category B)
    (2, 'إشارات الأولوية', 'Priority Signs', 'Voorrangsborden', 'Signaux de priorité',
     'إشارات الأولوية تحدد من له حق المرور أولاً عند التقاطعات. احترامها ضروري للسلامة.',
     'Priority signs determine who has the right of way at intersections. Respecting them is essential for safety.',
     'Voorrangsborden bepalen wie voorrang heeft bij kruispunten. Het respecteren ervan is essentieel voor de veiligheid.',
     'Les panneaux de priorité déterminent qui a la priorité aux intersections. Les respecter est essentiel pour la sécurité.',
     2, 5, TRUE, NOW(), NOW()),
     
    -- Lesson 3: Prohibition Signs (Category C)
    (3, 'إشارات المنع', 'Prohibition Signs', 'Verbodsborden', 'Signaux d''interdiction',
     'إشارات المنع تمنع تصرفات معينة على الطريق. يجب الالتزام بها لتجنب المخالفات والحوادث.',
     'Prohibition signs forbid certain actions on the road. They must be obeyed to avoid violations and accidents.',
     'Verbodsborden verbieden bepaalde handelingen op de weg. Ze moeten worden gehoorzaamd om overtredingen en ongevallen te voorkomen.',
     'Les panneaux d''interdiction interdisent certaines actions sur la route. Ils doivent être respectés pour éviter les infractions et les accidents.',
     3, 5, TRUE, NOW(), NOW()),
     
    -- Lesson 4: Mandatory Signs (Category D)
    (4, 'إشارات الإلزام', 'Mandatory Signs', 'Gebodsborden', 'Signaux d''obligation',
     'إشارات الإلزام تفرض تصرفات محددة يجب على السائق اتباعها. مثل الاتجاه الإلزامي.',
     'Mandatory signs impose specific actions that the driver must follow, such as compulsory direction.',
     'Gebodsborden leggen specifieke acties op die de bestuurder moet volgen, zoals verplichte rijrichting.',
     'Les panneaux obligatoires imposent des actions spécifiques que le conducteur doit suivre, comme la direction obligatoire.',
     4, 5, TRUE, NOW(), NOW()),
     
    -- Lesson 5: Parking Signs (Category E)
    (5, 'إشارات الوقوف والانتظار', 'Parking and Stopping Signs', 'Parkeer- en stopborden', 'Signaux de stationnement',
     'إشارات الوقوف تنظم أماكن وأوقات الانتظار والوقوف. الالتزام بها يمنع الازدحام المروري.',
     'Parking signs regulate where and when parking and stopping are allowed. Following them prevents traffic congestion.',
     'Parkeer- en stopborden regelen waar en wanneer parkeren en stoppen is toegestaan. Het volgen ervan voorkomt verkeersopstoppingen.',
     'Les panneaux de stationnement régulent où et quand le stationnement et l''arrêt sont autorisés. Les suivre évite les embouteillages.',
     5, 5, TRUE, NOW(), NOW()),
     
    -- Lesson 6: Direction Signs (Category F)
    (6, 'إشارات الإرشاد والطرق', 'Direction and Road Signs', 'Richtings- en wegborden', 'Signaux de direction',
     'إشارات الإرشاد توجه السائقين للمدن والطرق السريعة. تساعد في التنقل بسهولة.',
     'Direction signs guide drivers to cities and highways. They help navigate easily.',
     'Richtingsborden leiden bestuurders naar steden en snelwegen. Ze helpen gemakkelijk navigeren.',
     'Les panneaux de direction guident les conducteurs vers les villes et les autoroutes. Ils aident à naviguer facilement.',
     6, 5, TRUE, NOW(), NOW()),
     
    -- Lesson 7: Additional Signs (Category G)
    (7, 'إشارات إضافية', 'Additional Information Signs', 'Aanvullende informatieborden', 'Signaux additionnels',
     'الإشارات الإضافية تكمل الإشارات الرئيسية بمعلومات إضافية مثل المسافة أو الاستثناءات.',
     'Additional signs complement main signs with extra information such as distance or exceptions.',
     'Aanvullende borden vullen hoofdborden aan met extra informatie zoals afstand of uitzonderingen.',
     'Les panneaux additionnels complètent les panneaux principaux avec des informations supplémentaires telles que la distance ou les exceptions.',
     7, 5, TRUE, NOW(), NOW());

-- Insert 15 Practice Questions (2-3 per lesson)
INSERT INTO practice_questions (lesson_id, question_ar, question_en, question_nl, question_fr, option1_ar, option1_en, option1_nl, option1_fr, option2_ar, option2_en, option2_nl, option2_fr, option3_ar, option3_en, option3_nl, option3_fr, option4_ar, option4_en, option4_nl, option4_fr, correct_answer, explanation_ar, explanation_en, explanation_nl, explanation_fr, display_order, is_active, created_at, updated_at)
VALUES
    -- Lesson 1: Warning Signs
    (1, 'ماذا تعني إشارة التحذير المثلثية الحمراء؟', 'What does a red triangular warning sign mean?', 'Wat betekent een rood driehoekig waarschuwingsbord?', 'Que signifie un panneau triangulaire rouge?',
     'خطر محتمل في الطريق', 'Potential danger on the road', 'Mogelijk gevaar op de weg', 'Danger potentiel sur la route',
     'ممنوع المرور', 'No entry', 'Geen toegang', 'Accès interdit',
     'طريق آمن', 'Safe road', 'Veilige weg', 'Route sûre',
     'سرعة قصوى', 'Maximum speed', 'Maximumsnelheid', 'Vitesse maximale',
     1, 'الإشارات المثلثية الحمراء هي إشارات تحذير', 'Red triangular signs are warning signs', 'Rode driehoekige borden zijn waarschuwingsborden', 'Les panneaux triangulaires rouges sont des panneaux d''avertissement',
     1, TRUE, NOW(), NOW()),
     
    (1, 'متى يجب أن تبطئ سرعتك عند رؤية إشارة تحذير؟', 'When should you slow down after seeing a warning sign?', 'Wanneer moet je vertragen na het zien van een waarschuwingsbord?', 'Quand devez-vous ralentir après avoir vu un panneau d''avertissement?',
     'فوراً', 'Immediately', 'Onmiddellijk', 'Immédiatement',
     'بعد 500 متر', 'After 500 meters', 'Na 500 meter', 'Après 500 mètres',
     'عند التقاطع التالي', 'At next intersection', 'Bij volgende kruispunt', 'Au prochain carrefour',
     'ليس ضرورياً', 'Not necessary', 'Niet nodig', 'Pas nécessaire',
     1, 'يجب الاستعداد فوراً عند رؤية إشارة التحذير', 'You must prepare immediately when seeing a warning sign', 'Je moet je onmiddellijk voorbereiden bij het zien van een waarschuwingsbord', 'Vous devez vous préparer immédiatement en voyant un panneau d''avertissement',
     2, TRUE, NOW(), NOW()),
     
    -- Lesson 2: Priority Signs
    (2, 'ماذا يعني إشارة المثلث المقلوب؟', 'What does an inverted triangle sign mean?', 'Wat betekent een omgekeerd driehoekig bord?', 'Que signifie un panneau triangulaire inversé?',
     'أعط الأولوية', 'Give way', 'Voorrang verlenen', 'Céder le passage',
     'توقف', 'Stop', 'Stop', 'Arrêt',
     'ممنوع الدخول', 'No entry', 'Geen toegang', 'Accès interdit',
     'طريق ذو أولوية', 'Priority road', 'Voorrangsweg', 'Route prioritaire',
     1, 'المثلث المقلوب يعني يجب إعطاء الأولوية', 'Inverted triangle means give way', 'Omgekeerde driehoek betekent voorrang verlenen', 'Triangle inversé signifie céder le passage',
     1, TRUE, NOW(), NOW()),
     
    (2, 'من له الأولوية في تقاطع بدون إشارات؟', 'Who has priority at an intersection without signs?', 'Wie heeft voorrang bij een kruispunt zonder borden?', 'Qui a la priorité à un carrefour sans panneaux?',
     'المركبة من اليمين', 'Vehicle from the right', 'Voertuig van rechts', 'Véhicule de droite',
     'المركبة من اليسار', 'Vehicle from the left', 'Voertuig van links', 'Véhicule de gauche',
     'المركبة الأسرع', 'Faster vehicle', 'Snelste voertuig', 'Véhicule le plus rapide',
     'المركبة الأكبر', 'Larger vehicle', 'Groter voertuig', 'Véhicule plus grand',
     1, 'قاعدة اليمين: من يأتي من اليمين له الأولوية', 'Right-hand rule: who comes from right has priority', 'Rechts-regel: wie van rechts komt heeft voorrang', 'Règle de droite: qui vient de droite a la priorité',
     2, TRUE, NOW(), NOW()),
     
    -- Lesson 3: Prohibition Signs
    (3, 'ماذا تعني الدائرة الحمراء مع خط أبيض أفقي؟', 'What does a red circle with a white horizontal line mean?', 'Wat betekent een rode cirkel met een witte horizontale lijn?', 'Que signifie un cercle rouge avec une ligne blanche horizontale?',
     'ممنوع الدخول', 'No entry', 'Geen toegang', 'Accès interdit',
     'ممنوع الوقوف', 'No parking', 'Parkeren verboden', 'Stationnement interdit',
     'ممنوع التجاوز', 'No overtaking', 'Inhalen verboden', 'Dépassement interdit',
     'قف', 'Stop', 'Stop', 'Arrêt',
     1, 'الدائرة الحمراء مع خط أبيض أفقي تعني ممنوع الدخول', 'Red circle with white horizontal line means no entry', 'Rode cirkel met witte horizontale lijn betekent geen toegang', 'Cercle rouge avec ligne blanche horizontale signifie accès interdit',
     1, TRUE, NOW(), NOW()),
     
    (3, 'هل يمكنك تجاوز السرعة القصوى في حالة الطوارئ؟', 'Can you exceed the speed limit in an emergency?', 'Mag je de maximumsnelheid overschrijden in noodgevallen?', 'Pouvez-vous dépasser la limite de vitesse en cas d''urgence?',
     'لا، أبداً', 'No, never', 'Nee, nooit', 'Non, jamais',
     'نعم، دائماً', 'Yes, always', 'Ja, altijd', 'Oui, toujours',
     'نعم، إذا كان الطريق فارغاً', 'Yes, if road is empty', 'Ja, als de weg leeg is', 'Oui, si la route est vide',
     'نعم، في الليل فقط', 'Yes, only at night', 'Ja, alleen \'s nachts', 'Oui, seulement la nuit',
     1, 'إشارات المنع يجب احترامها دائماً', 'Prohibition signs must always be respected', 'Verbodsborden moeten altijd worden gerespecteerd', 'Les panneaux d''interdiction doivent toujours être respectés',
     2, TRUE, NOW(), NOW()),
     
    -- Lesson 4: Mandatory Signs
    (4, 'ماذا تعني الدائرة الزرقاء مع سهم أبيض لليمين؟', 'What does a blue circle with a white arrow to the right mean?', 'Wat betekent een blauwe cirkel met een witte pijl naar rechts?', 'Que signifie un cercle bleu avec une flèche blanche vers la droite?',
     'اتجاه إلزامي لليمين', 'Compulsory direction right', 'Verplichte richting rechts', 'Direction obligatoire à droite',
     'يمين ممنوع', 'Right turn forbidden', 'Rechts afslaan verboden', 'Tourner à droite interdit',
     'يمين مسموح', 'Right turn allowed', 'Rechts afslaan toegestaan', 'Tourner à droite autorisé',
     'طريق لليمين', 'Road to the right', 'Weg naar rechts', 'Route vers la droite',
     1, 'الدائرة الزرقاء تعني إلزام، والسهم يحدد الاتجاه', 'Blue circle means mandatory, arrow shows direction', 'Blauwe cirkel betekent verplicht, pijl toont richting', 'Cercle bleu signifie obligatoire, flèche indique direction',
     1, TRUE, NOW(), NOW()),
     
    (4, 'هل يمكنك الانعطاف يساراً عند وجود إشارة اتجاه إلزامي لليمين؟', 'Can you turn left when there is a compulsory right turn sign?', 'Kun je linksaf slaan als er een verplicht rechts bord is?', 'Pouvez-vous tourner à gauche lorsqu''il y a un panneau obligatoire à droite?',
     'لا، يجب الالتزام', 'No, must comply', 'Nee, moet volgen', 'Non, doit respecter',
     'نعم، إذا لا توجد سيارات', 'Yes, if no cars', 'Ja, als er geen auto\'s zijn', 'Oui, s\'il n\'y a pas de voitures',
     'نعم، في حالة الطوارئ', 'Yes, in emergency', 'Ja, in noodgevallen', 'Oui, en urgence',
     'يعتمد على الوقت', 'Depends on time', 'Hangt af van tijd', 'Dépend du temps',
     1, 'إشارات الإلزام يجب الالتزام بها دائماً', 'Mandatory signs must always be followed', 'Gebodsborden moeten altijd worden gevolgd', 'Les panneaux obligatoires doivent toujours être suivis',
     2, TRUE, NOW(), NOW()),
     
    -- Lesson 5: Parking Signs
    (5, 'ماذا تعني إشارة E مع خط أحمر؟', 'What does sign E with a red line mean?', 'Wat betekent bord E met een rode lijn?', 'Que signifie le panneau E avec une ligne rouge?',
     'ممنوع الوقوف والانتظار', 'No stopping or parking', 'Stilstaan en parkeren verboden', 'Arrêt et stationnement interdits',
     'وقوف مسموح', 'Parking allowed', 'Parkeren toegestaan', 'Stationnement autorisé',
     'وقوف مدفوع', 'Paid parking', 'Betaald parkeren', 'Stationnement payant',
     'منطقة خدمة', 'Service area', 'Servicegebied', 'Zone de service',
     1, 'E مع خط أحمر يعني ممنوع الوقوف والانتظار تماماً', 'E with red line means no stopping or parking at all', 'E met rode lijn betekent absoluut geen stoppen of parkeren', 'E avec ligne rouge signifie aucun arrêt ou stationnement',
     1, TRUE, NOW(), NOW()),
     
    (5, 'كم من الوقت يمكنك التوقف لإنزال راكب في منطقة ممنوع الوقوف؟', 'How long can you stop to drop off a passenger in a no-parking zone?', 'Hoe lang mag je stoppen om een passagier af te zetten in een parkeerverbod zone?', 'Combien de temps pouvez-vous vous arrêter pour déposer un passager dans une zone de stationnement interdit?',
     'لحظة قصيرة فقط', 'Brief moment only', 'Alleen kort moment', 'Seulement un bref moment',
     '5 دقائق', '5 minutes', '5 minuten', '5 minutes',
     '10 دقائق', '10 minutes', '10 minuten', '10 minutes',
     'ممنوع تماماً', 'Completely forbidden', 'Volledig verboden', 'Complètement interdit',
     1, 'في منطقة ممنوع الوقوف يمكن التوقف لحظة قصيرة فقط', 'In no-parking zone can stop briefly only', 'In parkeerverbod zone mag alleen kort stoppen', 'Dans zone de stationnement interdit peut s''arrêter brièvement seulement',
     2, TRUE, NOW(), NOW()),
     
    -- Lesson 6: Direction Signs
    (6, 'ما لون إشارات الطرق السريعة؟', 'What color are highway signs?', 'Welke kleur hebben snelwegborden?', 'De quelle couleur sont les panneaux d''autoroute?',
     'أخضر', 'Green', 'Groen', 'Vert',
     'أزرق', 'Blue', 'Blauw', 'Bleu',
     'أصفر', 'Yellow', 'Geel', 'Jaune',
     'أبيض', 'White', 'Wit', 'Blanc',
     1, 'إشارات الطرق السريعة في بلجيكا خضراء', 'Highway signs in Belgium are green', 'Snelwegborden in België zijn groen', 'Les panneaux d''autoroute en Belgique sont verts',
     1, TRUE, NOW(), NOW()),
     
    (6, 'ما معنى الإشارة الزرقاء مع رموز بيضاء للمدن؟', 'What does a blue sign with white city symbols mean?', 'Wat betekent een blauw bord met witte stadssymbolen?', 'Que signifie un panneau bleu avec des symboles de ville blancs?',
     'طريق سريع إقليمي', 'Express road', 'Autoweg', 'Route express',
     'طريق سريع', 'Highway', 'Snelweg', 'Autoroute',
     'طريق محلي', 'Local road', 'Lokale weg', 'Route locale',
     'طريق خاص', 'Private road', 'Privéweg', 'Route privée',
     1, 'الإشارات الزرقاء للطرق السريعة الإقليمية', 'Blue signs for express roads', 'Blauwe borden voor autowegen', 'Panneaux bleus pour routes express',
     2, TRUE, NOW(), NOW()),
     
    -- Lesson 7: Additional Signs
    (7, 'ماذا يعني اللوحة الإضافية "200m"؟', 'What does the additional plate "200m" mean?', 'Wat betekent het aanvullende bord "200m"?', 'Que signifie le panneau additionnel "200m"?',
     'مسافة سريان الإشارة', 'Distance of sign validity', 'Afstand van bordgeldigheid', 'Distance de validité du panneau',
     'المسافة المتبقية', 'Remaining distance', 'Resterende afstand', 'Distance restante',
     'سرعة قصوى', 'Maximum speed', 'Maximumsnelheid', 'Vitesse maximale',
     'عدد الأمتار للوقوف', 'Meters to park', 'Meters om te parkeren', 'Mètres pour stationner',
     1, 'الأرقام على اللوحات الإضافية تشير لمسافة سريان الإشارة', 'Numbers on additional plates indicate sign validity distance', 'Nummers op aanvullende borden geven bordgeldigheidafstand aan', 'Les numéros sur panneaux additionnels indiquent la distance de validité',
     1, TRUE, NOW(), NOW()),
     
    (7, 'ماذا تعني اللوحة الإضافية مع رمز دراجة؟', 'What does an additional plate with a bicycle symbol mean?', 'Wat betekent een aanvullend bord met een fietssymbool?', 'Que signifie un panneau additionnel avec un symbole de vélo?',
     'استثناء للدراجات', 'Exception for bicycles', 'Uitzondering voor fietsen', 'Exception pour vélos',
     'ممنوع على الدراجات', 'Forbidden for bicycles', 'Verboden voor fietsen', 'Interdit aux vélos',
     'طريق للدراجات فقط', 'Bicycle road only', 'Alleen fietsweg', 'Route cyclable seulement',
     'دراجات مسموحة', 'Bicycles allowed', 'Fietsen toegestaan', 'Vélos autorisés',
     1, 'اللوحة الإضافية مع رمز تشير لاستثناء', 'Additional plate with symbol indicates exception', 'Aanvullend bord met symbool geeft uitzondering aan', 'Panneau additionnel avec symbole indique exception',
     2, TRUE, NOW(), NOW());

-- Insert 10 Exam Questions (mixed categories)
INSERT INTO exam_questions (category_id, question_ar, question_en, question_nl, question_fr, option1_ar, option1_en, option1_nl, option1_fr, option2_ar, option2_en, option2_nl, option2_fr, option3_ar, option3_en, option3_nl, option3_fr, option4_ar, option4_en, option4_nl, option4_fr, correct_answer, explanation_ar, explanation_en, explanation_nl, explanation_fr, image_url, difficulty, is_important, is_active, created_at, updated_at)
VALUES
    -- Q1: Pattern 3 (Single-Rule Application) - MEDIUM
    (1, 'إشارة تحذير بمنعطف، ماذا تفعل؟', 'Warning sign for bend, what do you do?', 'Waarschuwingsbord voor bocht, wat doet u?', 'Panneau avertissement virage, que faites-vous?',
     'خفف السرعة', 'Reduce speed', 'Verminder snelheid', 'Réduisez vitesse',
     'أسرع للمرور', 'Speed up to pass', 'Versnel om te passeren', 'Accélérez pour passer',
     'توقف تماماً', 'Stop completely', 'Stop volledig', 'Arrêtez complètement',
     'تابع بنفس السرعة', 'Continue same speed', 'Ga door zelfde snelheid', 'Continuez même vitesse',
     1, 'إشارة التحذير = خفف السرعة فوراً', 'Warning sign = reduce speed immediately', 'Waarschuwingsbord = verminder snelheid onmiddellijk', 'Panneau avertissement = réduisez vitesse immédiatement',
     NULL, 'MEDIUM', TRUE, TRUE, NOW(), NOW()),
     
    -- Q2: Pattern 2 (Visual Recognition) - EASY
    (1, 'مثلث أحمر فارغ، ماذا يعني؟', 'Empty red triangle, what does it mean?', 'Lege rode driehoek, wat betekent?', 'Triangle rouge vide, que signifie?',
     'أعط الأولوية', 'Give way', 'Voorrang verlenen', 'Cédez passage',
     'توقف', 'Stop', 'Stop', 'Arrêt',
     'ممنوع الدخول', 'No entry', 'Geen toegang', 'Accès interdit',
     'تحذير', 'Warning', 'Waarschuwing', 'Avertissement',
     1, 'مثلث فارغ = أعط الأولوية', 'Empty triangle = give way', 'Lege driehoek = voorrang verlenen', 'Triangle vide = cédez passage',
     NULL, 'EASY', TRUE, TRUE, NOW(), NOW()),
     
    -- Q3: Pattern 4 (Exception Trap) - HARD
    (3, 'متى يُسمح تجاوز السرعة؟', 'When is exceeding speed allowed?', 'Wanneer is snelheid overschrijden toegestaan?', 'Quand dépasser vitesse est autorisé?',
     'أبدًا', 'Never', 'Nooit', 'Jamais',
     'في الطوارئ', 'In emergency', 'Bij noodgeval', 'En urgence',
     'طريق فارغ', 'Empty road', 'Lege weg', 'Route vide',
     'ليلاً', 'At night', '\'s Nachts', 'La nuit',
     1, 'يجب احترام حدود السرعة دائماً', 'Speed limits must always be respected', 'Snelheidslimieten moeten altijd worden gerespecteerd', 'Limites vitesse doivent toujours être respectées',
     NULL, 'HARD', TRUE, TRUE, NOW(), NOW()),
     
    -- Q4: Pattern 2 (Visual Recognition) - EASY
    (4, 'دائرة زرقاء مع سهم، ماذا تعني؟', 'Blue circle with arrow, what does it mean?', 'Blauwe cirkel met pijl, wat betekent?', 'Cercle bleu avec flèche, que signifie?',
     'اتجاه إلزامي', 'Mandatory direction', 'Verplichte richting', 'Direction obligatoire',
     'اتجاه ممنوع', 'Direction forbidden', 'Richting verboden', 'Direction interdite',
     'اتجاه مسموح', 'Direction allowed', 'Richting toegestaan', 'Direction autorisée',
     'اتجاه مقترح', 'Suggested direction', 'Voorgestelde richting', 'Direction suggérée',
     1, 'دائرة زرقاء = إلزام، دائرة حمراء = منع', 'Blue circle = mandatory, red circle = prohibition', 'Blauwe cirkel = verplicht, rode cirkel = verbod', 'Cercle bleu = obligatoire, cercle rouge = interdiction',
     NULL, 'EASY', TRUE, TRUE, NOW(), NOW()),
     
    -- Q5: Pattern 4 (Exception Trap) - HARD
    (5, 'متى يُسمح التوقف في ممنوع التوقف؟', 'When is stopping allowed in no-stopping zone?', 'Wanneer is stilstaan toegestaan in stilstaanverbod?', 'Quand arrêt autorisé en zone arrêt interdit?',
     'أبدًا', 'Never', 'Nooit', 'Jamais',
     'لإنزال راكب', 'To drop passenger', 'Passagier afzetten', 'Déposer passager',
     'ليلاً', 'At night', '\'s Nachts', 'La nuit',
     'أحد', 'Sunday', 'Zondag', 'Dimanche',
     1, 'ممنوع التوقف = ممنوع تماماً', 'No stopping = completely forbidden', 'Stilstaan verboden = volledig verboden', 'Arrêt interdit = totalement interdit',
     NULL, 'HARD', TRUE, TRUE, NOW(), NOW()),
     
    -- Q6: Pattern 9 (Safety Priority) - MEDIUM
    (6, 'إشارة خضراء + مخرج قريب، ماذا تفعل؟', 'Green sign + exit close, what do you do?', 'Groen bord + afrit dichtbij, wat doet u?', 'Panneau vert + sortie proche, que faites-vous?',
     'غيّر الحارة مبكراً', 'Change lane early', 'Verander baan vroeg', 'Changez voie tôt',
     'غيّر في اللحظة الأخيرة', 'Change last moment', 'Verander laatste moment', 'Changez dernier moment',
     'غيّر بسرعة', 'Change quickly', 'Verander snel', 'Changez rapidement',
     'ابقَ في حارتك', 'Stay in lane', 'Blijf in baan', 'Restez dans voie',
     1, 'السلامة = غيّر الحارة مبكراً', 'Safety = change lane early', 'Veiligheid = verander baan vroeg', 'Sécurité = changez voie tôt',
     NULL, 'MEDIUM', TRUE, TRUE, NOW(), NOW()),
     
    -- Q7: Pattern 7 (Conditional Logic) - HARD
    (7, 'لوحة "إلا الدراجات"، ماذا تعني؟', 'Plate "except bicycles", what does it mean?', 'Bord "uitgezonderd fietsen", wat betekent?', 'Plaque "sauf vélos", que signifie?',
     'الدراجات مستثناة', 'Bicycles excepted', 'Fietsen uitgezonderd', 'Vélos exceptés',
     'ممنوع الدراجات', 'Bicycles forbidden', 'Fietsen verboden', 'Vélos interdits',
     'للدراجات فقط', 'Bicycles only', 'Alleen fietsen', 'Vélos seulement',
     'الدراجات تتوقف', 'Bicycles stop', 'Fietsen stoppen', 'Vélos arrêtent',
     1, '"إلا" = استثناء من القاعدة', '"Except" = exception from rule', '"Uitgezonderd" = uitzondering van regel', '"Sauf" = exception de règle',
     NULL, 'HARD', TRUE, TRUE, NOW(), NOW()),
     
    -- Q8: Pattern 1 (Direct Rule Recall) - EASY
    (3, 'السرعة داخل المدن بدون إشارات؟', 'Speed in cities without signs?', 'Snelheid in steden zonder borden?', 'Vitesse en ville sans panneaux?',
     '50 كم/س', '50 km/h', '50 km/u', '50 km/h',
     '30 كم/س', '30 km/h', '30 km/u', '30 km/h',
     '70 كم/س', '70 km/h', '70 km/u', '70 km/h',
     '90 كم/س', '90 km/h', '90 km/u', '90 km/h',
     1, 'القاعدة الافتراضية: 50 كم/س', 'Default rule: 50 km/h', 'Standaardregel: 50 km/u', 'Règle défaut: 50 km/h',
     NULL, 'EASY', TRUE, TRUE, NOW(), NOW()),
     
    -- Q9: Pattern 3 (Single-Rule Application) - MEDIUM
    (2, 'دوار بدون إشارات، من يمر؟', 'Roundabout without signs, who passes?', 'Rotonde zonder borden, wie gaat?', 'Rond-point sans panneaux, qui passe?',
     'من داخل الدوار', 'Who is inside', 'Wie binnen is', 'Qui est dedans',
     'من يدخل', 'Who enters', 'Wie binnenrijdt', 'Qui entre',
     'من اليمين', 'From right', 'Van rechts', 'De droite',
     'الأكبر', 'Larger', 'Groter', 'Plus grand',
     1, 'الدوار: من بالداخل له الأولوية', 'Roundabout: inside has priority', 'Rotonde: binnen heeft voorrang', 'Rond-point: intérieur a priorité',
     NULL, 'MEDIUM', TRUE, TRUE, NOW(), NOW()),
     
    -- Q10: Pattern 8 (Absolute vs Relative) - MEDIUM
    (1, 'الأضواء العالية، متى تُستخدم؟', 'High beams, when to use?', 'Grootlicht, wanneer gebruiken?', 'Feux route, quand utiliser?',
     'طرق مظلمة بدون مركبات', 'Dark roads without vehicles', 'Donkere wegen zonder voertuigen', 'Routes sombres sans véhicules',
     'دائماً ليلاً', 'Always at night', 'Altijd \'s nachts', 'Toujours la nuit',
     'في المدن', 'In cities', 'In steden', 'En ville',
     'عند المطر', 'When raining', 'Bij regen', 'Quand pluie',
     1, 'الأضواء العالية تستخدم على الطرق المظلمة عندما لا توجد مركبات مقابلة', 'High beams used on dark roads when no oncoming vehicles', 'Grootlicht wordt gebruikt op donkere wegen zonder tegemoetkomend verkeer', 'Feux de route utilisés sur routes sombres sans véhicules venant en sens inverse',
     NULL, 'MEDIUM', TRUE, TRUE, NOW(), NOW());
