-- Deep polish B15f-B15g learner-facing priority-configuration content.
-- Replaces previously imported wrong-family question banks with the corrected
-- intersecting-side-road priority content from source JSON.

UPDATE road_signs
SET
  name_nl = 'Voorrang op kruisende zijwegen',
  name_en = 'Priority over intersecting side roads',
  name_fr = 'Priorité sur les routes latérales de croisement',
  name_ar = CASE
    WHEN sign_code = 'B15f' THEN 'الأولوية على الطرق الجانبية المتقاطعة (أعلى)'
    WHEN sign_code = 'B15g' THEN 'الأولوية على الطرق الجانبية المتقاطعة (أسفل)'
  END,
  description_nl = 'Voorrang. De horizontale streep van het symbool mag worden gewijzigd om duidelijker plaatsgesteldheid weer te geven.',
  description_en = 'Priority. The horizontal line may be modified to show local conditions.',
  description_fr = 'Priorité. La ligne horizontale peut être modifiée pour montrer les conditions locales.',
  description_ar = 'أولوية. يمكن تعديل الخط الأفقي لإظهار الظروف المحلية.'
WHERE sign_code IN ('B15f', 'B15g');

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sq.question_ar = 'ما معنى هذه العلامة المرورية؟',
  sq.question_en = 'What does this traffic sign mean?',
  sq.question_nl = 'Wat betekent dit verkeersbord?',
  sq.question_fr = 'Que signifie ce panneau de signalisation ?',
  sq.explanation_ar = 'تُشير هذه العلامة المرورية إلى أن لك الأولوية على المركبات القادمة من الطرق الجانبية المبينة في الرمز. ويُظهر الرمز شكل الطريق بدقة.',
  sq.explanation_en = 'This sign indicates that you have priority over drivers coming from the side roads shown in the symbol. The symbol shows the exact road configuration.',
  sq.explanation_nl = 'Dit bord geeft aan dat u voorrang heeft op bestuurders die uit de in het symbool aangegeven zijwegen komen. Het symbool toont de precieze wegconfiguratie.',
  sq.explanation_fr = 'Ce panneau indique que vous avez la priorité sur les conducteurs venant des routes latérales indiquées dans le symbole. Le symbole montre la configuration exacte de la route.'
WHERE rs.sign_code IN ('B15f', 'B15g')
  AND sq.question_ref IN ('B15f_Q01', 'B15g_Q01');

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'لديك الأولوية على المركبات القادمة من الطرق الجانبية المتقاطعة',
  sc.text_en = 'You have priority over the traffic coming from the intersecting side roads',
  sc.text_nl = 'U heeft voorrang op het verkeer dat uit de kruisende zijwegen komt',
  sc.text_fr = 'Vous avez la priorité sur la circulation venant des routes latérales de croisement',
  sc.is_correct = 1
WHERE rs.sign_code IN ('B15f', 'B15g')
  AND sq.question_ref IN ('B15f_Q01', 'B15g_Q01')
  AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'يجب عليك إعطاء الأولوية للمركبات على الطرق الجانبية المتقاطعة',
  sc.text_en = 'You must give way to traffic on the intersecting side roads',
  sc.text_nl = 'U moet voorrang verlenen aan verkeer op de kruisende zijwegen',
  sc.text_fr = 'Vous devez céder le passage à la circulation sur les routes latérales de croisement',
  sc.is_correct = 0
WHERE rs.sign_code IN ('B15f', 'B15g')
  AND sq.question_ref IN ('B15f_Q01', 'B15g_Q01')
  AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'جميع المركبات في التقاطع لها أولوية متساوية',
  sc.text_en = 'All drivers at the junction have equal priority',
  sc.text_nl = 'Alle bestuurders op het kruispunt hebben gelijke voorrang',
  sc.text_fr = 'Tous les conducteurs au carrefour ont une priorité égale',
  sc.is_correct = 0
WHERE rs.sign_code IN ('B15f', 'B15g')
  AND sq.question_ref IN ('B15f_Q01', 'B15g_Q01')
  AND sc.display_order = 3;

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sq.question_ar = 'أي علامة مرورية تستخدم رمزًا لتوضيح شكل الطريق وتبيّن حق أولويتك على الطرق الجانبية المتقاطعة؟',
  sq.question_en = 'Which sign uses a symbol to show the road configuration and indicates your right of way on the intersecting side roads?',
  sq.question_nl = 'Welk bord gebruikt een symbool om de wegconfiguratie te tonen en uw voorrang op de kruisende zijwegen aan te geven?',
  sq.question_fr = 'Quel panneau utilise un symbole pour montrer la configuration de la route et indiquer votre priorité sur les routes latérales de croisement ?',
  sq.explanation_ar = 'تستخدم هذه العلامة رمزًا يوضح شكل الطريق لتبيّن حق الأولوية على الطرق الجانبية المحددة. أما علامة طريق الأولوية فتعني أن الطريق الذي تسلكه هو طريق أولوية بشكل عام، وعلامة إعطاء الأولوية تلزمك بالتخلي عن الأولوية.',
  sq.explanation_en = 'This type of sign uses symbols to show the road configuration. A priority road sign marks the main priority road, while a give way sign requires you to yield.',
  sq.explanation_nl = 'Dit type bord gebruikt symbolen om de wegconfiguratie te tonen. Een voorrangswegbord duidt de hoofdweg aan, terwijl een bord voorrang verlenen u verplicht voorrang te geven.',
  sq.explanation_fr = 'Ce type de panneau utilise des symboles pour montrer la configuration de la route. Un panneau de route prioritaire signale la route principale, tandis qu''un panneau de cédez le passage vous oblige à laisser la priorité.'
WHERE rs.sign_code IN ('B15f', 'B15g')
  AND sq.question_ref IN ('B15f_Q02', 'B15g_Q02');

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'أولوية على الطرق الجانبية المتقاطعة',
  sc.text_en = 'Priority over intersecting side roads',
  sc.text_nl = 'Voorrang op kruisende zijwegen',
  sc.text_fr = 'Priorité sur les routes latérales de croisement',
  sc.is_correct = 1
WHERE rs.sign_code IN ('B15f', 'B15g')
  AND sq.question_ref IN ('B15f_Q02', 'B15g_Q02')
  AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'طريق أولوية',
  sc.text_en = 'Priority road sign',
  sc.text_nl = 'Voorrangsweg',
  sc.text_fr = 'Panneau de route prioritaire',
  sc.is_correct = 0
WHERE rs.sign_code IN ('B15f', 'B15g')
  AND sq.question_ref IN ('B15f_Q02', 'B15g_Q02')
  AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'إعطاء الأولوية',
  sc.text_en = 'Give way sign',
  sc.text_nl = 'Voorrang verlenen',
  sc.text_fr = 'Panneau de cédez le passage',
  sc.is_correct = 0
WHERE rs.sign_code IN ('B15f', 'B15g')
  AND sq.question_ref IN ('B15f_Q02', 'B15g_Q02')
  AND sc.display_order = 3;

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sq.question_ar = 'ما هو الخطر الذي تشير إليه هذه العلامة المرورية؟',
  sq.question_en = 'What hazard does this sign warn you about?',
  sq.question_nl = 'Voor welk gevaar waarschuwt dit bord u?',
  sq.question_fr = 'À quel danger ce panneau vous avertit-il ?',
  sq.explanation_ar = 'تنبّهك هذه العلامة المرورية إلى وجود طرق جانبية متقاطعة. وحتى عندما تكون لك الأولوية، يجب أن تبقى يقظًا لأن السائقين الآخرين قد يرتكبون أخطاء.',
  sq.explanation_en = 'This sign warns you about intersecting side roads. Even when you have priority, you must remain alert because other drivers may make mistakes.',
  sq.explanation_nl = 'Dit bord waarschuwt u voor kruisende zijwegen. Ook wanneer u voorrang heeft, blijft waakzaamheid nodig omdat andere bestuurders fouten kunnen maken.',
  sq.explanation_fr = 'Ce panneau vous avertit de routes latérales de croisement. Même si vous avez la priorité, vous devez rester vigilant car d''autres conducteurs peuvent commettre des erreurs.'
WHERE rs.sign_code IN ('B15f', 'B15g')
  AND sq.question_ref IN ('B15f_Q03', 'B15g_Q03');

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'طرق جانبية متقاطعة يجب على سائقيها إعطاؤك الأولوية لكنهم قد لا يلتزمون بذلك',
  sc.text_en = 'Intersecting side roads whose drivers must give way to you but may fail to do so',
  sc.text_nl = 'Kruisende zijwegen waarvan bestuurders u voorrang moeten verlenen maar dit mogelijk niet doen',
  sc.text_fr = 'Des routes latérales de croisement dont les conducteurs doivent vous céder le passage mais peuvent ne pas le faire',
  sc.is_correct = 1
WHERE rs.sign_code IN ('B15f', 'B15g')
  AND sq.question_ref IN ('B15f_Q03', 'B15g_Q03')
  AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'منعطف خطير مع رؤية محدودة',
  sc.text_en = 'A dangerous curve with limited visibility',
  sc.text_nl = 'Een gevaarlijke bocht met beperkte zichtbaarheid',
  sc.text_fr = 'Un virage dangereux avec visibilité réduite',
  sc.is_correct = 0
WHERE rs.sign_code IN ('B15f', 'B15g')
  AND sq.question_ref IN ('B15f_Q03', 'B15g_Q03')
  AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'حركة مرور مقابلة تسير في مسارك',
  sc.text_en = 'Oncoming traffic driving in your lane',
  sc.text_nl = 'Tegemoetkomend verkeer dat in uw rijstrook rijdt',
  sc.text_fr = 'Une circulation venant en sens inverse dans votre voie',
  sc.is_correct = 0
WHERE rs.sign_code IN ('B15f', 'B15g')
  AND sq.question_ref IN ('B15f_Q03', 'B15g_Q03')
  AND sc.display_order = 3;

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sq.question_ar = 'ماذا يجب على السائقين القادمين من الطرق الجانبية فعله عند وجود هذه العلامة المرورية؟',
  sq.question_en = 'What must drivers on the side roads do when this sign is present?',
  sq.question_nl = 'Wat moeten bestuurders op de zijwegen doen wanneer dit bord aanwezig is?',
  sq.question_fr = 'Que doivent faire les conducteurs venant des routes latérales lorsque ce panneau est présent ?',
  sq.explanation_ar = 'يجب على السائقين القادمين من الطرق الجانبية المبينة في الرمز الانتظار وإعطاء الأولوية لحركة المرور على طريق الأولوية.',
  sq.explanation_en = 'Drivers using the side roads shown in the symbol must wait and give way to traffic on the priority road.',
  sq.explanation_nl = 'Bestuurders op de in het symbool aangegeven zijwegen moeten wachten en voorrang verlenen aan het verkeer op de voorrangsweg.',
  sq.explanation_fr = 'Les conducteurs venant des routes latérales indiquées dans le symbole doivent attendre et céder le passage à la circulation sur la route prioritaire.'
WHERE rs.sign_code IN ('B15f', 'B15g')
  AND sq.question_ref IN ('B15f_Q04', 'B15g_Q04');

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'الانتظار وإعطاء الأولوية لحركة المرور على الطريق الرئيسي قبل الدخول',
  sc.text_en = 'Wait and give way to traffic on the main road before entering',
  sc.text_nl = 'Wachten en voorrang verlenen aan het verkeer op de hoofdweg voordat zij oprijden',
  sc.text_fr = 'Attendre et céder le passage à la circulation sur la route principale avant de s''engager',
  sc.is_correct = 1
WHERE rs.sign_code IN ('B15f', 'B15g')
  AND sq.question_ref IN ('B15f_Q04', 'B15g_Q04')
  AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'دخول الطريق ما دام السير على الطريق الرئيسي يبطئ',
  sc.text_en = 'Enter as long as traffic on the main road slows down',
  sc.text_nl = 'De weg oprijden zolang het verkeer op de hoofdweg vertraagt',
  sc.text_fr = 'S''engager tant que la circulation sur la route principale ralentit',
  sc.is_correct = 0
WHERE rs.sign_code IN ('B15f', 'B15g')
  AND sq.question_ref IN ('B15f_Q04', 'B15g_Q04')
  AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'المتابعة لأن الأولوية من جهتهم',
  sc.text_en = 'Continue because priority is on their side',
  sc.text_nl = 'Doorrijden omdat de voorrang aan hun kant ligt',
  sc.text_fr = 'Continuer parce que la priorité est de leur côté',
  sc.is_correct = 0
WHERE rs.sign_code IN ('B15f', 'B15g')
  AND sq.question_ref IN ('B15f_Q04', 'B15g_Q04')
  AND sc.display_order = 3;

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sq.question_ar = 'أنت تسير على طريق الأولوية مع هذه العلامة المرورية. مركبة قادمة من طريق جانبي تدخل رغم ذلك. ماذا تفعل؟',
  sq.question_en = 'You are driving on the priority road with this sign. A vehicle from a side road drives on anyway. What do you do?',
  sq.question_nl = 'U rijdt op de voorrangsweg met dit bord. Een voertuig uit een zijweg rijdt toch door. Wat doet u?',
  sq.question_fr = 'Vous circulez sur la route prioritaire avec ce panneau. Un véhicule venant d''une route latérale s''engage quand même. Que faites-vous ?',
  sq.explanation_ar = 'القيادة الآمنة تتقدم دائمًا على التمسك بحق الأولوية. يجب عليك تجنب الاصطدام بغض النظر عمّن له الحق قانونيًا.',
  sq.explanation_en = 'Driving safely always comes before claiming priority. You must avoid a collision regardless of who is legally in the right.',
  sq.explanation_nl = 'Veilig rijden gaat altijd vóór het opeisen van voorrang. U moet een aanrijding vermijden, ongeacht wie juridisch in zijn recht is.',
  sq.explanation_fr = 'La conduite en sécurité passe toujours avant le fait de revendiquer la priorité. Vous devez éviter la collision, quelle que soit la personne juridiquement prioritaire.'
WHERE rs.sign_code IN ('B15f', 'B15g')
  AND sq.question_ref IN ('B15f_Q05', 'B15g_Q05');

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'تفرمل وتتجنب الاصطدام عند الحاجة حتى لو كانت لك الأولوية',
  sc.text_en = 'Brake and, if necessary, swerve to avoid a collision, even though you have priority',
  sc.text_nl = 'U remt en wijkt zo nodig uit om een botsing te vermijden, ook al bent u in uw recht',
  sc.text_fr = 'Vous freinez et vous déviez si nécessaire pour éviter une collision, même si vous êtes prioritaire',
  sc.is_correct = 1
WHERE rs.sign_code IN ('B15f', 'B15g')
  AND sq.question_ref IN ('B15f_Q05', 'B15g_Q05')
  AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'تتابع دون إبطاء؛ فالخطأ على السائق الآخر',
  sc.text_en = 'Continue without slowing down; it is the other driver''s fault',
  sc.text_nl = 'U rijdt onverminderd door; het is de fout van de andere bestuurder',
  sc.text_fr = 'Vous continuez sans ralentir ; c''est la faute de l''autre conducteur',
  sc.is_correct = 0
WHERE rs.sign_code IN ('B15f', 'B15g')
  AND sq.question_ref IN ('B15f_Q05', 'B15g_Q05')
  AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'تزيد سرعتك لتوضح أن لك الأولوية',
  sc.text_en = 'Accelerate to make it clear that you have priority',
  sc.text_nl = 'U versnelt om duidelijk te maken dat u voorrang heeft',
  sc.text_fr = 'Vous accélérez pour montrer clairement que vous avez la priorité',
  sc.is_correct = 0
WHERE rs.sign_code IN ('B15f', 'B15g')
  AND sq.question_ref IN ('B15f_Q05', 'B15g_Q05')
  AND sc.display_order = 3;

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sq.question_ar = 'ما الذي يُمثّله الخط السميك في رمز هذه العلامة المرورية؟',
  sq.question_en = 'What does the thick line in the symbol of this sign represent?',
  sq.question_nl = 'Wat stelt de dikke lijn in het symbool van dit bord voor?',
  sq.question_fr = 'Que représente la ligne épaisse dans le symbole de ce panneau ?',
  sq.explanation_ar = 'في هذا النوع من العلامات يُمثّل الخط السميك دائمًا طريقك، أي طريق الأولوية. أما الخطوط الرفيعة فتمثّل الطرق الجانبية التي يجب على سائقيها الانتظار وإعطاؤك الأولوية.',
  sq.explanation_en = 'In this type of sign, the thick line always represents your own road, that is, the priority road. The thin lines show the side roads that must wait.',
  sq.explanation_nl = 'Bij dit type bord stelt de dikke lijn altijd uw weg voor, dus de voorrangsweg. De dunne lijnen tonen de zijwegen die moeten wachten.',
  sq.explanation_fr = 'Dans ce type de panneau, la ligne épaisse représente toujours votre route, c''est-à-dire la route prioritaire. Les lignes fines montrent les routes latérales qui doivent attendre.'
WHERE rs.sign_code IN ('B15f', 'B15g')
  AND sq.question_ref IN ('B15f_Q06', 'B15g_Q06');

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'طريقك الذي تتمتع عليه بحق الأولوية',
  sc.text_en = 'Your own road on which you have right of way',
  sc.text_nl = 'Uw eigen weg waarop u voorrang heeft',
  sc.text_fr = 'Votre propre route sur laquelle vous avez la priorité',
  sc.is_correct = 1
WHERE rs.sign_code IN ('B15f', 'B15g')
  AND sq.question_ref IN ('B15f_Q06', 'B15g_Q06')
  AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'الطرق الجانبية التي يجب أن تعطيها الأولوية',
  sc.text_en = 'The side roads to which you must give way',
  sc.text_nl = 'De zijwegen waaraan u voorrang moet verlenen',
  sc.text_fr = 'Les routes latérales auxquelles vous devez céder le passage',
  sc.is_correct = 0
WHERE rs.sign_code IN ('B15f', 'B15g')
  AND sq.question_ref IN ('B15f_Q06', 'B15g_Q06')
  AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'طريق لا يخضع لأي تنظيم للأولوية',
  sc.text_en = 'A road with no priority regulation at all',
  sc.text_nl = 'Een weg zonder enige voorrangsregeling',
  sc.text_fr = 'Une route sans aucune règle de priorité',
  sc.is_correct = 0
WHERE rs.sign_code IN ('B15f', 'B15g')
  AND sq.question_ref IN ('B15f_Q06', 'B15g_Q06')
  AND sc.display_order = 3;

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sq.question_ar = 'أنت تسير على طريق الأولوية مع هذه العلامة المرورية. هل يجوز لك تجاوز الحد الأقصى للسرعة لفرض حق الأولوية؟',
  sq.question_en = 'You are driving on the priority road with this sign. May you exceed the speed limit to enforce your priority?',
  sq.question_nl = 'U rijdt op de voorrangsweg met dit bord. Mag u sneller rijden dan de maximumsnelheid om uw voorrang af te dwingen?',
  sq.question_fr = 'Vous roulez sur la route prioritaire avec ce panneau. Pouvez-vous dépasser la limitation de vitesse pour imposer votre priorité ?',
  sq.explanation_ar = 'امتلاك الأولوية يعني فقط أن السائقين الآخرين يجب أن يتركوا لك المجال. ولا يعفيك ذلك أبدًا من الالتزام بقواعد المرور.',
  sq.explanation_en = 'Having priority only means other drivers must let you pass. It never exempts you from complying with the traffic rules.',
  sq.explanation_nl = 'Voorrang betekent alleen dat andere bestuurders u moeten laten voorgaan. Het ontslaat u nooit van de plicht om de verkeersregels te volgen.',
  sq.explanation_fr = 'Le fait d''être prioritaire signifie seulement que les autres conducteurs doivent vous laisser passer. Cela ne vous dispense jamais de respecter le code de la route.'
WHERE rs.sign_code IN ('B15f', 'B15g')
  AND sq.question_ref IN ('B15f_Q07', 'B15g_Q07');

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'لا، امتلاك الأولوية لا يعفيك أبدًا من احترام حد السرعة',
  sc.text_en = 'No, having priority never removes your duty to respect the speed limit',
  sc.text_nl = 'Nee, voorrang hebben heft uw plicht om de maximumsnelheid te respecteren nooit op',
  sc.text_fr = 'Non, le fait d''être prioritaire ne supprime jamais l''obligation de respecter la limitation de vitesse',
  sc.is_correct = 1
WHERE rs.sign_code IN ('B15f', 'B15g')
  AND sq.question_ref IN ('B15f_Q07', 'B15g_Q07')
  AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'نعم، على طريق الأولوية يمكنك مؤقتًا القيادة بسرعة أعلى لفرض حقك',
  sc.text_en = 'Yes, on a priority road you may temporarily drive faster to assert your right',
  sc.text_nl = 'Ja, op een voorrangsweg mag u tijdelijk sneller rijden om uw recht te laten gelden',
  sc.text_fr = 'Oui, sur une route prioritaire vous pouvez rouler momentanément plus vite pour faire valoir votre droit',
  sc.is_correct = 0
WHERE rs.sign_code IN ('B15f', 'B15g')
  AND sq.question_ref IN ('B15f_Q07', 'B15g_Q07')
  AND sc.display_order = 2;

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sq.question_ar = 'هذه العلامة المرورية تالفة ويصعب قراءتها. هل يجوز لسائقي الطرق الجانبية أن يفترضوا أن لديهم الأولوية؟',
  sq.question_en = 'This sign is damaged and difficult to read. May drivers from the side roads assume that they have priority?',
  sq.question_nl = 'Dit bord is beschadigd en moeilijk leesbaar. Mogen bestuurders uit de zijwegen aannemen dat zij voorrang hebben?',
  sq.question_fr = 'Ce panneau est endommagé et difficile à lire. Les conducteurs venant des routes latérales peuvent-ils supposer qu''ils ont la priorité ?',
  sq.explanation_ar = 'العلامة المرورية التالفة أو غير الواضحة لا تُلغي القاعدة القانونية. وعند الشك يجب على السائق اختيار التصرف الأكثر أمانًا: التوقف وإعطاء الأولوية.',
  sq.explanation_en = 'A damaged or difficult-to-read sign does not cancel the legal rule. When in doubt, a driver must choose the safest option: stop and give way.',
  sq.explanation_nl = 'Een beschadigd of moeilijk leesbaar bord heft de wettelijke regel niet op. Bij twijfel kiest een bestuurder voor de veiligste optie: stoppen en voorrang verlenen.',
  sq.explanation_fr = 'Un panneau endommagé ou difficile à lire ne supprime pas la règle légale. En cas de doute, le conducteur doit choisir l''option la plus sûre : s''arrêter et céder le passage.'
WHERE rs.sign_code IN ('B15f', 'B15g')
  AND sq.question_ref IN ('B15f_Q08', 'B15g_Q08');

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'لا، العلامة المرورية التالفة لا تُلغي الالتزام القانوني؛ وعند الشك يجب التوقف وإعطاء الأولوية',
  sc.text_en = 'No, a damaged sign does not remove the legal obligation; when in doubt they must stop and give way',
  sc.text_nl = 'Nee, een beschadigd bord heft de wettelijke verplichting niet op; bij twijfel moeten zij stoppen en voorrang verlenen',
  sc.text_fr = 'Non, un panneau endommagé ne supprime pas l''obligation légale ; en cas de doute ils doivent s''arrêter et céder le passage',
  sc.is_correct = 1
WHERE rs.sign_code IN ('B15f', 'B15g')
  AND sq.question_ref IN ('B15f_Q08', 'B15g_Q08')
  AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'نعم، إذا كانت العلامة غير مقروءة تسري تلقائيًا قاعدة أولوية اليمين العادية',
  sc.text_en = 'Yes, if the sign is unreadable the normal priority-from-the-right rule automatically applies',
  sc.text_nl = 'Ja, als het bord onleesbaar is, geldt automatisch de gewone voorrang van rechts',
  sc.text_fr = 'Oui, si le panneau est illisible, la priorité de droite ordinaire s''applique automatiquement',
  sc.is_correct = 0
WHERE rs.sign_code IN ('B15f', 'B15g')
  AND sq.question_ref IN ('B15f_Q08', 'B15g_Q08')
  AND sc.display_order = 2;