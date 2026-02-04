Write-Host "`nðŸš€ Starting ReadyRoad Application..." -ForegroundColor Cyan
Stop-Process -Name "java" -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2
.\mvnw.cmd spring-boot:run
