package com.readyroad.readyroadbackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.Lesson;
import com.readyroad.readyroadbackend.domain.entity.LessonPage;
import com.readyroad.readyroadbackend.domain.repository.LessonRepository;
import com.readyroad.readyroadbackend.dto.response.LessonImportResult;
import com.readyroad.readyroadbackend.dto.response.LessonImportResult.LessonImportItem;
import com.readyroad.readyroadbackend.util.DrivingTextSanitizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Service responsible for importing lessons from lessons_content.json into the
 * database.
 * <p>
 * Supports:
 * <ul>
 * <li>Validation of JSON structure</li>
 * <li>Preview / dry-run (no DB writes)</li>
 * <li>Execute (upsert into DB)</li>
 * <li>Idempotent re-imports</li>
 * </ul>
 */
@Service
public class LessonImportService {

    private static final Logger log = LoggerFactory.getLogger(LessonImportService.class);
    private static final String CANONICAL_LESSONS_RESOURCE = "data/lessons_content.json";

    private final LessonRepository lessonRepository;
    private final ObjectMapper objectMapper;

    public LessonImportService(LessonRepository lessonRepository, ObjectMapper objectMapper) {
        this.lessonRepository = lessonRepository;
        this.objectMapper = objectMapper;
    }

    // ═══════════════════════════════════════════════════════════
    // Public entry points
    // ═══════════════════════════════════════════════════════════

    /**
     * Preview import without writing to DB.
     */
    public LessonImportResult preview(InputStream jsonStream) {
        return doImport(jsonStream, true);
    }

    /**
     * Execute the import: upsert all lessons into the DB.
     */
    @Transactional
    public LessonImportResult execute(InputStream jsonStream) {
        return doImport(jsonStream, false);
    }

    /**
     * Import from the single bundled canonical resource.
     */
    @Transactional
    public LessonImportResult importFromClasspath() {
        InputStream is = getClass().getClassLoader().getResourceAsStream(CANONICAL_LESSONS_RESOURCE);
        if (is == null) {
            return new LessonImportResult(false, 0, 0, 0, 0,
                    List.of(CANONICAL_LESSONS_RESOURCE + " not found on classpath"), List.of());
        }
        return doImport(is, false);
    }

    // ═══════════════════════════════════════════════════════════
    // Core import logic
    // ═══════════════════════════════════════════════════════════

    private LessonImportResult doImport(InputStream jsonStream, boolean dryRun) {
        List<String> errors = new ArrayList<>();
        List<LessonImportItem> items = new ArrayList<>();
        int created = 0, updated = 0, skipped = 0;

        JsonNode root;
        try {
            root = objectMapper.readTree(jsonStream);
        } catch (Exception e) {
            log.error("Failed to parse lessons JSON", e);
            return new LessonImportResult(dryRun, 0, 0, 0, 0,
                    List.of("Invalid JSON: " + e.getMessage()), List.of());
        }

        JsonNode lessonsNode = root.path("lessons");
        if (!lessonsNode.isArray()) {
            return new LessonImportResult(dryRun, 0, 0, 0, 0,
                    List.of("JSON must contain a top-level \"lessons\" array"), List.of());
        }

        int totalInFile = lessonsNode.size();

        for (int i = 0; i < lessonsNode.size(); i++) {
            JsonNode node = lessonsNode.get(i);
            String lessonCode = textOr(node, "id", null);
            if (lessonCode == null || lessonCode.isBlank()) {
                errors.add("Lesson at index " + i + " has no 'id' field");
                items.add(new LessonImportItem("?", "?", "ERROR", "Missing 'id'"));
                continue;
            }

            String titleEn = textOr(node, "title_en", textOr(node, "title", ""));

            // Validate required fields
            List<String> fieldErrors = validateLesson(node, i);
            if (!fieldErrors.isEmpty()) {
                errors.addAll(fieldErrors);
                items.add(new LessonImportItem(lessonCode, titleEn, "ERROR",
                        String.join("; ", fieldErrors)));
                continue;
            }

            try {
                if (dryRun) {
                    ImportAction action = previewAction(node, lessonCode, i);
                    items.add(new LessonImportItem(lessonCode, titleEn, action.name(), "Preview"));
                    if (action == ImportAction.CREATED)
                        created++;
                    else if (action == ImportAction.UPDATED)
                        updated++;
                    else
                        skipped++;
                } else {
                    ImportAction action = upsertLesson(node, lessonCode, i);
                    items.add(new LessonImportItem(lessonCode, titleEn, action.name(), "OK"));
                    if (action == ImportAction.CREATED)
                        created++;
                    else if (action == ImportAction.UPDATED)
                        updated++;
                    else
                        skipped++;
                }
            } catch (Exception e) {
                log.error("Error importing lesson {} at index {}", lessonCode, i, e);
                errors.add("Lesson " + lessonCode + ": " + e.getMessage());
                items.add(new LessonImportItem(lessonCode, titleEn, "ERROR", e.getMessage()));
            }
        }

        log.info("Lesson import {} complete: {} created, {} updated, {} skipped, {} errors (total in file: {})",
                dryRun ? "PREVIEW" : "EXECUTE", created, updated, skipped, errors.size(), totalInFile);

        return new LessonImportResult(dryRun, created, updated, skipped, totalInFile, errors, items);
    }

    // ═══════════════════════════════════════════════════════════
    // Upsert a single lesson + its pages
    // ═══════════════════════════════════════════════════════════

    private ImportAction previewAction(JsonNode node, String lessonCode, int index) {
        Optional<Lesson> existing = lessonRepository.findByLessonCode(lessonCode);
        if (existing.isEmpty()) {
            return ImportAction.CREATED;
        }
        return matchesCanonicalLesson(existing.get(), buildCanonicalLesson(node, lessonCode, index))
                ? ImportAction.SKIPPED
                : ImportAction.UPDATED;
    }

    private ImportAction upsertLesson(JsonNode node, String lessonCode, int index) {
        Optional<Lesson> existing = lessonRepository.findByLessonCode(lessonCode);
        boolean isNew = existing.isEmpty();
        Lesson canonical = buildCanonicalLesson(node, lessonCode, index);

        if (!isNew && matchesCanonicalLesson(existing.get(), canonical)) {
            return ImportAction.SKIPPED;
        }

        Lesson lesson = existing.orElseGet(Lesson::new);
        copyLessonFields(canonical, lesson);

        // Clear and rebuild pages
        lesson.clearPages();
        lesson = lessonRepository.saveAndFlush(lesson);

        for (LessonPage canonicalPage : canonical.getPages()) {
            lesson.addPage(copyPage(canonicalPage));
        }
        if (!canonical.getPages().isEmpty()) {
            lessonRepository.saveAndFlush(lesson);
        }

        return isNew ? ImportAction.CREATED : ImportAction.UPDATED;
    }

    private Lesson buildCanonicalLesson(JsonNode node, String lessonCode, int index) {
        Lesson lesson = new Lesson();
        lesson.setLessonCode(lessonCode);
        lesson.setTitleNl(textOr(node, "title_nl", textOr(node, "title", "")));
        lesson.setTitleEn(textOr(node, "title_en", textOr(node, "title", "")));
        lesson.setTitleFr(textOr(node, "title_fr", textOr(node, "title", "")));
        lesson.setTitleAr(textOr(node, "title_ar", textOr(node, "title", "")));
        lesson.setDescriptionNl(sanitize("NL", textOr(node, "description_nl", textOr(node, "description", ""))));
        lesson.setDescriptionEn(sanitize("EN", textOr(node, "description_en", textOr(node, "description", ""))));
        lesson.setDescriptionFr(sanitize("FR", textOr(node, "description_fr", textOr(node, "description", ""))));
        lesson.setDescriptionAr(sanitize("AR", textOr(node, "description_ar", textOr(node, "description", ""))));
        lesson.setIcon(textOr(node, "icon", "📖"));
        lesson.setDisplayOrder(index + 1);
        lesson.setIsActive(true);

        JsonNode pagesNode = node.path("pages");
        int pageCount = pagesNode.isArray() ? pagesNode.size() : 0;
        lesson.setEstimatedMinutes(Math.max(5, pageCount * 3));
        if (pagesNode.isArray()) {
            for (int p = 0; p < pagesNode.size(); p++) {
                JsonNode pageNode = pagesNode.get(p);
                LessonPage page = new LessonPage();
                page.setPageNumber(intOr(pageNode, "pageNumber", p + 1));
                page.setTitleNl(textOr(pageNode, "title_nl", textOr(pageNode, "title", "")));
                page.setTitleEn(textOr(pageNode, "title_en", textOr(pageNode, "title", "")));
                page.setTitleFr(textOr(pageNode, "title_fr", textOr(pageNode, "title", "")));
                page.setTitleAr(textOr(pageNode, "title_ar", textOr(pageNode, "title", "")));
                page.setContentNl(sanitize("NL", textOr(pageNode, "content_nl", textOr(pageNode, "content", ""))));
                page.setContentEn(sanitize("EN", textOr(pageNode, "content_en", textOr(pageNode, "content", ""))));
                page.setContentFr(sanitize("FR", textOr(pageNode, "content_fr", textOr(pageNode, "content", ""))));
                page.setContentAr(sanitize("AR", textOr(pageNode, "content_ar", textOr(pageNode, "content", ""))));
                page.setBulletPointsNl(jsonArrayToString(pageNode, "bulletPoints_nl", "bulletPoints"));
                page.setBulletPointsEn(jsonArrayToString(pageNode, "bulletPoints_en", "bulletPoints"));
                page.setBulletPointsFr(jsonArrayToString(pageNode, "bulletPoints_fr", "bulletPoints"));
                page.setBulletPointsAr(jsonArrayToString(pageNode, "bulletPoints_ar", "bulletPoints"));
                lesson.addPage(page);
            }
        }
        return lesson;
    }

    private void copyLessonFields(Lesson source, Lesson target) {
        target.setLessonCode(source.getLessonCode());
        target.setTitleNl(source.getTitleNl());
        target.setTitleEn(source.getTitleEn());
        target.setTitleFr(source.getTitleFr());
        target.setTitleAr(source.getTitleAr());
        target.setDescriptionNl(source.getDescriptionNl());
        target.setDescriptionEn(source.getDescriptionEn());
        target.setDescriptionFr(source.getDescriptionFr());
        target.setDescriptionAr(source.getDescriptionAr());
        target.setIcon(source.getIcon());
        target.setDisplayOrder(source.getDisplayOrder());
        target.setEstimatedMinutes(source.getEstimatedMinutes());
        target.setIsActive(source.getIsActive());
    }

    private LessonPage copyPage(LessonPage source) {
        LessonPage target = new LessonPage();
        target.setPageNumber(source.getPageNumber());
        target.setTitleNl(source.getTitleNl());
        target.setTitleEn(source.getTitleEn());
        target.setTitleFr(source.getTitleFr());
        target.setTitleAr(source.getTitleAr());
        target.setContentNl(source.getContentNl());
        target.setContentEn(source.getContentEn());
        target.setContentFr(source.getContentFr());
        target.setContentAr(source.getContentAr());
        target.setBulletPointsNl(source.getBulletPointsNl());
        target.setBulletPointsEn(source.getBulletPointsEn());
        target.setBulletPointsFr(source.getBulletPointsFr());
        target.setBulletPointsAr(source.getBulletPointsAr());
        return target;
    }

    private boolean matchesCanonicalLesson(Lesson existing, Lesson canonical) {
        if (!Objects.equals(existing.getLessonCode(), canonical.getLessonCode())
                || !Objects.equals(existing.getTitleNl(), canonical.getTitleNl())
                || !Objects.equals(existing.getTitleEn(), canonical.getTitleEn())
                || !Objects.equals(existing.getTitleFr(), canonical.getTitleFr())
                || !Objects.equals(existing.getTitleAr(), canonical.getTitleAr())
                || !Objects.equals(existing.getDescriptionNl(), canonical.getDescriptionNl())
                || !Objects.equals(existing.getDescriptionEn(), canonical.getDescriptionEn())
                || !Objects.equals(existing.getDescriptionFr(), canonical.getDescriptionFr())
                || !Objects.equals(existing.getDescriptionAr(), canonical.getDescriptionAr())
                || !Objects.equals(existing.getIcon(), canonical.getIcon())
                || !Objects.equals(existing.getDisplayOrder(), canonical.getDisplayOrder())
                || !Objects.equals(existing.getEstimatedMinutes(), canonical.getEstimatedMinutes())
                || !Objects.equals(existing.getIsActive(), canonical.getIsActive())
                || existing.getPages().size() != canonical.getPages().size()) {
            return false;
        }

        for (int index = 0; index < existing.getPages().size(); index++) {
            if (!matchesCanonicalPage(existing.getPages().get(index), canonical.getPages().get(index))) {
                return false;
            }
        }
        return true;
    }

    private boolean matchesCanonicalPage(LessonPage existing, LessonPage canonical) {
        return Objects.equals(existing.getPageNumber(), canonical.getPageNumber())
                && Objects.equals(existing.getTitleNl(), canonical.getTitleNl())
                && Objects.equals(existing.getTitleEn(), canonical.getTitleEn())
                && Objects.equals(existing.getTitleFr(), canonical.getTitleFr())
                && Objects.equals(existing.getTitleAr(), canonical.getTitleAr())
                && Objects.equals(existing.getContentNl(), canonical.getContentNl())
                && Objects.equals(existing.getContentEn(), canonical.getContentEn())
                && Objects.equals(existing.getContentFr(), canonical.getContentFr())
                && Objects.equals(existing.getContentAr(), canonical.getContentAr())
                && Objects.equals(existing.getBulletPointsNl(), canonical.getBulletPointsNl())
                && Objects.equals(existing.getBulletPointsEn(), canonical.getBulletPointsEn())
                && Objects.equals(existing.getBulletPointsFr(), canonical.getBulletPointsFr())
                && Objects.equals(existing.getBulletPointsAr(), canonical.getBulletPointsAr());
    }

    private enum ImportAction {
        CREATED,
        UPDATED,
        SKIPPED
    }

    // ═══════════════════════════════════════════════════════════
    // Validation
    // ═══════════════════════════════════════════════════════════

    private List<String> validateLesson(JsonNode node, int index) {
        List<String> errs = new ArrayList<>();
        String code = textOr(node, "id", null);

        // Must have at least one title
        boolean hasTitle = hasText(node, "title_nl") || hasText(node, "title_en")
                || hasText(node, "title_fr") || hasText(node, "title_ar")
                || hasText(node, "title");
        if (!hasTitle) {
            errs.add("Lesson " + code + " (index " + index + "): no title in any language");
        }

        // Pages must be an array if present
        JsonNode pages = node.path("pages");
        if (pages.isContainerNode() && !pages.isArray()) {
            errs.add("Lesson " + code + " (index " + index + "): 'pages' must be an array");
        }

        return errs;
    }

    // ═══════════════════════════════════════════════════════════
    // JSON helpers
    // ═══════════════════════════════════════════════════════════

    private String textOr(JsonNode node, String field, String fallback) {
        JsonNode child = node.path(field);
        if (child.isMissingNode() || child.isNull())
            return fallback;
        return normalizeText(child.asText(fallback));
    }

    private int intOr(JsonNode node, String field, int fallback) {
        JsonNode child = node.path(field);
        if (child.isMissingNode() || child.isNull())
            return fallback;
        return child.asInt(fallback);
    }

    private boolean hasText(JsonNode node, String field) {
        JsonNode child = node.path(field);
        return !child.isMissingNode() && !child.isNull() && !child.asText("").isBlank();
    }

    /**
     * Read a JSON array field and serialise it to a JSON string for DB storage.
     * Falls back to {@code fallbackField} if primary is absent.
     */
    private String jsonArrayToString(JsonNode parent, String field, String fallbackField) {
        JsonNode arr = parent.path(field);
        if (!arr.isArray() || arr.isEmpty()) {
            arr = parent.path(fallbackField);
        }
        if (!arr.isArray() || arr.isEmpty()) {
            return "[]";
        }
        try {
            List<String> list = objectMapper.convertValue(arr, new TypeReference<List<String>>() {
            }).stream()
                    .map(this::normalizeText)
                    .filter(item -> item != null && !item.isBlank())
                    .toList();
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            return "[]";
        }
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }

        return value
                .replace("\uFEFF", "")
                .replace("\u200B", "")
                .replace("\u200C", "")
                .replace("\u200D", "")
                .replace('\u00A0', ' ')
                .replaceAll("[ \\t]+\\n", "\n")
                .replaceAll("\\n{3,}", "\n\n")
                .replaceAll(" {2,}", " ")
                .trim();
    }

    private String sanitize(String languageCode, String value) {
        return DrivingTextSanitizer.sanitize(languageCode, value);
    }
}
