# Ready Road - Direct Upload to GitHub
# Repository: https://github.com/haydartarek/readyroad

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "Ready Road - GitHub Upload" -ForegroundColor Cyan
Write-Host "Uploading to: haydartarek/readyroad" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# Check if Git is installed
$gitPath = Get-Command git -ErrorAction SilentlyContinue
if (-not $gitPath) {
    Write-Host "Git is not installed!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Installing Git..." -ForegroundColor Yellow
    Write-Host ""

    try {
        # Try to install Git using winget
        winget install --id Git.Git -e --source winget --silent

        Write-Host ""
        Write-Host "Git installation initiated!" -ForegroundColor Green
        Write-Host ""
        Write-Host "IMPORTANT:" -ForegroundColor Yellow
        Write-Host "1. Wait for Git installation to complete" -ForegroundColor White
        Write-Host "2. CLOSE this PowerShell window" -ForegroundColor White
        Write-Host "3. Open a NEW PowerShell window" -ForegroundColor White
        Write-Host "4. Run this script again" -ForegroundColor White
        Write-Host ""
    } catch {
        Write-Host "Failed to auto-install Git" -ForegroundColor Red
        Write-Host ""
        Write-Host "Please install Git manually:" -ForegroundColor Yellow
        Write-Host "1. Visit: https://git-scm.com/download/win" -ForegroundColor White
        Write-Host "2. Download and install Git" -ForegroundColor White
        Write-Host "3. Restart this script" -ForegroundColor White
        Write-Host ""
    }

    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "Git found! Version:" -ForegroundColor Green
git --version
Write-Host ""

# Navigate to project
$projectPath = "C:\Users\fqsdg\IdeaProjects\readyroad"
Set-Location $projectPath

# Initialize git if not already
if (-not (Test-Path ".git")) {
    Write-Host "[1/6] Initializing Git repository..." -ForegroundColor Green
    git init
    Write-Host ""
} else {
    Write-Host "[1/6] Git repository already initialized" -ForegroundColor Yellow
    Write-Host ""
}

# Configure git user if needed
$gitUser = git config user.name 2>$null
if (-not $gitUser) {
    Write-Host "Configuring Git user..." -ForegroundColor Yellow
    git config user.name "Haydar Tarek"
    git config user.email "haydartarek@users.noreply.github.com"
    Write-Host ""
}

# Add all files
Write-Host "[2/6] Adding all files..." -ForegroundColor Green
git add .
Write-Host ""

# Create initial commit
Write-Host "[3/6] Creating initial commit..." -ForegroundColor Green
$commitResult = git commit -m "Phase 0: Backend Complete - Spring Boot + MySQL + Clean Architecture + Multilingual Support (ar/en/nl/fr)" 2>&1
Write-Host $commitResult
Write-Host ""

# Remove old remote if exists
Write-Host "[4/6] Setting up remote repository..." -ForegroundColor Green
git remote remove origin 2>$null
git remote add origin https://github.com/haydartarek/readyroad.git
Write-Host "Remote: https://github.com/haydartarek/readyroad.git" -ForegroundColor Cyan
Write-Host ""

# Rename branch to main
Write-Host "[5/6] Setting branch to main..." -ForegroundColor Green
git branch -M main
Write-Host ""

# Push to GitHub
Write-Host "[6/6] Pushing to GitHub..." -ForegroundColor Green
Write-Host "You may need to authenticate with GitHub..." -ForegroundColor Yellow
Write-Host ""
Write-Host "Authentication options:" -ForegroundColor Cyan
Write-Host "1. Use GitHub Personal Access Token" -ForegroundColor White
Write-Host "2. Sign in through browser (recommended)" -ForegroundColor White
Write-Host ""

$pushResult = git push -u origin main 2>&1
$exitCode = $LASTEXITCODE

Write-Host ""

if ($exitCode -eq 0) {
    Write-Host "============================================" -ForegroundColor Green
    Write-Host "SUCCESS! Project uploaded to GitHub!" -ForegroundColor Green
    Write-Host "============================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Repository URL:" -ForegroundColor Cyan
    Write-Host "https://github.com/haydartarek/readyroad" -ForegroundColor White
    Write-Host ""
    Write-Host "Next steps:" -ForegroundColor Yellow
    Write-Host "1. Visit the repository on GitHub" -ForegroundColor White
    Write-Host "2. Check the README.md" -ForegroundColor White
    Write-Host "3. Continue with Phase 0 - Mobile (Flutter)" -ForegroundColor White
    Write-Host ""
} else {
    Write-Host "============================================" -ForegroundColor Red
    Write-Host "Upload encountered an issue" -ForegroundColor Red
    Write-Host "============================================" -ForegroundColor Red
    Write-Host ""

    if ($pushResult -like "*Authentication*" -or $pushResult -like "*fatal: could not read*") {
        Write-Host "Authentication is required!" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "Option 1: Use Personal Access Token" -ForegroundColor Cyan
        Write-Host "1. Visit: https://github.com/settings/tokens" -ForegroundColor White
        Write-Host "2. Generate new token (classic)" -ForegroundColor White
        Write-Host "3. Select: repo (full control)" -ForegroundColor White
        Write-Host "4. Copy the token" -ForegroundColor White
        Write-Host "5. Run: git push -u origin main" -ForegroundColor White
        Write-Host "6. Use token as password" -ForegroundColor White
        Write-Host ""
        Write-Host "Option 2: Use GitHub CLI (Easier)" -ForegroundColor Cyan
        Write-Host "1. Run: winget install GitHub.cli" -ForegroundColor White
        Write-Host "2. Run: gh auth login" -ForegroundColor White
        Write-Host "3. Run: git push -u origin main" -ForegroundColor White
        Write-Host ""
    } elseif ($pushResult -like "*repository not found*") {
        Write-Host "Repository not found or not accessible!" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "Please verify:" -ForegroundColor Cyan
        Write-Host "1. Repository exists at: https://github.com/haydartarek/readyroad" -ForegroundColor White
        Write-Host "2. You have access to this repository" -ForegroundColor White
        Write-Host "3. Repository name is correct" -ForegroundColor White
        Write-Host ""
    } else {
        Write-Host "Error details:" -ForegroundColor Yellow
        Write-Host $pushResult -ForegroundColor Gray
        Write-Host ""
    }

    Write-Host "To retry manually:" -ForegroundColor Cyan
    Write-Host "git push -u origin main" -ForegroundColor White
    Write-Host ""
}

Read-Host "Press Enter to exit"

