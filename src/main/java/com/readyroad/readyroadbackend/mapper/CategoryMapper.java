package com.readyroad.readyroadbackend.mapper;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.dto.response.CategoryResponse;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category, long signCount) {
        return new CategoryResponse(
                category.getId(),
                category.getCode(),
                category.getNameAr(),
                category.getNameEn(),
                category.getNameNl(),
                category.getNameFr(),
                category.getDescriptionAr(),
                category.getDescriptionEn(),
                category.getDescriptionNl(),
                category.getDescriptionFr(),
                category.getDisplayOrder(),
                signCount
        );
    }
}

