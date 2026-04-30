package com.readyroad.readyroadbackend.util;

import com.readyroad.readyroadbackend.domain.enums.SignQuestionType;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Sanitizes user-facing text for sign questions.
 *
 * <p>For {@code WHICH_SIGN} questions we intentionally remove legacy category
 * markers such as "(danger signs)" or "(الفئة أ)" so the learner sees only the
 * category name.</p>
 */
public final class SignQuestionTextSanitizer {

    private static final List<Pattern> WHICH_SIGN_CATEGORY_MARKER_PATTERNS = List.of(
            Pattern.compile("\\s*\\([A-Z]+-se" + "ries\\)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\s*\\([A-Z]+-re" + "eks\\)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\s*\\((?:s\u00E9rie|serie|sÃ©rie)\\s*[A-Z]+\\)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\s*\\((?:الفئة|سل" + "سلة)\\s*[A-Zأ-ي]+\\)")
    );

    private SignQuestionTextSanitizer() {
    }

    public static String sanitizeQuestion(SignQuestionType questionType, String languageCode, String value) {
        return sanitize(questionType, languageCode, value);
    }

    public static String sanitizeChoice(SignQuestionType questionType, String value) {
        return sanitize(questionType, null, value);
    }

    public static String sanitizeChoice(SignQuestionType questionType, String languageCode, String value) {
        return sanitize(questionType, languageCode, value);
    }

    public static String sanitizeExplanation(SignQuestionType questionType, String value) {
        return sanitize(questionType, null, value);
    }

    public static String sanitizeExplanation(SignQuestionType questionType, String languageCode, String value) {
        return sanitize(questionType, languageCode, value);
    }

    private static String sanitize(SignQuestionType questionType, String languageCode, String value) {
        String normalized = DrivingTextSanitizer.sanitize(languageCode, value);
        if (normalized == null || questionType != SignQuestionType.WHICH_SIGN) {
            return normalized;
        }

        String sanitized = normalized;
        for (Pattern pattern : WHICH_SIGN_CATEGORY_MARKER_PATTERNS) {
            sanitized = pattern.matcher(sanitized).replaceAll("");
        }

        return sanitized
                .replaceAll("\\s{2,}", " ")
                .replaceAll("\\s+([.,;:!?])", "$1")
                .trim();
    }
}
