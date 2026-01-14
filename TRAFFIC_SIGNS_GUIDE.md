# دليل نظام العلامات المرورية
# Traffic Signs System Guide

## نظرة عامة / Overview

هذا النظام يدير العلامات المرورية البلجيكية بعشر فئات رئيسية مع الصور والأوصاف بأربع لغات.

This system manages Belgian traffic signs with ten main categories, images, and descriptions in four languages.

---

## الفئات الرئيسية / Main Categories

| الرمز<br>Code | الاسم بالعربية<br>Arabic Name | English Name | Nederlands | Français |
|------|-----------------|--------------|------------|----------|
| **A** | علامات الخطر | Danger Signs | Gevaar | Danger |
| **B** | علامات الأولوية | Priority Signs | Voorrang | Priorité |
| **C** | علامات المنع | Prohibition Signs | Verbod | Interdiction |
| **D** | علامات الإلزام | Mandatory Signs | Gebod | Obligation |
| **E** | علامات الوقوف | Parking Signs | Stilstaan en parkeren | Stationnement |
| **F** | علامات إرشادية | Information Signs | Aanwijzing | Indication |
| **G** | لوحات إضافية | Additional Panels | Onderborden | Panneaux additionnels |
| **M** | لوحات الدراجات | Bicycle Signs | Fietsborden | Panneaux vélos |
| **T** | علامات التحديد | Boundary Signs | Afbakeningsborden | Balises |
| **Z** | علامات المناطق | Zone Signs | Zoneborden | Panneaux de zone |

---

## هيكل المجلدات / Folder Structure

```
mobile_app/assets/traffic_signs/
├── danger_signs/         (A-serie: ~90 علامة)
├── priority_signs/       (B-serie: ~25 علامة)
├── prohibition_signs/    (C-serie: ~50 علامة)
├── mandatory_signs/      (D-serie: ~15 علامة)
├── parking_signs/        (E-serie: ~20 علامة)
├── information_signs/    (F-serie: ~150 علامة)
├── additional_panels/    (G-serie: ~15 لوحة)
├── bicycle_signs/        (M-serie: ~20 لوحة)
├── boundary_signs/       (T-serie: ~10 علامات)
└── zone_signs/           (Z-serie: ~15 علامة)
```

---

## خطوات التنفيذ / Implementation Steps

### 1️⃣ تنزيل الصور / Download Images

#### الطريقة الأولى: PowerShell Script (موصى به / Recommended)

```powershell
# تشغيل السكريبت / Run the script
.\Download-Traffic-Signs.ps1

# للمعاينة بدون تنزيل / Dry run (preview only)
.\Download-Traffic-Signs.ps1 -DryRun

# تحديد عدد التنزيلات المتزامنة / Set concurrent downloads
.\Download-Traffic-Signs.ps1 -MaxConcurrent 10
```

#### الطريقة الثانية: Java Extractor

```powershell
# الترجمة / Compile
.\mvnw compile

# التشغيل / Run
.\mvnw exec:java -D"exec.mainClass=com.readyroad.util.TrafficSignExtractor"
```

---

### 2️⃣ تطبيق Migration / Apply Database Migration

```powershell
# تشغيل التطبيق لتطبيق Flyway migration
# Run application to apply Flyway migration
.\mvnw spring-boot:run

# أو التحقق من ملفات migration يدوياً
# Or check migration files manually
Get-Content src\main\resources\db\migration\V6__Add_Traffic_Signs_Data.sql
```

**ملف Migration يحتوي على:**
- 10 فئات رئيسية (categories)
- أمثلة على العلامات الأكثر شيوعاً (5 examples)
- تعليقات توضيحية لإضافة بقية البيانات

**Migration file contains:**
- 10 main categories
- 5 examples of most common signs
- Comments explaining how to add remaining data

---

### 3️⃣ تحديث Assets / Update Flutter Assets

```powershell
# الملف محدث بالفعل / File already updated
Get-Content mobile_app\pubspec.yaml
```

تأكد من وجود قسم assets:
```yaml
assets:
  - assets/traffic_signs/danger_signs/
  - assets/traffic_signs/priority_signs/
  # ... الخ
```

---

### 4️⃣ تشغيل التطبيق / Run the App

```powershell
cd mobile_app

# تحديث dependencies
flutter pub get

# تشغيل على Web
flutter run -d chrome

# تشغيل على Android
flutter run -d <device-id>
```

---

## إضافة بيانات العلامات الكاملة / Adding Full Signs Data

### الخيار 1: إضافة يدوية في Migration

فتح ملف:
```
src/main/resources/db/migration/V6__Add_Traffic_Signs_Data.sql
```

إضافة INSERT statements:
```sql
INSERT INTO traffic_signs (
  sign_code, category_id, name_ar, name_en, name_nl, name_fr,
  description_ar, description_en, description_nl, description_fr,
  image_path, created_at, updated_at
) VALUES (
  'A2', 
  (SELECT id FROM traffic_sign_categories WHERE code = 'A'),
  'منعطف خطر لليمين',
  'Dangerous bend to the right',
  'Gevaarlijke bocht naar rechts',
  'Virage dangereux à droite',
  -- ...
  'assets/traffic_signs/danger_signs/A2.png',
  NOW(),
  NOW()
);
```

### الخيار 2: استخدام Java Extractor

السكريبت `TrafficSignExtractor.java` يستخرج البيانات من HTML ويولد:
- ملف JSON بكل البيانات
- INSERT statements جاهزة
- يمكن نسخها إلى migration

---

## التحقق من التنصيب / Verify Installation

### 1. التحقق من الصور / Check Images

```powershell
# عدد الصور في كل مجلد / Count images in each folder
Get-ChildItem -Path mobile_app\assets\traffic_signs -Recurse -File | 
  Group-Object Directory | 
  Select-Object Name, Count
```

### 2. التحقق من Database / Check Database

```sql
-- عدد الفئات / Count categories
SELECT COUNT(*) FROM traffic_sign_categories;  -- يجب أن يكون 10

-- عدد العلامات / Count signs
SELECT COUNT(*) FROM traffic_signs;

-- العلامات حسب الفئة / Signs by category
SELECT 
  c.code,
  c.name_en,
  COUNT(ts.id) as sign_count
FROM traffic_sign_categories c
LEFT JOIN traffic_signs ts ON ts.category_id = c.id
GROUP BY c.id, c.code, c.name_en
ORDER BY c.display_order;
```

### 3. التحقق من Flutter Assets / Check Flutter Assets

```dart
// في كود Dart:
const String imagePath = 'assets/traffic_signs/danger_signs/A1.png';
Image.asset(imagePath);
```

---

## استكشاف الأخطاء / Troubleshooting

### ❌ الصور لا تظهر في Flutter / Images not showing

**الحل:**
```powershell
# التأكد من تحديث pubspec.yaml
Get-Content mobile_app\pubspec.yaml | Select-String "traffic_signs"

# إعادة تحميل assets
cd mobile_app
flutter clean
flutter pub get
```

### ❌ Migration فشل / Migration failed

**الحل:**
```sql
-- التحقق من حالة migrations
SELECT * FROM flyway_schema_history ORDER BY installed_rank DESC;

-- حذف migration الفاشل (إذا لزم الأمر)
DELETE FROM flyway_schema_history WHERE version = '6';

-- إعادة تشغيل التطبيق
```

### ❌ تنزيل الصور فشل / Image download failed

**الحل:**
```powershell
# إعادة تشغيل السكريبت (سيتخطى الصور الموجودة)
.\Download-Traffic-Signs.ps1

# التحقق من الاتصال بالإنترنت
Test-NetConnection -ComputerName www.verkeersbord.be -Port 443
```

---

## الإحصائيات / Statistics

بعد تشغيل سكريبت التنزيل، يتم إنشاء ملف إحصائيات:
```
mobile_app/assets/traffic_signs/download_stats.json
```

يحتوي على:
- عدد العلامات الكلي
- عدد الصور المنزلة
- عدد الصور المتخطاة
- عدد الأخطاء
- توزيع العلامات حسب الفئات

---

## الخطوات القادمة / Next Steps

### 🎯 Backend

- [ ] إنشاء REST API للعلامات المرورية
- [ ] إضافة endpoint للبحث حسب الفئة
- [ ] إضافة endpoint للبحث بالنص
- [ ] إضافة الترجمة متعددة اللغات

### 📱 Mobile App

- [ ] إنشاء صفحة عرض الفئات
- [ ] إنشاء صفحة عرض العلامات حسب الفئة
- [ ] إنشاء صفحة تفاصيل العلامة
- [ ] إضافة البحث والتصفية
- [ ] إضافة المفضلة
- [ ] إضافة اختبار التعرف على العلامات

---

## الملفات المهمة / Important Files

| الملف / File | الوصف / Description |
|-------------|---------------------|
| `data/traffic_signs.html` | ملف HTML الأصلي بكل العلامات |
| `Download-Traffic-Signs.ps1` | سكريبت PowerShell لتنزيل الصور |
| `src/main/java/com/readyroad/util/TrafficSignExtractor.java` | Java extractor |
| `src/main/resources/db/migration/V6__Add_Traffic_Signs_Data.sql` | Migration SQL |
| `mobile_app/pubspec.yaml` | تكوين Flutter assets |
| `mobile_app/assets/traffic_signs/` | مجلد الصور |

---

## المراجع / References

- **المصدر / Source**: [verkeersbord.be](https://www.verkeersbord.be/)
- **التشريعات**: Belgian Traffic Signs Code
- **المعايير**: Vienna Convention on Road Signs and Signals

---

## الدعم / Support

للمساعدة أو الأسئلة:
- 📧 Email: support@readyroad.com
- 📝 Documentation: README.md
- 🐛 Issues: GitHub Issues

---

**آخر تحديث / Last Updated**: 2024
**الإصدار / Version**: 1.0.0
**الحالة / Status**: ✅ جاهز للتنفيذ / Ready for Implementation
