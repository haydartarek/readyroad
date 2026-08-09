package com.readyroad.readyroadbackend.service;

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
import com.readyroad.readyroadbackend.dto.response.AdminQuizQuestionResponse;
import com.readyroad.readyroadbackend.dto.response.AdminQuizCategoryResponse;
import com.readyroad.readyroadbackend.dto.response.AnswerDistributionPreviewResponse;
import com.readyroad.readyroadbackend.dto.response.CorrectAnswerDistributionResponse;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
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
    private final QuizAnswerOptionRepository optionRepository;
    private final CategoryRepository categoryRepository;
    private final QuizUserAnswerRepository userAnswerRepository;
    private final UserQuestionHistoryRepository historyRepository;
    private final BackendMessageService messages;

    private static final List<String> ALLOWED_SORT_FIELDS = List.of(
            "id", "questionEn", "questionAr", "difficultyLevel", "questionType",
            "category.code", "isActive", "createdAt", "updatedAt");

    private static final Set<CategoryContentScope> THEORETICAL_CATEGORY_SCOPES = Set.of(
            CategoryContentScope.THEORETICAL_EXAM,
            CategoryContentScope.BOTH);

    public List<AdminQuizCategoryResponse> getTheoreticalCategories() {
        return categoryRepository
                .findAllByIsActiveTrueAndContentScopeInOrderByDisplayOrderAsc(THEORETICAL_CATEGORY_SCOPES)
                .stream()
                .map(category -> new AdminQuizCategoryResponse(
                        category.getCode(),
                        category.getNameEn(),
                        category.getNameAr(),
                        category.getNameNl(),
                        category.getNameFr()))
                .toList();
    }

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

        Category category = getTheoreticalCategory(request.getCategoryCode());

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
            option.setIsActive(true);
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

        QuizQuestion question = questionRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new IllegalArgumentException(messages.get("admin.quiz.not_found", id)));
        question.getOptions().size();
        validateRequestedOptionIds(question, request.getOptions());
        QuizUpdateAudit audit = captureAudit(question, request);

        // Update category if changed
        if (!question.getCategory().getCode().equals(request.getCategoryCode())) {
            Category category = getTheoreticalCategory(request.getCategoryCode());
            question.setCategory(category);
        }

        mapRequestToEntity(request, question);
        synchronizeDeliveryStatus(question);

        List<QuizAnswerOption> existingOptions = new ArrayList<>(question.getActiveOptions());
        Map<Long, QuizAnswerOption> existingById = existingOptions.stream()
                .filter(option -> option.getId() != null)
                .collect(Collectors.toMap(QuizAnswerOption::getId, option -> option));
        Map<Long, Integer> originalOrders = existingOptions.stream()
                .filter(option -> option.getId() != null)
                .collect(Collectors.toMap(QuizAnswerOption::getId, QuizAnswerOption::getDisplayOrder));

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
                option = new QuizAnswerOption();
                option.setIsActive(true);
                question.addOption(option);
            } else {
                unmatchedExistingOptions.remove(option);
            }

            option.setOptionTextEn(DrivingTextSanitizer.sanitize("EN", optDto.getTextEn()));
            option.setOptionTextAr(DrivingTextSanitizer.sanitize("AR", optDto.getTextAr() != null ? optDto.getTextAr() : ""));
            option.setOptionTextNl(DrivingTextSanitizer.sanitize("NL", optDto.getTextNl() != null ? optDto.getTextNl() : ""));
            option.setOptionTextFr(DrivingTextSanitizer.sanitize("FR", optDto.getTextFr() != null ? optDto.getTextFr() : ""));
            option.setIsCorrect(optDto.getIsCorrect() != null ? optDto.getIsCorrect() : false);
            option.setDisplayOrder(optDto.getDisplayOrder() != null ? optDto.getDisplayOrder() : 0);
            option.setIsActive(true);
        }

        for (QuizAnswerOption staleOption : unmatchedExistingOptions) {
            staleOption.setIsActive(false);
            staleOption.setDisplayOrder(originalOrders.get(staleOption.getId()));
        }

        questionRepository.flush();
        auditAfterCommit(audit);
        return toResponse(question);
    }

    // ─── Options policy validation ─────────────────────

    /**
     * Validate the 2-3 options policy enforced across the full stack.
     * Rejects payloads that violate:
     * - Option count outside [2, 3]
     * - Not exactly 1 correct option
     * - Duplicate displayOrder values
     * - Any option missing text in a supported language
     * - Duplicate option text in any supported language
     */
    private void validateOptionsPolicy(AdminQuizQuestionRequest request) {
        List<AdminQuizQuestionRequest.OptionDTO> options = request.getOptions();
        if (options == null) {
            throw new IllegalArgumentException(messages.get("admin.quiz.options_required"));
        }
        if (isBlank(request.getQuestionEn()) || isBlank(request.getQuestionAr()) ||
                isBlank(request.getQuestionNl()) || isBlank(request.getQuestionFr())) {
            throw new IllegalArgumentException(messages.get("admin.quiz.question_text_required"));
        }

        if (parseQuestionType(request.getQuestionType()) == QuizQuestion.QuestionType.IMAGE_BASED &&
                (request.getContentImageUrl() == null || request.getContentImageUrl().isBlank())) {
            throw new IllegalArgumentException(messages.get("admin.quiz.image_required"));
        }
        validateImageReference(request.getContentImageUrl());

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
        Map<String, Set<String>> normalizedTexts = Map.of(
                "EN", new HashSet<>(),
                "AR", new HashSet<>(),
                "NL", new HashSet<>(),
                "FR", new HashSet<>());
        for (AdminQuizQuestionRequest.OptionDTO opt : options) {
            int order = opt.getDisplayOrder() != null ? opt.getDisplayOrder() : 0;
            if (!orders.add(order)) {
                throw new IllegalArgumentException(messages.get("admin.quiz.display_order_duplicate", order));
            }
            if (isBlank(opt.getTextEn()) || isBlank(opt.getTextAr()) ||
                    isBlank(opt.getTextNl()) || isBlank(opt.getTextFr())) {
                throw new IllegalArgumentException(messages.get("admin.quiz.option_text_required"));
            }
            addUniqueOptionText(normalizedTexts.get("EN"), opt.getTextEn(), "EN");
            addUniqueOptionText(normalizedTexts.get("AR"), opt.getTextAr(), "AR");
            addUniqueOptionText(normalizedTexts.get("NL"), opt.getTextNl(), "NL");
            addUniqueOptionText(normalizedTexts.get("FR"), opt.getTextFr(), "FR");
            if (PlaceholderDetector.hasPlaceholderNonBlank(
                    opt.getTextEn(), opt.getTextNl(), opt.getTextFr(), opt.getTextAr())) {
                throw new IllegalArgumentException(messages.get("admin.quiz.option_placeholder"));
            }
        }
    }

    private Category getTheoreticalCategory(String categoryCode) {
        Category category = categoryRepository.findByCode(categoryCode)
                .orElseThrow(() -> new IllegalArgumentException(
                        messages.get("admin.quiz.category_not_found", categoryCode)));
        if (!Boolean.TRUE.equals(category.getIsActive()) || category.getContentScope() == null
                || !category.getContentScope().supportsTheoreticalExam()) {
            throw new IllegalArgumentException(messages.get("admin.quiz.category_not_theoretical", categoryCode));
        }
        return category;
    }

    public CorrectAnswerDistributionResponse getCorrectAnswerDistribution() {
        Map<Integer, Long> counts = optionRepository.countCorrectAnswersByDisplayOrder().stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).intValue(),
                        row -> ((Number) row[1]).longValue()));
        long total = counts.values().stream().mapToLong(Long::longValue).sum();
        List<CorrectAnswerDistributionResponse.AnswerPosition> positions = new ArrayList<>();
        for (int order = 1; order <= 3; order++) {
            long count = counts.getOrDefault(order, 0L);
            double percentage = total == 0 ? 0.0 : Math.round((count * 1000.0) / total) / 10.0;
            positions.add(new CorrectAnswerDistributionResponse.AnswerPosition(
                    String.valueOf((char) ('A' + order - 1)), count, percentage));
        }
        return new CorrectAnswerDistributionResponse(total, positions);
    }

    public AnswerDistributionPreviewResponse previewAnswerDistribution(List<Long> questionIds) {
        return createBalancePlan(loadSelectedQuestions(questionIds, false)).response();
    }

    @Transactional
    public AnswerDistributionPreviewResponse balanceAnswerOrder(List<Long> questionIds) {
        BalancePlan plan = createBalancePlan(loadSelectedQuestions(questionIds, true));

        for (QuestionAssignment assignment : plan.assignments()) {
            if (assignment.currentPosition() == assignment.targetPosition()) {
                continue;
            }
            List<QuizAnswerOption> options = assignment.options();
            List<Integer> originalOrders = options.stream()
                    .map(QuizAnswerOption::getDisplayOrder)
                    .sorted()
                    .toList();
            QuizAnswerOption correct = options.get(assignment.currentPosition() - 1);
            QuizAnswerOption target = options.get(assignment.targetPosition() - 1);

            for (int index = 0; index < options.size(); index++) {
                options.get(index).setDisplayOrder(1000 + index);
            }
            questionRepository.flush();

            correct.setDisplayOrder(originalOrders.get(assignment.targetPosition() - 1));
            target.setDisplayOrder(originalOrders.get(assignment.currentPosition() - 1));
            for (int index = 0; index < options.size(); index++) {
                QuizAnswerOption option = options.get(index);
                if (option != correct && option != target) {
                    option.setDisplayOrder(originalOrders.get(index));
                }
            }
            questionRepository.flush();
        }

        List<Long> selectedIds = questionIds.stream().distinct().sorted().toList();
        Runnable writeAudit = () -> log.info(
                "AUDIT admin_quiz_answer_balance actor={} questionIds={} before={} after={} timestamp={}",
                currentAdmin(), selectedIds, plan.response().before(), plan.response().after(), LocalDateTime.now());
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    writeAudit.run();
                }
            });
        } else {
            writeAudit.run();
        }
        return plan.response();
    }

    private List<QuizQuestion> loadSelectedQuestions(List<Long> questionIds, boolean lock) {
        List<Long> selectedIds = questionIds.stream().distinct().sorted().toList();
        List<QuizQuestion> questions = new ArrayList<>();
        for (Long questionId : selectedIds) {
            QuizQuestion question = (lock
                    ? questionRepository.findByIdForUpdate(questionId)
                    : questionRepository.findByIdWithOptions(questionId))
                    .orElseThrow(() -> new IllegalArgumentException(messages.get("admin.quiz.not_found", questionId)));
            question.getOptions().size();
            questions.add(question);
        }
        return questions;
    }

    private BalancePlan createBalancePlan(List<QuizQuestion> questions) {
        Map<DistributionGroup, List<QuizQuestion>> groups = new LinkedHashMap<>();
        questions.stream()
                .sorted(Comparator.comparing(QuizQuestion::getDifficultyLevel)
                        .thenComparing(QuizQuestion::getId))
                .forEach(question -> {
                    List<QuizAnswerOption> options = validatedBalanceOptions(question);
                    groups.computeIfAbsent(
                            new DistributionGroup(question.getDifficultyLevel(), options.size()),
                            ignored -> new ArrayList<>()).add(question);
                });

        List<QuestionAssignment> assignments = new ArrayList<>();
        List<AnswerDistributionPreviewResponse.GroupDistribution> before = new ArrayList<>();
        List<AnswerDistributionPreviewResponse.GroupDistribution> after = new ArrayList<>();

        for (Map.Entry<DistributionGroup, List<QuizQuestion>> entry : groups.entrySet()) {
            DistributionGroup group = entry.getKey();
            int[] beforeCounts = new int[group.optionCount()];
            int[] afterCounts = new int[group.optionCount()];

            for (QuizQuestion question : entry.getValue()) {
                List<QuizAnswerOption> options = validatedBalanceOptions(question);
                int currentPosition = correctPosition(options);
                beforeCounts[currentPosition - 1]++;

                int targetPosition = leastUsedPosition(afterCounts, currentPosition);
                afterCounts[targetPosition - 1]++;
                assignments.add(new QuestionAssignment(question.getId(), options, currentPosition, targetPosition));
            }

            before.add(distribution(group, beforeCounts));
            after.add(distribution(group, afterCounts));
        }

        return new BalancePlan(
                assignments,
                new AnswerDistributionPreviewResponse(questions.size(), before, after));
    }

    private List<QuizAnswerOption> validatedBalanceOptions(QuizQuestion question) {
        List<QuizAnswerOption> options = new ArrayList<>(question.getActiveOptions());
        if (options.size() < 2 || options.size() > 3
                || options.stream().filter(option -> Boolean.TRUE.equals(option.getIsCorrect())).count() != 1) {
            throw new IllegalArgumentException(messages.get("admin.quiz.invalid_options"));
        }
        return options;
    }

    private int correctPosition(List<QuizAnswerOption> options) {
        for (int index = 0; index < options.size(); index++) {
            if (Boolean.TRUE.equals(options.get(index).getIsCorrect())) {
                return index + 1;
            }
        }
        throw new IllegalArgumentException(messages.get("admin.quiz.invalid_options"));
    }

    private int leastUsedPosition(int[] counts, int currentPosition) {
        int minimum = java.util.Arrays.stream(counts).min().orElse(0);
        if (counts[currentPosition - 1] == minimum) {
            return currentPosition;
        }
        for (int index = 0; index < counts.length; index++) {
            if (counts[index] == minimum) {
                return index + 1;
            }
        }
        return 1;
    }

    private AnswerDistributionPreviewResponse.GroupDistribution distribution(
            DistributionGroup group,
            int[] counts) {
        int total = java.util.Arrays.stream(counts).sum();
        List<CorrectAnswerDistributionResponse.AnswerPosition> positions = new ArrayList<>();
        for (int index = 0; index < counts.length; index++) {
            double percentage = total == 0 ? 0.0 : Math.round(counts[index] * 1000.0 / total) / 10.0;
            positions.add(new CorrectAnswerDistributionResponse.AnswerPosition(
                    String.valueOf((char) ('A' + index)), counts[index], percentage));
        }
        return new AnswerDistributionPreviewResponse.GroupDistribution(
                group.difficulty().name(), group.optionCount(), total, positions);
    }

    private record DistributionGroup(QuizQuestion.DifficultyLevel difficulty, int optionCount) {
    }

    private record QuestionAssignment(
            Long questionId,
            List<QuizAnswerOption> options,
            int currentPosition,
            int targetPosition) {
    }

    private record BalancePlan(
            List<QuestionAssignment> assignments,
            AnswerDistributionPreviewResponse response) {
    }

    private void validateRequestedOptionIds(
            QuizQuestion question,
            List<AdminQuizQuestionRequest.OptionDTO> requestedOptions) {
        Set<Long> activeOptionIds = question.getActiveOptions().stream()
                .map(QuizAnswerOption::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<Long> requestedIds = new HashSet<>();

        for (AdminQuizQuestionRequest.OptionDTO option : requestedOptions) {
            Long optionId = option.getId();
            if (optionId == null) {
                continue;
            }
            if (!requestedIds.add(optionId) || !activeOptionIds.contains(optionId)) {
                throw new IllegalArgumentException(messages.get("admin.quiz.option_not_owned", optionId));
            }
        }
    }

    private void addUniqueOptionText(Set<String> values, String value, String language) {
        if (!values.add(normalizeOptionText(value))) {
            throw new IllegalArgumentException(messages.get("admin.quiz.option_text_duplicate", language));
        }
    }

    private String normalizeOptionText(String value) {
        return normalize(value).replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private void validateImageReference(String imageReference) {
        if (imageReference == null || imageReference.isBlank()) {
            return;
        }

        String value = imageReference.trim();
        if (value.matches("^/images/quiz/[A-Za-z0-9][A-Za-z0-9._-]*\\.(?i:jpg|jpeg|png|webp)$")) {
            return;
        }

        try {
            URI uri = new URI(value);
            if ("https".equalsIgnoreCase(uri.getScheme()) && uri.getHost() != null &&
                    uri.getUserInfo() == null) {
                return;
            }
        } catch (URISyntaxException ignored) {
            // Converted to the API's normal 400 validation response below.
        }

        throw new IllegalArgumentException(messages.get("admin.quiz.image_reference_invalid"));
    }

    private String normalizeImageReference(String imageReference) {
        return imageReference == null || imageReference.isBlank() ? null : imageReference.trim();
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

    private QuizUpdateAudit captureAudit(QuizQuestion question, AdminQuizQuestionRequest request) {
        Set<String> changedFields = new LinkedHashSet<>();
        addChanged(changedFields, "questionEn", question.getQuestionEn(), request.getQuestionEn());
        addChanged(changedFields, "questionAr", question.getQuestionAr(), request.getQuestionAr());
        addChanged(changedFields, "questionNl", question.getQuestionNl(), request.getQuestionNl());
        addChanged(changedFields, "questionFr", question.getQuestionFr(), request.getQuestionFr());
        addChanged(changedFields, "explanationEn", question.getExplanationEn(), request.getExplanationEn());
        addChanged(changedFields, "explanationAr", question.getExplanationAr(), request.getExplanationAr());
        addChanged(changedFields, "explanationNl", question.getExplanationNl(), request.getExplanationNl());
        addChanged(changedFields, "explanationFr", question.getExplanationFr(), request.getExplanationFr());
        addChanged(changedFields, "contentImageUrl", question.getContentImageUrl(), request.getContentImageUrl());

        if (!Objects.equals(question.getIsActive(), request.getIsActive())) changedFields.add("isActive");
        if (question.getDifficultyLevel() != parseDifficultyOrDefault(request.getDifficultyLevel())) changedFields.add("difficultyLevel");
        if (question.getQuestionType() != parseQuestionType(request.getQuestionType())) changedFields.add("questionType");
        if (question.getCategory() == null || !Objects.equals(question.getCategory().getCode(), request.getCategoryCode())) {
            changedFields.add("categoryCode");
        }

        List<QuizAnswerOption> activeOptions = question.getActiveOptions();
        Set<Long> requestedIds = request.getOptions().stream()
                .map(AdminQuizQuestionRequest.OptionDTO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        int added = (int) request.getOptions().stream().filter(option -> option.getId() == null).count();
        int removed = (int) activeOptions.stream()
                .filter(option -> option.getId() != null && !requestedIds.contains(option.getId()))
                .count();
        if (added > 0 || removed > 0 || optionsContentChanged(activeOptions, request.getOptions())) {
            changedFields.add("options");
        }

        Long oldCorrectId = activeOptions.stream()
                .filter(option -> Boolean.TRUE.equals(option.getIsCorrect()))
                .map(QuizAnswerOption::getId)
                .findFirst()
                .orElse(null);
        Long requestedCorrectId = request.getOptions().stream()
                .filter(option -> Boolean.TRUE.equals(option.getIsCorrect()))
                .map(AdminQuizQuestionRequest.OptionDTO::getId)
                .findFirst()
                .orElse(null);
        boolean correctChanged = !Objects.equals(oldCorrectId, requestedCorrectId);
        if (correctChanged) changedFields.add("correctAnswer");

        String oldImage = normalize(question.getContentImageUrl());
        String newImage = normalize(request.getContentImageUrl());
        String imageAction = Objects.equals(oldImage, newImage) ? "UNCHANGED"
                : oldImage.isEmpty() ? "ADDED"
                : newImage.isEmpty() ? "REMOVED" : "REPLACED";

        return new QuizUpdateAudit(
                currentAdmin(),
                question.getId(),
                List.copyOf(changedFields),
                correctChanged,
                added,
                removed,
                imageAction,
                LocalDateTime.now());
    }

    private void addChanged(Set<String> changedFields, String field, String before, String after) {
        if (!Objects.equals(normalize(before), normalize(after))) {
            changedFields.add(field);
        }
    }

    private boolean optionsContentChanged(
            List<QuizAnswerOption> existing,
            List<AdminQuizQuestionRequest.OptionDTO> requested) {
        Map<Long, QuizAnswerOption> existingById = existing.stream()
                .filter(option -> option.getId() != null)
                .collect(Collectors.toMap(QuizAnswerOption::getId, option -> option));
        return requested.stream()
                .filter(option -> option.getId() != null)
                .anyMatch(option -> {
                    QuizAnswerOption current = existingById.get(option.getId());
                    return current == null ||
                            !Objects.equals(normalize(current.getOptionTextEn()), normalize(option.getTextEn())) ||
                            !Objects.equals(normalize(current.getOptionTextAr()), normalize(option.getTextAr())) ||
                            !Objects.equals(normalize(current.getOptionTextNl()), normalize(option.getTextNl())) ||
                            !Objects.equals(normalize(current.getOptionTextFr()), normalize(option.getTextFr())) ||
                            !Objects.equals(current.getDisplayOrder(), option.getDisplayOrder());
                });
    }

    private String currentAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                ? authentication.getName()
                : "system";
    }

    private void auditAfterCommit(QuizUpdateAudit audit) {
        Runnable writeAudit = () -> log.info(
                "AUDIT admin_quiz_update actor={} questionId={} changedFields={} correctAnswerChanged={} " +
                        "optionsAdded={} optionsArchived={} imageAction={} timestamp={}",
                audit.adminUser(), audit.questionId(), audit.changedFields(), audit.correctAnswerChanged(),
                audit.optionsAdded(), audit.optionsArchived(), audit.imageAction(), audit.timestamp());

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    writeAudit.run();
                }
            });
        } else {
            writeAudit.run();
        }
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

        question.setExplanationEn(DrivingTextSanitizer.sanitize("EN", normalize(request.getExplanationEn())));
        question.setExplanationAr(DrivingTextSanitizer.sanitize("AR", normalize(request.getExplanationAr())));
        question.setExplanationNl(DrivingTextSanitizer.sanitize("NL", normalize(request.getExplanationNl())));
        question.setExplanationFr(DrivingTextSanitizer.sanitize("FR", normalize(request.getExplanationFr())));
        question.setContextSpecific(false);
        question.setRequiresSignImage(false);

        question.setContentImageUrl(normalizeImageReference(request.getContentImageUrl()));
        question.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        // Parse enums with defaults
        question.setQuestionType(parseQuestionType(request.getQuestionType()));
        question.setDifficultyLevel(parseDifficultyOrDefault(request.getDifficultyLevel()));
    }

    private AdminQuizQuestionResponse toResponse(QuizQuestion q) {
        List<AdminQuizQuestionResponse.OptionResponse> options = q.getActiveOptions().stream()
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
                DrivingTextSanitizer.sanitize("EN", q.getExplanationEn()),
                DrivingTextSanitizer.sanitize("AR", q.getExplanationAr()),
                DrivingTextSanitizer.sanitize("NL", q.getExplanationNl()),
                DrivingTextSanitizer.sanitize("FR", q.getExplanationFr()),
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
            return Sort.by(Sort.Direction.DESC, "createdAt")
                    .and(Sort.by(Sort.Direction.DESC, "id"));
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
            return Sort.by(Sort.Direction.DESC, "createdAt")
                    .and(Sort.by(Sort.Direction.DESC, "id"));
        }
        Sort sort = Sort.by(direction, field);
        return "id".equals(field) ? sort : sort.and(Sort.by(Sort.Direction.DESC, "id"));
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
            throw new IllegalArgumentException(messages.get("admin.quiz.difficulty_invalid", level));
        }
    }

    private QuizQuestion.QuestionType parseQuestionType(String type) {
        if (type == null || type.isBlank())
            return QuizQuestion.QuestionType.MULTIPLE_CHOICE;
        try {
            return QuizQuestion.QuestionType.valueOf(type.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(messages.get("admin.quiz.type_invalid", type));
        }
    }

    private record QuizUpdateAudit(
            String adminUser,
            Long questionId,
            List<String> changedFields,
            boolean correctAnswerChanged,
            int optionsAdded,
            int optionsArchived,
            String imageAction,
            LocalDateTime timestamp) {
    }
}
