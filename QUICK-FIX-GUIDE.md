# 🚀 دليل الإصلاح السريع - IntelliJ IDEA

## ⚡ الإصلاحات المطلوبة (3 دقائق)

---

## 📌 الخطوة 1: إصلاح IntelliJ Run Configuration

### افتح Run/Debug Configurations:
- اضغط على القائمة بجانب زر ▶ (Run)
- اختر "Edit Configurations..."

### الإصلاحات المطلوبة:

#### ❌ 1. احذف VM Options (السطر الأحمر الأول)
```
BEFORE (خطأ):
-Djwt.secret-key=MRk2DTYum+VZTg9cQa5izYrJjcCjX8XTZDBOaJRGPRs+Y=

AFTER (صحيح):
[اتركه فارغاً تماماً - احذف السطر]
```

**لماذا؟** JWT secret يُقرأ من application-prod.properties تلقائياً

#### ✅ 2. تأكد من Main Class (صحيح - لا تغيير)
```
com.readyroad.readyroadbackend.ReadyroadApplication
```

#### ✅ 3. Active Profiles (صحيح - لا تغيير)
```
prod
```

#### ✅ 4. Environment Variables (صحيح - لا تغيير)
```
DB_PASSWORD=Hh06101987@
```

### اضغط "Apply" ثم "OK"

---

## 📌 الخطوة 2: إعداد Production Database

### نفذ هذا الأمر في Terminal:

```bash
mysql -u root -p < setup-database-prod.sql
```

**أو يدوياً:**

```sql
mysql -u root -p

CREATE DATABASE IF NOT EXISTS readyroad_prod
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

GRANT ALL PRIVILEGES ON readyroad_prod.* TO 'haydar'@'localhost';
FLUSH PRIVILEGES;

exit;
```

### تحقق من نجاح الإعداد:

```bash
mysql -u haydar -pHh06101987@ readyroad_prod
```

إذا دخلت بدون أخطاء، الإعداد نجح! ✅

---

## 📌 الخطوة 3: تشغيل التطبيق

### في IntelliJ IDEA:

1. اختر `ReadyroadApplication` من القائمة المنسدلة
2. اضغط زر ▶ (Run) الأخضر
3. راقب Console

### النتائج المتوقعة:

```
✅ JWT Service Initialization ===
✅ Secret Key Length: 96 characters
✅ JWT secret key is valid: 72 bytes (576 bits)
✅ Flyway migration completed successfully
✅ Started ReadyroadApplication in ~8 seconds
```

---

## 🎯 الملخص البصري

```
IntelliJ Run Configuration:
┌─────────────────────────────────────────────────┐
│ Name: ReadyroadApplication                      │
├─────────────────────────────────────────────────┤
│ VM options:       [EMPTY - DELETE THIS LINE] ❌ │
│                   ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^│
│                   احذف السطر الأحمر!            │
├─────────────────────────────────────────────────┤
│ Main class:       com.readyroad.readyroad...  ✅│
├─────────────────────────────────────────────────┤
│ Active profiles:  prod                        ✅│
├─────────────────────────────────────────────────┤
│ Env variables:    DB_PASSWORD=Hh06101987@    ✅│
└─────────────────────────────────────────────────┘
```

---

## ❗ نقطة مهمة جداً

### لماذا يجب حذف VM Options؟

**المشكلة:**
```
-Djwt.secret-key=MRk2DTYum+VZTg9cQa5izYrJjcCjX8XTZDBOaJRGPRs+Y=
```

هذا السطر يحاول تحديد JWT secret عبر VM options، لكن:

1. ❌ الكود يقرأ من `jwt.secret-key` في application.yml
2. ❌ هذا يسبب تضارب
3. ❌ القيمة هنا قد لا تطابق القيمة في config files
4. ❌ يجعل التطبيق يستخدم قيمتين مختلفتين

**الحل الصحيح:**
- احذف السطر من VM options
- دع التطبيق يقرأ من application-prod.properties
- JWT secret محدد هناك بشكل صحيح

---

## 🔍 التحقق من الإعدادات

### Checklist قبل التشغيل:

```
☐ VM Options فارغ (حذفت السطر الأحمر)
☐ Main Class = com.readyroad.readyroadbackend.ReadyroadApplication
☐ Active Profiles = prod
☐ Environment Variables = DB_PASSWORD=Hh06101987@
☐ Database readyroad_prod موجود
☐ User haydar له صلاحيات على readyroad_prod
```

إذا كل النقاط ✅، اضغط Run! 🚀

---

## 📂 الملفات المحدثة

| الملف | الحالة | التغيير |
|------|--------|----------|
| `application-prod.properties` | ✅ محدث | Fixed MySQLDialect + jwt.secret-key |
| `setup-database-prod.sql` | ✅ جديد | SQL script للـ production database |
| `INTELLIJ-RUN-CONFIG.md` | ✅ جديد | دليل مفصل للإعدادات |
| `QUICK-FIX-GUIDE.md` | ✅ جديد | هذا الملف - دليل سريع |

---

## 💡 نصائح إضافية

### إذا فشل الاتصال بـ Database:

```bash
# تحقق من وجود database
mysql -u root -p -e "SHOW DATABASES LIKE 'readyroad_prod';"

# تحقق من صلاحيات user
mysql -u root -p -e "SHOW GRANTS FOR 'haydar'@'localhost';"

# اختبر الاتصال
mysql -u haydar -pHh06101987@ readyroad_prod
```

### إذا ظهرت مشاكل في JWT:

```bash
# تأكد من أن VM options فارغ
# تحقق من application-prod.properties يحتوي على:
jwt.secret-key=${JWT_SECRET:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970337336763979244226452948404D6351}
```

---

## ✅ النتيجة النهائية

بعد تطبيق هذه الإصلاحات:

✅ IntelliJ configuration صحيح
✅ Database permissions صحيحة
✅ JWT configuration صحيح
✅ Hibernate dialect صحيح
✅ التطبيق يعمل بنجاح

🎉 جاهز للتشغيل!
