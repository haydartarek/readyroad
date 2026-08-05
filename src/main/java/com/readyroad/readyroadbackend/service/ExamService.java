package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulation;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulationAnswer;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulationQuestion;
import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.entity.UserCategoryProgress;
import com.readyroad.readyroadbackend.domain.entity.NotificationType;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationAnswerRepository;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationRepository;
import com.readyroad.readyroadbackend.domain.repository.NotificationRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizAnswerOptionRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.UserCategoryProgressRepository;
import com.readyroad.readyroadbackend.domain.repository.UserQuestionHistoryRepository;
import com.readyroad.readyroadbackend.domain.repository.UserWeakAreaRepository;
import com.readyroad.readyroadbackend.util.PlaceholderDetector;
import com.readyroad.readyroadbackend.dto.exam.SubmitExamAnswerRequest;
import com.readyroad.readyroadbackend.dto.exam.SubmitExamAnswerResponse;
import com.readyroad.readyroadbackend.dto.exam.ExamResultsDTO;
import com.readyroad.readyroadbackend.dto.exam.ExamStartResponse;
import com.readyroad.readyroadbackend.dto.exam.CategoryBreakdownDTO;
import com.readyroad.readyroadbackend.dto.exam.IncorrectQuestionDTO;
import com.readyroad.readyroadbackend.dto.exam.AllAnsweredQuestionDTO;
import com.readyroad.readyroadbackend.exception.ActiveExamAlreadyExistsException;
import com.readyroad.readyroadbackend.exception.ExamNotActiveException;
import com.readyroad.readyroadbackend.exception.ExamNotCompletedException;
import com.readyroad.readyroadbackend.exception.ExamNotFoundException;
import com.readyroad.readyroadbackend.exception.ExamExpiredException;
import com.readyroad.readyroadbackend.exception.InvalidAnswerException;
import com.readyroad.readyroadbackend.exception.QuestionNotFoundException;
import com.readyroad.readyroadbackend.exception.UnauthorizedException;
import com.readyroad.readyroadbackend.mapper.ExamMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Exam Simulation Service - Phase 5
 *
 * Implements Belgian driving license exam simulation:
 * - 50 questions per exam
 * - 30 minutes time limit
 * - 41/50 passing score (82%)
 * - Respects 24h cooldown (Law #1)
 * - Uses adaptive difficulty (Law #2)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExamService {

    private final ExamSimulationRepository examRepository;
    private final ExamSimulationQuestionRepository examQuestionRepository;
    private final ExamSimulationAnswerRepository answerRepository; // Story A2
    private final QuizAnswerOptionRepository optionRepository; // Story A2
    private final QuizQuestionRepository questionRepository; // Story A2 - Load questions
    private final NotificationService notificationService; // Story N1: Exam result notifications
    private final NotificationRepository notificationRepository; // Dedup checks
    private final AchievementService achievementService; // Story N2: Achievement notifications
    private final UserCategoryProgressRepository progressRepository; // Dashboard progress tracking
    private final UserQuestionHistoryRepository historyRepository; // Streak & lastActivityDate tracking
    private final StreakService streakService; // Story N3: Study streak update on exam completion
    private final UserWeakAreaRepository weakAreaRepository; // Story N2: Persist weak areas on exam completion
    private final RoadSignReferenceTextResolver roadSignReferenceTextResolver;
    private final ExamMapper examMapper;
    private final BackendMessageService messages;

    private static final int EXAM_QUESTION_COUNT = 50;
    private static final int EXAM_TIME_LIMIT_MINUTES = 30;
    private static final int PASSING_SCORE = 41;

    // Belgian driving exam: fixed difficulty distribution
    private static final int EASY_QUESTION_COUNT = 20; // 40%
    private static final int MEDIUM_QUESTION_COUNT = 20; // 40%
    private static final int HARD_QUESTION_COUNT = 10; // 20%
    // Total: 50 = EASY + MEDIUM + HARD ✅

    /**
     * Start a new exam simulation.
     *
     * Story A1 Implementation:
     * 1. Check if user has active exam
     * 2. Generate 50 questions (respecting 24h cooldown + adaptive difficulty)
     * 3. Create exam simulation record
     * 4. Link questions to exam
     * 5. Set time limit (30 minutes)
     *
     * @param userId User ID
     * @return ExamSimulation entity (Controller will map to DTO)
     * @throws IllegalStateException if user already has active exam
     * @throws IllegalStateException if insufficient questions available
     */
    @Transactional
    public ExamSimulation startExamSimulation(Long userId) {
        log.info("Starting exam simulation for user: {}", userId);

        // AC1: Check for active exam — auto-expire stale IN_PROGRESS exams first
        ExamSimulation activeExam = examRepository
                .findByUserIdAndStatus(userId, ExamSimulation.ExamStatus.IN_PROGRESS)
                .orElse(null);

        if (activeExam != null) {
            if (Instant.now().isAfter(activeExam.getExpiresAt())) {
                // Exam time window has passed — expire it silently so user can start fresh
                log.info("Auto-expiring stale IN_PROGRESS exam {} for user {} (expired at {})",
                        activeExam.getId(), userId, activeExam.getExpiresAt());
                activeExam.setStatus(ExamSimulation.ExamStatus.EXPIRED);
                examRepository.save(activeExam);
            } else {
                // ✅ Use custom exception for proper 409 Conflict response
                throw new ActiveExamAlreadyExistsException(userId, activeExam.getId());
            }
        }

        // ✅ Belgian exam standard: 20 EASY + 20 MEDIUM + 10 HARD = 50 questions
        // Uses database-native random ordering for each difficulty tier
        List<QuizQuestion> easyPool = questionRepository
                .findRandomQuestionsByDifficulty(QuizQuestion.DifficultyLevel.EASY).stream()
                .filter(q -> q != null && q.getId() != null)
                .filter(q -> hasMinValidOptionsForExam(q))
                .collect(Collectors.toList());

        List<QuizQuestion> mediumPool = questionRepository
                .findRandomQuestionsByDifficulty(QuizQuestion.DifficultyLevel.MEDIUM).stream()
                .filter(q -> q != null && q.getId() != null)
                .filter(q -> hasMinValidOptionsForExam(q))
                .collect(Collectors.toList());

        List<QuizQuestion> hardPool = questionRepository
                .findRandomQuestionsByDifficulty(QuizQuestion.DifficultyLevel.HARD).stream()
                .filter(q -> q != null && q.getId() != null)
                .filter(q -> hasMinValidOptionsForExam(q))
                .collect(Collectors.toList());

        log.debug("Difficulty pool sizes — EASY: {}, MEDIUM: {}, HARD: {}",
                easyPool.size(), mediumPool.size(), hardPool.size());

        // Validate each tier has enough questions before building the exam
        if (easyPool.size() < EASY_QUESTION_COUNT) {
            log.error("Insufficient EASY questions. Required: {}, Available: {}",
                    EASY_QUESTION_COUNT, easyPool.size());
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    messages.get(
                            "exam.pool.insufficient_easy",
                            EASY_QUESTION_COUNT,
                            easyPool.size()));
        }
        if (mediumPool.size() < MEDIUM_QUESTION_COUNT) {
            log.error("Insufficient MEDIUM questions. Required: {}, Available: {}",
                    MEDIUM_QUESTION_COUNT, mediumPool.size());
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    messages.get(
                            "exam.pool.insufficient_medium",
                            MEDIUM_QUESTION_COUNT,
                            mediumPool.size()));
        }
        if (hardPool.size() < HARD_QUESTION_COUNT) {
            log.error("Insufficient HARD questions. Required: {}, Available: {}",
                    HARD_QUESTION_COUNT, hardPool.size());
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    messages.get(
                            "exam.pool.insufficient_hard",
                            HARD_QUESTION_COUNT,
                            hardPool.size()));
        }

        // Build the 50-question set with correct distribution, then shuffle order
        List<QuizQuestion> questions = new ArrayList<>(EXAM_QUESTION_COUNT);
        questions.addAll(easyPool.subList(0, EASY_QUESTION_COUNT));
        questions.addAll(mediumPool.subList(0, MEDIUM_QUESTION_COUNT));
        questions.addAll(hardPool.subList(0, HARD_QUESTION_COUNT));
        Collections.shuffle(questions); // Randomise final question order

        log.info("Selected {} questions for exam (EASY={}, MEDIUM={}, HARD={})",
                questions.size(), EASY_QUESTION_COUNT, MEDIUM_QUESTION_COUNT, HARD_QUESTION_COUNT);

        // AC6-AC7: Create exam simulation (UTC-aware timestamps)
        Instant now = Instant.now();
        Instant expiresAt = now.plus(Duration.ofMinutes(EXAM_TIME_LIMIT_MINUTES));

        ExamSimulation exam = new ExamSimulation();
        exam.setUserId(userId);
        exam.setStartedAt(now);
        exam.setExpiresAt(expiresAt);
        exam.setTotalQuestions(EXAM_QUESTION_COUNT);
        exam.setStatus(ExamSimulation.ExamStatus.IN_PROGRESS);

        exam = examRepository.save(exam);
        log.info("Created exam simulation: id={}, expiresAt={}", exam.getId(), expiresAt);

        // Link questions to exam
        List<ExamSimulationQuestion> examQuestionsList = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            QuizQuestion question = questions.get(i);

            ExamSimulationQuestion esq = new ExamSimulationQuestion();
            esq.setExam(exam);
            esq.setQuestion(question); // ✅ Set the actual question object (fixes NPE!)
            esq.setQuestionId(question.getId());
            esq.setQuestionOrder(i + 1);
            esq = examQuestionRepository.save(esq);
            examQuestionsList.add(esq);
        }

        // ✅ Force load lazy collections to prevent LazyInitializationException
        // This must be done inside @Transactional method
        examQuestionsList.forEach(esq -> {
            QuizQuestion q = esq.getQuestion();
            if (q != null && q.getOptions() != null) {
                q.getOptions().size(); // Trigger lazy loading
            }
        });

        log.info("✅ Exam simulation started successfully: examId={}, userId={}, questions={}",
                exam.getId(), userId, EXAM_QUESTION_COUNT);

        return exam; // Return entity, controller will map to DTO
    }

    @Transactional
    public ExamStartResponse startExamResponse(Long userId) {
        ExamSimulation exam = startExamSimulation(userId);
        return examMapper.toStartResponse(exam, getExamQuestions(exam.getId()));
    }

    /**
     * Get exam by ID.
     *
     * @param examId Exam ID
     * @return Exam simulation
     * @throws IllegalArgumentException if exam not found
     */
    @Transactional(readOnly = true)
    public ExamSimulation getExamById(Long examId) {
        return examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException(messages.get("exam.not_found", examId)));
    }

    /**
     * Get exam questions in order.
     * ✅ Eagerly loads options to prevent LazyInitializationException
     *
     * @param examId Exam ID
     * @return List of questions in exam order with loaded options
     */
    @Transactional(readOnly = true)
    public List<ExamSimulationQuestion> getExamQuestions(Long examId) {
        List<ExamSimulationQuestion> examQuestions = examQuestionRepository.findByExamIdOrderByQuestionOrder(examId);

        // ✅ Force load lazy collections inside transaction
        examQuestions.forEach(esq -> {
            QuizQuestion q = esq.getQuestion();
            if (q != null && q.getOptions() != null) {
                q.getOptions().size(); // Trigger lazy loading
            }
        });

        return examQuestions;
    }

    /**
     * Check if user can start exam.
     *
     * @param userId User ID
     * @return true if user can start exam
     */
    @Transactional
    public boolean canStartExam(Long userId) {
        ExamSimulation exam = examRepository.findByUserIdAndStatus(userId, ExamSimulation.ExamStatus.IN_PROGRESS)
                .orElse(null);

        if (exam == null) {
            return true;
        }

        if (Instant.now().isAfter(exam.getExpiresAt())) {
            exam.setStatus(ExamSimulation.ExamStatus.EXPIRED);
            examRepository.save(exam);
            log.info("Auto-expired stale IN_PROGRESS exam {} during canStartExam for user {}",
                    exam.getId(), userId);
            return true;
        }

        return false;
    }

    /**
     * Returns {@code true} if the question has at least 2 options whose text is
     * free of placeholder or corrupted content across all four language fields.
     * Questions failing this check are excluded from the exam pool.
     */
    private boolean hasMinValidOptionsForExam(QuizQuestion question) {
        if (question.getOptions() == null) {
            return false;
        }
        long validCount = question.getDeliverableOptions().stream()
                .filter(option -> !PlaceholderDetector.hasPlaceholder(
                        option.getOptionTextEn(), option.getOptionTextNl(),
                        option.getOptionTextFr(), option.getOptionTextAr()))
                .count();
        if (validCount < 2) {
            log.warn("⚠️ Exam pool: question {} excluded — only {} valid option(s) after placeholder check",
                    question.getId(), validCount);
        }
        return validCount >= 2;
    }

    /**
     * Get active exam for user.
     * ✅ Eagerly loads exam questions and options to prevent
     * LazyInitializationException
     *
     * @param userId User ID
     * @return Active exam or null
     */
    @Transactional
    public ExamSimulation getActiveExam(Long userId) {
        ExamSimulation exam = examRepository.findByUserIdAndStatus(userId, ExamSimulation.ExamStatus.IN_PROGRESS)
                .orElse(null);

        if (exam != null) {
            if (Instant.now().isAfter(exam.getExpiresAt())) {
                exam.setStatus(ExamSimulation.ExamStatus.EXPIRED);
                examRepository.save(exam);
                log.info("Auto-expired stale IN_PROGRESS exam {} during getActiveExam for user {}",
                        exam.getId(), userId);
                return null;
            }

            // ✅ Force load exam questions and their options inside transaction
            List<ExamSimulationQuestion> questions = examQuestionRepository
                    .findByExamIdOrderByQuestionOrder(exam.getId());
            questions.forEach(esq -> {
                QuizQuestion q = esq.getQuestion();
                if (q != null && q.getOptions() != null) {
                    q.getOptions().size(); // Trigger lazy loading
                }
            });
        }

        return exam;
    }

    @Transactional
    public ExamStartResponse getActiveExamResponse(Long userId) {
        ExamSimulation activeExam = getActiveExam(userId);
        if (activeExam == null) {
            return null;
        }

        return examMapper.toStartResponse(activeExam, getExamQuestions(activeExam.getId()));
    }

    /**
     * Get exam history for user — includes COMPLETED, EXPIRED, and ABANDONED exams.
     * IN_PROGRESS exams are excluded (they haven't produced results yet).
     *
     * @param userId User ID
     * @return All non-active exams ordered by start date (newest first)
     */
    @Transactional(readOnly = true)
    public List<ExamSimulation> getCompletedExams(Long userId) {
        log.info("Fetching exam history for user: {}", userId);
        return examRepository.findByUserIdAndStatusNotOrderByStartedAtDesc(
                userId, ExamSimulation.ExamStatus.IN_PROGRESS);
    }

    /**
     * Complete (submit) an exam simulation.
     *
     * Called when the user clicks "Submit Exam". Marks the exam as COMPLETED,
     * calculates and persists the final score on the exam entity.
     *
     * Idempotent: safe to call multiple times — subsequent calls on an already
     * COMPLETED or EXPIRED exam are silently ignored.
     *
     * @param examId Exam ID
     * @param userId Authenticated user ID (ownership check)
     */
    @Transactional
    public void completeExam(Long examId, Long userId) {
        log.info("Completing exam: examId={}, userId={}", examId, userId);

        ExamSimulation exam = examRepository.findById(examId)
                .orElseThrow(() -> new ExamNotFoundException(
                        messages.get("exam.not_found", examId)));

        if (!exam.getUserId().equals(userId)) {
            throw new UnauthorizedException(userId, examId);
        }

        // Idempotent — already in a terminal state, nothing to do
        if (exam.getStatus() == ExamSimulation.ExamStatus.COMPLETED ||
                exam.getStatus() == ExamSimulation.ExamStatus.EXPIRED ||
                exam.getStatus() == ExamSimulation.ExamStatus.ABANDONED) {
            log.info("Exam {} already in terminal state: {}", examId, exam.getStatus());
            return;
        }

        if (exam.getStatus() != ExamSimulation.ExamStatus.IN_PROGRESS) {
            throw new ExamNotActiveException(
                    messages.get("exam.complete.invalid_status", exam.getStatus()));
        }

        // Calculate final score from all submitted answers
        List<ExamSimulationAnswer> answers = answerRepository.findByExamId(examId);
        int correctCount = (int) answers.stream()
                .filter(ExamSimulationAnswer::getIsCorrect)
                .count();
        double scorePercentage = (correctCount * 100.0) / EXAM_QUESTION_COUNT;

        // Persist completion state and score on the exam entity
        Instant completedAt = Instant.now();
        exam.setStatus(ExamSimulation.ExamStatus.COMPLETED);
        exam.setCompletedAt(completedAt);
        exam.setCorrectAnswers(correctCount);
        exam.setScorePercentage(scorePercentage);
        exam.setTimeTakenSeconds(calculateElapsedSeconds(exam.getStartedAt(), completedAt));

        examRepository.save(exam);

        log.info("Exam completed: examId={}, score={}/{} ({}%)",
                examId, correctCount, EXAM_QUESTION_COUNT,
                String.format("%.1f", scorePercentage));

        // ── Story N1: Fire exam-result notification ──────────────────────────
        boolean passed = correctCount >= PASSING_SCORE;
        try {
            if (passed) {
                notificationService.createExamPassedNotification(
                        exam.getUserId(), examId, correctCount, EXAM_QUESTION_COUNT);
            } else {
                int pointsShort = PASSING_SCORE - correctCount;
                notificationService.createExamFailedNotification(
                        exam.getUserId(), examId, correctCount, EXAM_QUESTION_COUNT, pointsShort);
            }
        } catch (Exception ex) {
            // Notification failure must NEVER roll back the exam completion
            log.warn("Failed to create exam notification for examId={}: {}", examId, ex.getMessage());
        }

        // ── Story N2: Persist WEAK_AREA rows + fire notification (deduped per 24h)
        // ──────────
        try {
            Map<Long, CategoryBreakdownDTO> categoryMap = calculateCategoryBreakdown(answers);
            Instant weakAreaCutoff = Instant.now().minusSeconds(24 * 3600);
            boolean notifSent = false;
            for (CategoryBreakdownDTO cat : categoryMap.values()) {
                if (Boolean.TRUE.equals(cat.getIsWeakArea())) {
                    // ── Persist every weak category to user_weak_areas (idempotent upsert) ──
                    weakAreaRepository.upsertByCategoryName(
                            exam.getUserId(),
                            cat.getCategoryNameEn(),
                            cat.getTotalQuestions(),
                            cat.getCorrectAnswers(),
                            cat.getWrongAnswers());
                    log.debug("Weak area persisted for userId={}, category={}", exam.getUserId(),
                            cat.getCategoryNameEn());

                    // ── Send max 1 notification per exam (deduped per 24h) ──
                    if (!notifSent) {
                        boolean recentlySent = !notificationRepository
                                .findByUserIdAndTypeAndCreatedAtAfter(
                                        exam.getUserId(), NotificationType.WEAK_AREA, weakAreaCutoff)
                                .isEmpty();
                        if (!recentlySent) {
                            notificationService.createWeakAreaNotification(
                                    exam.getUserId(), cat.getCategoryNameEn());
                            log.info("WEAK_AREA notification sent for userId={}, category={}",
                                    exam.getUserId(), cat.getCategoryNameEn());
                        } else {
                            log.debug("WEAK_AREA notification skipped (sent within 24h) for userId={}",
                                    exam.getUserId());
                        }
                        notifSent = true;
                    }
                }
            }
        } catch (Exception ex) {
            log.warn("Failed to persist/notify weak-area for examId={}: {}", examId, ex.getMessage());
        }

        // ── Story N3: Fire ACHIEVEMENT notifications ──────────────────────────
        try {
            achievementService.checkAndAwardExamAchievements(exam.getUserId(), examId, passed, correctCount);
        } catch (Exception ex) {
            log.warn("Failed to check achievements for examId={}: {}", examId, ex.getMessage());
        }

        // ── Dashboard: Update user_category_progress from exam answers ────────
        // Each answered exam question now counts towards the user's category progress
        // so the dashboard reflects exam activity (not just practice sessions).
        try {
            LocalDateTime now_ldt = LocalDateTime.now();
            for (ExamSimulationAnswer answer : answers) {
                QuizQuestion q = answer.getQuestion();
                if (q == null)
                    continue;
                Category cat = q.getCategory();
                if (cat == null)
                    continue;

                UserCategoryProgress progress = progressRepository
                        .findByUserIdAndCategoryId(userId, cat.getId())
                        .orElseGet(() -> {
                            UserCategoryProgress np = new UserCategoryProgress();
                            np.setUserId(userId);
                            np.setCategoryId(cat.getId());
                            np.setCategory(cat);
                            np.setQuestionsAttempted(0);
                            np.setCorrectAnswers(0);
                            np.setMasteryLevel(UserCategoryProgress.MasteryLevel.BEGINNER);
                            return np;
                        });

                progress.setQuestionsAttempted(progress.getQuestionsAttempted() + 1);
                if (Boolean.TRUE.equals(answer.getIsCorrect())) {
                    progress.setCorrectAnswers(progress.getCorrectAnswers() + 1);
                }
                progress.setLastPracticed(now_ldt);
                progress.updateAccuracy();
                progressRepository.save(progress);
            }
            log.info("Dashboard progress updated from exam {}: {} answers processed for userId={}",
                    examId, answers.size(), userId);
        } catch (Exception ex) {
            // Progress update failure must NEVER roll back the exam completion
            log.warn("Failed to update dashboard progress from exam {}: {}", examId, ex.getMessage());
        }

        // ── History: Record exam answers in user_question_history ─────────────
        // Required for study streak calculation and question-history freshness
        // tracking.
        try {
            LocalDateTime now_ldt2 = LocalDateTime.now();
            for (ExamSimulationAnswer answer : answers) {
                QuizQuestion q = answer.getQuestion();
                if (q == null)
                    continue;
                historyRepository.upsertQuestionAnswered(
                        userId,
                        q.getId(),
                        now_ldt2,
                        Boolean.TRUE.equals(answer.getIsCorrect()),
                        0);
            }
            log.info("Question history updated from exam {}: {} records for userId={}",
                    examId, answers.size(), userId);
        } catch (Exception ex) {
            log.warn("Failed to update question history from exam {}: {}", examId, ex.getMessage());
        }

        // ── Streak: Count exam completion as a study day ───────────────────────
        try {
            streakService.updateStreakAndNotify(userId);
        } catch (Exception ex) {
            log.warn("Failed to update streak after exam {}: {}", examId, ex.getMessage());
        }
    }

    /**
     * Cancel an active exam.
     *
     * @param examId Exam ID
     */
    @Transactional
    public void cancelExam(Long examId) {
        log.info("Cancelling exam: {}", examId);

        ExamSimulation exam = examRepository.findById(examId)
                .orElseThrow(() -> new ExamNotFoundException(messages.get("exam.not_found", examId)));

        if (exam.getStatus() != ExamSimulation.ExamStatus.IN_PROGRESS) {
            log.warn("Cannot cancel exam that is not in progress: examId={}, status={}",
                    examId, exam.getStatus());
            throw new ExamNotActiveException(messages.get("exam.cancel.not_active"));
        }

        // Mark as abandoned — distinct from a completed (submitted) exam
        exam.setStatus(ExamSimulation.ExamStatus.ABANDONED);
        exam.setCompletedAt(Instant.now());
        exam.setCorrectAnswers(0);
        exam.setScorePercentage(0.0);

        examRepository.save(exam);

        // Fire a neutral cancelled notification — not exam-failed, which implies a performance result
        try {
            notificationService.createExamAbandonedNotification(exam.getUserId(), examId);
        } catch (Exception ex) {
            log.warn("Failed to create notification for cancelled examId={}: {}", examId, ex.getMessage());
        }

        log.info("✅ Exam cancelled: examId={}", examId);
    }

    /**
     * Submit answer for an exam question.
     * Story A2: Submit Exam Answer
     *
     * Features:
     * - Validates exam is IN_PROGRESS
     * - Validates question belongs to exam
     * - Validates selected option exists
     * - Records answer with timestamp
     * - Updates existing answer if already answered
     * - Does NOT reveal correctness (security)
     *
     * @param examId     Exam simulation ID
     * @param questionId Question ID
     * @param request    Answer submission request
     * @return Answer submission response
     * @throws ExamNotFoundException     if exam not found
     * @throws ExamNotActiveException    if exam not in progress
     * @throws QuestionNotFoundException if question not found in exam
     * @throws InvalidAnswerException    if selected option invalid
     */
    @Transactional
    public SubmitExamAnswerResponse submitAnswer(
            Long examId,
            Long questionId,
            SubmitExamAnswerRequest request,
            Long userId) {

        log.info("Submitting answer for exam {} question {} — user {}", examId, questionId, userId);

        // Story A4: Check time limit first (UTC-aware)
        Instant now = Instant.now();

        // 1. Validate exam exists and ownership
        ExamSimulation exam = getExamById(examId);
        if (!exam.getUserId().equals(userId)) {
            throw new UnauthorizedException(userId, examId);
        }
        if (exam.getStatus() != ExamSimulation.ExamStatus.IN_PROGRESS) {
            throw new ExamNotActiveException(
                    messages.get("exam.submit.invalid_status", exam.getStatus()));
        }

        // Story A4: Check time limit
        if (now.isAfter(exam.getExpiresAt())) {
            // Auto-expire the exam
            exam.setStatus(ExamSimulation.ExamStatus.EXPIRED);
            examRepository.save(exam);
            log.warn("Exam {} expired at {}. Current time: {}", examId, exam.getExpiresAt(), now);
            throw new ExamExpiredException(
                    messages.get("exam.submit.expired", EXAM_TIME_LIMIT_MINUTES),
                    examId);
        }

        // 2. Validate question belongs to this exam
        examQuestionRepository
                .findByExamIdAndQuestionId(examId, questionId)
                .orElseThrow(() -> new QuestionNotFoundException(
                        messages.get("exam.submit.question_not_found_in_exam", questionId, examId)));

        // Load the actual question entity
        QuizQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(
                        messages.get("exam.submit.question_not_found", questionId)));

        // 3. Validate selected option exists and belongs to question
        QuizAnswerOption selectedOption = optionRepository
                .findById(request.getSelectedOptionId())
                .orElseThrow(() -> new InvalidAnswerException(
                        messages.get("exam.submit.invalid_option", request.getSelectedOptionId())));

        boolean deliverableOption = question.getDeliverableOptions().stream()
                .anyMatch(option -> option.getId().equals(selectedOption.getId()));
        if (!selectedOption.getQuestion().getId().equals(questionId) || !deliverableOption) {
            throw new InvalidAnswerException(
                    messages.get("exam.submit.option_mismatch"));
        }
        QuizAnswerOption correctOption = question.getDeliverableOptions().stream()
                .filter(QuizAnswerOption::getIsCorrect)
                .findFirst()
                .orElseThrow(() -> new InvalidAnswerException(
                        messages.get("exam.submit.option_mismatch")));

        // 4. Check if answer already exists (update if exists, create if not)
        ExamSimulationAnswer answer = answerRepository
                .findByExamIdAndQuestionId(examId, questionId)
                .orElse(null);

        if (answer == null) {
            // Create new answer
            answer = ExamSimulationAnswer.builder()
                    .exam(exam)
                    .question(question) // Use the loaded question entity
                    .selectedOption(selectedOption)
                    .correctOption(correctOption)
                    .isCorrect(selectedOption.getIsCorrect()) // Store but don't reveal
                    .answeredAt(now)
                    .timeTakenSeconds(request.getTimeTakenSeconds() != null
                            ? request.getTimeTakenSeconds()
                            : 0)
                    .build();
            log.info("Created new answer for exam {} question {}", examId, questionId);
        } else {
            // Update existing answer
            answer.setQuestion(question); // Ensure question is set
            answer.setSelectedOption(selectedOption);
            answer.setCorrectOption(correctOption);
            answer.setIsCorrect(selectedOption.getIsCorrect());
            answer.setAnsweredAt(now);
            if (request.getTimeTakenSeconds() != null) {
                answer.setTimeTakenSeconds(request.getTimeTakenSeconds());
            }
            log.info("Updated existing answer for exam {} question {}", examId, questionId);
        }

        answer = answerRepository.save(answer);

        // 5. Get progress statistics
        long totalAnswered = answerRepository.countByExamId(examId);

        // 6. Build response (do NOT reveal if correct - security requirement)
        SubmitExamAnswerResponse response = SubmitExamAnswerResponse.builder()
                .answerId(answer.getId())
                .examId(examId)
                .questionId(questionId)
                .selectedOptionId(request.getSelectedOptionId())
                .submittedAt(answer.getAnsweredAt())
                .message(messages.get("exam.submit.success"))
                .totalAnswered((int) totalAnswered)
                .totalQuestions(EXAM_QUESTION_COUNT)
                .build();

        log.info("Answer submitted successfully. Progress: {}/{}", totalAnswered, EXAM_QUESTION_COUNT);

        return response;
    }

    /**
     * Get exam results with detailed breakdown.
     * Story A3: View Exam Results (Production Ready v2.0)
     *
     * @param examId Exam ID
     * @param userId User ID (for ownership verification)
     * @return Comprehensive exam results with recommendations
     * @throws ExamNotFoundException     if exam not found
     * @throws ExamNotCompletedException if exam not completed
     * @throws UnauthorizedException     if user doesn't own the exam
     */
    @Transactional // ✅ Writable transaction (allows auto-expire save)
    public ExamResultsDTO getExamResults(Long examId, Long userId) {
        log.info("Fetching exam results: examId={}, userId={}", examId, userId);

        // 1. Load exam and verify ownership
        ExamSimulation exam = examRepository.findById(examId)
                .orElseThrow(() -> new ExamNotFoundException(
                        messages.get("exam.not_found", examId)));

        if (!exam.getUserId().equals(userId)) {
            throw new UnauthorizedException(userId, examId);
        }

        // ✅ Auto-expire if time has passed (Story A4)
        if (exam.getStatus() == ExamSimulation.ExamStatus.IN_PROGRESS &&
                Instant.now().isAfter(exam.getExpiresAt())) {
            exam.setStatus(ExamSimulation.ExamStatus.EXPIRED);
            examRepository.saveAndFlush(exam); // Save immediately
            log.info("Auto-expired exam {} after time limit", examId);
        }

        // 2. Verify exam is completed or expired (Story A4)
        if (exam.getStatus() != ExamSimulation.ExamStatus.COMPLETED &&
                exam.getStatus() != ExamSimulation.ExamStatus.EXPIRED) {
            throw new ExamNotCompletedException(examId, exam.getStatus().name());
        }

        // 3. Load all answers
        List<ExamSimulationAnswer> answers = answerRepository.findByExamId(examId);
        log.debug("Loaded {} answers for exam {}", answers.size(), examId);

        // 4. Calculate category breakdown (with performance levels)
        Map<Long, CategoryBreakdownDTO> categoryMap = calculateCategoryBreakdown(answers);
        List<CategoryBreakdownDTO> categoryBreakdown = new ArrayList<>(categoryMap.values());

        // Sort by accuracy (worst first) to highlight weak areas
        categoryBreakdown.sort(Comparator.comparing(CategoryBreakdownDTO::getAccuracyPercentage));

        // 5. Get incorrect questions (with enhanced details)
        List<IncorrectQuestionDTO> incorrectQuestions = getIncorrectQuestions(answers);

        // 5b. Get all answered questions for full review
        List<AllAnsweredQuestionDTO> allAnswers = getAllAnswers(answers);

        // 6. Calculate statistics
        int answeredCount = answers.size();
        int correctCount = (int) answers.stream().filter(ExamSimulationAnswer::getIsCorrect).count();
        int wrongCount = answeredCount - correctCount;
        int unansweredCount = EXAM_QUESTION_COUNT - answeredCount;

        double scorePercentage = (correctCount * 100.0) / EXAM_QUESTION_COUNT;
        boolean passed = correctCount >= PASSING_SCORE;

        String resultStatus = passed ? "PASSED" : "FAILED";
        int pointsToPass = passed ? 0 : (PASSING_SCORE - correctCount);

        Integer totalTime = resolveExamElapsedSeconds(exam);
        Integer avgTime = totalTime != null && answeredCount > 0
                ? totalTime / answeredCount
                : null;

        // Calculate duration in minutes
        Integer durationMinutes = totalTime != null ? totalTime / 60 : null;

        // 7. Identify weak categories (<60% accuracy)
        List<String> weakCategories = categoryBreakdown.stream()
                .filter(cat -> cat.getIsWeakArea() != null && cat.getIsWeakArea())
                .map(CategoryBreakdownDTO::getCategoryNameEn)
                .collect(Collectors.toList());

        // 8. Generate personalized recommendation
        String recommendedAction = generateRecommendation(passed, weakCategories, scorePercentage, pointsToPass);

        // 9. Build comprehensive DTO
        ExamResultsDTO results = ExamResultsDTO.builder()
                .examId(examId)
                .userId(userId)
                .completedAt(exam.getCompletedAt())
                .totalQuestions(EXAM_QUESTION_COUNT)
                .correctAnswers(correctCount)
                .wrongAnswers(wrongCount)
                .scorePercentage(Math.round(scorePercentage * 100.0) / 100.0) // 2 decimals
                .passed(passed)
                .passingScore(PASSING_SCORE)
                .timeTakenSeconds(totalTime)
                .averageTimePerQuestion(avgTime)
                // Production enhancements (v2.0)
                .durationMinutes(durationMinutes)
                .answeredCount(answeredCount)
                .unansweredCount(unansweredCount)
                .resultStatus(resultStatus)
                .passingThreshold(PASSING_SCORE)
                .pointsToPass(pointsToPass)
                .weakCategories(weakCategories)
                .recommendedAction(recommendedAction)
                // Breakdowns
                .categoryBreakdown(categoryBreakdown)
                .incorrectQuestions(incorrectQuestions)
                .allAnswers(allAnswers)
                .build();

        log.info("✅ Exam results calculated: examId={}, score={}/{} ({}%), passed={}",
                examId, correctCount, EXAM_QUESTION_COUNT, scorePercentage, passed);

        return results;
    }

    /**
     * Calculate performance breakdown by category (Production v2.0).
     * Includes performance levels and weak area detection.
     */
    private Map<Long, CategoryBreakdownDTO> calculateCategoryBreakdown(List<ExamSimulationAnswer> answers) {
        Map<Long, CategoryBreakdownDTO> categoryMap = new HashMap<>();

        for (ExamSimulationAnswer answer : answers) {
            // Get question from relationship
            QuizQuestion question = answer.getQuestion();

            if (question == null || question.getCategory() == null) {
                continue; // Skip if no category
            }

            Long categoryId = question.getCategory().getId();

            // Get or create category breakdown
            CategoryBreakdownDTO breakdown = categoryMap.computeIfAbsent(categoryId, id -> {
                var category = question.getCategory();
                return CategoryBreakdownDTO.builder()
                        .categoryId(id)
                        .categoryCode(category.getCode()) // Production enhancement
                        .categoryNameEn(category.getNameEn())
                        .categoryNameAr(category.getNameAr())
                        .categoryNameNl(category.getNameNl())
                        .categoryNameFr(category.getNameFr())
                        .totalQuestions(0)
                        .correctAnswers(0)
                        .wrongAnswers(0) // Production enhancement
                        .accuracyPercentage(0.0)
                        .build();
            });

            // Update counts
            breakdown.setTotalQuestions(breakdown.getTotalQuestions() + 1);
            if (answer.getIsCorrect()) {
                breakdown.setCorrectAnswers(breakdown.getCorrectAnswers() + 1);
            } else {
                breakdown.setWrongAnswers(breakdown.getWrongAnswers() + 1);
            }
        }

        // Calculate accuracy, performance level, and weak area flag
        categoryMap.values().forEach(breakdown -> {
            if (breakdown.getTotalQuestions() > 0) {
                double accuracy = (breakdown.getCorrectAnswers() * 100.0) / breakdown.getTotalQuestions();
                breakdown.setAccuracyPercentage(Math.round(accuracy * 100.0) / 100.0);

                // Calculate performance level (Production v2.0)
                String performanceLevel = calculatePerformanceLevel(accuracy);
                breakdown.setPerformanceLevel(performanceLevel);

                // Set weak area flag (Production v2.0)
                boolean isWeakArea = accuracy < 60.0;
                breakdown.setIsWeakArea(isWeakArea);
            }
        });

        return categoryMap;
    }

    /**
     * Get details of incorrectly answered questions (Production v2.0).
     * Enhanced with analytics fields and error categorization.
     */
    private List<IncorrectQuestionDTO> getIncorrectQuestions(List<ExamSimulationAnswer> answers) {
        return answers.stream()
                .filter(answer -> !answer.getIsCorrect())
                .map(answer -> {
                    // Get question and options from relationships
                    QuizQuestion question = answer.getQuestion();
                    QuizAnswerOption selectedOption = answer.getSelectedOption();

                    if (question == null || selectedOption == null) {
                        return null; // Skip if relationship not loaded
                    }

                    // Get correct option
                    QuizAnswerOption correctOption = resolveHistoricalCorrectOption(answer, question);

                    if (correctOption == null) {
                        return null; // Skip if no correct option
                    }

                    Category category = question.getCategory();
                    String selectedEn = roadSignReferenceTextResolver.resolveEn(selectedOption.getOptionTextEn());
                    String selectedAr = roadSignReferenceTextResolver.resolveAr(selectedOption.getOptionTextAr());
                    String selectedNl = roadSignReferenceTextResolver.resolveNl(selectedOption.getOptionTextNl());
                    String selectedFr = roadSignReferenceTextResolver.resolveFr(selectedOption.getOptionTextFr());
                    String correctEn = roadSignReferenceTextResolver.resolveEn(correctOption.getOptionTextEn());
                    String correctAr = roadSignReferenceTextResolver.resolveAr(correctOption.getOptionTextAr());
                    String correctNl = roadSignReferenceTextResolver.resolveNl(correctOption.getOptionTextNl());
                    String correctFr = roadSignReferenceTextResolver.resolveFr(correctOption.getOptionTextFr());

                    return IncorrectQuestionDTO.builder()
                            .questionId(question.getId())
                            .questionTextEn(roadSignReferenceTextResolver.resolveEn(question.getQuestionEn()))
                            .questionTextAr(roadSignReferenceTextResolver.resolveAr(question.getQuestionAr()))
                            .questionTextNl(roadSignReferenceTextResolver.resolveNl(question.getQuestionNl()))
                            .questionTextFr(roadSignReferenceTextResolver.resolveFr(question.getQuestionFr()))
                            .selectedOptionId(selectedOption.getId())
                            .selectedOptionText(selectedEn)
                            .selectedOptionTextEn(selectedEn)
                            .selectedOptionTextAr(selectedAr)
                            .selectedOptionTextNl(selectedNl)
                            .selectedOptionTextFr(selectedFr)
                            .correctOptionId(correctOption.getId())
                            .correctOptionText(correctEn)
                            .correctOptionTextEn(correctEn)
                            .correctOptionTextAr(correctAr)
                            .correctOptionTextNl(correctNl)
                            .correctOptionTextFr(correctFr)
                            .explanationEn(roadSignReferenceTextResolver.resolveEn(question.getExplanationEn()))
                            .explanationAr(roadSignReferenceTextResolver.resolveAr(question.getExplanationAr()))
                            .explanationNl(roadSignReferenceTextResolver.resolveNl(question.getExplanationNl()))
                            .explanationFr(roadSignReferenceTextResolver.resolveFr(question.getExplanationFr()))
                            .categoryName(category != null
                                    ? category.getNameEn()
                                    : messages.get("analytics.category.unknown"))
                            .categoryNameEn(category != null ? category.getNameEn() : null)
                            .categoryNameAr(category != null ? category.getNameAr() : null)
                            .categoryNameNl(category != null ? category.getNameNl() : null)
                            .categoryNameFr(category != null ? category.getNameFr() : null)
                            // Production enhancements (v2.0)
                            .categoryCode(category != null ? category.getCode() : null)
                            .contentImageUrl(question.getContentImageUrl()) // Traffic sign image
                            .userAnswerOptionId(selectedOption.getId()) // For analytics
                            .correctAnswerOptionId(correctOption.getId()) // For analytics
                            .typicalErrorType(
                                    question.getTypicalErrorType() != null ? question.getTypicalErrorType().name()
                                            : null)
                            .build();
                })
                .filter(java.util.Objects::nonNull) // Remove nulls
                .collect(Collectors.toList());
    }

    /**
     * Get all answered questions (correct and incorrect) for full review.
     */
    private List<AllAnsweredQuestionDTO> getAllAnswers(List<ExamSimulationAnswer> answers) {
        return answers.stream()
                .map(answer -> {
                    QuizQuestion question = answer.getQuestion();
                    QuizAnswerOption selectedOption = answer.getSelectedOption();

                    if (question == null || selectedOption == null) {
                        return null;
                    }

                    QuizAnswerOption correctOption = resolveHistoricalCorrectOption(answer, question);

                    if (correctOption == null) {
                        return null;
                    }

                    Category category = question.getCategory();
                    String selectedEn = roadSignReferenceTextResolver.resolveEn(selectedOption.getOptionTextEn());
                    String selectedAr = roadSignReferenceTextResolver.resolveAr(selectedOption.getOptionTextAr());
                    String selectedNl = roadSignReferenceTextResolver.resolveNl(selectedOption.getOptionTextNl());
                    String selectedFr = roadSignReferenceTextResolver.resolveFr(selectedOption.getOptionTextFr());
                    String correctEn = roadSignReferenceTextResolver.resolveEn(correctOption.getOptionTextEn());
                    String correctAr = roadSignReferenceTextResolver.resolveAr(correctOption.getOptionTextAr());
                    String correctNl = roadSignReferenceTextResolver.resolveNl(correctOption.getOptionTextNl());
                    String correctFr = roadSignReferenceTextResolver.resolveFr(correctOption.getOptionTextFr());

                    return AllAnsweredQuestionDTO.builder()
                            .questionId(question.getId())
                            .questionTextEn(roadSignReferenceTextResolver.resolveEn(question.getQuestionEn()))
                            .questionTextAr(roadSignReferenceTextResolver.resolveAr(question.getQuestionAr()))
                            .questionTextNl(roadSignReferenceTextResolver.resolveNl(question.getQuestionNl()))
                            .questionTextFr(roadSignReferenceTextResolver.resolveFr(question.getQuestionFr()))
                            .selectedOptionId(selectedOption.getId())
                            .selectedOptionText(selectedEn)
                            .selectedOptionTextEn(selectedEn)
                            .selectedOptionTextAr(selectedAr)
                            .selectedOptionTextNl(selectedNl)
                            .selectedOptionTextFr(selectedFr)
                            .correctOptionId(correctOption.getId())
                            .correctOptionText(correctEn)
                            .correctOptionTextEn(correctEn)
                            .correctOptionTextAr(correctAr)
                            .correctOptionTextNl(correctNl)
                            .correctOptionTextFr(correctFr)
                            .explanationEn(roadSignReferenceTextResolver.resolveEn(question.getExplanationEn()))
                            .explanationAr(roadSignReferenceTextResolver.resolveAr(question.getExplanationAr()))
                            .explanationNl(roadSignReferenceTextResolver.resolveNl(question.getExplanationNl()))
                            .explanationFr(roadSignReferenceTextResolver.resolveFr(question.getExplanationFr()))
                            .categoryName(category != null
                                    ? category.getNameEn()
                                    : messages.get("analytics.category.unknown"))
                            .categoryNameEn(category != null ? category.getNameEn() : null)
                            .categoryNameAr(category != null ? category.getNameAr() : null)
                            .categoryNameNl(category != null ? category.getNameNl() : null)
                            .categoryNameFr(category != null ? category.getNameFr() : null)
                            .categoryCode(category != null ? category.getCode() : null)
                            .contentImageUrl(question.getContentImageUrl())
                            .isCorrect(answer.getIsCorrect())
                            .build();
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    private QuizAnswerOption resolveHistoricalCorrectOption(
            ExamSimulationAnswer answer,
            QuizQuestion question) {
        if (answer.getCorrectOption() != null) {
            return answer.getCorrectOption();
        }
        return question.getOptions().stream()
                .filter(QuizAnswerOption::getIsCorrect)
                .sorted(Comparator.comparing(
                        QuizAnswerOption::getDisplayOrder,
                        Comparator.nullsLast(Integer::compareTo)))
                .findFirst()
                .orElse(null);
    }

    private Integer resolveExamElapsedSeconds(ExamSimulation exam) {
        Integer calculated = calculateElapsedSeconds(exam.getStartedAt(), exam.getCompletedAt());
        if (calculated != null) {
            return calculated;
        }

        Integer stored = exam.getTimeTakenSeconds();
        return stored != null && stored > 0 ? stored : null;
    }

    private Integer calculateElapsedSeconds(Instant startedAt, Instant completedAt) {
        if (startedAt == null || completedAt == null || completedAt.isBefore(startedAt)) {
            return null;
        }

        long elapsedSeconds = Duration.between(startedAt, completedAt).getSeconds();
        long maximumSeconds = Duration.ofMinutes(EXAM_TIME_LIMIT_MINUTES).getSeconds();
        return (int) Math.min(elapsedSeconds, maximumSeconds);
    }

    /**
     * Calculate performance level based on accuracy percentage.
     * Production v2.0 helper method.
     *
     * @param accuracy Accuracy percentage (0-100)
     * @return "EXCELLENT" (≥80%), "GOOD" (≥60%), or "NEEDS_IMPROVEMENT" (<60%)
     */
    private String calculatePerformanceLevel(double accuracy) {
        if (accuracy >= 80.0) {
            return "EXCELLENT";
        } else if (accuracy >= 60.0) {
            return "GOOD";
        } else {
            return "NEEDS_IMPROVEMENT";
        }
    }

    /**
     * Generate personalized recommendation based on exam performance.
     * Production v2.0 helper method.
     *
     * @param passed          Whether user passed the exam
     * @param weakCategories  List of weak category names
     * @param scorePercentage Score percentage (0-100)
     * @param pointsToPass    Points needed to pass (0 if already passed)
     * @return Personalized recommendation message
     */
    private String generateRecommendation(boolean passed, List<String> weakCategories,
            double scorePercentage, int pointsToPass) {
        if (passed) {
            // User passed the exam
            if (weakCategories.isEmpty() && scorePercentage >= 96.0) {
                return messages.get("exam.results.outstanding");
            } else if (weakCategories.isEmpty()) {
                return messages.get("exam.results.excellent");
            } else {
                return messages.get("exam.results.passed_review", String.join(", ", weakCategories));
            }
        } else {
            // User failed the exam
            if (weakCategories.isEmpty()) {
                return messages.get("exam.results.keep_practicing", pointsToPass);
            } else {
                return messages.get(
                        "exam.results.focus_on",
                        String.join(", ", weakCategories),
                        pointsToPass);
            }
        }
    }
}
