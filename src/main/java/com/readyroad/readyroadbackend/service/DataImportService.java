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
import java.util.*;

@Service
public class DataImportService {

    private static final Logger log = LoggerFactory.getLogger(DataImportService.class);

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
     * @param dataDir path to the data directory containing JSON files
     */
    @Transactional
    public void importAllData(String dataDir) {
        log.info("Starting data import from directory: {}", dataDir);

        try {
            importCategoryDescriptions(dataDir + "/category_descriptions.json");
            importTrafficSigns(dataDir + "/signs.json");
            importLessons(dataDir + "/lessons_content.json");
            log.info("Data import completed successfully!");
        } catch (Exception e) {
            log.error("Data import failed: {}", e.getMessage(), e);
            throw new RuntimeException("Data import failed", e);
        }
    }

    /**
     * Import category descriptions from category_descriptions.json
     */
    private void importCategoryDescriptions(String filePath) throws IOException {
        log.info("Importing category descriptions from: {}", filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            log.warn("Category descriptions file not found: {}", filePath);
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
                log.warn("No mapping found for category: {}", categoryName);
                continue;
            }

            Optional<Category> optCategory = categoryRepository.findByCode(categoryCode);
            if (optCategory.isEmpty()) {
                log.warn("Category not found in DB: code={} (from {})", categoryCode, categoryName);
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
            log.debug("Updated category {} ({}) descriptions", categoryCode, categoryName);
        }

        log.info("Category descriptions import complete: {} categories updated", updated);
    }

    /**
     * Import traffic signs from signs.json
     */
    private void importTrafficSigns(String filePath) throws IOException {
        log.info("Importing traffic signs from: {}", filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            log.warn("Signs file not found: {}", filePath);
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
                log.warn("Sign {} has no category, skipping", signCode);
                skipped++;
                continue;
            }

            String categoryCode = CATEGORY_NAME_TO_CODE.get(categoryName);
            if (categoryCode == null) {
                log.warn("No category mapping for: {} (sign: {})", categoryName, signCode);
                skipped++;
                continue;
            }

            Optional<Category> optCategory = categoryRepository.findByCode(categoryCode);
            if (optCategory.isEmpty()) {
                log.warn("Category {} not found in DB for sign {}", categoryCode, signCode);
                skipped++;
                continue;
            }

            Category category = optCategory.get();

            // Get multilingual names
            String nameNl = getTextOrNull(signNode, "title_nl");
            if (nameNl == null) nameNl = getTextOrNull(signNode, "title");
            String nameEn = getTextOrNull(signNode, "title_en");
            String nameFr = getTextOrNull(signNode, "title_fr");
            String nameAr = getTextOrNull(signNode, "title_ar");

            // Fallback: use title for all missing
            if (nameNl == null) nameNl = signCode;
            if (nameEn == null) nameEn = nameNl;
            if (nameFr == null) nameFr = nameNl;
            if (nameAr == null) nameAr = nameNl;

            // Get multilingual descriptions
            String descNl = getTextOrNull(signNode, "long_description_nl");
            if (descNl == null) descNl = getTextOrNull(signNode, "long_description");
            String descEn = getTextOrNull(signNode, "long_description_en");
            String descFr = getTextOrNull(signNode, "long_description_fr");
            String descAr = getTextOrNull(signNode, "long_description_ar");

            // Get image URL
            String imageUrl = getTextOrNull(signNode, "image");

            // Check if sign already exists
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

        log.info("Traffic signs import complete: {} created, {} updated, {} skipped", created, updated, skipped);
    }

    /**
     * Import lessons from lessons_content.json
     */
    private void importLessons(String filePath) throws IOException {
        log.info("Importing lessons from: {}", filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            log.warn("Lessons file not found: {}", filePath);
            return;
        }

        JsonNode root = objectMapper.readTree(file);
        JsonNode lessonsArray = root.get("lessons");
        if (lessonsArray == null || !lessonsArray.isArray()) {
            log.warn("No 'lessons' array found in lessons file");
            return;
        }

        // Map lesson IDs to category codes based on naming patterns
        Map<String, String> lessonCategoryMapping = buildLessonCategoryMapping();

        int created = 0;
        int updated = 0;
        int displayOrder = 1;

        for (JsonNode lessonNode : lessonsArray) {
            String lessonId = getTextOrNull(lessonNode, "id");
            if (lessonId == null) continue;

            // Determine category for this lesson
            String categoryCode = determineLessonCategory(lessonId, lessonNode, lessonCategoryMapping);

            Optional<Category> optCategory = categoryRepository.findByCode(categoryCode);
            if (optCategory.isEmpty()) {
                log.warn("Category {} not found for lesson {}, using default A", categoryCode, lessonId);
                optCategory = categoryRepository.findByCode("A");
                if (optCategory.isEmpty()) continue;
            }

            Category category = optCategory.get();

            // Get multilingual titles
            String titleNl = getTextOrNull(lessonNode, "title_nl");
            if (titleNl == null) titleNl = getTextOrNull(lessonNode, "title");
            String titleEn = getTextOrNull(lessonNode, "title_en");
            String titleFr = getTextOrNull(lessonNode, "title_fr");
            String titleAr = getTextOrNull(lessonNode, "title_ar");

            if (titleNl == null) titleNl = lessonId;
            if (titleEn == null) titleEn = titleNl;
            if (titleFr == null) titleFr = titleNl;
            if (titleAr == null) titleAr = titleNl;

            // Build content from pages
            String contentNl = buildLessonContent(lessonNode, "nl");
            String contentEn = buildLessonContent(lessonNode, "en");
            String contentFr = buildLessonContent(lessonNode, "fr");
            String contentAr = buildLessonContent(lessonNode, "ar");

            // Estimate reading time based on content length
            int estimatedMinutes = Math.max(5, contentNl.length() / 500);

            // Try to find existing lesson by matching title
            List<Lesson> existingLessons = lessonRepository.findByCategoryIdOrderByDisplayOrderAsc(category.getId());
            Lesson lesson = null;

            // Check for existing lesson with same title
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
        }

        log.info("Lessons import complete: {} created, {} updated", created, updated);
    }

    /**
     * Build lesson content from pages array
     */
    private String buildLessonContent(JsonNode lessonNode, String lang) {
        JsonNode pages = lessonNode.get("pages");
        if (pages == null || !pages.isArray()) {
            // Fallback to description
            String descKey = "description_" + lang;
            String desc = getTextOrNull(lessonNode, descKey);
            if (desc == null) desc = getTextOrNull(lessonNode, "description");
            return desc != null ? desc : "";
        }

        StringBuilder content = new StringBuilder();
        for (JsonNode page : pages) {
            // Add page title
            String titleKey = "title_" + lang;
            String title = getTextOrNull(page, titleKey);
            if (title == null) title = getTextOrNull(page, "title");
            if (title != null) {
                content.append("## ").append(title).append("\n\n");
            }

            // Add page content
            String contentKey = "content_" + lang;
            String pageContent = getTextOrNull(page, contentKey);
            if (pageContent == null) pageContent = getTextOrNull(page, "content");
            if (pageContent != null) {
                content.append(pageContent).append("\n\n");
            }

            // Add bullet points
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
        // Map specific lesson IDs to categories based on known patterns
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
        // Direct ID mapping
        String code = mapping.get(lessonId);
        if (code != null) return code;

        // Try to match by title keywords
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

        // Try by lesson number - distribute among categories
        if (lessonId.startsWith("les-")) {
            try {
                int num = Integer.parseInt(lessonId.replace("les-", ""));
                // Map lesson numbers to categories
                String[] codes = {"A", "B", "C", "D", "E", "F", "G", "Z", "M", "H"};
                return codes[num % codes.length];
            } catch (NumberFormatException ignored) {
            }
        }

        return "A"; // Default
    }

    private String getTextOrNull(JsonNode node, String field) {
        JsonNode fieldNode = node.get(field);
        if (fieldNode == null || fieldNode.isNull()) return null;
        String text = fieldNode.asText();
        return text.isEmpty() ? null : text;
    }
}
