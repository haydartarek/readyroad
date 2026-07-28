package com.readyroad.readyroadbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Read-only learning intelligence calculated from persisted user history.
 *
 * Nullable metric values mean that the stored evidence is insufficient for a
 * truthful calculation. Consumers must display an informational state instead
 * of replacing null with zero.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentIntelligenceResponse {

    private String dataStatus;
    private String studentLevel;
    private Integer examReadinessScore;
    private Integer confidenceScore;
    private Integer learningConsistencyScore;
    private Integer knowledgeRetentionScore;
    private Integer estimatedPassProbability;
    private BigDecimal weeklyProgress;
    private BigDecimal monthlyProgress;
    private String overallLearningTrend;
    private Integer totalLearningActivities;
    private Integer activeDaysLast28;
    private Integer evidenceQuestions;
    private ExamAnalytics examAnalytics;
    private TimingAnalytics timingAnalytics;
    private ProgressJourney progressJourney;
    private List<LearningPriority> learningPriorities;
    private List<LearningPriority> strongestCategories;
    private List<Recommendation> recommendations;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExamAnalytics {
        private Integer totalExams;
        private Integer completedExams;
        private Integer passedExams;
        private Integer failedExams;
        private BigDecimal passRate;
        private BigDecimal averageScore;
        private BigDecimal highestScore;
        private BigDecimal lowestScore;
        private Integer averageCompletionTimeSeconds;
        private Integer fastestCompletionTimeSeconds;
        private Integer slowestCompletionTimeSeconds;
        private BigDecimal scoreTrend;
        private BigDecimal passTrend;
        private List<BigDecimal> recentScores;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LearningPriority {
        private Long categoryId;
        private String categoryCode;
        private String categoryNameEn;
        private String categoryNameNl;
        private String categoryNameFr;
        private String categoryNameAr;
        private BigDecimal accuracy;
        private Integer questionsAttempted;
        private BigDecimal priorityScore;
        private Integer confidenceScore;
        private String trend;
        private BigDecimal trendChange;
        private Integer daysSincePractice;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimingAnalytics {
        private Integer averageAnswerTimeSeconds;
        private BigDecimal answerTimeTrendSeconds;
        private BigDecimal examTimeTrendSeconds;
        private Integer answerTimingSamples;
        private String answerTimingScope;
        private List<CategoryTiming> categoryTimings;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryTiming {
        private Long categoryId;
        private String categoryCode;
        private String categoryNameEn;
        private String categoryNameNl;
        private String categoryNameFr;
        private String categoryNameAr;
        private Integer averageAnswerTimeSeconds;
        private Integer samples;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProgressJourney {
        private Integer lessonsStarted;
        private Integer lessonsCompleted;
        private Integer lessonRevisitCount;
        private Integer currentStudyStreak;
        private Boolean activeToday;
        private Integer activeDaysLast7;
        private Integer activeDaysLast30;
        private Integer completedPracticeSessions;
        private Integer completedOfficialExams;
        private Integer masteredCategories;
        private Integer masteredSigns;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Recommendation {
        private String key;
        private String categoryCode;
        private String actionPath;
        private Integer priority;
    }
}
