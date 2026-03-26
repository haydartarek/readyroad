$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = Split-Path -Parent $scriptDir

Push-Location $projectRoot
try {
    Write-Host "Rebuilding frontend only..."
    docker compose build frontend
    docker compose up -d --no-deps frontend

    Write-Host ""
    Write-Host "Current container status:"
    docker compose ps frontend backend
}
finally {
    Pop-Location
}
