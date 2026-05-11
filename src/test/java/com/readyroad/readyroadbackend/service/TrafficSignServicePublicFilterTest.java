package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.enums.SignCategory;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import com.readyroad.readyroadbackend.domain.repository.SignExamRepository;
import com.readyroad.readyroadbackend.dto.response.TrafficSignResponse;
import com.readyroad.readyroadbackend.mapper.TrafficSignMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrafficSignServicePublicFilterTest {

    @Mock
    private RoadSignRepository roadSignRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SignExamRepository signExamRepository;

    @Mock
    private TrafficSignMapper trafficSignMapper;

    @Mock
    private CanonicalSignCatalogService canonicalSignCatalogService;

    @Mock
    private BackendMessageService backendMessageService;

    private TrafficSignService trafficSignService;

    @BeforeEach
    void setUp() {
        trafficSignService = new TrafficSignService(
                roadSignRepository,
                categoryRepository,
                signExamRepository,
                trafficSignMapper,
                canonicalSignCatalogService,
                backendMessageService);

        when(canonicalSignCatalogService.isPubliclyAllowed(any(RoadSign.class))).thenReturn(true);
        when(signExamRepository.findBySignIdAndExamNumberAndIsActiveTrue(anyLong(), eq(1)))
                .thenReturn(Optional.empty());
        when(trafficSignMapper.toResponse(any(RoadSign.class)))
                .thenAnswer(invocation -> toResponse(invocation.getArgument(0)));
    }

    @Test
    void combinesCategoryAndQueryFiltersOnCollectionRoute() {
        Category dangerCategory = new Category();
        dangerCategory.setCode("A");
        when(categoryRepository.findById(7L)).thenReturn(Optional.of(dangerCategory));
        when(roadSignRepository.findAllByCategoryAndIsActiveTrue(SignCategory.DANGER)).thenReturn(List.of(
                sign(1L, "A1a", SignCategory.DANGER, "Dangerous bend to the left", "Slow down before the bend"),
                sign(2L, "A14", SignCategory.DANGER, "Slippery road", "Drive carefully on slippery surfaces")));

        List<TrafficSignResponse> response = trafficSignService.getFilteredPublicSigns(7L, "bend");

        assertThat(response)
                .extracting(TrafficSignResponse::signCode)
                .containsExactly("A1a");
    }

    private static RoadSign sign(Long id, String signCode, SignCategory category, String nameEn, String descriptionEn) {
        RoadSign sign = new RoadSign();
        sign.setId(id);
        sign.setSignCode(signCode);
        sign.setCategory(category);
        sign.setNameEn(nameEn);
        sign.setDescriptionEn(descriptionEn);
        sign.setNameAr("");
        sign.setNameNl("");
        sign.setNameFr("");
        sign.setDescriptionAr("");
        sign.setDescriptionNl("");
        sign.setDescriptionFr("");
        sign.setIsActive(true);
        return sign;
    }

    private static TrafficSignResponse toResponse(RoadSign sign) {
        return new TrafficSignResponse(
                sign.getId(),
                sign.getSignCode(),
                "A",
                sign.getSignCode(),
                null,
                null,
                sign.getNameAr(),
                sign.getNameEn(),
                sign.getNameNl(),
                sign.getNameFr(),
                sign.getDescriptionAr(),
                sign.getDescriptionEn(),
                sign.getDescriptionNl(),
                sign.getDescriptionFr(),
                sign.getDescriptionAr(),
                sign.getDescriptionEn(),
                sign.getDescriptionNl(),
                sign.getDescriptionFr(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                null);
    }
}