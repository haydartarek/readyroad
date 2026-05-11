package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import com.readyroad.readyroadbackend.domain.repository.SignExamRepository;
import com.readyroad.readyroadbackend.dto.CreateTrafficSignRequest;
import com.readyroad.readyroadbackend.dto.response.TrafficSignResponse;
import com.readyroad.readyroadbackend.mapper.TrafficSignMapper;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrafficSignServiceNormalizationTest {

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

        when(trafficSignMapper.toResponse(any(RoadSign.class))).thenReturn(dummyResponse());
        when(roadSignRepository.save(any(RoadSign.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void createSignNormalizesSignCodeUsingRootLocale() {
        Locale previousLocale = Locale.getDefault();
        Locale.setDefault(Locale.forLanguageTag("tr"));

        try {
            CreateTrafficSignRequest request = requestWithSignCode("GIII");
            when(roadSignRepository.existsBySignCode("GIII")).thenReturn(false);

            trafficSignService.createSign(request);

            ArgumentCaptor<RoadSign> signCaptor = ArgumentCaptor.forClass(RoadSign.class);
            verify(roadSignRepository).save(signCaptor.capture());
            assertThat(signCaptor.getValue().getNormalizedSignCode()).isEqualTo("giii");
        } finally {
            Locale.setDefault(previousLocale);
        }
    }

    @Test
    void updateSignNormalizesChangedCodeUsingRootLocale() {
        Locale previousLocale = Locale.getDefault();
        Locale.setDefault(Locale.forLanguageTag("tr"));

        try {
            RoadSign existing = new RoadSign();
            existing.setSignCode("A1");
            existing.setNormalizedSignCode("a1");
            existing.setImagePath("/images/signs/existing.png");

            CreateTrafficSignRequest request = requestWithSignCode("GIII");
            when(roadSignRepository.findById(42L)).thenReturn(Optional.of(existing));
            when(roadSignRepository.existsBySignCodeAndIdNot("GIII", 42L)).thenReturn(false);

            trafficSignService.updateSign(42L, request);

            ArgumentCaptor<RoadSign> signCaptor = ArgumentCaptor.forClass(RoadSign.class);
            verify(roadSignRepository).save(signCaptor.capture());
            assertThat(signCaptor.getValue().getNormalizedSignCode()).isEqualTo("giii");
            assertThat(signCaptor.getValue().getImagePath()).isEqualTo("/images/signs/existing.png");
        } finally {
            Locale.setDefault(previousLocale);
        }
    }

    private static CreateTrafficSignRequest requestWithSignCode(String signCode) {
        CreateTrafficSignRequest request = new CreateTrafficSignRequest();
        request.setSignCode(signCode);
        request.setCategoryCode("G");
        request.setNameEn("Supplementary sign");
        request.setNameAr(null);
        request.setNameNl(null);
        request.setNameFr(null);
        request.setDescriptionEn("What does this sign mean?");
        request.setDescriptionAr(null);
        request.setDescriptionNl(null);
        request.setDescriptionFr(null);
        request.setImageUrl(null);
        return request;
    }

    private static TrafficSignResponse dummyResponse() {
        return new TrafficSignResponse(
                1L,
                "GIII",
                "G",
                "GIII",
                null,
                null,
                "",
                "Supplementary sign",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
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