# Simple Backend Startup Script
$env:DB_USERNAME = "haydar"
$env:DB_PASSWORD = "Hh06101987@"
$env:DB_HOST = "localhost"
$env:DB_PORT = "3306"
$env:DB_NAME = "readyroad_prod"
$env:ADMIN_DEFAULT_PASSWORD = "Admin2026Secure!"
$env:JWT_SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970337336763979244226452948404D6351"
$env:SPRING_PROFILES_ACTIVE = "prod"

Write-Host "Starting ReadyRoad Backend..." -ForegroundColor Cyan
Write-Host "Database: $env:DB_NAME" -ForegroundColor Gray
Write-Host "Profile: $env:SPRING_PROFILES_ACTIVE" -ForegroundColor Gray
Write-Host ""

& ./mvnw spring-boot:run
