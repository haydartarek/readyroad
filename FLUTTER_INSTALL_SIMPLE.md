# 📥 خطوات تثبيت Flutter - النسخة المبسطة

---

## ⚡ الطريقة الأسرع (5 دقائق)

### 1️⃣ حمّل Flutter
افتح هذا الرابط وحمّل الملف:
👉 https://docs.flutter.dev/get-started/install/windows

أو حمّل مباشرة من:
👉 https://storage.googleapis.com/flutter_infra_release/releases/stable/windows/flutter_windows_3.27.1-stable.zip

### 2️⃣ استخرج الملف
- استخرج الـ ZIP إلى: `C:\`
- يجب أن يصبح المسار: `C:\flutter\bin\flutter.bat`

### 3️⃣ أضف Flutter للـ PATH

#### الطريقة السهلة:
افتح PowerShell وشغّل:
```powershell
cd C:\Users\fqsdg\IdeaProjects\readyroad
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
.\Quick-Install-Flutter.ps1
```

#### أو يدوياً:
```powershell
[Environment]::SetEnvironmentVariable("Path", "$env:Path;C:\flutter\bin", "User")
```

### 4️⃣ تحقق
افتح terminal جديد:
```bash
flutter --version
```

---

## ✅ تحقق من الإعداد

```bash
flutter doctor
```

يجب أن ترى:
```
[✓] Flutter (Channel stable, 3.27.1)
[✓] Windows Version
```

---

## 🚀 بعد التثبيت

قل لي: **"تم تثبيت Flutter"**

وسأكمل إعداد مشروع Ready Road Mobile! 💪

---

## المساعدة 🆘

إذا واجهت مشاكل:
1. تأكد من الاستخراج إلى `C:\flutter`
2. افتح terminal جديد بعد إضافة PATH
3. شغّل `flutter doctor` لفحص المشاكل

---

**الملفات المساعدة:**
- `Quick-Install-Flutter.ps1` - سكريبت سريع
- `HOW_TO_INSTALL_FLUTTER.md` - شرح مفصل
- `FLUTTER_SETUP_GUIDE.md` - الدليل الكامل

