package com.readyroad.readyroadbackend.mapper;

import com.readyroad.readyroadbackend.domain.entity.ExamQuestion;
import com.readyroad.readyroadbackend.dto.response.ExamQuestionResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExamQuestionMapper {

    public ExamQuestionResponse toResponse(ExamQuestion question) {
        List<ExamQuestionResponse.OptionResponse> options = List.of(
                new ExamQuestionResponse.OptionResponse(1, question.getOption1Ar(), question.getOption1En(), question.getOption1Nl(), question.getOption1Fr()),
                new ExamQuestionResponse.OptionResponse(2, question.getOption2Ar(), question.getOption2En(), question.getOption2Nl(), question.getOption2Fr()),
                new ExamQuestionResponse.OptionResponse(3, question.getOption3Ar(), question.getOption3En(), question.getOption3Nl(), question.getOption3Fr()),
                new ExamQuestionResponse.OptionResponse(4, question.getOption4Ar(), question.getOption4En(), question.getOption4Nl(), question.getOption4Fr())
        );

        return new ExamQuestionResponse(
                question.getId(),
                question.getCategory().getId(),
                question.getCategory().getCode(),
                question.getQuestionAr(),
                question.getQuestionEn(),
                question.getQuestionNl(),
                question.getQuestionFr(),
                options,
                question.getCorrectAnswer(),
                question.getExplanationAr(),
                question.getExplanationEn(),
                question.getExplanationNl(),
                question.getExplanationFr(),
                question.getImageUrl(),
                question.getDifficulty().name(),
                question.getIsImportant()
        );
    }
}
