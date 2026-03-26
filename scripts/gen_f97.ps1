[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8

$base = "C:\Users\haydar\Desktop\end_project\readyroad\src\main\resources\data\signs_import\F97"

# sign.json
$sign = [ordered]@{
    code              = "F97"
    category          = "INFORMATION"
    image_path        = "images/signs/information_signs/F97 Rijstrook versmalling.png"
    serious_violation = $false
    i18n              = [ordered]@{
        NL = [ordered]@{
            name        = "Rijstrook versmalling"
            description = "Aanduiding van een rijstrookversmalling. Een rijstrook valt weg en het verkeer moet samenvoegen."
        }
        EN = [ordered]@{
            name        = "Lane narrowing"
            description = "Indication of a lane narrowing. One lane ends and traffic must merge into the remaining lane."
        }
        FR = [ordered]@{
            name        = "Retrecissement de voie"
            description = "Indication d'un retrecissement de voie. Une voie disparait et la circulation doit se fusionner."
        }
        AR = [ordered]@{
            name        = "تضييق مسار السير"
            description = "إشارة إلى تضييق مسار السير. تنتهي حارة مرور ويجب على السائقين الاندماج في الحارة المتبقية."
        }
    }
}
$signJson = $sign | ConvertTo-Json -Depth 10
[System.IO.File]::WriteAllText("$base\sign.json", $signJson, [System.Text.Encoding]::UTF8)
Write-Host "sign.json written"

# questions.json
$q = @(
    [ordered]@{
        question_id = "F97_Q01"
        type        = "WHAT_DOES_IT_MEAN"
        difficulty  = "EASY"
        is_critical = $false
        show_sign   = $true
        i18n        = [ordered]@{
            NL = [ordered]@{
                question    = "Wat betekent bord F97?"
                choices     = @(
                    [ordered]@{ text = "Een rijstrook valt weg en het verkeer moet samenvoegen"; is_correct = $true }
                    [ordered]@{ text = "De rijbaan wordt breder"; is_correct = $false }
                    [ordered]@{ text = "Inhalen is verboden"; is_correct = $false }
                )
                explanation = "F97 duidt aan dat een rijstrook versmalt. Een van de rijstroken eindigt en het verkeer moet samenvoegen naar de overblijvende rijstrook."
            }
            EN = [ordered]@{
                question    = "What does sign F97 mean?"
                choices     = @(
                    [ordered]@{ text = "A lane ends and traffic must merge"; is_correct = $true }
                    [ordered]@{ text = "The road becomes wider"; is_correct = $false }
                    [ordered]@{ text = "Overtaking is prohibited"; is_correct = $false }
                )
                explanation = "F97 indicates that a lane is narrowing. One of the lanes ends and traffic must merge into the remaining lane."
            }
            FR = [ordered]@{
                question    = "Que signifie le panneau F97 ?"
                choices     = @(
                    [ordered]@{ text = "Une voie disparait et la circulation doit fusionner"; is_correct = $true }
                    [ordered]@{ text = "La route s elargit"; is_correct = $false }
                    [ordered]@{ text = "Le depassement est interdit"; is_correct = $false }
                )
                explanation = "F97 indique un retrecissement de voie. L une des voies se termine et la circulation doit fusionner dans la voie restante."
            }
            AR = [ordered]@{
                question    = "ما معنى لافتة F97؟"
                choices     = @(
                    [ordered]@{ text = "تنتهي حارة مرور ويجب على السائقين الاندماج"; is_correct = $true }
                    [ordered]@{ text = "الطريق يتسع"; is_correct = $false }
                    [ordered]@{ text = "التجاوز محظور"; is_correct = $false }
                )
                explanation = "تشير F97 إلى تضييق مسار السير. تنتهي إحدى الحارات ويجب على السائقين الاندماج في الحارة المتبقية."
            }
        }
    }
    [ordered]@{
        question_id = "F97_Q02"
        type        = "WHICH_SIGN"
        difficulty  = "EASY"
        is_critical = $false
        show_sign   = $true
        i18n        = [ordered]@{
            NL = [ordered]@{
                question    = "Welk bord geeft aan dat een rijstrook wegvalt en verkeer moet samenvoegen?"
                choices     = @(
                    [ordered]@{ text = "F97 rijstrook versmalling"; is_correct = $true }
                    [ordered]@{ text = "F45 verplichte rijrichting rechtdoor"; is_correct = $false }
                    [ordered]@{ text = "A7c rijbaanversmalling"; is_correct = $false }
                )
                explanation = "F97 is het informatief bord dat een rijstrookversmalling aanduidt. A7c is een gevarenbord voor een rijbaanversmalling maar heeft een andere functie en plaatsing."
            }
            EN = [ordered]@{
                question    = "Which sign indicates that a lane ends and traffic must merge?"
                choices     = @(
                    [ordered]@{ text = "F97 lane narrowing"; is_correct = $true }
                    [ordered]@{ text = "F45 compulsory direction straight ahead"; is_correct = $false }
                    [ordered]@{ text = "A7c road narrows"; is_correct = $false }
                )
                explanation = "F97 is the information sign indicating a lane narrowing. A7c is a danger sign for road narrowing but has a different function and position."
            }
            FR = [ordered]@{
                question    = "Quel panneau indique qu une voie disparait et que la circulation doit fusionner ?"
                choices     = @(
                    [ordered]@{ text = "F97 retrecissement de voie"; is_correct = $true }
                    [ordered]@{ text = "F45 direction obligatoire tout droit"; is_correct = $false }
                    [ordered]@{ text = "A7c retrecissement de la chaussee"; is_correct = $false }
                )
                explanation = "F97 est le panneau d information indiquant un retrecissement de voie. A7c est un panneau de danger pour le retrecissement de chaussee mais a une fonction et un placement differents."
            }
            AR = [ordered]@{
                question    = "أي لافتة تشير إلى انتهاء حارة مرور ووجوب اندماج السائقين؟"
                choices     = @(
                    [ordered]@{ text = "F97 تضييق مسار السير"; is_correct = $true }
                    [ordered]@{ text = "F45 اتجاه إلزامي للأمام"; is_correct = $false }
                    [ordered]@{ text = "A7c تضييق الطريق"; is_correct = $false }
                )
                explanation = "F97 هي لافتة المعلومات التي تشير إلى تضييق مسار السير. A7c هي لافتة خطر لتضييق الطريق ولكن لها وظيفة وموضع مختلفان."
            }
        }
    }
    [ordered]@{
        question_id = "F97_Q03"
        type        = "HAZARD_IDENTIFICATION"
        difficulty  = "EASY"
        is_critical = $false
        show_sign   = $true
        i18n        = [ordered]@{
            NL = [ordered]@{
                question    = "Welk gevaar is er bij een F97 rijstrookversmalling?"
                choices     = @(
                    [ordered]@{ text = "Botsingsrisico doordat voertuigen moeten samenvoegen"; is_correct = $true }
                    [ordered]@{ text = "Gevaar van gladheid door water op de weg"; is_correct = $false }
                    [ordered]@{ text = "Verhoogd risico op voetgangers op de rijbaan"; is_correct = $false }
                )
                explanation = "Bij een rijstrookversmalling moeten voertuigen samenvoegen. Dit verhoogt het risico op zijdelingse aanrijdingen als bestuurders het samenvoegen niet correct uitvoeren."
            }
            EN = [ordered]@{
                question    = "What hazard is present at an F97 lane narrowing?"
                choices     = @(
                    [ordered]@{ text = "Collision risk because vehicles must merge"; is_correct = $true }
                    [ordered]@{ text = "Risk of skidding due to water on the road"; is_correct = $false }
                    [ordered]@{ text = "Increased risk of pedestrians on the road"; is_correct = $false }
                )
                explanation = "At a lane narrowing vehicles must merge. This increases the risk of side collisions if drivers do not perform the merge correctly."
            }
            FR = [ordered]@{
                question    = "Quel danger est present lors d un retrecissement de voie F97 ?"
                choices     = @(
                    [ordered]@{ text = "Risque de collision car les vehicules doivent fusionner"; is_correct = $true }
                    [ordered]@{ text = "Risque de glissement du a l eau sur la chaussee"; is_correct = $false }
                    [ordered]@{ text = "Risque accru de pietons sur la chaussee"; is_correct = $false }
                )
                explanation = "Lors d un retrecissement de voie les vehicules doivent fusionner. Cela augmente le risque de collisions laterales si les conducteurs ne realisent pas la fusion correctement."
            }
            AR = [ordered]@{
                question    = "ما الخطر الموجود عند تضييق مسار F97؟"
                choices     = @(
                    [ordered]@{ text = "خطر الاصطدام لأن المركبات يجب أن تندمج"; is_correct = $true }
                    [ordered]@{ text = "خطر الانزلاق بسبب الماء على الطريق"; is_correct = $false }
                    [ordered]@{ text = "زيادة خطر المشاة على الطريق"; is_correct = $false }
                )
                explanation = "عند تضييق المسار يجب على المركبات الاندماج. هذا يزيد من خطر الاصطدامات الجانبية إذا لم يقم السائقون بالاندماج بشكل صحيح."
            }
        }
    }
    [ordered]@{
        question_id = "F97_Q04"
        type        = "WHAT_MUST_YOU_DO"
        difficulty  = "MEDIUM"
        is_critical = $false
        show_sign   = $true
        i18n        = [ordered]@{
            NL = [ordered]@{
                question    = "Wat moet je doen als je bord F97 ziet en in de wegvallende rijstrook rijdt?"
                choices     = @(
                    [ordered]@{ text = "Tijdig de ritsbeweging uitvoeren en invoegen in de doorgaande rijstrook"; is_correct = $true }
                    [ordered]@{ text = "Stoppen en wachten tot de andere rijstrook vrij is"; is_correct = $false }
                    [ordered]@{ text = "Versnellen om zoveel mogelijk voertuigen voor te blijven"; is_correct = $false }
                )
                explanation = "Bij een rijstrookversmalling moet je de ritsbeweging toepassen. Dat betekent dat je tot aan het punt waar de rijstrook eindigt doorrijdt en dan om beurt invoegt. Je moet dit veilig en tijdig uitvoeren."
            }
            EN = [ordered]@{
                question    = "What must you do when you see sign F97 and are driving in the ending lane?"
                choices     = @(
                    [ordered]@{ text = "Perform the zipper merge in time and merge into the continuing lane"; is_correct = $true }
                    [ordered]@{ text = "Stop and wait until the other lane is clear"; is_correct = $false }
                    [ordered]@{ text = "Accelerate to stay ahead of as many vehicles as possible"; is_correct = $false }
                )
                explanation = "At a lane narrowing you must apply the zipper merge rule. This means you continue to the point where the lane ends and then merge alternately. You must do this safely and in time."
            }
            FR = [ordered]@{
                question    = "Que devez-vous faire lorsque vous voyez le panneau F97 et que vous conduisez dans la voie qui disparait ?"
                choices     = @(
                    [ordered]@{ text = "Effectuer la fermeture eclair a temps et vous inserer dans la voie continue"; is_correct = $true }
                    [ordered]@{ text = "Vous arreter et attendre que l autre voie soit libre"; is_correct = $false }
                    [ordered]@{ text = "Accelerer pour devancer le plus de vehicules possible"; is_correct = $false }
                )
                explanation = "Lors d un retrecissement de voie vous devez appliquer la regle de la fermeture eclair. Cela signifie que vous continuez jusqu au point ou la voie se termine puis vous vous inserez alternativement. Vous devez le faire en toute securite et a temps."
            }
            AR = [ordered]@{
                question    = "ماذا يجب أن تفعل عندما ترى لافتة F97 وأنت تسير في الحارة المنتهية؟"
                choices     = @(
                    [ordered]@{ text = "تنفيذ حركة السحاب في الوقت المناسب والاندماج في الحارة المستمرة"; is_correct = $true }
                    [ordered]@{ text = "التوقف والانتظار حتى تخلو الحارة الأخرى"; is_correct = $false }
                    [ordered]@{ text = "التسارع للبقاء أمام أكبر عدد من المركبات"; is_correct = $false }
                )
                explanation = "عند تضييق المسار يجب تطبيق قاعدة الاندماج المتناوب (السحاب). هذا يعني الاستمرار إلى النقطة التي تنتهي فيها الحارة ثم الاندماج بالتناوب. يجب القيام بذلك بأمان وفي الوقت المناسب."
            }
        }
    }
    [ordered]@{
        question_id = "F97_Q05"
        type        = "WHAT_MUST_YOU_DO"
        difficulty  = "MEDIUM"
        is_critical = $false
        show_sign   = $true
        i18n        = [ordered]@{
            NL = [ordered]@{
                question    = "Hoe moet je als bestuurder in de doorgaande rijstrook reageren bij een F97 rijstrookversmalling?"
                choices     = @(
                    [ordered]@{ text = "Voldoende ruimte laten voor invoegend verkeer en de ritsbeweging faciliteren"; is_correct = $true }
                    [ordered]@{ text = "Je snelheid verhogen zodat er minder ruimte ontstaat voor invoegers"; is_correct = $false }
                    [ordered]@{ text = "Naar de wegvallende rijstrook rijden om die te gebruiken"; is_correct = $false }
                )
                explanation = "De bestuurder in de doorgaande rijstrook moet medewerking verlenen aan de ritsbeweging door voldoende ruimte te laten voor het invoegend verkeer. Dit zorgt voor een vlotte en veilige doorstroom."
            }
            EN = [ordered]@{
                question    = "How must you as a driver in the continuing lane react at an F97 lane narrowing?"
                choices     = @(
                    [ordered]@{ text = "Leave enough space for merging traffic and facilitate the zipper merge"; is_correct = $true }
                    [ordered]@{ text = "Increase your speed so there is less space for merging vehicles"; is_correct = $false }
                    [ordered]@{ text = "Move to the ending lane to use it"; is_correct = $false }
                )
                explanation = "The driver in the continuing lane must cooperate with the zipper merge by leaving sufficient space for merging traffic. This ensures smooth and safe traffic flow."
            }
            FR = [ordered]@{
                question    = "Comment devez-vous en tant que conducteur dans la voie continue reagir lors d un retrecissement F97 ?"
                choices     = @(
                    [ordered]@{ text = "Laisser suffisamment d espace pour le trafic qui s insere et faciliter la fermeture eclair"; is_correct = $true }
                    [ordered]@{ text = "Augmenter votre vitesse pour qu il y ait moins d espace pour les vehicules qui s inserent"; is_correct = $false }
                    [ordered]@{ text = "Vous deplacer vers la voie qui disparait pour l utiliser"; is_correct = $false }
                )
                explanation = "Le conducteur dans la voie continue doit cooperer avec la fermeture eclair en laissant suffisamment d espace pour le trafic qui s insere. Cela assure un flux de trafic fluide et sur."
            }
            AR = [ordered]@{
                question    = "كيف يجب أن تتصرف كسائق في الحارة المستمرة عند تضييق مسار F97؟"
                choices     = @(
                    [ordered]@{ text = "ترك مساحة كافية للمركبات المندمجة وتسهيل حركة الاندماج المتناوب"; is_correct = $true }
                    [ordered]@{ text = "زيادة السرعة لتقليل المساحة للمركبات المندمجة"; is_correct = $false }
                    [ordered]@{ text = "التحرك إلى الحارة المنتهية لاستخدامها"; is_correct = $false }
                )
                explanation = "يجب على السائق في الحارة المستمرة التعاون مع حركة الاندماج المتناوب بترك مساحة كافية للمركبات المندمجة. هذا يضمن تدفق مرور سلس وآمن."
            }
        }
    }
    [ordered]@{
        question_id = "F97_Q06"
        type        = "WHAT_MUST_YOU_DO"
        difficulty  = "MEDIUM"
        is_critical = $false
        show_sign   = $true
        i18n        = [ordered]@{
            NL = [ordered]@{
                question    = "Wat is de ritsregel bij een rijstrookversmalling (F97)?"
                choices     = @(
                    [ordered]@{ text = "Voertuigen wisselen om beurten in: een uit de doorgaande rijstrook dan een uit de wegvallende rijstrook"; is_correct = $true }
                    [ordered]@{ text = "De rijstrook die het eerst vol is heeft altijd voorrang"; is_correct = $false }
                    [ordered]@{ text = "Voertuigen in de wegvallende rijstrook moeten stoppen en wachten"; is_correct = $false }
                )
                explanation = "De ritsregel bepaalt dat voertuigen bij een rijstrookversmalling om beurten invoegen zoals de tanden van een rits. Een voertuig uit de doorgaande rijstrook laat er een in vanuit de wegvallende rijstrook. Dit zorgt voor een eerlijke en efficiënte doorstroom."
            }
            EN = [ordered]@{
                question    = "What is the zipper rule at a lane narrowing (F97)?"
                choices     = @(
                    [ordered]@{ text = "Vehicles alternate merging: one from the continuing lane then one from the ending lane"; is_correct = $true }
                    [ordered]@{ text = "The lane that fills up first always has priority"; is_correct = $false }
                    [ordered]@{ text = "Vehicles in the ending lane must stop and wait"; is_correct = $false }
                )
                explanation = "The zipper rule states that at a lane narrowing vehicles merge alternately like the teeth of a zip. One vehicle from the continuing lane lets one in from the ending lane. This ensures fair and efficient traffic flow."
            }
            FR = [ordered]@{
                question    = "Quelle est la regle de la fermeture eclair lors d un retrecissement de voie (F97) ?"
                choices     = @(
                    [ordered]@{ text = "Les vehicules s inserent alternativement : un de la voie continue puis un de la voie qui disparait"; is_correct = $true }
                    [ordered]@{ text = "La voie qui se remplit en premier a toujours la priorite"; is_correct = $false }
                    [ordered]@{ text = "Les vehicules dans la voie qui disparait doivent s arreter et attendre"; is_correct = $false }
                )
                explanation = "La regle de la fermeture eclair stipule que lors d un retrecissement de voie les vehicules fusionnent alternativement comme les dents d une fermeture eclair. Un vehicule de la voie continue en laisse entrer un de la voie qui disparait. Cela assure un flux de trafic equitable et efficace."
            }
            AR = [ordered]@{
                question    = "ما هي قاعدة الاندماج المتناوب (السحاب) عند تضييق المسار (F97)؟"
                choices     = @(
                    [ordered]@{ text = "تتناوب المركبات في الاندماج: واحدة من الحارة المستمرة ثم واحدة من الحارة المنتهية"; is_correct = $true }
                    [ordered]@{ text = "الحارة التي تمتلئ أولاً لها الأولوية دائماً"; is_correct = $false }
                    [ordered]@{ text = "يجب على المركبات في الحارة المنتهية التوقف والانتظار"; is_correct = $false }
                )
                explanation = "تنص قاعدة الاندماج المتناوب على أن المركبات عند تضييق المسار تندمج بالتناوب مثل أسنان السحاب. مركبة من الحارة المستمرة تترك مركبة من الحارة المنتهية تندمج. هذا يضمن تدفق مرور عادل وفعّال."
            }
        }
    }
    [ordered]@{
        question_id = "F97_Q07"
        type        = "IS_IT_ALLOWED"
        difficulty  = "HARD"
        is_critical = $true
        show_sign   = $true
        i18n        = [ordered]@{
            NL = [ordered]@{
                question    = "Mag je in de wegvallende rijstrook tot helemaal aan het samenvoegpunt doorrijden alvorens in te voegen?"
                choices     = @(
                    [ordered]@{ text = "Ja de ritsregel staat toe om de volledige lengte van de wegvallende rijstrook te gebruiken"; is_correct = $true }
                    [ordered]@{ text = "Nee je moet zo snel mogelijk invoegen zodra je bord F97 ziet"; is_correct = $false }
                )
                explanation = "Ja de ritsregel laat uitdrukkelijk toe dat je de volledige lengte van de wegvallende rijstrook gebruikt. Vroeg invoegen is niet verplicht en kan zelfs de doorstroom verstoren. Je voegt in bij het samenvoegpunt en dat is geen voorrangskwestie maar beurtregeling."
            }
            EN = [ordered]@{
                question    = "Are you allowed to continue in the ending lane all the way to the merge point before merging?"
                choices     = @(
                    [ordered]@{ text = "Yes the zipper rule allows use of the full length of the ending lane"; is_correct = $true }
                    [ordered]@{ text = "No you must merge as soon as possible when you see sign F97"; is_correct = $false }
                )
                explanation = "Yes the zipper rule explicitly allows you to use the full length of the ending lane. Merging early is not mandatory and can even disrupt traffic flow. You merge at the merge point which is not a priority matter but an alternating turn system."
            }
            FR = [ordered]@{
                question    = "Etes-vous autorise a continuer dans la voie qui disparait jusqu au point de fusion avant de vous inserer ?"
                choices     = @(
                    [ordered]@{ text = "Oui la regle de la fermeture eclair permet d utiliser toute la longueur de la voie qui disparait"; is_correct = $true }
                    [ordered]@{ text = "Non vous devez vous inserer le plus tot possible lorsque vous voyez le panneau F97"; is_correct = $false }
                )
                explanation = "Oui la regle de la fermeture eclair vous autorise explicitement a utiliser toute la longueur de la voie qui disparait. S inserer tot n est pas obligatoire et peut meme perturber le flux de trafic. Vous vous inserez au point de fusion ce qui n est pas une question de priorite mais un systeme de rotation."
            }
            AR = [ordered]@{
                question    = "هل يُسمح لك بالاستمرار في الحارة المنتهية حتى نقطة الاندماج قبل الاندماج؟"
                choices     = @(
                    [ordered]@{ text = "نعم تسمح قاعدة الاندماج المتناوب باستخدام كامل طول الحارة المنتهية"; is_correct = $true }
                    [ordered]@{ text = "لا يجب الاندماج في أقرب وقت ممكن عند رؤية لافتة F97"; is_correct = $false }
                )
                explanation = "نعم تسمح قاعدة الاندماج المتناوب صراحةً باستخدام كامل طول الحارة المنتهية. الاندماج المبكر ليس إلزامياً وقد يعطل تدفق المرور. تندمج عند نقطة الاندماج وهذه ليست مسألة أولوية بل نظام تناوب."
            }
        }
    }
    [ordered]@{
        question_id = "F97_Q08"
        type        = "IS_IT_ALLOWED"
        difficulty  = "HARD"
        is_critical = $true
        show_sign   = $true
        i18n        = [ordered]@{
            NL = [ordered]@{
                question    = "Is het toegestaan om bij een F97 rijstrookversmalling de volgorde van de ritsbeweging te negeren en zonder toestemming in te voegen?"
                choices     = @(
                    [ordered]@{ text = "Nee je moet de beurtregeling respecteren en wachten tot het voertuig voor jou heeft ingevoegd"; is_correct = $true }
                    [ordered]@{ text = "Ja als je haast hebt mag je twee achtereenvolgende voertuigen laten invoegen en dan pas gaan"; is_correct = $false }
                )
                explanation = "De ritsregel vereist dat voertuigen om beurt invoegen. Je mag niet buiten de beurt invoegen. Als je in de wegvallende rijstrook rijdt moet je wachten tot het voertuig dat voor jou staat in de rits heeft ingevoegd en dan pas is het jouw beurt."
            }
            EN = [ordered]@{
                question    = "Is it allowed at an F97 lane narrowing to ignore the zipper turn order and merge without permission?"
                choices     = @(
                    [ordered]@{ text = "No you must respect the turn order and wait until the vehicle in front of you has merged"; is_correct = $true }
                    [ordered]@{ text = "Yes if you are in a hurry you may let two consecutive vehicles merge then go"; is_correct = $false }
                )
                explanation = "The zipper rule requires vehicles to merge alternately. You may not merge out of turn. If you are in the ending lane you must wait until the vehicle ahead of you in the zipper has merged and then it is your turn."
            }
            FR = [ordered]@{
                question    = "Est-il permis lors d un retrecissement de voie F97 d ignorer l ordre de la fermeture eclair et de s inserer sans permission ?"
                choices     = @(
                    [ordered]@{ text = "Non vous devez respecter l ordre de rotation et attendre que le vehicule devant vous se soit insere"; is_correct = $true }
                    [ordered]@{ text = "Oui si vous etes presse vous pouvez laisser deux vehicules consecutifs s inserer puis partir"; is_correct = $false }
                )
                explanation = "La regle de la fermeture eclair exige que les vehicules fusionnent alternativement. Vous ne pouvez pas vous inserer hors tour. Si vous etes dans la voie qui disparait vous devez attendre que le vehicule devant vous dans la fermeture eclair se soit insere et c est alors votre tour."
            }
            AR = [ordered]@{
                question    = "هل يُسمح عند تضييق مسار F97 بتجاهل ترتيب الاندماج المتناوب والاندماج دون إذن؟"
                choices     = @(
                    [ordered]@{ text = "لا يجب احترام ترتيب الدور والانتظار حتى تندمج المركبة أمامك"; is_correct = $true }
                    [ordered]@{ text = "نعم إذا كنت في عجلة يمكنك السماح لمركبتين متتاليتين بالاندماج ثم المضي"; is_correct = $false }
                )
                explanation = "تشترط قاعدة الاندماج المتناوب أن تندمج المركبات بالتناوب. لا يجوز الاندماج خارج الدور. إذا كنت في الحارة المنتهية يجب الانتظار حتى تندمج المركبة أمامك في الاندماج المتناوب ثم يأتي دورك."
            }
        }
    }
)

$questionsJson = $q | ConvertTo-Json -Depth 20 -Compress
[System.IO.File]::WriteAllText("$base\questions.json", $questionsJson, [System.Text.Encoding]::UTF8)
Write-Host "questions.json written ($($questionsJson.Length) bytes)"

# exams.json
$exams = [ordered]@{
    exam_1          = [ordered]@{
        questions       = @("F97_Q01", "F97_Q02", "F97_Q03", "F97_Q04", "F97_Q05", "F97_Q06", "F97_Q07", "F97_Q08")
        passing_score   = 6
        total_questions = 8
        distribution    = [ordered]@{ EASY = 3; MEDIUM = 3; HARD = 2 }
    }
    passing_score   = 6
    total_questions = 8
    distribution    = [ordered]@{ EASY = 3; MEDIUM = 3; HARD = 2 }
}
$examsJson = $exams | ConvertTo-Json -Depth 10 -Compress
[System.IO.File]::WriteAllText("$base\exams.json", $examsJson, [System.Text.Encoding]::UTF8)
Write-Host "exams.json written"

Write-Host "F97 complete - all 3 files created"
