package com.readyroad.readyroadbackend.dto.response;

import java.util.List;

public record HomeLessonOverviewResponse(
        Long id,
        String lessonCode,
        int displayOrder,
        String titleNl,
        String titleEn,
        String titleFr,
        String titleAr,
        List<CategorySummary> categories) {

    public record CategorySummary(
            String categoryCode,
            String nameNl,
            String nameEn,
            String nameFr,
            String nameAr,
            long questionCount,
            boolean primary,
            int displayOrder) {
    }
}
