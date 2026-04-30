package com.readyroad.readyroadbackend.dto;

import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for Story B2: View Overall Progress
 * Response containing user's overall learning progress metrics
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OverallProgressResponse {

    /**
     * Total number of questions attempted across all categories
     */
    private Integer totalAttempted;

    /**
     * Total number of questions answered correctly
     */
    private Integer totalCorrect;

    /**
     * Overall accuracy percentage (0-100)
     */
    private BigDecimal overallAccuracy;

    /**
     * Categories where user is struggling (<70% accuracy, ≥5 attempts)
     * Sorted by lowest accuracy first
     */
    private List<CategoryProgressSummary> weakCategories;

    /**
     * Categories where user excels (>85% accuracy, ≥5 attempts)
     * Sorted by highest accuracy first
     */
    private List<CategoryProgressSummary> strongCategories;

    /**
     * Number of questions remaining to reach 500 questions goal
     */
    private Integer questionsRemaining;

    /**
     * Consecutive days of study activity (real consecutive-day streak)
     */
    private Integer studyStreak;

    /**
     * Recommended difficulty level based on performance
     */
    private QuizQuestion.DifficultyLevel recommendedDifficulty;

    /**
     * Top 3 most-studied categories by questions attempted (descending).
     * Used by the dashboard "Most Studied" widget.
     */
    private List<CategoryProgressSummary> mostStudiedCategories;

    /**
     * Date of the user's last practice activity in ISO format (yyyy-MM-dd).
     * null if the user has never answered any question.
     */
    private String lastActivityDate;

    /**
     * Total number of completed exam simulations taken by the user.
     */
    private Integer totalExamsTaken;

    /**
     * Number of completed exams where the user scored ≥82% (pass threshold).
     */
    private Integer passedExams;

    /**
     * Number of completed exams where the user scored <82%.
     */
    private Integer failedExams;

    /**
     * Percentage of exams that were passed (0-100).
     * 0.0 when no exams have been taken.
     */
    private BigDecimal passRate;

    /**
     * Total number of completed sign practice sessions.
     */
    private Integer signPracticeCount;

    /**
     * Total number of sign exam submissions.
     */
    private Integer signExamCount;

    /**
     * Number of unique signs where the user has passed exam 1.
     */
    private Integer signPassedCount;

    /**
     * Total number of completed mixed traffic-sign exam sessions from
     * /practice/random.
     */
    private Integer signRandomExamCount;

    /**
     * Number of passed mixed traffic-sign exam sessions from /practice/random.
     */
    private Integer signRandomExamPassedCount;

    /**
     * Number of lessons the user has started reading.
     */
    private Integer lessonsStartedCount;

    /**
     * Number of lessons the user has completed.
     */
    private Integer lessonsCompletedCount;

    /**
     * Number of currently unfinished learning activities across the dashboard.
     * This includes:
     * - active theory exams
     * - in-progress sign practice sessions
     * - active mixed traffic-sign exams
     */
    private Integer incompleteActivitiesCount;

    /**
     * Number of active theory exams currently in progress.
     */
    private Integer activeTheoryExamCount;

    /**
     * Number of in-progress sign practice sessions.
     */
    private Integer incompleteSignPracticeCount;

    /**
     * Number of active mixed traffic-sign exam sessions.
     */
    private Integer activeRandomSignExamCount;

    /**
     * Weakest traffic-sign level entries for the user.
     * This complements category-level weak areas by surfacing specific signs
     * (for example: A37) that still need improvement.
     */
    private List<SignWeaknessSummary> weakSigns;

    /**
     * Summary of progress for a specific category.
     * Used in weakCategories, strongCategories, and mostStudiedCategories lists.
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryProgressSummary {

        /**
         * Category name in English
         */
        private String categoryName;

        /**
         * Short category code (e.g. "A", "B", "C")
         */
        private String categoryCode;

        /**
         * Accuracy percentage for this category (0-100)
         */
        private BigDecimal accuracy;

        /**
         * Number of questions attempted in this category
         */
        private Integer attempted;
    }

    /**
     * Sign-level weakness summary used in the dashboard.
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SignWeaknessSummary {

        /** Canonical sign code, e.g. A37 */
        private String signCode;

        /** Localized names for direct dashboard rendering */
        private String signNameEn;
        private String signNameNl;
        private String signNameFr;
        private String signNameAr;

        /** Accuracy percentage for this sign (0-100) */
        private BigDecimal accuracy;

        /** Number of answered questions accumulated for this sign */
        private Integer attempted;

        /** Number of wrong answers for this sign */
        private Integer wrongAnswers;
    }
}
