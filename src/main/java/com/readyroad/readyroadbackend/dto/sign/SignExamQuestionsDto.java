package com.readyroad.readyroadbackend.dto.sign;

import com.readyroad.readyroadbackend.domain.entity.SignExam;
import com.readyroad.readyroadbackend.domain.enums.SignCategory;

import java.util.List;

/**
 * Returned when the user requests exam questions (stateless GET).
 * Questions are ordered by {@code questionOrder} from sign_exam_questions.
 */
public record SignExamQuestionsDto(
        String       signCode,
        SignCategory category,
        String       imagePath,
        String       nameNl,
        String       nameEn,
        String       nameFr,
        String       nameAr,
        int          examNumber,
        int          passingScore,
        /** Number of questions actually linked (may be < 15 until all 30 questions are written). */
        int          totalLinked,
        int          easyCount,
        int          mediumCount,
        int          hardCount,
        List<SignQuizQuestionDto> questions
) {
    public static SignExamQuestionsDto from(SignExam exam, List<SignQuizQuestionDto> questions) {
        return new SignExamQuestionsDto(
                exam.getSign().getSignCode(),
                exam.getSign().getCategory(),
                exam.getSign().getImagePath(),
                exam.getSign().getNameNl(),
                exam.getSign().getNameEn(),
                exam.getSign().getNameFr(),
                exam.getSign().getNameAr(),
                exam.getExamNumber(),
                exam.getPassingScore(),
                questions.size(),
                exam.getEasyCount(),
                exam.getMediumCount(),
                exam.getHardCount(),
                questions
        );
    }
}
