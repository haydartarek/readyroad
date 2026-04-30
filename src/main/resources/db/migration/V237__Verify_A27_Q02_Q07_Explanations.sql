-- ════════════════════════════════════════════════════════════════════════════════
-- V237__Verify_A27_Q02_Q07_Explanations.sql
--
-- AUDIT MIGRATION
-- Purpose : Compare A27_Q02–Q07 texts set by V222 against the canonical
--           signs_import JSON source of truth and repair all divergences.
--           V236 §23 already corrected A27_Q01; this migration completes
--           the audit called for in the V236 header note.
--
-- Audit findings (all four languages checked against
--   src/main/resources/data/signs_import/A27/questions.json):
--
--   A27_Q02  ✅  All 4 languages match JSON exactly — NO action required.
--   A27_Q03  ❌  V222 skipped this question entirely; explanation_* reset to JSON.
--   A27_Q04  ❌  V222 question_* uses obligatory "must / devez / يجب / moet u";
--                JSON uses advisory "should / devriez / ينبغي / doet u best".
--                explanation_* has the same shift plus "verminderen" vs "verlaagt".
--   A27_Q05  ❌  V222 explanation_* uses "wildlife / du gibier / wild" and adds
--                "large" qualifier absent from JSON; reset to exact JSON wording.
--   A27_Q06  ❌  V222 question_*, explanation_*, choice d.o.=1, and choice d.o.=3
--                (NL/FR/AR) all use "grand gibier / groot wild / الكبيرة" where
--                JSON uses "animaux sauvages / wilde dieren / البرية"; reset all.
--   A27_Q07  ❌  V222 question_* uses "verplicht / obligé / ملزمًا / required";
--                JSON uses "doet u … goed aan / devriez / ينبغي / should".
--                V222 explanation_* begins with a framing sentence absent from
--                JSON and ends with "only measure … enough time" vs JSON's
--                simpler "gives you more time to react"; reset both.
--   A27_Q08  ❌  Bonus: V222 also set Q08 with "Groot wild / grand gibier /
--                الكبيرة" vs JSON "Wilde dieren / animaux sauvages / البرية";
--                included here to complete the A27 full-question audit.
--
-- Affected tables : sign_questions, sign_choices
-- Sign            : A27 (Overstekend groot wild)
-- ════════════════════════════════════════════════════════════════════════════════

START TRANSACTION;

-- ────────────────────────────────────────────────────────────────────────────────
-- §1  A27_Q03 explanation_*
--     V222 skipped A27_Q03 entirely; this section brings all four explanations
--     to the JSON source of truth for the first time.
-- ────────────────────────────────────────────────────────────────────────────────
UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.explanation_nl = 'Wilde dieren zoals herten en everzwijnen zijn het gevaarlijkst bij schemering en ''s nachts. Ze steken de weg onverwacht over en zijn moeilijk zichtbaar in het duister.',
    q.explanation_en = 'Wild animals such as deer and wild boar are most dangerous at dusk and at night. They cross the road unexpectedly and are difficult to see in darkness.',
    q.explanation_fr = 'Les animaux sauvages comme les cerfs et les sangliers sont les plus dangereux au crépuscule et la nuit. Ils traversent la route de manière inattendue et sont difficiles à voir dans l''obscurité.',
    q.explanation_ar = 'الحيوانات البرية مثل الغزلان والخنازير البرية أشد خطورة عند الغسق وفي الليل. تعبر الطريق بصورة غير متوقعة وتصعب رؤيتها في الظلام.'
WHERE rs.sign_code = 'A27' AND q.question_ref = 'A27_Q03';

-- ────────────────────────────────────────────────────────────────────────────────
-- §2  A27_Q04 question_* + explanation_*
--     V222 used obligatory language ("must / devez / يجب / moet u verminderen").
--     JSON uses advisory language ("should / devriez / ينبغي / doet u best /
--     verlaagt u best").
-- ────────────────────────────────────────────────────────────────────────────────
UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.question_nl    = 'Wat doet u best wanneer u dit verkeersbord ziet, vooral bij schemering of ''s nachts?',
    q.question_en    = 'What should you do when you see this sign, especially at dusk or at night?',
    q.question_fr    = 'Que devriez-vous faire lorsque vous voyez ce panneau, surtout au crépuscule ou la nuit ?',
    q.question_ar    = 'ماذا ينبغي عليك فعله عند رؤية هذه العلامة المرورية، خاصة عند الغسق أو في الليل؟',
    q.explanation_nl = 'In deze zone verlaagt u best uw snelheid aanzienlijk en blijft u extra waakzaam. Het risico is het grootst bij schemering en ''s nachts, en grootlicht kan helpen om dieren tijdig op te merken wanneer het veilig is om het te gebruiken.',
    q.explanation_en = 'In this area, you should significantly reduce speed and remain highly alert. The risk is greatest at dusk and at night, and full beam can help you spot animals in time when it is safe to use it.',
    q.explanation_fr = 'Dans cette zone, vous devriez réduire nettement votre vitesse et rester très vigilant. Le risque est maximal au crépuscule et la nuit, et les pleins phares peuvent aider à repérer les animaux à temps lorsqu''il est sûr de les utiliser.',
    q.explanation_ar = 'في هذه المنطقة ينبغي عليك تخفيض السرعة بشكل ملحوظ والحفاظ على درجة عالية من الانتباه. يزداد الخطر عند الغسق وفي الليل، وقد تساعد الأضواء العالية على رؤية الحيوانات في الوقت المناسب عندما يكون استخدامها مسموحًا.'
WHERE rs.sign_code = 'A27' AND q.question_ref = 'A27_Q04';

-- ────────────────────────────────────────────────────────────────────────────────
-- §3  A27_Q05 explanation_*
--     V222 used "wildlife / du gibier / wild" (collective) and added "large"
--     qualifier ("groot / grand / كبير") absent from JSON.
--     JSON uses "a wild animal / un animal sauvage / een wild dier" and
--     "injured wild animal / animal sauvage blessé / gewond wild dier".
-- ────────────────────────────────────────────────────────────────────────────────
UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.explanation_nl = 'Na een aanrijding met een wild dier moet u stoppen, de weg beveiligen en de politie en eventueel de lokale dienst voor wildschade verwittigen. Verplaats een gewond wild dier nooit zelf, want het kan gevaarlijk reageren.',
    q.explanation_en = 'After a collision with a wild animal, you must stop, secure the road and notify the police and the local wildlife damage authority. Never move an injured wild animal yourself, because it may react dangerously.',
    q.explanation_fr = 'Après une collision avec un animal sauvage, vous devez vous arrêter, sécuriser la route et avertir la police ainsi que l''instance locale compétente pour les dégâts causés par la faune. Ne déplacez jamais vous-même un animal sauvage blessé, car il peut réagir dangereusement.',
    q.explanation_ar = 'بعد الاصطدام بحيوان بري، يجب التوقف وتأمين الطريق وإبلاغ الشرطة والجهة المحلية المختصة بأضرار الحياة البرية. لا تحاول أبدًا نقل الحيوان البري المصاب بنفسك، لأنه قد يتصرف بشكل خطير.'
WHERE rs.sign_code = 'A27' AND q.question_ref = 'A27_Q05';

-- ────────────────────────────────────────────────────────────────────────────────
-- §4  A27_Q06 question_* + explanation_*
--     V222 used "grand gibier / groot wild / الكبيرة" terminology throughout;
--     JSON consistently uses "animaux sauvages / wilde dieren / البرية".
--     V222 question_en had "wild animals" (plural); JSON has "wild animal"
--     (singular) in the possessive form of the sign name.
-- ────────────────────────────────────────────────────────────────────────────────
UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.question_nl    = 'Wat is het verschil tussen een waarschuwingsbord voor overstekende wilde dieren en een waarschuwingsbord voor overstekend vee?',
    q.question_en    = 'What is the difference between a wild animal crossing sign and a livestock crossing sign?',
    q.question_fr    = 'Quelle est la différence entre un panneau de passage d''animaux sauvages et un panneau de passage de bétail ?',
    q.question_ar    = 'ما الفرق بين علامة عبور الحيوانات البرية وعلامة عبور الماشية؟',
    q.explanation_nl = 'Een waarschuwingsbord voor overstekende wilde dieren heeft betrekking op dieren die in de natuur leven en onvoorspelbaar zijn. Een waarschuwingsbord voor overstekend vee gaat over gedomesticeerde landbouwdieren die meestal onder begeleiding de weg oversteken.',
    q.explanation_en = 'A wild animal crossing sign concerns animals living in the wild that may appear unexpectedly. A livestock crossing sign concerns farm animals that are usually guided across the road by a handler.',
    q.explanation_fr = 'Le panneau de passage d''animaux sauvages concerne des animaux vivant à l''état sauvage, donc imprévisibles. Le panneau de passage de bétail concerne des animaux d''élevage domestiqués, généralement conduits à travers la route par un accompagnateur.',
    q.explanation_ar = 'علامة عبور الحيوانات البرية تتعلق بحيوانات تعيش في الطبيعة وقد تظهر بشكل غير متوقع. أما علامة عبور الماشية فتتعلق بحيوانات مزرعة مستأنسة تُقاد عادة عبر الطريق من قبل شخص مرافق.'
WHERE rs.sign_code = 'A27' AND q.question_ref = 'A27_Q06';

-- ────────────────────────────────────────────────────────────────────────────────
-- §5  A27_Q06 choice display_order=1 (correct answer) text_*
--     V222 phrased as "warns of untamed animals such as deer or wild boar …
--     guided by a handler/attendant".
--     JSON uses a colon-separated contrast structure:
--     "Wild animals crossing: animals living in the wild that may appear suddenly;
--      livestock crossing: farm animals such as cows and sheep, usually with a handler".
-- ────────────────────────────────────────────────────────────────────────────────
UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.text_nl = 'Overstekende wilde dieren: dieren die in de natuur leven en plots kunnen opduiken; overstekend vee: landbouwdieren zoals koeien en schapen, meestal met een begeleider',
    c.text_en = 'Wild animals crossing: animals living in the wild that may appear suddenly; livestock crossing: farm animals such as cows and sheep, usually with a handler',
    c.text_fr = 'Passage d''animaux sauvages : des animaux vivant à l''état sauvage peuvent surgir soudainement ; passage de bétail : des animaux de ferme comme les vaches et les moutons, généralement accompagnés d''un berger',
    c.text_ar = 'عبور الحيوانات البرية: حيوانات تعيش في الطبيعة وقد تظهر فجأة، وعبور الماشية: حيوانات مزرعة مثل الأبقار والأغنام، غالبًا تكون مع راعٍ'
WHERE rs.sign_code = 'A27' AND q.question_ref = 'A27_Q06' AND c.display_order = 1;

-- ────────────────────────────────────────────────────────────────────────────────
-- §6  A27_Q06 choice display_order=3 (incorrect answer) text_* NL / FR / AR
--     V222 NL : "groot wild"       → JSON: "wilde dieren"
--     V222 FR : "grand gibier"     → JSON: "d'animaux sauvages"
--     V222 AR : "البرية الكبيرة"   → JSON: "البرية"
--     EN already matches JSON exactly; not touched.
-- ────────────────────────────────────────────────────────────────────────────────
UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.text_nl = 'Een bord voor overstekende wilde dieren verbiedt alle doorgang, terwijl een bord voor overstekend vee langzaam doorrijden toestaat',
    c.text_fr = 'Le panneau de passage d''animaux sauvages interdit totalement le passage, tandis que le panneau de passage de bétail autorise un passage lent',
    c.text_ar = 'علامة عبور الحيوانات البرية تمنع المرور تمامًا، بينما تسمح علامة عبور الماشية بالمرور البطيء'
WHERE rs.sign_code = 'A27' AND q.question_ref = 'A27_Q06' AND c.display_order = 3;

-- ────────────────────────────────────────────────────────────────────────────────
-- §7  A27_Q07 question_* + explanation_*
--     V222 question used obligatory language:
--       NL  "Bent u nog steeds verplicht uw snelheid te verminderen?"
--       EN  "Are you still required to reduce your speed?"
--       FR  "Êtes-vous encore obligé de réduire votre vitesse ?"
--       AR  "هل ما زلت ملزمًا بتخفيف سرعتك؟"
--     JSON uses advisory language:
--       NL  "Doet u er nog steeds goed aan uw snelheid te verminderen?"
--       EN  "Should you still reduce your speed?"
--       FR  "Devriez-vous tout de même réduire votre vitesse ?"
--       AR  "هل ينبغي عليك رغم ذلك تخفيض سرعتك؟"
--     V222 explanation began "Deze waarschuwing blijft… / This warning remains…";
--     JSON begins "Ja. / Yes. / Oui. / نعم." and ends with the simpler
--     "gives you more time to react" instead of V222's "only measure … enough time".
-- ────────────────────────────────────────────────────────────────────────────────
UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.question_nl    = 'U passeert dit verkeersbord maar ziet geen dieren. Doet u er nog steeds goed aan uw snelheid te verminderen?',
    q.question_en    = 'You pass this sign but see no animals. Should you still reduce your speed?',
    q.question_fr    = 'Vous passez ce panneau mais ne voyez aucun animal. Devriez-vous tout de même réduire votre vitesse ?',
    q.question_ar    = 'تمر بهذه العلامة المرورية لكن لا ترى أي حيوانات. هل ينبغي عليك رغم ذلك تخفيض سرعتك؟',
    q.explanation_nl = 'Ja. Dieren kunnen zich in deze zone verschuilen in de begroeiing naast de weg en op elk moment plots oversteken. Een lagere snelheid geeft u meer tijd om te reageren.',
    q.explanation_en = 'Yes. Animals may appear suddenly at any time in this area, even if none are visible at the moment. Reducing speed gives you more time to react.',
    q.explanation_fr = 'Oui. Des animaux peuvent se cacher dans la végétation au bord de la route et surgir à tout moment dans cette zone. Réduire la vitesse vous laisse plus de temps pour réagir.',
    q.explanation_ar = 'نعم. قد تختبئ الحيوانات في الغطاء النباتي على جانب الطريق وتندفع إلى الطريق في أي لحظة داخل هذه المنطقة. تخفيض السرعة يمنحك وقتًا أكبر لرد الفعل.'
WHERE rs.sign_code = 'A27' AND q.question_ref = 'A27_Q07';

-- ────────────────────────────────────────────────────────────────────────────────
-- §8  A27_Q08 question_* + explanation_*
--     Bonus section: Q08 was outside the stated Q02–Q07 audit scope but V222 set
--     it with the same "Groot wild / grand gibier / البرية الكبيرة" terminology
--     that diverges from JSON "Wilde dieren / animaux sauvages / البرية".
--     Including here to complete the full A27 audit.
-- ────────────────────────────────────────────────────────────────────────────────
UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.question_nl    = 'Mag u ''s nachts aan de maximumsnelheid rijden wanneer dit bord waarschuwt voor overstekende wilde dieren?',
    q.question_en    = 'Can you drive at full speed at night when this sign warns of wild animals crossing?',
    q.question_fr    = 'Pouvez-vous rouler à pleine vitesse la nuit lorsque ce panneau avertit d''un passage d''animaux sauvages ?',
    q.question_ar    = 'هل يمكنك القيادة بأقصى سرعة ليلًا عند وجود علامة عبور الحيوانات البرية؟',
    q.explanation_nl = 'Wilde dieren zijn het actiefst bij schemering en ''s nachts. U moet uw snelheid altijd aanpassen aan de omstandigheden, en ''s nachts mag uw stopafstand nooit groter zijn dan uw zichtafstand.',
    q.explanation_en = 'Wild animals are most active at dusk and at night. You must always adapt your speed to the conditions, and at night your stopping distance must never exceed what you can see.',
    q.explanation_fr = 'Les animaux sauvages sont les plus actifs au crépuscule et la nuit. Vous devez toujours adapter votre vitesse aux conditions, et la nuit votre distance d''arrêt ne doit jamais dépasser votre distance de visibilité.',
    q.explanation_ar = 'تكون الحيوانات البرية أكثر نشاطًا عند الغسق وفي الليل. يجب عليك دائمًا تكييف سرعتك مع الظروف، ويجب ألا تتجاوز مسافة التوقف ليلاً مدى الرؤية المتاح لك.'
WHERE rs.sign_code = 'A27' AND q.question_ref = 'A27_Q08';

COMMIT;
