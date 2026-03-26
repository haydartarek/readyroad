[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8

$base = "C:\Users\haydar\Desktop\end_project\readyroad\src\main\resources\data\signs_import"

function Save-Questions($code, $questions) {
    $path = Join-Path $base "$code\questions.json"
    $json = $questions | ConvertTo-Json -Depth 20 -Compress
    [System.IO.File]::WriteAllText($path, $json, (New-Object System.Text.UTF8Encoding $false))
    Write-Host "$code : written $($questions.Count) questions"
}

function Load-Questions($code) {
    $path = Join-Path $base "$code\questions.json"
    $text = [System.Text.Encoding]::UTF8.GetString([System.IO.File]::ReadAllBytes($path)).TrimStart([char]0xFEFF)
    return $text | ConvertFrom-Json
}

function q($id, $type, $diff, $crit, $nlQ, $nlC1, $nlC2, $nlC3, $nlE,
                                      $enQ, $enC1, $enC2, $enC3, $enE,
                                      $frQ, $frC1, $frC2, $frC3, $frE,
                                      $arQ, $arC1, $arC2, $arC3, $arE) {
    $choices2 = if ($nlC3) {
        @([ordered]@{text=$nlC1;is_correct=$true},[ordered]@{text=$nlC2;is_correct=$false},[ordered]@{text=$nlC3;is_correct=$false})
    } else {
        @([ordered]@{text=$nlC1;is_correct=$true},[ordered]@{text=$nlC2;is_correct=$false})
    }
    $enChoices = if ($enC3) {
        @([ordered]@{text=$enC1;is_correct=$true},[ordered]@{text=$enC2;is_correct=$false},[ordered]@{text=$enC3;is_correct=$false})
    } else {
        @([ordered]@{text=$enC1;is_correct=$true},[ordered]@{text=$enC2;is_correct=$false})
    }
    $frChoices = if ($frC3) {
        @([ordered]@{text=$frC1;is_correct=$true},[ordered]@{text=$frC2;is_correct=$false},[ordered]@{text=$frC3;is_correct=$false})
    } else {
        @([ordered]@{text=$frC1;is_correct=$true},[ordered]@{text=$frC2;is_correct=$false})
    }
    $arChoices = if ($arC3) {
        @([ordered]@{text=$arC1;is_correct=$true},[ordered]@{text=$arC2;is_correct=$false},[ordered]@{text=$arC3;is_correct=$false})
    } else {
        @([ordered]@{text=$arC1;is_correct=$true},[ordered]@{text=$arC2;is_correct=$false})
    }
    return [ordered]@{
        question_id=$id; type=$type; difficulty=$diff; is_critical=$crit; show_sign=$true
        i18n=[ordered]@{
            NL=[ordered]@{question=$nlQ;choices=$choices2;explanation=$nlE}
            EN=[ordered]@{question=$enQ;choices=$enChoices;explanation=$enE}
            FR=[ordered]@{question=$frQ;choices=$frChoices;explanation=$frE}
            AR=[ordered]@{question=$arQ;choices=$arChoices;explanation=$arE}
        }
    }
}

# ============================================================
# F101c: Einde weg voorbehouden aanvullende vragen
# ============================================================
$f101cExtra = @(
    (q "F101c_Q06" "WHAT_MUST_YOU_DO" "MEDIUM" $false
        "Welk bord vormt het beginpunt van de weg die eindigt bij F101c?"
        "F99a of een gelijkaardig F99/F101-bord dat de voorbehouden weg aanduidt"
        "F103 begin voetgangerszone" "F1a autosnelweg" 
        "F101c beëindigt een weg die werd aangeduid met één van de F99/F101-reeksborden zoals F99a (voetgangers fietsers ruiters) of F101a/b/c. Dit paar van begin- en eindborden begrenst de voorbehouden wegzone."
        "Which sign marks the beginning of the road that ends with F101c?"
        "F99a or a similar F99/F101 sign indicating the reserved path"
        "F103 start of pedestrian zone" "F1a motorway"
        "F101c ends a road that was indicated with one of the F99/F101 series signs such as F99a (pedestrians cyclists horse riders) or F101a/b/c. This pair of start and end signs delimits the reserved road zone."
        "Quel panneau marque le debut de la route qui se termine par F101c ?"
        "F99a ou un panneau F99/F101 similaire indiquant le chemin reserve"
        "F103 debut zone pietonne" "F1a autoroute"
        "F101c termine une route qui etait indiquee par l un des panneaux de la serie F99/F101 comme F99a (pietons cyclistes cavaliers) ou F101a/b/c. Cette paire de panneaux de debut et de fin delimite la zone de route reservee."
        "ما هي اللافتة التي تمثل نقطة البداية للطريق الذي ينتهي عند F101c؟"
        "F99a أو لافتة مماثلة من سلسلة F99/F101 تشير إلى المسار المحجوز"
        "F103 بداية منطقة المشاة" "F1a طريق سريع"
        "تنهي F101c طريقاً كان مُشاراً إليه بأحد لافتات سلسلة F99/F101 مثل F99a (مشاة وراكبو دراجات وراكبو خيل) أو F101a/b/c. يحدد هذا الزوج من لافتات البداية والنهاية منطقة الطريق المحجوز.")

    (q "F101c_Q07" "IS_IT_ALLOWED" "HARD" $true
        "Mag een motorvoertuig na bord F101c de voormalige voorbehouden weg oprijden?"
        "Ja tenzij een ander bord dit verbiedt gelden er na F101c de normale verkeersregels voor alle weggebruikers"
        "Nee motorvoertuigen blijven altijd verboden ook voorbij F101c"
        $null
        "Na F101c vervalt de voorbehoudenstatus van de weg. Tenzij een ander bord bijkomende beperkingen oplegt (bv. een doorgaand verbod) mogen alle voertuigen de weg gebruiken."
        "May a motor vehicle drive on the former reserved road after sign F101c?"
        "Yes unless another sign prohibits it normal traffic rules apply after F101c for all road users"
        "No motor vehicles remain always prohibited even beyond F101c"
        $null
        "After F101c the reserved status of the road ends. Unless another sign imposes additional restrictions for example a continuing prohibition all vehicles may use the road."
        "Un vehicule a moteur peut-il emprunter l ancienne route reservee apres le panneau F101c ?"
        "Oui sauf si un autre panneau l interdit les regles normales de circulation s appliquent apres F101c pour tous les usagers"
        "Non les vehicules a moteur restent toujours interdits meme au-dela de F101c"
        $null
        "Apres F101c le statut reserve de la route prend fin. Sauf si un autre panneau impose des restrictions supplementaires par exemple une interdiction persistante tous les vehicules peuvent utiliser la route."
        "هل يجوز للمركبة الآلية السير على الطريق المحجوز السابق بعد لافتة F101c؟"
        "نعم ما لم تحظر لافتة أخرى ذلك تسري قواعد المرور العادية بعد F101c لجميع مستخدمي الطريق"
        "لا تبقى المركبات الآلية محظورة دائماً حتى بعد F101c"
        $null
        "بعد F101c ينتهي الوضع المحجوز للطريق. ما لم تفرض لافتة أخرى قيوداً إضافية مثل حظر مستمر يجوز لجميع المركبات استخدام الطريق.")

    (q "F101c_Q08" "IS_IT_ALLOWED" "HARD" $true
        "Is een fietser na bord F101c nog verplicht het voormalige voorbehouden pad te gebruiken?"
        "Nee na F101c is het gebruik van het vroegere pad niet meer verplicht en gelden normale verkeersregels"
        "Ja fietsers moeten het pad blijven gebruiken ook na F101c"
        $null
        "Het verplicht gebruik van het voorbehouden pad geldt alleen binnen de zone aangeduid door het beginbord (F99a of gelijkaardig) en het eindbord F101c. Na F101c vervalt die verplichting."
        "Is a cyclist still required to use the former reserved path after sign F101c?"
        "No after F101c use of the former path is no longer compulsory and normal traffic rules apply"
        "Yes cyclists must continue to use the path even after F101c"
        $null
        "The compulsory use of the reserved path only applies within the zone indicated by the start sign (F99a or similar) and the end sign F101c. After F101c that obligation ceases."
        "Un cycliste est-il encore oblige d utiliser l ancien chemin reserve apres le panneau F101c ?"
        "Non apres F101c l utilisation de l ancien chemin n est plus obligatoire et les regles normales de circulation s appliquent"
        "Oui les cyclistes doivent continuer a utiliser le chemin meme apres F101c"
        $null
        "L utilisation obligatoire du chemin reserve s applique uniquement dans la zone indiquee par le panneau de debut (F99a ou similaire) et le panneau de fin F101c. Apres F101c cette obligation cesse."
        "هل لا يزال الدراج ملزماً باستخدام المسار المحجوز السابق بعد لافتة F101c؟"
        "لا بعد F101c لم يعد استخدام المسار السابق إلزامياً وتسري قواعد المرور العادية"
        "نعم يجب على الدراجين الاستمرار في استخدام المسار حتى بعد F101c"
        $null
        "ينطبق الاستخدام الإلزامي للمسار المحجوز فقط داخل المنطقة المشار إليها بلافتة البداية (F99a أو مماثلة) ولافتة النهاية F101c. بعد F101c تنتهي هذه الالتزامية.")
)

$f101c = Load-Questions "F101c"
Save-Questions "F101c" ($f101c + $f101cExtra)

# ============================================================
# F103: Begin voetgangerszone
# ============================================================
$f103Extra = @(
    (q "F103_Q06" "WHAT_MUST_YOU_DO" "MEDIUM" $false
        "Tijdens welke uren mogen voertuigen een voetgangerszone (F103) betreden voor laden en lossen?"
        "Van 6 uur tot 12 uur mogen bestelwagens en kleine vrachtwagens laden en lossen"
        "Voertuigen mogen de zone nooit betreden voor wat doel ook"
        "Voertuigen mogen de zone 24 uur per dag betreden voor laden en lossen"
        "De voetgangerszone aangeduid door F103 staat het betreden voor laden en lossen uitsluitend toe tussen 6:00 en 12:00. Buiten die uren is de zone ontoegankelijk voor motorvoertuigen."
        "During which hours may vehicles enter a pedestrian zone (F103) for loading and unloading?"
        "From 6 to 12 o'clock vans and small trucks may load and unload"
        "Vehicles may never enter the zone for any purpose"
        "Vehicles may enter the zone 24 hours a day for loading and unloading"
        "The pedestrian zone indicated by F103 allows entry for loading and unloading exclusively between 6:00 and 12:00. Outside those hours the zone is inaccessible to motor vehicles."
        "Pendant quelles heures les vehicules peuvent-ils entrer dans une zone pietonne (F103) pour charger et decharger ?"
        "De 6 heures a 12 heures les camionnettes et petits camions peuvent charger et decharger"
        "Les vehicules ne peuvent jamais entrer dans la zone quelle qu en soit la raison"
        "Les vehicules peuvent entrer dans la zone 24 heures sur 24 pour charger et decharger"
        "La zone pietonne indiquee par F103 permet l acces pour le chargement et le dechargement exclusivement entre 6h00 et 12h00. En dehors de ces heures la zone est inaccessible aux vehicules a moteur."
        "خلال أي ساعات يمكن للمركبات دخول منطقة المشاة (F103) للتحميل والتفريغ؟"
        "من الساعة 6 إلى 12 يمكن للشاحنات الصغيرة والفانات التحميل والتفريغ"
        "لا يمكن للمركبات أبداً دخول المنطقة لأي غرض"
        "يمكن للمركبات دخول المنطقة على مدار 24 ساعة للتحميل والتفريغ"
        "تسمح منطقة المشاة المشار إليها بـF103 بالدخول للتحميل والتفريغ حصراً بين 6:00 و12:00. خارج تلك الساعات تكون المنطقة غير مسموح فيها للمركبات الآلية.")

    (q "F103_Q07" "IS_IT_ALLOWED" "HARD" $true
        "Mag een leveringswagen een F103-voetgangerszone betreden om 14 uur?"
        "Nee leveringen zijn alleen toegestaan tussen 6 en 12 uur; om 14 uur is het betreden verboden"
        "Ja leveringswagens mogen de zone altijd betreden ongeacht het uur"
        $null
        "De voetgangerszone is na 12 uur gesloten voor alle voertuigen inclusief leveringswagens. Een wagen die om 14 uur de zone betreedt overtreedt de verkeersregels."
        "May a delivery van enter an F103 pedestrian zone at 14:00?"
        "No deliveries are only permitted between 6 and 12 o'clock; entering at 14:00 is prohibited"
        "Yes delivery vans may always enter the zone regardless of the time"
        $null
        "The pedestrian zone is closed after 12:00 to all vehicles including delivery vans. A vehicle entering the zone at 14:00 violates traffic law."
        "Un camion de livraison peut-il entrer dans une zone pietonne F103 a 14 heures ?"
        "Non les livraisons ne sont autorisees qu entre 6 et 12 heures; entrer a 14 heures est interdit"
        "Oui les camions de livraison peuvent toujours entrer dans la zone quelle que soit l heure"
        $null
        "La zone pietonne est fermee apres 12h a tous les vehicules y compris les camions de livraison. Un vehicule qui entre dans la zone a 14h enfreint le code de la route."
        "هل يمكن لشاحنة توصيل دخول منطقة مشاة F103 الساعة 14:00؟"
        "لا يُسمح بالتوصيل فقط بين الساعة 6 و12؛ الدخول الساعة 14:00 محظور"
        "نعم يمكن لشاحنات التوصيل الدخول دائماً بغض النظر عن الوقت"
        $null
        "منطقة المشاة مغلقة بعد الساعة 12:00 لجميع المركبات بما في ذلك شاحنات التوصيل. المركبة التي تدخل المنطقة الساعة 14:00 تنتهك قواعد المرور.")

    (q "F103_Q08" "IS_IT_ALLOWED" "HARD" $true
        "Is fietsen toegestaan in een voetgangerszone aangeduid door F103?"
        "Alleen als een onderbord of aanvullend bord dit uitdrukkelijk toestaat; anders is de zone alleen voor voetgangers"
        "Ja fietsen is altijd toegestaan in iedere voetgangerszone"
        $null
        "Een voetgangerszone (F103) is in principe voorbehouden voor voetgangers. Fietsen is alleen toegestaan indien een onderbord of aanvullend bord dit expliciet aangeeft."
        "Is cycling permitted in a pedestrian zone indicated by F103?"
        "Only if a sub-sign or additional sign explicitly permits it; otherwise the zone is for pedestrians only"
        "Yes cycling is always permitted in every pedestrian zone"
        $null
        "A pedestrian zone (F103) is in principle reserved for pedestrians. Cycling is only permitted if a sub-sign or additional sign explicitly states this."
        "Le cyclisme est-il autorise dans une zone pietonne indiquee par F103 ?"
        "Seulement si un sous-panneau ou un panneau supplementaire l autorise explicitement; sinon la zone est reservee aux pietons"
        "Oui le cyclisme est toujours autorise dans chaque zone pietonne"
        $null
        "Une zone pietonne (F103) est en principe reservee aux pietons. Le cyclisme n est autorise que si un sous-panneau ou un panneau supplementaire l indique explicitement."
        "هل يُسمح بركوب الدراجة في منطقة مشاة مشار إليها بـF103؟"
        "فقط إذا سمح بذلك لافتة فرعية أو لافتة إضافية صراحةً؛ وإلا فالمنطقة للمشاة فقط"
        "نعم ركوب الدراجة مسموح به دائماً في كل منطقة مشاة"
        $null
        "منطقة المشاة (F103) مخصصة مبدئياً للمشاة. يُسمح بركوب الدراجة فقط إذا نصت لافتة فرعية أو لافتة إضافية على ذلك صراحةً.")
)

$f103 = Load-Questions "F103"
Save-Questions "F103" ($f103 + $f103Extra)

# ============================================================
# F105: Einde voetgangerszone
# ============================================================
$f105Extra = @(
    (q "F105_Q06" "WHAT_MUST_YOU_DO" "MEDIUM" $false
        "Welke verkeersregels gelden na het passeren van bord F105?"
        "De normale verkeersregels zijn van toepassing; de beperkingen van de voetgangerszone vervallen"
        "De voetgangerszoneregels blijven nog 100 meter na F105 gelden"
        "Na F105 geldt een snelheidslimiet van 20 km/u"
        "F105 markeert het einde van de voetgangerszone. Vanaf dat punt gelden de gewone verkeersregels van kracht voor de betreffende weg."
        "Which traffic rules apply after passing sign F105?"
        "Normal traffic rules apply; the restrictions of the pedestrian zone cease"
        "Pedestrian zone rules continue for another 100 metres after F105"
        "A speed limit of 20 km/h applies after F105"
        "F105 marks the end of the pedestrian zone. From that point the normal traffic rules in force for the relevant road apply."
        "Quelles regles de circulation s appliquent apres avoir passe le panneau F105 ?"
        "Les regles normales de circulation s appliquent; les restrictions de la zone pietonne cessent"
        "Les regles de la zone pietonne continuent encore 100 metres apres F105"
        "Une limite de vitesse de 20 km/h s applique apres F105"
        "F105 marque la fin de la zone pietonne. A partir de ce point les regles de circulation normales en vigueur pour la route concernee s appliquent."
        "ما قواعد المرور السارية بعد تجاوز لافتة F105؟"
        "تسري قواعد المرور العادية؛ وتنتهي قيود منطقة المشاة"
        "تستمر قواعد منطقة المشاة 100 متر أخرى بعد F105"
        "يسري حد السرعة 20 كم/ساعة بعد F105"
        "تضع F105 علامة على نهاية منطقة المشاة. من تلك النقطة تسري قواعد المرور العادية المعمول بها على الطريق المعني.")

    (q "F105_Q07" "IS_IT_ALLOWED" "HARD" $true
        "Mogen alle voertuigen zonder beperking rijden na het passeren van F105?"
        "Ja de beperkingen van de voetgangerszone gelden niet meer; normale verkeersregels zijn van toepassing"
        "Nee motorvoertuigen blijven beperkt tot laad- en losverkeer ook na F105"
        $null
        "F105 beëindigt uitdrukkelijk de voetgangerszone. Daarna gelden gewone verkeersregels; er is geen reden om voertuigen verder te beperken op basis van de voetgangerszone."
        "May all vehicles drive without restriction after passing F105?"
        "Yes the restrictions of the pedestrian zone no longer apply; normal traffic rules are in force"
        "No motor vehicles remain restricted to loading and unloading traffic even after F105"
        $null
        "F105 explicitly ends the pedestrian zone. After that normal traffic rules apply; there is no reason to continue restricting vehicles based on the pedestrian zone."
        "Tous les vehicules peuvent-ils circuler sans restriction apres avoir passe F105 ?"
        "Oui les restrictions de la zone pietonne ne s appliquent plus; les regles normales de circulation sont en vigueur"
        "Non les vehicules a moteur restent limites au trafic de chargement et de dechargement meme apres F105"
        $null
        "F105 termine explicitement la zone pietonne. Apres cela les regles normales de circulation s appliquent; il n y a aucune raison de continuer a restreindre les vehicules en raison de la zone pietonne."
        "هل يستطيع جميع المركبات السير بدون قيود بعد تجاوز F105؟"
        "نعم لم تعد قيود منطقة المشاة سارية؛ وتسري قواعد المرور العادية"
        "لا تبقى المركبات الآلية مقيدة بحركة التحميل والتفريغ حتى بعد F105"
        $null
        "تنهي F105 صراحةً منطقة المشاة. بعد ذلك تسري قواعد المرور العادية؛ ولا يوجد سبب للاستمرار في تقييد المركبات بناءً على منطقة المشاة.")

    (q "F105_Q08" "IS_IT_ALLOWED" "HARD" $true
        "Is het noodzakelijk om F105 te plaatsen aan elke uitgang van een voetgangerszone?"
        "Ja F105 wordt geplaatst aan elke uitgang zodat alle weggebruikers weten dat de voetgangerszoneregels eindigen"
        "Nee één bord bij de hoofduitgang volstaat voor de volledige zone"
        $null
        "Net zoals bij andere zones (woonerf LEZ) wordt het eindbord F105 aan elke uitgang van de zone geplaatst. Zo weet elke bestuurder die de zone verlaat dat de aparte regels niet meer gelden."
        "Is it necessary to place F105 at every exit of a pedestrian zone?"
        "Yes F105 is placed at every exit so that all road users know the pedestrian zone rules end"
        "No one sign at the main exit is sufficient for the entire zone"
        $null
        "As with other zones (residential area LEZ) the end sign F105 is placed at every exit of the zone. This way every driver leaving the zone knows the special rules no longer apply."
        "Est-il necessaire de placer F105 a chaque sortie d une zone pietonne ?"
        "Oui F105 est place a chaque sortie pour que tous les usagers sachent que les regles de la zone pietonne se terminent"
        "Non un seul panneau a la sortie principale est suffisant pour toute la zone"
        $null
        "Comme pour les autres zones (zone residentielle ZBE) le panneau de fin F105 est place a chaque sortie de la zone. Ainsi chaque conducteur quittant la zone sait que les regles speciales ne s appliquent plus."
        "هل من الضروري وضع F105 عند كل مخرج من منطقة المشاة؟"
        "نعم يُوضع F105 عند كل مخرج حتى يعرف جميع مستخدمي الطريق أن قواعد منطقة المشاة تنتهي"
        "لا لافتة واحدة عند المخرج الرئيسي كافية للمنطقة بأكملها"
        $null
        "كما هو الحال مع المناطق الأخرى (الحي السكني ومنطقة الانبعاثات المنخفضة) يُوضع لافتة النهاية F105 عند كل مخرج من المنطقة. بهذه الطريقة يعرف كل سائق مغادر المنطقة أن القواعد الخاصة لم تعد سارية.")
)

$f105 = Load-Questions "F105"
Save-Questions "F105" ($f105 + $f105Extra)

# ============================================================
# F111: Fietsstraat
# ============================================================
$f111Extra = @(
    (q "F111_Q06" "WHAT_MUST_YOU_DO" "MEDIUM" $false
        "Wat is de maximumsnelheid voor motorvoertuigen in een fietsstraat (F111)?"
        "30 km/u; motorvoertuigen mogen niet sneller rijden dan 30 km/u in een fietsstraat"
        "50 km/u; de normale bebouwde-kom-snelheid geldt"
        "20 km/u gelijk aan een woonerf"
        "In een fietsstraat bedraagt de maximumsnelheid 30 km/u voor motorvoertuigen. Dit is lager dan de normale bebouwde-kom-snelheid om fietsers te beschermen die de volledige rijbaan mogen gebruiken."
        "What is the maximum speed for motor vehicles in a bicycle street (F111)?"
        "30 km/h; motor vehicles may not drive faster than 30 km/h in a bicycle street"
        "50 km/h; the normal built-up area speed applies"
        "20 km/h equal to a residential area"
        "In a bicycle street the maximum speed is 30 km/h for motor vehicles. This is lower than the normal built-up area speed to protect cyclists who may use the full carriageway."
        "Quelle est la vitesse maximale pour les vehicules a moteur dans une rue cyclable (F111) ?"
        "30 km/h; les vehicules a moteur ne peuvent pas rouler plus vite que 30 km/h dans une rue cyclable"
        "50 km/h; la vitesse normale en agglomeration s applique"
        "20 km/h identique a une zone residentielle"
        "Dans une rue cyclable la vitesse maximale est de 30 km/h pour les vehicules a moteur. C est inferieur a la vitesse normale en agglomeration pour proteger les cyclistes qui peuvent utiliser toute la chaussee."
        "ما الحد الأقصى للسرعة للمركبات الآلية في شارع الدراجات (F111)؟"
        "30 كم/ساعة؛ لا يجوز للمركبات الآلية السير أسرع من 30 كم/ساعة في شارع الدراجات"
        "50 كم/ساعة؛ تسري سرعة المناطق المبنية العادية"
        "20 كم/ساعة مثل الحي السكني"
        "في شارع الدراجات يبلغ الحد الأقصى للسرعة 30 كم/ساعة للمركبات الآلية. وهذا أقل من سرعة المناطق المبنية العادية لحماية الدراجين الذين يجوز لهم استخدام كامل الطريق.")

    (q "F111_Q07" "IS_IT_ALLOWED" "HARD" $true
        "Mag een automobilist een fietser inhalen in een fietsstraat (F111)?"
        "Nee motorvoertuigen mogen fietsers niet inhalen in een fietsstraat; fietsers hebben het recht de volledige rijbaan te gebruiken"
        "Ja inhalen is altijd toegestaan als er voldoende ruimte is"
        $null
        "In een fietsstraat (aangeduid met F111) hebben fietsers absolute voorrang en mogen de volledige rijbaan gebruiken. Motorvoertuigen mogen fietsers NIET inhalen. Als de weg te smal is om veilig langs een fietser te rijden moet je wachten."
        "May a motorist overtake a cyclist in a bicycle street (F111)?"
        "No motor vehicles may not overtake cyclists in a bicycle street; cyclists have the right to use the full carriageway"
        "Yes overtaking is always permitted if there is enough space"
        $null
        "In a bicycle street (indicated by F111) cyclists have absolute priority and may use the full carriageway. Motor vehicles may NOT overtake cyclists. If the road is too narrow to pass a cyclist safely you must wait."
        "Un automobiliste peut-il depasser un cycliste dans une rue cyclable (F111) ?"
        "Non les vehicules a moteur ne peuvent pas depasser les cyclistes dans une rue cyclable; les cyclistes ont le droit d utiliser toute la chaussee"
        "Oui le depassement est toujours autorise s il y a suffisamment d espace"
        $null
        "Dans une rue cyclable (indiquee par F111) les cyclistes ont une priorite absolue et peuvent utiliser toute la chaussee. Les vehicules a moteur ne peuvent PAS depasser les cyclistes. Si la route est trop etroite pour passer un cycliste en toute securite vous devez attendre."
        "هل يجوز للسائق تجاوز الدراج في شارع الدراجات (F111)؟"
        "لا لا يجوز للمركبات الآلية تجاوز الدراجين في شارع الدراجات؛ للدراجين الحق في استخدام كامل الطريق"
        "نعم التجاوز مسموح به دائماً إذا كانت المساحة كافية"
        $null
        "في شارع الدراجات (المشار إليه بـF111) يتمتع الدراجون بالأولوية المطلقة ويجوز لهم استخدام كامل الطريق. لا يجوز للمركبات الآلية تجاوز الدراجين. إذا كان الطريق ضيقاً جداً للمرور بأمان بجانب دراج يجب الانتظار.")

    (q "F111_Q08" "IS_IT_ALLOWED" "HARD" $true
        "Mogen fietsers naast elkaar rijden in een fietsstraat (F111)?"
        "Ja in een fietsstraat mogen fietsers twee aan twee naast elkaar rijden"
        "Nee fietsers moeten altijd achter elkaar rijden in een fietsstraat"
        $null
        "In een fietsstraat mag je als fietser naast een andere fietser rijden (twee aan twee). Dit is een van de kenmerken van een fietsstraat: fietsers mogen de volledige rijbanen benutten ook om naast elkaar te rijden."
        "May cyclists ride side by side in a bicycle street (F111)?"
        "Yes in a bicycle street cyclists may ride two abreast"
        "No cyclists must always ride in single file in a bicycle street"
        $null
        "In a bicycle street you may as a cyclist ride next to another cyclist (two abreast). This is one of the characteristics of a bicycle street: cyclists may use the full carriageway including to ride side by side."
        "Les cyclistes peuvent-ils rouler cote a cote dans une rue cyclable (F111) ?"
        "Oui dans une rue cyclable les cyclistes peuvent rouler deux de front"
        "Non les cyclistes doivent toujours rouler en file indienne dans une rue cyclable"
        $null
        "Dans une rue cyclable vous pouvez en tant que cycliste rouler a cote d un autre cycliste (deux de front). C est l une des caracteristiques d une rue cyclable: les cyclistes peuvent utiliser toute la chaussee y compris pour rouler cote a cote."
        "هل يجوز للدراجين السير جنباً إلى جنب في شارع الدراجات (F111)؟"
        "نعم يجوز للدراجين في شارع الدراجات السير اثنين جنباً إلى جنب"
        "لا يجب على الدراجين دائماً السير في صف واحد في شارع الدراجات"
        $null
        "في شارع الدراجات يمكنك كدراج السير بجانب دراج آخر (اثنين جنباً إلى جنب). هذه إحدى خصائص شارع الدراجات: يجوز للدراجين استخدام كامل الطريق بما في ذلك السير جنباً إلى جنب.")
)

$f111 = Load-Questions "F111"
Save-Questions "F111" ($f111 + $f111Extra)

# ============================================================
# F113: Einde fietsstraat
# ============================================================
$f113Extra = @(
    (q "F113_Q06" "WHAT_MUST_YOU_DO" "MEDIUM" $false
        "Mag een automobilist na bord F113 een fietser inhalen?"
        "Ja na F113 gelden normale verkeersregels en mag men fietsers inhalen als het verkeersveilig is"
        "Nee het inhalverbod voor fietsers blijft 200 meter voorbij F113 van kracht"
        "Enkel na 50 meter na F113 is inhalen toegestaan"
        "F113 markeert het einde van de fietsstraat. Voorbij dit bord zijn de bijzondere regels van de fietsstraat niet meer van toepassing. Inhalen van fietsers is weer toegestaan mits veilig."
        "May a motorist overtake a cyclist after sign F113?"
        "Yes after F113 normal traffic rules apply and overtaking cyclists is permitted when traffic-safe"
        "No the overtaking prohibition for cyclists remains in force 200 metres beyond F113"
        "Overtaking is only permitted 50 metres after F113"
        "F113 marks the end of the bicycle street. Beyond this sign the special rules of the bicycle street no longer apply. Overtaking cyclists is permitted again provided it is safe."
        "Un automobiliste peut-il depasser un cycliste apres le panneau F113 ?"
        "Oui apres F113 les regles normales de circulation s appliquent et il est permis de depasser les cyclistes en toute securite"
        "Non l interdiction de depasser les cyclistes reste en vigueur 200 metres apres F113"
        "Le depassement n est autorise qu a 50 metres apres F113"
        "F113 marque la fin de la rue cyclable. Au-dela de ce panneau les regles speciales de la rue cyclable ne s appliquent plus. Il est a nouveau permis de depasser les cyclistes si c est securise."
        "هل يجوز للسائق تجاوز دراج بعد لافتة F113؟"
        "نعم بعد F113 تسري قواعد المرور العادية ويُسمح بتجاوز الدراجين عند الأمان المروري"
        "لا يبقى حظر تجاوز الدراجين سارياً 200 متر بعد F113"
        "يُسمح بالتجاوز فقط بعد 50 متراً من F113"
        "تضع F113 علامة على نهاية شارع الدراجات. بعد هذه اللافتة لم تعد القواعد الخاصة بشارع الدراجات سارية. يُسمح مجدداً بتجاوز الدراجين إذا كان آمناً.")

    (q "F113_Q07" "IS_IT_ALLOWED" "HARD" $true
        "Geldt de 30 km/u limiet van de fietsstraat nog na het passeren van F113?"
        "Nee de 30 km/u limiet van de fietsstraat vervalt na F113; de normale snelheidslimiet van de weg is van toepassing"
        "Ja de 30 km/u limiet blijft gelden totdat een nieuw snelheidsbord een andere limiet aangeeft"
        $null
        "De snelheidslimiet van 30 km/u in een fietsstraat is een specifieke regelgeving voor de fietsstraat. Zodra je F113 passeert wordt de normale snelheidslimiet voor die weg van kracht tenzij een snelheidsbord iets anders aangeeft."
        "Does the 30 km/h limit of the bicycle street still apply after passing F113?"
        "No the 30 km/h limit of the bicycle street ceases after F113; the normal speed limit of the road applies"
        "Yes the 30 km/h limit continues to apply until a new speed sign indicates another limit"
        $null
        "The 30 km/h speed limit in a bicycle street is a specific regulation for the bicycle street. Once you pass F113 the normal speed limit for that road comes into force unless a speed sign indicates otherwise."
        "La limite de 30 km/h de la rue cyclable s applique-t-elle encore apres avoir passe F113 ?"
        "Non la limite de 30 km/h de la rue cyclable cesse apres F113; la limite de vitesse normale de la route s applique"
        "Oui la limite de 30 km/h continue de s appliquer jusqu a ce qu un nouveau panneau de vitesse indique une autre limite"
        $null
        "La limite de vitesse de 30 km/h dans une rue cyclable est une reglementation specifique a la rue cyclable. Une fois que vous passez F113 la limite de vitesse normale de cette route entre en vigueur sauf si un panneau de vitesse indique autre chose."
        "هل يسري حد السرعة 30 كم/ساعة لشارع الدراجات بعد تجاوز F113؟"
        "لا ينتهي حد سرعة 30 كم/ساعة لشارع الدراجات بعد F113؛ ويسري حد السرعة العادي للطريق"
        "نعم يستمر حد السرعة 30 كم/ساعة حتى تشير لافتة سرعة جديدة إلى حد مختلف"
        $null
        "حد السرعة 30 كم/ساعة في شارع الدراجات تنظيم محدد لشارع الدراجات. بمجرد تجاوز F113 يسري حد السرعة العادي لذلك الطريق ما لم تشر لافتة سرعة إلى خلاف ذلك.")

    (q "F113_Q08" "IS_IT_ALLOWED" "HARD" $true
        "Moet F113 aan elke uitgang van een fietsstraat geplaatst worden?"
        "Ja F113 staat aan elke uitgang van de fietsstraat zodat bestuurders weten waar de speciale fietsstraatregels eindigen"
        "Nee één F113-bord aan de hoofduitgang volstaat"
        $null
        "Zoals bij alle zones wordt het eindbord (F113) aan elke uitgang van de fietsstraat geplaatst. Dit zorgt ervoor dat bestuurders die de zone via een zijstraat verlaten ook weten dat de fietsstraatregels niet meer gelden."
        "Must F113 be placed at every exit of a bicycle street?"
        "Yes F113 is placed at every exit of the bicycle street so drivers know where the special bicycle street rules end"
        "No one F113 sign at the main exit is sufficient"
        $null
        "As with all zones the end sign (F113) is placed at every exit of the bicycle street. This ensures that drivers leaving the zone via a side street also know that the bicycle street rules no longer apply."
        "Doit-on placer F113 a chaque sortie d une rue cyclable ?"
        "Oui F113 est place a chaque sortie de la rue cyclable pour que les conducteurs sachent ou les regles speciales de la rue cyclable prennent fin"
        "Non un seul panneau F113 a la sortie principale suffit"
        $null
        "Comme pour toutes les zones le panneau de fin (F113) est place a chaque sortie de la rue cyclable. Cela garantit que les conducteurs quittant la zone par une rue laterale savent egalement que les regles de la rue cyclable ne s appliquent plus."
        "هل يجب وضع F113 عند كل مخرج من شارع الدراجات؟"
        "نعم يُوضع F113 عند كل مخرج من شارع الدراجات حتى يعرف السائقون أين تنتهي قواعد شارع الدراجات الخاصة"
        "لا لافتة F113 واحدة عند المخرج الرئيسي كافية"
        $null
        "كما هو الحال مع جميع المناطق يُوضع لافتة النهاية (F113) عند كل مخرج من شارع الدراجات. هذا يضمن أن السائقين المغادرين المنطقة عبر شارع جانبي يعلمون أيضاً أن قواعد شارع الدراجات لم تعد سارية.")
)

$f113 = Load-Questions "F113"
Save-Questions "F113" ($f113 + $f113Extra)

Write-Host "`nPart 2 done: F101c F103 F105 F111 F113 extended to 8 questions`n"
