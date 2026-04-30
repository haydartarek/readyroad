-- Deep polish E11 learner-facing content.
-- Removes raw sign-code mentions and companion-sign leakage from displayed
-- text, keeping the persisted question bank aligned with the cleaned source
-- JSON.

UPDATE sign_questions
SET question_ar = 'ما معنى هذه العلامة المرورية؟',
    question_en = 'What does this traffic sign mean?',
    question_nl = 'Wat betekent dit verkeersbord?',
    question_fr = 'Que signifie ce panneau de signalisation ?',
    explanation_ar = 'تعرض هذه العلامة المرورية المجمعة قواعد الوقوف المتناوب نصف الشهري معا لتسهيل القراءة.',
    explanation_en = 'This combined overview sign shows the rules for half-monthly alternating parking together for better readability.',
    explanation_nl = 'Dit gecombineerde overzichtsbord toont de regels voor halfmaandelijks afwisselend parkeren samen voor betere leesbaarheid.',
    explanation_fr = 'Ce panneau recapitulatif combine presente ensemble les regles du stationnement alternatif semi-mensuel pour une meilleure lisibilite.'
WHERE question_ref = 'E11_Q01';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'وقوف متناوب نصف شهري: قاعدة النصف الاول من الشهر وقاعدة النصف الثاني معروضتان معا في علامة مرورية واحدة',
    sc.text_en = 'Half-monthly alternating parking: the rule for the first half of the month and the rule for the second half are shown together on one combined sign',
    sc.text_nl = 'Halfmaandelijks afwisselend parkeren: de regel voor de eerste helft van de maand en die voor de tweede helft staan samen op een gecombineerd bord',
    sc.text_fr = 'Stationnement alternatif semi-mensuel : la regle pour la premiere moitie du mois et celle pour la seconde moitie sont reunies sur un seul panneau combine',
    sc.is_correct = 1
WHERE sq.question_ref = 'E11_Q01' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'حظر وقوف في اول واخر يوم من الشهر',
    sc.text_en = 'Parking ban on the first and last day of the month',
    sc.text_nl = 'Parkeerverbod de eerste en de laatste dag van de maand',
    sc.text_fr = 'Interdiction de stationnement le premier et le dernier jour du mois',
    sc.is_correct = 0
WHERE sq.question_ref = 'E11_Q01' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'وقوف الزامي 11 يوما في الشهر',
    sc.text_en = 'Compulsory parking 11 days per month',
    sc.text_nl = 'Verplicht parkeren 11 dagen per maand',
    sc.text_fr = 'Stationnement obligatoire 11 jours par mois',
    sc.is_correct = 0
WHERE sq.question_ref = 'E11_Q01' AND sc.display_order = 3;

UPDATE sign_questions
SET question_ar = 'ماذا يظهر السهمان في هذه العلامة المرورية؟',
    question_en = 'What do the two arrows on this sign show?',
    question_nl = 'Wat tonen de twee pijlen op dit bord?',
    question_fr = 'Que montrent les deux fleches sur ce panneau ?',
    explanation_ar = 'تظهر هذه العلامة المرورية المجمعة فترتي الحظر معا وتبين اي جانب يقابل اي نطاق من التواريخ.',
    explanation_en = 'This combined sign shows both prohibition periods on one board and indicates which side corresponds to each date range.',
    explanation_nl = 'Dit gecombineerde bord toont beide verbodperiodes op een paneel en geeft aan welke zijde bij welk datumbereik hoort.',
    explanation_fr = 'Ce panneau combine montre les deux periodes d interdiction sur un seul support et indique quel cote correspond a quelle plage de dates.'
WHERE question_ref = 'E11_Q02';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'جانب الطريق وفترة الحظر: السهم العلوي يطبق خلال الايام 1-15، والسهم السفلي خلال الايام 16 حتى نهاية الشهر',
    sc.text_en = 'The side of the road and the ban period: the upper arrow applies during days 1-15, the lower arrow during days 16 to month end',
    sc.text_nl = 'De zijde van de weg en de verbodperiode: de bovenste pijl geldt voor dagen 1-15, de onderste pijl voor dagen 16 tot maand einde',
    sc.text_fr = 'Le cote de la route et la periode d interdiction : la fleche superieure vaut pour les jours 1 a 15, la fleche inferieure pour les jours 16 a la fin du mois',
    sc.is_correct = 1
WHERE sq.question_ref = 'E11_Q02' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'اتجاهان للسير يحظر فيهما التجاوز',
    sc.text_en = 'Two driving directions where overtaking is forbidden',
    sc.text_nl = 'Twee rijrichtingen waar inhalen is verboden',
    sc.text_fr = 'Deux sens de circulation ou le depassement est interdit',
    sc.is_correct = 0
WHERE sq.question_ref = 'E11_Q02' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'فترتان يوميتان يحظر فيهما الوقوف',
    sc.text_en = 'Two daily time periods when parking is forbidden',
    sc.text_nl = 'Twee tijdvakken per dag waarop parkeren verboden is',
    sc.text_fr = 'Deux periodes horaires quotidiennes ou le stationnement est interdit',
    sc.is_correct = 0
WHERE sq.question_ref = 'E11_Q02' AND sc.display_order = 3;

UPDATE sign_questions
SET question_ar = 'لماذا تستخدم هذه العلامة المرورية المجمعة بدلا من علامتين منفصلتين للوقوف نصف الشهري؟',
    question_en = 'Why is this combined sign used instead of placing the two half-month parking rules on separate signs?',
    question_nl = 'Waarom gebruikt men dit gecombineerde bord in plaats van twee afzonderlijke borden voor halfmaandelijks parkeren?',
    question_fr = 'Pourquoi utilise-t-on ce panneau combine plutot que deux panneaux separes pour le stationnement semi-mensuel ?',
    explanation_ar = 'هذه العلامة المرورية ملخص معلوماتي يمنح السائقين نظرة شاملة فورية على نظام الوقوف المتناوب.',
    explanation_en = 'This sign is an informative summary that gives drivers an immediate complete overview of the alternating parking system.',
    explanation_nl = 'Dit bord is een informatieve samenvatting die bestuurders meteen een volledig overzicht geeft van het afwisselend parkeersysteem.',
    explanation_fr = 'Ce panneau est un resume informatif qui donne aux conducteurs un apercu complet et immediat du systeme de stationnement alterne.'
WHERE question_ref = 'E11_Q03';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'لتفادي الارتباك: يرى السائقون قواعد كلا الجانبين دفعة واحدة',
    sc.text_en = 'To avoid confusion: drivers see the rules for both sides at a single glance',
    sc.text_nl = 'Om verwarring te vermijden: bestuurders zien de regels voor beide zijden in een oogopslag',
    sc.text_fr = 'Pour eviter la confusion : les conducteurs voient les regles des deux cotes d un seul coup d oeil',
    sc.is_correct = 1
WHERE sq.question_ref = 'E11_Q03' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'لان علامتين منفصلتين ستكونان غير صالحتين قانونا اذا وضعتا كل على حدة',
    sc.text_en = 'Because two separate signs would be legally invalid if they were placed separately',
    sc.text_nl = 'Omdat twee afzonderlijke borden juridisch ongeldig zouden zijn als ze apart staan',
    sc.text_fr = 'Parce que deux panneaux separes seraient juridiquement invalides s ils etaient places separement',
    sc.is_correct = 0
WHERE sq.question_ref = 'E11_Q03' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'لتوفير المساحة لان علامة مرورية واحدة ارخص',
    sc.text_en = 'To save space as only one sign is cheaper',
    sc.text_nl = 'Om ruimte te besparen want een bord is goedkoper',
    sc.text_fr = 'Pour economiser de l espace car un seul panneau est moins cher',
    sc.is_correct = 0
WHERE sq.question_ref = 'E11_Q03' AND sc.display_order = 3;

UPDATE sign_questions
SET question_ar = 'اليوم هو العاشر. انت عند هذه العلامة المرورية. على اي جانب لا يجوز لك الوقوف؟',
    question_en = 'Today is the 10th. You are at this sign. On which side may you NOT park?',
    question_nl = 'Vandaag is de 10e. U staat bij dit bord. Aan welke kant mag u NIET parkeren?',
    question_fr = 'Aujourd hui c est le 10. Vous etes devant ce panneau. De quel cote ne pouvez-vous PAS vous garer ?',
    explanation_ar = 'في اليوم العاشر، اي ضمن فترة 1-15، يكون جانب السهم العلوي محظورا. اما جانب السهم السفلي فمسموح.',
    explanation_en = 'On the 10th, so within the period 1-15, the upper-arrow side is forbidden. The lower-arrow side is free.',
    explanation_nl = 'Op de 10e, dus in de periode 1-15, is de zijde van de bovenste pijl verboden. De zijde van de onderste pijl is vrij.',
    explanation_fr = 'Le 10, donc dans la periode 1 a 15, le cote de la fleche superieure est interdit. Le cote de la fleche inferieure est libre.'
WHERE question_ref = 'E11_Q04';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'على جانب السهم العلوي: الايام 1-15 هي فترة الحظر لذلك الجانب',
    sc.text_en = 'On the upper-arrow side: days 1-15 are the ban period for that side',
    sc.text_nl = 'Aan de kant van de bovenste pijl: dagen 1-15 zijn de verbodperiode voor die zijde',
    sc.text_fr = 'Du cote de la fleche superieure : les jours 1 a 15 sont la periode d interdiction pour ce cote',
    sc.is_correct = 1
WHERE sq.question_ref = 'E11_Q04' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'على جانب السهم السفلي لان ذلك النصف يكون نشطا دائما',
    sc.text_en = 'On the lower-arrow side because that half is always active',
    sc.text_nl = 'Aan de kant van de onderste pijl want die helft is altijd actief',
    sc.text_fr = 'Du cote de la fleche inferieure car cette moitie est toujours active',
    sc.is_correct = 0
WHERE sq.question_ref = 'E11_Q04' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'على كلا الجانبين لان هذه العلامة المرورية تحظر الوقوف على الجانبين معا',
    sc.text_en = 'On both sides because this sign forbids parking on both sides simultaneously',
    sc.text_nl = 'Aan beide kanten want dit bord verbiedt parkeren aan beide kanten tegelijk',
    sc.text_fr = 'Des deux cotes car ce panneau interdit le stationnement des deux cotes simultanement',
    sc.is_correct = 0
WHERE sq.question_ref = 'E11_Q04' AND sc.display_order = 3;

UPDATE sign_questions
SET question_ar = 'ترى هذه العلامة المرورية لكنك لا تعرف النظام. ماذا تفعل؟',
    question_en = 'You see this sign but do not know the system. What do you do?',
    question_nl = 'U ziet dit bord maar kent het systeem niet. Wat doet u?',
    question_fr = 'Vous voyez ce panneau mais vous ne connaissez pas le systeme. Que faites-vous ?',
    explanation_ar = 'صممت هذه العلامة المرورية لتكون سهلة القراءة بديهيا. تحقق من التاريخ ومن جانب الطريق لتطبيق القاعدة الصحيحة.',
    explanation_en = 'This sign is designed to be intuitively readable. Check the date and the side of the road to apply the correct rule.',
    explanation_nl = 'Dit bord is bedoeld om intuitief leesbaar te zijn. Kijk naar de datum en de zijde van de weg om de juiste regel toe te passen.',
    explanation_fr = 'Ce panneau est concu pour etre lisible intuitivement. Regardez la date et le cote de la route pour appliquer la bonne regle.'
WHERE question_ref = 'E11_Q05';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'تقرأ العلامة المرورية بعناية: النصف العلوي = جانب الايام 1-15، النصف السفلي = جانب الايام 16 حتى نهاية الشهر؛ ثم تتحقق من التاريخ',
    sc.text_en = 'Read the sign carefully: upper half = side for days 1-15, lower half = side for days 16 to month end; then check the date',
    sc.text_nl = 'U leest het bord zorgvuldig: bovenste helft = zijde voor dagen 1-15, onderste helft = zijde voor dagen 16 tot maand einde; daarna kijkt u naar de datum',
    sc.text_fr = 'Lisez attentivement le panneau : moitie superieure = cote des jours 1 a 15, moitie inferieure = cote des jours 16 a la fin du mois ; puis verifiez la date',
    sc.is_correct = 1
WHERE sq.question_ref = 'E11_Q05' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'تتجاهل العلامة المرورية لانها تخص المقيمين فقط',
    sc.text_en = 'Ignore the sign because it is only for residents',
    sc.text_nl = 'U negeert het bord want het geldt alleen voor bewoners',
    sc.text_fr = 'Ignorez le panneau car il est reserve aux riverains',
    sc.is_correct = 0
WHERE sq.question_ref = 'E11_Q05' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'تقف على الجانب ذي ارقام المنازل الاقل',
    sc.text_en = 'Park on the side with the lowest house numbers',
    sc.text_nl = 'U parkeert aan de kant met de laagste huisnummers',
    sc.text_fr = 'Garez-vous du cote avec les numeros de maison les plus bas',
    sc.is_correct = 0
WHERE sq.question_ref = 'E11_Q05' AND sc.display_order = 3;

UPDATE sign_questions
SET question_ar = 'هل هذه العلامة المرورية المجمعة الزامية ام اختيارية الى جانب العلامتين الاساسيتين للوقوف نصف الشهري؟',
    question_en = 'Is this combined overview sign mandatory or optional alongside the two underlying half-month parking signs?',
    question_nl = 'Is dit gecombineerde overzichtsbord verplicht of optioneel naast de twee onderliggende borden voor halfmaandelijks parkeren?',
    question_fr = 'Ce panneau recapitulatif combine est-il obligatoire ou facultatif a cote des deux panneaux sous-jacents du stationnement semi-mensuel ?',
    explanation_ar = 'العلامتان الاساسيتان للوقوف نصف الشهري هما العلامتان الملزمتان قانونا. اما هذه العلامة المرورية المجمعة فتوفر معلومات اضافية لكنها ليست الزامية.',
    explanation_en = 'The two underlying half-month parking signs are the legally binding signs. This combined sign provides additional information but is not mandatory.',
    explanation_nl = 'De twee onderliggende borden voor halfmaandelijks parkeren zijn de juridisch bindende borden. Dit gecombineerde bord geeft bijkomende informatie maar is niet verplicht.',
    explanation_fr = 'Les deux panneaux sous-jacents du stationnement semi-mensuel sont les panneaux juridiquement contraignants. Ce panneau combine fournit des informations supplementaires mais n est pas obligatoire.'
WHERE question_ref = 'E11_Q06';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'هي علامة مرورية اضافية معلوماتية؛ اما علامتا الوقوف الملزمتان قانونا للنصف الاول والثاني من الشهر فتبقيان نافذتين',
    sc.text_en = 'It is an informational additional sign; the two underlying legally binding parking signs for the first and second half of the month remain enforceable',
    sc.text_nl = 'Het is een informatief aanvullend bord; de twee onderliggende juridisch bindende parkeerborden voor de eerste en tweede helft van de maand blijven van kracht',
    sc.text_fr = 'Il s agit d un panneau informatif complementaire ; les deux panneaux de stationnement juridiquement contraignants pour la premiere et la seconde moitie du mois restent applicables',
    sc.is_correct = 1
WHERE sq.question_ref = 'E11_Q06' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'هي تحل محل علامتي الوقوف الاساسيتين؛ فهاتان تصبحان غير ضروريتين عند وجود هذه العلامة المرورية',
    sc.text_en = 'It replaces the two underlying parking signs; they become redundant when this sign is present',
    sc.text_nl = 'Het vervangt de twee onderliggende parkeerborden; die zijn overbodig zodra dit bord aanwezig is',
    sc.text_fr = 'Il remplace les deux panneaux de stationnement sous-jacents ; ceux-ci deviennent superflus lorsque ce panneau est present',
    sc.is_correct = 0
WHERE sq.question_ref = 'E11_Q06' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'يجب دائما وضعها مقابل العلامة الخاصة بالنصف الاول من الشهر',
    sc.text_en = 'It must always be placed opposite the sign for the first half of the month',
    sc.text_nl = 'Het moet altijd tegenover het bord voor de eerste helft van de maand worden geplaatst',
    sc.text_fr = 'Il doit toujours etre place en face du panneau de la premiere moitie du mois',
    sc.is_correct = 0
WHERE sq.question_ref = 'E11_Q06' AND sc.display_order = 3;

UPDATE sign_questions
SET question_ar = 'هل يجوز تجاهل جانب السهم العلوي في هذه العلامة المرورية لانك تجدها صعبة القراءة؟',
    question_en = 'May you ignore the upper-arrow side of this sign because you find the combined sign hard to read?',
    question_nl = 'Mag u de zijde van de bovenste pijl negeren omdat u dit gecombineerde bord moeilijk leesbaar vindt?',
    question_fr = 'Pouvez-vous ignorer le cote de la fleche superieure de ce panneau parce que vous trouvez ce panneau combine difficile a lire ?',
    explanation_ar = 'الاصل القانوني ان العلامة المرورية الموضوعة بشكل صحيح تكون واضحة بما يكفي. صعوبة قراءتها لا تعفيك من العقوبة.',
    explanation_en = 'The legal presumption is that a lawfully placed sign is clear enough. Difficulty reading it does not exempt you from a sanction.',
    explanation_nl = 'Het wettelijke uitgangspunt is dat een rechtmatig geplaatst bord voldoende duidelijk is. Het moeilijk leesbaar vinden ontslaat u niet van een sanctie.',
    explanation_fr = 'La presomption legale est qu un panneau place legalement est suffisamment clair. La difficulte a le lire ne vous exonere pas d une sanction.'
WHERE question_ref = 'E11_Q07';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'لا: صعوبة قراءة العلامة المرورية ليست عذرا مقبولا للمخالفة؛ فقيود الوقوف الاساسية تبقى نافذة',
    sc.text_en = 'No: difficulty reading the sign is not a valid excuse for a violation; the underlying parking prohibitions still apply',
    sc.text_nl = 'Nee: moeite hebben om het bord te lezen is geen geldig excuus voor een overtreding; de onderliggende parkeerverboden blijven gelden',
    sc.text_fr = 'Non : avoir du mal a lire le panneau n est pas une excuse valable pour une infraction ; les interdictions de stationnement sous-jacentes restent applicables',
    sc.is_correct = 1
WHERE sq.question_ref = 'E11_Q07' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'نعم: اذا كانت العلامة المرورية غير واضحة يمكنك اختيار التفسير الاكثر ملاءمة',
    sc.text_en = 'Yes: if the sign is unclear you may choose the most favourable interpretation',
    sc.text_nl = 'Ja: als het bord onduidelijk is, mag u de gunstigste interpretatie kiezen',
    sc.text_fr = 'Oui : si le panneau est peu clair vous pouvez choisir l interpretation la plus favorable',
    sc.is_correct = 0
WHERE sq.question_ref = 'E11_Q07' AND sc.display_order = 2;

UPDATE sign_questions
SET question_ar = 'هل يسمح بالوقوف المتواصل لشهر كامل على جانب السهم السفلي تحت هذه العلامة المرورية؟',
    question_en = 'Is it allowed to park uninterruptedly for a month on the lower-arrow side under this sign?',
    question_nl = 'Is het toegestaan een maand lang ononderbroken te parkeren aan de zijde van de onderste pijl onder dit bord?',
    question_fr = 'Est-il permis de se garer sans interruption pendant un mois du cote de la fleche inferieure sous ce panneau ?',
    explanation_ar = 'يكون جانب السهم السفلي محظورا من اليوم 16 حتى نهاية الشهر. لذلك فالوقوف المتواصل لشهر كامل على ذلك الجانب غير ممكن.',
    explanation_en = 'The lower-arrow side is forbidden from the 16th to the end of the month. Parking continuously for a month on that side is therefore not possible.',
    explanation_nl = 'De zijde van de onderste pijl is verboden van de 16e tot het einde van de maand. Een hele maand ononderbroken parkeren aan die zijde is dus niet mogelijk.',
    explanation_fr = 'Le cote de la fleche inferieure est interdit du 16 a la fin du mois. Le stationnement continu pendant un mois de ce cote n est donc pas possible.'
WHERE question_ref = 'E11_Q08';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'لا: من اليوم 16 حتى نهاية الشهر يكون جانب السهم السفلي محظورا؛ لذلك فالوقوف المتواصل لشهر كامل غير ممكن',
    sc.text_en = 'No: from the 16th to the end of the month the lower-arrow side is forbidden; uninterrupted parking for a month is therefore not possible',
    sc.text_nl = 'Nee: van de 16e tot het einde van de maand is de zijde van de onderste pijl verboden; ononderbroken parkeren gedurende een maand is dus niet mogelijk',
    sc.text_fr = 'Non : du 16 a la fin du mois le cote de la fleche inferieure est interdit ; le stationnement ininterrompu pendant un mois n est donc pas possible',
    sc.is_correct = 1
WHERE sq.question_ref = 'E11_Q08' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET sc.text_ar = 'نعم: اذا بقيت مع المركبة يسمح بالوقوف الطويل دائما',
    sc.text_en = 'Yes: if you stay with the vehicle, long-term parking is always allowed',
    sc.text_nl = 'Ja: als u bij het voertuig blijft, is langdurig parkeren altijd toegestaan',
    sc.text_fr = 'Oui : si vous restez pres du vehicule, le stationnement de longue duree est toujours autorise',
    sc.is_correct = 0
WHERE sq.question_ref = 'E11_Q08' AND sc.display_order = 2;