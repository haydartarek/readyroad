#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
توليد ملف SQL migration لجميع العلامات المرورية
Generate SQL migration file for ALL traffic signs
"""

import json
from pathlib import Path
from datetime import datetime

def escape_sql(text):
    """تنظيف النص لـ SQL"""
    if not text:
        return ''
    return text.replace("'", "''").replace('\n', ' ').replace('\r', '').strip()

def generate_sql_migration():
    """توليد ملف SQL migration"""
    
    # قراءة بيانات العلامات
    signs_file = Path("data/traffic_signs_complete.json")
    
    with open(signs_file, 'r', encoding='utf-8') as f:
        signs_data = json.load(f)
    
    print(f"📊 Loaded {len(signs_data)} traffic signs")
    
    # بداية ملف SQL
    sql_content = []
    sql_content.append("-- V6__Add_All_Traffic_Signs.sql")
    sql_content.append("-- إضافة جميع العلامات المرورية البلجيكية")
    sql_content.append("-- Belgian Traffic Signs - Complete Database")
    sql_content.append(f"-- Generated: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    sql_content.append(f"-- Total Signs: {len(signs_data)}")
    sql_content.append("")
    sql_content.append("-- ========================================")
    sql_content.append("-- إدراج فئات العلامات المرورية")
    sql_content.append("-- Traffic Signs Categories")
    sql_content.append("-- ========================================")
    sql_content.append("")
    
    # إضافة الفئات
    categories = {
        'A': {
            'ar': 'علامات الخطر',
            'en': 'Danger Signs',
            'nl': 'Gevaar',
            'fr': 'Danger',
            'desc_ar': 'علامات تحذيرية للإشارة إلى المخاطر على الطريق',
            'desc_en': 'Warning signs indicating road hazards',
            'desc_nl': 'Waarschuwingsborden voor gevaren op de weg',
            'desc_fr': 'Panneaux d\'avertissement des dangers sur la route'
        },
        'B': {
            'ar': 'علامات الأولوية',
            'en': 'Priority Signs',
            'nl': 'Voorrang',
            'fr': 'Priorité',
            'desc_ar': 'علامات تحدد حق الأولوية على الطريق',
            'desc_en': 'Signs determining priority on the road',
            'desc_nl': 'Borden die voorrang op de weg bepalen',
            'desc_fr': 'Panneaux déterminant la priorité sur la route'
        },
        'C': {
            'ar': 'علامات المنع',
            'en': 'Prohibition Signs',
            'nl': 'Verbod',
            'fr': 'Interdiction',
            'desc_ar': 'علامات تمنع أو تحظر إجراءات معينة',
            'desc_en': 'Signs prohibiting certain actions',
            'desc_nl': 'Borden die bepaalde handelingen verbieden',
            'desc_fr': 'Panneaux interdisant certaines actions'
        },
        'D': {
            'ar': 'علامات الإلزام',
            'en': 'Mandatory Signs',
            'nl': 'Gebod',
            'fr': 'Obligation',
            'desc_ar': 'علامات تفرض سلوكاً معيناً',
            'desc_en': 'Signs imposing specific behavior',
            'desc_nl': 'Borden die specifiek gedrag opleggen',
            'desc_fr': 'Panneaux imposant un comportement spécifique'
        },
        'E': {
            'ar': 'علامات الوقوف والانتظار',
            'en': 'Parking Signs',
            'nl': 'Stilstaan en parkeren',
            'fr': 'Stationnement',
            'desc_ar': 'علامات تنظم الوقوف والانتظار',
            'desc_en': 'Signs regulating stopping and parking',
            'desc_nl': 'Borden die stilstaan en parkeren regelen',
            'desc_fr': 'Panneaux réglementant l\'arrêt et le stationnement'
        },
        'F': {
            'ar': 'علامات إرشادية',
            'en': 'Information Signs',
            'nl': 'Aanwijzing',
            'fr': 'Indication',
            'desc_ar': 'علامات توفر معلومات ودلالات',
            'desc_en': 'Signs providing information and directions',
            'desc_nl': 'Borden die informatie en aanwijzingen geven',
            'desc_fr': 'Panneaux fournissant des informations et des indications'
        },
        'M': {
            'ar': 'لوحات الدراجات',
            'en': 'Bicycle Signs',
            'nl': 'Onderborden betreffende fietsen',
            'fr': 'Panneaux vélos',
            'desc_ar': 'لوحات خاصة بالدراجات والدراجات النارية',
            'desc_en': 'Signs specific to bicycles and mopeds',
            'desc_nl': 'Borden specifiek voor fietsen en bromfietsen',
            'desc_fr': 'Panneaux spécifiques aux vélos et cyclomoteurs'
        }
    }
    
    order = 1
    for code, cat_data in categories.items():
        sql = f"""INSERT INTO traffic_sign_categories (code, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, display_order)
VALUES ('{code}', '{cat_data['ar']}', '{cat_data['en']}', '{cat_data['nl']}', '{cat_data['fr']}', 
        '{cat_data['desc_ar']}', '{cat_data['desc_en']}', '{cat_data['desc_nl']}', '{cat_data['desc_fr']}', {order});
"""
        sql_content.append(sql)
        order += 1
    
    sql_content.append("")
    sql_content.append("-- ========================================")
    sql_content.append("-- إدراج جميع العلامات المرورية")
    sql_content.append("-- Insert ALL Traffic Signs")
    sql_content.append("-- ========================================")
    sql_content.append("")
    
    # فرز العلامات حسب الفئة والرمز
    sorted_signs = sorted(signs_data.items(), key=lambda x: (x[1]['category'], x[0]))
    
    current_category = None
    for sign_code, sign_data in sorted_signs:
        category = sign_data['category']
        
        # إضافة تعليق للفئة الجديدة
        if category != current_category:
            cat_name = categories.get(category, {}).get('ar', 'Unknown')
            sql_content.append("")
            sql_content.append(f"-- Category {category}: {cat_name}")
            sql_content.append("")
            current_category = category
        
        # تنظيف البيانات
        name_ar = escape_sql(sign_data.get('name_ar', f'علامة {sign_code}'))
        name_en = escape_sql(sign_data.get('name_en', sign_data.get('name_nl', '')))
        name_nl = escape_sql(sign_data.get('name_nl', ''))
        name_fr = escape_sql(sign_data.get('name_fr', name_en))
        image_path = escape_sql(sign_data.get('image_path', ''))
        
        # استخدام الاسم كوصف مبدئي
        desc_ar = escape_sql(sign_data.get('description_ar', name_ar))
        desc_en = escape_sql(sign_data.get('description_en', name_en))
        desc_nl = escape_sql(sign_data.get('description_nl', name_nl))
        desc_fr = escape_sql(sign_data.get('description_fr', name_fr))
        
        sql = f"""INSERT INTO traffic_signs (
  sign_code, category_id, name_ar, name_en, name_nl, name_fr,
  description_ar, description_en, description_nl, description_fr,
  image_path, created_at, updated_at
) VALUES (
  '{sign_code}', 
  (SELECT id FROM traffic_sign_categories WHERE code = '{category}'),
  '{name_ar}',
  '{name_en}',
  '{name_nl}',
  '{name_fr}',
  '{desc_ar}',
  '{desc_en}',
  '{desc_nl}',
  '{desc_fr}',
  '{image_path}',
  NOW(),
  NOW()
);
"""
        sql_content.append(sql)
    
    # حفظ الملف
    output_file = Path("src/main/resources/db/migration/V6__Add_All_Traffic_Signs.sql")
    output_file.parent.mkdir(parents=True, exist_ok=True)
    
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write('\n'.join(sql_content))
    
    print(f"✅ Generated SQL migration: {output_file}")
    print(f"📊 Total INSERT statements: {len(sorted_signs)}")
    
    # إحصائيات
    print(f"\n📈 Signs by category:")
    cat_counts = {}
    for sign_data in signs_data.values():
        cat = sign_data['category']
        cat_counts[cat] = cat_counts.get(cat, 0) + 1
    
    for cat in sorted(cat_counts.keys()):
        cat_name = categories.get(cat, {}).get('ar', 'Unknown')
        print(f"   {cat} ({cat_name}): {cat_counts[cat]} signs")

def main():
    print("=" * 70)
    print("🔨 توليد SQL Migration لجميع العلامات المرورية")
    print("   Generate SQL Migration for ALL Traffic Signs")
    print("=" * 70)
    print()
    
    generate_sql_migration()
    
    print("\n🎉 Done! Migration file created successfully")
    print("💡 Run: ./mvnw spring-boot:run to apply the migration")

if __name__ == "__main__":
    main()
