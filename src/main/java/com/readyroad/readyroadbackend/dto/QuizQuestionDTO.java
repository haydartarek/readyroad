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
    private Long categoryId;
    private String categoryName;
    
    // ✅ Law #5 & #6: Generic content image URL
    // النظام لا يعرف TrafficSign - فقط contentImageUrl عام
    private String contentImageUrl;
    
    private List<QuizAnswerOptionDTO> options;
}
