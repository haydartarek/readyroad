# Flutter Auto Installer for Windows
# Run this script as Administrator

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "Flutter Auto Installation Script" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# Check if running as Administrator
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
if (-not $isAdmin) {
    Write-Host "WARNING: Not running as Administrator" -ForegroundColor Yellow
    Write-Host "PATH changes may not persist system-wide" -ForegroundColor Yellow
    Write-Host ""
}

# Configuration
$flutterPath = "C:\flutter"
$flutterZip = "C:\flutter_sdk.zip"
$flutterUrl = "https://storage.googleapis.com/flutter_infra_release/releases/stable/windows/flutter_windows_3.27.1-stable.zip"

# Check if Flutter already exists
Write-Host "[1/5] Checking existing Flutter installation..." -ForegroundColor Green
if (Test-Path "$flutterPath\bin\flutter.bat") {
    Write-Host "Flutter is already installed!" -ForegroundColor Yellow
    Write-Host ""
    & "$flutterPath\bin\flutter.bat" --version
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 0
}

# Create flutter directory
Write-Host "[2/5] Creating Flutter directory..." -ForegroundColor Green
if (-not (Test-Path $flutterPath)) {
    New-Item -ItemType Directory -Path $flutterPath -Force | Out-Null
}

# Download Flutter SDK
Write-Host "[3/5] Downloading Flutter SDK..." -ForegroundColor Green
Write-Host "This may take 5-10 minutes depending on your internet speed..." -ForegroundColor Yellow
Write-Host ""

try {
    [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

    # Use WebClient for progress
    $webClient = New-Object System.Net.WebClient
    $webClient.DownloadFile($flutterUrl, $flutterZip)

    Write-Host "Download complete!" -ForegroundColor Green
} catch {
    Write-Host "ERROR: Failed to download Flutter SDK" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Write-Host ""
    Write-Host "Please download manually from:" -ForegroundColor Yellow
    Write-Host "https://docs.flutter.dev/get-started/install/windows" -ForegroundColor Cyan
    Read-Host "Press Enter to exit"
    exit 1
}

# Extract Flutter SDK
Write-Host "[4/5] Extracting Flutter SDK..." -ForegroundColor Green
Write-Host "This may take a few minutes..." -ForegroundColor Yellow

try {
    Expand-Archive -Path $flutterZip -DestinationPath "C:\" -Force
    Write-Host "Extraction complete!" -ForegroundColor Green
} catch {
    Write-Host "ERROR: Failed to extract Flutter SDK" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

# Clean up
Write-Host "Cleaning up..." -ForegroundColor Green
Remove-Item $flutterZip -Force

# Add to PATH
Write-Host "[5/5] Adding Flutter to PATH..." -ForegroundColor Green

$currentPath = [Environment]::GetEnvironmentVariable("Path", "Machine")
if ($currentPath -notlike "*$flutterPath\bin*") {
    try {
        $newPath = "$currentPath;$flutterPath\bin"
        [Environment]::SetEnvironmentVariable("Path", $newPath, "Machine")
        Write-Host "Flutter added to system PATH" -ForegroundColor Green
    } catch {
        Write-Host "WARNING: Could not modify system PATH" -ForegroundColor Yellow
        Write-Host "You may need to add C:\flutter\bin to PATH manually" -ForegroundColor Yellow
    }
} else {
    Write-Host "Flutter is already in PATH" -ForegroundColor Yellow
}

# Update current session PATH
$env:Path = "$env:Path;$flutterPath\bin"

Write-Host ""
Write-Host "============================================" -ForegroundColor Green
Write-Host "Flutter Installation Complete!" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Green
Write-Host ""
Write-Host "Flutter Location: C:\flutter" -ForegroundColor Cyan
Write-Host ""
Write-Host "IMPORTANT NEXT STEPS:" -ForegroundColor Yellow
Write-Host "1. CLOSE this PowerShell window" -ForegroundColor White
Write-Host "2. Open a NEW PowerShell/CMD window" -ForegroundColor White
Write-Host "3. Run: flutter doctor" -ForegroundColor White
Write-Host "4. Run: flutter doctor --android-licenses (if needed)" -ForegroundColor White
Write-Host ""
Write-Host "Testing Flutter in current session..." -ForegroundColor Cyan

try {
    & "$flutterPath\bin\flutter.bat" --version
    Write-Host ""
    Write-Host "Flutter is working!" -ForegroundColor Green
} catch {
    Write-Host "Please open a new terminal to use Flutter" -ForegroundColor Yellow
}

Write-Host ""
Read-Host "Press Enter to exit"

