package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.enums.CategoryContentScope;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.dto.admin.AdminTheoryCategoryRequest;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminTheoryCategoryServiceTest {

    @Mock
    CategoryRepository categoryRepository;

    private AdminTheoryCategoryService service;

    @BeforeEach
    void setUp() {
        service = new AdminTheoryCategoryService(categoryRepository);
    }

    @Test
    void createGeneratesStableTheoryCodeAndDefaultWeight() {
        for (int sequence = 1; sequence <= 8; sequence++) {
            when(categoryRepository.existsByCode(
                    String.format("TH%02d", sequence)))
                    .thenReturn(true);
        }

        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocation -> {
                    Category category = invocation.getArgument(0);
                    category.setId(9L);
                    return category;
                });

        var response = service.createCategory(
                request(
                        "ADMIN_TYPED_CODE",
                        "Emergency situations",
                        null,
                        true,
                        9));

        assertThat(response.id()).isEqualTo(9L);
        assertThat(response.code()).isEqualTo("TH09");
        assertThat(response.nameEn()).isEqualTo("Emergency situations");
        assertThat(response.examTargetWeight())
                .isEqualTo(TheoryExamBlueprintPolicy.DEFAULT_CATEGORY_WEIGHT);
        assertThat(response.active()).isTrue();
        assertThat(response.contentScope())
                .isEqualTo("THEORETICAL_EXAM");
    }

    @Test
    void updateCanRenameCategoryWithoutChangingStableCode() {
        Category category = category(
                4L,
                "TH04",
                "Parking",
                10);

        when(categoryRepository.findById(4L))
                .thenReturn(Optional.of(category));

        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.updateCategory(
                4L,
                request(
                        null,
                        "Parking and stopping",
                        18,
                        false,
                        12));

        assertThat(response.id()).isEqualTo(4L);
        assertThat(response.code()).isEqualTo("TH04");
        assertThat(response.nameEn())
                .isEqualTo("Parking and stopping");
        assertThat(response.examTargetWeight()).isEqualTo(18);
        assertThat(response.active()).isFalse();
        assertThat(response.displayOrder()).isEqualTo(12);
    }

    @Test
    void updateWithMissingWeightUsesTheDefaultInsteadOfLeavingNull() {
        Category category = category(
                9L,
                "TH09",
                "Emergency situations",
                null);

        when(categoryRepository.findById(9L))
                .thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.updateCategory(
                9L,
                request(
                        null,
                        "Emergency situations",
                        null,
                        true,
                        9));

        assertThat(response.code()).isEqualTo("TH09");
        assertThat(response.examTargetWeight())
                .isEqualTo(TheoryExamBlueprintPolicy.DEFAULT_CATEGORY_WEIGHT);
    }
    @Test
    void updateRejectsAttemptToChangeStableCode() {
        Category category = category(
                4L,
                "TH04",
                "Parking",
                10);

        when(categoryRepository.findById(4L))
                .thenReturn(Optional.of(category));

        assertThatThrownBy(() ->
                service.updateCategory(
                        4L,
                        request(
                                "TH99",
                                "Parking",
                                10,
                                true,
                                4)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("stable identifier");
    }
    @Test
    void updateRejectsLegacyTheoryCategory() {
        Category legacy = category(
                29L,
                "TH_PRI",
                "Legacy priority",
                10);

        when(categoryRepository.findById(29L))
                .thenReturn(Optional.of(legacy));

        assertThatThrownBy(() ->
                service.updateCategory(
                        29L,
                        request(
                                null,
                                "Legacy priority",
                                10,
                                true,
                                29)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Legacy theory categories");
    }


    private static AdminTheoryCategoryRequest request(
            String code,
            String nameEn,
            Integer weight,
            boolean active,
            int displayOrder) {

        return new AdminTheoryCategoryRequest(
                code,
                nameEn,
                "Regels",
                "Regles",
                "القواعد",
                null,
                null,
                null,
                null,
                displayOrder,
                active,
                "THEORETICAL_EXAM",
                weight);
    }

    private static Category category(
            long id,
            String code,
            String nameEn,
            Integer weight) {

        Category category = new Category();
        category.setId(id);
        category.setCode(code);
        category.setNameEn(nameEn);
        category.setNameNl("Regels");
        category.setNameFr("Regles");
        category.setNameAr("القواعد");
        category.setDisplayOrder((int) id);
        category.setIsActive(true);
        category.setContentScope(
                CategoryContentScope.THEORETICAL_EXAM);
        category.setExamTargetWeight(weight);
        return category;
    }
}
