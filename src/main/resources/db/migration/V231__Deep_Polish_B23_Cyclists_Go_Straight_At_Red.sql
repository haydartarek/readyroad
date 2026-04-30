-- Deep polish B23 learner-facing content.
-- Removes raw sign-code mentions from displayed text while keeping persisted
-- question banks aligned with the cleaned source JSON.

UPDATE sign_questions
SET explanation_ar = 'توضع هذه العلامة المرورية عند التقاطعات ذات مسار الدراجات. يجوز لراكبي الدراجات وسبيد بيديليكس الاستمرار للامام عند الضوء الاحمر بشرط اعطاء الاولوية لمستخدمي الطريق الاخرين على الطريق المتقاطع.',
    explanation_en = 'This sign is placed at junctions with a cycle path. Cyclists and speed pedelecs may go straight at a red light, provided they give way to other road users on the crossing road.',
    explanation_nl = 'Dit bord staat bij kruispunten met een fietspad. Fietsers en speed pedelecs mogen rechtdoor rijden bij een rood licht, mits ze voorrang verlenen aan de overige weggebruikers op de kruisende weg.',
    explanation_fr = 'Ce panneau est place aux carrefours avec une piste cyclable. Les cyclistes et speed pedelecs peuvent aller tout droit au feu rouge, a condition de ceder le passage aux autres usagers sur la route transversale.'
WHERE question_ref = 'B23_Q01';

UPDATE sign_questions
SET question_ar = 'اي علامة مرورية تسمح لراكبي الدراجات بالاستمرار للامام عند الضوء الاحمر؟',
    question_en = 'Which sign gives cyclists permission to go straight at a red light?',
    question_nl = 'Welk bord geeft fietsers toestemming om rechtdoor te rijden bij een rood licht?',
    question_fr = 'Quel panneau donne aux cyclistes la permission d aller tout droit au feu rouge ?',
    explanation_ar = 'الجواب الصحيح هو العلامة التي تسمح لراكبي الدراجات وسبيد بيديليكس بالاستمرار للامام عند الضوء الاحمر. اما علامة الانعطاف يمينا عند الاحمر فهي استثناء مختلف، وعلامة الاتجاه الالزامي لا علاقة لها بالضوء الاحمر.',
    explanation_en = 'The correct choice is the sign that allows cyclists and speed pedelecs to go straight at a red light. The sign for turning right at red is a different exception, and a mandatory direction sign is unrelated to the red light.',
    explanation_nl = 'De juiste keuze is het bord dat fietsers en speed pedelecs toelaat rechtdoor te rijden bij rood licht. Het bord voor rechtsaf bij rood is een andere uitzondering, en een verplichte richting staat los van het rode licht.',
    explanation_fr = 'La bonne reponse est le panneau qui permet aux cyclistes et speed pedelecs d aller tout droit au feu rouge. Le panneau pour tourner a droite au rouge est une autre exception, et un panneau de direction obligatoire n a rien a voir avec le feu rouge.'
WHERE question_ref = 'B23_Q02';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'العلامة التي تسمح لراكبي الدراجات وسبيد بيديليكس بالاستمرار للامام عند الاحمر',
    sc.text_en = 'The sign that allows cyclists and speed pedelecs to go straight at a red light',
    sc.text_nl = 'Het bord dat fietsers en speed pedelecs toelaat rechtdoor te rijden bij rood licht',
    sc.text_fr = 'Le panneau qui permet aux cyclistes et speed pedelecs d aller tout droit au feu rouge',
    sc.is_correct = 1
WHERE sq.question_ref = 'B23_Q02' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'العلامة التي تسمح لراكبي الدراجات وسبيد بيديليكس بالانعطاف يمينا عند الاحمر',
    sc.text_en = 'The sign that allows cyclists and speed pedelecs to turn right at a red light',
    sc.text_nl = 'Het bord dat fietsers en speed pedelecs toelaat rechtsaf te slaan bij rood licht',
    sc.text_fr = 'Le panneau qui permet aux cyclistes et speed pedelecs de tourner a droite au feu rouge',
    sc.is_correct = 0
WHERE sq.question_ref = 'B23_Q02' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'العلامة التي تفرض الاستمرار للامام على جميع مستخدمي الطريق',
    sc.text_en = 'The sign that makes going straight mandatory for all traffic',
    sc.text_nl = 'Het bord dat rechtdoor rijden verplicht voor alle verkeer',
    sc.text_fr = 'Le panneau qui impose d aller tout droit a tous les usagers',
    sc.is_correct = 0
WHERE sq.question_ref = 'B23_Q02' AND sc.display_order = 3;

UPDATE sign_questions
SET question_ar = 'ما الخطر الذي يجب على راكب الدراجة الانتباه له مع هذه العلامة المرورية عند الاستمرار للامام وقت الاحمر؟',
    question_en = 'What hazard must a cyclist with this sign watch out for when going straight at red?',
    question_nl = 'Op welk gevaar moet een fietser met dit bord extra letten als hij rechtdoor rijdt bij rood?',
    question_fr = 'A quel danger un cycliste avec ce panneau doit-il faire particulierement attention lorsqu il va tout droit au rouge ?',
    explanation_ar = 'مع هذه العلامة المرورية يكون للمرور على الطريق المتقاطع غالبا ضوء اخضر. يجب على راكب الدراجة ان يدع هؤلاء المستخدمين يمرون قبل دخول التقاطع.',
    explanation_en = 'With this sign, traffic on the crossing road often has a green light. The cyclist must let those road users pass before entering the junction.',
    explanation_nl = 'Bij dit bord heeft het verkeer op de kruisende weg vaak groen licht. De fietser moet deze weggebruikers laten voorgaan voordat hij het kruispunt oprijdt.',
    explanation_fr = 'Avec ce panneau, le trafic sur la route transversale a souvent le feu vert. Le cycliste doit laisser passer ces usagers avant d entrer dans le carrefour.'
WHERE question_ref = 'B23_Q03';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'المركبات والمشاة من الطريق المتقاطع الذين لديهم ضوء اخضر ويعبرون المسار او ممر المشاة',
    sc.text_en = 'Vehicles and pedestrians from the crossing road who have a green light and are crossing the carriageway or zebra crossing',
    sc.text_nl = 'Voertuigen en voetgangers die vanuit de kruisende weg groen licht hebben en de rijbaan of het zebrapad oversteken',
    sc.text_fr = 'Vehicules et pietons venant de la route transversale qui ont le feu vert et traversent la chaussee ou le passage pieton',
    sc.is_correct = 1
WHERE sq.question_ref = 'B23_Q03' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'السيارات خلفه التي لها نفس الحق في الاستمرار للامام عند الاحمر',
    sc.text_en = 'Cars behind them that have the same right to go straight at red',
    sc.text_nl = 'Auto s achter hem die hetzelfde recht hebben om rechtdoor te rijden bij rood',
    sc.text_fr = 'Voitures derriere lui qui ont le meme droit d aller tout droit au rouge',
    sc.is_correct = 0
WHERE sq.question_ref = 'B23_Q03' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'لا يوجد خطر اضافي؛ هذه العلامة المرورية تضمن سلامته',
    sc.text_en = 'There is no extra hazard; this sign guarantees their safety',
    sc.text_nl = 'Er is geen extra gevaar; dit bord waarborgt zijn veiligheid',
    sc.text_fr = 'Il n y a pas de danger supplementaire ; ce panneau garantit sa securite',
    sc.is_correct = 0
WHERE sq.question_ref = 'B23_Q03' AND sc.display_order = 3;

UPDATE sign_questions
SET question_ar = 'انت راكب دراجة عند هذه العلامة المرورية والضوء احمر. ماذا يجب عليك فعله قبل الاستمرار للامام؟',
    question_en = 'You are a cyclist at this sign and the light is red. What must you do before going straight?',
    question_nl = 'U bent een fietser bij dit bord en het licht staat op rood. Wat moet u doen voordat u rechtdoor rijdt?',
    question_fr = 'Vous etes cycliste devant ce panneau et le feu est rouge. Que devez-vous faire avant d aller tout droit ?',
    explanation_ar = 'هذه العلامة المرورية لا تعفي راكب الدراجة من واجب اعطاء الاولوية. يجب ان يتفحص الطريق المتقاطع جيدا وينتظر حتى يتمكن من العبور بامان دون ان يعيق الاخرين.',
    explanation_en = 'This sign does not release the cyclist from the duty to give way. They must carefully check the crossing road and wait until they can cross safely without hindering others.',
    explanation_nl = 'Dit bord ontheft de fietser niet van de voorrangsplicht. Hij moet de kruisende weg goed bekijken en wachten totdat hij veilig en zonder hinder van anderen kan oversteken.',
    explanation_fr = 'Ce panneau ne dispense pas le cycliste de l obligation de ceder le passage. Il doit bien verifier la route transversale et attendre de pouvoir traverser en toute securite.'
WHERE question_ref = 'B23_Q04';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'التحقق من خلو الطريق المتقاطع واعطاء الاولوية لجميع المركبات والمشاة ذوي الضوء الاخضر',
    sc.text_en = 'Check that the crossing road is clear and give way to all vehicles and pedestrians with a green light',
    sc.text_nl = 'Controleren of de kruisende weg vrij is en voorrang verlenen aan alle voertuigen en voetgangers met groen licht',
    sc.text_fr = 'Verifier que la route transversale est degagee et ceder le passage a tous les vehicules et pietons ayant le feu vert',
    sc.is_correct = 1
WHERE sq.question_ref = 'B23_Q04' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'المضي للامام مباشرة لان هذه العلامة المرورية تعلق قواعد الاولوية بالنسبة لراكبي الدراجات',
    sc.text_en = 'Just ride through because this sign suspends the right-of-way rules for cyclists',
    sc.text_nl = 'Gewoon doorrijden want dit bord heft de voorrangsregels op voor fietsers',
    sc.text_fr = 'Passer directement car ce panneau suspend les regles de priorite pour les cyclistes',
    sc.is_correct = 0
WHERE sq.question_ref = 'B23_Q04' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'الانتباه للسيارات فقط؛ المشاة ليس لهم اولوية على راكبي الدراجات',
    sc.text_en = 'Only watch out for cars; pedestrians do not have priority over cyclists',
    sc.text_nl = 'Enkel op auto s letten; voetgangers hebben geen voorrang op fietsers',
    sc.text_fr = 'Ne surveiller que les voitures ; les pietons n ont pas la priorite sur les cyclistes',
    sc.is_correct = 0
WHERE sq.question_ref = 'B23_Q04' AND sc.display_order = 3;

UPDATE sign_questions
SET question_ar = 'انت تقود سيارتك ولديك ضوء اخضر عند تقاطع توجد فيه هذه العلامة المرورية. ترى راكب دراجة يريد الاستمرار للامام من مسار الدراجات. ماذا تفعل؟',
    question_en = 'You are driving your car and have a green light at a junction where this sign is present. You see a cyclist who wants to go straight from the cycle lane. What do you do?',
    question_nl = 'U rijdt met uw auto en heeft groen licht op een kruispunt waar dit bord staat. U ziet een fietser die rechtdoor wil rijden vanuit de fietsstrook. Wat doet u?',
    question_fr = 'Vous conduisez votre voiture et avez le feu vert a un carrefour ou ce panneau est present. Vous voyez un cycliste qui veut aller tout droit depuis la piste cyclable. Que faites-vous ?',
    explanation_ar = 'بصفتك سائق سيارة ولديك ضوء اخضر فانت تملك الاولوية. راكب الدراجة عند هذه العلامة المرورية ملزم بإعطائك الاولوية. لست بحاجة للتوقف لكن يجب البقاء يقظا.',
    explanation_en = 'As a car driver with a green light you have priority. The cyclist at this sign is obliged to give way to you. You do not need to stop but must remain alert.',
    explanation_nl = 'Als automobilist met groen licht heeft u voorrang. De fietser bij dit bord is verplicht u voorrang te verlenen. U hoeft niet te stoppen maar moet alert blijven.',
    explanation_fr = 'En tant que conducteur avec le feu vert, vous avez la priorite. Le cycliste devant ce panneau est oblige de vous ceder le passage. Vous n avez pas besoin de vous arreter mais devez rester vigilant.'
WHERE question_ref = 'B23_Q05';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'تستمر بالقيادة بشكل طبيعي؛ راكب الدراجة يجب ان يعطيك الاولوية لان لديك ضوء اخضر',
    sc.text_en = 'You continue normally; the cyclist must give way to you because you have a green light',
    sc.text_nl = 'U rijdt normaal door; de fietser moet u voorrang geven want u heeft groen licht',
    sc.text_fr = 'Vous continuez normalement ; le cycliste doit vous ceder le passage car vous avez le feu vert',
    sc.is_correct = 1
WHERE sq.question_ref = 'B23_Q05' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'تتوقف لتدع راكب الدراجة يمر لان هذه العلامة المرورية تمنحه الاولوية دائما',
    sc.text_en = 'You stop to let the cyclist pass because this sign always gives them priority',
    sc.text_nl = 'U stopt om de fietser voor te laten gaan want dit bord geeft hem altijd voorrang',
    sc.text_fr = 'Vous vous arretez pour laisser passer le cycliste car ce panneau lui donne toujours la priorite',
    sc.is_correct = 0
WHERE sq.question_ref = 'B23_Q05' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'تسرع لمغادرة التقاطع بسرعة حتى يتمكن راكب الدراجة من العبور',
    sc.text_en = 'You accelerate to leave the junction quickly so the cyclist can cross',
    sc.text_nl = 'U versnelt om het kruispunt snel te verlaten zodat de fietser kan oversteken',
    sc.text_fr = 'Vous accelerez pour quitter rapidement le carrefour afin que le cycliste puisse traverser',
    sc.is_correct = 0
WHERE sq.question_ref = 'B23_Q05' AND sc.display_order = 3;

UPDATE sign_questions
SET question_ar = 'ما الفرق بين هذه العلامة المرورية والعلامة التي تسمح لراكبي الدراجات بالانعطاف يمينا عند الاحمر؟',
    question_en = 'What is the difference between this sign and the sign that allows cyclists to turn right at a red light?',
    question_nl = 'Wat is het verschil tussen dit bord en het bord dat fietsers toelaat rechtsaf te slaan bij rood?',
    question_fr = 'Quelle est la difference entre ce panneau et le panneau qui permet aux cyclistes de tourner a droite au feu rouge ?',
    explanation_ar = 'هاتان العلامتان استثناءان مختلفان لراكبي الدراجات وسبيد بيديليكس. احداهما تسمح بالانعطاف يمينا عند الاحمر، والاخرى تسمح بالاستمرار للامام عند الاحمر. في كلتا الحالتين يبقى التزام اعطاء الاولوية قائما.',
    explanation_en = 'These two signs are different exceptions for cyclists and speed pedelecs. One allows turning right at red, the other going straight at red. In both cases the duty to give way applies.',
    explanation_nl = 'Deze twee borden zijn verschillende uitzonderingen voor fietsers en speed pedelecs. Het ene laat rechtsaf bij rood toe, het andere rechtdoor bij rood. In beide gevallen geldt de voorrangsplicht.',
    explanation_fr = 'Ces deux panneaux sont des exceptions differentes pour les cyclistes et speed pedelecs. L un permet de tourner a droite au rouge, l autre d aller tout droit au rouge. Dans les deux cas l obligation de ceder le passage s applique.'
WHERE question_ref = 'B23_Q06';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'تلك العلامة تسمح بالانعطاف يمينا عند الاحمر، اما هذه العلامة فتسمح بالاستمرار للامام عند الاحمر، لكن كلتيهما تشترطان اعطاء الاولوية',
    sc.text_en = 'That other sign allows turning right at red; this sign allows going straight at red, but both require giving way',
    sc.text_nl = 'Dat andere bord laat rechtsaf bij rood toe; dit bord laat rechtdoor bij rood toe, maar beide vereisen voorrang verlenen',
    sc.text_fr = 'L autre panneau permet de tourner a droite au rouge ; celui-ci permet d aller tout droit au rouge, mais les deux exigent de ceder le passage',
    sc.is_correct = 1
WHERE sq.question_ref = 'B23_Q06' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'تلك العلامة تنطبق على جميع مستخدمي الطريق، اما هذه العلامة فتنطبق على راكبي الدراجات فقط',
    sc.text_en = 'That other sign applies to all road users; this sign only to cyclists',
    sc.text_nl = 'Dat andere bord geldt voor alle weggebruikers; dit bord enkel voor fietsers',
    sc.text_fr = 'L autre panneau s applique a tous les usagers ; celui-ci seulement aux cyclistes',
    sc.is_correct = 0
WHERE sq.question_ref = 'B23_Q06' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'كلتا العلامتين تعنيان الشيء نفسه: عبور حر عند الاحمر لراكبي الدراجات',
    sc.text_en = 'Both signs mean the same thing: free passage at red for cyclists',
    sc.text_nl = 'Beide borden betekenen hetzelfde: vrij doorrijden bij rood voor fietsers',
    sc.text_fr = 'Les deux panneaux signifient la meme chose : passage libre au rouge pour les cyclistes',
    sc.is_correct = 0
WHERE sq.question_ref = 'B23_Q06' AND sc.display_order = 3;

UPDATE sign_questions
SET question_ar = 'انت سائق سيارة على طريق توجد عليه هذه العلامة المرورية. هل يجوز لك ايضا الاستمرار للامام عند الاحمر بسببها؟',
    question_en = 'You are a car driver on a road with this sign. May you also go straight at red because of this sign?',
    question_nl = 'U bent een autobestuurder op een weg met dit bord. Mag u ook rechtdoor rijden bij rood op basis van dit bord?',
    question_fr = 'Vous etes conducteur automobile sur une route avec ce panneau. Pouvez-vous vous aussi aller tout droit au rouge grace a ce panneau ?',
    explanation_ar = 'هذه العلامة المرورية موجهة فقط لراكبي الدراجات وسبيد بيديليكس. سائق السيارة يجب دائما احترام الضوء الاحمر وليس له الحق في هذا الاستثناء.',
    explanation_en = 'This sign is directed only at cyclists and speed pedelecs. A car driver must always respect the red light and has no entitlement to this exception.',
    explanation_nl = 'Dit bord richt zich enkel tot fietsers en speed pedelecs. Een autobestuurder moet altijd het rode licht respecteren en heeft geen recht op deze uitzondering.',
    explanation_fr = 'Ce panneau s adresse uniquement aux cyclistes et speed pedelecs. Un conducteur automobile doit toujours respecter le feu rouge et n a pas droit a cette exception.'
WHERE question_ref = 'B23_Q07';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'لا، هذه العلامة المرورية تنطبق فقط على راكبي الدراجات وسبيد بيديليكس ولا تمنح سائقي السيارات اي حق اضافي',
    sc.text_en = 'No, this sign applies only to cyclists and speed pedelecs and gives car drivers no additional rights',
    sc.text_nl = 'Nee, dit bord geldt uitsluitend voor fietsers en speed pedelecs en geeft autobestuurders geen enkel recht',
    sc.text_fr = 'Non, ce panneau s applique uniquement aux cyclistes et speed pedelecs et ne donne aucun droit supplementaire aux conducteurs automobiles',
    sc.is_correct = 1
WHERE sq.question_ref = 'B23_Q07' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'نعم، هذه العلامة المرورية تنطبق على جميع مستخدمي الطريق في مسار الاستمرار للامام',
    sc.text_en = 'Yes, this sign applies to all road users in the straight-ahead lane',
    sc.text_nl = 'Ja, dit bord geldt voor alle weggebruikers in de rechtdoorstrook',
    sc.text_fr = 'Oui, ce panneau s applique a tous les usagers dans la voie tout droit',
    sc.is_correct = 0
WHERE sq.question_ref = 'B23_Q07' AND sc.display_order = 2;

UPDATE sign_questions
SET question_ar = 'هل تنطبق صلاحية هذه العلامة المرورية على جميع التقاطعات ام فقط حيث توضع؟',
    question_en = 'Does the permission given by this sign apply at all junctions, or only where the sign is placed?',
    question_nl = 'Geldt de toestemming van dit bord op alle kruispunten, of enkel waar het bord geplaatst is?',
    question_fr = 'La permission de ce panneau s applique-t-elle a tous les carrefours, ou uniquement la ou le panneau est place ?',
    explanation_ar = 'تنطبق هذه العلامة المرورية حصرا على التقاطع المحدد الذي تم تركيبها فيه. عند تقاطع لا توجد فيه هذه العلامة المرورية يجب على راكب الدراجة احترام الضوء الاحمر.',
    explanation_en = 'This sign applies exclusively at the specific junction where it is installed. At a junction without it, the cyclist must respect the red light.',
    explanation_nl = 'Dit bord geldt uitsluitend op het specifieke kruispunt waar het geplaatst is. Op een kruispunt zonder dit bord moet de fietser het rode licht respecteren.',
    explanation_fr = 'Ce panneau s applique exclusivement au carrefour specifique ou il est installe. A un carrefour sans ce panneau, le cycliste doit respecter le feu rouge.'
WHERE question_ref = 'B23_Q08';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'فقط عند التقاطع المحدد الذي توجد فيه هذه العلامة المرورية فعليا',
    sc.text_en = 'Only at the specific junction where this sign is physically present',
    sc.text_nl = 'Enkel op het kruispunt waar dit bord fysiek aanwezig is',
    sc.text_fr = 'Uniquement au carrefour specifique ou ce panneau est physiquement present',
    sc.is_correct = 1
WHERE sq.question_ref = 'B23_Q08' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'في جميع التقاطعات ذات الاشارات الضوئية في بلجيكا',
    sc.text_en = 'At all junctions with a traffic light in Belgium',
    sc.text_nl = 'Op alle kruispunten met een verkeerslicht in Belgie',
    sc.text_fr = 'A tous les carrefours avec un feu de signalisation en Belgique',
    sc.is_correct = 0
WHERE sq.question_ref = 'B23_Q08' AND sc.display_order = 2;