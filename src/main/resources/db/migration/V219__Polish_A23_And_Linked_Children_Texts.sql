UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.question_ar = 'ما معنى هذه العلامة المرورية؟',
    q.question_en = 'What does this traffic sign mean?',
    q.question_nl = 'Wat betekent dit verkeersbord?',
    q.question_fr = 'Que signifie ce panneau de signalisation ?',
    q.explanation_ar = 'تشير هذه العلامة المرورية إلى منطقة يرتادها الأطفال كثيرًا، مثل محيط المدارس أو أماكن اللعب. وقد يندفع الأطفال إلى الطريق فجأة.',
    q.explanation_en = 'This traffic sign warns of an area frequented by children, such as near schools or playgrounds. Children may suddenly enter the road.',
    q.explanation_nl = 'Dit verkeersbord waarschuwt voor een zone waar vaak kinderen komen, zoals in de buurt van scholen of speelpleinen. Kinderen kunnen plots de rijbaan oplopen.',
    q.explanation_fr = 'Ce panneau de signalisation avertit d’une zone fréquentée par des enfants, par exemple près des écoles ou des aires de jeux. Les enfants peuvent soudainement s’engager sur la chaussée.'
WHERE rs.sign_code = 'A23' AND q.question_ref = 'A23_Q01';

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 1,
    c.text_ar = 'علامة تحذير: أطفال — منطقة يرتادها الأطفال كثيرًا وقد يعبرون الطريق فجأة',
    c.text_en = 'Warning: children — an area frequently used by children who may suddenly enter the road',
    c.text_nl = 'Waarschuwing: kinderen — een zone waar veel kinderen komen en plots de weg kunnen oversteken',
    c.text_fr = 'Attention : enfants — une zone fréquentée par des enfants qui peuvent surgir soudainement sur la chaussée'
WHERE rs.sign_code = 'A23' AND q.question_ref = 'A23_Q01' AND c.display_order = 1;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 0,
    c.text_ar = 'علامة ممر عبور المشاة — ممر مخصص يجب فيه إعطاء الأولوية للمشاة',
    c.text_en = 'Pedestrian crossing — a marked crossing where you must give way to pedestrians',
    c.text_nl = 'Oversteekplaats voor voetgangers — een gemarkeerde oversteek waar u voorrang moet verlenen',
    c.text_fr = 'Passage pour piétons — un passage balisé où vous devez céder la priorité aux piétons'
WHERE rs.sign_code = 'A23' AND q.question_ref = 'A23_Q01' AND c.display_order = 2;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 0,
    c.text_ar = 'علامة عبور الدراجات — ممر مخصص للدراجات والدراجات البخارية',
    c.text_en = 'Cyclist crossing — a crossing for cyclists and moped riders',
    c.text_nl = 'Oversteekplaats voor fietsers — een oversteek voor fietsers en bromfietsers',
    c.text_fr = 'Traversée de cyclistes — un passage réservé aux cyclistes et aux cyclomotoristes'
WHERE rs.sign_code = 'A23' AND q.question_ref = 'A23_Q01' AND c.display_order = 3;

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.question_ar = 'إلى أي فئة تُصنَّف هذه العلامة المرورية؟',
    q.question_en = 'To which category of traffic signs does this sign belong?',
    q.question_nl = 'Tot welke categorie verkeerstekens behoort dit bord?',
    q.question_fr = 'À quelle catégorie de panneaux de signalisation ce panneau appartient-il ?',
    q.explanation_ar = 'تنتمي هذه العلامة المرورية إلى علامات الخطر. وتكون علامات الخطر مثلثة الشكل ذات إطار أحمر، وتُستخدم للتنبيه إلى أخطار محددة.',
    q.explanation_en = 'This sign belongs to the danger signs. Danger signs are triangular with a red border and warn of a specific hazard.',
    q.explanation_nl = 'Dit bord behoort tot de gevaarsborden. Gevaarsborden zijn driehoekig met een rode rand en waarschuwen voor een specifiek gevaar.',
    q.explanation_fr = 'Ce panneau appartient aux panneaux de danger. Les panneaux de danger sont triangulaires avec une bordure rouge et signalent un danger spécifique.'
WHERE rs.sign_code = 'A23' AND q.question_ref = 'A23_Q02';

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 1,
    c.text_ar = 'علامات الخطر',
    c.text_en = 'Danger signs',
    c.text_nl = 'Gevaarsborden',
    c.text_fr = 'Panneaux de danger'
WHERE rs.sign_code = 'A23' AND q.question_ref = 'A23_Q02' AND c.display_order = 1;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 0,
    c.text_ar = 'علامات الأولوية',
    c.text_en = 'Priority signs',
    c.text_nl = 'Voorrangsborden',
    c.text_fr = 'Panneaux de priorité'
WHERE rs.sign_code = 'A23' AND q.question_ref = 'A23_Q02' AND c.display_order = 2;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 0,
    c.text_ar = 'علامات الحظر',
    c.text_en = 'Prohibition signs',
    c.text_nl = 'Verbodsborden',
    c.text_fr = 'Panneaux d''interdiction'
WHERE rs.sign_code = 'A23' AND q.question_ref = 'A23_Q02' AND c.display_order = 3;

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.question_ar = 'ما هو الخطر الذي تشير إليه هذه العلامة المرورية؟',
    q.question_en = 'What danger does this traffic sign indicate?',
    q.question_nl = 'Welk gevaar duidt dit verkeersbord aan?',
    q.question_fr = 'Quel danger indique ce panneau de signalisation ?',
    q.explanation_ar = 'تشير هذه العلامة المرورية إلى احتمال ظهور الأطفال بشكل مفاجئ بالقرب من الطريق. لذلك يجب القيادة بحذر شديد والانتباه المستمر.',
    q.explanation_en = 'This traffic sign warns that children may appear suddenly near the road. You should drive with great caution and remain highly attentive.',
    q.explanation_nl = 'Dit verkeersbord waarschuwt dat kinderen plots bij de weg kunnen verschijnen. Daarom moet u zeer voorzichtig rijden en voortdurend alert blijven.',
    q.explanation_fr = 'Ce panneau de signalisation avertit que des enfants peuvent apparaître soudainement près de la route. Vous devez donc conduire avec une grande prudence et rester très attentif.'
WHERE rs.sign_code = 'A23' AND q.question_ref = 'A23_Q03';

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 1,
    c.text_ar = 'قد يندفع الأطفال إلى الطريق فجأة، وتصرفاتهم غير متوقعة.',
    c.text_en = 'Children may suddenly run into the road, and their behaviour is unpredictable.',
    c.text_nl = 'Kinderen kunnen plots de rijbaan oplopen en hun gedrag is onvoorspelbaar.',
    c.text_fr = 'Des enfants peuvent soudainement s’engager sur la chaussée et leur comportement est imprévisible.'
WHERE rs.sign_code = 'A23' AND q.question_ref = 'A23_Q03' AND c.display_order = 1;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 0,
    c.text_ar = 'مشاة ينتظرون عند ممر عبور المشاة',
    c.text_en = 'Pedestrians waiting at a marked pedestrian crossing',
    c.text_nl = 'Voetgangers die wachten aan een gemarkeerde voetgangersoversteekplaats',
    c.text_fr = 'Des piétons attendant à un passage pour piétons balisé'
WHERE rs.sign_code = 'A23' AND q.question_ref = 'A23_Q03' AND c.display_order = 2;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 0,
    c.text_ar = 'حافلات مدرسية تتوقف بانتظام على جانب الطريق',
    c.text_en = 'School buses stopping regularly along the roadside',
    c.text_nl = 'Schoolbussen die regelmatig langs de weg stoppen',
    c.text_fr = 'Des bus scolaires s’arrêtant régulièrement le long de la route'
WHERE rs.sign_code = 'A23' AND q.question_ref = 'A23_Q03' AND c.display_order = 3;

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.question_ar = 'ماذا يجب عليك فعله عند رؤية هذه العلامة المرورية؟',
    q.question_en = 'What should you do when you see this traffic sign?',
    q.question_nl = 'Wat moet u doen wanneer u dit verkeersbord ziet?',
    q.question_fr = 'Que devez-vous faire lorsque vous voyez ce panneau de signalisation ?',
    q.explanation_ar = 'عند رؤية هذه العلامة المرورية يجب تخفيف السرعة وزيادة الانتباه، حتى لو لم يظهر أطفال في تلك اللحظة.',
    q.explanation_en = 'When you see this traffic sign, you must reduce speed and increase your attention, even if no children are visible at that moment.',
    q.explanation_nl = 'Wanneer u dit verkeersbord ziet, moet u uw snelheid verlagen en extra aandachtig zijn, ook als er op dat moment geen kinderen zichtbaar zijn.',
    q.explanation_fr = 'Lorsque vous voyez ce panneau de signalisation, vous devez réduire votre vitesse et redoubler d’attention, même si aucun enfant n’est visible à ce moment-là.'
WHERE rs.sign_code = 'A23' AND q.question_ref = 'A23_Q04';

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 1,
    c.text_ar = 'أخفف السرعة، أراقب الطريق جيدًا، وأستعد للتوقف عند الحاجة.',
    c.text_en = 'Slow down, watch the road carefully, and be ready to stop if needed.',
    c.text_nl = 'Vertraag, kijk de weg goed na en wees klaar om te stoppen als dat nodig is.',
    c.text_fr = 'Ralentir, bien observer la route et être prêt à vous arrêter si nécessaire.'
WHERE rs.sign_code = 'A23' AND q.question_ref = 'A23_Q04' AND c.display_order = 1;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 0,
    c.text_ar = 'أتابع السير بالسرعة العادية إذا لم يكن هناك أطفال ظاهرون',
    c.text_en = 'Keep driving at the normal speed if no children are visible.',
    c.text_nl = 'Blijf aan normale snelheid rijden als er geen kinderen zichtbaar zijn.',
    c.text_fr = 'Continuer à la vitesse normale si aucun enfant n’est visible.'
WHERE rs.sign_code = 'A23' AND q.question_ref = 'A23_Q04' AND c.display_order = 2;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 0,
    c.text_ar = 'أستخدم المنبّه لتنبيه الأطفال ثم أتابع السير بالسرعة العادية',
    c.text_en = 'Use the horn to warn the children and then continue at the normal speed.',
    c.text_nl = 'Gebruik de claxon om kinderen te waarschuwen en rijd daarna met normale snelheid verder.',
    c.text_fr = 'Utiliser le klaxon pour avertir les enfants puis continuer à vitesse normale.'
WHERE rs.sign_code = 'A23' AND q.question_ref = 'A23_Q04' AND c.display_order = 3;

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.question_ar = 'أمامك علامة تحذير: أطفال. الساعة 15:30 بعد انتهاء المدرسة. كيف تتصرف أثناء القيادة؟',
    q.question_en = 'You are approaching a children warning sign. It is 15:30, just after school has ended. How should you drive?',
    q.question_nl = 'U nadert een waarschuwingsbord voor kinderen. Het is 15.30 uur, net na schooltijd. Hoe moet u rijden?',
    q.question_fr = 'Vous approchez d’un panneau de danger signalant des enfants. Il est 15 h 30, juste après la sortie de l’école. Comment devez-vous conduire ?',
    q.explanation_ar = 'بعد انتهاء المدرسة يزداد احتمال ظهور الأطفال بشكل مفاجئ. لذلك يجب أن تكون سرعتك منخفضة جدًا وأن تبقى مستعدًا للتوقف الفوري.',
    q.explanation_en = 'Just after school ends, children may appear suddenly and move unpredictably. Your speed must be very low and you must be ready to stop immediately.',
    q.explanation_nl = 'Net na schooltijd kunnen kinderen plots verschijnen en zich onvoorspelbaar gedragen. Uw snelheid moet zeer laag zijn en u moet onmiddellijk kunnen stoppen.',
    q.explanation_fr = 'Juste après la sortie de l’école, des enfants peuvent surgir soudainement et se déplacer de manière imprévisible. Votre vitesse doit être très faible et vous devez pouvoir vous arrêter immédiatement.'
WHERE rs.sign_code = 'A23' AND q.question_ref = 'A23_Q05';

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 0,
    c.text_ar = 'أخفف السرعة، أراقب الطريق جيدًا، وأستعد للتوقف عند الحاجة.',
    c.text_en = 'Slow down, watch the road carefully, and be ready to stop if needed.',
    c.text_nl = 'Vertraag, kijk de weg goed na en wees klaar om te stoppen als dat nodig is.',
    c.text_fr = 'Ralentir, bien observer la route et être prêt à vous arrêter si nécessaire.'
WHERE rs.sign_code = 'A23' AND q.question_ref = 'A23_Q05' AND c.display_order = 1;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 0,
    c.text_ar = 'أتابع القيادة بشكل عادي لأن الدوام انتهى بالفعل',
    c.text_en = 'Continue driving normally because school has already finished.',
    c.text_nl = 'Blijf normaal rijden omdat de school al uit is.',
    c.text_fr = 'Continuer à conduire normalement puisque l’école est déjà terminée.'
WHERE rs.sign_code = 'A23' AND q.question_ref = 'A23_Q05' AND c.display_order = 2;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 1,
    c.text_ar = 'أسير بسرعة المشي أو ببطء شديد وأبقى قادرًا على التوقف التام فور دخول طفل إلى الطريق',
    c.text_en = 'Drive at walking pace or very slowly and be able to stop completely as soon as a child steps into the road.',
    c.text_nl = 'Rijd stapvoets of zeer langzaam en blijf in staat om volledig te stoppen zodra een kind de rijbaan opkomt.',
    c.text_fr = 'Rouler au pas ou très lentement et rester capable de vous arrêter complètement dès qu’un enfant s’engage sur la chaussée.'
WHERE rs.sign_code = 'A23' AND q.question_ref = 'A23_Q05' AND c.display_order = 3;

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.question_ar = 'ما الفرق بين علامة تحذير: أطفال وعلامة ممر عبور المشاة؟',
    q.question_en = 'What is the difference between a children warning sign and a pedestrian crossing sign?',
    q.question_nl = 'Wat is het verschil tussen een waarschuwingsbord voor kinderen en een bord voor een voetgangersoversteekplaats?',
    q.question_fr = 'Quelle est la différence entre un panneau signalant des enfants et un panneau de passage pour piétons ?',
    q.explanation_ar = 'علامة تحذير: أطفال تنبّه إلى احتمال وجود أطفال بالقرب من الطريق أو اندفاعهم إليه فجأة، أما علامة ممر عبور المشاة فتشير إلى مكان عبور مخصص يجب فيه احترام أولوية المشاة.',
    q.explanation_en = 'A children warning sign warns of children near the road or of children who may suddenly enter it. A pedestrian crossing sign indicates a designated crossing where you must respect pedestrians'' priority.',
    q.explanation_nl = 'Een waarschuwingsbord voor kinderen waarschuwt voor kinderen in de buurt van de weg of voor kinderen die plots de rijbaan kunnen oplopen. Een bord voor een voetgangersoversteekplaats duidt een vaste oversteek aan waar u de voorrang van voetgangers moet respecteren.',
    q.explanation_fr = 'Un panneau signalant des enfants avertit de la présence d’enfants près de la route ou d’enfants susceptibles de s’y engager soudainement. Un panneau de passage pour piétons indique une traversée aménagée où vous devez respecter la priorité des piétons.'
WHERE rs.sign_code = 'A23' AND q.question_ref = 'A23_Q06';

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 0,
    c.text_ar = 'علامة ممر عبور المشاة أخطر من علامة تحذير: أطفال وتستلزم سرعة أقل دائمًا',
    c.text_en = 'A pedestrian crossing sign is more dangerous than a children warning sign and always requires a lower speed.',
    c.text_nl = 'Een bord voor een voetgangersoversteekplaats is gevaarlijker dan een waarschuwingsbord voor kinderen en vereist altijd een lagere snelheid.',
    c.text_fr = 'Un panneau de passage pour piétons est plus dangereux qu’un panneau signalant des enfants et impose toujours une vitesse plus faible.'
WHERE rs.sign_code = 'A23' AND q.question_ref = 'A23_Q06' AND c.display_order = 1;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 1,
    c.text_ar = 'علامة تحذير: أطفال تنبّه إلى منطقة قد يظهر فيها الأطفال فجأة؛ وعلامة ممر عبور المشاة تحدد ممرًا مخصصًا يجب فيه احترام أولوية المشاة',
    c.text_en = 'A children warning sign warns of an area where children may appear suddenly; a pedestrian crossing sign marks a designated crossing where pedestrians have priority.',
    c.text_nl = 'Een waarschuwingsbord voor kinderen waarschuwt voor een zone waar kinderen plots kunnen verschijnen; een bord voor een voetgangersoversteekplaats duidt een vaste oversteek aan waar voetgangers voorrang hebben.',
    c.text_fr = 'Un panneau signalant des enfants avertit d’une zone où des enfants peuvent surgir soudainement ; un panneau de passage pour piétons indique une traversée aménagée où les piétons ont la priorité.'
WHERE rs.sign_code = 'A23' AND q.question_ref = 'A23_Q06' AND c.display_order = 2;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 0,
    c.text_ar = 'علامة تحذير: أطفال وعلامة ممر عبور المشاة متطابقتان تمامًا في المعنى والحكم',
    c.text_en = 'A children warning sign and a pedestrian crossing sign mean exactly the same thing.',
    c.text_nl = 'Een waarschuwingsbord voor kinderen en een bord voor een voetgangersoversteekplaats betekenen exact hetzelfde.',
    c.text_fr = 'Un panneau signalant des enfants et un panneau de passage pour piétons ont exactement la même signification.'
WHERE rs.sign_code = 'A23' AND q.question_ref = 'A23_Q06' AND c.display_order = 3;

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.question_ar = 'ترى علامة تحذير بوجود أطفال، لكن لا يوجد أطفال ظاهرون. هل يجب عليك مع ذلك تخفيف السرعة وزيادة الانتباه؟',
    q.question_en = 'You see a children warning sign, but no children are visible. Do you still have to reduce your speed and be extra alert?',
    q.question_nl = 'U ziet een waarschuwingsbord voor kinderen, maar er zijn geen kinderen zichtbaar. Moet u toch uw snelheid verlagen en extra oplettend zijn?',
    q.question_fr = 'Vous voyez un panneau signalant des enfants, mais aucun enfant n’est visible. Devez-vous malgré tout réduire votre vitesse et redoubler de vigilance ?',
    q.explanation_ar = 'وجود هذه العلامة يعني أن احتمال ظهور الأطفال قائم دائمًا، لذلك يجب تخفيف السرعة والانتباه حتى إن لم ترَ أحدًا في تلك اللحظة.',
    q.explanation_en = 'The presence of this sign means that children may appear at any time. You must therefore reduce speed and remain attentive even if you do not currently see any children.',
    q.explanation_nl = 'De aanwezigheid van dit bord betekent dat kinderen op elk moment kunnen verschijnen. U moet daarom uw snelheid verlagen en aandachtig blijven, ook als u op dat moment geen kinderen ziet.',
    q.explanation_fr = 'La présence de ce panneau signifie que des enfants peuvent apparaître à tout moment. Vous devez donc réduire votre vitesse et rester attentif même si vous ne voyez actuellement aucun enfant.'
WHERE rs.sign_code = 'A23' AND q.question_ref = 'A23_Q07';

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 0,
    c.text_ar = 'لا، ما دامت الطريق خالية يمكن متابعة السير بالسرعة العادية',
    c.text_en = 'No, if no children are visible you may continue at the normal speed.',
    c.text_nl = 'Nee, als er geen kinderen zichtbaar zijn mag u aan normale snelheid verder rijden.',
    c.text_fr = 'Non, si aucun enfant n’est visible vous pouvez continuer à vitesse normale.'
WHERE rs.sign_code = 'A23' AND q.question_ref = 'A23_Q07' AND c.display_order = 1;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 1,
    c.text_ar = 'نعم، لأن الأطفال قد يظهرون فجأة حتى لو لم يكونوا مرئيين الآن',
    c.text_en = 'Yes, because children may appear suddenly even if none are visible right now.',
    c.text_nl = 'Ja, want kinderen kunnen plots verschijnen, ook als u er nu geen ziet.',
    c.text_fr = 'Oui, parce que des enfants peuvent surgir soudainement même si vous n’en voyez pas pour le moment.'
WHERE rs.sign_code = 'A23' AND q.question_ref = 'A23_Q07' AND c.display_order = 2;

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.question_ar = 'هل يُسمح بالتجاوز في منطقة تحمل علامة تحذير: أطفال؟',
    q.question_en = 'Is overtaking allowed in an area marked by a children warning sign?',
    q.question_nl = 'Is inhalen toegestaan in een gebied met een waarschuwingsbord voor kinderen?',
    q.question_fr = 'Le dépassement est-il autorisé dans une zone signalée par un panneau indiquant des enfants ?',
    q.explanation_ar = 'لا يُسمح بالتجاوز في منطقة قد يظهر فيها الأطفال بشكل مفاجئ، لأن ذلك يزيد الخطر ويقلل مجال الرؤية.',
    q.explanation_en = 'Overtaking is not allowed in an area where children may suddenly appear, because it increases the danger and reduces visibility.',
    q.explanation_nl = 'Inhalen is niet toegestaan in een gebied waar kinderen plots kunnen verschijnen, omdat dit het gevaar vergroot en het zicht beperkt.',
    q.explanation_fr = 'Le dépassement n’est pas autorisé dans une zone où des enfants peuvent apparaître soudainement, car cela augmente le danger et réduit la visibilité.'
WHERE rs.sign_code = 'A23' AND q.question_ref = 'A23_Q08';

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 1,
    c.text_ar = 'لا، التجاوز محظور في المناطق التي قد يعبر فيها الأطفال الطريق فجأة',
    c.text_en = 'No, overtaking is prohibited in areas where children may cross the road unexpectedly.',
    c.text_nl = 'Nee, inhalen is verboden in gebieden waar kinderen plots de weg kunnen oversteken.',
    c.text_fr = 'Non, le dépassement est interdit dans les zones où des enfants peuvent traverser soudainement.'
WHERE rs.sign_code = 'A23' AND q.question_ref = 'A23_Q08' AND c.display_order = 1;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 0,
    c.text_ar = 'نعم، يُسمح بالتجاوز إذا كان الطريق واسعًا بما يكفي ولا يوجد أطفال ظاهرون',
    c.text_en = 'Yes, overtaking is allowed if the road is wide enough and no children are visible.',
    c.text_nl = 'Ja, inhalen is toegestaan als de weg breed genoeg is en er geen kinderen zichtbaar zijn.',
    c.text_fr = 'Oui, le dépassement est autorisé si la route est assez large et qu’aucun enfant n’est visible.'
WHERE rs.sign_code = 'A23' AND q.question_ref = 'A23_Q08' AND c.display_order = 2;

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.question_ar = 'أنت تقود ليلاً بسرعة 50 كم/ساعة وتقترب من هذه العلامة المرورية. أحد المشاة يقف عند حافة ممر المشاة. ماذا تفعل؟',
    q.question_en = 'You are driving at night at 50 km/h and approach this sign. A pedestrian is standing at the edge of the zebra crossing. What do you do?',
    q.question_nl = 'U rijdt ''s nachts aan 50 km/u en nadert dit bord. Een voetganger staat aan de rand van het zebrapad. Wat doet u?',
    q.question_fr = 'Vous roulez de nuit à 50 km/h et approchez ce panneau. Un piéton se tient au bord du passage clouté. Que faites-vous ?',
    q.explanation_ar = 'ليلاً تكون الرؤية محدودة والمشاة الذي ينتظر عند ممر المشاة لديه الحق في العبور. أنت ملزم بالتوقف.',
    q.explanation_en = 'At night visibility is reduced and a pedestrian waiting at a zebra crossing has the right to cross. You are required to stop.',
    q.explanation_nl = '''s Nachts is de zichtbaarheid verminderd en een voetganger die staat te wachten bij een zebrapad heeft het recht om over te steken. U bent verplicht te stoppen.',
    q.explanation_fr = 'De nuit, la visibilité est réduite et un piéton attendant à un passage clouté a le droit de traverser. Vous êtes obligé de vous arrêter.'
WHERE rs.sign_code = 'A21' AND q.question_ref = 'A21_Q05';

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 1,
    c.text_ar = 'تقليل السرعة والتوقف قبل الممر والسماح للمشاة بالعبور',
    c.text_en = 'Reduce speed, stop before the crossing and let the pedestrian cross',
    c.text_nl = 'Snelheid verminderen, stoppen voor het zebrapad en de voetganger laten oversteken',
    c.text_fr = 'Réduire la vitesse, s''arrêter avant le passage et laisser le piéton traverser'
WHERE rs.sign_code = 'A21' AND q.question_ref = 'A21_Q05' AND c.display_order = 1;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 0,
    c.text_ar = 'أواصل السير لأن المشاة ما زال عند الحافة ولم يبدأ العبور بعد',
    c.text_en = 'Continue since the pedestrian is at the edge and has not yet stepped onto the crossing',
    c.text_nl = 'Doorrijden want de voetganger staat aan de rand en loopt nog niet',
    c.text_fr = 'Continuer car le piéton est au bord et n''a pas encore commencé à traverser'
WHERE rs.sign_code = 'A21' AND q.question_ref = 'A21_Q05' AND c.display_order = 2;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 0,
    c.text_ar = 'أستخدم المنبّه حتى يعرف المشاة أنني قادم',
    c.text_en = 'Sound your horn so the pedestrian knows you are approaching',
    c.text_nl = 'Claxonneren zodat de voetganger weet dat u aankomt',
    c.text_fr = 'Klaxonner pour que le piéton sache que vous arrivez'
WHERE rs.sign_code = 'A21' AND q.question_ref = 'A21_Q05' AND c.display_order = 3;

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.question_ar = 'ما معنى هذه العلامة المرورية؟',
    q.question_en = 'What does this traffic sign mean?',
    q.question_nl = 'Wat betekent dit verkeersbord?',
    q.question_fr = 'Que signifie ce panneau de signalisation ?',
    q.explanation_ar = 'تحذّر A25 من أن الدراجين وراكبي الدراجات البخارية قد يعبرون الطريق هنا. يجب مراعاة هؤلاء المستخدمين وإعطائهم الأولوية عند العبور.',
    q.explanation_en = 'A25 warns that cyclists and moped riders may cross the road here. You must be aware of these road users and give way to them when crossing.',
    q.explanation_nl = 'A25 waarschuwt dat fietsers en bromfietsers hier de rijbaan oversteken. U moet rekening houden met deze weggebruikers en hen voorrang verlenen als zij oversteken.',
    q.explanation_fr = 'A25 indique que des cyclistes et cyclomotoristes peuvent traverser la chaussée ici. Vous devez tenir compte de ces usagers et leur céder la priorité lorsqu''ils traversent.'
WHERE rs.sign_code = 'A25' AND q.question_ref = 'A25_Q01';

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 1,
    c.text_ar = 'ممر عبور الدراجات والدراجات البخارية: يمكنهم عبور الطريق هنا',
    c.text_en = 'Bicycle and moped crossing: cyclists and mopeds may cross the road here',
    c.text_nl = 'Oversteekplaats voor fietsers en bromfietsers: zij kunnen hier de rijbaan oversteken',
    c.text_fr = 'Traversée de cyclistes et cyclomoteurs: ils peuvent traverser la chaussée ici'
WHERE rs.sign_code = 'A25' AND q.question_ref = 'A25_Q01' AND c.display_order = 1;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 0,
    c.text_ar = 'علامة ممر عبور المشاة — ممر مخصص يجب فيه إعطاء الأولوية للمشاة',
    c.text_en = 'Pedestrian crossing — a designated crossing where pedestrians must be given priority',
    c.text_nl = 'Oversteekplaats voor voetgangers — een aangeduide oversteek waar voetgangers voorrang hebben',
    c.text_fr = 'Passage pour piétons — une traversée aménagée où les piétons ont la priorité'
WHERE rs.sign_code = 'A25' AND q.question_ref = 'A25_Q01' AND c.display_order = 2;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 0,
    c.text_ar = 'علامة تحذير: أطفال — منطقة قريبة من المدارس قد يندفع فيها الأطفال نحو الطريق',
    c.text_en = 'Warning: children — a zone near schools where children may suddenly enter the road',
    c.text_nl = 'Opgelet kinderen: een zone nabij scholen waar kinderen onverwacht de weg kunnen oplopen',
    c.text_fr = 'Attention : enfants — une zone proche des écoles où des enfants peuvent surgir sur la route'
WHERE rs.sign_code = 'A25' AND q.question_ref = 'A25_Q01' AND c.display_order = 3;

UPDATE sign_questions q
JOIN road_signs rs ON rs.id = q.sign_id
SET
    q.question_ar = 'ماذا يجب عليك فعله عند رؤية هذه العلامة المرورية؟',
    q.question_en = 'What must you do when you see this sign?',
    q.question_nl = 'Wat moet u doen wanneer u dit bord ziet?',
    q.question_fr = 'Que devez-vous faire lorsque vous voyez ce panneau ?',
    q.explanation_ar = 'تلزمك A25 بتكييف سلوكك: تقليل السرعة والانتباه بشكل خاص للدراجين وراكبي الدراجات البخارية العابرين. يمكن أن يظهروا بسرعة وبشكل غير متوقع.',
    q.explanation_en = 'A25 requires you to adapt your behaviour: reduce speed and pay extra attention to crossing cyclists and moped riders. They can appear quickly and unexpectedly.',
    q.explanation_nl = 'A25 verplicht u uw rijgedrag aan te passen: snelheid verminderen en extra aandacht hebben voor overstekende fietsers en bromfietsers. Zij kunnen snel en onverwacht verschijnen.',
    q.explanation_fr = 'A25 vous oblige à adapter votre comportement: réduire la vitesse et porter une attention particulière aux cyclistes et cyclomotoristes qui traversent. Ils peuvent apparaître rapidement et de manière inattendue.'
WHERE rs.sign_code = 'A25' AND q.question_ref = 'A25_Q04';

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 1,
    c.text_ar = 'تقليل السرعة والانتباه جيداً للدراجين وراكبي الدراجات البخارية الذين قد يعبرون الطريق',
    c.text_en = 'Reduce speed and watch carefully for cyclists and moped riders who may cross the road',
    c.text_nl = 'Snelheid verminderen en extra uitkijken naar fietsers en bromfietsers die de rijbaan kunnen oversteken',
    c.text_fr = 'Réduire la vitesse et surveiller attentivement les cyclistes et cyclomotoristes pouvant traverser la chaussée'
WHERE rs.sign_code = 'A25' AND q.question_ref = 'A25_Q04' AND c.display_order = 1;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 0,
    c.text_ar = 'أستخدم المنبّه لتنبيه الدراجين ثم أتابع السير بالسرعة العادية',
    c.text_en = 'Sound your horn to warn cyclists and then continue at normal speed',
    c.text_nl = 'Claxonneren om fietsers te waarschuwen en daarna op normale snelheid doorrijden',
    c.text_fr = 'Klaxonner pour avertir les cyclistes puis continuer à vitesse normale'
WHERE rs.sign_code = 'A25' AND q.question_ref = 'A25_Q04' AND c.display_order = 2;

UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET
    c.is_correct = 0,
    c.text_ar = 'لا شيء خاصاً: على الدراجين أنفسهم إعطاء الأولوية لحركة المرور العابرة',
    c.text_en = 'Nothing special: cyclists must themselves give way to through traffic',
    c.text_nl = 'Niets bijzonders: fietsers moeten zelf voorrang verlenen aan rijverkeer',
    c.text_fr = 'Rien de spécial: les cyclistes doivent eux-mêmes céder la priorité à la circulation'
WHERE rs.sign_code = 'A25' AND q.question_ref = 'A25_Q04' AND c.display_order = 3;
