#!/usr/bin/env python3
"""
Generate questions.json for all 61 LEGACY signs.
Run from any directory. Uses absolute path for signs_import.
"""
import json
import os

BASE = r"C:\Users\haydar\Desktop\end_project\readyroad\src\main\resources\data\signs_import"

def q3(qid, qtype, diff, crit,
       nl_q, nl_c, nl_exp,
       en_q, en_c, en_exp,
       fr_q, fr_c, fr_exp,
       ar_q, ar_c, ar_exp):
    """3-choice question. nl_c is list of (text, is_correct)."""
    def lang(q, c, exp):
        return {"question": q, "choices": [{"text": t, "is_correct": ok} for t, ok in c], "explanation": exp}
    return {
        "question_id": qid, "type": qtype, "difficulty": diff,
        "is_critical": crit, "show_sign": True,
        "i18n": {
            "NL": lang(nl_q, nl_c, nl_exp),
            "EN": lang(en_q, en_c, en_exp),
            "FR": lang(fr_q, fr_c, fr_exp),
            "AR": lang(ar_q, ar_c, ar_exp),
        }
    }

def q2(qid, qtype, diff, crit,
       nl_q, nl_c, nl_exp,
       en_q, en_c, en_exp,
       fr_q, fr_c, fr_exp,
       ar_q, ar_c, ar_exp):
    """2-choice question (IS_IT_ALLOWED). nl_c is list of (text, is_correct)."""
    return q3(qid, qtype, diff, crit,
              nl_q, nl_c, nl_exp, en_q, en_c, en_exp,
              fr_q, fr_c, fr_exp, ar_q, ar_c, ar_exp)

def write(sign_code, questions):
    path = os.path.join(BASE, sign_code, "questions.json")
    with open(path, "w", encoding="utf-8") as f:
        json.dump(questions, f, ensure_ascii=False, indent=2)
    print(f"  {sign_code}: {len(questions)} questions -> {os.path.getsize(path)} bytes")

# ─────────────────────────────────────────────
# E1 — No parking
# ─────────────────────────────────────────────
write("E1", [
  q3("E1_Q01","WHAT_DOES_IT_MEAN","EASY",False,
     "Wat betekent bord E1?",
     [("Parkeerverbod: u mag uw voertuig hier niet parkeren",True),("Stopverbod: stoppen en parkeren zijn beiden verboden",False),("Parkeerzone: parkeren is hier verplicht",False)],
     "E1 verbiedt parkeren. Stoppen (even stilstaan) is nog toegestaan zolang u bij het voertuig blijft.",
     "What does sign E1 mean?",
     [("No parking: you may not park your vehicle here",True),("No stopping: both stopping and parking are forbidden",False),("Parking zone: parking is required here",False)],
     "E1 forbids parking. Stopping briefly is still allowed as long as you stay with the vehicle.",
     "Que signifie le panneau E1 ?",
     [("Interdiction de stationnement: vous ne pouvez pas garer votre vehicule ici",True),("Interdiction d'arret: l'arret et le stationnement sont tous deux interdits",False),("Zone de stationnement: le stationnement est obligatoire ici",False)],
     "E1 interdit le stationnement. S'arreter brievement reste autorise tant que vous restez aupres du vehicule.",
     "ما معنى لافتة E1؟",
     [("ممنوع الوقوف: لا يجوز ركن سيارتك هنا",True),("ممنوع التوقف: التوقف والوقوف كلاهما محظور",False),("منطقة وقوف: الوقوف إلزامي هنا",False)],
     "E1 تحظر الوقوف. التوقف القصير لا يزال مسموحاً طالما بقيت مع المركبة."),
  q3("E1_Q02","WHICH_SIGN","EASY",False,
     "Tot welke reeks behoort bord E1?",
     [("E-reeks: parkeer- en stilstandsreglementsborden",True),("C-reeks: verbodsborden voor rijverkeer",False),("F-reeks: informatieborden",False)],
     "De E-reeks bevat alle parkeer- en stilstandsreglementsborden. E1 verbiedt parkeren op de betrokken locatie.",
     "To which series does sign E1 belong?",
     [("E-series: parking and stopping regulation signs",True),("C-series: prohibitory signs for moving traffic",False),("F-series: information signs",False)],
     "The E-series contains all parking and stopping regulation signs. E1 specifically prohibits parking.",
     "A quelle serie appartient le panneau E1 ?",
     [("Serie E: panneaux de reglementation de stationnement et d'arret",True),("Serie C: panneaux d'interdiction pour la circulation",False),("Serie F: panneaux d'information",False)],
     "La serie E contient tous les panneaux de reglementation de stationnement. E1 interdit specifiquement le stationnement.",
     "إلى أي سلسلة تنتمي لافتة E1؟",
     [("السلسلة E: لافتات تنظيم الوقوف والتوقف",True),("السلسلة C: لافتات الحظر لحركة المرور المتحركة",False),("السلسلة F: لافتات المعلومات",False)],
     "تحتوي السلسلة E على جميع لافتات تنظيم الوقوف. E1 تحظر الوقوف تحديداً في الموقع المُشار إليه."),
  q3("E1_Q03","HAZARD_IDENTIFICATION","EASY",False,
     "Welk gevaar pakt bord E1 aan?",
     [("Rijstroken en zichthoeken geblokkeerd door geparkeerde voertuigen",True),("Rijden aan te hoge snelheid in woongebieden",False),("Gevaarlijke kruispunten zonder voorrangsbord",False)],
     "E1 voorkomt dat voertuigen rijstroken, fietsstroken of zichthoeken blokkeren door hier te parkeren.",
     "What hazard does sign E1 address?",
     [("Lanes and sight lines blocked by parked vehicles",True),("Driving at excessive speed in residential areas",False),("Dangerous intersections without priority sign",False)],
     "E1 prevents vehicles from blocking carriageways, cycle lanes or sight lines by parking here.",
     "Quel danger le panneau E1 traite-t-il ?",
     [("Voies et angles de visibilite bloques par des vehicules gares",True),("Conduite a vitesse excessive dans les zones residentielles",False),("Carrefours dangereux sans panneau de priorite",False)],
     "E1 empeche les vehicules de bloquer la chaussee, les pistes cyclables ou les angles de visibilite.",
     "ما الخطر الذي تُعالجه لافتة E1؟",
     [("حارات وخطوط رؤية محجوبة بسبب سيارات موقوفة",True),("السير بسرعة مفرطة في المناطق السكنية",False),("تقاطعات خطيرة بدون لافتة أولوية",False)],
     "E1 تمنع المركبات من حجب الطريق أو مسارات الدراجات أو خطوط الرؤية."),
  q3("E1_Q04","WHAT_MUST_YOU_DO","MEDIUM",False,
     "U staat voor bord E1. Wat mag u wél doen?",
     [("Kort stoppen om een passagier in of uit te laten, mits u bij het voertuig blijft",True),("Uw voertuig achterlaten terwijl u boodschappen doet",False),("Parkeren als u de noodknipperlichten aanzet",False)],
     "E1 verbiedt parkeren maar staat stoppen toe. Kort stilstaan om iemand in/uit te laten is toegelaten.",
     "You are in front of sign E1. What may you still do?",
     [("Briefly stop to let a passenger in or out, provided you stay with the vehicle",True),("Leave your vehicle while you go shopping",False),("Park if you switch on the hazard lights",False)],
     "E1 only forbids parking. Briefly stopping to let someone in or out remains permitted.",
     "Vous etes devant le panneau E1. Que pouvez-vous encore faire ?",
     [("S'arreter brievement pour laisser monter/descendre un passager en restant aupres du vehicule",True),("Laisser votre vehicule pendant que vous faites des courses",False),("Se garer en allumant les feux de detresse",False)],
     "E1 interdit seulement le stationnement. S'arreter brievement reste autorise.",
     "أنت أمام لافتة E1. ما الذي لا يزال مسموحاً به؟",
     [("التوقف لفترة قصيرة لإنزال أو إركاب راكب مع البقاء بجانب المركبة",True),("ترك مركبتك أثناء التسوق",False),("الوقوف مع تشغيل أضواء الخطر",False)],
     "E1 تحظر الوقوف فقط. التوقف القصير لا يزال مسموحاً."),
  q3("E1_Q05","WHAT_MUST_YOU_DO","MEDIUM",False,
     "Aan welke zijde van de weg geldt E1?",
     [("Enkel aan de zijde waar het bord staat opgesteld",True),("Aan beide zijden van de rijbaan",False),("Aan de zijde tegenover het bord",False)],
     "Een E1-bord geldt enkel voor de kant van de rijbaan waaraan het is geplaatst.",
     "On which side of the road does E1 apply?",
     [("Only on the side where the sign is placed",True),("On both sides of the road",False),("On the side opposite the sign",False)],
     "An E1 sign applies only to the side of the road on which it is installed.",
     "De quel cote de la route E1 s'applique-t-il ?",
     [("Uniquement du cote ou le panneau est place",True),("Des deux cotes de la route",False),("Du cote oppose au panneau",False)],
     "Un panneau E1 ne s'applique qu'au cote de la route sur lequel il est installe.",
     "على أي جانب من الطريق تنطبق E1؟",
     [("فقط على الجانب الذي وُضعت عليه اللافتة",True),("على كلا جانبي الطريق",False),("على الجانب المقابل للافتة",False)],
     "لافتة E1 تنطبق فقط على الجانب الذي وُضعت عليه."),
  q3("E1_Q06","WHAT_MUST_YOU_DO","MEDIUM",False,
     "Er staat een E1-bord. Waar mag u uw auto parkeren?",
     [("In een zijstraat of op een parkeerterrein buiten de verbodzone",True),("Op het trottoir want dat is geen rijbaan",False),("Recht tegenover het bord",False)],
     "Bij een E1-bord moet u verder rijden naar een locatie waar parkeren wél is toegestaan.",
     "There is an E1 sign. Where may you park?",
     [("In a side street or car park outside the no-parking zone",True),("On the pavement because it is not a carriageway",False),("Directly opposite the sign",False)],
     "With an E1 sign you must drive to a location where parking is permitted.",
     "Il y a un panneau E1. Ou pouvez-vous vous garer ?",
     [("Dans une rue laterale ou un parking en dehors de la zone d'interdiction",True),("Sur le trottoir car ce n'est pas une chaussee",False),("Directement en face du panneau",False)],
     "Avec un panneau E1 vous devez continuer jusqu'a un endroit ou le stationnement est autorise.",
     "هناك لافتة E1. أين يمكنك الوقوف؟",
     [("في شارع جانبي أو موقف سيارات خارج منطقة الحظر",True),("على الرصيف لأنه ليس طريقاً",False),("مقابل اللافتة مباشرة",False)],
     "مع لافتة E1 يجب المتابعة حتى موقع مسموح فيه بالوقوف."),
  q2("E1_Q07","IS_IT_ALLOWED","HARD",True,
     "Mag u bij E1 parkeren als u de noodknipperlichten aanzet?",
     [("Neen: noodknipperlichten geven geen parkeerrecht; het verbod blijft van kracht",True),("Ja: noodknipperlichten zijn gelijkgesteld aan een uitzondering op het verbod",False)],
     "Noodknipperlichten dienen als waarschuwing bij pech of noodstop. Ze verlenen geen parkeerrecht.",
     "May you park under E1 if you switch on the hazard lights?",
     [("No: hazard lights do not grant any parking right; the prohibition remains in force",True),("Yes: hazard lights are equivalent to an exemption from the prohibition",False)],
     "Hazard lights serve as a warning in breakdown or emergency stops. They grant no parking right.",
     "Pouvez-vous vous garer sous E1 en allumant les feux de detresse ?",
     [("Non: les feux de detresse ne conferent aucun droit de stationnement; l'interdiction reste en vigueur",True),("Oui: les feux de detresse equivalents a une derogation a l'interdiction",False)],
     "Les feux de detresse servent d'avertissement en cas de panne. Ils ne conferent aucun droit de stationnement.",
     "هل يُسمح لك بالوقوف تحت E1 إذا شغّلت أضواء الخطر؟",
     [("لا: أضواء الخطر لا تمنح أي حق وقوف؛ يبقى الحظر سارياً",True),("نعم: أضواء الخطر معادلة للإعفاء من الحظر",False)],
     "أضواء الخطر تُستخدم كتحذير عند العطل أو التوقف الطارئ. لا تمنح أي حق وقوف."),
  q2("E1_Q08","IS_IT_ALLOWED","HARD",True,
     "Mag u bij E1 laden en lossen als u bij het voertuig blijft?",
     [("Ja: laden/lossen waarbij de bestuurder aanwezig blijft geldt als stoppen, niet als parkeren",True),("Neen: E1 verbiedt elke vorm van stilstaan inclusief laden en lossen",False)],
     "Parkeren = voertuig onbeheerd achterlaten. Laden/lossen met de bestuurder aanwezig geldt als stoppen en valt niet onder E1.",
     "May you load/unload under E1 if you stay with the vehicle?",
     [("Yes: loading/unloading with the driver present counts as stopping, not parking",True),("No: E1 forbids every form of stopping including loading and unloading",False)],
     "Parking = leaving the vehicle unattended. Loading/unloading with the driver present counts as stopping and is not covered by E1.",
     "Pouvez-vous charger/decharger sous E1 si vous restez aupres du vehicule ?",
     [("Oui: le chargement/dechargement avec le conducteur present compte comme un arret, pas un stationnement",True),("Non: E1 interdit toute forme d'arret y compris le chargement",False)],
     "Stationnement = laisser le vehicule sans surveillance. Charger/decharger avec le conducteur present compte comme un arret.",
     "هل يُسمح بتحميل/تفريغ تحت E1 إذا بقيت مع المركبة؟",
     [("نعم: التحميل/التفريغ مع وجود السائق يُعدّ توقفاً لا وقوفاً",True),("لا: E1 تحظر كل أشكال التوقف بما فيها التحميل والتفريغ",False)],
     "الوقوف = ترك المركبة دون مراقبة. التحميل/التفريغ مع وجود السائق يُعدّ توقفاً ولا يشمله حظر E1."),
])

print("E1 done")
