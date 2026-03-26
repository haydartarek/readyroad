package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.enums.SignCategory;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import com.readyroad.readyroadbackend.dto.response.CategoryResponse;
import com.readyroad.readyroadbackend.mapper.CategoryMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private static final Map<String, SignCategory> LETTER_TO_SIGN_CATEGORY = Map.ofEntries(
            Map.entry("A", SignCategory.DANGER),
            Map.entry("B", SignCategory.PRIORITY),
            Map.entry("C", SignCategory.PROHIBITION),
            Map.entry("D", SignCategory.MANDATORY),
            Map.entry("E", SignCategory.PARKING),
            Map.entry("F", SignCategory.INFORMATION),
            Map.entry("G", SignCategory.ADDITIONAL),
            Map.entry("M", SignCategory.CYCLIST),
            Map.entry("T", SignCategory.DELINEATION),
            Map.entry("Z", SignCategory.ZONE)
    );

    private final CategoryRepository categoryRepository;
    private final RoadSignRepository roadSignRepository;
    private final CategoryMapper categoryMapper;
    private final CanonicalSignCatalogService canonicalSignCatalogService;

    public CategoryService(
            CategoryRepository categoryRepository,
            RoadSignRepository roadSignRepository,
            CategoryMapper categoryMapper,
            CanonicalSignCatalogService canonicalSignCatalogService
    ) {
        this.categoryRepository = categoryRepository;
        this.roadSignRepository = roadSignRepository;
        this.categoryMapper = categoryMapper;
        this.canonicalSignCatalogService = canonicalSignCatalogService;
    }

    public List<CategoryResponse> getAllActiveCategories() {
        return categoryRepository.findAllByIsActiveTrueOrderByDisplayOrderAsc()
                .stream()
                .map(this::toResponseWithSignCount)
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> getAllPublicTrafficSignCategories() {
        Map<SignCategory, Long> publicCounts = getPublicSignCountsByEnum();

        return categoryRepository.findAllByIsActiveTrueOrderByDisplayOrderAsc()
                .stream()
                .map(category -> toResponseWithPublicSignCount(category, publicCounts))
                .filter(response -> response.signCount() > 0)
                .sorted(Comparator.comparing(CategoryResponse::displayOrder).thenComparing(CategoryResponse::code))
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategoryByCode(String code) {
        Category category = categoryRepository.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Category not found: " + code));
        return toResponseWithSignCount(category);
    }

    public CategoryResponse getPublicTrafficSignCategoryByCode(String code) {
        Category category = categoryRepository.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Category not found: " + code));

        CategoryResponse response = toResponseWithPublicSignCount(category, getPublicSignCountsByEnum());
        if (response.signCount() <= 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Public traffic sign category not found: " + code);
        }

        return response;
    }

    private CategoryResponse toResponseWithSignCount(Category category) {
        SignCategory signCategory = LETTER_TO_SIGN_CATEGORY.get(category.getCode());
        long signCount = signCategory == null ? 0L : roadSignRepository.countByCategoryAndIsActiveTrue(signCategory);
        return categoryMapper.toResponse(category, signCount);
    }

    private CategoryResponse toResponseWithPublicSignCount(Category category, Map<SignCategory, Long> publicCounts) {
        SignCategory signCategory = LETTER_TO_SIGN_CATEGORY.get(category.getCode());
        long signCount = signCategory == null ? 0L : publicCounts.getOrDefault(signCategory, 0L);
        return categoryMapper.toResponse(category, signCount);
    }

    private Map<SignCategory, Long> getPublicSignCountsByEnum() {
        return roadSignRepository.findAllByIsActiveTrue()
                .stream()
                .filter(canonicalSignCatalogService::isPubliclyAllowed)
                .collect(Collectors.groupingBy(RoadSign::getCategory, Collectors.counting()));
    }
}

