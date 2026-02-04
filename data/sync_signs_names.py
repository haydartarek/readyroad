import os
import json
import re
from pathlib import Path

# المسارات
ASSETS_PATH = r"D:\driving_school_app\assets\signs"
JSON_DIR = r"C:\Users\heyde\Desktop\end_project\readyroad\data"

# تعيين المجلدات
CATEGORY_MAPPING = {
    "zoneborden": "zone_signs",
    "afbakeningsborden": "delineation_signs",
    "Informatieborden_en_tijdelijke_verkeersmaatregelen": "information_signs",
    "gevaarsborden": "danger_signs",
    "voorrangsborden": "priority_signs",
    "verbodsborden": "prohibition_signs",
    "gebodsborden": "mandatory_signs",
    "parkeer- en stilstaanborden": "parking_signs",
    "aanwijzingsborden": "direction_signs",
    "onderborden": "additional_signs"
}

def extract_sign_id(filename):
    """استخراج معرف اللافتة من اسم الملف"""
    # أزل .png
    name = filename.replace('.png', '')
    
    # أنماط مختلفة
    patterns = [
        r'^(ZC\d+T?)',           # ZC35, ZC21T
        r'^(ZE\d+[a-z]?T?)',     # ZE9a, ZE9T
        r'^(F\d+)',              # F39, F79
        r'^(Type\s*[IV][abc]?\d*)', # Type Ia1, Type V
        r'^([A-Z]+\d+[a-z]?)',   # عام
    ]
    
    for pattern in patterns:
        match = re.search(pattern, name, re.IGNORECASE)
        if match:
            return match.group(1).replace(' ', '')
    
    return None

def scan_assets_folder():
    """مسح جميع الصور في assets وإنشاء خريطة"""
    image_map = {}
    
    for category_folder in os.listdir(ASSETS_PATH):
        folder_path = os.path.join(ASSETS_PATH, category_folder)
        
        if not os.path.isdir(folder_path):
            continue
        
        # تحويل اسم المجلد
        category = CATEGORY_MAPPING.get(category_folder, category_folder)
        
        for filename in os.listdir(folder_path):
            if not filename.endswith('.png'):
                continue
            
            sign_id = extract_sign_id(filename)
            if sign_id:
                # نظّف معرف اللافتة
                clean_id = sign_id.upper().replace(' ', '')
                
                # احفظ المعلومات
                if clean_id not in image_map:
                    image_map[clean_id] = {
                        'filename': filename,
                        'category': category,
                        'folder': category_folder,
                        'path': f'images/signs/{category}/{filename}'
                    }
                    print(f"✓ {clean_id:15s} → {filename}")
    
    return image_map

def update_signs_json(image_map):
    """تحديث signs.json"""
    signs_path = os.path.join(JSON_DIR, 'signs.json')
    
    with open(signs_path, 'r', encoding='utf-8') as f:
        signs_data = json.load(f)
    
    updated_count = 0
    
    for sign in signs_data:
        sign_id = sign.get('id', '').upper().replace(' ', '')
        
        if sign_id in image_map:
            old_image = sign.get('image', '')
            new_image = image_map[sign_id]['path']
            
            if old_image != new_image:
                sign['image'] = new_image
                updated_count += 1
                print(f"  ✅ {sign_id}: {old_image} → {new_image}")
    
    # حفظ التحديثات
    with open(signs_path, 'w', encoding='utf-8') as f:
        json.dump(signs_data, f, ensure_ascii=False, indent=2)
    
    print(f"\n✅ تم تحديث {updated_count} لافتة في signs.json")

def update_sign_index_json(image_map):
    """تحديث sign_index.json"""
    index_path = os.path.join(JSON_DIR, 'sign_index.json')
    
    with open(index_path, 'r', encoding='utf-8') as f:
        index_data = json.load(f)
    
    updated_count = 0
    
    for category, signs in index_data.items():
        for i, sign_id in enumerate(signs):
            clean_id = sign_id.upper().replace(' ', '')
            
            if clean_id in image_map:
                # تحديث المعرف إذا لزم الأمر
                if sign_id != clean_id:
                    index_data[category][i] = clean_id
                    updated_count += 1
    
    # حفظ التحديثات
    with open(index_path, 'w', encoding='utf-8') as f:
        json.dump(index_data, f, ensure_ascii=False, indent=2)
    
    print(f"✅ تم تحديث {updated_count} معرف في sign_index.json")

def generate_missing_report(image_map):
    """إنشاء تقرير بالصور المفقودة"""
    signs_path = os.path.join(JSON_DIR, 'signs.json')
    
    with open(signs_path, 'r', encoding='utf-8') as f:
        signs_data = json.load(f)
    
    missing = []
    
    for sign in signs_data:
        sign_id = sign.get('id', '').upper().replace(' ', '')
        
        if sign_id not in image_map:
            missing.append({
                'id': sign.get('id'),
                'name_nl': sign.get('name_nl', ''),
                'category': sign.get('category', '')
            })
    
    if missing:
        print("\n" + "=" * 80)
        print(f"⚠️ لافتات مفقودة في assets: {len(missing)}")
        print("=" * 80)
        
        for item in missing[:20]:  # أول 20
            print(f"  • {item['id']:15s} | {item['category']:20s} | {item['name_nl']}")
        
        if len(missing) > 20:
            print(f"  ... و {len(missing) - 20} أخرى")
    else:
        print("\n✅ جميع اللافتات موجودة في assets!")

def main():
    print("=" * 80)
    print("🚀 بدء مطابقة أسماء الصور مع ملفات JSON")
    print("=" * 80)
    
    # الخطوة 1: مسح مجلد assets
    print("\n📂 الخطوة 1: مسح مجلد assets...")
    print("-" * 80)
    image_map = scan_assets_folder()
    print(f"\n✅ تم العثور على {len(image_map)} صورة")
    
    # الخطوة 2: تحديث signs.json
    print("\n📝 الخطوة 2: تحديث signs.json...")
    print("-" * 80)
    update_signs_json(image_map)
    
    # الخطوة 3: تحديث sign_index.json
    print("\n📝 الخطوة 3: تحديث sign_index.json...")
    print("-" * 80)
    update_sign_index_json(image_map)
    
    # الخطوة 4: تقرير الصور المفقودة
    print("\n📊 الخطوة 4: فحص الصور المفقودة...")
    print("-" * 80)
    generate_missing_report(image_map)
    
    print("\n" + "=" * 80)
    print("✅ اكتمل التحديث!")
    print("=" * 80)

if __name__ == "__main__":
    main()
