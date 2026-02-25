package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizUserAnswerRepository;
import com.readyroad.readyroadbackend.domain.repository.UserQuestionHistoryRepository;
import com.readyroad.readyroadbackend.dto.AdminQuizQuestionRequest;
import com.readyroad.readyroadbackend.dto.response.AdminQuizQuestionResponse;
import com.readyroad.readyroadbackend.dto.response.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Admin Quiz Question Service — CRUD operations for quiz management.
 * Follows the same pattern as TrafficSignService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminQuizService {

    private final QuizQuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;
    private final QuizUserAnswerRepository userAnswerRepository;
    private final UserQuestionHistoryRepository historyRepository;

    private static final List<String> ALLOWED_SORT_FIELDS = List.of(
            "id", "questionEn", "questionAr", "difficultyLevel", "questionType",
            "category.code", "isActive", "status", "createdAt", "updatedAt");

    // ─── List (paginated) ──────────────────────────────

    public PageResponse<AdminQuizQuestionResponse> getQuestionsPaginated(
            int page, int size, String sortParam,
            String categoryCode, String difficultyLevel, String q) {

        page = Math.max(0, page);
        size = Math.max(1, Math.min(size, 100));

        Sort sort = parseSort(sortParam);
        Pageable pageable = PageRequest.of(page, size, sort);

        String catFilter = (categoryCode != null && !categoryCode.isBlank()) ? categoryCode.trim() : null;
        String qFilter = (q != null && !q.isBlank()) ? q.trim() : null;
        QuizQuestion.DifficultyLevel diffFilter = parseDifficulty(difficultyLevel);

        Page<QuizQuestion> questionPage = questionRepository.findAdminQuestions(
                catFilter, diffFilter, qFilter, pageable);

        List<AdminQuizQuestionResponse> items = questionPage.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return PageResponse.of(items, page, size, questionPage.getTotalElements());
    }

    // ─── Get single ────────────────────────────────────

    public AdminQuizQuestionResponse getQuestionById(Long id) {
        QuizQuestion question = questionRepository.findByIdWithOptions(id)
                .orElseThrow(() -> new IllegalArgumentException("Quiz question not found with id: " + id));
        return toResponse(question);
    }

    // ─── Create ────────────────────────────────────────

    @Transactional
    public AdminQuizQuestionResponse createQuestion(AdminQuizQuestionRequest request) {
        validateOptionsPolicy(request);

        Category category = categoryRepository.findByCode(request.getCategoryCode())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Category not found: " + request.getCategoryCode()));

        QuizQuestion question = new QuizQuestion();
        mapRequestToEntity(request, question);
        question.setCategory(category);
        question.setStatus(QuizQuestion.QuestionStatus.DRAFT);

        // Add options
        for (AdminQuizQuestionRequest.OptionDTO optDto : request.getOptions()) {
            QuizAnswerOption option = new QuizAnswerOption();
            option.setOptionTextEn(optDto.getTextEn());
            option.setOptionTextAr(optDto.getTextAr() != null ? optDto.getTextAr() : "");
            option.setOptionTextNl(optDto.getTextNl() != null ? optDto.getTextNl() : "");
            option.setOptionTextFr(optDto.getTextFr() != null ? optDto.getTextFr() : "");
            option.setIsCorrect(optDto.getIsCorrect() != null ? optDto.getIsCorrect() : false);
            option.setDisplayOrder(optDto.getDisplayOrder() != null ? optDto.getDisplayOrder() : 0);
            question.addOption(option);
        }

        QuizQuestion saved = questionRepository.save(question);
        log.info("✅ Quiz question created with id={}", saved.getId());
        return toResponse(saved);
    }

    // ─── Update ────────────────────────────────────────

    @Transactional
    public AdminQuizQuestionResponse updateQuestion(Long id, AdminQuizQuestionRequest request) {
        validateOptionsPolicy(request);

        QuizQuestion question = questionRepository.findByIdWithOptions(id)
                .orElseThrow(() -> new IllegalArgumentException("Quiz question not found with id: " + id));

        // Production-like edit protection: block meaning-changing fields if referenced
        if (isQuestionReferenced(id)) {
            checkMeaningChangingFields(question, request);
        }

        // Update category if changed
        if (!question.getCategory().getCode().equals(request.getCategoryCode())) {
            Category category = categoryRepository.findByCode(request.getCategoryCode())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Category not found: " + request.getCategoryCode()));
            question.setCategory(category);
        }

        mapRequestToEntity(request, question);

        // Sync options: clear existing and re-add as new
        question.getOptions().clear();
        for (AdminQuizQuestionRequest.OptionDTO optDto : request.getOptions()) {
            QuizAnswerOption option = new QuizAnswerOption();
            option.setOptionTextEn(optDto.getTextEn());
            option.setOptionTextAr(optDto.getTextAr() != null ? optDto.getTextAr() : "");
            option.setOptionTextNl(optDto.getTextNl() != null ? optDto.getTextNl() : "");
            option.setOptionTextFr(optDto.getTextFr() != null ? optDto.getTextFr() : "");
            option.setIsCorrect(optDto.getIsCorrect() != null ? optDto.getIsCorrect() : false);
            option.setDisplayOrder(optDto.getDisplayOrder() != null ? optDto.getDisplayOrder() : 0);
            question.addOption(option);
        }

        QuizQuestion saved = questionRepository.save(question);
        log.info("✅ Quiz question updated id={}", saved.getId());
        return toResponse(saved);
    }

    // ─── Options policy validation ─────────────────────

    /**
     * Validate the 2-3 options policy enforced across the full stack.
     * Rejects payloads that violate:
     * - Option count outside [2, 3]
     * - Not exactly 1 correct option
     * - Duplicate displayOrder values
     * - Any option missing English text
     */
    private void validateOptionsPolicy(AdminQuizQuestionRequest request) {
        List<AdminQuizQuestionRequest.OptionDTO> options = request.getOptions();
        if (options == null) {
            throw new IllegalArgumentException("Options are required");
        }

        int count = options.size();
        if (count < 2 || count > 3) {
            throw new IllegalArgumentException(
                    String.format("Belgian standard requires 2-3 options. Found: %d", count));
        }

        long correctCount = options.stream()
                .filter(o -> Boolean.TRUE.equals(o.getIsCorrect()))
                .count();
        if (correctCount != 1) {
            throw new IllegalArgumentException(
                    String.format("Exactly 1 option must be marked correct. Found: %d", correctCount));
        }

        Set<Integer> orders = new HashSet<>();
        for (AdminQuizQuestionRequest.OptionDTO opt : options) {
            int order = opt.getDisplayOrder() != null ? opt.getDisplayOrder() : 0;
            if (!orders.add(order)) {
                throw new IllegalArgumentException(
                        "Duplicate displayOrder value: " + order);
            }
            if (opt.getTextEn() == null || opt.getTextEn().isBlank()) {
                throw new IllegalArgumentException(
                        "All options must have English text");
            }
        }
    }

    /**
     * Block meaning-changing field edits when a question is referenced by
     * attempts/answers.
     * This prevents historical corruption — the same production rule in all
     * environments.
     * Safe fields (always editable): question text translations, explanations,
     * isActive, contentImageUrl (cosmetic).
     * Blocked fields: category, difficulty, questionType, options (correct answer /
     * option set changes).
     */
    private void checkMeaningChangingFields(QuizQuestion existing, AdminQuizQuestionRequest request) {
        // Category change blocked
        if (!existing.getCategory().getCode().equals(request.getCategoryCode())) {
            throw new IllegalStateException(
                    "Cannot change category — this question is referenced by quiz attempts. " +
                            "Create a new question instead.");
        }

        // Difficulty change blocked
        String existingDiff = existing.getDifficultyLevel() != null ? existing.getDifficultyLevel().name() : "EASY";
        String requestDiff = request.getDifficultyLevel() != null ? request.getDifficultyLevel().toUpperCase().trim()
                : "EASY";
        if (!existingDiff.equals(requestDiff)) {
            throw new IllegalStateException(
                    "Cannot change difficulty — this question is referenced by quiz attempts. " +
                            "Create a new question instead.");
        }

        // Question type change blocked
        String existingType = existing.getQuestionType() != null ? existing.getQuestionType().name()
                : "MULTIPLE_CHOICE";
        String requestType = request.getQuestionType() != null ? request.getQuestionType().toUpperCase().trim()
                : "MULTIPLE_CHOICE";
        if (!existingType.equals(requestType)) {
            throw new IllegalStateException(
                    "Cannot change question type — this question is referenced by quiz attempts. " +
                            "Create a new question instead.");
        }

        // Correct answer change blocked: compare which options are marked correct
        List<Boolean> existingCorrect = existing.getOptions().stream()
                .map(QuizAnswerOption::getIsCorrect)
                .collect(Collectors.toList());
        List<Boolean> requestCorrect = request.getOptions().stream()
                .map(o -> o.getIsCorrect() != null ? o.getIsCorrect() : false)
                .collect(Collectors.toList());
        if (!existingCorrect.equals(requestCorrect)) {
            throw new IllegalStateException(
                    "Cannot change correct answers — this question is referenced by quiz attempts. " +
                            "Create a new question instead.");
        }

        // Number of options change blocked
        if (existing.getOptions().size() != request.getOptions().size()) {
            throw new IllegalStateException(
                    "Cannot change the number of options — this question is referenced by quiz attempts. " +
                            "Create a new question instead.");
        }
    }

    // ─── Delete ────────────────────────────────────────

    @Transactional
    public void deleteQuestion(Long id) {
        QuizQuestion question = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quiz question not found with id: " + id));

        // Production-like reference check: block if referenced by answers or history
        if (isQuestionReferenced(id)) {
            throw new IllegalStateException(
                    "Cannot delete question — it is referenced by quiz attempts or user answers. Remove those references first.");
        }

        questionRepository.delete(question);
        log.info("✅ Quiz question deleted id={}", id);
    }

    // ─── Reference check ───────────────────────────────

    /**
     * Check if a question is referenced by any user answers or question history.
     * Used to enforce production-like delete/edit protection in ALL environments.
     */
    public boolean isQuestionReferenced(Long questionId) {
        // Check quiz_user_answers (polymorphic questionRefId)
        if (userAnswerRepository.existsByQuestionRefId(questionId)) {
            return true;
        }
        // Check user_question_history
        if (historyRepository.existsByQuestionId(questionId)) {
            return true;
        }
        return false;
    }

    // ─── Mapping helpers ───────────────────────────────

    private void mapRequestToEntity(AdminQuizQuestionRequest request, QuizQuestion question) {
        question.setQuestionEn(request.getQuestionEn());
        question.setQuestionAr(request.getQuestionAr() != null ? request.getQuestionAr() : "");
        question.setQuestionNl(request.getQuestionNl() != null ? request.getQuestionNl() : "");
        question.setQuestionFr(request.getQuestionFr() != null ? request.getQuestionFr() : "");

        question.setExplanationEn(request.getExplanationEn());
        question.setExplanationAr(request.getExplanationAr());
        question.setExplanationNl(request.getExplanationNl());
        question.setExplanationFr(request.getExplanationFr());

        question.setContentImageUrl(request.getContentImageUrl());
        question.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        // Parse enums with defaults
        question.setQuestionType(parseQuestionType(request.getQuestionType()));
        question.setDifficultyLevel(parseDifficultyOrDefault(request.getDifficultyLevel()));
    }

    private AdminQuizQuestionResponse toResponse(QuizQuestion q) {
        List<AdminQuizQuestionResponse.OptionResponse> options = q.getOptions().stream()
                .map(o -> new AdminQuizQuestionResponse.OptionResponse(
                        o.getId(),
                        o.getOptionTextEn(),
                        o.getOptionTextAr(),
                        o.getOptionTextNl(),
                        o.getOptionTextFr(),
                        o.getIsCorrect(),
                        o.getDisplayOrder()))
                .collect(Collectors.toList());

        return new AdminQuizQuestionResponse(
                q.getId(),
                q.getCategory() != null ? q.getCategory().getCode() : null,
                q.getCategory() != null ? q.getCategory().getNameEn() : null,
                q.getDifficultyLevel() != null ? q.getDifficultyLevel().name() : null,
                q.getQuestionType() != null ? q.getQuestionType().name() : null,
                q.getQuestionEn(),
                q.getQuestionAr(),
                q.getQuestionNl(),
                q.getQuestionFr(),
                q.getExplanationEn(),
                q.getExplanationAr(),
                q.getExplanationNl(),
                q.getExplanationFr(),
                q.getContentImageUrl(),
                q.getIsActive(),
                q.getStatus() != null ? q.getStatus().name() : null,
                options.size(),
                options,
                isQuestionReferenced(q.getId()),
                q.getCreatedAt(),
                q.getUpdatedAt());
    }

    // ─── Parse helpers ─────────────────────────────────

    private Sort parseSort(String sortParam) {
        if (sortParam == null || sortParam.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        String[] parts = sortParam.split(",");
        String field = parts[0].trim();
        Sort.Direction direction = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim()))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        if ("categoryCode".equals(field)) {
            field = "category.code";
        }

        if (!ALLOWED_SORT_FIELDS.contains(field)) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        return Sort.by(direction, field);
    }

    private QuizQuestion.DifficultyLevel parseDifficulty(String level) {
        if (level == null || level.isBlank())
            return null;
        try {
            return QuizQuestion.DifficultyLevel.valueOf(level.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private QuizQuestion.DifficultyLevel parseDifficultyOrDefault(String level) {
        if (level == null || level.isBlank())
            return QuizQuestion.DifficultyLevel.EASY;
        try {
            return QuizQuestion.DifficultyLevel.valueOf(level.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return QuizQuestion.DifficultyLevel.EASY;
        }
    }

    private QuizQuestion.QuestionType parseQuestionType(String type) {
        if (type == null || type.isBlank())
            return QuizQuestion.QuestionType.MULTIPLE_CHOICE;
        try {
            return QuizQuestion.QuestionType.valueOf(type.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return QuizQuestion.QuestionType.MULTIPLE_CHOICE;
        }
    }
}
