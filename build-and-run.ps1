#!/usr/bin/env pwsh
# Build and Run ReadyRoad Backend

Write-Host "🚀 ReadyRoad Build & Run Script" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

# Set location
Set-Location "C:\Users\heyde\Desktop\end_project\readyroad"

# Set environment variables
Write-Host "📝 Setting environment variables..." -ForegroundColor Yellow
$env:DB_USERNAME = "haydar"
$env:DB_PASSWORD = "Hh06101987@"
$env:DB_HOST = "localhost"
$env:DB_PORT = "3306"
$env:DB_NAME = "readyroad_prod"
$env:ADMIN_DEFAULT_PASSWORD = "Admin2026Secure!"
$env:JWT_SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970337336763979244226452948404D6351"
$env:SPRING_PROFILES_ACTIVE = "prod"

Write-Host "✅ Environment variables set" -ForegroundColor Green
Write-Host ""

# Clean and build
Write-Host "🔨 Building project (skipping tests)..." -ForegroundColor Yellow
& ./mvnw clean package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Build failed!" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Build successful!" -ForegroundColor Green
Write-Host ""

# Run application
Write-Host "🚀 Starting ReadyRoad application..." -ForegroundColor Yellow
Write-Host "📍 Backend will be available at: http://localhost:8890" -ForegroundColor Cyan
Write-Host "📍 Swagger UI: http://localhost:8890/swagger-ui.html" -ForegroundColor Cyan
Write-Host "📍 Health Check: http://localhost:8890/actuator/health" -ForegroundColor Cyan
Write-Host ""
Write-Host "Press Ctrl+C to stop the application" -ForegroundColor Yellow
Write-Host ""

& ./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
