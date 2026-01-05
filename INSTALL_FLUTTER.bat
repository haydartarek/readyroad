@echo off
echo ============================================
echo Flutter Auto Installation Script
echo ============================================
echo.
echo This script will:
echo 1. Download Flutter SDK
echo 2. Extract it to C:\flutter
echo 3. Add it to PATH
echo.
pause

echo.
echo [1/4] Checking if Flutter already exists...
if exist "C:\flutter\bin\flutter.bat" (
    echo Flutter already installed at C:\flutter
    echo.
    C:\flutter\bin\flutter.bat --version
    pause
    exit /b 0
)

echo.
echo [2/4] Creating flutter directory...
if not exist "C:\flutter" mkdir "C:\flutter"

echo.
echo [3/4] Downloading Flutter SDK...
echo Please wait, this may take a few minutes...
echo.

powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://storage.googleapis.com/flutter_infra_release/releases/stable/windows/flutter_windows_3.27.1-stable.zip' -OutFile 'C:\flutter_sdk.zip'}"

if not exist "C:\flutter_sdk.zip" (
    echo ERROR: Failed to download Flutter SDK
    echo Please download manually from: https://docs.flutter.dev/get-started/install/windows
    pause
    exit /b 1
)

echo.
echo [4/4] Extracting Flutter SDK...
powershell -Command "Expand-Archive -Path 'C:\flutter_sdk.zip' -DestinationPath 'C:\' -Force"

echo.
echo Cleaning up...
del "C:\flutter_sdk.zip"

echo.
echo ============================================
echo Flutter installed successfully!
echo ============================================
echo.
echo Location: C:\flutter
echo.
echo IMPORTANT: Adding Flutter to PATH...
echo.

REM Add Flutter to PATH permanently
setx PATH "%PATH%;C:\flutter\bin" /M

echo.
echo ============================================
echo Installation Complete!
echo ============================================
echo.
echo Please CLOSE this window and open a NEW PowerShell/CMD
echo Then run: flutter doctor
echo.
pause

