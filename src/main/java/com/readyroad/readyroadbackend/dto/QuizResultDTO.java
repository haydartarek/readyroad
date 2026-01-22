package com.readyroad.readyroadbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultDTO {
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private Double scorePercentage;
    private Boolean passed; // Assuming 70% is pass threshold
    private Map<Long, QuestionResultDTO> questionResults; // questionId -> result details

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionResultDTO {
        private Long questionId;
        private Long selectedOptionId;
        private Long correctOptionId;
        private Boolean isCorrect;
    }
}
