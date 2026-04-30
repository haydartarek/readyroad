package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.enums.SignCategory;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import com.readyroad.readyroadbackend.domain.repository.SignExamRepository;
import com.readyroad.readyroadbackend.mapper.TrafficSignMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrafficSignServiceDisplayGroupCountTest {

    @Mock
    private RoadSignRepository roadSignRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TrafficSignMapper trafficSignMapper;

    @Mock
    private SignExamRepository signExamRepository;

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
    }

    @Test
    void countsDisplayGroupsUsingPublicSignPresentationLogic() {
        when(roadSignRepository.findAllByIsActiveTrue()).thenReturn(List.of(
                sign("A11", SignCategory.DANGER, "/images/signs/danger_signs/A11.png"),
                sign("B1", SignCategory.PRIORITY, "/images/signs/priority_signs/B1.png"),
                sign("C1", SignCategory.PROHIBITION, "/images/signs/prohibition_signs/C1.png"),
                sign("D1", SignCategory.MANDATORY, "/images/signs/mandatory_signs/D1.png"),
                sign("E1", SignCategory.PARKING, "/images/signs/parking_signs/E1.png"),
                sign("F1", SignCategory.INFORMATION, "/images/signs/information_signs/F1.png"),
                sign("F39", SignCategory.INFORMATION, "/images/signs/road_markings/F39.png"),
                sign("GIII", SignCategory.ADDITIONAL, "/images/signs/additional_signs/GIII.png"),
                sign("ZE1", SignCategory.ZONE, "/images/signs/zone_signs/ZE1.png"),
                sign("F50", SignCategory.INFORMATION, "/images/signs/information_signs/F50.png")));

        assertEquals(9, trafficSignService.countActiveDisplayGroups());
    }

    private static RoadSign sign(String signCode, SignCategory category, String imagePath) {
        RoadSign sign = new RoadSign();
        sign.setSignCode(signCode);
        sign.setCategory(category);
        sign.setImagePath(imagePath);
        sign.setIsActive(true);
        return sign;
    }
}
