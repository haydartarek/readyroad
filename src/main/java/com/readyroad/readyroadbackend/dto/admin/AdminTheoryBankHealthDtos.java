package com.readyroad.readyroadbackend.dto.admin;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class AdminTheoryBankHealthDtos {

    private AdminTheoryBankHealthDtos() {
    }

    public record BankHealthResponse(
            Instant generatedAt,
            Summary summary,
            List<LocaleHealth> locales,
            List<CategoryHealth> categories,
            List<QuestionQuality> questionsNeedingReview,
            List<QuestionExposure> rarelyExposedQuestions,
            List<QuestionExposure> heavilyExposedQuestions) {
    }

    public record Summary(
            long totalQuestions,
            long activeQuestions,
            long inactiveQuestions,
            long publishedQuestions,
            long eligibleAllLocales,
            long translationGapQuestions,
            long explanationGapQuestions,
            long invalidQuestions,
            long underrepresentedCategories,
            long overrepresentedCategories) {
    }

    public record LocaleHealth(
            String locale,
            long eligibleQuestions,
            long translationGapQuestions) {
    }

    public record CategoryHealth(
            long id,
            String code,
            String nameEn,
            String nameNl,
            String nameFr,
            String nameAr,
            String descriptionEn,
            String descriptionNl,
            String descriptionFr,
            String descriptionAr,
            int displayOrder,
            boolean active,
            String contentScope,
            Integer examTargetWeight,
            long totalQuestions,
            long activeQuestions,
            long publishedQuestions,
            long eligibleAllLocales,
            Map<String, Long> eligibleByLocale,
            Map<String, Long> eligibleByDifficulty,
            long translationGapQuestions,
            long explanationGapQuestions,
            long invalidQuestions,
            long totalPresentations,
            double inventoryShare,
            double targetShare,
            String representationStatus) {
    }

    public record CategoryResponse(
            long id,
            String code,
            String nameEn,
            String nameNl,
            String nameFr,
            String nameAr,
            String descriptionEn,
            String descriptionNl,
            String descriptionFr,
            String descriptionAr,
            int displayOrder,
            boolean active,
            String contentScope,
            Integer examTargetWeight) {
    }

    public record LocalePerformance(
            long answered,
            long correct,
            Double correctRate,
            Double averageAnswerTimeSeconds) {
    }

    public record QuestionQuality(
            long questionId,
            String categoryCode,
            String difficulty,
            long presentations,
            long answered,
            Double correctRate,
            Double incorrectRate,
            Double averageAnswerTimeSeconds,
            Map<String, LocalePerformance> performanceByLocale,
            List<String> flags) {
    }

    public record QuestionExposure(
            long questionId,
            String categoryCode,
            String difficulty,
            long presentations) {
    }
}
