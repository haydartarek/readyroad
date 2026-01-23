# Test Registration Endpoint
Write-Host "================================" -ForegroundColor Cyan
Write-Host "Testing Registration Endpoint" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

# Test data
$body = @{
    username = "testuser_" + (Get-Date -Format "HHmmss")
    email = "test_" + (Get-Date -Format "HHmmss") + "@example.com"
    password = "SecurePass123!"
    fullName = "Test User"
} | ConvertTo-Json

Write-Host "Request URL: http://localhost:8890/api/auth/register" -ForegroundColor Yellow
Write-Host "Request Body:" -ForegroundColor Yellow
Write-Host $body -ForegroundColor White
Write-Host ""

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8890/api/auth/register" `
        -Method POST `
        -ContentType "application/json" `
        -Body $body `
        -UseBasicParsing

    Write-Host "✅ Success!" -ForegroundColor Green
    Write-Host "Status Code: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Response:" -ForegroundColor Green
    Write-Host $response.Content -ForegroundColor White
} catch {
    Write-Host "❌ Error!" -ForegroundColor Red
    Write-Host "Status Code: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
    Write-Host "Error Message: $($_.Exception.Message)" -ForegroundColor Red

    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response Body:" -ForegroundColor Red
        Write-Host $responseBody -ForegroundColor White
    }
}

Write-Host ""
Write-Host "================================" -ForegroundColor Cyan
Write-Host "Check backend console for detailed logs!" -ForegroundColor Yellow
Write-Host "================================" -ForegroundColor Cyan
