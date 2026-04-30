UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET q.explanation_ar = 'تحذر هذه العلامة المرورية من تعاقب منعطفات خطيرة، أولها إلى اليسار. خفف السرعة على امتداد جميع المنعطفات.'
WHERE rs.sign_code = 'A1c'
  AND q.question_ref = 'A1c_Q01';

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET q.explanation_ar = 'تشير هذه العلامة المرورية إلى تعاقب منعطفات خطيرة، أولها إلى اليسار. بعد المنعطف الأول قد تظهر منعطفات خطيرة أخرى، لذلك يجب تعديل طريقة القيادة على امتداد جميع المنعطفات.'
WHERE rs.sign_code = 'A1c'
  AND q.question_ref = 'A1c_Q03';

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET q.explanation_ar = 'تشير هذه العلامة المرورية إلى تعاقب منعطفات متعددة. يجب ضبط سرعتك على امتداد جميع المنعطفات، وليس فقط المنعطف الأول.'
WHERE rs.sign_code = 'A1c'
  AND q.question_ref = 'A1c_Q04';

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET q.question_ar = 'أنت تقود بسرعة 90 كم/ساعة على طريق مبلل، وأمامك تعاقب منعطفات أولها إلى اليسار. ترى هذه العلامة المرورية. ماذا تفعل أولاً؟',
    q.explanation_ar = 'على طريق مبلل مع تعاقب منعطفات، خفف السرعة بشكل ملحوظ قبل المنعطف الأول. لا تزد السرعة بين المنعطفات وابقَ دائمًا في مسارك.'
WHERE rs.sign_code = 'A1c'
  AND q.question_ref = 'A1c_Q05';

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET q.question_ar = 'ما هي المسافة التي توضع عندها هذه العلامة التحذيرية خارج المنطقة السكنية؟'
WHERE rs.sign_code = 'A1c'
  AND q.question_ref = 'A1c_Q06';

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET q.question_ar = 'هل يجب الحفاظ على السرعة المنخفضة على امتداد جميع المنعطفات المعلنة، وليس فقط المنعطف الأول؟',
    q.question_nl = 'Moet u uw snelheid verlagen voor alle opeenvolgende bochten die dit verkeersbord aankondigt, niet alleen voor de eerste?',
    q.question_fr = 'Devez-vous maintenir une vitesse réduite sur l''ensemble des virages annoncés, pas seulement pour le premier ?',
    q.explanation_ar = 'تشير هذه العلامة المرورية إلى تعاقب منعطفات خطيرة. يجب ضبط سرعتك على امتداد جميع المنعطفات، وليس فقط المنعطف الأول.'
WHERE rs.sign_code = 'A1c'
  AND q.question_ref = 'A1c_Q08';

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET q.explanation_ar = 'تحذر هذه العلامة المرورية من تعاقب منعطفات خطيرة، أولها إلى اليمين. خفف السرعة على امتداد جميع المنعطفات.'
WHERE rs.sign_code = 'A1d'
  AND q.question_ref = 'A1d_Q01';

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET q.explanation_ar = 'تشير هذه العلامة المرورية إلى تعاقب منعطفات خطيرة، أولها إلى اليمين. بعد المنعطف الأول قد تظهر منعطفات خطيرة أخرى، لذلك يجب أن تقود بيقظة أكبر على امتداد جميع المنعطفات.'
WHERE rs.sign_code = 'A1d'
  AND q.question_ref = 'A1d_Q03';

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET q.explanation_ar = 'تشير هذه العلامة المرورية إلى تعاقب منعطفات متعددة. يجب ضبط سرعتك على امتداد جميع المنعطفات والبقاء دائمًا في مسارك.'
WHERE rs.sign_code = 'A1d'
  AND q.question_ref = 'A1d_Q04';

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET q.question_ar = 'أنت تقود ليلًا بسرعة 90 كم/ساعة، وترى هذه العلامة المرورية التي تشير إلى تعاقب منعطفات أولها إلى اليمين. ماذا تفعل أولاً؟',
    q.explanation_ar = 'ليلًا، خفف السرعة قبل المنعطف الأول وواصل القيادة بسرعة منخفضة على امتداد جميع المنعطفات. التحرك نحو منتصف الطريق يزيد خطر التصادم مع المركبات القادمة.'
WHERE rs.sign_code = 'A1d'
  AND q.question_ref = 'A1d_Q05';

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET q.question_ar = 'ما هي المسافة التي توضع عندها هذه العلامة التحذيرية خارج المنطقة السكنية؟'
WHERE rs.sign_code = 'A1d'
  AND q.question_ref = 'A1d_Q06';

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET q.question_ar = 'هل يُسمح بالتجاوز مباشرة قبل تعاقب منعطفات خطيرة، أولها إلى اليمين؟',
    q.explanation_ar = 'يُحظر التجاوز مباشرة قبل تعاقب منعطفات خطيرة. لن تتمكن من العودة إلى مسارك بأمان قبل المنعطف الأول.'
WHERE rs.sign_code = 'A1d'
  AND q.question_ref = 'A1d_Q07';

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET c.text_ar = 'منعطف مزدوج خطير أو تعاقب منعطفات، الأول إلى اليسار',
    c.text_en = 'Dangerous double bend or succession of bends, first to the left',
    c.text_nl = 'Gevaarlijke dubbele bocht of opeenvolgende bochten, de eerste naar links',
    c.text_fr = 'Double virage dangereux ou succession de virages, le premier à gauche'
WHERE rs.sign_code = 'A1c'
  AND q.question_ref = 'A1c_Q01'
  AND c.display_order = 2;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET c.text_ar = 'منعطف مزدوج خطير أو تعاقب منعطفات، الأول إلى اليمين',
    c.text_en = 'Dangerous double bend or succession of bends, first to the right',
    c.text_nl = 'Gevaarlijke dubbele bocht of opeenvolgende bochten, de eerste naar rechts',
    c.text_fr = 'Double virage dangereux ou succession de virages, le premier à droite'
WHERE rs.sign_code = 'A1c'
  AND q.question_ref = 'A1c_Q01'
  AND c.display_order = 3;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET c.text_ar = 'تعاقب منعطفات خطيرة، أولها إلى اليسار',
    c.text_en = 'A succession of dangerous bends where the first is to the left',
    c.text_nl = 'Opeenvolgende gevaarlijke bochten waarvan de eerste naar links gaat',
    c.text_fr = 'Une succession de virages dangereux dont le premier est à gauche'
WHERE rs.sign_code = 'A1c'
  AND q.question_ref = 'A1c_Q03'
  AND c.display_order = 1;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET c.text_ar = 'تقليل السرعة والحفاظ على قيادة متزنة على امتداد جميع المنعطفات',
    c.text_nl = 'Snelheid verminderen en aangepast blijven rijden tijdens alle opeenvolgende bochten',
    c.text_fr = 'Réduire la vitesse et garder une conduite adaptée tout au long de la succession de virages'
WHERE rs.sign_code = 'A1c'
  AND q.question_ref = 'A1c_Q04'
  AND c.display_order = 3;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET c.text_ar = 'منعطف مزدوج خطير أو تعاقب منعطفات، الأول إلى اليسار',
    c.text_en = 'Dangerous double bend or succession of bends, first to the left',
    c.text_nl = 'Gevaarlijke dubbele bocht of opeenvolgende bochten, de eerste naar links',
    c.text_fr = 'Double virage dangereux ou succession de virages, le premier à gauche'
WHERE rs.sign_code = 'A1d'
  AND q.question_ref = 'A1d_Q01'
  AND c.display_order = 2;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET c.text_ar = 'منعطف مزدوج خطير أو تعاقب منعطفات، الأول إلى اليمين',
    c.text_en = 'Dangerous double bend or succession of bends, first to the right',
    c.text_nl = 'Gevaarlijke dubbele bocht of opeenvolgende bochten, de eerste naar rechts',
    c.text_fr = 'Double virage dangereux ou succession de virages, le premier à droite'
WHERE rs.sign_code = 'A1d'
  AND q.question_ref = 'A1d_Q01'
  AND c.display_order = 1;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET c.text_ar = 'تعاقب منعطفات خطيرة، أولها إلى اليمين',
    c.text_en = 'A succession of dangerous bends where the first is to the right',
    c.text_nl = 'Opeenvolgende gevaarlijke bochten waarvan de eerste naar rechts gaat',
    c.text_fr = 'Une succession de virages dangereux dont le premier est à droite'
WHERE rs.sign_code = 'A1d'
  AND q.question_ref = 'A1d_Q03'
  AND c.display_order = 3;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET c.text_ar = 'تقليل السرعة والبقاء في مسارك على امتداد جميع المنعطفات',
    c.text_nl = 'Snelheid verminderen en in uw rijbaan blijven tijdens alle opeenvolgende bochten',
    c.text_fr = 'Réduire la vitesse et rester dans votre voie tout au long de la succession de virages'
WHERE rs.sign_code = 'A1d'
  AND q.question_ref = 'A1d_Q04'
  AND c.display_order = 3;
