# IntelliJ IDEA Run Configuration - الإعدادات الصحيحة

## 🔧 الإعدادات الحالية (في الصورة)

### ❌ المشاكل المحددة بالأحمر:

1. **VM Options (يجب حذفه!):**
   ```
   -Djwt.secret-key=MRk2DTYum+VZTg9cQa5izYrJjcCjX8XTZDBOaJRGPRs+Y=
   ```
   ❌ **احذف هذا السطر بالكامل** - JWT secret يُقرأ من application.yml

2. **Main Class:**
   ```
   com.readyroad.readyroadbackend.ReadyroadApplication
   ```
   ✅ هذا صحيح

---

## ✅ الإعدادات الصحيحة

### 1. General Settings

| Setting | Value |
|---------|-------|
| **Name** | ReadyroadApplication |
| **Run on** | Local machine |
| **JDK** | java 24 SDK of 'readyroad-backend' module |

### 2. Main Class
```
com.readyroad.readyroadbackend.ReadyroadApplication
```
✅ صحيح - لا تغيير مطلوب

### 3. VM Options
```
(leave empty - DELETE the existing line)
```
❌ **احذف السطر الموجود:**
```
-Djwt.secret-key=MRk2DTYum+VZTg9cQa5izYrJjcCjX8XTZDBOaJRGPRs+Y=
```

**لماذا؟**
- JWT secret يُقرأ تلقائياً من `application-prod.yml`
- تحديده في VM options يسبب تضارب
- يجب أن يكون فارغاً

### 4. Active Profiles
```
prod
```
✅ صحيح

### 5. Environment Variables
```
DB_PASSWORD=Hh06101987@
```
✅ صحيح

**أو بشكل أفضل (إذا تريد تحديد JWT أيضاً):**
```
DB_PASSWORD=Hh06101987@;JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970337336763979244226452948404D6351
```

---

## 📋 خطوات الإصلاح في IntelliJ IDEA

### الطريقة 1: تعديل الإعدادات الموجودة

1. **افتح Run/Debug Configurations:**
   - من القائمة: `Run` → `Edit Configurations...`
   - أو اضغط على الإعدادات في الصورة

2. **احذف VM Options:**
   - ابحث عن حقل "VM options"
   - احذف السطر:
     ```
     -Djwt.secret-key=MRk2DTYum+VZTg9cQa5izYrJjcCjX8XTZDBOaJRGPRs+Y=
     ```
   - اترك الحقل **فارغاً تماماً**

3. **تحقق من Active Profiles:**
   - تأكد أن القيمة: `prod`
   - ✅ صحيح

4. **تحقق من Environment Variables:**
   - الحد الأدنى:
     ```
     DB_PASSWORD=Hh06101987@
     ```
   - موصى به:
     ```
     DB_PASSWORD=Hh06101987@;JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970337336763979244226452948404D6351
     ```

5. **اضغط Apply ثم OK**

---

### الطريقة 2: حذف الإعدادات وإنشاء جديدة

إذا كانت المشاكل مستمرة:

1. **احذف الإعدادات الحالية:**
   - في Run/Debug Configurations
   - اختر `ReadyroadApplication`
   - اضغط على `-` (Delete)

2. **أنشئ إعدادات جديدة:**
   - اضغط `+` → `Spring Boot`
   - املأ الحقول كالتالي:

```
Name: ReadyroadApplication-Prod
Main class: com.readyroad.readyroadbackend.ReadyroadApplication
Active profiles: prod
Environment variables: DB_PASSWORD=Hh06101987@
VM options: (اتركه فارغاً!)
```

3. **اضغط Apply ثم OK**

---

## 🎯 الإعدادات الصحيحة الكاملة

### Configuration Screenshot

```
┌─────────────────────────────────────────────────────────────┐
│ Run/Debug Configurations                                     │
├─────────────────────────────────────────────────────────────┤
│ Name:                    ReadyroadApplication               │
│ Run on:                  ○ Local machine                    │
├─────────────────────────────────────────────────────────────┤
│ java 24 SDK of 'readyroad-backend' module                   │
│                                                              │
│ VM options:              [EMPTY - DELETE EXISTING LINE]     │
│                                                              │
│ Main class:              com.readyroad.readyroadbackend.    │
│                          ReadyroadApplication               │
├─────────────────────────────────────────────────────────────┤
│ Active profiles:         prod                               │
│                                                              │
│ Environment variables:   DB_PASSWORD=Hh06101987@            │
│                                                              │
│ ☑ Open run/debug tool window when started                   │
│ ☑ Add dependencies with "provided" scope to classpath       │
└─────────────────────────────────────────────────────────────┘
```

---

## 🗂️ ملفات الإعدادات

### application-prod.properties (تم تحديثه)

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/readyroad_prod?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC
spring.datasource.username=haydar
spring.datasource.password=${DB_PASSWORD}

# JWT Configuration (FIXED)
jwt.secret-key=${JWT_SECRET:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970337336763979244226452948404D6351}
jwt.expiration=86400000
jwt.issuer=readyroad-backend

# Hibernate (FIXED)
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

---

## 🚀 اختبار الإعدادات

### 1. Setup Database (مرة واحدة فقط)

```sql
-- من MySQL CLI
mysql -u root -p

CREATE DATABASE IF NOT EXISTS readyroad_prod
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

GRANT ALL PRIVILEGES ON readyroad_prod.* TO 'haydar'@'localhost';
FLUSH PRIVILEGES;

exit;
```

### 2. Run من IntelliJ IDEA

1. اختر `ReadyroadApplication` من القائمة المنسدلة
2. اضغط على زر ▶ (Run) الأخضر
3. راقب Console output

### 3. النتائج المتوقعة

```
✅ JWT Service Initialization ===
✅ Secret Key Length: 96 characters
✅ JWT secret key is valid: 72 bytes (576 bits)
✅ Flyway migration completed successfully
✅ HikariPool-1 - Start completed
✅ Started ReadyroadApplication in X.XXX seconds
```

---

## ❌ الأخطاء المحتملة

### Error 1: JWT Secret Key Invalid

```
❌ CRITICAL: JWT secret key is not configured properly!
```

**الحل:**
- تأكد من حذف VM options
- تأكد من `jwt.secret-key` في application-prod.properties

### Error 2: Access Denied for Database

```
Access denied for user 'haydar'@'localhost' to database 'readyroad_prod'
```

**الحل:**
```sql
GRANT ALL PRIVILEGES ON readyroad_prod.* TO 'haydar'@'localhost';
FLUSH PRIVILEGES;
```

### Error 3: ClassNotFoundException MySQL8Dialect

```
ClassNotFoundException: org.hibernate.dialect.MySQL8Dialect
```

**الحل:**
- تم إصلاحه في application-prod.properties
- تأكد من استخدام `MySQLDialect` وليس `MySQL8Dialect`

---

## 📝 ملخص التغييرات المطلوبة

| الإعداد | القيمة الحالية (خطأ) | القيمة الصحيحة |
|---------|----------------------|-----------------|
| **VM Options** | `-Djwt.secret-key=...` | (فارغ) ❌ احذفه |
| **Main Class** | `com.readyroad.readyroadbackend.ReadyroadApplication` | ✅ صحيح |
| **Active Profiles** | `prod` | ✅ صحيح |
| **Env Variables** | `DB_PASSWORD=Hh06101987@` | ✅ صحيح |

---

## ✅ Checklist

قبل تشغيل التطبيق:

- [ ] ✅ حذف VM options (السطر الأحمر الأول)
- [ ] ✅ تأكد من Main class صحيح
- [ ] ✅ Active profile = `prod`
- [ ] ✅ Environment variable `DB_PASSWORD` موجود
- [ ] ✅ Database `readyroad_prod` موجود
- [ ] ✅ User `haydar` له صلاحيات على database
- [ ] ✅ application-prod.properties محدث (MySQLDialect, jwt.secret-key)

بعد إكمال هذه الخطوات، اضغط Run! 🚀
