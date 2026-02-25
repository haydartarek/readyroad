package com.readyroad.readyroadbackend.dto.response;

import java.util.List;

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
        List<String> bulletPointsNl,
        List<String> bulletPointsEn,
        List<String> bulletPointsFr,
        List<String> bulletPointsAr) {
}
