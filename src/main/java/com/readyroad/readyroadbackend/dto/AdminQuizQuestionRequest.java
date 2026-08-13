package com.readyroad.readyroadbackend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for creating / updating a quiz question with its options.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminQuizQuestionRequest {

    /** Required for updates so a stale Admin form cannot overwrite newer content. */
    private Long version;

    @NotBlank(message = "Category code is required")
    @Size(max = 10)
    private String categoryCode;

    /** EASY, MEDIUM, HARD – defaults to EASY if null */
    private String difficultyLevel;

    /** MULTIPLE_CHOICE, TRUE_FALSE, IMAGE_BASED – defaults to MULTIPLE_CHOICE */
    private String questionType;

    @NotBlank(message = "English question text is required")
    private String questionEn;

    @NotBlank(message = "Arabic question text is required")
    private String questionAr;
    @NotBlank(message = "Dutch question text is required")
    private String questionNl;
    @NotBlank(message = "French question text is required")
    private String questionFr;

    private String explanationEn;
    private String explanationAr;
    private String explanationNl;
    private String explanationFr;

    private String contentImageUrl;

    private Boolean isActive;

    @NotEmpty(message = "At least 2 options are required")
    @Size(min = 2, max = 3, message = "Belgian standard requires 2-3 options")
    @Valid
    private List<OptionDTO> options;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionDTO {
        private Long id; // null for new options

        @NotBlank(message = "English option text is required")
        private String textEn;

        @NotBlank(message = "Arabic option text is required")
        private String textAr;
        @NotBlank(message = "Dutch option text is required")
        private String textNl;
        @NotBlank(message = "French option text is required")
        private String textFr;

        private Boolean isCorrect = false;
        private Integer displayOrder = 0;
    }
}
