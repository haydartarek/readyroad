Write-Host "Starting ReadyRoad Backend Application..." -ForegroundColor Cyan

# Stop any running Java processes
Write-Host "Stopping any existing Java processes..." -ForegroundColor Yellow
Get-Process -Name "java" -ErrorAction SilentlyContinue | Stop-Process -Force
Start-Sleep -Seconds 2

# Start the application
Write-Host "Starting Spring Boot application in DEV mode..." -ForegroundColor Green
./mvnw spring-boot:run "-Dspring-boot.run.profiles=dev"
