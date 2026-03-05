import json

with open('sign_index.json', 'r', encoding='utf-8') as f:
    signs = json.load(f)

# تحديث المسارات المتبقية
mapping = {
    'onderborden': 'additional_signs',
    'afbakeningsborden': 'delineation_signs',
    'gevaarsborden': 'danger_signs',
    'voorrangsborden': 'priority_signs',
    'verbodsborden': 'prohibition_signs',
    'gebodsborden': 'mandatory_signs',
    'parkeren': 'parking_signs',
    'aanwijzingsborden': 'direction_signs',
    'zoneborden': 'zone_signs',
    'Informatieborden_en_tijdelijke_verkeersmaatregelen': 'information_signs'
}

fixed = 0
for sign in signs:
    path = sign.get('imagePath', '')
    
    # إذا كان المسار لا يزال يبدأ بـ assets
    if path.startswith('assets/signs/'):
        # استخراج اسم المجلد
        parts = path.split('/')
        if len(parts) >= 3:
            old_folder = parts[2]
            filename = parts[-1]
            
            # تحويل اسم المجلد
            new_folder = mapping.get(old_folder, old_folder)
            
            # المسار الجديد
            new_path = 'images/signs/' + new_folder + '/' + filename
            
            sign['imagePath'] = new_path
            fixed += 1
            print('✅ ' + sign.get('signCode', 'N/A') + ': ' + filename)

# حفظ التحديثات
with open('sign_index.json', 'w', encoding='utf-8') as f:
    json.dump(signs, f, ensure_ascii=False, indent=2)

print('')
print('✅ تم إصلاح ' + str(fixed) + ' مسار إضافي!')
