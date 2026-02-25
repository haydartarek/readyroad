package com.readyroad.readyroadbackend.mapper;

import com.readyroad.readyroadbackend.domain.entity.TrafficSign;
import com.readyroad.readyroadbackend.dto.response.AdminTrafficSignResponse;
import com.readyroad.readyroadbackend.dto.response.TrafficSignResponse;
import org.springframework.stereotype.Component;

@Component
public class TrafficSignMapper {

    public TrafficSignResponse toResponse(TrafficSign sign) {
        return new TrafficSignResponse(
                sign.getId(),
                sign.getSignCode(),
                sign.getCategory().getCode(),
                sign.getNameAr(),
                sign.getNameEn(),
                sign.getNameNl(),
                sign.getNameFr(),
                sign.getDescriptionAr(),
                sign.getDescriptionEn(),
                sign.getDescriptionNl(),
                sign.getDescriptionFr(),
                sign.getLongDescriptionEn(),
                sign.getLongDescriptionNl(),
                sign.getLongDescriptionFr(),
                sign.getLongDescriptionAr(),
                sign.isLongDescriptionComplete(),
                sign.getImageUrl());
    }

    public AdminTrafficSignResponse toAdminResponse(TrafficSign sign) {
        return new AdminTrafficSignResponse(
                sign.getId(),
                sign.getSignCode(),
                sign.getCategory().getCode(),
                sign.getNameAr(),
                sign.getNameEn(),
                sign.getNameNl(),
                sign.getNameFr(),
                sign.getDescriptionAr(),
                sign.getDescriptionEn(),
                sign.getDescriptionNl(),
                sign.getDescriptionFr(),
                sign.getLongDescriptionEn(),
                sign.getLongDescriptionNl(),
                sign.getLongDescriptionFr(),
                sign.getLongDescriptionAr(),
                sign.isLongDescriptionComplete(),
                sign.getImageUrl(),
                sign.getIsActive(),
                sign.getCreatedAt(),
                sign.getUpdatedAt());
    }
}
