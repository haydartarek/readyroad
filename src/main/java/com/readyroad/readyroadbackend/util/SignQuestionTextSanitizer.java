package com.readyroad.readyroadbackend.util;

import com.readyroad.readyroadbackend.domain.enums.SignQuestionType;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Sanitizes user-facing text for sign questions.
 *
 * <p>For {@code WHICH_SIGN} questions we intentionally remove legacy series
 * wording such as "(A-series)" or "(سلسلة A)" so the learner sees only the
 * category name.</p>
 */
public final class SignQuestionTextSanitizer {

    private static final List<Pattern> WHICH_SIGN_SERIES_PATTERNS = List.of(
            Pattern.compile("\\s*\\([A-Z]+-series\\)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\s*\\([A-Z]+-reeks\\)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\s*\\((?:série|serie|sÃ©rie)\\s*[A-Z]+\\)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\s*\\(سلسلة\\s*[A-Z]+\\)")
    );

    private SignQuestionTextSanitizer() {
    }

    public static String sanitizeChoice(SignQuestionType questionType, String value) {
        return sanitize(questionType, value);
    }

    public static String sanitizeExplanation(SignQuestionType questionType, String value) {
        return sanitize(questionType, value);
    }

    private static String sanitize(SignQuestionType questionType, String value) {
        String normalized = ImportedTextSanitizer.sanitize(value);
        if (normalized == null || questionType != SignQuestionType.WHICH_SIGN) {
            return normalized;
        }

        String sanitized = normalized;
        for (Pattern pattern : WHICH_SIGN_SERIES_PATTERNS) {
            sanitized = pattern.matcher(sanitized).replaceAll("");
        }

        return sanitized
                .replaceAll("\\s{2,}", " ")
                .replaceAll("\\s+([.,;:!?])", "$1")
                .trim();
    }
}
