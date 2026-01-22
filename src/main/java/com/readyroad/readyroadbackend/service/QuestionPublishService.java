package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.exception.BelgianComplianceException;
import com.readyroad.readyroadbackend.exception.QuestionNotFoundException;
import com.readyroad.readyroadbackend.validation.PublishValidation;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Question Publishing Service - Story D4
 *
 * Enforces Belgian compliance at publish time:
 * - 2-3 answer options (Story D1)
 * - NL and FR translations (Story D2)
 * - Traffic sign context (Story D3)
 * - Published questions are immutable
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionPublishService {

    private final QuizQuestionRepository questionRepository;
    private final Validator validator;

    /**
     * Publish a question with full Belgian compliance validation
     *
     * @param questionId The question to publish
     * @throws QuestionNotFoundException if question doesn't exist
     * @throws BelgianComplianceException if validation fails
     * @throws IllegalStateException if question is already published
     */
    @Transactional
    public void publishQuestion(Long questionId) {
        log.info("[D4] Publishing question {}", questionId);

        // Load question
        QuizQuestion question = questionRepository.findById(questionId)
            .orElseThrow(() -> new QuestionNotFoundException(questionId));

        // Check if already published
        if (question.getStatus() == QuizQuestion.QuestionStatus.PUBLISHED) {
            throw new IllegalStateException(
                String.format("Question %d is already published and cannot be re-published", questionId)
            );
        }

        // Manual validation for NL/FR translations (Story D2)
        List<String> validationErrors = new ArrayList<>();

        if (question.getQuestionNl() == null || question.getQuestionNl().isBlank()) {
            validationErrors.add("NL translation required for Belgian compliance");
        }

        if (question.getQuestionFr() == null || question.getQuestionFr().isBlank()) {
            validationErrors.add("FR translation required for Belgian compliance");
        }

        // Validate Belgian compliance using PublishValidation group
        Set<ConstraintViolation<QuizQuestion>> violations =
            validator.validate(question, PublishValidation.class);

        if (!violations.isEmpty()) {
            validationErrors.addAll(violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList()));
        }

        if (!validationErrors.isEmpty()) {
            String errors = String.join("; ", validationErrors);
            log.warn("[D4] Question {} failed Belgian compliance: {}", questionId, errors);
            throw new BelgianComplianceException(
                String.format("Cannot publish question %d: %s", questionId, errors)
            );
        }

        // Mark as published
        question.setStatus(QuizQuestion.QuestionStatus.PUBLISHED);
        question.setPublishedAt(LocalDateTime.now());
        questionRepository.save(question);

        log.info("[D4] Question {} published successfully", questionId);
    }

    /**
     * Check if a question can be published (dry-run validation)
     *
     * @param questionId The question to check
     * @return true if publishable, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean canPublish(Long questionId) {
        QuizQuestion question = questionRepository.findById(questionId)
            .orElseThrow(() -> new QuestionNotFoundException(questionId));

        if (question.getStatus() == QuizQuestion.QuestionStatus.PUBLISHED) {
            return false;
        }

        Set<ConstraintViolation<QuizQuestion>> violations =
            validator.validate(question, PublishValidation.class);

        return violations.isEmpty();
    }

    /**
     * Get validation errors for a question without publishing
     *
     * @param questionId The question to validate
     * @return Set of validation error messages
     */
    @Transactional(readOnly = true)
    public Set<String> getPublishValidationErrors(Long questionId) {
        QuizQuestion question = questionRepository.findById(questionId)
            .orElseThrow(() -> new QuestionNotFoundException(questionId));

        Set<String> errors = new java.util.HashSet<>();

        // Manual NL/FR checks (Story D2)
        if (question.getQuestionNl() == null || question.getQuestionNl().isBlank()) {
            errors.add("NL translation required for Belgian compliance");
        }

        if (question.getQuestionFr() == null || question.getQuestionFr().isBlank()) {
            errors.add("FR translation required for Belgian compliance");
        }

        // Validation group checks (Stories D1, D3)
        Set<ConstraintViolation<QuizQuestion>> violations =
            validator.validate(question, PublishValidation.class);

        errors.addAll(violations.stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.toSet()));

        return errors;
    }
}
