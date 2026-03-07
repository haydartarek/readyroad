-- Answer options for questions 124-142 (3 options each, 1 correct)

-- ID 124: What shape is a danger sign in Belgium?
INSERT INTO quiz_answer_options (question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order) VALUES
(124, 'Triangular with a red border', 'مثلثة الشكل ذات حدود حمراء', 'Driehoekig met een rode rand', 'Triangulaire avec un bord rouge', 1, 1),
(124, 'Circular with a red border', 'دائرية ذات حدود حمراء', 'Rond met een rode rand', 'Circulaire avec un bord rouge', 0, 2),
(124, 'Square with a blue border', 'مربعة ذات حدود زرقاء', 'Vierkant met een blauwe rand', 'Carre avec un bord bleu', 0, 3);

-- ID 125: What colour is the background of a priority road sign?
INSERT INTO quiz_answer_options (question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order) VALUES
(125, 'Yellow with a white border', 'أصفر مع حدود بيضاء', 'Geel met een witte rand', 'Jaune avec un bord blanc', 1, 1),
(125, 'Blue with a white border', 'أزرق مع حدود بيضاء', 'Blauw met een witte rand', 'Bleu avec un bord blanc', 0, 2),
(125, 'Red with a white border', 'أحمر مع حدود بيضاء', 'Rood met een witte rand', 'Rouge avec un bord blanc', 0, 3);

-- ID 126: Maximum speed in built-up area
INSERT INTO quiz_answer_options (question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order) VALUES
(126, '50 km/h', '50 كم/ساعة', '50 km/u', '50 km/h', 1, 1),
(126, '70 km/h', '70 كم/ساعة', '70 km/u', '70 km/h', 0, 2),
(126, '30 km/h', '30 كم/ساعة', '30 km/u', '30 km/h', 0, 3);

-- ID 127: Unmarked intersection - right of way
INSERT INTO quiz_answer_options (question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order) VALUES
(127, 'The vehicle coming from the right', 'السيارة القادمة من اليمين', 'Het voertuig van rechts', 'Le vehicule venant de droite', 1, 1),
(127, 'The vehicle coming from the left', 'السيارة القادمة من اليسار', 'Het voertuig van links', 'Le vehicule venant de gauche', 0, 2),
(127, 'The vehicle that arrived first', 'السيارة التي وصلت أولاً', 'Het voertuig dat als eerste aankwam', 'Le vehicule arrive en premier', 0, 3);

-- ID 128: Solid white line
INSERT INTO quiz_answer_options (question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order) VALUES
(128, 'You must not cross it, overtaking is forbidden', 'لا يجوز تخطيه، التجاوز ممنوع', 'Je mag het niet overschrijden, inhalen is verboden', 'Vous ne devez pas la franchir, le depassement est interdit', 1, 1),
(128, 'You may cross it to overtake if the road is clear', 'يمكن تخطيه للتجاوز إذا كان الطريق واضحاً', 'Je mag het overschrijden om in te halen als de weg vrij is', 'Vous pouvez la franchir pour depasser si la route est libre', 0, 2),
(128, 'It marks the boundary of a bicycle lane', 'تحدد حدود مسار الدراجات', 'Het markeert de grens van een fietsstrook', 'Elle marque la limite d une piste cyclable', 0, 3);

-- ID 129: STOP sign
INSERT INTO quiz_answer_options (question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order) VALUES
(129, 'Stop completely and give way to all traffic', 'توقف تماماً وأعطِ الأولوية لجميع المركبات', 'Volledig stoppen en alle verkeer voorrang geven', 'S arreter completement et ceder la priorite a tous', 1, 1),
(129, 'Slow down and continue if the road is clear', 'أبطئ وتابع إذا كان الطريق واضحاً', 'Vertragen en doorgaan als de weg vrij is', 'Ralentir et continuer si la route est libre', 0, 2),
(129, 'Give way to vehicles from the right only', 'أعطِ الأولوية للمركبات القادمة من اليمين فقط', 'Alleen verkeer van rechts voorrang geven', 'Ceder la priorite uniquement aux vehicules de droite', 0, 3);

-- ID 130: Parking distance from pedestrian crossing
INSERT INTO quiz_answer_options (question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order) VALUES
(130, 'At least 5 metres before the crossing', 'على الأقل 5 أمتار قبل ممر المشاة', 'Minstens 5 meter voor het zebrapad', 'Au moins 5 metres avant le passage pieton', 1, 1),
(130, 'At least 3 metres before the crossing', 'على الأقل 3 أمتار قبل ممر المشاة', 'Minstens 3 meter voor het zebrapad', 'Au moins 3 metres avant le passage pieton', 0, 2),
(130, 'At least 10 metres before the crossing', 'على الأقل 10 أمتار قبل ممر المشاة', 'Minstens 10 meter voor het zebrapad', 'Au moins 10 metres avant le passage pieton', 0, 3);

-- ID 131: Blue circular sign with white arrow
INSERT INTO quiz_answer_options (question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order) VALUES
(131, 'Mandatory direction — you must go in the direction shown', 'اتجاه إلزامي — يجب السير في الاتجاه المبيَّن', 'Verplichte rijrichting — je moet de aangegeven richting volgen', 'Direction obligatoire — vous devez suivre la direction indiquee', 1, 1),
(131, 'Recommended route in that direction', 'طريق موصى به في ذلك الاتجاه', 'Aanbevolen route in die richting', 'Itineraire recommande dans cette direction', 0, 2),
(131, 'No entry from that direction', 'لا يُسمح بالدخول من ذلك الاتجاه', 'Verboden richting van die kant', 'Sens interdit depuis cette direction', 0, 3);

-- ID 132: Minimum following distance on motorway
INSERT INTO quiz_answer_options (question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order) VALUES
(132, 'At least 2 seconds behind the vehicle in front', 'على الأقل ثانيتين خلف السيارة الأمامية', 'Minstens 2 seconden achter het voertuig voor je', 'Au moins 2 secondes derriere le vehicule precedent', 1, 1),
(132, 'At least 1 second behind the vehicle in front', 'على الأقل ثانية واحدة خلف السيارة الأمامية', 'Minstens 1 seconde achter het voertuig voor je', 'Au moins 1 seconde derriere le vehicule precedent', 0, 2),
(132, 'At least 5 seconds behind the vehicle in front', 'على الأقل 5 ثوانٍ خلف السيارة الأمامية', 'Minstens 5 seconden achter het voertuig voor je', 'Au moins 5 secondes derriere le vehicule precedent', 0, 3);

-- ID 133: Hazard warning lights
INSERT INTO quiz_answer_options (question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order) VALUES
(133, 'When your vehicle is a danger or obstruction to other road users', 'عندما تكون سيارتك تمثل خطراً أو عائقاً للآخرين', 'Wanneer je voertuig gevaar oplevert voor andere weggebruikers', 'Quand votre vehicule constitue un danger ou une gene', 1, 1),
(133, 'Whenever you drive slowly in traffic', 'في أي وقت تقود ببطء في حركة المرور', 'Altijd wanneer je langzaam rijdt in het verkeer', 'Des que vous roulez lentement dans la circulation', 0, 2),
(133, 'Only when parked illegally', 'فقط عند الوقوف بشكل غير قانوني', 'Alleen wanneer je illegaal geparkeerd staat', 'Uniquement quand vous etes gare illegalement', 0, 3);

-- ID 134: Yellow diamond sign
INSERT INTO quiz_answer_options (question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order) VALUES
(134, 'You are on a priority road — you have right of way over side roads', 'أنت على طريق ذو أولوية — لك حق المرور على الطرق الجانبية', 'Je rijdt op een voorrangsweg — je hebt voorrang op zijwegen', 'Vous etes sur une route prioritaire — vous avez la priorite', 1, 1),
(134, 'You must give way to all traffic', 'يجب إعطاء الأولوية لجميع المركبات', 'Je moet alle verkeer voorrang geven', 'Vous devez ceder la priorite a tous', 0, 2),
(134, 'You are entering a built-up area', 'أنت تدخل منطقة سكنية', 'Je rijdt een bebouwde kom binnen', 'Vous entrez dans une agglomeration', 0, 3);

-- ID 135: 3 lanes, which lane normally?
INSERT INTO quiz_answer_options (question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order) VALUES
(135, 'The rightmost lane', 'المسار الأيمن', 'De meest rechtse rijstrook', 'La voie la plus a droite', 1, 1),
(135, 'The middle lane', 'المسار الأوسط', 'De middelste rijstrook', 'La voie du milieu', 0, 2),
(135, 'Any lane you choose', 'أي مسار تختاره', 'Elke rijstrook die je wilt', 'La voie de votre choix', 0, 3);

-- ID 136: Speed limit sign end
INSERT INTO quiz_answer_options (question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order) VALUES
(136, 'When you pass an end-of-speed-limit sign or enter a new zone', 'عند تجاوز علامة نهاية حد السرعة أو الدخول إلى منطقة جديدة', 'Als je een einde snelheidsbeperking bord passeert of een nieuwe zone binnenrijdt', 'Quand vous passez un panneau fin de limitation ou entrez dans une nouvelle zone', 1, 1),
(136, 'After exactly 5 kilometres from the sign', 'بعد 5 كيلومترات بالضبط من العلامة', 'Na precies 5 kilometer vanaf het bord', 'Apres exactement 5 kilometres du panneau', 0, 2),
(136, 'When you leave the road and rejoin it', 'عند مغادرة الطريق والعودة إليه', 'Wanneer je de weg verlaat en er weer op rijdt', 'Quand vous quittez la route et la rejoignez', 0, 3);

-- ID 137: Blood alcohol limit novice drivers
INSERT INTO quiz_answer_options (question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order) VALUES
(137, '0.2 g/l (effectively zero tolerance)', '0.2 جرام/لتر (تسامح صفري فعلياً)', '0,2 g/l (in feite nultolerantie)', '0,2 g/l (tolerance zero en pratique)', 1, 1),
(137, '0.5 g/l (same as experienced drivers)', '0.5 جرام/لتر (نفس السائقين ذوي الخبرة)', '0,5 g/l (zelfde als ervaren bestuurders)', '0,5 g/l (meme que les conducteurs experimentes)', 0, 2),
(137, '0.8 g/l', '0.8 جرام/لتر', '0,8 g/l', '0,8 g/l', 0, 3);

-- ID 138: Overtake on the right
INSERT INTO quiz_answer_options (question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order) VALUES
(138, 'When the vehicle ahead signals a left turn and you use a right lane', 'عندما تشير السيارة الأمامية بالانعطاف يساراً وتستخدم مساراً يميناً منفصلاً', 'Als het voertuig voor je links afslaat en je een aparte rechterstrook gebruikt', 'Quand le vehicule devant tourne a gauche et vous utilisez une voie de droite', 1, 1),
(138, 'Never — overtaking on the right is always forbidden', 'أبداً — التجاوز من اليمين محظور دائماً', 'Nooit — rechts inhalen is altijd verboden', 'Jamais — le depassement par la droite est toujours interdit', 0, 2),
(138, 'When the left lane is occupied by other vehicles', 'عندما يكون المسار الأيسر مشغولاً بمركبات أخرى', 'Als de linkerrijstrook bezet is door andere voertuigen', 'Quand la voie de gauche est occupee par d autres vehicules', 0, 3);

-- ID 139: Flashing amber traffic light
INSERT INTO quiz_answer_options (question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order) VALUES
(139, 'Proceed with caution and give way if necessary', 'المضي قدماً بحذر وإعطاء الأولوية عند الضرورة', 'Rijden met voorzichtigheid en voorrang geven indien nodig', 'Proceder avec prudence et ceder la priorite si necessaire', 1, 1),
(139, 'Stop and wait for the green light', 'أوقف وانتظر الضوء الأخضر', 'Stoppen en wachten op groen licht', 'S arreter et attendre le feu vert', 0, 2),
(139, 'Continue normally at full speed', 'تابع عادةً بالسرعة الكاملة', 'Normaal doorrijden op volle snelheid', 'Continuer normalement a pleine vitesse', 0, 3);

-- ID 140: Red cross in tunnel
INSERT INTO quiz_answer_options (question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order) VALUES
(140, 'The lane is closed — change lanes immediately and safely', 'المسار مغلق — تغيير المسار فوراً وبأمان', 'De rijstrook is gesloten — onmiddellijk en veilig van rijstrook wisselen', 'La voie est fermee — changer de voie immediatement et en securite', 1, 1),
(140, 'Slow down to 30 km/h and stay in the lane', 'أبطئ إلى 30 كم/ساعة وابق في المسار', 'Vertragen tot 30 km/u en in de rijstrook blijven', 'Ralentir a 30 km/h et rester dans la voie', 0, 2),
(140, 'Stop in the lane and switch on hazard lights', 'أوقف في المسار وشغّل أضواء الطوارئ', 'In de rijstrook stoppen en alarmlichten aanzetten', 'S arreter dans la voie et allumer les feux de detresse', 0, 3);

-- ID 141: Fog lights
INSERT INTO quiz_answer_options (question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order) VALUES
(141, 'When visibility is less than 200 metres due to fog, snow or heavy rain', 'عندما تقل الرؤية عن 200 متر بسبب الضباب أو الثلج أو المطر الغزير', 'Als het zicht minder dan 200 meter bedraagt door mist, sneeuw of zware regen', 'Quand la visibilite est inferieure a 200 m a cause du brouillard, neige ou pluie', 1, 1),
(141, 'Always when driving at night', 'دائماً عند القيادة ليلاً', 'Altijd wanneer je s nachts rijdt', 'Toujours quand vous conduisez de nuit', 0, 2),
(141, 'Only when it is snowing heavily', 'فقط عندما يثلج بشدة', 'Alleen bij zware sneeuwval', 'Uniquement lors de fortes chutes de neige', 0, 3);

-- ID 142: After accident with injuries
INSERT INTO quiz_answer_options (question_id, option_text_en, option_text_ar, option_text_nl, option_text_fr, is_correct, display_order) VALUES
(142, 'Secure the scene with hazard lights and warning triangle, then call 112', 'تأمين الموقع بأضواء الطوارئ ومثلث التحذير، ثم الاتصال بـ 112', 'De plaats beveiligen met alarmlichten en gevarendriehoek, dan 112 bellen', 'Securiser les lieux avec feux de detresse et triangle, puis appeler le 112', 1, 1),
(142, 'Wait in your vehicle for emergency services to arrive', 'انتظر في سيارتك حتى تصل خدمات الطوارئ', 'In je voertuig wachten tot de hulpdiensten aankomen', 'Attendre dans votre vehicule l arrivee des secours', 0, 2),
(142, 'Leave the scene and call for help from the nearest house', 'اترك الموقع واطلب المساعدة من أقرب منزل', 'De plaats verlaten en hulp zoeken in het dichtstbijzijnde huis', 'Quitter les lieux et chercher de l aide dans la maison la plus proche', 0, 3);
