package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulationAnswer;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulationQuestion;
import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationAnswerRepository;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationQuestionRepository;
import com.readyroad.readyroadbackend.dto.exam.TheoryExamQuestionSnapshot;
import com.readyroad.readyroadbackend.dto.exam.TheoryExamQuestionSnapshot.CategorySnapshot;
import com.readyroad.readyroadbackend.dto.exam.TheoryExamQuestionSnapshot.LocalizedText;
import com.readyroad.readyroadbackend.dto.exam.TheoryExamQuestionSnapshot.OptionSnapshot;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminTheoryExamHistoryServiceTest {

    @Mock ExamSimulationQuestionRepository questionRepository;
    @Mock ExamSimulationAnswerRepository answerRepository;
    @Mock TheoryExamQuestionSnapshotService snapshotService;
    @Mock CategoryRepository categoryRepository;
    @InjectMocks AdminTheoryExamHistoryService service;

    @Test
    void returnsSnapshotContentAndStoredCorrectness() {
        QuizQuestion currentQuestion = new QuizQuestion();
        currentQuestion.setId(10L);
        QuizAnswerOption selected = option(102L);
        QuizAnswerOption correct = option(101L);
        ExamSimulationQuestion examQuestion = examQuestion(currentQuestion);
        ExamSimulationAnswer answer = ExamSimulationAnswer.builder()
                .question(currentQuestion)
                .selectedOption(selected)
                .correctOption(correct)
                .isCorrect(false)
                .build();
        when(questionRepository.findByExamIdOrderByQuestionOrder(22L)).thenReturn(List.of(examQuestion));
        when(answerRepository.findByExamId(22L)).thenReturn(List.of(answer));
        when(snapshotService.read(examQuestion)).thenReturn(snapshot());
        when(categoryRepository.findAll()).thenReturn(
                List.of(category(7L, "A", "Renamed category")));

        var result = service.load(22L);

        assertThat(result.status()).isEqualTo(AdminTheoryExamHistoryService.SNAPSHOT_COMPLETE);
        assertThat(result.result().questions()).singleElement().satisfies(question -> {
            assertThat(question.questionTextEn()).isEqualTo("Historical question");
            assertThat(question.selectedOptionTextEn()).isEqualTo("Historical selected");
            assertThat(question.correctOptionTextEn()).isEqualTo("Historical correct");
            assertThat(question.categoryCode()).isEqualTo("A");
            assertThat(question.categoryNameEn()).isEqualTo("Renamed category");
            assertThat(question.difficulty()).isEqualTo("MEDIUM");
            assertThat(question.isCorrect()).isFalse();
            assertThat(question.snapshotAvailable()).isTrue();
        });
    }

    @Test
    void exposesOnlyStoredIdentityForLegacyRows() {
        QuizQuestion currentQuestion = new QuizQuestion();
        currentQuestion.setId(10L);
        currentQuestion.setQuestionEn("Current edited question");
        ExamSimulationQuestion examQuestion = examQuestion(currentQuestion);
        ExamSimulationAnswer answer = ExamSimulationAnswer.builder()
                .question(currentQuestion)
                .selectedOption(option(102L))
                .correctOption(option(101L))
                .isCorrect(false)
                .build();
        when(questionRepository.findByExamIdOrderByQuestionOrder(22L)).thenReturn(List.of(examQuestion));
        when(answerRepository.findByExamId(22L)).thenReturn(List.of(answer));
        when(snapshotService.read(examQuestion)).thenReturn(null);
        when(categoryRepository.findAll()).thenReturn(List.of());

        var result = service.load(22L);

        assertThat(result.status()).isEqualTo(AdminTheoryExamHistoryService.LEGACY_NO_SNAPSHOT);
        assertThat(result.result().questions()).singleElement().satisfies(question -> {
            assertThat(question.questionId()).isEqualTo(10L);
            assertThat(question.selectedOptionId()).isEqualTo(102L);
            assertThat(question.correctOptionId()).isEqualTo(101L);
            assertThat(question.questionTextEn()).isNull();
            assertThat(question.categoryCode()).isNull();
            assertThat(question.difficulty()).isNull();
            assertThat(question.snapshotAvailable()).isFalse();
        });
    }

    private static Category category(
            long id,
            String code,
            String name) {

        Category category = new Category();
        category.setId(id);
        category.setCode(code);
        category.setNameEn(name);
        category.setNameNl(name);
        category.setNameFr(name);
        category.setNameAr(name);
        return category;
    }

    private static ExamSimulationQuestion examQuestion(QuizQuestion question) {
        ExamSimulationQuestion examQuestion = new ExamSimulationQuestion();
        examQuestion.setQuestion(question);
        examQuestion.setQuestionId(question.getId());
        examQuestion.setQuestionOrder(1);
        return examQuestion;
    }

    private static QuizAnswerOption option(long id) {
        QuizAnswerOption option = new QuizAnswerOption();
        option.setId(id);
        return option;
    }

    private static TheoryExamQuestionSnapshot snapshot() {
        return new TheoryExamQuestionSnapshot(
                (short) 1,
                10L,
                text("Historical question"),
                text("Historical explanation"),
                "/historical.webp",
                new CategorySnapshot(7L, "A", text("Historical category")),
                "MEDIUM",
                List.of(
                        new OptionSnapshot(101L, text("Historical correct"), true, 0),
                        new OptionSnapshot(102L, text("Historical selected"), false, 1)));
    }

    private static LocalizedText text(String value) {
        return new LocalizedText(value, value, value, value);
    }
}
