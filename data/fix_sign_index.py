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
    "parkeren": "parking_signs",
    "aanwijzingsborden": "direction_signs",
    "onderborden": "additional_signs"
}

def extract_sign_id(filename):
    """استخراج معرف اللافتة من اسم الملف"""
    name = filename.replace('.png', '')
    
    patterns = [
        r'^(ZC\d+T?)',
        r'^(ZE\d+[a-z]?T?)',
        r'^(F\d+)',
        r'^(Type\s*[IV][abc]?\d*)',
        r'^([A-Z]+\d+[a-z]?)',
    ]
    
    for pattern in patterns:
        match = re.search(pattern, name, re.IGNORECASE)
        if match:
            return match.group(1).replace(' ', '')
    
    return None

def scan_assets_folder():
    """مسح جميع الصور في assets"""
    image_map = {}
    
    for category_folder in os.listdir(ASSETS_PATH):
        folder_path = os.path.join(ASSETS_PATH, category_folder)
        
        if not os.path.isdir(folder_path):
            continue
        
        category = CATEGORY_MAPPING.get(category_folder, category_folder)
        
        for filename in os.listdir(folder_path):
            if not filename.endswith('.png'):
                continue
            
            sign_id = extract_sign_id(filename)
            if sign_id:
                clean_id = sign_id.upper().replace(' ', '')
                
                if clean_id not in image_map:
                    image_map[clean_id] = {
                        'filename': filename,
                        'category': category,
                        'folder': category_folder,
                        'path': f'images/signs/{category}/{filename}'
                    }
    
    return image_map

def update_sign_index_json(image_map):
    """تحديث sign_index.json - بنية Array"""
    index_path = os.path.join(JSON_DIR, 'sign_index.json')
    
    with open(index_path, 'r', encoding='utf-8') as f:
        index_data = json.load(f)
    
    updated_count = 0
    
    # التحديث لكل عنصر في الـ array
    for item in index_data:
        sign_code = item.get('signCode', '').upper().replace(' ', '')
        
        if sign_code in image_map:
            old_path = item.get('imagePath', '')
            new_path = image_map[sign_code]['path']
            
            # تحديث imagePath
            if old_path != new_path:
                item['imagePath'] = new_path
                updated_count += 1
                print(f"  ✅ {sign_code}: {old_path} → {new_path}")
    
    # حفظ التحديثات
    with open(index_path, 'w', encoding='utf-8') as f:
        json.dump(index_data, f, ensure_ascii=False, indent=2)
    
    print(f"\n✅ تم تحديث {updated_count} مسار في sign_index.json")

def main():
    print("=" * 80)
    print("🔧 إصلاح sign_index.json")
    print("=" * 80)
    
    # مسح assets
    print("\n📂 مسح مجلد assets...")
    image_map = scan_assets_folder()
    print(f"✅ تم العثور على {len(image_map)} صورة\n")
    
    # تحديث sign_index.json
    print("📝 تحديث sign_index.json...")
    print("-" * 80)
    update_sign_index_json(image_map)
    
    print("\n" + "=" * 80)
    print("✅ اكتمل!")
    print("=" * 80)

if __name__ == "__main__":
    main()
