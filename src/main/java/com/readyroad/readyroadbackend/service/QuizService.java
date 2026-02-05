package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.exception.BelgianComplianceException;
import com.readyroad.readyroadbackend.exception.TranslationRequiredException;
import com.readyroad.readyroadbackend.validation.TranslationValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * QuizService - Basic Quiz Generation
 *
 * **Phase 2 Restoration:** Implemented January 18, 2026
 * **Fixed:** February 5, 2026 - LazyInitializationException resolved
 *
 * Provides basic quiz generation functionality WITHOUT Smart Quiz features:
 * - ✅ Random question selection (two-step approach with @EntityGraph)
 * - ✅ Category filtering
 * - ✅ Belgian compliance validation
 * - ❌ NO 24-hour cooldown (deferred to SmartQuizService)
 * - ❌ NO user history tracking
 * - ❌ NO adaptive difficulty
 * - ❌ NO error pattern analysis
 *
 * For Smart Quiz features, see SmartQuizService (Phase 3).
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class QuizService {

    private final QuizQuestionRepository quizQuestionRepository;

    /**
     * Maximum questions allowed per quiz to prevent abuse
     */
    private static final int MAX_QUESTIONS_PER_QUIZ = 50;

    /**
     * Generate a random quiz with specified number of questions
     * 
     * **Fix Applied:** Uses two-step approach to prevent LazyInitializationException:
     * 1. Get random question IDs using native query with RAND()
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

        // Step 1: Get random question IDs (fast native query with RAND())
        List<Long> questionIds = quizQuestionRepository.findRandomQuestionIds(actualCount);

        // If no questions found, return empty list
        if (questionIds.isEmpty()) {
            log.warn("⚠️ No question IDs returned from random query");
            return Collections.emptyList();
        }

        log.debug("📋 Retrieved {} random question IDs", questionIds.size());

        // Step 2: Fetch full questions with options (eager loading with @EntityGraph)
        List<QuizQuestion> questions = quizQuestionRepository.findAllByIdWithOptions(questionIds);

        log.info("✅ Generated random quiz with {} questions (options eagerly loaded)", questions.size());
        return questions;
    }

    /**
     * Generate a quiz with questions from a specific category
     * 
     * **Fix Applied:** Uses two-step approach to prevent LazyInitializationException:
     * 1. Get random question IDs by category using native query with RAND()
     * 2. Fetch questions with options using @EntityGraph for eager loading
     *
     * @param categoryId Category ID to filter by
     * @param count Number of questions requested (capped at MAX_QUESTIONS_PER_QUIZ)
     * @return List of random quiz questions from the category with options loaded
     */
    public List<QuizQuestion> generateQuizByCategory(Long categoryId, int count) {
        log.info("🎲 Generating quiz for category {} with {} questions", categoryId, count);
        
        if (categoryId == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
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

        // Step 1: Get random question IDs by category (fast native query with RAND())
        List<Long> questionIds = quizQuestionRepository.findRandomQuestionIdsByCategory(
            categoryId,
            actualCount
        );

        // If no questions found, return empty list
        if (questionIds.isEmpty()) {
            log.warn("⚠️ No question IDs returned for category {}", categoryId);
            return Collections.emptyList();
        }

        log.debug("📋 Retrieved {} random question IDs for category {}", questionIds.size(), categoryId);

        // Step 2: Fetch full questions with options (eager loading with @EntityGraph)
        List<QuizQuestion> questions = quizQuestionRepository.findAllByIdWithOptions(questionIds);

        log.info("✅ Generated quiz with {} questions from category {} (options eagerly loaded)", 
                 questions.size(), categoryId);
        return questions;
    }

    /**
     * Get total count of active questions
     *
     * @return Total number of active quiz questions
     */
    public Long getTotalActiveQuestions() {
        Long count = quizQuestionRepository.countByIsActiveTrue();
        return count != null ? count : 0L;
    }

    /**
     * Get count of active questions in a specific category
     *
     * @param categoryId Category ID
     * @return Number of active quiz questions in the category
     */
    public Long getActiveQuestionsByCategory(Long categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }
        Long count = quizQuestionRepository.countByCategoryIdAndIsActiveTrue(categoryId);
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
            throw new BelgianComplianceException("Question must have options");
        }

        int optionCount = question.getOptions().size();
        if (optionCount < 2 || optionCount > 3) {
            throw new BelgianComplianceException(
                String.format("Belgian standard requires 2-3 options only. Found: %d", optionCount)
            );
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
        int count = question.getOptions().size();
        return count >= 2 && count <= 3;
    }

    /**
     * Publish a question - Story D2
     *
     * Requirements before publication:
     * 1. Must have 2-3 options (Story D1)
     * 2. Must have NL translation (Story D2)
     * 3. Must have FR translation (Story D2)
     *
     * @param questionId Question ID to publish
     * @throws IllegalArgumentException if question not found
     * @throws BelgianComplianceException if options count invalid
     * @throws TranslationRequiredException if translations missing
     */
    @Transactional
    public void publishQuestion(Long questionId) {
        QuizQuestion question = quizQuestionRepository.findById(questionId)
            .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));

        // Story D1: Validate Belgian compliance (2-3 options)
        validateBelgianCompliance(question);

        // Story D2: Validate required translations (NL + FR)
        TranslationValidator.validatePublicationRequirements(question);

        // Mark as active (published)
        question.setIsActive(true);
        quizQuestionRepository.save(question);

        log.info("✅ Question {} published successfully (NL/FR verified, {} options)",
            questionId, question.getOptions().size());
    }

    /**
     * Unpublish a question (mark as draft).
     *
     * @param questionId Question ID to unpublish
     */
    @Transactional
    public void unpublishQuestion(Long questionId) {
        QuizQuestion question = quizQuestionRepository.findById(questionId)
            .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));

        question.setIsActive(false);
        quizQuestionRepository.save(question);

        log.info("ℹ️ Question {} unpublished (marked as draft)", questionId);
    }
}
