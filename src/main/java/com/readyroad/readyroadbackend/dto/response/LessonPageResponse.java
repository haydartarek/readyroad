package com.readyroad.readyroadbackend.dto.response;

/**
 * Lesson page detail response — returned as nested items inside
 * LessonDetailResponse.
 */
public record LessonPageResponse(
        Long id,
        int pageNumber,
        String titleNl,
        String titleEn,
        String titleFr,
        String titleAr,
        String contentNl,
        String contentEn,
        String contentFr,
        String contentAr,
        String imageUrl) {
}
