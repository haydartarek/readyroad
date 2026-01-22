# Test Verification Script
# Run this to verify all fixes

cd C:\Users\fqsdg\Desktop\end_project\readyroad

Write-Host "======================================" -ForegroundColor Cyan
Write-Host "Starting Test Verification" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Step 1: Cleaning project..." -ForegroundColor Yellow
& .\mvnw.cmd clean

Write-Host ""
Write-Host "Step 2: Compiling..." -ForegroundColor Yellow
& .\mvnw.cmd compile test-compile

Write-Host ""
Write-Host "Step 3: Running ExamServiceIntegrationTest..." -ForegroundColor Yellow
& .\mvnw.cmd test -Dtest=ExamServiceIntegrationTest

Write-Host ""
Write-Host "Step 4: Running AdaptiveDifficultyIntegrationTest..." -ForegroundColor Yellow
& .\mvnw.cmd test -Dtest=AdaptiveDifficultyIntegrationTest

Write-Host ""
Write-Host "======================================" -ForegroundColor Green
Write-Host "Verification Complete!" -ForegroundColor Green
Write-Host "======================================" -ForegroundColor Green
Write-Host ""
Write-Host "Press any key to exit..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
