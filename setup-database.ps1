# ============================================
# ReadyRoad Database Setup Script (PowerShell)
# ============================================
# Purpose: Execute database setup using MySQL CLI
# ============================================

Write-Host "╔════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║  ReadyRoad Database Setup                            ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# MySQL connection details
$mysqlUser = "haydar"
$mysqlPassword = "Hh06101987@"
$databaseName = "readyroad"

Write-Host "📋 Configuration:" -ForegroundColor Yellow
Write-Host "   Database User: $mysqlUser" -ForegroundColor White
Write-Host "   Database Name: $databaseName" -ForegroundColor White
Write-Host ""

# Check if MySQL is installed and accessible
Write-Host "🔍 Checking MySQL installation..." -ForegroundColor Yellow
try {
    $mysqlVersion = & mysql --version 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ MySQL found: $mysqlVersion" -ForegroundColor Green
    }
} catch {
    Write-Host "❌ MySQL CLI not found in PATH" -ForegroundColor Red
    Write-Host "Please ensure MySQL is installed and added to PATH" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "🔐 Executing database setup..." -ForegroundColor Yellow
Write-Host "   (You may be prompted for MySQL root password)" -ForegroundColor White
Write-Host ""

# Execute SQL setup script as root
# Note: This requires root access to grant privileges
$sqlScript = Get-Content "setup-database.sql" -Raw

try {
    # Execute with root privileges
    Write-Host "Please enter MySQL root password when prompted:" -ForegroundColor Cyan
    $sqlScript | mysql -u root -p

    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "✅ Database setup completed successfully!" -ForegroundColor Green
        Write-Host ""
        Write-Host "📊 Summary:" -ForegroundColor Yellow
        Write-Host "   ✓ Database '$databaseName' created" -ForegroundColor Green
        Write-Host "   ✓ Permissions granted to user '$mysqlUser'" -ForegroundColor Green
        Write-Host "   ✓ Character set: utf8mb4_unicode_ci" -ForegroundColor Green
        Write-Host ""
        Write-Host "🚀 You can now start the application:" -ForegroundColor Cyan
        Write-Host "   ./mvnw spring-boot:run -Dspring-boot.run.arguments='--spring.profiles.active=secure'" -ForegroundColor White
    } else {
        Write-Host ""
        Write-Host "❌ Database setup failed!" -ForegroundColor Red
        Write-Host "   Check the error message above for details" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host ""
    Write-Host "❌ Error executing SQL script: $_" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "════════════════════════════════════════════════════════" -ForegroundColor Cyan
