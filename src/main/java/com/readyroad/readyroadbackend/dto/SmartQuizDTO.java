package com.readyroad.readyroadbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmartQuizDTO {
    private Long attemptId;
    private List<QuizQuestionDTO> questions;
    private Integer availableQuestionsCount;
    private Integer totalQuestionsInPool;
    private Integer recentlyShownCount;
    private Boolean lowAvailabilityWarning;
    private String warningMessage;
}
