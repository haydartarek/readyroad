-- Deep polish D1b (mandatory left turn) learner-facing texts in four languages.
-- Generated from source JSON to keep source and live data aligned.

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sq.question_ar = 'ما معنى هذه العلامة المرورية؟',
  sq.question_en = 'What does this traffic sign mean?',
  sq.question_nl = 'Wat betekent dit verkeersbord?',
  sq.question_fr = 'Que signifie ce panneau de signalisation ?',
  sq.explanation_ar = 'تُلزم هذه العلامة المرورية السائق بالانعطاف يسارًا. لذلك يُمنع متابعة السير إلى الأمام أو الانعطاف يمينًا.',
  sq.explanation_en = 'This traffic sign requires the driver to turn left. Driving straight ahead or turning right is therefore prohibited.',
  sq.explanation_nl = 'Dit verkeersbord verplicht de bestuurder om links af te slaan. Rechtdoor rijden of rechts afslaan is daarom verboden.',
  sq.explanation_fr = 'Ce panneau de signalisation oblige le conducteur à tourner à gauche. Aller tout droit ou tourner à droite est donc interdit.'
WHERE rs.sign_code = 'D1b-links' AND sq.question_ref = 'D1b-links_Q01';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'الانعطاف الإلزامي إلى اليسار',
  sc.text_en = 'Mandatory left turn',
  sc.text_nl = 'Verplicht links afslaan',
  sc.text_fr = 'Obligation de tourner à gauche',
  sc.is_correct = 1
WHERE rs.sign_code = 'D1b-links' AND sq.question_ref = 'D1b-links_Q01' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'متابعة السير إلى الأمام بشكل إلزامي',
  sc.text_en = 'Mandatory straight ahead',
  sc.text_nl = 'Verplicht rechtdoor',
  sc.text_fr = 'Obligation d''aller tout droit',
  sc.is_correct = 0
WHERE rs.sign_code = 'D1b-links' AND sq.question_ref = 'D1b-links_Q01' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'الانعطاف الإلزامي إلى اليمين',
  sc.text_en = 'Mandatory right turn',
  sc.text_nl = 'Verplicht rechts afslaan',
  sc.text_fr = 'Obligation de tourner à droite',
  sc.is_correct = 0
WHERE rs.sign_code = 'D1b-links' AND sq.question_ref = 'D1b-links_Q01' AND sc.display_order = 3;

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sq.question_ar = 'إلى أي فئة تُصنَّف هذه العلامة المرورية؟',
  sq.question_en = 'To which category does this traffic sign belong?',
  sq.question_nl = 'Tot welke categorie verkeersborden behoort dit bord?',
  sq.question_fr = 'À quelle catégorie de panneaux de signalisation appartient ce panneau ?',
  sq.explanation_ar = 'هذه العلامة من العلامات الإلزامية، لأنها تفرض على السائق اتباع اتجاه محدد، وهو الانعطاف يسارًا.',
  sq.explanation_en = 'This sign belongs to the category of mandatory signs because it requires the driver to follow a specific direction: turning left.',
  sq.explanation_nl = 'Dit bord behoort tot de gebodsborden, omdat het de bestuurder verplicht een bepaalde richting te volgen: links afslaan.',
  sq.explanation_fr = 'Ce panneau appartient à la catégorie des panneaux d''obligation, car il impose au conducteur une direction précise : tourner à gauche.'
WHERE rs.sign_code = 'D1b-links' AND sq.question_ref = 'D1b-links_Q02';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'العلامات الإلزامية',
  sc.text_en = 'Mandatory signs',
  sc.text_nl = 'Gebodsborden',
  sc.text_fr = 'Panneaux d''obligation',
  sc.is_correct = 1
WHERE rs.sign_code = 'D1b-links' AND sq.question_ref = 'D1b-links_Q02' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'علامات الحظر',
  sc.text_en = 'Prohibition signs',
  sc.text_nl = 'Verbodsborden',
  sc.text_fr = 'Panneaux d''interdiction',
  sc.is_correct = 0
WHERE rs.sign_code = 'D1b-links' AND sq.question_ref = 'D1b-links_Q02' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'علامات الوقوف',
  sc.text_en = 'Parking signs',
  sc.text_nl = 'Parkeerborden',
  sc.text_fr = 'Panneaux de stationnement',
  sc.is_correct = 0
WHERE rs.sign_code = 'D1b-links' AND sq.question_ref = 'D1b-links_Q02' AND sc.display_order = 3;

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sq.question_ar = 'ماذا تُلزمك هذه العلامة المرورية بفعله في هذا التقاطع؟',
  sq.question_en = 'What does this traffic sign require you to do at this junction?',
  sq.question_nl = 'Wat verplicht dit verkeersbord u te doen op dit kruispunt?',
  sq.question_fr = 'Que vous oblige ce panneau de signalisation à faire à ce carrefour ?',
  sq.explanation_ar = 'تُلزمك هذه العلامة بالانعطاف يسارًا عند التقاطع. أما الاتجاهات الأخرى فغير مسموح بها.',
  sq.explanation_en = 'This sign requires you to turn left at the junction. Other directions are not allowed.',
  sq.explanation_nl = 'Dit bord verplicht u om op het kruispunt links af te slaan. Andere richtingen zijn niet toegelaten.',
  sq.explanation_fr = 'Ce panneau vous oblige à tourner à gauche au carrefour. Les autres directions ne sont pas autorisées.'
WHERE rs.sign_code = 'D1b-links' AND sq.question_ref = 'D1b-links_Q03';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'الانعطاف يسارًا كما يشير السهم',
  sc.text_en = 'Turn left as shown by the arrow',
  sc.text_nl = 'Links afslaan zoals de pijl aangeeft',
  sc.text_fr = 'Tourner à gauche comme l''indique la flèche',
  sc.is_correct = 1
WHERE rs.sign_code = 'D1b-links' AND sq.question_ref = 'D1b-links_Q03' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'التوقف وإعطاء الأولوية للمركبات القادمة من اليمين',
  sc.text_en = 'Stop and give way to traffic coming from the right',
  sc.text_nl = 'Stoppen en voorrang verlenen aan verkeer van rechts',
  sc.text_fr = 'Vous arrêter et céder le passage au trafic venant de droite',
  sc.is_correct = 0
WHERE rs.sign_code = 'D1b-links' AND sq.question_ref = 'D1b-links_Q03' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'متابعة السير إلى الأمام ومغادرة التقاطع بسرعة',
  sc.text_en = 'Drive straight ahead and leave the junction quickly',
  sc.text_nl = 'Rechtdoor rijden en het kruispunt snel verlaten',
  sc.text_fr = 'Aller tout droit et quitter rapidement le carrefour',
  sc.is_correct = 0
WHERE rs.sign_code = 'D1b-links' AND sq.question_ref = 'D1b-links_Q03' AND sc.display_order = 3;

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sq.question_ar = 'أنت تقترب من تقاطع مع هذه العلامة المرورية، لكن الطريق الأيسر مغلق مؤقتًا بسبب أشغال. ماذا تفعل؟',
  sq.question_en = 'You are approaching a junction with this traffic sign, but the road to the left is temporarily blocked by roadworks. What do you do?',
  sq.question_nl = 'U nadert een kruispunt met dit verkeersbord, maar de weg links is tijdelijk afgesloten door werkzaamheden. Wat doet u?',
  sq.question_fr = 'Vous approchez d''un carrefour avec ce panneau de signalisation, mais la route à gauche est temporairement bloquée par des travaux. Que faites-vous ?',
  sq.explanation_ar = 'إغلاق الطريق مؤقتًا لا يلغي مفعول هذه العلامة. وإذا كان الانعطاف يسارًا غير ممكن، فعليك اختيار طريق آخر قبل الوصول إلى التقاطع.',
  sq.explanation_en = 'Temporary roadworks do not cancel the effect of this sign. If turning left is impossible, you must choose another route before reaching the junction.',
  sq.explanation_nl = 'Tijdelijke werkzaamheden heffen de werking van dit bord niet op. Als links afslaan onmogelijk is, moet u vóór het kruispunt een andere route kiezen.',
  sq.explanation_fr = 'Des travaux temporaires n''annulent pas l''effet de ce panneau. Si tourner à gauche est impossible, vous devez choisir un autre itinéraire avant d''arriver au carrefour.'
WHERE rs.sign_code = 'D1b-links' AND sq.question_ref = 'D1b-links_Q04';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'أبحث عن طريق بديل قبل الوصول إلى التقاطع، لأن هذه العلامة لا تستثني الأشغال',
  sc.text_en = 'Find an alternative route before reaching the junction, because this sign does not make an exception for roadworks',
  sc.text_nl = 'Een alternatieve route zoeken vóór u het kruispunt bereikt, want dit bord kent geen uitzondering voor werkzaamheden',
  sc.text_fr = 'Chercher un itinéraire alternatif avant d''atteindre le carrefour, car ce panneau ne prévoit aucune exception pour les travaux',
  sc.is_correct = 1
WHERE rs.sign_code = 'D1b-links' AND sq.question_ref = 'D1b-links_Q04' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'أتابع السير إلى الأمام لأن إغلاق الطريق يسمح بتجاهل العلامة',
  sc.text_en = 'Drive straight ahead because the road closure allows you to ignore the sign',
  sc.text_nl = 'Rechtdoor rijden omdat de wegafsluiting u toelaat het bord te negeren',
  sc.text_fr = 'Aller tout droit parce que la fermeture de la route vous permet d''ignorer le panneau',
  sc.is_correct = 0
WHERE rs.sign_code = 'D1b-links' AND sq.question_ref = 'D1b-links_Q04' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'أتوقف وأنتظر حتى يُعاد فتح الطريق',
  sc.text_en = 'Stop and wait until the road is reopened',
  sc.text_nl = 'Stoppen en wachten tot de weg opnieuw open is',
  sc.text_fr = 'Vous arrêter et attendre que la route soit rouverte',
  sc.is_correct = 0
WHERE rs.sign_code = 'D1b-links' AND sq.question_ref = 'D1b-links_Q04' AND sc.display_order = 3;

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sq.question_ar = 'ترى هذه العلامة المرورية، لكنك تريد الانعطاف يمينًا للوصول إلى وجهتك. ماذا يجب عليك فعله؟',
  sq.question_en = 'You see this traffic sign, but you want to turn right to reach your destination. What must you do?',
  sq.question_nl = 'U ziet dit verkeersbord, maar u wilt rechts afslaan om uw bestemming te bereiken. Wat moet u doen?',
  sq.question_fr = 'Vous voyez ce panneau de signalisation, mais vous souhaitez tourner à droite pour rejoindre votre destination. Que devez-vous faire ?',
  sq.explanation_ar = 'هذه العلامة ملزمة قانونيًا. لذلك يبقى الانعطاف يمينًا ممنوعًا، حتى لو كانت وجهتك في ذلك الاتجاه.',
  sq.explanation_en = 'This sign is legally binding. Turning right remains prohibited, even if your destination is in that direction.',
  sq.explanation_nl = 'Dit bord is wettelijk bindend. Rechts afslaan blijft verboden, ook als uw bestemming in die richting ligt.',
  sq.explanation_fr = 'Ce panneau est juridiquement contraignant. Tourner à droite reste interdit, même si votre destination se trouve dans cette direction.'
WHERE rs.sign_code = 'D1b-links' AND sq.question_ref = 'D1b-links_Q05';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'أنعطف يسارًا ثم أبحث لاحقًا عن طريق يوصلني إلى وجهتي',
  sc.text_en = 'Turn left and then find another route that leads to your destination',
  sc.text_nl = 'Links afslaan en daarna een andere route zoeken die naar uw bestemming leidt',
  sc.text_fr = 'Tourner à gauche puis chercher un autre itinéraire menant à votre destination',
  sc.is_correct = 1
WHERE rs.sign_code = 'D1b-links' AND sq.question_ref = 'D1b-links_Q05' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'أنعطف يمينًا لأن الوصول إلى الوجهة أهم من الالتزام بالعلامة',
  sc.text_en = 'Turn right because reaching your destination is more important than obeying the sign',
  sc.text_nl = 'Rechts afslaan omdat uw bestemming belangrijker is dan het volgen van het bord',
  sc.text_fr = 'Tourner à droite parce qu''atteindre votre destination est plus important que respecter le panneau',
  sc.is_correct = 0
WHERE rs.sign_code = 'D1b-links' AND sq.question_ref = 'D1b-links_Q05' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'أتوقف وأسأل مستخدمي الطريق الآخرين إن كان يمكنني الانعطاف يمينًا',
  sc.text_en = 'Stop and ask other road users whether you may turn right',
  sc.text_nl = 'Stoppen en andere weggebruikers vragen of u rechtsaf mag',
  sc.text_fr = 'Vous arrêter et demander aux autres usagers si vous pouvez tourner à droite',
  sc.is_correct = 0
WHERE rs.sign_code = 'D1b-links' AND sq.question_ref = 'D1b-links_Q05' AND sc.display_order = 3;

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sq.question_ar = 'هل تنطبق إلزامية هذه العلامة المرورية أيضًا على راكبي الدراجات والدراجات الخفيفة؟',
  sq.question_en = 'Does the obligation shown by this traffic sign also apply to cyclists and moped riders?',
  sq.question_nl = 'Geldt de verplichting van dit verkeersbord ook voor fietsers en bromfietsers?',
  sq.question_fr = 'L''obligation indiquée par ce panneau de signalisation s''applique-t-elle aussi aux cyclistes et aux cyclomotoristes ?',
  sq.explanation_ar = 'تسري العلامات الإلزامية على جميع فئات السائقين، ما لم تُوجد علامة تكميلية تنص صراحةً على استثناء فئة معينة.',
  sq.explanation_en = 'Mandatory signs apply to all categories of road user unless a supplementary sign explicitly creates an exception for a specific group.',
  sq.explanation_nl = 'Gebodsborden gelden voor alle categorieën bestuurders, tenzij een onderbord uitdrukkelijk een uitzondering voor een bepaalde groep aangeeft.',
  sq.explanation_fr = 'Les panneaux d''obligation s''appliquent à toutes les catégories d''usagers, sauf si un panneau complémentaire prévoit explicitement une exception pour un groupe déterminé.'
WHERE rs.sign_code = 'D1b-links' AND sq.question_ref = 'D1b-links_Q06';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'نعم، تسري هذه العلامة على جميع السائقين ما لم تُحدد علامة تكميلية استثناءً',
  sc.text_en = 'Yes, this sign applies to all drivers unless a supplementary sign states an exception',
  sc.text_nl = 'Ja, dit bord geldt voor alle bestuurders tenzij een onderbord een uitzondering vermeldt',
  sc.text_fr = 'Oui, ce panneau s''applique à tous les conducteurs sauf si un panneau complémentaire prévoit une exception',
  sc.is_correct = 1
WHERE rs.sign_code = 'D1b-links' AND sq.question_ref = 'D1b-links_Q06' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'لا، هذه العلامة تخص المركبات الآلية فقط',
  sc.text_en = 'No, this sign applies only to motor vehicles',
  sc.text_nl = 'Nee, dit bord geldt alleen voor motorvoertuigen',
  sc.text_fr = 'Non, ce panneau s''applique uniquement aux véhicules à moteur',
  sc.is_correct = 0
WHERE rs.sign_code = 'D1b-links' AND sq.question_ref = 'D1b-links_Q06' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'لا، يجوز لراكبي الدراجات اختيار اتجاههم بحرية دائمًا',
  sc.text_en = 'No, cyclists may always choose their direction freely',
  sc.text_nl = 'Nee, fietsers mogen altijd vrij hun richting kiezen',
  sc.text_fr = 'Non, les cyclistes peuvent toujours choisir librement leur direction',
  sc.is_correct = 0
WHERE rs.sign_code = 'D1b-links' AND sq.question_ref = 'D1b-links_Q06' AND sc.display_order = 3;

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sq.question_ar = 'هذه العلامة المرورية موجودة عند تقاطع، ولا توجد حركة مرور. هل يمكنك متابعة السير إلى الأمام؟',
  sq.question_en = 'This traffic sign is placed at a junction, and there is no traffic. May you continue straight ahead?',
  sq.question_nl = 'Dit verkeersbord staat bij een kruispunt en er is geen verkeer. Mag u rechtdoor blijven rijden?',
  sq.question_fr = 'Ce panneau de signalisation est placé à un carrefour et il n''y a pas de circulation. Pouvez-vous continuer tout droit ?',
  sq.explanation_ar = 'عدم وجود حركة مرور لا يغيّر مفعول هذه العلامة. يبقى الانعطاف يسارًا إلزاميًا في جميع الأحوال.',
  sq.explanation_en = 'The absence of traffic does not change the effect of this sign. Turning left remains mandatory in all circumstances.',
  sq.explanation_nl = 'Het ontbreken van verkeer verandert de werking van dit bord niet. Links afslaan blijft in alle omstandigheden verplicht.',
  sq.explanation_fr = 'L''absence de circulation ne change pas l''effet de ce panneau. Tourner à gauche reste obligatoire dans tous les cas.'
WHERE rs.sign_code = 'D1b-links' AND sq.question_ref = 'D1b-links_Q07';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'لا، هذه العلامة تفرض الانعطاف يسارًا بغض النظر عن حجم المرور',
  sc.text_en = 'No, this sign requires you to turn left regardless of the amount of traffic',
  sc.text_nl = 'Nee, dit bord verplicht u links af te slaan ongeacht de verkeersdrukte',
  sc.text_fr = 'Non, ce panneau vous oblige à tourner à gauche quelle que soit l''intensité de la circulation',
  sc.is_correct = 1
WHERE rs.sign_code = 'D1b-links' AND sq.question_ref = 'D1b-links_Q07' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'نعم، إذا لم توجد حركة مرور يمكنك متابعة السير إلى الأمام إذا كان ذلك أسهل',
  sc.text_en = 'Yes, if there is no traffic you may continue straight ahead if that is easier',
  sc.text_nl = 'Ja, als er geen verkeer is mag u rechtdoor rijden als dat gemakkelijker is',
  sc.text_fr = 'Oui, s''il n''y a pas de circulation vous pouvez continuer tout droit si cela est plus simple',
  sc.is_correct = 0
WHERE rs.sign_code = 'D1b-links' AND sq.question_ref = 'D1b-links_Q07' AND sc.display_order = 2;

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sq.question_ar = 'أنت تقود على طريق توجد عليه هذه العلامة المرورية، لكن الطريق الأيسر شديد الانحدار بالنسبة إلى مركبتك. هل يمكنك الانعطاف يمينًا؟',
  sq.question_en = 'You are driving on a road where this traffic sign is posted, but the road to the left is too steep for your vehicle. May you turn right?',
  sq.question_nl = 'U rijdt op een weg waar dit verkeersbord staat, maar de weg links is te steil voor uw voertuig. Mag u rechts afslaan?',
  sq.question_fr = 'Vous roulez sur une route où ce panneau de signalisation est placé, mais la route à gauche est trop pentue pour votre véhicule. Pouvez-vous tourner à droite ?',
  sq.explanation_ar = 'صعوبة الطريق أو شدة الانحدار لا تُشكّل استثناءً من هذه العلامة. يجب عليك البحث عن طريق بديل لا يتعارض مع الالتزام المفروض.',
  sq.explanation_en = 'Difficult road conditions or a steep gradient do not create an exception to this sign. You must look for an alternative route that does not conflict with the obligation.',
  sq.explanation_nl = 'Moeilijke wegomstandigheden of een steile helling vormen geen uitzondering op dit bord. U moet een alternatieve route zoeken die niet in strijd is met deze verplichting.',
  sq.explanation_fr = 'Des conditions routières difficiles ou une forte pente ne constituent pas une exception à ce panneau. Vous devez chercher un itinéraire alternatif qui ne soit pas contraire à l''obligation imposée.'
WHERE rs.sign_code = 'D1b-links' AND sq.question_ref = 'D1b-links_Q08';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'لا، شدة الانحدار ليست استثناءً، ويجب البحث عن طريق بديل',
  sc.text_en = 'No, the steep slope is not an exception and you must find an alternative route',
  sc.text_nl = 'Nee, de steile helling is geen uitzondering en u moet een alternatieve route zoeken',
  sc.text_fr = 'Non, une pente trop forte n''est pas une exception et vous devez chercher un itinéraire alternatif',
  sc.is_correct = 1
WHERE rs.sign_code = 'D1b-links' AND sq.question_ref = 'D1b-links_Q08' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'نعم، يمكن مخالفة العلامة إذا كانت حالة الطريق غير مناسبة',
  sc.text_en = 'Yes, you may ignore the sign if the road conditions are unsuitable',
  sc.text_nl = 'Ja, u mag het bord negeren als de wegomstandigheden ongeschikt zijn',
  sc.text_fr = 'Oui, vous pouvez ignorer le panneau si l''état de la route n''est pas adapté',
  sc.is_correct = 0
WHERE rs.sign_code = 'D1b-links' AND sq.question_ref = 'D1b-links_Q08' AND sc.display_order = 2;
