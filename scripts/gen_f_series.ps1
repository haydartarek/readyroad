# Generate questions.json for all 43 F-series LEGACY signs
# Uses PowerShell ConvertTo-Json to produce valid JSON files

$base = "C:\Users\haydar\Desktop\end_project\readyroad\src\main\resources\data\signs_import"

function New-Question {
    param($id, $type, $diff, $crit,
        $nlQ, $nlC, $nlE,
        $enQ, $enC, $enE,
        $frQ, $frC, $frE,
        $arQ, $arC, $arE)
    $makeLang = {
        param($q, $choices, $exp)
        @{ question = $q; choices = ($choices | ForEach-Object { @{ text = $_.text; is_correct = $_.ok } }); explanation = $exp }
    }
    @{
        question_id = $id; type = $type; difficulty = $diff; is_critical = $crit; show_sign = $true
        i18n = @{
            NL = & $makeLang $nlQ $nlC $nlE
            EN = & $makeLang $enQ $enC $enE
            FR = & $makeLang $frQ $frC $frE
            AR = & $makeLang $arQ $arC $arE
        }
    }
}

function c($t, $ok) { @{ text = $t; ok = $ok } }

function Save-Questions($code, $questions) {
    $path = Join-Path $base $code "questions.json"
    $questions | ConvertTo-Json -Depth 20 | Set-Content $path -Encoding UTF8
    $size = (Get-Item $path).Length
    Write-Host "  $code : $($questions.Count) q -> $size B"
}

# ═══════════════════════════════════════════════════════
# F1b — Begin van een bebouwde kom (Start of built-up area)
# ═══════════════════════════════════════════════════════
Save-Questions "F1b" @(
    (New-Question "F1b_Q01" "WHAT_DOES_IT_MEAN" "EASY" $false `
        "Wat betekent bord F1b?" `
    @(c "U rijdt een bebouwde kom binnen: maximumsnelheid 50 km/u en stedelijke verkeersregels gelden" $true; c "U verlaat een bebouwde kom" $false; c "Parkeren verboden in dit gebied" $false) `
        "F1b markeert het begin van een bebouwde kom. Zodra u dit bord passeert, geldt een snelheidslimiet van 50 km/u tenzij een ander bord een lagere limiet aangeeft." `
        "What does sign F1b mean?" `
    @(c "You are entering a built-up area: maximum speed 50 km/h and urban traffic rules apply" $true; c "You are leaving a built-up area" $false; c "Parking forbidden in this area" $false) `
        "F1b marks the start of a built-up area. Once you pass this sign, a 50 km/h speed limit applies unless another sign shows a lower limit." `
        "Que signifie le panneau F1b ?" `
    @(c "Vous entrez dans une agglomeration: vitesse maximale 50 km/h et regles de circulation urbaine s'appliquent" $true; c "Vous quittez une agglomeration" $false; c "Stationnement interdit dans cette zone" $false) `
        "F1b marque le debut d'une agglomeration. Des que vous passez ce panneau, une limite de vitesse de 50 km/h s'applique." `
        "ما معنى لافتة F1b؟" `
    @(c "انت تدخل منطقة عمرانية: السرعة القصوى 50 كم/س وتسري قواعد المرور الحضري" $true; c "انت تغادر منطقة عمرانية" $false; c "الوقوف ممنوع في هذه المنطقة" $false) `
        "F1b تُعلّم بداية المنطقة العمرانية. بمجرد مرورك بهذه اللافتة يسري حد السرعة 50 كم/س ما لم تُشر لافتة اخرى لحد ادنى.")
    (New-Question "F1b_Q02" "WHICH_SIGN" "EASY" $false `
        "Welk bord is het tegengestelde van F1b?" `
    @(c "F3b: einde van een bebouwde kom" $true; c "F4b: einde zone 30" $false; c "F7: einde autosnelweg" $false) `
        "F1b (begin bebouwde kom) en F3b (einde bebouwde kom) vormen een koppel. F3b staat aan de andere kant van de kom." `
        "Which sign is the opposite of F1b?" `
    @(c "F3b: end of built-up area" $true; c "F4b: end of zone 30" $false; c "F7: end of motorway" $false) `
        "F1b (start built-up area) and F3b (end built-up area) form a pair. F3b is placed at the other side of the village." `
        "Quel panneau est l'oppose de F1b ?" `
    @(c "F3b: fin d'une agglomeration" $true; c "F4b: fin de zone 30" $false; c "F7: fin d'autoroute" $false) `
        "F1b (debut agglomeration) et F3b (fin agglomeration) forment un duo. F3b est place de l'autre cote du village." `
        "ما اللافتة المقابلة للافتة F1b؟" `
    @(c "F3b: نهاية المنطقة العمرانية" $true; c "F4b: نهاية منطقة سرعة 30" $false; c "F7: نهاية الطريق السريع" $false) `
        "F1b (بداية المنطقة العمرانية) وF3b (نهايتها) يعملان معاً كزوج.")
    (New-Question "F1b_Q03" "HAZARD_IDENTIFICATION" "EASY" $false `
        "Welk gevaar is specifiek voor bebouwde kommen aangeduid door F1b?" `
    @(c "Voetgangers, fietsers en spelende kinderen naast de rijbaan; meer kansen op conflicten bij lagere snelheden" $true; c "Gevaar van dieren op de rijbaan" $false; c "Gevaar van ijzel op snelwegen" $false) `
        "In bebouwde kommen zijn er kwetsbare weggebruikers zoals voetgangers en kinderen die dicht bij de rijbaan kunnen zijn. Lagere snelheid vermindert het risico." `
        "What hazard is specific to built-up areas indicated by F1b?" `
    @(c "Pedestrians, cyclists and playing children near the road; higher chance of conflicts at lower speeds" $true; c "Risk of animals on motorways" $false; c "Risk of ice on motorways" $false) `
        "In built-up areas there are vulnerable road users such as pedestrians and children who may be close to the road." `
        "Quel danger est propre aux agglomerations signalees par F1b ?" `
    @(c "Pietons, cyclistes et enfants jouant pres de la route; risque eleve de conflits a vitesse reduite" $true; c "Risque d'animaux sur l'autoroute" $false; c "Risque de verglas sur les autoroutes" $false) `
        "Dans les agglomerations il y a des usagers vulnerables comme des pietons et des enfants proches de la route." `
        "ما الخطر الخاص بالمناطق العمرانية الذي تُشير إليه F1b؟" `
    @(c "المشاة وراكبو الدراجات والاطفال الذين يلعبون بالقرب من الطريق" $true; c "خطر الحيوانات على الطرق السريعة" $false; c "خطر الجليد على الطرق السريعة" $false) `
        "في المناطق العمرانية يوجد مستخدمو طريق ضعفاء كالمشاة والاطفال بالقرب من الطريق.")
    (New-Question "F1b_Q04" "WHAT_MUST_YOU_DO" "MEDIUM" $false `
        "U passeert F1b. Welke maximumsnelheid geldt nu onmiddellijk?" `
    @(c "50 km/u tenzij een ander bord een lagere limiet aangeeft" $true; c "70 km/u" $false; c "30 km/u" $false) `
        "De standaard snelheidslimiet binnen een bebouwde kom is 50 km/u. Lokale borden (bv. zone 30) kunnen een lagere limiet opleggen." `
        "You pass F1b. What maximum speed now applies immediately?" `
    @(c "50 km/h unless another sign indicates a lower limit" $true; c "70 km/h" $false; c "30 km/h" $false) `
        "The default speed limit inside a built-up area is 50 km/h. Local signs (e.g. zone 30) may impose a lower limit." `
        "Vous passez F1b. Quelle vitesse maximale s'applique desormais ?" `
    @(c "50 km/h sauf si un autre panneau indique une limite inferieure" $true; c "70 km/h" $false; c "30 km/h" $false) `
        "La limite de vitesse par defaut dans une agglomeration est de 50 km/h. Des panneaux locaux peuvent imposer une limite inferieure." `
        "عبرت لافتة F1b. ما السرعة القصوى التي تسري الان؟" `
    @(c "50 كم/س ما لم تُشر لافتة اخرى لحد ادنى" $true; c "70 كم/س" $false; c "30 كم/س" $false) `
        "الحد الافتراضي للسرعة داخل المنطقة العمرانية هو 50 كم/س. اللافتات المحلية كمنطقة 30 قد تفرض حداً ادنى.")
    (New-Question "F1b_Q05" "WHAT_MUST_YOU_DO" "MEDIUM" $false `
        "Welke extra verkeersregels treden in werking na F1b?" `
    @(c "Parkeer- en stilstandsregels voor bebouwde kommen, inhaalverboden en lagere snelheidslimieten" $true; c "De regels voor autosnelwegen" $false; c "Geen nieuwe regels: alleen de snelheidslimiet verandert" $false) `
        "Binnen een bebouwde kom gelden specifieke regels: lagere snelheid, bijzondere aandacht voor voetgangers, verbod te parkeren op bepaalde plaatsen enz." `
        "Which extra traffic rules come into effect after F1b?" `
    @(c "Parking and stopping rules for built-up areas, overtaking restrictions and lower speed limits" $true; c "The rules for motorways" $false; c "No new rules: only the speed limit changes" $false) `
        "Inside a built-up area specific rules apply: lower speed, extra attention for pedestrians, prohibition to park in certain places, etc." `
        "Quelles regles de circulation supplementaires entrent en vigueur apres F1b ?" `
    @(c "Regles de stationnement et d'arret pour les agglomerations, interdictions de depassement et limites de vitesse reduites" $true; c "Les regles des autoroutes" $false; c "Aucune nouvelle regle: seule la limite de vitesse change" $false) `
        "Dans une agglomeration des regles specifiques s'appliquent: vitesse reduite, attention aux pietons, interdiction de stationner a certains endroits." `
        "ما القواعد الاضافية للمرور التي تسري بعد F1b؟" `
    @(c "قواعد الوقوف والتوقف للمناطق العمرانية وقيود التجاوز ومحددات السرعة المنخفضة" $true; c "قواعد الطرق السريعة" $false; c "لا قواعد جديدة: فقط حد السرعة يتغير" $false) `
        "داخل المنطقة العمرانية تسري قواعد خاصة: سرعة منخفضة واهتمام اضافي بالمشاة وحظر وقوف في اماكن معينة.")
    (New-Question "F1b_Q06" "WHAT_MUST_YOU_DO" "MEDIUM" $false `
        "Moet u snelheid minderen zodra u F1b ziet of pas nadat u het passeert?" `
    @(c "U moet uw snelheid aanpassen voordat u het bord passeert zodat u de limiet niet overschrijdt bij binnenkomst" $true; c "Pas na het passeren van het bord" $false; c "Zodra u het bord ziet op een afstand van 100 m" $false) `
        "U moet uw snelheid aanpassen op het moment dat u de bebouwde kom binnenrijdt. Het bord zelf markeert de grens." `
        "Must you reduce speed as soon as you see F1b or only after you pass it?" `
    @(c "You must adjust your speed before passing the sign so you do not exceed the limit on entry" $true; c "Only after passing the sign" $false; c "As soon as you see the sign at 100 m distance" $false) `
        "You must adjust your speed at the point you enter the built-up area. The sign itself marks the boundary." `
        "Devez-vous reduire votre vitesse des que vous voyez F1b ou seulement apres l'avoir depasse ?" `
    @(c "Vous devez adapter votre vitesse avant de passer le panneau pour ne pas depasser la limite a l'entree" $true; c "Seulement apres avoir depasse le panneau" $false; c "Des que vous voyez le panneau a 100 m" $false) `
        "Vous devez adapter votre vitesse au moment ou vous entrez dans l'agglomeration. Le panneau lui-meme marque la limite." `
        "هل يجب تخفيض السرعة بمجرد رؤية F1b ام فقط بعد تجاوزها؟" `
    @(c "يجب تكييف سرعتك قبل تجاوز اللافتة حتى لا تتجاوز الحد عند الدخول" $true; c "فقط بعد تجاوز اللافتة" $false; c "بمجرد رؤية اللافتة على بعد 100 م" $false) `
        "يجب تكييف سرعتك لحظة دخول المنطقة العمرانية. اللافتة نفسها تُحدّد الحد.")
    (New-Question "F1b_Q07" "IS_IT_ALLOWED" "HARD" $true `
        "Mag u 70 km/u rijden net na F1b als de weg breed is en er weinig verkeer is?" `
    @(c "Neen: de maximumsnelheid van 50 km/u geldt ongeacht de wegbreedte of het verkeersniveau" $true; c "Ja: brede wegen hebben een hogere limiet" $false; c "Ja, tenzij een bord 50 aangeeft" $false) `
        "De snelheidslimiet van 50 km/u in een bebouwde kom is absoluut. Wegbreedte of verkeersdruk veranderen niets aan de wettelijke limiet." `
        "May you drive 70 km/h just after F1b if the road is wide and there is little traffic?" `
    @(c "No: the maximum speed of 50 km/h applies regardless of road width or traffic level" $true; c "Yes: wide roads have a higher limit" $false; c "Yes, unless a sign shows 50" $false) `
        "The 50 km/h speed limit in a built-up area is absolute. Road width or traffic pressure do not change the legal limit." `
        "Pouvez-vous rouler a 70 km/h juste apres F1b si la route est large et qu'il y a peu de trafic ?" `
    @(c "Non: la vitesse maximale de 50 km/h s'applique quelle que soit la largeur de la route ou le niveau de trafic" $true; c "Oui: les routes larges ont une limite plus elevee" $false; c "Oui, sauf si un panneau indique 50" $false) `
        "La limite de vitesse de 50 km/h dans une agglomeration est absolue. La largeur de la route ou la pression du trafic ne changent pas la limite legale." `
        "هل يجوز السير بسرعة 70 كم/س بعد F1b إذا كان الطريق واسعاً والحركة خفيفة؟" `
    @(c "لا: حد السرعة 50 كم/س ينطبق بصرف النظر عن عرض الطريق او مستوى الحركة" $true; c "نعم: الطرق العريضة لها حد اعلى" $false; c "نعم، إلا إذا اشارت لافتة إلى 50" $false) `
        "حد السرعة 50 كم/س في المنطقة العمرانية مطلق. عرض الطريق او كثافة المرور لا يغيّران الحد القانوني.")
    (New-Question "F1b_Q08" "IS_IT_ALLOWED" "HARD" $true `
        "Is het toegestaan in een bebouwde kom voetgangers die oversteken te negeren als u voorrang heeft?" `
    @(c "Neen: binnen een bebouwde kom moet u altijd voetgangers laten oversteken op een zebrapad of bord F49" $true; c "Ja: voorrang geldt altijd voor gemotoriseerd verkeer" $false; c "Ja, tenzij het verkeerslicht rood is" $false) `
        "Binnen een bebouwde kom hebben voetgangers op een zebrapad of oversteekplaats (F49) absolute voorrang. U bent verplicht te stoppen." `
        "Is it allowed in a built-up area to ignore pedestrians crossing if you have right of way?" `
    @(c "No: inside a built-up area you must always let pedestrians cross at a zebra crossing or F49 sign" $true; c "Yes: right of way always applies to motorised traffic" $false; c "Yes, unless the traffic light is red" $false) `
        "Inside a built-up area pedestrians at a zebra crossing or crossing sign (F49) have absolute priority. You are obliged to stop." `
        "Est-il permis dans une agglomeration d'ignorer les pietons qui traversent si vous avez la priorite ?" `
    @(c "Non: dans une agglomeration vous devez toujours laisser traverser les pietons sur un passage cloute ou panneau F49" $true; c "Oui: la priorite s'applique toujours aux vehicules motorises" $false; c "Oui, sauf si le feu est rouge" $false) `
        "Dans une agglomeration les pietons sur un passage cloute (F49) ont la priorite absolue. Vous etes oblige de vous arreter." `
        "هل يُسمح داخل المنطقة العمرانية بتجاهل المشاة العابرين إذا كانت لديك الاولوية؟" `
    @(c "لا: داخل المنطقة العمرانية يجب دائماً السماح للمشاة بالعبور عند ممر المشاة او لافتة F49" $true; c "نعم: الاولوية تنطبق دائماً على المركبات الآلية" $false; c "نعم، إلا إذا كانت اشارة المرور حمراء" $false) `
        "داخل المنطقة العمرانية للمشاة عند ممر العبور (F49) اولوية مطلقة. انت ملزم بالتوقف.")
)

Write-Host "F1b done"
