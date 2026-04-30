-- Deep polish B15b-B15e learner-facing priority-configuration content.
-- Keeps persisted road-sign metadata and question banks aligned with source JSON.

UPDATE road_signs
SET name_ar = CASE sign_code
        WHEN 'B15b' THEN 'الأولوية على الطريق الجانبي الأيسر (أعلى)'
        WHEN 'B15c' THEN 'الأولوية على الطريق الجانبي الأيسر (أسفل)'
        WHEN 'B15d' THEN 'الأولوية على الطريق الجانبي الأيمن (أعلى)'
        WHEN 'B15e' THEN 'الأولوية على الطريق الجانبي الأيمن (أسفل)'
    END,
    description_ar = 'توضح هذه العلامة أن لك الأولوية على الطريق الجانبي المبين في الرمز. يحدد شكل الخطوط مسار الطريق الرئيسي والطريق الجانبي الذي يجب على مستخدميه إعطاؤك الأولوية.'
WHERE sign_code IN ('B15b', 'B15c', 'B15d', 'B15e');

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET sq.question_ar = 'أي علامة مرورية تستخدم رمزًا لتوضيح شكل الطريق وتبيّن حق أولويتك على الطريق الجانبي؟',
    sq.explanation_nl = 'Dit type bord gebruikt symbolen om de wegconfiguratie te tonen. Een voorrangswegbord duidt de hoofdweg aan, terwijl een bord voorrang verlenen u verplicht voorrang te geven.',
    sq.explanation_en = 'This type of sign uses symbols to show the road configuration. A priority road sign marks the main priority road, while a give way sign requires you to yield.',
    sq.explanation_fr = 'Ce type de panneau utilise des symboles pour montrer la configuration de la route. Un panneau de route prioritaire signale la route principale, tandis qu''un panneau de cédez le passage vous oblige à laisser la priorité.',
    sq.explanation_ar = 'تستخدم هذه العلامة رمزًا يوضح شكل الطريق لتبيّن حق الأولوية على الطريق الجانبي المحدد. أما علامة طريق الأولوية فتعني أن الطريق الذي تسلكه هو طريق أولوية بشكل عام، وعلامة إعطاء الأولوية تلزمك بالتخلي عن الأولوية.'
WHERE rs.sign_code IN ('B15b', 'B15c', 'B15d', 'B15e')
  AND sq.question_ref IN ('B15b_Q02', 'B15c_Q02', 'B15d_Q02', 'B15e_Q02');

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET sc.text_nl = 'Voorrang op kruisende zijweg',
    sc.text_en = CASE WHEN rs.sign_code = 'B15e' THEN 'Priority over crossing side road' ELSE 'Priority over intersecting side road' END,
    sc.text_fr = 'Priorité sur la route latérale de croisement',
    sc.text_ar = 'أولوية على طريق جانبي متقاطع',
    sc.is_correct = 1
WHERE rs.sign_code IN ('B15b', 'B15c', 'B15d', 'B15e')
  AND sq.question_ref IN ('B15b_Q02', 'B15c_Q02', 'B15d_Q02', 'B15e_Q02')
  AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET sc.text_nl = 'Voorrangsweg',
    sc.text_en = 'Priority road sign',
    sc.text_fr = 'Panneau de route prioritaire',
    sc.text_ar = 'طريق أولوية',
    sc.is_correct = 0
WHERE rs.sign_code IN ('B15b', 'B15c', 'B15d', 'B15e')
  AND sq.question_ref IN ('B15b_Q02', 'B15c_Q02', 'B15d_Q02', 'B15e_Q02')
  AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET sc.text_nl = 'Voorrang verlenen',
    sc.text_en = 'Give way sign',
    sc.text_fr = 'Panneau de cédez le passage',
    sc.text_ar = 'إعطاء الأولوية',
    sc.is_correct = 0
WHERE rs.sign_code IN ('B15b', 'B15c', 'B15d', 'B15e')
  AND sq.question_ref IN ('B15b_Q02', 'B15c_Q02', 'B15d_Q02', 'B15e_Q02')
  AND sc.display_order = 3;

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET sq.question_ar = CASE
        WHEN rs.sign_code = 'B15b' THEN 'هذه العلامة المرورية على طريقك. ما معنى الرمز الموجود عليها؟'
        ELSE 'ما الذي يُمثّله الخط السميك في رمز هذه العلامة المرورية؟'
    END,
    sq.explanation_ar = 'في جميع هذه العلامات يُمثّل الخط السميك دائمًا طريقك، أي طريق الأولوية. أما الخط الرفيع فيمثّل الطريق الجانبي الذي يجب على سائقيه إعطاؤك الأولوية.'
WHERE rs.sign_code IN ('B15b', 'B15c', 'B15d', 'B15e')
  AND sq.question_ref IN ('B15b_Q06', 'B15c_Q06', 'B15d_Q06', 'B15e_Q06');

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET sc.text_ar = CASE
        WHEN rs.sign_code = 'B15b' THEN 'الخط السميك في الرمز يُمثّل طريقك، والخط الرفيع يُمثّل الطريق الجانبي الذي يجب أن يعطيك الأولوية'
        ELSE 'طريقك الذي تتمتع فيه بحق الأولوية'
    END,
    sc.is_correct = 1
WHERE rs.sign_code IN ('B15b', 'B15c', 'B15d', 'B15e')
  AND sq.question_ref IN ('B15b_Q06', 'B15c_Q06', 'B15d_Q06', 'B15e_Q06')
  AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET sc.text_ar = CASE
        WHEN rs.sign_code = 'B15b' THEN 'الخط الرفيع يُمثّل طريقك، ويجب أن تعطي الأولوية للخط السميك'
        ELSE 'الطريق الجانبي الذي يجب أن تعطيه الأولوية'
    END,
    sc.is_correct = 0
WHERE rs.sign_code IN ('B15b', 'B15c', 'B15d', 'B15e')
  AND sq.question_ref IN ('B15b_Q06', 'B15c_Q06', 'B15d_Q06', 'B15e_Q06')
  AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET sc.text_ar = CASE
        WHEN rs.sign_code = 'B15b' THEN 'الرمز عنصر زخرفي فقط ولا يحمل أي معنى مروري'
        ELSE 'طريق لا يخضع لأي تنظيم للأولوية'
    END,
    sc.is_correct = 0
WHERE rs.sign_code IN ('B15b', 'B15c', 'B15d', 'B15e')
  AND sq.question_ref IN ('B15b_Q06', 'B15c_Q06', 'B15d_Q06', 'B15e_Q06')
  AND sc.display_order = 3;

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET sq.question_ar = CASE
        WHEN rs.sign_code = 'B15b' THEN 'هذه العلامة المرورية تالفة ويصعب قراءتها. هل يجوز لسائق الطريق الجانبي أن يفترض أن لديه الأولوية؟'
        ELSE 'هل ينطبق حق الأولوية الذي تمنحه هذه العلامة المرورية أيضًا على المشاة الذين يعبرون الطريق الجانبي؟'
    END,
    sq.explanation_ar = CASE
        WHEN rs.sign_code = 'B15b' THEN 'العلامة المرورية التالفة أو غير الواضحة لا تُلغي القاعدة القانونية. عند الشك يجب على السائق اختيار التصرف الأكثر أمانًا: التوقف وإعطاء الأولوية.'
        ELSE 'تنظم علامات B15 الأولوية بين المركبات. أما المشاة على ممر مشاة نظامي فلهم الأولوية دائمًا على المركبات.'
    END
WHERE rs.sign_code IN ('B15b', 'B15c', 'B15d', 'B15e')
  AND sq.question_ref IN ('B15b_Q08', 'B15c_Q08', 'B15d_Q08', 'B15e_Q08');

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET sc.text_ar = CASE
        WHEN rs.sign_code = 'B15b' THEN 'لا، العلامة المرورية التالفة لا تُلغي الالتزام القانوني؛ عند الشك يجب على السائق التوقف وإعطاء الأولوية'
        ELSE 'لا، هذه العلامة المرورية تنظم الأولوية بين المركبات فقط؛ أما المشاة على ممر المشاة فلهم الأولوية دائمًا'
    END,
    sc.is_correct = 1
WHERE rs.sign_code IN ('B15b', 'B15c', 'B15d', 'B15e')
  AND sq.question_ref IN ('B15b_Q08', 'B15c_Q08', 'B15d_Q08', 'B15e_Q08')
  AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET sc.text_ar = CASE
        WHEN rs.sign_code = 'B15b' THEN 'نعم، إذا كانت العلامة غير مقروءة تسري قاعدة أولوية اليمين العادية'
        ELSE 'نعم، المشاة الذين يعبرون الطريق الجانبي يجب أيضًا أن يعطوك الأولوية'
    END,
    sc.is_correct = 0
WHERE rs.sign_code IN ('B15b', 'B15c', 'B15d', 'B15e')
  AND sq.question_ref IN ('B15b_Q08', 'B15c_Q08', 'B15d_Q08', 'B15e_Q08')
  AND sc.display_order = 2;

-- Supplementary B15b alignment with the cleaned source JSON.
-- These updates remove raw sign-code mentions from learner-facing text while
-- preserving the already-correct B15c/B15d/B15e content above.

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET sq.explanation_ar = 'تُشير هذه العلامة المرورية إلى أن لديك الأولوية على المركبة القادمة من الطريق الجانبي المتقاطع. ويُظهر الرمز شكل الطريق بدقة.',
    sq.explanation_en = 'This sign indicates that you have priority over the vehicle coming from the intersecting side road. The symbol shows the road layout precisely.',
    sq.explanation_nl = 'Dit bord geeft aan dat u voorrang heeft op het voertuig dat van de kruisende zijweg komt. Het symbool toont de wegconfiguratie nauwkeurig.',
    sq.explanation_fr = 'Ce panneau indique que vous avez la priorité sur le véhicule venant de la route latérale de croisement. Le symbole montre précisément la configuration de la route.'
WHERE rs.sign_code = 'B15b'
  AND sq.question_ref = 'B15b_Q01';

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET sq.question_en = 'What hazard does this traffic sign warn you about?',
    sq.question_nl = 'Voor welk gevaar waarschuwt dit verkeersbord u?',
    sq.question_fr = 'À quel danger ce panneau de signalisation vous avertit-il ?',
    sq.explanation_ar = 'تنبّهك هذه العلامة المرورية إلى وجود طريق جانبي متقاطع. ورغم أن لك الأولوية، يجب أن تبقى يقظًا لأن السائقين الآخرين قد يخطئون.',
    sq.explanation_en = 'This sign warns you about an intersecting side road. Even though you have priority, you must remain alert because other drivers can make mistakes.',
    sq.explanation_nl = 'Dit bord waarschuwt u voor een kruisende zijweg. Ook al heeft u voorrang, u moet alert blijven omdat andere bestuurders fouten kunnen maken.',
    sq.explanation_fr = 'Ce panneau vous avertit d''une route latérale de croisement. Même si vous avez la priorité, vous devez rester vigilant car d''autres conducteurs peuvent commettre des erreurs.'
WHERE rs.sign_code = 'B15b'
  AND sq.question_ref = 'B15b_Q03';

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET sq.question_ar = 'ماذا يجب على سائق الطريق الجانبي فعله عند وجود هذه العلامة المرورية؟',
    sq.question_en = 'What must the driver coming from the side road do when this sign is present?',
    sq.question_nl = 'Wat moet de bestuurder van de zijweg doen wanneer dit bord aanwezig is?',
    sq.question_fr = 'Que doit faire le conducteur venant de la route latérale lorsque ce panneau est présent ?',
    sq.explanation_ar = 'يجب على السائقين القادمين من الطريق الجانبي المبين في الرمز الانتظار وإعطاء الأولوية لحركة المرور على الطريق الرئيسي.',
    sq.explanation_en = 'Drivers coming from the side road shown in the symbol must wait and give way to traffic on the main road.',
    sq.explanation_nl = 'Bestuurders die van de zijweg komen die in het symbool is aangeduid, moeten wachten en voorrang verlenen aan het verkeer op de hoofdweg.',
    sq.explanation_fr = 'Les conducteurs venant de la route latérale indiquée dans le symbole doivent attendre et céder le passage à la circulation sur la route principale.'
WHERE rs.sign_code = 'B15b'
  AND sq.question_ref = 'B15b_Q04';

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET sq.question_ar = 'أنت تسير على الطريق الرئيسي مع هذه العلامة المرورية. مركبة من الطريق الجانبي تدخل طريقك رغم ذلك. ماذا تفعل؟',
    sq.question_en = 'You are on the main road with this sign. A vehicle from the side road enters your road anyway. What do you do?',
    sq.question_nl = 'U rijdt op de hoofdweg met dit bord. Een voertuig van de zijweg rijdt toch uw weg op. Wat doet u?',
    sq.question_fr = 'Vous roulez sur la route principale avec ce panneau. Un véhicule venant de la route latérale s''engage tout de même sur votre route. Que faites-vous ?'
WHERE rs.sign_code = 'B15b'
  AND sq.question_ref = 'B15b_Q05';

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET sq.question_ar = 'هذه العلامة المرورية على طريقك. ما معنى الرمز الموجود عليها؟',
    sq.question_en = 'This sign is on your road. What does the symbol on it mean?',
    sq.question_nl = 'Dit bord staat op uw weg. Wat betekent het symbool erop?',
    sq.question_fr = 'Ce panneau se trouve sur votre route. Que signifie le symbole qui y figure ?',
    sq.explanation_en = 'The symbol on this family of signs shows your road as the thick line. The thin line is the side road whose drivers must give way to you.',
    sq.explanation_nl = 'Het symbool op deze borden toont uw weg als de brede lijn. De smalle lijn is de zijweg waarvan de bestuurders u voorrang moeten verlenen.',
    sq.explanation_fr = 'Le symbole sur cette famille de panneaux montre votre route comme la ligne épaisse. La ligne fine représente la route latérale dont les conducteurs doivent vous céder le passage.'
WHERE rs.sign_code = 'B15b'
  AND sq.question_ref = 'B15b_Q06';

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET sq.question_ar = 'أنت تسير على الطريق الرئيسي مع هذه العلامة المرورية. هل يجوز لك رفع سرعتك فوق الحد المسموح به للمطالبة بحق الأولوية؟',
    sq.question_en = 'You are on the main road with this sign. May you exceed the speed limit to claim your right of way?',
    sq.question_nl = 'U rijdt op de hoofdweg met dit bord. Mag u de snelheidslimiet overschrijden om uw voorrang op te eisen?',
    sq.question_fr = 'Vous roulez sur la route principale avec ce panneau. Pouvez-vous dépasser la limite de vitesse pour revendiquer votre priorité ?'
WHERE rs.sign_code = 'B15b'
  AND sq.question_ref = 'B15b_Q07';

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET sq.question_ar = 'هذه العلامة المرورية تالفة ويصعب قراءتها. هل يجوز لسائق الطريق الجانبي أن يفترض أن لديه الأولوية؟',
    sq.question_en = 'This sign is damaged and difficult to read. May the driver from the side road assume they have priority?',
    sq.question_nl = 'Dit bord is beschadigd en moeilijk leesbaar. Mag de bestuurder van de zijweg aannemen dat hij voorrang heeft?',
    sq.question_fr = 'Ce panneau est endommagé et difficile à lire. Le conducteur venant de la route latérale peut-il supposer qu''il a la priorité ?'
WHERE rs.sign_code = 'B15b'
  AND sq.question_ref = 'B15b_Q08';
