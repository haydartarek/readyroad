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

import java.time.LocalDateTime;
import java.util.List;

/**
 * QuizService - Basic Quiz Generation
 *
 * **Phase 2 Restoration:** Implemented January 18, 2026
 *
 * Provides basic quiz generation functionality WITHOUT Smart Quiz features:
 * - ✅ Random question selection
 * - ✅ Category filtering
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
     * @param count Number of questions requested (capped at MAX_QUESTIONS_PER_QUIZ)
     * @return List of random quiz questions with options loaded
     */
    public List<QuizQuestion> generateRandomQuiz(int count) {
        // Validate and cap the count
        int actualCount = Math.min(Math.max(count, 1), MAX_QUESTIONS_PER_QUIZ);

        // Fetch random questions using native query with LIMIT
        // This prevents loading all questions into memory
        return quizQuestionRepository.findRandomQuestionsWithOptionsNative(actualCount);
    }

    /**
     * Generate a quiz with questions from a specific category
     *
     * @param categoryId Category ID to filter by
     * @param count Number of questions requested (capped at MAX_QUESTIONS_PER_QUIZ)
     * @return List of random quiz questions from the category with options loaded
     */
    public List<QuizQuestion> generateQuizByCategory(Long categoryId, int count) {
        if (categoryId == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }

        // Validate and cap the count
        int actualCount = Math.min(Math.max(count, 1), MAX_QUESTIONS_PER_QUIZ);

        // Fetch random questions from category using native query with LIMIT
        return quizQuestionRepository.findRandomQuestionsByCategoryWithOptionsNative(
            categoryId,
            actualCount
        );
    }

    /**
     * Get total count of active questions
     *
     * @return Total number of active quiz questions
     */
    public Long getTotalActiveQuestions() {
        return quizQuestionRepository.countByIsActiveTrue();
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
        return quizQuestionRepository.countByCategoryIdAndIsActiveTrue(categoryId);
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

        log.debug("Question {} validated: {} options (compliant)",
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

        log.info("Question {} unpublished (marked as draft)", questionId);
    }
}
