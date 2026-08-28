package com.readyroad.readyroadbackend.dto.admin;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AdminTheoryCategoryRequest(
        @Pattern(regexp = "[A-Za-z0-9_-]{1,10}") String code,
        @NotBlank @Size(max = 200) String nameEn,
        @NotBlank @Size(max = 200) String nameNl,
        @NotBlank @Size(max = 200) String nameFr,
        @NotBlank @Size(max = 200) String nameAr,
        @Size(max = 2000) String descriptionEn,
        @Size(max = 2000) String descriptionNl,
        @Size(max = 2000) String descriptionFr,
        @Size(max = 2000) String descriptionAr,
        @NotNull @Min(0) Integer displayOrder,
        @NotNull Boolean active,
        @NotBlank @Pattern(regexp = "THEORETICAL_EXAM|BOTH") String contentScope,
        @Min(1) @Max(100) Integer examTargetWeight) {
}
