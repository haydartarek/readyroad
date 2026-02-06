# ============================================
# Execute Database Setup (PowerShell)
# ============================================

Write-Host "╔════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║  ReadyRoad Complete Database Setup                    ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

Write-Host "📋 This will setup:" -ForegroundColor Yellow
Write-Host "   1. readyroad (development database)" -ForegroundColor White
Write-Host "   2. readyroad_prod (production database)" -ForegroundColor White
Write-Host "   3. Grant permissions to user 'haydar'" -ForegroundColor White
Write-Host ""

Write-Host "🔐 You will be prompted for MySQL ROOT password" -ForegroundColor Cyan
Write-Host ""

# Execute SQL script
Write-Host "Executing SQL script..." -ForegroundColor Yellow
$result = Get-Content "setup-all-databases.sql" | mysql -u root -p 2>&1

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "✅ Database setup completed successfully!" -ForegroundColor Green
    Write-Host ""
    Write-Host "📊 Summary:" -ForegroundColor Yellow
    Write-Host "   ✓ Database 'readyroad' ready" -ForegroundColor Green
    Write-Host "   ✓ Database 'readyroad_prod' ready" -ForegroundColor Green
    Write-Host "   ✓ Permissions granted to 'haydar'" -ForegroundColor Green
    Write-Host "   ✓ Character set: utf8mb4_unicode_ci" -ForegroundColor Green
    Write-Host ""

    # Test connection
    Write-Host "🔍 Testing connection..." -ForegroundColor Yellow
    $testResult = mysql -u haydar -pHh06101987@ -e "SHOW DATABASES LIKE 'readyroad%';" 2>&1

    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Connection test successful!" -ForegroundColor Green
        Write-Host ""
        Write-Host "Databases accessible by haydar:" -ForegroundColor White
        Write-Host "$testResult" -ForegroundColor Gray
    }

    Write-Host ""
    Write-Host "🚀 You can now run the application!" -ForegroundColor Cyan
    Write-Host "   ./mvnw spring-boot:run" -ForegroundColor White
} else {
    Write-Host ""
    Write-Host "❌ Database setup failed!" -ForegroundColor Red
    Write-Host "Error: $result" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "════════════════════════════════════════════════════════" -ForegroundColor Cyan
