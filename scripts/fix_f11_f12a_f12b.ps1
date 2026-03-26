[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8
$base = "C:\Users\haydar\Desktop\end_project\readyroad\src\main\resources\data\signs_import"

function Save-Q($code, $qs) {
    $path = Join-Path $base "$code\questions.json"
    $json = $qs | ConvertTo-Json -Depth 20 -Compress
    [System.IO.File]::WriteAllText($path, $json, (New-Object System.Text.UTF8Encoding $false))
    Write-Host "$code : $($qs.Count) questions"
}
function C3($ok, $w1, $w2) { @([ordered]@{text=$ok;is_correct=$true}; [ordered]@{text=$w1;is_correct=$false}; [ordered]@{text=$w2;is_correct=$false}) }
function C2($ok, $w1) { @([ordered]@{text=$ok;is_correct=$true}; [ordered]@{text=$w1;is_correct=$false}) }
function I18($nlQ, $nlC, $nlE, $enQ, $enC, $enE, $frQ, $frC, $frE, $arQ, $arC, $arE) {
    [ordered]@{NL=[ordered]@{question=$nlQ;choices=$nlC;explanation=$nlE};EN=[ordered]@{question=$enQ;choices=$enC;explanation=$enE};FR=[ordered]@{question=$frQ;choices=$frC;explanation=$frE};AR=[ordered]@{question=$arQ;choices=$arC;explanation=$arE}}
}
function Q($id, $type, $diff, $crit, $i18n) {
    [ordered]@{question_id=$id;type=$type;difficulty=$diff;is_critical=$crit;show_sign=$true;i18n=$i18n}
}

# ======================== F11 - Einde autoweg ========================
$f11Qs = @(
(Q "F11_Q01" "WHAT_DOES_IT_MEAN" "EASY" $false (I18 `
    "Wat betekent bord F11 'Einde autoweg'?" `
    (C3 "De autoweg eindigt; de bijzondere regels voor autowegen gelden niet meer en de gewone verkeersregels zijn van toepassing" "Het begin van een nieuwe autoweg" "Het einde van een snelweg") `
    "F11 geeft het einde van de autoweg aan (aangeduid door F9). Voorbij dit bord gelden de gewone verkeersregels: voetgangers en fietsers mogen de weg gebruiken en de normale snelheidslimieten zijn van toepassing." `
    "What does sign F11 'End of expressway' mean?" `
    (C3 "The expressway ends; the special rules for expressways no longer apply and normal traffic rules are in force" "The start of a new expressway" "The end of a motorway") `
    "F11 indicates the end of the expressway (indicated by F9). Beyond this sign normal traffic rules apply: pedestrians and cyclists may use the road and normal speed limits are in force." `
    "Que signifie le panneau F11 'Fin de la route pour automobiles' ?" `
    (C3 "La route pour automobiles se termine; les regles speciales pour les routes pour automobiles ne s appliquent plus et les regles normales de circulation sont en vigueur" "Le debut d une nouvelle route pour automobiles" "La fin d une autoroute") `
    "F11 indique la fin de la route pour automobiles (indiquee par F9). Au-dela de ce panneau les regles normales de circulation s appliquent: les pietons et cyclistes peuvent utiliser la route et les limites de vitesse normales sont en vigueur." `
    "ما معنى لافتة F11 'نهاية الطريق السريع للسيارات'؟" `
    (C3 "ينتهي الطريق السريع؛ القواعد الخاصة بالطرق السريعة لم تعد سارية وتسري قواعد المرور العادية" "بداية طريق سريع جديد للسيارات" "نهاية طريق الأوتوستراد") `
    "تشير F11 إلى نهاية الطريق السريع للسيارات (المشار إليه بـF9). بعد هذه اللافتة تسري قواعد المرور العادية: يمكن للمشاة والدراجين استخدام الطريق وتسري حدود السرعة العادية."))

(Q "F11_Q02" "WHICH_SIGN" "EASY" $false (I18 `
    "Welk bord geeft het einde van een autoweg aan?" `
    (C3 "F11 einde autoweg" "F9 begin autoweg" "F10 einde autosnelweg") `
    "F11 is het specifieke bord dat het einde van een autoweg aangeeft. Het bijbehorende beginbord is F9. Niet te verwarren met F10 dat het einde van een autosnelweg aangeeft." `
    "Which sign indicates the end of an expressway?" `
    (C3 "F11 end of expressway" "F9 start of expressway" "F10 end of motorway") `
    "F11 is the specific sign that indicates the end of an expressway. The corresponding start sign is F9. Not to be confused with F10 which indicates the end of a motorway." `
    "Quel panneau indique la fin d une route pour automobiles ?" `
    (C3 "F11 fin de la route pour automobiles" "F9 debut de la route pour automobiles" "F10 fin de l autoroute") `
    "F11 est le panneau specifique qui indique la fin d une route pour automobiles. Le panneau de debut correspondant est F9. A ne pas confondre avec F10 qui indique la fin d une autoroute." `
    "أي لافتة تشير إلى نهاية الطريق السريع للسيارات؟" `
    (C3 "F11 نهاية الطريق السريع للسيارات" "F9 بداية الطريق السريع للسيارات" "F10 نهاية الأوتوستراد") `
    "F11 هي اللافتة المحددة التي تشير إلى نهاية الطريق السريع للسيارات. لافتة البداية المقابلة هي F9. لا تُخلط مع F10 التي تشير إلى نهاية الأوتوستراد."))

(Q "F11_Q03" "HAZARD_IDENTIFICATION" "EASY" $false (I18 `
    "Welke weggebruikers mogen na bord F11 de weg gebruiken die ze op de autoweg niet mochten?" `
    (C3 "Voetgangers en fietsers zijn nu toegestaan omdat de autowegreglementering is opgeheven" "Zware vrachtwagens mogen nu met hogere snelheid rijden" "Trekkers en landbouwvoertuigen mogen de weg nu betreden") `
    "Op een autoweg zijn voetgangers en fietsers (en andere trage weggebruikers) verboden. Na F11 geldt de autowegreglementering niet meer en mogen alle weggebruikers de weg gebruiken voor zover de weg dit toelaat." `
    "Which road users may use the road after sign F11 that were not allowed on the expressway?" `
    (C3 "Pedestrians and cyclists are now permitted because the expressway regulations are lifted" "Heavy trucks may now drive at higher speeds" "Tractors and agricultural vehicles may now enter the road") `
    "On an expressway pedestrians and cyclists (and other slow road users) are prohibited. After F11 the expressway regulations no longer apply and all road users may use the road as far as the road allows." `
    "Quels usagers de la route peuvent utiliser la route apres le panneau F11 alors qu ils n etaient pas autorises sur la route pour automobiles ?" `
    (C3 "Les pietons et cyclistes sont maintenant autorises car la reglementation de la route pour automobiles est levee" "Les poids lourds peuvent maintenant rouler a des vitesses plus elevees" "Les tracteurs et vehicules agricoles peuvent maintenant emprunter la route") `
    "Sur une route pour automobiles les pietons et cyclistes (et autres usagers lents) sont interdits. Apres F11 la reglementation de la route pour automobiles ne s applique plus et tous les usagers peuvent utiliser la route dans la mesure ou la route le permet." `
    "أي مستخدمي الطريق يُسمح لهم باستخدام الطريق بعد لافتة F11 ولم يكن مسموحاً لهم بذلك على الطريق السريع؟" `
    (C3 "المشاة والدراجون مسموح لهم الآن لأن لوائح الطريق السريع قد رُفعت" "الشاحنات الثقيلة يمكنها الآن السير بسرعات أعلى" "الجرارات والمركبات الزراعية يمكنها الآن دخول الطريق") `
    "على الطريق السريع للسيارات يُحظر على المشاة والدراجين (ومستخدمي الطريق البطيئين الآخرين). بعد F11 لا تسري لوائح الطريق السريع بعد الآن ويمكن لجميع مستخدمي الطريق استخدامه بقدر ما يسمح الطريق."))

(Q "F11_Q04" "WHAT_MUST_YOU_DO" "MEDIUM" $false (I18 `
    "Welke snelheidsregel verandert na het passeren van bord F11?" `
    (C3 "De minimumsnelheid van 70 km/u op autowegen vervalt; de normale plaatselijke snelheidslimiet geldt" "U mag nu sneller rijden dan op de autoweg" "Er is geen minimumsnelheid meer maar ook geen maximumsnelheid") `
    "Op een autoweg geldt een minimumsnelheid van 70 km/u. Na F11 vervalt deze minimumsnelheid. De snelheidsregel wordt bepaald door de bebording op die specifieke weg (bv. 50 km/u in bebouwde kom)." `
    "What speed rule changes after passing sign F11?" `
    (C3 "The minimum speed of 70 km/h on expressways no longer applies; the normal local speed limit applies" "You may now drive faster than on the expressway" "There is no longer a minimum speed but also no maximum speed") `
    "On an expressway a minimum speed of 70 km/h applies. After F11 this minimum speed no longer applies. The speed rule is determined by the signage on that specific road (e.g. 50 km/h in built-up areas)." `
    "Quelle regle de vitesse change apres avoir passe le panneau F11 ?" `
    (C3 "La vitesse minimale de 70 km/h sur les routes pour automobiles ne s applique plus; la limite de vitesse locale normale s applique" "Vous pouvez maintenant rouler plus vite que sur la route pour automobiles" "Il n y a plus de vitesse minimale mais aussi pas de vitesse maximale") `
    "Sur une route pour automobiles une vitesse minimale de 70 km/h s applique. Apres F11 cette vitesse minimale ne s applique plus. La regle de vitesse est determinee par la signalisation sur cette route specifique (par ex. 50 km/h en agglomeration)." `
    "ما قاعدة السرعة التي تتغير بعد تجاوز لافتة F11؟" `
    (C3 "الحد الأدنى للسرعة البالغ 70 كم/ساعة على الطرق السريعة لم يعد سارياً؛ يسري الحد المحلي العادي للسرعة" "يمكنك الآن السير بسرعة أكبر مما كان عليه الطريق السريع" "لا يوجد حد أدنى للسرعة ولا حد أقصى بعد الآن") `
    "على الطريق السريع يسري حد أدنى للسرعة يبلغ 70 كم/ساعة. بعد F11 لا يسري هذا الحد الأدنى بعد الآن. تُحدَّد قاعدة السرعة بالإشارات على تلك الطريق المحددة (مثلاً 50 كم/ساعة في المناطق المبنية)."))

(Q "F11_Q05" "WHAT_MUST_YOU_DO" "MEDIUM" $false (I18 `
    "Mag u na bord F11 normaal inhalen als de weg en bebording dit toestaan?" `
    (C3 "Ja de bijzondere inhalregels van de autoweg zijn opgeheven; de gewone inhaalregels gelden" "Nee inhalen blijft verboden nog 500 meter na F11" "Nee u mag pas inhalen na het passeren van het volgende kruispunt") `
    "Op een autoweg gelden specifieke inhalregels (o.a. enkel links inhalen). Na F11 gelden de gewone inhalregels van toepassing op de betrokken weg. Als er geen inhalverbod is aangeduid mag u normaal inhalen." `
    "May you overtake normally after sign F11 if the road and signage permit this?" `
    (C3 "Yes the special overtaking rules of the expressway are lifted; the normal overtaking rules apply" "No overtaking remains prohibited for 500 metres after F11" "No you may only overtake after passing the next intersection") `
    "On an expressway specific overtaking rules apply (including overtaking only on the left). After F11 the normal overtaking rules apply to the road in question. If no overtaking prohibition is indicated you may overtake normally." `
    "Peut-on depasser normalement apres le panneau F11 si la route et la signalisation le permettent ?" `
    (C3 "Oui les regles speciales de depassement de la route pour automobiles sont levees; les regles normales de depassement s appliquent" "Non le depassement reste interdit pendant 500 metres apres F11" "Non vous ne pouvez depasser qu apres avoir passe le prochain carrefour") `
    "Sur une route pour automobiles des regles specifiques de depassement s appliquent (notamment depasser uniquement a gauche). Apres F11 les regles normales de depassement applicables a la route concernee s appliquent. Si aucune interdiction de depassement n est indiquee vous pouvez depasser normalement." `
    "هل يمكنك التجاوز بشكل عادي بعد لافتة F11 إذا سمحت الطريق والإشارات بذلك؟" `
    (C3 "نعم قواعد التجاوز الخاصة بالطريق السريع قد رُفعت؛ تسري قواعد التجاوز العادية" "لا يبقى التجاوز محظوراً لمدة 500 متر بعد F11" "لا يمكنك التجاوز إلا بعد تجاوز التقاطع التالي") `
    "على الطريق السريع تسري قواعد تجاوز محددة (بما فيها التجاوز من اليسار فقط). بعد F11 تسري قواعد التجاوز العادية على الطريق المعنية. إذا لم يُشر إلى حظر تجاوز يمكنك التجاوز بشكل عادي."))

(Q "F11_Q06" "WHAT_MUST_YOU_DO" "MEDIUM" $false (I18 `
    "Is pech stoppen op de rijbaan toegestaan na bord F11?" `
    (C3 "Nee u moet zo snel mogelijk stoppen op de berm en buiten de rijbaan een veiligheidsdriehoek plaatsen" "Ja na F11 mag u overal stoppen in noodgevallen" "Ja maar enkel als u uw alarmlichten aanzet") `
    "Zowel op een autoweg als op een gewone weg is pech stoppen midden op de rijbaan gevaarlijk en niet toegestaan. U moet de berm opzoeken. Na F11 kan een berm eventueel ontbreken dus rijd naar de dichtstbijzijnde veilige stopplaats." `
    "Is breaking down on the carriageway permitted after sign F11?" `
    (C3 "No you must stop on the hard shoulder as quickly as possible and place a warning triangle outside the carriageway" "Yes after F11 you may stop anywhere in emergencies" "Yes but only if you turn on your hazard lights") `
    "Both on an expressway and on a normal road breaking down in the middle of the carriageway is dangerous and not permitted. You must find the hard shoulder. After F11 there may not be a hard shoulder so drive to the nearest safe stopping place." `
    "Est-il permis de tomber en panne sur la chaussee apres le panneau F11 ?" `
    (C3 "Non vous devez vous arreter sur la bande d arret aussi vite que possible et placer un triangle de signalisation hors de la chaussee" "Oui apres F11 vous pouvez vous arreter n importe ou en cas d urgence" "Oui mais seulement si vous allumez vos feux de detresse") `
    "Aussi bien sur une route pour automobiles que sur une route normale tomber en panne au milieu de la chaussee est dangereux et non autorise. Vous devez trouver la bande d arret. Apres F11 il peut ne pas y avoir de bande d arret donc roulez jusqu au prochain arret securise." `
    "هل يُسمح بالتوقف بسبب عطل على الطريق بعد لافتة F11؟" `
    (C3 "لا يجب التوقف على الكتف في أقرب وقت ممكن ووضع مثلث تحذيري خارج الطريق" "نعم بعد F11 يمكنك التوقف في أي مكان في حالات الطوارئ" "نعم ولكن فقط إذا شغّلت أضواء الطوارئ") `
    "سواء على الطريق السريع أو الطريق العادي فإن التوقف بسبب عطل في وسط الطريق أمر خطير وغير مسموح. يجب إيجاد الكتف. بعد F11 قد لا يكون هناك كتف لذا سِر إلى أقرب مكان توقف آمن."))

(Q "F11_Q07" "IS_IT_ALLOWED" "HARD" $true (I18 `
    "Mag een bromfiets rijden op de weg direct na bord F11 als er geen verbodsbord is?" `
    (C2 "Ja op een gewone weg zonder bijzondere verboden mogen bromfietsen rijden; op de autoweg zelf was dat verboden" "Nee bromfietsen mogen nooit op wegen waar eerder een autoweg was") `
    "Een autoweg is verboden voor bromfietsen. Na F11 is de autowegreglementering opgeheven. Als er geen afzonderlijk verbodsbord staat voor bromfietsen mogen ze de weg na F11 gebruiken conform de gewone verkeersregels." `
    "May a moped ride on the road immediately after sign F11 if there is no prohibition sign?" `
    (C2 "Yes on a normal road without specific prohibitions mopeds may ride; on the expressway itself that was prohibited" "No mopeds may never ride on roads that were previously an expressway") `
    "An expressway is prohibited for mopeds. After F11 the expressway regulations are lifted. If there is no separate prohibition sign for mopeds they may use the road after F11 in accordance with normal traffic rules." `
    "Un cyclomoteur peut-il circuler sur la route juste apres le panneau F11 s il n y a pas de panneau d interdiction ?" `
    (C2 "Oui sur une route normale sans interdictions specifiques les cyclomoteurs peuvent circuler; sur la route pour automobiles elle-meme c etait interdit" "Non les cyclomoteurs ne peuvent jamais circuler sur des routes ou il y avait precedemment une route pour automobiles") `
    "Une route pour automobiles est interdite aux cyclomoteurs. Apres F11 la reglementation de la route pour automobiles est levee. S il n y a pas de panneau d interdiction separe pour les cyclomoteurs ils peuvent utiliser la route apres F11 conformement aux regles normales de circulation." `
    "هل يُسمح للدراجة البخارية بالسير على الطريق مباشرة بعد لافتة F11 إذا لم يكن هناك لافتة حظر؟" `
    (C2 "نعم على الطريق العادي دون محظورات خاصة يُسمح للدراجات البخارية بالسير؛ على الطريق السريع نفسه كان ذلك محظوراً" "لا لا يُسمح للدراجات البخارية أبداً على الطرق التي كانت سابقاً طرقاً سريعة") `
    "الطريق السريع محظور على الدراجات البخارية. بعد F11 تُرفع لوائح الطريق السريع. إذا لم يكن هناك لافتة حظر منفصلة للدراجات البخارية يمكنها استخدام الطريق بعد F11 وفقاً لقواعد المرور العادية."))

(Q "F11_Q08" "IS_IT_ALLOWED" "HARD" $true (I18 `
    "Is een ambulance verplicht de bijzondere regels van de autoweg te volgen als ze op weg gaat na bord F11?" `
    (C2 "Nee een ambulance die urgentiedienst verricht is vrijgesteld van de gewone verkeersreglementering en mag afwijken van de normale regels" "Ja ook ambulances moeten na F11 de gewone verkeersregels strikt volgen") `
    "Prioritaire voertuigen zoals ambulances brandweer en politie die in actieve dienst zijn mogen afwijken van de gewone verkeersregels (inclusief snelheidslimieten en roodlicht) zowel op autowegen als op gewone wegen." `
    "Is an ambulance required to follow the special expressway rules when proceeding after sign F11?" `
    (C2 "No an ambulance performing emergency duty is exempt from normal traffic regulations and may deviate from normal rules" "Yes even ambulances must strictly follow normal traffic rules after F11") `
    "Priority vehicles such as ambulances fire brigade and police on active duty may deviate from normal traffic rules (including speed limits and red lights) both on expressways and on normal roads." `
    "Une ambulance est-elle obligee de suivre les regles speciales de la route pour automobiles lorsqu elle poursuit apres le panneau F11 ?" `
    (C2 "Non une ambulance effectuant une mission d urgence est exempte de la reglementation normale de la circulation et peut s ecarter des regles normales" "Oui meme les ambulances doivent strictement suivre les regles normales de circulation apres F11") `
    "Les vehicules prioritaires comme les ambulances les pompiers et la police en service actif peuvent s ecarter des regles normales de circulation (y compris les limites de vitesse et les feux rouges) aussi bien sur les routes pour automobiles que sur les routes normales." `
    "هل يُلزَم سيارة الإسعاف باتباع القواعد الخاصة بالطريق السريع عند المضي قدماً بعد لافتة F11؟" `
    (C2 "لا سيارة الإسعاف التي تؤدي مهمة طوارئ معفاة من لوائح المرور العادية ويمكنها الانحراف عن القواعد العادية" "نعم حتى سيارات الإسعاف يجب أن تتبع قواعد المرور العادية بصرامة بعد F11") `
    "المركبات ذات الأولوية كسيارات الإسعاف وسيارات الإطفاء والشرطة في الخدمة الفعلية يمكنها الانحراف عن قواعد المرور العادية (بما فيها حدود السرعة والإشارات الحمراء) سواء على الطرق السريعة أو الطرق العادية."))
)
Save-Q "F11" $f11Qs

# ======================== F12a - Begin woonerf ========================
$f12aQs = @(
(Q "F12a_Q01" "WHAT_DOES_IT_MEAN" "EASY" $false (I18 `
    "Wat betekent bord F12a 'Begin woonerf'?" `
    (C3 "U rijdt een woonerf in waar voetgangers de volledige weg mogen gebruiken een maximumsnelheid van 20 km/u geldt en kinderen voorrang hebben" "U rijdt een voetgangerszone in waar auto's volledig verboden zijn" "U rijdt een erf in met een maximumsnelheid van 50 km/u") `
    "F12a geeft het begin van een woonerf aan. In een woonerf mogen voetgangers de volledige rijbaan gebruiken gelden speciale voorrangsregels voor spelende kinderen en is de maximumsnelheid 20 km/u (stapvoets)." `
    "What does sign F12a 'Start of residential area' mean?" `
    (C3 "You are entering a residential area where pedestrians may use the full road a maximum speed of 20 km/h applies and children have priority" "You are entering a pedestrian zone where cars are completely prohibited" "You are entering a yard with a maximum speed of 50 km/h") `
    "F12a indicates the start of a residential area (woonerf). In a residential area pedestrians may use the entire roadway special priority rules apply for playing children and the maximum speed is 20 km/h (walking pace)." `
    "Que signifie le panneau F12a 'Debut de zone residentielle' ?" `
    (C3 "Vous entrez dans une zone residentielle ou les pietons peuvent utiliser toute la route une vitesse maximale de 20 km/h s applique et les enfants ont la priorite" "Vous entrez dans une zone pietonne ou les voitures sont completement interdites" "Vous entrez dans une cour avec une vitesse maximale de 50 km/h") `
    "F12a indique le debut d une zone residentielle (woonerf). Dans une zone residentielle les pietons peuvent utiliser toute la chaussee des regles de priorite speciales s appliquent pour les enfants qui jouent et la vitesse maximale est de 20 km/h (au pas)." `
    "ما معنى لافتة F12a 'بداية المنطقة السكنية'؟" `
    (C3 "أنت تدخل منطقة سكنية يمكن فيها للمشاة استخدام الطريق بالكامل ويسري حد أقصى للسرعة 20 كم/ساعة وللأطفال الأولوية" "أنت تدخل منطقة مشاة حيث السيارات محظورة كلياً" "أنت تدخل فناء بحد أقصى للسرعة 50 كم/ساعة") `
    "تشير F12a إلى بداية المنطقة السكنية. في المنطقة السكنية يمكن للمشاة استخدام الطريق بالكامل وتسري قواعد أولوية خاصة للأطفال اللاعبين والحد الأقصى للسرعة هو 20 كم/ساعة."))

(Q "F12a_Q02" "WHICH_SIGN" "EASY" $false (I18 `
    "Welk bord markeert het begin van een woonerf?" `
    (C3 "F12a begin woonerf" "F12b einde woonerf" "F9 begin autoweg") `
    "F12a is het beginbord van een woonerf. Het bijbehorende eindbord is F12b. Het woonerf is een zone met bijzondere rechten voor voetgangers en een lage maximumsnelheid." `
    "Which sign marks the start of a residential area?" `
    (C3 "F12a start of residential area" "F12b end of residential area" "F9 start of expressway") `
    "F12a is the start sign of a residential area. The corresponding end sign is F12b. The residential area is a zone with special rights for pedestrians and a low maximum speed." `
    "Quel panneau marque le debut d une zone residentielle ?" `
    (C3 "F12a debut de zone residentielle" "F12b fin de zone residentielle" "F9 debut de route pour automobiles") `
    "F12a est le panneau de debut d une zone residentielle. Le panneau de fin correspondant est F12b. La zone residentielle est une zone avec des droits speciaux pour les pietons et une faible vitesse maximale." `
    "أي لافتة تُعلِّم بداية المنطقة السكنية؟" `
    (C3 "F12a بداية المنطقة السكنية" "F12b نهاية المنطقة السكنية" "F9 بداية الطريق السريع للسيارات") `
    "F12a هي لافتة البداية للمنطقة السكنية. لافتة النهاية المقابلة هي F12b. المنطقة السكنية هي منطقة بحقوق خاصة للمشاة وحد أقصى منخفض للسرعة."))

(Q "F12a_Q03" "HAZARD_IDENTIFICATION" "EASY" $false (I18 `
    "Wat is het grootste gevaar in een woonerf voor automobilisten?" `
    (C3 "Spelende kinderen die plotseling vanachter geparkeerde voertuigen kunnen tevoorschijn komen" "Voetgangers die enkel op stoepen mogen lopen" "Fietsers die verplicht een apart fietspad gebruiken") `
    "In een woonerf mogen kinderen en voetgangers de volledige weg gebruiken inclusief de rijbaan. Kinderen die achter geparkeerde auto's spelen zijn moeilijk zichtbaar en kunnen plotseling de rijbaan oplopen. Maximumsnelheid 20 km/u is verplicht voor dit gevaar." `
    "What is the greatest danger in a residential area for motorists?" `
    (C3 "Playing children who may suddenly appear from behind parked vehicles" "Pedestrians who are only allowed to walk on pavements" "Cyclists who are required to use a separate cycle path") `
    "In a residential area children and pedestrians may use the entire road including the carriageway. Children playing behind parked cars are difficult to see and may suddenly run onto the carriageway. Maximum speed 20 km/h is mandatory for this hazard." `
    "Quel est le plus grand danger dans une zone residentielle pour les automobilistes ?" `
    (C3 "Des enfants qui jouent et qui peuvent surgir soudainement de derriere des vehicules gares" "Des pietons qui ne sont autorises a marcher que sur les trottoirs" "Des cyclistes qui sont obliges d utiliser une piste cyclable separee") `
    "Dans une zone residentielle les enfants et les pietons peuvent utiliser toute la route y compris la chaussee. Les enfants qui jouent derriere des voitures garees sont difficiles a voir et peuvent soudainement courir sur la chaussee. La vitesse maximale de 20 km/h est obligatoire pour ce danger." `
    "ما أكبر خطر في المنطقة السكنية على السائقين؟" `
    (C3 "الأطفال اللاعبون الذين قد يظهرون فجأة من خلف المركبات المتوقفة" "المشاة المسموح لهم فقط بالسير على الأرصفة" "الدراجون الملزمون باستخدام مسار دراجات منفصل") `
    "في المنطقة السكنية يمكن للأطفال والمشاة استخدام الطريق بالكامل بما فيه طريق السير. الأطفال اللاعبون خلف السيارات المتوقفة يصعب رؤيتهم وقد يركضون فجأة على الطريق. الحد الأقصى 20 كم/ساعة إلزامي لهذا الخطر."))

(Q "F12a_Q04" "WHAT_MUST_YOU_DO" "MEDIUM" $false (I18 `
    "Welke maximumsnelheid geldt in een woonerf?" `
    (C3 "20 km/u (stapvoets)" "30 km/u" "10 km/u") `
    "In een woonerf (aangeduid door F12a) geldt een maximumsnelheid van 20 km/u. Dit is vergelijkbaar met stapvoets rijden. De lage snelheid is noodzakelijk omdat voetgangers en kinderen de volledige weg mogen gebruiken." `
    "What maximum speed applies in a residential area?" `
    (C3 "20 km/h (walking pace)" "30 km/h" "10 km/h") `
    "In a residential area (indicated by F12a) a maximum speed of 20 km/h applies. This is comparable to walking pace. The low speed is necessary because pedestrians and children may use the entire road." `
    "Quelle vitesse maximale s applique dans une zone residentielle ?" `
    (C3 "20 km/h (au pas)" "30 km/h" "10 km/h") `
    "Dans une zone residentielle (indiquee par F12a) une vitesse maximale de 20 km/h s applique. C est comparable a la vitesse du pas. La faible vitesse est necessaire car les pietons et les enfants peuvent utiliser toute la route." `
    "ما الحد الأقصى للسرعة المطبَّق في المنطقة السكنية؟" `
    (C3 "20 كم/ساعة (بخطى المشاة)" "30 كم/ساعة" "10 كم/ساعة") `
    "في المنطقة السكنية (المشار إليها بـF12a) يسري حد أقصى للسرعة يبلغ 20 كم/ساعة. هذا مماثل لخطى المشاة. السرعة المنخفضة ضرورية لأن المشاة والأطفال يمكنهم استخدام الطريق بالكامل."))

(Q "F12a_Q05" "WHAT_MUST_YOU_DO" "MEDIUM" $false (I18 `
    "Hoe moet u parkeren in een woonerf?" `
    (C3 "Enkel parkeren op de plaatsen die specifiek voor parkeren zijn aangeduid; willekeurig parkeren is verboden" "U mag overal parkeren zolang u een vrije doorgang laat" "Parkeren is volledig verboden in een woonerf") `
    "In een woonerf is parkeren alleen toegestaan op aangeduide parkeerplaatsen. Willekeurig parkeren op de rijbaan of op plaatsen die de bewegingsvrijheid van voetgangers beperken is verboden." `
    "How must you park in a residential area?" `
    (C3 "Only park in places specifically designated for parking; random parking is prohibited" "You may park anywhere as long as you leave a free passage" "Parking is completely prohibited in a residential area") `
    "In a residential area parking is only permitted in designated parking spaces. Random parking on the carriageway or in places that restrict the freedom of movement of pedestrians is prohibited." `
    "Comment devez-vous stationner dans une zone residentielle ?" `
    (C3 "Stationner uniquement aux endroits specifiquement designes pour le stationnement; le stationnement aleatoire est interdit" "Vous pouvez stationner n importe ou tant que vous laissez un passage libre" "Le stationnement est completement interdit dans une zone residentielle") `
    "Dans une zone residentielle le stationnement n est autorise que dans les emplacements de stationnement designes. Le stationnement aleatoire sur la chaussee ou dans des endroits qui limitent la liberte de mouvement des pietons est interdit." `
    "كيف يجب أن تقوم بالوقوف في المنطقة السكنية؟" `
    (C3 "اركن فقط في الأماكن المخصصة للوقوف تحديداً؛ الوقوف العشوائي محظور" "يمكنك الوقوف في أي مكان طالما تترك ممراً حراً" "الوقوف محظور كلياً في المنطقة السكنية") `
    "في المنطقة السكنية يُسمح بالوقوف فقط في أماكن الوقوف المخصصة. الوقوف العشوائي على طريق السير أو في أماكن تقيد حرية تنقل المشاة محظور."))

(Q "F12a_Q06" "WHAT_MUST_YOU_DO" "MEDIUM" $false (I18 `
    "Wie heeft voorrang in een woonerf als er geen andere regeling is?" `
    (C3 "Voetgangers en spelende kinderen hebben voorrang op motorvoertuigen" "Motorvoertuigen hebben altijd voorrang" "Fietsers hebben voorrang op zowel voetgangers als motorvoertuigen") `
    "In een woonerf heeft voetgangersverkeer (inclusief spelende kinderen) voorrang op motorvoertuigen. Dit is een fundamenteel kenmerk van de woonerf waarbij de leefkwaliteit boven de doorstroming van motorvoertuigen wordt gesteld." `
    "Who has priority in a residential area when there is no other arrangement?" `
    (C3 "Pedestrians and playing children have priority over motor vehicles" "Motor vehicles always have priority" "Cyclists have priority over both pedestrians and motor vehicles") `
    "In a residential area pedestrian traffic (including playing children) has priority over motor vehicles. This is a fundamental feature of the residential area where quality of life is placed above the flow of motor vehicles." `
    "Qui a la priorite dans une zone residentielle en l absence d autre reglementation ?" `
    (C3 "Les pietons et les enfants qui jouent ont la priorite sur les vehicules a moteur" "Les vehicules a moteur ont toujours la priorite" "Les cyclistes ont la priorite sur les pietons et les vehicules a moteur") `
    "Dans une zone residentielle les pietons (y compris les enfants qui jouent) ont la priorite sur les vehicules a moteur. C est une caracteristique fondamentale de la zone residentielle ou la qualite de vie est placee au-dessus du flux des vehicules a moteur." `
    "من له الأولوية في المنطقة السكنية عند عدم وجود ترتيب آخر؟" `
    (C3 "المشاة والأطفال اللاعبون لهم الأولوية على المركبات ذات المحرك" "المركبات ذات المحرك لها الأولوية دائماً" "الدراجون لهم الأولوية على كل من المشاة والمركبات ذات المحرك") `
    "في المنطقة السكنية حركة المشاة (بما فيها الأطفال اللاعبون) لها الأولوية على المركبات ذات المحرك. هذه ميزة أساسية للمنطقة السكنية حيث تُعطى الأولوية لجودة الحياة على تدفق المركبات."))

(Q "F12a_Q07" "IS_IT_ALLOWED" "HARD" $true (I18 `
    "Mag u in een woonerf de 20 km/u overschrijden als er geen voetgangers of kinderen zichtbaar zijn?" `
    (C2 "Nee 20 km/u is een absolute maximumsnelheid die altijd geldt ongeacht of er voetgangers aanwezig zijn" "Ja als de weg vrij is mag u kortstondig sneller rijden") `
    "De maximumsnelheid van 20 km/u in een woonerf is een absolute limiet die te allen tijde geldt. Het is irrelevant of er al dan niet voetgangers zichtbaar zijn. Overtredingen worden beboet en kunnen gevaarlijk zijn omdat kinderen plots in beeld kunnen komen." `
    "May you exceed 20 km/h in a residential area if no pedestrians or children are visible?" `
    (C2 "No 20 km/h is an absolute maximum speed that always applies regardless of whether pedestrians are present" "Yes if the road is clear you may drive faster briefly") `
    "The maximum speed of 20 km/h in a residential area is an absolute limit that applies at all times. It is irrelevant whether or not pedestrians are visible. Violations are fined and can be dangerous because children can suddenly appear." `
    "Peut-on depasser 20 km/h dans une zone residentielle si aucun pieton ou enfant n est visible ?" `
    (C2 "Non 20 km/h est une vitesse maximale absolue qui s applique toujours quelle que soit la presence de pietons" "Oui si la route est libre vous pouvez rouler plus vite brievement") `
    "La vitesse maximale de 20 km/h dans une zone residentielle est une limite absolue qui s applique en tout temps. Il est sans importance que des pietons soient visibles ou non. Les infractions sont sanctionnees et peuvent etre dangereuses car des enfants peuvent soudainement apparaitre." `
    "هل يمكنك تجاوز 20 كم/ساعة في المنطقة السكنية إذا لم يكن هناك مشاة أو أطفال مرئيون؟" `
    (C2 "لا 20 كم/ساعة هو حد سرعة مطلق يسري دائماً بغض النظر عن وجود المشاة" "نعم إذا كان الطريق خالياً يمكنك السير بسرعة أكبر لفترة وجيزة") `
    "الحد الأقصى للسرعة البالغ 20 كم/ساعة في المنطقة السكنية هو حد مطلق يسري في جميع الأوقات. لا يهم ما إذا كان المشاة مرئيين أم لا. المخالفات تُعاقَب عليها وقد تكون خطيرة لأن الأطفال قد يظهرون فجأة."))

(Q "F12a_Q08" "IS_IT_ALLOWED" "HARD" $true (I18 `
    "Mogen fietsers in twee richtingen rijden in een woonerf ook als de rijbaan eenrichtingsverkeer is?" `
    (C2 "Ja tenzij een specifiek verbodsbord dit verhindert mogen fietsers in woonerven in beide richtingen rijden" "Nee fietsers moeten altijd de rijrichting van het gemotoriseerd verkeer volgen") `
    "In woonerven is tweerichtingsfietsverkeer standaard toegestaan ook op eenrichtingswegen voor gemotoriseerd verkeer. Dit is een veiligheidsregel die fietsers beschermt. Een apart verbodsbord is nodig om dit te beperken." `
    "May cyclists ride in both directions in a residential area even if the carriageway is one-way traffic?" `
    (C2 "Yes unless a specific prohibition sign prevents this cyclists in residential areas may ride in both directions" "No cyclists must always follow the direction of motorised traffic") `
    "In residential areas two-way cycling is standard allowed even on one-way roads for motorised traffic. This is a safety rule that protects cyclists. A separate prohibition sign is required to restrict this." `
    "Les cyclistes peuvent-ils circuler dans les deux sens dans une zone residentielle meme si la chaussee est en sens unique ?" `
    (C2 "Oui sauf si un panneau d interdiction specifique l empeche les cyclistes dans les zones residentielles peuvent circuler dans les deux sens" "Non les cyclistes doivent toujours suivre le sens de circulation des vehicules motorises") `
    "Dans les zones residentielles la circulation cycliste bidirectionnelle est standard autorisee meme sur les routes a sens unique pour les vehicules motorises. C est une regle de securite qui protege les cyclistes. Un panneau d interdiction separe est necessaire pour le restreindre." `
    "هل يمكن للدراجين السير في الاتجاهين في المنطقة السكنية حتى لو كان طريق السير باتجاه واحد؟" `
    (C2 "نعم ما لم يمنع ذلك لافتة حظر محددة يمكن للدراجين في المناطق السكنية السير في الاتجاهين" "لا يجب على الدراجين دائماً اتباع اتجاه سير المركبات المحرَّكة") `
    "في المناطق السكنية يُسمح قياساً بركوب الدراجات في الاتجاهين حتى على الطرق ذات الاتجاه الواحد للمركبات المحرَّكة. هذه قاعدة أمان تحمي الدراجين. لافتة حظر منفصلة ضرورية لتقييد ذلك."))
)
Save-Q "F12a" $f12aQs

# ======================== F12b - Einde woonerf ========================
$f12bQs = @(
(Q "F12b_Q01" "WHAT_DOES_IT_MEAN" "EASY" $false (I18 `
    "Wat betekent bord F12b 'Einde woonerf'?" `
    (C3 "De woonerf eindigt; de bijzondere regels (20 km/u voorrang voetgangers) gelden niet meer en de gewone verkeersregels zijn van toepassing" "Het begin van een nieuw woonerf" "Het einde van een voetgangerszone") `
    "F12b geeft het einde van een woonerf aan. Voorbij dit bord zijn de bijzondere woonerf-regels (maximumsnelheid 20 km/u voorrang voor voetgangers toegestaan parkeren enkel op aangeduide plaatsen) niet meer van toepassing." `
    "What does sign F12b 'End of residential area' mean?" `
    (C3 "The residential area ends; the special rules (20 km/h priority pedestrians) no longer apply and normal traffic rules are in force" "The start of a new residential area" "The end of a pedestrian zone") `
    "F12b indicates the end of a residential area. Beyond this sign the special residential area rules (maximum speed 20 km/h priority for pedestrians parking only in designated places) no longer apply." `
    "Que signifie le panneau F12b 'Fin de zone residentielle' ?" `
    (C3 "La zone residentielle se termine; les regles speciales (20 km/h priorite pietons) ne s appliquent plus et les regles normales de circulation sont en vigueur" "Le debut d une nouvelle zone residentielle" "La fin d une zone pietonne") `
    "F12b indique la fin d une zone residentielle. Au-dela de ce panneau les regles speciales de la zone residentielle (vitesse maximale 20 km/h priorite aux pietons stationnement uniquement aux endroits designes) ne s appliquent plus." `
    "ما معنى لافتة F12b 'نهاية المنطقة السكنية'؟" `
    (C3 "تنتهي المنطقة السكنية؛ القواعد الخاصة (20 كم/ساعة أولوية المشاة) لم تعد سارية وتسري قواعد المرور العادية" "بداية منطقة سكنية جديدة" "نهاية منطقة المشاة") `
    "تشير F12b إلى نهاية المنطقة السكنية. بعد هذه اللافتة لا تسري القواعد الخاصة للمنطقة السكنية (الحد الأقصى للسرعة 20 كم/ساعة الأولوية للمشاة الوقوف فقط في الأماكن المخصصة) بعد الآن."))

(Q "F12b_Q02" "WHICH_SIGN" "EASY" $false (I18 `
    "Welk bord markeert het einde van een woonerf?" `
    (C3 "F12b einde woonerf" "F12a begin woonerf" "F105 einde voetgangerszone") `
    "F12b is het eindbord van een woonerf. Het bijbehorende beginbord is F12a. Niet te verwarren met andere eindzone-borden zoals F105 (einde voetgangerszone)." `
    "Which sign marks the end of a residential area?" `
    (C3 "F12b end of residential area" "F12a start of residential area" "F105 end of pedestrian zone") `
    "F12b is the end sign of a residential area. The corresponding start sign is F12a. Not to be confused with other end zone signs such as F105 (end of pedestrian zone)." `
    "Quel panneau marque la fin d une zone residentielle ?" `
    (C3 "F12b fin de zone residentielle" "F12a debut de zone residentielle" "F105 fin zone pietonne") `
    "F12b est le panneau de fin d une zone residentielle. Le panneau de debut correspondant est F12a. A ne pas confondre avec d autres panneaux de fin de zone comme F105 (fin de zone pietonne)." `
    "أي لافتة تُعلِّم نهاية المنطقة السكنية؟" `
    (C3 "F12b نهاية المنطقة السكنية" "F12a بداية المنطقة السكنية" "F105 نهاية منطقة المشاة") `
    "F12b هي لافتة نهاية المنطقة السكنية. لافتة البداية المقابلة هي F12a. لا تُخلط مع لافتات نهاية مناطق أخرى مثل F105 (نهاية منطقة المشاة)."))

(Q "F12b_Q03" "HAZARD_IDENTIFICATION" "EASY" $false (I18 `
    "Moet u na bord F12b extra opletten voor spelende kinderen?" `
    (C3 "Ja direct na F12b kunnen nog kinderen zijn die vanuit het woonerf de straat oplopen; wees extra voorzichtig" "Nee na F12b mag u direct de normale snelheid aannemen zonder extra aandacht" "Nee kinderen mogen na F12b de rijbaan niet meer betreden") `
    "Direct na de grens van het woonerf (F12b) kunnen kinderen die in het woonerf speelden de straat nog oplopen. U moet extra alert zijn totdat u zich voldoende ver van het woonerf bevindt. De snelheid mag geleidelijk worden aangepast." `
    "Must you pay extra attention for playing children after sign F12b?" `
    (C3 "Yes immediately after F12b there may still be children running from the residential area onto the street; be extra careful" "No after F12b you may immediately assume normal speed without extra attention" "No children may no longer enter the carriageway after F12b") `
    "Immediately after the boundary of the residential area (F12b) children who were playing in the residential area may still run onto the street. You must be extra alert until you are sufficiently far from the residential area. Speed may be gradually adjusted." `
    "Devez-vous faire attention aux enfants qui jouent apres le panneau F12b ?" `
    (C3 "Oui juste apres F12b il peut encore y avoir des enfants qui courent de la zone residentielle vers la rue; soyez tres prudent" "Non apres F12b vous pouvez adopter immediatement la vitesse normale sans attention supplementaire" "Non les enfants ne peuvent plus entrer sur la chaussee apres F12b") `
    "Juste apres la limite de la zone residentielle (F12b) des enfants qui jouaient dans la zone residentielle peuvent encore courir dans la rue. Vous devez etre particulierement vigilant jusqu a ce que vous soyez suffisamment loin de la zone residentielle. La vitesse peut etre ajustee progressivement." `
    "هل يجب عليك التنبه للأطفال اللاعبين بعد لافتة F12b؟" `
    (C3 "نعم مباشرة بعد F12b قد يكون هناك أطفال يركضون من المنطقة السكنية إلى الشارع؛ كن حذراً" "لا بعد F12b يمكنك اعتماد السرعة العادية فوراً دون انتباه إضافي" "لا لا يمكن للأطفال دخول طريق السير بعد F12b") `
    "مباشرة بعد حدود المنطقة السكنية (F12b) قد يكون أطفال كانوا يلعبون في المنطقة السكنية لا يزالون يركضون إلى الشارع. يجب أن تكون يقظاً بشكل خاص حتى تكون بعيداً بما يكفي عن المنطقة السكنية. يمكن تعديل السرعة تدريجياً."))

(Q "F12b_Q04" "WHAT_MUST_YOU_DO" "MEDIUM" $false (I18 `
    "Welke snelheid is geldig direct na bord F12b?" `
    (C3 "De snelheid van toepassing op de betrokken weg zoals bepaald door de bebording (bv. 50 km/u in bebouwde kom)" "Nog steeds 20 km/u gedurende 100 meter na F12b" "30 km/u als overgangssnelheid na een woonerf") `
    "Na F12b is de maximumsnelheid terug de normale limiet voor dat wegtype. In de bebouwde kom is dat doorgaans 50 km/u tenzij anders aangegeven. Er is geen overgangssnelheid; de gewone regel geldt onmiddellijk na F12b." `
    "What speed applies immediately after sign F12b?" `
    (C3 "The speed applicable to the road in question as determined by the signage (e.g. 50 km/h in built-up areas)" "Still 20 km/h for 100 metres after F12b" "30 km/h as transition speed after a residential area") `
    "After F12b the maximum speed reverts to the normal limit for that road type. In built-up areas that is typically 50 km/h unless otherwise indicated. There is no transition speed; the normal rule applies immediately after F12b." `
    "Quelle vitesse est valable juste apres le panneau F12b ?" `
    (C3 "La vitesse applicable a la route concernee telle que determinee par la signalisation (par ex. 50 km/h en agglomeration)" "Encore 20 km/h pendant 100 metres apres F12b" "30 km/h comme vitesse de transition apres une zone residentielle") `
    "Apres F12b la vitesse maximale revient a la limite normale pour ce type de route. En agglomeration c est generalement 50 km/h sauf indication contraire. Il n y a pas de vitesse de transition; la regle normale s applique immediatement apres F12b." `
    "ما السرعة السارية مباشرة بعد لافتة F12b؟" `
    (C3 "السرعة المطبَّقة على الطريق المعنية كما تُحددها الإشارات (مثلاً 50 كم/ساعة في المناطق المبنية)" "لا تزال 20 كم/ساعة لمدة 100 متر بعد F12b" "30 كم/ساعة كسرعة انتقالية بعد المنطقة السكنية") `
    "بعد F12b يعود الحد الأقصى للسرعة إلى الحد العادي لذلك النوع من الطرق. في المناطق المبنية عادةً 50 كم/ساعة ما لم يُشر إلى خلاف ذلك. لا توجد سرعة انتقالية؛ تسري القاعدة العادية فوراً بعد F12b."))

(Q "F12b_Q05" "WHAT_MUST_YOU_DO" "MEDIUM" $false (I18 `
    "Waar moeten voetgangers na bord F12b lopen?" `
    (C3 "Op het trottoir of de stoep als die aanwezig is; ze mogen niet meer de volledige rijbaan gebruiken" "Voetgangers mogen na F12b nog steeds de rijbaan gebruiken" "Voetgangers moeten na F12b een speciaal voetgangerspad nemen") `
    "In een woonerf mogen voetgangers de volledige rijbaan gebruiken. Na F12b geldt de gewone regel: voetgangers moeten het trottoir of de stoep gebruiken als die aanwezig is. Ze mogen de rijbaan nog gebruiken als er geen trottoir is maar moeten andere weggebruikers voorrang geven." `
    "Where must pedestrians walk after sign F12b?" `
    (C3 "On the pavement or footpath if present; they may no longer use the entire carriageway" "Pedestrians may still use the carriageway after F12b" "Pedestrians must take a special pedestrian path after F12b") `
    "In a residential area pedestrians may use the entire carriageway. After F12b the normal rule applies: pedestrians must use the pavement or footpath if present. They may still use the carriageway if there is no pavement but must give way to other road users." `
    "Ou les pietons doivent-ils marcher apres le panneau F12b ?" `
    (C3 "Sur le trottoir ou le chemin pietонnier s il est present; ils ne peuvent plus utiliser toute la chaussee" "Les pietons peuvent encore utiliser la chaussee apres F12b" "Les pietons doivent emprunter un chemin pietонnier special apres F12b") `
    "Dans une zone residentielle les pietons peuvent utiliser toute la chaussee. Apres F12b la regle normale s applique: les pietons doivent utiliser le trottoir ou le chemin pietонnier s il est present. Ils peuvent encore utiliser la chaussee s il n y a pas de trottoir mais doivent ceder la priorite aux autres usagers." `
    "أين يجب على المشاة السير بعد لافتة F12b؟" `
    (C3 "على الرصيف أو المشاة إن وُجد؛ لا يمكنهم بعد الآن استخدام طريق السير بالكامل" "يمكن للمشاة الاستمرار في استخدام طريق السير بعد F12b" "يجب على المشاة سلوك مسار مشاة خاص بعد F12b") `
    "في المنطقة السكنية يمكن للمشاة استخدام طريق السير بالكامل. بعد F12b تسري القاعدة العادية: يجب على المشاة استخدام الرصيف أو مسار المشاة إن وُجد. يمكنهم الاستمرار في استخدام طريق السير إذا لم يكن هناك رصيف لكن يجب إعطاء الأولوية لمستخدمي الطريق الآخرين."))

(Q "F12b_Q06" "WHAT_MUST_YOU_DO" "MEDIUM" $false (I18 `
    "Moet bord F12b worden geplaatst aan elke uitgang van een woonerf?" `
    (C3 "Ja F12b moet worden geplaatst aan elke uitgang van de woonerf zodat alle weggebruikers weten dat de zone eindigt" "Nee F12b wordt enkel geplaatst aan de hoofduitgang" "Nee F12b is optioneel en wordt enkel op verzoek van de gemeente geplaatst") `
    "Net als andere zone-borden moet F12b worden geplaatst aan elke uitgang van de woonerf. Zo worden alle vertrekkende weggebruikers gewaarschuwd dat de bijzondere woonerf-regels niet meer gelden." `
    "Must sign F12b be placed at every exit of a residential area?" `
    (C3 "Yes F12b must be placed at every exit of the residential area so all road users know the zone ends" "No F12b is only placed at the main exit" "No F12b is optional and only placed at the request of the municipality") `
    "Like other zone signs F12b must be placed at every exit of the residential area. This way all departing road users are warned that the special residential area rules no longer apply." `
    "Le panneau F12b doit-il etre place a chaque sortie d une zone residentielle ?" `
    (C3 "Oui F12b doit etre place a chaque sortie de la zone residentielle afin que tous les usagers sachent que la zone se termine" "Non F12b est uniquement place a la sortie principale" "Non F12b est optionnel et n est place qu a la demande de la commune") `
    "Comme les autres panneaux de zone F12b doit etre place a chaque sortie de la zone residentielle. Ainsi tous les usagers qui partent sont avertis que les regles speciales de la zone residentielle ne s appliquent plus." `
    "هل يجب وضع لافتة F12b عند كل مخرج من المنطقة السكنية؟" `
    (C3 "نعم يجب وضع F12b عند كل مخرج من المنطقة السكنية حتى يعرف جميع مستخدمي الطريق أن المنطقة تنتهي" "لا تُوضع F12b فقط عند المخرج الرئيسي" "لا F12b اختيارية وتُوضع فقط بطلب من البلدية") `
    "مثل لافتات المناطق الأخرى يجب وضع F12b عند كل مخرج من المنطقة السكنية. بهذه الطريقة يُحذَّر جميع مستخدمي الطريق المغادرون من أن القواعد الخاصة للمنطقة السكنية لم تعد سارية."))

(Q "F12b_Q07" "IS_IT_ALLOWED" "HARD" $true (I18 `
    "Mogen fietsers na bord F12b nog steeds in beide richtingen rijden als de weg eenrichtingsverkeer is?" `
    (C2 "Nee na F12b geldt de gewone regel; voor eenrichtingsverkeer voor fietsers is een apart bord nodig dat tweewegfietsverkeer toestaat" "Ja fietsers mogen na een woonerf altijd in beide richtingen blijven rijden") `
    "In een woonerf is tweerichtingsfietsverkeer automatisch toegestaan. Na F12b is de woonerf-reglementering opgeheven. Voor tweewegfietsverkeer op een eenrichtingsweg buiten het woonerf is een apart aanduiding nodig (bv. uitzondering bord)." `
    "May cyclists still ride in both directions after sign F12b if the road is one-way?" `
    (C2 "No after F12b the normal rule applies; for one-way roads a separate sign is needed allowing two-way cycling" "Yes cyclists may always continue riding in both directions after a residential area") `
    "In a residential area two-way cycling is automatically permitted. After F12b the residential area regulations are lifted. For two-way cycling on a one-way road outside the residential area a separate indication is needed (e.g. exception sign)." `
    "Les cyclistes peuvent-ils encore circuler dans les deux sens apres le panneau F12b si la route est a sens unique ?" `
    (C2 "Non apres F12b la regle normale s applique; pour les routes a sens unique un panneau separe est necessaire autorisant la circulation cycliste a double sens" "Oui les cyclistes peuvent toujours continuer a circuler dans les deux sens apres une zone residentielle") `
    "Dans une zone residentielle la circulation cycliste bidirectionnelle est automatiquement autorisee. Apres F12b la reglementation de la zone residentielle est levee. Pour la circulation cycliste bidirectionnelle sur une route a sens unique hors zone residentielle une indication separee est necessaire (par ex. panneau d exception)." `
    "هل يمكن للدراجين الاستمرار في السير في الاتجاهين بعد لافتة F12b إذا كان الطريق باتجاه واحد؟" `
    (C2 "لا بعد F12b تسري القاعدة العادية؛ للطرق ذات الاتجاه الواحد لافتة منفصلة ضرورية للسماح بركوب الدراجات في الاتجاهين" "نعم يمكن للدراجين دائماً الاستمرار في السير في الاتجاهين بعد المنطقة السكنية") `
    "في المنطقة السكنية يُسمح تلقائياً بركوب الدراجات في الاتجاهين. بعد F12b تُرفع لائحة المنطقة السكنية. لركوب الدراجات في الاتجاهين على طريق ذي اتجاه واحد خارج المنطقة السكنية لافتة منفصلة ضرورية."))

(Q "F12b_Q08" "IS_IT_ALLOWED" "HARD" $true (I18 `
    "Houdt de verhoogde alertheid voor kinderen op bij bord F12b?" `
    (C2 "Nee u moet nog enige tijd verhoogd alert blijven omdat kinderen de grens van het woonerf niet altijd kennen en de straat kunnen oplopen" "Ja zodra u F12b passeert hoeft u geen extra aandacht meer te hebben voor kinderen") `
    "F12b markeert juridisch het einde van het woonerf maar in de praktijk kunnen kinderen die in het woonerf spelen de grens oversteken. Als voorzichtig bestuurder moet u uw rijgedrag aanpassen aan de werkelijke omstandigheden ook na F12b." `
    "Does the heightened alertness for children stop at sign F12b?" `
    (C2 "No you must remain extra alert for some time because children do not always know the boundary of the residential area and may run onto the street" "Yes once you pass F12b you no longer need to pay extra attention to children") `
    "F12b legally marks the end of the residential area but in practice children playing in the residential area may cross the boundary. As a careful driver you must adapt your driving behaviour to actual conditions even after F12b." `
    "L attention accrue pour les enfants s arrete-t-elle au panneau F12b ?" `
    (C2 "Non vous devez rester extra vigilant pendant un certain temps car les enfants ne connaissent pas toujours la limite de la zone residentielle et peuvent courir dans la rue" "Oui une fois que vous passez F12b vous n avez plus besoin de faire attention aux enfants") `
    "F12b marque juridiquement la fin de la zone residentielle mais en pratique les enfants qui jouent dans la zone residentielle peuvent franchir la limite. En tant que conducteur prudent vous devez adapter votre comportement de conduite aux conditions reelles meme apres F12b." `
    "هل تتوقف اليقظة المتزايدة تجاه الأطفال عند لافتة F12b؟" `
    (C2 "لا يجب أن تبقى يقظاً بشكل مميز لفترة من الوقت لأن الأطفال لا يعرفون دائماً حدود المنطقة السكنية وقد يركضون إلى الشارع" "نعم بمجرد تجاوز F12b لا تحتاج إلى الانتباه للأطفال بعد الآن") `
    "تُعلِّم F12b قانونياً نهاية المنطقة السكنية لكن عملياً الأطفال اللاعبون في المنطقة السكنية قد يتجاوزون الحدود. بوصفك سائقاً حذراً يجب تعديل سلوك قيادتك وفق الظروف الفعلية حتى بعد F12b."))
)
Save-Q "F12b" $f12bQs

Write-Host "`nDone: F11, F12a, F12b fully rewritten with correct content and 8 questions each`n"
