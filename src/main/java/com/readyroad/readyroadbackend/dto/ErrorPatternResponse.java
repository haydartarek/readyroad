package com.readyroad.readyroadbackend.dto;

import com.readyroad.readyroadbackend.domain.entity.QuizQuestion.TypicalErrorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    /**
     * Percentage of this pattern among all errors (0-100)
     */
    private Double percentage;

    /**
     * Human-readable description of this error pattern
     */
    private String description;

    /**
     * List of example questions where user made this type of error
     */
    private List<ExampleQuestionDTO> exampleQuestions;

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
