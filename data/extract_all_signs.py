#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
استخراج أوصاف جميع العلامات المرورية من HTML
Extract ALL traffic signs descriptions from HTML file
"""

import json
import re
from pathlib import Path
from bs4 import BeautifulSoup

def extract_from_html():
    """استخراج جميع العلامات من ملف HTML"""
    
    html_file = Path("data/theory_source/Overzicht alle officiële Belgische verkeersborden + betekenis - verkeersbord oefenen.html")
    
    if not html_file.exists():
        print(f"❌ HTML file not found: {html_file}")
        return {}
    
    print(f"📄 Reading HTML file: {html_file.name}")
    
    with open(html_file, 'r', encoding='utf-8') as f:
        html_content = f.read()
    
    soup = BeautifulSoup(html_content, 'html.parser')
    
    # البحث عن جميع عناصر العلامات
    sign_elements = soup.find_all('a', class_='listing-item')
    
    print(f"✅ Found {len(sign_elements)} traffic signs")
    
    signs_data = {}
    category_map = {
        'A': 'danger_signs',
        'B': 'priority_signs',
        'C': 'prohibition_signs',
        'D': 'mandatory_signs',
        'E': 'parking_signs',
        'F': 'information_signs',
        'G': 'additional_panels',
        'M': 'bicycle_signs',
        'T': 'boundary_signs',
        'Z': 'zone_signs'
    }
    
    for element in sign_elements:
        try:
            # استخراج الصورة
            img = element.find('img')
            if not img:
                continue
            
            # استخراج رمز العلامة من alt
            alt_text = img.get('alt', '')
            match = re.search(r'Verkeersbord ([A-Z]\d+[a-z]?)', alt_text)
            if not match:
                continue
            
            sign_code = match.group(1)
            
            # استخراج العنوان/الوصف
            title_elem = element.find('div', class_='listing-item__title')
            if not title_elem:
                continue
            
            title_text = title_elem.get_text(strip=True)
            
            # فصل رمز العلامة عن الوصف
            parts = title_text.split(' ', 1)
            if len(parts) < 2:
                description_nl = title_text
            else:
                description_nl = parts[1]
            
            # تحديد الفئة
            category = sign_code[0]
            
            # استخراج URL الصورة
            img_url = img.get('src', '')
            srcset = img.get('srcset', '')
            
            # الحصول على أعلى دقة
            high_res_url = img_url
            if srcset:
                srcset_match = re.search(r'(https?://[^\s]+)\s+2x', srcset)
                if srcset_match:
                    high_res_url = srcset_match.group(1)
            
            # حفظ البيانات
            signs_data[sign_code] = {
                'sign_code': sign_code,
                'category': category,
                'folder_name': category_map.get(category, 'unknown'),
                'name_nl': description_nl,
                'image_url': high_res_url,
                'image_path': f'assets/traffic_signs/{category_map.get(category, "unknown")}/{sign_code}.png'
            }
            
        except Exception as e:
            print(f"⚠️  Error processing element: {e}")
            continue
    
    return signs_data

def translate_descriptions(signs_data):
    """ترجمة الأوصاف إلى العربية والإنجليزية والفرنسية"""
    
    # قاموس ترجمة أساسي
    translations = {
        # A-serie: Danger signs / علامات الخطر
        "Gevaarlijke bocht naar links": {
            "ar": "منعطف خطر لليسار",
            "en": "Dangerous bend to the left",
            "fr": "Virage dangereux à gauche"
        },
        "Gevaarlijke bocht naar rechts": {
            "ar": "منعطف خطر لليمين",
            "en": "Dangerous bend to the right",
            "fr": "Virage dangereux à droite"
        },
        "Gevaarlijke dubbele of meer dan twee bochten, de eerste naar links": {
            "ar": "منعطفات خطرة، الأول لليسار",
            "en": "Dangerous double or multiple bends, first to the left",
            "fr": "Virages dangereux, le premier à gauche"
        },
        "Gevaarlijke dubbele of meer dan twee bochten, de eerste naar rechts": {
            "ar": "منعطفات خطرة، الأول لليمين",
            "en": "Dangerous double or multiple bends, first to the right",
            "fr": "Virages dangereux, le premier à droite"
        },
        "Gevaarlijke daling": {
            "ar": "انحدار خطر",
            "en": "Dangerous descent",
            "fr": "Descente dangereuse"
        },
        "Gevaarlijke helling": {
            "ar": "صعود خطر",
            "en": "Dangerous ascent",
            "fr": "Montée dangereuse"
        },
        "Rijbaanversmalling": {
            "ar": "تضييق الطريق",
            "en": "Road narrowing",
            "fr": "Rétrécissement de chaussée"
        },
        "Rijbaanversmalling links": {
            "ar": "تضييق الطريق من اليسار",
            "en": "Road narrowing on the left",
            "fr": "Rétrécissement à gauche"
        },
        "Rijbaanversmalling rechts": {
            "ar": "تضييق الطريق من اليمين",
            "en": "Road narrowing on the right",
            "fr": "Rétrécissement à droite"
        },
        "Beweegbare brug": {
            "ar": "جسر متحرك",
            "en": "Movable bridge",
            "fr": "Pont mobile"
        },
        "Uitweg op kaai of oever": {
            "ar": "طريق يؤدي إلى رصيف أو شاطئ",
            "en": "Road leads to quay or waterside",
            "fr": "Route menant au quai ou à la rive"
        },
        "Dwarse uitholling of ezelsrug": {
            "ar": "حفرة عرضية أو مطب",
            "en": "Transverse depression or hump",
            "fr": "Dépression transversale ou dos d'âne"
        },
        "Ongeregelde rijkruising met onverplichte voorrang van rechts": {
            "ar": "تقاطع غير منظم مع أولوية اليمين",
            "en": "Uncontrolled intersection with priority from right",
            "fr": "Intersection non réglementée avec priorité à droite"
        },
        "Oversteekplaats voor voetgangers": {
            "ar": "معبر للمشاة",
            "en": "Pedestrian crossing",
            "fr": "Passage pour piétons"
        },
        "Kinderen": {
            "ar": "أطفال",
            "en": "Children",
            "fr": "Enfants"
        },
        "Fietsers en bromfietsers": {
            "ar": "دراجات ودراجات نارية",
            "en": "Cyclists and moped riders",
            "fr": "Cyclistes et cyclomotoristes"
        },
        "Doorgang van ruiters": {
            "ar": "عبور الفرسان",
            "en": "Horse riders crossing",
            "fr": "Passage de cavaliers"
        },
        "Doorgang van landbouwvoertuigen": {
            "ar": "عبور مركبات زراعية",
            "en": "Agricultural vehicles crossing",
            "fr": "Passage de véhicules agricoles"
        },
        "Doorgang van wilde dieren": {
            "ar": "عبور حيوانات برية",
            "en": "Wild animals crossing",
            "fr": "Passage d'animaux sauvages"
        },
        "Doorgang van vee": {
            "ar": "عبور المواشي",
            "en": "Cattle crossing",
            "fr": "Passage de bétail"
        },
        
        # B-serie: Priority signs / علامات الأولوية
        "Stop": {
            "ar": "قف",
            "en": "Stop",
            "fr": "Stop"
        },
        "Voorrang verlenen": {
            "ar": "أعط الأولوية",
            "en": "Give way",
            "fr": "Cédez le passage"
        },
        "Voorrang op kruisende weg": {
            "ar": "لك الأولوية على الطريق المتقاطع",
            "en": "Priority over crossing road",
            "fr": "Priorité sur la route croisée"
        },
        "Voorrangsweg": {
            "ar": "طريق ذو أولوية",
            "en": "Priority road",
            "fr": "Route prioritaire"
        },
        "Einde voorrangsweg": {
            "ar": "نهاية طريق الأولوية",
            "en": "End of priority road",
            "fr": "Fin de route prioritaire"
        },
        
        # C-serie: Prohibition signs / علامات المنع
        "Verboden toegang": {
            "ar": "ممنوع الدخول",
            "en": "No entry",
            "fr": "Accès interdit"
        },
        "Verboden in te rijden": {
            "ar": "ممنوع الدخول",
            "en": "No entry",
            "fr": "Sens interdit"
        },
        "Verboden richting": {
            "ar": "اتجاه ممنوع",
            "en": "Direction prohibited",
            "fr": "Direction interdite"
        },
        "Verboden voor motorvoertuigen": {
            "ar": "ممنوع للمركبات الآلية",
            "en": "No motor vehicles",
            "fr": "Interdit aux véhicules à moteur"
        },
        "Verboden voor vrachtauto's": {
            "ar": "ممنوع للشاحنات",
            "en": "No trucks",
            "fr": "Interdit aux camions"
        },
        "Verboden voor voertuigen voor goederenvervoer": {
            "ar": "ممنوع لمركبات نقل البضائع",
            "en": "No goods vehicles",
            "fr": "Interdit aux véhicules de transport de marchandises"
        },
        "Verboden voor fietsen": {
            "ar": "ممنوع للدراجات",
            "en": "No bicycles",
            "fr": "Interdit aux vélos"
        },
        "Verboden voor voetgangers": {
            "ar": "ممنوع للمشاة",
            "en": "No pedestrians",
            "fr": "Interdit aux piétons"
        },
        "Inhaalverbod": {
            "ar": "ممنوع التجاوز",
            "en": "No overtaking",
            "fr": "Interdiction de dépasser"
        },
        "Maximumsnelheid": {
            "ar": "السرعة القصوى",
            "en": "Maximum speed",
            "fr": "Vitesse maximale"
        },
        "Verboden te parkeren": {
            "ar": "ممنوع الانتظار",
            "en": "No parking",
            "fr": "Stationnement interdit"
        },
        "Stil staan en parkeren verboden": {
            "ar": "ممنوع التوقف والانتظار",
            "en": "No stopping or parking",
            "fr": "Arrêt et stationnement interdits"
        },
        
        # D-serie: Mandatory signs / علامات الإلزام
        "Verplichte rijrichting rechtdoor": {
            "ar": "اتجاه إلزامي - مستقيم",
            "en": "Mandatory direction - straight ahead",
            "fr": "Direction obligatoire - tout droit"
        },
        "Verplichte rijrichting rechts": {
            "ar": "اتجاه إلزامي - يمين",
            "en": "Mandatory direction - right",
            "fr": "Direction obligatoire - droite"
        },
        "Verplichte rijrichting links": {
            "ar": "اتجاه إلزامي - يسار",
            "en": "Mandatory direction - left",
            "fr": "Direction obligatoire - gauche"
        },
        "Rotonde": {
            "ar": "دوار إلزامي",
            "en": "Roundabout",
            "fr": "Rond-point obligatoire"
        },
        "Verplicht fietspad": {
            "ar": "ممر دراجات إلزامي",
            "en": "Compulsory cycle path",
            "fr": "Piste cyclable obligatoire"
        },
        "Verplicht voetpad": {
            "ar": "ممر مشاة إلزامي",
            "en": "Compulsory footpath",
            "fr": "Chemin piétonnier obligatoire"
        },
        "Minimumsnelheid": {
            "ar": "السرعة الدنيا",
            "en": "Minimum speed",
            "fr": "Vitesse minimale"
        },
        
        # E-serie: Parking signs / علامات الوقوف
        "Stilstaan en parkeren verboden": {
            "ar": "ممنوع التوقف والانتظار",
            "en": "No stopping or parking",
            "fr": "Arrêt et stationnement interdits"
        },
        "Parkeren verboden": {
            "ar": "ممنوع الانتظار",
            "en": "No parking",
            "fr": "Stationnement interdit"
        },
        "Parkeerverbod": {
            "ar": "ممنوع الانتظار",
            "en": "Parking prohibited",
            "fr": "Interdiction de stationner"
        },
        "Parkeerzone": {
            "ar": "منطقة انتظار",
            "en": "Parking zone",
            "fr": "Zone de stationnement"
        },
        
        # F-serie: Information signs / علامات إرشادية
        "Autosnelweg": {
            "ar": "طريق سريع",
            "en": "Motorway",
            "fr": "Autoroute"
        },
        "Einde autosnelweg": {
            "ar": "نهاية الطريق السريع",
            "en": "End of motorway",
            "fr": "Fin d'autoroute"
        },
        "Autoweg": {
            "ar": "طريق سيارات",
            "en": "Expressway",
            "fr": "Route pour automobiles"
        },
        "Einde autoweg": {
            "ar": "نهاية طريق السيارات",
            "en": "End of expressway",
            "fr": "Fin de route pour automobiles"
        },
        "Tunnel": {
            "ar": "نفق",
            "en": "Tunnel",
            "fr": "Tunnel"
        },
        "Voetgangersoversteekplaats": {
            "ar": "معبر المشاة",
            "en": "Pedestrian crossing",
            "fr": "Passage pour piétons"
        },
        "Parkeren toegestaan": {
            "ar": "يسمح بالانتظار",
            "en": "Parking permitted",
            "fr": "Stationnement autorisé"
        },
        "Bushalte": {
            "ar": "محطة حافلات",
            "en": "Bus stop",
            "fr": "Arrêt de bus"
        },
        "Tramhalte": {
            "ar": "محطة ترام",
            "en": "Tram stop",
            "fr": "Arrêt de tram"
        },
        "Eenrichtingsweg": {
            "ar": "اتجاه واحد",
            "en": "One-way street",
            "fr": "Sens unique"
        },
        "Fietsstraat": {
            "ar": "شارع الدراجات",
            "en": "Cycle street",
            "fr": "Rue cyclable"
        },
        "Woonerf": {
            "ar": "منطقة سكنية",
            "en": "Residential zone",
            "fr": "Zone résidentielle"
        },
        "Zone 30": {
            "ar": "منطقة 30",
            "en": "Zone 30",
            "fr": "Zone 30"
        }
    }
    
    for sign_code, data in signs_data.items():
        name_nl = data.get('name_nl', '')
        
        # البحث عن ترجمة مطابقة
        found = False
        for nl_text, trans in translations.items():
            if nl_text.lower() in name_nl.lower():
                data['name_ar'] = trans['ar']
                data['name_en'] = trans['en']
                data['name_fr'] = trans['fr']
                found = True
                break
        
        # إذا لم نجد ترجمة، استخدم ترجمة عامة
        if not found:
            data['name_ar'] = f"علامة {sign_code}"
            data['name_en'] = name_nl  # استخدم النص الهولندي كإنجليزي مؤقت
            data['name_fr'] = name_nl
    
    return signs_data

def main():
    print("=" * 70)
    print("🚦 استخراج أوصاف جميع العلامات المرورية من HTML")
    print("   Extract ALL Traffic Signs Descriptions from HTML")
    print("=" * 70)
    
    # استخراج البيانات من HTML
    signs_data = extract_from_html()
    
    if not signs_data:
        print("❌ No data extracted!")
        return
    
    # ترجمة الأوصاف
    print(f"\n🌐 Translating descriptions...")
    signs_data = translate_descriptions(signs_data)
    
    # حفظ البيانات
    output_file = Path("data/traffic_signs_complete.json")
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(signs_data, f, ensure_ascii=False, indent=2)
    
    print(f"\n✅ Saved {len(signs_data)} complete sign descriptions to: {output_file}")
    
    # إحصائيات
    print(f"\n📈 Statistics by category:")
    categories = {}
    for data in signs_data.values():
        cat = data.get('category', 'Unknown')
        categories[cat] = categories.get(cat, 0) + 1
    
    category_names = {
        'A': 'Danger / علامات الخطر',
        'B': 'Priority / علامات الأولوية',
        'C': 'Prohibition / علامات المنع',
        'D': 'Mandatory / علامات الإلزام',
        'E': 'Parking / علامات الوقوف',
        'F': 'Information / علامات إرشادية',
        'G': 'Additional / لوحات إضافية',
        'M': 'Bicycle / لوحات الدراجات',
        'T': 'Boundary / علامات التحديد',
        'Z': 'Zone / علامات المناطق'
    }
    
    for cat in sorted(categories.keys()):
        cat_name = category_names.get(cat, 'Unknown')
        print(f"   {cat} ({cat_name}): {categories[cat]} signs")
    
    print(f"\n🎉 Done! Total: {len(signs_data)} traffic signs extracted")

if __name__ == "__main__":
    main()
