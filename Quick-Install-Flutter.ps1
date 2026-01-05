# Quick Flutter Download and Setup

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "Flutter Quick Installer" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

$flutterPath = "C:\flutter"

# Check if Flutter already exists
if (Test-Path "$flutterPath\bin\flutter.bat") {
    Write-Host "Flutter is already installed!" -ForegroundColor Green
    & "$flutterPath\bin\flutter.bat" --version
    Read-Host "Press Enter to exit"
    exit 0
}

Write-Host "Opening Flutter download page in your browser..." -ForegroundColor Yellow
Write-Host ""
Write-Host "Please:" -ForegroundColor Cyan
Write-Host "1. Download the Flutter SDK ZIP file" -ForegroundColor White
Write-Host "2. Extract it to: C:\" -ForegroundColor White
Write-Host "   (You should have: C:\flutter\bin\flutter.bat)" -ForegroundColor White
Write-Host ""

# Open download page
Start-Process "https://docs.flutter.dev/get-started/install/windows"

Write-Host "After extracting, press Enter to add Flutter to PATH..." -ForegroundColor Yellow
Read-Host

# Check if extracted
if (-not (Test-Path "$flutterPath\bin\flutter.bat")) {
    Write-Host "ERROR: Flutter not found at C:\flutter" -ForegroundColor Red
    Write-Host "Please extract the ZIP to C:\ first" -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "Flutter found! Adding to PATH..." -ForegroundColor Green

# Add to PATH
$currentPath = [Environment]::GetEnvironmentVariable("Path", "User")
if ($currentPath -notlike "*$flutterPath\bin*") {
    $newPath = "$currentPath;$flutterPath\bin"
    [Environment]::SetEnvironmentVariable("Path", $newPath, "User")
    Write-Host "Flutter added to USER PATH" -ForegroundColor Green
}

$env:Path = "$env:Path;$flutterPath\bin"

Write-Host ""
Write-Host "Testing Flutter..." -ForegroundColor Cyan
& "$flutterPath\bin\flutter.bat" --version

Write-Host ""
Write-Host "============================================" -ForegroundColor Green
Write-Host "Setup Complete!" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Green
Write-Host ""
Write-Host "IMPORTANT: Open a NEW terminal and run:" -ForegroundColor Yellow
Write-Host "  flutter doctor" -ForegroundColor White
Write-Host ""
Read-Host "Press Enter to exit"

