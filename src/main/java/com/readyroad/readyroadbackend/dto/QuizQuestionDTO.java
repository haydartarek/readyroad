package com.readyroad.readyroadbackend.dto;

import com.readyroad.readyroadbackend.domain.entity.QuizQuestion.DifficultyLevel;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionDTO {
    private Long id;
    private String questionAr;
    private String questionEn;
    private String questionNl;
    private String questionFr;
    private QuestionType questionType;
    private DifficultyLevel difficultyLevel;

    // Category
    private Long categoryId;
    private String categoryCode;
    private String categoryNameEn;
    private String categoryNameAr;
    private String categoryNameNl;
    private String categoryNameFr;

    // Content image URL (generic, not tied to traffic sign)
    private String contentImageUrl;

    // Options ordered by displayOrder — NO correctness signals
    private List<QuizAnswerOptionDTO> options;
}
