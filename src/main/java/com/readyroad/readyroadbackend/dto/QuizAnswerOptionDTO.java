package com.readyroad.readyroadbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswerOptionDTO {
    private Long id;
    private String optionTextAr;
    private String optionTextEn;
    private String optionTextNl;
    private String optionTextFr;
    private Integer displayOrder;
    // isCorrect is NOT included in DTO sent to client (security)
}
