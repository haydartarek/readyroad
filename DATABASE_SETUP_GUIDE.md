# إعداد قاعدة البيانات readyroad_prod

## ✅ التعديلات المطبقة

### 1. إعدادات قاعدة البيانات (Database Configuration)

تم تعديل الملفات التالية للإشارة إلى `readyroad_prod`:

- ✅ `src/main/resources/application.yml`
- ✅ `src/main/resources/application-secure.yml`
- ✅ `src/main/resources/application-prod.properties` (كان صحيحاً مسبقاً)

**التغييرات:**

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/readyroad_prod?...
    username: haydar
    password: Hh06101987@
  
  flyway:
    url: jdbc:mysql://localhost:3306/readyroad_prod?...
    user: haydar
    password: Hh06101987@
```

---

## 🔧 خطوات التشغيل

### الخطوة 1: إعداد قاعدة البيانات

قم بتنفيذ SQL script لإنشاء القاعدة ومنح الصلاحيات:

```powershell
# من PowerShell
mysql -u root -p < setup_readyroad_prod_db.sql

# أو من MySQL Workbench
# افتح ونفذ محتوى setup_readyroad_prod_db.sql
```

**أو يدوياً من MySQL command line:**

```sql
CREATE DATABASE IF NOT EXISTS readyroad_prod
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

GRANT ALL PRIVILEGES ON readyroad_prod.* TO 'haydar'@'localhost';
FLUSH PRIVILEGES;
```

### الخطوة 2: تشغيل Backend

استخدم الـ script الجاهز:

```powershell
.\start_backend_prod.ps1
```

**أو يدوياً:**

```powershell
cd C:\Users\heyde\Desktop\end_project\readyroad
.\mvnw.cmd -DskipTests spring-boot:run `
  "-Dspring-boot.run.arguments=--spring.profiles.active=secure --logging.level.com.readyroad=DEBUG"
```

### الخطوة 3: التحقق من الاتصال

بعد بدء التشغيل، اختبر endpoint:

```powershell
# اختبار Smart Quiz
curl.exe -i "http://localhost:8890/api/smart-quiz/random?count=5"

# اختبار Health Check
curl.exe -i "http://localhost:8890/actuator/health"
```

---

## 🔍 استكشاف الأخطاء

### خطأ: Access denied for user 'haydar'@'localhost' to database 'readyroad_prod'

**الحل:**

```sql
-- تنفيذ كـ root
GRANT ALL PRIVILEGES ON readyroad_prod.* TO 'haydar'@'localhost';
FLUSH PRIVILEGES;
```

### خطأ: Unknown database 'readyroad'

**السبب:** لم يتم تحديث application-secure.yml  
**الحل:** تم إصلاحه بالفعل! تأكد من أن `url` يشير إلى `readyroad_prod`

### خطأ: Flyway validation failed

**الحل:** حذف Flyway history والبدء من جديد:

```sql
USE readyroad_prod;
DROP TABLE IF EXISTS flyway_schema_history;
```

---

## 📁 الملفات الجديدة

- `setup_readyroad_prod_db.sql` - SQL script لإعداد القاعدة
- `start_backend_prod.ps1` - PowerShell script للتشغيل السريع
- `DATABASE_SETUP_GUIDE.md` - هذا الملف

---

## 📝 ملاحظات مهمة

1. **قاعدة البيانات:** الآن `readyroad_prod` بدلاً من `readyroad`
2. **المستخدم:** `haydar` (بدلاً من root)
3. **كلمة المرور:** `Hh06101987@`
4. **المنفذ:** `8890` (كما هو)
5. **Profile:** `secure` (يتطلب JWT authentication)

---

## ✅ الخطوة التالية

بعد تطبيق هذه الإعدادات، يجب أن:

- ✅ يختفي خطأ "Access denied to database 'readyroad'"
- ✅ يبدأ Backend بنجاح على المنفذ 8890
- ✅ تعمل Flyway migrations على readyroad_prod
- 🔍 نستطيع الآن التركيز على إصلاح خطأ 500 في SmartQuiz endpoint
