package com.readyroad.readyroadbackend.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Current THEORY bank exposure and answer metrics for one learner. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TheoryQuestionCoverageResponse {

    private String languageCode;
    private Long eligibleQuestions;
    private Long uniqueQuestionsSeen;
    private Long uniqueQuestionsAnswered;
    private Long unseenQuestions;
    private BigDecimal coveragePercentage;
    private Long timesPresented;
    private Long timesAnswered;
    private Long timesCorrect;
    private Long timesIncorrect;
    private BigDecimal accuracyPercentage;
    private String confidenceState;
    private List<CategoryCoverage> categories;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryCoverage {

        private Long categoryId;
        private String categoryCode;
        private String categoryName;
        private Long eligibleQuestions;
        private Long uniqueQuestionsSeen;
        private Long uniqueQuestionsAnswered;
        private Long unseenQuestions;
        private BigDecimal coveragePercentage;
        private Long timesPresented;
        private Long timesAnswered;
        private Long timesCorrect;
        private Long timesIncorrect;
        private BigDecimal accuracyPercentage;
        private String confidenceState;
    }
}
