@echo off
echo ============================================
echo Ready Road - GitHub Upload Script
echo ============================================
echo.

REM Check if Git is installed
where git >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Git is not installed!
    echo.
    echo Please install Git from: https://git-scm.com/
    echo.
    pause
    exit /b 1
)

echo Git found! Version:
git --version
echo.

cd /d "%~dp0"

REM Check if already initialized
if exist ".git" (
    echo Git repository already initialized.
    echo.
) else (
    echo Initializing Git repository...
    git init
    echo.
)

echo Adding all files...
git add .
echo.

echo Creating initial commit...
git commit -m "Phase 0: Backend Complete - Spring Boot + MySQL + Clean Architecture + Multilingual Support"
echo.

echo ============================================
echo IMPORTANT: Set your GitHub repository URL
echo ============================================
echo.
echo Please:
echo 1. Go to https://github.com/new
echo 2. Create a new repository named: readyroad
echo 3. Copy the repository URL
echo.
set /p REPO_URL="Paste your GitHub repository URL here: "

echo.
echo Adding remote origin...
git remote remove origin 2>nul
git remote add origin %REPO_URL%
echo.

echo Renaming branch to main...
git branch -M main
echo.

echo Pushing to GitHub...
echo (You may need to enter your GitHub credentials)
echo.
git push -u origin main

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ============================================
    echo SUCCESS! Project uploaded to GitHub!
    echo ============================================
    echo.
    echo Your project is now available at:
    echo %REPO_URL%
    echo.
) else (
    echo.
    echo ============================================
    echo Upload failed!
    echo ============================================
    echo.
    echo Possible reasons:
    echo 1. Invalid credentials
    echo 2. Repository doesn't exist
    echo 3. No internet connection
    echo.
    echo Please check GITHUB_UPLOAD_GUIDE.md for help
    echo.
)

pause

