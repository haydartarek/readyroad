package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Stable free-preview question catalog.
 *
 * The free theory preview always exposes the same ten questions
 * in the configured order. These questions intentionally bypass
 * the normal per-user cooldown because they are product/demo content.
 */
@Component
public class TheoryExamPreviewCatalog {

    public static final int FREE_QUESTION_LIMIT = 10;

    private final QuizQuestionRepository questionRepository;
    private final List<Long> questionIds;

    @Autowired
    public TheoryExamPreviewCatalog(
            QuizQuestionRepository questionRepository,
            @Value("${rijvia.theory-exam.free-preview-question-ids:127,60,62,364,220,20,136,41,428,65}")
            String configuredQuestionIds) {
        this.questionRepository = questionRepository;
        this.questionIds = parseQuestionIds(configuredQuestionIds);
    }

    public List<Long> questionIds() {
        return questionIds;
    }

    @Transactional(readOnly = true)
    public List<QuizQuestion> loadQuestions(String languageCode) {

        List<QuizQuestion> eligibleQuestions =
                questionRepository.findTheoryQuestionBankCandidates(
                        languageCode);

        Map<Long, QuizQuestion> byId =
                eligibleQuestions.stream()
                        .filter(Objects::nonNull)
                        .filter(question ->
                                questionIds.contains(question.getId()))
                        .collect(Collectors.toMap(
                                QuizQuestion::getId,
                                Function.identity(),
                                (first, ignored) -> first));

        List<Long> unavailableIds =
                questionIds.stream()
                        .filter(id -> !byId.containsKey(id))
                        .toList();

        if (!unavailableIds.isEmpty()) {
            throw new IllegalStateException(
                    "Free theory preview is misconfigured for language "
                            + languageCode
                            + "; unavailable question IDs: "
                            + unavailableIds);
        }

        return questionIds.stream()
                .map(byId::get)
                .toList();
    }

    /**
     * Builds one complete FREE attempt.
     *
     * Orders 1..10 are always the stable preview catalog.
     * The remaining positions are filled from the normal allocator,
     * excluding any preview question already selected by that allocator.
     */
    @Transactional(readOnly = true)
    public List<QuizQuestion> composeAttemptQuestions(
            List<QuizQuestion> allocatedQuestions,
            int totalQuestions,
            String languageCode) {

        if (totalQuestions < FREE_QUESTION_LIMIT) {
            throw new IllegalArgumentException(
                    "Total theory exam size cannot be smaller than the free preview");
        }

        List<QuizQuestion> previewQuestions =
                loadQuestions(languageCode);

        Set<Long> previewIds =
                Set.copyOf(questionIds);

        int remainingNeeded =
                totalQuestions - previewQuestions.size();

        List<QuizQuestion> remainingQuestions =
                allocatedQuestions.stream()
                        .filter(Objects::nonNull)
                        .filter(question ->
                                !previewIds.contains(question.getId()))
                        .limit(remainingNeeded)
                        .toList();

        if (remainingQuestions.size() != remainingNeeded) {
            throw new IllegalStateException(
                    "Unable to compose free theory exam: required "
                            + remainingNeeded
                            + " non-preview allocated questions but found "
                            + remainingQuestions.size());
        }

        List<QuizQuestion> result =
                new ArrayList<>(totalQuestions);

        result.addAll(previewQuestions);
        result.addAll(remainingQuestions);

        if (result.size() != totalQuestions) {
            throw new IllegalStateException(
                    "Free theory exam composition did not produce "
                            + totalQuestions
                            + " questions");
        }

        return List.copyOf(result);
    }

    static List<Long> parseQuestionIds(String configuredQuestionIds) {
        if (configuredQuestionIds == null || configuredQuestionIds.isBlank()) {
            throw new IllegalArgumentException(
                    "Free theory preview question IDs must not be empty");
        }

        List<Long> ids;
        try {
            ids = Arrays.stream(configuredQuestionIds.split(","))
                    .map(String::trim)
                    .filter(value -> !value.isEmpty())
                    .map(Long::valueOf)
                    .toList();
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "Free theory preview question IDs must be numeric",
                    ex);
        }

        if (ids.size() != FREE_QUESTION_LIMIT) {
            throw new IllegalArgumentException(
                    "Free theory preview must contain exactly "
                            + FREE_QUESTION_LIMIT
                            + " question IDs");
        }

        if (ids.stream().anyMatch(id -> id == null || id <= 0)) {
            throw new IllegalArgumentException(
                    "Free theory preview question IDs must be positive");
        }

        if (new LinkedHashSet<>(ids).size() != ids.size()) {
            throw new IllegalArgumentException(
                    "Free theory preview question IDs must be unique");
        }

        return List.copyOf(ids);
    }
}