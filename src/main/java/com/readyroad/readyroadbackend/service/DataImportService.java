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
        JsonNode root;
        try {
            root = objectMapper.readTree(content);
        } catch (Exception e) {
            log.error("Categories upload import failed: {}", e.getMessage());
            b.error(messages.get("admin.import.parse_categories_failed", e.getMessage()));
            return b.build();
        }

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
        return b.build();
    }

    // ── Quiz Questions (Phase 3) ──────────────────────────────────

    @Transactional
    public ImportReport importQuizQuestionsFromUpload(byte[] content, boolean dryRun) {
        ImportReport.Builder b = new ImportReport.Builder("quiz_questions", dryRun);
        List<JsonNode> questions;
        try {
            questions = objectMapper.readValue(content, new TypeReference<List<JsonNode>>() {
            });
        } catch (Exception e) {
            log.error("Quiz questions upload import failed: {}", e.getMessage());
            b.error(messages.get("admin.import.parse_quiz_failed", e.getMessage()));
            return b.build();
        }

        List<QuizImportCandidate> candidates = new ArrayList<>();
        Set<String> uploadKeys = new HashSet<>();
        boolean hasValidationErrors = false;

        for (int i = 0; i < questions.size(); i++) {
            JsonNode qNode = questions.get(i);
            int itemNumber = i + 1;
            String catCode = getTextOrNull(qNode, "categoryCode");
            if (catCode == null) {
                b.incSkipped().error(messages.get("admin.import.quiz.missing_category", itemNumber));
                hasValidationErrors = true;
                continue;
            }

            Optional<Category> optCat = categoryRepository.findByCode(catCode);
            if (optCat.isEmpty()) {
                b.incSkipped().error(messages.get("admin.import.quiz.category_not_found", itemNumber, catCode));
                hasValidationErrors = true;
                continue;
            }

            String qEn = getTextOrNull(qNode, "questionEn");
            if (qEn == null || qEn.isBlank()) {
                b.incSkipped().error(messages.get("admin.import.quiz.missing_question_en", itemNumber));
                hasValidationErrors = true;
                continue;
            }

            JsonNode optionsNode = qNode.get("options");
            if (optionsNode == null || !optionsNode.isArray() || optionsNode.size() < 2 || optionsNode.size() > 3) {
                b.incSkipped().error(messages.get("admin.import.quiz.options_count", itemNumber));
                hasValidationErrors = true;
                continue;
            }

            int correctCount = 0;
            boolean invalidOptionText = false;
            for (JsonNode option : optionsNode) {
                String textEn = getTextOrNull(option, "textEn");
                String textAr = getTextOrNull(option, "textAr");
                String textNl = getTextOrNull(option, "textNl");
                String textFr = getTextOrNull(option, "textFr");
                if (textEn == null || textEn.isBlank()
                        || PlaceholderDetector.hasPlaceholderNonBlank(textEn, textNl, textFr, textAr)) {
                    invalidOptionText = true;
                }
                if (option.path("isCorrect").asBoolean(false)) {
                    correctCount++;
                }
            }
            if (invalidOptionText) {
                b.incSkipped().error(messages.get("admin.import.quiz.invalid_option_text", itemNumber));
                hasValidationErrors = true;
                continue;
            }
            if (correctCount != 1) {
                b.incSkipped().error(messages.get("admin.import.quiz.correct_count", itemNumber));
                hasValidationErrors = true;
                continue;
            }

            Category category = optCat.get();
            String duplicateKey = category.getId() + "\u0000" + qEn.trim().toLowerCase(Locale.ROOT);
            if (!uploadKeys.add(duplicateKey)
                    || quizQuestionRepository.existsByCategoryIdAndNormalizedQuestionEn(category.getId(), qEn)) {
                b.incSkipped().warn(messages.get("admin.import.quiz.duplicate", itemNumber));
                continue;
            }

            b.incCreated();
            candidates.add(new QuizImportCandidate(category, qNode, qEn, optionsNode));
        }

        if (dryRun || hasValidationErrors) {
            return b.build();
        }

        for (QuizImportCandidate candidate : candidates) {
            quizQuestionRepository.save(toQuizQuestion(candidate));
        }
        return b.build();
    }

    private QuizQuestion toQuizQuestion(QuizImportCandidate candidate) {
        JsonNode qNode = candidate.question();
        String qEn = candidate.questionEn();
        QuizQuestion question = new QuizQuestion();
        question.setCategory(candidate.category());
        question.setQuestionEn(DrivingTextSanitizer.sanitize("EN", qEn));
        question.setQuestionAr(DrivingTextSanitizer.sanitize("AR",
                getTextOrNull(qNode, "questionAr") != null ? getTextOrNull(qNode, "questionAr") : qEn));
        question.setQuestionNl(DrivingTextSanitizer.sanitize("NL",
                getTextOrNull(qNode, "questionNl") != null ? getTextOrNull(qNode, "questionNl") : qEn));
        question.setQuestionFr(DrivingTextSanitizer.sanitize("FR",
                getTextOrNull(qNode, "questionFr") != null ? getTextOrNull(qNode, "questionFr") : qEn));
        question.setDifficultyLevel(parseDifficulty(getTextOrNull(qNode, "difficultyLevel")));
        question.setQuestionType(parseQuestionType(getTextOrNull(qNode, "questionType")));
        question.setExplanationEn(DrivingTextSanitizer.sanitize("EN", getTextOrNull(qNode, "explanationEn")));
        question.setExplanationAr(DrivingTextSanitizer.sanitize("AR", getTextOrNull(qNode, "explanationAr")));
        question.setExplanationNl(DrivingTextSanitizer.sanitize("NL", getTextOrNull(qNode, "explanationNl")));
        question.setExplanationFr(DrivingTextSanitizer.sanitize("FR", getTextOrNull(qNode, "explanationFr")));
        question.setContentImageUrl(getTextOrNull(qNode, "contentImageUrl"));
        question.setIsActive(true);
        question.setStatus(QuizQuestion.QuestionStatus.PUBLISHED);
        question.setPublishedAt(LocalDateTime.now());

        int order = 0;
        for (JsonNode optionNode : candidate.options()) {
            QuizAnswerOption option = new QuizAnswerOption();
            String textEn = getTextOrNull(optionNode, "textEn");
            String textAr = getTextOrNull(optionNode, "textAr") != null ? getTextOrNull(optionNode, "textAr") : textEn;
            String textNl = getTextOrNull(optionNode, "textNl") != null ? getTextOrNull(optionNode, "textNl") : textEn;
            String textFr = getTextOrNull(optionNode, "textFr") != null ? getTextOrNull(optionNode, "textFr") : textEn;
            option.setOptionTextEn(DrivingTextSanitizer.sanitize("EN", textEn));
            option.setOptionTextAr(DrivingTextSanitizer.sanitize("AR", textAr));
            option.setOptionTextNl(DrivingTextSanitizer.sanitize("NL", textNl));
            option.setOptionTextFr(DrivingTextSanitizer.sanitize("FR", textFr));
            option.setIsCorrect(optionNode.path("isCorrect").asBoolean(false));
            option.setDisplayOrder(order++);
            question.addOption(option);
        }
        return question;
    }

    private record QuizImportCandidate(Category category, JsonNode question, String questionEn, JsonNode options) {
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
