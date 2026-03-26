package com.readyroad.readyroadbackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.*;
import com.readyroad.readyroadbackend.domain.enums.SignCategory;
import com.readyroad.readyroadbackend.domain.enums.SignDifficulty;
import com.readyroad.readyroadbackend.domain.enums.SignQuestionType;
import com.readyroad.readyroadbackend.domain.repository.*;
import com.readyroad.readyroadbackend.dto.RoadSignDetailDto;
import com.readyroad.readyroadbackend.dto.RoadSignSummaryDto;
import com.readyroad.readyroadbackend.dto.SignImportResultDto;
import com.readyroad.readyroadbackend.util.ImportedTextSanitizer;
import com.readyroad.readyroadbackend.util.RouteCodeNormalizer;
import com.readyroad.readyroadbackend.util.SignQuestionTextSanitizer;
import com.readyroad.readyroadbackend.util.TextNormalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.File;
import java.util.*;

/**
 * Sign Quiz Importer — reads the signs_import/ directory and populates
 * the road_signs, sign_questions, sign_choices, sign_exams, and
 * sign_exam_questions tables.
 *
 * <h3>9-Step validation per sign directory:</h3>
 * <ol>
 * <li>Directory scan — collect sign folders</li>
 * <li>File existence — sign.json, questions.json, exams.json</li>
 * <li>sign.json validation — code, category, i18n (NL/EN/FR/AR)</li>
 * <li>questions.json validation — types, choices count:
 * IS_IT_ALLOWED→2, others→3, anything else→reject</li>
 * <li>exams.json validation — 8 questions in exam_1 (3 EASY + 3 MEDIUM + 2
 * HARD)</li>
 * <li>Upsert road_signs</li>
 * <li>Upsert sign_questions + sign_choices in place (preserve historical FK integrity)</li>
 * <li>Upsert sign_exams + sign_exam_questions in place</li>
 * <li>Save sign_import_runs and return result</li>
 * </ol>
 */
@Service
public class SignQuizImportService {

    private static final Logger log = LoggerFactory.getLogger(SignQuizImportService.class);

    private static final int REQUIRED_CHOICES_BINARY = 2;
    private static final int REQUIRED_CHOICES_MULTI = 3;
    private static final int REQUIRED_EXAM_QUESTIONS = 8;
    private static final int REQUIRED_EASY = 3;
    private static final int REQUIRED_MEDIUM = 3;
    private static final int REQUIRED_HARD = 2;

    private static final Set<String> VALID_LANGS = Set.of("NL", "EN", "FR", "AR");

    private final RoadSignRepository roadSignRepo;
    private final SignQuestionRepository questionRepo;
    private final SignExamRepository examRepo;
    private final SignImportRunRepository importRunRepo;
    private final ObjectMapper mapper;
    private final CanonicalSignCatalogService canonicalSignCatalogService;
    /**
     * Used for per-sign transaction isolation.
     * Each sign is committed independently so a DB error in one sign
     * does NOT roll back the rest of the import.
     */
    private final TransactionTemplate txTemplate;

    @Value("${readyroad.signs-import.path:C:/Users/haydar/Desktop/end_project/readyroad/src/main/resources/data/signs_import}")
    private String signsImportPath;

    public SignQuizImportService(RoadSignRepository roadSignRepo,
            SignQuestionRepository questionRepo,
            SignExamRepository examRepo,
            SignImportRunRepository importRunRepo,
            ObjectMapper mapper,
            PlatformTransactionManager txManager,
            CanonicalSignCatalogService canonicalSignCatalogService) {
        this.roadSignRepo = roadSignRepo;
        this.questionRepo = questionRepo;
        this.examRepo = examRepo;
        this.importRunRepo = importRunRepo;
        this.mapper = mapper;
        this.txTemplate = new TransactionTemplate(txManager);
        this.canonicalSignCatalogService = canonicalSignCatalogService;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Public entry point
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Runs the full import. Each sign directory is committed in its own
     * independent transaction (via {@link TransactionTemplate}) so that a DB
     * error in one sign never rolls back the rest.
     *
     * <p>
     * Steps 2-5 (JSON parsing + validation) run outside any transaction.
 * Steps 6-8 (DB upsert) run inside a per-sign {@code txTemplate.execute}.
     * Step 9 (run-record save) runs in its own transaction.
     * </p>
     *
     * @return a summary record (also persisted to sign_import_runs)
     */
    public SignImportRun runImport(String performedBy) {
        long startMs = System.currentTimeMillis();
        log.info("═══ Sign Quiz Import started by [{}] ═══", performedBy);

        // Step 1 — scan directory
        File importDir = new File(signsImportPath);
        if (!importDir.exists() || !importDir.isDirectory()) {
            return failRun(performedBy, startMs,
                    "Import directory not found: " + signsImportPath);
        }

        File[] signDirs = importDir.listFiles(File::isDirectory);
        if (signDirs == null || signDirs.length == 0) {
            return failRun(performedBy, startMs, "No sign directories found in: " + signsImportPath);
        }

        log.info("Found {} sign directories", signDirs.length);

        // Counters
        int signsCreated = 0;
        int signsUpdated = 0;
        int signsSkipped = 0;
        int questionsCreated = 0;
        int questionsUpdated = 0;
        int examsCreated = 0;
        int errorsCount = 0;
        List<String> errorLines = new ArrayList<>();

        // Process each sign
        for (File dir : signDirs) {
            String dirName = dir.getName();
            try {
                // Steps 2-5 — file IO + validation (no DB, no transaction needed)
                File signFile = new File(dir, "sign.json");
                File questFile = new File(dir, "questions.json");
                File examsFile = new File(dir, "exams.json");

                if (!signFile.exists() || !questFile.exists() || !examsFile.exists()) {
                    throw new ImportValidationException("Missing required JSON file(s)");
                }

                final JsonNode signNode = mapper.readTree(signFile);
                final JsonNode questionsNode = mapper.readTree(questFile);
                final JsonNode examsNode = mapper.readTree(examsFile);
                final List<String> qRefs = extractQuestionRefs(questionsNode, signNode);
                final Map<String, SignDifficulty> questionDifficulties = extractQuestionDifficulties(questionsNode);

                validateSignJson(signNode, dirName);
                validateQuestionsJson(questionsNode, dirName);
                validateExamsJson(examsNode, qRefs, questionDifficulties, dirName);

                // Steps 6-8 — DB upsert in its own independent transaction.
                // If this sign fails, only THIS sign is rolled back; others are unaffected.
                int[] counts = txTemplate.execute(status -> {
                    boolean isNew = !roadSignRepo.existsBySignCode(
                            signNode.path("code").asText().trim());
                    RoadSign sign = upsertRoadSign(signNode);
                    int[] qCounts = upsertQuestions(questionsNode, sign);
                    int ec = upsertExams(examsNode, sign);
                    // [0]=created, [1]=updated, [2]=qCreated, [3]=qUpdated, [4]=exams
                    return new int[] { isNew ? 1 : 0, isNew ? 0 : 1,
                            qCounts[0], qCounts[1], ec };
                });

                if (counts == null)
                    throw new IllegalStateException("Transaction returned null");

                signsCreated += counts[0];
                signsUpdated += counts[1];
                questionsCreated += counts[2];
                questionsUpdated += counts[3];
                examsCreated += counts[4];

                log.debug("✓ {} ({})", dirName, counts[0] > 0 ? "NEW" : "UPDATE");

            } catch (ImportValidationException e) {
                signsSkipped++;
                errorsCount++;
                String msg = "[" + dirName + "] VALIDATION: " + e.getMessage();
                errorLines.add(msg);
                log.warn(msg);
            } catch (Exception e) {
                errorsCount++;
                String msg = "[" + dirName + "] ERROR: " + e.getMessage();
                errorLines.add(msg);
                log.error(msg, e);
            }
        }

        Integer cleanupCount = txTemplate.execute(status -> canonicalizeAndDeactivateStaleSigns());
        int deactivatedCount = cleanupCount == null ? 0 : cleanupCount;
        if (deactivatedCount > 0) {
            log.info("Deactivated {} stale or duplicate road sign rows after import", deactivatedCount);
        }

        Integer childCleanupCount = txTemplate.execute(status -> deactivateArtifactsForInactiveSigns());
        int childArtifactsDeactivated = childCleanupCount == null ? 0 : childCleanupCount;
        if (childArtifactsDeactivated > 0) {
            log.info("Deactivated {} orphaned question/exam rows for inactive signs", childArtifactsDeactivated);
        }

        List<String> coverageIssues = txTemplate.execute(status -> validateActiveSignCoverage());
        if (coverageIssues != null && !coverageIssues.isEmpty()) {
            errorsCount += coverageIssues.size();
            errorLines.addAll(coverageIssues);
            coverageIssues.forEach(issue -> log.error("Coverage issue: {}", issue));
        }

        // Step 9 — save run record in its own transaction
        final int fProcessed = signDirs.length;
        final int fCreated = signsCreated;
        final int fUpdated = signsUpdated;
        final int fSkipped = signsSkipped;
        final int fQCreated = questionsCreated;
        final int fQUpdated = questionsUpdated;
        final int fECreated = examsCreated;
        final int fErrors = errorsCount;
        String rawSummary = errorLines.isEmpty() ? null : String.join("\n", errorLines);
        final String fErrorSummary = (rawSummary != null && rawSummary.length() > 60000)
                ? rawSummary.substring(0, 60000) + "\n[truncated]"
                : rawSummary;
        final long fDuration = System.currentTimeMillis() - startMs;
        final String fStatus = fErrors == 0 ? "SUCCESS"
                : (fCreated + fUpdated > 0) ? "PARTIAL" : "FAILED";

        SignImportRun saved = txTemplate.execute(status -> {
            SignImportRun run = new SignImportRun();
            run.setPerformedBy(performedBy);
            run.setStatus(fStatus);
            run.setSignsProcessed(fProcessed);
            run.setSignsCreated(fCreated);
            run.setSignsUpdated(fUpdated);
            run.setSignsSkipped(fSkipped);
            run.setQuestionsCreated(fQCreated);
            run.setQuestionsUpdated(fQUpdated);
            run.setExamsCreated(fECreated);
            run.setErrorsCount(fErrors);
            run.setErrorSummary(fErrorSummary);
            run.setDurationMs(fDuration);
            return importRunRepo.save(run);
        });

        log.info("═══ Import DONE — {} — signs: +{} ~{} skip:{}, questions: +{} ~{}, exams: +{}, errors: {} ═══",
                fStatus, fCreated, fUpdated, fSkipped, fQCreated, fQUpdated, fECreated, fErrors);

        return saved;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Step 3 — Validate sign.json
    // ─────────────────────────────────────────────────────────────────────────

    private void validateSignJson(JsonNode node, String dir) throws ImportValidationException {
        requireText(node, "code", dir);
        requireText(node, "category", dir);

        // category must be a known enum value
        String cat = node.path("category").asText().trim();
        try {
            SignCategory.valueOf(cat);
        } catch (IllegalArgumentException ex) {
            throw new ImportValidationException("Unknown category: " + cat);
        }

        // i18n must contain all 4 languages
        JsonNode i18n = node.path("i18n");
        if (i18n.isMissingNode() || !i18n.isObject()) {
            throw new ImportValidationException("sign.json missing 'i18n' object");
        }
        for (String lang : VALID_LANGS) {
            if (!i18n.has(lang)) {
                throw new ImportValidationException("sign.json i18n missing language: " + lang);
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Step 4 — Validate questions.json
    // ─────────────────────────────────────────────────────────────────────────

    private void validateQuestionsJson(JsonNode node, String dir) throws ImportValidationException {
        if (!node.isArray() || node.size() == 0) {
            throw new ImportValidationException("questions.json must be a non-empty array");
        }

        for (JsonNode q : node) {
            String qid = q.path("question_id").asText("?");
            String type = q.path("type").asText("").trim();

            // validate question type
            SignQuestionType qType;
            try {
                qType = SignQuestionType.valueOf(type);
            } catch (IllegalArgumentException ex) {
                throw new ImportValidationException(
                        "[" + qid + "] Unknown question type: " + type);
            }

            // validate difficulty
            String diff = q.path("difficulty").asText("").trim();
            try {
                SignDifficulty.valueOf(diff);
            } catch (IllegalArgumentException ex) {
                throw new ImportValidationException(
                        "[" + qid + "] Unknown difficulty: " + diff);
            }

            // validate i18n
            JsonNode i18n = q.path("i18n");
            if (i18n.isMissingNode() || !i18n.isObject()) {
                throw new ImportValidationException("[" + qid + "] Missing i18n");
            }

            for (String lang : VALID_LANGS) {
                JsonNode langNode = i18n.path(lang);
                if (langNode.isMissingNode()) {
                    throw new ImportValidationException(
                            "[" + qid + "] i18n missing language: " + lang);
                }

                JsonNode choices = langNode.path("choices");
                if (!choices.isArray()) {
                    throw new ImportValidationException(
                            "[" + qid + "][" + lang + "] choices must be an array");
                }

                int choiceCount = choices.size();
                // IS_IT_ALLOWED is binary. All other supported question types
                // must offer three choices.
                int required = (qType == SignQuestionType.IS_IT_ALLOWED)
                        ? REQUIRED_CHOICES_BINARY
                        : REQUIRED_CHOICES_MULTI;

                if (choiceCount != required) {
                    throw new ImportValidationException(
                            "[" + qid + "][" + lang + "] difficulty=" + diff
                                    + " requires exactly " + required + " choices but found " + choiceCount);
                }

                // exactly 1 correct choice per language
                long correctCount = 0;
                for (JsonNode c : choices) {
                    if (c.path("is_correct").asBoolean(false))
                        correctCount++;
                }
                if (correctCount != 1) {
                    throw new ImportValidationException(
                            "[" + qid + "][" + lang + "] must have exactly 1 correct choice, found " + correctCount);
                }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Step 5 — Validate exams.json
    // ─────────────────────────────────────────────────────────────────────────

    private void validateExamsJson(
            JsonNode node,
            List<String> allQuestionRefs,
            Map<String, SignDifficulty> questionDifficulties,
            String dir)
            throws ImportValidationException {

        // exams.json may contain template brackets like [A1_Q06] — skip those
        // Only validate if exams have real (non-bracketed) question references

        JsonNode exam1 = node.path("exam_1");

        if (exam1.isMissingNode()) {
            throw new ImportValidationException("exams.json must contain 'exam_1'");
        }

        JsonNode q1 = exam1.path("questions");

        if (!q1.isArray()) {
            throw new ImportValidationException("exams.json exam_1 must have a 'questions' array");
        }

        if (q1.size() != REQUIRED_EXAM_QUESTIONS) {
            throw new ImportValidationException(
                    "exam_1 must have exactly " + REQUIRED_EXAM_QUESTIONS
                            + " questions. Found: " + q1.size());
        }

        // All real refs (non-bracketed) must exist in questions.json
        List<String> refs1 = toRealRefs(q1);
        Set<String> knownRefs = new HashSet<>(allQuestionRefs);
        for (String r : refs1) {
            if (!knownRefs.contains(r)) {
                throw new ImportValidationException(
                        "exam_1 references unknown question: " + r);
            }
        }

        if (refs1.stream().distinct().count() != refs1.size()) {
            throw new ImportValidationException("exam_1 contains duplicate question references");
        }

        long easyCount = refs1.stream()
                .filter(ref -> questionDifficulties.get(ref) == SignDifficulty.EASY)
                .count();
        long mediumCount = refs1.stream()
                .filter(ref -> questionDifficulties.get(ref) == SignDifficulty.MEDIUM)
                .count();
        long hardCount = refs1.stream()
                .filter(ref -> questionDifficulties.get(ref) == SignDifficulty.HARD)
                .count();

        if (easyCount != REQUIRED_EASY || mediumCount != REQUIRED_MEDIUM || hardCount != REQUIRED_HARD) {
            throw new ImportValidationException(
                    "exam_1 must contain exactly " + REQUIRED_EASY + " EASY, "
                            + REQUIRED_MEDIUM + " MEDIUM, and " + REQUIRED_HARD + " HARD questions"
                            + " based on questions.json; found easy=" + easyCount
                            + ", medium=" + mediumCount
                            + ", hard=" + hardCount);
        }
    }

    private List<String> toRealRefs(JsonNode qArray) {
        List<String> result = new ArrayList<>();
        for (JsonNode q : qArray) {
            String ref = q.asText("").trim();
            if (!ref.startsWith("[")) {
                result.add(ref);
            }
        }
        return result;
    }

    private List<String> extractQuestionRefs(JsonNode questionsNode, JsonNode signNode) {
        List<String> refs = new ArrayList<>();
        for (JsonNode q : questionsNode) {
            String ref = q.path("question_id").asText("").trim();
            if (!ref.isEmpty())
                refs.add(ref);
        }
        return refs;
    }

    private Map<String, SignDifficulty> extractQuestionDifficulties(JsonNode questionsNode) {
        Map<String, SignDifficulty> difficulties = new LinkedHashMap<>();
        for (JsonNode q : questionsNode) {
            String ref = q.path("question_id").asText("").trim();
            String difficulty = q.path("difficulty").asText("").trim();
            if (!ref.isEmpty() && !difficulty.isEmpty()) {
                difficulties.put(ref, SignDifficulty.valueOf(difficulty));
            }
        }
        return difficulties;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Step 6 — Upsert RoadSign
    // ─────────────────────────────────────────────────────────────────────────

    private RoadSign upsertRoadSign(JsonNode node) {
        String signCode = node.path("code").asText().trim();
        String imagePath = node.path("image_path").asText(null);
        String routeSource = firstNonBlank(
                node.path("route_code").asText(""),
                node.path("id").asText(""),
                signCode);
        String routeKey = normalizeRouteKey(routeSource);

        Optional<CanonicalSignCatalogService.CanonicalSignSeed> seedOpt = canonicalSignCatalogService
                .findSeedByRouteCode(routeSource)
                .or(() -> canonicalSignCatalogService.findSeedByImagePath(imagePath));

        String preferredRouteKey = seedOpt
                .map(CanonicalSignCatalogService.CanonicalSignSeed::routeKey)
                .orElse(routeKey);
        String preferredSignCode = seedOpt
                .map(CanonicalSignCatalogService.CanonicalSignSeed::routeCode)
                .orElse(signCode);

        RoadSign sign = roadSignRepo.findByNormalizedSignCode(preferredRouteKey)
                .or(() -> roadSignRepo.findFirstBySignCodeOrderByIdAsc(preferredSignCode))
                .or(() -> roadSignRepo.findByNormalizedSignCode(routeKey))
                .or(() -> roadSignRepo.findFirstBySignCodeOrderByIdAsc(signCode))
                .orElse(new RoadSign());

        sign.setSignCode(signCode);
        sign.setNormalizedSignCode(routeKey);
        sign.setCategory(SignCategory.valueOf(node.path("category").asText().trim()));
        sign.setImagePath(imagePath);
        sign.setSeriousViolation(node.path("serious_violation").asBoolean(false));
        sign.setIsActive(true);

        JsonNode i18n = node.path("i18n");
        sign.setNameNl(text(i18n, "NL", "name"));
        sign.setNameEn(text(i18n, "EN", "name"));
        sign.setNameFr(text(i18n, "FR", "name"));
        sign.setNameAr(text(i18n, "AR", "name"));
        sign.setDescriptionNl(text(i18n, "NL", "description"));
        sign.setDescriptionEn(text(i18n, "EN", "description"));
        sign.setDescriptionFr(text(i18n, "FR", "description"));
        sign.setDescriptionAr(text(i18n, "AR", "description"));
        canonicalSignCatalogService.applyCanonicalFields(sign);

        return roadSignRepo.save(sign);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Step 7 — Upsert Questions + Choices
    // ─────────────────────────────────────────────────────────────────────────

    private int[] upsertQuestions(JsonNode questionsNode, RoadSign sign) {
        int created = 0;
        int updated = 0;
        Set<String> importedRefs = new HashSet<>();

        for (JsonNode q : questionsNode) {
            String ref = q.path("question_id").asText().trim();
            importedRefs.add(ref);

            SignQuestion question = questionRepo.findByQuestionRef(ref)
                    .orElseGet(SignQuestion::new);
            boolean isNew = question.getId() == null;
            SignQuestionType questionType = SignQuestionType.valueOf(q.path("type").asText().trim());

            question.setSign(sign);
            question.setQuestionRef(ref);
            question.setQuestionType(questionType);
            question.setDifficulty(SignDifficulty.valueOf(q.path("difficulty").asText().trim()));
            question.setIsCritical(q.path("is_critical").asBoolean(false));
            question.setShowSign(q.path("show_sign").asBoolean(true));
            question.setIsActive(true);

            JsonNode i18n = q.path("i18n");
            question.setQuestionNl(text(i18n, "NL", "question"));
            question.setQuestionEn(text(i18n, "EN", "question"));
            question.setQuestionFr(text(i18n, "FR", "question"));
            question.setQuestionAr(text(i18n, "AR", "question"));
            question.setExplanationNl(SignQuestionTextSanitizer.sanitizeExplanation(questionType, text(i18n, "NL", "explanation")));
            question.setExplanationEn(SignQuestionTextSanitizer.sanitizeExplanation(questionType, text(i18n, "EN", "explanation")));
            question.setExplanationFr(SignQuestionTextSanitizer.sanitizeExplanation(questionType, text(i18n, "FR", "explanation")));
            question.setExplanationAr(SignQuestionTextSanitizer.sanitizeExplanation(questionType, text(i18n, "AR", "explanation")));

            SignQuestion saved = questionRepo.save(question);
            syncChoices(saved, i18n, isNew, questionType);
            questionRepo.save(saved);

            if (isNew)
                created++;
            else
                updated++;
        }

        for (SignQuestion existing : questionRepo.findAllBySignId(sign.getId())) {
            if (!importedRefs.contains(existing.getQuestionRef())) {
                existing.setIsActive(false);
                questionRepo.save(existing);
            }
        }

        return new int[] { created, updated };
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Step 8 — Upsert Exams
    // ─────────────────────────────────────────────────────────────────────────

    private int upsertExams(JsonNode examsNode, RoadSign sign) {
        int created = 0;
        Set<Integer> importedExamNumbers = new HashSet<>();

        Iterator<String> fieldNames = examsNode.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            if (!fieldName.startsWith("exam_")) {
                continue;
            }

            Integer examNumber;
            try {
                examNumber = Integer.parseInt(fieldName.substring("exam_".length()));
            } catch (NumberFormatException ex) {
                continue;
            }

            importedExamNumbers.add(examNumber);

            SignExam exam = examRepo.findBySignIdAndExamNumber(sign.getId(), examNumber)
                    .orElseGet(SignExam::new);
            boolean isNew = exam.getId() == null;

            exam.setSign(sign);
            exam.setExamNumber(examNumber);
            exam.setPassingScore(examsNode.path("passing_score").asInt(12));
            exam.setTotalQuestions(examsNode.path("total_questions").asInt(15));
            exam.setIsActive(true);

            JsonNode dist = examsNode.path("distribution");
            exam.setEasyCount(dist.path("EASY").asInt(REQUIRED_EASY));
            exam.setMediumCount(dist.path("MEDIUM").asInt(REQUIRED_MEDIUM));
            exam.setHardCount(dist.path("HARD").asInt(REQUIRED_HARD));

            SignExam savedExam = examRepo.save(exam);
            syncExamQuestions(savedExam, examsNode.path(fieldName).path("questions"));
            examRepo.save(savedExam);

            if (isNew) {
                created++;
            }
        }

        for (SignExam existing : examRepo.findAllBySignIdOrderByExamNumberAsc(sign.getId())) {
            if (!importedExamNumbers.contains(existing.getExamNumber())) {
                existing.setIsActive(false);
                examRepo.save(existing);
            }
        }

        return created;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Public read methods (used by SignQuizImportController)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns the most recent import run record as a DTO, or empty if none exists.
     */
    @Transactional(readOnly = true)
    public Optional<SignImportResultDto> getLastImportRun() {
        return importRunRepo.findTopByOrderByCreatedAtDesc()
                .map(SignImportResultDto::from);
    }

    /**
     * Returns all active road signs as lightweight summary DTOs,
     * ordered by sign code ascending.
     */
    @Transactional(readOnly = true)
    public List<RoadSignSummaryDto> getAllActiveSigns() {
        return roadSignRepo.findAllByIsActiveTrueOrderBySignCodeAsc()
                .stream()
                .map(RoadSignSummaryDto::from)
                .toList();
    }

    /**
     * Returns the full detail of a road sign (including questions + choices)
     * as a DTO, or empty if the sign code is not found.
     * Lazy collections are initialized inside this transaction.
     */
    @Transactional(readOnly = true)
    public Optional<RoadSignDetailDto> getSignDetailByCode(String code) {
        String normalized = code.toLowerCase().replaceAll("[^a-z0-9]+", "_");
        return roadSignRepo.findFirstByNormalizedSignCodeAndIsActiveTrueOrderByIdAsc(normalized)
                .or(() -> roadSignRepo.findFirstBySignCodeAndIsActiveTrueOrderByIdAsc(code))
                .map(RoadSignDetailDto::from);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private String text(JsonNode i18n, String lang, String field) {
        JsonNode n = i18n.path(lang).path(field);
        if (n.isMissingNode() || n.isNull())
            return null;
        String v = ImportedTextSanitizer.sanitize(n.asText(""));
        return v.isEmpty() ? null : v;
    }

    private String choiceText(JsonNode i18n, String lang, int idx, SignQuestionType questionType) {
        JsonNode choices = i18n.path(lang).path("choices");
        if (!choices.isArray() || idx >= choices.size())
            return null;
        return SignQuestionTextSanitizer.sanitizeChoice(
                questionType,
                ImportedTextSanitizer.sanitize(choices.get(idx).path("text").asText(null)));
    }

    private void syncChoices(SignQuestion question, JsonNode i18n, boolean isNewQuestion, SignQuestionType questionType) {
        List<ImportedChoiceData> importedChoices = buildImportedChoices(i18n, questionType);
        if (importedChoices.isEmpty()) {
            throw new IllegalStateException("Question " + question.getQuestionRef() + " has no choices");
        }

        if (isNewQuestion || question.getChoices().isEmpty()) {
            List<ImportedChoiceData> choiceOrder = new ArrayList<>(importedChoices);
            Collections.shuffle(choiceOrder);
            int order = 1;
            for (ImportedChoiceData importedChoice : choiceOrder) {
                SignChoice choice = new SignChoice();
                choice.setDisplayOrder(order++);
                applyChoiceData(choice, importedChoice);
                question.addChoice(choice);
            }
            return;
        }

        List<SignChoice> existingChoices = new ArrayList<>(question.getChoices());
        existingChoices.sort(Comparator.comparing(
                choice -> Optional.ofNullable(choice.getDisplayOrder()).orElse(Integer.MAX_VALUE)));

        if (existingChoices.size() != importedChoices.size()) {
            throw new IllegalStateException("Question " + question.getQuestionRef()
                    + " choice count mismatch: existing=" + existingChoices.size()
                    + ", imported=" + importedChoices.size());
        }

        Map<String, Deque<SignChoice>> choicesByFingerprint = new HashMap<>();
        for (SignChoice existingChoice : existingChoices) {
            choicesByFingerprint
                    .computeIfAbsent(choiceFingerprint(existingChoice), key -> new ArrayDeque<>())
                    .add(existingChoice);
        }

        List<SignChoice> remainingExisting = new ArrayList<>(existingChoices);
        Map<SignChoice, ImportedChoiceData> assignments = new LinkedHashMap<>();

        for (ImportedChoiceData importedChoice : importedChoices) {
            Deque<SignChoice> exactMatches = choicesByFingerprint.get(importedChoice.fingerprint());
            if (exactMatches != null && !exactMatches.isEmpty()) {
                SignChoice matched = exactMatches.removeFirst();
                assignments.put(matched, importedChoice);
                remainingExisting.remove(matched);
            }
        }

        List<ImportedChoiceData> unmatchedImported = new ArrayList<>();
        for (ImportedChoiceData importedChoice : importedChoices) {
            if (!assignments.containsValue(importedChoice)) {
                unmatchedImported.add(importedChoice);
            }
        }

        for (ImportedChoiceData importedChoice : unmatchedImported) {
            SignChoice bestMatch = findBestChoiceMatch(remainingExisting, importedChoice);
            if (bestMatch == null) {
                throw new IllegalStateException("Unable to map imported choice for question "
                        + question.getQuestionRef());
            }
            assignments.put(bestMatch, importedChoice);
            remainingExisting.remove(bestMatch);
        }

        if (!remainingExisting.isEmpty() || assignments.size() != importedChoices.size()) {
            throw new IllegalStateException("Choice synchronization left unmatched rows for question "
                    + question.getQuestionRef());
        }

        for (Map.Entry<SignChoice, ImportedChoiceData> entry : assignments.entrySet()) {
            applyChoiceData(entry.getKey(), entry.getValue());
        }
    }

    private List<ImportedChoiceData> buildImportedChoices(JsonNode i18n, SignQuestionType questionType) {
        JsonNode nlChoices = i18n.path("NL").path("choices");
        List<ImportedChoiceData> importedChoices = new ArrayList<>();
        for (int idx = 0; idx < nlChoices.size(); idx++) {
            importedChoices.add(new ImportedChoiceData(
                    choiceText(i18n, "NL", idx, questionType),
                    choiceText(i18n, "EN", idx, questionType),
                    choiceText(i18n, "FR", idx, questionType),
                    choiceText(i18n, "AR", idx, questionType),
                    nlChoices.get(idx).path("is_correct").asBoolean(false)));
        }
        return importedChoices;
    }

    private SignChoice findBestChoiceMatch(List<SignChoice> candidates, ImportedChoiceData importedChoice) {
        SignChoice bestMatch = null;
        int bestScore = Integer.MIN_VALUE;

        for (SignChoice candidate : candidates) {
            int score = choiceSimilarity(candidate, importedChoice);
            if (score > bestScore) {
                bestScore = score;
                bestMatch = candidate;
            }
        }

        return bestScore > Integer.MIN_VALUE ? bestMatch : null;
    }

    private int choiceSimilarity(SignChoice existingChoice, ImportedChoiceData importedChoice) {
        int score = 0;

        if (Objects.equals(choiceKey(existingChoice.getTextNl()), choiceKey(importedChoice.textNl()))) {
            score += 4;
        }
        if (Objects.equals(choiceKey(existingChoice.getTextEn()), choiceKey(importedChoice.textEn()))) {
            score += 3;
        }
        if (Objects.equals(choiceKey(existingChoice.getTextFr()), choiceKey(importedChoice.textFr()))) {
            score += 3;
        }
        if (Objects.equals(choiceKey(existingChoice.getTextAr()), choiceKey(importedChoice.textAr()))) {
            score += 5;
        }
        if (Objects.equals(Boolean.TRUE.equals(existingChoice.getIsCorrect()), importedChoice.isCorrect())) {
            score += 2;
        }

        return score;
    }

    private void applyChoiceData(SignChoice choice, ImportedChoiceData importedChoice) {
        choice.setIsCorrect(importedChoice.isCorrect());
        choice.setTextNl(importedChoice.textNl());
        choice.setTextEn(importedChoice.textEn());
        choice.setTextFr(importedChoice.textFr());
        choice.setTextAr(importedChoice.textAr());
    }

    private String choiceFingerprint(SignChoice choice) {
        return String.join("|",
                choiceKey(choice.getTextNl()),
                choiceKey(choice.getTextEn()),
                choiceKey(choice.getTextFr()),
                choiceKey(choice.getTextAr()),
                String.valueOf(Boolean.TRUE.equals(choice.getIsCorrect())));
    }

    private String choiceKey(String value) {
        if (value == null) {
            return "";
        }
        return TextNormalizer.normalize(ImportedTextSanitizer.sanitize(value))
                .trim()
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }

    private void syncExamQuestions(SignExam exam, JsonNode questionRefsNode) {
        if (!exam.getExamQuestions().isEmpty()) {
            exam.clearExamQuestions();
            examRepo.saveAndFlush(exam);
        }

        int order = 1;
        for (JsonNode qRef : questionRefsNode) {
            String ref = qRef.asText("").trim();
            if (ref.startsWith("[")) {
                continue;
            }

            SignQuestion question = questionRepo.findByQuestionRef(ref)
                    .orElseThrow(() -> new IllegalStateException(
                            "Exam references unknown question: " + ref));

            SignExamQuestion eq = new SignExamQuestion();
            eq.setQuestion(question);
            eq.setQuestionOrder(order++);
            exam.addExamQuestion(eq);
        }
    }

    private record ImportedChoiceData(
            String textNl,
            String textEn,
            String textFr,
            String textAr,
            boolean isCorrect) {
        private String fingerprint() {
            return String.join("|",
                    normalize(textNl),
                    normalize(textEn),
                    normalize(textFr),
                    normalize(textAr),
                    String.valueOf(isCorrect));
        }

        private static String normalize(String value) {
            if (value == null) {
                return "";
            }
            return TextNormalizer.normalize(ImportedTextSanitizer.sanitize(value))
                    .trim()
                    .replaceAll("\\s+", " ")
                    .toLowerCase(Locale.ROOT);
        }
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
        }
        return "";
    }

    private String normalizeRouteKey(String value) {
        return RouteCodeNormalizer.normalize(value);
    }

    private int canonicalizeAndDeactivateStaleSigns() {
        List<RoadSign> activeSigns = roadSignRepo.findAllByIsActiveTrueOrderBySignCodeAsc();
        Map<Long, Long> questionCountBySignId = new HashMap<>();
        Map<Long, Integer> examCountBySignId = new HashMap<>();
        Map<String, List<RoadSign>> signsByCanonicalRoute = new LinkedHashMap<>();
        int deactivated = 0;

        for (RoadSign sign : activeSigns) {
            Optional<CanonicalSignCatalogService.CanonicalSignSeed> seedOpt = canonicalSignCatalogService.findSeedFor(sign);
            if (seedOpt.isEmpty()) {
                sign.setIsActive(false);
                roadSignRepo.save(sign);
                deactivated++;
                continue;
            }

            CanonicalSignCatalogService.CanonicalSignSeed seed = seedOpt.get();
            signsByCanonicalRoute
                    .computeIfAbsent(seed.routeKey(), key -> new ArrayList<>())
                    .add(sign);
        }

        for (List<RoadSign> group : signsByCanonicalRoute.values()) {
            if (group.isEmpty()) {
                continue;
            }

            RoadSign keeper = selectKeeper(group, questionCountBySignId, examCountBySignId);
            canonicalSignCatalogService.applyCanonicalFields(keeper);
            keeper.setIsActive(true);
            roadSignRepo.save(keeper);

            for (RoadSign sign : group) {
                if (sign.getId().equals(keeper.getId())) {
                    continue;
                }
                sign.setIsActive(false);
                roadSignRepo.save(sign);
                deactivated++;
            }
        }

        return deactivated;
    }

    private RoadSign selectKeeper(
            List<RoadSign> group,
            Map<Long, Long> questionCountBySignId,
            Map<Long, Integer> examCountBySignId) {
        return group.stream()
                .max(Comparator
                        .comparingLong((RoadSign sign) -> questionCountBySignId.computeIfAbsent(
                                sign.getId(), questionRepo::countBySignId))
                        .thenComparingInt(sign -> examCountBySignId.computeIfAbsent(
                                sign.getId(), id -> examRepo.findAllBySignIdOrderByExamNumberAsc(id).size()))
                        .thenComparingInt(sign -> Boolean.TRUE.equals(sign.getIsActive()) ? 1 : 0)
                .thenComparingLong(RoadSign::getId))
                .orElseThrow(() -> new IllegalStateException("Expected at least one road sign in canonical group"));
    }

    private int deactivateArtifactsForInactiveSigns() {
        int updated = 0;

        for (RoadSign sign : roadSignRepo.findAll()) {
            if (Boolean.TRUE.equals(sign.getIsActive())) {
                continue;
            }

            List<SignQuestion> questions = questionRepo.findAllBySignId(sign.getId());
            for (SignQuestion question : questions) {
                if (Boolean.TRUE.equals(question.getIsActive())) {
                    question.setIsActive(false);
                    questionRepo.save(question);
                    updated++;
                }
            }

            List<SignExam> exams = examRepo.findAllBySignIdOrderByExamNumberAsc(sign.getId());
            for (SignExam exam : exams) {
                if (Boolean.TRUE.equals(exam.getIsActive())) {
                    exam.setIsActive(false);
                    examRepo.save(exam);
                    updated++;
                }
            }
        }

        return updated;
    }

    private List<String> validateActiveSignCoverage() {
        List<String> issues = new ArrayList<>();

        for (RoadSign sign : roadSignRepo.findAllByIsActiveTrueOrderBySignCodeAsc()) {
            List<SignQuestion> activeQuestions = questionRepo.findAllBySignIdAndIsActiveTrue(sign.getId());
            List<SignExam> activeExams = examRepo.findAllBySignIdAndIsActiveTrueOrderByExamNumberAsc(sign.getId());

            if (activeQuestions.size() != REQUIRED_EXAM_QUESTIONS) {
                issues.add("Active sign " + sign.getSignCode()
                        + " must have exactly " + REQUIRED_EXAM_QUESTIONS
                        + " active questions, found " + activeQuestions.size());
            }

            for (SignQuestion question : activeQuestions) {
                long correctChoices = question.getChoices().stream()
                        .filter(choice -> Boolean.TRUE.equals(choice.getIsCorrect()))
                        .count();
                int expectedChoiceCount = question.getQuestionType() == SignQuestionType.IS_IT_ALLOWED
                        ? REQUIRED_CHOICES_BINARY
                        : REQUIRED_CHOICES_MULTI;
                int actualChoiceCount = question.getChoices().size();

                if (actualChoiceCount != expectedChoiceCount) {
                    issues.add("Question " + question.getQuestionRef()
                            + " has " + actualChoiceCount
                            + " choices; expected " + expectedChoiceCount);
                }

                if (correctChoices != 1) {
                    issues.add("Question " + question.getQuestionRef()
                            + " must have exactly 1 correct choice, found " + correctChoices);
                }
            }

            if (activeExams.size() != 1) {
                issues.add("Active sign " + sign.getSignCode()
                        + " must have exactly 1 active exam, found " + activeExams.size());
                continue;
            }

            SignExam exam = activeExams.get(0);
            if (!Objects.equals(exam.getTotalQuestions(), REQUIRED_EXAM_QUESTIONS)
                    || !Objects.equals(exam.getEasyCount(), REQUIRED_EASY)
                    || !Objects.equals(exam.getMediumCount(), REQUIRED_MEDIUM)
                    || !Objects.equals(exam.getHardCount(), REQUIRED_HARD)) {
                issues.add("Exam " + sign.getSignCode() + "/" + exam.getExamNumber()
                        + " has invalid distribution: total=" + exam.getTotalQuestions()
                        + ", easy=" + exam.getEasyCount()
                        + ", medium=" + exam.getMediumCount()
                        + ", hard=" + exam.getHardCount());
            }

            if (exam.getExamQuestions().size() != REQUIRED_EXAM_QUESTIONS) {
                issues.add("Exam " + sign.getSignCode() + "/" + exam.getExamNumber()
                        + " must link exactly " + REQUIRED_EXAM_QUESTIONS
                        + " questions, found " + exam.getExamQuestions().size());
            }

            long distinctQuestionCount = exam.getExamQuestions().stream()
                    .map(SignExamQuestion::getQuestion)
                    .filter(Objects::nonNull)
                    .map(SignQuestion::getId)
                    .distinct()
                    .count();
            if (distinctQuestionCount != exam.getExamQuestions().size()) {
                issues.add("Exam " + sign.getSignCode() + "/" + exam.getExamNumber()
                        + " contains duplicate linked questions");
            }

            long actualEasyCount = exam.getExamQuestions().stream()
                    .map(SignExamQuestion::getQuestion)
                    .filter(Objects::nonNull)
                    .filter(linkedQuestion -> linkedQuestion.getDifficulty() == SignDifficulty.EASY)
                    .count();
            long actualMediumCount = exam.getExamQuestions().stream()
                    .map(SignExamQuestion::getQuestion)
                    .filter(Objects::nonNull)
                    .filter(linkedQuestion -> linkedQuestion.getDifficulty() == SignDifficulty.MEDIUM)
                    .count();
            long actualHardCount = exam.getExamQuestions().stream()
                    .map(SignExamQuestion::getQuestion)
                    .filter(Objects::nonNull)
                    .filter(linkedQuestion -> linkedQuestion.getDifficulty() == SignDifficulty.HARD)
                    .count();

            if (actualEasyCount != REQUIRED_EASY
                    || actualMediumCount != REQUIRED_MEDIUM
                    || actualHardCount != REQUIRED_HARD) {
                issues.add("Exam " + sign.getSignCode() + "/" + exam.getExamNumber()
                        + " links questions with invalid actual distribution: easy=" + actualEasyCount
                        + ", medium=" + actualMediumCount
                        + ", hard=" + actualHardCount);
            }

            for (SignExamQuestion examQuestion : exam.getExamQuestions()) {
                SignQuestion linkedQuestion = examQuestion.getQuestion();
                if (linkedQuestion == null || !Boolean.TRUE.equals(linkedQuestion.getIsActive())) {
                    issues.add("Exam " + sign.getSignCode() + "/" + exam.getExamNumber()
                            + " references an inactive or missing question");
                    continue;
                }
                if (linkedQuestion.getSign() == null
                        || !Objects.equals(linkedQuestion.getSign().getId(), sign.getId())) {
                    issues.add("Exam " + sign.getSignCode() + "/" + exam.getExamNumber()
                            + " references a question that belongs to another sign");
                }
            }
        }

        return issues;
    }

    private void requireText(JsonNode node, String field, String dir) throws ImportValidationException {
        String v = node.path(field).asText("").trim();
        if (v.isEmpty()) {
            throw new ImportValidationException("sign.json missing required field: " + field);
        }
    }

    private SignImportRun failRun(String performedBy, long startMs, String reason) {
        log.error("Import failed: {}", reason);
        final long duration = System.currentTimeMillis() - startMs;
        return txTemplate.execute(status -> {
            SignImportRun run = new SignImportRun();
            run.setPerformedBy(performedBy);
            run.setStatus("FAILED");
            run.setErrorSummary(reason);
            run.setDurationMs(duration);
            return importRunRepo.save(run);
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Inner exception class
    // ─────────────────────────────────────────────────────────────────────────

    static class ImportValidationException extends Exception {
        ImportValidationException(String msg) {
            super(msg);
        }
    }
}
