package com.readyroad.readyroadbackend.mapper;

import com.readyroad.readyroadbackend.domain.entity.ExamSimulation;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulationQuestion;
import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.dto.exam.ExamOptionDTO;
import com.readyroad.readyroadbackend.dto.exam.ExamQuestionDTO;
import com.readyroad.readyroadbackend.dto.exam.ExamStartResponse;
import com.readyroad.readyroadbackend.service.RoadSignReferenceTextResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for Exam Simulation DTOs - Phase 5
 */
@Component
@RequiredArgsConstructor
public class ExamMapper {

    private final RoadSignReferenceTextResolver roadSignReferenceTextResolver;

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
                .questionTextEn(roadSignReferenceTextResolver.resolveEn(question.getQuestionEn()))
                .questionTextAr(roadSignReferenceTextResolver.resolveAr(question.getQuestionAr()))
                .questionTextNl(roadSignReferenceTextResolver.resolveNl(question.getQuestionNl()))
                .questionTextFr(roadSignReferenceTextResolver.resolveFr(question.getQuestionFr()))
                .difficultyLevel(question.getDifficultyLevel().name())
                .categoryName(question.getCategory() != null ? question.getCategory().getNameEn() : null)
                .options(question.getOptions() != null ? shuffled(question.getDeliverableOptions().stream()
                        .map(this::toOptionDTO)
                        .collect(Collectors.toCollection(ArrayList::new))) : new ArrayList<>())
                .build();
    }

    /** Shuffle in-place and return the same list. */
    private <T> List<T> shuffled(List<T> list) {
        Collections.shuffle(list);
        return list;
    }

    private ExamOptionDTO toOptionDTO(QuizAnswerOption option) {
        return ExamOptionDTO.builder()
                .optionId(option.getId())
                .optionTextEn(roadSignReferenceTextResolver.resolveEn(option.getOptionTextEn()))
                .optionTextAr(roadSignReferenceTextResolver.resolveAr(option.getOptionTextAr()))
                .optionTextNl(roadSignReferenceTextResolver.resolveNl(option.getOptionTextNl()))
                .optionTextFr(roadSignReferenceTextResolver.resolveFr(option.getOptionTextFr()))
                // Security: Do NOT expose isCorrect
                .build();
    }
}
