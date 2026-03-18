package com.readyroad.readyroadbackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.TrafficSign;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.TrafficSignRepository;
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
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final TrafficSignRepository trafficSignRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("🚀 Starting data initialization...");

        try {
            loadTrafficSigns();

            log.info("✅ Data initialization completed successfully!");
        } catch (Exception e) {
            log.error("❌ Data initialization failed: {}", e.getMessage(), e);
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
                Map.entry("fietsersborden", "M"),
                Map.entry("zoneborden", "Z"),
                Map.entry("afbakeningsborden", "T"),
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
