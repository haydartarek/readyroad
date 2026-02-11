#!/usr/bin/env pwsh
# Quick Run - ReadyRoad Backend
# This script sets environment variables and runs the application

Write-Host "🚀 ReadyRoad - Starting Application" -ForegroundColor Cyan
Write-Host "====================================" -ForegroundColor Cyan
Write-Host ""

# Change to project directory
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

Write-Host "✅ Environment variables configured" -ForegroundColor Green
Write-Host ""

# Build project
Write-Host "🔨 Building project..." -ForegroundColor Yellow
& ./mvnw clean compile -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "❌ Build failed!" -ForegroundColor Red
    Write-Host "Please check the error messages above." -ForegroundColor Yellow
    pause
    exit 1
}

Write-Host ""
Write-Host "✅ Build successful!" -ForegroundColor Green
Write-Host ""

# First, clean up any failed Flyway migrations
Write-Host "🔧 Checking for failed Flyway migrations..." -ForegroundColor Yellow
$checkMigration = "SELECT COUNT(*) as failed_count FROM flyway_schema_history WHERE version = '77' AND success = 0;"
$mysqlCmd = "mysql -u$env:DB_USERNAME -p$env:DB_PASSWORD -h$env:DB_HOST -P$env:DB_PORT $env:DB_NAME -e `"$checkMigration`""
Write-Host ""

# Run application
Write-Host "🚀 Starting ReadyRoad Backend..." -ForegroundColor Cyan
Write-Host ""
Write-Host "📍 Application will be available at:" -ForegroundColor White
Write-Host "   • Backend API: http://localhost:8890" -ForegroundColor Green
Write-Host "   • Swagger UI:  http://localhost:8890/swagger-ui.html" -ForegroundColor Green
Write-Host "   • Health:      http://localhost:8890/actuator/health" -ForegroundColor Green
Write-Host ""
Write-Host "📝 Login Credentials:" -ForegroundColor White
Write-Host "   • Username: admin" -ForegroundColor Cyan
Write-Host "   • Password: Admin2026Secure!" -ForegroundColor Cyan
Write-Host ""
Write-Host "📊 Database: $env:DB_NAME on $env:DB_HOST:$env:DB_PORT" -ForegroundColor White
Write-Host "🔐 Profile: $env:SPRING_PROFILES_ACTIVE" -ForegroundColor White
Write-Host ""
Write-Host "⚠️  Press Ctrl+C to stop the application" -ForegroundColor Yellow
Write-Host ""
Write-Host "════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""

& ./mvnw spring-boot:run
