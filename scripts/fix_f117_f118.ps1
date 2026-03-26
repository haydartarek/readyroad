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

# ======================== F117 - Begin lage emissiezone (REWRITE with correct IDs) ========================
$f117Qs = @(
(Q "F117_Q01" "WHAT_DOES_IT_MEAN" "EASY" $false (I18 `
    "Wat betekent bord F117 'Begin van lage emissiezone'?" `
    (C3 "Je rijdt een lage-emissiezone (LEZ) in waar alleen voertuigen die voldoen aan bepaalde milieunormen zijn toegestaan" "Je rijdt een snelheidszone van 30 km/u in" "Het begin van een voetgangerszone") `
    "F117 geeft het begin van een lage-emissiezone aan. Alleen voertuigen die voldoen aan specifieke emissienormen (Euro-norm) mogen de zone betreden. Voertuigen die niet voldoen riskeren een boete." `
    "What does sign F117 'Start of low emission zone' mean?" `
    (C3 "You are entering a low emission zone (LEZ) where only vehicles meeting certain emission standards are permitted" "You are entering a 30 km/h speed zone" "The start of a pedestrian zone") `
    "F117 indicates the start of a low emission zone. Only vehicles that meet specific emission standards (Euro standard) may enter the zone. Vehicles that do not comply risk a fine." `
    "Que signifie le panneau F117 'Debut de zone a basses emissions' ?" `
    (C3 "Vous entrez dans une zone a basses emissions (ZBE) ou seuls les vehicules respectant certaines normes d emission sont autorises" "Vous entrez dans une zone de vitesse a 30 km/h" "Le debut d une zone pietonne") `
    "F117 indique le debut d une zone a basses emissions. Seuls les vehicules qui respectent des normes d emission specifiques (norme Euro) peuvent entrer dans la zone. Les vehicules non conformes risquent une amende." `
    "ما معنى لافتة F117 'بداية منطقة الانبعاثات المنخفضة'؟" `
    (C3 "أنت تدخل منطقة انبعاثات منخفضة (LEZ) حيث يُسمح فقط للمركبات التي تستوفي معايير الانبعاثات المحددة" "أنت تدخل منطقة سرعة 30 كم/ساعة" "بداية منطقة المشاة") `
    "تشير F117 إلى بداية منطقة الانبعاثات المنخفضة. يجوز فقط للمركبات التي تستوفي معايير الانبعاثات المحددة (معيار يورو) دخول المنطقة. المركبات غير الممتثلة تخاطر بغرامة."))

(Q "F117_Q02" "WHICH_SIGN" "EASY" $false (I18 `
    "Welk bord geeft het begin van een lage-emissiezone aan?" `
    (C3 "F117 begin van lage emissiezone" "F118 einde van lage emissiezone" "F103 begin voetgangerszone") `
    "F117 is het specifieke bord dat het begin van een lage-emissiezone aangeeft. F118 is het bijbehorende eindbord." `
    "Which sign indicates the start of a low emission zone?" `
    (C3 "F117 start of low emission zone" "F118 end of low emission zone" "F103 start of pedestrian zone") `
    "F117 is the specific sign that indicates the start of a low emission zone. F118 is the corresponding end sign." `
    "Quel panneau indique le debut d une zone a basses emissions ?" `
    (C3 "F117 debut de zone a basses emissions" "F118 fin de zone a basses emissions" "F103 debut zone pietonne") `
    "F117 est le panneau specifique qui indique le debut d une zone a basses emissions. F118 est le panneau de fin correspondant." `
    "أي لافتة تشير إلى بداية منطقة الانبعاثات المنخفضة؟" `
    (C3 "F117 بداية منطقة الانبعاثات المنخفضة" "F118 نهاية منطقة الانبعاثات المنخفضة" "F103 بداية منطقة المشاة") `
    "F117 هي اللافتة المحددة التي تشير إلى بداية منطقة الانبعاثات المنخفضة. F118 هي لافتة النهاية المقابلة."))

(Q "F117_Q03" "HAZARD_IDENTIFICATION" "EASY" $false (I18 `
    "Wat is het risico als u een lage-emissiezone (F117) inrijdt met een niet-toegelaten voertuig?" `
    (C3 "U riskeert een automatische boete via camerabewaking" "U mag de zone betreden maar moet dan maximaal 20 km/u rijden" "Er is geen risico als u slechts even door de zone rijdt") `
    "LEZ-zones worden gehandhaafd via automatische nummerplaatherkenning (ANPR-camera's). Als uw voertuig niet aan de normen voldoet wordt de boete automatisch verstuurd naar het geregistreerde adres." `
    "What is the risk if you enter a low emission zone (F117) with a non-permitted vehicle?" `
    (C3 "You risk an automatic fine via camera enforcement" "You may enter the zone but must drive at maximum 20 km/h" "There is no risk if you only drive through briefly") `
    "LEZ zones are enforced via automatic number plate recognition (ANPR cameras). If your vehicle does not meet the standards the fine is automatically sent to the registered address." `
    "Quel est le risque si vous entrez dans une zone a basses emissions (F117) avec un vehicule non autorise ?" `
    (C3 "Vous risquez une amende automatique via surveillance par camera" "Vous pouvez entrer dans la zone mais devez rouler a maximum 20 km/h" "Il n y a aucun risque si vous ne faites que traverser brievement") `
    "Les zones ZBE sont controlees via la reconnaissance automatique des plaques d immatriculation (cameras ANPR). Si votre vehicule ne repond pas aux normes l amende est automatiquement envoyee a l adresse enregistree." `
    "ما الخطر إذا دخلت منطقة انبعاثات منخفضة (F117) بمركبة غير مسموح بها؟" `
    (C3 "تخاطر بغرامة تلقائية عبر مراقبة الكاميرات" "يمكنك الدخول لكن يجب السير بحد أقصى 20 كم/ساعة" "لا يوجد خطر إذا مررت بسرعة") `
    "تُطبَّق مناطق LEZ عبر التعرف التلقائي على لوحات الأرقام (كاميرات ANPR). إذا لم تستوف مركبتك المعايير تُرسَل الغرامة تلقائياً إلى العنوان المسجل."))

(Q "F117_Q04" "WHAT_MUST_YOU_DO" "MEDIUM" $false (I18 `
    "Hoe controleert u of uw voertuig een lage-emissiezone (F117) mag betreden?" `
    (C3 "Controleer de Euro-emissienorm van uw voertuig en raadpleeg het LEZ-register van de betreffende stad" "Stop voor het bord en wacht op inspectie" "U mag altijd de zone in; handhaving geldt enkel voor zware vrachtwagens") `
    "Elk voertuig heeft een Euro-emissienorm (Euro 0 tot Euro 6 of hoger). U kunt via het LEZ-register van de stad of gemeente nagaan of uw voertuig is toegelaten. Doorgaans zijn oudere diesel- en benzinevoertuigen met een lage Euro-norm verboden." `
    "How do you check whether your vehicle may enter a low emission zone (F117)?" `
    (C3 "Check the Euro emission standard of your vehicle and consult the LEZ register of the relevant city" "Stop at the sign and wait for inspection" "You may always enter the zone; enforcement only applies to heavy trucks") `
    "Every vehicle has a Euro emission standard (Euro 0 to Euro 6 or higher). You can check via the LEZ register of the city or municipality whether your vehicle is admitted. Typically older diesel and petrol vehicles with a low Euro standard are prohibited." `
    "Comment verifiez-vous si votre vehicule peut entrer dans une zone a basses emissions (F117) ?" `
    (C3 "Verifiez la norme d emission Euro de votre vehicule et consultez le registre ZBE de la ville concernee" "Arretez-vous devant le panneau et attendez l inspection" "Vous pouvez toujours entrer dans la zone; le controle ne s applique qu aux poids lourds") `
    "Chaque vehicule a une norme d emission Euro (Euro 0 a Euro 6 ou superieure). Vous pouvez verifier via le registre ZBE de la ville ou commune si votre vehicule est admis. En general les anciens vehicules diesel et essence avec une faible norme Euro sont interdits." `
    "كيف تتحقق من أن مركبتك مسموح لها بدخول منطقة الانبعاثات المنخفضة (F117)؟" `
    (C3 "تحقق من معيار الانبعاثات يورو لمركبتك وراجع سجل LEZ للمدينة المعنية" "توقف أمام اللافتة وانتظر الفحص" "يمكنك دائماً الدخول؛ التطبيق يسري فقط على الشاحنات الثقيلة") `
    "لكل مركبة معيار انبعاثات يورو (من يورو 0 إلى يورو 6 أو أعلى). يمكنك التحقق عبر سجل LEZ للمدينة أو البلدية إذا كانت مركبتك مسموحاً بها. عادةً تُحظر المركبات الديزل والبنزين القديمة ذات معيار يورو المنخفض."))

(Q "F117_Q05" "WHAT_MUST_YOU_DO" "MEDIUM" $false (I18 `
    "Welke voertuigen worden doorgaans verboden in een Belgische lage-emissiezone na F117?" `
    (C3 "Oudere diesel- en benzinemotoren die niet voldoen aan de minimaal vereiste Euro-norm" "Alle voertuigen met een brandstofmotor" "Enkel vrachtwagens zwaarder dan 3,5 ton") `
    "Belgische LEZ's hanteren doorgaans een minimale Euro-norm als toelatingscriterium. Voertuigen die niet aan deze norm voldoen zoals oudere diesels (pre-Euro 4 of 5) of benzines (pre-Euro 1 of 2) zijn verboden." `
    "Which vehicles are typically prohibited in a Belgian low emission zone after F117?" `
    (C3 "Older diesel and petrol engines that do not meet the minimum required Euro standard" "All vehicles with a combustion engine" "Only trucks heavier than 3.5 tonnes") `
    "Belgian LEZ zones typically use a minimum Euro standard as admission criterion. Vehicles that do not meet this standard such as older diesels (pre-Euro 4 or 5) or petrol (pre-Euro 1 or 2) are prohibited." `
    "Quels vehicules sont generalement interdits dans une zone a basses emissions belge apres F117 ?" `
    (C3 "Les anciens moteurs diesel et essence qui ne respectent pas la norme Euro minimale requise" "Tous les vehicules a moteur a combustion" "Uniquement les camions de plus de 3,5 tonnes") `
    "Les ZBE belges utilisent generalement une norme Euro minimale comme critere d admission. Les vehicules ne respectant pas cette norme comme les anciens diesels (avant Euro 4 ou 5) ou essence (avant Euro 1 ou 2) sont interdits." `
    "ما المركبات التي تُحظر عادةً في منطقة الانبعاثات المنخفضة البلجيكية بعد F117؟" `
    (C3 "المحركات الديزل والبنزين القديمة التي لا تستوفي الحد الأدنى من معيار يورو" "جميع المركبات ذات المحرك الاحتراقي" "فقط الشاحنات الأثقل من 3.5 طن") `
    "تستخدم مناطق LEZ البلجيكية عادةً حداً أدنى لمعيار يورو كمعيار للقبول. المركبات التي لا تستوفي هذا المعيار مثل الديزل القديمة (ما قبل يورو 4 أو 5) أو البنزين (ما قبل يورو 1 أو 2) محظورة."))

(Q "F117_Q06" "WHAT_MUST_YOU_DO" "MEDIUM" $false (I18 `
    "Moet u stoppen bij bord F117 om uw voertuig te laten controleren?" `
    (C3 "Nee de zone wordt automatisch gecontroleerd via ANPR-camera's; u hoeft niet te stoppen" "Ja u moet stoppen en uw kentekenbewijs tonen aan de handhaver" "Ja u moet een toegangspasje kopen bij het bord") `
    "In een LEZ worden voertuigen automatisch gecontroleerd via nummerplaatherkenning zodra ze de zone binnenrijden. Er is geen manuele controle aan het bord. U hoeft dus niet te stoppen." `
    "Must you stop at sign F117 to have your vehicle checked?" `
    (C3 "No the zone is automatically monitored via ANPR cameras; you do not need to stop" "Yes you must stop and show your vehicle registration to an enforcement officer" "Yes you must buy an access pass at the sign") `
    "In a LEZ vehicles are automatically checked via number plate recognition as they enter the zone. There is no manual check at the sign. You do not need to stop." `
    "Devez-vous vous arreter au panneau F117 pour faire contrôler votre vehicule ?" `
    (C3 "Non la zone est automatiquement controlee via cameras ANPR; vous n avez pas besoin de vous arreter" "Oui vous devez vous arreter et montrer votre carte grise a un agent" "Oui vous devez acheter un laissez-passer au panneau") `
    "Dans une ZBE les vehicules sont automatiquement controles via la reconnaissance des plaques d immatriculation des leur entree dans la zone. Il n y a pas de controle manuel au panneau. Vous n avez pas besoin de vous arreter." `
    "هل يجب عليك التوقف عند لافتة F117 لفحص مركبتك؟" `
    (C3 "لا تُراقَب المنطقة تلقائياً عبر كاميرات ANPR؛ لست بحاجة للتوقف" "نعم يجب التوقف وإبراز بطاقة تسجيل مركبتك للمُنفِّذ" "نعم يجب شراء تصريح دخول عند اللافتة") `
    "في منطقة LEZ تُفحص المركبات تلقائياً عبر التعرف على لوحات الأرقام عند دخولها المنطقة. لا يوجد فحص يدوي عند اللافتة. لست بحاجة للتوقف."))

(Q "F117_Q07" "IS_IT_ALLOWED" "HARD" $true (I18 `
    "Mag een bewoner die in de lage-emissiezone woont een niet-conform voertuig gebruiken?" `
    (C2 "Nee bewoners moeten via het LEZ-systeem een ontheffing aanvragen; zonder ontheffing zijn ze ook in overtreding" "Ja bewoners zijn vrijgesteld van de LEZ-regels") `
    "Bewoners zijn niet automatisch vrijgesteld. Ze kunnen echter via het stedelijk of gemeentelijk LEZ-systeem een tijdelijke ontheffing aanvragen. Zonder geldige ontheffing zijn ook bewoners in overtreding." `
    "May a resident who lives in the low emission zone use a non-compliant vehicle?" `
    (C2 "No residents must apply for an exemption via the LEZ system; without an exemption they are also in violation" "Yes residents are automatically exempt from LEZ rules") `
    "Residents are not automatically exempt. However they can apply for a temporary exemption via the city or municipality LEZ system. Without a valid exemption residents are also in violation." `
    "Un resident qui habite dans la zone a basses emissions peut-il utiliser un vehicule non conforme ?" `
    (C2 "Non les residents doivent demander une derogation via le systeme ZBE; sans derogation ils sont egalement en infraction" "Oui les residents sont exemptes des regles ZBE") `
    "Les residents ne sont pas automatiquement exemptes. Ils peuvent cependant demander une derogation temporaire via le systeme ZBE de la ville ou commune. Sans derogation valide les residents sont egalement en infraction." `
    "هل يجوز لساكن يعيش في منطقة الانبعاثات المنخفضة استخدام مركبة غير ممتثلة؟" `
    (C2 "لا يجب على السكان التقدم بطلب إعفاء عبر نظام LEZ؛ بدون إعفاء يكونون أيضاً مخالفين" "نعم السكان معفيون تلقائياً من قواعد LEZ") `
    "السكان ليسوا معفيين تلقائياً. يمكنهم مع ذلك التقدم بطلب إعفاء مؤقت عبر نظام LEZ للمدينة أو البلدية. بدون إعفاء صالح يكون السكان أيضاً مخالفين."))

(Q "F117_Q08" "IS_IT_ALLOWED" "HARD" $true (I18 `
    "Is een Euro 3 diesel toegestaan in een Belgische lage-emissiezone aangeduid met F117?" `
    (C2 "Nee in de meeste Belgische LEZ's is een Euro 3 diesel niet toegestaan; u moet het LEZ-register raadplegen" "Ja Euro 3 diesel voldoet aan de minimumnorm voor alle Belgische LEZ's") `
    "De toelaatbaarheid hangt af van de specifieke minimumnorm per LEZ. In de meeste grote Belgische steden is de minimumnorm voor diesels Euro 4 of hoger. Een Euro 3 diesel is dan niet toegestaan. Raadpleeg altijd het LEZ-register van de betrokken gemeente." `
    "Is a Euro 3 diesel permitted in a Belgian low emission zone indicated by F117?" `
    (C2 "No in most Belgian LEZs a Euro 3 diesel is not permitted; you must consult the LEZ register" "Yes Euro 3 diesel meets the minimum standard for all Belgian LEZs") `
    "Admissibility depends on the specific minimum standard per LEZ. In most major Belgian cities the minimum standard for diesels is Euro 4 or higher. A Euro 3 diesel is then not permitted. Always consult the LEZ register of the relevant municipality." `
    "Un diesel Euro 3 est-il autorise dans une zone a basses emissions belge indiquee par F117 ?" `
    (C2 "Non dans la plupart des ZBE belges un diesel Euro 3 n est pas autorise; vous devez consulter le registre ZBE" "Oui le diesel Euro 3 repond a la norme minimale de toutes les ZBE belges") `
    "L admissibilite depend de la norme minimale specifique par ZBE. Dans la plupart des grandes villes belges la norme minimale pour les diesels est Euro 4 ou superieure. Un diesel Euro 3 n est alors pas autorise. Consultez toujours le registre ZBE de la commune concernee." `
    "هل تُسمح سيارة ديزل يورو 3 في منطقة الانبعاثات المنخفضة البلجيكية المشار إليها بـF117؟" `
    (C2 "لا في معظم مناطق LEZ البلجيكية ديزل يورو 3 غير مسموح بها؛ يجب مراجعة سجل LEZ" "نعم ديزل يورو 3 تستوفي الحد الأدنى لجميع مناطق LEZ البلجيكية") `
    "تعتمد المقبولية على الحد الأدنى المحدد لكل منطقة LEZ. في معظم المدن البلجيكية الكبرى الحد الأدنى للديزل هو يورو 4 أو أعلى. لذلك ديزل يورو 3 غير مسموح بها. راجع دائماً سجل LEZ للبلدية المعنية."))
)
Save-Q "F117" $f117Qs

# ======================== F118 - Einde lage emissiezone (REWRITE with correct IDs) ========================
$f118Qs = @(
(Q "F118_Q01" "WHAT_DOES_IT_MEAN" "EASY" $false (I18 `
    "Wat betekent bord F118 'Einde van lage emissiezone'?" `
    (C3 "De lage-emissiezone eindigt; de emissiebeperkingen gelden niet meer en de normale verkeersregels zijn van kracht" "Je rijdt een nieuwe lage-emissiezone in" "Het einde van een autoweg") `
    "F118 geeft aan dat u de lage-emissiezone verlaat. Voorbij dit bord gelden de gewone verkeersregels en zijn er geen emissiebeperkingen meer van kracht." `
    "What does sign F118 'End of low emission zone' mean?" `
    (C3 "The low emission zone ends; emission restrictions no longer apply and normal traffic rules are in force" "You are entering a new low emission zone" "The end of an expressway") `
    "F118 indicates that you are leaving the low emission zone. Beyond this sign normal traffic rules apply and there are no more emission restrictions in force." `
    "Que signifie le panneau F118 'Fin de zone a basses emissions' ?" `
    (C3 "La zone a basses emissions se termine; les restrictions d emission ne s appliquent plus et les regles normales sont en vigueur" "Vous entrez dans une nouvelle zone a basses emissions" "La fin d une route express") `
    "F118 indique que vous quittez la zone a basses emissions. Au-dela de ce panneau les regles normales de circulation s appliquent et il n y a plus de restrictions d emission en vigueur." `
    "ما معنى لافتة F118 'نهاية منطقة الانبعاثات المنخفضة'؟" `
    (C3 "تنتهي منطقة الانبعاثات المنخفضة؛ قيود الانبعاثات لم تعد سارية وتسري قواعد المرور العادية" "أنت تدخل منطقة انبعاثات منخفضة جديدة" "نهاية طريق سريع") `
    "تشير F118 إلى أنك تغادر منطقة الانبعاثات المنخفضة. بعد هذه اللافتة تسري قواعد المرور العادية ولا توجد قيود انبعاثات سارية."))

(Q "F118_Q02" "WHICH_SIGN" "EASY" $false (I18 `
    "Welk bord geeft het einde van een lage-emissiezone aan?" `
    (C3 "F118 einde van lage emissiezone" "F117 begin van lage emissiezone" "F105 einde voetgangerszone") `
    "F118 is het specifieke eindbord voor de lage-emissiezone. Het bijbehorende beginbord is F117." `
    "Which sign indicates the end of a low emission zone?" `
    (C3 "F118 end of low emission zone" "F117 start of low emission zone" "F105 end of pedestrian zone") `
    "F118 is the specific end sign for the low emission zone. The corresponding start sign is F117." `
    "Quel panneau indique la fin d une zone a basses emissions ?" `
    (C3 "F118 fin de zone a basses emissions" "F117 debut de zone a basses emissions" "F105 fin zone pietonne") `
    "F118 est le panneau de fin specifique pour la zone a basses emissions. Le panneau de debut correspondant est F117." `
    "أي لافتة تشير إلى نهاية منطقة الانبعاثات المنخفضة؟" `
    (C3 "F118 نهاية منطقة الانبعاثات المنخفضة" "F117 بداية منطقة الانبعاثات المنخفضة" "F105 نهاية منطقة المشاة") `
    "F118 هي لافتة النهاية المحددة لمنطقة الانبعاثات المنخفضة. لافتة البداية المقابلة هي F117."))

(Q "F118_Q03" "HAZARD_IDENTIFICATION" "EASY" $false (I18 `
    "Wat verandert er voor verkeer na het passeren van bord F118?" `
    (C3 "Er zijn geen emissiebeperkingen meer; voertuigen die niet aan de LEZ-norm voldeden mogen nu rijden" "Voertuigen met lage Euro-norm blijven verboden ook na F118" "Na F118 geldt een aangepaste snelheidslimiet") `
    "Na F118 zijn de emissiebeperkingen opgeheven. Voertuigen die niet waren toegelaten in de LEZ mogen na dit bord weer normaal rijden. De normale verkeersregels voor die weg zijn van toepassing." `
    "What changes for traffic after passing sign F118?" `
    (C3 "There are no more emission restrictions; vehicles that did not meet the LEZ standard may now drive" "Vehicles with low Euro standard remain prohibited even after F118" "A modified speed limit applies after F118") `
    "After F118 the emission restrictions are lifted. Vehicles that were not admitted to the LEZ may drive normally again after this sign. The normal traffic rules for that road apply." `
    "Qu est-ce qui change pour la circulation apres avoir passe le panneau F118 ?" `
    (C3 "Il n y a plus de restrictions d emission; les vehicules qui ne respectaient pas la norme ZBE peuvent maintenant circuler" "Les vehicules avec une faible norme Euro restent interdits meme apres F118" "Une limite de vitesse modifiee s applique apres F118") `
    "Apres F118 les restrictions d emission sont levees. Les vehicules qui n etaient pas admis dans la ZBE peuvent de nouveau circuler normalement apres ce panneau. Les regles normales de circulation pour cette route s appliquent." `
    "ما الذي يتغير للمرور بعد تجاوز لافتة F118؟" `
    (C3 "لا توجد قيود انبعاثات بعد الآن؛ يمكن للمركبات التي لم تستوف معيار LEZ السير الآن" "تبقى المركبات ذات معيار يورو المنخفض محظورة حتى بعد F118" "يسري حد سرعة معدَّل بعد F118") `
    "بعد F118 تُرفع قيود الانبعاثات. يمكن للمركبات التي لم تُقبل في منطقة LEZ السير بشكل طبيعي مجدداً بعد هذه اللافتة. تسري قواعد المرور العادية للطريق."))

(Q "F118_Q04" "WHAT_MUST_YOU_DO" "MEDIUM" $false (I18 `
    "Waar wordt bord F118 geplaatst in verhouding tot de lage-emissiezone?" `
    (C3 "Aan elke uitgang van de lage-emissiezone zodat vertrekkend verkeer weet dat de zone eindigt" "Enkel aan de hoofduitgang van de zone" "Aan het begin van de zone naast F117") `
    "Zoals bij alle zones wordt het eindbord (F118) aan elke uitgang geplaatst. Dit geldt voor alle straten en wegen die de zone verlaten. Zo weet iedereen die de zone verlaat dat de LEZ-regels niet meer van toepassing zijn." `
    "Where is sign F118 placed in relation to the low emission zone?" `
    (C3 "At every exit of the low emission zone so departing traffic knows the zone ends" "Only at the main exit of the zone" "At the start of the zone next to F117") `
    "As with all zones the end sign (F118) is placed at every exit. This applies to all streets and roads leaving the zone. This way everyone leaving the zone knows the LEZ rules no longer apply." `
    "Ou est place le panneau F118 par rapport a la zone a basses emissions ?" `
    (C3 "A chaque sortie de la zone a basses emissions pour que le trafic sortant sache que la zone se termine" "Uniquement a la sortie principale de la zone" "Au debut de la zone a cote de F117") `
    "Comme pour toutes les zones le panneau de fin (F118) est place a chaque sortie. Cela s applique a toutes les rues et routes quittant la zone. Ainsi tous ceux qui quittent la zone savent que les regles ZBE ne s appliquent plus." `
    "أين تُوضع لافتة F118 بالنسبة لمنطقة الانبعاثات المنخفضة؟" `
    (C3 "عند كل مخرج من منطقة الانبعاثات المنخفضة حتى يعرف المرور المغادر أن المنطقة تنتهي" "فقط عند المخرج الرئيسي للمنطقة" "عند بداية المنطقة بجانب F117") `
    "كما هو الحال مع جميع المناطق يُوضع لافتة النهاية (F118) عند كل مخرج. ينطبق ذلك على جميع الشوارع والطرق التي تغادر المنطقة. بهذه الطريقة يعرف الجميع المغادرون المنطقة أن قواعد LEZ لم تعد سارية."))

(Q "F118_Q05" "WHAT_MUST_YOU_DO" "MEDIUM" $false (I18 `
    "Gelden de ANPR-camera's (nummerplaatherkenning) nog na het passeren van F118?" `
    (C3 "Nee de handhavingscamera's van de LEZ registreren enkel voertuigen binnen de zone; na F118 bent u buiten de zone" "Ja de camera's blijven actief tot 500 meter na F118" "Ja u bent pas buiten het controlgebied na het betreden van een andere gemeente") `
    "De ANPR-camera's die de LEZ handhaven zijn gepositioneerd aan de grenzen van de zone. Zodra u F118 passeert bevindt u zich buiten de zone en worden uw gegevens niet meer geregistreerd voor LEZ-handhaving." `
    "Do the ANPR cameras (number plate recognition) still apply after passing F118?" `
    (C3 "No the LEZ enforcement cameras only register vehicles within the zone; after F118 you are outside the zone" "Yes the cameras remain active up to 500 metres after F118" "Yes you are only outside the control area after entering another municipality") `
    "The ANPR cameras that enforce the LEZ are positioned at the zone boundaries. Once you pass F118 you are outside the zone and your data is no longer recorded for LEZ enforcement." `
    "Les cameras ANPR (reconnaissance des plaques) s appliquent-elles encore apres avoir passe F118 ?" `
    (C3 "Non les cameras de contrôle de la ZBE n enregistrent que les vehicules dans la zone; apres F118 vous etes hors zone" "Oui les cameras restent actives jusqu a 500 metres apres F118" "Oui vous n etes hors de la zone de contrôle qu apres entree dans une autre commune") `
    "Les cameras ANPR qui font respecter la ZBE sont positionnees aux limites de la zone. Une fois que vous passez F118 vous etes hors de la zone et vos donnees ne sont plus enregistrees pour le contrôle ZBE." `
    "هل تسري كاميرات ANPR (التعرف على لوحات الأرقام) بعد تجاوز F118؟" `
    (C3 "لا كاميرات تطبيق LEZ تسجل فقط المركبات داخل المنطقة؛ بعد F118 أنت خارج المنطقة" "نعم تبقى الكاميرات نشطة حتى 500 متر بعد F118" "نعم أنت خارج منطقة التحكم فقط بعد الدخول إلى بلدية أخرى") `
    "كاميرات ANPR التي تُطبِّق LEZ موضوعة عند حدود المنطقة. بمجرد تجاوز F118 تكون خارج المنطقة ولا تُسجَّل بياناتك لتطبيق LEZ."))

(Q "F118_Q06" "WHAT_MUST_YOU_DO" "MEDIUM" $false (I18 `
    "Stelt bord F118 u in staat om de LEZ te betreden vanuit de tegenovergestelde richting?" `
    (C3 "Nee F118 markeert uitsluitend het verlaten van de zone; u betreedt een LEZ altijd via een F117-bord" "Ja door rechtsomkeer te maken bij F118 kunt u de zone betreden" "Ja F118 geldt als zowel begin als einde van de zone") `
    "F118 is enkel een uitrijbord. U kunt de LEZ enkel betreden via een F117-ingangsbord. Proberen om via de F118-uitgang de zone in te rijden zou in de tegengestelde rijrichting zijn wat verkeer van onrecht is." `
    "Does sign F118 allow you to enter the LEZ from the opposite direction?" `
    (C3 "No F118 exclusively marks leaving the zone; you always enter a LEZ via an F117 sign" "Yes by making a U-turn at F118 you can enter the zone" "Yes F118 serves as both start and end of the zone") `
    "F118 is an exit sign only. You can only enter the LEZ via an F117 entry sign. Trying to enter the zone via the F118 exit would be driving in the wrong direction which is against traffic law." `
    "Le panneau F118 vous permet-il d entrer dans la ZBE depuis la direction opposee ?" `
    (C3 "Non F118 marque exclusivement la sortie de la zone; vous entrez toujours dans une ZBE par un panneau F117" "Oui en faisant demi-tour a F118 vous pouvez entrer dans la zone" "Oui F118 sert a la fois de debut et de fin de la zone") `
    "F118 est uniquement un panneau de sortie. Vous ne pouvez entrer dans la ZBE que par un panneau d entree F117. Essayer d entrer dans la zone par la sortie F118 serait conduire en sens interdit ce qui est illegal." `
    "هل تسمح لك لافتة F118 بالدخول إلى منطقة LEZ من الاتجاه المعاكس؟" `
    (C3 "لا F118 تُعلِّم فقط مغادرة المنطقة؛ تدخل منطقة LEZ دائماً عبر لافتة F117" "نعم بالعمل بدوران عند F118 يمكنك الدخول إلى المنطقة" "نعم F118 تعمل كبداية ونهاية للمنطقة") `
    "F118 هي لافتة خروج فقط. يمكنك الدخول إلى منطقة LEZ فقط عبر لافتة دخول F117. محاولة الدخول إلى المنطقة عبر مخرج F118 ستعني القيادة في الاتجاه الخاطئ وهو مخالف للقانون."))

(Q "F118_Q07" "IS_IT_ALLOWED" "HARD" $true (I18 `
    "Mag een voertuig dat niet aan de LEZ-norm voldoet direct na F118 doorrijden als er een nieuwe F117 staat?" `
    (C2 "Nee als direct na F118 een nieuwe F117 volgt betreedt u meteen een nieuwe LEZ en gelden de emissiebeperkingen opnieuw" "Ja het voertuig mag altijd even pauzeren tussen twee LEZ-zones") `
    "Twee opeenvolgende LEZ-zones zijn elk afzonderlijk aangeduid met F117 en F118. Als u F118 passeert en onmiddellijk daarna F117 ziet bent u een nieuwe zone binnengekomen en gelden de beperkingen opnieuw." `
    "May a vehicle that does not meet the LEZ standard continue driving immediately after F118 if a new F117 sign is present?" `
    (C2 "No if a new F117 follows immediately after F118 you are immediately entering a new LEZ and emission restrictions apply again" "Yes the vehicle may always take a short break between two LEZ zones") `
    "Two consecutive LEZ zones are each separately indicated with F117 and F118. If you pass F118 and immediately see F117 you have entered a new zone and the restrictions apply again." `
    "Un vehicule qui ne respecte pas la norme ZBE peut-il continuer a circuler juste apres F118 si un nouveau F117 est present ?" `
    (C2 "Non si un nouveau F117 suit immediatement apres F118 vous entrez immediatement dans une nouvelle ZBE et les restrictions d emission s appliquent a nouveau" "Oui le vehicule peut toujours faire une courte pause entre deux zones ZBE") `
    "Deux zones ZBE consecutives sont chacune indiquees separement avec F117 et F118. Si vous passez F118 et voyez immediatement F117 vous etes entre dans une nouvelle zone et les restrictions s appliquent a nouveau." `
    "هل يجوز للمركبة التي لا تستوفي معيار LEZ الاستمرار في السير مباشرة بعد F118 إذا كانت لافتة F117 جديدة موجودة؟" `
    (C2 "لا إذا كانت F117 جديدة تتبع مباشرة بعد F118 فأنت تدخل منطقة LEZ جديدة فوراً وتسري قيود الانبعاثات مجدداً" "نعم يمكن للمركبة دائماً التوقف مؤقتاً بين منطقتي LEZ") `
    "منطقتا LEZ المتتاليتان تُشار إليهما كل منها على حدة بـF117 وF118. إذا تجاوزت F118 ورأيت F117 مباشرة فأنت دخلت منطقة جديدة وتسري القيود مجدداً."))

(Q "F118_Q08" "IS_IT_ALLOWED" "HARD" $true (I18 `
    "Vervallen eventueel aangevraagde ontheffingen automatisch na het verlaten van de LEZ bij F118?" `
    (C2 "Nee ontheffingen zijn gekoppeld aan de specifieke LEZ-zone waarvoor ze zijn aangevraagd; ze gelden nog steeds als u die zone opnieuw binnenrijdt" "Ja u moet bij elke intrede een nieuwe ontheffing aanvragen") `
    "Een ontheffing is geldig voor de duur waarvoor ze is afgegeven voor de specifieke LEZ. Ze vervalt niet door de zone te verlaten. Zodra u de zone opnieuw binnenrijdt (via F117) is de ontheffing nog steeds geldig zolang de geldigheidsduur niet verstreken is." `
    "Do exemptions that were applied for automatically expire after leaving the LEZ at F118?" `
    (C2 "No exemptions are linked to the specific LEZ zone for which they were applied; they still apply when you re-enter that zone" "Yes you must apply for a new exemption with each entry") `
    "An exemption is valid for the duration for which it was issued for the specific LEZ. It does not expire by leaving the zone. Once you re-enter the zone (via F117) the exemption is still valid as long as its validity period has not expired." `
    "Les derogations eventuellement demandees expirent-elles automatiquement apres avoir quitte la ZBE a F118 ?" `
    (C2 "Non les derogations sont liees a la zone ZBE specifique pour laquelle elles ont ete demandees; elles s appliquent toujours lors de la rentree dans cette zone" "Oui vous devez demander une nouvelle derogation a chaque entree") `
    "Une derogation est valable pour la duree pour laquelle elle a ete delivree pour la ZBE specifique. Elle n expire pas en quittant la zone. Une fois que vous rentrez dans la zone (via F117) la derogation est toujours valable tant que sa periode de validite n est pas expiree." `
    "هل تنتهي الإعفاءات المطلوبة تلقائياً بعد مغادرة منطقة LEZ عند F118؟" `
    (C2 "لا الإعفاءات مرتبطة بمنطقة LEZ المحددة التي طُلبت من أجلها؛ وتسري عند إعادة الدخول إلى تلك المنطقة" "نعم يجب التقدم بطلب إعفاء جديد عند كل دخول") `
    "الإعفاء صالح للمدة التي صدر من أجلها لمنطقة LEZ المحددة. لا ينتهي بمغادرة المنطقة. بمجرد إعادة الدخول إلى المنطقة (عبر F117) يبقى الإعفاء صالحاً طالما لم تنتهِ مدة صلاحيته."))
)
Save-Q "F118" $f118Qs

Write-Host "`nDone: F117 and F118 rewritten with correct IDs and 8 questions each`n"
