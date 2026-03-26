[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8
$base = "C:\Users\haydar\Desktop\end_project\readyroad\src\main\resources\data\signs_import"

function Save-Q($code, $qs) {
    $path = Join-Path $base "$code\questions.json"
    $json = $qs | ConvertTo-Json -Depth 20 -Compress
    [System.IO.File]::WriteAllText($path, $json, (New-Object System.Text.UTF8Encoding $false))
    Write-Host "$code : $($qs.Count) questions"
}
function Load-Q($code) {
    $path = Join-Path $base "$code\questions.json"
    $text = [System.Text.Encoding]::UTF8.GetString([System.IO.File]::ReadAllBytes($path)).TrimStart([char]0xFEFF)
    return $text | ConvertFrom-Json
}
function C3($ok, $w1, $w2) { @([ordered]@{text=$ok;is_correct=$true}; [ordered]@{text=$w1;is_correct=$false}; [ordered]@{text=$w2;is_correct=$false}) }
function C2($ok, $w1) { @([ordered]@{text=$ok;is_correct=$true}; [ordered]@{text=$w1;is_correct=$false}) }
function I18($nlQ, $nlC, $nlE, $enQ, $enC, $enE, $frQ, $frC, $frE, $arQ, $arC, $arE) {
    [ordered]@{NL=[ordered]@{question=$nlQ;choices=$nlC;explanation=$nlE};EN=[ordered]@{question=$enQ;choices=$enC;explanation=$enE};FR=[ordered]@{question=$frQ;choices=$frC;explanation=$frE};AR=[ordered]@{question=$arQ;choices=$arC;explanation=$arE}}
}
function Q($id, $type, $diff, $crit, $i18n) {
    [ordered]@{question_id=$id;type=$type;difficulty=$diff;is_critical=$crit;show_sign=$true;i18n=$i18n}
}

# ======================== F101c (add Q06 Q07 Q08) ========================
$f101cNew = @(
(Q "F101c_Q06" "WHAT_MUST_YOU_DO" "MEDIUM" $false (I18 `
    "Welk bord vormt het beginpunt bij een F101c-voorbehouden zone?" `
    (C3 "Een bord uit de F99/F101-reeks dat de voorbehouden weg opende zoals F99a of F101a" "F103 begin voetgangerszone" "F17 autosnelweg begin") `
    "F101c beëindigt een voorbehouden weg. Die weg werd eerder geopend door een bijpassend beginbord uit de F99/F101-reeks zoals F99a (voetgangers fietsers ruiters)." `
    "Which sign forms the starting point for an F101c reserved zone?" `
    (C3 "A sign from the F99/F101 series that opened the reserved road such as F99a or F101a" "F103 start of pedestrian zone" "F17 motorway start") `
    "F101c ends a reserved road. That road was previously opened by a matching start sign from the F99/F101 series such as F99a (pedestrians cyclists horse riders)." `
    "Quel panneau forme le point de depart d une zone reservee F101c ?" `
    (C3 "Un panneau de la serie F99/F101 qui a ouvert la route reservee comme F99a ou F101a" "F103 debut zone pietonne" "F17 debut autoroute") `
    "F101c termine une route reservee. Cette route a prealablement ete ouverte par un panneau de debut de la serie F99/F101 comme F99a (pietons cyclistes cavaliers)." `
    "ما هي اللافتة التي تُشكّل نقطة البداية لمنطقة F101c المحجوزة؟" `
    (C3 "لافتة من سلسلة F99/F101 فتحت الطريق المحجوز مثل F99a أو F101a" "F103 بداية منطقة المشاة" "F17 بداية الطريق السريع") `
    "تنهي F101c طريقاً محجوزاً. فُتح ذلك الطريق مسبقاً بلافتة بداية من سلسلة F99/F101 مثل F99a (مشاة ودراجون وراكبو خيل)."))
(Q "F101c_Q07" "IS_IT_ALLOWED" "HARD" $true (I18 `
    "Mag een motorvoertuig na F101c de voormalige voorbehouden weg oprijden?" `
    (C2 "Ja tenzij een ander bord dit verbiedt gelden na F101c normale verkeersregels voor iedereen" "Nee motorvoertuigen blijven verboden ook na F101c") `
    "Na F101c vervalt de voorbehoudenstatus. Normale verkeersregels zijn van toepassing tenzij een ander bord bijkomende beperkingen oplegt." `
    "May a motor vehicle drive on the former reserved road after F101c?" `
    (C2 "Yes unless another sign prohibits it normal traffic rules apply after F101c for everyone" "No motor vehicles remain prohibited even after F101c") `
    "After F101c the reserved status ends. Normal traffic rules apply unless another sign imposes additional restrictions." `
    "Un vehicule a moteur peut-il emprunter l ancienne route reservee apres F101c ?" `
    (C2 "Oui sauf si un autre panneau l interdit les regles normales s appliquent apres F101c" "Non les vehicules a moteur restent interdits meme apres F101c") `
    "Apres F101c le statut reserve prend fin. Les regles normales s appliquent sauf si un autre panneau impose des restrictions supplementaires." `
    "هل يجوز للمركبة الآلية السير على الطريق المحجوز السابق بعد F101c؟" `
    (C2 "نعم ما لم تحظر لافتة أخرى تسري قواعد المرور العادية بعد F101c للجميع" "لا تبقى المركبات الآلية محظورة حتى بعد F101c") `
    "بعد F101c ينتهي الوضع المحجوز. تسري قواعد المرور العادية ما لم تفرض لافتة أخرى قيوداً إضافية."))
(Q "F101c_Q08" "IS_IT_ALLOWED" "HARD" $true (I18 `
    "Is een fietser na F101c nog verplicht het voormalige pad te gebruiken?" `
    (C2 "Nee na F101c vervalt de verplichting om het pad te gebruiken" "Ja fietsers moeten het pad blijven gebruiken ook voorbij F101c") `
    "De verplicht-gebruik-verplichting geldt alleen binnen de zone aangeduid door het begin- en eindbord. Na F101c is er geen verplichting meer." `
    "Is a cyclist still required to use the former path after F101c?" `
    (C2 "No after F101c the obligation to use the path ceases" "Yes cyclists must continue to use the path even beyond F101c") `
    "The compulsory use obligation only applies within the zone indicated by the start and end signs. After F101c there is no more obligation." `
    "Un cycliste est-il encore oblige d utiliser l ancien chemin apres F101c ?" `
    (C2 "Non apres F101c l obligation d utiliser le chemin cesse" "Oui les cyclistes doivent continuer meme au-dela de F101c") `
    "L obligation d utilisation s applique uniquement dans la zone indiquee par les panneaux de debut et de fin. Apres F101c il n y a plus d obligation." `
    "هل لا يزال الدراج ملزماً باستخدام المسار السابق بعد F101c؟" `
    (C2 "لا بعد F101c تنتهي إلزامية استخدام المسار" "نعم يجب على الدراجين الاستمرار حتى بعد F101c") `
    "تنطبق إلزامية الاستخدام فقط داخل المنطقة المشار إليها بلافتتي البداية والنهاية. بعد F101c لا توجد إلزامية."))
)
Save-Q "F101c" ((Load-Q "F101c") + $f101cNew)

# ======================== F103 (add Q06 Q07 Q08) ========================
$f103New = @(
(Q "F103_Q06" "WHAT_MUST_YOU_DO" "MEDIUM" $false (I18 `
    "Tijdens welke uren mogen voertuigen een F103-voetgangerszone betreden voor laden en lossen?" `
    (C3 "Van 6 uur tot 12 uur mogen bestelwagens en kleine vrachtwagens laden en lossen" "Voertuigen mogen de zone nooit betreden" "Voertuigen mogen 24 uur laden en lossen") `
    "De voetgangerszone aangeduid door F103 staat het betreden voor laden en lossen uitsluitend toe tussen 6:00 en 12:00. Buiten die uren is de zone ontoegankelijk voor motorvoertuigen." `
    "During which hours may vehicles enter an F103 pedestrian zone for loading and unloading?" `
    (C3 "From 6 to 12 o'clock vans and small trucks may load and unload" "Vehicles may never enter the zone" "Vehicles may load and unload 24 hours a day") `
    "The pedestrian zone indicated by F103 allows entry for loading and unloading exclusively between 6:00 and 12:00. Outside those hours the zone is inaccessible to motor vehicles." `
    "Pendant quelles heures les vehicules peuvent-ils entrer dans une zone pietonne F103 pour le chargement ?" `
    (C3 "De 6h a 12h les camionnettes et petits camions peuvent charger et decharger" "Les vehicules ne peuvent jamais entrer" "Les vehicules peuvent entrer 24h/24") `
    "La zone pietonne indiquee par F103 permet l acces pour le chargement et dechargement exclusivement entre 6h00 et 12h00. En dehors de ces heures la zone est inaccessible." `
    "خلال أي ساعات يمكن للمركبات دخول منطقة مشاة F103 للتحميل والتفريغ؟" `
    (C3 "من 6 حتى 12 يمكن للفانات والشاحنات الصغيرة التحميل والتفريغ" "لا يمكن للمركبات أبداً دخول المنطقة" "يمكن للمركبات التحميل 24 ساعة") `
    "تسمح منطقة المشاة المشار إليها بـF103 بالدخول للتحميل والتفريغ حصراً بين 6:00 و12:00. خارج تلك الساعات تُغلق المنطقة أمام المركبات."))
(Q "F103_Q07" "IS_IT_ALLOWED" "HARD" $true (I18 `
    "Mag een leveringswagen een F103-voetgangerszone betreden om 14 uur?" `
    (C2 "Nee leveringen zijn alleen toegestaan tussen 6 en 12 uur; om 14 uur is het betreden verboden" "Ja leveringswagens mogen de zone altijd betreden") `
    "De voetgangerszone is na 12 uur gesloten voor alle voertuigen inclusief leveringswagens. Een wagen die om 14 uur de zone betreedt overtreedt de verkeersregels." `
    "May a delivery van enter an F103 pedestrian zone at 14:00?" `
    (C2 "No deliveries are only permitted between 6 and 12; entering at 14:00 is prohibited" "Yes delivery vans may always enter the zone") `
    "The pedestrian zone is closed after 12:00 to all vehicles including delivery vans. A vehicle entering at 14:00 violates traffic law." `
    "Un camion de livraison peut-il entrer dans une zone pietonne F103 a 14h ?" `
    (C2 "Non les livraisons ne sont autorisees qu entre 6h et 12h; entrer a 14h est interdit" "Oui les camions peuvent toujours entrer") `
    "La zone pietonne est fermee apres 12h a tous les vehicules. Un vehicule entrant a 14h enfreint le code de la route." `
    "هل يمكن لشاحنة توصيل دخول منطقة مشاة F103 الساعة 14:00؟" `
    (C2 "لا يُسمح بالتوصيل فقط بين 6 و12؛ الدخول الساعة 14:00 محظور" "نعم يمكن لشاحنات التوصيل الدخول دائماً") `
    "منطقة المشاة مغلقة بعد 12:00 لجميع المركبات. المركبة التي تدخل الساعة 14:00 تنتهك قواعد المرور."))
(Q "F103_Q08" "IS_IT_ALLOWED" "HARD" $true (I18 `
    "Is fietsen toegestaan in een F103-voetgangerszone?" `
    (C2 "Alleen als een onderbord dit uitdrukkelijk toestaat; anders is de zone enkel voor voetgangers" "Ja fietsen is altijd toegestaan in iedere voetgangerszone") `
    "Een voetgangerszone (F103) is in principe voorbehouden voor voetgangers. Fietsen is alleen toegestaan indien een onderbord dit expliciet aangeeft." `
    "Is cycling permitted in an F103 pedestrian zone?" `
    (C2 "Only if a sub-sign explicitly permits it; otherwise the zone is for pedestrians only" "Yes cycling is always permitted in every pedestrian zone") `
    "A pedestrian zone (F103) is in principle reserved for pedestrians. Cycling is only permitted if a sub-sign explicitly states this." `
    "Le cyclisme est-il autorise dans une zone pietonne F103 ?" `
    (C2 "Seulement si un sous-panneau l autorise explicitement; sinon la zone est pour les pietons" "Oui le cyclisme est toujours autorise dans chaque zone pietonne") `
    "Une zone pietonne (F103) est en principe reservee aux pietons. Le cyclisme n est autorise que si un sous-panneau l indique explicitement." `
    "هل يُسمح بركوب الدراجة في منطقة مشاة F103؟" `
    (C2 "فقط إذا سمح بذلك لافتة فرعية صراحةً؛ وإلا فالمنطقة للمشاة فقط" "نعم ركوب الدراجة مسموح به دائماً في كل منطقة مشاة") `
    "منطقة المشاة (F103) مخصصة مبدئياً للمشاة. يُسمح بركوب الدراجة فقط إذا نصت لافتة فرعية على ذلك صراحةً."))
)
Save-Q "F103" ((Load-Q "F103") + $f103New)

# ======================== F105 (add Q06 Q07 Q08) ========================
$f105New = @(
(Q "F105_Q06" "WHAT_MUST_YOU_DO" "MEDIUM" $false (I18 `
    "Welke regels gelden na het passeren van bord F105?" `
    (C3 "De normale verkeersregels; de beperkingen van de voetgangerszone vervallen" "De voetgangerszoneregels blijven nog 100 meter geldig" "Na F105 geldt een limiet van 20 km/u") `
    "F105 markeert het einde van de voetgangerszone. Vanaf dat punt gelden de gewone verkeersregels voor die weg." `
    "Which rules apply after passing sign F105?" `
    (C3 "Normal traffic rules; the pedestrian zone restrictions cease" "Pedestrian zone rules remain valid for another 100 metres" "A 20 km/h limit applies after F105") `
    "F105 marks the end of the pedestrian zone. From that point normal traffic rules apply for that road." `
    "Quelles regles s appliquent apres avoir passe le panneau F105 ?" `
    (C3 "Les regles normales; les restrictions de zone pietonne cessent" "Les regles pietonnes restent valables 100 metres encore" "Une limite 20 km/h s applique apres F105") `
    "F105 marque la fin de la zone pietonne. A partir de ce point les regles normales de circulation s appliquent." `
    "ما القواعد السارية بعد تجاوز لافتة F105؟" `
    (C3 "قواعد المرور العادية؛ تنتهي قيود منطقة المشاة" "تستمر قواعد منطقة المشاة 100 متر أخرى" "يسري حد 20 كم/ساعة بعد F105") `
    "تضع F105 علامة على نهاية منطقة المشاة. من تلك النقطة تسري قواعد المرور العادية للطريق."))
(Q "F105_Q07" "IS_IT_ALLOWED" "HARD" $true (I18 `
    "Mogen alle voertuigen zonder beperking rijden na het passeren van F105?" `
    (C2 "Ja de beperkingen van de voetgangerszone gelden niet meer; normale verkeersregels zijn van toepassing" "Nee motorvoertuigen blijven beperkt tot laad- en losverkeer ook na F105") `
    "F105 beëindigt uitdrukkelijk de voetgangerszone. Daarna gelden gewone verkeersregels voor alle voertuigen." `
    "May all vehicles drive without restriction after passing F105?" `
    (C2 "Yes the pedestrian zone restrictions no longer apply; normal traffic rules are in force" "No motor vehicles remain restricted to loading and unloading even after F105") `
    "F105 explicitly ends the pedestrian zone. After that normal traffic rules apply for all vehicles." `
    "Tous les vehicules peuvent-ils circuler sans restriction apres F105 ?" `
    (C2 "Oui les restrictions de zone pietonne ne s appliquent plus; les regles normales sont en vigueur" "Non les vehicules restent limites au chargement apres F105") `
    "F105 termine explicitement la zone pietonne. Apres cela les regles normales s appliquent." `
    "هل يستطيع جميع المركبات السير بدون قيود بعد F105؟" `
    (C2 "نعم لم تعد قيود منطقة المشاة سارية؛ وتسري قواعد المرور العادية" "لا تبقى المركبات مقيدة بالتحميل حتى بعد F105") `
    "تنهي F105 صراحةً منطقة المشاة. بعد ذلك تسري قواعد المرور العادية لجميع المركبات."))
(Q "F105_Q08" "IS_IT_ALLOWED" "HARD" $true (I18 `
    "Is het noodzakelijk om F105 aan elke uitgang van een voetgangerszone te plaatsen?" `
    (C2 "Ja F105 wordt aan elke uitgang geplaatst zodat weggebruikers weten dat de voetgangerszone eindigt" "Nee één bord bij de hoofdingang volstaat voor de volledige zone") `
    "Net zoals bij andere zones wordt het eindbord (F105) aan elke uitgang geplaatst. Zo weet elke bestuurder die de zone verlaat dat de aparte regels niet meer gelden." `
    "Is it necessary to place F105 at every exit of a pedestrian zone?" `
    (C2 "Yes F105 is placed at every exit so road users know the pedestrian zone ends" "No one sign at the main entrance is sufficient for the entire zone") `
    "As with other zones the end sign (F105) is placed at every exit. This way every driver leaving the zone knows the special rules no longer apply." `
    "Est-il necessaire de placer F105 a chaque sortie d une zone pietonne ?" `
    (C2 "Oui F105 est place a chaque sortie pour que les usagers sachent que la zone se termine" "Non un seul panneau a l entree principale suffit") `
    "Comme pour les autres zones le panneau de fin (F105) est place a chaque sortie. Ainsi chaque conducteur quittant la zone sait que les regles speciales ne s appliquent plus." `
    "هل من الضروري وضع F105 عند كل مخرج من منطقة المشاة؟" `
    (C2 "نعم يُوضع F105 عند كل مخرج حتى يعرف مستخدمو الطريق أن المنطقة تنتهي" "لا لافتة واحدة عند المدخل الرئيسي كافية") `
    "كما هو الحال مع المناطق الأخرى يُوضع لافتة النهاية (F105) عند كل مخرج. يعرف كل سائق مغادر المنطقة أن القواعد الخاصة لم تعد سارية."))
)
Save-Q "F105" ((Load-Q "F105") + $f105New)

# ======================== F111 (add Q06 Q07 Q08) ========================
$f111New = @(
(Q "F111_Q06" "WHAT_MUST_YOU_DO" "MEDIUM" $false (I18 `
    "Wat is de maximumsnelheid voor motorvoertuigen in een fietsstraat (F111)?" `
    (C3 "30 km/u; motorvoertuigen mogen niet sneller rijden dan 30 km/u in een fietsstraat" "50 km/u; de normale bebouwde-kom-snelheid" "20 km/u gelijk aan een woonerf") `
    "In een fietsstraat bedraagt de maximumsnelheid 30 km/u voor motorvoertuigen om de fietsers te beschermen die de volledige rijbaan mogen gebruiken." `
    "What is the maximum speed for motor vehicles in a bicycle street (F111)?" `
    (C3 "30 km/h; motor vehicles may not drive faster than 30 km/h in a bicycle street" "50 km/h; the normal built-up area speed" "20 km/h equal to a residential area") `
    "In a bicycle street the maximum speed is 30 km/h for motor vehicles to protect cyclists who may use the full carriageway." `
    "Quelle est la vitesse maximale pour les vehicules a moteur dans une rue cyclable (F111) ?" `
    (C3 "30 km/h; les vehicules a moteur ne peuvent pas rouler plus vite que 30 km/h" "50 km/h; la vitesse normale en agglomeration" "20 km/h comme une zone residentielle") `
    "Dans une rue cyclable la vitesse maximale est 30 km/h pour les vehicules a moteur afin de proteger les cyclistes qui peuvent utiliser toute la chaussee." `
    "ما الحد الأقصى للسرعة للمركبات الآلية في شارع الدراجات (F111)؟" `
    (C3 "30 كم/ساعة؛ لا يجوز للمركبات الآلية السير أسرع من 30 كم/ساعة" "50 كم/ساعة؛ السرعة العادية في المناطق المبنية" "20 كم/ساعة مثل الحي السكني") `
    "في شارع الدراجات الحد الأقصى 30 كم/ساعة للمركبات الآلية لحماية الدراجين الذين يجوز لهم استخدام كامل الطريق."))
(Q "F111_Q07" "IS_IT_ALLOWED" "HARD" $true (I18 `
    "Mag een automobilist een fietser inhalen in een fietsstraat (F111)?" `
    (C2 "Nee motorvoertuigen mogen fietsers niet inhalen; fietsers hebben het recht de volledige rijbaan te gebruiken" "Ja inhalen is altijd toegestaan als er voldoende ruimte is") `
    "In een fietsstraat hebben fietsers absolute voorrang en mogen de volledige rijbaan gebruiken. Motorvoertuigen mogen fietsers NIET inhalen." `
    "May a motorist overtake a cyclist in a bicycle street (F111)?" `
    (C2 "No motor vehicles may not overtake cyclists; cyclists have the right to use the full carriageway" "Yes overtaking is always permitted if there is enough space") `
    "In a bicycle street cyclists have absolute priority and may use the full carriageway. Motor vehicles may NOT overtake cyclists." `
    "Un automobiliste peut-il depasser un cycliste dans une rue cyclable (F111) ?" `
    (C2 "Non les vehicules a moteur ne peuvent pas depasser les cyclistes; ils ont le droit d utiliser toute la chaussee" "Oui le depassement est toujours permis avec suffisamment d espace") `
    "Dans une rue cyclable les cyclistes ont une priorite absolue et peuvent utiliser toute la chaussee. Les vehicules a moteur ne peuvent PAS depasser les cyclistes." `
    "هل يجوز للسائق تجاوز دراج في شارع الدراجات (F111)؟" `
    (C2 "لا لا يجوز للمركبات الآلية تجاوز الدراجين؛ للدراجين الحق في استخدام كامل الطريق" "نعم التجاوز مسموح به دائماً إذا كانت المساحة كافية") `
    "في شارع الدراجات يتمتع الدراجون بالأولوية المطلقة ويجوز لهم استخدام كامل الطريق. لا يجوز للمركبات الآلية تجاوز الدراجين."))
(Q "F111_Q08" "IS_IT_ALLOWED" "HARD" $true (I18 `
    "Mogen fietsers naast elkaar rijden in een fietsstraat (F111)?" `
    (C2 "Ja in een fietsstraat mogen fietsers twee aan twee naast elkaar rijden" "Nee fietsers moeten altijd achter elkaar rijden in een fietsstraat") `
    "In een fietsstraat mag je als fietser naast een andere fietser rijden (twee aan twee). Fietsers mogen de volledige rijbaan gebruiken ook om naast elkaar te rijden." `
    "May cyclists ride side by side in a bicycle street (F111)?" `
    (C2 "Yes in a bicycle street cyclists may ride two abreast" "No cyclists must always ride in single file in a bicycle street") `
    "In a bicycle street you may ride next to another cyclist (two abreast). Cyclists may use the full carriageway including to ride side by side." `
    "Les cyclistes peuvent-ils rouler cote a cote dans une rue cyclable (F111) ?" `
    (C2 "Oui dans une rue cyclable les cyclistes peuvent rouler deux de front" "Non les cyclistes doivent toujours rouler en file indienne") `
    "Dans une rue cyclable vous pouvez rouler a cote d un autre cycliste (deux de front). Les cyclistes peuvent utiliser toute la chaussee y compris pour rouler cote a cote." `
    "هل يجوز للدراجين السير جنباً إلى جنب في شارع الدراجات (F111)؟" `
    (C2 "نعم يجوز للدراجين السير اثنين جنباً إلى جنب في شارع الدراجات" "لا يجب على الدراجين دائماً السير في صف واحد") `
    "في شارع الدراجات يمكنك السير بجانب دراج آخر (اثنين جنباً إلى جنب). يجوز للدراجين استخدام كامل الطريق بما في ذلك السير جنباً إلى جنب."))
)
Save-Q "F111" ((Load-Q "F111") + $f111New)

# ======================== F113 (add Q06 Q07 Q08) ========================
$f113New = @(
(Q "F113_Q06" "WHAT_MUST_YOU_DO" "MEDIUM" $false (I18 `
    "Mag een automobilist na bord F113 een fietser inhalen?" `
    (C3 "Ja na F113 gelden normale verkeersregels en mag men fietsers inhalen als het veilig is" "Nee het inhalverbod voor fietsers blijft 200 meter voorbij F113 van kracht" "Inhalen mag pas na 50 meter na F113") `
    "F113 markeert het einde van de fietsstraat. Voorbij dit bord is het inhalverbod voor fietsers niet meer van toepassing. Inhalen is weer toegestaan mits veilig." `
    "May a motorist overtake a cyclist after sign F113?" `
    (C3 "Yes after F113 normal traffic rules apply and overtaking cyclists is permitted when safe" "No the overtaking prohibition remains in force 200 metres beyond F113" "Overtaking is only permitted after 50 metres") `
    "F113 marks the end of the bicycle street. Beyond this sign the no-overtaking rule for cyclists no longer applies. Overtaking is permitted again provided it is safe." `
    "Un automobiliste peut-il depasser un cycliste apres le panneau F113 ?" `
    (C3 "Oui apres F113 les regles normales s appliquent et il est permis de depasser si c est securise" "Non l interdiction de depasser reste en vigueur 200 metres apres F113" "On ne peut depasser qu apres 50 metres") `
    "F113 marque la fin de la rue cyclable. Au-dela les regles speciales ne s appliquent plus. Il est a nouveau permis de depasser les cyclistes si c est securise." `
    "هل يجوز للسائق تجاوز دراج بعد لافتة F113؟" `
    (C3 "نعم بعد F113 تسري قواعد المرور العادية ويُسمح بتجاوز الدراجين عند الأمان" "لا يبقى حظر التجاوز سارياً 200 متر بعد F113" "يُسمح بالتجاوز فقط بعد 50 متراً") `
    "تضع F113 علامة على نهاية شارع الدراجات. بعد هذه اللافتة لا يسري حكم حظر التجاوز. يُسمح مجدداً بتجاوز الدراجين إذا كان آمناً."))
(Q "F113_Q07" "IS_IT_ALLOWED" "HARD" $true (I18 `
    "Geldt de 30 km/u limiet van de fietsstraat nog na het passeren van F113?" `
    (C2 "Nee de 30 km/u limiet vervalt na F113; de normale snelheidslimiet van de weg is van toepassing" "Ja de 30 km/u limiet blijft gelden tot een nieuw snelheidsbord anders aangeeft") `
    "De 30 km/u limiet is een specifieke regelgeving voor de fietsstraat. Zodra je F113 passeert geldt de normale snelheidslimiet van die weg." `
    "Does the 30 km/h limit of the bicycle street still apply after passing F113?" `
    (C2 "No the 30 km/h limit ceases after F113; the normal speed limit of the road applies" "Yes the 30 km/h limit continues until a new speed sign indicates otherwise") `
    "The 30 km/h speed limit is specific to the bicycle street. Once you pass F113 the normal speed limit for that road applies." `
    "La limite de 30 km/h de la rue cyclable s applique-t-elle encore apres F113 ?" `
    (C2 "Non la limite de 30 km/h cesse apres F113; la limite normale de la route s applique" "Oui la limite de 30 km/h continue jusqu a un nouveau panneau de vitesse") `
    "La limite de 30 km/h est specifique a la rue cyclable. Une fois que vous passez F113 la limite normale de la route s applique." `
    "هل يسري حد 30 كم/ساعة لشارع الدراجات بعد F113؟" `
    (C2 "لا ينتهي حد 30 كم/ساعة بعد F113؛ يسري حد السرعة العادي للطريق" "نعم يستمر حد 30 كم/ساعة حتى تشير لافتة سرعة جديدة لحد مختلف") `
    "حد السرعة 30 كم/ساعة خاص بشارع الدراجات. بمجرد تجاوز F113 يسري حد السرعة العادي للطريق."))
(Q "F113_Q08" "IS_IT_ALLOWED" "HARD" $true (I18 `
    "Moet F113 aan elke uitgang van een fietsstraat geplaatst worden?" `
    (C2 "Ja F113 staat aan elke uitgang zodat bestuurders weten waar de fietsstraatregels eindigen" "Nee één F113-bord aan de hoofduitgang volstaat") `
    "Het eindbord (F113) wordt aan elke uitgang van de fietsstraat geplaatst zodat bestuurders die de zone via een zijstraat verlaten ook weten dat de fietsstraatregels niet meer gelden." `
    "Must F113 be placed at every exit of a bicycle street?" `
    (C2 "Yes F113 is placed at every exit so drivers know where the bicycle street rules end" "No one F113 at the main exit is sufficient") `
    "The end sign (F113) is placed at every exit of the bicycle street ensuring drivers leaving via a side street also know the bicycle street rules no longer apply." `
    "Doit-on placer F113 a chaque sortie d une rue cyclable ?" `
    (C2 "Oui F113 est place a chaque sortie pour que les conducteurs sachent ou les regles se terminent" "Non un seul F113 a la sortie principale suffit") `
    "Le panneau de fin (F113) est place a chaque sortie de la rue cyclable pour que les conducteurs quittant par une rue laterale sachent aussi que les regles ne s appliquent plus." `
    "هل يجب وضع F113 عند كل مخرج من شارع الدراجات؟" `
    (C2 "نعم يُوضع F113 عند كل مخرج حتى يعرف السائقون أين تنتهي قواعد شارع الدراجات" "لا لافتة F113 واحدة عند المخرج الرئيسي كافية") `
    "يُوضع لافتة النهاية (F113) عند كل مخرج من شارع الدراجات لضمان معرفة السائقين المغادرين عبر شارع جانبي بانتهاء القواعد."))
)
Save-Q "F113" ((Load-Q "F113") + $f113New)

Write-Host "`nDone: F101c F103 F105 F111 F113 extended to 8 questions`n"
