package com.readyroad.readyroadbackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.Lesson;
import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.entity.TrafficSign;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.LessonRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.TrafficSignRepository;
import com.readyroad.readyroadbackend.dto.ImportReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.nio.file.Files;
import java.util.*;

@Service
public class DataImportService {

    private static final Logger log = LoggerFactory.getLogger(DataImportService.class);

    // ⭐ SINGLE SOURCE OF TRUTH - اسم الملف الوحيد للدروس
    private static final String LESSONS_FILE_NAME = "lessen.json";

    private final CategoryRepository categoryRepository;
    private final TrafficSignRepository trafficSignRepository;
    private final LessonRepository lessonRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final ObjectMapper objectMapper;

    // Mapping from JSON category names to database category codes
    private static final Map<String, String> CATEGORY_NAME_TO_CODE = Map.ofEntries(
            Map.entry("gevaarsborden", "A"),
            Map.entry("voorrangsborden", "B"),
            Map.entry("verbodsborden", "C"),
            Map.entry("gebodsborden", "D"),
            Map.entry("parkeer- en stilstaanborden", "E"),
            Map.entry("parkeren", "E"),
            Map.entry("aanwijzingsborden", "F"),
            Map.entry("onderborden", "G"),
            Map.entry("zoneborden", "Z"),
            Map.entry("afbakeningsborden", "M"),
            Map.entry("informatieborden_en_tijdelijke_verkeersmaatregelen", "H"),
            Map.entry("Informatieborden_en_tijdelijke_verkeersmaatregelen", "H"));

    public DataImportService(CategoryRepository categoryRepository,
            TrafficSignRepository trafficSignRepository,
            LessonRepository lessonRepository,
            QuizQuestionRepository quizQuestionRepository,
            ObjectMapper objectMapper) {
        this.categoryRepository = categoryRepository;
        this.trafficSignRepository = trafficSignRepository;
        this.lessonRepository = lessonRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Import all data from JSON files in the data directory.
     * ⭐ SINGLE SOURCE OF TRUTH: Uses lessen.json for lessons
     * 
     * @param dataDir path to the data directory containing JSON files
     */
    @Transactional
    public void importAllData(String dataDir) {
        log.info("═══════════════════════════════════════════════════");
        log.info("🚀 Starting data import from directory: {}", dataDir);
        log.info("═══════════════════════════════════════════════════");

        try {
            importCategoryDescriptions(dataDir + "/category_descriptions.json");
            importTrafficSigns(dataDir + "/signs.json");

            // ⭐ استخدام lessen.json بدلاً من lessons_content.json
            importLessons(dataDir + "/" + LESSONS_FILE_NAME);

            log.info("═══════════════════════════════════════════════════");
            log.info("✅ Data import completed successfully!");
            log.info("═══════════════════════════════════════════════════");
        } catch (Exception e) {
            log.error("═══════════════════════════════════════════════════");
            log.error("❌ Data import failed: {}", e.getMessage(), e);
            log.error("═══════════════════════════════════════════════════");
            throw new RuntimeException("Data import failed", e);
        }
    }

    /**
     * Import category descriptions from category_descriptions.json
     */
    private void importCategoryDescriptions(String filePath) throws IOException {
        log.info("📂 Importing category descriptions from: {}", filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            log.warn("⚠️  Category descriptions file not found: {}", filePath);
            return;
        }

        JsonNode root = objectMapper.readTree(file);
        int updated = 0;

        Iterator<Map.Entry<String, JsonNode>> fields = root.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String categoryName = entry.getKey();
            JsonNode descriptions = entry.getValue();

            String categoryCode = CATEGORY_NAME_TO_CODE.get(categoryName);
            if (categoryCode == null) {
                log.warn("⚠️  No mapping found for category: {}", categoryName);
                continue;
            }

            Optional<Category> optCategory = categoryRepository.findByCode(categoryCode);
            if (optCategory.isEmpty()) {
                log.warn("⚠️  Category not found in DB: code={} (from {})", categoryCode, categoryName);
                continue;
            }

            Category category = optCategory.get();
            String descNl = getTextOrNull(descriptions, "description_nl");
            String descEn = getTextOrNull(descriptions, "description_en");
            String descFr = getTextOrNull(descriptions, "description_fr");
            String descAr = getTextOrNull(descriptions, "description_ar");

            if (descNl != null)
                category.setDescriptionNl(descNl);
            if (descEn != null)
                category.setDescriptionEn(descEn);
            if (descFr != null)
                category.setDescriptionFr(descFr);
            if (descAr != null)
                category.setDescriptionAr(descAr);

            categoryRepository.save(category);
            updated++;
            log.debug("✅ Updated category {} ({}) descriptions", categoryCode, categoryName);
        }

        log.info("✅ Category descriptions import complete: {} categories updated", updated);
    }

    /**
     * Import traffic signs from signs.json
     */
    private void importTrafficSigns(String filePath) throws IOException {
        log.info("🚦 Importing traffic signs from: {}", filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            log.warn("⚠️  Signs file not found: {}", filePath);
            return;
        }

        List<JsonNode> signs = objectMapper.readValue(file, new TypeReference<List<JsonNode>>() {
        });
        int created = 0;
        int updated = 0;
        int skipped = 0;

        for (JsonNode signNode : signs) {
            String signCode = getTextOrNull(signNode, "code");
            if (signCode == null) {
                signCode = getTextOrNull(signNode, "id");
            }
            if (signCode == null) {
                skipped++;
                continue;
            }

            String categoryName = getTextOrNull(signNode, "category");
            if (categoryName == null) {
                log.warn("⚠️  Sign {} has no category, skipping", signCode);
                skipped++;
                continue;
            }

            String categoryCode = CATEGORY_NAME_TO_CODE.get(categoryName);
            if (categoryCode == null) {
                log.warn("⚠️  No category mapping for: {} (sign: {})", categoryName, signCode);
                skipped++;
                continue;
            }

            Optional<Category> optCategory = categoryRepository.findByCode(categoryCode);
            if (optCategory.isEmpty()) {
                log.warn("⚠️  Category {} not found in DB for sign {}", categoryCode, signCode);
                skipped++;
                continue;
            }

            Category category = optCategory.get();

            String nameNl = getTextOrNull(signNode, "title_nl");
            if (nameNl == null)
                nameNl = getTextOrNull(signNode, "title");
            String nameEn = getTextOrNull(signNode, "title_en");
            String nameFr = getTextOrNull(signNode, "title_fr");
            String nameAr = getTextOrNull(signNode, "title_ar");

            if (nameNl == null)
                nameNl = signCode;
            if (nameEn == null)
                nameEn = nameNl;
            if (nameFr == null)
                nameFr = nameNl;
            if (nameAr == null)
                nameAr = nameNl;

            String descNl = getTextOrNull(signNode, "long_description_nl");
            if (descNl == null)
                descNl = getTextOrNull(signNode, "long_description");
            String descEn = getTextOrNull(signNode, "long_description_en");
            String descFr = getTextOrNull(signNode, "long_description_fr");
            String descAr = getTextOrNull(signNode, "long_description_ar");

            String imageUrl = getTextOrNull(signNode, "image");

            Optional<TrafficSign> existingSign = trafficSignRepository.findBySignCode(signCode);

            TrafficSign sign;
            if (existingSign.isPresent()) {
                sign = existingSign.get();
                updated++;
            } else {
                sign = new TrafficSign();
                sign.setSignCode(signCode);
                sign.setIsActive(true);
                created++;
            }

            sign.setCategory(category);
            sign.setNameNl(nameNl);
            sign.setNameEn(nameEn);
            sign.setNameFr(nameFr);
            sign.setNameAr(nameAr);
            if (descNl != null)
                sign.setDescriptionNl(descNl);
            if (descEn != null)
                sign.setDescriptionEn(descEn);
            if (descFr != null)
                sign.setDescriptionFr(descFr);
            if (descAr != null)
                sign.setDescriptionAr(descAr);
            if (imageUrl != null)
                sign.setImageUrl(imageUrl);

            trafficSignRepository.save(sign);
        }

        log.info("✅ Traffic signs import complete: {} created, {} updated, {} skipped", created, updated, skipped);
    }

    /**
     * ⭐ Import lessons from lessen.json - SINGLE SOURCE OF TRUTH
     */
    private void importLessons(String filePath) throws IOException {
        log.info("═══════════════════════════════════════════════════");
        log.info("📚 IMPORTING LESSONS - SINGLE SOURCE OF TRUTH");
        log.info("═══════════════════════════════════════════════════");
        log.info("📄 Source File: {}", filePath);

        File file = new File(filePath);
        if (!file.exists()) {
            log.error("❌ CRITICAL: Lessons file not found: {}", filePath);
            log.error("Expected file: {}", LESSONS_FILE_NAME);
            throw new IOException("Lessons file not found: " + filePath);
        }

        // ⭐ حساب checksum للتحقق من سلامة الملف
        String fileChecksum = calculateFileChecksum(file);
        log.info("🔐 File Checksum (SHA-256): {}", fileChecksum);

        JsonNode root = objectMapper.readTree(file);
        JsonNode lessonsArray = root.get("lessons");
        if (lessonsArray == null || !lessonsArray.isArray()) {
            log.warn("⚠️  No 'lessons' array found in lessons file");
            return;
        }

        log.info("📊 Found {} lessons in {}", lessonsArray.size(), LESSONS_FILE_NAME);

        Map<String, String> lessonCategoryMapping = buildLessonCategoryMapping();

        int created = 0;
        int updated = 0;
        int skipped = 0;
        int displayOrder = 1;

        for (JsonNode lessonNode : lessonsArray) {
            try {
                String lessonId = getTextOrNull(lessonNode, "id");
                if (lessonId == null) {
                    skipped++;
                    continue;
                }

                String categoryCode = determineLessonCategory(lessonId, lessonNode, lessonCategoryMapping);

                Optional<Category> optCategory = categoryRepository.findByCode(categoryCode);
                if (optCategory.isEmpty()) {
                    log.warn("⚠️  Category {} not found for lesson {}, using default A", categoryCode, lessonId);
                    optCategory = categoryRepository.findByCode("A");
                    if (optCategory.isEmpty()) {
                        skipped++;
                        continue;
                    }
                }

                Category category = optCategory.get();

                String titleNl = getTextOrNull(lessonNode, "title_nl");
                if (titleNl == null)
                    titleNl = getTextOrNull(lessonNode, "title");
                String titleEn = getTextOrNull(lessonNode, "title_en");
                String titleFr = getTextOrNull(lessonNode, "title_fr");
                String titleAr = getTextOrNull(lessonNode, "title_ar");

                if (titleNl == null)
                    titleNl = lessonId;
                if (titleEn == null)
                    titleEn = titleNl;
                if (titleFr == null)
                    titleFr = titleNl;
                if (titleAr == null)
                    titleAr = titleNl;

                String contentNl = buildLessonContent(lessonNode, "nl");
                String contentEn = buildLessonContent(lessonNode, "en");
                String contentFr = buildLessonContent(lessonNode, "fr");
                String contentAr = buildLessonContent(lessonNode, "ar");

                int estimatedMinutes = Math.max(5, contentNl.length() / 500);

                List<Lesson> existingLessons = lessonRepository
                        .findByCategoryIdOrderByDisplayOrderAsc(category.getId());
                Lesson lesson = null;

                for (Lesson existing : existingLessons) {
                    if (existing.getTitleNl() != null && existing.getTitleNl().equals(titleNl)) {
                        lesson = existing;
                        break;
                    }
                }

                if (lesson != null) {
                    updated++;
                } else {
                    lesson = new Lesson();
                    lesson.setIsActive(true);
                    created++;
                }

                lesson.setCategory(category);
                lesson.setTitleNl(titleNl);
                lesson.setTitleEn(titleEn);
                lesson.setTitleFr(titleFr);
                lesson.setTitleAr(titleAr);
                lesson.setContentNl(contentNl);
                lesson.setContentEn(contentEn);
                lesson.setContentFr(contentFr);
                lesson.setContentAr(contentAr);
                lesson.setDisplayOrder(displayOrder);
                lesson.setEstimatedMinutes(estimatedMinutes);

                lessonRepository.save(lesson);
                displayOrder++;

            } catch (Exception e) {
                log.error("❌ Failed to import lesson: {}", e.getMessage());
                skipped++;
            }
        }

        log.info("═══════════════════════════════════════════════════");
        log.info("✅ LESSONS IMPORT COMPLETED");
        log.info("📊 Total: {} | Created: {} | Updated: {} | Skipped: {}",
                lessonsArray.size(), created, updated, skipped);
        log.info("📄 Source: {} (SINGLE SOURCE OF TRUTH)", LESSONS_FILE_NAME);
        log.info("🔐 Checksum: {}", fileChecksum);
        log.info("═══════════════════════════════════════════════════");
    }

    /**
     * ⭐ حساب checksum للتحقق من سلامة الملف
     */
    private String calculateFileChecksum(File file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            byte[] hash = digest.digest(fileBytes);

            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            log.warn("⚠️  Could not calculate checksum: {}", e.getMessage());
            return "N/A";
        }
    }

    /**
     * Build lesson content from pages array
     */
    private String buildLessonContent(JsonNode lessonNode, String lang) {
        JsonNode pages = lessonNode.get("pages");
        if (pages == null || !pages.isArray()) {
            String descKey = "description_" + lang;
            String desc = getTextOrNull(lessonNode, descKey);
            if (desc == null)
                desc = getTextOrNull(lessonNode, "description");
            return desc != null ? desc : "";
        }

        StringBuilder content = new StringBuilder();
        for (JsonNode page : pages) {
            String titleKey = "title_" + lang;
            String title = getTextOrNull(page, titleKey);
            if (title == null)
                title = getTextOrNull(page, "title");
            if (title != null) {
                content.append("## ").append(title).append("\n\n");
            }

            String contentKey = "content_" + lang;
            String pageContent = getTextOrNull(page, contentKey);
            if (pageContent == null)
                pageContent = getTextOrNull(page, "content");
            if (pageContent != null) {
                content.append(pageContent).append("\n\n");
            }

            String bulletKey = "bulletPoints_" + lang;
            JsonNode bullets = page.get(bulletKey);
            if (bullets == null)
                bullets = page.get("bulletPoints");
            if (bullets != null && bullets.isArray()) {
                for (JsonNode bullet : bullets) {
                    content.append("• ").append(bullet.asText()).append("\n");
                }
                content.append("\n");
            }
        }

        return content.toString().trim();
    }

    /**
     * Build mapping from lesson IDs to category codes
     */
    private Map<String, String> buildLessonCategoryMapping() {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("les-gevaarsborden", "A");
        mapping.put("les-voorrangsborden", "B");
        mapping.put("les-verbodsborden", "C");
        mapping.put("les-gebodsborden", "D");
        mapping.put("les-parkeren", "E");
        mapping.put("les-aanwijzingsborden", "F");
        mapping.put("les-onderborden", "G");
        mapping.put("les-zoneborden", "Z");
        mapping.put("les-afbakeningsborden", "M");
        mapping.put("les-informatieborden", "H");
        return mapping;
    }

    /**
     * Determine which category a lesson belongs to
     */
    private String determineLessonCategory(String lessonId, JsonNode lessonNode, Map<String, String> mapping) {
        String code = mapping.get(lessonId);
        if (code != null)
            return code;

        String titleNl = getTextOrNull(lessonNode, "title_nl");
        if (titleNl == null)
            titleNl = getTextOrNull(lessonNode, "title");
        if (titleNl == null)
            titleNl = "";
        String titleLower = titleNl.toLowerCase();

        if (titleLower.contains("gevaar") || titleLower.contains("waarschuwing"))
            return "A";
        if (titleLower.contains("voorrang") || titleLower.contains("priorit"))
            return "B";
        if (titleLower.contains("verbod") || titleLower.contains("prohib"))
            return "C";
        if (titleLower.contains("gebod") || titleLower.contains("verplich"))
            return "D";
        if (titleLower.contains("parkeer") || titleLower.contains("stilstaan"))
            return "E";
        if (titleLower.contains("aanwijzing") || titleLower.contains("richting"))
            return "F";
        if (titleLower.contains("onderbord") || titleLower.contains("aanvullend"))
            return "G";
        if (titleLower.contains("zone"))
            return "Z";
        if (titleLower.contains("afbakening"))
            return "M";
        if (titleLower.contains("informatie") || titleLower.contains("tijdelijk"))
            return "H";

        if (lessonId.startsWith("les-")) {
            try {
                int num = Integer.parseInt(lessonId.replace("les-", ""));
                String[] codes = { "A", "B", "C", "D", "E", "F", "G", "Z", "M", "H" };
                return codes[num % codes.length];
            } catch (NumberFormatException ignored) {
            }
        }

        return "A";
    }

    private String getTextOrNull(JsonNode node, String field) {
        JsonNode fieldNode = node.get(field);
        if (fieldNode == null || fieldNode.isNull())
            return null;
        String text = fieldNode.asText();
        return text.isEmpty() ? null : text;
    }

    // ═══════════════════════════════════════════════════════════════
    // Upload-based import methods (Phases 1-3)
    // ═══════════════════════════════════════════════════════════════

    /**
     * Compute SHA-256 checksum from raw bytes.
     */
    public String checksumOf(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1)
                    hex.append('0');
                hex.append(h);
            }
            return hex.toString();
        } catch (Exception e) {
            return "N/A";
        }
    }

    // ── Signs ──────────────────────────────────────────────────────

    @Transactional
    public ImportReport importSignsFromUpload(byte[] content, boolean dryRun) {
        ImportReport.Builder b = new ImportReport.Builder("signs", dryRun);
        try {
            List<JsonNode> signs = objectMapper.readValue(content, new TypeReference<List<JsonNode>>() {
            });
            for (JsonNode signNode : signs) {
                String signCode = getTextOrNull(signNode, "code");
                if (signCode == null)
                    signCode = getTextOrNull(signNode, "id");
                if (signCode == null) {
                    b.incSkipped().warn("Sign entry without code/id — skipped");
                    continue;
                }

                String categoryName = getTextOrNull(signNode, "category");
                if (categoryName == null) {
                    b.incSkipped().warn("Sign " + signCode + " has no category — skipped");
                    continue;
                }

                String categoryCode = CATEGORY_NAME_TO_CODE.get(categoryName);
                if (categoryCode == null) {
                    b.incSkipped().warn("No category mapping for '" + categoryName + "' (sign: " + signCode + ")");
                    continue;
                }

                Optional<Category> optCat = categoryRepository.findByCode(categoryCode);
                if (optCat.isEmpty()) {
                    b.incSkipped().warn("Category " + categoryCode + " not in DB (sign: " + signCode + ")");
                    continue;
                }

                Optional<TrafficSign> existing = trafficSignRepository.findBySignCode(signCode);
                if (existing.isPresent()) {
                    b.incUpdated();
                } else {
                    b.incCreated();
                }

                if (!dryRun) {
                    Category category = optCat.get();
                    final String sc = signCode;
                    TrafficSign sign = existing.orElseGet(() -> {
                        TrafficSign s = new TrafficSign();
                        s.setSignCode(sc);
                        s.setIsActive(true);
                        return s;
                    });
                    sign.setCategory(category);
                    String nameNl = getTextOrNull(signNode, "title_nl");
                    if (nameNl == null)
                        nameNl = getTextOrNull(signNode, "title");
                    if (nameNl == null)
                        nameNl = signCode;
                    String nameEn = getTextOrNull(signNode, "title_en");
                    if (nameEn == null)
                        nameEn = nameNl;
                    String nameFr = getTextOrNull(signNode, "title_fr");
                    if (nameFr == null)
                        nameFr = nameNl;
                    String nameAr = getTextOrNull(signNode, "title_ar");
                    if (nameAr == null)
                        nameAr = nameNl;
                    sign.setNameNl(nameNl);
                    sign.setNameEn(nameEn);
                    sign.setNameFr(nameFr);
                    sign.setNameAr(nameAr);
                    String descNl = getTextOrNull(signNode, "long_description_nl");
                    if (descNl == null)
                        descNl = getTextOrNull(signNode, "long_description");
                    if (descNl != null)
                        sign.setDescriptionNl(descNl);
                    String descEn = getTextOrNull(signNode, "long_description_en");
                    if (descEn != null)
                        sign.setDescriptionEn(descEn);
                    String descFr = getTextOrNull(signNode, "long_description_fr");
                    if (descFr != null)
                        sign.setDescriptionFr(descFr);
                    String descAr = getTextOrNull(signNode, "long_description_ar");
                    if (descAr != null)
                        sign.setDescriptionAr(descAr);
                    String imageUrl = getTextOrNull(signNode, "image");
                    if (imageUrl != null)
                        sign.setImageUrl(imageUrl);
                    trafficSignRepository.save(sign);
                }
            }
        } catch (Exception e) {
            log.error("Signs upload import failed: {}", e.getMessage());
            b.error("Failed to parse signs JSON: " + e.getMessage());
        }
        return b.build();
    }

    // ── Category Descriptions ─────────────────────────────────────

    @Transactional
    public ImportReport importCategoriesFromUpload(byte[] content, boolean dryRun) {
        ImportReport.Builder b = new ImportReport.Builder("categories", dryRun);
        try {
            JsonNode root = objectMapper.readTree(content);
            Iterator<Map.Entry<String, JsonNode>> fields = root.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String categoryName = entry.getKey();
                JsonNode descriptions = entry.getValue();

                String categoryCode = CATEGORY_NAME_TO_CODE.get(categoryName);
                if (categoryCode == null) {
                    b.incSkipped().warn("No mapping for category '" + categoryName + "'");
                    continue;
                }

                Optional<Category> optCat = categoryRepository.findByCode(categoryCode);
                if (optCat.isEmpty()) {
                    b.incSkipped().warn("Category " + categoryCode + " not in DB");
                    continue;
                }

                b.incUpdated();
                if (!dryRun) {
                    Category category = optCat.get();
                    String dNl = getTextOrNull(descriptions, "description_nl");
                    String dEn = getTextOrNull(descriptions, "description_en");
                    String dFr = getTextOrNull(descriptions, "description_fr");
                    String dAr = getTextOrNull(descriptions, "description_ar");
                    if (dNl != null)
                        category.setDescriptionNl(dNl);
                    if (dEn != null)
                        category.setDescriptionEn(dEn);
                    if (dFr != null)
                        category.setDescriptionFr(dFr);
                    if (dAr != null)
                        category.setDescriptionAr(dAr);
                    categoryRepository.save(category);
                }
            }
        } catch (Exception e) {
            log.error("Categories upload import failed: {}", e.getMessage());
            b.error("Failed to parse categories JSON: " + e.getMessage());
        }
        return b.build();
    }

    // ── Lessons ────────────────────────────────────────────────────

    @Transactional
    public ImportReport importLessonsFromUpload(byte[] content, boolean dryRun) {
        ImportReport.Builder b = new ImportReport.Builder("lessons", dryRun);
        try {
            JsonNode root = objectMapper.readTree(content);
            JsonNode lessonsArray = root.get("lessons");
            if (lessonsArray == null || !lessonsArray.isArray()) {
                b.error("JSON must contain a 'lessons' array");
                return b.build();
            }

            Map<String, String> lessonCategoryMapping = buildLessonCategoryMapping();
            int displayOrder = 1;

            for (JsonNode lessonNode : lessonsArray) {
                String lessonId = getTextOrNull(lessonNode, "id");
                if (lessonId == null) {
                    b.incSkipped().warn("Lesson entry without id — skipped");
                    continue;
                }

                String categoryCode = determineLessonCategory(lessonId, lessonNode, lessonCategoryMapping);
                Optional<Category> optCat = categoryRepository.findByCode(categoryCode);
                if (optCat.isEmpty()) {
                    optCat = categoryRepository.findByCode("A");
                    if (optCat.isEmpty()) {
                        b.incSkipped().warn("Default category A not in DB (lesson: " + lessonId + ")");
                        continue;
                    }
                    b.warn("Category " + categoryCode + " not found, using A for lesson " + lessonId);
                }

                String titleNl = getTextOrNull(lessonNode, "title_nl");
                if (titleNl == null)
                    titleNl = getTextOrNull(lessonNode, "title");
                if (titleNl == null)
                    titleNl = lessonId;
                final String finalTitleNl = titleNl;

                // Check if existing lesson by title
                Category category = optCat.get();
                List<Lesson> existingList = lessonRepository.findByCategoryIdOrderByDisplayOrderAsc(category.getId());
                boolean exists = existingList.stream().anyMatch(l -> finalTitleNl.equals(l.getTitleNl()));

                if (exists) {
                    b.incUpdated();
                } else {
                    b.incCreated();
                }

                if (!dryRun) {
                    Lesson lesson = existingList.stream().filter(l -> finalTitleNl.equals(l.getTitleNl())).findFirst()
                            .orElseGet(() -> {
                                Lesson l = new Lesson();
                                l.setIsActive(true);
                                return l;
                            });
                    String tEn = getTextOrNull(lessonNode, "title_en");
                    if (tEn == null)
                        tEn = titleNl;
                    String tFr = getTextOrNull(lessonNode, "title_fr");
                    if (tFr == null)
                        tFr = titleNl;
                    String tAr = getTextOrNull(lessonNode, "title_ar");
                    if (tAr == null)
                        tAr = titleNl;
                    lesson.setCategory(category);
                    lesson.setTitleNl(titleNl);
                    lesson.setTitleEn(tEn);
                    lesson.setTitleFr(tFr);
                    lesson.setTitleAr(tAr);
                    lesson.setContentNl(buildLessonContent(lessonNode, "nl"));
                    lesson.setContentEn(buildLessonContent(lessonNode, "en"));
                    lesson.setContentFr(buildLessonContent(lessonNode, "fr"));
                    lesson.setContentAr(buildLessonContent(lessonNode, "ar"));
                    lesson.setDisplayOrder(displayOrder);
                    lesson.setEstimatedMinutes(Math.max(5, lesson.getContentNl().length() / 500));
                    lessonRepository.save(lesson);
                }
                displayOrder++;
            }
        } catch (Exception e) {
            log.error("Lessons upload import failed: {}", e.getMessage());
            b.error("Failed to parse lessons JSON: " + e.getMessage());
        }
        return b.build();
    }

    // ── Quiz Questions (Phase 3) ──────────────────────────────────

    @Transactional
    public ImportReport importQuizQuestionsFromUpload(byte[] content, boolean dryRun) {
        ImportReport.Builder b = new ImportReport.Builder("quiz_questions", dryRun);
        try {
            List<JsonNode> questions = objectMapper.readValue(content, new TypeReference<List<JsonNode>>() {
            });
            for (int i = 0; i < questions.size(); i++) {
                JsonNode qNode = questions.get(i);
                String label = "Question #" + (i + 1);

                String catCode = getTextOrNull(qNode, "categoryCode");
                if (catCode == null) {
                    b.incSkipped().warn(label + ": missing categoryCode");
                    continue;
                }

                Optional<Category> optCat = categoryRepository.findByCode(catCode);
                if (optCat.isEmpty()) {
                    b.incSkipped().warn(label + ": category '" + catCode + "' not in DB");
                    continue;
                }

                String qEn = getTextOrNull(qNode, "questionEn");
                if (qEn == null) {
                    b.incSkipped().warn(label + ": missing questionEn");
                    continue;
                }

                JsonNode optionsNode = qNode.get("options");
                if (optionsNode == null || !optionsNode.isArray() || optionsNode.size() < 2) {
                    b.incSkipped().warn(label + ": requires at least 2 options");
                    continue;
                }

                boolean hasCorrect = false;
                for (JsonNode opt : optionsNode) {
                    JsonNode ic = opt.get("isCorrect");
                    if (ic != null && ic.asBoolean()) {
                        hasCorrect = true;
                        break;
                    }
                }
                if (!hasCorrect) {
                    b.incSkipped().warn(label + ": no correct option marked");
                    continue;
                }

                b.incCreated(); // quiz questions are always new (no upsert key)

                if (!dryRun) {
                    Category category = optCat.get();
                    QuizQuestion qq = new QuizQuestion();
                    qq.setCategory(category);
                    qq.setQuestionEn(qEn);
                    qq.setQuestionAr(
                            getTextOrNull(qNode, "questionAr") != null ? getTextOrNull(qNode, "questionAr") : qEn);
                    qq.setQuestionNl(
                            getTextOrNull(qNode, "questionNl") != null ? getTextOrNull(qNode, "questionNl") : qEn);
                    qq.setQuestionFr(
                            getTextOrNull(qNode, "questionFr") != null ? getTextOrNull(qNode, "questionFr") : qEn);

                    String diff = getTextOrNull(qNode, "difficultyLevel");
                    qq.setDifficultyLevel(parseDifficulty(diff));

                    String qType = getTextOrNull(qNode, "questionType");
                    qq.setQuestionType(parseQuestionType(qType));

                    qq.setExplanationEn(getTextOrNull(qNode, "explanationEn"));
                    qq.setExplanationAr(getTextOrNull(qNode, "explanationAr"));
                    qq.setExplanationNl(getTextOrNull(qNode, "explanationNl"));
                    qq.setExplanationFr(getTextOrNull(qNode, "explanationFr"));
                    qq.setContentImageUrl(getTextOrNull(qNode, "contentImageUrl"));
                    qq.setIsActive(true);
                    qq.setStatus(QuizQuestion.QuestionStatus.DRAFT);

                    int order = 0;
                    for (JsonNode optNode : optionsNode) {
                        QuizAnswerOption opt = new QuizAnswerOption();
                        String tEn = getTextOrNull(optNode, "textEn");
                        if (tEn == null)
                            tEn = "";
                        opt.setOptionTextEn(tEn);
                        opt.setOptionTextAr(
                                getTextOrNull(optNode, "textAr") != null ? getTextOrNull(optNode, "textAr") : tEn);
                        opt.setOptionTextNl(
                                getTextOrNull(optNode, "textNl") != null ? getTextOrNull(optNode, "textNl") : tEn);
                        opt.setOptionTextFr(
                                getTextOrNull(optNode, "textFr") != null ? getTextOrNull(optNode, "textFr") : tEn);
                        JsonNode ic = optNode.get("isCorrect");
                        opt.setIsCorrect(ic != null && ic.asBoolean());
                        opt.setDisplayOrder(order++);
                        qq.addOption(opt);
                    }
                    quizQuestionRepository.save(qq);
                }
            }
        } catch (Exception e) {
            log.error("Quiz questions upload import failed: {}", e.getMessage());
            b.error("Failed to parse quiz questions JSON: " + e.getMessage());
        }
        return b.build();
    }

    private QuizQuestion.DifficultyLevel parseDifficulty(String val) {
        if (val == null)
            return QuizQuestion.DifficultyLevel.MEDIUM;
        try {
            return QuizQuestion.DifficultyLevel.valueOf(val.toUpperCase());
        } catch (IllegalArgumentException e) {
            return QuizQuestion.DifficultyLevel.MEDIUM;
        }
    }

    private QuizQuestion.QuestionType parseQuestionType(String val) {
        if (val == null)
            return QuizQuestion.QuestionType.MULTIPLE_CHOICE;
        try {
            return QuizQuestion.QuestionType.valueOf(val.toUpperCase());
        } catch (IllegalArgumentException e) {
            return QuizQuestion.QuestionType.MULTIPLE_CHOICE;
        }
    }
}
