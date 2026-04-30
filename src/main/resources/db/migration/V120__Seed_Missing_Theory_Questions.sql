-- V120: Seed Missing Theory Questions
-- Adds 19 real Belgian driving theory questions to reach required 20/20/10 distribution
-- Current state before this migration: 18 EASY, 10 MEDIUM, 3 HARD
-- Target state after this migration:   20 EASY, 20 MEDIUM, 10 HARD
--
-- New questions:  +2 EASY (IDs 124-125)
--                 +10 MEDIUM (IDs 126-135)
--                 +7 HARD (IDs 136-142)
-- New options:    IDs 593-649

-- ============================================================
-- EASY QUESTIONS (2 new: IDs 124, 125)
-- ============================================================

-- Q124 | EASY | Category B (Priority Signs)
INSERT INTO quiz_questions
  (id, category_id, question_type, difficulty_level, is_active, status, published_at,
   question_en, question_ar, question_nl, question_fr,
   explanation_en, explanation_ar, explanation_nl, explanation_fr,
   typical_error_type, context_specific, requires_sign_image)
VALUES
  (124, 2, 'MULTIPLE_CHOICE', 'EASY', 1, 'PUBLISHED', NOW(),
   'What does a yellow diamond-shaped sign indicate to drivers in Belgium?',
   'ماذا يعني العلامة المعينية الصفراء على الطرق البلجيكية؟',
   'Wat geeft een geel ruitvormig bord aan voor bestuurders in België?',
   'Que signifie un panneau en forme de losange jaune pour les conducteurs en Belgique?',
   'A yellow diamond sign (B1) means you are on a priority road. Other traffic must yield to you at intersections unless signs indicate otherwise.',
   'العلامة المعينية الصفراء (B1) تعني أنك على طريق ذي أولوية. يجب على المركبات الأخرى إعطاءك الأولوية عند التقاطعات.',
   'Een geel ruitvormig bord (B1) betekent dat u op een voorrangsweg rijdt. Ander verkeer moet voorrang aan u verlenen bij kruispunten.',
   'Un panneau losange jaune (B1) signifie que vous êtes sur une route prioritaire. Les autres véhicules doivent vous céder le passage aux intersections.',
   'SIGN_CONFUSION', 1, 0);

INSERT INTO quiz_answer_options
  (id, question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order)
VALUES
  (593, 124, 'You are driving on a priority road',
   'أنت تسير على طريق ذي أولوية',
   'U rijdt op een voorrangsweg',
   'Vous roulez sur une route prioritaire', 1, 1),
  (594, 124, 'You must give way to all oncoming traffic',
   'يجب عليك إعطاء الأولوية لجميع المركبات القادمة',
   'U moet voorrang verlenen aan al het tegemoetkomend verkeer',
   'Vous devez céder le passage à tout le trafic venant en sens inverse', 0, 2),
  (595, 124, 'You are approaching a roundabout',
   'أنت تقترب من دوار',
   'U nadert een rotonde',
   'Vous approchez d''un rond-point', 0, 3);

-- Q125 | EASY | Category D (Mandatory Signs)
INSERT INTO quiz_questions
  (id, category_id, question_type, difficulty_level, is_active, status, published_at,
   question_en, question_ar, question_nl, question_fr,
   explanation_en, explanation_ar, explanation_nl, explanation_fr,
   typical_error_type, context_specific, requires_sign_image)
VALUES
  (125, 4, 'MULTIPLE_CHOICE', 'EASY', 1, 'PUBLISHED', NOW(),
   'What does a blue circular sign with a white upward arrow mean?',
   'ماذا يعني العلامة الدائرية الزرقاء ذات السهم الأبيض الموجه للأعلى؟',
   'Wat betekent een blauw rond bord met een witte pijl omhoog?',
   'Que signifie un panneau circulaire bleu avec une flèche blanche vers le haut?',
   'Blue circular signs with white arrows are mandatory direction signs. An upward arrow means you must continue straight ahead — no turning is permitted at that point.',
   'العلامات الدائرية الزرقاء ذات الأسهم البيضاء هي علامات اتجاه إلزامي. السهم الموجه للأعلى يعني يجب الاستمرار في السير مستقيماً دون انعطاف.',
   'Blauwe ronde borden met witte pijlen zijn verplichte richtingsborden. Een pijl omhoog betekent dat u rechtdoor moet rijden — afslaan is op dat punt niet toegestaan.',
   'Les panneaux circulaires bleus avec des flèches blanches sont des panneaux de direction obligatoire. Une flèche vers le haut signifie que vous devez continuer tout droit, sans tourner.',
   'SIGN_CONFUSION', 1, 0);

INSERT INTO quiz_answer_options
  (id, question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order)
VALUES
  (596, 125, 'You must continue straight ahead',
   'يجب عليك الاستمرار في السير مستقيماً',
   'U moet rechtdoor rijden',
   'Vous devez continuer tout droit', 1, 1),
  (597, 125, 'You may only enter with a permit',
   'يمكنك الدخول فقط بتصريح',
   'U mag alleen met een vergunning binnenrijden',
   'Vous ne pouvez entrer qu''avec une autorisation', 0, 2),
  (598, 125, 'One-way street ahead',
   'شارع ذو اتجاه واحد في الأمام',
   'Eenrichtingsstraat voor u',
   'Sens unique en avant', 0, 3);

-- ============================================================
-- MEDIUM QUESTIONS (10 new: IDs 126-135)
-- ============================================================

-- Q126 | MEDIUM | Category A (Danger Signs) — General hazard
INSERT INTO quiz_questions
  (id, category_id, question_type, difficulty_level, is_active, status, published_at,
   question_en, question_ar, question_nl, question_fr,
   explanation_en, explanation_ar, explanation_nl, explanation_fr,
   typical_error_type, context_specific, requires_sign_image)
VALUES
  (126, 1, 'MULTIPLE_CHOICE', 'MEDIUM', 1, 'PUBLISHED', NOW(),
   'What does a red-bordered triangular sign with an exclamation mark indicate?',
   'ماذا يعني العلامة المثلثة ذات الإطار الأحمر وعلامة التعجب؟',
   'Wat geeft een driehoekig bord met rode rand en uitroepteken aan?',
   'Que signifie un panneau triangulaire à bordure rouge avec un point d''exclamation?',
   'A triangle with an exclamation mark (A51) is a general danger sign. It warns of a hazard ahead that is not covered by a more specific sign. Reduce speed and proceed cautiously.',
   'المثلث مع علامة التعجب (A51) هو علامة خطر عام. يحذر من خطر في الأمام لا تغطيه علامة أكثر تحديداً. قلل السرعة وتقدم بحذر.',
   'Een driehoek met uitroepteken (A51) is een algemeen gevaarsbord. Het waarschuwt voor een gevaar vooruit dat niet door een specifiekere aanduiding wordt gedekt. Snelheid verminderen en voorzichtig rijden.',
   'Un triangle avec un point d''exclamation (A51) est un panneau de danger général. Il avertit d''un danger en avant non couvert par un panneau plus spécifique. Réduire la vitesse et procéder avec prudence.',
   'SIGN_CONFUSION', 1, 0);

INSERT INTO quiz_answer_options
  (id, question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order)
VALUES
  (599, 126, 'General danger or hazard ahead',
   'خطر عام في الطريق',
   'Algemeen gevaar of hindernis vooruit',
   'Danger général ou obstacle en avant', 1, 1),
  (600, 126, 'You must stop immediately',
   'يجب عليك التوقف فوراً',
   'U moet onmiddellijk stoppen',
   'Vous devez vous arrêter immédiatement', 0, 2),
  (601, 126, 'Speed limit zone begins',
   'تبدأ منطقة حد السرعة',
   'Zone met snelheidsbeperking begint',
   'Début d''une zone de limitation de vitesse', 0, 3);

-- Q127 | MEDIUM | Category A (Danger Signs) — Slippery road
INSERT INTO quiz_questions
  (id, category_id, question_type, difficulty_level, is_active, status, published_at,
   question_en, question_ar, question_nl, question_fr,
   explanation_en, explanation_ar, explanation_nl, explanation_fr,
   typical_error_type, context_specific, requires_sign_image)
VALUES
  (127, 1, 'MULTIPLE_CHOICE', 'MEDIUM', 1, 'PUBLISHED', NOW(),
   'What should you do when you see a slippery road warning sign?',
   'ماذا يجب أن تفعل عندما ترى علامة تحذير من الطريق الزلق؟',
   'Wat moet u doen wanneer u een waarschuwingsbord voor een glad wegdek ziet?',
   'Que devez-vous faire lorsque vous voyez un panneau d''avertissement de chaussée glissante?',
   'A slippery road sign requires you to reduce speed and increase following distance. Braking distances are much longer on slippery surfaces, so extra caution is essential.',
   'علامة الطريق الزلق تتطلب منك تقليل السرعة وزيادة مسافة الأمان. مسافات التوقف أطول كثيراً على الأسطح الزلقة لذا الحذر الإضافي ضروري.',
   'Een glad wegbord vereist dat u snelheid vermindert en rijafstand vergroot. Remafstanden zijn veel langer op gladde oppervlakken, dus extra voorzichtigheid is essentieel.',
   'Un panneau de chaussée glissante vous oblige à réduire la vitesse et augmenter la distance de sécurité. Les distances de freinage sont bien plus longues sur les surfaces glissantes.',
   'RULE_OVERGENERALIZATION', 1, 0);

INSERT INTO quiz_answer_options
  (id, question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order)
VALUES
  (602, 127, 'Reduce speed and increase following distance',
   'تقليل السرعة وزيادة مسافة الأمان',
   'Snelheid verminderen en rijafstand vergroten',
   'Réduire la vitesse et augmenter la distance de sécurité', 1, 1),
  (603, 127, 'Stop the vehicle and wait for the hazard to clear',
   'أوقف السيارة وانتظر حتى تختفي الخطورة',
   'Stop het voertuig en wacht tot het gevaar voorbij is',
   'Arrêter le véhicule et attendre que le danger disparaisse', 0, 2),
  (604, 127, 'Switch on your hazard warning lights only',
   'تشغيل أضواء الطوارئ فقط',
   'Alleen de gevarendriehoek inschakelen',
   'Activer uniquement les feux de détresse', 0, 3);

-- Q128 | MEDIUM | Category B (Priority Signs) — Give-way sign
INSERT INTO quiz_questions
  (id, category_id, question_type, difficulty_level, is_active, status, published_at,
   question_en, question_ar, question_nl, question_fr,
   explanation_en, explanation_ar, explanation_nl, explanation_fr,
   typical_error_type, context_specific, requires_sign_image)
VALUES
  (128, 2, 'MULTIPLE_CHOICE', 'MEDIUM', 1, 'PUBLISHED', NOW(),
   'What does an inverted triangle with a red border (give-way sign) require of you?',
   'ماذا تتطلب علامة المثلث المقلوب ذو الإطار الأحمر منك؟',
   'Wat vereist een omgekeerde driehoek met rode rand (voorrangsbord) van u?',
   'Qu''exige de vous un triangle inversé à bordure rouge (panneau cédez le passage)?',
   'The give-way sign (B1a/B17) means you must yield to all traffic on the intersecting road. You may proceed when it is safe, but you do not need to stop completely unless necessary.',
   'علامة إعطاء الأولوية (B1a/B17) تعني يجب إعطاء الأولوية لجميع المركبات على الطريق المتقاطع. يمكنك المتابعة عند الأمان ولكن لا تحتاج للتوقف الكامل إلا إذا لزم.',
   'Het voorrangsbord (B1a/B17) betekent dat u voorrang moet verlenen aan al het verkeer op de kruisende weg. U mag doorrijden als het veilig is, maar u hoeft niet volledig te stoppen tenzij nodig.',
   'Le panneau cédez le passage (B1a/B17) signifie que vous devez céder le passage à tout le trafic sur la route croisée. Vous pouvez avancer quand c''est sécuritaire, sans nécessairement vous arrêter.',
   'PRIORITY_MISUNDERSTANDING', 1, 0);

INSERT INTO quiz_answer_options
  (id, question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order)
VALUES
  (605, 128, 'Give way to all traffic on the intersecting road',
   'إعطاء الأولوية لجميع المركبات على الطريق المتقاطع',
   'Voorrang verlenen aan al het verkeer op de kruisende weg',
   'Céder le passage à tout le trafic sur la route croisée', 1, 1),
  (606, 128, 'Stop completely before entering the intersection',
   'التوقف التام قبل الدخول إلى التقاطع',
   'Volledig stoppen voor het oprijden van het kruispunt',
   'S''arrêter complètement avant d''entrer dans l''intersection', 0, 2),
  (607, 128, 'Give way only to trams and buses',
   'إعطاء الأولوية للترام والحافلات فقط',
   'Alleen voorrang verlenen aan trams en bussen',
   'Céder le passage uniquement aux trams et aux bus', 0, 3);

-- Q129 | MEDIUM | Category B (Priority Signs) — Priority to the right
INSERT INTO quiz_questions
  (id, category_id, question_type, difficulty_level, is_active, status, published_at,
   question_en, question_ar, question_nl, question_fr,
   explanation_en, explanation_ar, explanation_nl, explanation_fr,
   typical_error_type, context_specific, requires_sign_image)
VALUES
  (129, 2, 'MULTIPLE_CHOICE', 'MEDIUM', 1, 'PUBLISHED', NOW(),
   'At a Belgian intersection with no traffic signs, which vehicle has priority?',
   'عند تقاطع بلجيكي بدون علامات مرورية، أي مركبة لها حق الأولوية؟',
   'Op een Belgisch kruispunt zonder verkeersborden, welk voertuig heeft voorrang?',
   'À une intersection belge sans panneaux de signalisation, quel véhicule a la priorité?',
   'In Belgium, the default rule is priority to the right (priorité à droite). At intersections without signs, any vehicle approaching from the right has priority over you.',
   'في بلجيكا القاعدة الافتراضية هي الأولوية لليمين. عند التقاطعات بدون علامات، أي مركبة تأتي من اليمين لها الأولوية عليك.',
   'In België is de standaardregel voorrang van rechts. Op kruispunten zonder borden heeft elk voertuig dat van rechts nadert voorrang op u.',
   'En Belgique, la règle par défaut est la priorité à droite. Aux intersections sans panneaux, tout véhicule venant de droite a la priorité sur vous.',
   'PRIORITY_MISUNDERSTANDING', 1, 0);

INSERT INTO quiz_answer_options
  (id, question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order)
VALUES
  (608, 129, 'The vehicle coming from the right',
   'المركبة القادمة من اليمين',
   'Het voertuig dat van rechts komt',
   'Le véhicule venant de droite', 1, 1),
  (609, 129, 'The vehicle travelling at the higher speed',
   'المركبة التي تسير بسرعة أعلى',
   'Het voertuig dat met hogere snelheid rijdt',
   'Le véhicule circulant à la vitesse la plus élevée', 0, 2),
  (610, 129, 'The vehicle on the wider road',
   'المركبة على الطريق الأوسع',
   'Het voertuig op de bredere weg',
   'Le véhicule sur la route la plus large', 0, 3);

-- Q130 | MEDIUM | Category C (Prohibition Signs) — No entry
INSERT INTO quiz_questions
  (id, category_id, question_type, difficulty_level, is_active, status, published_at,
   question_en, question_ar, question_nl, question_fr,
   explanation_en, explanation_ar, explanation_nl, explanation_fr,
   typical_error_type, context_specific, requires_sign_image)
VALUES
  (130, 3, 'MULTIPLE_CHOICE', 'MEDIUM', 1, 'PUBLISHED', NOW(),
   'What does a white circle with a red border and a horizontal white bar in the center mean?',
   'ماذا يعني الدائرة البيضاء ذات الإطار الأحمر والشريط الأبيض الأفقي في المنتصف؟',
   'Wat betekent een witte cirkel met rode rand en een horizontale witte balk in het midden?',
   'Que signifie un cercle blanc à bordure rouge avec une barre horizontale blanche au centre?',
   'This is the C1 "No Entry" sign. It prohibits all vehicles from entering. It is commonly placed at the entry of one-way streets and restricted zones.',
   'هذه علامة C1 "ممنوع الدخول". تحظر دخول جميع المركبات. يُضعها عادة عند مدخل الطرق ذات الاتجاه الواحد والمناطق المقيدة.',
   'Dit is het C1 "Verboden toegang"-bord. Het verbiedt alle voertuigen om in te rijden. Het wordt gewoonlijk geplaatst bij de ingang van eenrichtingswegen en beperkte zones.',
   'C''est le panneau C1 "Accès interdit". Il interdit à tous les véhicules d''entrer. Il est couramment placé à l''entrée des sens uniques et des zones restreintes.',
   'SIGN_CONFUSION', 1, 0);

INSERT INTO quiz_answer_options
  (id, question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order)
VALUES
  (611, 130, 'No entry for all vehicles',
   'ممنوع الدخول لجميع المركبات',
   'Verboden toegang voor alle voertuigen',
   'Accès interdit à tous les véhicules', 1, 1),
  (612, 130, 'Speed limit zone ahead',
   'منطقة حد السرعة في الأمام',
   'Zone met snelheidsbeperking voor u',
   'Zone de limitation de vitesse en avant', 0, 2),
  (613, 130, 'No parking on this side of the road',
   'لا موقف على هذا الجانب من الطريق',
   'Parkeren verboden aan deze kant van de weg',
   'Stationnement interdit de ce côté de la route', 0, 3);

-- Q131 | MEDIUM | Category C (Prohibition Signs) — Speed limit
INSERT INTO quiz_questions
  (id, category_id, question_type, difficulty_level, is_active, status, published_at,
   question_en, question_ar, question_nl, question_fr,
   explanation_en, explanation_ar, explanation_nl, explanation_fr,
   typical_error_type, context_specific, requires_sign_image)
VALUES
  (131, 3, 'MULTIPLE_CHOICE', 'MEDIUM', 1, 'PUBLISHED', NOW(),
   'What does a round sign with a black number inside a red circle indicate?',
   'ماذا يعني العلامة الدائرية بأرقام سوداء داخل دائرة حمراء؟',
   'Wat geeft een rond bord met een zwart getal binnen een rode cirkel aan?',
   'Que signifie un panneau rond avec un chiffre noir à l''intérieur d''un cercle rouge?',
   'A red circle with a number inside is a maximum speed limit sign (C43). Drivers must not exceed this speed. It overrides any default speed limit for that road type.',
   'الدائرة الحمراء مع رقم بداخلها هي علامة الحد الأقصى للسرعة (C43). يجب على السائقين عدم تجاوز هذه السرعة. تتجاوز الحد الافتراضي لذلك النوع من الطريق.',
   'Een rode cirkel met een getal erin is een maximumsnelheidslimietbord (C43). Bestuurders mogen deze snelheid niet overschrijden. Het vervangt elke standaard snelheidslimiet voor dat wegtype.',
   'Un cercle rouge avec un chiffre est un panneau de vitesse maximale (C43). Les conducteurs ne doivent pas dépasser cette vitesse. Il remplace toute limite de vitesse par défaut pour ce type de route.',
   'SPEED_LIMIT_ERROR', 1, 0);

INSERT INTO quiz_answer_options
  (id, question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order)
VALUES
  (614, 131, 'Maximum speed limit in kilometres per hour',
   'الحد الأقصى للسرعة بالكيلومتر في الساعة',
   'Maximale snelheidslimiet in kilometer per uur',
   'Vitesse maximale autorisée en kilomètres par heure', 1, 1),
  (615, 131, 'Minimum speed required in kilometres per hour',
   'الحد الأدنى للسرعة المطلوبة بالكيلومتر في الساعة',
   'Minimumsnelheid vereist in kilometer per uur',
   'Vitesse minimale requise en kilomètres par heure', 0, 2),
  (616, 131, 'Advisory speed for dangerous curves',
   'السرعة الموصى بها للمنعطفات الخطرة',
   'Aanbevolen snelheid voor gevaarlijke bochten',
   'Vitesse conseillée pour les courbes dangereuses', 0, 3);

-- Q132 | MEDIUM | Category D (Mandatory Signs) — Turn right
INSERT INTO quiz_questions
  (id, category_id, question_type, difficulty_level, is_active, status, published_at,
   question_en, question_ar, question_nl, question_fr,
   explanation_en, explanation_ar, explanation_nl, explanation_fr,
   typical_error_type, context_specific, requires_sign_image)
VALUES
  (132, 4, 'MULTIPLE_CHOICE', 'MEDIUM', 1, 'PUBLISHED', NOW(),
   'What does a blue circular sign with a white arrow pointing to the right mean?',
   'ماذا يعني العلامة الدائرية الزرقاء ذات السهم الأبيض المشير إلى اليمين؟',
   'Wat betekent een blauw rond bord met een witte pijl naar rechts?',
   'Que signifie un panneau rond bleu avec une flèche blanche pointant vers la droite?',
   'A blue circle with a rightward white arrow (D5) is a mandatory direction sign. You must turn right at this junction. It is an obligation, not a suggestion.',
   'الدائرة الزرقاء مع السهم الأبيض المشير يميناً (D5) هي علامة اتجاه إلزامي. يجب الانعطاف يميناً عند هذا التقاطع. هذا التزام وليس اقتراحاً.',
   'Een blauwe cirkel met een witte pijl naar rechts (D5) is een verplicht richtingsbord. U moet rechtsaf slaan op dit kruispunt. Het is een verplichting, geen suggestie.',
   'Un cercle bleu avec une flèche blanche vers la droite (D5) est un panneau de direction obligatoire. Vous devez tourner à droite à cette jonction. C''est une obligation.',
   'SIGN_CONFUSION', 1, 0);

INSERT INTO quiz_answer_options
  (id, question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order)
VALUES
  (617, 132, 'You must turn right',
   'يجب عليك الانعطاف يميناً',
   'U moet rechtsaf slaan',
   'Vous devez tourner à droite', 1, 1),
  (618, 132, 'You may turn right if you wish',
   'يمكنك الانعطاف يميناً إذا أردت',
   'U mag rechtsaf slaan als u wilt',
   'Vous pouvez tourner à droite si vous le souhaitez', 0, 2),
  (619, 132, 'Right turn is prohibited ahead',
   'الانعطاف يميناً محظور في الأمام',
   'Rechtsaf slaan is verboden voor u',
   'Le virage à droite est interdit en avant', 0, 3);

-- Q133 | MEDIUM | Category D (Mandatory Signs) — Blue circular sign category
INSERT INTO quiz_questions
  (id, category_id, question_type, difficulty_level, is_active, status, published_at,
   question_en, question_ar, question_nl, question_fr,
   explanation_en, explanation_ar, explanation_nl, explanation_fr,
   typical_error_type, context_specific, requires_sign_image)
VALUES
  (133, 4, 'MULTIPLE_CHOICE', 'MEDIUM', 1, 'PUBLISHED', NOW(),
   'Blue circular signs with white symbols in Belgian road signs belong to which category?',
   'العلامات الدائرية الزرقاء ذات الرموز البيضاء في علامات الطريق البلجيكية تنتمي إلى أي فئة؟',
   'Blauwe ronde borden met witte symbolen bij Belgische verkeersborden behoren tot welke categorie?',
   'Les panneaux circulaires bleus avec des symboles blancs dans la signalisation belge appartiennent à quelle catégorie?',
   'In Belgian road sign classification, blue circular signs indicate obligations or mandatory instructions (category D). They tell you what you must do, as opposed to red circles which prohibit actions.',
   'في تصنيف علامات الطريق البلجيكية، العلامات الدائرية الزرقاء تشير إلى الالتزامات أو التعليمات الإلزامية (الفئة D). تخبرك بما يجب فعله، بعكس الدوائر الحمراء التي تحظر أفعالاً.',
   'In de Belgische verkeersbordclassificatie geven blauwe ronde borden verplichtingen of verplichte instructies aan (categorie D). Ze vertellen u wat u moet doen, in tegenstelling tot rode cirkels die handelingen verbieden.',
   'Dans la classification belge des panneaux, les panneaux ronds bleus indiquent des obligations ou instructions obligatoires (catégorie D). Ils indiquent ce que vous devez faire, par opposition aux cercles rouges qui interdisent.',
   'SIGN_CONFUSION', 0, 0);

INSERT INTO quiz_answer_options
  (id, question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order)
VALUES
  (620, 133, 'Obligation or mandatory instruction signs',
   'علامات الالتزام أو التعليمات الإلزامية',
   'Gebodsverkeerstekens of verplichtingsborden',
   'Panneaux d''obligation ou d''instruction obligatoire', 1, 1),
  (621, 133, 'Information and service signs',
   'علامات المعلومات والخدمات',
   'Informatie- en dienstenborden',
   'Panneaux d''information et de services', 0, 2),
  (622, 133, 'Warning and danger signs',
   'علامات التحذير والخطر',
   'Waarschuwings- en gevaarsborden',
   'Panneaux d''avertissement et de danger', 0, 3);

-- Q134 | MEDIUM | Category E (Parking Signs) — No parking (P crossed out)
INSERT INTO quiz_questions
  (id, category_id, question_type, difficulty_level, is_active, status, published_at,
   question_en, question_ar, question_nl, question_fr,
   explanation_en, explanation_ar, explanation_nl, explanation_fr,
   typical_error_type, context_specific, requires_sign_image)
VALUES
  (134, 5, 'MULTIPLE_CHOICE', 'MEDIUM', 1, 'PUBLISHED', NOW(),
   'What does a blue sign with the letter P and a red diagonal line through it mean?',
   'ماذا يعني العلامة الزرقاء التي تحمل الحرف P وعليها خط أحمر قطري؟',
   'Wat betekent een blauw bord met de letter P en een rode diagonale streep erdoor?',
   'Que signifie un panneau bleu avec la lettre P et une ligne diagonale rouge?',
   'A blue P sign with a red diagonal line is the E3 "no parking" sign. Parking is prohibited but brief stopping to drop off a passenger is still permitted.',
   'العلامة الزرقاء P مع الخط الأحمر القطري هي علامة E3 "ممنوع الوقوف". الوقوف محظور لكن التوقف القصير لإنزال راكب لا يزال مسموحاً به.',
   'Een blauw P-bord met een rode diagonale streep is het E3 "verboden te parkeren"-bord. Parkeren is verboden maar kort stoppen om een passagier af te zetten is nog steeds toegestaan.',
   'Un panneau P bleu avec une ligne diagonale rouge est le panneau E3 "stationnement interdit". Le stationnement est interdit mais un bref arrêt pour déposer un passager est encore autorisé.',
   'ZONE_CONFUSION', 1, 0);

INSERT INTO quiz_answer_options
  (id, question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order)
VALUES
  (623, 134, 'Parking is not allowed',
   'الوقوف غير مسموح به',
   'Parkeren is niet toegestaan',
   'Le stationnement n''est pas autorisé', 1, 1),
  (624, 134, 'Paid parking zone',
   'منطقة الوقوف المدفوع',
   'Betalende parkeerzone',
   'Zone de stationnement payant', 0, 2),
  (625, 134, 'Parking reserved for permit holders',
   'الوقوف مخصص لحاملي التصاريح',
   'Parkeren voorbehouden aan vergunninghouders',
   'Stationnement réservé aux détenteurs de permis', 0, 3);

-- Q135 | MEDIUM | Category E (Parking Signs) — Blue P (permitted parking)
INSERT INTO quiz_questions
  (id, category_id, question_type, difficulty_level, is_active, status, published_at,
   question_en, question_ar, question_nl, question_fr,
   explanation_en, explanation_ar, explanation_nl, explanation_fr,
   typical_error_type, context_specific, requires_sign_image)
VALUES
  (135, 5, 'MULTIPLE_CHOICE', 'MEDIUM', 1, 'PUBLISHED', NOW(),
   'What does a blue rectangular sign showing only the letter P indicate?',
   'ماذا تدل العلامة المستطيلة الزرقاء التي تحمل الحرف P فقط؟',
   'Wat geeft een blauw rechthoekig bord met alleen de letter P aan?',
   'Que signifie un panneau rectangulaire bleu affichant uniquement la lettre P?',
   'A plain blue P sign (E9a) indicates a permitted public parking area. Unless supplementary signs restrict the time or user type, parking is allowed for all vehicles.',
   'العلامة الزرقاء P البسيطة (E9a) تشير إلى منطقة وقوف عامة مسموح بها. ما لم تقيّدها علامات تكميلية تقيد الوقت أو نوع المستخدم، الوقوف مسموح لجميع المركبات.',
   'Een gewoon blauw P-bord (E9a) geeft een toegestane openbare parkeerplaats aan. Tenzij aanvullende borden de tijd of het gebruikerstype beperken, is parkeren toegestaan voor alle voertuigen.',
   'Un simple panneau P bleu (E9a) indique une zone de stationnement public autorisé. Sauf si des panneaux complémentaires restreignent la durée ou le type d''utilisateur, le stationnement est autorisé.',
   'ZONE_CONFUSION', 1, 0);

INSERT INTO quiz_answer_options
  (id, question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order)
VALUES
  (626, 135, 'A permitted parking area',
   'منطقة وقوف مسموح بها',
   'Een toegestane parkeerplaats',
   'Une zone de stationnement autorisée', 1, 1),
  (627, 135, 'Parking for police vehicles only',
   'وقوف لمركبات الشرطة فقط',
   'Parkeren alleen voor politievoertuigen',
   'Parking réservé aux véhicules de police', 0, 2),
  (628, 135, 'End of parking restrictions',
   'نهاية قيود الوقوف',
   'Einde van parkeerbeperkingen',
   'Fin des restrictions de stationnement', 0, 3);

-- ============================================================
-- HARD QUESTIONS (7 new: IDs 136-142)
-- ============================================================

-- Q136 | HARD | Category A (Danger Signs) — Car with wavy lines (slippery)
INSERT INTO quiz_questions
  (id, category_id, question_type, difficulty_level, is_active, status, published_at,
   question_en, question_ar, question_nl, question_fr,
   explanation_en, explanation_ar, explanation_nl, explanation_fr,
   typical_error_type, context_specific, requires_sign_image)
VALUES
  (136, 1, 'MULTIPLE_CHOICE', 'HARD', 1, 'PUBLISHED', NOW(),
   'A triangular warning sign shows a car with wavy lines beneath it. What specific hazard does this indicate?',
   'تُظهر علامة تحذير مثلثة سيارة مع خطوط متموجة تحتها. ما الخطر المحدد الذي تشير إليه؟',
   'Een driehoekig waarschuwingsbord toont een auto met golvende lijnen eronder. Welk specifiek gevaar geeft dit aan?',
   'Un panneau d''avertissement triangulaire montre une voiture avec des lignes ondulées en dessous. Quel danger spécifique cela indique-t-il?',
   'The A1a sign shows a skidding car over wavy lines indicating a slippery road surface. This is different from the general "uneven surface" sign. It warns specifically of grip loss risk, often near bridges or shaded sections.',
   'علامة A1a تُظهر سيارة تنزلق فوق خطوط متموجة مشيرة إلى سطح طريق زلق. تختلف عن علامة السطح غير المستوي. تحذر تحديداً من خطر فقدان الإمساك بالطريق.',
   'Het A1a-bord toont een glijdende auto over golvende lijnen die een glad wegoppervlak aangeven. Dit verschilt van het algemene "ongelijk oppervlak"-bord. Het waarschuwt specifiek voor het risico van grip verlies.',
   'Le panneau A1a montre une voiture en dérapage sur des lignes ondulées indiquant une surface glissante. C''est différent du panneau "surface inégale". Il avertit spécifiquement du risque de perte d''adhérence.',
   'SIGN_CONFUSION', 1, 0);

INSERT INTO quiz_answer_options
  (id, question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order)
VALUES
  (629, 136, 'Slippery or unstable road surface ahead',
   'طريق زلق أو غير مستقر في الأمام',
   'Glad of onstabiel wegdek voor u',
   'Chaussée glissante ou instable en avant', 1, 1),
  (630, 136, 'Flooding or water on the road surface',
   'فيضانات أو مياه على سطح الطريق',
   'Overstromingen of water op het wegoppervlak',
   'Inondations ou eau sur la surface de la route', 0, 2),
  (631, 136, 'Speed bumps or traffic calming measures ahead',
   'مطبات سرعة أو تدابير تهدئة المرور في الأمام',
   'Verkeersdrempels of andere verkeersremmende maatregelen voor u',
   'Ralentisseurs ou mesures d''apaisement de la circulation en avant', 0, 3);

-- Q137 | HARD | Category A (Danger Signs) — Narrowing road (two arrows converging)
INSERT INTO quiz_questions
  (id, category_id, question_type, difficulty_level, is_active, status, published_at,
   question_en, question_ar, question_nl, question_fr,
   explanation_en, explanation_ar, explanation_nl, explanation_fr,
   typical_error_type, context_specific, requires_sign_image)
VALUES
  (137, 1, 'MULTIPLE_CHOICE', 'HARD', 1, 'PUBLISHED', NOW(),
   'A triangular danger sign shows two arrows pointing toward each other from the sides. What does this warn about?',
  'تُظهر علامة خطر مثلثة سهمين يشيران من الجانبين نحو بعضهما. ما الخطر الذي تشير إليه هذه العلامة المرورية؟',
   'Een driehoekig gevaarsbord toont twee pijlen die van de zijkanten naar elkaar toe wijzen. Waarvoor waarschuwt dit?',
   'Un panneau de danger triangulaire montre deux flèches pointant l''une vers l''autre depuis les côtés. De quoi avertit-il?',
   'The A7a "road narrows on both sides" sign warns that the carriageway reduces in width. This may require extra caution when meeting oncoming traffic. Drivers should slow and be prepared to give way.',
   'علامة A7a "تضييق الطريق من كلا الجانبين" تحذر من أن حارة الطريق تضيق. قد يتطلب هذا توخي الحذر عند مرور مركبات مقابلة. يجب على السائقين التباطؤ والاستعداد لإعطاء الأولوية.',
   'Het A7a-bord "weg versmalt aan beide zijden" waarschuwt dat de rijbaan smaller wordt. Dit vereist mogelijk extra voorzichtigheid bij tegemoetkomend verkeer. Bestuurders moeten vertragen.',
   'Le panneau A7a "chaussée rétrécie des deux côtés" avertit que la chaussée se réduit en largeur. Cela peut nécessiter une prudence supplémentaire pour le trafic venant en sens inverse.',
   'SIGN_CONFUSION', 1, 0);

INSERT INTO quiz_answer_options
  (id, question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order)
VALUES
  (632, 137, 'The road narrows ahead on both sides',
   'الطريق يضيق في الأمام من كلا الجانبين',
   'De weg versmalt voor u aan beide zijden',
   'La route se rétrécit des deux côtés en avant', 1, 1),
  (633, 137, 'Two-way traffic begins ahead',
   'يبدأ المرور في اتجاهين في الأمام',
   'Tweerichtingsverkeer begint voor u',
   'La circulation à double sens commence en avant', 0, 2),
  (634, 137, 'Merge zone where two lanes join into one',
   'منطقة اندماج حيث تلتحم حارتان في حارة واحدة',
   'Samenvoegzone waar twee rijstroken samenkomen tot één',
   'Zone de fusion où deux voies se rejoignent en une seule', 0, 3);

-- Q138 | HARD | Category B (Priority Signs) — Priority road vs fault
INSERT INTO quiz_questions
  (id, category_id, question_type, difficulty_level, is_active, status, published_at,
   question_en, question_ar, question_nl, question_fr,
   explanation_en, explanation_ar, explanation_nl, explanation_fr,
   typical_error_type, context_specific, requires_sign_image)
VALUES
  (138, 2, 'MULTIPLE_CHOICE', 'HARD', 1, 'PUBLISHED', NOW(),
   'You are on a priority road and another vehicle from the right enters without yielding. Who bears legal responsibility for the incident?',
   'أنت على طريق ذي أولوية وتدخل مركبة من اليمين دون إعطاء الأولوية. من يتحمل المسؤولية القانونية عن الحادث؟',
   'U rijdt op een voorrangsweg en een ander voertuig van rechts rijdt op zonder voorrang te verlenen. Wie draagt de wettelijke verantwoordelijkheid voor het incident?',
   'Vous roulez sur une route prioritaire et un autre véhicule venant de droite entre sans céder le passage. Qui porte la responsabilité légale pour l''incident?',
   'On a priority road you have the right of way. The vehicle that failed to yield from the right is legally at fault. However, even on a priority road you must maintain a safe speed and stay alert — contributory negligence may apply if you were speeding.',
   'على الطريق ذي الأولوية لديك حق المرور. المركبة التي لم تعطِ الأولوية من اليمين مسؤولة قانونياً. ومع ذلك حتى على طريق الأولوية يجب الحفاظ على سرعة آمنة.',
   'Op een voorrangsweg heeft u het recht van weg. Het voertuig dat geen voorrang verleende van rechts is wettelijk schuldig. Toch moet u ook op een voorrangsweg een veilige snelheid handhaven.',
   'Sur une route prioritaire vous avez la priorité. Le véhicule qui n''a pas cédé le passage venant de droite est légalement responsable. Cependant même sur une route prioritaire vous devez maintenir une vitesse sûre.',
   'PRIORITY_MISUNDERSTANDING', 1, 0);

INSERT INTO quiz_answer_options
  (id, question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order)
VALUES
  (635, 138, 'The vehicle from the right, which failed to yield',
   'المركبة القادمة من اليمين التي لم تعطِ الأولوية',
   'Het voertuig van rechts dat geen voorrang verleende',
   'Le véhicule venant de droite qui n''a pas cédé le passage', 1, 1),
  (636, 138, 'The driver on the priority road for not anticipating the hazard',
   'السائق على طريق الأولوية لعدم توقع الخطر',
   'De bestuurder op de voorrangsweg voor het niet anticiperen op het gevaar',
   'Le conducteur sur la route prioritaire pour ne pas avoir anticipé le danger', 0, 2),
  (637, 138, 'Liability is shared equally between both drivers',
   'المسؤولية مشتركة بالتساوي بين كلا السائقين',
   'De aansprakelijkheid wordt gelijk verdeeld tussen beide bestuurders',
   'La responsabilité est partagée à parts égales entre les deux conducteurs', 0, 3);

-- Q139 | HARD | Category C (Prohibition Signs) — End of all prohibitions
INSERT INTO quiz_questions
  (id, category_id, question_type, difficulty_level, is_active, status, published_at,
   question_en, question_ar, question_nl, question_fr,
   explanation_en, explanation_ar, explanation_nl, explanation_fr,
   typical_error_type, context_specific, requires_sign_image)
VALUES
  (139, 3, 'MULTIPLE_CHOICE', 'HARD', 1, 'PUBLISHED', NOW(),
   'A white circle with multiple diagonal grey lines is posted after a series of restrictions. What does this sign mean?',
  'دائرة بيضاء بخطوط رمادية قطرية متعددة تظهر بعد سلسلة من القيود. ماذا تعني هذه العلامة المرورية؟',
   'Een witte cirkel met meerdere diagonale grijze lijnen staat na een reeks beperkingen. Wat betekent dit bord?',
   'Un cercle blanc avec plusieurs lignes grises diagonales est placé après une série de restrictions. Que signifie ce panneau?',
   'This C99 sign cancels all previous prohibitions that were signed on the road (such as speed limits, overtaking bans, etc.). The normal traffic rules resume from this point onwards.',
   'علامة C99 تلغي جميع الحظر السابق الذي تم الإشارة إليه على الطريق (مثل حدود السرعة وحظر التجاوز وما إلى ذلك). تستأنف قواعد المرور العادية من هذه النقطة.',
   'Dit C99-bord heft alle eerder aangeduide verboden op de weg op (zoals snelheidslimieten, inhaalverboden, enz.). De normale verkeersregels hervatten vanaf dit punt.',
   'Ce panneau C99 annule toutes les interdictions précédemment signalées sur la route (limites de vitesse, interdictions de dépassement, etc.). Les règles normales de circulation reprennent.',
   'RULE_OVERGENERALIZATION', 1, 0);

INSERT INTO quiz_answer_options
  (id, question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order)
VALUES
  (638, 139, 'End of all previously posted prohibitions on this road',
   'نهاية جميع الحظر المُنشر سابقاً على هذا الطريق',
   'Einde van alle eerder aangeduide verboden op deze weg',
   'Fin de toutes les interdictions précédemment signalées sur cette route', 1, 1),
  (639, 139, 'Start of a new restricted zone with different rules',
   'بداية منطقة مقيدة جديدة بقواعد مختلفة',
   'Begin van een nieuwe beperkte zone met andere regels',
   'Début d''une nouvelle zone restreinte avec des règles différentes', 0, 2),
  (640, 139, 'No restrictions apply on this particular road segment',
   'لا تطبق قيود على هذا الجزء المحدد من الطريق',
   'Er gelden geen beperkingen op dit specifieke weggedeelte',
   'Aucune restriction ne s''applique sur ce segment particulier de route', 0, 3);

-- Q140 | HARD | Category D (Mandatory Signs) — Shared path cyclists/pedestrians divided
INSERT INTO quiz_questions
  (id, category_id, question_type, difficulty_level, is_active, status, published_at,
   question_en, question_ar, question_nl, question_fr,
   explanation_en, explanation_ar, explanation_nl, explanation_fr,
   typical_error_type, context_specific, requires_sign_image)
VALUES
  (140, 4, 'MULTIPLE_CHOICE', 'HARD', 1, 'PUBLISHED', NOW(),
   'A blue circular sign shows a bicycle symbol and a pedestrian symbol separated by a vertical line. What does this indicate?',
   'تُظهر علامة دائرية زرقاء رمز دراجة ورمز مشاة مفصولَين بخط عمودي. ماذا يعني هذا؟',
   'Een blauw rond bord toont een fietssymbool en een voetgangerssymbool gescheiden door een verticale lijn. Wat geeft dit aan?',
   'Un panneau rond bleu montre un symbole de vélo et un symbole piéton séparés par une ligne verticale. Que signifie cela?',
   'The D10 sign indicates a shared path that is divided: one side for cyclists and one for pedestrians. Both users must stay in their designated zone. This is different from D9 where they share the full path without separation.',
   'علامة D10 تشير إلى مسار مشترك مقسم: جانب للدراجات وآخر للمشاة. يجب أن يبقى كلا المستخدمين في منطقتهم المخصصة. تختلف عن D9 حيث يتقاسمون المسار الكامل.',
   'Het D10-bord geeft een gedeeld pad aan dat verdeeld is: één zijde voor fietsers en één voor voetgangers. Beide gebruikers moeten in hun aangewezen zone blijven. Dit verschilt van D9 waar ze het gehele pad delen.',
   'Le panneau D10 indique un chemin partagé divisé: un côté pour les cyclistes et un pour les piétons. Les deux utilisateurs doivent rester dans leur zone désignée. Différent du D9 où ils partagent l''intégralité.',
   'SIGN_CONFUSION', 1, 0);

INSERT INTO quiz_answer_options
  (id, question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order)
VALUES
  (641, 140, 'A shared path with separate zones for cyclists and pedestrians',
   'مسار مشترك له مناطق منفصلة لراكبي الدراجات والمشاة',
   'Een gedeeld pad met aparte zones voor fietsers en voetgangers',
   'Un chemin partagé avec des zones séparées pour les cyclistes et les piétons', 1, 1),
  (642, 140, 'A path where cyclists and pedestrians share the full width freely',
   'مسار حيث يتقاسم راكبو الدراجات والمشاة العرض الكامل بحرية',
   'Een pad waar fietsers en voetgangers de volledige breedte vrij delen',
   'Un chemin où cyclistes et piétons partagent toute la largeur librement', 0, 2),
  (643, 140, 'A mandatory bicycle lane from which pedestrians are excluded entirely',
   'مسار دراجات إلزامي مع استثناء المشاة كلياً',
   'Een verplicht fietspad waarvan voetgangers volledig zijn uitgesloten',
   'Une piste cyclable obligatoire dont les piétons sont entièrement exclus', 0, 3);

-- Q141 | HARD | Category E (Parking Signs) — E1 vs E3 legal difference
INSERT INTO quiz_questions
  (id, category_id, question_type, difficulty_level, is_active, status, published_at,
   question_en, question_ar, question_nl, question_fr,
   explanation_en, explanation_ar, explanation_nl, explanation_fr,
   typical_error_type, context_specific, requires_sign_image)
VALUES
  (141, 5, 'MULTIPLE_CHOICE', 'HARD', 1, 'PUBLISHED', NOW(),
   'What is the key legal difference between an E1 (no stopping) sign and an E3 (no parking) sign in Belgium?',
   'ما الفرق القانوني الرئيسي بين علامة E1 (ممنوع التوقف) وعلامة E3 (ممنوع الركن) في بلجيكا؟',
   'Wat is het belangrijkste juridische verschil tussen een E1-bord (verboden te stoppen) en een E3-bord (verboden te parkeren) in België?',
   'Quelle est la différence juridique essentielle entre un panneau E1 (arrêt interdit) et un panneau E3 (stationnement interdit) en Belgique?',
   'E1 (no stopping) prohibits any form of stopping, even momentarily. E3 (no parking) only prohibits leaving a vehicle unattended but allows brief stops for loading, unloading or dropping off passengers.',
   'E1 (ممنوع التوقف) يحظر أي شكل من أشكال التوقف حتى للحظة. E3 (ممنوع الركن) يحظر فقط ترك المركبة دون مراقبة لكن يسمح بالتوقف القصير للتحميل أو التفريغ أو إنزال الركاب.',
   'E1 (verboden te stoppen) verbiedt elke vorm van stoppen, zelfs kort. E3 (verboden te parkeren) verbiedt alleen het verlaten van een voertuig zonder toezicht, maar staat korte stops voor laden/lossen of uitstappen toe.',
   'E1 (arrêt interdit) interdit toute forme d''arrêt, même momentanée. E3 (stationnement interdit) interdit uniquement de laisser un véhicule sans surveillance, mais permet de brefs arrêts pour charger/décharger.',
   'ZONE_CONFUSION', 0, 0);

INSERT INTO quiz_answer_options
  (id, question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order)
VALUES
  (644, 141, 'E1 prohibits any stopping; E3 prohibits only prolonged parking but allows brief stops',
   'E1 يحظر أي توقف؛ E3 يحظر الوقوف المطول فقط ويسمح بالتوقف القصير',
   'E1 verbiedt elk stoppen; E3 verbiedt alleen langdurig parkeren maar staat korte stops toe',
   'E1 interdit tout arrêt; E3 interdit seulement le stationnement prolongé mais permet les brefs arrêts', 1, 1),
  (645, 141, 'E1 applies only during day hours; E3 applies around the clock',
   'E1 ينطبق فقط خلال ساعات النهار؛ E3 ينطبق على مدار الساعة',
   'E1 geldt alleen overdag; E3 geldt de klok rond',
   'E1 s''applique uniquement pendant les heures de jour; E3 s''applique à toute heure', 0, 2),
  (646, 141, 'E1 and E3 have the same meaning but different visual designs',
   'E1 و E3 لهما نفس المعنى ولكن تصاميم بصرية مختلفة',
   'E1 en E3 hebben dezelfde betekenis maar een ander visueel ontwerp',
   'E1 et E3 ont la même signification mais des designs visuels différents', 0, 3);

-- Q142 | HARD | Category F (Information Signs) — Hospital direction (H sign)
INSERT INTO quiz_questions
  (id, category_id, question_type, difficulty_level, is_active, status, published_at,
   question_en, question_ar, question_nl, question_fr,
   explanation_en, explanation_ar, explanation_nl, explanation_fr,
   typical_error_type, context_specific, requires_sign_image)
VALUES
  (142, 6, 'MULTIPLE_CHOICE', 'HARD', 1, 'PUBLISHED', NOW(),
   'A blue rectangular information sign displays the letter H with a directional arrow. What does this sign indicate?',
  'تُظهر علامة معلومات مستطيلة زرقاء الحرف H مع سهم اتجاه. ماذا تشير هذه العلامة المرورية؟',
   'Een blauw rechthoekig informatiebord toont de letter H met een richtingspijl. Wat geeft dit bord aan?',
   'Un panneau d''information rectangulaire bleu affiche la lettre H avec une flèche directionnelle. Que signifie ce panneau?',
   'The letter H on a blue information sign stands for "Hôpital" (French) or "Hospital". It is a service direction sign and guides drivers toward the nearest hospital. These signs are part of the F-category (information signs) in Belgian road sign classification.',
  'الحرف H على علامة معلومات زرقاء يرمز إلى "Hôpital" (بالفرنسية) أو المستشفى. وهي علامة اتجاه خدمية ترشد السائقين نحو أقرب مستشفى. تنتمي هذه العلامات إلى الفئة F (علامات المعلومات) في تصنيف علامات الطريق البلجيكية.',
   'De letter H op een blauw informatiebord staat voor "Hôpital" (Frans) of ziekenhuis. Het is een dienstrichtingsbord en begeleidt bestuurders naar het dichtstbijzijnde ziekenhuis. Deze borden behoren tot de F-categorie in de Belgische verkeersbordclassificatie.',
   'La lettre H sur un panneau d''information bleu signifie "Hôpital". C''est un panneau de direction de service qui guide les conducteurs vers l''hôpital le plus proche. Ces panneaux font partie de la catégorie F des panneaux belges.',
   'SIGN_CONFUSION', 0, 0);

INSERT INTO quiz_answer_options
  (id, question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order)
VALUES
  (647, 142, 'Direction to the nearest hospital',
   'الاتجاه نحو أقرب مستشفى',
   'Richting naar het dichtstbijzijnde ziekenhuis',
   'Direction vers l''hôpital le plus proche', 1, 1),
  (648, 142, 'Hazardous materials transport route',
   'طريق نقل المواد الخطرة',
   'Route voor transport van gevaarlijke stoffen',
   'Route de transport de matières dangereuses', 0, 2),
  (649, 142, 'Heavy vehicles permitted on this route',
   'المركبات الثقيلة مسموح بها على هذا الطريق',
   'Zwaar verkeer toegestaan op deze route',
   'Véhicules lourds autorisés sur cette route', 0, 3);
