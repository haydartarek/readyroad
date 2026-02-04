package com.readyroad.readyroadbackend.config;

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
import java.util.List;
import java.util.Map;

@Slf4j
@Component  // Re-enabled to load data
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final LessonRepository lessonRepository;
    private final TrafficSignRepository trafficSignRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("🚀 Starting data initialization...");

        try {
            // Skip categories and lessons - they come from migrations
            // Only load traffic signs from JSON
            loadTrafficSigns();

            log.info("✅ Data initialization completed successfully!");
        } catch (Exception e) {
            log.error("❌ Data initialization failed: {}", e.getMessage(), e);
        }
    }

    private void loadCategories() throws IOException {
        if (categoryRepository.count() > 0) {
            log.info("📂 Categories already exist. Skipping...");
            return;
        }

        log.info("📂 Loading categories from category_descriptions.json...");

        InputStream inputStream = new ClassPathResource("data/category_descriptions.json").getInputStream();
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        Map<String, Object> data = objectMapper.readValue(reader, Map.class);
        List<Map<String, Object>> categoriesData = (List<Map<String, Object>>) data.get("categories");

        for (Map<String, Object> categoryData : categoriesData) {
            Category category = new Category();
            category.setCode((String) categoryData.get("id"));
            category.setNameEn((String) categoryData.get("title_en"));
            category.setNameAr(FixArabicEncoding.fixDoubleEncoding((String) categoryData.get("title_ar")));
            category.setNameNl((String) categoryData.get("title_nl"));
            category.setNameFr((String) categoryData.get("title_fr"));
            category.setDescriptionEn((String) categoryData.get("description_en"));
            category.setDescriptionAr(FixArabicEncoding.fixDoubleEncoding((String) categoryData.get("description_ar")));
            category.setDescriptionNl((String) categoryData.get("description_nl"));
            category.setDescriptionFr((String) categoryData.get("description_fr"));
            
            categoryRepository.save(category);
        }

        log.info("✅ Loaded {} categories", categoriesData.size());
    }

    private void loadLessons() throws IOException {
        if (lessonRepository.count() > 0) {
            log.info("📚 Lessons already exist. Skipping...");
            return;
        }

        log.info("📚 Loading lessons from lessons_content.json...");

        InputStream inputStream = new ClassPathResource("data/lessons_content.json").getInputStream();
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        Map<String, Object> data = objectMapper.readValue(reader, Map.class);
        List<Map<String, Object>> lessonsData = (List<Map<String, Object>>) data.get("lessons");

        for (Map<String, Object> lessonData : lessonsData) {
            Lesson lesson = new Lesson();
            lesson.setTitleEn((String) lessonData.get("title_en"));
            lesson.setTitleAr(FixArabicEncoding.fixDoubleEncoding((String) lessonData.get("title_ar")));
            lesson.setTitleNl((String) lessonData.get("title_nl"));
            lesson.setTitleFr((String) lessonData.get("title_fr"));
            lesson.setContentEn((String) lessonData.get("content_en"));
            lesson.setContentAr(FixArabicEncoding.fixDoubleEncoding((String) lessonData.get("content_ar")));
            lesson.setContentNl((String) lessonData.get("content_nl"));
            lesson.setContentFr((String) lessonData.get("content_fr"));
            
            if (lessonData.containsKey("order")) {
                lesson.setDisplayOrder(((Number) lessonData.get("order")).intValue());
            }
            
            if (lessonData.containsKey("category_id")) {
                String categoryCode = (String) lessonData.get("category_id");
                categoryRepository.findByCode(categoryCode).ifPresent(lesson::setCategory);
            }
            
            lessonRepository.save(lesson);
        }

        log.info("✅ Loaded {} lessons", lessonsData.size());
    }

    private void loadTrafficSigns() throws IOException {
        if (trafficSignRepository.count() > 0) {
            log.info("🚦 Traffic signs already exist. Skipping...");
            return;
        }

        log.info("🚦 Loading traffic signs from signs.json...");

        InputStream inputStream = new ClassPathResource("data/signs.json").getInputStream();
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        List<Map<String, Object>> signsData = objectMapper.readValue(reader, List.class);

        // Create mapping from Dutch category names to category codes
        Map<String, String> categoryMapping = Map.ofEntries(
            Map.entry("gevaarsborden", "A"),        // Danger signs
            Map.entry("voorrangsborden", "B"),      // Priority signs
            Map.entry("verbodsborden", "C"),        // Prohibition signs
            Map.entry("gebodsborden", "D"),         // Mandatory signs
            Map.entry("parkeerborden", "E"),        // Parking signs
            Map.entry("aanwijzingsborden", "F"),    // Information signs
            Map.entry("onderborden", "G"),          // Additional signs
            Map.entry("zoneborden", "Z"),           // Zone signs
            Map.entry("afbakeningsborden", "M"),    // Delineation signs
            Map.entry("informatieborden_en_tijdelijke_verkeersmaatregelen", "F")  // Information & temporary measures
        );

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

            // Set image path directly from JSON
            String imagePath = (String) signData.get("image");
            if (imagePath != null) {
                sign.setImageUrl(imagePath);
            }

            // Map category using Dutch name from signs.json
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