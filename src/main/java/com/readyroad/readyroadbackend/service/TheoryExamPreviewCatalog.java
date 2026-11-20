package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** One stable free question per active, deliverable theory category. */
@Component
public class TheoryExamPreviewCatalog {

    private final QuizQuestionRepository questionRepository;

    public TheoryExamPreviewCatalog(QuizQuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Transactional(readOnly = true)
    public List<QuizQuestion> loadQuestions(String languageCode) {
        if (!Set.of("en", "nl", "fr", "ar").contains(languageCode)) {
            throw new IllegalArgumentException("Unsupported preview language");
        }
        List<QuizQuestion> candidates = new ArrayList<>(
                questionRepository.findTheoryQuestionBankCandidates("en"));
        candidates.removeIf(Objects::isNull);
        Set<Long> multilingualIds = new HashSet<>();
        candidates.forEach(question -> multilingualIds.add(question.getId()));
        for (String locale : List.of("nl", "fr", "ar")) {
            Set<Long> localizedIds = new HashSet<>();
            questionRepository.findTheoryQuestionBankCandidates(locale)
                    .forEach(question -> localizedIds.add(question.getId()));
            multilingualIds.retainAll(localizedIds);
        }
        candidates.removeIf(question -> !multilingualIds.contains(question.getId()));
        candidates.removeIf(question -> question == null || question.getCategory() == null
                || question.getCategory().getId() == null);
        candidates.sort(Comparator
                .comparing((QuizQuestion question) -> question.getCategory().getDisplayOrder(),
                        Comparator.nullsLast(Integer::compareTo))
                .thenComparing(question -> question.getCategory().getId())
                .thenComparing(QuizQuestion::getId));
        Map<Long, QuizQuestion> firstByCategory = new LinkedHashMap<>();
        for (QuizQuestion question : candidates) {
            firstByCategory.putIfAbsent(question.getCategory().getId(), question);
        }
        if (firstByCategory.isEmpty()) {
            throw new IllegalStateException("No deliverable theory categories for the free preview");
        }
        return List.copyOf(firstByCategory.values());
    }

    @Transactional(readOnly = true)
    public QuizQuestion questionForCategory(String categoryCode, String languageCode) {
        return loadQuestions(languageCode).stream()
                .filter(question -> categoryCode.equalsIgnoreCase(question.getCategory().getCode()))
                .findFirst()
                .orElse(null);
    }

    /** The preview prefix and limit come from current bank data, including new categories. */
    @Transactional(readOnly = true)
    public List<QuizQuestion> composeAttemptQuestions(
            List<QuizQuestion> allocatedQuestions,
            int totalQuestions,
            String languageCode) {
        List<QuizQuestion> previewQuestions = loadQuestions(languageCode);
        if (totalQuestions < previewQuestions.size()) {
            throw new IllegalArgumentException(
                    "Total theory exam size cannot be smaller than the free preview");
        }
        Set<Long> previewIds = new HashSet<>();
        previewQuestions.forEach(question -> previewIds.add(question.getId()));
        int remainingNeeded = totalQuestions - previewQuestions.size();
        List<QuizQuestion> remainingQuestions = allocatedQuestions.stream()
                .filter(Objects::nonNull)
                .filter(question -> !previewIds.contains(question.getId()))
                .limit(remainingNeeded)
                .toList();
        if (remainingQuestions.size() != remainingNeeded) {
            throw new IllegalStateException(
                    "Unable to compose free theory exam: required " + remainingNeeded
                            + " non-preview allocated questions but found " + remainingQuestions.size());
        }
        List<QuizQuestion> result = new ArrayList<>(totalQuestions);
        result.addAll(previewQuestions);
        result.addAll(remainingQuestions);
        return List.copyOf(result);
    }
}
