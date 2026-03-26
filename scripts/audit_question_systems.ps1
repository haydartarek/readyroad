param(
    [string]$BackendBaseUrl = "http://localhost:8890",
    [switch]$SkipFullSignAudit
)

$ErrorActionPreference = "Stop"

function Invoke-Json {
    param(
        [Parameter(Mandatory = $true)][string]$Method,
        [Parameter(Mandatory = $true)][string]$Url,
        [hashtable]$Headers,
        $Body,
        [int[]]$AllowedStatusCodes = @(200, 201)
    )

    $params = @{
        Method = $Method
        Uri = $Url
        Headers = $Headers
        ContentType = "application/json"
        SkipHttpErrorCheck = $true
    }

    if ($null -ne $Body) {
        $params.Body = ($Body | ConvertTo-Json -Depth 20 -Compress)
    }

    $response = Invoke-WebRequest @params
    if ($AllowedStatusCodes -notcontains [int]$response.StatusCode) {
        throw "Unexpected status $($response.StatusCode) for $Method $Url`n$($response.Content)"
    }

    if ([string]::IsNullOrWhiteSpace($response.Content)) {
        return @{
            status = [int]$response.StatusCode
            data = $null
        }
    }

    return @{
        status = [int]$response.StatusCode
        data = ($response.Content | ConvertFrom-Json -Depth 20)
    }
}

function New-Distribution {
    @{
        "1" = 0
        "2" = 0
        "3" = 0
        unknown = 0
    }
}

function Add-DistributionValue {
    param(
        [hashtable]$Distribution,
        [int]$Position
    )

    if ($Position -lt 1) {
        $Distribution["unknown"]++
        return
    }

    $key = [string]$Position
    if (-not $Distribution.ContainsKey($key)) {
        $Distribution[$key] = 0
    }

    $Distribution[$key]++
}

function Test-ChoiceContract {
    param(
        [System.Collections.Generic.List[object]]$Anomalies,
        [string]$SystemName,
        [array]$Options,
        [hashtable]$Context = @{}
    )

    if ($Options.Count -lt 2) {
        Add-Anomaly -List $Anomalies -SystemName $SystemName -Message "Question exposes fewer than 2 options." -Context $Context
    }

    if ($Options.Count -gt 3) {
        Add-Anomaly -List $Anomalies -SystemName $SystemName -Message "Question exposes more than 3 options." -Context $Context
    }
}

function Test-NoLegacySignSeriesWording {
    param(
        [System.Collections.Generic.List[object]]$Anomalies,
        [string]$SystemName,
        [array]$Options,
        [hashtable]$Context = @{}
    )

    $pattern = '\([A-Z]+-series\)|\([A-Z]+-reeks\)|\((série|serie|sÃ©rie)\s*[A-Z]+\)|\(سلسلة\s*[A-Z]+\)'

    foreach ($option in $Options) {
        foreach ($field in @('textEn', 'textNl', 'textFr', 'textAr')) {
            if ($null -eq $option.PSObject.Properties[$field]) {
                continue
            }

            $value = [string]$option.$field
            if (-not [string]::IsNullOrWhiteSpace($value) -and $value -match $pattern) {
                Add-Anomaly -List $Anomalies -SystemName $SystemName -Message "Legacy sign series wording leaked into visible answer choices." -Context (@{
                    field = $field
                    value = $value
                } + $Context)
                return
            }
        }
    }
}

function Get-OptionId {
    param($Option)

    foreach ($name in @("id", "optionId", "choiceId")) {
        if ($null -ne $Option.PSObject.Properties[$name]) {
            return [long]$Option.$name
        }
    }

    throw "Option object does not expose a known ID field."
}

function Get-CorrectPosition {
    param(
        [array]$Options,
        [long]$CorrectId
    )

    for ($i = 0; $i -lt $Options.Count; $i++) {
        if ((Get-OptionId $Options[$i]) -eq $CorrectId) {
            return $i + 1
        }
    }

    return -1
}

function Assert {
    param(
        [Parameter(Mandatory = $true)][bool]$Condition,
        [Parameter(Mandatory = $true)][string]$Message
    )

    if (-not $Condition) {
        throw $Message
    }
}

function New-AuthHeaders {
    param([string]$Token)
    @{
        Authorization = "Bearer $Token"
    }
}

function Add-Anomaly {
    param(
        [System.Collections.Generic.List[object]]$List,
        [string]$SystemName,
        [string]$Message,
        [hashtable]$Context = @{}
    )

    $item = [ordered]@{
        system  = $SystemName
        message = $Message
    }

    foreach ($key in $Context.Keys) {
        $item[$key] = $Context[$key]
    }

    $List.Add([pscustomobject]$item)
}

$report = [ordered]@{
    generatedAt = (Get-Date).ToString("o")
    backendBaseUrl = $BackendBaseUrl
    signAssetsUntouched = $true
    user = $null
    summary = [ordered]@{}
    systems = [ordered]@{}
    anomalies = New-Object System.Collections.Generic.List[object]
}

$stamp = Get-Date -Format "yyyyMMddHHmmssfff"
$registerPayload = @{
    username = "au$($stamp.Substring($stamp.Length - 10))"
    email    = "audit_$stamp@example.com"
    fullName = "Audit User $stamp"
    password = "AuditUser2026!"
}

$register = Invoke-Json -Method "POST" -Url "$BackendBaseUrl/api/auth/register" -Body $registerPayload
$token = $register.data.token
Assert ($null -ne $token -and $token.Length -gt 10) "Registration did not return a usable JWT token."
$headers = New-AuthHeaders -Token $token
$report.user = [ordered]@{
    username = $registerPayload.username
    email = $registerPayload.email
}

# ---------------------------------------------------------------------------
# Category inventory
# ---------------------------------------------------------------------------

$categoriesRes = Invoke-Json -Method "GET" -Url "$BackendBaseUrl/api/categories" -Headers $headers
$categories = @($categoriesRes.data)
$report.summary.categoryCount = $categories.Count

# ---------------------------------------------------------------------------
# Basic quiz (random)
# ---------------------------------------------------------------------------

$quizRandomQuestions = @((Invoke-Json -Method "GET" -Url "$BackendBaseUrl/api/quiz/random?count=50" -Headers $headers).data)
$quizRandomDistribution = New-Distribution
$quizRandomAnswered = 0

foreach ($question in $quizRandomQuestions) {
    Test-ChoiceContract -Anomalies $report.anomalies -SystemName "quiz.random" -Options @($question.options) -Context @{
        questionId = $question.id
    }
    $firstOptionId = Get-OptionId $question.options[0]
    $answer = Invoke-Json `
        -Method "POST" `
        -Url "$BackendBaseUrl/api/quiz/questions/$($question.id)/answer" `
        -Headers $headers `
        -Body @{ selectedOptionId = $firstOptionId; timeTakenSeconds = 5 }

    $correctOptionId = [long]$answer.data.correctOptionId
    $position = Get-CorrectPosition -Options @($question.options) -CorrectId $correctOptionId
    Add-DistributionValue -Distribution $quizRandomDistribution -Position $position

    $expectedCorrect = ($firstOptionId -eq $correctOptionId)
    if ([bool]$answer.data.isCorrect -ne $expectedCorrect) {
        Add-Anomaly -List $report.anomalies -SystemName "quiz.random" -Message "Immediate feedback correctness mismatch." -Context @{
            questionId = $question.id
            selectedOptionId = $firstOptionId
            correctOptionId = $correctOptionId
        }
    }

    $quizRandomAnswered++
}

$report.systems.quiz_random = [ordered]@{
    questionsFetched = $quizRandomQuestions.Count
    questionsAnswered = $quizRandomAnswered
    correctPositionDistribution = $quizRandomDistribution
}

# ---------------------------------------------------------------------------
# Basic quiz by category (smoke all categories)
# ---------------------------------------------------------------------------

$quizCategoryChecks = New-Object System.Collections.Generic.List[object]

foreach ($category in $categories) {
    $questions = @((Invoke-Json -Method "GET" -Url "$BackendBaseUrl/api/quiz/category/$($category.id)?count=5" -Headers $headers).data)
    if ($questions.Count -eq 0) {
        Add-Anomaly -List $report.anomalies -SystemName "quiz.by_category" -Message "Public category returned zero quiz questions." -Context @{
            categoryId = $category.id
            categoryCode = $category.code
        }
    }
    $quizCategoryChecks.Add([pscustomobject]@{
        categoryId = $category.id
        categoryCode = $category.code
        fetched = $questions.Count
    })
}

$report.systems.quiz_by_category = [ordered]@{
    categoriesChecked = $quizCategoryChecks.Count
    checks = $quizCategoryChecks
}

# ---------------------------------------------------------------------------
# Smart quiz (random)
# ---------------------------------------------------------------------------

$smartRandomQuestions = @((Invoke-Json -Method "GET" -Url "$BackendBaseUrl/api/smart-quiz/random?count=50" -Headers $headers).data)
$smartRandomDistribution = New-Distribution
$smartRandomAnswered = 0

foreach ($question in $smartRandomQuestions) {
    Test-ChoiceContract -Anomalies $report.anomalies -SystemName "smart_quiz.random" -Options @($question.options) -Context @{
        questionId = $question.id
    }
    $firstOptionId = Get-OptionId $question.options[0]
    $answer = Invoke-Json `
        -Method "POST" `
        -Url "$BackendBaseUrl/api/quiz/questions/$($question.id)/answer" `
        -Headers $headers `
        -Body @{ selectedOptionId = $firstOptionId; timeTakenSeconds = 4 }

    $correctOptionId = [long]$answer.data.correctOptionId
    $position = Get-CorrectPosition -Options @($question.options) -CorrectId $correctOptionId
    Add-DistributionValue -Distribution $smartRandomDistribution -Position $position

    $expectedCorrect = ($firstOptionId -eq $correctOptionId)
    if ([bool]$answer.data.isCorrect -ne $expectedCorrect) {
        Add-Anomaly -List $report.anomalies -SystemName "smart_quiz.random" -Message "Immediate feedback correctness mismatch." -Context @{
            questionId = $question.id
            selectedOptionId = $firstOptionId
            correctOptionId = $correctOptionId
        }
    }

    $smartRandomAnswered++
}

$smartStats = (Invoke-Json -Method "GET" -Url "$BackendBaseUrl/api/smart-quiz/stats" -Headers $headers).data

$report.systems.smart_quiz_random = [ordered]@{
    questionsFetched = $smartRandomQuestions.Count
    questionsAnswered = $smartRandomAnswered
    correctPositionDistribution = $smartRandomDistribution
    personalizedStats = $smartStats
}

# ---------------------------------------------------------------------------
# Smart quiz by category (smoke all categories)
# ---------------------------------------------------------------------------

$smartCategoryChecks = New-Object System.Collections.Generic.List[object]

foreach ($category in $categories) {
    $questions = @((Invoke-Json -Method "GET" -Url "$BackendBaseUrl/api/smart-quiz/category/$($category.id)?count=5" -Headers $headers).data)
    if ($questions.Count -eq 0) {
        Add-Anomaly -List $report.anomalies -SystemName "smart_quiz.by_category" -Message "Public category returned zero smart-quiz questions." -Context @{
            categoryId = $category.id
            categoryCode = $category.code
        }
    }
    $smartCategoryChecks.Add([pscustomobject]@{
        categoryId = $category.id
        categoryCode = $category.code
        fetched = $questions.Count
    })
}

$report.systems.smart_quiz_by_category = [ordered]@{
    categoriesChecked = $smartCategoryChecks.Count
    checks = $smartCategoryChecks
}

# ---------------------------------------------------------------------------
# Theory exam
# ---------------------------------------------------------------------------

$theoryQuestions = @((Invoke-Json -Method "GET" -Url "$BackendBaseUrl/api/quiz/theory-exam" -Headers $headers).data)
$theoryFirstAttempt = @()
foreach ($question in $theoryQuestions) {
    Test-ChoiceContract -Anomalies $report.anomalies -SystemName "theory_exam" -Options @($question.options) -Context @{
        questionId = $question.id
    }
    $theoryFirstAttempt += @{
        questionId = $question.id
        selectedOptionId = (Get-OptionId $question.options[0])
    }
}

$theoryResult = (Invoke-Json -Method "POST" -Url "$BackendBaseUrl/api/quiz/theory-exam/check" -Headers $headers -Body $theoryFirstAttempt).data
$theoryDistribution = New-Distribution
$theoryPerfectAttempt = @()

foreach ($question in $theoryQuestions) {
    $resultItem = @($theoryResult.questions | Where-Object { $_.questionId -eq $question.id })[0]
    Assert ($null -ne $resultItem) "Theory exam result item missing for question $($question.id)."

    $correctOptionId = [long]$resultItem.correctOptionId
    $position = Get-CorrectPosition -Options @($question.options) -CorrectId $correctOptionId
    Add-DistributionValue -Distribution $theoryDistribution -Position $position

    $selectedOptionId = (Get-OptionId $question.options[0])
    $expectedCorrect = ($selectedOptionId -eq $correctOptionId)
    if ([bool]$resultItem.isCorrect -ne $expectedCorrect) {
        Add-Anomaly -List $report.anomalies -SystemName "theory_exam" -Message "Theory result correctness mismatch." -Context @{
            questionId = $question.id
            selectedOptionId = $selectedOptionId
            correctOptionId = $correctOptionId
        }
    }

    $theoryPerfectAttempt += @{
        questionId = $question.id
        selectedOptionId = $correctOptionId
    }
}

$theoryPerfect = (Invoke-Json -Method "POST" -Url "$BackendBaseUrl/api/quiz/theory-exam/check" -Headers $headers -Body $theoryPerfectAttempt).data

$report.systems.theory_exam = [ordered]@{
    questionsFetched = $theoryQuestions.Count
    firstAttempt = [ordered]@{
        correctAnswers = $theoryResult.correctAnswers
        wrongAnswers = $theoryResult.wrongAnswers
        unanswered = $theoryResult.unanswered
        scorePercentage = $theoryResult.scorePercentage
        passed = $theoryResult.passed
    }
    perfectAttempt = [ordered]@{
        correctAnswers = $theoryPerfect.correctAnswers
        totalQuestions = $theoryPerfect.totalQuestions
        passed = $theoryPerfect.passed
    }
    correctPositionDistribution = $theoryDistribution
}

# ---------------------------------------------------------------------------
# Random sign practice
# ---------------------------------------------------------------------------

$randomSignQuestions = @((Invoke-Json -Method "GET" -Url "$BackendBaseUrl/api/sign-quiz/random-practice" -Headers $headers).data)
$randomSignFirstAttempt = @()
foreach ($question in $randomSignQuestions) {
    Test-ChoiceContract -Anomalies $report.anomalies -SystemName "sign_random_practice" -Options @($question.choices) -Context @{
        questionId = $question.id
    }
    Test-NoLegacySignSeriesWording -Anomalies $report.anomalies -SystemName "sign_random_practice" -Options @($question.choices) -Context @{
        questionId = $question.id
    }
    $randomSignFirstAttempt += @{
        questionId = $question.id
        selectedChoiceId = (Get-OptionId $question.choices[0])
    }
}

$randomSignResult = (Invoke-Json -Method "POST" -Url "$BackendBaseUrl/api/sign-quiz/random-practice/check" -Headers $headers -Body $randomSignFirstAttempt).data
$randomSignDistribution = New-Distribution
$randomSignPerfectAttempt = @()

foreach ($question in $randomSignQuestions) {
    $resultItem = @($randomSignResult.questions | Where-Object { $_.questionId -eq $question.id })[0]
    Assert ($null -ne $resultItem) "Random sign practice result item missing for question $($question.id)."

    $correctChoiceId = [long]$resultItem.correctChoiceId
    $position = Get-CorrectPosition -Options @($question.choices) -CorrectId $correctChoiceId
    Add-DistributionValue -Distribution $randomSignDistribution -Position $position

    $selectedChoiceId = (Get-OptionId $question.choices[0])
    $expectedCorrect = ($selectedChoiceId -eq $correctChoiceId)
    if ([bool]$resultItem.isCorrect -ne $expectedCorrect) {
        Add-Anomaly -List $report.anomalies -SystemName "sign_random_practice" -Message "Random sign practice correctness mismatch." -Context @{
            questionId = $question.id
            selectedChoiceId = $selectedChoiceId
            correctChoiceId = $correctChoiceId
        }
    }

    $randomSignPerfectAttempt += @{
        questionId = $question.id
        selectedChoiceId = $correctChoiceId
    }
}

$randomSignPerfect = (Invoke-Json -Method "POST" -Url "$BackendBaseUrl/api/sign-quiz/random-practice/check" -Headers $headers -Body $randomSignPerfectAttempt).data

$report.systems.sign_random_practice = [ordered]@{
    questionsFetched = $randomSignQuestions.Count
    firstAttempt = [ordered]@{
        correctAnswers = $randomSignResult.correctAnswers
        wrongAnswers = $randomSignResult.wrongAnswers
        unanswered = $randomSignResult.unanswered
        scorePercentage = $randomSignResult.scorePercentage
        passed = $randomSignResult.passed
    }
    perfectAttempt = [ordered]@{
        correctAnswers = $randomSignPerfect.correctAnswers
        totalQuestions = $randomSignPerfect.totalQuestions
        passed = $randomSignPerfect.passed
    }
    correctPositionDistribution = $randomSignDistribution
}

# ---------------------------------------------------------------------------
# Assessment
# ---------------------------------------------------------------------------

$assessmentCategories = @((Invoke-Json -Method "GET" -Url "$BackendBaseUrl/api/assessment/categories?lang=en" -Headers $headers).data)
$assessmentDistribution = New-Distribution
$assessmentQuestionCount = 0
$assessmentChecks = New-Object System.Collections.Generic.List[object]

foreach ($category in $assessmentCategories) {
    foreach ($difficulty in @($category.difficulties)) {
        $questions = @((Invoke-Json -Method "GET" -Url "$BackendBaseUrl/api/assessment/categories/$($category.slug)/questions?level=$difficulty&lang=en&limit=50" -Headers $headers).data)
        foreach ($question in $questions) {
            Test-ChoiceContract -Anomalies $report.anomalies -SystemName "assessment" -Options @($question.choices) -Context @{
                categorySlug = $category.slug
                difficulty = $difficulty
                questionId = $question.id
            }
            $firstChoiceId = Get-OptionId $question.choices[0]
            $check = (Invoke-Json -Method "POST" -Url "$BackendBaseUrl/api/assessment/questions/$($question.id)/check" -Headers $headers -Body @{ choiceId = $firstChoiceId }).data
            $position = Get-CorrectPosition -Options @($question.choices) -CorrectId ([long]$check.correctChoiceId)
            Add-DistributionValue -Distribution $assessmentDistribution -Position $position

            $expectedCorrect = ($firstChoiceId -eq [long]$check.correctChoiceId)
            if ([bool]$check.correct -ne $expectedCorrect) {
                Add-Anomaly -List $report.anomalies -SystemName "assessment" -Message "Assessment correctness mismatch." -Context @{
                    categorySlug = $category.slug
                    difficulty = $difficulty
                    questionId = $question.id
                    selectedChoiceId = $firstChoiceId
                    correctChoiceId = $check.correctChoiceId
                }
            }

            $assessmentQuestionCount++
        }

        $assessmentChecks.Add([pscustomobject]@{
            categorySlug = $category.slug
            difficulty = $difficulty
            fetched = $questions.Count
        })
    }
}

$report.systems.assessment = [ordered]@{
    categoriesChecked = $assessmentCategories.Count
    questionsChecked = $assessmentQuestionCount
    correctPositionDistribution = $assessmentDistribution
    checks = $assessmentChecks
}

# ---------------------------------------------------------------------------
# Exam simulation
# ---------------------------------------------------------------------------

$examStart = (Invoke-Json -Method "POST" -Url "$BackendBaseUrl/api/exams/simulations/start" -Headers $headers).data
$examId = [long]$examStart.examId
$simulationDistribution = New-Distribution
$leakedCorrectnessFields = New-Object System.Collections.Generic.List[string]

foreach ($question in @($examStart.questions)) {
    Test-ChoiceContract -Anomalies $report.anomalies -SystemName "exam_simulation" -Options @($question.options) -Context @{
        questionId = $question.questionId
    }
    $firstOptionId = Get-OptionId $question.options[0]
    $submit = (Invoke-Json `
        -Method "POST" `
        -Url "$BackendBaseUrl/api/exams/simulations/$examId/questions/$($question.questionId)/answer" `
        -Headers $headers `
        -Body @{ selectedOptionId = $firstOptionId }).data

    foreach ($fieldName in @("isCorrect", "correctOptionId", "correctOptionEn", "correctOptionTextEn", "correctChoiceId")) {
        if ($null -ne $submit.PSObject.Properties[$fieldName]) {
            $leakedCorrectnessFields.Add($fieldName)
        }
    }
}

[void](Invoke-Json -Method "POST" -Url "$BackendBaseUrl/api/exams/simulations/$examId/submit" -Headers $headers)
$examResults = (Invoke-Json -Method "GET" -Url "$BackendBaseUrl/api/exams/simulations/$examId/results" -Headers $headers).data

foreach ($question in @($examStart.questions)) {
    $answerReview = @($examResults.allAnswers | Where-Object { $_.questionId -eq $question.questionId })[0]
    Assert ($null -ne $answerReview) "Exam review item missing for question $($question.questionId)."
    $position = Get-CorrectPosition -Options @($question.options) -CorrectId ([long]$answerReview.correctOptionId)
    Add-DistributionValue -Distribution $simulationDistribution -Position $position

    $selectedOptionId = (Get-OptionId $question.options[0])
    $expectedCorrect = ($selectedOptionId -eq [long]$answerReview.correctOptionId)
    if ([bool]$answerReview.isCorrect -ne $expectedCorrect) {
        Add-Anomaly -List $report.anomalies -SystemName "exam_simulation" -Message "Exam simulation correctness mismatch." -Context @{
            questionId = $question.questionId
            selectedOptionId = $selectedOptionId
            correctOptionId = $answerReview.correctOptionId
        }
    }
}

$report.systems.exam_simulation = [ordered]@{
    examId = $examId
    questionsFetched = @($examStart.questions).Count
    submitAnswerLeaksCorrectness = (@($leakedCorrectnessFields).Count -gt 0)
    leakedFields = @($leakedCorrectnessFields | Sort-Object -Unique)
    result = [ordered]@{
        correctAnswers = $examResults.correctAnswers
        wrongAnswers = $examResults.wrongAnswers
        unansweredCount = $examResults.unansweredCount
        scorePercentage = $examResults.scorePercentage
        passed = $examResults.passed
    }
    correctPositionDistribution = $simulationDistribution
}

# ---------------------------------------------------------------------------
# Per-sign audit
# ---------------------------------------------------------------------------

if (-not $SkipFullSignAudit) {
    $signs = @((Invoke-Json -Method "GET" -Url "$BackendBaseUrl/api/sign-quiz/signs" -Headers $headers).data)
    $signPracticeDistribution = New-Distribution
    $signExamDistribution = New-Distribution
    $signPracticeQuestionsChecked = 0
    $signExamQuestionsChecked = 0

    foreach ($sign in $signs) {
        $practiceSession = (Invoke-Json -Method "POST" -Url "$BackendBaseUrl/api/sign-quiz/practice/$($sign.signCode)" -Headers $headers).data
        foreach ($question in @($practiceSession.questions)) {
            Test-ChoiceContract -Anomalies $report.anomalies -SystemName "sign.practice" -Options @($question.choices) -Context @{
                signCode = $sign.signCode
                questionId = $question.id
            }
            Test-NoLegacySignSeriesWording -Anomalies $report.anomalies -SystemName "sign.practice" -Options @($question.choices) -Context @{
                signCode = $sign.signCode
                questionId = $question.id
            }
            $firstChoiceId = Get-OptionId $question.choices[0]
            $answer = (Invoke-Json `
                -Method "POST" `
                -Url "$BackendBaseUrl/api/sign-quiz/practice/$($practiceSession.sessionId)/questions/$($question.id)/answer" `
                -Headers $headers `
                -Body @{ choiceId = $firstChoiceId; timeTakenSeconds = 5 }).data

            $correctChoiceId = [long]$answer.correctChoiceId
            $position = Get-CorrectPosition -Options @($question.choices) -CorrectId $correctChoiceId
            Add-DistributionValue -Distribution $signPracticeDistribution -Position $position

            $expectedCorrect = ($firstChoiceId -eq $correctChoiceId)
            if ([bool]$answer.isCorrect -ne $expectedCorrect) {
                Add-Anomaly -List $report.anomalies -SystemName "sign.practice" -Message "Per-sign practice correctness mismatch." -Context @{
                    signCode = $sign.signCode
                    questionId = $question.id
                    selectedChoiceId = $firstChoiceId
                    correctChoiceId = $correctChoiceId
                }
            }

            $signPracticeQuestionsChecked++
        }

        $exam = (Invoke-Json -Method "GET" -Url "$BackendBaseUrl/api/sign-quiz/exam/$($sign.signCode)/1" -Headers $headers).data
        $examAnswers = @()
        foreach ($question in @($exam.questions)) {
            Test-ChoiceContract -Anomalies $report.anomalies -SystemName "sign.exam" -Options @($question.choices) -Context @{
                signCode = $sign.signCode
                questionId = $question.id
            }
            $examAnswers += @{
                questionId = $question.id
                choiceId = (Get-OptionId $question.choices[0])
            }
        }

        $examResult = (Invoke-Json -Method "POST" -Url "$BackendBaseUrl/api/sign-quiz/exam/$($sign.signCode)/1/submit" -Headers $headers -Body @{ answers = $examAnswers }).data
        foreach ($question in @($exam.questions)) {
            Test-NoLegacySignSeriesWording -Anomalies $report.anomalies -SystemName "sign.exam" -Options @($question.choices) -Context @{
                signCode = $sign.signCode
                questionId = $question.id
            }
            $resultItem = @($examResult.questionResults | Where-Object { $_.questionId -eq $question.id })[0]
            Assert ($null -ne $resultItem) "Per-sign exam result item missing for sign $($sign.signCode), question $($question.id)."

            $correctChoiceId = [long]$resultItem.correctChoiceId
            $position = Get-CorrectPosition -Options @($question.choices) -CorrectId $correctChoiceId
            Add-DistributionValue -Distribution $signExamDistribution -Position $position

            $selectedChoiceId = (Get-OptionId $question.choices[0])
            $expectedCorrect = ($selectedChoiceId -eq $correctChoiceId)
            if ([bool]$resultItem.isCorrect -ne $expectedCorrect) {
                Add-Anomaly -List $report.anomalies -SystemName "sign.exam" -Message "Per-sign exam correctness mismatch." -Context @{
                    signCode = $sign.signCode
                    questionId = $question.id
                    selectedChoiceId = $selectedChoiceId
                    correctChoiceId = $correctChoiceId
                }
            }

            $signExamQuestionsChecked++
        }
    }

    $report.systems.sign_full_audit = [ordered]@{
        signsChecked = $signs.Count
        practiceQuestionsChecked = $signPracticeQuestionsChecked
        examQuestionsChecked = $signExamQuestionsChecked
        practiceCorrectPositionDistribution = $signPracticeDistribution
        examCorrectPositionDistribution = $signExamDistribution
    }
}

$report.summary.systemCount = $report.systems.Count
$report.summary.anomalyCount = $report.anomalies.Count
$report.summary.success = ($report.anomalies.Count -eq 0)

$report | ConvertTo-Json -Depth 20
