# ⚡ Database Setup - تنفيذ فوري

## 🎯 الهدف
إنشاء databases المطلوبة ومنح صلاحيات لـ user `haydar`

---

## 📋 الوضع الحالي

✅ **readyroad_prod** - موجود ولديه صلاحيات
❌ **readyroad** - غير موجود أو بدون صلاحيات

---

## 🚀 الحل - 3 خيارات سريعة

---

### ⭐ الخيار 1: PowerShell Script (موصى به)

```powershell
# في PowerShell من مجلد المشروع:
./run-database-setup.ps1
```

سيطلب منك كلمة مرور MySQL root، ثم سينفذ كل شيء تلقائياً ✅

---

### ⭐ الخيار 2: SQL Script مباشر

```bash
# من Command Prompt أو PowerShell:
mysql -u root -p < setup-all-databases.sql
```

أدخل كلمة مرور root عند الطلب ✅

---

### ⭐ الخيار 3: Manual Commands (يدوي)

1. **افتح MySQL CLI كـ root:**
```bash
mysql -u root -p
```

2. **نفذ هذه الأوامر:**
```sql
-- إنشاء database للتطوير
CREATE DATABASE IF NOT EXISTS readyroad
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- إنشاء database للإنتاج (إذا لم يكن موجود)
CREATE DATABASE IF NOT EXISTS readyroad_prod
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- منح صلاحيات لـ haydar على كلا الـ databases
GRANT ALL PRIVILEGES ON readyroad.* TO 'haydar'@'localhost';
GRANT ALL PRIVILEGES ON readyroad_prod.* TO 'haydar'@'localhost';

-- تطبيق التغييرات
FLUSH PRIVILEGES;

-- التحقق
SHOW GRANTS FOR 'haydar'@'localhost';

-- الخروج
exit;
```

---

## ✅ التحقق من النجاح

بعد تنفيذ أي من الخيارات أعلاه، نفذ:

```bash
mysql -u haydar -pHh06101987@ -e "SHOW DATABASES LIKE 'readyroad%';"
```

**النتيجة المتوقعة:**
```
Database (readyroad%)
readyroad
readyroad_prod
```

إذا ظهر الاثنان، الإعداد نجح! ✅

---

## 🎯 الخطوة التالية

بعد إعداد الـ databases:

### 1. تنظيف البناء السابق:
```bash
./mvnw clean
```

### 2. إعادة البناء:
```bash
./mvnw compile -DskipTests
```

### 3. تشغيل التطبيق:

**للتطوير (readyroad):**
```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

**للإنتاج (readyroad_prod):**
```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

**للإعدادات الآمنة (readyroad):**
```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=secure"
```

---

## 📊 ملخص الـ Profiles

| Profile | Database | DDL Mode | JWT | Security |
|---------|----------|----------|-----|----------|
| **dev** | readyroad | update | Enabled | Relaxed |
| **secure** | readyroad | update | Enabled | Strict |
| **prod** | readyroad_prod | validate | Enabled | Strict |

---

## ⚠️ ملاحظات مهمة

### 1. Database Names في Config Files:

**application-dev.yml & application-secure.yml:**
```yaml
url: jdbc:mysql://localhost:3306/readyroad?...
```
👉 يستخدمان `readyroad`

**application-prod.properties:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/readyroad_prod?...
```
👉 يستخدم `readyroad_prod`

### 2. لذلك نحتاج كلا الـ databases:
- ✅ `readyroad` - للـ dev & secure profiles
- ✅ `readyroad_prod` - للـ prod profile

---

## 🔧 إذا واجهت مشاكل

### Problem: "Access denied"
```
ERROR 1044 (42000): Access denied for user 'haydar'@'localhost'
```

**Solution:** استخدم root للإعداد (أي من الخيارات أعلاه)

### Problem: "Unknown database"
```
Unknown database 'readyroad'
```

**Solution:** نفذ CREATE DATABASE commands أعلاه

### Problem: "Communications link failure"
```
Communications link failure
```

**Solution:** تأكد من أن MySQL service يعمل:
```bash
# Windows
net start MySQL80

# Linux/Mac
sudo service mysql start
```

---

## ✅ Quick Checklist

قبل تشغيل التطبيق:

```
☐ Database 'readyroad' موجود
☐ Database 'readyroad_prod' موجود
☐ User 'haydar' له صلاحيات ALL على readyroad.*
☐ User 'haydar' له صلاحيات ALL على readyroad_prod.*
☐ Character set: utf8mb4_unicode_ci
☐ Connection test successful: mysql -u haydar -p...
```

---

## 🎉 بعد الإعداد

التطبيق جاهز للعمل! اختر أي profile وشغّل:

```bash
# Development mode
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

أو من IntelliJ IDEA:
1. Edit Configurations
2. Active profiles: `dev` أو `secure` أو `prod`
3. ▶ Run

🚀 **Done!**
