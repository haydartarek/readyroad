-- ════════════════════════════════════════════════════════════════════════════════
-- V249__Align_A27_Q02_Q08_With_JSON_Truth.sql
--
-- CORRECTIVE MIGRATION
-- Purpose : Reset A27 sign question/choice texts for Q02–Q08 to the canonical
--           signs_import JSON source of truth.
--           V222 (Finalize_A27_Wild_Animals_Polish) introduced two classes of
--           divergence from the JSON:
--             (a) "large"/"groot wild"/"grand gibier" qualifier inserted where JSON
--                 simply says "wild animals"/"wilde dieren"/"animaux sauvages"
--             (b) Advisory phrasing ("should"/"devriez"/"ينبغي") upgraded to
--                 imperative ("must"/"devez"/"يجب") in Q04 and Q07
--             (c) Q02 explanation replaced with generic sign-shape description
--                 instead of the A27/A29/A21 comparison from JSON
--             (d) Q06 and Q07 questions and explanations rewritten with different
--                 framing and vocabulary
--           V236 §23 already fixed Q01.  This migration covers Q02–Q08.
--
-- Affected tables  : sign_questions, sign_choices
-- Affected sign    : A27 (wild animals crossing)
--
-- Section map
--   §1   sign_questions explanation       — A27_Q02  (V222 shape-description override)
--   §2   sign_questions + sign_choices    — A27_Q03  (safety-net: untouched by V222)
--   §3   sign_questions question+exp      — A27_Q04  (V222 "must" override)
--   §4   sign_questions explanation       — A27_Q05  (V222 "large" qualifier)
--   §5   sign_questions question+exp      — A27_Q06  (V222 "grand gibier" override)
--   §6   sign_choices                     — A27_Q06  d.o 1,2,3
--   §7   sign_questions question+exp      — A27_Q07  (V222 obligatory phrasing)
--   §8   sign_questions question+exp      — A27_Q08  (V222 "Large wild animals")
-- ════════════════════════════════════════════════════════════════════════════════

START TRANSACTION;

-- ────────────────────────────────────────────────────────────────────────────────
-- §1  sign_questions.explanation — A27_Q02
--     V222 replaced the JSON's A27/A29/A21 comparison explanation with a generic
--     "triangular, red border, white background" shape description that is wrong
--     for a WHICH_SIGN question type.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE sign_questions q
JOIN   road_signs rs ON rs.id = q.sign_id
SET    q.explanation_nl = 'Het gevaarsbord A27 waarschuwt specifiek voor overstekend wild. Het bord voor vee (A29) lijkt er op maar geldt voor begeleide dieren. Het bord voor voetgangers (A21) geldt voor mensen op een zebrapad.',
       q.explanation_en = 'Danger sign A27 specifically warns of wild animals crossing. The livestock sign (A29) looks similar but applies to animals accompanied by a handler. The pedestrian crossing sign (A21) applies to people at a zebra crossing.',
       q.explanation_fr = 'Le panneau de danger A27 signale spécifiquement le risque d''animaux sauvages traversant. Le panneau bétail (A29) lui ressemble mais concerne des animaux accompagnés. Le panneau piétons (A21) concerne les personnes sur un passage clouté.',
       q.explanation_ar = 'علامة الخطر A27 تُحذّر تحديداً من حيوانات برية تعبر الطريق. علامة المواشي (A29) تشبهها لكنها تختص بحيوانات برفقة سائق. علامة المشاة (A21) تختص بالأشخاص على الممر المخطط.'
WHERE  rs.sign_code = 'A27'
  AND  q.question_ref = 'A27_Q02';

-- ────────────────────────────────────────────────────────────────────────────────
-- §2  sign_questions + sign_choices — A27_Q03  (safety-net alignment)
--     V222 did not touch Q03.  This section ensures the question text and all three
--     choice texts match the JSON exactly in case an earlier migration diverged.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE sign_questions q
JOIN   road_signs rs ON rs.id = q.sign_id
SET    q.question_nl    = 'Welk gevaar kondigt dit bord aan?',
       q.question_en    = 'What hazard does this sign announce?',
       q.question_fr    = 'Quel danger ce panneau annonce-t-il ?',
       q.question_ar    = 'ما الخطر الذي يُحذّرك منه هذا اللوح في منطقة الحيوانات البرية؟',
       q.explanation_nl = 'Wilde dieren zoals herten en everzwijnen zijn het gevaarlijkst bij schemering en ''s nachts. Ze steken de weg onverwacht over en zijn moeilijk zichtbaar in het duister.',
       q.explanation_en = 'Wild animals such as deer and wild boar are most dangerous at dusk and at night. They cross the road unexpectedly and are difficult to see in darkness.',
       q.explanation_fr = 'Les animaux sauvages comme les cerfs et les sangliers sont les plus dangereux au crépuscule et la nuit. Ils traversent la route de manière inattendue et sont difficiles à voir dans l''obscurité.',
       q.explanation_ar = 'الحيوانات البرية مثل الغزلان والخنازير البرية أشد خطورة عند الغسق وفي الليل. تعبر الطريق بصورة غير متوقعة وتصعب رؤيتها في الظلام.'
WHERE  rs.sign_code = 'A27'
  AND  q.question_ref = 'A27_Q03';

UPDATE sign_choices c
JOIN   sign_questions q ON q.id = c.question_id
JOIN   road_signs     rs ON rs.id = q.sign_id
SET    c.text_nl = 'Wilde dieren kunnen plotseling de rijbaan oversteken',
       c.text_en = 'Wild animals may suddenly cross the road',
       c.text_fr = 'Des animaux sauvages peuvent soudainement traverser la chaussée',
       c.text_ar = 'حيوانات برية قد تعبر الطريق فجأة'
WHERE  rs.sign_code = 'A27'
  AND  q.question_ref = 'A27_Q03'
  AND  c.display_order = 1;

UPDATE sign_choices c
JOIN   sign_questions q ON q.id = c.question_id
JOIN   road_signs     rs ON rs.id = q.sign_id
SET    c.text_nl = 'Een dierenpark aan de wegzijde met uitlopers op de rijbaan',
       c.text_en = 'A wildlife park at the roadside with animals straying onto the road',
       c.text_fr = 'Un parc animalier en bord de route avec des animaux s''aventurant sur la chaussée',
       c.text_ar = 'حديقة حيوانات على جانب الطريق تتسلل منها حيوانات إلى الطريق'
WHERE  rs.sign_code = 'A27'
  AND  q.question_ref = 'A27_Q03'
  AND  c.display_order = 2;

UPDATE sign_choices c
JOIN   sign_questions q ON q.id = c.question_id
JOIN   road_signs     rs ON rs.id = q.sign_id
SET    c.text_nl = 'Landbouwvoertuigen die vanuit velden de rijbaan oprijden',
       c.text_en = 'Agricultural vehicles entering the road from fields',
       c.text_fr = 'Des véhicules agricoles entrant sur la route depuis les champs',
       c.text_ar = 'مركبات زراعية تدخل الطريق من الحقول'
WHERE  rs.sign_code = 'A27'
  AND  q.question_ref = 'A27_Q03'
  AND  c.display_order = 3;

-- ────────────────────────────────────────────────────────────────────────────────
-- §3  sign_questions question + explanation — A27_Q04
--     V222 changed "What should you do" → "What must you do" (NL/EN/FR/AR) and
--     replaced "you should significantly reduce speed" with "you must significantly
--     reduce speed" in the explanation.  JSON uses the softer advisory form.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE sign_questions q
JOIN   road_signs rs ON rs.id = q.sign_id
SET    q.question_nl    = 'Wat doet u best wanneer u dit verkeersbord ziet, vooral bij schemering of ''s nachts?',
       q.question_en    = 'What should you do when you see this sign, especially at dusk or at night?',
       q.question_fr    = 'Que devriez-vous faire lorsque vous voyez ce panneau, surtout au crépuscule ou la nuit ?',
       q.question_ar    = 'ماذا ينبغي عليك فعله عند رؤية هذه العلامة المرورية، خاصة عند الغسق أو في الليل؟',
       q.explanation_nl = 'In deze zone verlaagt u best uw snelheid aanzienlijk en blijft u extra waakzaam. Het risico is het grootst bij schemering en ''s nachts, en grootlicht kan helpen om dieren tijdig op te merken wanneer het veilig is om het te gebruiken.',
       q.explanation_en = 'In this area, you should significantly reduce speed and remain highly alert. The risk is greatest at dusk and at night, and full beam can help you spot animals in time when it is safe to use it.',
       q.explanation_fr = 'Dans cette zone, vous devriez réduire nettement votre vitesse et rester très vigilant. Le risque est maximal au crépuscule et la nuit, et les pleins phares peuvent aider à repérer les animaux à temps lorsqu''il est sûr de les utiliser.',
       q.explanation_ar = 'في هذه المنطقة ينبغي عليك تخفيض السرعة بشكل ملحوظ والحفاظ على درجة عالية من الانتباه. يزداد الخطر عند الغسق وفي الليل، وقد تساعد الأضواء العالية على رؤية الحيوانات في الوقت المناسب عندما يكون استخدامها مسموحًا.'
WHERE  rs.sign_code = 'A27'
  AND  q.question_ref = 'A27_Q04';

-- ────────────────────────────────────────────────────────────────────────────────
-- §4  sign_questions.explanation — A27_Q05
--     V222 inserted "large" ("groot"/"grand") before "wild animal" in the explanation
--     ("Never move a large injured animal" / "Verplaats een groot gewond dier" /
--     "Ne déplacez jamais vous-même un grand animal" / "لا تحاول أبدًا نقل حيوان
--     كبير مصاب").  JSON uses plain "wild animal"/"gewond wild dier" without the
--     size qualifier, and uses "collision with a wild animal" not "collision with
--     wildlife".
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE sign_questions q
JOIN   road_signs rs ON rs.id = q.sign_id
SET    q.explanation_nl = 'Na een aanrijding met een wild dier moet u stoppen, de weg beveiligen en de politie en eventueel de lokale dienst voor wildschade verwittigen. Verplaats een gewond wild dier nooit zelf, want het kan gevaarlijk reageren.',
       q.explanation_en = 'After a collision with a wild animal, you must stop, secure the road and notify the police and the local wildlife damage authority. Never move an injured wild animal yourself, because it may react dangerously.',
       q.explanation_fr = 'Après une collision avec un animal sauvage, vous devez vous arrêter, sécuriser la route et avertir la police ainsi que l''instance locale compétente pour les dégâts causés par la faune. Ne déplacez jamais vous-même un animal sauvage blessé, car il peut réagir dangereusement.',
       q.explanation_ar = 'بعد الاصطدام بحيوان بري، يجب التوقف وتأمين الطريق وإبلاغ الشرطة والجهة المحلية المختصة بأضرار الحياة البرية. لا تحاول أبدًا نقل الحيوان البري المصاب بنفسك، لأنه قد يتصرف بشكل خطير.'
WHERE  rs.sign_code = 'A27'
  AND  q.question_ref = 'A27_Q05';

-- ────────────────────────────────────────────────────────────────────────────────
-- §5  sign_questions question + explanation — A27_Q06
--     V222 changed:
--       NL: "overstekende wilde dieren" → "overstekend groot wild" (introduced hunting
--           jargon not present in JSON)
--       FR: "animaux sauvages" → "grand gibier" (hunting register)
--       AR: added "الكبيرة" (large/major)
--     The explanation was rewritten from the JSON's "animals living in the wild that
--     may appear unexpectedly" / "domesticated farm animals usually guided by handler"
--     to V222's "untamed animals in forest and rural areas" / different vocabulary.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE sign_questions q
JOIN   road_signs rs ON rs.id = q.sign_id
SET    q.question_nl    = 'Wat is het verschil tussen een waarschuwingsbord voor overstekende wilde dieren en een waarschuwingsbord voor overstekend vee?',
       q.question_en    = 'What is the difference between a wild animal crossing sign and a livestock crossing sign?',
       q.question_fr    = 'Quelle est la différence entre un panneau de passage d''animaux sauvages et un panneau de passage de bétail ?',
       q.question_ar    = 'ما الفرق بين علامة عبور الحيوانات البرية وعلامة عبور الماشية؟',
       q.explanation_nl = 'Een waarschuwingsbord voor overstekende wilde dieren heeft betrekking op dieren die in de natuur leven en onvoorspelbaar zijn. Een waarschuwingsbord voor overstekend vee gaat over gedomesticeerde landbouwdieren die meestal onder begeleiding de weg oversteken.',
       q.explanation_en = 'A wild animal crossing sign concerns animals living in the wild that may appear unexpectedly. A livestock crossing sign concerns farm animals that are usually guided across the road by a handler.',
       q.explanation_fr = 'Le panneau de passage d''animaux sauvages concerne des animaux vivant à l''état sauvage, donc imprévisibles. Le panneau de passage de bétail concerne des animaux d''élevage domestiqués, généralement conduits à travers la route par un accompagnateur.',
       q.explanation_ar = 'علامة عبور الحيوانات البرية تتعلق بحيوانات تعيش في الطبيعة وقد تظهر بشكل غير متوقع. أما علامة عبور الماشية فتتعلق بحيوانات مزرعة مستأنسة تُقاد عادة عبر الطريق من قبل شخص مرافق.'
WHERE  rs.sign_code = 'A27'
  AND  q.question_ref = 'A27_Q06';

-- ────────────────────────────────────────────────────────────────────────────────
-- §6  sign_choices — A27_Q06 display_order 1, 2, and 3
--     V222 rewrote all three choice texts to match its "grand gibier"/"groot wild"
--     vocabulary.  JSON uses plain "wild animals"/"wilde dieren"/"animaux sauvages".
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE sign_choices c
JOIN   sign_questions q ON q.id = c.question_id
JOIN   road_signs     rs ON rs.id = q.sign_id
SET    c.text_nl = 'Overstekende wilde dieren: dieren die in de natuur leven en plots kunnen opduiken; overstekend vee: landbouwdieren zoals koeien en schapen, meestal met een begeleider',
       c.text_en = 'Wild animals crossing: animals living in the wild that may appear suddenly; livestock crossing: farm animals such as cows and sheep, usually with a handler',
       c.text_fr = 'Passage d''animaux sauvages : des animaux vivant à l''état sauvage peuvent surgir soudainement ; passage de bétail : des animaux de ferme comme les vaches et les moutons, généralement accompagnés d''un berger',
       c.text_ar = 'عبور الحيوانات البرية: حيوانات تعيش في الطبيعة وقد تظهر فجأة، وعبور الماشية: حيوانات مزرعة مثل الأبقار والأغنام، غالبًا تكون مع راعٍ'
WHERE  rs.sign_code = 'A27'
  AND  q.question_ref = 'A27_Q06'
  AND  c.display_order = 1;

UPDATE sign_choices c
JOIN   sign_questions q ON q.id = c.question_id
JOIN   road_signs     rs ON rs.id = q.sign_id
SET    c.text_nl = 'Ze zijn identiek en duiden allebei simpelweg op dieren die de weg oversteken',
       c.text_en = 'They are identical and both simply indicate animals crossing the road',
       c.text_fr = 'Ils sont identiques et signalent simplement des animaux qui traversent la route',
       c.text_ar = 'هما متطابقتان وتشيران ببساطة إلى حيوانات تعبر الطريق'
WHERE  rs.sign_code = 'A27'
  AND  q.question_ref = 'A27_Q06'
  AND  c.display_order = 2;

UPDATE sign_choices c
JOIN   sign_questions q ON q.id = c.question_id
JOIN   road_signs     rs ON rs.id = q.sign_id
SET    c.text_nl = 'Een bord voor overstekend groot wild verbiedt alle doorgang, terwijl een bord voor overstekend vee langzaam doorrijden toestaat',
       c.text_en = 'A wild animals crossing sign completely forbids passage, while a livestock crossing sign allows slow driving through',
       c.text_fr = 'Le panneau de passage d''animaux sauvages interdit totalement le passage, tandis que le panneau de passage de bétail autorise un passage lent',
       c.text_ar = 'علامة عبور الحيوانات البرية تمنع المرور تمامًا، بينما تسمح علامة عبور الماشية بالمرور البطيء'
WHERE  rs.sign_code = 'A27'
  AND  q.question_ref = 'A27_Q06'
  AND  c.display_order = 3;

-- ────────────────────────────────────────────────────────────────────────────────
-- §7  sign_questions question + explanation — A27_Q07
--     V222 changed the question from JSON's advisory IS_IT_ALLOWED format
--     ("Should you still reduce your speed?" / "Doet u er goed aan...") to an
--     obligatory "Are you still required to reduce your speed?" / "Bent u nog
--     steeds verplicht".
--     V222 also rewrote the explanation from JSON's concise "Yes. Animals may
--     appear suddenly at any time in this area" to a longer variant that changes
--     the meaning ("This warning remains in force throughout the area").
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE sign_questions q
JOIN   road_signs rs ON rs.id = q.sign_id
SET    q.question_nl    = 'U passeert dit verkeersbord maar ziet geen dieren. Doet u er nog steeds goed aan uw snelheid te verminderen?',
       q.question_en    = 'You pass this sign but see no animals. Should you still reduce your speed?',
       q.question_fr    = 'Vous passez ce panneau mais ne voyez aucun animal. Devriez-vous tout de même réduire votre vitesse ?',
       q.question_ar    = 'تمر بهذه العلامة المرورية لكن لا ترى أي حيوانات. هل ينبغي عليك رغم ذلك تخفيض سرعتك؟',
       q.explanation_nl = 'Ja. Dieren kunnen zich in deze zone verschuilen in de begroeiing naast de weg en op elk moment plots oversteken. Een lagere snelheid geeft u meer tijd om te reageren.',
       q.explanation_en = 'Yes. Animals may appear suddenly at any time in this area, even if none are visible at the moment. Reducing speed gives you more time to react.',
       q.explanation_fr = 'Oui. Des animaux peuvent se cacher dans la végétation au bord de la route et surgir à tout moment dans cette zone. Réduire la vitesse vous laisse plus de temps pour réagir.',
       q.explanation_ar = 'نعم. قد تختبئ الحيوانات في الغطاء النباتي على جانب الطريق وتندفع إلى الطريق في أي لحظة داخل هذه المنطقة. تخفيض السرعة يمنحك وقتًا أكبر لرد الفعل.'
WHERE  rs.sign_code = 'A27'
  AND  q.question_ref = 'A27_Q07';

-- ────────────────────────────────────────────────────────────────────────────────
-- §8  sign_questions question + explanation — A27_Q08
--     V222 changed the question framing from JSON's conditional
--     "Can you drive at full speed at night when this sign warns..." to a generic
--     "Is it allowed to drive at full speed at night in an area marked by this sign?"
--     V222 also inserted "Large" / "Groot wild" / "Le grand gibier" in the
--     explanation where JSON uses plain "Wild animals"/"Wilde dieren"/"Les animaux
--     sauvages".
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE sign_questions q
JOIN   road_signs rs ON rs.id = q.sign_id
SET    q.question_nl    = 'Mag u ''s nachts aan de maximumsnelheid rijden wanneer dit bord waarschuwt voor overstekende wilde dieren?',
       q.question_en    = 'Can you drive at full speed at night when this sign warns of wild animals crossing?',
       q.question_fr    = 'Pouvez-vous rouler à pleine vitesse la nuit lorsque ce panneau avertit d''un passage d''animaux sauvages ?',
       q.question_ar    = 'هل يمكنك القيادة بأقصى سرعة ليلًا عند وجود علامة عبور الحيوانات البرية؟',
       q.explanation_nl = 'Wilde dieren zijn het actiefst bij schemering en ''s nachts. U moet uw snelheid altijd aanpassen aan de omstandigheden, en ''s nachts mag uw stopafstand nooit groter zijn dan uw zichtafstand.',
       q.explanation_en = 'Wild animals are most active at dusk and at night. You must always adapt your speed to the conditions, and at night your stopping distance must never exceed what you can see.',
       q.explanation_fr = 'Les animaux sauvages sont les plus actifs au crépuscule et la nuit. Vous devez toujours adapter votre vitesse aux conditions, et la nuit votre distance d''arrêt ne doit jamais dépasser votre distance de visibilité.',
       q.explanation_ar = 'تكون الحيوانات البرية أكثر نشاطًا عند الغسق وفي الليل. يجب عليك دائمًا تكييف سرعتك مع الظروف، ويجب ألا تتجاوز مسافة التوقف ليلاً مدى الرؤية المتاح لك.'
WHERE  rs.sign_code = 'A27'
  AND  q.question_ref = 'A27_Q08';

COMMIT;
