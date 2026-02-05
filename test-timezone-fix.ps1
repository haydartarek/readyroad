# Test Timezone Fix - Verify UTC timestamps
$baseUrl = "http://localhost:8890"

Write-Host ""
Write-Host "================================================================" -ForegroundColor Cyan
Write-Host "  TEST: TIMEZONE FIX VERIFICATION" -ForegroundColor Cyan
Write-Host "================================================================" -ForegroundColor Cyan
Write-Host ""

# Login
Write-Host "[1/3] Logging in..." -ForegroundColor Yellow
try {
    $loginBody = @{ username = "admin"; password = "Admin123!" } | ConvertTo-Json
    $login = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
    $token = $login.token
    $headers = @{ "Authorization" = "Bearer $token" }
    Write-Host "     SUCCESS: Authenticated" -ForegroundColor Green
} catch {
    Write-Host "     FAIL: Authentication failed" -ForegroundColor Red
    exit 1
}

# Clean up
Write-Host "[2/3] Cleaning up..." -ForegroundColor Yellow
try {
    $null = Invoke-RestMethod -Uri "$baseUrl/api/exam-simulations/active" -Method DELETE -Headers $headers -ErrorAction SilentlyContinue
    Write-Host "     SUCCESS: Cleanup complete" -ForegroundColor Green
} catch {
    Write-Host "     INFO: No active exams" -ForegroundColor Gray
}

# Start exam and test timestamps
Write-Host "[3/3] Testing timezone fix..." -ForegroundColor Yellow
try {
    $exam = Invoke-RestMethod -Uri "$baseUrl/api/exam-simulations/start" -Method POST -Headers $headers -ErrorAction Stop

    # Parse timestamps
    $startedAt = [DateTimeOffset]::Parse($exam.startedAt)
    $expiresAt = [DateTimeOffset]::Parse($exam.expiresAt)
    $nowUtc = [DateTimeOffset]::UtcNow

    # Calculate differences
    $minsToStart = ($startedAt - $nowUtc).TotalMinutes
    $minsToExpire = ($expiresAt - $nowUtc).TotalMinutes
    $duration = ($expiresAt - $startedAt).TotalMinutes

    Write-Host ""
    Write-Host "================================================================" -ForegroundColor Cyan
    Write-Host "  TIMESTAMP ANALYSIS" -ForegroundColor Cyan
    Write-Host "================================================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "  Exam ID:     $($exam.examId)" -ForegroundColor White
    Write-Host "  Questions:   $($exam.questions.Count)" -ForegroundColor White
    Write-Host ""
    Write-Host "  TIMESTAMPS (UTC):" -ForegroundColor Yellow
    Write-Host "  Started:     $startedAt" -ForegroundColor Gray
    Write-Host "  Expires:     $expiresAt" -ForegroundColor Gray
    Write-Host "  Now:         $nowUtc" -ForegroundColor Gray
    Write-Host ""
    Write-Host "  TIME DIFFERENCES:" -ForegroundColor Yellow
    Write-Host "  To START:    $($minsToStart.ToString('F1')) minutes" -ForegroundColor $(if ([Math]::Abs($minsToStart) -lt 1) { "Green" } else { "Red" })
    Write-Host "  To EXPIRE:   $($minsToExpire.ToString('F1')) minutes" -ForegroundColor $(if ($minsToExpire -ge 29 -and $minsToExpire -le 31) { "Green" } else { "Red" })
    Write-Host "  Duration:    $($duration.ToString('F1')) minutes" -ForegroundColor $(if ($duration -eq 30) { "Green" } else { "Red" })
    Write-Host ""

    # Validation
    $allPassed = $true

    Write-Host "  VALIDATION:" -ForegroundColor Yellow

    # Test 1: Started at is close to now (within 1 minute)
    if ([Math]::Abs($minsToStart) -lt 1) {
        Write-Host "  PASS: startedAt is current time" -ForegroundColor Green
    } else {
        Write-Host "  FAIL: startedAt is off by $($minsToStart.ToString('F1')) minutes" -ForegroundColor Red
        $allPassed = $false
    }

    # Test 2: Duration is exactly 30 minutes
    if ($duration -eq 30) {
        Write-Host "  PASS: Duration is exactly 30 minutes" -ForegroundColor Green
    } else {
        Write-Host "  FAIL: Duration is $($duration.ToString('F1')) minutes (expected 30)" -ForegroundColor Red
        $allPassed = $false
    }

    # Test 3: Expires in ~30 minutes
    if ($minsToExpire -ge 29 -and $minsToExpire -le 31) {
        Write-Host "  PASS: Expires in ~30 minutes" -ForegroundColor Green
    } else {
        Write-Host "  FAIL: Expires in $($minsToExpire.ToString('F1')) minutes" -ForegroundColor Red
        $allPassed = $false
    }

    # Test 4: Timestamps are UTC (check Z suffix)
    $startedAtStr = $exam.startedAt.ToString()
    $expiresAtStr = $exam.expiresAt.ToString()

    if ($startedAtStr -like "*Z" -and $expiresAtStr -like "*Z") {
        Write-Host "  PASS: Timestamps are in UTC format (Z suffix)" -ForegroundColor Green
    } else {
        Write-Host "  FAIL: Timestamps missing Z suffix (not UTC)" -ForegroundColor Red
        $allPassed = $false
    }

    Write-Host ""
    Write-Host "================================================================" -ForegroundColor Cyan

    if ($allPassed) {
        Write-Host "  RESULT: ALL TESTS PASSED!" -ForegroundColor Green
        Write-Host "  Timezone fix is working correctly" -ForegroundColor Green
    } else {
        Write-Host "  RESULT: SOME TESTS FAILED" -ForegroundColor Red
        Write-Host "  Timezone issue still exists" -ForegroundColor Red
    }

    Write-Host "================================================================" -ForegroundColor Cyan
    Write-Host ""

    # Clean up
    try {
        $null = Invoke-RestMethod -Uri "$baseUrl/api/exam-simulations/active" -Method DELETE -Headers $headers -ErrorAction SilentlyContinue
    } catch {}

} catch {
    Write-Host "     FAIL: Exam Start failed" -ForegroundColor Red
    Write-Host "     Error: $($_.Exception.Message)" -ForegroundColor Red
}
