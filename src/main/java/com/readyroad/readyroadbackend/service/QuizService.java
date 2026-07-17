package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.dto.TheoryExamAnswerRequest;
import com.readyroad.readyroadbackend.dto.TheoryExamQuestionResultDTO;
import com.readyroad.readyroadbackend.dto.TheoryExamResultDTO;
import com.readyroad.readyroadbackend.exception.BelgianComplianceException;
import com.readyroad.readyroadbackend.util.PlaceholderDetector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * QuizService - Basic Quiz Generation
 *
 * **Phase 2 Restoration:** Implemented January 18, 2026
 * **Fixed:** February 5, 2026 - LazyInitializationException resolved
 *
 * Provides basic quiz generation functionality for the current theory-question
 * bank:
 * - ✅ Random question selection (two-step approach with @EntityGraph)
 * - ✅ Category filtering
 * - ✅ Belgian compliance validation
 * - ❌ NO 24-hour cooldown
 * - ❌ NO user history tracking
 * - ❌ NO adaptive difficulty
 * - ❌ NO error pattern analysis
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class QuizService {

    private final QuizQuestionRepository quizQuestionRepository;
    private final com.readyroad.readyroadbackend.domain.repository.CategoryRepository categoryRepository;
    private final RoadSignReferenceTextResolver roadSignReferenceTextResolver;
    private final BackendMessageService messages;

    /**
     * Maximum questions allowed per quiz to prevent abuse
     */
    private static final int MAX_QUESTIONS_PER_QUIZ = 50;

    /**
     * Generate a random quiz with specified number of questions
     * 
     * **Fix Applied:** Uses two-step approach to prevent
     * LazyInitializationException:
     * 1. Get random question IDs using database-native random ordering
     * 2. Fetch questions with options using @EntityGraph for eager loading
     *
     * @param count Number of questions requested (capped at MAX_QUESTIONS_PER_QUIZ)
     * @return List of random quiz questions with options loaded
     */
    public List<QuizQuestion> generateRandomQuiz(int count) {
        log.info("🎲 Generating random quiz with {} questions", count);

        // Validate and cap the count
        int actualCount = Math.min(Math.max(count, 1), MAX_QUESTIONS_PER_QUIZ);

        // Check if we have any active questions
        Long totalQuestions = quizQuestionRepository.countByIsActiveTrue();
        if (totalQuestions == null || totalQuestions == 0) {
            log.warn("⚠️ No active questions available");
            return Collections.emptyList();
        }

        // Adjust count if requested more than available
        actualCount = (int) Math.min(actualCount, totalQuestions);

        // Step 1: Get random question IDs using database-native random ordering
        List<Long> questionIds = quizQuestionRepository.findRandomQuestionIds(actualCount);

        // If no questions found, return empty list
        if (questionIds.isEmpty()) {
            log.warn("⚠️ No question IDs returned from random query");
            return Collections.emptyList();
        }

        log.debug("📋 Retrieved {} random question IDs", questionIds.size());

        // Step 2: Fetch every association required by the response mapper.
        List<QuizQuestion> questions = quizQuestionRepository.findAllByIdWithOptionsAndCategory(questionIds);

        // Filter out questions whose options are placeholder / corrupted content
        List<QuizQuestion> validQuestions = questions.stream()
                .filter(this::hasMinValidOptions)
                .collect(Collectors.toList());
        if (validQuestions.size() < questions.size()) {
            log.warn("⚠️ Random quiz: {} question(s) excluded — insufficient valid (non-placeholder) options",
                    questions.size() - validQuestions.size());
        }

        log.info("✅ Generated random quiz with {} questions (options and categories eagerly loaded)",
                validQuestions.size());
        return validQuestions;
    }

    /**
     * Generate a quiz with questions from a specific category
     * 
     * **Fix Applied:** Uses two-step approach to prevent
     * LazyInitializationException:
     * 1. Get random question IDs by category using database-native random ordering
     * 2. Fetch questions with options using @EntityGraph for eager loading
     *
     * @param categoryId Category ID to filter by
     * @param count      Number of questions requested (capped at
     *                   MAX_QUESTIONS_PER_QUIZ)
     * @return List of random quiz questions from the category with options loaded
     */
    public List<QuizQuestion> generateQuizByCategory(Long categoryId, int count) {
        log.info("🎲 Generating quiz for category {} with {} questions", categoryId, count);

        if (categoryId == null) {
            throw new IllegalArgumentException(messages.get("quiz.category_required"));
        }

        // Validate category exists
        boolean categoryExists = categoryRepository.existsById(categoryId);
        if (!categoryExists) {
            log.error("❌ Category not found: {}", categoryId);
            throw new IllegalArgumentException(messages.get("quiz.category_not_found", categoryId));
        }

        // Validate and cap the count
        int actualCount = Math.min(Math.max(count, 1), MAX_QUESTIONS_PER_QUIZ);

        // Check if we have any active questions in this category
        Long totalQuestions = quizQuestionRepository.countByCategoryIdAndIsActiveTrue(categoryId);
        if (totalQuestions == null || totalQuestions == 0) {
            log.warn("⚠️ No active questions available for category {}", categoryId);
            return Collections.emptyList();
        }

        // Adjust count if requested more than available
        actualCount = (int) Math.min(actualCount, totalQuestions);

        // Step 1: Get random question IDs by category using database-native random ordering
        List<Long> questionIds = quizQuestionRepository.findRandomQuestionIdsByCategory(
                categoryId,
                actualCount);

        // If no questions found, return empty list
        if (questionIds.isEmpty()) {
            log.warn("⚠️ No question IDs returned for category {}", categoryId);
            return Collections.emptyList();
        }

        log.debug("📋 Retrieved {} random question IDs for category {}", questionIds.size(), categoryId);

        // Step 2: Fetch every association required by the response mapper.
        List<QuizQuestion> questions = quizQuestionRepository.findAllByIdWithOptionsAndCategory(questionIds);

        // Filter out questions whose options are placeholder / corrupted content
        List<QuizQuestion> validQuestions = questions.stream()
                .filter(this::hasMinValidOptions)
                .collect(Collectors.toList());
        if (validQuestions.size() < questions.size()) {
            log.warn("⚠️ Category quiz: {} question(s) excluded — insufficient valid (non-placeholder) options",
                    questions.size() - validQuestions.size());
        }

        log.info("✅ Generated quiz with {} questions from category {} (options and categories eagerly loaded)",
                validQuestions.size(), categoryId);
        return validQuestions;
    }

    /**
     * Get total count of active + PUBLISHED questions.
     * Only PUBLISHED questions are counted — drafts are invisible to the delivery
     * pool.
     *
     * @return Total number of deliverable quiz questions
     */
    public Long getTotalActiveQuestions() {
        Long count = quizQuestionRepository.countByIsActiveTrueAndStatus(QuizQuestion.QuestionStatus.PUBLISHED);
        return count != null ? count : 0L;
    }

    /**
     * Get count of active + PUBLISHED questions in a specific category.
     * Only PUBLISHED questions are counted — drafts are invisible to the delivery
     * pool.
     *
     * @param categoryId Category ID
     * @return Number of deliverable quiz questions in the category
     */
    public Long getActiveQuestionsByCategory(Long categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException(messages.get("quiz.category_required"));
        }
        Long count = quizQuestionRepository.countByCategoryIdAndIsActiveTrueAndStatus(
                categoryId, QuizQuestion.QuestionStatus.PUBLISHED);
        return count != null ? count : 0L;
    }

    /**
     * Validate Belgian compliance before creating/updating question.
     * Story D1: Enforce 2-3 options rule
     *
     * @param question Question to validate
     * @throws BelgianComplianceException if validation fails
     */
    public void validateBelgianCompliance(QuizQuestion question) {
        if (question.getOptions() == null || question.getOptions().isEmpty()) {
            throw new BelgianComplianceException(messages.get("quiz.compliance.options_required"));
        }

        int optionCount = question.getDeliverableOptions().size();
        int expectedCount = question.getExpectedOptionCount();
        if (optionCount < 2 || optionCount > 3) {
            throw new BelgianComplianceException(
                    messages.get("quiz.compliance.options_range", optionCount));
        }
        if (question.getDifficultyLevel() == QuizQuestion.DifficultyLevel.HARD && optionCount != expectedCount) {
            throw new BelgianComplianceException(
                    messages.get("quiz.compliance.hard_exact", expectedCount, optionCount));
        }

        log.debug("✅ Question {} validated: {} options (compliant)",
                question.getId(), optionCount);
    }

    /**
     * Check if question is compliant with Belgian standards.
     * Story D1: Used for filtering during exam generation
     *
     * @param question Question to check
     * @return true if compliant, false otherwise
     */
    public boolean isCompliantQuestion(QuizQuestion question) {
        if (question.getOptions() == null) {
            return false;
        }
        int count = question.getDeliverableOptions().size();
        return count >= 2 && count <= 3
                && (question.getDifficultyLevel() != QuizQuestion.DifficultyLevel.HARD || count == 2);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Belgian Theory Exam (practice/random) — stateless, no DB session
    // Distribution: 20 EASY + 20 MEDIUM + 10 HARD = 50 questions
    // ─────────────────────────────────────────────────────────────────────────

    private static final int THEORY_EASY_COUNT = 20;
    private static final int THEORY_MEDIUM_COUNT = 20;
    private static final int THEORY_HARD_COUNT = 10;
    private static final int THEORY_PASSING_SCORE = 41;

    /**
     * Return 50 randomly selected questions (20E+20M+10H) for a Belgian theory exam
     * practice session. Shuffled so difficulty distribution is not predictable.
     * Uses two-step database-native random ordering to limit entity hydration.
     */
    public List<QuizQuestion> getTheoryExamQuestions() {
        List<Long> easyIds = quizQuestionRepository.findRandomQuestionIdsByDifficulty("EASY", THEORY_EASY_COUNT);
        List<Long> mediumIds = quizQuestionRepository.findRandomQuestionIdsByDifficulty("MEDIUM", THEORY_MEDIUM_COUNT);
        List<Long> hardIds = quizQuestionRepository.findRandomQuestionIdsByDifficulty("HARD", THEORY_HARD_COUNT);

        if (easyIds.size() < THEORY_EASY_COUNT)
            log.warn("⚠️ Theory exam: only {} EASY questions available (need {})", easyIds.size(), THEORY_EASY_COUNT);
        if (mediumIds.size() < THEORY_MEDIUM_COUNT)
            log.warn("⚠️ Theory exam: only {} MEDIUM questions available (need {})", mediumIds.size(),
                    THEORY_MEDIUM_COUNT);
        if (hardIds.size() < THEORY_HARD_COUNT)
            log.warn("⚠️ Theory exam: only {} HARD questions available (need {})", hardIds.size(), THEORY_HARD_COUNT);

        List<Long> allIds = new ArrayList<>();
        allIds.addAll(easyIds);
        allIds.addAll(mediumIds);
        allIds.addAll(hardIds);
        Collections.shuffle(allIds);

        if (allIds.isEmpty()) {
            log.error("❌ No questions available for theory exam");
            return Collections.emptyList();
        }

        List<QuizQuestion> questions = quizQuestionRepository.findAllByIdWithOptions(allIds);

        // Filter out questions whose options are placeholder / corrupted content
        List<QuizQuestion> validQuestions = questions.stream()
                .filter(this::hasMinValidOptions)
                .collect(Collectors.toList());
        if (validQuestions.size() < questions.size()) {
            log.warn("⚠️ Theory exam: {} question(s) excluded — insufficient valid (non-placeholder) options",
                    questions.size() - validQuestions.size());
        }

        log.info("✅ Theory exam prepared: {} questions ({}E / {}M / {}H)",
                validQuestions.size(), easyIds.size(), mediumIds.size(), hardIds.size());
        return validQuestions;
    }

    /**
     * Returns {@code true} if the question has at least 2 options whose text is
     * free of placeholder or corrupted content across all four language fields.
     * Questions failing this check are excluded from practice and exam pools.
     */
    private boolean hasMinValidOptions(QuizQuestion question) {
        if (question.getOptions() == null) {
            return false;
        }
        long validCount = question.getDeliverableOptions().stream()
                .filter(option -> !PlaceholderDetector.hasPlaceholder(
                        option.getOptionTextEn(), option.getOptionTextNl(),
                        option.getOptionTextFr(), option.getOptionTextAr()))
                .count();
        if (validCount < 2) {
            log.warn("⚠️ Question {} excluded from pool: only {} valid option(s) after placeholder check",
                    question.getId(), validCount);
        }
        return validCount >= 2;
    }

    /**
     * Stateless check of a completed theory exam.
     * Looks up each question's correct option and compares to the submitted answer.
     * Does NOT record history, update streaks, or write to the DB.
     *
     * @param answers List of {questionId, selectedOptionId} — selectedOptionId null
     *                means timeout
     * @return Full result DTO including per-question breakdown
     */
    @Transactional(readOnly = true)
    public TheoryExamResultDTO checkTheoryExamAnswers(List<TheoryExamAnswerRequest> answers) {
        if (answers == null || answers.isEmpty()) {
            return TheoryExamResultDTO.builder()
                    .totalQuestions(0).correctAnswers(0).wrongAnswers(0).unanswered(0)
                    .scorePercentage(0).passed(false).passingScore(THEORY_PASSING_SCORE)
                    .questions(Collections.emptyList()).build();
        }

        // Fetch all 50 questions with options + category in 1 query
        List<Long> questionIds = answers.stream()
                .map(TheoryExamAnswerRequest::getQuestionId)
                .collect(Collectors.toList());
        List<QuizQuestion> questions = quizQuestionRepository.findAllByIdWithOptionsAndCategory(questionIds);
        Map<Long, QuizQuestion> questionMap = questions.stream()
                .collect(Collectors.toMap(QuizQuestion::getId, q -> q));

        int correct = 0, wrong = 0, unanswered = 0;
        List<TheoryExamQuestionResultDTO> results = new ArrayList<>();

        for (TheoryExamAnswerRequest answer : answers) {
            QuizQuestion q = questionMap.get(answer.getQuestionId());
            if (q == null)
                continue;

            // Find the correct option
            var correctOption = q.getDeliverableOptions().stream()
                    .filter(o -> Boolean.TRUE.equals(o.getIsCorrect()))
                    .findFirst().orElse(null);

            boolean isTimeout = answer.getSelectedOptionId() == null;
            boolean isCorrect = !isTimeout && correctOption != null
                    && answer.getSelectedOptionId().equals(correctOption.getId());

            if (isTimeout)
                unanswered++;
            else if (isCorrect)
                correct++;
            else
                wrong++;

            // Category names (eagerly loaded above)
            String catEn = null, catAr = null, catNl = null, catFr = null;
            if (q.getCategory() != null) {
                catEn = q.getCategory().getNameEn();
                catAr = q.getCategory().getNameAr();
                catNl = q.getCategory().getNameNl();
                catFr = q.getCategory().getNameFr();
            }

            results.add(TheoryExamQuestionResultDTO.builder()
                    .questionId(q.getId())
                    .questionEn(roadSignReferenceTextResolver.resolveEn(q.getQuestionEn()))
                    .questionAr(roadSignReferenceTextResolver.resolveAr(q.getQuestionAr()))
                    .questionNl(roadSignReferenceTextResolver.resolveNl(q.getQuestionNl()))
                    .questionFr(roadSignReferenceTextResolver.resolveFr(q.getQuestionFr()))
                    .selectedOptionId(answer.getSelectedOptionId())
                    .correctOptionId(correctOption != null ? correctOption.getId() : null)
                    .correctOptionEn(correctOption != null ? roadSignReferenceTextResolver.resolveEn(correctOption.getOptionTextEn()) : null)
                    .correctOptionAr(correctOption != null ? roadSignReferenceTextResolver.resolveAr(correctOption.getOptionTextAr()) : null)
                    .correctOptionNl(correctOption != null ? roadSignReferenceTextResolver.resolveNl(correctOption.getOptionTextNl()) : null)
                    .correctOptionFr(correctOption != null ? roadSignReferenceTextResolver.resolveFr(correctOption.getOptionTextFr()) : null)
                    .isCorrect(isCorrect)
                    .wasTimeout(isTimeout)
                    .categoryNameEn(catEn)
                    .categoryNameAr(catAr)
                    .categoryNameNl(catNl)
                    .categoryNameFr(catFr)
                    .difficultyLevel(q.getDifficultyLevel() != null ? q.getDifficultyLevel().name() : null)
                    .build());
        }

        double pct = answers.size() > 0 ? (correct * 100.0 / answers.size()) : 0;
        return TheoryExamResultDTO.builder()
                .totalQuestions(answers.size())
                .correctAnswers(correct)
                .wrongAnswers(wrong)
                .unanswered(unanswered)
                .scorePercentage(Math.round(pct * 10.0) / 10.0)
                .passed(correct >= THEORY_PASSING_SCORE)
                .passingScore(THEORY_PASSING_SCORE)
                .questions(results)
                .build();
    }
}
