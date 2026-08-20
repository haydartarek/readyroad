package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulationQuestion;
import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TheoryExamQuestionSnapshotServiceTest {

    @Mock RoadSignReferenceTextResolver textResolver;

    private TheoryExamQuestionSnapshotService service;

    @BeforeEach
    void setUp() {
        service = new TheoryExamQuestionSnapshotService(new ObjectMapper(), textResolver);
    }

    @Test
    void capturesLocalizedQuestionCategoryDifficultyAndOptionsImmutably() {
        when(textResolver.resolveEn(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(textResolver.resolveNl(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(textResolver.resolveFr(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(textResolver.resolveAr(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        QuizQuestion question = question();
        ExamSimulationQuestion examQuestion = new ExamSimulationQuestion();
        examQuestion.setQuestionId(question.getId());

        service.captureInto(examQuestion, question);
        question.setQuestionEn("Edited question");
        question.setDifficultyLevel(QuizQuestion.DifficultyLevel.HARD);
        question.getOptions().getFirst().setOptionTextEn("Edited option");

        var snapshot = service.read(examQuestion);

        assertThat(snapshot.questionText().en()).isEqualTo("Original question");
        assertThat(snapshot.category().code()).isEqualTo("A");
        assertThat(snapshot.difficulty()).isEqualTo("MEDIUM");
        assertThat(snapshot.options()).extracting(option -> option.text().en())
                .containsExactly("Original correct", "Original selected");
        assertThat(snapshot.options()).filteredOn(option -> option.correct()).singleElement()
                .satisfies(option -> assertThat(option.id()).isEqualTo(101L));
    }

    @Test
    void treatsRowsWithoutSnapshotAsLegacyRatherThanUsingCurrentContent() {
        ExamSimulationQuestion examQuestion = new ExamSimulationQuestion();
        examQuestion.setQuestionId(10L);

        assertThat(service.read(examQuestion)).isNull();
    }

    @Test
    void rawSnapshotPayloadIsNeverSerializedFromTheEntity() throws Exception {
        ExamSimulationQuestion examQuestion = new ExamSimulationQuestion();
        examQuestion.setHistoricalSnapshotVersion((short) 1);
        examQuestion.setHistoricalSnapshotJson("{\"correct\":true}");

        String json = new ObjectMapper().writeValueAsString(examQuestion);

        assertThat(json).doesNotContain("historicalSnapshot", "correct");
    }

    private static QuizQuestion question() {
        Category category = new Category();
        category.setId(7L);
        category.setCode("A");
        category.setNameEn("Priority");
        category.setNameNl("Voorrang");
        category.setNameFr("Priorite");
        category.setNameAr("الأولوية");

        QuizQuestion question = new QuizQuestion();
        question.setId(10L);
        question.setQuestionEn("Original question");
        question.setQuestionNl("Originele vraag");
        question.setQuestionFr("Question originale");
        question.setQuestionAr("السؤال الأصلي");
        question.setExplanationEn("Original explanation");
        question.setExplanationNl("Originele uitleg");
        question.setExplanationFr("Explication originale");
        question.setExplanationAr("الشرح الأصلي");
        question.setContentImageUrl("/images/original.webp");
        question.setDifficultyLevel(QuizQuestion.DifficultyLevel.MEDIUM);
        question.setCategory(category);

        question.addOption(option(101L, "Original correct", true, 0));
        question.addOption(option(102L, "Original selected", false, 1));
        return question;
    }

    private static QuizAnswerOption option(long id, String text, boolean correct, int order) {
        QuizAnswerOption option = new QuizAnswerOption();
        option.setId(id);
        option.setOptionTextEn(text);
        option.setOptionTextNl(text + " NL");
        option.setOptionTextFr(text + " FR");
        option.setOptionTextAr(text + " AR");
        option.setIsCorrect(correct);
        option.setDisplayOrder(order);
        option.setIsActive(true);
        return option;
    }
}
