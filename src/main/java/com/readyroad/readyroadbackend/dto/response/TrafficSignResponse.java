package com.readyroad.readyroadbackend.dto.response;

public record TrafficSignResponse(
                Long id,
                String signCode,
                String categoryCode,
                String routeCode,
                String nameAr,
                String nameEn,
                String nameNl,
                String nameFr,
                String descriptionAr,
                String descriptionEn,
                String descriptionNl,
                String descriptionFr,
                String longDescriptionEn,
                String longDescriptionNl,
                String longDescriptionFr,
                String longDescriptionAr,
                boolean isLongDescriptionComplete,
                String imageUrl) {
}
