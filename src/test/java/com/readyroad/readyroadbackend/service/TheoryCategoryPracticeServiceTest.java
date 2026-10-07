package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.enums.CategoryContentScope;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.TheoryQuestionCoverageStore;
import com.readyroad.readyroadbackend.dto.practice.SubmitPracticeAnswerRequest;
import com.readyroad.readyroadbackend.dto.practice.SubmitPracticeAnswerResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TheoryCategoryPracticeServiceTest {

    private static final Long USER_ID = 42L;
    private static final Long CATEGORY_ID = 101L;
    private static final Long QUESTION_ID = 501L;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private QuizQuestionRepository quizQuestionRepository;

    @Mock
    private QuizService quizService;

    @Mock
    private PracticeService practiceService;

    @Mock
    private TheoryExamAccessService theoryExamAccessService;

    @Mock
    private TheoryQuestionCoverageStore coverageStore;

    private TheoryCategoryPracticeService service;

    @BeforeEach
    void setUp() {
        service = new TheoryCategoryPracticeService(
                categoryRepository,
                quizQuestionRepository,
                quizService,
                practiceService,
                theoryExamAccessService,
                coverageStore);
    }

    @Test
    void freeOrExpiredAccessCannotReceiveCategoryQuestions() {
        when(theoryExamAccessService.hasFullAccess(USER_ID))
                .thenReturn(false);

        ResponseStatusException error =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.getQuestions(
                                USER_ID,
                                "TH01",
                                10));

        assertEquals(
                HttpStatus.FORBIDDEN,
                error.getStatusCode());

        assertEquals(
                "PAID_ACCESS_REQUIRED",
                error.getReason());

        verifyNoInteractions(categoryRepository);
        verifyNoInteractions(quizService);
    }

    @Test
    void paidAccessReceivesOnlyRequestedCategoryQuestions() {
        Category category = activeTheoryCategory(
                CATEGORY_ID,
                "TH01");

        QuizQuestion question = new QuizQuestion();

        when(theoryExamAccessService.hasFullAccess(USER_ID))
                .thenReturn(true);

        when(categoryRepository.findByCode("TH01"))
                .thenReturn(Optional.of(category));

        when(quizService.generateQuizByCategory(
                CATEGORY_ID,
                10))
                .thenReturn(List.of(question));

        List<QuizQuestion> result =
                service.getQuestions(
                        USER_ID,
                        "TH01",
                        10);

        assertEquals(1, result.size());
        assertSame(question, result.get(0));

        verify(quizService)
                .generateQuizByCategory(
                        CATEGORY_ID,
                        10);
    }

    @Test
    void categoryCodeIsNormalizedInternally() {
        Category category = activeTheoryCategory(
                CATEGORY_ID,
                "TH01");

        when(theoryExamAccessService.hasFullAccess(USER_ID))
                .thenReturn(true);

        when(categoryRepository.findByCode("TH01"))
                .thenReturn(Optional.of(category));

        when(quizService.generateQuizByCategory(
                CATEGORY_ID,
                7))
                .thenReturn(List.of());

        service.getQuestions(
                USER_ID,
                "  th01  ",
                7);

        verify(categoryRepository)
                .findByCode("TH01");

        verify(quizService)
                .generateQuizByCategory(
                        CATEGORY_ID,
                        7);
    }

    @Test
    void invalidOrLegacyCategoryCodeIsRejected() {
        when(theoryExamAccessService.hasFullAccess(USER_ID))
                .thenReturn(true);

        ResponseStatusException invalid =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.getQuestions(
                                USER_ID,
                                "TH11",
                                10));

        assertEquals(
                HttpStatus.NOT_FOUND,
                invalid.getStatusCode());

        assertEquals(
                "THEORY_CATEGORY_NOT_FOUND",
                invalid.getReason());

        ResponseStatusException legacy =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.getQuestions(
                                USER_ID,
                                "TH_SPEED",
                                10));

        assertEquals(
                HttpStatus.NOT_FOUND,
                legacy.getStatusCode());

        assertEquals(
                "THEORY_CATEGORY_NOT_FOUND",
                legacy.getReason());

        verifyNoInteractions(categoryRepository);
        verifyNoInteractions(quizService);
    }

    @Test
    void inactiveTheoryCategoryIsRejected() {
        Category category = new Category();
        category.setCode("TH01");
        category.setIsActive(false);
        category.setContentScope(
                CategoryContentScope.THEORETICAL_EXAM);

        when(theoryExamAccessService.hasFullAccess(USER_ID))
                .thenReturn(true);

        when(categoryRepository.findByCode("TH01"))
                .thenReturn(Optional.of(category));

        ResponseStatusException error =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.getQuestions(
                                USER_ID,
                                "TH01",
                                10));

        assertEquals(
                HttpStatus.NOT_FOUND,
                error.getStatusCode());

        verifyNoInteractions(quizService);
    }

    @Test
    void answerQuestionFromDifferentCategoryIsRejected() {
        Category requestedCategory =
                activeTheoryCategory(
                        CATEGORY_ID,
                        "TH01");

        Category otherCategory =
                activeTheoryCategory(
                        202L,
                        "TH02");

        QuizQuestion question = new QuizQuestion();
        question.setCategory(otherCategory);

        SubmitPracticeAnswerRequest request =
                new SubmitPracticeAnswerRequest();

        when(theoryExamAccessService.hasFullAccess(USER_ID))
                .thenReturn(true);

        when(categoryRepository.findByCode("TH01"))
                .thenReturn(Optional.of(requestedCategory));

        when(quizQuestionRepository.findById(QUESTION_ID))
                .thenReturn(Optional.of(question));

        ResponseStatusException error =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.submitAnswer(
                                USER_ID,
                                "TH01",
                                QUESTION_ID,
                                request));

        assertEquals(
                HttpStatus.BAD_REQUEST,
                error.getStatusCode());

        assertEquals(
                "QUESTION_CATEGORY_MISMATCH",
                error.getReason());

        verify(practiceService, never())
                .submitPracticeAnswer(
                        USER_ID,
                        QUESTION_ID,
                        request);
    }

    @Test
    void paidAnswerInCorrectCategoryDelegatesToPracticeService() {
        Category category =
                activeTheoryCategory(
                        CATEGORY_ID,
                        "TH01");

        QuizQuestion question = new QuizQuestion();
        question.setCategory(category);

        SubmitPracticeAnswerRequest request =
                new SubmitPracticeAnswerRequest();

        SubmitPracticeAnswerResponse expected =
                new SubmitPracticeAnswerResponse();

        when(theoryExamAccessService.hasFullAccess(USER_ID))
                .thenReturn(true);

        when(categoryRepository.findByCode("TH01"))
                .thenReturn(Optional.of(category));

        when(quizQuestionRepository.findById(QUESTION_ID))
                .thenReturn(Optional.of(question));

        when(practiceService.submitPracticeAnswer(
                USER_ID,
                QUESTION_ID,
                request))
                .thenReturn(expected);

        SubmitPracticeAnswerResponse actual =
                service.submitAnswer(
                        USER_ID,
                        "TH01",
                        QUESTION_ID,
                        request);

        assertSame(expected, actual);

        verify(practiceService)
                .submitPracticeAnswer(
                        USER_ID,
                        QUESTION_ID,
                        request);
    }

    @Test
    void missingQuestionReturnsNotFoundBeforePracticeService() {
        Category category =
                activeTheoryCategory(
                        CATEGORY_ID,
                        "TH01");

        SubmitPracticeAnswerRequest request =
                new SubmitPracticeAnswerRequest();

        when(theoryExamAccessService.hasFullAccess(USER_ID))
                .thenReturn(true);

        when(categoryRepository.findByCode("TH01"))
                .thenReturn(Optional.of(category));

        when(quizQuestionRepository.findById(QUESTION_ID))
                .thenReturn(Optional.empty());

        ResponseStatusException error =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.submitAnswer(
                                USER_ID,
                                "TH01",
                                QUESTION_ID,
                                request));

        assertEquals(
                HttpStatus.NOT_FOUND,
                error.getStatusCode());

        assertEquals(
                "QUESTION_NOT_FOUND",
                error.getReason());

        verifyNoInteractions(practiceService);
    }


    @Test
    void legacyCanonicalCategoryRequiresAuthentication() {
        Category category =
                activeTheoryCategory(
                        CATEGORY_ID,
                        "TH01");

        when(categoryRepository.findById(CATEGORY_ID))
                .thenReturn(Optional.of(category));

        ResponseStatusException error =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.getLegacyCategoryQuestions(
                                null,
                                CATEGORY_ID,
                                10));

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                error.getStatusCode());

        assertEquals(
                "AUTHENTICATION_REQUIRED",
                error.getReason());

        verifyNoInteractions(quizService);
    }

    @Test
    void legacyCanonicalCategoryRequiresPaidAccess() {
        Category category =
                activeTheoryCategory(
                        CATEGORY_ID,
                        "TH01");

        when(categoryRepository.findById(CATEGORY_ID))
                .thenReturn(Optional.of(category));

        when(theoryExamAccessService.hasFullAccess(USER_ID))
                .thenReturn(false);

        ResponseStatusException error =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.getLegacyCategoryQuestions(
                                USER_ID,
                                CATEGORY_ID,
                                10));

        assertEquals(
                HttpStatus.FORBIDDEN,
                error.getStatusCode());

        assertEquals(
                "PAID_ACCESS_REQUIRED",
                error.getReason());

        verifyNoInteractions(quizService);
    }

    @Test
    void legacyCanonicalCategoryAllowsPaidAccess() {
        Category category =
                activeTheoryCategory(
                        CATEGORY_ID,
                        "TH01");

        QuizQuestion question = new QuizQuestion();
        question.setCategory(category);

        when(categoryRepository.findById(CATEGORY_ID))
                .thenReturn(Optional.of(category));

        when(theoryExamAccessService.hasFullAccess(USER_ID))
                .thenReturn(true);

        when(quizService.generateQuizByCategory(
                CATEGORY_ID,
                10))
                .thenReturn(List.of(question));

        List<QuizQuestion> result =
                service.getLegacyCategoryQuestions(
                        USER_ID,
                        CATEGORY_ID,
                        10);

        assertEquals(1, result.size());
        assertSame(question, result.get(0));
    }

    @Test
    void legacyNonTheoryCategoryRemainsPublic() {
        Category category = new Category();
        category.setId(CATEGORY_ID);
        category.setCode("RANDOM");

        QuizQuestion question = new QuizQuestion();
        question.setCategory(category);

        when(categoryRepository.findById(CATEGORY_ID))
                .thenReturn(Optional.of(category));

        when(quizService.generateQuizByCategory(
                CATEGORY_ID,
                10))
                .thenReturn(List.of(question));

        List<QuizQuestion> result =
                service.getLegacyCategoryQuestions(
                        null,
                        CATEGORY_ID,
                        10);

        assertEquals(1, result.size());
        assertSame(question, result.get(0));

        verifyNoInteractions(theoryExamAccessService);
    }

    @Test
    void legacyRandomRejectsCanonicalTheoryWithoutPaidAccess() {
        Category category =
                activeTheoryCategory(
                        CATEGORY_ID,
                        "TH01");

        QuizQuestion question = new QuizQuestion();
        question.setCategory(category);

        when(quizService.generateRandomQuiz(10))
                .thenReturn(List.of(question));

        when(theoryExamAccessService.hasFullAccess(USER_ID))
                .thenReturn(false);

        ResponseStatusException error =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.getLegacyRandomQuestions(
                                USER_ID,
                                10));

        assertEquals(
                HttpStatus.FORBIDDEN,
                error.getStatusCode());

        assertEquals(
                "PAID_ACCESS_REQUIRED",
                error.getReason());
    }

    @Test
    void legacyRandomAllowsNonTheoryQuestionsWithoutSubscription() {
        Category category = new Category();
        category.setId(CATEGORY_ID);
        category.setCode("RANDOM");

        QuizQuestion question = new QuizQuestion();
        question.setCategory(category);

        when(quizService.generateRandomQuiz(10))
                .thenReturn(List.of(question));

        List<QuizQuestion> result =
                service.getLegacyRandomQuestions(
                        null,
                        10);

        assertEquals(1, result.size());
        assertSame(question, result.get(0));

        verifyNoInteractions(theoryExamAccessService);
    }

    @Test
    void legacyTheoryAnswerRequiresPaidAccess() {
        Category category =
                activeTheoryCategory(
                        CATEGORY_ID,
                        "TH01");

        QuizQuestion question = new QuizQuestion();
        question.setCategory(category);

        SubmitPracticeAnswerRequest request =
                new SubmitPracticeAnswerRequest();

        when(quizQuestionRepository.findById(QUESTION_ID))
                .thenReturn(Optional.of(question));

        when(theoryExamAccessService.hasFullAccess(USER_ID))
                .thenReturn(false);

        ResponseStatusException error =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.submitLegacyAnswer(
                                USER_ID,
                                QUESTION_ID,
                                request));

        assertEquals(
                HttpStatus.FORBIDDEN,
                error.getStatusCode());

        verify(practiceService, never())
                .submitPracticeAnswer(
                        USER_ID,
                        QUESTION_ID,
                        request);
    }

    @Test
    void legacyNonTheoryAnswerPreservesExistingFlow() {
        Category category = new Category();
        category.setId(CATEGORY_ID);
        category.setCode("RANDOM");

        QuizQuestion question = new QuizQuestion();
        question.setCategory(category);

        SubmitPracticeAnswerRequest request =
                new SubmitPracticeAnswerRequest();

        SubmitPracticeAnswerResponse expected =
                new SubmitPracticeAnswerResponse();

        when(quizQuestionRepository.findById(QUESTION_ID))
                .thenReturn(Optional.of(question));

        when(practiceService.submitPracticeAnswer(
                USER_ID,
                QUESTION_ID,
                request))
                .thenReturn(expected);

        SubmitPracticeAnswerResponse actual =
                service.submitLegacyAnswer(
                        USER_ID,
                        QUESTION_ID,
                        request);

        assertSame(expected, actual);

        verifyNoInteractions(theoryExamAccessService);
    }

    @Test
    void legacyTheoryExamRequiresPaidAccess() {
        ResponseStatusException unauthenticated =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service
                                .requireFullAccessForLegacyTheoryExam(
                                        null));

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                unauthenticated.getStatusCode());

        when(theoryExamAccessService.hasFullAccess(USER_ID))
                .thenReturn(false);

        ResponseStatusException unpaid =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service
                                .requireFullAccessForLegacyTheoryExam(
                                        USER_ID));

        assertEquals(
                HttpStatus.FORBIDDEN,
                unpaid.getStatusCode());
    }

    @Test
    void legacyTheoryExamAllowsPaidAccess() {
        when(theoryExamAccessService.hasFullAccess(USER_ID))
                .thenReturn(true);

        service.requireFullAccessForLegacyTheoryExam(
                USER_ID);

        verify(theoryExamAccessService)
                .hasFullAccess(USER_ID);
    }
    private Category activeTheoryCategory(
            Long id,
            String code) {

        Category category = new Category();

        category.setId(id);
        category.setCode(code);
        category.setIsActive(true);
        category.setContentScope(
                CategoryContentScope.THEORETICAL_EXAM);

        return category;
    }
}
