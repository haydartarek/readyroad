package com.readyroad.readyroadbackend.validation;

import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.exception.TranslationRequiredException;

import java.util.ArrayList;
import java.util.List;

/**
 * Translation Validator - Story D2
 *
 * Validates that quiz questions have required NL and FR translations
 * before publication (Belgian legal requirement).
 */
public class TranslationValidator {

    /**
     * Validate that question has required translations for publication.
     *
     * Belgian law requires:
     * - NL (Dutch) translation - mandatory
     * - FR (French) translation - mandatory
     * - EN (English) - optional but recommended
     * - AR (Arabic) - optional
     *
     * @param question Question to validate
     * @throws TranslationRequiredException if required translations are missing
     */
    public static void validatePublicationRequirements(QuizQuestion question) {
        List<String> errors = new ArrayList<>();

        // Check NL translation
        if (question.getQuestionNl() == null || question.getQuestionNl().isBlank()) {
            errors.add("NL (Dutch) translation is required for publication");
        }

        // Check FR translation
        if (question.getQuestionFr() == null || question.getQuestionFr().isBlank()) {
            errors.add("FR (French) translation is required for publication");
        }

        if (!errors.isEmpty()) {
            throw new TranslationRequiredException(
                "Missing required translations: " + String.join(", ", errors)
            );
        }
    }

    /**
     * Check if question has all required translations.
     *
     * @param question Question to check
     * @return true if all required translations present, false otherwise
     */
    public static boolean hasRequiredTranslations(QuizQuestion question) {
        return question.getQuestionNl() != null && !question.getQuestionNl().isBlank()
            && question.getQuestionFr() != null && !question.getQuestionFr().isBlank();
    }
}
