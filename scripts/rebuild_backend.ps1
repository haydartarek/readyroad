$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = Split-Path -Parent $scriptDir

Push-Location $projectRoot
try {
    Write-Host "Rebuilding backend only..."
    docker compose build backend
    docker compose up -d --no-deps backend

    Write-Host ""
    Write-Host "Current container status:"
    docker compose ps backend mysql
}
finally {
    Pop-Location
}
