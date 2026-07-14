package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.SignImportRun;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SignQuizDataInitializerTest {

    @Mock
    private RoadSignRepository roadSignRepository;

    @Mock
    private SignQuizImportService signQuizImportService;

    @Mock
    private CanonicalSignCatalogService canonicalSignCatalogService;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Test
    void reconcilesCanonicalContentWhenDatabaseCountsAreAlreadyComplete() {
        @SuppressWarnings("unchecked")
        List<CanonicalSignCatalogService.CanonicalSignSeed> seeds = mock(List.class);
        SignImportRun run = mock(SignImportRun.class);

        when(seeds.size()).thenReturn(184);
        when(canonicalSignCatalogService.getCanonicalSeeds()).thenReturn(seeds);
        when(roadSignRepository.countByIsActiveTrue()).thenReturn(184L);
        when(jdbcTemplate.queryForObject(anyString(), org.mockito.ArgumentMatchers.eq(Long.class)))
                .thenReturn(1472L, 184L);
        when(signQuizImportService.runImport("SYSTEM_INIT")).thenReturn(run);
        when(run.getStatus()).thenReturn("SUCCESS");

        SignQuizDataInitializer initializer = new SignQuizDataInitializer(
                roadSignRepository,
                signQuizImportService,
                canonicalSignCatalogService,
                jdbcTemplate);

        initializer.onApplicationReady();

        verify(signQuizImportService, times(1)).runImport("SYSTEM_INIT");
    }
}
