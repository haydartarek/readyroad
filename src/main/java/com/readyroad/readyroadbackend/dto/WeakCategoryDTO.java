package com.readyroad.readyroadbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeakCategoryDTO {
    private Long categoryId;
    private String categoryName;
    private Double accuracyPercentage;
}
