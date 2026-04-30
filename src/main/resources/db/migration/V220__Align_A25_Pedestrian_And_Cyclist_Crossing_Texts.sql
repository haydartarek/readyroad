UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.question_ar = 'ما الفرق بين A21 (ممر عبور المشاة) و A25 (ممر عبور الدراجات)؟',
    q.question_en = 'What is the difference between A21 (pedestrian crossing) and A25 (cyclist crossing)?',
    q.question_nl = 'Wat is het verschil tussen A21 (oversteekplaats voor voetgangers) en A25 (oversteekplaats voor fietsers)?',
    q.question_fr = 'Quelle est la différence entre A21 (passage pour piétons) et A25 (traversée de cyclistes) ?',
    q.explanation_ar = 'تتعلق A21 بممرات عبور المشاة؛ وA25 بالدراجين وراكبي الدراجات البخارية. كلاهما يستوجب إعطاء الأولوية لمستخدمي الطريق المعنيين العابرين.',
    q.explanation_en = 'A21 refers to pedestrian crossings; A25 to cyclists and moped riders. Both require giving way to the respective road users who are crossing.',
    q.explanation_nl = 'A21 slaat op voetgangersoversteken; A25 op fietsers en bromfietsers. Beide vereisen voorrang verlenen aan de betrokken overstekende weggebruikers.',
    q.explanation_fr = 'A21 concerne les passages pour piétons; A25 les cyclistes et cyclomotoristes. Les deux exigent de céder la priorité aux usagers concernés qui traversent.'
WHERE rs.sign_code = 'A25' AND q.question_ref = 'A25_Q06';

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 1,
    c.text_ar = 'A21 ممر مميّز للمشاة؛ A25 ممر مميّز خصيصاً للدراجين وراكبي الدراجات البخارية',
    c.text_en = 'A21 is a marked crossing for pedestrians; A25 is a marked crossing specifically for cyclists and moped riders',
    c.text_nl = 'A21 is een gemarkeerde oversteek voor voetgangers; A25 is een gemarkeerde oversteek specifiek voor fietsers en bromfietsers',
    c.text_fr = 'A21 est un passage balisé pour piétons; A25 est un passage balisé spécifiquement pour cyclistes et cyclomotoristes'
WHERE rs.sign_code = 'A25' AND q.question_ref = 'A25_Q06' AND c.display_order = 1;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 0,
    c.text_ar = 'متطابقتان: كلتاهما تنطبق على جميع مستخدمي الطريق دون تمييز',
    c.text_en = 'They are identical: both apply to all road users without distinction',
    c.text_nl = 'Ze zijn identiek: beide gelden voor alle weggebruikers zonder onderscheid',
    c.text_fr = 'Ils sont identiques: les deux s''appliquent à tous les usagers de la route sans distinction'
WHERE rs.sign_code = 'A25' AND q.question_ref = 'A25_Q06' AND c.display_order = 2;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 0,
    c.text_ar = 'A21 أكثر خطورة من A25 وتستوجب دائماً التوقف',
    c.text_en = 'A21 is more dangerous than A25 and always requires stopping',
    c.text_nl = 'A21 is gevaarlijker dan A25 en vereist altijd stoppen',
    c.text_fr = 'A21 est plus dangereux que A25 et nécessite toujours de s''arrêter'
WHERE rs.sign_code = 'A25' AND q.question_ref = 'A25_Q06' AND c.display_order = 3;

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.question_ar = 'دراج مستعد للعبور عند A25. هل أنت ملزم بالتوقف؟',
    q.question_en = 'A cyclist is ready to cross the road at A25. Are you required to stop?',
    q.question_nl = 'Een fietser staat klaar om bij A25 de rijbaan over te steken. Bent u verplicht te stoppen?',
    q.question_fr = 'Un cycliste est prêt à traverser la chaussée à l''emplacement A25. Êtes-vous obligé de vous arrêter ?',
    q.explanation_ar = 'عند ممر عبور الدراجات (A25) يجب التوقف أو التباطؤ لإعطاء الأولوية للدراجين العابرين أو المزمعين العبور بوضوح. نفس القاعدة المطبقة على A21 للمشاة.',
    q.explanation_en = 'At a cyclist crossing (A25), you must stop or slow down to give way to cyclists who are crossing or clearly about to cross. The same rule applies as for A21 for pedestrians.',
    q.explanation_nl = 'Bij een oversteekplaats voor fietsers (A25) moet u stoppen of vertragen om voorrang te verlenen aan fietsers die oversteken of duidelijk aanstalten maken om over te steken. Dezelfde regel als bij A21 voor voetgangers.',
    q.explanation_fr = 'À une traversée de cyclistes (A25), vous devez vous arrêter ou ralentir pour céder la priorité aux cyclistes qui traversent ou s''apprêtent clairement à traverser. La même règle s''applique que pour A21 pour les piétons.'
WHERE rs.sign_code = 'A25' AND q.question_ref = 'A25_Q07';

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 1,
    c.text_ar = 'نعم، يجب إعطاء الأولوية للدراج المزمع العبور',
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
    c.text_nl = 'Neen, de fietser moet wachten tot de weg vrij is voordat hij oversteekt',
    c.text_fr = 'Non, le cycliste doit attendre que la route soit libre avant de traverser'
WHERE rs.sign_code = 'A25' AND q.question_ref = 'A25_Q07' AND c.display_order = 2;
