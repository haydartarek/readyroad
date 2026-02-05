# ReadyRoad - Preparation for 100 percent Test Success

$baseUrl = "http://localhost:8890"

Write-Host "`nPreparing for 100 percent test..." -ForegroundColor Cyan

# 1. Login
Write-Host "[1] Logging in..." -ForegroundColor Yellow
$loginBody = @{ username = "admin"; password = "Admin123!" } | ConvertTo-Json
$login = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
$headers = @{ "Authorization" = "Bearer $($login.token)" }
Write-Host "Logged in successfully" -ForegroundColor Green

# 2. Cancel any active exams
Write-Host "[2] Cleaning up active exams..." -ForegroundColor Yellow
try {
    $result = Invoke-RestMethod -Uri "$baseUrl/api/exam-simulations/active" -Method DELETE -Headers $headers -ErrorAction Stop
    Write-Host "Cancelled active exam ID: $($result.cancelledExamId)" -ForegroundColor Green
} catch {
    Write-Host "No active exams found - OK" -ForegroundColor Gray
}

# 3. Verify exam can start
Write-Host "[3] Verifying exam start..." -ForegroundColor Yellow
try {
    $exam = Invoke-RestMethod -Uri "$baseUrl/api/exam-simulations/start" -Method POST -Headers $headers
    Write-Host "SUCCESS: Exam started ID: $($exam.examId)" -ForegroundColor Green

    # Clean up test exam
    $null = Invoke-RestMethod -Uri "$baseUrl/api/exam-simulations/active" -Method DELETE -Headers $headers
    Write-Host "Test exam cleaned up" -ForegroundColor Gray
} catch {
    Write-Host "FAILED: Could not start exam" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host "`n===================================" -ForegroundColor Green
Write-Host "Ready for 100 percent test!" -ForegroundColor Green
Write-Host "===================================" -ForegroundColor Green
Write-Host "`nRun: powershell -ExecutionPolicy Bypass -File test-complete.ps1`n" -ForegroundColor Cyan
