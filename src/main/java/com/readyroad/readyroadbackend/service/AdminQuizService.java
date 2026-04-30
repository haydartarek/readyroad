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
import com.readyroad.readyroadbackend.util.DrivingTextSanitizer;
import com.readyroad.readyroadbackend.dto.response.PageResponse;
import com.readyroad.readyroadbackend.util.PlaceholderDetector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    private final BackendMessageService messages;

    private static final List<String> ALLOWED_SORT_FIELDS = List.of(
            "id", "questionEn", "questionAr", "difficultyLevel", "questionType",
            "category.code", "isActive", "createdAt", "updatedAt");

    // ─── List (paginated) ──────────────────────────────

    public PageResponse<AdminQuizQuestionResponse> getQuestionsPaginated(
            int page, int size, String sortParam,
            String categoryCode, String difficultyLevel, String hasImage, String q) {

        page = Math.max(0, page);
        size = Math.max(1, Math.min(size, 100));

        Sort sort = parseSort(sortParam);
        Pageable pageable = PageRequest.of(page, size, sort);

        String catFilter = (categoryCode != null && !categoryCode.isBlank()) ? categoryCode.trim() : null;
        String qFilter = (q != null && !q.isBlank()) ? q.trim() : null;
        QuizQuestion.DifficultyLevel diffFilter = parseDifficulty(difficultyLevel);
        Boolean hasImageFilter = parseHasImage(hasImage);

        Page<QuizQuestion> questionPage = questionRepository.findAdminQuestions(
                catFilter, diffFilter, hasImageFilter, qFilter, pageable);

        List<AdminQuizQuestionResponse> items = questionPage.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return PageResponse.of(items, page, size, questionPage.getTotalElements());
    }

    private Boolean parseHasImage(String hasImage) {
        if (hasImage == null || hasImage.isBlank()) {
            return null;
        }

        String normalized = hasImage.trim().toLowerCase();
        if (normalized.equals("yes") || normalized.equals("true")) {
            return true;
        }
        if (normalized.equals("no") || normalized.equals("false")) {
            return false;
        }
        return null;
    }

    // ─── Get single ────────────────────────────────────

    public AdminQuizQuestionResponse getQuestionById(Long id) {
        QuizQuestion question = questionRepository.findByIdWithOptions(id)
                .orElseThrow(() -> new IllegalArgumentException(messages.get("admin.quiz.not_found", id)));
        return toResponse(question);
    }

    // ─── Create ────────────────────────────────────────

    @Transactional
    public AdminQuizQuestionResponse createQuestion(AdminQuizQuestionRequest request) {
        validateOptionsPolicy(request);

        Category category = categoryRepository.findByCode(request.getCategoryCode())
                .orElseThrow(() -> new IllegalArgumentException(
                        messages.get("admin.quiz.category_not_found", request.getCategoryCode())));

        QuizQuestion question = new QuizQuestion();
        mapRequestToEntity(request, question);
        question.setCategory(category);
        synchronizeDeliveryStatus(question);

        // Add options
        for (AdminQuizQuestionRequest.OptionDTO optDto : request.getOptions()) {
            QuizAnswerOption option = new QuizAnswerOption();
            option.setOptionTextEn(DrivingTextSanitizer.sanitize("EN", optDto.getTextEn()));
            option.setOptionTextAr(DrivingTextSanitizer.sanitize("AR", optDto.getTextAr() != null ? optDto.getTextAr() : ""));
            option.setOptionTextNl(DrivingTextSanitizer.sanitize("NL", optDto.getTextNl() != null ? optDto.getTextNl() : ""));
            option.setOptionTextFr(DrivingTextSanitizer.sanitize("FR", optDto.getTextFr() != null ? optDto.getTextFr() : ""));
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
                .orElseThrow(() -> new IllegalArgumentException(messages.get("admin.quiz.not_found", id)));
        boolean isReferenced = isQuestionReferenced(id);

        if (isReferenced) {
            validateReferencedQuestionUpdate(question, request);
        }

        // Update category if changed
        if (!question.getCategory().getCode().equals(request.getCategoryCode())) {
            Category category = categoryRepository.findByCode(request.getCategoryCode())
                    .orElseThrow(() -> new IllegalArgumentException(
                            messages.get("admin.quiz.category_not_found", request.getCategoryCode())));
            question.setCategory(category);
        }

        mapRequestToEntity(request, question);
        synchronizeDeliveryStatus(question);

        List<QuizAnswerOption> existingOptions = new ArrayList<>(question.getOptions());
        Map<Long, QuizAnswerOption> existingById = existingOptions.stream()
                .filter(option -> option.getId() != null)
                .collect(Collectors.toMap(QuizAnswerOption::getId, option -> option));

        for (int index = 0; index < existingOptions.size(); index++) {
            existingOptions.get(index).setDisplayOrder(1000 + index);
        }
        questionRepository.flush();

        List<QuizAnswerOption> unmatchedExistingOptions = existingOptions.stream()
                .filter(option -> option.getId() != null)
                .sorted(Comparator.comparing(QuizAnswerOption::getId))
                .collect(Collectors.toCollection(ArrayList::new));

        for (AdminQuizQuestionRequest.OptionDTO optDto : request.getOptions()) {
            QuizAnswerOption option = optDto.getId() != null ? existingById.get(optDto.getId()) : null;
            if (option == null) {
                if (!unmatchedExistingOptions.isEmpty()) {
                    option = unmatchedExistingOptions.remove(0);
                } else {
                    option = new QuizAnswerOption();
                    question.addOption(option);
                }
            } else {
                unmatchedExistingOptions.remove(option);
            }

            option.setOptionTextEn(DrivingTextSanitizer.sanitize("EN", optDto.getTextEn()));
            option.setOptionTextAr(DrivingTextSanitizer.sanitize("AR", optDto.getTextAr() != null ? optDto.getTextAr() : ""));
            option.setOptionTextNl(DrivingTextSanitizer.sanitize("NL", optDto.getTextNl() != null ? optDto.getTextNl() : ""));
            option.setOptionTextFr(DrivingTextSanitizer.sanitize("FR", optDto.getTextFr() != null ? optDto.getTextFr() : ""));
            option.setIsCorrect(optDto.getIsCorrect() != null ? optDto.getIsCorrect() : false);
            option.setDisplayOrder(optDto.getDisplayOrder() != null ? optDto.getDisplayOrder() : 0);
        }

        for (QuizAnswerOption staleOption : unmatchedExistingOptions) {
            question.removeOption(staleOption);
        }

        questionRepository.flush();
        log.info("✅ Quiz question updated id={}", question.getId());
        return toResponse(question);
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
            throw new IllegalArgumentException(messages.get("admin.quiz.options_required"));
        }

        if (parseQuestionType(request.getQuestionType()) == QuizQuestion.QuestionType.IMAGE_BASED &&
                (request.getContentImageUrl() == null || request.getContentImageUrl().isBlank())) {
            throw new IllegalArgumentException(messages.get("admin.quiz.image_required"));
        }

        int count = options.size();
        if (count < 2 || count > 3) {
            throw new IllegalArgumentException(messages.get("admin.quiz.options_count", count));
        }

        long correctCount = options.stream()
                .filter(o -> Boolean.TRUE.equals(o.getIsCorrect()))
                .count();
        if (correctCount != 1) {
            throw new IllegalArgumentException(messages.get("admin.quiz.correct_count", correctCount));
        }

        Set<Integer> orders = new HashSet<>();
        for (AdminQuizQuestionRequest.OptionDTO opt : options) {
            int order = opt.getDisplayOrder() != null ? opt.getDisplayOrder() : 0;
            if (!orders.add(order)) {
                throw new IllegalArgumentException(messages.get("admin.quiz.display_order_duplicate", order));
            }
            if (opt.getTextEn() == null || opt.getTextEn().isBlank()) {
                throw new IllegalArgumentException(messages.get("admin.quiz.option_text_required"));
            }
            if (PlaceholderDetector.hasPlaceholderNonBlank(
                    opt.getTextEn(), opt.getTextNl(), opt.getTextFr(), opt.getTextAr())) {
                throw new IllegalArgumentException(messages.get("admin.quiz.option_placeholder"));
            }
        }
    }

    private void validateReferencedQuestionUpdate(QuizQuestion existing, AdminQuizQuestionRequest request) {
        boolean categoryChanged = existing.getCategory() == null
                || !existing.getCategory().getCode().equals(request.getCategoryCode());
        boolean difficultyChanged = parseDifficultyOrDefault(request.getDifficultyLevel()) != existing.getDifficultyLevel();
        boolean typeChanged = parseQuestionType(request.getQuestionType()) != existing.getQuestionType();
        boolean optionsChanged = !hasSameOptionStructure(existing, request.getOptions());

        if (categoryChanged || difficultyChanged || typeChanged || optionsChanged) {
            throw new IllegalStateException(messages.get("admin.quiz.referenced_update_blocked"));
        }
    }

    private boolean hasSameOptionStructure(
            QuizQuestion existing,
            List<AdminQuizQuestionRequest.OptionDTO> requestedOptions) {

        if (existing.getOptions() == null || requestedOptions == null) {
            return existing.getOptions() == null && requestedOptions == null;
        }

        List<OptionSignature> existingOptions = existing.getOptions().stream()
                .sorted(Comparator.comparing(
                        QuizAnswerOption::getDisplayOrder,
                        Comparator.nullsLast(Integer::compareTo)))
                .map(option -> new OptionSignature(
                        normalize(option.getOptionTextEn()),
                        normalize(option.getOptionTextAr()),
                        normalize(option.getOptionTextNl()),
                        normalize(option.getOptionTextFr()),
                        Boolean.TRUE.equals(option.getIsCorrect()),
                        option.getDisplayOrder() != null ? option.getDisplayOrder() : 0))
                .toList();

        List<OptionSignature> incomingOptions = requestedOptions.stream()
                .sorted(Comparator.comparing(
                        AdminQuizQuestionRequest.OptionDTO::getDisplayOrder,
                        Comparator.nullsLast(Integer::compareTo)))
                .map(option -> new OptionSignature(
                        normalize(option.getTextEn()),
                        normalize(option.getTextAr()),
                        normalize(option.getTextNl()),
                        normalize(option.getTextFr()),
                        Boolean.TRUE.equals(option.getIsCorrect()),
                        option.getDisplayOrder() != null ? option.getDisplayOrder() : 0))
                .toList();

        return existingOptions.equals(incomingOptions);
    }

    private void synchronizeDeliveryStatus(QuizQuestion question) {
        boolean isActive = Boolean.TRUE.equals(question.getIsActive());
        if (isActive) {
            question.setStatus(QuizQuestion.QuestionStatus.PUBLISHED);
            if (question.getPublishedAt() == null) {
                question.setPublishedAt(LocalDateTime.now());
            }
        } else {
            question.setStatus(QuizQuestion.QuestionStatus.DRAFT);
            question.setPublishedAt(null);
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    // ─── Delete ────────────────────────────────────────

    @Transactional
    public void deleteQuestion(Long id) {
        QuizQuestion question = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(messages.get("admin.quiz.not_found", id)));

        // Production-like reference check: block if referenced by answers or history
        if (isQuestionReferenced(id)) {
            throw new IllegalStateException(messages.get("admin.quiz.delete_referenced"));
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
        question.setQuestionEn(DrivingTextSanitizer.sanitize("EN", request.getQuestionEn()));
        question.setQuestionAr(DrivingTextSanitizer.sanitize("AR", request.getQuestionAr() != null ? request.getQuestionAr() : ""));
        question.setQuestionNl(DrivingTextSanitizer.sanitize("NL", request.getQuestionNl() != null ? request.getQuestionNl() : ""));
        question.setQuestionFr(DrivingTextSanitizer.sanitize("FR", request.getQuestionFr() != null ? request.getQuestionFr() : ""));

        question.setExplanationEn(null);
        question.setExplanationAr(null);
        question.setExplanationNl(null);
        question.setExplanationFr(null);
        question.setErrorExplanationEn(null);
        question.setErrorExplanationAr(null);
        question.setErrorExplanationNl(null);
        question.setErrorExplanationFr(null);
        question.setContextSpecific(false);
        question.setRequiresSignImage(false);

        question.setContentImageUrl(request.getContentImageUrl());
        question.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        // Parse enums with defaults
        question.setQuestionType(parseQuestionType(request.getQuestionType()));
        question.setDifficultyLevel(parseDifficultyOrDefault(request.getDifficultyLevel()));
    }

    private AdminQuizQuestionResponse toResponse(QuizQuestion q) {
        List<AdminQuizQuestionResponse.OptionResponse> options = q.getDeliverableOptions().stream()
                .map(o -> new AdminQuizQuestionResponse.OptionResponse(
                        o.getId(),
                        DrivingTextSanitizer.sanitize("EN", o.getOptionTextEn()),
                        DrivingTextSanitizer.sanitize("AR", o.getOptionTextAr()),
                        DrivingTextSanitizer.sanitize("NL", o.getOptionTextNl()),
                        DrivingTextSanitizer.sanitize("FR", o.getOptionTextFr()),
                        o.getIsCorrect(),
                        o.getDisplayOrder()))
                .collect(Collectors.toList());

        return new AdminQuizQuestionResponse(
                q.getId(),
                q.getCategory() != null ? q.getCategory().getCode() : null,
                q.getCategory() != null ? q.getCategory().getNameEn() : null,
                q.getDifficultyLevel() != null ? q.getDifficultyLevel().name() : null,
                q.getQuestionType() != null ? q.getQuestionType().name() : null,
                DrivingTextSanitizer.sanitize("EN", q.getQuestionEn()),
                DrivingTextSanitizer.sanitize("AR", q.getQuestionAr()),
                DrivingTextSanitizer.sanitize("NL", q.getQuestionNl()),
                DrivingTextSanitizer.sanitize("FR", q.getQuestionFr()),
                q.getContentImageUrl(),
                q.getIsActive(),
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

    private record OptionSignature(
            String textEn,
            String textAr,
            String textNl,
            String textFr,
            boolean isCorrect,
            int displayOrder) {
    }
}
