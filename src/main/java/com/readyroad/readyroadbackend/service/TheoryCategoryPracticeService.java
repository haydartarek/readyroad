package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.TheoryQuestionCoverageStore;
import com.readyroad.readyroadbackend.dto.response.HomeLessonOverviewResponse.CategorySummary;
import com.readyroad.readyroadbackend.dto.practice.SubmitPracticeAnswerRequest;
import com.readyroad.readyroadbackend.dto.practice.SubmitPracticeAnswerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Collections;
import org.springframework.transaction.annotation.Transactional;

/**
 * Paid practice flow for the ten canonical theoretical-exam categories.
 *
 * Category codes are internal identifiers only.
 * Presentation layers must render localized category names instead.
 */
@Service
@RequiredArgsConstructor
public class TheoryCategoryPracticeService {

    private static final Pattern CANONICAL_THEORY_CATEGORY =
            Pattern.compile("^TH(?:0[1-9]|10)$");

    private final CategoryRepository categoryRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizService quizService;
    private final PracticeService practiceService;
    private final TheoryExamAccessService theoryExamAccessService;
    private final TheoryQuestionCoverageStore coverageStore;

    @Transactional(readOnly = true)
    public CategorySummary getSummary(String categoryCode, String languageCode) {
        Category category = requireActiveTheoryCategory(categoryCode);
        long count = coverageStore.findEligibleQuestionIds(category.getId(), languageCode).size();
        return new CategorySummary(category.getCode(), category.getNameNl(), category.getNameEn(),
                category.getNameFr(), category.getNameAr(), count, false, category.getDisplayOrder());
    }

    /** All currently deliverable questions, with no random-quiz count cap. */
    @Transactional(readOnly = true)
    public List<QuizQuestion> getExamQuestions(Long userId, String categoryCode, String languageCode) {
        requireFullAccess(userId);
        Category category = requireActiveTheoryCategory(categoryCode);
        List<Long> ids = coverageStore.findEligibleQuestionIds(category.getId(), languageCode);
        if (ids.isEmpty()) {
            return List.of();
        }
        List<QuizQuestion> questions = new ArrayList<>(
                quizQuestionRepository.findAllByIdWithOptionsAndCategory(ids));
        Collections.shuffle(questions);
        return questions;
    }

    /**
     * Return random questions from exactly one canonical theory category.
     * Paid access is required before question content is delivered.
     */
    public List<QuizQuestion> getQuestions(
            Long userId,
            String categoryCode,
            int count) {

        requireFullAccess(userId);

        Category category =
                requireActiveTheoryCategory(categoryCode);

        return quizService.generateQuizByCategory(
                category.getId(),
                count);
    }

    /**
     * Submit an answer inside a paid category-practice flow.
     *
     * The question must belong to the requested category. This prevents
     * a client from mixing question IDs between category routes.
     */
    public SubmitPracticeAnswerResponse submitAnswer(
            Long userId,
            String categoryCode,
            Long questionId,
            SubmitPracticeAnswerRequest request) {

        requireFullAccess(userId);

        Category category =
                requireActiveTheoryCategory(categoryCode);

        QuizQuestion question =
                quizQuestionRepository.findById(questionId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "QUESTION_NOT_FOUND"));

        if (question.getCategory() == null
                || !Objects.equals(
                        question.getCategory().getId(),
                        category.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "QUESTION_CATEGORY_MISMATCH");
        }

        return practiceService.submitPracticeAnswer(
                userId,
                questionId,
                request);
    }


    /**
     * Compatibility path for the legacy random quiz endpoint.
     *
     * Non-theory questions preserve the historical public behavior.
     * If any returned question belongs to TH01..TH10,
     * full paid access is required before delivery.
     */
    public List<QuizQuestion> getLegacyRandomQuestions(
            Long userId,
            int count) {

        List<QuizQuestion> questions =
                quizService.generateRandomQuiz(count);

        if (containsCanonicalTheoryQuestion(questions)) {
            requireFullAccess(userId);
        }

        return questions;
    }

    /**
     * Compatibility path for the legacy numeric category endpoint.
     */
    public List<QuizQuestion> getLegacyCategoryQuestions(
            Long userId,
            Long categoryId,
            int count) {

        Category category =
                categoryRepository.findById(categoryId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "CATEGORY_NOT_FOUND"));

        if (isCanonicalTheoryCategory(category)) {
            requireFullAccess(userId);
        }

        return quizService.generateQuizByCategory(
                categoryId,
                count);
    }

    /**
     * Protect correctness and explanations exposed through
     * the legacy question-id answer endpoint.
     */
    public SubmitPracticeAnswerResponse submitLegacyAnswer(
            Long userId,
            Long questionId,
            SubmitPracticeAnswerRequest request) {

        QuizQuestion question =
                quizQuestionRepository.findById(questionId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "QUESTION_NOT_FOUND"));

        if (isCanonicalTheoryCategory(
                question.getCategory())) {

            requireFullAccess(userId);
        }

        return practiceService.submitPracticeAnswer(
                userId,
                questionId,
                request);
    }

    public void requireFullAccessForLegacyTheoryExam(
            Long userId) {

        requireFullAccess(userId);
    }

    private boolean containsCanonicalTheoryQuestion(
            List<QuizQuestion> questions) {

        return questions != null
                && questions.stream()
                        .filter(Objects::nonNull)
                        .anyMatch(question ->
                                isCanonicalTheoryCategory(
                                        question.getCategory()));
    }

    private boolean isCanonicalTheoryCategory(
            Category category) {

        if (category == null
                || category.getCode() == null) {

            return false;
        }

        String categoryCode =
                category.getCode()
                        .trim()
                        .toUpperCase(Locale.ROOT);

        return CANONICAL_THEORY_CATEGORY
                .matcher(categoryCode)
                .matches();
    }
    private void requireFullAccess(Long userId) {

        if (userId == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "AUTHENTICATION_REQUIRED");
        }

        if (!theoryExamAccessService.hasFullAccess(userId)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "PAID_ACCESS_REQUIRED");
        }
    }

    private Category requireActiveTheoryCategory(
            String rawCategoryCode) {

        String categoryCode =
                rawCategoryCode == null
                        ? ""
                        : rawCategoryCode
                                .trim()
                                .toUpperCase(Locale.ROOT);

        if (!CANONICAL_THEORY_CATEGORY
                .matcher(categoryCode)
                .matches()) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "THEORY_CATEGORY_NOT_FOUND");
        }

        return categoryRepository
                .findByCode(categoryCode)
                .filter(category ->
                        Boolean.TRUE.equals(
                                category.getIsActive()))
                .filter(category ->
                        category.getContentScope() != null
                                && category.getContentScope()
                                        .supportsTheoreticalExam())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "THEORY_CATEGORY_NOT_FOUND"));
    }
}
