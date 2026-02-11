# ========================================
# Test Authentication Fixes for SmartQuiz
# ========================================

$baseUrl = "http://localhost:8890"

Write-Host "`n=== Testing SmartQuiz Authentication Fixes ===" -ForegroundColor Cyan
Write-Host "Expected: Public endpoints return 200/401 (NOT 500)" -ForegroundColor Yellow
Write-Host "Base URL: $baseUrl`n" -ForegroundColor Yellow

# Test 1: Random quiz WITHOUT auth (should work - guest mode)
Write-Host "1. Testing /api/smart-quiz/random WITHOUT auth (guest mode)" -ForegroundColor Green
try {
    $response = Invoke-WebRequest "$baseUrl/api/smart-quiz/random?count=5" -UseBasicParsing
    Write-Host "✅ Status: $($response.StatusCode)" -ForegroundColor Green
    $questions = $response.Content | ConvertFrom-Json
    Write-Host "✅ Questions received: $($questions.Length)" -ForegroundColor Green
    if ($questions.Length -gt 0) {
        Write-Host "✅ First question ID: $($questions[0].id)" -ForegroundColor Green
    }
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    Write-Host "❌ Status: $statusCode" -ForegroundColor Red
    if ($statusCode -eq 401) {
        Write-Host "⚠️  Expected: 200 (public endpoint), Got: 401" -ForegroundColor Yellow
        Write-Host "   Check if /api/smart-quiz/** is in permitAll() list" -ForegroundColor Yellow
    } elseif ($statusCode -eq 500) {
        Write-Host "❌ FAIL: Still returning 500 (should be 200 or 401)" -ForegroundColor Red
    }
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Gray
}

# Test 2: Category quiz WITHOUT auth (should work - guest mode)
Write-Host "`n2. Testing /api/smart-quiz/category/1 WITHOUT auth (guest mode)" -ForegroundColor Green
try {
    $response = Invoke-WebRequest "$baseUrl/api/smart-quiz/category/1?count=10" -UseBasicParsing
    Write-Host "✅ Status: $($response.StatusCode)" -ForegroundColor Green
    $questions = $response.Content | ConvertFrom-Json
    Write-Host "✅ Questions received: $($questions.Length)" -ForegroundColor Green
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    Write-Host "❌ Status: $statusCode" -ForegroundColor Red
    if ($statusCode -eq 500) {
        Write-Host "❌ FAIL: Still returning 500 instead of 200/401" -ForegroundColor Red
    }
}

# Test 3: Stats WITHOUT auth (should work - guest mode)
Write-Host "`n3. Testing /api/smart-quiz/stats WITHOUT auth (guest mode)" -ForegroundColor Green
try {
    $response = Invoke-WebRequest "$baseUrl/api/smart-quiz/stats" -UseBasicParsing
    Write-Host "✅ Status: $($response.StatusCode)" -ForegroundColor Green
    $stats = $response.Content | ConvertFrom-Json
    Write-Host "✅ Guest mode active: $($stats.guestMode)" -ForegroundColor Green
    Write-Host "✅ Total questions: $($stats.totalQuestionsAvailable)" -ForegroundColor Green
    Write-Host "✅ Message: $($stats.message)" -ForegroundColor Yellow
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    Write-Host "❌ Status: $statusCode" -ForegroundColor Red
}

# Test 4: WITH token (for comparison)
Write-Host "`n4. Testing WITH JWT token (authenticated mode)" -ForegroundColor Green
Write-Host "   ⚠️  Requires valid JWT token from login" -ForegroundColor Yellow
Write-Host "   If you have a token, run:" -ForegroundColor Gray
Write-Host '   $token = "YOUR_JWT_HERE"' -ForegroundColor Gray
Write-Host '   curl.exe -H "Authorization: Bearer $token" "$baseUrl/api/smart-quiz/random?count=5"' -ForegroundColor Gray

Write-Host "`n=== Summary ===" -ForegroundColor Cyan
Write-Host "✅ If Test 1-3 return 200: Authentication fix successful!" -ForegroundColor Green
Write-Host "❌ If any returns 500: Check logs for IllegalStateException" -ForegroundColor Red
Write-Host "❌ If returns 401: SecurityConfig may need /api/smart-quiz/** in permitAll()" -ForegroundColor Yellow
Write-Host "`nNext: Restart backend and run this script" -ForegroundColor Cyan
