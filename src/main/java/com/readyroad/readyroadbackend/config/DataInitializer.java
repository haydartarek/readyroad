package com.readyroad.readyroadbackend.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.Lesson;
import com.readyroad.readyroadbackend.domain.entity.TrafficSign;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.LessonRepository;
import com.readyroad.readyroadbackend.domain.repository.TrafficSignRepository;
import com.readyroad.readyroadbackend.util.FixArabicEncoding;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final LessonRepository lessonRepository;
    private final TrafficSignRepository trafficSignRepository;
    private final ObjectMapper objectMapper;

    // ⭐ اسم الملف الوحيد للدروس - Single Source of Truth
    private static final String LESSONS_FILE = "data/lessen.json";

    // ⭐ Manual mapping للدروس → الفئات (يمكن تعديله حسب احتياجك)
    private static final Map<String, String> LESSON_TO_CATEGORY_MAP = Map.ofEntries(
            Map.entry("les-0", "A"), // Rijbewijs B - Basis
            Map.entry("les-1", "A"), // Verkeersborden Begrijpen
            Map.entry("les-2", "B"), // Voorrang
            Map.entry("les-3", "C"), // Snelheid en Afstand
            Map.entry("les-4", "E"), // Parkeren en Stilstaan
            Map.entry("les-5", "F"), // Rijbaan en Rijstroken
            Map.entry("les-6", "A"), // Algemene Verkeersregels
            Map.entry("les-7", "A"), // Fietsers en de Weg
            Map.entry("les-8", "F"), // De Autosnelweg
            Map.entry("les-9", "F"), // De Autoweg
            Map.entry("les-10", "A"), // Voetgangers op de Weg
            Map.entry("les-11", "A"), // De Bestuurder
            Map.entry("les-12", "A"), // Maximaal Toegelaten Massa
            Map.entry("les-13", "A"), // Lading van Voertuigen
            Map.entry("les-14", "D"), // Lichten en Claxon
            Map.entry("les-15", "A"), // Kruisen van Voertuigen
            Map.entry("les-16", "C"), // Inhalen van Voertuigen
            Map.entry("les-17", "B"), // Voorrangsborden
            Map.entry("les-18", "B"), // Bevelen Bevoegde Personen
            Map.entry("les-19", "F"), // Verkeerslichten
            Map.entry("les-20", "Z"), // Zoneborden
            Map.entry("les-21", "A"), // Verkeersregels Tram en Bus
            Map.entry("les-22", "A"), // Rotondes
            Map.entry("les-23", "A"), // Spoorwegovergangen
            Map.entry("les-24", "A"), // Alcohol en Drugs in Verkeer
            Map.entry("les-25", "A"), // Overzicht Overtredingen
            Map.entry("les-26", "A"), // Ongeval Procedures
            Map.entry("les-27", "A"), // Zuinig en Ecologisch Rijden
            Map.entry("les-28", "E"), // Regels Betreffende Parkeren
            Map.entry("les-29", "A") // Techniek van de Auto
    );

    @Override
    @Transactional
    public void run(String... args) {
        log.info("🚀 Starting data initialization...");

        try {
            // ⭐ تفعيل تحميل الدروس من lessen.json فقط
            loadLessonsFromSingleSource();
            loadTrafficSigns();

            log.info("✅ Data initialization completed successfully!");
        } catch (Exception e) {
            log.error("❌ Data initialization failed: {}", e.getMessage(), e);
        }
    }

    /**
     * ⭐ METHOD الوحيدة لتحميل الدروس - Single Source of Truth
     * المصدر: lessen.json فقط
     */
    private void loadLessonsFromSingleSource() throws IOException {
        // تحقق من وجود دروس
        long existingCount = lessonRepository.count();
        if (existingCount > 0) {
            log.info("📚 Lessons already exist ({} records). Skipping...", existingCount);
            return;
        }

        log.info("═══════════════════════════════════════════════════");
        log.info("📚 LOADING LESSONS - SINGLE SOURCE OF TRUTH");
        log.info("═══════════════════════════════════════════════════");
        log.info("📄 Source File: {}", LESSONS_FILE);

        // قراءة الملف
        InputStream inputStream = new ClassPathResource(LESSONS_FILE).getInputStream();
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

        // حساب checksum للتحقق
        String fileChecksum = calculateFileChecksum(LESSONS_FILE);
        log.info("🔐 File Checksum (SHA-256): {}", fileChecksum);

        JsonNode rootNode = objectMapper.readTree(reader);
        JsonNode lessonsArray = rootNode.get("lessons");

        if (lessonsArray == null || !lessonsArray.isArray()) {
            log.warn("⚠️  No lessons found in {}", LESSONS_FILE);
            return;
        }

        log.info("📊 Found {} lessons in {}", lessonsArray.size(), LESSONS_FILE);

        int loaded = 0;
        int failed = 0;
        int displayOrder = 1;

        for (JsonNode lessonNode : lessonsArray) {
            try {
                String lessonId = getTextOrNull(lessonNode, "id");
                String titleNl = getTextOrNull(lessonNode, "title_nl");
                if (titleNl == null)
                    titleNl = getTextOrNull(lessonNode, "title");

                // ⭐ تحديد الفئة بناءً على lesson ID أو title
                Long categoryId = determineLessonCategory(lessonNode);
                if (categoryId == null) {
                    log.error("❌ Cannot determine category for lesson: {} ({})", lessonId, titleNl);
                    failed++;
                    continue;
                }

                Optional<Category> optCategory = categoryRepository.findById(categoryId);
                if (optCategory.isEmpty()) {
                    log.error("❌ Category with ID {} not found!", categoryId);
                    failed++;
                    continue;
                }

                Category category = optCategory.get();

                // بناء محتوى الدرس من pages
                String contentNl = buildLessonContent(lessonNode, "nl");
                String contentEn = buildLessonContent(lessonNode, "en");
                String contentFr = buildLessonContent(lessonNode, "fr");
                String contentAr = buildLessonContent(lessonNode, "ar");

                // إنشاء الدرس
                Lesson lesson = new Lesson();
                lesson.setCategory(category);

                lesson.setTitleNl(titleNl);
                lesson.setTitleEn(getTextOrNull(lessonNode, "title_en"));
                lesson.setTitleFr(getTextOrNull(lessonNode, "title_fr"));
                lesson.setTitleAr(FixArabicEncoding.fixDoubleEncoding(getTextOrNull(lessonNode, "title_ar")));

                lesson.setContentNl(contentNl);
                lesson.setContentEn(contentEn);
                lesson.setContentFr(contentFr);
                lesson.setContentAr(FixArabicEncoding.fixDoubleEncoding(contentAr));

                lesson.setDisplayOrder(displayOrder);
                lesson.setEstimatedMinutes(Math.max(5, contentNl.length() / 500));
                lesson.setIsActive(true);

                lessonRepository.save(lesson);
                loaded++;
                displayOrder++;

                log.debug("✅ Loaded lesson: {} → Category {}", titleNl, category.getCode());

            } catch (Exception e) {
                log.error("❌ Failed to load lesson: {}", e.getMessage());
                failed++;
            }
        }

        log.info("═══════════════════════════════════════════════════");
        log.info("✅ LESSONS LOADING COMPLETED");
        log.info("📊 Total: {} | Loaded: {} | Failed: {}", lessonsArray.size(), loaded, failed);
        log.info("📄 Source: {} (SINGLE SOURCE OF TRUTH)", LESSONS_FILE);
        log.info("🔐 Checksum: {}", fileChecksum);
        log.info("═══════════════════════════════════════════════════");
    }

    /**
     * ⭐ تحديد الفئة بناءً على id الدرس أو العنوان
     * الأولوية: 1) Manual mapping 2) Explicit category_id 3) Pattern matching 4)
     * Default
     */
    private Long determineLessonCategory(JsonNode lessonNode) {
        String lessonId = getTextOrNull(lessonNode, "id");
        String titleNl = getTextOrNull(lessonNode, "title_nl");
        if (titleNl == null)
            titleNl = getTextOrNull(lessonNode, "title");

        String categoryCode = null;

        // 1. ⭐ أولاً: تحقق من Manual mapping
        if (lessonId != null && LESSON_TO_CATEGORY_MAP.containsKey(lessonId)) {
            categoryCode = LESSON_TO_CATEGORY_MAP.get(lessonId);
            log.debug("📌 Lesson {} mapped to category {} (manual mapping)", lessonId, categoryCode);
        }

        // 2. إذا لم يوجد، تحقق من explicit category_id في JSON
        if (categoryCode == null) {
            String explicitCategory = getTextOrNull(lessonNode, "category_id");
            if (explicitCategory != null) {
                categoryCode = explicitCategory;
                log.debug("📌 Lesson {} has explicit category_id: {}", lessonId, categoryCode);
            }
        }

        // 3. إذا لم يوجد، حاول من lesson ID
        if (categoryCode == null && lessonId != null) {
            String idLower = lessonId.toLowerCase();
            if (idLower.contains("gevaar"))
                categoryCode = "A";
            else if (idLower.contains("voorrang") || idLower.contains("priorit"))
                categoryCode = "B";
            else if (idLower.contains("verbod") || idLower.contains("prohib"))
                categoryCode = "C";
            else if (idLower.contains("gebod") || idLower.contains("verplich"))
                categoryCode = "D";
            else if (idLower.contains("parkeer") || idLower.contains("stilstaan"))
                categoryCode = "E";
            else if (idLower.contains("aanwijzing") || idLower.contains("richting"))
                categoryCode = "F";
            else if (idLower.contains("onderbord") || idLower.contains("aanvullend"))
                categoryCode = "G";
            else if (idLower.contains("zone"))
                categoryCode = "Z";
            else if (idLower.contains("afbakening"))
                categoryCode = "M";
            else if (idLower.contains("informatie") || idLower.contains("tijdelijk"))
                categoryCode = "H";
        }

        // 4. إذا لم ينجح، حاول من العنوان
        if (categoryCode == null && titleNl != null) {
            String titleLower = titleNl.toLowerCase();
            if (titleLower.contains("gevaar") || titleLower.contains("danger"))
                categoryCode = "A";
            else if (titleLower.contains("voorrang") || titleLower.contains("priorit")
                    || titleLower.contains("priority"))
                categoryCode = "B";
            else if (titleLower.contains("verbod") || titleLower.contains("prohib") || titleLower.contains("interdi"))
                categoryCode = "C";
            else if (titleLower.contains("gebod") || titleLower.contains("verplich")
                    || titleLower.contains("obligation"))
                categoryCode = "D";
            else if (titleLower.contains("parkeer") || titleLower.contains("parking")
                    || titleLower.contains("stationnement"))
                categoryCode = "E";
            else if (titleLower.contains("aanwijzing") || titleLower.contains("richting")
                    || titleLower.contains("direction"))
                categoryCode = "F";
            else if (titleLower.contains("onderbord") || titleLower.contains("aanvullend")
                    || titleLower.contains("additional"))
                categoryCode = "G";
            else if (titleLower.contains("zone"))
                categoryCode = "Z";
            else if (titleLower.contains("afbakening") || titleLower.contains("delimitation"))
                categoryCode = "M";
            else if (titleLower.contains("informatie") || titleLower.contains("information")
                    || titleLower.contains("tijdelijk"))
                categoryCode = "H";
            else if (titleLower.contains("rijbewijs") || titleLower.contains("license")
                    || titleLower.contains("permis"))
                categoryCode = "A";
        }

        // 5. Default: Category A
        if (categoryCode == null) {
            log.warn("⚠️  Could not determine category for lesson: {} / {}. Using default 'A'", lessonId, titleNl);
            categoryCode = "A";
        }

        // 6. احصل على Category من قاعدة البيانات
        Optional<Category> optCategory = categoryRepository.findByCode(categoryCode);
        if (optCategory.isEmpty()) {
            log.error("❌ Category {} not found in database!", categoryCode);
            // Try default A
            optCategory = categoryRepository.findByCode("A");
        }

        return optCategory.map(Category::getId).orElse(null);
    }

    /**
     * بناء محتوى الدرس من pages
     */
    private String buildLessonContent(JsonNode lessonNode, String lang) {
        JsonNode pages = lessonNode.get("pages");
        if (pages == null || !pages.isArray()) {
            // Fallback to direct description
            String descKey = "description_" + lang;
            String desc = getTextOrNull(lessonNode, descKey);
            if (desc == null)
                desc = getTextOrNull(lessonNode, "description");
            if (desc == null)
                desc = getTextOrNull(lessonNode, "content_" + lang);
            if (desc == null)
                desc = getTextOrNull(lessonNode, "content");
            return desc != null ? desc : "";
        }

        StringBuilder content = new StringBuilder();
        for (JsonNode page : pages) {
            // Add page title
            String titleKey = "title_" + lang;
            String title = getTextOrNull(page, titleKey);
            if (title == null)
                title = getTextOrNull(page, "title");
            if (title != null) {
                content.append("## ").append(title).append("\n\n");
            }

            // Add page content
            String contentKey = "content_" + lang;
            String pageContent = getTextOrNull(page, contentKey);
            if (pageContent == null)
                pageContent = getTextOrNull(page, "content");
            if (pageContent != null) {
                content.append(pageContent).append("\n\n");
            }

            // Add bullet points
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
     * Helper method لقراءة text من JSON node
     */
    private String getTextOrNull(JsonNode node, String fieldName) {
        if (node == null || !node.has(fieldName))
            return null;
        JsonNode fieldNode = node.get(fieldName);
        if (fieldNode == null || fieldNode.isNull())
            return null;
        String text = fieldNode.asText();
        return text.isEmpty() ? null : text;
    }

    /**
     * حساب checksum للملف للتحقق من سلامته
     */
    private String calculateFileChecksum(String filePath) {
        try {
            InputStream is = new ClassPathResource(filePath).getInputStream();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
            byte[] hash = digest.digest();
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            log.warn("⚠️  Could not calculate checksum: {}", e.getMessage());
            return "N/A";
        }
    }

    private void loadTrafficSigns() throws IOException {
        if (trafficSignRepository.count() > 0) {
            log.info("🚦 Traffic signs already exist. Skipping...");
            return;
        }

        log.info("🚦 Loading traffic signs from signs.json...");

        InputStream inputStream = new ClassPathResource("data/signs.json").getInputStream();
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

        // ✅ Use TypeReference for type-safe deserialization
        List<Map<String, Object>> signsData = objectMapper.readValue(
                reader,
                new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>() {
                });

        Map<String, String> categoryMapping = Map.ofEntries(
                Map.entry("gevaarsborden", "A"),
                Map.entry("voorrangsborden", "B"),
                Map.entry("verbodsborden", "C"),
                Map.entry("gebodsborden", "D"),
                Map.entry("parkeerborden", "E"),
                Map.entry("aanwijzingsborden", "F"),
                Map.entry("onderborden", "G"),
                Map.entry("zoneborden", "Z"),
                Map.entry("afbakeningsborden", "M"),
                Map.entry("informatieborden_en_tijdelijke_verkeersmaatregelen", "F"));

        int loaded = 0;
        int skipped = 0;

        for (Map<String, Object> signData : signsData) {
            TrafficSign sign = new TrafficSign();
            sign.setSignCode((String) signData.get("code"));
            sign.setNameEn((String) signData.get("title_en"));
            sign.setNameAr((String) signData.get("title_ar"));
            sign.setNameNl((String) signData.get("title_nl"));
            sign.setNameFr((String) signData.get("title_fr"));
            sign.setDescriptionEn((String) signData.get("long_description_en"));
            sign.setDescriptionAr((String) signData.get("long_description_ar"));
            sign.setDescriptionNl((String) signData.get("long_description_nl"));
            sign.setDescriptionFr((String) signData.get("long_description_fr"));

            String imagePath = (String) signData.get("image");
            if (imagePath != null) {
                sign.setImageUrl(imagePath);
            }

            Category category = null;
            if (signData.containsKey("category")) {
                String dutchCategoryName = (String) signData.get("category");
                String categoryCode = categoryMapping.get(dutchCategoryName.toLowerCase());

                if (categoryCode != null) {
                    category = categoryRepository.findByCode(categoryCode).orElse(null);
                } else {
                    log.warn("⚠️  Unknown category name: {} for sign {}", dutchCategoryName, sign.getSignCode());
                }
            } else if (signData.containsKey("category_id")) {
                String categoryCode = (String) signData.get("category_id");
                category = categoryRepository.findByCode(categoryCode).orElse(null);
            }

            if (category != null) {
                sign.setCategory(category);
                trafficSignRepository.save(sign);
                loaded++;
            } else {
                log.warn("⚠️  Skipping sign {} - no category found", sign.getSignCode());
                skipped++;
            }
        }

        log.info("✅ Loaded {} traffic signs, skipped {} (no category)", loaded, skipped);
    }
}
