-- Deep polish B21 learner-facing content.
-- Removes raw sign-code mentions from displayed text, fixes source/DB drift
-- in Arabic, and keeps persisted question banks aligned with the cleaned
-- source JSON.

UPDATE sign_questions
SET question_ar = 'ما معنى هذه العلامة المرورية؟',
    question_en = 'What does this traffic sign mean?',
    question_nl = 'Wat betekent dit verkeersbord?',
    question_fr = 'Que signifie ce panneau de signalisation ?',
    explanation_ar = 'تشير هذه العلامة المرورية الى ان لديك الاولوية عند ممر ضيق. يجب على حركة المرور المقابلة الانتظار واعطاؤك الاولوية.',
    explanation_en = 'This sign indicates that you have priority at a narrow passage. Oncoming traffic must wait and give way to you.',
    explanation_nl = 'Dit bord geeft aan dat u voorrang heeft bij een smalle doorgang. Het tegemoetkomend verkeer moet wachten en u voorrang verlenen.',
    explanation_fr = 'Ce panneau indique que vous avez la priorite dans un passage etroit. Le trafic venant en sens inverse doit attendre et vous ceder le passage.'
WHERE question_ref = 'B21_Q01';

UPDATE sign_questions
SET question_ar = 'اي علامة مرورية تدل على ان لك الاولوية على حركة المرور المقابلة عند ممر ضيق؟',
    question_en = 'Which sign indicates that at a narrow passage you have priority over oncoming traffic?',
    question_nl = 'Welk bord geeft aan dat u bij een smalle doorgang voorrang heeft op het tegemoetkomend verkeer?',
    question_fr = 'Quel panneau indique qu au passage etroit vous avez la priorite sur le trafic venant en sens inverse ?',
    explanation_ar = 'العلامة الصحيحة تمنحك الاولوية عند ممر ضيق. اما العلامة الاخرى الخاصة بالممر الضيق فتلزمك بالانتظار. وعلامة الطريق ذي الاولوية تنطبق على طريق كامل لا على ممر واحد.',
    explanation_en = 'The correct sign gives you priority at a narrow passage. The other narrow-passage sign requires you to wait instead. The priority-road sign applies to an entire road, not a single passage.',
    explanation_nl = 'Het juiste bord geeft u voorrang bij een smalle doorgang. Het andere smalle-doorgangsbord verplicht u juist te wachten. Het bord voor een voorrangsweg geldt voor een hele weg, niet voor een enkele doorgang.',
    explanation_fr = 'Le bon panneau vous donne la priorite dans un passage etroit. L autre panneau de passage etroit vous oblige au contraire a attendre. Le panneau de route prioritaire s applique a une route entiere, pas a un seul passage.'
WHERE question_ref = 'B21_Q02';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'العلامة التي تدل على ان لك الاولوية على حركة المرور المقابلة عند ممر ضيق',
    sc.text_en = 'The sign that tells you that you have priority over oncoming traffic at a narrow passage',
    sc.text_nl = 'Het bord dat aangeeft dat u bij een smalle doorgang voorrang heeft op het tegemoetkomend verkeer',
    sc.text_fr = 'Le panneau qui indique que vous avez la priorite sur le trafic venant en sens inverse dans un passage etroit',
    sc.is_correct = 1
WHERE sq.question_ref = 'B21_Q02' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'العلامة التي تدل على وجوب اعطاء الاولوية لحركة المرور المقابلة عند ممر ضيق',
    sc.text_en = 'The sign that tells you to give way to oncoming traffic at a narrow passage',
    sc.text_nl = 'Het bord dat aangeeft dat u bij een smalle doorgang voorrang moet verlenen aan het tegemoetkomend verkeer',
    sc.text_fr = 'Le panneau qui indique que vous devez ceder le passage au trafic venant en sens inverse dans un passage etroit',
    sc.is_correct = 0
WHERE sq.question_ref = 'B21_Q02' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'العلامة التي تدل على ان طريقا كاملا هو طريق ذو اولوية',
    sc.text_en = 'The sign that indicates an entire road is a priority road',
    sc.text_nl = 'Het bord dat aangeeft dat een hele weg een voorrangsweg is',
    sc.text_fr = 'Le panneau qui indique qu une route entiere est prioritaire',
    sc.is_correct = 0
WHERE sq.question_ref = 'B21_Q02' AND sc.display_order = 3;

UPDATE sign_questions
SET question_ar = 'ما هو الخطر الذي يبقى قائما رغم الاولوية التي تمنحها لك هذه العلامة المرورية؟',
    question_en = 'What hazard remains present despite the priority given by this sign?',
    question_nl = 'Welk gevaar blijft aanwezig ondanks de voorrang die dit bord u geeft?',
    question_fr = 'Quel danger subsiste malgre la priorite que ce panneau vous donne ?',
    explanation_ar = 'حتى مع هذه العلامة المرورية يجب ان تبقى يقظا. قد يتجاهل سائق مقابل اولويتك او يسيء تقدير الموقف ويدخل الممر على اي حال.',
    explanation_en = 'Even with this sign you must remain alert. An oncoming driver may ignore your priority or misjudge the situation and enter the passage anyway.',
    explanation_nl = 'Ook met dit bord moet u alert blijven. Een tegenligger kan uw voorrang negeren of de situatie verkeerd inschatten en de doorgang toch inrijden.',
    explanation_fr = 'Meme avec ce panneau, vous devez rester vigilant. Un conducteur venant en sens inverse peut ignorer votre priorite ou mal evaluer la situation et s engager quand meme dans le passage.'
WHERE question_ref = 'B21_Q03';

UPDATE sign_questions
SET question_ar = 'انت تقترب من ممر ضيق توجد عنده هذه العلامة المرورية وتريد الدخول فيه. ماذا تفعل؟',
    question_en = 'You are approaching a narrow passage with this sign and want to enter it. What do you do?',
    question_nl = 'U nadert een smalle doorgang met dit bord en wilt die doorgang inrijden. Wat doet u?',
    question_fr = 'Vous approchez d un passage etroit avec ce panneau et voulez vous y engager. Que faites-vous ?',
    explanation_ar = 'مع هذه العلامة المرورية لديك الاولوية. يمكنك دخول الممر؛ السائقون المقابلون ملزمون بالانتظار. ابق يقظا للسائقين الذين لا يحترمون اولويتك.',
    explanation_en = 'With this sign you have priority. You may enter the passage; oncoming drivers are required to wait. However, stay alert for drivers who do not respect your priority.',
    explanation_nl = 'Met dit bord heeft u voorrang. U mag de doorgang inrijden; tegenliggers zijn verplicht te wachten. Wees wel alert voor bestuurders die uw voorrang niet respecteren.',
    explanation_fr = 'Avec ce panneau, vous avez la priorite. Vous pouvez vous engager dans le passage ; les conducteurs venant en sens inverse sont obliges d attendre. Restez toutefois vigilant face aux conducteurs qui ne respectent pas votre priorite.'
WHERE question_ref = 'B21_Q04';

UPDATE sign_questions
SET question_ar = 'انت داخل الممر الضيق الذي تمنحك فيه هذه العلامة المرورية الاولوية. في المنتصف ترى مركبة مقابلة دخلت الممر ايضا. ماذا تفعل؟',
    question_en = 'You are in the narrow passage where this sign gives you priority. Halfway through you see an oncoming vehicle that also entered the passage. What do you do?',
    question_nl = 'U rijdt in de smalle doorgang waarvoor dit bord u voorrang geeft. Halverwege ziet u een tegenligger die ook de doorgang is ingereden. Wat doet u?',
    question_fr = 'Vous etes dans le passage etroit ou ce panneau vous donne la priorite. A mi-chemin, vous voyez un vehicule venant en sens inverse qui s est egalement engage dans le passage. Que faites-vous ?',
    explanation_ar = 'حتى لو كنت صاحب الاولوية يجب تجنب الاصطدام. تتوقف وترسل اشارة للسائق المقابل الذي انتهك حق اولويتك وتنتظر حتى يتراجع.',
    explanation_en = 'Even though you have priority, you must avoid a collision. You stop, signal the oncoming driver who violated your right of way and wait until they reverse.',
    explanation_nl = 'Ook al heeft u voorrang, u moet een botsing vermijden. U stopt, geeft een signaal aan de tegenligger die uw voorrang heeft geschonden en wacht tot hij achteruitgezet heeft.',
    explanation_fr = 'Meme si vous avez la priorite, vous devez eviter une collision. Vous vous arretez, signalez au conducteur venant en sens inverse qu il a viole votre droit de priorite et attendez qu il fasse marche arriere.'
WHERE question_ref = 'B21_Q05';

UPDATE sign_questions
SET question_ar = 'توجد هذه العلامة المرورية على جانبك من الممر الضيق. ماذا يجب على حركة المرور المقابلة عند الجانب الاخر من الممر فعله؟',
    question_en = 'This sign is on your side of the narrow passage. What must oncoming traffic on the other side of the passage do?',
    question_nl = 'Dit bord staat aan uw kant van de smalle doorgang. Wat moet het tegemoetkomend verkeer aan de andere kant van de doorgang doen?',
    question_fr = 'Ce panneau se trouve de votre cote du passage etroit. Que doit faire le trafic venant en sens inverse de l autre cote du passage ?',
    explanation_ar = 'عند الطرف الاخر للممر الضيق توجد العلامة الاخرى الخاصة بالممر الضيق، وهي التي تلزم السائقين بالانتظار واعطاء الاولوية. يجب على اولئك السائقين الانتظار خارج الممر حتى تمر بالكامل.',
    explanation_en = 'At the other end of the narrow passage stands the other narrow-passage sign, the one that requires drivers to wait and give way. Those drivers must wait outside the passage until you have fully passed through.',
    explanation_nl = 'Aan het andere uiteinde van de smalle doorgang staat het andere smalle-doorgangsbord, het bord dat bestuurders verplicht te wachten en voorrang te verlenen. Die bestuurders moeten buiten de doorgang wachten tot u volledig gepasseerd bent.',
    explanation_fr = 'A l autre extremite du passage etroit se trouve l autre panneau de passage etroit, celui qui oblige les conducteurs a attendre et a ceder le passage. Ces conducteurs doivent attendre hors du passage jusqu a ce que vous soyez completement passe.'
WHERE question_ref = 'B21_Q06';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'الانتظار خارج الممر حتى تمر بالكامل لان العلامة الموجودة على جانبهم تلزمهم باعطاء الاولوية',
    sc.text_en = 'Wait outside the passage until you have fully passed, because the sign on their side requires them to give way',
    sc.text_nl = 'Buiten de doorgang wachten tot u volledig gepasseerd bent, omdat het bord aan hun kant hen verplicht voorrang te verlenen',
    sc.text_fr = 'Attendre hors du passage jusqu a ce que vous soyez completement passe, car le panneau de leur cote leur impose de ceder le passage',
    sc.is_correct = 1
WHERE sq.question_ref = 'B21_Q06' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'الدخول ببطء الى الممر وتركك تقرر من يجب عليه الانحراف',
    sc.text_en = 'Slowly enter the passage and let you decide who should swerve',
    sc.text_nl = 'Langzaam de doorgang inrijden en u laten beslissen wie uitwijkt',
    sc.text_fr = 'S engager lentement dans le passage et vous laisser decider qui doit s ecarter',
    sc.is_correct = 0
WHERE sq.question_ref = 'B21_Q06' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'دخول الممر في نفس الوقت الذي تدخل فيه مع البقاء على الجانب الايمن',
    sc.text_en = 'Enter the passage at the same time as you and stay on the right side',
    sc.text_nl = 'Tegelijk met u de doorgang inrijden en rechts houden',
    sc.text_fr = 'S engager dans le passage en meme temps que vous et rester du cote droit',
    sc.is_correct = 0
WHERE sq.question_ref = 'B21_Q06' AND sc.display_order = 3;

UPDATE sign_questions
SET question_ar = 'انت تقترب من الممر الضيق مع هذه العلامة المرورية. يقف احد المشاة على الرصيف بجانب الممر. هل يجوز لك دخول الممر دون انتظار؟',
    question_en = 'You are approaching the narrow passage with this sign. A pedestrian is standing on the pavement next to the passage. May you enter the passage without waiting?',
    question_nl = 'U nadert de smalle doorgang met dit bord. Er staat een voetganger op het trottoir naast de doorgang. Mag u de doorgang inrijden zonder te wachten?',
    question_fr = 'Vous approchez du passage etroit avec ce panneau. Un pieton se tient sur le trottoir a cote du passage. Pouvez-vous entrer dans le passage sans attendre ?',
    explanation_ar = 'هذه العلامة المرورية تنظم الاولوية على المركبات ذات المحرك المقابلة في الطريق. وجود احد المشاة على الرصيف لا يعيق الممر ولا يستوجب الانتظار.',
    explanation_en = 'This sign governs priority over motorised oncoming traffic in the roadway. A pedestrian waiting on the pavement does not obstruct the passage and does not require a waiting turn.',
    explanation_nl = 'Dit bord regelt de voorrang op gemotoriseerde tegenliggers in de rijbaan. Een voetganger die op het trottoir wacht, belemmert de doorgang niet en vereist geen wachtbeurt.',
    explanation_fr = 'Ce panneau regit la priorite sur les vehicules motorises venant en sens inverse dans la chaussee. Un pieton qui attend sur le trottoir n obstrue pas le passage et ne necessite pas d attente.'
WHERE question_ref = 'B21_Q07';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'نعم، هذه العلامة المرورية تمنحك الاولوية على المركبات المقابلة في الطريق؛ وجود احد المشاة على الرصيف لا يعيق الممر',
    sc.text_en = 'Yes, this sign gives you priority over oncoming vehicles in the roadway; a pedestrian on the pavement does not obstruct the passage',
    sc.text_nl = 'Ja, dit bord geeft u voorrang op tegenliggers in de rijbaan; een voetganger op het trottoir belemmert de doorgang niet',
    sc.text_fr = 'Oui, ce panneau vous donne la priorite sur les vehicules venant en sens inverse dans la chaussee ; un pieton sur le trottoir n obstrue pas le passage',
    sc.is_correct = 1
WHERE sq.question_ref = 'B21_Q07' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'لا، يجب الانتظار حتى يخلو جميع مستخدمي الطريق بما فيهم المشاة',
    sc.text_en = 'No, you must wait until all road users including pedestrians are completely clear',
    sc.text_nl = 'Nee, u moet wachten totdat alle weggebruikers inclusief voetgangers volledig weg zijn',
    sc.text_fr = 'Non, vous devez attendre que tous les usagers de la route y compris les pietons soient completement degages',
    sc.is_correct = 0
WHERE sq.question_ref = 'B21_Q07' AND sc.display_order = 2;

UPDATE sign_questions
SET question_ar = 'تعطيك هذه العلامة المرورية الاولوية وتدخل الممر الضيق. لكن السائق المقابل يدخل الممر ايضا ويرفض التوقف. هل يجوز لك الاستمرار واجباره على التوقف؟',
    question_en = 'This sign gives you priority and you enter the narrow passage. However, the oncoming driver also enters the passage and refuses to stop. May you continue and force them to stop?',
    question_nl = 'Dit bord geeft u voorrang en u rijdt de smalle doorgang in. De tegenligger rijdt echter ook de doorgang in en weigert te stoppen. Mag u doorrijden en hem forceren te stoppen?',
    question_fr = 'Ce panneau vous donne la priorite et vous vous engagez dans le passage etroit. Cependant, le conducteur venant en sens inverse s engage aussi dans le passage et refuse de s arreter. Pouvez-vous continuer et le forcer a s arreter ?',
    explanation_ar = 'امتلاك الحق وتعريض نفسك لخطر الاصطدام امران مختلفان. يجب دائما تجنب الاصطدام حتى لو كان ذلك يعني التوقف بينما انت على حق قانونيا.',
    explanation_en = 'Being in the right and risking a collision are two different things. You must always avoid a collision, even if that means stopping while you are legally correct.',
    explanation_nl = 'Het recht hebben en een botsing riskeren zijn twee verschillende dingen. U moet altijd een aanrijding vermijden, zelfs als dat betekent dat u stopt terwijl u in uw recht staat.',
    explanation_fr = 'Avoir le droit et risquer une collision sont deux choses differentes. Vous devez toujours eviter une collision, meme si cela signifie vous arreter alors que vous etes dans votre droit.'
WHERE question_ref = 'B21_Q08';