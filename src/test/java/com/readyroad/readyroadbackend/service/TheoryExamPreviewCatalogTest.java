package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import java.util.List;
import java.util.stream.LongStream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TheoryExamPreviewCatalogTest {

    @Test
    void selectsOneStableQuestionPerEligibleCategoryIncludingNewCategories() {
        QuizQuestionRepository repository = mock(QuizQuestionRepository.class);
        List<QuizQuestion> eligible = List.of(
                question(9, 1, "TH01", 1), question(4, 1, "TH01", 1),
                question(17, 2, "TH02", 2), question(8, 3, "TH11", 3));
        for (String locale : List.of("en", "nl", "fr", "ar")) {
            when(repository.findTheoryQuestionBankCandidates(locale)).thenReturn(eligible);
        }
        TheoryExamPreviewCatalog catalog = new TheoryExamPreviewCatalog(repository);

        assertEquals(List.of(4L, 17L, 8L), catalog.loadQuestions("en").stream()
                .map(QuizQuestion::getId).toList());
        assertEquals(8L, catalog.questionForCategory("TH11", "ar").getId());
    }

    @Test
    void excludesQuestionsMissingInAnyLanguageAndComposesOnePerCategory() {
        QuizQuestionRepository repository = mock(QuizQuestionRepository.class);
        List<QuizQuestion> eligible = List.of(question(1, 1, "TH01", 1),
                question(2, 1, "TH01", 1), question(3, 2, "TH11", 2));
        when(repository.findTheoryQuestionBankCandidates("en")).thenReturn(eligible);
        when(repository.findTheoryQuestionBankCandidates("nl")).thenReturn(eligible);
        when(repository.findTheoryQuestionBankCandidates("fr")).thenReturn(eligible);
        when(repository.findTheoryQuestionBankCandidates("ar")).thenReturn(eligible.subList(1, 3));
        TheoryExamPreviewCatalog catalog = new TheoryExamPreviewCatalog(repository);
        List<QuizQuestion> allocated = LongStream.range(100, 150)
                .mapToObj(id -> question(id, 1, "TH01", 1)).toList();

        List<QuizQuestion> result = catalog.composeAttemptQuestions(allocated, 50, "ar");

        assertEquals(50, result.size());
        assertEquals(List.of(2L, 3L), result.stream().limit(2).map(QuizQuestion::getId).toList());
        assertEquals(100L, result.get(2).getId());
    }

    private static QuizQuestion question(long id, long categoryId, String code, int order) {
        Category category = new Category();
        category.setId(categoryId);
        category.setCode(code);
        category.setDisplayOrder(order);
        QuizQuestion question = new QuizQuestion();
        question.setId(id);
        question.setCategory(category);
        return question;
    }
}
