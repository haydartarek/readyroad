package com.readyroad.readyroadbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmartQuizResultDTO {
    private Long attemptId;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private Double scorePercentage;
    private Boolean passed;
    private Map<Long, QuestionResultDTO> questionResults;
    private ErrorAnalysisDTO errorAnalysis;
}
