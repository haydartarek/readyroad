package com.readyroad.readyroadbackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.Lesson;
import com.readyroad.readyroadbackend.domain.entity.TrafficSign;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.LessonRepository;
import com.readyroad.readyroadbackend.domain.repository.TrafficSignRepository;
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
            Map.entry("Informatieborden_en_tijdelijke_verkeersmaatregelen", "H")
    );

    public DataImportService(CategoryRepository categoryRepository,
                             TrafficSignRepository trafficSignRepository,
                             LessonRepository lessonRepository,
                             ObjectMapper objectMapper) {
        this.categoryRepository = categoryRepository;
        this.trafficSignRepository = trafficSignRepository;
        this.lessonRepository = lessonRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Import all data from JSON files in the data directory.
     * ⭐ SINGLE SOURCE OF TRUTH: Uses lessen.json for lessons
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

            if (descNl != null) category.setDescriptionNl(descNl);
            if (descEn != null) category.setDescriptionEn(descEn);
            if (descFr != null) category.setDescriptionFr(descFr);
            if (descAr != null) category.setDescriptionAr(descAr);

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

        List<JsonNode> signs = objectMapper.readValue(file, new TypeReference<List<JsonNode>>() {});
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
            if (nameNl == null) nameNl = getTextOrNull(signNode, "title");
            String nameEn = getTextOrNull(signNode, "title_en");
            String nameFr = getTextOrNull(signNode, "title_fr");
            String nameAr = getTextOrNull(signNode, "title_ar");

            if (nameNl == null) nameNl = signCode;
            if (nameEn == null) nameEn = nameNl;
            if (nameFr == null) nameFr = nameNl;
            if (nameAr == null) nameAr = nameNl;

            String descNl = getTextOrNull(signNode, "long_description_nl");
            if (descNl == null) descNl = getTextOrNull(signNode, "long_description");
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
            if (descNl != null) sign.setDescriptionNl(descNl);
            if (descEn != null) sign.setDescriptionEn(descEn);
            if (descFr != null) sign.setDescriptionFr(descFr);
            if (descAr != null) sign.setDescriptionAr(descAr);
            if (imageUrl != null) sign.setImageUrl(imageUrl);

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
                if (titleNl == null) titleNl = getTextOrNull(lessonNode, "title");
                String titleEn = getTextOrNull(lessonNode, "title_en");
                String titleFr = getTextOrNull(lessonNode, "title_fr");
                String titleAr = getTextOrNull(lessonNode, "title_ar");

                if (titleNl == null) titleNl = lessonId;
                if (titleEn == null) titleEn = titleNl;
                if (titleFr == null) titleFr = titleNl;
                if (titleAr == null) titleAr = titleNl;

                String contentNl = buildLessonContent(lessonNode, "nl");
                String contentEn = buildLessonContent(lessonNode, "en");
                String contentFr = buildLessonContent(lessonNode, "fr");
                String contentAr = buildLessonContent(lessonNode, "ar");

                int estimatedMinutes = Math.max(5, contentNl.length() / 500);

                List<Lesson> existingLessons = lessonRepository.findByCategoryIdOrderByDisplayOrderAsc(category.getId());
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
                if (hex.length() == 1) hexString.append('0');
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
            if (desc == null) desc = getTextOrNull(lessonNode, "description");
            return desc != null ? desc : "";
        }

        StringBuilder content = new StringBuilder();
        for (JsonNode page : pages) {
            String titleKey = "title_" + lang;
            String title = getTextOrNull(page, titleKey);
            if (title == null) title = getTextOrNull(page, "title");
            if (title != null) {
                content.append("## ").append(title).append("\n\n");
            }

            String contentKey = "content_" + lang;
            String pageContent = getTextOrNull(page, contentKey);
            if (pageContent == null) pageContent = getTextOrNull(page, "content");
            if (pageContent != null) {
                content.append(pageContent).append("\n\n");
            }

            String bulletKey = "bulletPoints_" + lang;
            JsonNode bullets = page.get(bulletKey);
            if (bullets == null) bullets = page.get("bulletPoints");
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
        if (code != null) return code;

        String titleNl = getTextOrNull(lessonNode, "title_nl");
        if (titleNl == null) titleNl = getTextOrNull(lessonNode, "title");
        if (titleNl == null) titleNl = "";
        String titleLower = titleNl.toLowerCase();

        if (titleLower.contains("gevaar") || titleLower.contains("waarschuwing")) return "A";
        if (titleLower.contains("voorrang") || titleLower.contains("priorit")) return "B";
        if (titleLower.contains("verbod") || titleLower.contains("prohib")) return "C";
        if (titleLower.contains("gebod") || titleLower.contains("verplich")) return "D";
        if (titleLower.contains("parkeer") || titleLower.contains("stilstaan")) return "E";
        if (titleLower.contains("aanwijzing") || titleLower.contains("richting")) return "F";
        if (titleLower.contains("onderbord") || titleLower.contains("aanvullend")) return "G";
        if (titleLower.contains("zone")) return "Z";
        if (titleLower.contains("afbakening")) return "M";
        if (titleLower.contains("informatie") || titleLower.contains("tijdelijk")) return "H";

        if (lessonId.startsWith("les-")) {
            try {
                int num = Integer.parseInt(lessonId.replace("les-", ""));
                String[] codes = {"A", "B", "C", "D", "E", "F", "G", "Z", "M", "H"};
                return codes[num % codes.length];
            } catch (NumberFormatException ignored) {
            }
        }

        return "A";
    }

    private String getTextOrNull(JsonNode node, String field) {
        JsonNode fieldNode = node.get(field);
        if (fieldNode == null || fieldNode.isNull()) return null;
        String text = fieldNode.asText();
        return text.isEmpty() ? null : text;
    }
}
