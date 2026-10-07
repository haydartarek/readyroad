param(
    [switch]$Stop,
    [switch]$NoWeb,
    [switch]$NoMobile
)

$ErrorActionPreference = "Stop"

$Root = Split-Path -Parent $MyInvocation.MyCommand.Path

$BackendRoot  = Join-Path $Root "readyroad"
$FrontendRoot = Join-Path $Root "readyroad_front_end"
$WebRoot      = Join-Path $FrontendRoot "web_app"
$MobileRoot   = Join-Path $FrontendRoot "mobile_app"

$ComposeFile = Join-Path $Root "docker-compose.local.yml"
$EnvFile     = Join-Path $BackendRoot ".env"

$RuntimeDir = Join-Path $Root ".local"
$LogDir     = Join-Path $RuntimeDir "logs"
$PidFile    = Join-Path $RuntimeDir "run-local.pids.json"

$FlutterExecutable = "flutter"

New-Item -ItemType Directory -Force -Path $LogDir | Out-Null


function Invoke-LocalCompose {

    param(
        [Parameter(Mandatory = $true)]
        [string[]]$Arguments
    )

    & docker compose `
        --env-file $EnvFile `
        -f $ComposeFile `
        @Arguments

    if ($LASTEXITCODE -ne 0) {
        throw "docker compose failed with exit code $LASTEXITCODE"
    }
}


function Stop-TrackedProcess {

    param(
        [int]$ProcessId
    )

    if ($ProcessId -le 0) {
        return
    }

    $Existing = Get-Process `
        -Id $ProcessId `
        -ErrorAction SilentlyContinue

    if ($Existing) {
        # Native stderr must not terminate Windows PowerShell when a child
        # exits during taskkill. Check whether the tracked process survived.
        $PreviousErrorActionPreference = $ErrorActionPreference
        try {
            $ErrorActionPreference = "Continue"
            & taskkill.exe /PID $ProcessId /T /F 2>$null | Out-Null
        }
        finally {
            $ErrorActionPreference = $PreviousErrorActionPreference
        }
        if (Get-Process -Id $ProcessId -ErrorAction SilentlyContinue) {
            throw "Could not stop tracked process $ProcessId."
        }
    }
}


function Stop-PreviousHostApps {

    if (-not (Test-Path -LiteralPath $PidFile)) {
        return
    }

    try {

        $Pids = Get-Content `
            -LiteralPath $PidFile `
            -Raw |
            ConvertFrom-Json

        if ($Pids.PSObject.Properties.Name -contains "web") {
            Stop-TrackedProcess -ProcessId ([int]$Pids.web)
        }

        if ($Pids.PSObject.Properties.Name -contains "mobile") {
            Stop-TrackedProcess -ProcessId ([int]$Pids.mobile)
        }
    }
    finally {

        Remove-Item `
            -LiteralPath $PidFile `
            -Force `
            -ErrorAction SilentlyContinue
    }
}


function Test-LocalPort {

    param(
        [int]$Port
    )

    $Client = New-Object System.Net.Sockets.TcpClient

    try {

        $Task = $Client.ConnectAsync(
            "127.0.0.1",
            $Port
        )

        if (-not $Task.Wait(400)) {
            return $false
        }

        return $Client.Connected
    }
    catch {
        return $false
    }
    finally {
        $Client.Dispose()
    }
}


function Wait-LocalPort {

    param(
        [int]$Port,
        [int]$TimeoutSeconds,
        [string]$Name,
        [System.Diagnostics.Process]$Process = $null
    )

    $Deadline = (Get-Date).AddSeconds($TimeoutSeconds)

    while ((Get-Date) -lt $Deadline) {

        if (Test-LocalPort -Port $Port) {

            Write-Host "$Name ready: $Port" -ForegroundColor Green
            return
        }

        if (
            $Process -and
            $Process.HasExited
        ) {
            throw "$Name exited before port $Port became ready."
        }

        Start-Sleep -Seconds 2
    }

    throw "$Name did not become ready on port $Port."
}


function Get-LocalEnvValue {

    param(
        [string]$Key
    )

    $Line = Get-Content `
        -LiteralPath $EnvFile |
        Where-Object {
            $_ -match "^$([regex]::Escape($Key))="
        } |
        Select-Object -First 1

    if (-not $Line) {
        throw "Missing .env key: $Key"
    }

    return (($Line -split "=", 2)[1]).Trim().Trim('"')
}


# ============================================================
# PRECHECK
# ============================================================

foreach ($RequiredPath in @(
    $ComposeFile,
    $EnvFile,
    $BackendRoot,
    $WebRoot,
    $MobileRoot
)) {

    if (-not (Test-Path -LiteralPath $RequiredPath)) {
        throw "Missing required path: $RequiredPath"
    }
}


# ============================================================
# STOP MODE
# ============================================================

if ($Stop) {

    Write-Host "===== STOP WEB + MOBILE =====" -ForegroundColor Cyan

    Stop-PreviousHostApps

    Write-Host "===== STOP BACKEND + POSTGRES =====" -ForegroundColor Cyan

    Invoke-LocalCompose -Arguments @(
        "stop",
        "backend",
        "postgres"
    )

    Write-Host ""
    Write-Host "Rijvia local stack stopped." -ForegroundColor Green
    Write-Host "Database volume preserved."

    exit 0
}


# ============================================================
# STOP ONLY PREVIOUS DEV SERVERS
# ============================================================

Write-Host "===== STOP PREVIOUS DEV SERVERS =====" -ForegroundColor Cyan

Stop-PreviousHostApps


# ============================================================
# REMOVE OLD PRODUCTION FRONTEND CONTAINER IF IT STILL EXISTS
# ============================================================

$LegacyFrontend = @(
    docker ps `
        -a `
        --filter "name=^/readyroad-frontend$" `
        -q
)

if ($LegacyFrontend.Count -gt 0) {

    Write-Host "===== REMOVE OLD FRONTEND CONTAINER =====" -ForegroundColor Cyan

    docker rm -f readyroad-frontend | Out-Null

    if ($LASTEXITCODE -ne 0) {
        throw "Could not remove old readyroad-frontend container."
    }
}


# ============================================================
# PORT SAFETY
# ============================================================

if (
    -not $NoWeb -and
    (Test-LocalPort -Port 3000)
) {
    throw "Port 3000 is already in use."
}

if (
    -not $NoMobile -and
    (Test-LocalPort -Port 63783)
) {
    throw "Port 63783 is already in use."
}


# ============================================================
# VERIFY MEDIA OBJECT STORAGE
# ============================================================

Write-Host "===== VERIFY MEDIA OBJECT STORAGE =====" -ForegroundColor Cyan

$StorageProvider = Get-LocalEnvValue "STORAGE_PROVIDER"
$MediaRuntimeStatus = "LOCAL"

if ($StorageProvider -ieq "s3") {

    $StoragePublicBaseUrl = (Get-LocalEnvValue "STORAGE_PUBLIC_BASE_URL").TrimEnd('/')
    if ([string]::IsNullOrWhiteSpace($StoragePublicBaseUrl)) {
        throw "STORAGE_PUBLIC_BASE_URL is required when STORAGE_PROVIDER=s3."
    }

    $KnownLessonObject = "lessons/les-30/les-30-page-1-ec828f2f-9e7.png"
    $KnownLessonObjectUrl = "$StoragePublicBaseUrl/$KnownLessonObject"

    try {
        $ObjectResponse = Invoke-WebRequest `
            -UseBasicParsing `
            -Uri $KnownLessonObjectUrl `
            -Method Head `
            -TimeoutSec 20

        if ($ObjectResponse.StatusCode -lt 200 -or $ObjectResponse.StatusCode -ge 300) {
            throw "Unexpected object response status: $($ObjectResponse.StatusCode)"
        }
    }
    catch {
        throw "Object Storage media check failed: $KnownLessonObject"
    }

    $MediaRuntimeStatus = "S3"
    Write-Host "Object Storage: PASS" -ForegroundColor Green
}
else {
    Write-Host "Object Storage: SKIPPED (STORAGE_PROVIDER=$StorageProvider)" -ForegroundColor Yellow
}


# ============================================================
# START DATABASE + REBUILD CURRENT BACKEND SOURCE
# ============================================================

# ============================================================
# RIJVIA LOCAL QUIZ MEDIA GUARD - PRESTART
# ============================================================

if ($StorageProvider -eq "local") {

    Write-Host "===== VERIFY LOCAL QUIZ MEDIA =====" -ForegroundColor Cyan

    $LocalQuizImageRoot = Join-Path `
        $BackendRoot `
        "public\images\quiz"

    if (-not (Test-Path -LiteralPath $LocalQuizImageRoot)) {
        throw "Local quiz image directory is missing: $LocalQuizImageRoot"
    }

    $LocalQuizImageCount = @(
        Get-ChildItem `
            -LiteralPath $LocalQuizImageRoot `
            -Recurse `
            -File
    ).Count

    if ($LocalQuizImageCount -eq 0) {
        throw "Local quiz image directory is empty."
    }

    $QuizComposeLines = @(
        [System.IO.File]::ReadAllLines(
            $ComposeFile,
            [System.Text.Encoding]::UTF8
        )
    )

    $QuizTargetIndexes = @()

    for ($i = 0; $i -lt $QuizComposeLines.Count; $i++) {

        if (
            $QuizComposeLines[$i].Trim() -eq
            "target: /app/public/images/quiz"
        ) {
            $QuizTargetIndexes += $i
        }
    }

    if ($QuizTargetIndexes.Count -ne 1) {
        throw "Expected exactly one local quiz bind mount target."
    }

    $QuizTargetIndex = [int]$QuizTargetIndexes[0]

    if ($QuizTargetIndex -lt 1) {
        throw "Local quiz bind mount structure is invalid."
    }

    $QuizSourceLine =
        $QuizComposeLines[$QuizTargetIndex - 1].Trim()

    if (
        $QuizSourceLine -ne
        "source: ./readyroad/public/images/quiz"
    ) {
        throw "Local quiz bind mount points to an unexpected source."
    }

    $QuizBlockEnd =
        [Math]::Min(
            $QuizComposeLines.Count - 1,
            $QuizTargetIndex + 3
        )

    for (
        $i = $QuizTargetIndex + 1;
        $i -le $QuizBlockEnd;
        $i++
    ) {

        $CurrentLine =
            $QuizComposeLines[$i].Trim()

        if ($CurrentLine -eq "- type: bind") {
            break
        }

        if ($CurrentLine -eq "read_only: true") {
            throw "Local quiz bind mount is configured read-only."
        }
    }

    Write-Host "Quiz media : $LocalQuizImageCount local files" -ForegroundColor Green
    Write-Host "Quiz config: persistent READ/WRITE bind mount" -ForegroundColor Green
}
Write-Host "===== START DATABASE + BUILD BACKEND =====" -ForegroundColor Cyan

Invoke-LocalCompose -Arguments @(
    "up",
    "-d",
    "--build",
    "postgres",
    "backend"
)


# ============================================================
# WAIT FOR BACKEND HEALTH
# ============================================================

Write-Host "===== WAIT FOR BACKEND =====" -ForegroundColor Cyan

$Deadline = (Get-Date).AddMinutes(5)
$Health = ""

do {

    $Health = (
        docker inspect `
            -f "{{.State.Health.Status}}" `
            readyroad-backend `
            2>$null
    )

    if ($Health -eq "healthy") {
        break
    }

    if ($Health -eq "unhealthy") {
        throw "Backend became unhealthy."
    }

    Start-Sleep -Seconds 3

} while ((Get-Date) -lt $Deadline)

if ($Health -ne "healthy") {
    throw "Backend did not become healthy within 5 minutes."
}

Write-Host "Backend: HEALTHY" -ForegroundColor Green


# ============================================================
# VERIFY CMS DATABASE
# ============================================================

# ============================================================
# RIJVIA LOCAL QUIZ MEDIA GUARD - RUNTIME
# ============================================================

if ($StorageProvider -eq "local") {

    Write-Host "===== VERIFY LOCAL QUIZ MEDIA MOUNT =====" -ForegroundColor Cyan

    $BackendMediaInspectRaw =
        & docker inspect readyroad-backend

    if ($LASTEXITCODE -ne 0) {
        throw "Could not inspect readyroad-backend for local media."
    }

    $BackendMediaInspect =
        $BackendMediaInspectRaw |
        ConvertFrom-Json

    $QuizRuntimeMount = @(
        $BackendMediaInspect[0].Mounts |
        Where-Object {
            $_.Destination -eq "/app/public/images/quiz"
        }
    )

    if ($QuizRuntimeMount.Count -ne 1) {
        throw "Runtime quiz mount count is not exactly one."
    }

    if ($QuizRuntimeMount[0].Type -ne "bind") {
        throw "Runtime quiz media is not a bind mount."
    }

    if (-not $QuizRuntimeMount[0].RW) {
        throw "Runtime quiz bind mount is not writable."
    }

    $ContainerQuizFiles = @(
        & docker exec `
            readyroad-backend `
            find `
            /app/public/images/quiz `
            -type f
    )

    if ($LASTEXITCODE -ne 0) {
        throw "Could not enumerate runtime quiz media."
    }

    $HostQuizFiles = @(
        Get-ChildItem `
            -LiteralPath $LocalQuizImageRoot `
            -Recurse `
            -File
    )

    if ($ContainerQuizFiles.Count -ne $HostQuizFiles.Count) {
        throw "Quiz media mismatch: host=$($HostQuizFiles.Count), container=$($ContainerQuizFiles.Count)."
    }

    Write-Host "Quiz mount : PERSISTENT READ/WRITE" -ForegroundColor Green
    Write-Host "Quiz files : $($HostQuizFiles.Count) host = $($ContainerQuizFiles.Count) container" -ForegroundColor Green
}
Write-Host "===== VERIFY CMS DATABASE =====" -ForegroundColor Cyan

$DbUser = Get-LocalEnvValue "POSTGRES_USERNAME"
$DbName = Get-LocalEnvValue "POSTGRES_DB"

$Sql = "SELECT COUNT(*)::text || '|' || COALESCE((SELECT lesson_code FROM readyroad.lessons ORDER BY display_order DESC, id DESC LIMIT 1),'none') || '|' || COALESCE(MAX(display_order),0)::text || '|' || (SELECT COUNT(*)::text FROM readyroad.lesson_pages p JOIN readyroad.lessons l ON l.id=p.lesson_id WHERE l.lesson_code='les-30') || '|' || (SELECT COUNT(*)::text FROM readyroad.lesson_pages p JOIN readyroad.lessons l ON l.id=p.lesson_id WHERE l.lesson_code='les-31') FROM readyroad.lessons;"

$DbLines = @(
    & docker compose `
        --env-file $EnvFile `
        -f $ComposeFile `
        exec -T postgres `
        psql `
        -X `
        -U $DbUser `
        -d $DbName `
        -A `
        -t `
        -v ON_ERROR_STOP=1 `
        -c $Sql
)

if ($LASTEXITCODE -ne 0) {
    throw "Database verification failed."
}

$DbResult = ($DbLines -join "`n").Trim()

$Parts = $DbResult -split "\|"

if ($Parts.Count -ne 5) {
    throw "Unexpected database verification result: $DbResult"
}

$LessonCount = [int]$Parts[0]
$LastLesson  = $Parts[1]
$MaxOrder    = [int]$Parts[2]
$Les30Pages  = [int]$Parts[3]
$Les31Pages  = [int]$Parts[4]

if ($LessonCount -lt 32) {
    throw "Database has only $LessonCount lessons."
}

if ($Les30Pages -ne 8) {
    throw "les-30 page count is $Les30Pages instead of 8."
}

if ($Les31Pages -ne 10) {
    throw "les-31 page count is $Les31Pages instead of 10."
}

Write-Host "Lessons      : $LessonCount" -ForegroundColor Green
Write-Host "Last lesson  : $LastLesson"
Write-Host "Display order: $MaxOrder"
Write-Host "les-30 pages : $Les30Pages"
Write-Host "les-31 pages : $Les31Pages"


# ============================================================
# START WEB_APP FROM CURRENT SOURCE
# ============================================================

$Started = @{}


if (-not $NoWeb) {

    Write-Host "===== START WEB_APP =====" -ForegroundColor Cyan

    $WebLog      = Join-Path $LogDir "web.log"
    $WebErrorLog = Join-Path $LogDir "web.error.log"

    $WebCommand = '$env:BACKEND_URL="http://localhost:8890/api"; $env:NEXT_PUBLIC_API_BASE_URL="http://localhost:8890"; $env:FRONTEND_URL="http://localhost:3000"; npm run dev -- --hostname 127.0.0.1 --port 3000'

    $WebProcess = Start-Process `
        -FilePath "powershell.exe" `
        -ArgumentList @(
            "-NoProfile",
            "-ExecutionPolicy",
            "Bypass",
            "-Command",
            $WebCommand
        ) `
        -WorkingDirectory $WebRoot `
        -WindowStyle Hidden `
        -RedirectStandardOutput $WebLog `
        -RedirectStandardError $WebErrorLog `
        -PassThru

    $Started.web = $WebProcess.Id
    $Started | ConvertTo-Json | Set-Content -LiteralPath $PidFile -Encoding UTF8

    Wait-LocalPort `
        -Port 3000 `
        -TimeoutSeconds 120 `
        -Name "Rijvia Web" `
        -Process $WebProcess
}


# ============================================================
# START MOBILE_APP
# ============================================================

if (-not $NoMobile) {

    Write-Host "===== START MOBILE_APP =====" -ForegroundColor Cyan

    $MobileLog      = Join-Path $LogDir "mobile.log"
    $MobileErrorLog = Join-Path $LogDir "mobile.error.log"

    $FlutterCommandInfo = Get-Command "flutter" -ErrorAction SilentlyContinue
    if ($FlutterCommandInfo) {
        $FlutterExecutable = $FlutterCommandInfo.Source
    }
    elseif (Test-Path -LiteralPath "C:\src\flutter\bin\flutter.bat") {
        $FlutterExecutable = "C:\src\flutter\bin\flutter.bat"
    }
    else {
        throw "Flutter executable was not found. Add Flutter bin to PATH."
    }

    $FlutterCommand = "& '$($FlutterExecutable.Replace("'", "''"))' run -d chrome --web-port 63783 --dart-define=API_BASE_URL=http://localhost:8890"

    $MobileProcess = Start-Process `
        -FilePath "powershell.exe" `
        -ArgumentList @(
            "-NoProfile",
            "-ExecutionPolicy",
            "Bypass",
            "-Command",
            $FlutterCommand
        ) `
        -WorkingDirectory $MobileRoot `
        -WindowStyle Hidden `
        -RedirectStandardOutput $MobileLog `
        -RedirectStandardError $MobileErrorLog `
        -PassThru

    $Started.mobile = $MobileProcess.Id
    $Started | ConvertTo-Json | Set-Content -LiteralPath $PidFile -Encoding UTF8

    Wait-LocalPort `
        -Port 63783 `
        -TimeoutSeconds 180 `
        -Name "Rijvia Mobile" `
        -Process $MobileProcess
}


# ============================================================
# SAVE DEV SERVER PIDS
# ============================================================

$Started |
    ConvertTo-Json |
    Set-Content `
        -LiteralPath $PidFile `
        -Encoding UTF8


# ============================================================
# OPEN WEB
# ============================================================

if (-not $NoWeb) {
    Start-Process "http://localhost:3000"
}


# ============================================================
# FINAL RESULT
# ============================================================

Write-Host ""
Write-Host "============================================" -ForegroundColor Green
Write-Host "RIJVIA LOCAL STACK READY" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Green
Write-Host "Web       : http://localhost:3000"
Write-Host "Mobile    : http://localhost:63783"
Write-Host "Backend   : http://localhost:8890"
Write-Host "Lessons   : $LessonCount"
Write-Host "Last      : $LastLesson"
Write-Host "Media     : $MediaRuntimeStatus"
Write-Host "Logs      : $LogDir"
Write-Host ""
Write-Host "Stop      : .\run-local.ps1 -Stop"
