# ========================================
# Start ReadyRoad Backend with readyroad_prod
# ========================================

Write-Host "`n=== Starting ReadyRoad Backend with secure profile ===" -ForegroundColor Cyan
Write-Host "Database: readyroad_prod" -ForegroundColor Yellow
Write-Host "User: haydar" -ForegroundColor Yellow
Write-Host "Port: 8890`n" -ForegroundColor Yellow

# Change to backend directory
Set-Location -Path "C:\Users\heyde\Desktop\end_project\readyroad"

# Start backend with secure profile and debug logging
.\mvnw.cmd -DskipTests spring-boot:run `
  "-Dspring-boot.run.arguments=--spring.profiles.active=secure --logging.level.com.readyroad=DEBUG"

# Note: If you encounter MySQL connection errors, run setup_readyroad_prod_db.sql first:
# mysql -u root -p < setup_readyroad_prod_db.sql
