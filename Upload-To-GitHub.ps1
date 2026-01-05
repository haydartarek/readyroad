# Ready Road - GitHub Upload Script (PowerShell)

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "Ready Road - GitHub Upload Script" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# Check if Git is installed
$gitInstalled = Get-Command git -ErrorAction SilentlyContinue
if (-not $gitInstalled) {
    Write-Host "ERROR: Git is not installed!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please install Git from: https://git-scm.com/" -ForegroundColor Yellow
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "Git found! Version:" -ForegroundColor Green
git --version
Write-Host ""

# Navigate to project directory
$projectPath = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectPath

# Check if already initialized
if (Test-Path ".git") {
    Write-Host "Git repository already initialized." -ForegroundColor Yellow
    Write-Host ""
} else {
    Write-Host "Initializing Git repository..." -ForegroundColor Green
    git init
    Write-Host ""
}

# Add all files
Write-Host "Adding all files..." -ForegroundColor Green
git add .
Write-Host ""

# Create initial commit
Write-Host "Creating initial commit..." -ForegroundColor Green
git commit -m "Phase 0: Backend Complete - Spring Boot + MySQL + Clean Architecture + Multilingual Support"
Write-Host ""

# Get GitHub repository URL
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "GitHub Repository Setup" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Please:" -ForegroundColor Yellow
Write-Host "1. Go to https://github.com/new" -ForegroundColor White
Write-Host "2. Create a new repository named: readyroad" -ForegroundColor White
Write-Host "3. Copy the repository URL" -ForegroundColor White
Write-Host ""
Write-Host "Example: https://github.com/YOUR_USERNAME/readyroad.git" -ForegroundColor Gray
Write-Host ""

$repoUrl = Read-Host "Paste your GitHub repository URL here"

if ([string]::IsNullOrWhiteSpace($repoUrl)) {
    Write-Host ""
    Write-Host "ERROR: No repository URL provided!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host ""
Write-Host "Adding remote origin..." -ForegroundColor Green
git remote remove origin 2>$null
git remote add origin $repoUrl
Write-Host ""

Write-Host "Renaming branch to main..." -ForegroundColor Green
git branch -M main
Write-Host ""

Write-Host "Pushing to GitHub..." -ForegroundColor Green
Write-Host "(You may need to enter your GitHub credentials)" -ForegroundColor Yellow
Write-Host ""

$pushResult = git push -u origin main 2>&1
$exitCode = $LASTEXITCODE

if ($exitCode -eq 0) {
    Write-Host ""
    Write-Host "============================================" -ForegroundColor Green
    Write-Host "SUCCESS! Project uploaded to GitHub!" -ForegroundColor Green
    Write-Host "============================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Your project is now available at:" -ForegroundColor Cyan
    Write-Host $repoUrl -ForegroundColor White
    Write-Host ""
    Write-Host "Next steps:" -ForegroundColor Yellow
    Write-Host "1. Visit your repository on GitHub" -ForegroundColor White
    Write-Host "2. Add a description and topics" -ForegroundColor White
    Write-Host "3. Invite collaborators if needed" -ForegroundColor White
    Write-Host ""
} else {
    Write-Host ""
    Write-Host "============================================" -ForegroundColor Red
    Write-Host "Upload failed!" -ForegroundColor Red
    Write-Host "============================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "Possible reasons:" -ForegroundColor Yellow
    Write-Host "1. Invalid credentials or token" -ForegroundColor White
    Write-Host "2. Repository doesn't exist on GitHub" -ForegroundColor White
    Write-Host "3. No internet connection" -ForegroundColor White
    Write-Host "4. Repository URL is incorrect" -ForegroundColor White
    Write-Host ""
    Write-Host "Error details:" -ForegroundColor Yellow
    Write-Host $pushResult -ForegroundColor Gray
    Write-Host ""
    Write-Host "Please check GITHUB_UPLOAD_GUIDE.md for detailed help" -ForegroundColor Cyan
    Write-Host ""
}

Read-Host "Press Enter to exit"

