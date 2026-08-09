package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.readyroad.readyroadbackend.dto.StudentIntelligenceResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class StudentIntelligenceEngineTest {

    private final StudentIntelligenceEngine engine = new StudentIntelligenceEngine();
    private final LocalDate today = LocalDate.of(2026, 7, 28);

    @Test
    void returnsExplicitInsufficientStateWithoutInventingScores() {
        StudentIntelligenceResponse result = engine.analyze(
                new StudentIntelligenceEngine.AnalyticsInput(
                        0,
                        List.of(),
                        List.of(),
                        List.of(),
                        Set.of(),
                        0,
                        0,
                        0,
                        0),
                today);

        assertThat(result.getDataStatus()).isEqualTo("NO_DATA");
        assertThat(result.getStudentLevel()).isEqualTo("BEGINNER");
        assertThat(result.getExamReadinessScore()).isNull();
        assertThat(result.getConfidenceScore()).isNull();
        assertThat(result.getEstimatedPassProbability()).isNull();
        assertThat(result.getKnowledgeRetentionScore()).isNull();
        assertThat(result.getWeeklyProgress()).isNull();
        assertThat(result.getMonthlyProgress()).isNull();
        assertThat(result.getOverallLearningTrend()).isEqualTo("INSUFFICIENT_DATA");
        assertThat(result.getExamAnalytics().getAverageScore()).isNull();
        assertThat(result.getExamAnalytics().getAverageCompletionTimeSeconds()).isNull();
        assertThat(result.getTimingAnalytics().getAverageAnswerTimeSeconds()).isNull();
        assertThat(result.getProgressJourney().getLessonRevisitCount()).isNull();
        assertThat(result.getRecommendations())
                .extracting(StudentIntelligenceResponse.Recommendation::getKey)
                .containsExactly("student_intelligence.recommendation.start_learning");
    }

    @Test
    void calculatesHistoricalExamAndLearningIntelligenceFromCompleteEvidence() {
        List<StudentIntelligenceEngine.ScoredActivity> activities = List.of(
                official(today.minusDays(50), 74, false, 1_500),
                official(today.minusDays(35), 80, false, null),
                official(today.minusDays(20), 84, true, 1_320),
                official(today.minusDays(8), 88, true, 1_200),
                official(today.minusDays(2), 92, true, 1_080),
                activity(today.minusDays(12), 76, false, "SIGN_PRACTICE"),
                activity(today.minusDays(5), 86, true, "SIGN_EXAM"),
                activity(today.minusDays(1), 90, true, "MIXED_SIGN_EXAM"));

        List<StudentIntelligenceEngine.CategoryEvidence> categories = List.of(
                category(1L, "PRIORITY", 50, 42, today.minusDays(1), 20, 18, 15, 10),
                category(2L, "PARKING", 40, 24, today.minusDays(3), 18, 9, 12, 9),
                category(3L, "DANGER", 30, 27, today.minusDays(2), 12, 11, 10, 8));

        List<StudentIntelligenceEngine.QuestionEvidence> questions = List.of(
                question(5, 4, today.minusDays(1), 18, 1L, "PRIORITY"),
                question(4, 3, today.minusDays(2), 24, 2L, "PARKING"),
                question(1, 1, today.minusDays(3), null, 3L, "DANGER"));

        Set<LocalDate> activeDates = Set.of(
                today,
                today.minusDays(1),
                today.minusDays(2),
                today.minusDays(4),
                today.minusDays(6),
                today.minusDays(8),
                today.minusDays(10),
                today.minusDays(13));

        StudentIntelligenceResponse result = engine.analyze(
                new StudentIntelligenceEngine.AnalyticsInput(
                        6,
                        activities,
                        categories,
                        questions,
                        activeDates,
                        6,
                        4,
                        3,
                        2),
                today);

        assertThat(result.getDataStatus()).isEqualTo("SUFFICIENT");
        assertThat(result.getStudentLevel()).isIn("ADVANCED", "EXAM_READY");
        assertThat(result.getExamReadinessScore()).isBetween(70, 100);
        assertThat(result.getConfidenceScore()).isBetween(60, 100);
        assertThat(result.getLearningConsistencyScore()).isEqualTo(67);
        assertThat(result.getKnowledgeRetentionScore()).isEqualTo(78);
        assertThat(result.getEstimatedPassProbability()).isBetween(70, 100);
        assertThat(result.getOverallLearningTrend()).isEqualTo("IMPROVING");

        assertThat(result.getExamAnalytics().getTotalExams()).isEqualTo(6);
        assertThat(result.getExamAnalytics().getCompletedExams()).isEqualTo(5);
        assertThat(result.getExamAnalytics().getPassedExams()).isEqualTo(3);
        assertThat(result.getExamAnalytics().getFailedExams()).isEqualTo(2);
        assertThat(result.getExamAnalytics().getAverageScore()).isEqualByComparingTo("83.60");
        assertThat(result.getExamAnalytics().getHighestScore()).isEqualByComparingTo("92.00");
        assertThat(result.getExamAnalytics().getLowestScore()).isEqualByComparingTo("74.00");
        assertThat(result.getExamAnalytics().getAverageCompletionTimeSeconds()).isEqualTo(1_275);
        assertThat(result.getExamAnalytics().getFastestCompletionTimeSeconds()).isEqualTo(1_080);
        assertThat(result.getExamAnalytics().getSlowestCompletionTimeSeconds()).isEqualTo(1_500);
        assertThat(result.getTimingAnalytics().getAverageAnswerTimeSeconds()).isEqualTo(21);
        assertThat(result.getTimingAnalytics().getAnswerTimingSamples()).isEqualTo(2);
        assertThat(result.getTimingAnalytics().getAnswerTimingScope())
                .isEqualTo("LATEST_RECORDED_PER_QUESTION");
        assertThat(result.getProgressJourney().getCurrentStudyStreak()).isEqualTo(3);
        assertThat(result.getProgressJourney().getCompletedPracticeSessions()).isEqualTo(3);
        assertThat(result.getProgressJourney().getMasteredSigns()).isEqualTo(2);

        assertThat(result.getLearningPriorities())
                .extracting(StudentIntelligenceResponse.LearningPriority::getCategoryCode)
                .containsExactly("PARKING");
        assertThat(result.getLearningPriorities().getFirst().getTrend()).isEqualTo("DECLINING");
        assertThat(result.getRecommendations())
                .extracting(StudentIntelligenceResponse.Recommendation::getKey)
                .contains("student_intelligence.recommendation.focus_weak_category");
        assertThat(result.getRecommendations())
                .filteredOn(recommendation ->
                        "student_intelligence.recommendation.focus_weak_category"
                                .equals(recommendation.getKey()))
                .extracting(StudentIntelligenceResponse.Recommendation::getActionPath)
                .containsExactly("/exam");
    }

    @Test
    void leavesProgressComparisonUnavailableWithoutBothTimeWindows() {
        StudentIntelligenceResponse result = engine.analyze(
                new StudentIntelligenceEngine.AnalyticsInput(
                        1,
                        List.of(official(today.minusDays(2), 86, true, null)),
                        List.of(category(1L, "PRIORITY", 25, 20, today, 0, 0, 0, 0)),
                        List.of(),
                        Set.of(today),
                        1,
                        0,
                        0,
                        0),
                today);

        assertThat(result.getWeeklyProgress()).isNull();
        assertThat(result.getMonthlyProgress()).isNull();
        assertThat(result.getExamAnalytics().getAverageCompletionTimeSeconds()).isNull();
        assertThat(result.getEstimatedPassProbability()).isNull();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("historicalPersonas")
    void keepsAllHistoricalPersonasCoherentAndWithinNumericBounds(
            String persona,
            StudentIntelligenceEngine.AnalyticsInput input,
            boolean expectsAnswerTiming) {
        StudentIntelligenceResponse result = engine.analyze(input, today);

        assertBounded(result.getExamReadinessScore());
        assertBounded(result.getConfidenceScore());
        assertBounded(result.getLearningConsistencyScore());
        assertBounded(result.getKnowledgeRetentionScore());
        assertBounded(result.getEstimatedPassProbability());
        assertThat(result.getEvidenceQuestions()).isGreaterThanOrEqualTo(0);
        assertThat(result.getTotalLearningActivities()).isGreaterThanOrEqualTo(0);
        assertThat(result.getActiveDaysLast28()).isBetween(0, 28);

        if (input.totalOfficialExams() <= 1) {
            assertThat(result.getEstimatedPassProbability())
                    .as("%s requires multiple exams for probability", persona)
                    .isNull();
        }
        if (result.getEvidenceQuestions() < 20) {
            assertThat(result.getStudentLevel())
                    .as("%s has insufficient evidence for an advanced label", persona)
                    .isEqualTo("BEGINNER");
            assertThat(result.getExamReadinessScore())
                    .as("%s has insufficient evidence for readiness", persona)
                    .isNull();
        }
        if (expectsAnswerTiming) {
            assertThat(result.getTimingAnalytics().getAverageAnswerTimeSeconds()).isPositive();
            assertThat(result.getTimingAnalytics().getAnswerTimingSamples()).isPositive();
        } else {
            assertThat(result.getTimingAnalytics().getAverageAnswerTimeSeconds()).isNull();
            assertThat(result.getTimingAnalytics().getAnswerTimingSamples()).isZero();
        }
        if ("learner with repeated failed exams".equals(persona)) {
            assertThat(result.getStudentLevel()).isNotIn("EXAM_READY", "EXPERT");
            assertThat(result.getExamAnalytics().getPassedExams()).isZero();
        }
        if ("steadily improving learner".equals(persona)) {
            assertThat(result.getOverallLearningTrend()).isEqualTo("IMPROVING");
        }
        if ("consistently successful learner".equals(persona)) {
            assertThat(result.getStudentLevel()).isIn("EXAM_READY", "EXPERT");
            assertThat(result.getExamAnalytics().getPassedExams()).isEqualTo(5);
        }
    }

    private static Stream<Arguments> historicalPersonas() {
        LocalDate today = LocalDate.of(2026, 7, 28);
        Set<LocalDate> activeWeek = Set.of(
                today,
                today.minusDays(1),
                today.minusDays(2),
                today.minusDays(4));

        return Stream.of(
                Arguments.of(
                        "new user without activity",
                        new StudentIntelligenceEngine.AnalyticsInput(
                                0, List.of(), List.of(), List.of(), Set.of(), 0, 0, 0, 0),
                        false),
                Arguments.of(
                        "practice-only learner",
                        new StudentIntelligenceEngine.AnalyticsInput(
                                0,
                                List.of(activity(today.minusDays(1), 68, false, "SIGN_PRACTICE")),
                                List.of(category(1L, "PRIORITY", 30, 20, today, 12, 8, 10, 6)),
                                List.of(question(1, 1, today, null, 1L, "PRIORITY")),
                                activeWeek,
                                2, 1, 3, 0),
                        false),
                Arguments.of(
                        "learner with one official exam",
                        new StudentIntelligenceEngine.AnalyticsInput(
                                1,
                                List.of(official(today.minusDays(1), 84, true, null)),
                                List.of(category(1L, "DANGER", 35, 29, today, 10, 8, 10, 8)),
                                List.of(question(1, 1, today, null, 1L, "DANGER")),
                                activeWeek,
                                3, 2, 1, 1),
                        false),
                Arguments.of(
                        "learner with repeated failed exams",
                        new StudentIntelligenceEngine.AnalyticsInput(
                                3,
                                List.of(
                                        official(today.minusDays(20), 58, false, null),
                                        official(today.minusDays(10), 63, false, null),
                                        official(today.minusDays(2), 69, false, null)),
                                List.of(category(1L, "PARKING", 120, 65, today, 20, 11, 20, 12)),
                                List.of(question(4, 1, today, null, 1L, "PARKING")),
                                activeWeek,
                                5, 2, 4, 0),
                        false),
                Arguments.of(
                        "steadily improving learner",
                        new StudentIntelligenceEngine.AnalyticsInput(
                                4,
                                List.of(
                                        official(today.minusDays(35), 62, false, null),
                                        official(today.minusDays(20), 74, false, null),
                                        official(today.minusDays(8), 84, true, null),
                                        official(today.minusDays(1), 90, true, null)),
                                List.of(category(1L, "PRIORITY", 130, 108, today, 25, 22, 25, 18)),
                                List.of(question(5, 4, today, null, 1L, "PRIORITY")),
                                activeWeek,
                                6, 5, 5, 2),
                        false),
                Arguments.of(
                        "consistently successful learner",
                        new StudentIntelligenceEngine.AnalyticsInput(
                                5,
                                List.of(
                                        official(today.minusDays(30), 91, true, 1_300),
                                        official(today.minusDays(22), 93, true, 1_260),
                                        official(today.minusDays(14), 92, true, 1_220),
                                        official(today.minusDays(7), 94, true, 1_180),
                                        official(today.minusDays(1), 95, true, 1_140)),
                                List.of(category(1L, "MANDATORY", 180, 171, today, 30, 29, 30, 28)),
                                List.of(question(6, 6, today, 14, 1L, "MANDATORY")),
                                Set.of(
                                        today,
                                        today.minusDays(1),
                                        today.minusDays(2),
                                        today.minusDays(3),
                                        today.minusDays(4),
                                        today.minusDays(5),
                                        today.minusDays(6),
                                        today.minusDays(7),
                                        today.minusDays(8),
                                        today.minusDays(9),
                                        today.minusDays(10),
                                        today.minusDays(11)),
                                8, 8, 8, 5),
                        true),
                Arguments.of(
                        "historical records without answer timing",
                        new StudentIntelligenceEngine.AnalyticsInput(
                                3,
                                List.of(
                                        official(today.minusDays(18), 78, false, null),
                                        official(today.minusDays(9), 84, true, null),
                                        official(today.minusDays(2), 86, true, null)),
                                List.of(category(1L, "INFORMATION", 105, 88, today, 20, 17, 20, 16)),
                                List.of(question(3, 2, today, null, 1L, "INFORMATION")),
                                activeWeek,
                                4, 3, 3, 1),
                        false),
                Arguments.of(
                        "new records with answer timing",
                        new StudentIntelligenceEngine.AnalyticsInput(
                                2,
                                List.of(
                                        official(today.minusDays(7), 80, false, 1_400),
                                        official(today.minusDays(1), 86, true, 1_250)),
                                List.of(category(1L, "PROHIBITION", 110, 90, today, 20, 17, 20, 16)),
                                List.of(
                                        question(4, 3, today.minusDays(1), 19, 1L, "PROHIBITION"),
                                        question(3, 3, today, 15, 1L, "PROHIBITION")),
                                activeWeek,
                                4, 3, 4, 2),
                        true));
    }

    private static void assertBounded(Integer value) {
        if (value != null) {
            assertThat(value).isBetween(0, 100);
        }
    }

    private static StudentIntelligenceEngine.ScoredActivity official(
            LocalDate date,
            double score,
            boolean passed,
            Integer durationSeconds) {
        return new StudentIntelligenceEngine.ScoredActivity(
                date, score, passed, "OFFICIAL_EXAM", durationSeconds);
    }

    private static StudentIntelligenceEngine.ScoredActivity activity(
            LocalDate date,
            double score,
            boolean passed,
            String type) {
        return new StudentIntelligenceEngine.ScoredActivity(date, score, passed, type, null);
    }

    private static StudentIntelligenceEngine.CategoryEvidence category(
            Long id,
            String code,
            int attempted,
            int correct,
            LocalDate lastPracticed,
            int recentAttempts,
            int recentCorrect,
            int previousAttempts,
            int previousCorrect) {
        return new StudentIntelligenceEngine.CategoryEvidence(
                id,
                code,
                code + " EN",
                code + " NL",
                code + " FR",
                code + " AR",
                attempted,
                correct,
                lastPracticed,
                recentAttempts,
                recentCorrect,
                previousAttempts,
                previousCorrect);
    }

    private static StudentIntelligenceEngine.QuestionEvidence question(
            int attempts,
            int correct,
            LocalDate date,
            Integer timeTakenSeconds,
            Long categoryId,
            String categoryCode) {
        return new StudentIntelligenceEngine.QuestionEvidence(
                attempts,
                correct,
                date,
                timeTakenSeconds,
                categoryId,
                categoryCode,
                categoryCode + " EN",
                categoryCode + " NL",
                categoryCode + " FR",
                categoryCode + " AR");
    }
}
