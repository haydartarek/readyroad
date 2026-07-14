package com.readyroad.readyroadbackend.mapper;

import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.enums.SignCategory;
import com.readyroad.readyroadbackend.dto.response.AdminTrafficSignResponse;
import com.readyroad.readyroadbackend.dto.response.TrafficSignResponse;
import com.readyroad.readyroadbackend.service.CanonicalSignCatalogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrafficSignMapperImagePathTest {

    @Mock
    private CanonicalSignCatalogService canonicalSignCatalogService;

    private TrafficSignMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TrafficSignMapper(canonicalSignCatalogService);
    }

    @Test
    void publicResponseKeepsOfficialImagePathFolderVerbatim() {
        RoadSign sign = sign("F4a", SignCategory.INFORMATION);
        String officialImagePath = "/images/signs/zone_signs/Zone-F4a Zone 30 km.png";

        when(canonicalSignCatalogService.resolve(sign))
                .thenReturn(resolved("F4a", officialImagePath));
        when(canonicalSignCatalogService.routeCodeFor(sign)).thenReturn("F4a");

        TrafficSignResponse response = mapper.toResponse(sign);

        assertEquals(officialImagePath, response.imageUrl());
        assertEquals("Summary EN", response.summaryEn());
        assertEquals("Guidance EN", response.driverGuidanceEn());
        assertEquals(List.of(), response.exceptionsEn());
        assertFalse(response.imageUrl().contains("/information_signs/Zone-F"));
    }

    @Test
    void adminResponseDoesNotOverrideOfficialPathBySignCode() {
        RoadSign sign = sign("F39", SignCategory.ROAD_MANAGEMENT);
        String officialImagePath = "/images/signs/road_markings/F39 Official from catalog.png";

        when(canonicalSignCatalogService.resolve(sign))
                .thenReturn(resolved("F39", officialImagePath));

        AdminTrafficSignResponse response = mapper.toAdminResponse(sign);

        assertEquals(officialImagePath, response.imageUrl());
    }

    private static RoadSign sign(String signCode, SignCategory category) {
        RoadSign sign = new RoadSign();
        sign.setId(1L);
        sign.setSignCode(signCode);
        sign.setCategory(category);
        sign.setIsActive(true);
        return sign;
    }

    private static CanonicalSignCatalogService.ResolvedSignData resolved(
            String signCode,
            String imagePath) {
        return new CanonicalSignCatalogService.ResolvedSignData(
                signCode,
                signCode,
                "Name EN",
                "Name AR",
                "Name NL",
                "Name FR",
                "Description EN",
                "Description AR",
                "Description NL",
                "Description FR",
                "Summary EN",
                "Summary AR",
                "Summary NL",
                "Summary FR",
                "Guidance EN",
                "Guidance AR",
                "Guidance NL",
                "Guidance FR",
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                imagePath,
                true);
    }
}
