package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TheoryExamPreviewCatalogTest {

    private static final String CONFIG =
            "127,60,62,364,220,20,136,41,428,65";

    @Test
    void configuredIdsHaveTheApprovedStableOrder() {
        QuizQuestionRepository repository =
                mock(QuizQuestionRepository.class);

        TheoryExamPreviewCatalog catalog =
                new TheoryExamPreviewCatalog(
                        repository,
                        CONFIG);

        assertEquals(
                List.of(
                        127L,
                        60L,
                        62L,
                        364L,
                        220L,
                        20L,
                        136L,
                        41L,
                        428L,
                        65L),
                catalog.questionIds());
    }

    @Test
    void eligibleQuestionsAreReturnedInConfiguredOrderForRequestedLanguage() {
        QuizQuestionRepository repository =
                mock(QuizQuestionRepository.class);

        TheoryExamPreviewCatalog catalog =
                new TheoryExamPreviewCatalog(
                        repository,
                        CONFIG);

        when(repository.findTheoryQuestionBankCandidates("en"))
                .thenReturn(List.of(
                        question(9999L),
                        question(65L),
                        question(428L),
                        question(41L),
                        question(136L),
                        question(20L),
                        question(220L),
                        question(364L),
                        question(62L),
                        question(60L),
                        question(127L)));

        List<Long> resultIds =
                catalog.loadQuestions("en")
                        .stream()
                        .map(QuizQuestion::getId)
                        .toList();

        assertEquals(
                List.of(
                        127L,
                        60L,
                        62L,
                        364L,
                        220L,
                        20L,
                        136L,
                        41L,
                        428L,
                        65L),
                resultIds);
    }

    @Test
    void freeAttemptStartsWithApprovedTenAndAddsFortyAllocatedQuestions() {
        QuizQuestionRepository repository =
                mock(QuizQuestionRepository.class);

        TheoryExamPreviewCatalog catalog =
                new TheoryExamPreviewCatalog(
                        repository,
                        CONFIG);

        List<QuizQuestion> previewQuestions =
                catalog.questionIds()
                        .stream()
                        .map(TheoryExamPreviewCatalogTest::question)
                        .toList();

        when(repository.findTheoryQuestionBankCandidates("en"))
                .thenReturn(previewQuestions);

        List<QuizQuestion> allocatedQuestions =
                java.util.stream.LongStream
                        .range(1000L, 1050L)
                        .mapToObj(TheoryExamPreviewCatalogTest::question)
                        .toList();

        List<QuizQuestion> result =
                catalog.composeAttemptQuestions(
                        allocatedQuestions,
                        50,
                        "en");

        assertEquals(
                50,
                result.size());

        assertEquals(
                List.of(
                        127L,
                        60L,
                        62L,
                        364L,
                        220L,
                        20L,
                        136L,
                        41L,
                        428L,
                        65L),
                result.stream()
                        .limit(10)
                        .map(QuizQuestion::getId)
                        .toList());

        assertEquals(
                java.util.stream.LongStream
                        .range(1000L, 1040L)
                        .boxed()
                        .toList(),
                result.stream()
                        .skip(10)
                        .map(QuizQuestion::getId)
                        .toList());

        assertEquals(
                50L,
                result.stream()
                        .map(QuizQuestion::getId)
                        .distinct()
                        .count());
    }

    @Test
    void configuredQuestionUnavailableForRequestedLanguageFailsClosed() {
        QuizQuestionRepository repository =
                mock(QuizQuestionRepository.class);

        TheoryExamPreviewCatalog catalog =
                new TheoryExamPreviewCatalog(
                        repository,
                        CONFIG);

        List<QuizQuestion> eligible =
                new ArrayList<>(
                        catalog.questionIds()
                                .stream()
                                .map(TheoryExamPreviewCatalogTest::question)
                                .toList());

        eligible.removeIf(
                question ->
                        question.getId().equals(65L));

        when(repository.findTheoryQuestionBankCandidates("ar"))
                .thenReturn(eligible);

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> catalog.loadQuestions("ar"));

        assertEquals(
                "Free theory preview is misconfigured for language ar; "
                        + "unavailable question IDs: [65]",
                exception.getMessage());
    }

    @Test
    void configurationMustContainExactlyTenIds() {
        QuizQuestionRepository repository =
                mock(QuizQuestionRepository.class);

        assertThrows(
                IllegalArgumentException.class,
                () -> new TheoryExamPreviewCatalog(
                        repository,
                        "1,2,3,4,5,6,7,8,9"));
    }

    @Test
    void configurationRejectsDuplicateIds() {
        QuizQuestionRepository repository =
                mock(QuizQuestionRepository.class);

        assertThrows(
                IllegalArgumentException.class,
                () -> new TheoryExamPreviewCatalog(
                        repository,
                        "1,2,3,4,5,6,7,8,9,9"));
    }

    private static QuizQuestion question(Long id) {
        QuizQuestion question =
                new QuizQuestion();

        question.setId(id);

        return question;
    }
}