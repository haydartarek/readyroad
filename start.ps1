# ReadyRoad Quick Start
Write-Host "Starting ReadyRoad with Docker..." -ForegroundColor Cyan

# Check Docker
docker version | Out-Null
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Docker not running!" -ForegroundColor Red
    exit 1
}

# Create .env if missing
if (-not (Test-Path ".env")) {
    Copy-Item ".env.example" ".env"
    Write-Host "Created .env - Please edit and rerun" -ForegroundColor Yellow
    exit 0
}

# Start containers (try both docker compose and docker-compose)
docker compose up -d 2>$null
if ($LASTEXITCODE -ne 0) {
    docker-compose up -d
}

if ($LASTEXITCODE -eq 0) {
    Write-Host "SUCCESS! Backend: http://localhost:8890" -ForegroundColor Green
    Write-Host "Logs: docker-compose logs -f backend" -ForegroundColor White
} else {
    Write-Host "ERROR: Failed to start" -ForegroundColor Red
    exit 1
}
