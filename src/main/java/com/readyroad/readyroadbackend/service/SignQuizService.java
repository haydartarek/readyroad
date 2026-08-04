package com.readyroad.readyroadbackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.*;
import com.readyroad.readyroadbackend.domain.entity.SignPracticeSession.SessionStatus;
import com.readyroad.readyroadbackend.domain.enums.SignCategory;
import com.readyroad.readyroadbackend.domain.enums.SignDifficulty;
import com.readyroad.readyroadbackend.domain.enums.SignQuestionType;
import com.readyroad.readyroadbackend.domain.repository.*;
import com.readyroad.readyroadbackend.dto.RoadSignSummaryDto;
import com.readyroad.readyroadbackend.dto.sign.*;
import com.readyroad.readyroadbackend.util.RouteCodeNormalizer;
import com.readyroad.readyroadbackend.util.SignQuestionTextSanitizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Sign Quiz Engine — business logic for the user-facing quiz endpoints.
 *
 * <p>
 * Handles two quiz modes:
 * <ul>
 * <li><b>Practice (Stateful)</b> – session persisted in DB, one answer at a
 * time,
 * immediate feedback, V11 tracking on every answer.</li>
 * <li><b>Exam (Stateless)</b> – questions fetched with GET, all answers
 * submitted
 * at once, result computed on-the-fly, V11 user_weak_areas updated in
 * bulk.</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SignQuizService {

        private final RoadSignRepository roadSignRepo;
        private final SignQuestionRepository questionRepo;
        private final SignExamRepository examRepo;
        private final SignPracticeSessionRepository sessionRepo;
        private final SignPracticeAnswerRepository answerRepo;
        private final SignRandomPracticeSessionRepository randomPracticeSessionRepo;
        private final SignRandomPracticeQuestionRepository randomPracticeQuestionRepo;
        private final UserRepository userRepo;
        private final UserWeakAreaRepository weakAreaRepo;
        private final UserErrorPatternRepository errorPatternRepo;
        private final SignExamResultRepository signExamResultRepo;
        private final CanonicalSignCatalogService canonicalSignCatalogService;
        private final RoadSignReferenceTextResolver roadSignReferenceTextResolver;
        private final NotificationService notificationService;
        private final ObjectMapper objectMapper;
        // Bridge: sign quiz answers → main dashboard category progress
        private final CategoryRepository categoryRepo;
        private final UserCategoryProgressRepository categoryProgressRepo;

        private static final int RANDOM_PRACTICE_TOTAL = 50;
        private static final int RANDOM_PRACTICE_EASY = 20;
        private static final int RANDOM_PRACTICE_MEDIUM = 20;
        private static final int RANDOM_PRACTICE_HARD = 10;
        private static final int RANDOM_PRACTICE_PASSING_SCORE = 41;
        private static final int RANDOM_PRACTICE_COOLDOWN_HOURS = 24;

        private static final Pattern DIRECT_SIGN_CODE_PATTERN = Pattern.compile("^[A-Za-z]+[0-9]+[A-Za-z0-9]*$");

        public record SignPracticeHistoryItem(
                        Long sessionId,
                        String signCode,
                        String nameNl,
                        String nameEn,
                        String nameFr,
                        String nameAr,
                        String status,
                        int totalQuestions,
                        int answeredCount,
                        int correctAnswers,
                        int wrongAnswers,
                        double scorePercentage,
                        boolean passed,
                        LocalDateTime startedAt,
                        LocalDateTime completedAt) {
        }

        public record SignPracticeHistoryResponse(
                        int totalSessions,
                        List<SignPracticeHistoryItem> sessions) {
        }

        // ── 1. Get active signs ──────────────────────────────────────────────────

        /**
         * Returns a lightweight list of all active road signs, ordered by sign code.
         * Reuses {@link RoadSignSummaryDto} from the admin import service.
         */
        @Transactional(readOnly = true)
        public List<RoadSignSummaryDto> getActiveSigns() {
                return roadSignRepo.findAllByIsActiveTrueOrderBySignCodeAsc()
                                .stream()
                                .filter(canonicalSignCatalogService::isPubliclyAllowed)
                                .map(RoadSignSummaryDto::from)
                                .toList();
        }

        // ── 2. Start practice session ────────────────────────────────────────────

        /**
         * Creates a new stateful practice session for the given sign.
         *
         * <p>
         * If the user already has an IN_PROGRESS session for this sign, the
         * existing session is returned (idempotent — avoids duplicate sessions).
         * </p>
         *
         * @param userId   authenticated user ID
         * @param signCode road sign code, e.g. "A1", "B19", "C3"
         * @return session DTO with shuffled questions (choices without isCorrect)
         */
        @Transactional
        public SignPracticeSessionDto startPracticeSession(Long userId, String signCode) {

                RoadSign sign = findActiveSignOrThrow(signCode);

                // Idempotent: return existing IN_PROGRESS session if present
                Optional<SignPracticeSession> existing = sessionRepo.findByUserIdAndSignIdAndStatus(userId,
                                sign.getId(), SessionStatus.IN_PROGRESS);
                if (existing.isPresent()) {
                        SignPracticeSession s = existing.get();
                        List<Long> answeredQuestionIds = answerRepo.findQuestionIdsBySessionId(s.getId());
                        List<SignQuizQuestionDto> questions = buildRemainingQuestionDtos(sign.getId(),
                                        answeredQuestionIds);
                        log.info("Returning existing IN_PROGRESS session {} for user {} / sign {} ({} answered, {} remaining)",
                                        s.getId(), userId, signCode, answeredQuestionIds.size(), questions.size());
                        return SignPracticeSessionDto.from(s, questions);
                }

                // Load questions (active only)
                List<SignQuestion> allQuestions = questionRepo.findAllBySignIdAndIsActiveTrue(sign.getId());

                if (allQuestions.isEmpty()) {
                        throw new ResponseStatusException(
                                        HttpStatus.valueOf(422), "No active questions for sign: " + signCode);
                }

                // Shuffle for variety
                List<SignQuestion> shuffled = new ArrayList<>(allQuestions);
                Collections.shuffle(shuffled);

                // Build user reference (lazy — just need the id)
                User user = userRepo.findById(userId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                                                "User not found"));

                // Create and persist session
                SignPracticeSession session = new SignPracticeSession();
                session.setUser(user);
                session.setSign(sign);
                session.setSignCode(sign.getSignCode());
                session.setTotalQuestions(shuffled.size());
                sessionRepo.save(session);

                // Map questions to DTOs (no isCorrect)
                List<SignQuizQuestionDto> questionDtos = shuffled.stream()
                                .map(question -> SignQuizQuestionDto.from(question, roadSignReferenceTextResolver))
                                .toList();

                log.info("Started practice session {} for user {} / sign {} ({} questions)",
                                session.getId(), userId, signCode, shuffled.size());
                return SignPracticeSessionDto.from(session, questionDtos);
        }

        // ── 3. Submit practice answer ────────────────────────────────────────────

        /**
         * Records one answer within a practice session and returns immediate feedback.
         *
         * <p>
         * V11 tracking:
         * <ul>
         * <li>Always updates {@code user_weak_areas} for the sign (upsert).</li>
         * <li>On wrong answer: inserts a row into {@code user_error_patterns}.</li>
         * </ul>
         *
         * @param sessionId  the session to answer within
         * @param questionId the question being answered
         * @param choiceId   the choice selected by the user
         * @param timeSecs   optional: time taken in seconds
         * @param userId     authenticated user ID (ownership check)
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
                                        HttpStatus.CONFLICT,
                                        "Question " + questionId + " already answered in this session");
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
                SignChoice selected = question.getDeliverableChoices().stream()
                                .filter(c -> c.getId().equals(choiceId))
                                .findFirst()
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.BAD_REQUEST,
                                                "Choice " + choiceId + " does not belong to question " + questionId));

                // ── Find the correct choice for feedback ─────────────────────────────
                SignChoice correct = question.getDeliverableChoices().stream()
                                .filter(c -> Boolean.TRUE.equals(c.getIsCorrect()))
                                .findFirst()
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.INTERNAL_SERVER_ERROR,
                                                "Question " + questionId
                                                                + " has no correct choice — data integrity error"));

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
                int wrong_ = isCorrect ? 0 : 1;
                try {
                        weakAreaRepo.upsertBySignCode(userId, session.getSignCode(), 1, correct_, wrong_);
                } catch (Exception e) {
                        log.warn("V11 upsertBySignCode failed for user={} sign={}: {}", userId, session.getSignCode(),
                                        e.getMessage());
                }

                // ── V11: insert error pattern when wrong ──────────────────────────────
                if (!isCorrect) {
                        String errorType = resolveErrorType(session.getSign().getCategory());
                        try {
                                errorPatternRepo.insertSignError(userId, errorType, questionId, session.getSignCode());
                        } catch (Exception e) {
                                log.warn("V11 insertSignError failed for user={} question={}: {}", userId, questionId,
                                                e.getMessage());
                        }
                }

                // ── Dashboard bridge: update user_category_progress ───────────────────
                // This makes sign-quiz practice answers visible in the main dashboard
                // and analytics pages (weak areas, category progress).
                try {
                        String catCode = signCategoryToCode(session.getSign().getCategory());
                        categoryRepo.findByCode(catCode)
                                        .ifPresent(cat -> updateSignCategoryProgress(userId, cat, isCorrect));
                } catch (Exception e) {
                        log.warn("Dashboard bridge failed for user={} sign={}: {}",
                                        userId, session.getSignCode(), e.getMessage());
                }

                // ── Compute accuracy for response (best-effort) ───────────────────────
                double accuracy = answeredCount == 0 ? 0.0
                                : (session.getCorrectCount() * 100.0 / answeredCount);
                int totalAttempts = (int) answeredCount;
                SignQuestionType questionType = question.getQuestionType();

                // ── Build and return response ─────────────────────────────────────────
                return new SignPracticeAnswerResponse(
                                questionId,
                                isCorrect,

                                selected.getId(),
                                sanitizeAndResolveChoice(questionType, TextLanguage.NL, selected.getTextNl()),
                                sanitizeAndResolveChoice(questionType, TextLanguage.EN, selected.getTextEn()),
                                sanitizeAndResolveChoice(questionType, TextLanguage.FR, selected.getTextFr()),
                                sanitizeAndResolveChoice(questionType, TextLanguage.AR, selected.getTextAr()),

                                correct.getId(),
                                sanitizeAndResolveChoice(questionType, TextLanguage.NL, correct.getTextNl()),
                                sanitizeAndResolveChoice(questionType, TextLanguage.EN, correct.getTextEn()),
                                sanitizeAndResolveChoice(questionType, TextLanguage.FR, correct.getTextFr()),
                                sanitizeAndResolveChoice(questionType, TextLanguage.AR, correct.getTextAr()),

                                sanitizeAndResolveExplanation(questionType, TextLanguage.NL,
                                                question.getExplanationNl()),
                                sanitizeAndResolveExplanation(questionType, TextLanguage.EN,
                                                question.getExplanationEn()),
                                sanitizeAndResolveExplanation(questionType, TextLanguage.FR,
                                                question.getExplanationFr()),
                                sanitizeAndResolveExplanation(questionType, TextLanguage.AR,
                                                question.getExplanationAr()),

                                (int) answeredCount,
                                session.getTotalQuestions(),
                                sessionCompleted,

                                Math.round(accuracy * 100.0) / 100.0,
                                totalAttempts);
        }

        // ── 4. Get practice results ──────────────────────────────────────────────

        /**
         * Returns the full result summary for a practice session.
         * Works for both IN_PROGRESS (partial) and COMPLETED sessions.
         *
         * @param sessionId the session to retrieve
         * @param userId    authenticated user ID (ownership check)
         */
        @Transactional(readOnly = true)
        public SignPracticeResultDto getPracticeResults(Long sessionId, Long userId) {

                SignPracticeSession session = sessionRepo.findByIdAndUserId(sessionId, userId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Session not found or does not belong to user"));

                // Load answers with question + choice eagerly (JOIN FETCH in repo)
                List<SignPracticeAnswer> answers = answerRepo.findAllBySessionIdWithDetails(sessionId);

                return SignPracticeResultDto.from(session, answers, roadSignReferenceTextResolver);
        }

        @Transactional(readOnly = true)
        public SignPracticeHistoryResponse getPracticeHistory(Long userId) {
                List<SignPracticeHistoryItem> sessions = sessionRepo.findAllByUserIdOrderByStartedAtDesc(userId)
                                .stream()
                                .map(this::toPracticeHistoryItem)
                                .toList();

                return new SignPracticeHistoryResponse(sessions.size(), sessions);
        }

        // ── 5. Get exam questions (stateless) ────────────────────────────────────

        /**
         * Returns the ordered questions for a sign exam (no DB session created).
         *
         * @param signCode   road sign code
         * @param examNumber exam number (currently only 1 is supported)
         */
        @Transactional(readOnly = true)
        public SignExamQuestionsDto getExamQuestions(String signCode, int examNumber) {

                RoadSign sign = findActiveSignOrThrow(signCode);

                SignExam exam = examRepo.findBySignIdAndExamNumberAndIsActiveTrue(sign.getId(), examNumber)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Exam " + examNumber + " not found for sign: " + signCode));

                List<SignQuizQuestionDto> questions = new ArrayList<>(
                                exam.getExamQuestions().stream()
                                                .map(eq -> SignQuizQuestionDto.from(eq.getQuestion(),
                                                                roadSignReferenceTextResolver))
                                                .toList());
                Collections.shuffle(questions);

                return SignExamQuestionsDto.from(exam, questions);
        }

        // ── 6. Submit exam (stateless) ───────────────────────────────────────────

        /**
         * Evaluates all submitted exam answers in one request.
         *
         * <p>
         * Passing rule: {@code correctAnswers >= exam.passingScore}
         * </p>
         * <p>
         * V11: updates {@code user_weak_areas} for the sign in bulk.
         * </p>
         *
         * @param signCode   road sign code
         * @param examNumber exam number
         * @param answers    list of (questionId, choiceId) pairs; may be empty
         * @param userId     authenticated user ID (for V11 tracking)
         */
        @Transactional
        public SignExamResultDto submitExam(String signCode, int examNumber,
                        List<SignExamAnswerItem> answers, Long userId) {

                RoadSign sign = findActiveSignOrThrow(signCode);

                SignExam exam = examRepo.findBySignIdAndExamNumberAndIsActiveTrue(sign.getId(), examNumber)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Exam " + examNumber + " not found for sign: " + signCode));

                List<SignExamQuestion> examQuestions = exam.getExamQuestions();
                int linkedCount = examQuestions.size();

                if (answers.isEmpty()) {
                        log.info("Exam {}/{} submitted by user {} with no answers; all questions will be marked unanswered",
                                        signCode, examNumber, userId);
                }

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
                        SignQuestion q = eq.getQuestion();
                        Long qId = q.getId();
                        Long submitted = submittedMap.get(qId);
                        SignQuestionType questionType = q.getQuestionType();

                        // Find correct choice
                        SignChoice correctChoice = q.getDeliverableChoices().stream()
                                        .filter(c -> Boolean.TRUE.equals(c.getIsCorrect()))
                                        .findFirst()
                                        .orElse(null);

                        if (submitted == null) {
                                // Not answered
                                results.add(new SignExamResultDto.ExamQuestionResult(
                                                qId,
                                                q.getQuestionRef(),
                                                q.getDifficulty().name(),
                                                resolveText(TextLanguage.NL, q.getQuestionNl()),
                                                resolveText(TextLanguage.EN, q.getQuestionEn()),
                                                resolveText(TextLanguage.FR, q.getQuestionFr()),
                                                resolveText(TextLanguage.AR, q.getQuestionAr()),
                                                false, // answered
                                                null, // isCorrect
                                                null, // selectedChoiceId
                                                correctChoice != null ? correctChoice.getId() : null,
                                                correctChoice != null ? sanitizeAndResolveChoice(questionType,
                                                                TextLanguage.NL, correctChoice.getTextNl()) : null,
                                                correctChoice != null ? sanitizeAndResolveChoice(questionType,
                                                                TextLanguage.EN, correctChoice.getTextEn()) : null,
                                                correctChoice != null ? sanitizeAndResolveChoice(questionType,
                                                                TextLanguage.FR, correctChoice.getTextFr()) : null,
                                                correctChoice != null ? sanitizeAndResolveChoice(questionType,
                                                                TextLanguage.AR, correctChoice.getTextAr()) : null,
                                                sanitizeAndResolveExplanation(questionType, TextLanguage.NL,
                                                                q.getExplanationNl()),
                                                sanitizeAndResolveExplanation(questionType, TextLanguage.EN,
                                                                q.getExplanationEn()),
                                                sanitizeAndResolveExplanation(questionType, TextLanguage.FR,
                                                                q.getExplanationFr()),
                                                sanitizeAndResolveExplanation(questionType, TextLanguage.AR,
                                                                q.getExplanationAr())));
                                continue;
                        }

                        answeredCount++;

                        // Validate submitted choice belongs to this question
                        SignChoice selectedChoice = q.getDeliverableChoices().stream()
                                        .filter(c -> c.getId().equals(submitted))
                                        .findFirst()
                                        .orElse(null);

                        boolean isCorrect = selectedChoice != null
                                        && Boolean.TRUE.equals(selectedChoice.getIsCorrect());
                        if (isCorrect)
                                correctCount++;
                        answeredResults.add(isCorrect); // track for dashboard bridge

                        results.add(new SignExamResultDto.ExamQuestionResult(
                                        qId,
                                        q.getQuestionRef(),
                                        q.getDifficulty().name(),
                                        resolveText(TextLanguage.NL, q.getQuestionNl()),
                                        resolveText(TextLanguage.EN, q.getQuestionEn()),
                                        resolveText(TextLanguage.FR, q.getQuestionFr()),
                                        resolveText(TextLanguage.AR, q.getQuestionAr()),
                                        true, // answered
                                        isCorrect,
                                        submitted,
                                        correctChoice != null ? correctChoice.getId() : null,
                                        correctChoice != null
                                                        ? sanitizeAndResolveChoice(questionType, TextLanguage.NL,
                                                                        correctChoice.getTextNl())
                                                        : null,
                                        correctChoice != null
                                                        ? sanitizeAndResolveChoice(questionType, TextLanguage.EN,
                                                                        correctChoice.getTextEn())
                                                        : null,
                                        correctChoice != null
                                                        ? sanitizeAndResolveChoice(questionType, TextLanguage.FR,
                                                                        correctChoice.getTextFr())
                                                        : null,
                                        correctChoice != null
                                                        ? sanitizeAndResolveChoice(questionType, TextLanguage.AR,
                                                                        correctChoice.getTextAr())
                                                        : null,
                                        sanitizeAndResolveExplanation(questionType, TextLanguage.NL,
                                                        q.getExplanationNl()),
                                        sanitizeAndResolveExplanation(questionType, TextLanguage.EN,
                                                        q.getExplanationEn()),
                                        sanitizeAndResolveExplanation(questionType, TextLanguage.FR,
                                                        q.getExplanationFr()),
                                        sanitizeAndResolveExplanation(questionType, TextLanguage.AR,
                                                        q.getExplanationAr())));
                }

                // ── Passing threshold ─────────────────────────────────────────────────
                int requiredToPass = exam.getPassingScore() != null
                                ? exam.getPassingScore()
                                : (int) Math.ceil(linkedCount * 0.8);
                boolean passed = correctCount >= requiredToPass;
                double scorePct = linkedCount == 0 ? 0.0
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

                SignExamResult persistedResult = null;

                // ── Save exam result ──────────────────────────────────────────────────
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
                        result.setQuestionResultsJson(objectMapper.writeValueAsString(results));
                        persistedResult = signExamResultRepo.save(result);
                        log.info("Saved SignExamResult id={} user={} sign={} passed={}",
                                        persistedResult.getId(), userId, sign.getSignCode(), passed);
                } catch (Exception e) {
                        // Non-fatal — log and continue so the user still gets their result
                        log.error("Failed to save SignExamResult for user={} sign={} exam={}: {}",
                                        userId, sign.getSignCode(), examNumber, e.getMessage(), e);
                }

                if (persistedResult != null) {
                        try {
                                if (passed) {
                                        notificationService.createSignExamPassedNotification(
                                                        userId,
                                                        persistedResult.getId(),
                                                        correctCount,
                                                        linkedCount);
                                } else {
                                        notificationService.createSignExamFailedNotification(
                                                        userId,
                                                        persistedResult.getId(),
                                                        correctCount,
                                                        linkedCount,
                                                        Math.max(0, requiredToPass - correctCount));
                                }
                        } catch (Exception e) {
                                log.warn("Failed to create sign-exam notification for result {}: {}",
                                                persistedResult.getId(), e.getMessage());
                        }
                }

                log.info("Exam {}/{} submitted by user {} — {}/{} correct, passed={}",
                                signCode, examNumber, userId, correctCount, linkedCount, passed);

                return buildStoredSignExamResultDto(
                                persistedResult,
                                sign,
                                examNumber,
                                linkedCount,
                                answeredCount,
                                correctCount,
                                requiredToPass,
                                scorePct,
                                passed,
                                results);
        }

        // ── 6b. Sign exam history/results ───────────────────────────────────────

        @Transactional(readOnly = true)
        public SignExamHistoryResponseDto getSignExamHistory(Long userId) {
                List<SignExamResult> results = signExamResultRepo.findByUserIdOrderByCompletedAtDesc(userId);
                Map<Long, RoadSign> signsById = roadSignRepo.findAllById(results.stream()
                                .map(SignExamResult::getSignId)
                                .filter(Objects::nonNull)
                                .distinct()
                                .toList())
                                .stream()
                                .collect(Collectors.toMap(RoadSign::getId, sign -> sign));

                List<SignExamHistoryItemDto> items = results.stream()
                                .map(result -> {
                                        RoadSign sign = signsById.get(result.getSignId());
                                        return new SignExamHistoryItemDto(
                                                        result.getId(),
                                                        result.getSignCode(),
                                                        sign != null ? canonicalSignCatalogService.routeCodeFor(sign)
                                                                        : result.getSignCode(),
                                                        sign != null ? sign.getImagePath() : null,
                                                        sign != null ? sign.getNameNl() : null,
                                                        sign != null ? sign.getNameEn() : null,
                                                        sign != null ? sign.getNameFr() : null,
                                                        sign != null ? sign.getNameAr() : null,
                                                        result.getExamNumber() != null ? result.getExamNumber() : 1,
                                                        result.getTotalQuestions() != null ? result.getTotalQuestions()
                                                                        : 0,
                                                        result.getAnsweredCount() != null ? result.getAnsweredCount()
                                                                        : 0,
                                                        result.getCorrectCount() != null ? result.getCorrectCount() : 0,
                                                        Math.max(0,
                                                                        (result.getAnsweredCount() != null
                                                                                        ? result.getAnsweredCount()
                                                                                        : 0)
                                                                                        - (result.getCorrectCount() != null
                                                                                                        ? result.getCorrectCount()
                                                                                                        : 0)),
                                                        Math.max(0,
                                                                        (result.getTotalQuestions() != null
                                                                                        ? result.getTotalQuestions()
                                                                                        : 0)
                                                                                        - (result.getAnsweredCount() != null
                                                                                                        ? result.getAnsweredCount()
                                                                                                        : 0)),
                                                        result.getScorePct() != null ? result.getScorePct() : 0.0,
                                                        result.getRequiredToPass() != null ? result.getRequiredToPass()
                                                                        : 0,
                                                        Boolean.TRUE.equals(result.getPassed()),
                                                        result.getCompletedAt());
                                })
                                .toList();

                return new SignExamHistoryResponseDto(items.size(), items);
        }

        @Transactional(readOnly = true)
        public SignExamResultDto getStoredSignExamResult(Long resultId, Long userId) {
                SignExamResult storedResult = signExamResultRepo.findByIdAndUserId(resultId, userId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Sign exam result not found"));

                RoadSign sign = roadSignRepo.findById(storedResult.getSignId()).orElse(null);
                return buildStoredSignExamResultDto(
                                storedResult,
                                sign,
                                storedResult.getExamNumber() != null ? storedResult.getExamNumber() : 1,
                                storedResult.getTotalQuestions() != null ? storedResult.getTotalQuestions() : 0,
                                storedResult.getAnsweredCount() != null ? storedResult.getAnsweredCount() : 0,
                                storedResult.getCorrectCount() != null ? storedResult.getCorrectCount() : 0,
                                storedResult.getRequiredToPass() != null ? storedResult.getRequiredToPass() : 0,
                                storedResult.getScorePct() != null ? storedResult.getScorePct() : 0.0,
                                Boolean.TRUE.equals(storedResult.getPassed()),
                                readStoredQuestionResults(storedResult));
        }

        // ── 7. User progress for a single sign ──────────────────────────────────

        /**
         * Returns the progress snapshot for a single sign (practice + exam).
         * Used by the sign hub page.
         */
        @Transactional(readOnly = true)
        public SignUserProgressDto getUserSignProgress(String signCode, Long userId) {
                RoadSign sign = findActiveSignOrThrow(signCode);
                return buildProgressDto(sign, userId);
        }

        /**
         * Returns the progress snapshot for EVERY active sign (practice + exam).
         * Used by the signs list page to show progress badges without N+1 round-trips.
         */
        @Transactional(readOnly = true)
        public List<SignUserProgressDto> getAllUserProgress(Long userId) {
                Map<Long, PracticeProgressSummary> practiceBySignId = sessionRepo
                                .findProgressSummariesByUserId(userId)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> numberAsLong(row[0]),
                                                row -> new PracticeProgressSummary(
                                                                numberAsLong(row[1]) > 0,
                                                                numberAsLong(row[2]) > 0,
                                                                nullableNumberAsDouble(row[3]))));
                Map<String, ExamProgressSummary> examBySignCode = signExamResultRepo
                                .findProgressSummariesByUserId(userId)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (String) row[0],
                                                row -> new ExamProgressSummary(
                                                                numberAsLong(row[1]) > 0,
                                                                numberAsLong(row[2]) > 0,
                                                                nullableNumberAsDouble(row[3]),
                                                                ((Number) row[1]).intValue())));
                Map<Long, ExamProgressConfig> examConfigBySignId = examRepo
                                .findActiveExamOneProgressConfigs()
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> numberAsLong(row[0]),
                                                row -> new ExamProgressConfig(
                                                                ((Number) row[1]).intValue(),
                                                                ((Number) row[2]).intValue())));

                return roadSignRepo.findAllByIsActiveTrueOrderBySignCodeAsc()
                                .stream()
                                .filter(canonicalSignCatalogService::isPubliclyAllowed)
                                .map(sign -> buildProgressDto(
                                                sign,
                                                practiceBySignId.get(sign.getId()),
                                                examBySignCode.get(sign.getSignCode()),
                                                examConfigBySignId.get(sign.getId())))
                                .toList();
        }

        private SignUserProgressDto buildProgressDto(
                        RoadSign sign,
                        PracticeProgressSummary practice,
                        ExamProgressSummary exam,
                        ExamProgressConfig examConfig) {
                String code = sign.getSignCode();
                String routeCode = canonicalSignCatalogService.routeCodeFor(sign);

                return new SignUserProgressDto(
                                sign.getId(),
                                code,
                                routeCode,
                                sign.getCategory(),
                                sign.getImagePath(),
                                sign.getNameNl(),
                                sign.getNameEn(),
                                sign.getNameFr(),
                                sign.getNameAr(),
                                practice != null && practice.started(),
                                practice != null && practice.completed(),
                                practice != null ? practice.bestScorePct() : null,
                                exam != null && exam.attempted(),
                                exam != null && exam.passed(),
                                exam != null ? exam.bestScorePct() : null,
                                exam != null ? exam.attempts() : 0,
                                examConfig != null ? examConfig.totalQuestions() : null,
                                examConfig != null ? examConfig.passingScore() : null);
        }

        private SignUserProgressDto buildProgressDto(RoadSign sign, Long userId) {
                String code = sign.getSignCode();
                String routeCode = canonicalSignCatalogService.routeCodeFor(sign);
                Long signId = sign.getId();

                // Practice
                boolean practiceStarted = sessionRepo.existsByUserIdAndSignId(userId, signId);
                boolean practiceCompleted = sessionRepo.existsByUserIdAndSignIdAndStatus(
                                userId, signId, SignPracticeSession.SessionStatus.COMPLETED);
                Double practiceBestScore = practiceCompleted
                                ? sessionRepo.findBestScorePctByUserIdAndSignId(userId, signId)
                                : null;

                // Exam
                boolean examAttempted = signExamResultRepo.existsByUserIdAndSignCode(userId, code);
                boolean examPassed = signExamResultRepo.existsByUserIdAndSignCodeAndPassedTrue(userId, code);
                Double examBest = examAttempted
                                ? signExamResultRepo.findBestScorePctByUserIdAndSignCode(userId, code)
                                : null;
                int examAttempts = (int) signExamResultRepo.countByUserIdAndSignCode(userId, code);
                Optional<SignExam> exam1Config = examRepo.findBySignIdAndExamNumberAndIsActiveTrue(signId, 1);

                return new SignUserProgressDto(
                                signId,
                                code,
                                routeCode,
                                sign.getCategory(),
                                sign.getImagePath(),
                                sign.getNameNl(),
                                sign.getNameEn(),
                                sign.getNameFr(),
                                sign.getNameAr(),
                                practiceStarted,
                                practiceCompleted,
                                practiceBestScore,
                                examAttempted,
                                examPassed,
                                examBest,
                                examAttempts,
                                exam1Config.map(SignExam::getTotalQuestions).orElse(null),
                                exam1Config.map(SignExam::getPassingScore).orElse(null));
        }

        private static long numberAsLong(Object value) {
                return ((Number) value).longValue();
        }

        private static Double nullableNumberAsDouble(Object value) {
                return value == null ? null : ((Number) value).doubleValue();
        }

        private record PracticeProgressSummary(
                        boolean started,
                        boolean completed,
                        Double bestScorePct) {
        }

        private record ExamProgressSummary(
                        boolean attempted,
                        boolean passed,
                        Double bestScorePct,
                        int attempts) {
        }

        private record ExamProgressConfig(
                        int totalQuestions,
                        int passingScore) {
        }

        private SignExamResultDto buildStoredSignExamResultDto(
                        SignExamResult storedResult,
                        RoadSign sign,
                        int examNumber,
                        int totalLinked,
                        int answeredCount,
                        int correctCount,
                        int requiredToPass,
                        double scorePct,
                        boolean passed,
                        List<SignExamResultDto.ExamQuestionResult> questionResults) {
                int unansweredCount = Math.max(0, totalLinked - answeredCount);
                int wrongCount = Math.max(0, answeredCount - correctCount);
                String signCode = sign != null ? sign.getSignCode()
                                : storedResult != null ? storedResult.getSignCode() : null;

                return new SignExamResultDto(
                                storedResult != null ? storedResult.getId() : null,
                                signCode,
                                sign != null ? canonicalSignCatalogService.routeCodeFor(sign) : signCode,
                                sign != null ? sign.getImagePath() : null,
                                sign != null ? sign.getNameNl() : null,
                                sign != null ? sign.getNameEn() : null,
                                sign != null ? sign.getNameFr() : null,
                                sign != null ? sign.getNameAr() : null,
                                examNumber,
                                storedResult != null ? storedResult.getCompletedAt() : LocalDateTime.now(),
                                totalLinked,
                                answeredCount,
                                unansweredCount,
                                correctCount,
                                wrongCount,
                                scorePct,
                                requiredToPass,
                                passed,
                                passed ? "PASSED" : "FAILED",
                                questionResults);
        }

        private List<SignExamResultDto.ExamQuestionResult> readStoredQuestionResults(SignExamResult storedResult) {
                if (storedResult == null || storedResult.getQuestionResultsJson() == null
                                || storedResult.getQuestionResultsJson().isBlank()) {
                        return List.of();
                }

                try {
                        return objectMapper.readValue(
                                        storedResult.getQuestionResultsJson(),
                                        new TypeReference<List<SignExamResultDto.ExamQuestionResult>>() {
                                        });
                } catch (Exception e) {
                        log.warn("Failed to parse stored sign exam question results for result {}: {}",
                                        storedResult.getId(), e.getMessage());
                        return List.of();
                }
        }

        // ── Private helpers ──────────────────────────────────────────────────────

        /**
         * Maps a {@link SignCategory} to the appropriate {@code error_type} string
         * used in the {@code user_error_patterns} table.
         */
        private static String resolveErrorType(SignCategory category) {
                if (category == null)
                        return "SIGN_CONFUSION";
                return switch (category) {
                        case PRIORITY -> "PRIORITY_MISUNDERSTANDING";
                        case ADDITIONAL -> "SUPPLEMENTARY_IGNORED";
                        case ZONE -> "ZONE_CONFUSION";
                        default -> "SIGN_CONFUSION";
                };
        }

        /**
         * Reloads question DTOs for an existing session (used when returning an
         * idempotent session response).
         */
        private List<SignQuizQuestionDto> buildRemainingQuestionDtos(Long signId,
                        Collection<Long> answeredQuestionIds) {
                Set<Long> answeredIds = answeredQuestionIds == null
                                ? Collections.emptySet()
                                : new HashSet<>(answeredQuestionIds);
                List<SignQuestion> qs = questionRepo.findAllBySignIdAndIsActiveTrue(signId);
                return qs.stream()
                                .filter(question -> !answeredIds.contains(question.getId()))
                                .map(question -> SignQuizQuestionDto.from(question, roadSignReferenceTextResolver))
                                .toList();
        }

        private RoadSign findActiveSignOrThrow(String identifier) {
                return findSignByRouteOrCode(identifier)
                                .filter(sign -> Boolean.TRUE.equals(sign.getIsActive()))
                                .filter(canonicalSignCatalogService::isPubliclyAllowed)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Sign not found: " + identifier));
        }

        private Optional<RoadSign> findSignByRouteOrCode(String identifier) {
                String raw = RouteCodeNormalizer.resolveLegacyAlias(identifier);
                if (raw.isBlank()) {
                        return Optional.empty();
                }

                Optional<RoadSign> byExactCode = roadSignRepo.findFirstActiveBySignCodeCaseSensitive(raw);
                if (byExactCode.isPresent()) {
                        return byExactCode;
                }

                if (looksLikeDirectSignCode(raw)) {
                        return Optional.empty();
                }

                String routeKey = normalizeRouteKey(raw);
                if (routeKey.isBlank()) {
                        return Optional.empty();
                }

                return roadSignRepo.findByNormalizedSignCode(routeKey);
        }

        private static boolean looksLikeDirectSignCode(String value) {
                return DIRECT_SIGN_CODE_PATTERN.matcher(value).matches();
        }

        private static String normalizeRouteKey(String value) {
                return RouteCodeNormalizer.normalize(value);
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
         * CYCLIST     → M  (Fiets/Bromfiets onderborden — shares category M with DELINEATION in dashboard)
         * DELINEATION → M  (Afbakeningsborden/TYPE-* — category M in dashboard)
         * ZONE        → Z  (Zone Signs)
         * </pre>
         */
        private static String signCategoryToCode(SignCategory cat) {
                if (cat == null)
                        return "A";
                return switch (cat) {
                        case DANGER -> "A";
                        case PRIORITY -> "B";
                        case PROHIBITION -> "C";
                        case MANDATORY -> "D";
                        case PARKING -> "E";
                        case INFORMATION -> "F";
                        case ADDITIONAL -> "G";
                        case CYCLIST -> "M";
                        case DELINEATION -> "M";
                        case ZONE -> "Z";
                        case ROAD_MANAGEMENT -> "F";
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
                                userId, category.getCode(), progress.getQuestionsAttempted(),
                                progress.getAccuracyRate());
        }

        // ── Random Sign Practice (persistent 50-question mixed sign exam) ──────

        @Transactional
        public SignRandomPracticeSessionDto startRandomSignPracticeSession(Long userId) {
                expireRandomPracticeSessionIfNeeded(userId);

                Optional<SignRandomPracticeSession> active = randomPracticeSessionRepo
                                .findFirstByUser_IdAndStatusOrderByStartedAtDesc(
                                                userId, SignRandomPracticeSession.SessionStatus.IN_PROGRESS);
                if (active.isPresent() && !active.get().isExpired()) {
                        SignRandomPracticeSession session = active.get();
                        return SignRandomPracticeSessionDto.from(
                                        session,
                                        loadRandomPracticeQuestionDtos(session.getId()));
                }

                User user = userRepo.findById(userId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.UNAUTHORIZED, "User not found"));

                LocalDateTime now = LocalDateTime.now();
                LocalDateTime cooldownCutoff = now.minusHours(RANDOM_PRACTICE_COOLDOWN_HOURS);
                Set<Long> excludedQuestionIds = new HashSet<>(
                                randomPracticeQuestionRepo.findDistinctRecentQuestionIdsByUserIdSince(
                                                userId, cooldownCutoff));

                List<SignQuestion> easyPool = filterEligibleRandomPracticeQuestions(
                                questionRepo.findAllActiveForActiveSignsByDifficulty(SignDifficulty.EASY),
                                excludedQuestionIds);
                List<SignQuestion> mediumPool = filterEligibleRandomPracticeQuestions(
                                questionRepo.findAllActiveForActiveSignsByDifficulty(SignDifficulty.MEDIUM),
                                excludedQuestionIds);
                List<SignQuestion> hardPool = filterEligibleRandomPracticeQuestions(
                                questionRepo.findAllActiveForActiveSignsByDifficulty(SignDifficulty.HARD),
                                excludedQuestionIds);

                if (easyPool.size() < RANDOM_PRACTICE_EASY ||
                                mediumPool.size() < RANDOM_PRACTICE_MEDIUM ||
                                hardPool.size() < RANDOM_PRACTICE_HARD) {
                        throw new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "Not enough fresh sign questions are available right now. Try again later.");
                }

                List<SignQuestion> selected = new ArrayList<>(RANDOM_PRACTICE_TOTAL);
                selected.addAll(pickBalancedRandomPracticeQuestionsBySign(easyPool, RANDOM_PRACTICE_EASY));
                selected.addAll(pickBalancedRandomPracticeQuestionsBySign(mediumPool, RANDOM_PRACTICE_MEDIUM));
                selected.addAll(pickBalancedRandomPracticeQuestionsBySign(hardPool, RANDOM_PRACTICE_HARD));
                Collections.shuffle(selected);

                SignRandomPracticeSession session = new SignRandomPracticeSession();
                session.setUser(user);
                session.setTotalQuestions(RANDOM_PRACTICE_TOTAL);
                session.setPassingScore(RANDOM_PRACTICE_PASSING_SCORE);
                session.setStatus(SignRandomPracticeSession.SessionStatus.IN_PROGRESS);
                session.setStartedAt(now);
                session.setExpiresAt(now.plusHours(RANDOM_PRACTICE_COOLDOWN_HOURS));
                session = randomPracticeSessionRepo.save(session);

                int order = 1;
                for (SignQuestion question : selected) {
                        SignRandomPracticeQuestion row = new SignRandomPracticeQuestion();
                        row.setSession(session);
                        row.setQuestion(question);
                        row.setQuestionOrder(order++);
                        randomPracticeQuestionRepo.save(row);
                }

                return SignRandomPracticeSessionDto.from(session, loadRandomPracticeQuestionDtos(session.getId()));
        }

        @Transactional
        public SignRandomPracticeResultDto submitRandomSignPracticeAnswers(
                        Long sessionId,
                        List<SignRandomPracticeAnswerRequest> answers,
                        Long userId) {
                SignRandomPracticeSession session = randomPracticeSessionRepo.findByIdAndUserId(sessionId, userId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Random practice session not found"));

                if (session.getStatus() == SignRandomPracticeSession.SessionStatus.COMPLETED) {
                        return getRandomSignPracticeResult(sessionId, userId);
                }

                if (session.isExpired()) {
                        session.setStatus(SignRandomPracticeSession.SessionStatus.EXPIRED);
                        randomPracticeSessionRepo.save(session);
                        throw new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "This mixed sign exam expired. Start a new one to continue.");
                }

                List<SignRandomPracticeQuestion> rows = randomPracticeQuestionRepo.findBySessionIdOrderByQuestionOrder(
                                sessionId);
                Map<Long, Long> submittedMap = new HashMap<>();
                for (SignRandomPracticeAnswerRequest answer : answers) {
                        submittedMap.putIfAbsent(answer.questionId(), answer.selectedChoiceId());
                }

                int correctCount = 0;
                int answeredCount = 0;
                LocalDateTime answeredAt = LocalDateTime.now();
                Map<String, int[]> signStats = new HashMap<>();
                Map<String, Category> categoryCache = new HashMap<>();

                for (SignRandomPracticeQuestion row : rows) {
                        SignQuestion question = row.getQuestion();
                        question.getDeliverableChoices().stream()
                                        .filter(c -> Boolean.TRUE.equals(c.getIsCorrect()))
                                        .findFirst()
                                        .orElseThrow(() -> new ResponseStatusException(
                                                        HttpStatus.UNPROCESSABLE_CONTENT,
                                                        "Question has no correct choice: " + question.getId()));

                        Long submittedChoiceId = submittedMap.get(question.getId());
                        boolean wasTimeout = submittedChoiceId == null;
                        SignChoice selectedChoice = null;
                        if (!wasTimeout) {
                                selectedChoice = question.getDeliverableChoices().stream()
                                                .filter(choice -> choice.getId().equals(submittedChoiceId))
                                                .findFirst()
                                                .orElseThrow(() -> new ResponseStatusException(
                                                                HttpStatus.BAD_REQUEST,
                                                                "Choice " + submittedChoiceId
                                                                                + " does not belong to question "
                                                                                + question.getId()));
                                answeredCount++;
                        }

                        boolean isCorrect = !wasTimeout && selectedChoice != null
                                        && Boolean.TRUE.equals(selectedChoice.getIsCorrect());
                        if (isCorrect) {
                                correctCount++;
                        }

                        row.setSelectedChoice(selectedChoice);
                        row.setIsCorrect(isCorrect);
                        row.setWasTimeout(wasTimeout);
                        row.setAnsweredAt(answeredAt);

                        String signCode = question.getSign().getSignCode();
                        int[] signCounters = signStats.computeIfAbsent(signCode, ignored -> new int[] { 0, 0, 0 });
                        signCounters[0] += 1;
                        if (isCorrect) {
                                signCounters[1] += 1;
                        } else {
                                signCounters[2] += 1;
                        }

                        try {
                                String catCode = signCategoryToCode(question.getSign().getCategory());
                                Category category = categoryCache.computeIfAbsent(catCode,
                                                code -> categoryRepo.findByCode(code).orElse(null));
                                if (category != null) {
                                        updateSignCategoryProgress(userId, category, isCorrect);
                                }
                        } catch (Exception e) {
                                log.warn("Dashboard bridge (random sign exam) failed for user={} question={}: {}",
                                                userId, question.getId(), e.getMessage());
                        }

                        if (!wasTimeout && !isCorrect) {
                                try {
                                        errorPatternRepo.insertSignError(
                                                        userId,
                                                        resolveErrorType(question.getSign().getCategory()),
                                                        question.getId(),
                                                        signCode);
                                } catch (Exception e) {
                                        log.warn("Random sign exam error pattern insert failed for user={} question={}: {}",
                                                        userId, question.getId(), e.getMessage());
                                }
                        }
                }

                randomPracticeQuestionRepo.saveAll(rows);

                signStats.forEach((signCode, counters) -> {
                        try {
                                weakAreaRepo.upsertBySignCode(userId, signCode, counters[0], counters[1], counters[2]);
                        } catch (Exception e) {
                                log.warn("Random sign exam weak-area upsert failed for user={} sign={}: {}",
                                                userId, signCode, e.getMessage());
                        }
                });

                int totalQuestions = rows.size();
                int unanswered = totalQuestions - answeredCount;
                int wrongAnswers = answeredCount - correctCount;
                double scorePct = totalQuestions == 0 ? 0.0
                                : Math.round(correctCount * 10000.0 / totalQuestions) / 100.0;
                boolean passed = correctCount >= session.getPassingScore();

                session.setAnsweredCount(answeredCount);
                session.setCorrectCount(correctCount);
                session.setScorePct(scorePct);
                session.setPassed(passed);
                session.setCompletedAt(LocalDateTime.now());
                session.setStatus(SignRandomPracticeSession.SessionStatus.COMPLETED);
                randomPracticeSessionRepo.save(session);

                try {
                        if (passed) {
                                notificationService.createRandomSignExamPassedNotification(
                                                userId, session.getId(), correctCount, totalQuestions);
                        } else {
                                int pointsShort = session.getPassingScore() - correctCount;
                                notificationService.createRandomSignExamFailedNotification(
                                                userId, session.getId(), correctCount, totalQuestions,
                                                pointsShort);
                        }
                } catch (Exception e) {
                        log.warn("Random sign exam notification failed for session {}: {}", sessionId,
                                        e.getMessage());
                }

                return new SignRandomPracticeResultDto(
                                session.getId(),
                                session.getStatus().name(),
                                session.getStartedAt(),
                                session.getCompletedAt(),
                                totalQuestions,
                                answeredCount,
                                correctCount,
                                wrongAnswers,
                                unanswered,
                                scorePct,
                                passed,
                                session.getPassingScore(),
                                rows.stream().map(this::toRandomPracticeQuestionResult).toList());
        }

        @Transactional(readOnly = true)
        public SignRandomPracticeHistoryResponseDto getRandomSignPracticeHistory(Long userId) {
                List<SignRandomPracticeHistoryItemDto> sessions = randomPracticeSessionRepo
                                .findAllByUserIdOrderByStartedAtDesc(userId)
                                .stream()
                                .map(this::toRandomPracticeHistoryItem)
                                .toList();
                return new SignRandomPracticeHistoryResponseDto(sessions.size(), sessions);
        }

        @Transactional(readOnly = true)
        public SignRandomPracticeResultDto getRandomSignPracticeResult(Long sessionId, Long userId) {
                SignRandomPracticeSession session = randomPracticeSessionRepo.findByIdAndUserId(sessionId, userId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Random practice session not found"));
                List<SignRandomPracticeQuestion> rows = randomPracticeQuestionRepo.findBySessionIdOrderByQuestionOrder(
                                sessionId);
                int totalQuestions = rows.size();
                int answeredCount = session.getAnsweredCount() != null ? session.getAnsweredCount()
                                : (int) rows.stream().filter(row -> !Boolean.TRUE.equals(row.getWasTimeout())).count();
                int correctAnswers = session.getCorrectCount() != null ? session.getCorrectCount()
                                : (int) rows.stream().filter(row -> Boolean.TRUE.equals(row.getIsCorrect())).count();
                int unanswered = totalQuestions - answeredCount;
                int wrongAnswers = answeredCount - correctAnswers;
                double scorePct = session.getScorePct() != null ? session.getScorePct()
                                : (totalQuestions == 0 ? 0.0
                                                : Math.round(correctAnswers * 10000.0 / totalQuestions) / 100.0);
                boolean passed = Boolean.TRUE.equals(session.getPassed());

                return new SignRandomPracticeResultDto(
                                session.getId(),
                                session.getStatus().name(),
                                session.getStartedAt(),
                                session.getCompletedAt(),
                                totalQuestions,
                                answeredCount,
                                correctAnswers,
                                wrongAnswers,
                                unanswered,
                                scorePct,
                                passed,
                                session.getPassingScore(),
                                rows.stream().map(this::toRandomPracticeQuestionResult).toList());
        }

        private void expireRandomPracticeSessionIfNeeded(Long userId) {
                randomPracticeSessionRepo.findFirstByUser_IdAndStatusOrderByStartedAtDesc(
                                userId, SignRandomPracticeSession.SessionStatus.IN_PROGRESS)
                                .ifPresent(session -> {
                                        if (session.isExpired()) {
                                                session.setStatus(SignRandomPracticeSession.SessionStatus.EXPIRED);
                                                randomPracticeSessionRepo.save(session);
                                        }
                                });
        }

        private SignPracticeHistoryItem toPracticeHistoryItem(SignPracticeSession session) {
                int answeredCount = (int) answerRepo.countBySessionId(session.getId());
                int totalQuestions = session.getTotalQuestions();
                int correctAnswers = session.getCorrectCount();
                int wrongAnswers = Math.max(0, answeredCount - correctAnswers);
                double scorePct = totalQuestions == 0 ? 0.0
                                : Math.round(correctAnswers * 10000.0 / totalQuestions) / 100.0;
                boolean passed = session.getStatus() == SessionStatus.COMPLETED && scorePct >= 80.0;

                return new SignPracticeHistoryItem(
                                session.getId(),
                                session.getSignCode(),
                                session.getSign().getNameNl(),
                                session.getSign().getNameEn(),
                                session.getSign().getNameFr(),
                                session.getSign().getNameAr(),
                                session.getStatus().name(),
                                totalQuestions,
                                answeredCount,
                                correctAnswers,
                                wrongAnswers,
                                scorePct,
                                passed,
                                session.getStartedAt(),
                                session.getCompletedAt());
        }

        private List<SignQuestion> filterEligibleRandomPracticeQuestions(
                        List<SignQuestion> questions,
                        Set<Long> excludedQuestionIds) {
                return new ArrayList<>(questions.stream()
                                .filter(question -> question.getSign() != null
                                                && canonicalSignCatalogService.isPubliclyAllowed(question.getSign()))
                                .filter(question -> !excludedQuestionIds.contains(question.getId()))
                                .filter(this::hasValidRandomPracticeChoices)
                                .toList());
        }

        /**
         * Picks questions with better sign diversity inside one difficulty bucket.
         *
         * <p>
         * Strategy: group by sign, then pick at most one question per sign per round
         * until the target is reached. This prevents one sign with many questions from
         * dominating the whole bucket while still keeping randomness.
         * </p>
         */
        private List<SignQuestion> pickBalancedRandomPracticeQuestionsBySign(
                        List<SignQuestion> pool,
                        int targetCount) {
                Map<Long, List<SignQuestion>> bySignId = new HashMap<>();
                for (SignQuestion question : pool) {
                        Long signId = question.getSign().getId();
                        bySignId.computeIfAbsent(signId, ignored -> new ArrayList<>()).add(question);
                }

                List<List<SignQuestion>> buckets = new ArrayList<>(bySignId.values());
                for (List<SignQuestion> bucket : buckets) {
                        Collections.shuffle(bucket);
                }

                List<SignQuestion> picked = new ArrayList<>(targetCount);
                while (picked.size() < targetCount) {
                        boolean pickedInRound = false;
                        Collections.shuffle(buckets);

                        for (List<SignQuestion> bucket : buckets) {
                                if (picked.size() >= targetCount) {
                                        break;
                                }
                                if (bucket.isEmpty()) {
                                        continue;
                                }
                                picked.add(bucket.remove(bucket.size() - 1));
                                pickedInRound = true;
                        }

                        if (!pickedInRound) {
                                break;
                        }
                }

                if (picked.size() < targetCount) {
                        throw new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "Not enough fresh sign questions are available right now. Try again later.");
                }

                return picked;
        }

        private boolean hasValidRandomPracticeChoices(SignQuestion question) {
                long correctChoices = question.getDeliverableChoices().stream()
                                .filter(choice -> Boolean.TRUE.equals(choice.getIsCorrect()))
                                .count();
                int totalChoices = question.getDeliverableChoices().size();
                return correctChoices == 1
                                && totalChoices >= 2
                                && totalChoices <= 3
                                && (question.getDifficulty() != SignDifficulty.HARD || totalChoices == 2);
        }

        private List<SignQuizQuestionDto> loadRandomPracticeQuestionDtos(Long sessionId) {
                return randomPracticeQuestionRepo.findBySessionIdOrderByQuestionOrder(sessionId)
                                .stream()
                                .map(SignRandomPracticeQuestion::getQuestion)
                                .map(question -> SignQuizQuestionDto.from(question, roadSignReferenceTextResolver))
                                .toList();
        }

        private SignRandomPracticeHistoryItemDto toRandomPracticeHistoryItem(SignRandomPracticeSession session) {
                int totalQuestions = session.getTotalQuestions() != null ? session.getTotalQuestions() : 0;
                int answeredCount = session.getAnsweredCount() != null ? session.getAnsweredCount() : 0;
                int correctAnswers = session.getCorrectCount() != null ? session.getCorrectCount() : 0;
                int unanswered = Math.max(0, totalQuestions - answeredCount);
                int wrongAnswers = Math.max(0, answeredCount - correctAnswers);
                double scorePct = session.getScorePct() != null ? session.getScorePct() : 0.0;
                return new SignRandomPracticeHistoryItemDto(
                                session.getId(),
                                session.getStatus().name(),
                                totalQuestions,
                                answeredCount,
                                correctAnswers,
                                wrongAnswers,
                                unanswered,
                                scorePct,
                                Boolean.TRUE.equals(session.getPassed()),
                                session.getPassingScore(),
                                session.getStartedAt(),
                                session.getCompletedAt());
        }

        private SignRandomPracticeResultDto.QuestionResult toRandomPracticeQuestionResult(
                        SignRandomPracticeQuestion row) {
                SignQuestion question = row.getQuestion();
                SignQuestionType questionType = question.getQuestionType();
                SignChoice selectedChoice = row.getSelectedChoice();
                SignChoice correctChoice = question.getDeliverableChoices().stream()
                                .filter(choice -> Boolean.TRUE.equals(choice.getIsCorrect()))
                                .findFirst()
                                .orElse(null);

                return new SignRandomPracticeResultDto.QuestionResult(
                                question.getId(),
                                resolveText(TextLanguage.NL, question.getQuestionNl()),
                                resolveText(TextLanguage.EN, question.getQuestionEn()),
                                resolveText(TextLanguage.FR, question.getQuestionFr()),
                                resolveText(TextLanguage.AR, question.getQuestionAr()),
                                selectedChoice != null ? selectedChoice.getId() : null,
                                selectedChoice != null ? sanitizeAndResolveChoice(questionType, TextLanguage.NL,
                                                selectedChoice.getTextNl()) : null,
                                selectedChoice != null ? sanitizeAndResolveChoice(questionType, TextLanguage.EN,
                                                selectedChoice.getTextEn()) : null,
                                selectedChoice != null ? sanitizeAndResolveChoice(questionType, TextLanguage.FR,
                                                selectedChoice.getTextFr()) : null,
                                selectedChoice != null ? sanitizeAndResolveChoice(questionType, TextLanguage.AR,
                                                selectedChoice.getTextAr()) : null,
                                correctChoice != null ? correctChoice.getId() : null,
                                correctChoice != null ? sanitizeAndResolveChoice(questionType, TextLanguage.NL,
                                                correctChoice.getTextNl()) : null,
                                correctChoice != null ? sanitizeAndResolveChoice(questionType, TextLanguage.EN,
                                                correctChoice.getTextEn()) : null,
                                correctChoice != null ? sanitizeAndResolveChoice(questionType, TextLanguage.FR,
                                                correctChoice.getTextFr()) : null,
                                correctChoice != null ? sanitizeAndResolveChoice(questionType, TextLanguage.AR,
                                                correctChoice.getTextAr()) : null,
                                Boolean.TRUE.equals(row.getIsCorrect()),
                                Boolean.TRUE.equals(row.getWasTimeout()),
                                sanitizeAndResolveExplanation(questionType, TextLanguage.NL,
                                                question.getExplanationNl()),
                                sanitizeAndResolveExplanation(questionType, TextLanguage.EN,
                                                question.getExplanationEn()),
                                sanitizeAndResolveExplanation(questionType, TextLanguage.FR,
                                                question.getExplanationFr()),
                                sanitizeAndResolveExplanation(questionType, TextLanguage.AR,
                                                question.getExplanationAr()),
                                question.getSign() != null ? question.getSign().getSignCode() : null,
                                question.getSign() != null ? question.getSign().getImagePath() : null,
                                question.getDifficulty() != null ? question.getDifficulty().name() : null);
        }

        private String resolveText(TextLanguage language, String value) {
                return switch (language) {
                        case NL -> roadSignReferenceTextResolver.resolveNl(value);
                        case EN -> roadSignReferenceTextResolver.resolveEn(value);
                        case FR -> roadSignReferenceTextResolver.resolveFr(value);
                        case AR -> roadSignReferenceTextResolver.resolveAr(value);
                };
        }

        private String sanitizeAndResolveChoice(SignQuestionType questionType, TextLanguage language, String value) {
                return resolveText(language,
                                SignQuestionTextSanitizer.sanitizeChoice(questionType, language.name(), value));
        }

        private String sanitizeAndResolveExplanation(SignQuestionType questionType, TextLanguage language,
                        String value) {
                return resolveText(language,
                                SignQuestionTextSanitizer.sanitizeExplanation(questionType, language.name(), value));
        }

        private enum TextLanguage {
                NL,
                EN,
                FR,
                AR
        }
}
