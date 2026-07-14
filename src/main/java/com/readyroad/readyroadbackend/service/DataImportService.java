package com.readyroad.readyroadbackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.dto.ImportReport;
import com.readyroad.readyroadbackend.util.DrivingTextSanitizer;
import com.readyroad.readyroadbackend.util.PlaceholderDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DataImportService {

    private static final Logger log = LoggerFactory.getLogger(DataImportService.class);

    private final CategoryRepository categoryRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final BackendMessageService messages;
    private final ObjectMapper objectMapper;

    // Mapping from JSON category names to database category codes
    private static final Map<String, String> CATEGORY_NAME_TO_CODE = Map.ofEntries(
            Map.entry("gevaarsborden", "A"),
            Map.entry("voorrangsborden", "B"),
            Map.entry("verbodsborden", "C"),
            Map.entry("gebodsborden", "D"),
            Map.entry("parkeer- en stilstaanborden", "E"),
            Map.entry("parkeerborden", "E"),
            Map.entry("parkeren", "E"),
            Map.entry("aanwijzingsborden", "F"),
            Map.entry("onderborden", "G"),
            Map.entry("zoneborden", "Z"),
            Map.entry("afbakeningsborden", "M"),
            Map.entry("informatieborden_en_tijdelijke_verkeersmaatregelen", "H"),
            Map.entry("Informatieborden_en_tijdelijke_verkeersmaatregelen", "H"));

    public DataImportService(CategoryRepository categoryRepository,
            QuizQuestionRepository quizQuestionRepository,
            ObjectMapper objectMapper,
            BackendMessageService messages) {
        this.categoryRepository = categoryRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.objectMapper = objectMapper;
        this.messages = messages;
    }

    private String getTextOrNull(JsonNode node, String field) {
        JsonNode fieldNode = node.get(field);
        if (fieldNode == null || fieldNode.isNull())
            return null;
        String text = fieldNode.asText();
        return text.isEmpty() ? null : text;
    }

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

    // ── Category Descriptions ─────────────────────────────────────

    @Transactional
    public ImportReport importCategoriesFromUpload(byte[] content, boolean dryRun) {
        ImportReport.Builder b = new ImportReport.Builder("categories", dryRun);
        try {
            JsonNode root = objectMapper.readTree(content);
            Iterator<Map.Entry<String, JsonNode>> fields = root.properties().iterator();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String categoryName = entry.getKey();
                JsonNode descriptions = entry.getValue();

                String categoryCode = CATEGORY_NAME_TO_CODE.get(categoryName);
                if (categoryCode == null) {
                    b.incSkipped().warn(messages.get("admin.import.category.mapping_missing", categoryName));
                    continue;
                }

                Optional<Category> optCat = categoryRepository.findByCode(categoryCode);
                if (optCat.isEmpty()) {
                    b.incSkipped().warn(messages.get("admin.import.category.not_found", categoryCode));
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
                        category.setDescriptionNl(DrivingTextSanitizer.sanitize("NL", dNl));
                    if (dEn != null)
                        category.setDescriptionEn(DrivingTextSanitizer.sanitize("EN", dEn));
                    if (dFr != null)
                        category.setDescriptionFr(DrivingTextSanitizer.sanitize("FR", dFr));
                    if (dAr != null)
                        category.setDescriptionAr(DrivingTextSanitizer.sanitize("AR", dAr));
                    categoryRepository.save(category);
                }
            }
        } catch (Exception e) {
            log.error("Categories upload import failed: {}", e.getMessage());
            b.error(messages.get("admin.import.parse_categories_failed", e.getMessage()));
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
                String catCode = getTextOrNull(qNode, "categoryCode");
                if (catCode == null) {
                    b.incSkipped().warn(messages.get("admin.import.quiz.missing_category", i + 1));
                    continue;
                }

                Optional<Category> optCat = categoryRepository.findByCode(catCode);
                if (optCat.isEmpty()) {
                    b.incSkipped().warn(messages.get("admin.import.quiz.category_not_found", i + 1, catCode));
                    continue;
                }

                String qEn = getTextOrNull(qNode, "questionEn");
                if (qEn == null) {
                    b.incSkipped().warn(messages.get("admin.import.quiz.missing_question_en", i + 1));
                    continue;
                }

                JsonNode optionsNode = qNode.get("options");
                if (optionsNode == null || !optionsNode.isArray() || optionsNode.size() < 2) {
                    b.incSkipped().warn(messages.get("admin.import.quiz.options_min", i + 1));
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
                    b.incSkipped().warn(messages.get("admin.import.quiz.no_correct", i + 1));
                    continue;
                }

                b.incCreated(); // quiz questions are always new (no upsert key)

                if (!dryRun) {
                    Category category = optCat.get();
                    QuizQuestion qq = new QuizQuestion();
                    qq.setCategory(category);
                    qq.setQuestionEn(DrivingTextSanitizer.sanitize("EN", qEn));
                    qq.setQuestionAr(DrivingTextSanitizer.sanitize("AR",
                            getTextOrNull(qNode, "questionAr") != null ? getTextOrNull(qNode, "questionAr") : qEn));
                    qq.setQuestionNl(DrivingTextSanitizer.sanitize("NL",
                            getTextOrNull(qNode, "questionNl") != null ? getTextOrNull(qNode, "questionNl") : qEn));
                    qq.setQuestionFr(DrivingTextSanitizer.sanitize("FR",
                            getTextOrNull(qNode, "questionFr") != null ? getTextOrNull(qNode, "questionFr") : qEn));

                    String diff = getTextOrNull(qNode, "difficultyLevel");
                    qq.setDifficultyLevel(parseDifficulty(diff));

                    String qType = getTextOrNull(qNode, "questionType");
                    qq.setQuestionType(parseQuestionType(qType));

                    qq.setExplanationEn(DrivingTextSanitizer.sanitize("EN", getTextOrNull(qNode, "explanationEn")));
                    qq.setExplanationAr(DrivingTextSanitizer.sanitize("AR", getTextOrNull(qNode, "explanationAr")));
                    qq.setExplanationNl(DrivingTextSanitizer.sanitize("NL", getTextOrNull(qNode, "explanationNl")));
                    qq.setExplanationFr(DrivingTextSanitizer.sanitize("FR", getTextOrNull(qNode, "explanationFr")));
                    qq.setContentImageUrl(getTextOrNull(qNode, "contentImageUrl"));
                    qq.setIsActive(true);
                    qq.setStatus(QuizQuestion.QuestionStatus.PUBLISHED);
                    qq.setPublishedAt(LocalDateTime.now());

                    int order = 0;
                    for (JsonNode optNode : optionsNode) {
                        QuizAnswerOption opt = new QuizAnswerOption();
                        String tEn = getTextOrNull(optNode, "textEn");
                        if (tEn == null)
                            tEn = "";
                        String tAr = getTextOrNull(optNode, "textAr") != null ? getTextOrNull(optNode, "textAr") : tEn;
                        String tNl = getTextOrNull(optNode, "textNl") != null ? getTextOrNull(optNode, "textNl") : tEn;
                        String tFr = getTextOrNull(optNode, "textFr") != null ? getTextOrNull(optNode, "textFr") : tEn;
                        // Reject placeholder / corrupted translations at import time
                        if (PlaceholderDetector.hasPlaceholderNonBlank(tEn, tNl, tFr, tAr)) {
                            log.warn("⚠️ DataImport: placeholder option skipped — question='{}', text_en='{}'", qEn,
                                    tEn);
                            order++;
                            continue;
                        }
                        opt.setOptionTextEn(DrivingTextSanitizer.sanitize("EN", tEn));
                        opt.setOptionTextAr(DrivingTextSanitizer.sanitize("AR", tAr));
                        opt.setOptionTextNl(DrivingTextSanitizer.sanitize("NL", tNl));
                        opt.setOptionTextFr(DrivingTextSanitizer.sanitize("FR", tFr));
                        JsonNode ic = optNode.get("isCorrect");
                        opt.setIsCorrect(ic != null && ic.asBoolean());
                        opt.setDisplayOrder(order++);
                        qq.addOption(opt);
                    }
                    ;
                    quizQuestionRepository.save(qq);
                }
            }
        } catch (Exception e) {
            log.error("Quiz questions upload import failed: {}", e.getMessage());
            b.error(messages.get("admin.import.parse_quiz_failed", e.getMessage()));
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
