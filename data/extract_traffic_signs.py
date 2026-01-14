#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
مستخرج بيانات العلامات المرورية البلجيكية
Belgian Traffic Signs Data Extractor
"""

import re
import json
import os
import urllib.request
import urllib.parse
from html.parser import HTMLParser
from pathlib import Path
import time

class TrafficSignExtractor(HTMLParser):
    def __init__(self):
        super().__init__()
        self.signs = []
        self.current_category = None
        self.in_category_header = False
        self.in_listing = False
        self.in_listing_item = False
        self.current_sign = {}
        self.in_title = False
        
        # تصنيفات العلامات المرورية
        self.categories = {
            'A-serie': {'name_ar': 'علامات الخطر', 'name_en': 'Danger Signs', 'name_nl': 'Gevaar', 'name_fr': 'Danger'},
            'B-serie': {'name_ar': 'علامات الأولوية', 'name_en': 'Priority Signs', 'name_nl': 'Voorrang', 'name_fr': 'Priorité'},
            'C-serie': {'name_ar': 'علامات المنع', 'name_en': 'Prohibition Signs', 'name_nl': 'Verbod', 'name_fr': 'Interdiction'},
            'D-serie': {'name_ar': 'علامات الإلزام', 'name_en': 'Mandatory Signs', 'name_nl': 'Gebod', 'name_fr': 'Obligation'},
            'E-serie': {'name_ar': 'علامات الوقوف والانتظار', 'name_en': 'Parking Signs', 'name_nl': 'Stilstaan en parkeren', 'name_fr': 'Stationnement'},
            'F-serie': {'name_ar': 'علامات إرشادية', 'name_en': 'Information Signs', 'name_nl': 'Aanwijzing', 'name_fr': 'Indication'},
            'G-serie': {'name_ar': 'لوحات إضافية', 'name_en': 'Additional Panels', 'name_nl': 'Onderborden', 'name_fr': 'Panneaux additionnels'},
            'M-serie': {'name_ar': 'لوحات الدراجات', 'name_en': 'Bicycle Signs', 'name_nl': 'Onderborden betreffende fietsen en bromfietsen', 'name_fr': 'Panneaux vélos'},
            'T-serie': {'name_ar': 'علامات التحديد', 'name_en': 'Boundary Signs', 'name_nl': 'Afbakeningsborden', 'name_fr': 'Balises'},
            'Z-serie': {'name_ar': 'علامات المناطق', 'name_en': 'Zone Signs', 'name_nl': 'Zoneborden', 'name_fr': 'Panneaux de zone'}
        }
    
    def handle_starttag(self, tag, attrs):
        attrs_dict = dict(attrs)
        
        # البحث عن عناوين الفئات
        if tag == 'h2':
            self.in_category_header = True
        
        # البحث عن قسم القائمة
        if tag == 'div' and attrs_dict.get('class') == 'listing':
            self.in_listing = True
        
        # البحث عن عنصر في القائمة
        if self.in_listing and tag == 'a' and 'listing-item' in attrs_dict.get('class', ''):
            self.in_listing_item = True
            self.current_sign = {
                'url': attrs_dict.get('href', ''),
                'category_code': self.current_category
            }
            if self.current_category and self.current_category in self.categories:
                cat_info = self.categories[self.current_category]
                self.current_sign['category_name_ar'] = cat_info['name_ar']
                self.current_sign['category_name_en'] = cat_info['name_en']
                self.current_sign['category_name_nl'] = cat_info['name_nl']
                self.current_sign['category_name_fr'] = cat_info['name_fr']
        
        # البحث عن الصورة
        if self.in_listing_item and tag == 'img':
            src = attrs_dict.get('src', '')
            srcset = attrs_dict.get('srcset', '')
            alt = attrs_dict.get('alt', '')
            
            # استخراج رابط الصورة عالية الدقة من srcset
            if srcset:
                # البحث عن رابط 2x (أعلى دقة)
                parts = srcset.split(',')
                for part in parts:
                    if '2x' in part:
                        image_url = part.strip().split()[0]
                        self.current_sign['image_url'] = image_url
                        break
                if 'image_url' not in self.current_sign and parts:
                    self.current_sign['image_url'] = parts[0].strip().split()[0]
            elif src:
                self.current_sign['image_url'] = src
            
            self.current_sign['alt'] = alt
        
        # البحث عن العنوان
        if self.in_listing_item and tag == 'div' and 'listing-item__title' in attrs_dict.get('class', ''):
            self.in_title = True
    
    def handle_data(self, data):
        data = data.strip()
        if not data:
            return
        
        # تحديد الفئة الحالية
        if self.in_category_header:
            for category_key in self.categories.keys():
                if category_key in data:
                    self.current_category = category_key
                    break
        
        # استخراج عنوان العلامة
        if self.in_title and data:
            self.current_sign['title_nl'] = data
            
            # استخراج رمز العلامة من العنوان
            # مثال: "A1 Gevaarlijke bocht naar links"
            code_match = re.match(r'^([A-Z]+\d+[a-zA-Z]*)\s+(.+)$', data)
            if code_match:
                self.current_sign['sign_code'] = code_match.group(1)
                self.current_sign['description_nl'] = code_match.group(2)
            else:
                self.current_sign['description_nl'] = data
    
    def handle_endtag(self, tag):
        if tag == 'h2':
            self.in_category_header = False
        
        if tag == 'div' and self.in_listing:
            self.in_listing = False
        
        if tag == 'a' and self.in_listing_item:
            self.in_listing_item = False
            if self.current_sign and 'title_nl' in self.current_sign:
                self.signs.append(self.current_sign.copy())
            self.current_sign = {}
        
        if tag == 'div' and self.in_title:
            self.in_title = False


def download_image(url, save_path):
    """تنزيل صورة من URL"""
    try:
        # إنشاء المجلد إذا لم يكن موجوداً
        os.makedirs(os.path.dirname(save_path), exist_ok=True)
        
        # تنزيل الصورة
        headers = {'User-Agent': 'Mozilla/5.0'}
        req = urllib.request.Request(url, headers=headers)
        
        with urllib.request.urlopen(req) as response:
            with open(save_path, 'wb') as out_file:
                out_file.write(response.read())
        
        print(f"✓ تم تنزيل: {os.path.basename(save_path)}")
        return True
    except Exception as e:
        print(f"✗ خطأ في تنزيل {url}: {str(e)}")
        return False


def create_folder_structure(base_path):
    """إنشاء هيكل المجلدات للصور"""
    folders = {
        'A-serie': 'danger_signs',
        'B-serie': 'priority_signs',
        'C-serie': 'prohibition_signs',
        'D-serie': 'mandatory_signs',
        'E-serie': 'parking_signs',
        'F-serie': 'information_signs',
        'G-serie': 'additional_panels',
        'M-serie': 'bicycle_signs',
        'T-serie': 'boundary_signs',
        'Z-serie': 'zone_signs'
    }
    
    for folder_name in folders.values():
        folder_path = os.path.join(base_path, folder_name)
        os.makedirs(folder_path, exist_ok=True)
    
    return folders


def main():
    # قراءة ملف HTML
    html_file = 'traffic_signs.html'
    
    if not os.path.exists(html_file):
        print(f"❌ الملف {html_file} غير موجود!")
        return
    
    print("🚦 بدء استخراج بيانات العلامات المرورية...")
    
    # قراءة وتحليل HTML
    with open(html_file, 'r', encoding='utf-8') as f:
        html_content = f.read()
    
    parser = TrafficSignExtractor()
    parser.feed(html_content)
    
    print(f"\n✓ تم استخراج {len(parser.signs)} علامة مرورية")
    
    # إحصائيات حسب الفئة
    category_counts = {}
    for sign in parser.signs:
        cat = sign.get('category_code', 'Unknown')
        category_counts[cat] = category_counts.get(cat, 0) + 1
    
    print("\n📊 إحصائيات حسب الفئة:")
    for cat, count in sorted(category_counts.items()):
        cat_name = parser.categories.get(cat, {}).get('name_ar', cat)
        print(f"  {cat} ({cat_name}): {count} علامة")
    
    # حفظ البيانات في JSON
    output_file = 'traffic_signs_data.json'
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(parser.signs, f, ensure_ascii=False, indent=2)
    
    print(f"\n✓ تم حفظ البيانات في: {output_file}")
    
    # إنشاء هيكل المجلدات للصور
    base_image_path = '../mobile_app/assets/traffic_signs'
    folder_mapping = create_folder_structure(base_image_path)
    
    print(f"\n✓ تم إنشاء هيكل المجلدات في: {base_image_path}")
    
    # سؤال المستخدم عن تنزيل الصور
    download_images = input("\n❓ هل تريد تنزيل الصور الآن؟ (y/n): ").strip().lower()
    
    if download_images == 'y':
        print("\n📥 بدء تنزيل الصور...")
        successful = 0
        failed = 0
        
        for i, sign in enumerate(parser.signs, 1):
            if 'image_url' not in sign:
                continue
            
            # تحديد مسار حفظ الصورة
            category_code = sign.get('category_code', 'unknown')
            folder_name = folder_mapping.get(category_code, 'other')
            sign_code = sign.get('sign_code', f'sign_{i}')
            
            # تنظيف اسم الملف
            clean_code = re.sub(r'[^\w\-]', '_', sign_code)
            image_filename = f"{clean_code}.png"
            image_path = os.path.join(base_image_path, folder_name, image_filename)
            
            # تنزيل الصورة
            if download_image(sign['image_url'], image_path):
                # تحديث مسار الصورة في البيانات
                sign['image_path'] = f"assets/traffic_signs/{folder_name}/{image_filename}"
                successful += 1
            else:
                failed += 1
            
            # توقف قصير لتجنب الحظر
            time.sleep(0.5)
        
        print(f"\n✅ اكتمل التنزيل: {successful} نجح، {failed} فشل")
        
        # حفظ البيانات المحدثة
        with open(output_file, 'w', encoding='utf-8') as f:
            json.dump(parser.signs, f, ensure_ascii=False, indent=2)
        
        print(f"✓ تم تحديث البيانات مع مسارات الصور")
    
    print("\n🎉 انتهت عملية الاستخراج بنجاح!")
    print(f"📁 الملفات المنتجة:")
    print(f"  - {output_file}")
    print(f"  - {base_image_path}/")


if __name__ == '__main__':
    main()
