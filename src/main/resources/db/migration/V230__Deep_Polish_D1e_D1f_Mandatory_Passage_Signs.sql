-- Deep polish D1e-D1f learner-facing mandatory-passage content.
-- Generated from the cleaned source JSON so persisted questions and choices match the live content.

UPDATE sign_questions
SET question_ar = 'ما معنى هذه العلامة المرورية؟',
    question_en = 'What does this traffic sign mean?',
    question_nl = 'Wat betekent dit verkeersbord?',
    question_fr = 'Que signifie ce panneau de signalisation ?',
    explanation_ar = 'تُلزم هذه العلامة المرورية السائقين بالانعطاف يسارًا عند الممر المحدد. الاتجاهات الأخرى محظورة.',
    explanation_en = 'This sign requires drivers to turn left at the indicated passage. Other directions are prohibited.',
    explanation_nl = 'Dit bord verplicht bestuurders linksaf te slaan bij de aangeduide doorgang. Andere richtingen zijn verboden.',
    explanation_fr = 'Ce panneau oblige les conducteurs à tourner à gauche au passage indiqué. Les autres directions sont interdites.'
WHERE question_ref = 'D1e_Q01';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'الانعطاف الإلزامي إلى اليسار',
    sc.text_en = 'Mandatory turn left as indicated',
    sc.text_nl = 'Verplicht de aangeduide richting te volgen (linksaf)',
    sc.text_fr = 'Obligation de suivre la direction indiquee (a gauche)',
    sc.is_correct = 1
WHERE sq.question_ref = 'D1e_Q01' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'الانعطاف الإلزامي إلى اليمين',
    sc.text_en = 'Mandatory turn right as indicated',
    sc.text_nl = 'Verplicht de aangeduide richting te volgen (rechtsaf)',
    sc.text_fr = 'Obligation de suivre la direction indiquee (a droite)',
    sc.is_correct = 0
WHERE sq.question_ref = 'D1e_Q01' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'طريق إجباري إلى الأمام',
    sc.text_en = 'Mandatory straight ahead',
    sc.text_nl = 'Verplichting rechtdoor',
    sc.text_fr = 'Obligation d aller tout droit',
    sc.is_correct = 0
WHERE sq.question_ref = 'D1e_Q01' AND sc.display_order = 3;

UPDATE sign_questions
SET question_ar = 'إلى أي فئة تُصنَّف هذه العلامة المرورية؟',
    question_en = 'To which category of traffic signs does this sign belong?',
    question_nl = 'Tot welke categorie verkeersborden behoort dit bord?',
    question_fr = 'A quelle categorie de panneaux appartient ce panneau ?',
    explanation_ar = 'تنتمي هذه العلامة المرورية إلى فئة علامات الإلزام. وهي تفرض انعطافًا إلزاميًا إلى اليسار عند الممر المحدد.',
    explanation_en = 'This sign belongs to the mandatory signs category. It imposes a mandatory left turn at the indicated passage.',
    explanation_nl = 'Dit bord behoort tot de gebodsborden. Het legt een verplichte linksafbeweging op bij de aangeduide doorgang.',
    explanation_fr = 'Ce panneau appartient à la catégorie des panneaux d’obligation. Il impose un virage obligatoire à gauche au passage indiqué.'
WHERE question_ref = 'D1e_Q02';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'العلامات الإلزامية',
    sc.text_en = 'Mandatory signs',
    sc.text_nl = 'Gebodsborden',
    sc.text_fr = 'Panneaux d obligation',
    sc.is_correct = 1
WHERE sq.question_ref = 'D1e_Q02' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'علامات الحظر',
    sc.text_en = 'Prohibition signs',
    sc.text_nl = 'Verbodsborden',
    sc.text_fr = 'Panneaux d interdiction',
    sc.is_correct = 0
WHERE sq.question_ref = 'D1e_Q02' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'علامات الوقوف',
    sc.text_en = 'Parking signs',
    sc.text_nl = 'Parkeerborden',
    sc.text_fr = 'Panneaux de stationnement',
    sc.is_correct = 0
WHERE sq.question_ref = 'D1e_Q02' AND sc.display_order = 3;

UPDATE sign_questions
SET question_ar = 'أين تُستخدم هذه العلامة المرورية عادةً؟',
    question_en = 'Where is this traffic sign typically used?',
    question_nl = 'Waar wordt dit verkeersbord meestal gebruikt?',
    question_fr = 'Où ce panneau de signalisation est-il généralement utilisé ?',
    explanation_ar = 'تُوضع هذه العلامة المرورية عند ممرات محددة أو في مناطق خاصة حيث يجب أن يكون اتجاه القيادة إلى اليسار.',
    explanation_en = 'This sign is placed at specific passages or in special zones where traffic must turn to the left.',
    explanation_nl = 'Dit bord wordt geplaatst bij specifieke doorgangen of bijzondere zones waar het verkeer links moet afslaan.',
    explanation_fr = 'Ce panneau est placé à des passages précis ou dans des zones spéciales où la circulation doit tourner à gauche.'
WHERE question_ref = 'D1e_Q03';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'عند الممرات الضيقة ومواقف السيارات والأحياء السكنية حيث يٌسمح فقط بالانعطاف يسارًا',
    sc.text_en = 'At narrow passages, car parks or residential zones where only turning left is allowed',
    sc.text_nl = 'Bij smalle doorgangen, parkeerterreinen of woonwijken waar alleen linksaf mag',
    sc.text_fr = 'Aux passages etroits, parkings ou zones residentielles ou seul le virage a gauche est autorise',
    sc.is_correct = 1
WHERE sq.question_ref = 'D1e_Q03' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'على الطرق الرئيسية كبديل لـ D1b',
    sc.text_en = 'On main roads as an alternative to D1b',
    sc.text_nl = 'Op hoofdwegen als alternatief voor D1b',
    sc.text_fr = 'Sur les routes principales comme alternative a D1b',
    sc.is_correct = 0
WHERE sq.question_ref = 'D1e_Q03' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'على الطرق السريعة لتنظيم المرور',
    sc.text_en = 'On motorways to organise traffic',
    sc.text_nl = 'Op snelwegen om het verkeer te organiseren',
    sc.text_fr = 'Sur les autoroutes pour organiser le trafic',
    sc.is_correct = 0
WHERE sq.question_ref = 'D1e_Q03' AND sc.display_order = 3;

UPDATE sign_questions
SET question_ar = 'تقترب من ممر مع هذه العلامة المرورية لكنك تريد السير للأمام نحو موقف سيارات بعد الممر. هل هذا مسموح؟',
    question_en = 'You approach a passage with this sign but want to go straight to a car park beyond the passage. Is that allowed?',
    question_nl = 'U nadert een doorgang met dit bord, maar wilt rechtdoor rijden naar een parkeerplaats voorbij de doorgang. Mag dat?',
    question_fr = 'Vous approchez un passage avec ce panneau mais souhaitez aller tout droit vers un parking après le passage. Est-ce permis ?',
    explanation_ar = 'هذه العلامة المرورية ملزمة قانونيًا. بغض النظر عن الوجهة، السير للأمام محظور.',
    explanation_en = 'This sign is legally binding. Regardless of your destination, going straight is prohibited.',
    explanation_nl = 'Dit bord is juridisch bindend. Ongeacht uw bestemming is rechtdoor rijden verboden.',
    explanation_fr = 'Ce panneau est juridiquement contraignant. Quelle que soit la destination, aller tout droit est interdit.'
WHERE question_ref = 'D1e_Q04';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'لا، هذه العلامة المرورية تُلزم بالانعطاف يسارًا؛ السير للأمام محظور',
    sc.text_en = 'No, this sign requires turning left; going straight is prohibited',
    sc.text_nl = 'Neen, dit bord verplicht linksaf; rechtdoor rijden is verboden',
    sc.text_fr = 'Non, ce panneau oblige à tourner à gauche ; aller tout droit est interdit',
    sc.is_correct = 1
WHERE sq.question_ref = 'D1e_Q04' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'نعم، موقف السيارات دائمًا متاح بغض النظر عن العلامة المرورية',
    sc.text_en = 'Yes, a car park is always accessible regardless of the sign',
    sc.text_nl = 'Ja, een parkeerplaats is altijd bereikbaar ongeacht het bord',
    sc.text_fr = 'Oui, un parking est toujours accessible peu importe le panneau',
    sc.is_correct = 0
WHERE sq.question_ref = 'D1e_Q04' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'نعم، إذا قدت ببطء فأي اتجاه جيد',
    sc.text_en = 'Yes, if you drive slowly any direction is fine',
    sc.text_nl = 'Ja, als u langzaam rijdt maakt het niet uit welke richting u kiest',
    sc.text_fr = 'Oui, si vous roulez lentement peu importe la direction choisie',
    sc.is_correct = 0
WHERE sq.question_ref = 'D1e_Q04' AND sc.display_order = 3;

UPDATE sign_questions
SET question_ar = 'ما الفرق بين هذه العلامة المرورية وعلامة الانعطاف الإلزامي إلى اليسار عند التقاطع؟',
    question_en = 'What is the difference between this sign and the mandatory left-turn sign used at an ordinary junction?',
    question_nl = 'Wat is het verschil tussen dit bord en het verplichte linksafbord dat bij een gewoon kruispunt wordt gebruikt?',
    question_fr = 'Quelle est la différence entre ce panneau et le panneau d’obligation de tourner à gauche utilisé à un carrefour ordinaire ?',
    explanation_ar = 'كلتا العلامتين تفرضان الانعطاف يسارًا، لكن هذه العلامة تُستخدم عند ممر محدد، بينما تُستخدم العلامة الأخرى عند التقاطعات العادية.',
    explanation_en = 'Both signs require turning left, but this sign is used at a specific passage, while the other is used at ordinary junctions.',
    explanation_nl = 'Beide borden verplichten linksaf te slaan, maar dit bord wordt gebruikt bij een specifieke doorgang, terwijl het andere bij gewone kruispunten wordt gebruikt.',
    explanation_fr = 'Les deux panneaux imposent de tourner à gauche, mais celui-ci est utilisé à un passage précis, tandis que l’autre est utilisé aux carrefours ordinaires.'
WHERE question_ref = 'D1e_Q05';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'تُستخدم هذه العلامة المرورية عند الممرات المحددة أو الخاصة، بينما تُستخدم علامة الانعطاف الإلزامي عند التقاطعات العادية',
    sc.text_en = 'This sign is used at specific or special passages, while the mandatory turn sign is used at ordinary junctions',
    sc.text_nl = 'Dit bord wordt gebruikt bij specifieke of bijzondere doorgangen, terwijl het verplichte afslagbord bij gewone kruispunten wordt gebruikt',
    sc.text_fr = 'Ce panneau est utilisé à des passages précis ou spéciaux, tandis que le panneau d’obligation de tourner est utilisé aux carrefours ordinaires',
    sc.is_correct = 1
WHERE sq.question_ref = 'D1e_Q05' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'هاتان العلامتان متطابقتان في المعنى',
    sc.text_en = 'These two signs are identical in meaning',
    sc.text_nl = 'Deze twee borden zijn identiek van betekenis',
    sc.text_fr = 'Ces deux panneaux ont exactement la même signification',
    sc.is_correct = 0
WHERE sq.question_ref = 'D1e_Q05' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'علامة الانعطاف عند التقاطع تنطبق فقط على الشاحنات، بينما هذه العلامة تنطبق على جميع المركبات',
    sc.text_en = 'The junction turn sign applies only to trucks, while this sign applies to all vehicles',
    sc.text_nl = 'Het afslagbord op een kruispunt geldt alleen voor vrachtwagens, terwijl dit bord voor alle voertuigen geldt',
    sc.text_fr = 'Le panneau de virage au carrefour s’applique uniquement aux camions, tandis que ce panneau s’applique à tous les véhicules',
    sc.is_correct = 0
WHERE sq.question_ref = 'D1e_Q05' AND sc.display_order = 3;

UPDATE sign_questions
SET question_ar = 'هل تظل هذه العلامة المرورية سارية عندما تسمع سيارة طوارئ خلفك؟',
    question_en = 'Does this traffic sign still apply when you hear an emergency vehicle behind you?',
    question_nl = 'Blijft dit verkeersbord gelden wanneer u een noodvoertuig achter u hoort?',
    question_fr = 'Ce panneau de signalisation reste-t-il applicable lorsque vous entendez un véhicule d’urgence derrière vous ?',
    explanation_ar = 'تظل هذه العلامة المرورية سارية. تنعطف يسارًا أولًا ثم تفسح الطريق لسيارة الطوارئ فور الإمكان.',
    explanation_en = 'This sign remains in force. You turn left first and then give way to the emergency vehicle as soon as possible.',
    explanation_nl = 'Dit bord blijft van kracht. U slaat eerst linksaf en maakt daarna zo snel mogelijk plaats voor het noodvoertuig.',
    explanation_fr = 'Ce panneau reste en vigueur. Vous tournez d’abord à gauche puis vous laissez passer le véhicule d’urgence dès que possible.'
WHERE question_ref = 'D1e_Q06';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'نعم، هذه العلامة المرورية تظل سارية؛ أخل الطريق بعد نقطة الانعطاف فور الإمكان',
    sc.text_en = 'Yes, this sign remains in force; make way as soon as possible after the turning point',
    sc.text_nl = 'Ja, dit bord blijft van kracht; maak zo snel mogelijk ruimte na het afslagpunt',
    sc.text_fr = 'Oui, ce panneau reste en vigueur ; laissez passer dès que possible après le point de virage',
    sc.is_correct = 1
WHERE sq.question_ref = 'D1e_Q06' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'لا، سيارات الطوارئ تُلغي جميع قواعد المرور',
    sc.text_en = 'No, emergency vehicles cancel all traffic rules',
    sc.text_nl = 'Neen, noodvoertuigen heffen alle verkeersregels op',
    sc.text_fr = 'Non, les vehicules d urgence annulent toutes les regles de circulation',
    sc.is_correct = 0
WHERE sq.question_ref = 'D1e_Q06' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'نعم، لكن يمكن لسيارات الطوارئ المرور بالاتجاه المحظور',
    sc.text_en = 'Yes, but emergency vehicles may pass you via the prohibited direction',
    sc.text_nl = 'Ja, maar noodvoertuigen mogen u passeren via de verboden richting',
    sc.text_fr = 'Oui, mais les vehicules d urgence peuvent vous depasser par la direction interdite',
    sc.is_correct = 0
WHERE sq.question_ref = 'D1e_Q06' AND sc.display_order = 3;

UPDATE sign_questions
SET question_ar = 'هذه العلامة المرورية عند ممر، ولا توجد حركة مرور أخرى. هل يمكنك السير للأمام؟',
    question_en = 'This sign is placed at a passage and there is no other traffic. May you go straight?',
    question_nl = 'Dit bord staat bij een doorgang en er is geen ander verkeer. Mag u rechtdoor rijden?',
    question_fr = 'Ce panneau se trouve à un passage et il n’y a pas d’autre circulation. Pouvez-vous aller tout droit ?',
    explanation_ar = 'هذه العلامة المرورية مطلقة وغير مشروطة. السير للأمام محظور دائمًا.',
    explanation_en = 'This sign is absolute and unconditional. Going straight is always prohibited.',
    explanation_nl = 'Dit bord is absoluut en onvoorwaardelijk. Rechtdoor rijden is altijd verboden.',
    explanation_fr = 'Ce panneau est absolu et inconditionnel. Aller tout droit est toujours interdit.'
WHERE question_ref = 'D1e_Q07';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'لا، هذه العلامة المرورية تُلزم بالانعطاف يسارًا بغض النظر عن حجم المرور',
    sc.text_en = 'No, this sign requires turning left regardless of traffic volume',
    sc.text_nl = 'Neen, dit bord verplicht linksaf ongeacht de verkeersdrukte',
    sc.text_fr = 'Non, ce panneau oblige à tourner à gauche quelle que soit la densité du trafic',
    sc.is_correct = 1
WHERE sq.question_ref = 'D1e_Q07' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'نعم، في غياب المرور يمكنك السير للأمام إذا كان ذلك أسرع',
    sc.text_en = 'Yes, in the absence of traffic you may go straight if it is faster',
    sc.text_nl = 'Ja, bij afwezigheid van verkeer kunt u rechtdoor als dat sneller is',
    sc.text_fr = 'Oui, en l absence de trafic vous pouvez aller tout droit si c est plus rapide',
    sc.is_correct = 0
WHERE sq.question_ref = 'D1e_Q07' AND sc.display_order = 2;

UPDATE sign_questions
SET question_ar = 'عند هذه العلامة المرورية تريد الانعطاف يمينًا نحو ممر خلفها. هل هذا مسموح؟',
    question_en = 'At this sign you want to turn right toward a driveway behind it. Is that allowed?',
    question_nl = 'Bij dit bord wilt u rechtsaf slaan naar een inrit erachter. Mag dat?',
    question_fr = 'À ce panneau, vous souhaitez tourner à droite vers une entrée située derrière lui. Est-ce autorisé ?',
    explanation_ar = 'هذه العلامة المرورية لا تُجيز أي اتجاه غير اليسار. الانعطاف يمينًا محظور بغض النظر عن السبب.',
    explanation_en = 'This sign allows no direction other than left. Turning right is prohibited regardless of the reason.',
    explanation_nl = 'Dit bord laat geen andere richting toe dan linksaf. Rechtsaf slaan is verboden, ongeacht de reden.',
    explanation_fr = 'Ce panneau n’autorise aucune autre direction que la gauche. Tourner à droite est interdit quelle qu’en soit la raison.'
WHERE question_ref = 'D1e_Q08';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'لا، هذه العلامة المرورية تُجيز فقط الانعطاف يسارًا؛ الانعطاف يمينًا محظور',
    sc.text_en = 'No, this sign only allows turning left; turning right is prohibited',
    sc.text_nl = 'Neen, dit bord laat alleen linksaf toe; rechtsaf slaan is verboden',
    sc.text_fr = 'Non, ce panneau autorise uniquement de tourner à gauche ; tourner à droite est interdit',
    sc.is_correct = 1
WHERE sq.question_ref = 'D1e_Q08' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'نعم، ممر جانبي بجانب الدرب مسموح',
    sc.text_en = 'Yes, a driveway beside the passage is allowed',
    sc.text_nl = 'Ja, een oprit naast de doorgang is toegelaten',
    sc.text_fr = 'Oui, une allee pres du passage est autorisee',
    sc.is_correct = 0
WHERE sq.question_ref = 'D1e_Q08' AND sc.display_order = 2;

UPDATE sign_questions
SET question_ar = 'ما معنى هذه العلامة المرورية؟',
    question_en = 'What does this traffic sign mean?',
    question_nl = 'Wat betekent dit verkeersbord?',
    question_fr = 'Que signifie ce panneau de signalisation ?',
    explanation_ar = 'تُلزم هذه العلامة المرورية السائقين بالانعطاف يمينًا عند الممر المحدد. الاتجاهات الأخرى محظورة.',
    explanation_en = 'This sign requires drivers to turn right at the indicated passage. Other directions are prohibited.',
    explanation_nl = 'Dit bord verplicht bestuurders rechtsaf te slaan bij de aangeduide doorgang. Andere richtingen zijn verboden.',
    explanation_fr = 'Ce panneau oblige les conducteurs à tourner à droite au passage indiqué. Les autres directions sont interdites.'
WHERE question_ref = 'D1f_Q01';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'الانعطاف الإلزامي إلى اليمين',
    sc.text_en = 'Mandatory turn right as indicated',
    sc.text_nl = 'Verplicht de aangeduide richting te volgen (rechtsaf)',
    sc.text_fr = 'Obligation de suivre la direction indiquee (a droite)',
    sc.is_correct = 1
WHERE sq.question_ref = 'D1f_Q01' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'الانعطاف الإلزامي إلى اليسار',
    sc.text_en = 'Mandatory turn left as indicated',
    sc.text_nl = 'Verplicht de aangeduide richting te volgen (linksaf)',
    sc.text_fr = 'Obligation de suivre la direction indiquee (a gauche)',
    sc.is_correct = 0
WHERE sq.question_ref = 'D1f_Q01' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'طريق إجباري إلى الأمام',
    sc.text_en = 'Mandatory straight ahead',
    sc.text_nl = 'Verplichting rechtdoor',
    sc.text_fr = 'Obligation d aller tout droit',
    sc.is_correct = 0
WHERE sq.question_ref = 'D1f_Q01' AND sc.display_order = 3;

UPDATE sign_questions
SET question_ar = 'إلى أي فئة تُصنَّف هذه العلامة المرورية؟',
    question_en = 'To which category of traffic signs does this sign belong?',
    question_nl = 'Tot welke categorie verkeersborden behoort dit bord?',
    question_fr = 'A quelle categorie de panneaux appartient ce panneau ?',
    explanation_ar = 'تنتمي هذه العلامة المرورية إلى فئة علامات الإلزام. وهي تفرض انعطافًا إلزاميًا إلى اليمين عند الممر المحدد.',
    explanation_en = 'This sign belongs to the mandatory signs category. It imposes a mandatory right turn at the indicated passage.',
    explanation_nl = 'Dit bord behoort tot de gebodsborden. Het legt een verplichte rechtsafbeweging op bij de aangeduide doorgang.',
    explanation_fr = 'Ce panneau appartient à la catégorie des panneaux d’obligation. Il impose un virage obligatoire à droite au passage indiqué.'
WHERE question_ref = 'D1f_Q02';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'العلامات الإلزامية',
    sc.text_en = 'Mandatory signs',
    sc.text_nl = 'Gebodsborden',
    sc.text_fr = 'Panneaux d obligation',
    sc.is_correct = 1
WHERE sq.question_ref = 'D1f_Q02' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'علامات الحظر',
    sc.text_en = 'Prohibition signs',
    sc.text_nl = 'Verbodsborden',
    sc.text_fr = 'Panneaux d interdiction',
    sc.is_correct = 0
WHERE sq.question_ref = 'D1f_Q02' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'علامات الوقوف',
    sc.text_en = 'Parking signs',
    sc.text_nl = 'Parkeerborden',
    sc.text_fr = 'Panneaux de stationnement',
    sc.is_correct = 0
WHERE sq.question_ref = 'D1f_Q02' AND sc.display_order = 3;

UPDATE sign_questions
SET question_ar = 'أين تُستخدم هذه العلامة المرورية عادةً؟',
    question_en = 'Where is this traffic sign typically used?',
    question_nl = 'Waar wordt dit verkeersbord meestal gebruikt?',
    question_fr = 'Où ce panneau de signalisation est-il généralement utilisé ?',
    explanation_ar = 'تُوضع هذه العلامة المرورية عند ممرات محددة أو في مناطق خاصة حيث يجب أن يكون اتجاه القيادة إلى اليمين.',
    explanation_en = 'This sign is placed at specific passages or in special zones where traffic must turn to the right.',
    explanation_nl = 'Dit bord wordt geplaatst bij specifieke doorgangen of bijzondere zones waar het verkeer rechts moet afslaan.',
    explanation_fr = 'Ce panneau est placé à des passages précis ou dans des zones spéciales où la circulation doit tourner à droite.'
WHERE question_ref = 'D1f_Q03';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'عند الممرات الضيقة ومواقف السيارات والأحياء السكنية حيث يٌسمح فقط بالانعطاف يمينًا',
    sc.text_en = 'At narrow passages, car parks or residential zones where only turning right is allowed',
    sc.text_nl = 'Bij smalle doorgangen, parkeerterreinen of woonwijken waar alleen rechtsaf mag',
    sc.text_fr = 'Aux passages etroits, parkings ou zones residentielles ou seul le virage a droite est autorise',
    sc.is_correct = 1
WHERE sq.question_ref = 'D1f_Q03' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'عند التقاطعات المزدحمة كبديل لـ D1c',
    sc.text_en = 'At busy intersections as an alternative to D1c',
    sc.text_nl = 'Op drukke kruispunten als alternatief voor D1c',
    sc.text_fr = 'Aux carrefours charges comme alternative a D1c',
    sc.is_correct = 0
WHERE sq.question_ref = 'D1f_Q03' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'على الطرق السريعة لتنظيم المرور',
    sc.text_en = 'On motorways to organise traffic',
    sc.text_nl = 'Op snelwegen om het verkeer te organiseren',
    sc.text_fr = 'Sur les autoroutes pour organiser la circulation',
    sc.is_correct = 0
WHERE sq.question_ref = 'D1f_Q03' AND sc.display_order = 3;

UPDATE sign_questions
SET question_ar = 'تقترب من ممر مع هذه العلامة المرورية لكنك تريد الانعطاف يسارًا. هل هذا مسموح؟',
    question_en = 'You approach a passage with this sign but want to turn left into a street. Is that allowed?',
    question_nl = 'U nadert een doorgang met dit bord maar wilt linksaf een straat inrijden. Mag dat?',
    question_fr = 'Vous approchez un passage avec ce panneau mais souhaitez tourner à gauche dans une rue. Est-ce permis ?',
    explanation_ar = 'هذه العلامة المرورية ملزمة قانونيًا. الانعطاف يسارًا محظور.',
    explanation_en = 'This sign is legally binding. Turning left is prohibited.',
    explanation_nl = 'Dit bord is juridisch bindend. Linksaf slaan is verboden.',
    explanation_fr = 'Ce panneau est juridiquement contraignant. Tourner à gauche est interdit.'
WHERE question_ref = 'D1f_Q04';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'لا، هذه العلامة المرورية تُلزم بالانعطاف يمينًا؛ الانعطاف يسارًا محظور',
    sc.text_en = 'No, this sign requires turning right; turning left is prohibited',
    sc.text_nl = 'Neen, dit bord verplicht rechtsaf; linksaf slaan is verboden',
    sc.text_fr = 'Non, ce panneau oblige à tourner à droite ; tourner à gauche est interdit',
    sc.is_correct = 1
WHERE sq.question_ref = 'D1f_Q04' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'نعم، الشارع دائمًا متاح بغض النظر عن العلامة المرورية',
    sc.text_en = 'Yes, a street is always accessible regardless of the sign',
    sc.text_nl = 'Ja, een straat is altijd bereikbaar ongeacht het bord',
    sc.text_fr = 'Oui, une rue est toujours accessible peu importe le panneau',
    sc.is_correct = 0
WHERE sq.question_ref = 'D1f_Q04' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'نعم، إذا قدت ببطء يمكنك الانعطاف يسارًا',
    sc.text_en = 'Yes, if you drive slowly you may turn left',
    sc.text_nl = 'Ja, als u langzaam rijdt kunt u linksaf',
    sc.text_fr = 'Oui, si vous roulez lentement vous pouvez tourner a gauche',
    sc.is_correct = 0
WHERE sq.question_ref = 'D1f_Q04' AND sc.display_order = 3;

UPDATE sign_questions
SET question_ar = 'ما الفرق بين هذه العلامة المرورية وعلامة الانعطاف الإلزامي إلى اليمين عند التقاطع؟',
    question_en = 'What is the difference between this sign and the mandatory right-turn sign used at an ordinary junction?',
    question_nl = 'Wat is het verschil tussen dit bord en het verplichte rechtsafbord dat bij een gewoon kruispunt wordt gebruikt?',
    question_fr = 'Quelle est la différence entre ce panneau et le panneau d’obligation de tourner à droite utilisé à un carrefour ordinaire ?',
    explanation_ar = 'كلتا العلامتين تفرضان الانعطاف يمينًا، لكن هذه العلامة تُستخدم عند ممر محدد، بينما تُستخدم العلامة الأخرى عند التقاطعات العادية.',
    explanation_en = 'Both signs require turning right, but this sign is used at a specific passage, while the other is used at ordinary junctions.',
    explanation_nl = 'Beide borden verplichten rechtsaf te slaan, maar dit bord wordt gebruikt bij een specifieke doorgang, terwijl het andere bij gewone kruispunten wordt gebruikt.',
    explanation_fr = 'Les deux panneaux imposent de tourner à droite, mais celui-ci est utilisé à un passage précis, tandis que l’autre est utilisé aux carrefours ordinaires.'
WHERE question_ref = 'D1f_Q05';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'تُستخدم هذه العلامة المرورية عند الممرات المحددة أو الخاصة، بينما تُستخدم علامة الانعطاف الإلزامي عند التقاطعات العادية',
    sc.text_en = 'This sign is used at specific or special passages, while the mandatory turn sign is used at ordinary junctions',
    sc.text_nl = 'Dit bord wordt gebruikt bij specifieke of bijzondere doorgangen, terwijl het verplichte afslagbord bij gewone kruispunten wordt gebruikt',
    sc.text_fr = 'Ce panneau est utilisé à des passages précis ou spéciaux, tandis que le panneau d’obligation de tourner est utilisé aux carrefours ordinaires',
    sc.is_correct = 1
WHERE sq.question_ref = 'D1f_Q05' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'هاتان العلامتان متطابقتان في المعنى',
    sc.text_en = 'These two signs are identical in meaning',
    sc.text_nl = 'Deze twee borden zijn identiek van betekenis',
    sc.text_fr = 'Ces deux panneaux ont exactement la même signification',
    sc.is_correct = 0
WHERE sq.question_ref = 'D1f_Q05' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'علامة الانعطاف عند التقاطع تنطبق فقط على الشاحنات، بينما هذه العلامة تنطبق على جميع المركبات',
    sc.text_en = 'The junction turn sign applies only to trucks, while this sign applies to all vehicles',
    sc.text_nl = 'Het afslagbord op een kruispunt geldt alleen voor vrachtwagens, terwijl dit bord voor alle voertuigen geldt',
    sc.text_fr = 'Le panneau de virage au carrefour s’applique uniquement aux camions, tandis que ce panneau s’applique à tous les véhicules',
    sc.is_correct = 0
WHERE sq.question_ref = 'D1f_Q05' AND sc.display_order = 3;

UPDATE sign_questions
SET question_ar = 'أنت تقود في موقف سيارات وترى هذه العلامة المرورية. ماذا تفعل؟',
    question_en = 'You are driving in a car park and see this sign. What do you do?',
    question_nl = 'U rijdt in een parking en ziet dit bord. Wat doet u?',
    question_fr = 'Vous conduisez dans un parking et voyez ce panneau. Que faites-vous ?',
    explanation_ar = 'هذه العلامة المرورية ملزمة حتى في مواقف السيارات. يجب الانعطاف يمينًا.',
    explanation_en = 'This sign is binding even in a car park. You must turn right.',
    explanation_nl = 'Dit bord is ook in een parking bindend. U moet rechtsaf slaan.',
    explanation_fr = 'Ce panneau est contraignant même dans un parking. Vous devez tourner à droite.'
WHERE question_ref = 'D1f_Q06';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'الانعطاف يمينًا كما تشترط هذه العلامة المرورية',
    sc.text_en = 'Turn right as required by this sign',
    sc.text_nl = 'Rechtsaf slaan zoals dit bord verplicht',
    sc.text_fr = 'Tourner à droite comme l’exige ce panneau',
    sc.is_correct = 1
WHERE sq.question_ref = 'D1f_Q06' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'السير للأمام لأن الموقف خاص',
    sc.text_en = 'Go straight because the car park is private',
    sc.text_nl = 'Rechtdoor rijden omdat de parking privé is',
    sc.text_fr = 'Aller tout droit parce que le parking est privé',
    sc.is_correct = 0
WHERE sq.question_ref = 'D1f_Q06' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'تجاهل هذه العلامة المرورية لأن تخطيط المسار أولى في المواقف',
    sc.text_en = 'Ignore this sign because lane markings take precedence in car parks',
    sc.text_nl = 'Dit bord negeren omdat wegmarkeringen in parkings voorrang hebben',
    sc.text_fr = 'Ignorer ce panneau parce que le marquage au sol prime dans les parkings',
    sc.is_correct = 0
WHERE sq.question_ref = 'D1f_Q06' AND sc.display_order = 3;

UPDATE sign_questions
SET question_ar = 'هذه العلامة المرورية عند ممر، ولا توجد حركة مرور أخرى. هل يمكنك السير للأمام؟',
    question_en = 'This sign is placed at a passage and there is no other traffic. May you go straight?',
    question_nl = 'Dit bord staat bij een doorgang en er is geen ander verkeer. Mag u rechtdoor rijden?',
    question_fr = 'Ce panneau se trouve à un passage et il n’y a pas d’autre circulation. Pouvez-vous aller tout droit ?',
    explanation_ar = 'هذه العلامة المرورية مطلقة وغير مشروطة. السير للأمام محظور دائمًا.',
    explanation_en = 'This sign is absolute and unconditional. Going straight is always prohibited.',
    explanation_nl = 'Dit bord is absoluut en onvoorwaardelijk. Rechtdoor rijden is altijd verboden.',
    explanation_fr = 'Ce panneau est absolu et inconditionnel. Aller tout droit est toujours interdit.'
WHERE question_ref = 'D1f_Q07';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'لا، هذه العلامة المرورية تُلزم بالانعطاف يمينًا بغض النظر عن حجم المرور',
    sc.text_en = 'No, this sign requires turning right regardless of traffic volume',
    sc.text_nl = 'Neen, dit bord verplicht rechtsaf ongeacht de verkeersdrukte',
    sc.text_fr = 'Non, ce panneau oblige à tourner à droite quelle que soit la densité du trafic',
    sc.is_correct = 1
WHERE sq.question_ref = 'D1f_Q07' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'نعم، في غياب المرور يمكنك السير للأمام إذا كان ذلك أسرع',
    sc.text_en = 'Yes, in the absence of traffic you may go straight if it is faster',
    sc.text_nl = 'Ja, bij afwezigheid van verkeer kunt u rechtdoor als dat sneller is',
    sc.text_fr = 'Oui, en l absence de trafic vous pouvez aller tout droit si c est plus rapide',
    sc.is_correct = 0
WHERE sq.question_ref = 'D1f_Q07' AND sc.display_order = 2;

UPDATE sign_questions
SET question_ar = 'عند هذه العلامة المرورية تريد الانعطاف يسارًا نحو شارع خلفها. هل هذا مسموح؟',
    question_en = 'At this sign you want to turn left toward a street behind it. Is that allowed?',
    question_nl = 'Bij dit bord wilt u linksaf slaan naar een straat erachter. Mag dat?',
    question_fr = 'À ce panneau, vous souhaitez tourner à gauche vers une rue située derrière lui. Est-ce autorisé ?',
    explanation_ar = 'هذه العلامة المرورية لا تُجيز أي اتجاه غير اليمين. الانعطاف يسارًا محظور بغض النظر عن السبب.',
    explanation_en = 'This sign allows no direction other than right. Turning left is prohibited regardless of the reason.',
    explanation_nl = 'Dit bord laat geen andere richting toe dan rechtsaf. Linksaf slaan is verboden, ongeacht de reden.',
    explanation_fr = 'Ce panneau n’autorise aucune autre direction que la droite. Tourner à gauche est interdit quelle qu’en soit la raison.'
WHERE question_ref = 'D1f_Q08';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'لا، هذه العلامة المرورية تُجيز فقط الانعطاف يمينًا؛ الانعطاف يسارًا محظور',
    sc.text_en = 'No, this sign only allows turning right; turning left is prohibited',
    sc.text_nl = 'Neen, dit bord laat alleen rechtsaf toe; linksaf slaan is verboden',
    sc.text_fr = 'Non, ce panneau autorise uniquement de tourner à droite ; tourner à gauche est interdit',
    sc.is_correct = 1
WHERE sq.question_ref = 'D1f_Q08' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'نعم، شارع جانبي بجانب الممر مسموح',
    sc.text_en = 'Yes, a street beside the passage is allowed',
    sc.text_nl = 'Ja, een straat naast de doorgang is toegelaten',
    sc.text_fr = 'Oui, une rue pres du passage est autorisee',
    sc.is_correct = 0
WHERE sq.question_ref = 'D1f_Q08' AND sc.display_order = 2;
