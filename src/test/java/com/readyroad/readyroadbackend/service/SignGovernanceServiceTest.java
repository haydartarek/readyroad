package com.readyroad.readyroadbackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SignGovernanceServiceTest {

    @Mock
    private RoadSignRepository roadSignRepository;

    private CanonicalSignCatalogService canonicalCatalog;
    private SignGovernanceService governanceService;
    private List<RoadSign> canonicalRows;

    @BeforeEach
    void setUp() {
        canonicalCatalog = new CanonicalSignCatalogService(new ObjectMapper(), new DefaultResourceLoader());
        ReflectionTestUtils.setField(
                canonicalCatalog,
                "signsImportPath",
                Path.of("src/main/resources/data/signs_import").toAbsolutePath().normalize().toString());
        canonicalCatalog.refresh();
        governanceService = new SignGovernanceService(roadSignRepository, canonicalCatalog);
        canonicalRows = canonicalCatalog.getCanonicalSeeds().stream().map(SignGovernanceServiceTest::rowFrom).toList();
    }

    @Test
    void exactDerivedDatabasePasses() {
        when(roadSignRepository.findAll()).thenReturn(canonicalRows);

        var result = governanceService.audit();

        assertThat(result.passed()).isTrue();
        assertThat(result.fullyConsistent()).isEqualTo(184);
        assertThat(result.details()).isEmpty();
    }

    @Test
    void changedDatabaseContentIsReportedAsDrift() {
        List<RoadSign> rows = new ArrayList<>(canonicalRows);
        RoadSign drifted = rows.stream().filter(row -> row.getSignCode().equals("A15")).findFirst().orElseThrow();
        drifted.setNameEn("Database-only name");
        drifted.setDescriptionEn("Database-only description");
        drifted.setSummaryEn("Database-only summary");
        drifted.setDriverGuidanceEn("Database-only guidance");
        drifted.setExceptionsEn(List.of("Database-only exception"));
        drifted.setImagePath("/images/signs/database-only.png");
        when(roadSignRepository.findAll()).thenReturn(rows);

        var result = governanceService.audit();

        assertThat(result.passed()).isFalse();
        assertThat(result.details()).anySatisfy(item -> {
            assertThat(item.signCode()).isEqualTo("A15");
            assertThat(item.status()).isEqualTo("MISMATCH");
            assertThat(item.issues()).contains(
                    "name_en: DB does not match sign.json",
                    "description_en: DB does not match sign.json",
                    "summary_en: DB does not match sign.json",
                    "driver_guidance_en: DB does not match sign.json",
                    "exceptions_en: DB does not match sign.json",
                    "image_path: DB does not match sign.json");
        });
    }

    @Test
    void extraAndMissingDatabaseRowsAreReported() {
        List<RoadSign> rows = new ArrayList<>(canonicalRows);
        rows.removeIf(row -> row.getSignCode().equals("F5"));
        RoadSign extra = new RoadSign();
        extra.setSignCode("EXTRA");
        extra.setNormalizedSignCode("extra");
        extra.setIsActive(true);
        rows.add(extra);
        when(roadSignRepository.findAll()).thenReturn(rows);

        var result = governanceService.audit();

        assertThat(result.passed()).isFalse();
        assertThat(result.orphanInDb()).isEqualTo(1);
        assertThat(result.orphanInJson()).isEqualTo(1);
        assertThat(result.details()).anyMatch(item -> item.signCode().equals("EXTRA")
                && item.status().equals("ORPHAN_IN_DB"));
        assertThat(result.details()).anyMatch(item -> item.signCode().equals("F5")
                && item.status().equals("ORPHAN_IN_JSON"));
    }

    private static RoadSign rowFrom(CanonicalSignCatalogService.CanonicalSignSeed seed) {
        RoadSign row = new RoadSign();
        row.setSignCode(seed.routeCode());
        row.setNormalizedSignCode(seed.routeKey());
        row.setCategory(seed.category());
        row.setImagePath(seed.imagePath());
        row.setNameEn(seed.nameEn());
        row.setNameNl(seed.nameNl());
        row.setNameFr(seed.nameFr());
        row.setNameAr(seed.nameAr());
        row.setDescriptionEn(seed.descriptionEn());
        row.setDescriptionNl(seed.descriptionNl());
        row.setDescriptionFr(seed.descriptionFr());
        row.setDescriptionAr(seed.descriptionAr());
        row.setSummaryEn(seed.summaryEn());
        row.setSummaryNl(seed.summaryNl());
        row.setSummaryFr(seed.summaryFr());
        row.setSummaryAr(seed.summaryAr());
        row.setDriverGuidanceEn(seed.driverGuidanceEn());
        row.setDriverGuidanceNl(seed.driverGuidanceNl());
        row.setDriverGuidanceFr(seed.driverGuidanceFr());
        row.setDriverGuidanceAr(seed.driverGuidanceAr());
        row.setExceptionsEn(seed.exceptionsEn());
        row.setExceptionsNl(seed.exceptionsNl());
        row.setExceptionsFr(seed.exceptionsFr());
        row.setExceptionsAr(seed.exceptionsAr());
        row.setSeriousViolation(seed.seriousViolation());
        row.setIsActive(true);
        return row;
    }
}
