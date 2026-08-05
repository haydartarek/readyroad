package com.readyroad.readyroadbackend.dto;

import com.readyroad.readyroadbackend.domain.entity.QuizQuestion.TypicalErrorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Story C1: View Error Patterns - Response DTO
 * Returns analytics about user's mistake patterns
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorPatternResponse {

    /**
     * Type of error pattern (e.g., SIGN_CONFUSION, SPEED_LIMIT_ERROR)
     */
    private TypicalErrorType patternType;

    /**
     * Number of times this error pattern occurred
     */
    private Integer count;

    private Integer previousCount;
    private Integer currentCount;
    private Integer delta;
    private String trend;
    private Integer recentAttemptsCount;
    private LocalDateTime lastCalculatedAt;

    /**
     * Percentage of this pattern among all errors (0-100)
     */
    private Double percentage;

    /**
     * Human-readable description of this error pattern
     */
    private String description;

    /**
     * Severity derived from frequency and share of the user's complete history.
     */
    private String severity;

    /**
     * Number of different questions/sign references involved in this pattern.
     */
    private Integer uniqueQuestions;

    private LocalDateTime firstOccurredAt;
    private LocalDateTime lastOccurredAt;

    /**
     * Stable frontend translation key for the personalized recommendation.
     */
    private String recommendationKey;

    /**
     * Makes the analytical scope explicit to clients.
     */
    private String sourceScope;

    /**
     * Complete-history breakdowns that explain where a pattern occurs.
     */
    private List<ErrorGroupDTO> groups;

    /**
     * List of example questions where user made this type of error
     */
    private List<ExampleQuestionDTO> exampleQuestions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorGroupDTO {

        /**
         * CATEGORY, TRAFFIC_SIGN_FAMILY, LEGAL_CONCEPT, or
         * REPEATED_MISCONCEPTION.
         */
        private String groupType;

        /**
         * Stable category, sign-family, or error-concept identifier.
         */
        private String code;

        private String nameEn;
        private String nameAr;
        private String nameNl;
        private String nameFr;

        /**
         * Number of historical wrong answers represented by this group.
         */
        private Integer count;
    }

    /**
     * Nested DTO for example questions
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExampleQuestionDTO {

        /**
         * Question ID
         */
        private Long questionId;

        /**
         * Question text in English
         */
        private String questionTextEn;

        /**
         * Question text in Arabic
         */
        private String questionTextAr;

        /**
         * Question text in Dutch
         */
        private String questionTextNl;

        /**
         * Question text in French
         */
        private String questionTextFr;

        /**
         * Category name (e.g., "Speed Limits")
         */
        private String categoryName;
        private String categoryNameEn;
        private String categoryNameAr;
        private String categoryNameNl;
        private String categoryNameFr;

        /**
         * URL to content image (traffic sign, etc.)
         */
        private String contentImageUrl;

        /**
         * Number of times user got this question wrong
         */
        private Integer timesWrong;
    }
}
