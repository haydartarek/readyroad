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
 *   <li>Directory scan — collect sign folders</li>
 *   <li>File existence — sign.json, questions.json, exams.json</li>
 *   <li>sign.json validation — code, category, i18n (NL/EN/FR/AR)</li>
 *   <li>questions.json validation — types, choices count:
 *       IS_IT_ALLOWED→2, others→3, anything else→reject</li>
 *   <li>exams.json validation — 15 questions, 6+6+3, no overlap</li>
 *   <li>Upsert road_signs</li>
 *   <li>Upsert sign_questions + sign_choices (DELETE old, INSERT new)</li>
 *   <li>Upsert sign_exams + sign_exam_questions</li>
 *   <li>Save sign_import_runs and return result</li>
 * </ol>
 */
@Service
public class SignQuizImportService {

    private static final Logger log = LoggerFactory.getLogger(SignQuizImportService.class);

    private static final int REQUIRED_CHOICES_BINARY = 2;
    private static final int REQUIRED_CHOICES_MULTI  = 3;
    private static final int REQUIRED_EXAM_QUESTIONS = 15;
    private static final int REQUIRED_EASY   = 6;
    private static final int REQUIRED_MEDIUM = 6;
    private static final int REQUIRED_HARD   = 3;

    private static final Set<String> VALID_LANGS = Set.of("NL", "EN", "FR", "AR");

    private final RoadSignRepository      roadSignRepo;
    private final SignQuestionRepository  questionRepo;
    private final SignExamRepository      examRepo;
    private final SignImportRunRepository importRunRepo;
    private final ObjectMapper            mapper;
    /**
     * Used for per-sign transaction isolation.
     * Each sign is committed independently so a DB error in one sign
     * does NOT roll back the rest of the import.
     */
    private final TransactionTemplate     txTemplate;

    @Value("${readyroad.signs-import.path:C:/Users/haydar/Desktop/end_project/readyroad/src/main/resources/data/signs_import}")
    private String signsImportPath;

    public SignQuizImportService(RoadSignRepository roadSignRepo,
                                 SignQuestionRepository questionRepo,
                                 SignExamRepository examRepo,
                                 SignImportRunRepository importRunRepo,
                                 ObjectMapper mapper,
                                 PlatformTransactionManager txManager) {
        this.roadSignRepo  = roadSignRepo;
        this.questionRepo  = questionRepo;
        this.examRepo      = examRepo;
        this.importRunRepo = importRunRepo;
        this.mapper        = mapper;
        this.txTemplate    = new TransactionTemplate(txManager);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Public entry point
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Runs the full import. Each sign directory is committed in its own
     * independent transaction (via {@link TransactionTemplate}) so that a DB
     * error in one sign never rolls back the rest.
     *
     * <p>Steps 2-5 (JSON parsing + validation) run outside any transaction.
     * Steps 6-8 (DB upsert) run inside a per-sign {@code txTemplate.execute}.
     * Step 9 (run-record save) runs in its own transaction.</p>
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
        int signsCreated     = 0;
        int signsUpdated     = 0;
        int signsSkipped     = 0;
        int questionsCreated = 0;
        int questionsUpdated = 0;
        int examsCreated     = 0;
        int errorsCount      = 0;
        List<String> errorLines = new ArrayList<>();

        // Process each sign
        for (File dir : signDirs) {
            String dirName = dir.getName();
            try {
                // Steps 2-5 — file IO + validation (no DB, no transaction needed)
                File signFile  = new File(dir, "sign.json");
                File questFile = new File(dir, "questions.json");
                File examsFile = new File(dir, "exams.json");

                if (!signFile.exists() || !questFile.exists() || !examsFile.exists()) {
                    throw new ImportValidationException("Missing required JSON file(s)");
                }

                final JsonNode signNode      = mapper.readTree(signFile);
                final JsonNode questionsNode = mapper.readTree(questFile);
                final JsonNode examsNode     = mapper.readTree(examsFile);
                final List<String> qRefs     = extractQuestionRefs(questionsNode, signNode);

                validateSignJson(signNode, dirName);
                validateQuestionsJson(questionsNode, dirName);
                validateExamsJson(examsNode, qRefs, dirName);

                // Steps 6-8 — DB upsert in its own independent transaction.
                // If this sign fails, only THIS sign is rolled back; others are unaffected.
                int[] counts = txTemplate.execute(status -> {
                    boolean isNew = !roadSignRepo.existsBySignCode(
                            signNode.path("code").asText().trim());
                    RoadSign sign   = upsertRoadSign(signNode);
                    int[] qCounts   = upsertQuestions(questionsNode, sign, isNew);
                    int   ec        = upsertExams(examsNode, sign, qRefs);
                    // [0]=created, [1]=updated, [2]=qCreated, [3]=qUpdated, [4]=exams
                    return new int[]{ isNew ? 1 : 0, isNew ? 0 : 1,
                                      qCounts[0], qCounts[1], ec };
                });

                if (counts == null) throw new IllegalStateException("Transaction returned null");

                signsCreated     += counts[0];
                signsUpdated     += counts[1];
                questionsCreated += counts[2];
                questionsUpdated += counts[3];
                examsCreated     += counts[4];

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

        // Step 9 — save run record in its own transaction
        final int    fProcessed    = signDirs.length;
        final int    fCreated      = signsCreated;
        final int    fUpdated      = signsUpdated;
        final int    fSkipped      = signsSkipped;
        final int    fQCreated     = questionsCreated;
        final int    fQUpdated     = questionsUpdated;
        final int    fECreated     = examsCreated;
        final int    fErrors       = errorsCount;
        final String fErrorSummary = errorLines.isEmpty() ? null : String.join("\n", errorLines);
        final long   fDuration     = System.currentTimeMillis() - startMs;
        final String fStatus       = fErrors == 0 ? "SUCCESS"
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
            String qid  = q.path("question_id").asText("?");
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
                int required    = (qType == SignQuestionType.IS_IT_ALLOWED)
                        ? REQUIRED_CHOICES_BINARY
                        : REQUIRED_CHOICES_MULTI;

                if (choiceCount != required) {
                    throw new ImportValidationException(
                            "[" + qid + "][" + lang + "] " + type
                                    + " requires exactly " + required + " choices but found " + choiceCount);
                }

                // exactly 1 correct choice per language
                long correctCount = 0;
                for (JsonNode c : choices) {
                    if (c.path("is_correct").asBoolean(false)) correctCount++;
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

    private void validateExamsJson(JsonNode node, List<String> allQuestionRefs, String dir)
            throws ImportValidationException {

        // exams.json may contain template brackets like [A1_Q06] — skip those
        // Only validate if exams have real (non-bracketed) question references

        JsonNode exam1 = node.path("exam_1");
        JsonNode exam2 = node.path("exam_2");

        if (exam1.isMissingNode() || exam2.isMissingNode()) {
            throw new ImportValidationException("exams.json must contain 'exam_1' and 'exam_2'");
        }

        JsonNode q1 = exam1.path("questions");
        JsonNode q2 = exam2.path("questions");

        if (!q1.isArray() || !q2.isArray()) {
            throw new ImportValidationException("exams.json exam_1/exam_2 must have 'questions' arrays");
        }

        if (q1.size() != REQUIRED_EXAM_QUESTIONS || q2.size() != REQUIRED_EXAM_QUESTIONS) {
            throw new ImportValidationException(
                    "Each exam must have exactly " + REQUIRED_EXAM_QUESTIONS
                            + " questions. Found exam_1=" + q1.size() + ", exam_2=" + q2.size());
        }

        // Collect real refs (non-bracketed = not starting with '[')
        List<String> refs1 = toRealRefs(q1);
        List<String> refs2 = toRealRefs(q2);

        // No overlap check (only for real refs that are in both exams)
        Set<String> overlap = new HashSet<>(refs1);
        overlap.retainAll(new HashSet<>(refs2));
        if (!overlap.isEmpty()) {
            throw new ImportValidationException(
                    "exam_1 and exam_2 share question(s): " + overlap);
        }

        // All real refs must exist in questions.json
        Set<String> knownRefs = new HashSet<>(allQuestionRefs);
        for (String r : refs1) {
            if (!knownRefs.contains(r)) {
                throw new ImportValidationException(
                        "exam_1 references unknown question: " + r);
            }
        }
        for (String r : refs2) {
            if (!knownRefs.contains(r)) {
                throw new ImportValidationException(
                        "exam_2 references unknown question: " + r);
            }
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
            if (!ref.isEmpty()) refs.add(ref);
        }
        return refs;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Step 6 — Upsert RoadSign
    // ─────────────────────────────────────────────────────────────────────────

    private RoadSign upsertRoadSign(JsonNode node) {
        String signCode = node.path("code").asText().trim();
        RoadSign sign   = roadSignRepo.findBySignCode(signCode)
                                      .orElse(new RoadSign());

        sign.setSignCode(signCode);
        sign.setCategory(SignCategory.valueOf(node.path("category").asText().trim()));
        sign.setImagePath(node.path("image_path").asText(null));
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

        return roadSignRepo.save(sign);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Step 7 — Upsert Questions + Choices
    // ─────────────────────────────────────────────────────────────────────────

    private int[] upsertQuestions(JsonNode questionsNode, RoadSign sign, boolean signIsNew) {
        int created = 0;
        int updated = 0;

        for (JsonNode q : questionsNode) {
            String ref    = q.path("question_id").asText().trim();
            boolean isNew = !questionRepo.existsByQuestionRef(ref);

            // Fix: for existing questions, delete the old entity entirely so that
            // Hibernate cascades the DELETE to sign_choices at DB level.
            // This avoids the clearChoices() + save pattern that corrupts the
            // Hibernate session (null-ID orphan flush -> UnexpectedRollbackException).
            if (!isNew) {
                questionRepo.findByQuestionRef(ref)
                        .ifPresent(existing -> {
                            questionRepo.deleteById(existing.getId());
                            questionRepo.flush(); // ensure DELETE is sent before INSERT
                        });
            }

            // Always build a fresh (transient) SignQuestion -- no managed state left
            SignQuestion question = new SignQuestion();
            question.setSign(sign);
            question.setQuestionRef(ref);
            question.setQuestionType(SignQuestionType.valueOf(q.path("type").asText().trim()));
            question.setDifficulty(SignDifficulty.valueOf(q.path("difficulty").asText().trim()));
            question.setIsCritical(q.path("is_critical").asBoolean(false));
            question.setShowSign(q.path("show_sign").asBoolean(true));
            question.setIsActive(true);

            JsonNode i18n = q.path("i18n");
            question.setQuestionNl(text(i18n, "NL", "question"));
            question.setQuestionEn(text(i18n, "EN", "question"));
            question.setQuestionFr(text(i18n, "FR", "question"));
            question.setQuestionAr(text(i18n, "AR", "question"));
            question.setExplanationNl(text(i18n, "NL", "explanation"));
            question.setExplanationEn(text(i18n, "EN", "explanation"));
            question.setExplanationFr(text(i18n, "FR", "explanation"));
            question.setExplanationAr(text(i18n, "AR", "explanation"));

            // Save the fresh question to get its generated PK
            SignQuestion saved = questionRepo.save(question);

            // Build choices from the NL language block (choices are same count in all langs)
            // Shuffle indices so the correct answer is not always displayed first
            JsonNode nlChoices = i18n.path("NL").path("choices");
            List<Integer> choiceIndices = new ArrayList<>();
            for (int i = 0; i < nlChoices.size(); i++) choiceIndices.add(i);
            Collections.shuffle(choiceIndices);

            for (int pos = 0; pos < choiceIndices.size(); pos++) {
                int idx = choiceIndices.get(pos);
                SignChoice choice = new SignChoice();
                choice.setDisplayOrder(pos + 1);
                choice.setIsCorrect(nlChoices.get(idx).path("is_correct").asBoolean(false));
                choice.setTextNl(choiceText(i18n, "NL", idx));
                choice.setTextEn(choiceText(i18n, "EN", idx));
                choice.setTextFr(choiceText(i18n, "FR", idx));
                choice.setTextAr(choiceText(i18n, "AR", idx));
                saved.addChoice(choice);
            }

            questionRepo.save(saved);

            if (isNew) created++; else updated++;
        }
        return new int[]{created, updated};
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Step 8 — Upsert Exams
    // ─────────────────────────────────────────────────────────────────────────

    private int upsertExams(JsonNode examsNode, RoadSign sign, List<String> questionRefs) {
        // Delete old exams for this sign and recreate
        examRepo.deleteAllBySignId(sign.getId());

        int created = 0;

        JsonNode[] examNodes = { examsNode.path("exam_1"), examsNode.path("exam_2") };
        int[] examNums       = { 1, 2 };

        for (int i = 0; i < 2; i++) {
            JsonNode examNode = examNodes[i];
            if (examNode.isMissingNode()) continue;

            SignExam exam = new SignExam();
            exam.setSign(sign);
            exam.setExamNumber(examNums[i]);
            exam.setPassingScore(examsNode.path("passing_score").asInt(12));
            exam.setTotalQuestions(examsNode.path("total_questions").asInt(15));
            exam.setIsActive(true);

            // distribution from exams.json
            JsonNode dist = examsNode.path("distribution");
            exam.setEasyCount(dist.path("EASY").asInt(REQUIRED_EASY));
            exam.setMediumCount(dist.path("MEDIUM").asInt(REQUIRED_MEDIUM));
            exam.setHardCount(dist.path("HARD").asInt(REQUIRED_HARD));

            SignExam savedExam = examRepo.save(exam);

            // Attach real question refs only (skip [bracketed] template refs)
            JsonNode qArray = examNode.path("questions");
            int order = 1;
            for (JsonNode qRef : qArray) {
                String ref = qRef.asText("").trim();
                if (ref.startsWith("[")) continue; // template placeholder

                Optional<SignQuestion> qOpt = questionRepo.findByQuestionRef(ref);
                if (qOpt.isPresent()) {
                    SignExamQuestion eq = new SignExamQuestion();
                    eq.setQuestion(qOpt.get());
                    eq.setQuestionOrder(order++);
                    savedExam.addExamQuestion(eq);
                }
            }

            examRepo.save(savedExam);
            created++;
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
        return roadSignRepo.findBySignCode(code)
                           .map(RoadSignDetailDto::from);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private String text(JsonNode i18n, String lang, String field) {
        JsonNode n = i18n.path(lang).path(field);
        if (n.isMissingNode() || n.isNull()) return null;
        String v = n.asText("").trim();
        return v.isEmpty() ? null : v;
    }

    private String choiceText(JsonNode i18n, String lang, int idx) {
        JsonNode choices = i18n.path(lang).path("choices");
        if (!choices.isArray() || idx >= choices.size()) return null;
        return choices.get(idx).path("text").asText(null);
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
        ImportValidationException(String msg) { super(msg); }
    }
}
