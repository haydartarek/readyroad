package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizUserAnswerRepository;
import com.readyroad.readyroadbackend.domain.repository.UserQuestionHistoryRepository;
import com.readyroad.readyroadbackend.dto.AdminQuizQuestionRequest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminQuizServiceTest {

    @Mock
    private QuizQuestionRepository questionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private QuizUserAnswerRepository userAnswerRepository;

    @Mock
    private UserQuestionHistoryRepository historyRepository;

    @Mock
    private BackendMessageService messages;

    private AdminQuizService service;

    @BeforeEach
    void setUp() {
        service = new AdminQuizService(
                questionRepository,
                categoryRepository,
                userAnswerRepository,
                historyRepository,
                messages);
        lenient().when(messages.get(anyString(), any(Object[].class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void createsACompliantMultilingualQuestion() {
        Category category = category();
        when(categoryRepository.findByCode("A")).thenReturn(Optional.of(category));
        when(questionRepository.save(any(QuizQuestion.class))).thenAnswer(invocation -> {
            QuizQuestion question = invocation.getArgument(0);
            question.setId(99L);
            return question;
        });

        var response = service.createQuestion(validRequest());

        assertThat(response.id()).isEqualTo(99L);
        assertThat(response.questionEn()).isEqualTo("What does this traffic sign mean?");
        assertThat(response.questionAr()).isEqualTo("ماذا تعني هذه العلامة؟");
        assertThat(response.isActive()).isTrue();
        assertThat(response.options()).hasSize(2);
        assertThat(response.options()).filteredOn(option -> Boolean.TRUE.equals(option.isCorrect())).hasSize(1);
        verify(questionRepository).save(any(QuizQuestion.class));
    }

    @Test
    void rejectsMoreThanThreeOptionsBeforeDatabaseAccess() {
        AdminQuizQuestionRequest request = validRequest();
        request.setOptions(List.of(
                option("One", true, 0),
                option("Two", false, 1),
                option("Three", false, 2),
                option("Four", false, 3)));

        assertThatThrownBy(() -> service.createQuestion(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("admin.quiz.options_count");
        verifyNoInteractions(categoryRepository, questionRepository);
    }

    @Test
    void blocksDeletingAReferencedQuestion() {
        QuizQuestion question = new QuizQuestion();
        question.setId(7L);
        when(questionRepository.findById(7L)).thenReturn(Optional.of(question));
        when(userAnswerRepository.existsByQuestionRefId(7L)).thenReturn(true);

        assertThatThrownBy(() -> service.deleteQuestion(7L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("admin.quiz.delete_referenced");
        verify(questionRepository, never()).delete(any());
    }

    @Test
    void deletesAnUnreferencedQuestion() {
        QuizQuestion question = new QuizQuestion();
        question.setId(8L);
        when(questionRepository.findById(8L)).thenReturn(Optional.of(question));

        service.deleteQuestion(8L);

        verify(questionRepository).delete(question);
    }

    private AdminQuizQuestionRequest validRequest() {
        return new AdminQuizQuestionRequest(
                "A",
                "MEDIUM",
                "MULTIPLE_CHOICE",
                "What does this sign mean?",
                "ماذا تعني هذه العلامة؟",
                "Wat betekent dit bord?",
                "Que signifie ce panneau ?",
                null,
                true,
                List.of(option("Stop", true, 0), option("Continue", false, 1)));
    }

    private AdminQuizQuestionRequest.OptionDTO option(String text, boolean correct, int order) {
        return new AdminQuizQuestionRequest.OptionDTO(null, text, text, text, text, correct, order);
    }

    private Category category() {
        Category category = new Category();
        category.setId(1L);
        category.setCode("A");
        category.setNameEn("Danger signs");
        return category;
    }
}
