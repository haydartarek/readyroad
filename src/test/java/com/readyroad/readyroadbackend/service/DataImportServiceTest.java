package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DataImportServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private QuizQuestionRepository quizQuestionRepository;

    @Mock
    private BackendMessageService messages;

    private DataImportService service;

    @BeforeEach
    void setUp() {
        service = new DataImportService(categoryRepository, quizQuestionRepository, new ObjectMapper(), messages);
        lenient().when(messages.get(anyString(), any(Object[].class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void rejectsEntireExecuteBatchWhenAnyQuestionIsInvalid() {
        stubCategory();
        String json = """
                [
                  {"categoryCode":"A","questionEn":"Valid?","options":[
                    {"textEn":"Yes","isCorrect":true},{"textEn":"No","isCorrect":false}
                  ]},
                  {"categoryCode":"A","questionEn":"Invalid?","options":[
                    {"textEn":"One","isCorrect":true},{"textEn":"Two","isCorrect":true}
                  ]}
                ]
                """;

        var report = service.importQuizQuestionsFromUpload(bytes(json), false);

        assertThat(report.errors()).contains("admin.import.quiz.correct_count");
        verify(quizQuestionRepository, never()).save(any());
    }

    @Test
    void rejectsOptionCountsOutsideBelgianRange() {
        stubCategory();
        String json = """
                [{"categoryCode":"A","questionEn":"Too many?","options":[
                  {"textEn":"1","isCorrect":true},{"textEn":"2","isCorrect":false},
                  {"textEn":"3","isCorrect":false},{"textEn":"4","isCorrect":false}
                ]}]
                """;

        var report = service.importQuizQuestionsFromUpload(bytes(json), false);

        assertThat(report.errors()).contains("admin.import.quiz.options_count");
        verify(quizQuestionRepository, never()).save(any());
    }

    @Test
    void skipsDuplicateQuestionsWithoutWriting() {
        stubCategory();
        when(quizQuestionRepository.existsByCategoryIdAndNormalizedQuestionEn(1L, "Existing?"))
                .thenReturn(true);
        String json = """
                [{"categoryCode":"A","questionEn":"Existing?","options":[
                  {"textEn":"Yes","isCorrect":true},{"textEn":"No","isCorrect":false}
                ]}]
                """;

        var report = service.importQuizQuestionsFromUpload(bytes(json), false);

        assertThat(report.created()).isZero();
        assertThat(report.skipped()).isEqualTo(1);
        assertThat(report.warnings()).contains("admin.import.quiz.duplicate");
        verify(quizQuestionRepository, never()).save(any());
    }

    @Test
    void importsValidQuestionAndPropagatesPersistenceFailureForRollback() {
        stubCategory();
        when(quizQuestionRepository.existsByCategoryIdAndNormalizedQuestionEn(anyLong(), anyString()))
                .thenReturn(false);
        when(quizQuestionRepository.save(any(QuizQuestion.class))).thenThrow(new IllegalStateException("write failed"));
        String json = """
                [{"categoryCode":"A","questionEn":"Valid?","options":[
                  {"textEn":"Yes","isCorrect":true},{"textEn":"No","isCorrect":false}
                ]}]
                """;

        assertThatThrownBy(() -> service.importQuizQuestionsFromUpload(bytes(json), false))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("write failed");
    }

    @Test
    void categoryImportPropagatesPersistenceFailureForRollback() {
        Category category = new Category();
        category.setId(1L);
        category.setCode("A");
        when(categoryRepository.findByCode("A")).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenThrow(new IllegalStateException("write failed"));
        String json = """
                {"gevaarsborden":{"description_en":"Danger signs"}}
                """;

        assertThatThrownBy(() -> service.importCategoriesFromUpload(bytes(json), false))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("write failed");
    }

    private void stubCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setCode("A");
        when(categoryRepository.findByCode("A")).thenReturn(Optional.of(category));
    }

    private byte[] bytes(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }
}
