package com.readyroad.readyroadbackend.dto.sign;

import java.time.LocalDateTime;

public record SignRandomPracticeHistoryItemDto(
        Long sessionId,
        String status,
        int totalQuestions,
        int answeredCount,
        int correctAnswers,
        int wrongAnswers,
        int unanswered,
        double scorePercentage,
        boolean passed,
        int passingScore,
        LocalDateTime startedAt,
        LocalDateTime completedAt) {
}
