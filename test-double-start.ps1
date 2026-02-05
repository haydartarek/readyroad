# Test Double Start Behavior - Critical Test
# Tests if system correctly handles starting exam when one is already active

$baseUrl = "http://localhost:8890"

Write-Host ""
Write-Host "================================================================" -ForegroundColor Cyan
Write-Host "  TEST: DOUBLE START BEHAVIOR (Should return 409 Conflict)" -ForegroundColor Cyan
Write-Host "================================================================" -ForegroundColor Cyan
Write-Host ""

# Login
Write-Host "[1/4] Logging in..." -ForegroundColor Yellow
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

# Clean up any active exams
Write-Host "[2/4] Cleaning up active exams..." -ForegroundColor Yellow
try {
    $result = Invoke-RestMethod -Uri "$baseUrl/api/exam-simulations/active" -Method DELETE -Headers $headers -ErrorAction SilentlyContinue
    Write-Host "     SUCCESS: Cleanup complete" -ForegroundColor Green
} catch {
    Write-Host "     INFO: No active exams to clean" -ForegroundColor Gray
}

# Start first exam
Write-Host "[3/4] Starting first exam..." -ForegroundColor Yellow
try {
    $start1 = Invoke-RestMethod -Uri "$baseUrl/api/exam-simulations/start" -Method POST -Headers $headers -ErrorAction Stop
    Write-Host "     SUCCESS: First exam created (ID: $($start1.examId))" -ForegroundColor Green
    $firstExamId = $start1.examId
} catch {
    Write-Host "     FAIL: Could not create first exam" -ForegroundColor Red
    Write-Host "     Error: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Try starting second exam (should fail)
Write-Host "[4/4] Attempting to start second exam (should fail)..." -ForegroundColor Yellow
try {
    $start2 = Invoke-RestMethod -Uri "$baseUrl/api/exam-simulations/start" -Method POST -Headers $headers -ErrorAction Stop
    Write-Host "     PROBLEM: Second exam created (ID: $($start2.examId))" -ForegroundColor Red
    Write-Host "     This indicates a LOGIC BUG - should prevent multiple active exams!" -ForegroundColor Red
    $testPassed = $false
} catch {
    $statusCode = $null
    if ($_.Exception.Response) {
        $statusCode = [int]$_.Exception.Response.StatusCode
    }

    if ($statusCode -eq 409) {
        Write-Host "     SUCCESS: 409 Conflict returned (correct behavior)" -ForegroundColor Green
        Write-Host "     Message: $($_.Exception.Message)" -ForegroundColor Gray
        $testPassed = $true
    } elseif ($statusCode -eq 500) {
        Write-Host "     PROBLEM: 500 Internal Server Error" -ForegroundColor Red
        Write-Host "     This indicates EXCEPTION HANDLING needed" -ForegroundColor Red
        Write-Host "     Message: $($_.Exception.Message)" -ForegroundColor Gray
        $testPassed = $false
    } elseif ($statusCode -eq 400) {
        Write-Host "     ACCEPTABLE: 400 Bad Request returned" -ForegroundColor Yellow
        Write-Host "     Message: $($_.Exception.Message)" -ForegroundColor Gray
        $testPassed = $true
    } else {
        Write-Host "     UNEXPECTED: Status code $statusCode" -ForegroundColor Yellow
        Write-Host "     Message: $($_.Exception.Message)" -ForegroundColor Gray
        $testPassed = $false
    }
}

# Summary
Write-Host ""
Write-Host "================================================================" -ForegroundColor Cyan
Write-Host "  TEST RESULTS" -ForegroundColor Cyan
Write-Host "================================================================" -ForegroundColor Cyan
Write-Host ""

if ($testPassed) {
    Write-Host "  STATUS: PASS" -ForegroundColor Green
    Write-Host "  System correctly prevents multiple active exams" -ForegroundColor Green
} else {
    Write-Host "  STATUS: FAIL" -ForegroundColor Red
    Write-Host "  System needs improvement in handling double start" -ForegroundColor Red
}

Write-Host ""
Write-Host "  First Exam ID: $firstExamId" -ForegroundColor Gray
Write-Host ""

# Clean up
Write-Host "Cleaning up test exam..." -ForegroundColor Gray
try {
    $null = Invoke-RestMethod -Uri "$baseUrl/api/exam-simulations/active" -Method DELETE -Headers $headers -ErrorAction SilentlyContinue
} catch {}

Write-Host "Done." -ForegroundColor Gray
Write-Host ""
