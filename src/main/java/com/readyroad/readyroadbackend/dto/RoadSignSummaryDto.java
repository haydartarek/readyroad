package com.readyroad.readyroadbackend.dto;

import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.enums.SignCategory;
import com.readyroad.readyroadbackend.storage.MediaUrlResolver;

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
        return from(s, null);
    }

    public static RoadSignSummaryDto from(RoadSign s, MediaUrlResolver mediaUrlResolver) {
        return new RoadSignSummaryDto(
                s.getId(),
                s.getSignCode(),
                s.getCategory(),
                mediaUrlResolver == null ? s.getImagePath() : mediaUrlResolver.resolvePublicUrl(s.getImagePath()),
                Boolean.TRUE.equals(s.getSeriousViolation()),
                s.getNameNl(),
                s.getNameEn(),
                s.getNameFr(),
                s.getNameAr()
        );
    }
}
