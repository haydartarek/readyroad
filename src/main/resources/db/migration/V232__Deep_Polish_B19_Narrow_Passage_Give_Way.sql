-- Deep polish B19 learner-facing content.
-- Removes raw sign-code mentions from displayed text while keeping persisted
-- question banks aligned with the cleaned source JSON.

UPDATE sign_questions
SET explanation_ar = 'تشير هذه العلامة المرورية الى ممر ضيق يجب فيه اعطاء الاولوية للمركبات المقابلة. تنتظر حتى يخلو الممر.',
    explanation_en = 'This sign indicates a narrow passage where you must give way to oncoming traffic. You wait until the passage is clear.',
    explanation_nl = 'Dit bord geeft aan dat er een smalle doorgang is waarbij u voorrang moet verlenen aan het tegemoetkomend verkeer. U wacht totdat de doorgang vrij is.',
    explanation_fr = 'Ce panneau indique un passage etroit ou vous devez ceder le passage au trafic venant en sens inverse. Vous attendez que le passage soit libre.'
WHERE question_ref = 'B19_Q01';

UPDATE sign_questions
SET question_ar = 'اي علامة مرورية تدل على وجوب اعطاء الاولوية لحركة المرور المقابلة عند ممر ضيق؟',
    question_en = 'Which sign indicates that at a narrow passage you must give way to oncoming traffic?',
    question_nl = 'Welk bord geeft aan dat u bij een smalle doorgang voorrang moet verlenen aan het tegemoetkomend verkeer?',
    question_fr = 'Quel panneau indique qu au passage etroit vous devez ceder le passage au trafic venant en sens inverse ?',
    explanation_ar = 'العلامة الصحيحة تلزمك باعطاء الاولوية عند ممر ضيق. اما العلامة الاخرى الخاصة بالممر الضيق فتعني ان لديك الاولوية. والعلامة العامة لاعطاء الاولوية تنطبق على التقاطعات لا على الممرات الضيقة.',
    explanation_en = 'The correct sign requires you to give way at a narrow passage. The other narrow-passage sign gives you priority instead. The general give-way sign applies at junctions, not at narrow passages.',
    explanation_nl = 'Het juiste bord verplicht u voorrang te verlenen bij een smalle doorgang. Het andere smalle-doorgangsbord geeft juist aan dat u voorrang heeft. Het algemene voorrangsbord geldt op kruispunten, niet bij smalle doorgangen.',
    explanation_fr = 'Le bon panneau vous oblige a ceder le passage dans un passage etroit. L autre panneau de passage etroit indique au contraire que vous avez la priorite. Le panneau general de cedez le passage s applique aux carrefours, pas aux passages etroits.'
WHERE question_ref = 'B19_Q02';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'العلامة التي تدل على وجوب اعطاء الاولوية لحركة المرور المقابلة عند ممر ضيق',
    sc.text_en = 'The sign that tells you to give way to oncoming traffic at a narrow passage',
    sc.text_nl = 'Het bord dat aangeeft dat u bij een smalle doorgang voorrang moet verlenen aan tegemoetkomend verkeer',
    sc.text_fr = 'Le panneau qui indique que vous devez ceder le passage au trafic venant en sens inverse dans un passage etroit',
    sc.is_correct = 1
WHERE sq.question_ref = 'B19_Q02' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'العلامة التي تدل على ان لديك الاولوية على حركة المرور المقابلة عند ممر ضيق',
    sc.text_en = 'The sign that tells you that you have priority over oncoming traffic at a narrow passage',
    sc.text_nl = 'Het bord dat aangeeft dat u bij een smalle doorgang voorrang heeft op tegemoetkomend verkeer',
    sc.text_fr = 'Le panneau qui indique que vous avez la priorite sur le trafic venant en sens inverse dans un passage etroit',
    sc.is_correct = 0
WHERE sq.question_ref = 'B19_Q02' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'العلامة العامة التي تفرض اعطاء الاولوية عند التقاطعات',
    sc.text_en = 'The general give-way sign used at junctions',
    sc.text_nl = 'Het algemene bord dat voorrang verlenen op een kruispunt oplegt',
    sc.text_fr = 'Le panneau general de cedez le passage utilise aux carrefours',
    sc.is_correct = 0
WHERE sq.question_ref = 'B19_Q02' AND sc.display_order = 3;

UPDATE sign_questions
SET question_ar = 'ما هو الخطر الذي تشير إليه هذه العلامة المرورية؟',
    question_en = 'What hazard does this sign warn about?',
    question_nl = 'Op welk gevaar wijst dit bord?',
    question_fr = 'A quel danger ce panneau avertit-il ?',
    explanation_ar = 'تنبهك هذه العلامة المرورية الى ممر بعرض مركبة واحدة فقط. حركة المرور المقابلة موجودة بالفعل في الممر او لها الاولوية؛ يجب ان تنتظر.',
    explanation_en = 'This sign warns you about a passage only one vehicle wide. Oncoming traffic is already in the passage or has priority; you must wait.',
    explanation_nl = 'Dit bord waarschuwt voor een doorgang die slechts een voertuig breed is. Het tegemoetkomend verkeer is al in de doorgang of heeft er voorrang op; u moet wachten.',
    explanation_fr = 'Ce panneau avertit d un passage large d un seul vehicule. Le trafic venant en sens inverse est deja dans le passage ou a la priorite ; vous devez attendre.'
WHERE question_ref = 'B19_Q03';

UPDATE sign_questions
SET question_ar = 'ماذا يجب عليك فعله عند رؤية هذه العلامة المرورية ومركبة مقابلة موجودة بالفعل في الممر الضيق؟',
    question_en = 'What must you do when you see this sign and an oncoming vehicle is already in the narrow passage?',
    question_nl = 'Wat moet u doen als u dit bord ziet en een tegenligger al in de smalle doorgang rijdt?',
    question_fr = 'Que devez-vous faire lorsque vous voyez ce panneau et qu un vehicule venant en sens inverse est deja dans le passage etroit ?',
    explanation_ar = 'تلزمك هذه العلامة المرورية بالانتظار خارج الممر حتى تمر المركبة المقابلة بالكامل. الممر ضيق جدا لمركبتين.',
    explanation_en = 'This sign requires you to wait outside the passage until the oncoming vehicle has fully passed. The passage is too narrow for two vehicles.',
    explanation_nl = 'Dit bord verplicht u te wachten buiten de doorgang totdat het tegemoetkomende voertuig volledig voorbij is. De doorgang is te smal voor twee voertuigen.',
    explanation_fr = 'Ce panneau vous oblige a attendre hors du passage jusqu a ce que le vehicule venant en sens inverse soit completement passe. Le passage est trop etroit pour deux vehicules.'
WHERE question_ref = 'B19_Q04';

UPDATE sign_questions
SET question_ar = 'انت تقترب من ممر ضيق توجد عنده هذه العلامة المرورية. الممر فارغ حاليا. ماذا تفعل؟',
    question_en = 'You are approaching a narrow passage with this sign. The passage is currently empty. What do you do?',
    question_nl = 'U nadert een smalle doorgang met dit bord. De doorgang is momenteel leeg. Wat doet u?',
    question_fr = 'Vous approchez d un passage etroit avec ce panneau. Le passage est actuellement vide. Que faites-vous ?',
    explanation_ar = 'تلزمك هذه العلامة المرورية بالانتظار فقط عند وجود مركبة مقابلة. اذا كان الممر خاليا ولا تقترب مركبة مقابلة يمكنك المضي.',
    explanation_en = 'This sign only requires you to wait when oncoming traffic is present. If the passage is clear and no oncoming vehicle is approaching, you may drive through.',
    explanation_nl = 'Dit bord verplicht u enkel te wachten als er tegemoetkomend verkeer is. Als de doorgang vrij is en er geen tegenligger nadert, mag u doorrijden.',
    explanation_fr = 'Ce panneau ne vous oblige a attendre que lorsque du trafic venant en sens inverse est present. Si le passage est libre et qu aucun vehicule n approche, vous pouvez passer.'
WHERE question_ref = 'B19_Q05';

UPDATE sign_questions
SET question_ar = 'ما الفرق في السلوك المطلوب بين هذه العلامة المرورية وعلامة الممر الضيق الاخرى التي تمنحك الاولوية؟',
    question_en = 'What is the difference in required behaviour between this sign and the other narrow-passage sign that gives you priority?',
    question_nl = 'Wat is het verschil in gedragsregel tussen dit bord en het andere smalle-doorgangsbord dat u voorrang geeft?',
    question_fr = 'Quelle est la difference de comportement requis entre ce panneau et l autre panneau de passage etroit qui vous donne la priorite ?',
    explanation_ar = 'هذه العلامة المرورية تعني الانتظار واعطاء الاولوية للمرور المقابل. اما العلامة الاخرى الخاصة بالممر الضيق فتعني ان لك الاولوية ويجب على المرور المقابل الانتظار. توضع العلامتان كزوج عند طرفي الممر الضيق نفسه.',
    explanation_en = 'This sign means waiting and giving way to oncoming traffic. The other narrow-passage sign means that you have priority and that oncoming traffic must wait. The two signs are placed as a pair at the ends of the same narrow passage.',
    explanation_nl = 'Dit bord betekent wachten en voorrang verlenen aan tegenkomend verkeer. Het andere smalle-doorgangsbord betekent dat u voorrang heeft en het tegemoetkomend verkeer moet wachten. Beide borden staan als paar aan de uiteinden van dezelfde smalle doorgang.',
    explanation_fr = 'Ce panneau signifie attendre et ceder le passage au trafic venant en sens inverse. L autre panneau de passage etroit signifie que vous avez la priorite et que le trafic venant en sens inverse doit attendre. Les deux panneaux sont places en paire aux extremites du meme passage etroit.'
WHERE question_ref = 'B19_Q06';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'مع هذه العلامة يجب الانتظار واعطاء الاولوية للمركبات المقابلة؛ ومع العلامة الاخرى تكون لك الاولوية على المركبات المقابلة',
    sc.text_en = 'With this sign you must wait and give way to oncoming traffic; with the other sign you have priority over oncoming traffic',
    sc.text_nl = 'Bij dit bord moet u wachten en voorrang verlenen aan tegenliggers; bij het andere bord heeft u juist voorrang op tegenliggers',
    sc.text_fr = 'Avec ce panneau vous devez attendre et ceder le passage aux vehicules venant en sens inverse ; avec l autre vous avez la priorite sur eux',
    sc.is_correct = 1
WHERE sq.question_ref = 'B19_Q06' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'العلامتان متطابقتان: كلتاهما تلزمك بالانتظار للمركبات المقابلة',
    sc.text_en = 'The two signs are identical: both require you to wait for oncoming traffic',
    sc.text_nl = 'Beide borden zijn identiek: ze verplichten u allebei te wachten voor tegenliggers',
    sc.text_fr = 'Ces deux panneaux sont identiques : tous deux vous obligent a attendre les vehicules venant en sens inverse',
    sc.is_correct = 0
WHERE sq.question_ref = 'B19_Q06' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'العلامة الاخرى تستلزم التوقف الكامل؛ وهذه العلامة تستلزم فقط تخفيف السرعة',
    sc.text_en = 'The other sign requires a complete stop; this sign only requires reducing speed',
    sc.text_nl = 'Het andere bord verplicht tot stoppen; dit bord verplicht enkel tot vaart minderen',
    sc.text_fr = 'L autre panneau impose un arret complet ; celui-ci impose seulement de reduire la vitesse',
    sc.is_correct = 0
WHERE sq.question_ref = 'B19_Q06' AND sc.display_order = 3;

UPDATE sign_questions
SET question_ar = 'انت تنتظر امام ممر ضيق توجد عنده هذه العلامة المرورية. مركبة مقابلة موجودة في الممر. هل يجوز لك الدخول الى الممر لتسريع عبورها؟',
    question_en = 'You are waiting before a narrow passage with this sign. An oncoming vehicle is in the passage. May you enter the passage to let it through faster?',
    question_nl = 'U staat voor een smalle doorgang met dit bord. Er is een tegenligger in de doorgang. Mag u de doorgang inrijden om hem sneller door te laten?',
    question_fr = 'Vous attendez devant un passage etroit avec ce panneau. Un vehicule venant en sens inverse est dans le passage. Pouvez-vous entrer dans le passage pour le laisser passer plus vite ?',
    explanation_ar = 'تلزمك هذه العلامة المرورية بالانتظار خارج الممر حتى تخرج المركبة المقابلة منه بالكامل. الممر ضيق جدا لمركبتين جنبا الى جنب.',
    explanation_en = 'This sign requires you to wait outside the passage until the oncoming vehicle has fully exited. The passage is too narrow for two vehicles side by side.',
    explanation_nl = 'Dit bord verplicht u buiten de doorgang te wachten totdat de tegenligger er volledig uit is. De doorgang is te smal voor twee voertuigen naast elkaar.',
    explanation_fr = 'Ce panneau vous oblige a attendre hors du passage jusqu a ce que le vehicule venant en sens inverse en soit completement sorti. Le passage est trop etroit pour deux vehicules cote a cote.'
WHERE question_ref = 'B19_Q07';

UPDATE sign_questions
SET question_ar = 'هذه العلامة المرورية موجودة على جانبك من الممر الضيق. لا توجد علامة مرورية على الجانب الاخر. هل يعني ذلك ان حركة المرور المقابلة تكون لديها تلقائيا العلامة الاخرى الخاصة بالممر الضيق التي تمنحها الاولوية؟',
    question_en = 'This sign is on your side of the narrow passage. There is no sign on the other side. Does oncoming traffic then automatically have the companion narrow-passage sign that gives them priority?',
    question_nl = 'Dit bord staat op uw kant van de smalle doorgang. Aan de andere kant staat geen bord. Heeft het tegemoetkomend verkeer dan automatisch het andere smalle-doorgangsbord dat hen voorrang geeft?',
    question_fr = 'Ce panneau se trouve de votre cote du passage etroit. Il n y a pas de panneau de l autre cote. Le trafic venant en sens inverse a-t-il alors automatiquement l autre panneau de passage etroit qui lui donne la priorite ?',
    explanation_ar = 'تقترن هذه العلامة المرورية دائما بالعلامة الاخرى الخاصة بالممر الضيق عند الطرف الاخر للممر. وغياب تلك العلامة على الجانب الاخر يعد قصورا في الاشارات لا تغييرا للقاعدة.',
    explanation_en = 'This sign is always combined with the other narrow-passage sign at the opposite end of the passage. The absence of that sign on the far side is a signage deficiency, not a change to the rule.',
    explanation_nl = 'Dit bord wordt steeds gecombineerd met het andere smalle-doorgangsbord aan het andere uiteinde van de doorgang. Het ontbreken van dat bord aan de overkant is een tekort in de signalisatie, niet een wijziging van de regel.',
    explanation_fr = 'Ce panneau est toujours combine avec l autre panneau de passage etroit a l extremite opposee du passage. L absence de cet autre panneau est une deficience de signalisation, pas une modification de la regle.'
WHERE question_ref = 'B19_Q08';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'نعم، توضع علامتا الممر الضيق دائما كزوج: علامة تلزمك بالانتظار على جانب وعلامة تمنح الاتجاه الاخر الاولوية على الجانب الاخر',
    sc.text_en = 'Yes, these two narrow-passage signs are always placed as a pair: the sign that makes you wait on one side and the sign that gives the other direction priority on the other side',
    sc.text_nl = 'Ja, deze twee smalle-doorgangsborden worden altijd als paar geplaatst: het bord dat u laat wachten aan de ene kant en het bord dat de andere richting voorrang geeft aan de andere kant',
    sc.text_fr = 'Oui, ces deux panneaux de passage etroit sont toujours places en paire : le panneau qui vous fait attendre d un cote et celui qui donne la priorite a l autre direction de l autre cote',
    sc.is_correct = 1
WHERE sq.question_ref = 'B19_Q08' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'لا، من دون العلامة الاخرى على الجانب الاخر لا تسري قواعد اولوية خاصة عند ذلك الممر',
    sc.text_en = 'No, without the other sign on the far side, no special priority rules apply at that passage',
    sc.text_nl = 'Nee, zonder het andere bord aan de overkant gelden geen bijzondere voorrangsregels bij die doorgang',
    sc.text_fr = 'Non, sans l autre panneau a l extremite opposee, aucune regle de priorite speciale ne s applique a ce passage',
    sc.is_correct = 0
WHERE sq.question_ref = 'B19_Q08' AND sc.display_order = 2;