package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.*;
import com.readyroad.readyroadbackend.domain.entity.SignPracticeSession.SessionStatus;
import com.readyroad.readyroadbackend.domain.enums.SignCategory;
import com.readyroad.readyroadbackend.domain.enums.SignDifficulty;
import com.readyroad.readyroadbackend.domain.repository.*;
import com.readyroad.readyroadbackend.dto.RoadSignSummaryDto;
import com.readyroad.readyroadbackend.dto.sign.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Sign Quiz Engine — business logic for the user-facing quiz endpoints.
 *
 * <p>Handles two quiz modes:
 * <ul>
 *   <li><b>Practice (Stateful)</b> – session persisted in DB, one answer at a time,
 *       immediate feedback, V11 tracking on every answer.</li>
 *   <li><b>Exam (Stateless)</b> – questions fetched with GET, all answers submitted
 *       at once, result computed on-the-fly, V11 user_weak_areas updated in bulk.</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SignQuizService {

    private final RoadSignRepository              roadSignRepo;
    private final SignQuestionRepository          questionRepo;
    private final SignExamRepository              examRepo;
    private final SignPracticeSessionRepository   sessionRepo;
    private final SignPracticeAnswerRepository     answerRepo;
    private final UserRepository                  userRepo;
    private final UserWeakAreaRepository          weakAreaRepo;
    private final UserErrorPatternRepository      errorPatternRepo;
    private final SignExamResultRepository        signExamResultRepo;
    // Bridge: sign quiz answers → main dashboard category progress
    private final CategoryRepository              categoryRepo;
    private final UserCategoryProgressRepository  categoryProgressRepo;

    // ── 1. Get active signs ──────────────────────────────────────────────────

    /**
     * Returns a lightweight list of all active road signs, ordered by sign code.
     * Reuses {@link RoadSignSummaryDto} from the admin import service.
     */
    @Transactional(readOnly = true)
    public List<RoadSignSummaryDto> getActiveSigns() {
        return roadSignRepo.findAllByIsActiveTrueOrderBySignCodeAsc()
                .stream()
                .map(RoadSignSummaryDto::from)
                .toList();
    }

    // ── 2. Start practice session ────────────────────────────────────────────

    /**
     * Creates a new stateful practice session for the given sign.
     *
     * <p>If the user already has an IN_PROGRESS session for this sign, the
     * existing session is returned (idempotent — avoids duplicate sessions).</p>
     *
     * @param userId   authenticated user ID
     * @param signCode road sign code, e.g. "A1", "B19", "C3"
     * @return session DTO with shuffled questions (choices without isCorrect)
     */
    @Transactional
    public SignPracticeSessionDto startPracticeSession(Long userId, String signCode) {

        RoadSign sign = roadSignRepo.findBySignCode(signCode.toUpperCase())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Sign not found: " + signCode));

        if (!Boolean.TRUE.equals(sign.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sign not active: " + signCode);
        }

        // Idempotent: return existing IN_PROGRESS session if present
        Optional<SignPracticeSession> existing =
                sessionRepo.findByUserIdAndSignIdAndStatus(userId, sign.getId(), SessionStatus.IN_PROGRESS);
        if (existing.isPresent()) {
            SignPracticeSession s = existing.get();
            List<SignQuizQuestionDto> questions = buildQuestionDtos(s.getTotalQuestions(), sign.getId());
            log.info("Returning existing IN_PROGRESS session {} for user {} / sign {}",
                    s.getId(), userId, signCode);
            return SignPracticeSessionDto.from(s, questions);
        }

        // Load questions (active only)
        List<SignQuestion> allQuestions =
                questionRepo.findAllBySignIdAndIsActiveTrue(sign.getId());

        if (allQuestions.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY, "No active questions for sign: " + signCode);
        }

        // Shuffle for variety
        List<SignQuestion> shuffled = new ArrayList<>(allQuestions);
        Collections.shuffle(shuffled);

        // Build user reference (lazy — just need the id)
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        // Create and persist session
        SignPracticeSession session = new SignPracticeSession();
        session.setUser(user);
        session.setSign(sign);
        session.setSignCode(sign.getSignCode());
        session.setTotalQuestions(shuffled.size());
        sessionRepo.save(session);

        // Map questions to DTOs (no isCorrect)
        List<SignQuizQuestionDto> questionDtos = shuffled.stream()
                .map(SignQuizQuestionDto::from)
                .toList();

        log.info("Started practice session {} for user {} / sign {} ({} questions)",
                session.getId(), userId, signCode, shuffled.size());
        return SignPracticeSessionDto.from(session, questionDtos);
    }

    // ── 3. Submit practice answer ────────────────────────────────────────────

    /**
     * Records one answer within a practice session and returns immediate feedback.
     *
     * <p>V11 tracking:
     * <ul>
     *   <li>Always updates {@code user_weak_areas} for the sign (upsert).</li>
     *   <li>On wrong answer: inserts a row into {@code user_error_patterns}.</li>
     * </ul>
     *
     * @param sessionId     the session to answer within
     * @param questionId    the question being answered
     * @param choiceId      the choice selected by the user
     * @param timeSecs      optional: time taken in seconds
     * @param userId        authenticated user ID (ownership check)
     */
    @Transactional
    public SignPracticeAnswerResponse submitPracticeAnswer(
            Long sessionId, Long questionId, Long choiceId,
            Integer timeSecs, Long userId) {

        // ── Load & validate session ──────────────────────────────────────────
        SignPracticeSession session = sessionRepo.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Session not found or does not belong to user"));

        if (session.getStatus() != SessionStatus.IN_PROGRESS) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Session " + sessionId + " is already COMPLETED");
        }

        // ── Duplicate-answer guard ───────────────────────────────────────────
        if (answerRepo.existsBySessionIdAndQuestionId(sessionId, questionId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Question " + questionId + " already answered in this session");
        }

        // ── Load question (must belong to the same sign) ─────────────────────
        SignQuestion question = questionRepo.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Question not found: " + questionId));

        if (!question.getSign().getId().equals(session.getSign().getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Question does not belong to this sign's session");
        }

        // ── Load & validate choice (must belong to the question) ─────────────
        SignChoice selected = question.getChoices().stream()
                .filter(c -> c.getId().equals(choiceId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Choice " + choiceId + " does not belong to question " + questionId));

        // ── Find the correct choice for feedback ─────────────────────────────
        SignChoice correct = question.getChoices().stream()
                .filter(c -> Boolean.TRUE.equals(c.getIsCorrect()))
                .findFirst()
                .orElse(selected); // defensive fallback

        boolean isCorrect = Boolean.TRUE.equals(selected.getIsCorrect());

        // ── Persist answer ────────────────────────────────────────────────────
        SignPracticeAnswer answer = new SignPracticeAnswer();
        answer.setSession(session);
        answer.setQuestion(question);
        answer.setChoice(selected);
        answer.setIsCorrect(isCorrect);
        answer.setTimeTakenSeconds(timeSecs);
        answerRepo.save(answer);

        // ── Update session counters ───────────────────────────────────────────
        if (isCorrect) {
            session.setCorrectCount(session.getCorrectCount() + 1);
        }

        long answeredCount = answerRepo.countBySessionId(sessionId);
        boolean sessionCompleted = answeredCount >= session.getTotalQuestions();

        if (sessionCompleted) {
            session.setStatus(SessionStatus.COMPLETED);
            session.setCompletedAt(LocalDateTime.now());
            log.info("Session {} COMPLETED — score {}/{}", sessionId,
                    session.getCorrectCount(), session.getTotalQuestions());
        }
        sessionRepo.save(session);

        // ── V11: update user_weak_areas ───────────────────────────────────────
        int correct_ = isCorrect ? 1 : 0;
        int wrong_   = isCorrect ? 0 : 1;
        try {
            weakAreaRepo.upsertBySignCode(userId, session.getSignCode(), 1, correct_, wrong_);
        } catch (Exception e) {
            log.warn("V11 upsertBySignCode failed for user={} sign={}: {}", userId, session.getSignCode(), e.getMessage());
        }

        // ── V11: insert error pattern when wrong ──────────────────────────────
        if (!isCorrect) {
            String errorType = resolveErrorType(session.getSign().getCategory());
            try {
                errorPatternRepo.insertSignError(userId, errorType, questionId, session.getSignCode());
            } catch (Exception e) {
                log.warn("V11 insertSignError failed for user={} question={}: {}", userId, questionId, e.getMessage());
            }
        }

        // ── Dashboard bridge: update user_category_progress ───────────────────
        // This makes sign-quiz practice answers visible in the main dashboard
        // and analytics pages (weak areas, category progress).
        try {
            String catCode = signCategoryToCode(session.getSign().getCategory());
            categoryRepo.findByCode(catCode).ifPresent(cat ->
                    updateSignCategoryProgress(userId, cat, isCorrect));
        } catch (Exception e) {
            log.warn("Dashboard bridge failed for user={} sign={}: {}",
                    userId, session.getSignCode(), e.getMessage());
        }

        // ── Compute accuracy for response (best-effort) ───────────────────────
        double accuracy   = answeredCount == 0 ? 0.0
                : (session.getCorrectCount() * 100.0 / answeredCount);
        int    totalAttempts = (int) answeredCount;

        // ── Build and return response ─────────────────────────────────────────
        return new SignPracticeAnswerResponse(
                questionId,
                isCorrect,

                selected.getId(),
                selected.getTextNl(), selected.getTextEn(),
                selected.getTextFr(), selected.getTextAr(),

                correct.getId(),
                correct.getTextNl(), correct.getTextEn(),
                correct.getTextFr(), correct.getTextAr(),

                question.getExplanationNl(), question.getExplanationEn(),
                question.getExplanationFr(), question.getExplanationAr(),

                (int) answeredCount,
                session.getTotalQuestions(),
                sessionCompleted,

                Math.round(accuracy * 100.0) / 100.0,
                totalAttempts
        );
    }

    // ── 4. Get practice results ──────────────────────────────────────────────

    /**
     * Returns the full result summary for a practice session.
     * Works for both IN_PROGRESS (partial) and COMPLETED sessions.
     *
     * @param sessionId  the session to retrieve
     * @param userId     authenticated user ID (ownership check)
     */
    @Transactional(readOnly = true)
    public SignPracticeResultDto getPracticeResults(Long sessionId, Long userId) {

        SignPracticeSession session = sessionRepo.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Session not found or does not belong to user"));

        // Load answers with question + choice eagerly (JOIN FETCH in repo)
        List<SignPracticeAnswer> answers =
                answerRepo.findAllBySessionIdWithDetails(sessionId);

        return SignPracticeResultDto.from(session, answers);
    }

    // ── 5. Get exam questions (stateless) ────────────────────────────────────

    /**
     * Returns the ordered questions for a sign exam (no DB session created).
     *
     * <p><b>Exam-2 lock rule:</b> If the requested {@code examNumber} is 2 the user
     * must have at least one passed Exam-1 result for this sign; otherwise a
     * {@code 423 LOCKED} response is thrown so the frontend can show a meaningful
     * "Complete Exam 1 first" message.</p>
     *
     * @param signCode   road sign code
     * @param examNumber 1 or 2
     * @param userId     authenticated user ID (used for Exam-2 lock check only)
     */
    @Transactional(readOnly = true)
    public SignExamQuestionsDto getExamQuestions(String signCode, int examNumber, Long userId) {

        RoadSign sign = roadSignRepo.findBySignCode(signCode.toUpperCase())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Sign not found: " + signCode));

        // ── Exam-2 lock ───────────────────────────────────────────────────────
        if (examNumber == 2) {
            boolean passedExam1 = signExamResultRepo
                    .existsByUserIdAndSignCodeAndExamNumberAndPassedTrue(
                            userId, sign.getSignCode(), 1);
            if (!passedExam1) {
                throw new ResponseStatusException(
                        HttpStatus.LOCKED,
                        "Exam 2 is locked. Complete and pass Exam 1 for sign " + signCode + " first.");
            }
        }

        SignExam exam = examRepo.findBySignIdAndExamNumber(sign.getId(), examNumber)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Exam " + examNumber + " not found for sign: " + signCode));

        List<SignQuizQuestionDto> questions = new ArrayList<>(
                exam.getExamQuestions().stream()
                        .map(eq -> SignQuizQuestionDto.from(eq.getQuestion()))
                        .toList());
        Collections.shuffle(questions);

        return SignExamQuestionsDto.from(exam, questions);
    }

    // ── 6. Submit exam (stateless) ───────────────────────────────────────────

    /**
     * Evaluates all submitted exam answers in one request.
     *
     * <p>Passing rule: {@code correctAnswers >= ceil(linkedCount × 0.8)}</p>
     * <p>V11: updates {@code user_weak_areas} for the sign in bulk.</p>
     *
     * @param signCode   road sign code
     * @param examNumber 1 or 2
     * @param answers    list of (questionId, choiceId) pairs
     * @param userId     authenticated user ID (for V11 tracking)
     */
    @Transactional
    public SignExamResultDto submitExam(String signCode, int examNumber,
                                       List<SignExamAnswerItem> answers, Long userId) {

        RoadSign sign = roadSignRepo.findBySignCode(signCode.toUpperCase())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Sign not found: " + signCode));

        SignExam exam = examRepo.findBySignIdAndExamNumber(sign.getId(), examNumber)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Exam " + examNumber + " not found for sign: " + signCode));

        List<SignExamQuestion> examQuestions = exam.getExamQuestions();
        int linkedCount = examQuestions.size();

        // Build a map questionId → SignQuestion for O(1) lookup
        Map<Long, SignQuestion> questionMap = examQuestions.stream()
                .collect(Collectors.toMap(
                        eq -> eq.getQuestion().getId(),
                        SignExamQuestion::getQuestion));

        // Build a map questionId → submittedChoiceId from the request
        Map<Long, Long> submittedMap = answers.stream()
                .collect(Collectors.toMap(
                        SignExamAnswerItem::questionId,
                        SignExamAnswerItem::choiceId,
                        (a, b) -> a)); // keep first if duplicate questionId

        // Evaluate each exam question
        int correctCount = 0;
        int answeredCount = 0;
        List<SignExamResultDto.ExamQuestionResult> results = new ArrayList<>();
        List<Boolean> answeredResults = new ArrayList<>(); // for dashboard bridge

        for (SignExamQuestion eq : examQuestions) {
            SignQuestion q      = eq.getQuestion();
            Long         qId    = q.getId();
            Long         submitted = submittedMap.get(qId);

            // Find correct choice
            SignChoice correctChoice = q.getChoices().stream()
                    .filter(c -> Boolean.TRUE.equals(c.getIsCorrect()))
                    .findFirst()
                    .orElse(null);

            if (submitted == null) {
                // Not answered
                results.add(new SignExamResultDto.ExamQuestionResult(
                        qId,
                        q.getQuestionRef(),
                        q.getDifficulty().name(),
                        q.getQuestionNl(), q.getQuestionEn(),
                        q.getQuestionFr(), q.getQuestionAr(),
                        false,    // answered
                        null,     // isCorrect
                        null,     // selectedChoiceId
                        correctChoice != null ? correctChoice.getId() : null,
                        correctChoice != null ? correctChoice.getTextNl() : null,
                        correctChoice != null ? correctChoice.getTextEn() : null,
                        correctChoice != null ? correctChoice.getTextFr() : null,
                        correctChoice != null ? correctChoice.getTextAr() : null,
                        q.getExplanationNl(), q.getExplanationEn(),
                        q.getExplanationFr(), q.getExplanationAr()
                ));
                continue;
            }

            answeredCount++;

            // Validate submitted choice belongs to this question
            SignChoice selectedChoice = q.getChoices().stream()
                    .filter(c -> c.getId().equals(submitted))
                    .findFirst()
                    .orElse(null);

            boolean isCorrect = selectedChoice != null
                    && Boolean.TRUE.equals(selectedChoice.getIsCorrect());
            if (isCorrect) correctCount++;
            answeredResults.add(isCorrect); // track for dashboard bridge

            results.add(new SignExamResultDto.ExamQuestionResult(
                    qId,
                    q.getQuestionRef(),
                    q.getDifficulty().name(),
                    q.getQuestionNl(), q.getQuestionEn(),
                    q.getQuestionFr(), q.getQuestionAr(),
                    true,     // answered
                    isCorrect,
                    submitted,
                    correctChoice != null ? correctChoice.getId() : null,
                    correctChoice != null ? correctChoice.getTextNl() : null,
                    correctChoice != null ? correctChoice.getTextEn() : null,
                    correctChoice != null ? correctChoice.getTextFr() : null,
                    correctChoice != null ? correctChoice.getTextAr() : null,
                    q.getExplanationNl(), q.getExplanationEn(),
                    q.getExplanationFr(), q.getExplanationAr()
            ));
        }

        // ── Passing threshold ─────────────────────────────────────────────────
        int requiredToPass = (int) Math.ceil(linkedCount * 0.8);
        boolean passed     = correctCount >= requiredToPass;
        double  scorePct   = linkedCount == 0 ? 0.0
                : Math.round(correctCount * 100.0 / linkedCount * 100.0) / 100.0;

        // ── V11: update user_weak_areas in bulk ───────────────────────────────
        int wrong = answeredCount - correctCount;
        try {
            weakAreaRepo.upsertBySignCode(userId, sign.getSignCode(),
                    answeredCount, correctCount, wrong);
        } catch (Exception e) {
            log.warn("V11 exam upsertBySignCode failed for user={} sign={}: {}",
                    userId, sign.getSignCode(), e.getMessage());
        }

        // ── Dashboard bridge: update user_category_progress in bulk ──────────
        if (!answeredResults.isEmpty()) {
            try {
                String catCode = signCategoryToCode(sign.getCategory());
                categoryRepo.findByCode(catCode).ifPresent(cat -> {
                    for (boolean correct : answeredResults) {
                        updateSignCategoryProgress(userId, cat, correct);
                    }
                });
            } catch (Exception e) {
                log.warn("Dashboard bridge (exam) failed for user={} sign={}: {}",
                        userId, sign.getSignCode(), e.getMessage());
            }
        }

        // ── Save exam result (for exam-2 unlock tracking) ────────────────────
        try {
            SignExamResult result = new SignExamResult();
            result.setUserId(userId);
            result.setSignId(sign.getId());
            result.setSignCode(sign.getSignCode());
            result.setExamNumber(examNumber);
            result.setTotalQuestions(linkedCount);
            result.setAnsweredCount(answeredCount);
            result.setCorrectCount(correctCount);
            result.setRequiredToPass(requiredToPass);
            result.setScorePct(scorePct);
            result.setPassed(passed);
            result.setCompletedAt(LocalDateTime.now());
            signExamResultRepo.save(result);
            log.info("Saved SignExamResult id={} user={} sign={} exam={} passed={}",
                    result.getId(), userId, sign.getSignCode(), examNumber, passed);
        } catch (Exception e) {
            // Non-fatal — log and continue so the user still gets their result
            log.error("Failed to save SignExamResult for user={} sign={} exam={}: {}",
                    userId, sign.getSignCode(), examNumber, e.getMessage(), e);
        }

        log.info("Exam {}/{} submitted by user {} — {}/{} correct, passed={}",
                signCode, examNumber, userId, correctCount, linkedCount, passed);

        return new SignExamResultDto(
                sign.getSignCode(),
                examNumber,
                linkedCount,
                answeredCount,
                linkedCount - answeredCount,
                correctCount,
                answeredCount - correctCount,
                scorePct,
                requiredToPass,
                passed,
                passed ? "PASSED" : "FAILED",
                results
        );
    }

    // ── 7. User progress for a single sign ──────────────────────────────────

    /**
     * Returns the progress snapshot for a single sign (practice + exam 1 + exam 2).
     * Used by the sign hub page.
     */
    @Transactional(readOnly = true)
    public SignUserProgressDto getUserSignProgress(String signCode, Long userId) {
        RoadSign sign = roadSignRepo.findBySignCode(signCode.toUpperCase())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Sign not found: " + signCode));
        return buildProgressDto(sign, userId);
    }

    /**
     * Returns the progress snapshot for EVERY active sign (practice + exam 1 + exam 2).
     * Used by the signs list page to show progress badges without N+1 round-trips.
     */
    @Transactional(readOnly = true)
    public List<SignUserProgressDto> getAllUserProgress(Long userId) {
        return roadSignRepo.findAllByIsActiveTrueOrderBySignCodeAsc()
                .stream()
                .map(sign -> buildProgressDto(sign, userId))
                .toList();
    }

    private SignUserProgressDto buildProgressDto(RoadSign sign, Long userId) {
        String code   = sign.getSignCode();
        Long   signId = sign.getId();

        // Practice
        boolean practiceStarted   = sessionRepo.existsByUserIdAndSignId(userId, signId);
        boolean practiceCompleted = sessionRepo.existsByUserIdAndSignIdAndStatus(
                userId, signId, SignPracticeSession.SessionStatus.COMPLETED);
        Double  practiceBestScore = practiceCompleted
                ? sessionRepo.findBestScorePctByUserIdAndSignId(userId, signId)
                : null;

        // Exam 1
        boolean exam1Attempted = signExamResultRepo.existsByUserIdAndSignCodeAndExamNumber(userId, code, 1);
        boolean exam1Passed    = signExamResultRepo.existsByUserIdAndSignCodeAndExamNumberAndPassedTrue(userId, code, 1);
        Double  exam1Best      = exam1Attempted
                ? signExamResultRepo.findBestScorePctByUserIdAndSignCodeAndExamNumber(userId, code, 1)
                : null;
        int     exam1Attempts  = (int) signExamResultRepo.countByUserIdAndSignCodeAndExamNumber(userId, code, 1);

        // Exam 2 (unlocked only after passing exam 1)
        boolean exam2Attempted = signExamResultRepo.existsByUserIdAndSignCodeAndExamNumber(userId, code, 2);
        boolean exam2Passed    = signExamResultRepo.existsByUserIdAndSignCodeAndExamNumberAndPassedTrue(userId, code, 2);
        Double  exam2Best      = exam2Attempted
                ? signExamResultRepo.findBestScorePctByUserIdAndSignCodeAndExamNumber(userId, code, 2)
                : null;
        int     exam2Attempts  = (int) signExamResultRepo.countByUserIdAndSignCodeAndExamNumber(userId, code, 2);

        return new SignUserProgressDto(
                signId,
                code,
                sign.getCategory(),
                sign.getImagePath(),
                sign.getNameNl(),
                sign.getNameEn(),
                sign.getNameFr(),
                sign.getNameAr(),
                practiceStarted,
                practiceCompleted,
                practiceBestScore,
                exam1Attempted,
                exam1Passed,
                exam1Best,
                exam1Attempts,
                exam1Passed,   // exam2Unlocked == exam1Passed
                exam2Attempted,
                exam2Passed,
                exam2Best,
                exam2Attempts
        );
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    /**
     * Maps a {@link SignCategory} to the appropriate {@code error_type} string
     * used in the {@code user_error_patterns} table.
     */
    private static String resolveErrorType(SignCategory category) {
        if (category == null) return "SIGN_CONFUSION";
        return switch (category) {
            case PRIORITY    -> "PRIORITY_MISUNDERSTANDING";
            case ADDITIONAL  -> "SUPPLEMENTARY_IGNORED";
            case ZONE        -> "ZONE_CONFUSION";
            default          -> "SIGN_CONFUSION";
        };
    }

    /**
     * Reloads question DTOs for an existing session (used when returning an
     * idempotent session response).
     */
    private List<SignQuizQuestionDto> buildQuestionDtos(int totalQuestions, Long signId) {
        List<SignQuestion> qs = questionRepo.findAllBySignIdAndIsActiveTrue(signId);
        return qs.stream()
                .limit(totalQuestions)
                .map(SignQuizQuestionDto::from)
                .toList();
    }

    /**
     * Maps a {@link SignCategory} enum value to the single-letter category code
     * stored in the {@code categories} table, enabling the join between the
     * sign-quiz world and the main quiz/progress world.
     *
     * <pre>
     * DANGER      → A  (Warning Signs)
     * PRIORITY    → B  (Priority Signs)
     * PROHIBITION → C  (Prohibition Signs)
     * MANDATORY   → D  (Mandatory Signs)
     * PARKING     → E  (Parking and Standing Signs)
     * INFORMATION → F  (Information Signs)
     * ADDITIONAL  → G  (Supplementary Signs)
     * ZONE        → Z  (Zone Signs)
     * </pre>
     */
    private static String signCategoryToCode(SignCategory cat) {
        if (cat == null) return "A";
        return switch (cat) {
            case DANGER      -> "A";
            case PRIORITY    -> "B";
            case PROHIBITION -> "C";
            case MANDATORY   -> "D";
            case PARKING     -> "E";
            case INFORMATION -> "F";
            case ADDITIONAL  -> "G";
            case ZONE        -> "Z";
        };
    }

    /**
     * Creates or updates a {@link UserCategoryProgress} row for the given user
     * and category, mirroring the logic in {@link PracticeService} so that
     * sign-quiz answers appear in the main dashboard and analytics views.
     */
    private void updateSignCategoryProgress(Long userId, Category category, boolean isCorrect) {
        UserCategoryProgress progress = categoryProgressRepo
                .findByUserIdAndCategoryId(userId, category.getId())
                .orElseGet(() -> {
                    UserCategoryProgress np = new UserCategoryProgress();
                    np.setUserId(userId);
                    np.setCategoryId(category.getId());
                    np.setCategory(category);
                    np.setQuestionsAttempted(0);
                    np.setCorrectAnswers(0);
                    np.setMasteryLevel(UserCategoryProgress.MasteryLevel.BEGINNER);
                    return np;
                });

        progress.setQuestionsAttempted(progress.getQuestionsAttempted() + 1);
        if (isCorrect) {
            progress.setCorrectAnswers(progress.getCorrectAnswers() + 1);
        }
        progress.setLastPracticed(LocalDateTime.now());
        progress.updateAccuracy();
        categoryProgressRepo.save(progress);

        log.debug("Dashboard bridge updated: userId={}, category={}, attempted={}, accuracy={}",
                userId, category.getCode(), progress.getQuestionsAttempted(), progress.getAccuracyRate());
    }

    // ── Random Sign Practice (stateless, 50 questions across all signs) ──────

    /**
     * Returns 50 randomly selected active sign questions (20 EASY + 18 MEDIUM + 12 HARD),
     * shuffled together. Used by the free-practice endpoint — no session is created.
     */
    @Transactional(readOnly = true)
    public List<SignQuizQuestionDto> getRandomSignPracticeQuestions() {
        List<SignQuestion> easy   = new ArrayList<>(questionRepo.findAllByIsActiveTrueAndDifficulty(SignDifficulty.EASY));
        List<SignQuestion> medium = new ArrayList<>(questionRepo.findAllByIsActiveTrueAndDifficulty(SignDifficulty.MEDIUM));
        List<SignQuestion> hard   = new ArrayList<>(questionRepo.findAllByIsActiveTrueAndDifficulty(SignDifficulty.HARD));

        Collections.shuffle(easy);
        Collections.shuffle(medium);
        Collections.shuffle(hard);

        List<SignQuestion> selected = new ArrayList<>();
        selected.addAll(easy.subList(0, Math.min(20, easy.size())));
        selected.addAll(medium.subList(0, Math.min(18, medium.size())));
        selected.addAll(hard.subList(0, Math.min(12, hard.size())));
        Collections.shuffle(selected);

        return selected.stream().map(SignQuizQuestionDto::from).toList();
    }

    /**
     * Evaluates answers for a random sign practice session (stateless — no DB write).
     * Passing score: 41 / 50 (Belgian standard).
     */
    @Transactional(readOnly = true)
    public SignRandomPracticeResultDto checkRandomSignPracticeAnswers(
            List<SignRandomPracticeAnswerRequest> answers) {

        int correct = 0, wrong = 0, unanswered = 0;
        List<SignRandomPracticeResultDto.QuestionResult> qResults = new ArrayList<>();

        for (var answer : answers) {
            SignQuestion q = questionRepo.findById(answer.questionId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Question not found: " + answer.questionId()));

            SignChoice correctChoice = q.getChoices().stream()
                    .filter(c -> Boolean.TRUE.equals(c.getIsCorrect()))
                    .findFirst()
                    .orElse(null);

            boolean wasTimeout = answer.selectedChoiceId() == null;
            boolean isCorrect  = !wasTimeout && correctChoice != null
                    && correctChoice.getId().equals(answer.selectedChoiceId());

            if (wasTimeout)     unanswered++;
            else if (isCorrect) correct++;
            else                wrong++;

            qResults.add(new SignRandomPracticeResultDto.QuestionResult(
                    q.getId(),
                    q.getQuestionNl(), q.getQuestionEn(), q.getQuestionFr(), q.getQuestionAr(),
                    answer.selectedChoiceId(),
                    correctChoice != null ? correctChoice.getId()      : null,
                    correctChoice != null ? correctChoice.getTextNl()  : null,
                    correctChoice != null ? correctChoice.getTextEn()  : null,
                    correctChoice != null ? correctChoice.getTextFr()  : null,
                    correctChoice != null ? correctChoice.getTextAr()  : null,
                    isCorrect, wasTimeout,
                    q.getExplanationNl(), q.getExplanationEn(), q.getExplanationFr(), q.getExplanationAr(),
                    q.getSign() != null ? q.getSign().getSignCode()  : null,
                    q.getSign() != null ? q.getSign().getImagePath() : null,
                    q.getDifficulty() != null ? q.getDifficulty().name() : null
            ));
        }

        int total        = answers.size();
        int passingScore = 41;
        double pct       = total > 0 ? (double) correct / total * 100.0 : 0.0;
        return new SignRandomPracticeResultDto(
                total, correct, wrong, unanswered, pct, correct >= passingScore, passingScore, qResults);
    }
}
