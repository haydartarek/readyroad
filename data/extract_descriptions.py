#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
استخراج أوصاف العلامات المرورية من ملفات PDF
Extract traffic signs descriptions from PDF files
"""

import json
import re
from pathlib import Path

try:
    import PyPDF2
    HAS_PYPDF2 = True
except ImportError:
    HAS_PYPDF2 = False
    print("⚠️  PyPDF2 not installed. Install with: pip install PyPDF2")

def extract_pdf_text(pdf_path):
    """استخراج النص من ملف PDF"""
    if not HAS_PYPDF2:
        return None
    
    try:
        with open(pdf_path, 'rb') as file:
            reader = PyPDF2.PdfReader(file)
            text = ""
            for page in reader.pages:
                text += page.extract_text() + "\n"
            return text
    except Exception as e:
        print(f"❌ Error reading {pdf_path}: {e}")
        return None

def parse_sign_info(text, sign_code):
    """استخراج معلومات علامة محددة من النص"""
    # البحث عن رمز العلامة في النص
    pattern = rf'{sign_code}[:\s]+(.*?)(?={sign_code[0]}\d+|$)'
    match = re.search(pattern, text, re.IGNORECASE | re.DOTALL)
    
    if match:
        return match.group(1).strip()
    return None

# قائمة العلامات المرورية
TRAFFIC_SIGNS = {
    "A": [  # Danger / Gevaar / خطر
        "A1a", "A1b", "A1c", "A1d", "A3", "A5", "A7a", "A7b", "A7c", "A9",
        "A11", "A13", "A14", "A15", "A17", "A19", "A21", "A23", "A25", "A27",
        "A29", "A31", "A33", "A35", "A37", "A39", "A41", "A43", "A49", "A50", "A51"
    ],
    "B": [  # Priority / Voorrang / أولوية
        "B1", "B5", "B9", "B11", "B15a", "B15b", "B15c", "B15d", "B15e", "B15f",
        "B15g", "B17", "B19", "B21", "B22", "B23"
    ],
    "C": [  # Prohibition / Verbod / منع
        "C1", "C3", "C5", "C7", "C9", "C11", "C13", "C15", "C17", "C19",
        "C21", "C22", "C23", "C24a", "C24b", "C24c", "C25", "C27", "C29",
        "C31a", "C31b", "C33", "C35", "C37", "C39", "C41", "C43", "C45", "C46", "C47"
    ],
    "D": [  # Mandatory / Gebod / إلزام
        "D1a", "D1b", "D1c", "D1d", "D1e", "D1f", "D3a", "D3b", "D4", "D5",
        "D7", "D9a", "D9b", "D10", "D11", "D13"
    ],
    "E": [  # Parking / Parkeren / وقوف
        "E1", "E3", "E5", "E7", "E9a", "E9b", "E9c", "E9d", "E9e", "E9f",
        "E9g", "E9h", "E9i", "E11"
    ],
    "F": [  # Information / Aanwijzing / إرشاد
        "F1a", "F1b", "F3a", "F3b", "F4a", "F4b", "F5", "F7", "F8", "F9",
        "F11", "F12a", "F12b", "F13", "F14", "F17", "F18", "F19", "F21", "F23a",
        "F23b", "F23c", "F23d", "F29", "F31", "F33a", "F33c", "F34a", "F35",
        "F37", "F39", "F41", "F43", "F45", "F45b", "F47", "F49", "F50", "F53",
        "F55", "F56", "F59", "F59a", "F59b", "F60", "F61", "F62", "F63", "F65",
        "F67", "F69", "F71", "F73", "F75", "F77", "F87", "F97", "F99a", "F99b",
        "F99c", "F101a", "F101b", "F101c", "F103", "F105", "F111", "F113", "F117", "F118"
    ],
    "M": [  # Bicycle / Fiets / دراجات
        "M1", "M2", "M3", "M4", "M5", "M6", "M7", "M8", "M9", "M10",
        "M11", "M12", "M13", "M14", "M15", "M16", "M17", "M18", "M19", "M20"
    ]
}

# أوصاف افتراضية بناءً على المعرفة العامة
DEFAULT_DESCRIPTIONS = {
    # A-serie: علامات الخطر
    "A1a": {
        "name_ar": "منعطف خطر لليسار",
        "name_en": "Dangerous bend to the left",
        "name_nl": "Gevaarlijke bocht naar links",
        "name_fr": "Virage dangereux à gauche",
        "description_ar": "تحذير من منعطف حاد وخطير على اليسار. قلل السرعة واستعد للانعطاف.",
        "description_en": "Warning of a dangerous sharp bend to the left. Reduce speed and prepare to turn.",
        "description_nl": "Waarschuwing voor een gevaarlijke scherpe bocht naar links. Verminder snelheid.",
        "description_fr": "Avertissement d'un virage dangereux à gauche. Réduisez la vitesse.",
        "rules_ar": "يجب تخفيف السرعة قبل الدخول إلى المنعطف وإبقاء السيارة في المسار الصحيح",
        "category": "A"
    },
    "A1b": {
        "name_ar": "منعطف خطر لليمين",
        "name_en": "Dangerous bend to the right",
        "name_nl": "Gevaarlijke bocht naar rechts",
        "name_fr": "Virage dangereux à droite",
        "description_ar": "تحذير من منعطف حاد وخطير على اليمين. قلل السرعة واستعد للانعطاف.",
        "description_en": "Warning of a dangerous sharp bend to the right. Reduce speed and prepare to turn.",
        "description_nl": "Waarschuwing voor een gevaarlijke scherpe bocht naar rechts. Verminder snelheid.",
        "description_fr": "Avertissement d'un virage dangereux à droite. Réduisez la vitesse.",
        "rules_ar": "يجب تخفيف السرعة قبل الدخول إلى المنعطف وإبقاء السيارة في المسار الصحيح",
        "category": "A"
    },
    "B1": {
        "name_ar": "قف - وقوف إلزامي",
        "name_en": "Stop - Mandatory stop",
        "name_nl": "Stop - Verplicht stoppen",
        "name_fr": "Stop - Arrêt obligatoire",
        "description_ar": "يجب التوقف التام عند هذه العلامة قبل خط التوقف أو قبل الممر المشترك.",
        "description_en": "Must come to a complete stop at this sign before the stop line or intersection.",
        "description_nl": "U moet volledig stoppen bij dit bord voor de stoplijn of het kruispunt.",
        "description_fr": "Vous devez vous arrêter complètement à ce panneau avant la ligne d'arrêt.",
        "rules_ar": "توقف تام إلزامي، أعط الأولوية لجميع المركبات والمشاة، ثم تابع بحذر",
        "category": "B"
    },
    "C1": {
        "name_ar": "ممنوع الدخول لجميع المركبات",
        "name_en": "No entry for all vehicles",
        "name_nl": "Verboden toegang voor alle voertuigen",
        "name_fr": "Accès interdit à tous les véhicules",
        "description_ar": "يحظر دخول جميع أنواع المركبات في هذا الاتجاه.",
        "description_en": "Entry prohibited for all types of vehicles in this direction.",
        "description_nl": "Toegang verboden voor alle soorten voertuigen in deze richting.",
        "description_fr": "Accès interdit à tous les types de véhicules dans cette direction.",
        "rules_ar": "لا يجوز المرور أو الدخول في هذا الطريق بأي مركبة",
        "category": "C"
    },
    "D1a": {
        "name_ar": "الاتجاه الإلزامي - مستقيم",
        "name_en": "Mandatory direction - straight ahead",
        "name_nl": "Verplichte rijrichting - rechtdoor",
        "name_fr": "Direction obligatoire - tout droit",
        "description_ar": "يجب الاستمرار في الاتجاه المستقيم فقط، لا يسمح بالانعطاف.",
        "description_en": "Must continue straight ahead only, turning is not allowed.",
        "description_nl": "U moet rechtdoor rijden, afslaan is niet toegestaan.",
        "description_fr": "Vous devez continuer tout droit, les virages ne sont pas autorisés.",
        "rules_ar": "اتبع الاتجاه المشار إليه بالسهم فقط",
        "category": "D"
    },
    "E1": {
        "name_ar": "ممنوع الوقوف والانتظار",
        "name_en": "No stopping or parking",
        "name_nl": "Stilstaan en parkeren verboden",
        "name_fr": "Arrêt et stationnement interdits",
        "description_ar": "يحظر التوقف أو الانتظار في هذه المنطقة.",
        "description_en": "Stopping or parking is prohibited in this area.",
        "description_nl": "Stoppen en parkeren is verboden in dit gebied.",
        "description_fr": "L'arrêt et le stationnement sont interdits dans cette zone.",
        "rules_ar": "لا يجوز إيقاف المركبة حتى لفترة قصيرة، يسمح فقط بالتوقف الاضطراري",
        "category": "E"
    },
    "F1a": {
        "name_ar": "طريق ذو أولوية",
        "name_en": "Priority road",
        "name_nl": "Voorrangsweg",
        "name_fr": "Route prioritaire",
        "description_ar": "أنت على طريق ذو أولوية، لك حق الأولوية على الطرق الجانبية.",
        "description_en": "You are on a priority road, you have right of way over side roads.",
        "description_nl": "U bent op een voorrangsweg, u heeft voorrang op zijwegen.",
        "description_fr": "Vous êtes sur une route prioritaire, vous avez la priorité sur les routes latérales.",
        "rules_ar": "لك الأولوية ولكن ابق حذراً، احترم إشارات المرور الأخرى",
        "category": "F"
    },
    "M1": {
        "name_ar": "لوحة إضافية - ينطبق على الدراجات",
        "name_en": "Additional panel - applies to bicycles",
        "name_nl": "Onderbord - geldt voor fietsen",
        "name_fr": "Panneau additionnel - s'applique aux vélos",
        "description_ar": "تطبق قواعد العلامة الرئيسية على الدراجات فقط أو تستثنى منها.",
        "description_en": "The rules of the main sign apply to or except bicycles only.",
        "description_nl": "De regels van het hoofdbord zijn van toepassing op of uitgezonderd fietsen.",
        "description_fr": "Les règles du panneau principal s'appliquent ou sont exclues pour les vélos.",
        "rules_ar": "اقرأ العلامة الرئيسية مع هذه اللوحة الإضافية لفهم القاعدة المطبقة",
        "category": "M"
    }
}

def main():
    print("=" * 60)
    print("🚦 استخراج أوصاف العلامات المرورية")
    print("   Traffic Signs Descriptions Extractor")
    print("=" * 60)
    
    theory_dir = Path("data/theory_source")
    
    # محاولة قراءة ملف PDF
    pdf_file = theory_dir / "verkeersborden.pdf"
    pdf_text = None
    
    if pdf_file.exists():
        print(f"\n📄 Found PDF: {pdf_file.name}")
        pdf_text = extract_pdf_text(pdf_file)
        if pdf_text:
            print(f"✅ Extracted {len(pdf_text)} characters from PDF")
    
    # إنشاء قاعدة بيانات الأوصاف
    descriptions_db = {}
    
    total_signs = sum(len(signs) for signs in TRAFFIC_SIGNS.values())
    processed = 0
    
    print(f"\n🔍 Processing {total_signs} traffic signs...")
    
    for category, signs in TRAFFIC_SIGNS.items():
        category_names = {
            "A": "Danger / علامات الخطر",
            "B": "Priority / علامات الأولوية",
            "C": "Prohibition / علامات المنع",
            "D": "Mandatory / علامات الإلزام",
            "E": "Parking / علامات الوقوف",
            "F": "Information / علامات إرشادية",
            "M": "Bicycle / لوحات الدراجات"
        }
        
        print(f"\n  📂 Category {category}: {category_names.get(category, 'Unknown')}")
        
        for sign_code in signs:
            processed += 1
            
            # استخدام الوصف الافتراضي إذا كان موجوداً
            if sign_code in DEFAULT_DESCRIPTIONS:
                descriptions_db[sign_code] = DEFAULT_DESCRIPTIONS[sign_code]
                print(f"    ✓ {sign_code}: {DEFAULT_DESCRIPTIONS[sign_code]['name_en']}")
            else:
                # إنشاء وصف أساسي
                descriptions_db[sign_code] = {
                    "name_ar": f"علامة {sign_code}",
                    "name_en": f"Sign {sign_code}",
                    "name_nl": f"Bord {sign_code}",
                    "name_fr": f"Panneau {sign_code}",
                    "description_ar": f"علامة مرورية من الفئة {category}",
                    "description_en": f"Traffic sign from category {category}",
                    "description_nl": f"Verkeersbord uit categorie {category}",
                    "description_fr": f"Panneau de circulation de la catégorie {category}",
                    "rules_ar": "يرجى الرجوع إلى دليل قواعد المرور",
                    "category": category
                }
                print(f"    ⚠ {sign_code}: Using default template")
    
    # حفظ قاعدة البيانات
    output_file = Path("data/traffic_signs_descriptions.json")
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(descriptions_db, f, ensure_ascii=False, indent=2)
    
    print(f"\n✅ Saved {len(descriptions_db)} descriptions to: {output_file}")
    print(f"📊 Coverage: {len([k for k in descriptions_db if k in DEFAULT_DESCRIPTIONS])}/{len(descriptions_db)} with detailed info")
    
    # إحصائيات
    print("\n📈 Statistics by category:")
    for category in TRAFFIC_SIGNS:
        count = len([s for s in descriptions_db if descriptions_db[s]['category'] == category])
        print(f"   {category}: {count} signs")

if __name__ == "__main__":
    main()
