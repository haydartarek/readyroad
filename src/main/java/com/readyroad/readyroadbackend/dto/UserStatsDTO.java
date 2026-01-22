package com.readyroad.readyroadbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsDTO {
    private Integer totalAttempts;
    private Integer passedAttempts;
    private Double averageScore;
    private Integer uniqueQuestionsSeen;
    private Integer totalQuestions;
    private Double coveragePercentage;
    private Integer weakAreasCount;
    private Map<String, Long> commonErrorTypes;
}
