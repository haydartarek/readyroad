package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.enums.CategoryContentScope;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.dto.admin.AdminTheoryBankHealthDtos.CategoryResponse;
import com.readyroad.readyroadbackend.dto.admin.AdminTheoryCategoryRequest;
import java.util.Locale;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminTheoryCategoryService {

    private static final int MAX_THEORY_CATEGORY_SEQUENCE = 99_999_999;

    private final CategoryRepository categoryRepository;

    public AdminTheoryCategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    @CacheEvict(cacheNames = "categories", allEntries = true)
    public CategoryResponse createCategory(AdminTheoryCategoryRequest request) {
        Category category = new Category();
        category.setCode(nextTheoryCode());
        apply(category, request);
        return response(categoryRepository.save(category));
    }

    @Transactional
    @CacheEvict(cacheNames = "categories", allEntries = true)
    public CategoryResponse updateCategory(
            long categoryId,
            AdminTheoryCategoryRequest request) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Theory category not found"));

        String requestedCode = optional(request.code());

        if (requestedCode != null
                && !category.getCode().equalsIgnoreCase(requestedCode)) {
            throw new IllegalArgumentException(
                    "Category code is a stable identifier and cannot be changed");
        }

        apply(category, request);
        return response(categoryRepository.save(category));
    }

    private String nextTheoryCode() {
        for (int sequence = 1;
                sequence <= MAX_THEORY_CATEGORY_SEQUENCE;
                sequence++) {

            String code = String.format(Locale.ROOT, "TH%02d", sequence);

            if (!categoryRepository.existsByCode(code)) {
                return code;
            }
        }

        throw new IllegalStateException(
                "No available theoretical category code");
    }

    private static void apply(
            Category category,
            AdminTheoryCategoryRequest request) {

        CategoryContentScope scope =
                CategoryContentScope.valueOf(request.contentScope());

        if (!scope.supportsTheoreticalExam()) {
            throw new IllegalArgumentException(
                    "Theory categories must support theoretical exams");
        }

        category.setNameEn(required(request.nameEn()));
        category.setNameNl(required(request.nameNl()));
        category.setNameFr(required(request.nameFr()));
        category.setNameAr(required(request.nameAr()));

        category.setDescriptionEn(optional(request.descriptionEn()));
        category.setDescriptionNl(optional(request.descriptionNl()));
        category.setDescriptionFr(optional(request.descriptionFr()));
        category.setDescriptionAr(optional(request.descriptionAr()));

        category.setDisplayOrder(request.displayOrder());
        category.setIsActive(request.active());
        category.setContentScope(scope);

        category.setExamTargetWeight(
                TheoryExamBlueprintPolicy.effectiveCategoryWeight(
                        request.examTargetWeight()));
    }

    private static CategoryResponse response(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getCode(),
                category.getNameEn(),
                category.getNameNl(),
                category.getNameFr(),
                category.getNameAr(),
                category.getDescriptionEn(),
                category.getDescriptionNl(),
                category.getDescriptionFr(),
                category.getDescriptionAr(),
                category.getDisplayOrder(),
                Boolean.TRUE.equals(category.getIsActive()),
                category.getContentScope().name(),
                category.getExamTargetWeight());
    }

    private static String required(String value) {
        return value.trim();
    }

    private static String optional(String value) {
        return value == null || value.isBlank()
                ? null
                : value.trim();
    }
}
