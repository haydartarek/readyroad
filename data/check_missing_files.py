import json
import os

# قراءة الملفات الفعلية
public_dir = '../public/images/signs'
actual_files = {}

for root, dirs, files in os.walk(public_dir):
    for file in files:
        if file.endswith('.png'):
            rel_path = os.path.relpath(os.path.join(root, file), '../public')
            rel_path = rel_path.replace('\\', '/')
            # استخراج الكود من اسم الملف
            code = file.split()[0].replace('.png', '')
            if code not in actual_files:
                actual_files[code] = []
            actual_files[code].append(rel_path)

# فحص الأكواد المفقودة
missing_codes = ['ZC5', 'ZC21T', 'ZC35', 'F50b', 'F45b', 'M12-richtingen', 'GVIId-PR', 'GIII-aquaplaning', 'GVIId-CARPOOL']

print('📂 الملفات الفعلية الموجودة:')
print('=' * 70)
for code in missing_codes:
    if code in actual_files:
        print('✅ ' + code + ':')
        for path in actual_files[code]:
            print('   - ' + path)
    else:
        print('❌ ' + code + ': غير موجود')
    print()

# قراءة signs.json
with open('signs.json', 'r', encoding='utf-8') as f:
    signs = json.load(f)

print('📋 فحص signs.json:')
print('=' * 70)
for code in missing_codes:
    found = False
    for sign in signs:
        if sign.get('signCode') == code:
            print('✅ ' + code + ': موجود في signs.json')
            print('   المسار: ' + sign.get('imagePath', 'N/A'))
            found = True
            break
    if not found:
        print('❌ ' + code + ': مفقود من signs.json')
    print()
