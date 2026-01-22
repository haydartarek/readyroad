package com.readyroad.readyroadbackend.dto.exam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for exam answer option
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamOptionDTO {

    private Long optionId;

    private String optionTextEn;

    private String optionTextAr;

    private String optionTextNl;

    private String optionTextFr;

    // Note: isCorrect is NOT included (security)
}
