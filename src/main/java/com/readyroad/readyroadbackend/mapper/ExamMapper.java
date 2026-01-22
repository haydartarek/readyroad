package com.readyroad.readyroadbackend.mapper;

import com.readyroad.readyroadbackend.domain.entity.ExamSimulation;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulation;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulationQuestion;
import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.dto.exam.ExamOptionDTO;
import com.readyroad.readyroadbackend.dto.exam.ExamQuestionDTO;
import com.readyroad.readyroadbackend.dto.exam.ExamStartResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for Exam Simulation DTOs - Phase 5
 */
@Component
public class ExamMapper {

    public ExamStartResponse toStartResponse(ExamSimulation exam, List<ExamSimulationQuestion> examQuestions) {
        return ExamStartResponse.builder()
            .examId(exam.getId())
            .totalQuestions(exam.getTotalQuestions())
            .timeLimitMinutes(30)
            .status(exam.getStatus().name())
            .startedAt(exam.getStartedAt())
            .expiresAt(exam.getExpiresAt())
            .questions(examQuestions.stream()
                .map(this::toQuestionDTO)
                .collect(Collectors.toList()))
            .build();
    }

    private ExamQuestionDTO toQuestionDTO(ExamSimulationQuestion esq) {
        QuizQuestion question = esq.getQuestion();

        return ExamQuestionDTO.builder()
            .questionId(question.getId())
            .questionOrder(esq.getQuestionOrder())
            .questionTextEn(question.getQuestionEn())
            .questionTextAr(question.getQuestionAr())
            .questionTextNl(question.getQuestionNl())
            .questionTextFr(question.getQuestionFr())
            .difficultyLevel(question.getDifficultyLevel().name())
            .categoryName(question.getCategory() != null ? question.getCategory().getNameEn() : null)
            .options(question.getOptions() != null ?
                question.getOptions().stream()
                    .map(this::toOptionDTO)
                    .collect(Collectors.toList()) :
                new ArrayList<>())
            .build();
    }

    private ExamOptionDTO toOptionDTO(QuizAnswerOption option) {
        return ExamOptionDTO.builder()
            .optionId(option.getId())
            .optionTextEn(option.getOptionTextEn())
            .optionTextAr(option.getOptionTextAr())
            .optionTextNl(option.getOptionTextNl())
            .optionTextFr(option.getOptionTextFr())
            // Security: Do NOT expose isCorrect
            .build();
    }
}
