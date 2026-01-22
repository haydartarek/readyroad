#!/usr/bin/env python3
"""
Fix incorrect translations for traffic signs
إصلاح الترجمات الخاطئة للإشارات المرورية
"""

import json
from pathlib import Path

# Translation mappings based on Dutch text patterns
TRANSLATION_PATTERNS = {
    # Direction signs
    'Verplichting rechtdoor': {
        'ar': 'إلزامي المضي للأمام مباشرة',
        'en': 'Compulsory straight ahead',
        'fr': 'Direction obligatoire tout droit'
    },
    'Verplichting rechts afslaan': {
        'ar': 'إلزامي الانعطاف يميناً',
        'en': 'Compulsory turn right',
        'fr': 'Direction obligatoire à droite'
    },
    'Verplichting links aanhouden': {
        'ar': 'إلزامي الاستمرار يساراً',
        'en': 'Keep left compulsory',
        'fr': 'Direction obligatoire à gauche'
    },
    'Verplichting rechts aanhouden': {
        'ar': 'إلزامي الاستمرار يميناً',
        'en': 'Keep right compulsory',
        'fr': 'Obligation de tenir la droite'
    },
    'Verplicht de aangeduide richting te volgen (linksaf)': {
        'ar': 'إلزامي اتباع الاتجاه المحدد (يساراً)',
        'en': 'Compulsory direction to follow (left)',
        'fr': 'Direction obligatoire (à gauche)'
    },
    'Verplicht de aangeduide richting te volgen (rechtsaf)': {
        'ar': 'إلزامي اتباع الاتجاه المحدد (يميناً)',
        'en': 'Compulsory direction to follow (right)',
        'fr': 'Direction obligatoire (à droite)'
    },
    'Verplicht één van de pijlen te volgen': {
        'ar': 'إلزامي اتباع أحد الأسهم',
        'en': 'Compulsory to follow one of the arrows',
        'fr': 'Obligation de suivre une des flèches'
    },
    'Verplicht rondgaand verkeer': {
        'ar': 'دوار إلزامي',
        'en': 'Roundabout compulsory',
        'fr': 'Giratoire obligatoire'
    },
    # Parking signs
    'Einde van de snelheidsbeperking opgelegd door het verkeersbord C43': {
        'ar': 'نهاية حد السرعة المفروض بواسطة العلامة C43',
        'en': 'End of speed limit imposed by sign C43',
        'fr': 'Fin de limitation de vitesse imposée par le panneau C43'
    },
    'parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht': {
        'ar': 'وقوف محدود بالوقت، قرص الوقوف إلزامي',
        'en': 'Parking limited in time, parking disc required',
        'fr': 'Stationnement limité dans le temps, disque obligatoire'
    },
    'Parkeren uitsluitend voor auto\'s': {
        'ar': 'وقوف للسيارات فقط',
        'en': 'Parking for cars only',
        'fr': 'Stationnement réservé aux voitures'
    },
    'Parkeren uitsluitend voorvrachtwagens': {
        'ar': 'وقوف للشاحنات فقط',
        'en': 'Parking for trucks only',
        'fr': 'Stationnement réservé aux camions'
    },
    'Parkeren uitsluitend voor autocars': {
        'ar': 'وقوف للحافلات فقط',
        'en': 'Parking for buses only',
        'fr': 'Stationnement réservé aux autocars'
    },
    'Parkeren uitsluitend voor kampeerauto\'s': {
        'ar': 'وقوف للكرفانات فقط',
        'en': 'Parking for motorhomes only',
        'fr': 'Stationnement réservé aux camping-cars'
    },
    'Parkeren uitsluitend voor motorfietsen': {
        'ar': 'وقوف للدراجات النارية فقط',
        'en': 'Parking for motorcycles only',
        'fr': 'Stationnement réservé aux motos'
    },
    'Verplicht parkeren op de berm of op het trottoir': {
        'ar': 'إلزامي الوقوف على الرصيف أو الحافة',
        'en': 'Compulsory parking on verge or pavement',
        'fr': 'Stationnement obligatoire sur accotement ou trottoir'
    },
    'Verplicht parkeren deels op de berm of op het trottoir': {
        'ar': 'إلزامي الوقوف جزئياً على الرصيف',
        'en': 'Compulsory parking partly on pavement',
        'fr': 'Stationnement partiellement sur trottoir'
    },
    'Verplicht parkeren op de rijbaan': {
        'ar': 'إلزامي الوقوف على الطريق',
        'en': 'Compulsory parking on carriageway',
        'fr': 'Stationnement obligatoire sur chaussée'
    },
    'Halfmaandelijks parkeren in gans de bebouwde kom': {
        'ar': 'وقوف نصف شهري في المنطقة المبنية',
        'en': 'Fortnightly parking in built-up area',
        'fr': 'Stationnement bimensuel en agglomération'
    },
    # Information signs
    'Begin van een bebouwde kom': {
        'ar': 'بداية منطقة مبنية',
        'en': 'Start of built-up area',
        'fr': 'Début d\'agglomération'
    },
    'Einde van een bebouwde kom': {
        'ar': 'نهاية منطقة مبنية',
        'en': 'End of built-up area',
        'fr': 'Fin d\'agglomération'
    },
    'Opstelvak voor fietsers en bromfietsen': {
        'ar': 'منطقة انتظار للدراجات والدراجات النارية الصغيرة',
        'en': 'Waiting area for cyclists and mopeds',
        'fr': 'Zone d\'attente pour cyclistes et cyclomoteurs'
    },
    'Rijstrook aanduiding voorbehouden voor autobussen': {
        'ar': 'مسار محجوز للحافلات',
        'en': 'Lane reserved for buses',
        'fr': 'Voie réservée aux bus'
    },
    'Bijzondere overrijdbare bedding': {
        'ar': 'قاع طريق خاص قابل للعبور',
        'en': 'Special overrideable road surface',
        'fr': 'Lit de circulation spécial franchissable'
    },
    'Rechts of links voorbijrijden toegelaten': {
        'ar': 'مسموح التجاوز من اليمين أو اليسار',
        'en': 'Overtaking allowed on right or left',
        'fr': 'Dépassement autorisé à droite ou à gauche'
    },
    'Nummer van een gewone weg': {
        'ar': 'رقم طريق عادي',
        'en': 'Number of ordinary road',
        'fr': 'Numéro de route ordinaire'
    },
    'Nummer van een internationale weg': {
        'ar': 'رقم طريق دولي',
        'en': 'International road number',
        'fr': 'Numéro de route internationale'
    },
    'Nummer van een ringweg': {
        'ar': 'رقم طريق دائري',
        'en': 'Ring road number',
        'fr': 'Numéro de rocade'
    },
    'Bewegwijzeringsbord op afstand': {
        'ar': 'لوحة إرشادية عن بعد',
        'en': 'Advance direction sign',
        'fr': 'Panneau de direction à distance'
    },
    'Nabijheid van inrichting die van openbaar of algemeen belang is': {
        'ar': 'قرب مرفق ذو مصلحة عامة',
        'en': 'Proximity to public facility',
        'fr': 'Proximité d\'installation d\'intérêt public'
    },
    'Aanbevolen reisweg voor bepaalde weggebruikers': {
        'ar': 'طريق موصى به لمستخدمي الطريق المحددين',
        'en': 'Recommended route for specific road users',
        'fr': 'Itinéraire recommandé pour certains usagers'
    },
    'Plaats voor toerisme of ontspanning': {
        'ar': 'مكان للسياحة أو الاستجمام',
        'en': 'Place for tourism or recreation',
        'fr': 'Lieu de tourisme ou de détente'
    },
    'Wegwijzer naar hotels, campings, restaurant': {
        'ar': 'لافتة إلى الفنادق والمخيمات والمطاعم',
        'en': 'Sign to hotels, campsites, restaurant',
        'fr': 'Indication vers hôtels, campings, restaurant'
    },
    'Aankondiging van een omleiding': {
        'ar': 'إعلان عن تحويلة',
        'en': 'Announcement of diversion',
        'fr': 'Annonce de déviation'
    },
    'Wegwijzer omleidingsweg': {
        'ar': 'لافتة طريق التحويلة',
        'en': 'Diversion route sign',
        'fr': 'Panneau d\'itinéraire de déviation'
    },
    'Doodlopende weg, rechtse doorgang': {
        'ar': 'طريق مسدود، ممر أيمن',
        'en': 'Dead end, right passage',
        'fr': 'Impasse, passage à droite'
    },
    'Doodlopende weg, uitgezonderd voetgangers en fietsers': {
        'ar': 'طريق مسدود، باستثناء المشاة والدراجات',
        'en': 'Dead end, except pedestrians and cyclists',
        'fr': 'Impasse, sauf piétons et cyclistes'
    },
    'Verplegingsinrichting': {
        'ar': 'مرفق طعام',
        'en': 'Catering facility',
        'fr': 'Établissement de restauration'
    },
    'Aankondiging van een parking': {
        'ar': 'إعلان عن موقف سيارات',
        'en': 'Parking announcement',
        'fr': 'Annonce de parking'
    },
    'Aankondiging van een fietsparking': {
        'ar': 'إعلان عن موقف دراجات',
        'en': 'Bicycle parking announcement',
        'fr': 'Annonce de parking vélos'
    },
    'Tankstation met een specifieke brandstof': {
        'ar': 'محطة وقود بوقود محدد',
        'en': 'Gas station with specific fuel',
        'fr': 'Station-service avec carburant spécifique'
    },
    'Toeristische informatie': {
        'ar': 'معلومات سياحية',
        'en': 'Tourist information',
        'fr': 'Information touristique'
    },
    'Verhoogde inrichting (vluchtheuvel)': {
        'ar': 'منشأة مرتفعة (جزيرة مرورية)',
        'en': 'Raised facility (refuge island)',
        'fr': 'Aménagement surélevé (îlot refuge)'
    },
    'Rijstrook versmalling': {
        'ar': 'تضييق المسار',
        'en': 'Lane narrowing',
        'fr': 'Rétrécissement de voie'
    },
    # Pedestrian/cyclist zones
    'Voorbehouden voor het verkeer van voetgangers, fietsers, ruiters': {
        'ar': 'محجوز للمشاة والدراجات والفرسان',
        'en': 'Reserved for pedestrians, cyclists, horse riders',
        'fr': 'Réservé aux piétons, cyclistes, cavaliers'
    },
    'Deel van de openbare weg voorbehouden voor fietsers en voetgangers': {
        'ar': 'جزء من الطريق العام محجوز للدراجات والمشاة',
        'en': 'Part of public road reserved for cyclists and pedestrians',
        'fr': 'Partie de voie publique réservée aux cyclistes et piétons'
    },
    'Voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers': {
        'ar': 'محجوز للمركبات الزراعية والمشاة',
        'en': 'Reserved for agricultural vehicles and pedestrians',
        'fr': 'Réservé aux véhicules agricoles et piétons'
    },
    'Einde voorbehouden voor het verkeer van voetgangers, fietsers, ruiters': {
        'ar': 'نهاية المنطقة المحجوزة للمشاة والدراجات والفرسان',
        'en': 'End of area reserved for pedestrians, cyclists, riders',
        'fr': 'Fin de zone réservée aux piétons, cyclistes, cavaliers'
    },
    'Einde deel van de openbare weg voorbehouden voor fietsers en voetgange': {
        'ar': 'نهاية جزء الطريق المحجوز للدراجات والمشاة',
        'en': 'End of part reserved for cyclists and pedestrians',
        'fr': 'Fin de partie réservée aux cyclistes et piétons'
    },
    'Einde voorbehouden voor het verkeer van landbouwvoertuigen, voetganger': {
        'ar': 'نهاية المنطقة المحجوزة للمركبات الزراعية',
        'en': 'End of area reserved for agricultural vehicles',
        'fr': 'Fin de zone réservée aux véhicules agricoles'
    },
    'Begin van een voetgangerszone': {
        'ar': 'بداية منطقة المشاة',
        'en': 'Start of pedestrian zone',
        'fr': 'Début de zone piétonne'
    },
    'Einde van een voetgangerszone': {
        'ar': 'نهاية منطقة المشاة',
        'en': 'End of pedestrian zone',
        'fr': 'Fin de zone piétonne'
    },
    'Begin van een lage emissiezone': {
        'ar': 'بداية منطقة انبعاثات منخفضة',
        'en': 'Start of low emission zone',
        'fr': 'Début de zone à faibles émissions'
    },
    'Einde van een lage emissiezone': {
        'ar': 'نهاية منطقة انبعاثات منخفضة',
        'en': 'End of low emission zone',
        'fr': 'Fin de zone à faibles émissions'
    },
    # Bicycle signs
    'Uitgezonderd fietsers': {
        'ar': 'باستثناء الدراجات',
        'en': 'Except cyclists',
        'fr': 'Sauf cyclistes'
    },
    'Fietsers mogen in 2 richtingen': {
        'ar': 'يسمح للدراجات بالسير في اتجاهين',
        'en': 'Cyclists may go in 2 directions',
        'fr': 'Cyclistes autorisés dans les 2 sens'
    },
    'fietsers, bromfietsers klasse A, B en speed pedelecs mogen in 2 richti': {
        'ar': 'الدراجات ودراجات الموبيد يسمح لها السير باتجاهين',
        'en': 'Cyclists and mopeds may go in 2 directions',
        'fr': 'Cyclistes et cyclomoteurs dans les 2 sens'
    },
    'Verplichting voor bromfietsen klasse B': {
        'ar': 'إلزامي للدراجات النارية الصغيرة فئة B',
        'en': 'Compulsory for class B mopeds',
        'fr': 'Obligatoire pour cyclomoteurs classe B'
    },
    'Verbod voor bromfietsen klasse B': {
        'ar': 'ممنوع للدراجات النارية الصغيرة فئة B',
        'en': 'Prohibited for class B mopeds',
        'fr': 'Interdit aux cyclomoteurs classe B'
    },
    'Fietsers in twee richtingen op de dwarslopende weg die je gaat oprijde': {
        'ar': 'دراجات في اتجاهين على الطريق المتقاطع',
        'en': 'Cyclists in two directions on crossing road',
        'fr': 'Cyclistes dans deux sens sur route transversale'
    },
    'Fietsers en bromfietser in twee richtingen op de dwarslopende weg die': {
        'ar': 'دراجات ودراجات نارية صغيرة في اتجاهين',
        'en': 'Cyclists and mopeds in two directions',
        'fr': 'Cyclistes et cyclomoteurs dans deux sens'
    },
    'Uitgezonderd fietsers en speed pedelecs': {
        'ar': 'باستثناء الدراجات والدراجات الكهربائية السريعة',
        'en': 'Except cyclists and speed pedelecs',
        'fr': 'Sauf cyclistes et speed pedelecs'
    },
    'Uitgezonderd fietsers, bromfietsers klasse A en speed pedelecs': {
        'ar': 'باستثناء الدراجات والدراجات النارية الصغيرة',
        'en': 'Except cyclists, mopeds class A and speed pedelecs',
        'fr': 'Sauf cyclistes, cyclomoteurs classe A et speed pedelecs'
    },
    'Verplichting voor speed pedelecs': {
        'ar': 'إلزامي للدراجات الكهربائية السريعة',
        'en': 'Compulsory for speed pedelecs',
        'fr': 'Obligatoire pour speed pedelecs'
    },
    'Verplichting voor bromfietsen klasse B en Speed pedelecs': {
        'ar': 'إلزامي للدراجات النارية الصغيرة فئة B والدراجات السريعة',
        'en': 'Compulsory for class B mopeds and speed pedelecs',
        'fr': 'Obligatoire pour cyclomoteurs classe B et speed pedelecs'
    },
    'Verbod voor speed pedelecs': {
        'ar': 'ممنوع للدراجات الكهربائية السريعة',
        'en': 'Prohibited for speed pedelecs',
        'fr': 'Interdit aux speed pedelecs'
    },
    'Verbod voor bromfietsen klasse B en speed pedelecs': {
        'ar': 'ممنوع للدراجات النارية الصغيرة فئة B والدراجات السريعة',
        'en': 'Prohibited for class B mopeds and speed pedelecs',
        'fr': 'Interdit aux cyclomoteurs classe B et speed pedelecs'
    },
    'Fietsers en speed pedelecs mogen in 2 richtingen': {
        'ar': 'الدراجات والدراجات السريعة مسموح لها باتجاهين',
        'en': 'Cyclists and speed pedelecs may go in 2 directions',
        'fr': 'Cyclistes et speed pedelecs dans les 2 sens'
    },
    'Fietsers, bromfietsen klasse A en speed pedelecs mogen in 2 richtingen': {
        'ar': 'الدراجات والدراجات النارية مسموح لها باتجاهين',
        'en': 'Cyclists, mopeds and speed pedelecs in 2 directions',
        'fr': 'Cyclistes, cyclomoteurs et speed pedelecs dans 2 sens'
    },
    'Enkel voor speed pedelecs': {
        'ar': 'للدراجات الكهربائية السريعة فقط',
        'en': 'Only for speed pedelecs',
        'fr': 'Uniquement pour speed pedelecs'
    },
    'Enkel voor fietsers en speed pedelecs': {
        'ar': 'للدراجات والدراجات السريعة فقط',
        'en': 'Only for cyclists and speed pedelecs',
        'fr': 'Uniquement pour cyclistes et speed pedelecs'
    },
    # Mixed/path signs
    'Deel van de weg voorbehouden voor voetgangers en fietsers': {
        'ar': 'جزء من الطريق محجوز للمشاة والدراجات',
        'en': 'Part of road reserved for pedestrians and cyclists',
        'fr': 'Partie de route réservée aux piétons et cyclistes'
    },
    'Verplichte weg voor voetgangers': {
        'ar': 'طريق إلزامي للمشاة',
        'en': 'Compulsory path for pedestrians',
        'fr': 'Chemin obligatoire pour piétons'
    },
    'Verplichte weg voor ruiters': {
        'ar': 'طريق إلزامي للفرسان',
        'en': 'Compulsory path for horse riders',
        'fr': 'Chemin obligatoire pour cavaliers'
    },
    'Verplicht rechts voor voertuigen die gevaarlijke goederen vervoeren': {
        'ar': 'إلزامي لليمين للمركبات التي تنقل بضائع خطرة',
        'en': 'Compulsory right for vehicles carrying dangerous goods',
        'fr': 'Obligatoire à droite pour véhicules transportant marchandises dangereuses'
    },
    # Misc
    'Opgepast als je van richting veranderd, fietsers': {
        'ar': 'انتبه عند تغيير الاتجاه، دراجات',
        'en': 'Caution when changing direction, cyclists',
        'fr': 'Attention en changeant de direction, cyclistes'
    },
    'wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fiet': {
        'ar': 'وقوف متناوب للدراجات',
        'en': 'Alternating parking for bicycles',
        'fr': 'Stationnement alterné pour vélos'
    }
}

def find_best_translation(nl_text):
    """Find best matching translation for Dutch text."""
    nl_text = nl_text.strip()
    
    # Exact match
    if nl_text in TRANSLATION_PATTERNS:
        return TRANSLATION_PATTERNS[nl_text]
    
    # Partial match (for truncated texts)
    for pattern, translation in TRANSLATION_PATTERNS.items():
        if nl_text in pattern or pattern in nl_text:
            return translation
    
    # Default generic translation
    return {
        'ar': f'إشارة مرور',
        'en': 'Traffic sign',
        'fr': 'Panneau de signalisation'
    }

def main():
    print("🔧 Fixing incorrect translations...")
    print("=" * 60)
    
    # Load signs
    signs_file = Path(__file__).parent / "traffic_signs_complete.json"
    with open(signs_file, 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    fixed_count = 0
    updates = []
    
    for sign_code, sign in data.items():
        nl_text = sign.get('name_nl', '').strip()
        en_text = sign.get('name_en', '').strip()
        fr_text = sign.get('name_fr', '').strip()
        ar_text = sign.get('name_ar', '').strip()
        
        # Check if translation is incorrect
        needs_fix = False
        if nl_text and len(nl_text) > 20:
            if en_text == nl_text or fr_text == nl_text:
                needs_fix = True
            elif 'Einde' in ar_text or 'Verplicht' in ar_text:
                needs_fix = True
        
        if needs_fix:
            translation = find_best_translation(nl_text)
            updates.append({
                'sign_code': sign_code,
                'old': {
                    'ar': ar_text,
                    'en': en_text,
                    'fr': fr_text
                },
                'new': translation
            })
            
            # Update in data
            sign['name_ar'] = translation['ar']
            sign['name_en'] = translation['en']
            sign['name_fr'] = translation['fr']
            
            fixed_count += 1
    
    print(f"\n✅ Fixed {fixed_count} signs")
    
    # Save updated JSON
    with open(signs_file, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)
    
    print(f"📄 Updated: {signs_file}")
    
    # Generate SQL migration
    output_file = Path(__file__).parent.parent / "src" / "main" / "resources" / "db" / "migration" / "V9__Fix_Traffic_Sign_Translations.sql"
    
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write("-- V9__Fix_Traffic_Sign_Translations.sql\n")
        f.write("-- إصلاح الترجمات الخاطئة للإشارات المرورية\n")
        f.write("-- Fix incorrect translations for traffic signs\n\n")
        
        for update in updates:
            ar_text = update['new']['ar'].replace("'", "''")
            en_text = update['new']['en'].replace("'", "''")
            fr_text = update['new']['fr'].replace("'", "''")
            
            f.write(f"-- Fix {update['sign_code']}\n")
            f.write("UPDATE traffic_signs SET\n")
            f.write(f"  name_ar = '{ar_text}',\n")
            f.write(f"  name_en = '{en_text}',\n")
            f.write(f"  name_fr = '{fr_text}'\n")
            f.write(f"WHERE sign_code = '{update['sign_code']}';\n\n")
    
    print(f"📄 Generated migration: {output_file}")
    print(f"📏 Total updates: {len(updates)}")
    
    # Show sample
    print("\n📊 Sample corrections (first 5):")
    for update in updates[:5]:
        print(f"\n  {update['sign_code']}:")
        print(f"    AR: {update['old']['ar'][:40]} → {update['new']['ar'][:40]}")
        print(f"    EN: {update['old']['en'][:40]} → {update['new']['en'][:40]}")

if __name__ == '__main__':
    main()
