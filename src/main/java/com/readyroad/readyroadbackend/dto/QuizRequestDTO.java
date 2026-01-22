package com.readyroad.readyroadbackend.dto;

import com.readyroad.readyroadbackend.domain.entity.QuizQuestion.DifficultyLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizRequestDTO {
    private Integer count = 10; // Default 10 questions
    private Long categoryId; // Optional: filter by category
    private DifficultyLevel difficultyLevel; // Optional: filter by difficulty
}
