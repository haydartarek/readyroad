package com.readyroad.readyroadbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTrafficSignRequest {

    @NotBlank(message = "Sign code is required")
    @Size(max = 50)
    private String signCode;

    @NotBlank(message = "Category code is required")
    @Size(max = 10)
    private String categoryCode;

    @NotBlank(message = "English name is required")
    private String nameEn;

    private String nameAr;
    private String nameNl;
    private String nameFr;

    private String descriptionEn;
    private String descriptionAr;
    private String descriptionNl;
    private String descriptionFr;

    private String imageUrl;
}
