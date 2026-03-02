package com.readyroad.readyroadbackend.dto;

import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.enums.SignCategory;

/**
 * Lightweight sign summary used in list responses.
 */
public record RoadSignSummaryDto(
        Long         id,
        String       signCode,
        SignCategory  category,
        String       imagePath,
        boolean      seriousViolation,
        String       nameNl,
        String       nameEn,
        String       nameFr,
        String       nameAr
) {
    public static RoadSignSummaryDto from(RoadSign s) {
        return new RoadSignSummaryDto(
                s.getId(),
                s.getSignCode(),
                s.getCategory(),
                s.getImagePath(),
                Boolean.TRUE.equals(s.getSeriousViolation()),
                s.getNameNl(),
                s.getNameEn(),
                s.getNameFr(),
                s.getNameAr()
        );
    }
}
