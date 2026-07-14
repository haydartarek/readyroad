package com.readyroad.readyroadbackend.dto.response;

import java.time.LocalDateTime;

/**
 * Extended sign response for admin panel — includes isActive + timestamps.
 */
public record AdminTrafficSignResponse(
                Long id,
                String signCode,
                String categoryCode,
                String nameAr,
                String nameEn,
                String nameNl,
                String nameFr,
                String descriptionAr,
                String descriptionEn,
                String descriptionNl,
                String descriptionFr,
                String imageUrl,
                Boolean isActive,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {
}
