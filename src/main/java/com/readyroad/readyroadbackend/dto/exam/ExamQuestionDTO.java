package com.readyroad.readyroadbackend.dto.exam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for exam question with options
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamQuestionDTO {

    private Long questionId;

    private Integer questionOrder;

    private String questionTextEn;

    private String questionTextAr;

    private String questionTextNl;

    private String questionTextFr;

    private String difficultyLevel;

    private String categoryName;

    private List<ExamOptionDTO> options;
}
