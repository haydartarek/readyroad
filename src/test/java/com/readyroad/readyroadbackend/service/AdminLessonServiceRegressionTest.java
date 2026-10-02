package com.readyroad.readyroadbackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.Lesson;
import com.readyroad.readyroadbackend.domain.entity.LessonDraft;
import com.readyroad.readyroadbackend.domain.entity.LessonMediaAsset;
import com.readyroad.readyroadbackend.domain.entity.LessonPage;
import com.readyroad.readyroadbackend.domain.entity.LessonVersion;
import com.readyroad.readyroadbackend.domain.enums.LessonMediaStatus;
import com.readyroad.readyroadbackend.domain.enums.LessonMediaStorageProvider;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.LessonCategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.LessonDraftRepository;
import com.readyroad.readyroadbackend.domain.repository.LessonMediaAssetRepository;
import com.readyroad.readyroadbackend.domain.repository.LessonRepository;
import com.readyroad.readyroadbackend.domain.repository.LessonVersionRepository;
import com.readyroad.readyroadbackend.storage.MediaStorageProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminLessonServiceRegressionTest {

    private LessonRepository lessonRepository;
    private LessonDraftRepository draftRepository;
    private LessonVersionRepository versionRepository;
    private LessonCategoryRepository lessonCategoryRepository;
    private LessonMediaAssetRepository mediaRepository;
    private CategoryRepository categoryRepository;
    private FileUploadService fileUploadService;
    private JdbcTemplate jdbcTemplate;
    private MediaStorageProperties mediaStorageProperties;

    private ObjectMapper objectMapper;
    private AdminLessonService service;

    private Lesson lesson;
    private LessonDraft draft;

    @BeforeEach
    void setUp() {
        lessonRepository =
                mock(LessonRepository.class);

        draftRepository =
                mock(LessonDraftRepository.class);

        versionRepository =
                mock(LessonVersionRepository.class);

        lessonCategoryRepository =
                mock(LessonCategoryRepository.class);

        mediaRepository =
                mock(LessonMediaAssetRepository.class);

        categoryRepository =
                mock(CategoryRepository.class);

        fileUploadService =
                mock(FileUploadService.class);

        jdbcTemplate = mock(JdbcTemplate.class);

        mediaStorageProperties =
                new MediaStorageProperties();

        objectMapper =
                new ObjectMapper();

        service =
                new AdminLessonService(
                        lessonRepository,
                        draftRepository,
                        versionRepository,
                        lessonCategoryRepository,
                        mediaRepository,
                        categoryRepository,
                        objectMapper,
                        fileUploadService,
                        jdbcTemplate,
                        mediaStorageProperties);

        lesson =
                mock(Lesson.class);

        draft =
                mock(LessonDraft.class);

        when(lesson.getId())
                .thenReturn(1L);

        when(lesson.getLessonCode())
                .thenReturn("TH01");

        when(lesson.getTitleAr())
                .thenReturn("Arabic title");

        when(lesson.getTitleNl())
                .thenReturn("Dutch title");

        when(lesson.getTitleFr())
                .thenReturn("French title");

        when(lesson.getTitleEn())
                .thenReturn("English title");

        when(lesson.getIsActive())
                .thenReturn(true);

        when(
                lessonRepository
                        .findByLessonCode("TH01"))
                .thenReturn(
                        Optional.of(lesson));

        when(
                draftRepository
                        .findById(1L))
                .thenReturn(
                        Optional.of(draft));

        when(
                draftRepository
                        .findByLessonIdForUpdate(1L))
                .thenReturn(
                        Optional.of(draft));

        when(draft.getRevision())
                .thenReturn(4L);
    }

    @Test
    void publishRejectsStaleDraftRevisionBeforeMutation() {

        assertThrows(
                ObjectOptimisticLockingFailureException.class,
                () ->
                        service.publishDraft(
                                "TH01",
                                3L,
                                "Updated lesson",
                                7L));

        verify(
                versionRepository,
                never())
                .findTopByLesson_IdOrderByVersionNumberDesc(
                        anyLong());

        verify(
                draftRepository,
                never())
                .delete(any());
    }

    @Test
    void saveDraftRejectsImageAssetOutsideActiveLessonScope() {

        Map<String, Object> document =
                validDocument(99L);

        when(
                mediaRepository
                        .findByIdAndLesson_IdAndStatus(
                                99L,
                                1L,
                                LessonMediaStatus.ACTIVE))
                .thenReturn(
                        Optional.empty());

        IllegalArgumentException error =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                service.saveDraft(
                                        "TH01",
                                        4L,
                                        document,
                                        7L));

        assertEquals(
                "Active lesson media asset not found: 99",
                error.getMessage());

        verify(
                mediaRepository)
                .findByIdAndLesson_IdAndStatus(
                        99L,
                        1L,
                        LessonMediaStatus.ACTIVE);

        verify(
                draftRepository,
                never())
                .saveAndFlush(any());
    }

    @Test
    void saveDraftAcceptsActiveImageAssetBelongingToLesson() {

        LessonMediaAsset asset =
                mock(LessonMediaAsset.class);

        Map<String, Object> document =
                validDocument(99L);

        JsonNode internalDocument =
                objectMapper.valueToTree(
                        document);

        when(
                mediaRepository
                        .findByIdAndLesson_IdAndStatus(
                                99L,
                                1L,
                                LessonMediaStatus.ACTIVE))
                .thenReturn(
                        Optional.of(asset));

        when(
                draftRepository
                        .saveAndFlush(draft))
                .thenReturn(draft);

        when(draft.getLessonId())
                .thenReturn(1L);

        when(draft.getBaseVersionNumber())
                .thenReturn(2);

        when(draft.getDocument())
                .thenReturn(
                        internalDocument);

        when(draft.getCreatedByUserId())
                .thenReturn(7L);

        when(draft.getUpdatedByUserId())
                .thenReturn(7L);

        when(draft.getCreatedAt())
                .thenReturn(
                        Instant.parse(
                                "2026-09-24T10:00:00Z"));

        when(draft.getUpdatedAt())
                .thenReturn(
                        Instant.parse(
                                "2026-09-24T10:05:00Z"));

        var response =
                service.saveDraft(
                        "TH01",
                        4L,
                        document,
                        7L);

        assertNotNull(response);

        assertEquals(
                1L,
                response.lessonId());

        verify(
                mediaRepository)
                .findByIdAndLesson_IdAndStatus(
                        99L,
                        1L,
                        LessonMediaStatus.ACTIVE);

        verify(
                draft)
                .updateDocument(
                        any(JsonNode.class),
                        eq(7L));

        verify(
                draftRepository)
                .saveAndFlush(
                        draft);
    }

    @Test
    void uploadMediaUsesLocalProviderMetadataByDefault() {

        when(fileUploadService.uploadLessonImage(
                any(),
                eq("TH01"),
                eq("page-1")))
                .thenReturn("/images/lessons/TH01/page-1.png");

        LessonMediaAsset saved = mock(LessonMediaAsset.class);
        when(saved.getId()).thenReturn(99L);
        when(saved.getStorageKey()).thenReturn("lessons/TH01/page-1.png");
        when(saved.getStorageProvider()).thenReturn(LessonMediaStorageProvider.LOCAL);
        when(saved.getOriginalFilename()).thenReturn("page-1.png");
        when(saved.getMimeType()).thenReturn("image/png");
        when(saved.getSizeBytes()).thenReturn(3L);
        when(saved.getStatus()).thenReturn(LessonMediaStatus.ACTIVE);
        when(mediaRepository.saveAndFlush(any(LessonMediaAsset.class)))
                .thenReturn(saved);

        service.uploadMedia(
                "TH01",
                new MockMultipartFile(
                        "file",
                        "page-1.png",
                        "image/png",
                        new byte[]{1, 2, 3}),
                "page-1",
                7L);

        var captor = forClass(LessonMediaAsset.class);
        verify(mediaRepository).saveAndFlush(captor.capture());
        assertEquals(
                LessonMediaStorageProvider.LOCAL,
                captor.getValue().getStorageProvider());
        assertEquals(
                "lessons/TH01/page-1.png",
                captor.getValue().getStorageKey());
    }

    @Test
    void uploadMediaUsesObjectStorageMetadataWhenS3IsSelected() {

        mediaStorageProperties.setProvider("s3");

        when(fileUploadService.uploadLessonImage(
                any(),
                eq("TH01"),
                eq("page-1")))
                .thenReturn("/images/lessons/TH01/page-1.png");

        LessonMediaAsset saved = mock(LessonMediaAsset.class);
        when(saved.getId()).thenReturn(99L);
        when(saved.getStorageKey()).thenReturn("lessons/TH01/page-1.png");
        when(saved.getStorageProvider()).thenReturn(LessonMediaStorageProvider.OBJECT_STORAGE);
        when(saved.getOriginalFilename()).thenReturn("page-1.png");
        when(saved.getMimeType()).thenReturn("image/png");
        when(saved.getSizeBytes()).thenReturn(3L);
        when(saved.getStatus()).thenReturn(LessonMediaStatus.ACTIVE);
        when(mediaRepository.saveAndFlush(any(LessonMediaAsset.class)))
                .thenReturn(saved);

        service.uploadMedia(
                "TH01",
                new MockMultipartFile(
                        "file",
                        "page-1.png",
                        "image/png",
                        new byte[]{1, 2, 3}),
                "page-1",
                7L);

        var captor = forClass(LessonMediaAsset.class);
        verify(mediaRepository).saveAndFlush(captor.capture());
        assertEquals(
                LessonMediaStorageProvider.OBJECT_STORAGE,
                captor.getValue().getStorageProvider());
        assertEquals(
                "lessons/TH01/page-1.png",
                captor.getValue().getStorageKey());
    }

    @Test
    void purgeMediaRemovesLiveAndHistoricalReferencesBeforeDeletingAsset() throws Exception {

        LessonMediaAsset asset = mock(LessonMediaAsset.class);
        when(asset.getId()).thenReturn(99L);
        when(asset.getLesson()).thenReturn(lesson);
        when(asset.getStorageKey()).thenReturn("lessons/TH01/page-1.png");
        when(asset.getStorageProvider()).thenReturn(LessonMediaStorageProvider.LOCAL);

        LessonPage page = new LessonPage();
        page.setImageAsset(asset);
        when(lesson.getPages()).thenReturn(List.of(page));
        when(lessonRepository.findAll()).thenReturn(List.of(lesson));

        JsonNode draftDocument = objectMapper.readTree(
                "{\"pages\":[{\"imageAssetId\":99}]}" );
        when(draft.getDocument()).thenReturn(draftDocument);
        when(draft.getUpdatedByUserId()).thenReturn(7L);
        when(draftRepository.findAll()).thenReturn(List.of(draft));

        JsonNode versionDocument = objectMapper.readTree(
                "{\"pages\":[{\"imageAssetId\":99}],\"media\":{\"imageAssetId\":99}}" );
        LessonVersion version = LessonVersion.cms(
                lesson,
                1,
                versionDocument,
                "initial",
                7L);
        org.springframework.test.util.ReflectionTestUtils.setField(version, "id", 5L);
        when(versionRepository.findAll()).thenReturn(List.of(version));
        when(mediaRepository.findById(99L)).thenReturn(Optional.of(asset));
        when(draftRepository.findById(1L)).thenReturn(Optional.empty());

        service.purgeMedia(99L);

        assertTrue(page.getImageAsset() == null);
        verify(draft).updateDocument(
                org.mockito.ArgumentMatchers.argThat(document ->
                        document.at("/pages/0/imageAssetId").isNull()),
                eq(7L));
        verify(jdbcTemplate).update(
                eq("UPDATE lesson_versions SET document = CAST(? AS jsonb) WHERE id = ?"),
                eq("{\"pages\":[{\"imageAssetId\":null}],\"media\":{\"imageAssetId\":null}}"),
                eq(5L));
        verify(fileUploadService).deleteLessonImage("lessons/TH01/page-1.png");
        verify(mediaRepository).delete(asset);
    }

    private Map<String, Object> validDocument(
            Long imageAssetId) {

        Map<String, Object> lessonNode =
                new LinkedHashMap<>();

        lessonNode.put(
                "lessonCode",
                "TH01");

        lessonNode.put(
                "title",
                Map.of(
                        "ar", "Arabic title",
                        "nl", "Dutch title",
                        "fr", "French title",
                        "en", "English title"));

        lessonNode.put(
                "description",
                Map.of(
                        "ar", "",
                        "nl", "",
                        "fr", "",
                        "en", "Description"));

        lessonNode.put(
                "icon",
                "");

        lessonNode.put(
                "displayOrder",
                1);

        lessonNode.put(
                "estimatedMinutes",
                10);

        lessonNode.put(
                "isActive",
                true);

        Map<String, Object> page =
                new LinkedHashMap<>();

        page.put(
                "pageNumber",
                1);

        page.put(
                "title",
                Map.of(
                        "ar", "Page AR",
                        "nl", "Page NL",
                        "fr", "Page FR",
                        "en", "Page EN"));

        page.put(
                "content",
                Map.of(
                        "ar", "",
                        "nl", "",
                        "fr", "",
                        "en", "Content"));

        if (imageAssetId != null) {
            page.put(
                    "imageAssetId",
                    imageAssetId);
        }

        Map<String, Object> seo =
                new LinkedHashMap<>();

        seo.put(
                "ar",
                Map.of());

        seo.put(
                "nl",
                Map.of());

        seo.put(
                "fr",
                Map.of());

        seo.put(
                "en",
                Map.of());

        Map<String, Object> document =
                new LinkedHashMap<>();

        document.put(
                "schemaVersion",
                1);

        document.put(
                "lesson",
                lessonNode);

        document.put(
                "pages",
                List.of(page));

        document.put(
                "structuredSections",
                List.of());

        document.put(
                "seo",
                seo);

        document.put(
                "media",
                List.of());

        document.put(
                "categoryLinks",
                List.of());

        return document;
    }
}
