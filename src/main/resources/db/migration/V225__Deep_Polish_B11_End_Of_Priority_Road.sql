-- Deep polish B11 (end of priority road) learner-facing texts in four languages.
-- Generated from source JSON to keep source and live data aligned.

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sq.question_ar = 'ما معنى هذه العلامة المرورية؟',
  sq.question_en = 'What does this traffic sign mean?',
  sq.question_nl = 'Wat betekent dit verkeersbord?',
  sq.question_fr = 'Que signifie ce panneau de signalisation ?',
  sq.explanation_ar = 'تُشير هذه العلامة المرورية إلى انتهاء طريق الأولوية. من هذه النقطة تزول أولوية المرور الخاصة بك، وتعود قواعد الأولوية العادية إلى التطبيق.',
  sq.explanation_en = 'This traffic sign indicates the end of a priority road. From this point, your special right of way ends and the normal priority rules apply again.',
  sq.explanation_nl = 'Dit verkeersbord geeft het einde van een voorrangsweg aan. Vanaf dit punt vervalt uw bijzondere voorrang en gelden opnieuw de normale voorrangsregels.',
  sq.explanation_fr = 'Ce panneau de signalisation indique la fin d''une route prioritaire. À partir de ce point, votre droit de priorité spécial prend fin et les règles de priorité normales s''appliquent à nouveau.'
WHERE rs.sign_code = 'B11' AND sq.question_ref = 'B11_Q01';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'ينتهي طريق الأولوية هنا، ومن هذه النقطة تسري قواعد الأولوية العادية',
  sc.text_en = 'The priority road ends here, and from this point the normal priority rules apply',
  sc.text_nl = 'De voorrangsweg eindigt hier, en vanaf dit punt gelden de normale voorrangsregels',
  sc.text_fr = 'La route prioritaire se termine ici, et à partir de ce point les règles de priorité normales s''appliquent',
  sc.is_correct = 1
WHERE rs.sign_code = 'B11' AND sq.question_ref = 'B11_Q01' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'أنت تدخل طريق أولوية جديدًا دون إشارة إضافية',
  sc.text_en = 'You are entering a new priority road without further indication',
  sc.text_nl = 'U rijdt een nieuwe voorrangsweg op zonder extra aanduiding',
  sc.text_fr = 'Vous entrez sur une nouvelle route prioritaire sans indication supplémentaire',
  sc.is_correct = 0
WHERE rs.sign_code = 'B11' AND sq.question_ref = 'B11_Q01' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'لديك الأولوية على جميع المركبات القادمة من اليمين',
  sc.text_en = 'You have priority over all vehicles coming from the right',
  sc.text_nl = 'U heeft voorrang op alle voertuigen die van rechts komen',
  sc.text_fr = 'Vous avez la priorité sur tous les véhicules venant de droite',
  sc.is_correct = 0
WHERE rs.sign_code = 'B11' AND sq.question_ref = 'B11_Q01' AND sc.display_order = 3;

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sq.question_ar = 'أي علامة مرورية تشير إلى انتهاء طريق الأولوية؟',
  sq.question_en = 'Which sign indicates the end of a priority road?',
  sq.question_nl = 'Welk bord geeft het einde van een voorrangsweg aan?',
  sq.question_fr = 'Quel panneau indique la fin d''une route prioritaire ?',
  sq.explanation_ar = 'علامة طريق الأولوية تدل على بداية طريق الأولوية، أما هذه العلامة فتدل على نهايته. بعد هذه العلامة تعود قواعد الأولوية العادية إلى التطبيق.',
  sq.explanation_en = 'The priority road sign marks the start of a priority road, while this sign marks its end. After this sign, the normal priority rules apply again.',
  sq.explanation_nl = 'Het bord voorrangsweg markeert het begin van een voorrangsweg, terwijl dit bord het einde aangeeft. Na dit bord gelden opnieuw de normale voorrangsregels.',
  sq.explanation_fr = 'Le panneau route prioritaire marque le début d''une route prioritaire, tandis que ce panneau en marque la fin. Après ce panneau, les règles de priorité normales s''appliquent à nouveau.'
WHERE rs.sign_code = 'B11' AND sq.question_ref = 'B11_Q02';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'علامة نهاية طريق الأولوية',
  sc.text_en = 'End of priority road',
  sc.text_nl = 'Einde voorrangsweg',
  sc.text_fr = 'Fin de route prioritaire',
  sc.is_correct = 1
WHERE rs.sign_code = 'B11' AND sq.question_ref = 'B11_Q02' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'علامة طريق الأولوية',
  sc.text_en = 'Priority road',
  sc.text_nl = 'Voorrangsweg',
  sc.text_fr = 'Route prioritaire',
  sc.is_correct = 0
WHERE rs.sign_code = 'B11' AND sq.question_ref = 'B11_Q02' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'علامة إعطاء الأولوية',
  sc.text_en = 'Give way',
  sc.text_nl = 'Voorrang verlenen',
  sc.text_fr = 'Cédez le passage',
  sc.is_correct = 0
WHERE rs.sign_code = 'B11' AND sq.question_ref = 'B11_Q02' AND sc.display_order = 3;

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sq.question_ar = 'ما الخطر الذي تشير إليه هذه العلامة المرورية؟',
  sq.question_en = 'What hazard does this traffic sign warn you about?',
  sq.question_nl = 'Voor welk gevaar waarschuwt dit verkeersbord u?',
  sq.question_fr = 'Quel danger ce panneau de signalisation annonce-t-il ?',
  sq.explanation_ar = 'بعد هذه العلامة المرورية تفقد أولوية المرور الخاصة بك. لذلك يجب عليك من جديد الانتباه إلى المركبات الأخرى وتطبيق قواعد الأولوية العادية، ما لم توجد علامات أخرى.',
  sq.explanation_en = 'After this sign, you lose your special right of way. You must again watch for other road users and apply the normal priority rules unless other signs indicate otherwise.',
  sq.explanation_nl = 'Na dit bord verliest u uw bijzondere voorrang. U moet opnieuw letten op andere weggebruikers en de normale voorrangsregels toepassen, tenzij andere borden iets anders aangeven.',
  sq.explanation_fr = 'Après ce panneau, vous perdez votre priorité spéciale. Vous devez à nouveau être attentif aux autres usagers et appliquer les règles de priorité normales, sauf indication contraire d''autres panneaux.'
WHERE rs.sign_code = 'B11' AND sq.question_ref = 'B11_Q03';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'لن تبقى لك أولوية خاصة، وعليك الانتباه إلى المركبات التي قد تكون لها الأولوية',
  sc.text_en = 'Your special priority ends here, so you must watch for drivers who may now have priority',
  sc.text_nl = 'Uw bijzondere voorrang eindigt hier, dus u moet opletten voor bestuurders die nu mogelijk voorrang hebben',
  sc.text_fr = 'Votre priorité spéciale prend fin ici ; vous devez donc surveiller les conducteurs qui peuvent désormais être prioritaires',
  sc.is_correct = 1
WHERE rs.sign_code = 'B11' AND sq.question_ref = 'B11_Q03' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'هناك خطر من مركبات تنعطف أمامك مباشرة',
  sc.text_en = 'There is a danger of vehicles turning directly in front of you',
  sc.text_nl = 'Er is gevaar voor voertuigen die vlak voor u afslaan',
  sc.text_fr = 'Il y a un risque de véhicules qui tournent juste devant vous',
  sc.is_correct = 0
WHERE rs.sign_code = 'B11' AND sq.question_ref = 'B11_Q03' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'هناك خطر من حركة مرور قادمة على طريق أولوية تدخله',
  sc.text_en = 'There is a danger of oncoming traffic on a priority road you are entering',
  sc.text_nl = 'Er is gevaar voor tegemoetkomend verkeer op een voorrangsweg die u oprijdt',
  sc.text_fr = 'Il y a un risque de circulation venant en sens inverse sur une route prioritaire que vous empruntez',
  sc.is_correct = 0
WHERE rs.sign_code = 'B11' AND sq.question_ref = 'B11_Q03' AND sc.display_order = 3;

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sq.question_ar = 'ماذا يجب عليك فعله مباشرة بعد تجاوز هذه العلامة المرورية؟',
  sq.question_en = 'What must you do immediately after passing this traffic sign?',
  sq.question_nl = 'Wat moet u doen onmiddellijk nadat u dit verkeersbord bent gepasseerd?',
  sq.question_fr = 'Que devez-vous faire immédiatement après avoir dépassé ce panneau de signalisation ?',
  sq.explanation_ar = 'مباشرة بعد هذه العلامة تطبق قواعد الأولوية العادية. راقب العلامات الموجودة عند التقاطعات التالية، وإذا لم توجد علامات أخرى فتسري قاعدة أولوية اليمين.',
  sq.explanation_en = 'Immediately after this sign, you must apply the normal priority rules. Check the signs at the next junctions; if there are no other signs, right-of-way from the right applies.',
  sq.explanation_nl = 'Onmiddellijk na dit bord moet u de normale voorrangsregels toepassen. Let op de borden bij de volgende kruispunten; zonder andere borden geldt de voorrang van rechts.',
  sq.explanation_fr = 'Immédiatement après ce panneau, vous devez appliquer les règles de priorité normales. Vérifiez la signalisation aux carrefours suivants ; sans autre panneau, la priorité de droite s''applique.'
WHERE rs.sign_code = 'B11' AND sq.question_ref = 'B11_Q04';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'أطبق قواعد المرور العادية، وما لم توجد علامات أخرى تسري قاعدة أولوية اليمين',
  sc.text_en = 'Apply the normal traffic rules; unless other signs are present, right-of-way from the right applies',
  sc.text_nl = 'De normale verkeersregels toepassen; tenzij er andere borden staan, geldt de voorrang van rechts',
  sc.text_fr = 'Appliquer les règles de circulation normales ; sauf autre signalisation, la priorité de droite s''applique',
  sc.is_correct = 1
WHERE rs.sign_code = 'B11' AND sq.question_ref = 'B11_Q04' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'أستمر بالسرعة نفسها لأن هذه العلامة لا تؤثر في طريقة قيادتي',
  sc.text_en = 'Continue at the same speed because this sign does not affect your driving',
  sc.text_nl = 'Met dezelfde snelheid doorrijden omdat dit bord geen invloed heeft op mijn rijgedrag',
  sc.text_fr = 'Continuer à la même vitesse parce que ce panneau n''a aucun effet sur ma conduite',
  sc.is_correct = 0
WHERE rs.sign_code = 'B11' AND sq.question_ref = 'B11_Q04' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'أتوقف بالكامل لأن نهاية طريق الأولوية تعني دائمًا التوقف الإجباري',
  sc.text_en = 'Stop completely because the end of a priority road always means a mandatory stop',
  sc.text_nl = 'Volledig stoppen omdat het einde van een voorrangsweg altijd een verplichte stop betekent',
  sc.text_fr = 'M''arrêter complètement parce que la fin d''une route prioritaire signifie toujours un arrêt obligatoire',
  sc.is_correct = 0
WHERE rs.sign_code = 'B11' AND sq.question_ref = 'B11_Q04' AND sc.display_order = 3;

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sq.question_ar = 'لقد تجاوزت للتو هذه العلامة المرورية وتقترب من تقاطع غير منظَّم من دون علامات. تأتي مركبة من اليمين. ماذا تفعل؟',
  sq.question_en = 'You have just passed this traffic sign and are approaching an uncontrolled junction with no signs. A vehicle comes from the right. What do you do?',
  sq.question_nl = 'U bent dit verkeersbord net gepasseerd en nadert een ongeregeld kruispunt zonder borden. Er komt een voertuig van rechts. Wat doet u?',
  sq.question_fr = 'Vous venez de dépasser ce panneau de signalisation et vous approchez d''un carrefour non réglementé sans panneaux. Un véhicule vient de droite. Que faites-vous ?',
  sq.explanation_ar = 'بعد هذه العلامة تعود قواعد الأولوية العادية. وعند تقاطع غير منظَّم من دون علامات، تكون الأولوية للمركبة القادمة من اليمين.',
  sq.explanation_en = 'After this sign, the normal priority rules apply again. At an uncontrolled junction without signs, the vehicle coming from the right has priority.',
  sq.explanation_nl = 'Na dit bord gelden opnieuw de normale voorrangsregels. Op een ongeregeld kruispunt zonder borden heeft het voertuig van rechts voorrang.',
  sq.explanation_fr = 'Après ce panneau, les règles de priorité normales s''appliquent à nouveau. À un carrefour non réglementé sans signalisation, le véhicule venant de droite a la priorité.'
WHERE rs.sign_code = 'B11' AND sq.question_ref = 'B11_Q05';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'أعطي الأولوية للمركبة القادمة من اليمين لأن قاعدة أولوية اليمين تعود إلى التطبيق',
  sc.text_en = 'Give way to the vehicle from the right because right-of-way from the right applies again',
  sc.text_nl = 'Voorrang verlenen aan het voertuig van rechts omdat de voorrang van rechts opnieuw geldt',
  sc.text_fr = 'Céder le passage au véhicule venant de droite parce que la priorité de droite redevient applicable',
  sc.is_correct = 1
WHERE rs.sign_code = 'B11' AND sq.question_ref = 'B11_Q05' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'أواصل السير لأنني كنت قبل قليل على طريق له أولوية',
  sc.text_en = 'Continue because a moment ago you were still on a priority road',
  sc.text_nl = 'Doorrijden omdat u daarnet nog op een voorrangsweg reed',
  sc.text_fr = 'Continuer parce que vous étiez encore il y a un instant sur une route prioritaire',
  sc.is_correct = 0
WHERE rs.sign_code = 'B11' AND sq.question_ref = 'B11_Q05' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'أستخدم المنبّه لأطلب من المركبة القادمة من اليمين أن تتوقف',
  sc.text_en = 'Use your horn to tell the vehicle from the right to stop',
  sc.text_nl = 'Claxonneren om het voertuig van rechts te laten stoppen',
  sc.text_fr = 'Klaxonner pour demander au véhicule venant de droite de s''arrêter',
  sc.is_correct = 0
WHERE rs.sign_code = 'B11' AND sq.question_ref = 'B11_Q05' AND sc.display_order = 3;

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sq.question_ar = 'ما الفرق بين علامة طريق الأولوية وعلامة انتهاء طريق الأولوية؟',
  sq.question_en = 'What is the difference between the priority road sign and the end-of-priority-road sign?',
  sq.question_nl = 'Wat is het verschil tussen het bord voorrangsweg en het bord einde voorrangsweg?',
  sq.question_fr = 'Quelle est la différence entre le panneau route prioritaire et le panneau fin de route prioritaire ?',
  sq.explanation_ar = 'علامة طريق الأولوية تعني أنك تسير على طريق له أولوية. أما علامة انتهاء طريق الأولوية فتعني زوال هذه الأفضلية وعودة قواعد الأولوية العادية.',
  sq.explanation_en = 'The priority road sign means that you are driving on a road with priority. The end-of-priority-road sign means that this special right of way ends and the normal priority rules apply again.',
  sq.explanation_nl = 'Het bord voorrangsweg betekent dat u op een weg rijdt met voorrang. Het bord einde voorrangsweg betekent dat deze bijzondere voorrang stopt en dat de normale voorrangsregels opnieuw gelden.',
  sq.explanation_fr = 'Le panneau route prioritaire signifie que vous circulez sur une route bénéficiant de la priorité. Le panneau fin de route prioritaire signifie que cet avantage prend fin et que les règles de priorité normales s''appliquent à nouveau.'
WHERE rs.sign_code = 'B11' AND sq.question_ref = 'B11_Q06';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'علامة طريق الأولوية تدل على بداية طريق له أولوية، أما علامة انتهاء طريق الأولوية فتدل على نهايته وعودة القواعد العادية',
  sc.text_en = 'The priority road sign marks the beginning of a priority road, while the end-of-priority-road sign marks its end and the return to normal rules',
  sc.text_nl = 'Het bord voorrangsweg duidt het begin van een voorrangsweg aan, terwijl het bord einde voorrangsweg het einde en de terugkeer naar de normale regels aangeeft',
  sc.text_fr = 'Le panneau route prioritaire indique le début d''une route prioritaire, tandis que le panneau fin de route prioritaire en indique la fin et le retour aux règles normales',
  sc.is_correct = 1
WHERE rs.sign_code = 'B11' AND sq.question_ref = 'B11_Q06' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'العلامتان متطابقتان، وكلتاهما تدلان على بداية مقطع طريق جديد',
  sc.text_en = 'The two signs are identical and both indicate the beginning of a new road section',
  sc.text_nl = 'Beide borden zijn identiek en geven allebei het begin van een nieuw wegvak aan',
  sc.text_fr = 'Les deux panneaux sont identiques et indiquent tous deux le début d''un nouveau tronçon de route',
  sc.is_correct = 0
WHERE rs.sign_code = 'B11' AND sq.question_ref = 'B11_Q06' AND sc.display_order = 2;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'علامة انتهاء طريق الأولوية تمنح أولوية مؤقتة حتى التقاطع التالي، أما علامة طريق الأولوية فهي دائمة',
  sc.text_en = 'The end-of-priority-road sign grants temporary priority until the next junction, while the priority road sign is permanent',
  sc.text_nl = 'Het bord einde voorrangsweg verleent tijdelijk voorrang tot het volgende kruispunt, terwijl het bord voorrangsweg permanent is',
  sc.text_fr = 'Le panneau fin de route prioritaire accorde une priorité temporaire jusqu''au prochain carrefour, alors que le panneau route prioritaire est permanent',
  sc.is_correct = 0
WHERE rs.sign_code = 'B11' AND sq.question_ref = 'B11_Q06' AND sc.display_order = 3;

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sq.question_ar = 'لقد تجاوزت للتو هذه العلامة المرورية، لكن لوحة اسم الشارع مفقودة. هل يظل حق الأولوية السابق قائمًا؟',
  sq.question_en = 'You have just passed this sign, but a street name plate is missing. May you still claim the right of way from the earlier priority road?',
  sq.question_nl = 'U bent dit bord net gepasseerd, maar het straatnaambord ontbreekt. Mag u nog steeds aanspraak maken op de voorrang van de eerdere voorrangsweg?',
  sq.question_fr = 'Vous venez de dépasser ce panneau, mais la plaque de nom de rue manque. Pouvez-vous encore revendiquer la priorité de la route prioritaire précédente ?',
  sq.explanation_ar = 'ينتهي حق الأولوية الخاص بطريق الأولوية عند هذه العلامة تحديدًا. لا توجد منطقة انتقالية ولا فترة سماح بعد تجاوزها.',
  sq.explanation_en = 'The special right of way of a priority road ends exactly at this sign. There is no transition zone and no grace period after passing it.',
  sq.explanation_nl = 'De bijzondere voorrang van een voorrangsweg eindigt precies bij dit bord. Er is geen overgangszone en geen uitloopperiode nadat u het bent gepasseerd.',
  sq.explanation_fr = 'Le droit de priorité spécial d''une route prioritaire prend fin exactement à ce panneau. Il n''existe ni zone de transition ni période de tolérance après l''avoir dépassé.'
WHERE rs.sign_code = 'B11' AND sq.question_ref = 'B11_Q07';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'لا، بمجرد تجاوز هذه العلامة ينتهي حق الأولوية فورًا وبشكل كامل',
  sc.text_en = 'No, once you pass this sign, the right of way ends immediately and completely',
  sc.text_nl = 'Nee, zodra u dit bord voorbij bent, vervalt de voorrang onmiddellijk en volledig',
  sc.text_fr = 'Non, dès que vous dépassez ce panneau, la priorité prend fin immédiatement et complètement',
  sc.is_correct = 1
WHERE rs.sign_code = 'B11' AND sq.question_ref = 'B11_Q07' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'نعم، يبقى حق الأولوية ساريًا حتى أول إشارة مرور أو خط توقف بعد هذه العلامة',
  sc.text_en = 'Yes, the right of way remains valid until the first traffic light or stop line after this sign',
  sc.text_nl = 'Ja, de voorrang blijft gelden tot het eerste verkeerslicht of de eerste stopstreep na dit bord',
  sc.text_fr = 'Oui, la priorité reste valable jusqu''au premier feu ou à la première ligne d''arrêt après ce panneau',
  sc.is_correct = 0
WHERE rs.sign_code = 'B11' AND sq.question_ref = 'B11_Q07' AND sc.display_order = 2;

UPDATE sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sq.question_ar = 'بعد هذه العلامة المرورية تقترب من تقاطع توجد عنده علامة تفيد بتطبيق أولوية اليمين. هل تسري عليك قاعدة أولوية اليمين في ذلك التقاطع؟',
  sq.question_en = 'After this traffic sign, you approach a junction marked with a sign showing that right-of-way from the right applies. Does right-of-way from the right apply at that junction?',
  sq.question_nl = 'Na dit verkeersbord nadert u een kruispunt waar een bord aangeeft dat de voorrang van rechts geldt. Geldt daar de voorrang van rechts?',
  sq.question_fr = 'Après ce panneau de signalisation, vous approchez d''un carrefour où un panneau indique que la priorité de droite s''applique. La priorité de droite s''applique-t-elle à ce carrefour ?',
  sq.explanation_ar = 'بعد هذه العلامة، تسري القواعد والعلامات الموجودة عند التقاطع التالي. وإذا وُجدت علامة تفيد بتطبيق أولوية اليمين، فإن هذه القاعدة تسري على جميع السائقين في ذلك التقاطع.',
  sq.explanation_en = 'After this sign, the rules and signs present at the next junction apply. If there is a sign showing that right-of-way from the right applies, that rule applies to all drivers at that junction.',
  sq.explanation_nl = 'Na dit bord gelden de regels en borden die bij het volgende kruispunt aanwezig zijn. Staat daar een bord dat de voorrang van rechts aangeeft, dan geldt die regel voor alle bestuurders op dat kruispunt.',
  sq.explanation_fr = 'Après ce panneau, ce sont les règles et la signalisation présentes au carrefour suivant qui s''appliquent. S''il y a un panneau indiquant la priorité de droite, cette règle vaut pour tous les conducteurs à ce carrefour.'
WHERE rs.sign_code = 'B11' AND sq.question_ref = 'B11_Q08';

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'نعم، عندما توجد علامة تفيد بتطبيق أولوية اليمين، فإن هذه القاعدة تسري على جميع السائقين في ذلك التقاطع',
  sc.text_en = 'Yes, when there is a sign showing right-of-way from the right, that rule applies to all drivers at that junction',
  sc.text_nl = 'Ja, wanneer er een bord staat dat de voorrang van rechts aangeeft, geldt die regel voor alle bestuurders op dat kruispunt',
  sc.text_fr = 'Oui, lorsqu''un panneau indique la priorité de droite, cette règle s''applique à tous les conducteurs à ce carrefour',
  sc.is_correct = 1
WHERE rs.sign_code = 'B11' AND sq.question_ref = 'B11_Q08' AND sc.display_order = 1;

UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
SET
  sc.text_ar = 'لا، بعد هذه العلامة تبقى لك الأولوية دائمًا مهما كانت العلامة الموجودة عند التقاطع',
  sc.text_en = 'No, after this sign you always keep priority regardless of the sign at the junction',
  sc.text_nl = 'Nee, na dit bord behoudt u altijd voorrang, ongeacht het bord bij het kruispunt',
  sc.text_fr = 'Non, après ce panneau vous gardez toujours la priorité, quelle que soit la signalisation du carrefour',
  sc.is_correct = 0
WHERE rs.sign_code = 'B11' AND sq.question_ref = 'B11_Q08' AND sc.display_order = 2;
