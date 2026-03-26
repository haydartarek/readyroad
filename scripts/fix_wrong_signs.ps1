[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8

$base = "C:\Users\haydar\Desktop\end_project\readyroad\src\main\resources\data\signs_import"

function Save-Questions($code, $questions) {
    $path = Join-Path $base "$code\questions.json"
    $json = $questions | ConvertTo-Json -Depth 20 -Compress
    [System.IO.File]::WriteAllText($path, $json, (New-Object System.Text.UTF8Encoding $false))
    Write-Host "$code : written $($questions.Count) questions ($($json.Length) bytes)"
}

# ============================================================
# PART 1: Truncate A49, A50, A51, A53 to first 8 questions
# ============================================================
foreach ($code in @("A49","A50","A51","A53")) {
    $path = Join-Path $base "$code\questions.json"
    $j = Get-Content $path -Raw -Encoding UTF8 | ConvertFrom-Json
    $first8 = $j[0..7]
    Save-Questions $code $first8
}

Write-Host "`nPart 1 done: A-series truncated to 8`n"
