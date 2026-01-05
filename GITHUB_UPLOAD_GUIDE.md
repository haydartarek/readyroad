# 📤 كيفية رفع المشروع على GitHub

---

## المتطلبات الأولية

### 1️⃣ تثبيت Git
إذا لم يكن Git مثبتاً:

**التحميل:**
- افتح: https://git-scm.com/download/win
- حمّل Git for Windows
- ثبّته (Next → Next → Finish)

**التحقق:**
```bash
git --version
```

---

## 📋 الخطوات

### الطريقة 1: باستخدام GitHub Desktop (الأسهل) 🖱️

#### 1. حمّل GitHub Desktop
- https://desktop.github.com/
- ثبّته وسجّل الدخول بحسابك

#### 2. أضف المشروع
- File → Add Local Repository
- اختر: `C:\Users\fqsdg\IdeaProjects\readyroad`
- اضغط "Add Repository"

#### 3. Create Repository على GitHub
- اضغط "Publish repository"
- Name: `readyroad`
- Description: "Traffic Signs Learning Application"
- ✅ Keep this code private (أو public)
- اضغط "Publish Repository"

**✅ تم! المشروع الآن على GitHub**

---

### الطريقة 2: باستخدام Command Line 💻

#### الخطوة 1: إنشاء Repository على GitHub.com

1. **اذهب إلى:** https://github.com/new
2. **Repository name:** `readyroad`
3. **Description:** "Traffic Signs Learning Application - Backend"
4. **Privacy:** اختر Public أو Private
5. **✅ اضغط "Create repository"**

#### الخطوة 2: رفع المشروع من Terminal

افتح PowerShell في مجلد المشروع:

```bash
# الانتقال لمجلد المشروع
cd C:\Users\fqsdg\IdeaProjects\readyroad

# تهيئة Git
git init

# إضافة جميع الملفات
git add .

# عمل Commit أول
git commit -m "Phase 0: Backend Complete - Spring Boot + MySQL + Clean Architecture"

# ربط المشروع بـ GitHub (غيّر YOUR_USERNAME)
git remote add origin https://github.com/YOUR_USERNAME/readyroad.git

# رفع المشروع
git branch -M main
git push -u origin main
```

**ملاحظة:** استبدل `YOUR_USERNAME` باسم المستخدم الخاص بك على GitHub!

---

## 🔐 المصادقة

عند الرفع، قد يطلب منك Git المصادقة:

### الخيار 1: Personal Access Token (موصى به)
1. اذهب إلى: https://github.com/settings/tokens
2. Generate new token (classic)
3. اختر: repo (full control)
4. انسخ الـ Token
5. استخدمه كـ password عند الرفع

### الخيار 2: GitHub CLI
```bash
# تثبيت GitHub CLI
winget install GitHub.cli

# تسجيل الدخول
gh auth login

# رفع المشروع
gh repo create readyroad --private --source=. --push
```

---

## 📁 ما سيتم رفعه

✅ **الملفات المهمة:**
- `src/` - جميع ملفات Java
- `pom.xml` - Maven dependencies
- `README.md` - توثيق المشروع
- `LICENSE` - رخصة MIT
- `PHASES.md` - خطة المشروع
- `*.md` - جميع ملفات التوثيق
- `.gitignore` - الملفات المستثناة

❌ **الملفات المستثناة** (في `.gitignore`):
- `target/` - Compiled files
- `.idea/` - IntelliJ settings
- `*.class` - Java compiled classes
- `*.log` - Log files

---

## ✅ التحقق من النجاح

بعد الرفع:
1. افتح: `https://github.com/YOUR_USERNAME/readyroad`
2. يجب أن ترى:
   - ✅ README.md معروض
   - ✅ جميع المجلدات والملفات
   - ✅ Commit الأول

---

## 🔄 تحديثات مستقبلية

بعد إضافة ملفات جديدة أو تعديلات:

```bash
# إضافة التعديلات
git add .

# عمل Commit
git commit -m "وصف التعديل"

# رفع التحديث
git push
```

---

## 📝 رسائل Commit الموصى بها

استخدم رسائل واضحة:

```bash
# أمثلة جيدة:
git commit -m "Phase 0: Backend Complete"
git commit -m "Add Category API endpoints"
git commit -m "Fix MySQL connection issues"
git commit -m "Add multilingual support"

# أمثلة سيئة:
git commit -m "update"
git commit -m "fix"
git commit -m "changes"
```

---

## 🌿 Branching (اختياري)

للعمل على features جديدة:

```bash
# إنشاء branch جديد
git checkout -b feature/flutter-setup

# العمل على التعديلات...
git add .
git commit -m "Setup Flutter project structure"

# رفع الـ branch
git push -u origin feature/flutter-setup

# العودة للـ main
git checkout main
```

---

## 🆘 حل المشاكل

### مشكلة: Git not found
**الحل:** ثبّت Git من https://git-scm.com/

### مشكلة: Permission denied
**الحل:** استخدم Personal Access Token بدلاً من Password

### مشكلة: Repository already exists
**الحل:**
```bash
git remote remove origin
git remote add origin https://github.com/YOUR_USERNAME/readyroad.git
git push -u origin main
```

### مشكلة: Large files
**الحل:** تأكد من أن `.gitignore` يستثني `target/` و `.idea/`

---

## 📊 ملخص الخطوات السريعة

```bash
# 1. تهيئة Git
cd C:\Users\fqsdg\IdeaProjects\readyroad
git init

# 2. إضافة الملفات
git add .

# 3. أول Commit
git commit -m "Phase 0: Backend Complete"

# 4. ربط بـ GitHub (أنشئ repo أولاً على github.com)
git remote add origin https://github.com/YOUR_USERNAME/readyroad.git

# 5. رفع المشروع
git branch -M main
git push -u origin main
```

---

## 🎉 بعد الرفع

المشروع الآن على GitHub! يمكنك:
- ✅ مشاركة الرابط مع الفريق
- ✅ استنساخه على أجهزة أخرى: `git clone https://github.com/YOUR_USERNAME/readyroad.git`
- ✅ متابعة العمل على Phase 0 - Mobile
- ✅ إضافة Contributors

---

**جاهز للرفع؟** 🚀

اختر الطريقة المناسبة وابدأ!

