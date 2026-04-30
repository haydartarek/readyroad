package com.readyroad.readyroadbackend.dto.sign;

import java.time.LocalDateTime;

public record SignExamHistoryItemDto(
        Long resultId,
        String signCode,
        String routeCode,
        String signImagePath,
        String nameNl,
        String nameEn,
        String nameFr,
        String nameAr,
        int examNumber,
        int totalQuestions,
        int answeredCount,
        int correctAnswers,
        int wrongAnswers,
        int unansweredCount,
        double scorePercentage,
        int passingThreshold,
        boolean passed,
        LocalDateTime completedAt) {
}
