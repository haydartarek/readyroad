package com.readyroad.readyroadbackend.dto.response;

public record LessonResponse(
        Long id,
        Long categoryId,
        String categoryCode,
        String titleAr,
        String titleEn,
        String titleNl,
        String titleFr,
        String contentAr,
        String contentEn,
        String contentNl,
        String contentFr,
        Integer displayOrder,
        Integer estimatedMinutes,
        Integer questionsCount
) {
}
