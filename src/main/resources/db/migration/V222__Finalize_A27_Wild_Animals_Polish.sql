UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.explanation_ar = 'تشير هذه العلامة المرورية إلى احتمال عبور حيوانات برية كبيرة مثل الغزلان والخنازير البرية للطريق. هذه الحيوانات غير متوقعة وقد تظهر فجأة أمام مركبتك.',
    q.explanation_en = 'This traffic sign warns of large wild animals such as deer or wild boar that may cross the road. These animals are unpredictable and may suddenly appear in front of your vehicle.',
    q.explanation_nl = 'Dit verkeersbord waarschuwt voor groot wild zoals herten of everzwijnen dat de rijbaan kan oversteken. Deze dieren zijn onvoorspelbaar en kunnen plots voor uw voertuig verschijnen.',
    q.explanation_fr = 'Ce panneau avertit de la présence de grand gibier, comme des cerfs ou des sangliers, pouvant traverser la chaussée. Ces animaux sont imprévisibles et peuvent surgir soudainement devant votre véhicule.'
WHERE rs.sign_code = 'A27' AND q.question_ref = 'A27_Q01';

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.explanation_ar = 'تنتمي هذه العلامة المرورية إلى علامات الخطر. وهي مثلثة الشكل، ذات حافة حمراء وخلفية بيضاء.',
    q.explanation_en = 'This traffic sign belongs to the danger signs. It is triangular, with a red border and a white background.',
    q.explanation_nl = 'Dit verkeersbord behoort tot de gevaarsborden. Het is driehoekig, met een rode rand en een witte achtergrond.',
    q.explanation_fr = 'Ce panneau appartient aux panneaux de danger. Il est triangulaire, avec une bordure rouge et un fond blanc.'
WHERE rs.sign_code = 'A27' AND q.question_ref = 'A27_Q02';

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.question_ar = 'ماذا يجب عليك فعله عند رؤية هذه العلامة المرورية، خاصة عند الغسق أو في الليل؟',
    q.question_en = 'What must you do when you see this sign, especially at dusk or at night?',
    q.question_nl = 'Wat moet u doen wanneer u dit verkeersbord ziet, vooral bij schemering of ''s nachts?',
    q.question_fr = 'Que devez-vous faire lorsque vous voyez ce panneau, surtout au crépuscule ou la nuit ?',
    q.explanation_ar = 'في هذه المنطقة يجب عليك تخفيف السرعة بشكل ملحوظ والحفاظ على درجة عالية من الانتباه. يزداد الخطر عند الغسق وفي الليل، وقد تساعد الأضواء العالية على رؤية الحيوانات في الوقت المناسب عندما يكون استخدامها مسموحًا.',
    q.explanation_en = 'In this area, you must significantly reduce speed and remain highly alert. The risk is greatest at dusk and at night, and full beam can help you spot animals in time when it is safe to use it.',
    q.explanation_nl = 'In deze zone moet u uw snelheid aanzienlijk verminderen en extra waakzaam blijven. Het risico is het grootst bij schemering en ''s nachts, en grootlicht kan helpen om dieren tijdig op te merken wanneer het veilig is om het te gebruiken.',
    q.explanation_fr = 'Dans cette zone, vous devez réduire nettement votre vitesse et rester très vigilant. Le risque est maximal au crépuscule et la nuit, et les pleins phares peuvent aider à repérer les animaux à temps lorsqu''il est sûr de les utiliser.'
WHERE rs.sign_code = 'A27' AND q.question_ref = 'A27_Q04';

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.explanation_ar = 'بعد الاصطدام بحيوان بري، يجب التوقف وتأمين الطريق وإبلاغ الشرطة والجهة المحلية المختصة بأضرار الحياة البرية. لا تحاول أبدًا نقل حيوان كبير مصاب بنفسك، لأنه قد يتصرف بشكل خطير.',
    q.explanation_en = 'After a collision with wildlife, you must stop, secure the road and notify the police and the local wildlife damage authority. Never move a large injured animal yourself, because it may react dangerously.',
    q.explanation_nl = 'Na een aanrijding met wild moet u stoppen, de weg beveiligen en de politie en eventueel de lokale dienst voor wildschade verwittigen. Verplaats een groot gewond dier nooit zelf, want het kan gevaarlijk reageren.',
    q.explanation_fr = 'Après une collision avec du gibier, vous devez vous arrêter, sécuriser la route et avertir la police ainsi que l''instance locale compétente pour les dégâts causés par la faune. Ne déplacez jamais vous-même un grand animal blessé, car il peut réagir dangereusement.'
WHERE rs.sign_code = 'A27' AND q.question_ref = 'A27_Q05';

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.question_ar = 'ما الفرق بين علامة عبور الحيوانات البرية الكبيرة وعلامة عبور الماشية؟',
    q.question_en = 'What is the difference between a wild animals crossing sign and a livestock crossing sign?',
    q.question_nl = 'Wat is het verschil tussen een waarschuwingsbord voor overstekend groot wild en een waarschuwingsbord voor overstekend vee?',
    q.question_fr = 'Quelle est la différence entre un panneau de passage de grand gibier et un panneau de passage de bétail ?',
    q.explanation_ar = 'علامة عبور الحيوانات البرية الكبيرة تتعلق بحيوانات غير مستأنسة في المناطق الحرجية والريفية، وهي غير متوقعة. أما علامة عبور الماشية فتتعلق بحيوانات مزرعة مستأنسة تُقاد عادة عبر الطريق من قبل شخص مرافق.',
    q.explanation_en = 'A wild animals crossing sign relates to untamed animals in forest and rural areas, which are unpredictable. A livestock crossing sign concerns domesticated farm animals that are usually guided across the road by an attendant.',
    q.explanation_nl = 'Een waarschuwingsbord voor overstekend groot wild heeft betrekking op ongetemde dieren in bos- en landelijke gebieden, die onvoorspelbaar zijn. Een waarschuwingsbord voor overstekend vee gaat over gedomesticeerde landbouwdieren die meestal onder begeleiding de weg oversteken.',
    q.explanation_fr = 'Le panneau de passage de grand gibier concerne des animaux non domestiqués présents en zone forestière ou rurale, et donc imprévisibles. Le panneau de passage de bétail concerne des animaux d''élevage domestiqués, généralement conduits à travers la route par un accompagnateur.'
WHERE rs.sign_code = 'A27' AND q.question_ref = 'A27_Q06';

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 1,
    c.text_ar = 'علامة عبور الحيوانات البرية الكبيرة تحذّر من حيوانات غير مستأنسة مثل الغزلان والخنازير البرية؛ وعلامة عبور الماشية تحذّر من حيوانات المزرعة التي تكون غالبًا برفقة راعٍ',
    c.text_en = 'A wild animals crossing sign warns of untamed animals such as deer or wild boar; a livestock crossing sign warns of farm animals usually guided by a handler',
    c.text_nl = 'Een bord voor overstekend groot wild waarschuwt voor ongetemde dieren zoals herten of everzwijnen; een bord voor overstekend vee waarschuwt voor landbouwdieren die meestal door een begeleider worden geleid',
    c.text_fr = 'Un panneau de passage de grand gibier avertit de la présence d''animaux non domestiqués comme des cerfs ou des sangliers ; un panneau de passage de bétail avertit de la présence d''animaux d''élevage généralement guidés par un accompagnateur'
WHERE rs.sign_code = 'A27' AND q.question_ref = 'A27_Q06' AND c.display_order = 1;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 0,
    c.text_ar = 'هما متطابقتان وتشيران ببساطة إلى حيوانات تعبر الطريق',
    c.text_en = 'They are identical and both simply indicate animals crossing the road',
    c.text_nl = 'Ze zijn identiek en duiden allebei simpelweg op dieren die de weg oversteken',
    c.text_fr = 'Ils sont identiques et signalent simplement des animaux qui traversent la route'
WHERE rs.sign_code = 'A27' AND q.question_ref = 'A27_Q06' AND c.display_order = 2;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 0,
    c.text_ar = 'علامة عبور الحيوانات البرية الكبيرة تمنع المرور تمامًا، بينما تسمح علامة عبور الماشية بالمرور البطيء',
    c.text_en = 'A wild animals crossing sign completely forbids passage, while a livestock crossing sign allows slow driving through',
    c.text_nl = 'Een bord voor overstekend groot wild verbiedt alle doorgang, terwijl een bord voor overstekend vee langzaam doorrijden toestaat',
    c.text_fr = 'Le panneau de passage de grand gibier interdit totalement le passage, tandis que le panneau de passage de bétail autorise un passage lent'
WHERE rs.sign_code = 'A27' AND q.question_ref = 'A27_Q06' AND c.display_order = 3;

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.question_ar = 'تمر بهذه العلامة المرورية لكن لا ترى أي حيوانات. هل ما زلت ملزمًا بتخفيف سرعتك؟',
    q.question_en = 'You pass this sign but see no animals. Are you still required to reduce your speed?',
    q.question_nl = 'U passeert dit verkeersbord maar ziet geen dieren. Bent u nog steeds verplicht uw snelheid te verminderen?',
    q.question_fr = 'Vous passez ce panneau mais ne voyez aucun animal. Êtes-vous encore obligé de réduire votre vitesse ?',
    q.explanation_ar = 'هذا التحذير دائم. قد تختبئ الحيوانات في الغطاء النباتي على جانب الطريق وتندفع إلى الطريق في أي لحظة. تخفيف السرعة هو الإجراء الوحيد الذي يمنحك وقتًا كافيًا لرد الفعل.',
    q.explanation_en = 'This warning remains in force throughout the area. Animals can hide in vegetation beside the road and dart out at any moment. A reduced speed is the only measure that gives you enough time to react.',
    q.explanation_nl = 'Deze waarschuwing blijft in de hele zone gelden. Dieren kunnen zich verschuilen in begroeiing naast de weg en op elk moment plots oversteken. Een lagere snelheid is de enige maatregel die u voldoende reactietijd geeft.',
    q.explanation_fr = 'Cet avertissement reste valable dans toute la zone. Des animaux peuvent se cacher dans la végétation au bord de la route et surgir à tout moment. Une vitesse réduite est la seule mesure qui vous laisse suffisamment de temps pour réagir.'
WHERE rs.sign_code = 'A27' AND q.question_ref = 'A27_Q07';

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.question_ar = 'هل يُسمح بالقيادة بالسرعة القصوى ليلاً في منطقة تشير إليها هذه العلامة المرورية؟',
    q.question_en = 'Is it allowed to drive at full speed at night in an area marked by this sign?',
    q.question_nl = 'Mag u ''s nachts met de maximumsnelheid rijden in een zone die door dit verkeersbord wordt aangeduid?',
    q.question_fr = 'Est-il permis de rouler à pleine vitesse la nuit dans une zone signalée par ce panneau ?',
    q.explanation_ar = 'تكون الحيوانات البرية الكبيرة أكثر نشاطًا عند الغسق وفي الليل. يجب عليك دائمًا تكييف سرعتك مع الظروف، ويجب ألا تتجاوز مسافة التوقف ليلاً مدى الرؤية المتاح لك.',
    q.explanation_en = 'Large wild animals are most active at dusk and at night. You must always adapt your speed to the conditions, and at night your stopping distance must never exceed what you can see.',
    q.explanation_nl = 'Groot wild is het actiefst bij schemering en ''s nachts. U moet uw snelheid altijd aanpassen aan de omstandigheden, en ''s nachts mag uw stopafstand nooit groter zijn dan uw zichtafstand.',
    q.explanation_fr = 'Le grand gibier est le plus actif au crépuscule et la nuit. Vous devez toujours adapter votre vitesse aux conditions, et la nuit votre distance d''arrêt ne doit jamais dépasser votre distance de visibilité.'
WHERE rs.sign_code = 'A27' AND q.question_ref = 'A27_Q08';
