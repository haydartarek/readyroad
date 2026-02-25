package com.readyroad.readyroadbackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.Lesson;
import com.readyroad.readyroadbackend.domain.entity.LessonPage;
import com.readyroad.readyroadbackend.domain.repository.LessonRepository;
import com.readyroad.readyroadbackend.dto.response.LessonImportResult;
import com.readyroad.readyroadbackend.dto.response.LessonImportResult.LessonImportItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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
     * Import from the bundled classpath resource {@code data/lessons_content.json}.
     */
    @Transactional
    public LessonImportResult importFromClasspath() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("data/lessons_content.json");
        if (is == null) {
            // Try alternative location
            is = getClass().getClassLoader().getResourceAsStream("data/lessen.json");
        }
        if (is == null) {
            return new LessonImportResult(false, 0, 0, 0, 0,
                    List.of("lessons_content.json not found on classpath"), List.of());
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
                    boolean exists = lessonRepository.existsByLessonCode(lessonCode);
                    String action = exists ? "UPDATED" : "CREATED";
                    items.add(new LessonImportItem(lessonCode, titleEn, action, "Preview"));
                    if (exists)
                        updated++;
                    else
                        created++;
                } else {
                    boolean isNew = upsertLesson(node, lessonCode, i);
                    String action = isNew ? "CREATED" : "UPDATED";
                    items.add(new LessonImportItem(lessonCode, titleEn, action, "OK"));
                    if (isNew)
                        created++;
                    else
                        updated++;
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

    private boolean upsertLesson(JsonNode node, String lessonCode, int index) {
        Optional<Lesson> existing = lessonRepository.findByLessonCode(lessonCode);
        boolean isNew = existing.isEmpty();

        Lesson lesson = existing.orElseGet(Lesson::new);
        lesson.setLessonCode(lessonCode);
        lesson.setTitleNl(textOr(node, "title_nl", textOr(node, "title", "")));
        lesson.setTitleEn(textOr(node, "title_en", textOr(node, "title", "")));
        lesson.setTitleFr(textOr(node, "title_fr", textOr(node, "title", "")));
        lesson.setTitleAr(textOr(node, "title_ar", textOr(node, "title", "")));
        lesson.setDescriptionNl(textOr(node, "description_nl", textOr(node, "description", "")));
        lesson.setDescriptionEn(textOr(node, "description_en", textOr(node, "description", "")));
        lesson.setDescriptionFr(textOr(node, "description_fr", textOr(node, "description", "")));
        lesson.setDescriptionAr(textOr(node, "description_ar", textOr(node, "description", "")));
        lesson.setIcon(textOr(node, "icon", "📖"));
        lesson.setDisplayOrder(index + 1);
        lesson.setIsActive(true);

        // Calculate estimated minutes from pages
        JsonNode pagesNode = node.path("pages");
        int pageCount = pagesNode.isArray() ? pagesNode.size() : 0;
        lesson.setEstimatedMinutes(Math.max(5, pageCount * 3));

        // Clear and rebuild pages
        lesson.clearPages();
        lesson = lessonRepository.saveAndFlush(lesson);

        if (pagesNode.isArray()) {
            for (int p = 0; p < pagesNode.size(); p++) {
                JsonNode pageNode = pagesNode.get(p);
                LessonPage page = new LessonPage();
                page.setPageNumber(intOr(pageNode, "pageNumber", p + 1));
                page.setTitleNl(textOr(pageNode, "title_nl", textOr(pageNode, "title", "")));
                page.setTitleEn(textOr(pageNode, "title_en", textOr(pageNode, "title", "")));
                page.setTitleFr(textOr(pageNode, "title_fr", textOr(pageNode, "title", "")));
                page.setTitleAr(textOr(pageNode, "title_ar", textOr(pageNode, "title", "")));
                page.setContentNl(textOr(pageNode, "content_nl", textOr(pageNode, "content", "")));
                page.setContentEn(textOr(pageNode, "content_en", textOr(pageNode, "content", "")));
                page.setContentFr(textOr(pageNode, "content_fr", textOr(pageNode, "content", "")));
                page.setContentAr(textOr(pageNode, "content_ar", textOr(pageNode, "content", "")));
                page.setBulletPointsNl(jsonArrayToString(pageNode, "bulletPoints_nl", "bulletPoints"));
                page.setBulletPointsEn(jsonArrayToString(pageNode, "bulletPoints_en", "bulletPoints"));
                page.setBulletPointsFr(jsonArrayToString(pageNode, "bulletPoints_fr", "bulletPoints"));
                page.setBulletPointsAr(jsonArrayToString(pageNode, "bulletPoints_ar", "bulletPoints"));
                lesson.addPage(page);
            }
            lessonRepository.saveAndFlush(lesson);
        }

        return isNew;
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
        return child.asText(fallback);
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
            });
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            return "[]";
        }
    }
}
