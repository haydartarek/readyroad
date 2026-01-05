# 🚀 كيفية تثبيت Flutter - خطوات سريعة

---

## الطريقة الأولى: تثبيت تلقائي (الأسهل) ⚡

### الخطوات:

#### 1️⃣ افتح PowerShell كـ Administrator
- اضغط على زر Windows
- ابحث عن "PowerShell"
- اضغط كليك يمين → "Run as Administrator"

#### 2️⃣ شغّل السكريبت
```powershell
cd C:\Users\fqsdg\IdeaProjects\readyroad
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
.\Install-Flutter.ps1
```

#### 3️⃣ انتظر حتى ينتهي التثبيت (5-10 دقائق)

#### 4️⃣ أغلق PowerShell وافتح واحد جديد

#### 5️⃣ تحقق من التثبيت
```bash
flutter --version
flutter doctor
```

---

## الطريقة الثانية: تثبيت يدوي 📥

### الخطوات:

#### 1️⃣ تحميل Flutter SDK
- افتح: https://docs.flutter.dev/get-started/install/windows
- اضغط على "Download Flutter SDK"
- حمّل ملف `.zip`

#### 2️⃣ فك الضغط
- استخرج الملف إلى: `C:\flutter`
- يجب أن يكون المسار: `C:\flutter\bin\flutter.bat`

#### 3️⃣ إضافة Flutter للـ PATH
##### خيار أ: PowerShell (سريع)
```powershell
# Run as Administrator
[Environment]::SetEnvironmentVariable("Path", "$env:Path;C:\flutter\bin", "Machine")
```

##### خيار ب: يدوياً
1. اضغط Windows + كليك يمين على "This PC" → Properties
2. Advanced system settings → Environment Variables
3. تحت "System variables" اختر "Path" → Edit
4. اضغط New
5. أضف: `C:\flutter\bin`
6. OK → OK → OK

#### 4️⃣ أعد تشغيل الكمبيوتر (أو أغلق جميع النوافذ)

#### 5️⃣ تحقق من التثبيت
```bash
flutter --version
flutter doctor
```

---

## الطريقة الثالثة: باستخدام Batch File 📜

### الخطوات:

#### 1️⃣ Double-click على:
```
INSTALL_FLUTTER.bat
```

#### 2️⃣ انتظر حتى ينتهي

#### 3️⃣ أغلق CMD وافتح واحد جديد

#### 4️⃣ تحقق
```bash
flutter doctor
```

---

## التحقق من التثبيت ✅

بعد التثبيت، شغّل:

```bash
flutter doctor
```

**يجب أن ترى:**
```
Doctor summary (to see all details, run flutter doctor -v):
[✓] Flutter (Channel stable, 3.27.1, on Microsoft Windows...)
[✓] Windows Version (Installed version of Windows is version 10 or higher)
[!] Android toolchain - develop for Android devices
[!] Chrome - develop for the web
[!] Android Studio (not installed)
```

---

## إصلاح المشاكل الشائعة 🔧

### مشكلة: Command not found
**الحل:**
1. تأكد من إضافة `C:\flutter\bin` للـ PATH
2. أعد فتح Terminal جديد
3. أعد تشغيل الكمبيوتر

### مشكلة: Android licenses
**الحل:**
```bash
flutter doctor --android-licenses
```
اضغط `y` لجميع الأسئلة

### مشكلة: Android Studio not found
**الحل:**
- حمّل Android Studio من: https://developer.android.com/studio
- ثبّته وافتح Flutter project

---

## بعد التثبيت الناجح 🎉

### 1️⃣ تحقق من الأجهزة المتاحة
```bash
flutter devices
```

### 2️⃣ أنشئ مشروع تجريبي
```bash
flutter create test_app
cd test_app
flutter run -d chrome
```

### 3️⃣ ارجع لـ Ready Road Project
```bash
cd C:\Users\fqsdg\IdeaProjects\readyroad
```

ثم قل لي: **"Flutter جاهز"** وسأكمل Phase 0 - Mobile! 🚀

---

## الملفات المساعدة 📁

- ✅ `INSTALL_FLUTTER.bat` - تثبيت سريع (CMD)
- ✅ `Install-Flutter.ps1` - تثبيت متقدم (PowerShell)
- ✅ `FLUTTER_SETUP_GUIDE.md` - دليل مفصل

---

## الوقت المتوقع ⏱️

- **التحميل:** 3-5 دقائق (حسب الإنترنت)
- **الاستخراج:** 2-3 دقائق
- **الإعداد:** 1 دقيقة
- **المجموع:** ~10 دقائق

---

**جاهز للبدء؟** 

اختر الطريقة وابدأ! 💪

