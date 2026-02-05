# ═══════════════════════════════════════════════════════
# ReadyRoad - Final Verification Test
# ═══════════════════════════════════════════════════════

Write-Host ""
Write-Host "════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "  READYROAD - FINAL VERIFICATION TEST" -ForegroundColor Cyan
Write-Host "════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""

$baseUrl = "http://localhost:8890"
$passed = 0
$failed = 0

# Test 1: Health Check
Write-Host "[1/4] Testing Server Health..." -ForegroundColor Yellow
try {
    $health = Invoke-RestMethod -Uri "$baseUrl/actuator/health" -TimeoutSec 5
    if ($health.status -eq "UP") {
        Write-Host "     SUCCESS: Server is UP" -ForegroundColor Green
        $passed++
    }
} catch {
    Write-Host "     FAIL: Server is DOWN" -ForegroundColor Red
    $failed++
    exit 1
}

# Test 2: Authentication
Write-Host "[2/4] Testing Authentication..." -ForegroundColor Yellow
try {
    $loginBody = @{ username = "admin"; password = "Admin123!" } | ConvertTo-Json
    $login = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
    $token = $login.token
    $headers = @{ "Authorization" = "Bearer $token" }
    Write-Host "     SUCCESS: JWT token received" -ForegroundColor Green
    $passed++
} catch {
    Write-Host "     FAIL: Authentication failed" -ForegroundColor Red
    $failed++
    exit 1
}

# Test 3: Exam Start
Write-Host "[3/4] Testing Exam Start..." -ForegroundColor Yellow
try {
    $exam = Invoke-RestMethod -Uri "$baseUrl/api/exam-simulations/start" -Method POST -Headers $headers -ErrorAction Stop
    Write-Host "     SUCCESS: Exam created (ID: $($exam.examId), Questions: $($exam.questions.Count))" -ForegroundColor Green
    $passed++
} catch {
    Write-Host "     FAIL: Exam Start failed (Status: $($_.Exception.Response.StatusCode.value__))" -ForegroundColor Red
    $failed++
}

# Test 4: Security (Unauthorized Access)
Write-Host "[4/4] Testing Security..." -ForegroundColor Yellow
try {
    $null = Invoke-RestMethod -Uri "$baseUrl/api/users/me" -ErrorAction Stop
    Write-Host "     FAIL: Unauthorized access allowed (Should reject)" -ForegroundColor Red
    $failed++
} catch {
    Write-Host "     SUCCESS: Unauthorized access blocked" -ForegroundColor Green
    $passed++
}

# Summary
Write-Host ""
Write-Host "════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "  VERIFICATION RESULTS" -ForegroundColor Cyan
Write-Host "════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""
Write-Host "  Total Tests:    4" -ForegroundColor White
Write-Host "  Passed:         $passed" -ForegroundColor Green
Write-Host "  Failed:         $failed" -ForegroundColor $(if ($failed -eq 0) { "Green" } else { "Red" })
Write-Host "  Success Rate:   $([math]::Round(($passed / 4) * 100, 2))%" -ForegroundColor $(if ($passed -eq 4) { "Green" } else { "Yellow" })
Write-Host ""

if ($passed -eq 4) {
    Write-Host "  STATUS: ALL SYSTEMS OPERATIONAL!" -ForegroundColor Green
    Write-Host "  READY FOR PRODUCTION DEPLOYMENT" -ForegroundColor Green
} else {
    Write-Host "  STATUS: Some tests failed" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""
