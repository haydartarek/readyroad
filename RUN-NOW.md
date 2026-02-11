# 🚀 تشغيل ReadyRoad الآن

## ✅ الخطوة 1: افتح PowerShell

اضغط `Win + X` واختر **Windows PowerShell (Admin)**

---

## ✅ الخطوة 2: انتقل للمجلد

```powershell
cd C:\Users\heyde\Desktop\end_project\readyroad
```

---

## ✅ الخطوة 3: شغّل التطبيق

### خيار 1: تشغيل تلقائي (موصى به) ⚡

```powershell
./build-and-run.ps1
```

هذا السكريبت سيقوم بـ:
- ✅ تعيين Environment Variables
- ✅ بناء المشروع
- ✅ تشغيل التطبيق

---

### خيار 2: تشغيل يدوي

#### أ. تعيين Environment Variables:

```powershell
$env:DB_USERNAME="haydar"
$env:DB_PASSWORD="Hh06101987@"
$env:DB_HOST="localhost"
$env:DB_PORT="3306"
$env:DB_NAME="readyroad_prod"
$env:ADMIN_DEFAULT_PASSWORD="Admin2026Secure!"
$env:JWT_SECRET="404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970337336763979244226452948404D6351"
$env:SPRING_PROFILES_ACTIVE="prod"
```

#### ب. بناء المشروع:

```powershell
./mvnw clean package -DskipTests
```

#### ج. تشغيل التطبيق:

```powershell
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

---

## ✅ الخطوة 4: انتظر حتى يكتمل التشغيل

### انتظر هذه الرسائل في Console:
```
✅ JWT Service Initialization
✅ Flyway migration completed successfully
✅ Started ReadyroadApplication in X seconds
```

---

## ✅ الخطوة 5: اختبر التطبيق

### Health Check:
```powershell
curl http://localhost:8890/actuator/health
```

النتيجة المتوقعة: `{"status":"UP"}`

---

### Test Login:
```powershell
$body = @{
    username = "admin"
    password = "Admin2026Secure!"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri http://localhost:8890/api/auth/login `
    -Method POST `
    -Body $body `
    -ContentType "application/json"

Write-Host "JWT Token: $($response.token)"
```

---

## 🌐 روابط التطبيق

| الخدمة | الرابط |
|--------|--------|
| **Backend API** | http://localhost:8890 |
| **Swagger UI** | http://localhost:8890/swagger-ui.html |
| **Health Check** | http://localhost:8890/actuator/health |

---

## 📊 أوامر مفيدة

```powershell
# عرض حالة الـ containers
docker compose ps

# عرض الـ logs
docker compose logs -f backend
docker compose logs -f mysql

# إيقاف
docker compose stop

# إعادة تشغيل
docker compose restart backend

# حذف كل شيء (⚠️ يحذف البيانات)
docker compose down -v

# إعادة البناء
docker compose up -d --build
```

---

## 🐛 حل المشاكل

### المشكلة: Port مشغول
```powershell
# إيجاد البرنامج المستخدم للـ port
netstat -ano | findstr :8890

# إيقافه (استبدل PID)
taskkill /PID <PID> /F
```

### المشكلة: MySQL لا يعمل
```powershell
# تحقق من حالة MySQL
docker compose logs mysql

# إعادة تشغيل
docker compose restart mysql
```

### المشكلة: Backend لا يبدأ
```powershell
# شاهد الأخطاء
docker compose logs backend | Select-String "ERROR"

# تحقق من Environment Variables
docker compose exec backend env | Select-String "DB_"
```

---

## ✅ علامات النجاح

عندما يعمل كل شيء بنجاح، سترى:

1. ✅ Container readyroad-mysql: **Up (healthy)**
2. ✅ Container readyroad-backend: **Up (healthy)**
3. ✅ Logs: **"Started ReadyroadApplication"**
4. ✅ Health: **{"status":"UP"}**
5. ✅ Login: **يعيد JWT token**

---

## 🎉 بعد التشغيل

جرب Swagger UI:
```powershell
Start-Process http://localhost:8890/swagger-ui.html
```

---

**جاهز للاستخدام! 🚀**
