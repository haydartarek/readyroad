package com.readyroad.readyroadbackend.dto.admin;

import java.time.LocalDateTime;
import java.util.List;

public final class AdminLearningDtos {

    private AdminLearningDtos() {
    }

    public record PageResponse<T>(
            List<T> items,
            long total,
            int page,
            int size,
            int totalPages) {
    }

    public record CategoryPerformance(
            Long categoryId,
            String categoryCode,
            String nameEn,
            String nameNl,
            String nameFr,
            String nameAr,
            int questionsAttempted,
            int correctAnswers,
            double accuracy,
            LocalDateTime lastPracticedAt) {
    }

    public record StudentSummary(
            Long userId,
            String username,
            String displayName,
            String email,
            String preferredLanguage,
            LocalDateTime accountCreatedAt,
            LocalDateTime lastActiveAt,
            long totalCompletedExams,
            long totalCompletedPractices,
            Double averageExamScore,
            Double latestExamScore,
            List<CategoryPerformance> strongestCategories,
            List<CategoryPerformance> weakestCategories,
            String learningTrend,
            String lastActivityType) {
    }

    public record ExamSummary(
            Long examId,
            Long userId,
            String username,
            String displayName,
            String examType,
            String subjectCode,
            LocalDateTime startedAt,
            LocalDateTime completedAt,
            Long durationSeconds,
            int totalQuestions,
            int answeredQuestions,
            int correctAnswers,
            int incorrectAnswers,
            int unansweredAnswers,
            double scorePercentage,
            boolean passed,
            String languageCode) {
    }

    public record PracticeSummary(
            Long sessionId,
            String signCode,
            String status,
            LocalDateTime startedAt,
            LocalDateTime completedAt,
            int totalQuestions,
            int answeredQuestions,
            int correctAnswers,
            int incorrectAnswers,
            double accuracy,
            String languageCode) {
    }

    public record LessonActivity(
            Long lessonId,
            String lessonCode,
            String titleEn,
            String titleNl,
            String titleFr,
            String titleAr,
            String status,
            int pagesRead,
            LocalDateTime openedAt,
            LocalDateTime lastSeenAt,
            LocalDateTime completedAt,
            String languageCode) {
    }

    public record SignPerformance(
            Long signId,
            String signCode,
            long attempts,
            long passedAttempts,
            double averageScore,
            Double latestScore,
            LocalDateTime lastAttemptAt) {
    }

    public record ErrorPattern(
            Long id,
            String errorType,
            String questionType,
            Long questionRefId,
            String trafficSignCode,
            String ruleCategory,
            LocalDateTime occurredAt) {
    }

    public record ActivityAvailability(
            boolean trafficSignStudyTrackingAvailable,
            boolean videoTrackingAvailable,
            String trafficSignStudyReason,
            String videoReason) {
    }

    public record ExamDetail(
            Long userId,
            String examType,
            Long examId,
            Object result) {
    }
}
