$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$backendRoot = Split-Path -Parent $scriptDir
$workspaceRoot = Split-Path -Parent $backendRoot

$sourceRoot = Join-Path $backendRoot "public\\images\\signs"
$targetRoot = Join-Path $workspaceRoot "readyroad_front_end\\web_app\\public\\images\\signs"
$manifestPath = Join-Path $workspaceRoot "readyroad_front_end\\web_app\\src\\lib\\traffic-sign-image-manifest.json"

if (-not (Test-Path $sourceRoot)) {
    throw "Source signs directory not found: $sourceRoot"
}

if (-not (Test-Path $targetRoot)) {
    throw "Target signs directory not found: $targetRoot"
}

if (-not (Test-Path $manifestPath)) {
    throw "Canonical sign manifest not found: $manifestPath"
}

$copiedFiles = New-Object System.Collections.Generic.List[string]
$missingBackendFiles = New-Object System.Collections.Generic.List[string]
$manifest = Get-Content -Path $manifestPath -Raw | ConvertFrom-Json

foreach ($relativePath in $manifest) {
    $windowsRelativePath = ($relativePath -replace '^/images/signs/', '') -replace '/', '\'
    $sourceFile = Join-Path $sourceRoot $windowsRelativePath
    $targetFile = Join-Path $targetRoot $windowsRelativePath

    if (-not (Test-Path $sourceFile)) {
        $missingBackendFiles.Add($sourceFile) | Out-Null
        continue
    }

    if (-not (Test-Path $targetFile)) {
        $targetDirectory = Split-Path -Parent $targetFile
        if (-not (Test-Path $targetDirectory)) {
            New-Item -ItemType Directory -Path $targetDirectory | Out-Null
        }

        Copy-Item -Path $sourceFile -Destination $targetFile
        $copiedFiles.Add($targetFile) | Out-Null
    }
}

Write-Host "Copied $($copiedFiles.Count) exact canonical sign image(s)."

if ($copiedFiles.Count -gt 0) {
    $copiedFiles | Sort-Object | ForEach-Object { Write-Host $_ }
}

if ($missingBackendFiles.Count -gt 0) {
    Write-Host "Skipped $($missingBackendFiles.Count) manifest path(s) not present in backend source:"
    $missingBackendFiles | Sort-Object | ForEach-Object { Write-Host $_ }
}
