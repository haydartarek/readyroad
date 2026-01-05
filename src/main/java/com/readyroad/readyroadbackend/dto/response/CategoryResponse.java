package com.readyroad.readyroadbackend.dto.response;

public record CategoryResponse(
        Long id,
        String code,
        String nameAr,
        String nameEn,
        String nameNl,
        String nameFr,
        String descriptionAr,
        String descriptionEn,
        String descriptionNl,
        String descriptionFr,
        Integer displayOrder
) {
}

