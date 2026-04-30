-- ════════════════════════════════════════════════════════════════════════════════
-- V236__Align_A_Series_Sign_Texts_With_JSON_Truth.sql
--
-- CORRECTIVE MIGRATION
-- Purpose : Reset all A-series sign texts to the canonical signs_import JSON source
--           of truth.  Conflicts were introduced by V216, V217, V218, V219, V221,
--           and V222 which set verbose, expanded, or incorrectly phrased text that
--           diverges from the signs_import JSON files.
--           V223–V234 (B/D/E families) are verified aligned and are NOT touched.
--
-- Affected tables  : road_signs, sign_questions, sign_choices
-- Affected families: A1c, A1d, A23, A25, A27
--
-- Section map
--   §1   road_signs.name_*           — A1c, A1d          (V216)
--   §2   sign_questions explanation  — A1c_Q01 AR        (V217)
--   §3   sign_choices                — A1c_Q01 d.o 2,3   (V217)
--   §4   sign_questions explanation  — A1c_Q03 AR        (V217)
--   §5   sign_choices                — A1c_Q03 d.o=1     (V217)
--   §6   sign_choices                — A1c_Q04 d.o=3 NL/FR/AR (V217)
--   §7   sign_questions question     — A1c_Q08 NL/FR/AR  (V217)
--   §8   sign_questions explanation  — A1d_Q01 AR        (V217)
--   §9   sign_choices                — A1d_Q01 d.o 1,2,3 (V217 + V218)
--   §10  sign_questions explanation  — A1d_Q03 AR        (V217)
--   §11  sign_choices                — A1d_Q03 d.o=3     (V217)
--   §12  sign_questions explanation  — A1d_Q04 AR        (V217)
--   §13  sign_choices                — A1d_Q04 d.o=3 NL/FR/AR (V217)
--   §14  sign_choices                — A23_Q01 d.o 1,2,3 (V219)
--   §15  sign_questions explanation  — A25_Q01            (V221)
--   §16  sign_questions explanation  — A25_Q04            (V221)
--   §17  sign_questions question+exp — A25_Q05            (V221)
--   §18  sign_questions question+exp — A25_Q06            (V221)
--   §19  sign_choices                — A25_Q06 d.o 1, 3  (V221)
--   §20  sign_questions question+exp — A25_Q07            (V221)
--   §21  sign_choices                — A25_Q07 d.o=1     (V221)
--   §22  sign_questions question     — A25_Q08            (V221)
--   §23  sign_questions explanation  — A27_Q01            (V222)
--
-- NOTE: A27_Q02–Q07 explanations set by V222 have NOT yet been compared against
--       JSON; a follow-up audit (V237) should verify those questions.
-- ════════════════════════════════════════════════════════════════════════════════

START TRANSACTION;

-- ────────────────────────────────────────────────────────────────────────────────
-- §1  road_signs.name_* — A1c and A1d
--     V216 set verbose long-form names; JSON uses concise parenthetical names.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    name_nl = 'Dubbele bocht (eerste naar links)',
       name_en = 'Double bend (first to the left)',
       name_fr = 'Double virage (premier à gauche)',
       name_ar = 'منعطف مزدوج (الأول إلى اليسار)'
WHERE  sign_code = 'A1c';

UPDATE road_signs
SET    name_nl = 'Dubbele bocht (eerste naar rechts)',
       name_en = 'Double bend (first to the right)',
       name_fr = 'Double virage (premier à droite)',
       name_ar = 'منعطف مزدوج (الأول إلى اليمين)'
WHERE  sign_code = 'A1d';

-- ────────────────────────────────────────────────────────────────────────────────
-- §2  sign_questions.explanation_ar — A1c_Q01
--     V217 replaced the JSON explanation with a rewrapped/extended AR sentence.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE sign_questions q
JOIN   road_signs rs ON rs.id = q.sign_id
SET    q.explanation_ar = 'تشير هذه العلامة إلى تعاقب منعطفات خطيرة، أولها إلى اليسار.'
WHERE  rs.sign_code = 'A1c'
  AND  q.question_ref = 'A1c_Q01';

-- ────────────────────────────────────────────────────────────────────────────────
-- §3  sign_choices — A1c_Q01 display_order 2 and 3
--     V217 replaced both wrong-answer texts with verbose bent descriptions.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE sign_choices c
JOIN   sign_questions q ON q.id = c.question_id
JOIN   road_signs     rs ON rs.id = q.sign_id
SET    c.text_nl = 'Opeenvolgende gevaarlijke bochten, eerst naar rechts',
       c.text_en = 'A succession of dangerous bends, first to the right',
       c.text_fr = 'Succession de virages dangereux, le premier à droite',
       c.text_ar = 'تعاقب منعطفات خطيرة، الأول إلى اليمين'
WHERE  rs.sign_code = 'A1c'
  AND  q.question_ref = 'A1c_Q01'
  AND  c.display_order = 2;

UPDATE sign_choices c
JOIN   sign_questions q ON q.id = c.question_id
JOIN   road_signs     rs ON rs.id = q.sign_id
SET    c.text_nl = 'Gevaarlijke bocht naar links',
       c.text_en = 'A dangerous bend to the left',
       c.text_fr = 'Virage dangereux à gauche',
       c.text_ar = 'منعطف خطير إلى اليسار'
WHERE  rs.sign_code = 'A1c'
  AND  q.question_ref = 'A1c_Q01'
  AND  c.display_order = 3;

-- ────────────────────────────────────────────────────────────────────────────────
-- §4  sign_questions.explanation_ar — A1c_Q03
--     V217 replaced the explanation with a shorter paraphrase that loses the key
--     phrase about "continuous changes in direction increase the risk of losing control".
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE sign_questions q
JOIN   road_signs rs ON rs.id = q.sign_id
SET    q.explanation_ar = 'تشير هذه العلامة المرورية إلى سلسلة من المنعطفات الخطيرة حيث تزيد تغييرات الاتجاه المتواصلة من خطر فقدان السيطرة. لذلك لا ينتهي الخطر بعد المنعطف الأول إلى اليسار.'
WHERE  rs.sign_code = 'A1c'
  AND  q.question_ref = 'A1c_Q03';

-- ────────────────────────────────────────────────────────────────────────────────
-- §5  sign_choices — A1c_Q03 display_order=1 (correct answer)
--     V217 shortened the correct-answer text; JSON has the full long-form text.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE sign_choices c
JOIN   sign_questions q ON q.id = c.question_id
JOIN   road_signs     rs ON rs.id = q.sign_id
SET    c.text_nl = 'Opeenvolgende gevaarlijke bochten waarbij voortdurende richtingsveranderingen het risico op controleverlies vergroten, met de eerste bocht naar links',
       c.text_en = 'A succession of dangerous bends where continuous changes in direction increase the risk of losing control, first to the left',
       c.text_fr = 'Une succession de virages dangereux où les changements continus de direction augmentent le risque de perte de contrôle, le premier étant à gauche',
       c.text_ar = 'سلسلة من المنعطفات الخطيرة حيث تزيد تغييرات الاتجاه المتواصلة من خطر فقدان السيطرة، وأولها إلى اليسار'
WHERE  rs.sign_code = 'A1c'
  AND  q.question_ref = 'A1c_Q03'
  AND  c.display_order = 1;

-- ────────────────────────────────────────────────────────────────────────────────
-- §6  sign_choices — A1c_Q04 display_order=3 (wrong answer)
--     V217 overwrote NL/FR/AR with the correct-answer text, creating a duplicate.
--     EN was not changed by V217 ("Stop and assess...") and remains correct.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE sign_choices c
JOIN   sign_questions q ON q.id = c.question_id
JOIN   road_signs     rs ON rs.id = q.sign_id
SET    c.text_nl = 'Stoppen en de situatie verkennen alvorens verder te rijden',
       c.text_fr = 'S''arrêter et évaluer la situation avant de continuer',
       c.text_ar = 'التوقف وتقييم الوضع قبل المواصلة'
WHERE  rs.sign_code = 'A1c'
  AND  q.question_ref = 'A1c_Q04'
  AND  c.display_order = 3;

-- ────────────────────────────────────────────────────────────────────────────────
-- §7  sign_questions.question — A1c_Q08
--     V217 changed NL/FR/AR from the JSON's advisory phrasing ("Doet u er goed aan" /
--     "Devrait-on" / "هل ينبغي") to an imperative form and added extra clauses.
--     EN was not changed by V217.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE sign_questions q
JOIN   road_signs rs ON rs.id = q.sign_id
SET    q.question_nl = 'Doet u er goed aan uw snelheid aan te passen op de hele reeks bochten die dit verkeersbord aankondigt?',
       q.question_fr = 'Devrait-on maintenir une vitesse réduite sur toute la séquence de virages annoncée par ce panneau ?',
       q.question_ar = 'هل ينبغي أن تحافظ على سرعة منخفضة على امتداد جميع المنعطفات؟'
WHERE  rs.sign_code = 'A1c'
  AND  q.question_ref = 'A1c_Q08';

-- ────────────────────────────────────────────────────────────────────────────────
-- §8  sign_questions.explanation_ar — A1d_Q01
--     V217 added a second instruction sentence to the AR explanation.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE sign_questions q
JOIN   road_signs rs ON rs.id = q.sign_id
SET    q.explanation_ar = 'تشير هذه العلامة إلى تعاقب منعطفات خطيرة، أولها إلى اليمين.'
WHERE  rs.sign_code = 'A1d'
  AND  q.question_ref = 'A1d_Q01';

-- ────────────────────────────────────────────────────────────────────────────────
-- §9  sign_choices — A1d_Q01 display_order 1, 2, and 3
--     V217 changed d.o=1 (correct) and d.o=2 (wrong) to verbose long-form descriptions.
--     V218 further changed d.o=3 to "Dangerous curve to the right" instead of
--     JSON's "A dangerous bend to the right".
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE sign_choices c
JOIN   sign_questions q ON q.id = c.question_id
JOIN   road_signs     rs ON rs.id = q.sign_id
SET    c.text_nl = 'Dubbele bocht (eerste naar rechts)',
       c.text_en = 'Double bend (first to the right)',
       c.text_fr = 'Double virage (premier à droite)',
       c.text_ar = 'منعطف مزدوج (الأول إلى اليمين)'
WHERE  rs.sign_code = 'A1d'
  AND  q.question_ref = 'A1d_Q01'
  AND  c.display_order = 1;

UPDATE sign_choices c
JOIN   sign_questions q ON q.id = c.question_id
JOIN   road_signs     rs ON rs.id = q.sign_id
SET    c.text_nl = 'Opeenvolgende gevaarlijke bochten, eerst naar links',
       c.text_en = 'A succession of dangerous bends, first to the left',
       c.text_fr = 'Succession de virages dangereux, le premier à gauche',
       c.text_ar = 'تعاقب منعطفات خطيرة، الأول إلى اليسار'
WHERE  rs.sign_code = 'A1d'
  AND  q.question_ref = 'A1d_Q01'
  AND  c.display_order = 2;

UPDATE sign_choices c
JOIN   sign_questions q ON q.id = c.question_id
JOIN   road_signs     rs ON rs.id = q.sign_id
SET    c.text_nl = 'Gevaarlijke bocht naar rechts',
       c.text_en = 'A dangerous bend to the right',
       c.text_fr = 'Virage dangereux à droite',
       c.text_ar = 'منعطف خطير إلى اليمين'
WHERE  rs.sign_code = 'A1d'
  AND  q.question_ref = 'A1d_Q01'
  AND  c.display_order = 3;

-- ────────────────────────────────────────────────────────────────────────────────
-- §10  sign_questions.explanation_ar — A1d_Q03
--      V217 replaced the JSON explanation with a different paraphrase that loses
--      the key phrase about continuous direction changes and centreline drift risk.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE sign_questions q
JOIN   road_signs rs ON rs.id = q.sign_id
SET    q.explanation_ar = 'تشير هذه العلامة المرورية إلى تعاقب منعطفات خطيرة حيث تزيد تغييرات الاتجاه المتواصلة من خطر فقدان السيطرة. ولأن المنعطف الأول إلى اليمين، فقد تنجرف المركبة أيضًا نحو خط الوسط إذا كانت السرعة مرتفعة.'
WHERE  rs.sign_code = 'A1d'
  AND  q.question_ref = 'A1d_Q03';

-- ────────────────────────────────────────────────────────────────────────────────
-- §11  sign_choices — A1d_Q03 display_order=3 (wrong answer)
--      V217 replaced "dangerous descent with multiple lanes" with a near-duplicate
--      of the correct answer ("succession of bends first to the right"), making the
--      question ambiguous and confusing.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE sign_choices c
JOIN   sign_questions q ON q.id = c.question_id
JOIN   road_signs     rs ON rs.id = q.sign_id
SET    c.text_nl = 'Een gevaarlijke daling met meerdere rijstroken',
       c.text_en = 'A dangerous descent with multiple lanes',
       c.text_fr = 'Une descente dangereuse avec plusieurs voies',
       c.text_ar = 'منحدر خطير بمسارات متعددة'
WHERE  rs.sign_code = 'A1d'
  AND  q.question_ref = 'A1d_Q03'
  AND  c.display_order = 3;

-- ────────────────────────────────────────────────────────────────────────────────
-- §12  sign_questions.explanation_ar — A1d_Q04
--      V217 changed "ينبغي" (should) to "يجب" (must) and added "دائمًا" (always).
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE sign_questions q
JOIN   road_signs rs ON rs.id = q.sign_id
SET    q.explanation_ar = 'تشير هذه العلامة المرورية إلى تعاقب منعطفات متعددة. ينبغي ضبط سرعتك على امتداد جميع المنعطفات والبقاء في مسارك.'
WHERE  rs.sign_code = 'A1d'
  AND  q.question_ref = 'A1d_Q04';

-- ────────────────────────────────────────────────────────────────────────────────
-- §13  sign_choices — A1d_Q04 display_order=3 (wrong answer)
--      V217 overwrote NL/FR/AR with the correct-answer text, creating a duplicate.
--      EN was not changed by V217 ("Change to the left lane...") and remains correct.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE sign_choices c
JOIN   sign_questions q ON q.id = c.question_id
JOIN   road_signs     rs ON rs.id = q.sign_id
SET    c.text_nl = 'Uw rijstrook wisselen naar links voor een betere positie in de bochten',
       c.text_fr = 'Changer de voie vers la gauche pour une meilleure position dans les virages',
       c.text_ar = 'الانتقال إلى المسار الأيسر لوضعية أفضل في المنعطفات'
WHERE  rs.sign_code = 'A1d'
  AND  q.question_ref = 'A1d_Q04'
  AND  c.display_order = 3;

-- ────────────────────────────────────────────────────────────────────────────────
-- §14  sign_choices — A23_Q01 display_order 1, 2, and 3
--      V219 expanded short single-word/phrase labels into verbose definitions.
--      JSON uses concise labels: "Children", "Pedestrian crossing", "Cyclist and moped crossing".
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE sign_choices c
JOIN   sign_questions q ON q.id = c.question_id
JOIN   road_signs     rs ON rs.id = q.sign_id
SET    c.text_nl = 'Kinderen',
       c.text_en = 'Children',
       c.text_fr = 'Enfants',
       c.text_ar = 'وجود أطفال'
WHERE  rs.sign_code = 'A23'
  AND  q.question_ref = 'A23_Q01'
  AND  c.display_order = 1;

UPDATE sign_choices c
JOIN   sign_questions q ON q.id = c.question_id
JOIN   road_signs     rs ON rs.id = q.sign_id
SET    c.text_nl = 'Oversteekplaats voor voetgangers',
       c.text_en = 'Pedestrian crossing',
       c.text_fr = 'Passage pour piétons',
       c.text_ar = 'ممر عبور المشاة'
WHERE  rs.sign_code = 'A23'
  AND  q.question_ref = 'A23_Q01'
  AND  c.display_order = 2;

UPDATE sign_choices c
JOIN   sign_questions q ON q.id = c.question_id
JOIN   road_signs     rs ON rs.id = q.sign_id
SET    c.text_nl = 'Oversteekplaats voor fietsers en bromfietsers',
       c.text_en = 'Cyclist and moped crossing',
       c.text_fr = 'Traversée pour cyclistes et cyclomoteurs',
       c.text_ar = 'ممر عبور الدراجات والدراجات البخارية'
WHERE  rs.sign_code = 'A23'
  AND  q.question_ref = 'A23_Q01'
  AND  c.display_order = 3;

-- ────────────────────────────────────────────────────────────────────────────────
-- §15  sign_questions.explanation — A25_Q01
--      V221 appended an extra enforcement sentence not present in the JSON.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE sign_questions q
JOIN   road_signs rs ON rs.id = q.sign_id
SET    q.explanation_nl = 'Dit verkeersbord waarschuwt dat fietsers en bromfietsers hier de rijbaan kunnen oversteken.',
       q.explanation_en = 'This traffic sign warns that cyclists and moped riders may cross the road here.',
       q.explanation_fr = 'Ce panneau avertit que des cyclistes et des cyclomotoristes peuvent traverser la chaussée à cet endroit.',
       q.explanation_ar = 'تشير هذه العلامة المرورية إلى أن الدراجين وراكبي الدراجات البخارية قد يعبرون الطريق في هذا المكان.'
WHERE  rs.sign_code = 'A25'
  AND  q.question_ref = 'A25_Q01';

-- ────────────────────────────────────────────────────────────────────────────────
-- §16  sign_questions.explanation — A25_Q04
--      V221 upgraded advisory guidance ("should"/"devriez"/"ينبغي") to imperative
--      ("must"/"devez"/"يجب").  JSON uses the softer advisory form.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE sign_questions q
JOIN   road_signs rs ON rs.id = q.sign_id
SET    q.explanation_nl = 'Wanneer u dit bord ziet, mindert u best snelheid en let u extra op fietsers en bromfietsers die onverwacht kunnen oversteken.',
       q.explanation_en = 'You should slow down and watch carefully for cyclists and moped riders who may cross unexpectedly.',
       q.explanation_fr = 'Lorsque vous voyez ce panneau, vous devriez réduire votre vitesse et accorder une attention particulière aux cyclistes et cyclomotoristes qui peuvent traverser de manière inattendue.',
       q.explanation_ar = 'عند رؤية هذه العلامة المرورية، ينبغي عليك تقليل السرعة والانتباه بشكل خاص للدراجين وراكبي الدراجات البخارية الذين قد يعبرون الطريق فجأة.'
WHERE  rs.sign_code = 'A25'
  AND  q.question_ref = 'A25_Q04';

-- ────────────────────────────────────────────────────────────────────────────────
-- §17  sign_questions question + explanation — A25_Q05
--      V221 used shortened "cyclist crossing" / "fietsersoversteekplaats" /
--      "traversée cycliste" throughout; JSON uses the full legal term
--      "cyclist and moped crossing" / "oversteekplaats voor fietsers en bromfietsers" /
--      "passage pour cyclistes et cyclomotoristes".
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE sign_questions q
JOIN   road_signs rs ON rs.id = q.sign_id
SET    q.question_nl    = 'Een fietser nadert de oversteekplaats voor fietsers en bromfietsers van rechts. Wat doet u?',
       q.question_en    = 'A cyclist is approaching the cyclist and moped crossing from the right. What do you do?',
       q.question_fr    = 'Un cycliste approche du passage pour cyclistes et cyclomotoristes par la droite. Que faites-vous ?',
       q.question_ar    = 'دراج يقترب من ممر عبور الدراجات والدراجات البخارية من جهة اليمين. ماذا تفعل؟',
       q.explanation_nl = 'Aan een oversteekplaats voor fietsers en bromfietsers moet u voorrang verlenen aan fietsers en bromfietsers die oversteken of duidelijk op het punt staan over te steken. U moet stoppen als dat nodig is om hen veilig te laten passeren.',
       q.explanation_en = 'At a cyclist and moped crossing, you must give way to cyclists and moped riders who are crossing or clearly about to cross. You must stop if necessary to let them pass safely.',
       q.explanation_fr = 'À un passage pour cyclistes et cyclomotoristes, vous devez céder la priorité aux cyclistes et aux cyclomotoristes qui traversent ou qui montrent clairement l''intention de traverser. Vous devez vous arrêter si nécessaire pour les laisser passer en sécurité.',
       q.explanation_ar = 'عند ممر عبور الدراجات والدراجات البخارية، يجب إعطاء الأولوية للدراجين وراكبي الدراجات البخارية الذين يعبرون أو يظهر بوضوح أنهم على وشك العبور. يجب التوقف إذا لزم الأمر للسماح لهم بالمرور بأمان.'
WHERE  rs.sign_code = 'A25'
  AND  q.question_ref = 'A25_Q05';

-- ────────────────────────────────────────────────────────────────────────────────
-- §18  sign_questions question + explanation — A25_Q06
--      V221 used "cyclist crossing" / "fietsersoversteekplaats" / "traversée cycliste".
--      JSON uses the full legal term throughout.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE sign_questions q
JOIN   road_signs rs ON rs.id = q.sign_id
SET    q.question_nl    = 'Wat is het verschil tussen een voetgangersoversteekplaats en een oversteekplaats voor fietsers en bromfietsers?',
       q.question_en    = 'What is the difference between a pedestrian crossing and a cyclist and moped crossing?',
       q.question_fr    = 'Quelle est la différence entre un passage pour piétons et un passage pour cyclistes et cyclomotoristes ?',
       q.question_ar    = 'ما الفرق بين ممر عبور المشاة وممر عبور الدراجات والدراجات البخارية؟',
       q.explanation_nl = 'Een voetgangersoversteekplaats is voor voetgangers, terwijl een oversteekplaats voor fietsers en bromfietsers bedoeld is voor fietsers en bromfietsers. In beide gevallen moet u voorrang verlenen aan de betrokken overstekers.',
       q.explanation_en = 'A pedestrian crossing is for pedestrians, while a crossing for cyclists and moped riders is for cyclists and moped riders. In both cases, you must give way to the relevant road users who are crossing.',
       q.explanation_fr = 'Un passage pour piétons concerne les piétons, tandis qu''un passage pour cyclistes et cyclomotoristes concerne les cyclistes et les cyclomotoristes. Dans les deux cas, vous devez céder la priorité aux usagers concernés qui traversent.',
       q.explanation_ar = 'ممر عبور المشاة يخص المشاة، أما ممر عبور الدراجات والدراجات البخارية فيخص الدراجين وراكبي الدراجات البخارية. في الحالتين يجب إعطاء الأولوية للعابرين المعنيين.'
WHERE  rs.sign_code = 'A25'
  AND  q.question_ref = 'A25_Q06';

-- ────────────────────────────────────────────────────────────────────────────────
-- §19  sign_choices — A25_Q06
--      d.o=1 (correct): "fietsersoversteekplaats" → "oversteekplaats voor fietsers en bromfietsers"
--      d.o=3 (wrong):   "cyclist crossing" → "cyclist and moped crossing" (NL/EN/FR only;
--                        AR d.o=3 already matches JSON and is not touched).
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE sign_choices c
JOIN   sign_questions q ON q.id = c.question_id
JOIN   road_signs     rs ON rs.id = q.sign_id
SET    c.text_nl = 'Een voetgangersoversteekplaats is bedoeld voor voetgangers; een oversteekplaats voor fietsers en bromfietsers is bedoeld voor fietsers en bromfietsers',
       c.text_en = 'A pedestrian crossing is for pedestrians, whereas a cyclist and moped crossing is for cyclists and moped riders',
       c.text_fr = 'Un passage pour piétons est destiné aux piétons, tandis qu''un passage pour cyclistes et cyclomotoristes est destiné aux cyclistes et aux cyclomotoristes',
       c.text_ar = 'ممر عبور المشاة مخصص للمشاة، أما ممر عبور الدراجات والدراجات البخارية فهو مخصص للدراجين وراكبي الدراجات البخارية'
WHERE  rs.sign_code = 'A25'
  AND  q.question_ref = 'A25_Q06'
  AND  c.display_order = 1;

UPDATE sign_choices c
JOIN   sign_questions q ON q.id = c.question_id
JOIN   road_signs     rs ON rs.id = q.sign_id
SET    c.text_nl = 'Een voetgangersoversteekplaats is altijd gevaarlijker dan een oversteekplaats voor fietsers en bromfietsers en vereist altijd stoppen',
       c.text_en = 'A pedestrian crossing is always more dangerous than a cyclist and moped crossing and always requires stopping',
       c.text_fr = 'Un passage pour piétons est toujours plus dangereux qu''un passage pour cyclistes et cyclomotoristes et impose toujours l''arrêt'
WHERE  rs.sign_code = 'A25'
  AND  q.question_ref = 'A25_Q06'
  AND  c.display_order = 3;

-- ────────────────────────────────────────────────────────────────────────────────
-- §20  sign_questions question + explanation — A25_Q07
--      V221 simplified the question (removed "clearly about to cross"), used
--      "cyclist crossing" terminology, and used a 2nd-person question format
--      instead of the JSON's 3rd-person driver-focused question.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE sign_questions q
JOIN   road_signs rs ON rs.id = q.sign_id
SET    q.question_nl    = 'Wanneer een fietser een oversteekplaats voor fietsers en bromfietsers nadert en duidelijk wil oversteken, moet de bestuurder dan stoppen?',
       q.question_en    = 'When a cyclist approaches a cyclist and moped crossing and is clearly about to cross, must the driver stop?',
       q.question_fr    = 'Lorsqu''un cycliste s''approche d''un passage pour cyclistes et cyclomotoristes et montre clairement l''intention de traverser, le conducteur doit-il s''arrêter ?',
       q.question_ar    = 'هل يجب على السائق التوقف عند اقتراب دراج من ممر عبور الدراجات والدراجات البخارية وظهور نيته بوضوح في العبور؟',
       q.explanation_nl = 'Aan een oversteekplaats voor fietsers en bromfietsers moet u stoppen of vertragen om voorrang te verlenen aan fietsers die oversteken of duidelijk op het punt staan over te steken. Dezelfde regel geldt aan voetgangersoversteekplaatsen.',
       q.explanation_en = 'At a crossing for cyclists and moped riders, you must stop or slow down to give way to cyclists who are crossing or clearly about to cross. The same rule applies at pedestrian crossings.',
       q.explanation_fr = 'À un passage pour cyclistes et cyclomotoristes, vous devez vous arrêter ou ralentir pour céder la priorité aux cyclistes qui traversent ou qui s''apprêtent clairement à traverser. La même règle s''applique aux passages pour piétons.',
       q.explanation_ar = 'عند ممر عبور الدراجات والدراجات البخارية، يجب عليك التوقف أو تقليل السرعة لإعطاء الأولوية للدراجين الذين يعبرون أو يظهر بوضوح أنهم على وشك العبور. تنطبق القاعدة نفسها على ممرات عبور المشاة.'
WHERE  rs.sign_code = 'A25'
  AND  q.question_ref = 'A25_Q07';

-- ────────────────────────────────────────────────────────────────────────────────
-- §21  sign_choices — A25_Q07 display_order=1 (correct answer)
--      V221 dropped "clearly" / "duidelijk" / "clairement" / "يظهر بوضوح أنه على وشك"
--      from the choice text.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE sign_choices c
JOIN   sign_questions q ON q.id = c.question_id
JOIN   road_signs     rs ON rs.id = q.sign_id
SET    c.text_nl = 'Ja, u moet voorrang verlenen aan een fietser die duidelijk op het punt staat over te steken',
       c.text_en = 'Yes, you must give way to a cyclist who is clearly about to cross',
       c.text_fr = 'Oui, vous devez céder la priorité à un cycliste qui montre clairement l''intention de traverser',
       c.text_ar = 'نعم، يجب إعطاء الأولوية للدراج الذي يظهر بوضوح أنه على وشك العبور'
WHERE  rs.sign_code = 'A25'
  AND  q.question_ref = 'A25_Q07'
  AND  c.display_order = 1;

-- ────────────────────────────────────────────────────────────────────────────────
-- §22  sign_questions.question — A25_Q08
--      V221 used "cyclist crossing" / "fietsersoversteekplaats" / "traversée cycliste" /
--      "ممر عبور الدراجات" in all four languages.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE sign_questions q
JOIN   road_signs rs ON rs.id = q.sign_id
SET    q.question_nl = 'Mag u vlak voor een oversteekplaats voor fietsers en bromfietsers inhalen?',
       q.question_en = 'Is it allowed to overtake just before a cyclist and moped crossing?',
       q.question_fr = 'Est-il permis de dépasser juste avant un passage pour cyclistes et cyclomotoristes ?',
       q.question_ar = 'هل يُسمح بالتجاوز مباشرة قبل ممر عبور الدراجات والدراجات البخارية؟'
WHERE  rs.sign_code = 'A25'
  AND  q.question_ref = 'A25_Q08';

-- ────────────────────────────────────────────────────────────────────────────────
-- §23  sign_questions.explanation — A27_Q01
--      V222 inserted "large" ("grote" / "grands" / "كبيرة") before "wild animals".
--      JSON simply says "wild animals" without any size qualifier.
--      NOTE: A27_Q02–Q07 set by V222 have not been compared against JSON yet.
--            Include in a follow-up V237 audit before considering A27 fully aligned.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE sign_questions q
JOIN   road_signs rs ON rs.id = q.sign_id
SET    q.explanation_nl = 'Dit verkeersbord waarschuwt voor wilde dieren zoals herten of everzwijnen die de rijbaan kunnen oversteken. Deze dieren zijn onvoorspelbaar en kunnen plots voor uw voertuig verschijnen.',
       q.explanation_en = 'This traffic sign warns of wild animals such as deer or wild boar that may cross the road. These animals are unpredictable and may suddenly appear in front of your vehicle.',
       q.explanation_fr = 'Ce panneau avertit de la présence d''animaux sauvages, comme des cerfs ou des sangliers, pouvant traverser la chaussée. Ces animaux sont imprévisibles et peuvent surgir soudainement devant votre véhicule.',
       q.explanation_ar = 'تشير هذه العلامة المرورية إلى احتمال عبور حيوانات برية مثل الغزلان والخنازير البرية للطريق. هذه الحيوانات غير متوقعة وقد تظهر فجأة أمام مركبتك.'
WHERE  rs.sign_code = 'A27'
  AND  q.question_ref = 'A27_Q01';

COMMIT;
