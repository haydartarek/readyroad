package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.enums.CategoryContentScope;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizAnswerOptionRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizUserAnswerRepository;
import com.readyroad.readyroadbackend.domain.repository.UserQuestionHistoryRepository;
import com.readyroad.readyroadbackend.dto.AdminQuizQuestionRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminQuizServiceTest {

    @Mock private QuizQuestionRepository questionRepository;
    @Mock private QuizAnswerOptionRepository optionRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private QuizUserAnswerRepository userAnswerRepository;
    @Mock private UserQuestionHistoryRepository historyRepository;
    @Mock private BackendMessageService messages;

    private AdminQuizService service;

    @BeforeEach
    void setUp() {
        service = new AdminQuizService(
                questionRepository,
                optionRepository,
                categoryRepository,
                userAnswerRepository,
                historyRepository,
                messages);
        lenient().when(messages.get(anyString(), any(Object[].class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void usesNewestFirstSortingWithIdDescendingTieBreaker() {
        when(questionRepository.findAdminQuestions(any(), any(), any(), any(), any(Pageable.class)))
                .thenAnswer(invocation -> new PageImpl<>(List.of(), invocation.getArgument(4), 0));

        service.getQuestionsPaginated(0, 20, "createdAt,desc", null, null, null, null);

        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(questionRepository).findAdminQuestions(any(), any(), any(), any(), pageable.capture());
        assertThat(pageable.getValue().getSort().getOrderFor("createdAt").getDirection().isDescending()).isTrue();
        assertThat(pageable.getValue().getSort().getOrderFor("id").getDirection().isDescending()).isTrue();
    }

    @Test
    void reportsCorrectAnswerDistributionForAThroughC() {
        when(optionRepository.countCorrectAnswersByDisplayOrder()).thenReturn(List.of(
                new Object[] { 1, 6L },
                new Object[] { 2, 3L },
                new Object[] { 3, 1L }));

        var distribution = service.getCorrectAnswerDistribution();

        assertThat(distribution.total()).isEqualTo(10);
        assertThat(distribution.positions()).extracting(position -> position.percentage())
                .containsExactly(60.0, 30.0, 10.0);
    }

    @Test
    void shufflesOnlyDisplayOrderAndPreservesCorrectOptionIdentity() {
        QuizQuestion question = existingQuestion(3);
        Long correctOptionId = question.getActiveOptions().stream()
                .filter(option -> Boolean.TRUE.equals(option.getIsCorrect()))
                .findFirst().orElseThrow().getId();
        when(questionRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(question));

        int count = service.shuffleAnswerOrder(List.of(7L));

        assertThat(count).isEqualTo(1);
        assertThat(question.getActiveOptions()).filteredOn(option -> Boolean.TRUE.equals(option.getIsCorrect()))
                .singleElement().extracting(QuizAnswerOption::getId).isEqualTo(correctOptionId);
        verify(questionRepository, times(2)).flush();
    }

    @Test
    void createsACompliantMultilingualQuestionWithImageAndExplanation() {
        when(categoryRepository.findByCode("A")).thenReturn(Optional.of(category()));
        when(questionRepository.save(any(QuizQuestion.class))).thenAnswer(invocation -> {
            QuizQuestion question = invocation.getArgument(0);
            question.setId(99L);
            return question;
        });
        AdminQuizQuestionRequest request = validRequest();
        request.setContentImageUrl("/images/quiz/question.png");

        var response = service.createQuestion(request);

        assertThat(response.contentImageUrl()).isEqualTo("/images/quiz/question.png");
        assertThat(response.explanationAr()).isEqualTo("شرح عربي");
        assertThat(response.options()).hasSize(2);
        assertThat(response.options()).filteredOn(option -> Boolean.TRUE.equals(option.isCorrect())).hasSize(1);
    }

    @Test
    void rejectsTrafficSignOnlyCategoryForTheoreticalQuestion() {
        Category category = category();
        category.setContentScope(CategoryContentScope.TRAFFIC_SIGN);
        when(categoryRepository.findByCode("A")).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> service.createQuestion(validRequest()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("admin.quiz.category_not_theoretical");
        verify(questionRepository, never()).save(any());
    }

    @Test
    void listsOnlyTheoreticalCategoryScopes() {
        Category category = category();
        when(categoryRepository.findAllByIsActiveTrueAndContentScopeInOrderByDisplayOrderAsc(any()))
                .thenReturn(List.of(category));

        assertThat(service.getTheoreticalCategories())
                .singleElement()
                .extracting(response -> response.code())
                .isEqualTo("A");
    }

    @Test
    void updatesQuestionAndOptionTextInOneSave() {
        QuizQuestion question = existingQuestion(2);
        when(questionRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(question));
        AdminQuizQuestionRequest request = requestFrom(question);
        request.setQuestionEn("Updated question");
        request.getOptions().get(0).setTextEn("Updated answer");

        var response = service.updateQuestion(7L, request);

        assertThat(response.questionEn()).isEqualTo("Updated question");
        assertThat(response.options().get(0).textEn()).isEqualTo("Updated answer");
        assertThat(response.options().get(0).id()).isEqualTo(101L);
        verify(questionRepository, times(2)).flush();
    }

    @Test
    void changesTheCorrectOptionWithoutChangingItsIdentity() {
        QuizQuestion question = existingQuestion(2);
        when(questionRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(question));
        AdminQuizQuestionRequest request = requestFrom(question);
        request.getOptions().get(0).setIsCorrect(false);
        request.getOptions().get(1).setIsCorrect(true);

        var response = service.updateQuestion(7L, request);

        assertThat(response.options()).extracting(option -> option.isCorrect())
                .containsExactly(false, true);
        assertThat(response.options().get(1).id()).isEqualTo(102L);
    }

    @Test
    void addsAThirdOption() {
        QuizQuestion question = existingQuestion(2);
        when(questionRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(question));
        AdminQuizQuestionRequest request = requestFrom(question);
        request.getOptions().add(option(null, "Third", false, 3));

        var response = service.updateQuestion(7L, request);

        assertThat(response.options()).hasSize(3);
        assertThat(question.getActiveOptions()).hasSize(3);
    }

    @Test
    void archivesRemovedThirdOptionAndPreservesItsId() {
        QuizQuestion question = existingQuestion(3);
        when(questionRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(question));
        AdminQuizQuestionRequest request = requestFrom(question);
        request.setOptions(new ArrayList<>(request.getOptions().subList(0, 2)));

        var response = service.updateQuestion(7L, request);

        QuizAnswerOption archived = question.getOptions().stream()
                .filter(option -> option.getId().equals(103L))
                .findFirst().orElseThrow();
        assertThat(archived.getIsActive()).isFalse();
        assertThat(archived.getQuestion().getId()).isEqualTo(7L);
        assertThat(response.options()).hasSize(2);
    }

    @Test
    void referencedQuestionStillAllowsSafeAnswerEditing() {
        QuizQuestion question = existingQuestion(2);
        when(questionRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(question));
        when(userAnswerRepository.existsByQuestionRefId(7L)).thenReturn(true);
        AdminQuizQuestionRequest request = requestFrom(question);
        request.getOptions().get(0).setTextFr("Réponse mise à jour");

        var response = service.updateQuestion(7L, request);

        assertThat(response.options().get(0).textFr()).isEqualTo("Réponse mise à jour");
    }

    @Test
    void referencedQuestionAllowsCategoryDifficultyAndTypeEditing() {
        QuizQuestion question = existingQuestion(2);
        Category replacement = category();
        replacement.setCode("B");
        replacement.setNameEn("Priority");
        when(questionRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(question));
        when(userAnswerRepository.existsByQuestionRefId(7L)).thenReturn(true);
        when(categoryRepository.findByCode("B")).thenReturn(Optional.of(replacement));
        AdminQuizQuestionRequest request = requestFrom(question);
        request.setCategoryCode("B");
        request.setDifficultyLevel("HARD");
        request.setQuestionType("TRUE_FALSE");

        var response = service.updateQuestion(7L, request);

        assertThat(response.categoryCode()).isEqualTo("B");
        assertThat(response.difficultyLevel()).isEqualTo("HARD");
        assertThat(response.questionType()).isEqualTo("TRUE_FALSE");
    }

    @Test
    void rejectsUnknownDifficultyAndQuestionType() {
        when(categoryRepository.findByCode("A")).thenReturn(Optional.of(category()));
        AdminQuizQuestionRequest invalidDifficulty = validRequest();
        invalidDifficulty.setDifficultyLevel("EXPERT");
        assertThatThrownBy(() -> service.createQuestion(invalidDifficulty))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("admin.quiz.difficulty_invalid");

        AdminQuizQuestionRequest invalidType = validRequest();
        invalidType.setQuestionType("VIDEO");
        assertThatThrownBy(() -> service.createQuestion(invalidType))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("admin.quiz.type_invalid");
    }

    @Test
    void rejectsOptionOwnedByAnotherQuestionBeforeMutation() {
        QuizQuestion question = existingQuestion(2);
        when(questionRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(question));
        AdminQuizQuestionRequest request = requestFrom(question);
        request.getOptions().get(0).setId(999L);

        assertThatThrownBy(() -> service.updateQuestion(7L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("admin.quiz.option_not_owned");
        verify(questionRepository, never()).flush();
        assertThat(question.getOptions().get(0).getOptionTextEn()).isEqualTo("Option 1");
    }

    @Test
    void rejectsMalformedImageBeforeDatabaseAccess() {
        AdminQuizQuestionRequest request = validRequest();
        request.setContentImageUrl("javascript:alert(1)");

        assertThatThrownBy(() -> service.createQuestion(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("admin.quiz.image_reference_invalid");
        verifyNoInteractions(categoryRepository, questionRepository);
    }

    @Test
    void rejectsOneOrFourOptionsAndInvalidCorrectCounts() {
        assertRejectedOptions(List.of(option(null, "One", true, 1)), "admin.quiz.options_count");
        assertRejectedOptions(List.of(
                option(null, "One", true, 1), option(null, "Two", false, 2),
                option(null, "Three", false, 3), option(null, "Four", false, 4)),
                "admin.quiz.options_count");
        assertRejectedOptions(List.of(
                option(null, "One", false, 1), option(null, "Two", false, 2)),
                "admin.quiz.correct_count");
        assertRejectedOptions(List.of(
                option(null, "One", true, 1), option(null, "Two", true, 2)),
                "admin.quiz.correct_count");
    }

    @Test
    void rejectsDuplicateOptionTextAfterNormalization() {
        AdminQuizQuestionRequest request = validRequest();
        request.getOptions().get(1).setTextEn("  OPTION   1 ");

        assertThatThrownBy(() -> service.createQuestion(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("admin.quiz.option_text_duplicate");
    }

    private void assertRejectedOptions(List<AdminQuizQuestionRequest.OptionDTO> options, String message) {
        AdminQuizQuestionRequest request = validRequest();
        request.setOptions(new ArrayList<>(options));
        assertThatThrownBy(() -> service.createQuestion(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    private AdminQuizQuestionRequest validRequest() {
        AdminQuizQuestionRequest request = new AdminQuizQuestionRequest();
        request.setCategoryCode("A");
        request.setDifficultyLevel("MEDIUM");
        request.setQuestionType("MULTIPLE_CHOICE");
        request.setQuestionEn("What does this sign mean?");
        request.setQuestionAr("ماذا تعني هذه العلامة؟");
        request.setQuestionNl("Wat betekent dit bord?");
        request.setQuestionFr("Que signifie ce panneau ?");
        request.setExplanationEn("English explanation");
        request.setExplanationAr("شرح عربي");
        request.setExplanationNl("Nederlandse uitleg");
        request.setExplanationFr("Explication française");
        request.setIsActive(true);
        request.setOptions(new ArrayList<>(List.of(
                option(null, "Option 1", true, 1),
                option(null, "Option 2", false, 2))));
        return request;
    }

    private AdminQuizQuestionRequest requestFrom(QuizQuestion question) {
        AdminQuizQuestionRequest request = validRequest();
        request.setQuestionEn(question.getQuestionEn());
        request.setQuestionAr(question.getQuestionAr());
        request.setQuestionNl(question.getQuestionNl());
        request.setQuestionFr(question.getQuestionFr());
        request.setExplanationEn(question.getExplanationEn());
        request.setExplanationAr(question.getExplanationAr());
        request.setExplanationNl(question.getExplanationNl());
        request.setExplanationFr(question.getExplanationFr());
        request.setContentImageUrl(question.getContentImageUrl());
        request.setOptions(question.getActiveOptions().stream()
                .map(option -> new AdminQuizQuestionRequest.OptionDTO(
                        option.getId(), option.getOptionTextEn(), option.getOptionTextAr(),
                        option.getOptionTextNl(), option.getOptionTextFr(), option.getIsCorrect(),
                        option.getDisplayOrder()))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new)));
        return request;
    }

    private QuizQuestion existingQuestion(int optionCount) {
        QuizQuestion question = new QuizQuestion();
        question.setId(7L);
        question.setCategory(category());
        question.setDifficultyLevel(QuizQuestion.DifficultyLevel.MEDIUM);
        question.setQuestionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE);
        question.setQuestionEn("What does this sign mean?");
        question.setQuestionAr("ماذا تعني هذه العلامة؟");
        question.setQuestionNl("Wat betekent dit bord?");
        question.setQuestionFr("Que signifie ce panneau ?");
        question.setExplanationEn("English explanation");
        question.setExplanationAr("شرح عربي");
        question.setExplanationNl("Nederlandse uitleg");
        question.setExplanationFr("Explication française");
        question.setIsActive(true);
        question.setOptions(new ArrayList<>());
        for (int index = 1; index <= optionCount; index++) {
            QuizAnswerOption option = new QuizAnswerOption();
            option.setId(100L + index);
            option.setOptionTextEn("Option " + index);
            option.setOptionTextAr("خيار " + index);
            option.setOptionTextNl("Optie " + index);
            option.setOptionTextFr("Option " + index);
            option.setIsCorrect(index == 1);
            option.setDisplayOrder(index);
            option.setIsActive(true);
            question.addOption(option);
        }
        return question;
    }

    private AdminQuizQuestionRequest.OptionDTO option(Long id, String text, boolean correct, int order) {
        return new AdminQuizQuestionRequest.OptionDTO(
                id, text, text + " AR", text + " NL", text + " FR", correct, order);
    }

    private Category category() {
        Category category = new Category();
        category.setId(1L);
        category.setCode("A");
        category.setNameEn("Danger signs");
        category.setNameAr("علامات الخطر");
        category.setNameNl("Gevaarsborden");
        category.setNameFr("Panneaux de danger");
        category.setIsActive(true);
        category.setContentScope(CategoryContentScope.BOTH);
        return category;
    }
}
