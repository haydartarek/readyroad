package com.readyroad.readyroadbackend.mapper;

import com.readyroad.readyroadbackend.domain.entity.PracticeQuestion;
import com.readyroad.readyroadbackend.dto.response.PracticeQuestionResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PracticeQuestionMapper {

    public PracticeQuestionResponse toResponse(PracticeQuestion question) {
        List<PracticeQuestionResponse.OptionResponse> options = List.of(
                new PracticeQuestionResponse.OptionResponse(1, question.getOption1Ar(), question.getOption1En(), question.getOption1Nl(), question.getOption1Fr()),
                new PracticeQuestionResponse.OptionResponse(2, question.getOption2Ar(), question.getOption2En(), question.getOption2Nl(), question.getOption2Fr()),
                new PracticeQuestionResponse.OptionResponse(3, question.getOption3Ar(), question.getOption3En(), question.getOption3Nl(), question.getOption3Fr()),
                new PracticeQuestionResponse.OptionResponse(4, question.getOption4Ar(), question.getOption4En(), question.getOption4Nl(), question.getOption4Fr())
        );

        return new PracticeQuestionResponse(
                question.getId(),
                question.getLesson().getId(),
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
                question.getDisplayOrder()
        );
    }
}
