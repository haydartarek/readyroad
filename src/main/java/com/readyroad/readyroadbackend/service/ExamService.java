package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.ExamSimulation;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulationAnswer;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulationQuestion;
import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.entity.NotificationType;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationAnswerRepository;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationRepository;
import com.readyroad.readyroadbackend.domain.repository.NotificationRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizAnswerOptionRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.dto.exam.ExamStartResponse;
import com.readyroad.readyroadbackend.dto.exam.SubmitExamAnswerRequest;
import com.readyroad.readyroadbackend.dto.exam.SubmitExamAnswerResponse;
import com.readyroad.readyroadbackend.dto.exam.ExamResultsDTO;
import com.readyroad.readyroadbackend.dto.exam.CategoryBreakdownDTO;
import com.readyroad.readyroadbackend.dto.exam.IncorrectQuestionDTO;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
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
    private final SmartQuizService smartQuizService;
    private final QuizService quizService; // Story D1: Belgian compliance validation
    private final ExamMapper examMapper; // DTO mapping
    private final NotificationService notificationService; // Story N1: Exam result notifications
    private final NotificationRepository notificationRepository; // Dedup checks
    private final AchievementService achievementService; // Story N2: Achievement notifications

    private static final int EXAM_QUESTION_COUNT    = 50;
    private static final int EXAM_TIME_LIMIT_MINUTES = 30;
    private static final int PASSING_SCORE           = 41;

    // Belgian driving exam: fixed difficulty distribution
    private static final int EASY_QUESTION_COUNT   = 20;   // 40%
    private static final int MEDIUM_QUESTION_COUNT = 20;   // 40%
    private static final int HARD_QUESTION_COUNT   = 10;   // 20%
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

        // AC1: Check for active exam
        ExamSimulation activeExam = examRepository
            .findByUserIdAndStatus(userId, ExamSimulation.ExamStatus.IN_PROGRESS)
            .orElse(null);

        if (activeExam != null) {
            // ✅ Use custom exception for proper 409 Conflict response
            throw new ActiveExamAlreadyExistsException(userId, activeExam.getId());
        }

        // ✅ Belgian exam standard: 20 EASY + 20 MEDIUM + 10 HARD = 50 questions
        // Uses ORDER BY RAND() in DB for true randomness per difficulty tier
        List<QuizQuestion> easyPool = questionRepository
                .findRandomQuestionsByDifficulty(QuizQuestion.DifficultyLevel.EASY).stream()
                .filter(q -> q != null && q.getId() != null)
                .filter(q -> q.getOptions() != null && q.getOptions().size() >= 2)
                .collect(Collectors.toList());

        List<QuizQuestion> mediumPool = questionRepository
                .findRandomQuestionsByDifficulty(QuizQuestion.DifficultyLevel.MEDIUM).stream()
                .filter(q -> q != null && q.getId() != null)
                .filter(q -> q.getOptions() != null && q.getOptions().size() >= 2)
                .collect(Collectors.toList());

        List<QuizQuestion> hardPool = questionRepository
                .findRandomQuestionsByDifficulty(QuizQuestion.DifficultyLevel.HARD).stream()
                .filter(q -> q != null && q.getId() != null)
                .filter(q -> q.getOptions() != null && q.getOptions().size() >= 2)
                .collect(Collectors.toList());

        log.debug("Difficulty pool sizes — EASY: {}, MEDIUM: {}, HARD: {}",
                easyPool.size(), mediumPool.size(), hardPool.size());

        // Validate each tier has enough questions before building the exam
        if (easyPool.size() < EASY_QUESTION_COUNT) {
            log.error("Insufficient EASY questions. Required: {}, Available: {}",
                    EASY_QUESTION_COUNT, easyPool.size());
            throw new IllegalStateException(String.format(
                    "Insufficient EASY questions. Required: %d, Available: %d",
                    EASY_QUESTION_COUNT, easyPool.size()));
        }
        if (mediumPool.size() < MEDIUM_QUESTION_COUNT) {
            log.error("Insufficient MEDIUM questions. Required: {}, Available: {}",
                    MEDIUM_QUESTION_COUNT, mediumPool.size());
            throw new IllegalStateException(String.format(
                    "Insufficient MEDIUM questions. Required: %d, Available: %d",
                    MEDIUM_QUESTION_COUNT, mediumPool.size()));
        }
        if (hardPool.size() < HARD_QUESTION_COUNT) {
            log.error("Insufficient HARD questions. Required: {}, Available: {}",
                    HARD_QUESTION_COUNT, hardPool.size());
            throw new IllegalStateException(String.format(
                    "Insufficient HARD questions. Required: %d, Available: %d",
                    HARD_QUESTION_COUNT, hardPool.size()));
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
                .orElseThrow(() -> new IllegalArgumentException("Exam not found: " + examId));
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
    @Transactional(readOnly = true)
    public boolean canStartExam(Long userId) {
        return !examRepository.existsByUserIdAndStatus(userId, ExamSimulation.ExamStatus.IN_PROGRESS);
    }

    /**
     * Get active exam for user.
     * ✅ Eagerly loads exam questions and options to prevent LazyInitializationException
     *
     * @param userId User ID
     * @return Active exam or null
     */
    @Transactional(readOnly = true)
    public ExamSimulation getActiveExam(Long userId) {
        ExamSimulation exam = examRepository.findByUserIdAndStatus(userId, ExamSimulation.ExamStatus.IN_PROGRESS)
                .orElse(null);

        if (exam != null) {
            // ✅ Force load exam questions and their options inside transaction
            List<ExamSimulationQuestion> questions = examQuestionRepository.findByExamIdOrderByQuestionOrder(exam.getId());
            questions.forEach(esq -> {
                QuizQuestion q = esq.getQuestion();
                if (q != null && q.getOptions() != null) {
                    q.getOptions().size(); // Trigger lazy loading
                }
            });
        }

        return exam;
    }

    /**
     * Get completed exams for user.
     *
     * @param userId User ID
     * @return List of completed exams ordered by completion date (newest first)
     */
    @Transactional(readOnly = true)
    public List<ExamSimulation> getCompletedExams(Long userId) {
        log.info("Fetching completed exams for user: {}", userId);
        return examRepository.findByUserIdAndStatusOrderByCompletedAtDesc(
            userId,
            ExamSimulation.ExamStatus.COMPLETED
        );
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
                        String.format("Exam %d not found", examId)));

        if (!exam.getUserId().equals(userId)) {
            throw new UnauthorizedException(userId, examId);
        }

        // Idempotent — already in a terminal state, nothing to do
        if (exam.getStatus() == ExamSimulation.ExamStatus.COMPLETED ||
                exam.getStatus() == ExamSimulation.ExamStatus.EXPIRED) {
            log.info("Exam {} already in terminal state: {}", examId, exam.getStatus());
            return;
        }

        if (exam.getStatus() != ExamSimulation.ExamStatus.IN_PROGRESS) {
            throw new ExamNotActiveException(
                    "Cannot complete exam with status: " + exam.getStatus());
        }

        // Calculate final score from all submitted answers
        List<ExamSimulationAnswer> answers = answerRepository.findByExamId(examId);
        int correctCount = (int) answers.stream()
                .filter(ExamSimulationAnswer::getIsCorrect)
                .count();
        double scorePercentage = (correctCount * 100.0) / EXAM_QUESTION_COUNT;

        // Persist completion state and score on the exam entity
        exam.setStatus(ExamSimulation.ExamStatus.COMPLETED);
        exam.setCompletedAt(Instant.now());
        exam.setCorrectAnswers(correctCount);
        exam.setScorePercentage(scorePercentage);

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

        // ── Story N2: Fire WEAK_AREA notifications (deduped per 24h) ─────────
        try {
            List<ExamSimulationAnswer> allAnswers = answerRepository.findByExamId(examId);
            Map<Long, CategoryBreakdownDTO> categoryMap = calculateCategoryBreakdown(allAnswers);
            Instant weakAreaCutoff = Instant.now().minusSeconds(24 * 3600);
            for (CategoryBreakdownDTO cat : categoryMap.values()) {
                if (Boolean.TRUE.equals(cat.getIsWeakArea())) {
                    // Dedup: skip if we already sent a WEAK_AREA notif for this user in the last 24h
                    // (We use category name in message but dedup on type+userId+window to avoid flooding)
                    boolean recentlySent = !notificationRepository
                            .findByUserIdAndTypeAndCreatedAtAfter(
                                    exam.getUserId(), NotificationType.WEAK_AREA, weakAreaCutoff)
                            .isEmpty();
                    if (!recentlySent) {
                        notificationService.createWeakAreaNotification(
                                exam.getUserId(), cat.getCategoryNameEn());
                        log.info("WEAK_AREA notification sent for userId={}, category={}",
                                exam.getUserId(), cat.getCategoryNameEn());
                        break; // Send max 1 weak-area notif per exam to avoid spam
                    } else {
                        log.debug("WEAK_AREA notification skipped (sent within 24h) for userId={}",
                                exam.getUserId());
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            log.warn("Failed to create weak-area notifications for examId={}: {}", examId, ex.getMessage());
        }

        // ── Story N3: Fire ACHIEVEMENT notifications ──────────────────────────
        try {
            achievementService.checkAndAwardExamAchievements(exam.getUserId(), examId, passed, correctCount);
        } catch (Exception ex) {
            log.warn("Failed to check achievements for examId={}: {}", examId, ex.getMessage());
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
            .orElseThrow(() -> new ExamNotFoundException("Exam not found: " + examId));

        if (exam.getStatus() != ExamSimulation.ExamStatus.IN_PROGRESS) {
            log.warn("Cannot cancel exam that is not in progress: examId={}, status={}",
                examId, exam.getStatus());
            throw new ExamNotActiveException("Exam is not active");
        }

        // Mark as cancelled by setting status to COMPLETED with 0 score
        exam.setStatus(ExamSimulation.ExamStatus.COMPLETED);
        exam.setCompletedAt(Instant.now());
        exam.setCorrectAnswers(0);
        exam.setScorePercentage(0.0);

        examRepository.save(exam);

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
     * @param examId Exam simulation ID
     * @param questionId Question ID
     * @param request Answer submission request
     * @return Answer submission response
     * @throws ExamNotFoundException if exam not found
     * @throws ExamNotActiveException if exam not in progress
     * @throws QuestionNotFoundException if question not found in exam
     * @throws InvalidAnswerException if selected option invalid
     */
    @Transactional
    public SubmitExamAnswerResponse submitAnswer(
            Long examId,
            Long questionId,
            SubmitExamAnswerRequest request) {

        log.info("Submitting answer for exam {} question {}", examId, questionId);

        // Story A4: Check time limit first (UTC-aware)
        Instant now = Instant.now();

        // 1. Validate exam exists and is IN_PROGRESS
        ExamSimulation exam = getExamById(examId);
        if (exam.getStatus() != ExamSimulation.ExamStatus.IN_PROGRESS) {
            throw new ExamNotActiveException(
                    String.format("Cannot submit answer. Exam status: %s", exam.getStatus())
            );
        }

        // Story A4: Check time limit
        if (now.isAfter(exam.getExpiresAt())) {
            // Auto-expire the exam
            exam.setStatus(ExamSimulation.ExamStatus.EXPIRED);
            examRepository.save(exam);
            log.warn("Exam {} expired at {}. Current time: {}", examId, exam.getExpiresAt(), now);
            throw new ExamExpiredException(
                    String.format("Exam has expired. Time limit: %d minutes", EXAM_TIME_LIMIT_MINUTES),
                    examId
            );
        }

        // 2. Validate question belongs to this exam
        ExamSimulationQuestion examQuestion = examQuestionRepository
                .findByExamIdAndQuestionId(examId, questionId)
                .orElseThrow(() -> new QuestionNotFoundException(
                        String.format("Question %d not found in exam %d", questionId, examId)
                ));

        // Load the actual question entity (examQuestion.getQuestion() is lazy and may be null)
        QuizQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(
                        String.format("Question %d not found", questionId)
                ));

        // 3. Validate selected option exists and belongs to question
        QuizAnswerOption selectedOption = optionRepository
                .findById(request.getSelectedOptionId())
                .orElseThrow(() -> new InvalidAnswerException(
                        String.format("Invalid option ID: %d", request.getSelectedOptionId())
                ));

        if (!selectedOption.getQuestion().getId().equals(questionId)) {
            throw new InvalidAnswerException(
                    "Selected option does not belong to this question"
            );
        }

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
                    .isCorrect(selectedOption.getIsCorrect()) // Store but don't reveal
                    .answeredAt(now)
                    .timeTakenSeconds(0) // Not tracked in answer submission
                    .build();
            log.info("Created new answer for exam {} question {}", examId, questionId);
        } else {
            // Update existing answer
            answer.setQuestion(question); // Ensure question is set
            answer.setSelectedOption(selectedOption);
            answer.setIsCorrect(selectedOption.getIsCorrect());
            answer.setAnsweredAt(now);
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
                .message("Answer submitted successfully")
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
     * @throws ExamNotFoundException if exam not found
     * @throws ExamNotCompletedException if exam not completed
     * @throws UnauthorizedException if user doesn't own the exam
     */
    @Transactional // ✅ Writable transaction (allows auto-expire save)
    public ExamResultsDTO getExamResults(Long examId, Long userId) {
        log.info("Fetching exam results: examId={}, userId={}", examId, userId);

        // 1. Load exam and verify ownership
        ExamSimulation exam = examRepository.findById(examId)
                .orElseThrow(() -> new ExamNotFoundException(
                        String.format("Exam %d not found", examId)
                ));

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

        // 6. Calculate statistics
        int answeredCount = answers.size();
        int correctCount = (int) answers.stream().filter(ExamSimulationAnswer::getIsCorrect).count();
        int wrongCount = answeredCount - correctCount;
        int unansweredCount = EXAM_QUESTION_COUNT - answeredCount;

        double scorePercentage = (correctCount * 100.0) / EXAM_QUESTION_COUNT;
        boolean passed = correctCount >= PASSING_SCORE;

        String resultStatus = passed ? "PASSED" : "FAILED";
        int pointsToPass = passed ? 0 : (PASSING_SCORE - correctCount);

        int totalTime = answers.stream()
                .mapToInt(ExamSimulationAnswer::getTimeTakenSeconds)
                .sum();
        int avgTime = answeredCount > 0 ? totalTime / answeredCount : 0;

        // Calculate duration in minutes
        Integer durationMinutes = null;
        if (exam.getStartedAt() != null && exam.getCompletedAt() != null) {
            long durationSeconds = java.time.Duration.between(
                    exam.getStartedAt(),
                    exam.getCompletedAt()
            ).getSeconds();
            durationMinutes = (int) (durationSeconds / 60);
        }

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
                    QuizAnswerOption correctOption = question.getOptions().stream()
                            .filter(QuizAnswerOption::getIsCorrect)
                            .findFirst()
                            .orElse(null);

                    if (correctOption == null) {
                        return null; // Skip if no correct option
                    }

                    return IncorrectQuestionDTO.builder()
                            .questionId(question.getId())
                            .questionTextEn(question.getQuestionEn())
                            .questionTextAr(question.getQuestionAr())
                            .questionTextNl(question.getQuestionNl())
                            .questionTextFr(question.getQuestionFr())
                            .selectedOptionId(selectedOption.getId())
                            .selectedOptionText(selectedOption.getOptionTextEn())
                            .correctOptionId(correctOption.getId())
                            .correctOptionText(correctOption.getOptionTextEn())
                            .explanationEn(question.getExplanationEn())
                            .explanationAr(question.getExplanationAr())
                            .explanationNl(question.getExplanationNl())
                            .explanationFr(question.getExplanationFr())
                            .categoryName(question.getCategory() != null ? question.getCategory().getNameEn() : "Unknown")
                            // Production enhancements (v2.0)
                            .categoryCode(question.getCategory() != null ? question.getCategory().getCode() : null)
                            .contentImageUrl(question.getContentImageUrl()) // Traffic sign image
                            .userAnswerOptionId(selectedOption.getId()) // For analytics
                            .correctAnswerOptionId(correctOption.getId()) // For analytics
                            .typicalErrorType(question.getTypicalErrorType() != null ? question.getTypicalErrorType().name() : null)
                            .build();
                })
                .filter(java.util.Objects::nonNull) // Remove nulls
                .collect(Collectors.toList());
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
     * @param passed Whether user passed the exam
     * @param weakCategories List of weak category names
     * @param scorePercentage Score percentage (0-100)
     * @param pointsToPass Points needed to pass (0 if already passed)
     * @return Personalized recommendation message
     */
    private String generateRecommendation(boolean passed, List<String> weakCategories,
                                          double scorePercentage, int pointsToPass) {
        if (passed) {
            // User passed the exam
            if (weakCategories.isEmpty() && scorePercentage >= 96.0) {
                return "Outstanding! You're more than ready 🎉";
            } else if (weakCategories.isEmpty()) {
                return "Excellent! You're fully prepared 🚀";
            } else {
                return "You passed! Consider reviewing: " + String.join(", ", weakCategories);
            }
        } else {
            // User failed the exam
            if (weakCategories.isEmpty()) {
                return String.format("Keep practicing! You need %d more correct answer%s. Focus on understanding each question type.",
                        pointsToPass, pointsToPass == 1 ? "" : "s");
            } else {
                return String.format("Focus on: %s. You need %d more correct answer%s. Then retake the exam.",
                        String.join(", ", weakCategories),
                        pointsToPass,
                        pointsToPass == 1 ? "" : "s");
            }
        }
    }
}
