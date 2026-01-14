# Download-Traffic-Signs.ps1
# سكريبت تنزيل صور العلامات المرورية البلجيكية
# Belgian Traffic Signs Image Downloader

param(
    [string]$HtmlFile = "data\traffic_signs.html",
    [string]$OutputDir = "mobile_app\assets\traffic_signs",
    [int]$MaxConcurrent = 5,
    [switch]$DryRun
)

$ErrorActionPreference = "Stop"

# الألوان
$ColorSuccess = "Green"
$ColorError = "Red"
$ColorInfo = "Cyan"
$ColorWarning = "Yellow"

# تحميل HTML
Write-Host "`n=== تنزيل صور العلامات المرورية ===" -ForegroundColor $ColorInfo
Write-Host "=== Traffic Signs Image Downloader ===`n" -ForegroundColor $ColorInfo

if (-not (Test-Path $HtmlFile)) {
    Write-Host "ERROR: HTML file not found: $HtmlFile" -ForegroundColor $ColorError
    exit 1
}

Write-Host "Loading HTML file: $HtmlFile" -ForegroundColor $ColorInfo
$htmlContent = Get-Content -Path $HtmlFile -Raw -Encoding UTF8

# خريطة الفئات
$categoryMap = @{
    'A' = 'danger_signs'
    'B' = 'priority_signs'
    'C' = 'prohibition_signs'
    'D' = 'mandatory_signs'
    'E' = 'parking_signs'
    'F' = 'information_signs'
    'G' = 'additional_panels'
    'M' = 'bicycle_signs'
    'T' = 'boundary_signs'
    'Z' = 'zone_signs'
}

# استخراج العلامات
Write-Host "Extracting signs from HTML..." -ForegroundColor $ColorInfo

$signs = @()
$signMatches = [regex]::Matches($htmlContent, '<img[^>]+alt="Verkeersbord ([A-Z]\d+[a-z]?)"[^>]+src="([^"]+)"[^>]*srcset="([^"]+)"')

foreach ($match in $signMatches) {
    $signCode = $match.Groups[1].Value
    $imageUrl = $match.Groups[2].Value
    $srcset = $match.Groups[3].Value
    
    # استخراج الفئة من رمز العلامة
    if ($signCode -match '^([A-Z])') {
        $category = $Matches[1]
        
        # الحصول على أعلى دقة من srcset
        $highResUrl = $imageUrl
        if ($srcset -match '(https?://[^\s]+)\s+2x') {
            $highResUrl = $Matches[1]
        } elseif ($srcset -match '(https?://[^\s]+)\s+1\.5x') {
            $highResUrl = $Matches[1]
        }
        
        # تنظيف URL
        $highResUrl = $highResUrl -replace '\s.*$', ''
        
        # تحويل الروابط النسبية إلى مطلقة
        if ($highResUrl -notmatch '^https?://') {
            # تخطي الروابط النسبية (ملفات محلية من HTML المحفوظ)
            continue
        }
        
        # التأكد من أن الفئة موجودة في الخريطة
        if ($categoryMap.ContainsKey($category)) {
            $folderName = $categoryMap[$category]
            
            $signs += @{
                SignCode = $signCode
                Category = $category
                FolderName = $folderName
                ImageUrl = $highResUrl
                Alt = "Verkeersbord $signCode"
            }
        }
    }
}

Write-Host "Found $($signs.Count) traffic signs" -ForegroundColor $ColorSuccess

# إحصائيات حسب الفئة
Write-Host "`nSigns by category:" -ForegroundColor $ColorInfo
$signs | Group-Object Category | Sort-Object Name | ForEach-Object {
    $categoryName = if ($_.Name -and $categoryMap.ContainsKey($_.Name)) { $categoryMap[$_.Name] } else { "unknown" }
    Write-Host "  $($_.Name) ($categoryName): $($_.Count) signs" -ForegroundColor $ColorWarning
}

if ($DryRun) {
    Write-Host "`nDRY RUN - No images will be downloaded" -ForegroundColor $ColorWarning
    Write-Host "First 5 signs to download:" -ForegroundColor $ColorInfo
    $signs | Select-Object -First 5 | ForEach-Object {
        Write-Host "  $($_.SignCode) -> $($_.FolderName)/$($_.SignCode).png"
        Write-Host "    URL: $($_.ImageUrl)"
    }
    exit 0
}

# إنشاء المجلدات
Write-Host "`nCreating directories..." -ForegroundColor $ColorInfo
foreach ($folder in $categoryMap.Values | Select-Object -Unique) {
    $path = Join-Path $OutputDir $folder
    if (-not (Test-Path $path)) {
        New-Item -ItemType Directory -Path $path -Force | Out-Null
        Write-Host "  Created: $folder" -ForegroundColor $ColorSuccess
    }
}

# تنزيل الصور
Write-Host "`nDownloading images..." -ForegroundColor $ColorInfo
Write-Host "Max concurrent downloads: $MaxConcurrent" -ForegroundColor $ColorWarning

$downloadedCount = 0
$failedCount = 0
$skippedCount = 0
$totalCount = $signs.Count

# إنشاء WebClient للتنزيل
$webClient = New-Object System.Net.WebClient
$webClient.Headers.Add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")

$progressIndex = 0
foreach ($sign in $signs) {
    $progressIndex++
    $progressPercent = [math]::Round(($progressIndex / $totalCount) * 100, 1)
    
    $outputPath = Join-Path $OutputDir "$($sign.FolderName)\$($sign.SignCode).png"
    
    # تخطي إذا كان الملف موجوداً
    if (Test-Path $outputPath) {
        Write-Host "[$progressPercent%] ⏭️  Skipped: $($sign.SignCode) (already exists)" -ForegroundColor $ColorWarning
        $skippedCount++
        continue
    }
    
    try {
        Write-Host "[$progressPercent%] ⬇️  Downloading: $($sign.SignCode) -> $($sign.FolderName)/" -NoNewline
        
        $webClient.DownloadFile($sign.ImageUrl, $outputPath)
        
        Write-Host " ✅" -ForegroundColor $ColorSuccess
        $downloadedCount++
        
        # تأخير بسيط لتجنب تحميل الخادم
        Start-Sleep -Milliseconds 100
    }
    catch {
        Write-Host " ❌" -ForegroundColor $ColorError
        Write-Host "  Error: $($_.Exception.Message)" -ForegroundColor $ColorError
        $failedCount++
    }
}

$webClient.Dispose()

# النتائج
Write-Host "`n=== Download Summary ===" -ForegroundColor $ColorInfo
Write-Host "Total signs:    $totalCount" -ForegroundColor $ColorInfo
Write-Host "Downloaded:     $downloadedCount" -ForegroundColor $ColorSuccess
Write-Host "Skipped:        $skippedCount" -ForegroundColor $ColorWarning
Write-Host "Failed:         $failedCount" -ForegroundColor $(if ($failedCount -gt 0) { $ColorError } else { $ColorSuccess })
Write-Host "`nDone! ✅" -ForegroundColor $ColorSuccess

# إنشاء ملف JSON بالإحصائيات
$stats = @{
    TotalSigns = $totalCount
    Downloaded = $downloadedCount
    Skipped = $skippedCount
    Failed = $failedCount
    Timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    Categories = @{}
}

foreach ($category in $categoryMap.Keys) {
    $count = ($signs | Where-Object { $_.Category -eq $category }).Count
    if ($count -gt 0) {
        $stats.Categories[$category] = @{
            FolderName = $categoryMap[$category]
            Count = $count
        }
    }
}

$statsFile = Join-Path $OutputDir "download_stats.json"
$stats | ConvertTo-Json -Depth 10 | Set-Content -Path $statsFile -Encoding UTF8
Write-Host "`nStatistics saved to: $statsFile" -ForegroundColor $ColorInfo
