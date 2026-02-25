package com.readyroad.readyroadbackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.Lesson;
import com.readyroad.readyroadbackend.domain.entity.LessonPage;
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
import java.util.*;

@Service
public class DataImportService {

    private static final Logger log = LoggerFactory.getLogger(DataImportService.class);

    private final CategoryRepository categoryRepository;
    private final TrafficSignRepository trafficSignRepository;
    private final QuizQuestionRepository quizQuestionRepository;
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
            Map.entry("Informatieborden_en_tijdelijke_verkeersmaatregelen", "H"));

    public DataImportService(CategoryRepository categoryRepository,
            TrafficSignRepository trafficSignRepository,
            QuizQuestionRepository quizQuestionRepository,
            LessonRepository lessonRepository,
            ObjectMapper objectMapper) {
        this.categoryRepository = categoryRepository;
        this.trafficSignRepository = trafficSignRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.lessonRepository = lessonRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Import all data from JSON files in the data directory.
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
