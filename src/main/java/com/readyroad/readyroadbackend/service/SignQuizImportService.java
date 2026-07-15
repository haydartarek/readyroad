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
import com.readyroad.readyroadbackend.util.DrivingTextSanitizer;
import com.readyroad.readyroadbackend.util.ImportedTextSanitizer;
import com.readyroad.readyroadbackend.util.PlaceholderDetector;
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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
 * <li>questions.json validation — hard and binary allowed/not-allowed questions
 * expose 2 choices; other easy/medium questions expose 3 choices</li>
 * <li>exams.json validation — 8 questions in exam_1 (3 EASY + 3 MEDIUM + 2
 * HARD)</li>
 * <li>Upsert road_signs</li>
 * <li>Upsert sign_questions + sign_choices in place (preserve historical FK
 * integrity)</li>
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

    private static final List<String> VALID_LANGS = List.of("NL", "EN", "FR", "AR");
    private static final List<String> FALLBACK_LANGS = List.of("EN", "NL", "FR", "AR");

    private final RoadSignRepository roadSignRepo;
    private final SignQuestionRepository questionRepo;
    private final SignExamRepository examRepo;
    private final SignImportRunRepository importRunRepo;
    private final ObjectMapper mapper;
    private final CanonicalSignCatalogService canonicalSignCatalogService;
    private final RoadSignReferenceTextResolver roadSignReferenceTextResolver;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Used for per-sign transaction isolation.
     * Each sign is committed independently so a DB error in one sign
     * does NOT roll back the rest of the import.
     */
    private final TransactionTemplate txTemplate;

    @Value("${readyroad.signs-import.path:src/main/resources/data/signs_import}")
    private String signsImportPath;

    public SignQuizImportService(RoadSignRepository roadSignRepo,
            SignQuestionRepository questionRepo,
            SignExamRepository examRepo,
            SignImportRunRepository importRunRepo,
            ObjectMapper mapper,
            PlatformTransactionManager txManager,
            CanonicalSignCatalogService canonicalSignCatalogService,
            RoadSignReferenceTextResolver roadSignReferenceTextResolver) {
        this.roadSignRepo = roadSignRepo;
        this.questionRepo = questionRepo;
        this.examRepo = examRepo;
        this.importRunRepo = importRunRepo;
        this.mapper = mapper;
        this.txTemplate = new TransactionTemplate(txManager);
        this.canonicalSignCatalogService = canonicalSignCatalogService;
        this.roadSignReferenceTextResolver = roadSignReferenceTextResolver;
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
        File importDir = resolveImportDirectory();
        if (!importDir.exists() || !importDir.isDirectory()) {
            return failRun(performedBy, startMs,
                    "Import directory not found: " + importDir.getAbsolutePath());
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
                    RoadSignUpsertResult signResult = upsertRoadSign(signNode);
                    RoadSign sign = signResult.sign();
                    int[] qCounts = upsertQuestions(questionsNode, sign);
                    int ec = upsertExams(examsNode, sign);
                    // [0]=created, [1]=updated, [2]=qCreated, [3]=qUpdated, [4]=exams
                    return new int[] { signResult.created() ? 1 : 0, signResult.updated() ? 1 : 0,
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

    private File resolveImportDirectory() {
        List<File> candidates = new ArrayList<>();

        if (signsImportPath != null && !signsImportPath.isBlank()) {
            candidates.add(new File(signsImportPath));
        }

        candidates.add(new File("src/main/resources/data/signs_import"));
        candidates.add(new File("readyroad/src/main/resources/data/signs_import"));

        for (File candidate : candidates) {
            File absoluteCandidate = candidate.getAbsoluteFile();
            if (absoluteCandidate.exists() && absoluteCandidate.isDirectory()) {
                return absoluteCandidate;
            }
        }

        return candidates.get(0).getAbsoluteFile();
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

            JsonNode langNode = i18n.path(lang);
            validateImportedText(
                    text(i18n, lang, "name"),
                    "sign.json i18n[" + lang + "].name");
            validateImportedText(
                    text(i18n, lang, "description"),
                    "sign.json i18n[" + lang + "].description");
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
            SignDifficulty difficulty;
            try {
                difficulty = SignDifficulty.valueOf(diff);
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

                validateImportedText(
                        SignQuestionTextSanitizer.sanitizeQuestion(qType, lang, text(i18n, lang, "question")),
                        "[" + qid + "][" + lang + "] question");
                validateImportedText(
                        SignQuestionTextSanitizer.sanitizeExplanation(qType, lang, text(i18n, lang, "explanation")),
                        "[" + qid + "][" + lang + "] explanation");

                JsonNode choices = langNode.path("choices");
                if (!choices.isArray()) {
                    throw new ImportValidationException(
                            "[" + qid + "][" + lang + "] choices must be an array");
                }

                int choiceCount = choices.size();
                int required = expectedChoiceCount(difficulty, qType);
                if (choiceCount != required) {
                    throw new ImportValidationException(
                            "[" + qid + "][" + lang + "] difficulty=" + diff
                                    + " requires exactly " + required + " choices but found " + choiceCount);
                }

                // exactly 1 correct choice per language
                long correctCount = 0;
                int choiceIndex = 0;
                for (JsonNode c : choices) {
                    validateImportedText(
                            choiceText(i18n, lang, choiceIndex, qType),
                            "[" + qid + "][" + lang + "] choice[" + choiceIndex + "]");
                    if (c.path("is_correct").asBoolean(false))
                        correctCount++;
                    choiceIndex++;
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

    private RoadSignUpsertResult upsertRoadSign(JsonNode node) {
        String signCode = node.path("code").asText().trim();
        String routeSource = firstNonBlank(
                node.path("route_code").asText(""),
                node.path("id").asText(""),
                signCode);
        CanonicalSignCatalogService.CanonicalSignSeed seed = canonicalSignCatalogService
                .findSeedByRouteCode(routeSource)
                .orElseThrow(() -> new IllegalStateException(
                        "No canonical sign seed found for signs_import/" + routeSource + "/sign.json"));

        RoadSign sign = roadSignRepo.findByNormalizedSignCode(seed.routeKey())
                .or(() -> roadSignRepo.findFirstBySignCodeOrderByIdAsc(seed.routeCode()))
                .orElse(new RoadSign());
        boolean isNew = sign.getId() == null;
        boolean changed = isNew || !roadSignMatchesSeed(sign, seed);

        if (changed) {
            sign.setIsActive(true);
            canonicalSignCatalogService.applyCanonicalFields(sign, seed);
            sign = roadSignRepo.save(sign);
        }

        return new RoadSignUpsertResult(sign, isNew, !isNew && changed);
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
            JsonNode i18n = q.path("i18n");
            ImportedQuestionData importedQuestion = buildImportedQuestionData(q, questionType, i18n);
            List<ImportedChoiceData> importedChoices = buildImportedChoices(i18n, questionType);
            boolean changed = isNew
                    || !questionMatchesImport(question, sign, importedQuestion)
                    || !choicesMatchImport(question, importedChoices);

            if (!changed) {
                continue;
            }

            applyQuestionData(question, sign, importedQuestion);

            SignQuestion saved = questionRepo.save(question);
            syncChoices(saved, importedChoices, isNew);
            questionRepo.save(saved);

            if (isNew)
                created++;
            else
                updated++;
        }

        for (SignQuestion existing : questionRepo.findAllBySignId(sign.getId())) {
            if (!importedRefs.contains(existing.getQuestionRef()) && Boolean.TRUE.equals(existing.getIsActive())) {
                existing.setIsActive(false);
                questionRepo.save(existing);
                updated++;
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
            JsonNode dist = examsNode.path("distribution");
            ImportedExamData importedExam = new ImportedExamData(
                    examNumber,
                    examsNode.path("passing_score").asInt(6),
                    examsNode.path("total_questions").asInt(REQUIRED_EXAM_QUESTIONS),
                    dist.path("EASY").asInt(REQUIRED_EASY),
                    dist.path("MEDIUM").asInt(REQUIRED_MEDIUM),
                    dist.path("HARD").asInt(REQUIRED_HARD),
                    toRealRefs(examsNode.path(fieldName).path("questions")));

            if (!isNew && examMatchesImport(exam, sign, importedExam)) {
                continue;
            }

            applyExamData(exam, sign, importedExam);

            SignExam savedExam = examRepo.save(exam);
            syncExamQuestions(savedExam, importedExam.questionRefs());
            examRepo.save(savedExam);

            if (isNew) {
                created++;
            }
        }

        for (SignExam existing : examRepo.findAllBySignIdOrderByExamNumberAsc(sign.getId())) {
            if (!importedExamNumbers.contains(existing.getExamNumber()) && Boolean.TRUE.equals(existing.getIsActive())) {
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
                .map(sign -> RoadSignDetailDto.from(sign, roadSignReferenceTextResolver));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private String text(JsonNode i18n, String lang, String field) {
        String preferred = cleanImportedText(i18n.path(lang).path(field));
        if (preferred != null) {
            return preferred;
        }

        for (String fallbackLang : FALLBACK_LANGS) {
            if (fallbackLang.equals(lang)) {
                continue;
            }
            String fallback = cleanImportedText(i18n.path(fallbackLang).path(field));
            if (fallback != null) {
                return fallback;
            }
        }

        return null;
    }

    private String choiceText(JsonNode i18n, String lang, int idx, SignQuestionType questionType) {
        String preferred = cleanChoiceText(i18n, lang, idx, questionType);
        if (preferred != null) {
            return preferred;
        }

        for (String fallbackLang : FALLBACK_LANGS) {
            if (fallbackLang.equals(lang)) {
                continue;
            }
            String fallback = cleanChoiceText(i18n, fallbackLang, idx, questionType);
            if (fallback != null) {
                return fallback;
            }
        }

        return null;
    }

    private String cleanChoiceText(JsonNode i18n, String lang, int idx, SignQuestionType questionType) {
        JsonNode choices = i18n.path(lang).path("choices");
        if (!choices.isArray() || idx >= choices.size()) {
            return null;
        }

        String cleanText = cleanImportedText(choices.get(idx).path("text"));
        if (cleanText == null) {
            return null;
        }

        return cleanImportedText(SignQuestionTextSanitizer.sanitizeChoice(questionType, lang, cleanText));
    }

    private String cleanImportedText(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        return cleanImportedText(node.asText(null));
    }

    private String cleanImportedText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String sanitized = ImportedTextSanitizer.sanitize(value);
        if (sanitized == null || sanitized.isBlank()) {
            return null;
        }
        if (hasInvalidImportedText(sanitized)) {
            return null;
        }

        return sanitized.trim();
    }

    private void syncChoices(SignQuestion question, List<ImportedChoiceData> importedChoices,
            boolean isNewQuestion) {
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
        moveExistingChoicesToTemporaryDisplayOrders(existingChoices);

        Map<String, Deque<SignChoice>> choicesByFingerprint = new HashMap<>();
        for (SignChoice existingChoice : existingChoices) {
            choicesByFingerprint
                    .computeIfAbsent(choiceFingerprint(existingChoice), key -> new ArrayDeque<>())
                    .add(existingChoice);
        }

        List<SignChoice> remainingExisting = new ArrayList<>(existingChoices);
        int order = 1;
        for (ImportedChoiceData importedChoice : importedChoices) {
            SignChoice chosen = null;
            Deque<SignChoice> exactMatches = choicesByFingerprint.get(importedChoice.fingerprint());
            if (exactMatches != null && !exactMatches.isEmpty()) {
                chosen = exactMatches.removeFirst();
            }
            if (chosen == null) {
                chosen = findBestChoiceMatch(remainingExisting, importedChoice);
            }
            if (chosen == null) {
                chosen = new SignChoice();
                chosen.setQuestion(question);
                question.addChoice(chosen);
            } else {
                remainingExisting.remove(chosen);
            }
            chosen.setDisplayOrder(order++);
            applyChoiceData(chosen, importedChoice);
        }

        int hiddenOrder = importedChoices.size() + 100;
        for (SignChoice legacyChoice : remainingExisting) {
            legacyChoice.setDisplayOrder(hiddenOrder++);
            legacyChoice.setIsCorrect(false);
        }
    }

    private void moveExistingChoicesToTemporaryDisplayOrders(List<SignChoice> existingChoices) {
        int temporaryOrder = 1_000;
        for (SignChoice existingChoice : existingChoices) {
            existingChoice.setDisplayOrder(temporaryOrder++);
        }
        entityManager.flush();
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

    private int expectedChoiceCount(SignDifficulty difficulty, SignQuestionType questionType) {
        if (difficulty == SignDifficulty.HARD || questionType == SignQuestionType.IS_IT_ALLOWED) {
            return REQUIRED_CHOICES_BINARY;
        }
        return REQUIRED_CHOICES_MULTI;
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

    private void syncExamQuestions(SignExam exam, List<String> questionRefs) {
        if (!exam.getExamQuestions().isEmpty()) {
            exam.clearExamQuestions();
            examRepo.saveAndFlush(exam);
        }

        int order = 1;
        for (String ref : questionRefs) {
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
            Optional<CanonicalSignCatalogService.CanonicalSignSeed> seedOpt = canonicalSignCatalogService
                    .findSeedFor(sign);
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
                long correctChoices = question.getDeliverableChoices().stream()
                        .filter(choice -> Boolean.TRUE.equals(choice.getIsCorrect()))
                        .count();
                int expectedChoiceCount = expectedChoiceCount(question.getDifficulty(), question.getQuestionType());
                int actualChoiceCount = question.getDeliverableChoices().size();

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

    private void validateImportedText(String value, String context) throws ImportValidationException {
        if (value == null || value.isBlank()) {
            throw new ImportValidationException(context + " must not be blank");
        }

        if (hasInvalidImportedText(value)) {
            throw new ImportValidationException(context + " contains placeholder or corrupted text");
        }
    }

    private boolean roadSignMatchesSeed(
            RoadSign sign,
            CanonicalSignCatalogService.CanonicalSignSeed seed) {
        return Boolean.TRUE.equals(sign.getIsActive())
                && Objects.equals(sign.getSignCode(), seed.routeCode())
                && Objects.equals(sign.getNormalizedSignCode(), seed.routeKey())
                && Objects.equals(sign.getCategory(), seed.category())
                && (seed.imagePath().isBlank() || Objects.equals(sign.getImagePath(), seed.imagePath()))
                && Objects.equals(sign.getNameEn(), seed.nameEn())
                && Objects.equals(sign.getNameAr(), seed.nameAr())
                && Objects.equals(sign.getNameNl(), seed.nameNl())
                && Objects.equals(sign.getNameFr(), seed.nameFr())
                && Objects.equals(sign.getDescriptionEn(), seed.descriptionEn())
                && Objects.equals(sign.getDescriptionAr(), seed.descriptionAr())
                && Objects.equals(sign.getDescriptionNl(), seed.descriptionNl())
                && Objects.equals(sign.getDescriptionFr(), seed.descriptionFr())
                && Objects.equals(sign.getSummaryEn(), seed.summaryEn())
                && Objects.equals(sign.getSummaryAr(), seed.summaryAr())
                && Objects.equals(sign.getSummaryNl(), seed.summaryNl())
                && Objects.equals(sign.getSummaryFr(), seed.summaryFr())
                && Objects.equals(sign.getDriverGuidanceEn(), seed.driverGuidanceEn())
                && Objects.equals(sign.getDriverGuidanceAr(), seed.driverGuidanceAr())
                && Objects.equals(sign.getDriverGuidanceNl(), seed.driverGuidanceNl())
                && Objects.equals(sign.getDriverGuidanceFr(), seed.driverGuidanceFr())
                && Objects.equals(sign.getExceptionsEn(), seed.exceptionsEn())
                && Objects.equals(sign.getExceptionsAr(), seed.exceptionsAr())
                && Objects.equals(sign.getExceptionsNl(), seed.exceptionsNl())
                && Objects.equals(sign.getExceptionsFr(), seed.exceptionsFr())
                && Objects.equals(sign.getSeriousViolation(), seed.seriousViolation());
    }

    private ImportedQuestionData buildImportedQuestionData(
            JsonNode question,
            SignQuestionType questionType,
            JsonNode i18n) {
        return new ImportedQuestionData(
                question.path("question_id").asText().trim(),
                questionType,
                SignDifficulty.valueOf(question.path("difficulty").asText().trim()),
                question.path("is_critical").asBoolean(false),
                question.path("show_sign").asBoolean(true),
                TextNormalizer.normalize(
                        SignQuestionTextSanitizer.sanitizeQuestion(questionType, "NL", text(i18n, "NL", "question"))),
                TextNormalizer.normalize(
                        SignQuestionTextSanitizer.sanitizeQuestion(questionType, "EN", text(i18n, "EN", "question"))),
                TextNormalizer.normalize(
                        SignQuestionTextSanitizer.sanitizeQuestion(questionType, "FR", text(i18n, "FR", "question"))),
                TextNormalizer.normalize(
                        SignQuestionTextSanitizer.sanitizeQuestion(questionType, "AR", text(i18n, "AR", "question"))),
                TextNormalizer.normalize(SignQuestionTextSanitizer.sanitizeExplanation(
                        questionType, "NL", text(i18n, "NL", "explanation"))),
                TextNormalizer.normalize(SignQuestionTextSanitizer.sanitizeExplanation(
                        questionType, "EN", text(i18n, "EN", "explanation"))),
                TextNormalizer.normalize(SignQuestionTextSanitizer.sanitizeExplanation(
                        questionType, "FR", text(i18n, "FR", "explanation"))),
                TextNormalizer.normalize(SignQuestionTextSanitizer.sanitizeExplanation(
                        questionType, "AR", text(i18n, "AR", "explanation"))));
    }

    private boolean questionMatchesImport(
            SignQuestion question,
            RoadSign sign,
            ImportedQuestionData imported) {
        return question.getSign() != null
                && Objects.equals(question.getSign().getId(), sign.getId())
                && Objects.equals(question.getQuestionRef(), imported.questionRef())
                && Objects.equals(question.getQuestionType(), imported.questionType())
                && Objects.equals(question.getDifficulty(), imported.difficulty())
                && Objects.equals(Boolean.TRUE.equals(question.getIsCritical()), imported.isCritical())
                && Objects.equals(Boolean.TRUE.equals(question.getShowSign()), imported.showSign())
                && Boolean.TRUE.equals(question.getIsActive())
                && Objects.equals(question.getQuestionNl(), imported.questionNl())
                && Objects.equals(question.getQuestionEn(), imported.questionEn())
                && Objects.equals(question.getQuestionFr(), imported.questionFr())
                && Objects.equals(question.getQuestionAr(), imported.questionAr())
                && Objects.equals(question.getExplanationNl(), imported.explanationNl())
                && Objects.equals(question.getExplanationEn(), imported.explanationEn())
                && Objects.equals(question.getExplanationFr(), imported.explanationFr())
                && Objects.equals(question.getExplanationAr(), imported.explanationAr());
    }

    private boolean choicesMatchImport(
            SignQuestion question,
            List<ImportedChoiceData> importedChoices) {
        List<String> existingFingerprints = question.getDeliverableChoices().stream()
                .map(this::choiceFingerprint)
                .sorted()
                .toList();
        List<String> importedFingerprints = importedChoices.stream()
                .map(ImportedChoiceData::fingerprint)
                .sorted()
                .toList();
        return existingFingerprints.equals(importedFingerprints);
    }

    private void applyQuestionData(
            SignQuestion question,
            RoadSign sign,
            ImportedQuestionData imported) {
        question.setSign(sign);
        question.setQuestionRef(imported.questionRef());
        question.setQuestionType(imported.questionType());
        question.setDifficulty(imported.difficulty());
        question.setIsCritical(imported.isCritical());
        question.setShowSign(imported.showSign());
        question.setIsActive(true);
        question.setQuestionNl(imported.questionNl());
        question.setQuestionEn(imported.questionEn());
        question.setQuestionFr(imported.questionFr());
        question.setQuestionAr(imported.questionAr());
        question.setExplanationNl(imported.explanationNl());
        question.setExplanationEn(imported.explanationEn());
        question.setExplanationFr(imported.explanationFr());
        question.setExplanationAr(imported.explanationAr());
    }

    private boolean examMatchesImport(SignExam exam, RoadSign sign, ImportedExamData imported) {
        if (exam.getSign() == null
                || !Objects.equals(exam.getSign().getId(), sign.getId())
                || !Objects.equals(exam.getExamNumber(), imported.examNumber())
                || !Objects.equals(exam.getPassingScore(), imported.passingScore())
                || !Objects.equals(exam.getTotalQuestions(), imported.totalQuestions())
                || !Objects.equals(exam.getEasyCount(), imported.easyCount())
                || !Objects.equals(exam.getMediumCount(), imported.mediumCount())
                || !Objects.equals(exam.getHardCount(), imported.hardCount())
                || !Boolean.TRUE.equals(exam.getIsActive())) {
            return false;
        }

        List<String> existingRefs = exam.getExamQuestions().stream()
                .sorted(Comparator.comparing(SignExamQuestion::getQuestionOrder))
                .map(SignExamQuestion::getQuestion)
                .map(SignQuestion::getQuestionRef)
                .toList();
        return existingRefs.equals(imported.questionRefs());
    }

    private void applyExamData(SignExam exam, RoadSign sign, ImportedExamData imported) {
        exam.setSign(sign);
        exam.setExamNumber(imported.examNumber());
        exam.setPassingScore(imported.passingScore());
        exam.setTotalQuestions(imported.totalQuestions());
        exam.setEasyCount(imported.easyCount());
        exam.setMediumCount(imported.mediumCount());
        exam.setHardCount(imported.hardCount());
        exam.setIsActive(true);
    }

    private record RoadSignUpsertResult(RoadSign sign, boolean created, boolean updated) {
    }

    private record ImportedQuestionData(
            String questionRef,
            SignQuestionType questionType,
            SignDifficulty difficulty,
            boolean isCritical,
            boolean showSign,
            String questionNl,
            String questionEn,
            String questionFr,
            String questionAr,
            String explanationNl,
            String explanationEn,
            String explanationFr,
            String explanationAr) {
    }

    private record ImportedExamData(
            Integer examNumber,
            Integer passingScore,
            Integer totalQuestions,
            Integer easyCount,
            Integer mediumCount,
            Integer hardCount,
            List<String> questionRefs) {
        private ImportedExamData {
            questionRefs = List.copyOf(questionRefs);
        }
    }

    private boolean hasInvalidImportedText(String value) {
        return PlaceholderDetector.hasPlaceholderNonBlank(value)
                || value.indexOf('\uFFFD') >= 0
                || value.contains("�")
                || ImportedTextSanitizer.requiresRepair(value);
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
