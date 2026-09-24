package com.readyroad.readyroadbackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.Lesson;
import com.readyroad.readyroadbackend.domain.entity.LessonCategory;
import com.readyroad.readyroadbackend.domain.entity.LessonDraft;
import com.readyroad.readyroadbackend.domain.entity.LessonMediaAsset;
import com.readyroad.readyroadbackend.domain.entity.LessonPage;
import com.readyroad.readyroadbackend.domain.entity.LessonVersion;
import com.readyroad.readyroadbackend.domain.enums.CategoryContentScope;
import com.readyroad.readyroadbackend.domain.enums.LessonMediaStatus;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.LessonCategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.LessonDraftRepository;
import com.readyroad.readyroadbackend.domain.repository.LessonMediaAssetRepository;
import com.readyroad.readyroadbackend.domain.repository.LessonRepository;
import com.readyroad.readyroadbackend.domain.repository.LessonVersionRepository;
import com.readyroad.readyroadbackend.dto.admin.AdminLessonDtos.CategoryLinkResponse;
import com.readyroad.readyroadbackend.dto.admin.AdminLessonDtos.DraftResponse;
import com.readyroad.readyroadbackend.dto.admin.AdminLessonDtos.LessonDetail;
import com.readyroad.readyroadbackend.dto.admin.AdminLessonDtos.LessonSummary;
import com.readyroad.readyroadbackend.dto.admin.AdminLessonDtos.MediaAssetResponse;
import com.readyroad.readyroadbackend.dto.admin.AdminLessonDtos.TheoryCategoryResponse;
import com.readyroad.readyroadbackend.dto.admin.AdminLessonDtos.VersionSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminLessonService {

    private static final Set<CategoryContentScope> LESSON_CATEGORY_SCOPES =
            Set.of(CategoryContentScope.THEORETICAL_EXAM);

    private final LessonRepository lessonRepository;
    private final LessonDraftRepository draftRepository;
    private final LessonVersionRepository versionRepository;
    private final LessonCategoryRepository lessonCategoryRepository;
    private final LessonMediaAssetRepository mediaRepository;
    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper;

    public List<LessonSummary> listLessons() {

        Sort sort = Sort.by(
                Sort.Order.asc("displayOrder"),
                Sort.Order.asc("id"));

        return lessonRepository.findAll(sort)
                .stream()
                .map(this::toSummary)
                .toList();
    }

    public LessonDetail getLesson(String idOrCode) {

        Lesson lesson = resolveLesson(idOrCode);

        LessonVersion latestVersion =
                requireLatestVersion(lesson.getId());

        LessonDraft draft =
                draftRepository.findById(lesson.getId())
                        .orElse(null);

        List<CategoryLinkResponse> categories =
                lessonCategoryRepository
                        .findAllByLesson_IdOrderByDisplayOrderAsc(lesson.getId())
                        .stream()
                        .map(this::toCategoryResponse)
                        .toList();

        List<MediaAssetResponse> media =
                mediaRepository
                        .findAllByLesson_IdOrderByIdDesc(lesson.getId())
                        .stream()
                        .map(this::toMediaResponse)
                        .toList();

        List<VersionSummary> versions =
                versionRepository
                        .findAllByLesson_IdOrderByVersionNumberDesc(lesson.getId())
                        .stream()
                        .map(this::toVersionSummary)
                        .toList();

        return new LessonDetail(
                lesson.getId(),
                lesson.getLessonCode(),
                lesson.getTitleAr(),
                lesson.getTitleNl(),
                lesson.getTitleFr(),
                lesson.getTitleEn(),
                Boolean.TRUE.equals(lesson.getIsActive()),
                lesson.getDisplayOrder(),
                lesson.getEstimatedMinutes(),
                lesson.getPages().size(),
                latestVersion.getVersionNumber(),
                toHttpDocument(latestVersion.getDocument()),
                draft != null ? toDraftResponse(draft) : null,
                categories,
                media,
                versions);
    }

    public List<VersionSummary> getVersionHistory(String idOrCode) {

        Lesson lesson = resolveLesson(idOrCode);

        return versionRepository
                .findAllByLesson_IdOrderByVersionNumberDesc(lesson.getId())
                .stream()
                .map(this::toVersionSummary)
                .toList();
    }

    public List<TheoryCategoryResponse> getTheoryCategories() {

        return categoryRepository
                .findAllByIsActiveTrueAndContentScopeInOrderByDisplayOrderAsc(
                        LESSON_CATEGORY_SCOPES)
                .stream()
                .filter(category ->
                        category.getCode() != null
                                && category.getCode().matches("^TH[0-9]{2}$"))
                .map(category -> new TheoryCategoryResponse(
                        category.getId(),
                        category.getCode(),
                        category.getNameAr(),
                        category.getNameNl(),
                        category.getNameFr(),
                        category.getNameEn(),
                        category.getDisplayOrder()))
                .toList();
    }

    @Transactional
    public DraftResponse getOrCreateDraft(
            String idOrCode,
            Long actorUserId) {

        requireActor(actorUserId);

        Lesson lesson = resolveLesson(idOrCode);

        LessonDraft existing =
                draftRepository.findById(lesson.getId())
                        .orElse(null);

        if (existing != null) {
            return toDraftResponse(existing);
        }

        LessonVersion latest =
                requireLatestVersion(lesson.getId());

        LessonDraft draft = new LessonDraft(
                lesson,
                latest.getVersionNumber(),
                copyJson(latest.getDocument()),
                actorUserId);

        LessonDraft saved =
                draftRepository.saveAndFlush(draft);

        return toDraftResponse(saved);
    }

    @Transactional
    public DraftResponse saveDraft(
            String idOrCode,
            long expectedRevision,
            Map<String, Object> document,
            Long actorUserId) {

        requireActor(actorUserId);

        if (expectedRevision < 0) {
            throw new IllegalArgumentException(
                    "Draft revision cannot be negative");
        }

        Lesson lesson = resolveLesson(idOrCode);

        LessonDraft draft =
                draftRepository.findById(lesson.getId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "Lesson draft does not exist. Load the draft first."));

        if (draft.getRevision() != expectedRevision) {

            throw new ObjectOptimisticLockingFailureException(
                    LessonDraft.class,
                    lesson.getId());
        }

        JsonNode internalDocument =
                toInternalDocument(document);

        validateDraftDocument(
                lesson,
                internalDocument);

        draft.updateDocument(
                copyJson(internalDocument),
                actorUserId);

        LessonDraft saved =
                draftRepository.saveAndFlush(draft);

        return toDraftResponse(saved);
    }

    @Transactional
    public VersionSummary publishDraft(
            String idOrCode,
            long expectedRevision,
            String changeNote,
            Long actorUserId) {

        requireActor(actorUserId);

        if (expectedRevision < 0) {
            throw new IllegalArgumentException(
                    "Draft revision cannot be negative");
        }

        Lesson lesson =
                resolveLesson(idOrCode);

        LessonDraft draft =
                draftRepository
                        .findByLessonIdForUpdate(lesson.getId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "Lesson draft does not exist. Load the draft first."));

        if (!Objects.equals(
                draft.getRevision(),
                expectedRevision)) {

            throw new ObjectOptimisticLockingFailureException(
                    LessonDraft.class,
                    lesson.getId());
        }

        LessonVersion latest =
                requireLatestVersion(lesson.getId());

        if (draft.getBaseVersionNumber()
                != latest.getVersionNumber()) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Lesson draft is based on an older published version.");
        }

        JsonNode document =
                copyJson(draft.getDocument());

        validateDraftDocument(
                lesson,
                document);

        validateCorePublishScope(document);

        applyPublishedLessonFields(
                lesson,
                requireObject(document, "lesson"));

        /*
         * Important:
         * lesson_pages has UNIQUE (lesson_id, page_number).
         *
         * Flush orphan removal BEFORE inserting replacement rows,
         * otherwise Hibernate could attempt INSERT before DELETE.
         */
        lesson.clearPages();

        lessonRepository.saveAndFlush(lesson);

        applyPublishedPages(
                lesson,
                requireArray(document, "pages"));

        lessonRepository.saveAndFlush(lesson);

        int nextVersionNumber =
                latest.getVersionNumber() + 1;

        LessonVersion publishedVersion =
                versionRepository.saveAndFlush(
                        LessonVersion.cms(
                                lesson,
                                nextVersionNumber,
                                copyJson(document),
                                normalizeChangeNote(changeNote),
                                actorUserId));

        draftRepository.delete(draft);
        draftRepository.flush();

        return toVersionSummary(publishedVersion);
    }

    private LessonSummary toSummary(Lesson lesson) {

        LessonVersion latest =
                requireLatestVersion(lesson.getId());

        LessonDraft draft =
                draftRepository.findById(lesson.getId())
                        .orElse(null);

        int categoryCount =
                Math.toIntExact(
                        lessonCategoryRepository.countByLesson_Id(
                                lesson.getId()));

        long mediaCount =
                mediaRepository.countByLesson_IdAndStatus(
                        lesson.getId(),
                        LessonMediaStatus.ACTIVE);

        return new LessonSummary(
                lesson.getId(),
                lesson.getLessonCode(),
                lesson.getTitleAr(),
                lesson.getTitleNl(),
                lesson.getTitleFr(),
                lesson.getTitleEn(),
                Boolean.TRUE.equals(lesson.getIsActive()),
                editorState(lesson, draft),
                lesson.getDisplayOrder(),
                lesson.getEstimatedMinutes(),
                lesson.getPages().size(),
                latest.getVersionNumber(),
                draft != null,
                draft != null ? draft.getRevision() : null,
                categoryCount,
                mediaCount,
                lesson.getUpdatedAt());
    }

    private String editorState(
            Lesson lesson,
            LessonDraft draft) {

        boolean active =
                Boolean.TRUE.equals(lesson.getIsActive());

        if (active && draft != null) {
            return "PUBLISHED_WITH_DRAFT";
        }

        if (!active && draft != null) {
            return "UNPUBLISHED_WITH_DRAFT";
        }

        return active
                ? "PUBLISHED"
                : "UNPUBLISHED";
    }

    private Lesson resolveLesson(String idOrCode) {

        if (idOrCode == null || idOrCode.isBlank()) {
            throw new IllegalArgumentException(
                    "Lesson id or code is required");
        }

        String value = idOrCode.trim();

        try {

            Long id = Long.parseLong(value);

            return lessonRepository.findById(id)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Lesson not found: " + value));

        } catch (NumberFormatException ignored) {

            return lessonRepository
                    .findByLessonCode(value)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Lesson not found: " + value));
        }
    }

    private LessonVersion requireLatestVersion(
            Long lessonId) {

        return versionRepository
                .findTopByLesson_IdOrderByVersionNumberDesc(
                        lessonId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Published lesson version is missing for lesson "
                                        + lessonId));
    }

    private void validateDraftDocument(
            Lesson lesson,
            JsonNode document) {

        if (document == null || !document.isObject()) {
            throw new IllegalArgumentException(
                    "Lesson document must be a JSON object");
        }

        JsonNode schemaVersion =
                document.get("schemaVersion");

        if (schemaVersion == null
                || !schemaVersion.isIntegralNumber()
                || schemaVersion.asInt() != 1) {

            throw new IllegalArgumentException(
                    "Unsupported lesson document schemaVersion");
        }

        JsonNode lessonNode =
                requireObject(
                        document,
                        "lesson");

        String lessonCode =
                requireText(
                        lessonNode,
                        "lessonCode");

        if (!Objects.equals(
                lesson.getLessonCode(),
                lessonCode)) {

            throw new IllegalArgumentException(
                    "lessonCode is locked and cannot be changed");
        }

        JsonNode title =
                requireObject(
                        lessonNode,
                        "title");

        validateLockedTitle(
                lesson,
                title);

        JsonNode isActive =
                lessonNode.get("isActive");

        if (isActive != null
                && !isActive.isNull()) {

            if (!isActive.isBoolean()) {
                throw new IllegalArgumentException(
                        "lesson.isActive must be boolean");
            }

            if (isActive.asBoolean()
                    != Boolean.TRUE.equals(
                            lesson.getIsActive())) {

                throw new IllegalArgumentException(
                        "Publish state cannot be changed inside a lesson draft");
            }
        }

        JsonNode displayOrder =
                lessonNode.get("displayOrder");

        if (displayOrder != null
                && !displayOrder.isNull()) {

            if (!displayOrder.isIntegralNumber()
                    || displayOrder.asInt() < 0) {

                throw new IllegalArgumentException(
                        "lesson.displayOrder must be zero or greater");
            }
        }

        JsonNode estimatedMinutes =
                lessonNode.get("estimatedMinutes");

        if (estimatedMinutes != null
                && !estimatedMinutes.isNull()) {

            if (!estimatedMinutes.isIntegralNumber()
                    || estimatedMinutes.asInt() < 1) {

                throw new IllegalArgumentException(
                        "lesson.estimatedMinutes must be positive");
            }
        }

        validatePages(
                requireArray(document, "pages"));

        requireArray(
                document,
                "structuredSections");

        JsonNode seo =
                requireObject(
                        document,
                        "seo");

        for (String language :
                List.of("ar", "nl", "fr", "en")) {

            requireObject(
                    seo,
                    language);
        }

        requireArray(
                document,
                "media");

        validateCategoryLinks(
                requireArray(
                        document,
                        "categoryLinks"));
    }

    private void validateLockedTitle(
            Lesson lesson,
            JsonNode title) {

        assertLockedText(
                "AR",
                lesson.getTitleAr(),
                requireText(title, "ar"));

        assertLockedText(
                "NL",
                lesson.getTitleNl(),
                requireText(title, "nl"));

        assertLockedText(
                "FR",
                lesson.getTitleFr(),
                requireText(title, "fr"));

        assertLockedText(
                "EN",
                lesson.getTitleEn(),
                requireText(title, "en"));
    }

    private void assertLockedText(
            String language,
            String published,
            String draft) {

        if (!Objects.equals(
                published,
                draft)) {

            throw new IllegalArgumentException(
                    "Main lesson title is locked and cannot be changed ("
                            + language
                            + ")");
        }
    }

    private void validatePages(
            JsonNode pages) {

        if (pages.isEmpty()) {
            throw new IllegalArgumentException(
                    "Lesson must contain at least one page");
        }

        Set<Integer> pageNumbers =
                new HashSet<>();

        for (JsonNode page : pages) {

            if (!page.isObject()) {
                throw new IllegalArgumentException(
                        "Each lesson page must be an object");
            }

            JsonNode pageNumber =
                    page.get("pageNumber");

            if (pageNumber == null
                    || !pageNumber.isIntegralNumber()
                    || pageNumber.asInt() < 1) {

                throw new IllegalArgumentException(
                        "Each lesson page requires a positive pageNumber");
            }

            if (!pageNumbers.add(
                    pageNumber.asInt())) {

                throw new IllegalArgumentException(
                        "Duplicate lesson pageNumber: "
                                + pageNumber.asInt());
            }

            JsonNode pageTitle =
                    requireObject(
                            page,
                            "title");

            for (String language :
                    List.of("ar", "nl", "fr", "en")) {

                requireText(
                        pageTitle,
                        language);
            }

            requireObject(
                    page,
                    "content");

            requireObject(
                    page,
                    "bulletPointsRaw");
        }
    }

    private void validateCategoryLinks(
            JsonNode categoryLinks) {

        Set<String> codes =
                new HashSet<>();

        int primaryCount = 0;

        for (JsonNode link :
                categoryLinks) {

            if (!link.isObject()) {
                throw new IllegalArgumentException(
                        "Each category link must be an object");
            }

            String code =
                    requireText(
                            link,
                            "code");

            if (!code.matches("^TH[0-9]{2}$")) {
                throw new IllegalArgumentException(
                        "Invalid theoretical category code: "
                                + code);
            }

            if (!codes.add(code)) {
                throw new IllegalArgumentException(
                        "Duplicate lesson category: "
                                + code);
            }

            Category category =
                    categoryRepository
                            .findByCode(code)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Unknown theoretical category: "
                                                    + code));

            if (!Boolean.TRUE.equals(
                    category.getIsActive())
                    || category.getContentScope()
                    != CategoryContentScope.THEORETICAL_EXAM) {

                throw new IllegalArgumentException(
                        "Inactive or invalid lesson category: "
                                + code);
            }

            JsonNode primary =
                    link.get("primary");

            if (primary != null
                    && !primary.isNull()) {

                if (!primary.isBoolean()) {
                    throw new IllegalArgumentException(
                            "categoryLinks.primary must be boolean");
                }

                if (primary.asBoolean()) {
                    primaryCount++;
                }
            }

            JsonNode displayOrder =
                    link.get("displayOrder");

            if (displayOrder != null
                    && !displayOrder.isNull()) {

                if (!displayOrder.isIntegralNumber()
                        || displayOrder.asInt() < 0) {

                    throw new IllegalArgumentException(
                            "categoryLinks.displayOrder must be zero or greater");
                }
            }
        }

        if (primaryCount > 1) {
            throw new IllegalArgumentException(
                    "A lesson may have only one primary category");
        }
    }

    private void validateCorePublishScope(
            JsonNode document) {

        JsonNode structuredSections =
                requireArray(
                        document,
                        "structuredSections");

        if (!structuredSections.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Structured section publishing is not enabled yet.");
        }

        JsonNode media =
                requireArray(
                        document,
                        "media");

        if (!media.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Lesson media publishing is not enabled yet.");
        }

        JsonNode categoryLinks =
                requireArray(
                        document,
                        "categoryLinks");

        if (!categoryLinks.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Lesson category publishing is not enabled yet.");
        }

        JsonNode seo =
                requireObject(
                        document,
                        "seo");

        for (String language :
                List.of("ar", "nl", "fr", "en")) {

            JsonNode languageSeo =
                    requireObject(
                            seo,
                            language);

            if (!languageSeo.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Lesson SEO publishing is not enabled yet.");
            }
        }
    }


    private void applyPublishedLessonFields(
            Lesson lesson,
            JsonNode lessonNode) {

        JsonNode description =
                requireObject(
                        lessonNode,
                        "description");

        lesson.setDescriptionAr(
                nullableText(
                        description,
                        "ar"));

        lesson.setDescriptionNl(
                nullableText(
                        description,
                        "nl"));

        lesson.setDescriptionFr(
                nullableText(
                        description,
                        "fr"));

        lesson.setDescriptionEn(
                nullableText(
                        description,
                        "en"));

        lesson.setIcon(
                nullableText(
                        lessonNode,
                        "icon"));

        JsonNode displayOrder =
                lessonNode.get(
                        "displayOrder");

        if (displayOrder != null
                && !displayOrder.isNull()) {

            lesson.setDisplayOrder(
                    displayOrder.asInt());
        }

        JsonNode estimatedMinutes =
                lessonNode.get(
                        "estimatedMinutes");

        if (estimatedMinutes != null
                && !estimatedMinutes.isNull()) {

            lesson.setEstimatedMinutes(
                    estimatedMinutes.asInt());
        }

        /*
         * lessonCode, title and isActive are intentionally
         * not written here. They are locked by validation.
         */
    }


    private void applyPublishedPages(
            Lesson lesson,
            JsonNode pages) {

        for (JsonNode pageNode : pages) {

            LessonPage page =
                    new LessonPage();

            page.setPageNumber(
                    pageNode
                            .get("pageNumber")
                            .asInt());

            JsonNode title =
                    requireObject(
                            pageNode,
                            "title");

            page.setTitleAr(
                    requireText(
                            title,
                            "ar"));

            page.setTitleNl(
                    requireText(
                            title,
                            "nl"));

            page.setTitleFr(
                    requireText(
                            title,
                            "fr"));

            page.setTitleEn(
                    requireText(
                            title,
                            "en"));

            JsonNode content =
                    requireObject(
                            pageNode,
                            "content");

            page.setContentAr(
                    nullableText(
                            content,
                            "ar"));

            page.setContentNl(
                    nullableText(
                            content,
                            "nl"));

            page.setContentFr(
                    nullableText(
                            content,
                            "fr"));

            page.setContentEn(
                    nullableText(
                            content,
                            "en"));

            JsonNode bulletPointsRaw =
                    requireObject(
                            pageNode,
                            "bulletPointsRaw");

            page.setBulletPointsAr(
                    nullableText(
                            bulletPointsRaw,
                            "ar"));

            page.setBulletPointsNl(
                    nullableText(
                            bulletPointsRaw,
                            "nl"));

            page.setBulletPointsFr(
                    nullableText(
                            bulletPointsRaw,
                            "fr"));

            page.setBulletPointsEn(
                    nullableText(
                            bulletPointsRaw,
                            "en"));

            lesson.addPage(page);
        }
    }


    private String nullableText(
            JsonNode parent,
            String field) {

        JsonNode value =
                parent.get(field);

        if (value == null
                || value.isNull()) {

            return null;
        }

        if (!value.isTextual()) {

            throw new IllegalArgumentException(
                    field + " must contain text or null");
        }

        return value.asText();
    }


    private String normalizeChangeNote(
            String changeNote) {

        if (changeNote == null) {
            return null;
        }

        String normalized =
                changeNote.trim();

        return normalized.isEmpty()
                ? null
                : normalized;
    }

    private JsonNode requireObject(
            JsonNode parent,
            String field) {

        JsonNode value =
                parent.get(field);

        if (value == null
                || !value.isObject()) {

            throw new IllegalArgumentException(
                    field + " must be a JSON object");
        }

        return value;
    }

    private JsonNode requireArray(
            JsonNode parent,
            String field) {

        JsonNode value =
                parent.get(field);

        if (value == null
                || !value.isArray()) {

            throw new IllegalArgumentException(
                    field + " must be a JSON array");
        }

        return value;
    }

    private String requireText(
            JsonNode parent,
            String field) {

        JsonNode value =
                parent.get(field);

        if (value == null
                || !value.isTextual()
                || value.asText().isBlank()) {

            throw new IllegalArgumentException(
                    field + " must contain text");
        }

        return value.asText();
    }

    private void requireActor(
            Long actorUserId) {

        if (actorUserId == null
                || actorUserId <= 0) {

            throw new IllegalArgumentException(
                    "Authenticated admin user is required");
        }
    }

    private DraftResponse toDraftResponse(
            LessonDraft draft) {

        return new DraftResponse(
                draft.getLessonId(),
                draft.getBaseVersionNumber(),
                draft.getRevision(),
                toHttpDocument(draft.getDocument()),
                draft.getCreatedByUserId(),
                draft.getUpdatedByUserId(),
                draft.getCreatedAt(),
                draft.getUpdatedAt());
    }

    private VersionSummary toVersionSummary(
            LessonVersion version) {

        return new VersionSummary(
                version.getId(),
                version.getVersionNumber(),
                version.getSource().name(),
                version.getChangeNote(),
                version.getPublishedByUserId(),
                version.getPublishedAt());
    }

    private CategoryLinkResponse toCategoryResponse(
            LessonCategory link) {

        Category category =
                link.getCategory();

        return new CategoryLinkResponse(
                category.getId(),
                category.getCode(),
                category.getNameAr(),
                category.getNameNl(),
                category.getNameFr(),
                category.getNameEn(),
                link.isPrimary(),
                link.getDisplayOrder());
    }

    private MediaAssetResponse toMediaResponse(
            LessonMediaAsset asset) {

        return new MediaAssetResponse(
                asset.getId(),
                asset.getStorageKey(),
                asset.getStorageProvider().name(),
                asset.getOriginalFilename(),
                asset.getMimeType(),
                asset.getSizeBytes(),
                asset.getWidth(),
                asset.getHeight(),
                asset.getSha256(),
                asset.getStatus().name(),
                asset.getUploadedByUserId(),
                asset.getCreatedAt(),
                asset.getArchivedAt());
    }

    private Map<String, Object> toHttpDocument(
            JsonNode document) {

        if (document == null) {
            return null;
        }

        return objectMapper.convertValue(
                document,
                new TypeReference<Map<String, Object>>() {
                });
    }

    private JsonNode toInternalDocument(
            Map<String, Object> document) {

        if (document == null) {
            throw new IllegalArgumentException(
                    "Lesson document is required");
        }

        JsonNode internalDocument =
                objectMapper.valueToTree(document);

        if (internalDocument == null
                || !internalDocument.isObject()) {

            throw new IllegalArgumentException(
                    "Lesson document must be a JSON object");
        }

        return internalDocument;
    }

    private JsonNode copyJson(
            JsonNode document) {

        if (document == null) {
            return null;
        }

        return document.deepCopy();
    }
}