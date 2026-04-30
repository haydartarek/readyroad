package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.entity.SignExam;
import com.readyroad.readyroadbackend.domain.enums.SignCategory;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import com.readyroad.readyroadbackend.domain.repository.SignExamRepository;
import com.readyroad.readyroadbackend.dto.CreateTrafficSignRequest;
import com.readyroad.readyroadbackend.dto.response.AdminTrafficSignResponse;
import com.readyroad.readyroadbackend.dto.response.PageResponse;
import com.readyroad.readyroadbackend.dto.response.TrafficSignResponse;
import com.readyroad.readyroadbackend.exception.TrafficSignNotFoundException;
import com.readyroad.readyroadbackend.mapper.TrafficSignMapper;
import com.readyroad.readyroadbackend.util.DrivingTextSanitizer;
import com.readyroad.readyroadbackend.util.RouteCodeNormalizer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

@Service
@Transactional(readOnly = true)
public class TrafficSignService {

    private final RoadSignRepository roadSignRepository;
    private final CategoryRepository categoryRepository;
    private final SignExamRepository signExamRepository;
    private final TrafficSignMapper trafficSignMapper;
    private final CanonicalSignCatalogService canonicalSignCatalogService;
    private final BackendMessageService messages;

    // Letter code → SignCategory enum (used for admin category filter and
    // create/update)
    private static final Map<String, SignCategory> LETTER_TO_SIGN_CATEGORY;
    static {
        LETTER_TO_SIGN_CATEGORY = new HashMap<>();
        LETTER_TO_SIGN_CATEGORY.put("A", SignCategory.DANGER);
        LETTER_TO_SIGN_CATEGORY.put("B", SignCategory.PRIORITY);
        LETTER_TO_SIGN_CATEGORY.put("C", SignCategory.PROHIBITION);
        LETTER_TO_SIGN_CATEGORY.put("D", SignCategory.MANDATORY);
        LETTER_TO_SIGN_CATEGORY.put("E", SignCategory.PARKING);
        LETTER_TO_SIGN_CATEGORY.put("F", SignCategory.INFORMATION);
        LETTER_TO_SIGN_CATEGORY.put("G", SignCategory.ADDITIONAL);
        LETTER_TO_SIGN_CATEGORY.put("M", SignCategory.CYCLIST);
        LETTER_TO_SIGN_CATEGORY.put("T", SignCategory.DELINEATION);
        LETTER_TO_SIGN_CATEGORY.put("Z", SignCategory.ZONE);
    }

    // Allowed sort fields to prevent injection
    private static final List<String> ALLOWED_SORT_FIELDS = List.of(
            "signCode", "nameEn", "nameAr", "nameNl", "nameFr",
            "category", "isActive", "createdAt", "updatedAt", "id");

    private static final Pattern DIRECT_SIGN_CODE_PATTERN = Pattern.compile("^[A-Za-z]+[0-9]+[A-Za-z0-9]*$");

    public TrafficSignService(RoadSignRepository roadSignRepository,
            CategoryRepository categoryRepository,
            SignExamRepository signExamRepository,
            TrafficSignMapper trafficSignMapper,
            CanonicalSignCatalogService canonicalSignCatalogService,
            BackendMessageService messages) {
        this.roadSignRepository = roadSignRepository;
        this.categoryRepository = categoryRepository;
        this.signExamRepository = signExamRepository;
        this.trafficSignMapper = trafficSignMapper;
        this.canonicalSignCatalogService = canonicalSignCatalogService;
        this.messages = messages;
    }

    // ─── Public endpoints ───────────────────────────────

    public List<TrafficSignResponse> getAllActiveSigns() {
        return roadSignRepository.findAllByIsActiveTrueOrderBySignCodeAsc()
                .stream()
                .filter(canonicalSignCatalogService::isPubliclyAllowed)
                .map(this::toPublicResponse)
                .collect(Collectors.toList());
    }

    public List<TrafficSignResponse> getSignsByCategory(Long categoryId) {
        // Look up the category letter code from the DB, then map to SignCategory enum
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(
                        () -> new IllegalArgumentException(messages.get("admin.quiz.category_not_found", categoryId)));
        SignCategory signCategory = LETTER_TO_SIGN_CATEGORY.get(category.getCode());
        if (signCategory == null) {
            return List.of();
        }
        return roadSignRepository.findAllByCategoryAndIsActiveTrue(signCategory)
                .stream()
                .filter(canonicalSignCatalogService::isPubliclyAllowed)
                .map(this::toPublicResponse)
                .collect(Collectors.toList());
    }

    public TrafficSignResponse getSignByCode(String signCode) {
        RoadSign sign = findByRouteOrCode(signCode)
                .orElseThrow(() -> new TrafficSignNotFoundException(signCode));
        if (!canonicalSignCatalogService.isPubliclyAllowed(sign)) {
            throw new TrafficSignNotFoundException(signCode);
        }
        return toPublicResponse(sign);
    }

    public List<TrafficSignResponse> searchTrafficSigns(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllActiveSigns();
        }
        return roadSignRepository.searchRoadSigns(query.trim())
                .stream()
                .filter(canonicalSignCatalogService::isPubliclyAllowed)
                .map(this::toPublicResponse)
                .collect(Collectors.toList());
    }

    public long countActiveSigns() {
        return roadSignRepository.findAllByIsActiveTrue()
                .stream()
                .filter(canonicalSignCatalogService::isPubliclyAllowed)
                .count();
    }

    public long countActiveDisplayGroups() {
        Set<String> groups = new HashSet<>();

        roadSignRepository.findAllByIsActiveTrue()
                .stream()
                .filter(canonicalSignCatalogService::isPubliclyAllowed)
                .map(this::resolveDisplayGroup)
                .forEach(groups::add);

        return groups.size();
    }

    // ─── Admin: Paginated list ─────────────────────────

    /**
     * Paginated, sorted, filtered admin signs list.
     *
     * @param page         0-based page index
     * @param size         page size (capped at 100)
     * @param sortParam    Spring-style sort e.g. "signCode,asc" or "nameEn,desc"
     * @param categoryCode optional category filter (e.g. "A", "B")
     * @param q            optional search query
     */
    public PageResponse<AdminTrafficSignResponse> getAdminSignsPaginated(
            int page, int size, String sortParam, String categoryCode, String q) {

        // Sanitize
        page = Math.max(0, page);
        size = Math.max(1, Math.min(size, 100));

        // Parse sort
        Sort sort = parseSort(sortParam);

        Pageable pageable = PageRequest.of(page, size, sort);

        // Normalize empty strings to null for JPQL :param IS NULL check
        String qFilter = (q != null && !q.isBlank()) ? q.trim() : null;

        // Map letter code → SignCategory enum (null if no filter)
        SignCategory catFilter = null;
        if (categoryCode != null && !categoryCode.isBlank()) {
            catFilter = LETTER_TO_SIGN_CATEGORY.get(categoryCode.trim().substring(0, 1).toUpperCase());
        }

        Page<RoadSign> signPage = roadSignRepository.findAdminSigns(catFilter, qFilter, pageable);

        List<AdminTrafficSignResponse> items = signPage.getContent().stream()
                .map(trafficSignMapper::toAdminResponse)
                .collect(Collectors.toList());

        return PageResponse.of(items, page, size, signPage.getTotalElements());
    }

    // ─── Admin: Single sign by ID ──────────────────────

    public AdminTrafficSignResponse getAdminSignById(Long id) {
        RoadSign sign = roadSignRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(messages.get("admin.sign.not_found", id)));
        return trafficSignMapper.toAdminResponse(sign);
    }

    // ─── Admin: Create ─────────────────────────────────

    @Transactional
    public TrafficSignResponse createSign(CreateTrafficSignRequest request) {
        if (roadSignRepository.existsBySignCode(request.getSignCode())) {
            throw new IllegalArgumentException(messages.get("admin.sign.code_exists", request.getSignCode()));
        }

        SignCategory signCategory = LETTER_TO_SIGN_CATEGORY.get(request.getCategoryCode());
        if (signCategory == null) {
            throw new IllegalArgumentException(messages.get("admin.sign.unknown_category", request.getCategoryCode()));
        }

        RoadSign sign = new RoadSign();
        sign.setSignCode(request.getSignCode());
        sign.setNormalizedSignCode(request.getSignCode().toLowerCase().replaceAll("[^a-z0-9]", "_"));
        sign.setCategory(signCategory);
        sign.setNameEn(request.getNameEn());
        sign.setNameAr(request.getNameAr() != null ? request.getNameAr() : "");
        sign.setNameNl(request.getNameNl() != null ? request.getNameNl() : "");
        sign.setNameFr(request.getNameFr() != null ? request.getNameFr() : "");
        sign.setDescriptionEn(DrivingTextSanitizer.sanitize("EN",
                request.getDescriptionEn() != null ? request.getDescriptionEn() : ""));
        sign.setDescriptionAr(DrivingTextSanitizer.sanitize("AR",
                request.getDescriptionAr() != null ? request.getDescriptionAr() : ""));
        sign.setDescriptionNl(DrivingTextSanitizer.sanitize("NL",
                request.getDescriptionNl() != null ? request.getDescriptionNl() : ""));
        sign.setDescriptionFr(DrivingTextSanitizer.sanitize("FR",
                request.getDescriptionFr() != null ? request.getDescriptionFr() : ""));
        sign.setImagePath(request.getImageUrl());
        sign.setIsActive(true);

        RoadSign saved = roadSignRepository.save(sign);
        return trafficSignMapper.toResponse(saved);
    }

    // ─── Admin: Update ─────────────────────────────────

    @Transactional
    public TrafficSignResponse updateSign(Long id, CreateTrafficSignRequest request) {
        RoadSign sign = roadSignRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(messages.get("admin.sign.not_found", id)));

        // If signCode changed, check for conflicts (excluding current sign)
        if (!sign.getSignCode().equals(request.getSignCode())) {
            if (roadSignRepository.existsBySignCodeAndIdNot(request.getSignCode(), id)) {
                throw new IllegalArgumentException(messages.get("admin.sign.code_exists", request.getSignCode()));
            }
            sign.setSignCode(request.getSignCode());
            sign.setNormalizedSignCode(request.getSignCode().toLowerCase().replaceAll("[^a-z0-9]", "_"));
        }

        // Update category if changed
        SignCategory signCategory = LETTER_TO_SIGN_CATEGORY.get(request.getCategoryCode());
        if (signCategory == null) {
            throw new IllegalArgumentException(messages.get("admin.sign.unknown_category", request.getCategoryCode()));
        }
        sign.setCategory(signCategory);

        // Update all fields
        sign.setNameEn(request.getNameEn());
        sign.setNameAr(request.getNameAr() != null ? request.getNameAr() : "");
        sign.setNameNl(request.getNameNl() != null ? request.getNameNl() : "");
        sign.setNameFr(request.getNameFr() != null ? request.getNameFr() : "");
        sign.setDescriptionEn(DrivingTextSanitizer.sanitize("EN",
                request.getDescriptionEn() != null ? request.getDescriptionEn() : ""));
        sign.setDescriptionAr(DrivingTextSanitizer.sanitize("AR",
                request.getDescriptionAr() != null ? request.getDescriptionAr() : ""));
        sign.setDescriptionNl(DrivingTextSanitizer.sanitize("NL",
                request.getDescriptionNl() != null ? request.getDescriptionNl() : ""));
        sign.setDescriptionFr(DrivingTextSanitizer.sanitize("FR",
                request.getDescriptionFr() != null ? request.getDescriptionFr() : ""));
        if (request.getImageUrl() != null) {
            sign.setImagePath(request.getImageUrl());
        }

        RoadSign saved = roadSignRepository.save(sign);
        return trafficSignMapper.toResponse(saved);
    }

    // ─── Admin: Delete ─────────────────────────────────

    @Transactional
    public void deleteSign(Long id) {
        RoadSign sign = roadSignRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(messages.get("admin.sign.not_found", id)));
        roadSignRepository.delete(sign);
    }

    // ─── Private helpers ───────────────────────────────

    private Sort parseSort(String sortParam) {
        if (sortParam == null || sortParam.isBlank()) {
            return Sort.by(Sort.Direction.ASC, "signCode");
        }

        String[] parts = sortParam.split(",");
        String field = parts[0].trim();
        Sort.Direction direction = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim()))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        // Map frontend-friendly field names
        if ("categoryCode".equals(field)) {
            field = "category";
        }

        if (!ALLOWED_SORT_FIELDS.contains(field)) {
            return Sort.by(Sort.Direction.ASC, "signCode");
        }

        return Sort.by(direction, field);
    }

    private Optional<RoadSign> findByRouteOrCode(String identifier) {
        String raw = RouteCodeNormalizer.resolveLegacyAlias(identifier);
        if (raw.isBlank()) {
            return Optional.empty();
        }

        Optional<RoadSign> byExactCode = roadSignRepository.findFirstActiveBySignCodeCaseSensitive(raw);
        if (byExactCode.isPresent()) {
            return byExactCode;
        }

        if (looksLikeDirectSignCode(raw)) {
            return Optional.empty();
        }

        String routeKey = normalizeRouteKey(raw);
        if (routeKey.isBlank()) {
            return Optional.empty();
        }

        return roadSignRepository.findFirstByNormalizedSignCodeAndIsActiveTrueOrderByIdAsc(routeKey);
    }

    private static boolean looksLikeDirectSignCode(String value) {
        return DIRECT_SIGN_CODE_PATTERN.matcher(value).matches();
    }

    private TrafficSignResponse toPublicResponse(RoadSign sign) {
        TrafficSignResponse base = trafficSignMapper.toResponse(sign);
        Optional<SignExam> exam1 = signExamRepository.findBySignIdAndExamNumberAndIsActiveTrue(sign.getId(), 1);

        return new TrafficSignResponse(
                base.id(),
                base.signCode(),
                base.categoryCode(),
                base.routeCode(),
                exam1.map(SignExam::getTotalQuestions).orElse(null),
                exam1.map(SignExam::getPassingScore).orElse(null),
                base.nameAr(),
                base.nameEn(),
                base.nameNl(),
                base.nameFr(),
                base.descriptionAr(),
                base.descriptionEn(),
                base.descriptionNl(),
                base.descriptionFr(),
                base.meaningAr(),
                base.meaningEn(),
                base.meaningNl(),
                base.meaningFr(),
                base.longDescriptionEn(),
                base.longDescriptionNl(),
                base.longDescriptionFr(),
                base.longDescriptionAr(),
                base.guidanceAr(),
                base.guidanceEn(),
                base.guidanceNl(),
                base.guidanceFr(),
                base.isLongDescriptionComplete(),
                base.imageUrl());
    }

    private String resolveDisplayGroup(RoadSign sign) {
        String imagePath = Optional.ofNullable(sign.getImagePath()).orElse("").toLowerCase(Locale.ROOT);
        if (imagePath.contains("/road_markings/")) {
            return "FM";
        }

        if (sign.getCategory() == null) {
            return fallbackDisplayGroup(sign);
        }

        return switch (sign.getCategory()) {
            case DANGER -> "A";
            case PRIORITY -> "B";
            case PROHIBITION -> "C";
            case MANDATORY -> "D";
            case PARKING -> "E";
            case INFORMATION -> "F";
            case ADDITIONAL -> "G";
            case CYCLIST -> "M";
            case DELINEATION -> "T";
            case ZONE -> "Z";
            case ROAD_MANAGEMENT -> "FM";
        };
    }

    private String fallbackDisplayGroup(RoadSign sign) {
        String code = Optional.ofNullable(sign.getSignCode()).orElse("").trim();
        if (code.isBlank()) {
            return "F";
        }

        String firstLetter = code.substring(0, 1).toUpperCase(Locale.ROOT);
        if (LETTER_TO_SIGN_CATEGORY.containsKey(firstLetter)) {
            return firstLetter;
        }

        return "F";
    }

    private static String normalizeRouteKey(String value) {
        return RouteCodeNormalizer.normalize(value);
    }
}
