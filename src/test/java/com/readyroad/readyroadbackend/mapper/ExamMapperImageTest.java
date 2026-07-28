package com.readyroad.readyroadbackend.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.readyroad.readyroadbackend.domain.entity.ExamSimulation;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulationQuestion;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.service.RoadSignReferenceTextResolver;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExamMapperImageTest {

    @Test
    void startResponsePreservesQuestionImageUrl() {
        ExamMapper mapper = new ExamMapper(mock(RoadSignReferenceTextResolver.class));

        ExamSimulation exam = new ExamSimulation();
        exam.setId(7L);
        exam.setTotalQuestions(1);
        exam.setStatus(ExamSimulation.ExamStatus.IN_PROGRESS);
        exam.setStartedAt(Instant.parse("2026-07-28T10:00:00Z"));
        exam.setExpiresAt(Instant.parse("2026-07-28T10:30:00Z"));

        QuizQuestion question = new QuizQuestion();
        question.setId(19L);
        question.setDifficultyLevel(QuizQuestion.DifficultyLevel.EASY);
        question.setQuestionEn("Question");
        question.setQuestionAr("Question");
        question.setQuestionNl("Question");
        question.setQuestionFr("Question");
        question.setContentImageUrl("/images/questions/priority-19.png");
        question.setOptions(List.of());

        ExamSimulationQuestion examQuestion = new ExamSimulationQuestion();
        examQuestion.setExam(exam);
        examQuestion.setQuestionId(question.getId());
        examQuestion.setQuestionOrder(1);
        examQuestion.setQuestion(question);

        var response = mapper.toStartResponse(exam, List.of(examQuestion));

        assertThat(response.getQuestions()).hasSize(1);
        assertThat(response.getQuestions().getFirst().getImageUrl())
                .isEqualTo("/images/questions/priority-19.png");
    }
}
