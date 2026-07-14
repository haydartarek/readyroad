package com.readyroad.readyroadbackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CanonicalRoadSignSyncServiceTest {

    @Mock
    private RoadSignRepository roadSignRepository;

    private CanonicalSignCatalogService canonicalCatalog;
    private CanonicalRoadSignSyncService syncService;
    private List<RoadSign> canonicalRows;

    @BeforeEach
    void setUp() {
        canonicalCatalog = new CanonicalSignCatalogService(new ObjectMapper(), new DefaultResourceLoader());
        ReflectionTestUtils.setField(
                canonicalCatalog,
                "signsImportPath",
                Path.of("src/main/resources/data/signs_import").toAbsolutePath().normalize().toString());
        canonicalCatalog.refresh();
        syncService = new CanonicalRoadSignSyncService(roadSignRepository, canonicalCatalog);

        canonicalRows = new ArrayList<>();
        long id = 1L;
        for (CanonicalSignCatalogService.CanonicalSignSeed seed : canonicalCatalog.getCanonicalSeeds()) {
            RoadSign row = rowFrom(seed);
            row.setId(id++);
            canonicalRows.add(row);
        }
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void repairsCanonicalDriftAndDeletesExtraDatabaseRows() {
        RoadSign a15 = canonicalRows.stream()
                .filter(row -> row.getSignCode().equals("A15"))
                .findFirst()
                .orElseThrow();
        a15.setDescriptionEn("Database-only description");
        a15.setSummaryEn("Database-only summary");
        a15.setDriverGuidanceEn("Database-only guidance");
        a15.setExceptionsEn(List.of("Database-only exception"));
        a15.setImagePath("/images/signs/database-only.png");
        a15.setSeriousViolation(!Boolean.TRUE.equals(a15.getSeriousViolation()));

        RoadSign extra = new RoadSign();
        extra.setId(999L);
        extra.setSignCode("EXTRA");
        extra.setNormalizedSignCode("extra");
        extra.setIsActive(true);

        List<RoadSign> databaseRows = new ArrayList<>(canonicalRows);
        databaseRows.add(extra);
        when(roadSignRepository.findAll()).thenReturn(databaseRows);

        int changed = syncService.syncCanonicalFields();

        assertThat(changed).isEqualTo(2);
        CanonicalSignCatalogService.CanonicalSignSeed seed = canonicalCatalog.findSeedByRouteCode("A15").orElseThrow();
        assertThat(a15.getDescriptionEn()).isEqualTo(seed.descriptionEn());
        assertThat(a15.getSummaryEn()).isEqualTo(seed.summaryEn());
        assertThat(a15.getDriverGuidanceEn()).isEqualTo(seed.driverGuidanceEn());
        assertThat(a15.getExceptionsEn()).isEqualTo(seed.exceptionsEn());
        assertThat(a15.getImagePath()).isEqualTo(seed.imagePath());
        assertThat(a15.getSeriousViolation()).isEqualTo(seed.seriousViolation());

        ArgumentCaptor<Iterable> saved = ArgumentCaptor.forClass(Iterable.class);
        ArgumentCaptor<Iterable> deleted = ArgumentCaptor.forClass(Iterable.class);
        verify(roadSignRepository).saveAll(saved.capture());
        verify(roadSignRepository).deleteAll(deleted.capture());
        assertThat((Iterable<RoadSign>) saved.getValue()).containsExactly(a15);
        assertThat((Iterable<RoadSign>) deleted.getValue()).containsExactly(extra);
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
