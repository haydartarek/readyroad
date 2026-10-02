package com.readyroad.readyroadbackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.Lesson;
import com.readyroad.readyroadbackend.domain.entity.LessonPage;
import com.readyroad.readyroadbackend.domain.repository.LessonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LessonImportServiceIdempotencyTest {

    @Mock
    private LessonRepository lessonRepository;

    private final Map<String, Lesson> storedLessons = new LinkedHashMap<>();
    private LessonImportService lessonImportService;

    @BeforeEach
    void setUp() {
        lessonImportService = new LessonImportService(lessonRepository, new ObjectMapper());
        when(lessonRepository.findByLessonCode(any())).thenAnswer(invocation ->
                Optional.ofNullable(storedLessons.get(invocation.getArgument(0))));
        when(lessonRepository.saveAndFlush(any(Lesson.class))).thenAnswer(invocation -> {
            Lesson lesson = invocation.getArgument(0);
            storedLessons.put(lesson.getLessonCode(), lesson);
            return lesson;
        });
    }

    @Test
    void identicalCanonicalImportIsSkippedWithoutRebuildingLessons() {
        var firstImport = lessonImportService.importFromClasspath();
        var secondImport = lessonImportService.importFromClasspath();

        assertThat(firstImport.created()).isEqualTo(32);
        assertThat(secondImport.created()).isZero();
        assertThat(secondImport.updated()).isZero();
        assertThat(secondImport.skipped()).isEqualTo(32);
        assertThat(secondImport.errors()).isEmpty();
    }

    @Test
    void canonicalImportRestoresTheApprovedVehicleTechnologyTitleAndKeepsItOnRestart() {
        lessonImportService.importFromClasspath();
        Lesson mechanics = storedLessons.get("les-31");
        assertThat(mechanics.getTitleAr()).isEqualTo("الإطارات والفرامل وتقنيات السيارة لرخصة القيادة B");
        mechanics.setTitleAr("أساسيات تكنولوجيا السيارة");

        var repaired = lessonImportService.importFromClasspath();
        var restarted = lessonImportService.importFromClasspath();

        assertThat(repaired.updated()).isOne();
        assertThat(repaired.skipped()).isEqualTo(31);
        assertThat(storedLessons.get("les-31").getTitleAr()).isEqualTo("الإطارات والفرامل وتقنيات السيارة لرخصة القيادة B");
        assertThat(restarted.updated()).isZero();
        assertThat(restarted.skipped()).isEqualTo(32);
    }

    @Test
    void canonicalImportPreservesExistingPageMediaWhenContentChanges() {
        lessonImportService.importFromClasspath();
        Lesson lesson = storedLessons.get("les-0");
        LessonPage page = lesson.getPages().get(0);
        var imageAsset = org.mockito.Mockito.mock(
                com.readyroad.readyroadbackend.domain.entity.LessonMediaAsset.class);
        page.setImageAsset(imageAsset);
        page.setContentEn("stale content");

        lessonImportService.importFromClasspath();

        assertThat(storedLessons.get("les-0").getPages().get(0).getImageAsset())
                .isSameAs(imageAsset);
    }
}
