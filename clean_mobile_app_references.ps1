# ✅ تنظيف كامل لإشارات mobile_app
# ===================================

Write-Host "🧹 تنظيف إعدادات IntelliJ IDEA..." -ForegroundColor Cyan
Write-Host ""

$projectPath = "C:\Users\fqsdg\Desktop\end_project\readyroad"
cd $projectPath

# 1. التحقق من عدم وجود مجلد mobile_app
Write-Host "1️⃣ التحقق من مجلد mobile_app..." -ForegroundColor Yellow
if (Test-Path "mobile_app") {
    Write-Host "   ⚠️ مجلد mobile_app موجود!" -ForegroundColor Red
} else {
    Write-Host "   ✅ مجلد mobile_app غير موجود" -ForegroundColor Green
}
Write-Host ""

# 2. البحث عن إشارات في الملفات
Write-Host "2️⃣ البحث عن إشارات mobile_app في الملفات..." -ForegroundColor Yellow
$results = Get-ChildItem -Recurse -File -Exclude "*.log","*.class","*.jar" | Select-String "mobile_app" -List -ErrorAction SilentlyContinue
if ($results) {
    Write-Host "   ⚠️ تم العثور على إشارات:" -ForegroundColor Red
    $results | ForEach-Object { Write-Host "      - $($_.Path)" -ForegroundColor Yellow }
} else {
    Write-Host "   ✅ لا توجد إشارات لـ mobile_app في الملفات" -ForegroundColor Green
}
Write-Host ""

# 3. التحقق من ملف .iml
Write-Host "3️⃣ التحقق من ملف readyroad-backend.iml..." -ForegroundColor Yellow
$imlContent = Get-Content "readyroad-backend.iml" -Raw
if ($imlContent -match "mobile_app") {
    Write-Host "   ⚠️ يحتوي على إشارات لـ mobile_app" -ForegroundColor Red
} else {
    Write-Host "   ✅ نظيف - لا توجد إشارات" -ForegroundColor Green
}
Write-Host ""

# 4. حذف ذاكرة التخزين المؤقت لـ IntelliJ
Write-Host "4️⃣ حذف ذاكرة التخزين المؤقت..." -ForegroundColor Yellow

# حذف مجلد target
if (Test-Path "target") {
    Remove-Item "target" -Recurse -Force -ErrorAction SilentlyContinue
    Write-Host "   ✅ تم حذف مجلد target" -ForegroundColor Green
}

# حذف .idea/workspace.xml (يحتوي على إعدادات الجلسة)
if (Test-Path ".idea/workspace.xml") {
    Remove-Item ".idea/workspace.xml" -Force -ErrorAction SilentlyContinue
    Write-Host "   ✅ تم حذف workspace.xml" -ForegroundColor Green
}

Write-Host ""
Write-Host "════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "✅ اكتمل التنظيف!" -ForegroundColor Green
Write-Host "════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""
Write-Host "📋 الخطوات التالية:" -ForegroundColor Yellow
Write-Host "   1. افتح IntelliJ IDEA" -ForegroundColor White
Write-Host "   2. اذهب إلى: File → Invalidate Caches and Restart" -ForegroundColor White
Write-Host "   3. اختر: Invalidate and Restart" -ForegroundColor White
Write-Host "   4. بعد إعادة التشغيل، قم بتشغيل المشروع" -ForegroundColor White
Write-Host ""
Write-Host "   أو ببساطة:" -ForegroundColor Yellow
Write-Host "   .\mvnw.cmd spring-boot:run" -ForegroundColor White
Write-Host ""
