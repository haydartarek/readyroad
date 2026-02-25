package com.readyroad.readyroadbackend.dto.response;

import java.util.List;

/**
 * Lesson detail response — includes all pages. Used for the single-lesson
 * endpoint.
 */
public record LessonDetailResponse(
        Long id,
        String lessonCode,
        String icon,
        String titleNl,
        String titleEn,
        String titleFr,
        String titleAr,
        String descriptionNl,
        String descriptionEn,
        String descriptionFr,
        String descriptionAr,
        int displayOrder,
        int estimatedMinutes,
        List<LessonPageResponse> pages) {
}
