UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.explanation_ar = 'تشير هذه العلامة المرورية إلى أن الدراجين وراكبي الدراجات البخارية قد يعبرون الطريق هنا. يجب الانتباه لهم وإعطاؤهم الأولوية عند العبور.',
    q.explanation_en = 'This traffic sign warns that cyclists and moped riders may cross the road here. You must watch for them and give way when they are crossing.',
    q.explanation_nl = 'Dit verkeersbord waarschuwt dat fietsers en bromfietsers hier de rijbaan kunnen oversteken. U moet extra op hen letten en voorrang verlenen wanneer zij oversteken.',
    q.explanation_fr = 'Ce panneau avertit que des cyclistes et des cyclomotoristes peuvent traverser la chaussée ici. Vous devez les surveiller attentivement et leur céder la priorité lorsqu''ils traversent.'
WHERE rs.sign_code = 'A25' AND q.question_ref = 'A25_Q01';

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.explanation_ar = 'تنتمي هذه العلامة المرورية إلى علامات الخطر. علامات الخطر مثلثة الشكل، ذات حافة حمراء وخلفية بيضاء.',
    q.explanation_en = 'This traffic sign belongs to the danger signs. Danger signs are triangular, with a red border and a white background.',
    q.explanation_nl = 'Dit verkeersbord behoort tot de gevaarsborden. Gevaarsborden zijn driehoekig, met een rode rand en een witte achtergrond.',
    q.explanation_fr = 'Ce panneau appartient aux panneaux de danger. Les panneaux de danger sont triangulaires, avec une bordure rouge et un fond blanc.'
WHERE rs.sign_code = 'A25' AND q.question_ref = 'A25_Q02';

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.explanation_ar = 'تشير هذه العلامة المرورية إلى أن الدراجين وراكبي الدراجات البخارية قد يعبرون الطريق فجأة. قد يكون من الصعب رؤيتهم، وقد يتحركون بسرعة، لذلك يلزم توخي حذر إضافي.',
    q.explanation_en = 'This traffic sign warns that cyclists and moped riders may suddenly cross the road. They may be hard to see and can approach quickly, so extra caution is required.',
    q.explanation_nl = 'Dit verkeersbord waarschuwt dat fietsers en bromfietsers plots kunnen oversteken. Ze kunnen moeilijk zichtbaar zijn en snel naderen, daarom is extra voorzichtigheid nodig.',
    q.explanation_fr = 'Ce panneau avertit que des cyclistes et des cyclomotoristes peuvent traverser soudainement la chaussée. Ils peuvent être difficiles à voir et arriver rapidement, d''où la nécessité d''une vigilance accrue.'
WHERE rs.sign_code = 'A25' AND q.question_ref = 'A25_Q03';

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.explanation_ar = 'عند رؤية هذه العلامة المرورية، يجب عليك تخفيف السرعة والانتباه بشكل خاص للدراجين وراكبي الدراجات البخارية الذين قد يعبرون الطريق فجأة.',
    q.explanation_en = 'When you see this sign, you must reduce speed and pay special attention to cyclists and moped riders who may cross unexpectedly.',
    q.explanation_nl = 'Wanneer u dit bord ziet, moet u snelheid minderen en extra letten op fietsers en bromfietsers die onverwacht kunnen oversteken.',
    q.explanation_fr = 'Lorsque vous voyez ce panneau, vous devez réduire votre vitesse et accorder une attention particulière aux cyclistes et cyclomotoristes qui peuvent traverser de manière inattendue.'
WHERE rs.sign_code = 'A25' AND q.question_ref = 'A25_Q04';

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.question_ar = 'دراج يقترب من ممر عبور الدراجات من جهة اليمين. ماذا تفعل؟',
    q.question_en = 'A cyclist is approaching the cyclist crossing from the right. What do you do?',
    q.question_nl = 'Een fietser nadert de fietsersoversteekplaats van rechts. Wat doet u?',
    q.question_fr = 'Un cycliste approche de la traversée cycliste par la droite. Que faites-vous ?',
    q.explanation_ar = 'عند ممر عبور الدراجات، يجب إعطاء الأولوية للدراجين الذين يعبرون أو يستعدون للعبور. يجب التوقف إذا كان مرورك قد يعرّضهم للخطر.',
    q.explanation_en = 'At a cyclist crossing, you must give way to cyclists who are crossing or about to cross. You must stop if passing would put them in danger.',
    q.explanation_nl = 'Aan een fietsersoversteekplaats moet u voorrang verlenen aan fietsers die oversteken of duidelijk willen oversteken. U moet stoppen als doorrijden hen in gevaar kan brengen.',
    q.explanation_fr = 'À une traversée cycliste, vous devez céder la priorité aux cyclistes qui traversent ou qui sont sur le point de traverser. Vous devez vous arrêter si continuer risquerait de les mettre en danger.'
WHERE rs.sign_code = 'A25' AND q.question_ref = 'A25_Q05';

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.question_ar = 'ما الفرق بين ممر عبور المشاة وممر عبور الدراجات؟',
    q.question_en = 'What is the difference between a pedestrian crossing and a cyclist crossing?',
    q.question_nl = 'Wat is het verschil tussen een voetgangersoversteekplaats en een fietsersoversteekplaats?',
    q.question_fr = 'Quelle est la différence entre un passage pour piétons et une traversée cycliste ?',
    q.explanation_ar = 'ممر عبور المشاة يخص المشاة، أما ممر عبور الدراجات فيخص الدراجين وراكبي الدراجات البخارية. في الحالتين يجب إعطاء الأولوية للعابرين المعنيين.',
    q.explanation_en = 'A pedestrian crossing is for pedestrians, while a cyclist crossing is for cyclists and moped riders. In both cases, you must give way to the relevant road users who are crossing.',
    q.explanation_nl = 'Een voetgangersoversteekplaats is voor voetgangers, terwijl een fietsersoversteekplaats bedoeld is voor fietsers en bromfietsers. In beide gevallen moet u voorrang verlenen aan de betrokken overstekers.',
    q.explanation_fr = 'Un passage pour piétons concerne les piétons, tandis qu''une traversée cycliste concerne les cyclistes et les cyclomotoristes. Dans les deux cas, vous devez céder la priorité aux usagers concernés qui traversent.'
WHERE rs.sign_code = 'A25' AND q.question_ref = 'A25_Q06';

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 1,
    c.text_ar = 'ممر عبور المشاة مخصص للمشاة؛ وممر عبور الدراجات مخصص للدراجين وراكبي الدراجات البخارية',
    c.text_en = 'A pedestrian crossing is intended for pedestrians; a cyclist crossing is intended for cyclists and moped riders',
    c.text_nl = 'Een voetgangersoversteekplaats is bedoeld voor voetgangers; een fietsersoversteekplaats is bedoeld voor fietsers en bromfietsers',
    c.text_fr = 'Un passage pour piétons est destiné aux piétons ; une traversée cycliste est destinée aux cyclistes et aux cyclomotoristes'
WHERE rs.sign_code = 'A25' AND q.question_ref = 'A25_Q06' AND c.display_order = 1;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 0,
    c.text_ar = 'هما متطابقان وينطبقان على جميع مستخدمي الطريق دون تمييز',
    c.text_en = 'They are identical and apply to all road users without distinction',
    c.text_nl = 'Ze zijn identiek en gelden voor alle weggebruikers zonder onderscheid',
    c.text_fr = 'Ils sont identiques et s''appliquent à tous les usagers de la route sans distinction'
WHERE rs.sign_code = 'A25' AND q.question_ref = 'A25_Q06' AND c.display_order = 2;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 0,
    c.text_ar = 'ممر عبور المشاة أخطر دائماً من ممر عبور الدراجات ويستلزم التوقف في كل مرة',
    c.text_en = 'A pedestrian crossing is always more dangerous than a cyclist crossing and always requires stopping',
    c.text_nl = 'Een voetgangersoversteekplaats is altijd gevaarlijker dan een fietsersoversteekplaats en vereist altijd stoppen',
    c.text_fr = 'Un passage pour piétons est toujours plus dangereux qu''une traversée cycliste et impose toujours l''arrêt'
WHERE rs.sign_code = 'A25' AND q.question_ref = 'A25_Q06' AND c.display_order = 3;

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.question_ar = 'دراج مستعد لعبور الطريق عند ممر عبور الدراجات. هل يجب عليك التوقف؟',
    q.question_en = 'A cyclist is ready to cross the road at a cyclist crossing. Are you required to stop?',
    q.question_nl = 'Een fietser staat klaar om de rijbaan over te steken aan een fietsersoversteekplaats. Bent u verplicht te stoppen?',
    q.question_fr = 'Un cycliste est prêt à traverser la chaussée à une traversée cycliste. Êtes-vous obligé de vous arrêter ?',
    q.explanation_ar = 'عند ممر عبور الدراجات، يجب عليك التوقف أو التباطؤ لإعطاء الأولوية للدراجين الذين يعبرون أو يستعدون للعبور بوضوح. تنطبق القاعدة نفسها على ممرات عبور المشاة.',
    q.explanation_en = 'At a cyclist crossing, you must stop or slow down to give way to cyclists who are crossing or clearly about to cross. The same rule applies at pedestrian crossings.',
    q.explanation_nl = 'Aan een fietsersoversteekplaats moet u stoppen of vertragen om voorrang te verlenen aan fietsers die oversteken of duidelijk willen oversteken. Dezelfde regel geldt aan voetgangersoversteekplaatsen.',
    q.explanation_fr = 'À une traversée cycliste, vous devez vous arrêter ou ralentir pour céder la priorité aux cyclistes qui traversent ou qui s''apprêtent clairement à traverser. La même règle s''applique aux passages pour piétons.'
WHERE rs.sign_code = 'A25' AND q.question_ref = 'A25_Q07';

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 1,
    c.text_ar = 'نعم، يجب إعطاء الأولوية للدراج الذي يستعد للعبور',
    c.text_en = 'Yes, you must give way to a cyclist who is about to cross',
    c.text_nl = 'Ja, u moet voorrang verlenen aan een fietser die op het punt staat over te steken',
    c.text_fr = 'Oui, vous devez céder la priorité à un cycliste qui est sur le point de traverser'
WHERE rs.sign_code = 'A25' AND q.question_ref = 'A25_Q07' AND c.display_order = 1;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 0,
    c.text_ar = 'لا، يجب على الدراج الانتظار حتى يخلو الطريق قبل العبور',
    c.text_en = 'No, the cyclist must wait until the road is clear before crossing',
    c.text_nl = 'Nee, de fietser moet wachten tot de weg vrij is voordat hij oversteekt',
    c.text_fr = 'Non, le cycliste doit attendre que la route soit libre avant de traverser'
WHERE rs.sign_code = 'A25' AND q.question_ref = 'A25_Q07' AND c.display_order = 2;

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.question_ar = 'هل يُسمح بالتجاوز مباشرة قبل ممر عبور الدراجات؟',
    q.question_en = 'Is it allowed to overtake just before a cyclist crossing?',
    q.question_nl = 'Mag u vlak voor een fietsersoversteekplaats inhalen?',
    q.question_fr = 'Est-il permis de dépasser juste avant une traversée cycliste ?'
WHERE rs.sign_code = 'A25' AND q.question_ref = 'A25_Q08';
