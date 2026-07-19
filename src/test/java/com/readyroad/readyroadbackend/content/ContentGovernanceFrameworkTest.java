package com.readyroad.readyroadbackend.content;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class ContentGovernanceFrameworkTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Path ROOT = Path.of("").toAbsolutePath().normalize();
    private static final Path IMPORT_ROOT = ROOT.resolve("src/main/resources/data/signs_import");
    private static final Path GOVERNANCE_ROOT = ROOT.resolve("src/main/resources/data/content_governance");
    private static final Path REVIEW_INDEX = GOVERNANCE_ROOT.resolve("review-index.json");
    private static final List<String> LANGUAGES = List.of("NL", "EN", "FR", "AR");
    private static final List<String> REPORT_HEADINGS = List.of(
            "## Status",
            "## Scope",
            "## Quality Metrics",
            "## Critical Findings and Corrections",
            "## Major Findings and Corrections",
            "## Minor Findings and Corrections",
            "## Deferred and Human Review",
            "## Governance Validation",
            "## Evidence");

    @Test
    void centralReviewIndexMatchesTheCanonicalCatalog() throws IOException {
        JsonNode index = read(REVIEW_INDEX);
        assertEquals(1, index.path("schema_version").asInt(), "Review index schema version");
        assertEquals(1, index.path("framework_version").asInt(), "Governance framework version");
        assertDate(index.path("last_updated").asText(), "Review index last_updated");

        Map<String, CatalogCounts> catalog = scanCatalog();
        Map<String, JsonNode> indexedCategories = new TreeMap<>();
        Set<String> slugs = new HashSet<>();

        for (JsonNode entry : index.path("categories")) {
            String category = requiredText(entry, "category", "Indexed category");
            String slug = requiredText(entry, "slug", category + " slug");
            assertTrue(slug.matches("[a-z0-9]+(?:-[a-z0-9]+)*"), category + " slug format");
            assertTrue(slugs.add(slug), "Duplicate category slug: " + slug);
            assertTrue(indexedCategories.put(category, entry) == null, "Duplicate category: " + category);

            CatalogCounts actual = catalog.get(category);
            assertNotNull(actual, "Indexed category is absent from the canonical catalog: " + category);
            JsonNode counts = entry.path("counts");
            assertEquals(actual.signs(), counts.path("signs").asInt(), category + " sign count");
            assertEquals(actual.questions(), counts.path("questions").asInt(), category + " question count");
            assertEquals(actual.exams(), counts.path("exams").asInt(), category + " exam count");
            assertTrue(counts.path("lessons").asInt() >= 0, category + " lesson count");

            String status = requiredText(entry, "status", category + " status");
            String approval = requiredText(entry, "approval_status", category + " approval status");
            String risk = requiredText(entry, "risk_level", category + " risk level");
            assertTrue(Set.of("LOW", "MEDIUM", "HIGH", "CRITICAL", "UNASSESSED").contains(risk),
                    category + " unsupported risk level");

            if ("NOT_REVIEWED".equals(status)) {
                assertEquals("PENDING", approval, category + " unreviewed approval status");
                assertTrue(entry.path("last_reviewed").isNull(), category + " unreviewed date must be null");
                assertTrue(entry.path("review_file").isNull(), category + " unreviewed review file must be null");
                assertTrue(entry.path("report_file").isNull(), category + " unreviewed report file must be null");
            } else {
                assertDate(entry.path("last_reviewed").asText(), category + " last reviewed date");
                assertTrue(Files.isRegularFile(resolveGovernancePath(entry.path("review_file").asText())),
                        category + " review file");
                assertTrue(Files.isRegularFile(resolveProjectPath(entry.path("report_file").asText())),
                        category + " report file");
            }
        }

        assertEquals(catalog.keySet(), indexedCategories.keySet(),
                "The central index must list every canonical sign category exactly once");
    }

    @Test
    void sharedGovernanceRegistriesAreValid() throws IOException {
        JsonNode index = read(REVIEW_INDEX);
        JsonNode shared = index.path("shared_resources");
        Path sourcePath = resolveProjectPath(shared.path("legal_sources").asText());
        Path glossaryPath = resolveProjectPath(shared.path("terminology_glossary").asText());
        Path schemaPath = resolveProjectPath(shared.path("category_review_schema").asText());
        Path changeLogPath = resolveProjectPath(shared.path("content_change_log").asText());
        Path templatePath = resolveProjectPath(shared.path("report_template").asText());

        assertAll("Shared governance resources",
                () -> assertTrue(Files.isRegularFile(sourcePath), "Legal source matrix"),
                () -> assertTrue(Files.isRegularFile(glossaryPath), "Terminology glossary"),
                () -> assertTrue(Files.isRegularFile(schemaPath), "Category review schema"),
                () -> assertTrue(Files.isRegularFile(changeLogPath), "Content change log"),
                () -> assertTrue(Files.isRegularFile(templatePath), "Category report template"));

        Map<String, JsonNode> sources = loadSources(sourcePath);
        JsonNode glossary = read(glossaryPath);
        assertEquals("SHARED", glossary.path("scope").asText(), "Terminology glossary scope");
        Set<String> termIds = new HashSet<>();
        for (JsonNode term : glossary.path("terms")) {
            String termId = requiredText(term, "term_id", "Terminology entry");
            assertTrue(termIds.add(termId), "Duplicate terminology ID: " + termId);
            for (String language : LANGUAGES) {
                assertText(term.path("i18n"), language, termId + " " + language);
            }
            assertFalse(term.path("source_ids").isEmpty(), termId + " must have a source");
            for (JsonNode sourceId : term.path("source_ids")) {
                assertTrue(sources.containsKey(sourceId.asText()),
                        termId + " has unknown source " + sourceId.asText());
            }
        }

        JsonNode schema = read(schemaPath);
        assertEquals("object", schema.path("type").asText());
        assertEquals(2, schema.path("properties").path("schema_version").path("const").asInt());
        Set<String> required = textSet(schema.path("required"));
        assertTrue(required.containsAll(Set.of("category", "quality_metrics", "validation_rules", "reports", "signs")),
                "Category review schema is missing framework fields");

        assertStandardReportSections(Files.readString(templatePath), "Category report template");
    }

    @Test
    void reviewedCategoriesMeetTheReusableGovernanceContract() throws IOException {
        JsonNode index = read(REVIEW_INDEX);
        Map<String, JsonNode> sources = loadSources(GOVERNANCE_ROOT.resolve("legal-sources.json"));
        Set<String> terminologyIds = glossaryTermIds();

        for (JsonNode entry : reviewedCategoryEntries(index)) {
            JsonNode review = read(resolveGovernancePath(entry.path("review_file").asText()));
            validateReviewHeader(entry, review);
            validateCategoryContent(entry, review, sources);
            validateLessonItems(review, sources);
            validateQualityMetrics(entry, review, terminologyIds);
            evaluateContentAssertions(review);
        }
    }

    @Test
    void categoryReportsUseTheStandardStructureAndMatchTheirData() throws IOException {
        JsonNode index = read(REVIEW_INDEX);
        for (JsonNode entry : reviewedCategoryEntries(index)) {
            JsonNode review = read(resolveGovernancePath(entry.path("review_file").asText()));
            JsonNode reports = review.path("reports");
            Path reportPath = resolveProjectPath(reports.path("content_review").asText());
            assertEquals(resolveProjectPath(entry.path("report_file").asText()), reportPath,
                    entry.path("category").asText() + " central report path");

            String markdown = Files.readString(reportPath);
            assertStandardReportSections(markdown, entry.path("category").asText() + " report");
            assertQualityMetricRows(markdown, review.path("quality_metrics"));

            JsonNode consistency = read(resolveProjectPath(reports.path("cross_file_consistency").asText()));
            JsonNode ambiguity = read(resolveProjectPath(reports.path("ambiguous_questions").asText()));
            JsonNode approval = read(resolveProjectPath(reports.path("human_approval").asText()));
            JsonNode dates = read(resolveProjectPath(reports.path("last_reviewed").asText()));

            String category = review.path("category").asText();
            assertEquals(category, consistency.path("category").asText());
            assertEquals("PASSED", consistency.path("status").asText(), category + " consistency report");
            assertEquals(0, ambiguity.path("remaining_count").asInt(), category + " ambiguous questions");
            assertEquals(review.path("quality_metrics").path("human_review_required").asInt(),
                    approval.path("remaining_count").asInt(), category + " human review count");
            assertEquals(review.path("review_date").asText(), dates.path("review_date").asText(),
                    category + " review date report");
        }
    }

    private static void validateReviewHeader(JsonNode entry, JsonNode review) {
        String category = entry.path("category").asText();
        assertAll(category + " review header",
                () -> assertEquals(2, review.path("schema_version").asInt()),
                () -> assertEquals(1, review.path("framework_version").asInt()),
                () -> assertEquals(category, review.path("category").asText()),
                () -> assertEquals(entry.path("slug").asText(), review.path("slug").asText()),
                () -> assertEquals(entry.path("status").asText(), review.path("review_status").asText()),
                () -> assertEquals(entry.path("last_reviewed").asText(), review.path("review_date").asText()),
                () -> assertEquals(entry.path("risk_level").asText(), review.path("risk_level").asText()),
                () -> assertEquals(entry.path("counts"), review.path("counts"), category + " indexed counts"),
                () -> assertEquals(new HashSet<>(LANGUAGES), textSet(review.path("reviewed_languages"))));
    }

    private static void validateCategoryContent(
            JsonNode entry,
            JsonNode review,
            Map<String, JsonNode> sources) throws IOException {
        String category = review.path("category").asText();
        Set<String> catalogSigns = signCodesForCategory(category);
        assertEquals(catalogSigns, fieldSet(review.path("signs")), category + " reviewed sign set");

        JsonNode rules = review.path("validation_rules");
        int questionsPerSign = rules.path("questions_per_sign").asInt();
        int passingScore = rules.path("passing_score").asInt();
        int expectedCorrectChoices = rules.path("correct_choices_per_language").asInt();
        Set<String> binaryTypes = textSet(rules.path("binary_question_types"));
        Map<String, Integer> expectedDistribution = integerMap(rules.path("difficulty_distribution"));

        for (String signCode : catalogSigns) {
            JsonNode signReview = review.path("signs").path(signCode);
            assertReviewMetadata(signReview, signCode, review.path("review_status").asText());
            assertSourceApplications(signReview.path("source_ids"), sources, "applicable_signs", signCode);

            Path signPath = canonicalFile(signReview.path("canonical_path").asText());
            JsonNode sign = read(signPath);
            Path directory = signPath.getParent();
            JsonNode questions = read(directory.resolve("questions.json"));
            JsonNode exam = read(directory.resolve("exams.json"));

            assertAll(signCode,
                    () -> assertEquals(signCode, sign.path("code").asText()),
                    () -> assertEquals(category, sign.path("category").asText()),
                    () -> assertTrue(Files.isRegularFile(resolvePublicImage(sign.path("image_path").asText())),
                            "Missing image for " + signCode),
                    () -> assertEquals(questionsPerSign, questions.size(), signCode + " question count"),
                    () -> assertEquals(questionsPerSign, signReview.path("questions").size(),
                            signCode + " review question count"));

            for (String language : LANGUAGES) {
                JsonNode localized = sign.path("i18n").path(language);
                assertAll(signCode + " " + language,
                        () -> assertText(localized, "name", signCode + " name " + language),
                        () -> assertText(localized, "summary", signCode + " summary " + language),
                        () -> assertText(localized, "description", signCode + " description " + language),
                        () -> assertText(localized, "driver_guidance", signCode + " guidance " + language),
                        () -> assertTrue(localized.path("exceptions").isArray(), "exceptions must be an array"));
            }

            Map<String, Integer> actualDistribution = new HashMap<>();
            Set<String> questionIds = new LinkedHashSet<>();
            for (int index = 0; index < questions.size(); index++) {
                JsonNode question = questions.get(index);
                String expectedId = "%s_Q%02d".formatted(signCode, index + 1);
                String questionId = question.path("question_id").asText();
                String difficulty = question.path("difficulty").asText();
                String type = question.path("type").asText();
                int choiceCount = expectedChoiceCount(rules, difficulty, type, binaryTypes);

                assertEquals(expectedId, questionId, "Question IDs must be ordered and sign-scoped");
                questionIds.add(questionId);
                actualDistribution.merge(difficulty, 1, Integer::sum);

                JsonNode questionReview = signReview.path("questions").path(questionId);
                assertReviewMetadata(questionReview, questionId, review.path("review_status").asText());
                assertSourceApplications(questionReview.path("source_ids"), sources,
                        "applicable_questions", questionId);

                for (String language : LANGUAGES) {
                    JsonNode localized = question.path("i18n").path(language);
                    assertText(localized, "question", questionId + " question " + language);
                    assertText(localized, "explanation", questionId + " explanation " + language);
                    assertEquals(choiceCount, localized.path("choices").size(),
                            questionId + " choice count " + language);
                    int correctChoices = 0;
                    for (JsonNode choice : localized.path("choices")) {
                        assertText(choice, "text", questionId + " choice " + language);
                        if (choice.path("is_correct").asBoolean()) {
                            correctChoices++;
                        }
                    }
                    assertEquals(expectedCorrectChoices, correctChoices,
                            questionId + " correct choice count " + language);
                }
            }

            assertEquals(expectedDistribution, actualDistribution, signCode + " difficulty distribution");
            assertEquals(passingScore, exam.path("passing_score").asInt(), signCode + " passing score");
            assertEquals(questionsPerSign, exam.path("total_questions").asInt(), signCode + " total questions");
            for (Map.Entry<String, Integer> difficulty : expectedDistribution.entrySet()) {
                assertEquals(difficulty.getValue(), exam.path("distribution").path(difficulty.getKey()).asInt(),
                        signCode + " exam " + difficulty.getKey());
            }
            assertEquals(questionIds, textSet(exam.path("exam_1").path("questions")),
                    signCode + " exam references");

            JsonNode examReview = signReview.path("exam");
            assertReviewMetadata(examReview, signCode + " exam", review.path("review_status").asText());
            for (JsonNode sourceId : examReview.path("source_ids")) {
                assertTrue(sources.containsKey(sourceId.asText()),
                        signCode + " exam has unknown source " + sourceId.asText());
            }
        }

        JsonNode counts = entry.path("counts");
        assertEquals(catalogSigns.size(), counts.path("signs").asInt(), category + " indexed signs");
        assertEquals(catalogSigns.size() * questionsPerSign, counts.path("questions").asInt(),
                category + " indexed questions");
        assertEquals(catalogSigns.size(), counts.path("exams").asInt(), category + " indexed exams");
    }

    private static void validateLessonItems(JsonNode review, Map<String, JsonNode> sources) throws IOException {
        for (Map.Entry<String, JsonNode> item : review.path("lesson_items").properties()) {
            String itemId = item.getKey();
            JsonNode metadata = item.getValue();
            assertReviewMetadata(metadata, itemId, review.path("review_status").asText());
            assertSourceApplications(metadata.path("source_ids"), sources, "applicable_lessons", itemId);

            JsonNode content = locateContent(metadata.path("content_locator"));
            for (String language : LANGUAGES) {
                String suffix = language.toLowerCase(Locale.ROOT);
                assertText(content, "title_" + suffix, itemId + " title " + language);
                assertText(content, "content_" + suffix, itemId + " content " + language);
                assertFalse(content.path("bulletPoints_" + suffix).isEmpty(),
                        itemId + " bullet points " + language);
            }
        }
    }

    private static void validateQualityMetrics(
            JsonNode entry,
            JsonNode review,
            Set<String> glossaryIds) {
        JsonNode metrics = review.path("quality_metrics");
        JsonNode counts = review.path("counts");
        String category = review.path("category").asText();
        Set<String> usedSources = collectSourceIds(review);
        Set<String> termIds = textSet(review.path("terminology_ids"));

        assertAll(category + " quality metrics",
                () -> assertEquals(counts.path("signs").asInt(), metrics.path("signs_reviewed").asInt()),
                () -> assertEquals(counts.path("questions").asInt(), metrics.path("questions_reviewed").asInt()),
                () -> assertEquals(counts.path("lessons").asInt(), metrics.path("lessons_reviewed").asInt()),
                () -> assertEquals(counts.path("lessons").asInt(), review.path("lesson_items").size()),
                () -> assertEquals(usedSources.size(), metrics.path("legal_sources_linked").asInt()),
                () -> assertEquals(termIds.size(), metrics.path("terminology_standardized").asInt()),
                () -> assertEquals("PASSED", metrics.path("cross_language_consistency").asText()),
                () -> assertEquals(review.path("future_law_items").size(),
                        metrics.path("future_law_items").asInt()),
                () -> assertTrue(metrics.path("human_review_required").asInt() >= 0),
                () -> assertEquals(entry.path("risk_level").asText(), metrics.path("risk_level").asText()),
                () -> assertTrue(glossaryIds.containsAll(termIds), category + " has unknown terminology IDs"));
        if ("APPROVED".equals(review.path("review_status").asText())) {
            assertEquals(0, metrics.path("human_review_required").asInt(),
                    category + " approved category cannot require human review");
        }
    }

    private static void evaluateContentAssertions(JsonNode review) throws IOException {
        for (JsonNode assertion : review.path("content_assertions")) {
            String type = assertion.path("type").asText();
            switch (type) {
                case "FORBIDDEN_FRAGMENT" -> {
                    String content = collectReviewedContent(review).toLowerCase(Locale.ROOT);
                    for (JsonNode fragment : assertion.path("fragments")) {
                        assertFalse(content.contains(fragment.asText().toLowerCase(Locale.ROOT)),
                                "Forbidden governed fragment remains: " + fragment.asText());
                    }
                }
                case "CORRECT_CHOICE_CONTAINS" -> {
                    String choice = correctChoice(assertion);
                    assertTrue(choice.toLowerCase(Locale.ROOT)
                                    .contains(assertion.path("expected").asText().toLowerCase(Locale.ROOT)),
                            assertion.path("question_id").asText() + " correct choice content");
                }
                case "CORRECT_CHOICE_EQUALS" -> assertEquals(
                        assertion.path("expected").asText(),
                        correctChoice(assertion),
                        assertion.path("question_id").asText() + " correct choice content");
                case "FILE_CONTAINS_ALL" -> {
                    String content = Files.readString(resolveProjectPath(assertion.path("path").asText()));
                    for (JsonNode fragment : assertion.path("fragments")) {
                        assertTrue(content.contains(fragment.asText()),
                                assertion.path("path").asText() + " missing " + fragment.asText());
                    }
                }
                default -> fail("Unsupported governance content assertion type: " + type);
            }
        }
    }

    private static Map<String, CatalogCounts> scanCatalog() throws IOException {
        Map<String, CatalogCounts> counts = new TreeMap<>();
        try (var directories = Files.list(IMPORT_ROOT)) {
            for (Path directory : directories.filter(Files::isDirectory).toList()) {
                Path signFile = directory.resolve("sign.json");
                Path questionsFile = directory.resolve("questions.json");
                Path examFile = directory.resolve("exams.json");
                if (!Files.isRegularFile(signFile)) {
                    continue;
                }
                String category = requiredText(read(signFile), "category", directory.getFileName() + " category");
                int questions = read(questionsFile).size();
                int exams = Files.isRegularFile(examFile) ? 1 : 0;
                counts.merge(category, new CatalogCounts(1, questions, exams), CatalogCounts::plus);
            }
        }
        return counts;
    }

    private static Set<String> signCodesForCategory(String category) throws IOException {
        Set<String> signs = new TreeSet<>();
        try (var directories = Files.list(IMPORT_ROOT)) {
            for (Path directory : directories.filter(Files::isDirectory).toList()) {
                Path signFile = directory.resolve("sign.json");
                if (!Files.isRegularFile(signFile)) {
                    continue;
                }
                JsonNode sign = read(signFile);
                if (category.equals(sign.path("category").asText())) {
                    signs.add(sign.path("code").asText());
                }
            }
        }
        return signs;
    }

    private static List<JsonNode> reviewedCategoryEntries(JsonNode index) {
        List<JsonNode> entries = new ArrayList<>();
        for (JsonNode entry : index.path("categories")) {
            if (!"NOT_REVIEWED".equals(entry.path("status").asText())) {
                entries.add(entry);
            }
        }
        assertFalse(entries.isEmpty(), "At least one governed category must be active");
        return entries;
    }

    private static Map<String, JsonNode> loadSources(Path path) throws IOException {
        Map<String, JsonNode> sources = new LinkedHashMap<>();
        for (JsonNode source : read(path).path("sources")) {
            String sourceId = requiredText(source, "source_id", "Legal source");
            assertTrue(source.path("official_url").asText().startsWith("https://"), sourceId + " official URL");
            assertText(source, "authority", sourceId + " authority");
            assertText(source, "jurisdiction", sourceId + " jurisdiction");
            assertText(source, "last_verified_date", sourceId + " verification date");
            assertTrue(source.has("effective_date"), sourceId + " effective_date field");
            assertText(source.path("official_title"), "NL", sourceId + " NL title");
            assertText(source.path("official_title"), "FR", sourceId + " FR title");
            assertTrue(sources.put(sourceId, source) == null, "Duplicate Source ID: " + sourceId);
        }
        return sources;
    }

    private static Set<String> glossaryTermIds() throws IOException {
        Set<String> ids = new HashSet<>();
        for (JsonNode term : read(GOVERNANCE_ROOT.resolve("terminology-glossary.json")).path("terms")) {
            ids.add(term.path("term_id").asText());
        }
        return ids;
    }

    private static void assertReviewMetadata(JsonNode review, String item, String expectedStatus) {
        assertNotNull(review, item + " review record");
        assertEquals(expectedStatus, review.path("status").asText(), item + " review status");
        assertText(review, "canonical_path", item + " canonical path");
        assertTrue(Files.isRegularFile(canonicalFile(review.path("canonical_path").asText())),
                item + " canonical file");
        assertDate(review.path("last_reviewed").asText(), item + " review date");
        assertText(review, "review_reason", item + " review reason");
        assertText(review, "original_review_language", item + " original review language");
        assertEquals(new HashSet<>(LANGUAGES), textSet(review.path("reviewed_languages")),
                item + " reviewed languages");
        assertFalse(review.path("source_ids").isEmpty(), item + " source IDs");
    }

    private static void assertSourceApplications(
            JsonNode sourceIds,
            Map<String, JsonNode> sources,
            String applicationField,
            String itemId) {
        for (JsonNode sourceIdNode : sourceIds) {
            String sourceId = sourceIdNode.asText();
            JsonNode source = sources.get(sourceId);
            assertNotNull(source, itemId + " has unknown source " + sourceId);
            assertTrue(textSet(source.path(applicationField)).contains(itemId),
                    sourceId + " does not declare " + itemId + " in " + applicationField);
        }
    }

    private static JsonNode locateContent(JsonNode locator) throws IOException {
        String type = locator.path("type").asText();
        if (!"LESSON_PAGE".equals(type)) {
            throw new AssertionError("Unsupported governed content locator: " + type);
        }
        JsonNode lessons = read(resolveProjectPath(locator.path("file").asText())).path("lessons");
        for (JsonNode lesson : lessons) {
            if (locator.path("lesson_id").asText().equals(lesson.path("id").asText())) {
                for (JsonNode page : lesson.path("pages")) {
                    if (locator.path("page_number").asInt() == page.path("pageNumber").asInt()) {
                        return page;
                    }
                }
            }
        }
        throw new AssertionError("Missing governed lesson page: " + locator);
    }

    private static String collectReviewedContent(JsonNode review) throws IOException {
        List<String> content = new ArrayList<>();
        for (Map.Entry<String, JsonNode> sign : review.path("signs").properties()) {
            Path signPath = canonicalFile(sign.getValue().path("canonical_path").asText());
            content.add(Files.readString(signPath));
            content.add(Files.readString(signPath.getParent().resolve("questions.json")));
        }
        for (Map.Entry<String, JsonNode> lesson : review.path("lesson_items").properties()) {
            content.add(locateContent(lesson.getValue().path("content_locator")).toString());
        }
        return String.join("\n", content);
    }

    private static String correctChoice(JsonNode assertion) throws IOException {
        String signCode = assertion.path("sign_code").asText();
        String questionId = assertion.path("question_id").asText();
        String language = assertion.path("language").asText();
        JsonNode questions = read(IMPORT_ROOT.resolve(signCode).resolve("questions.json"));
        for (JsonNode question : questions) {
            if (questionId.equals(question.path("question_id").asText())) {
                for (JsonNode choice : question.path("i18n").path(language).path("choices")) {
                    if (choice.path("is_correct").asBoolean()) {
                        return choice.path("text").asText();
                    }
                }
            }
        }
        throw new AssertionError("Missing correct choice for " + questionId + " in " + language);
    }

    private static Set<String> collectSourceIds(JsonNode review) {
        Set<String> ids = new HashSet<>();
        for (var signs = review.path("signs").elements(); signs.hasNext();) {
            JsonNode sign = signs.next();
            ids.addAll(textSet(sign.path("source_ids")));
            for (var questions = sign.path("questions").elements(); questions.hasNext();) {
                ids.addAll(textSet(questions.next().path("source_ids")));
            }
            ids.addAll(textSet(sign.path("exam").path("source_ids")));
        }
        for (var lessons = review.path("lesson_items").elements(); lessons.hasNext();) {
            ids.addAll(textSet(lessons.next().path("source_ids")));
        }
        return ids;
    }

    private static int expectedChoiceCount(
            JsonNode rules,
            String difficulty,
            String type,
            Set<String> binaryTypes) {
        if (binaryTypes.contains(type)) {
            return rules.path("binary_choice_count").asInt();
        }
        if ("HARD".equals(difficulty)) {
            return rules.path("hard_choice_count").asInt();
        }
        return rules.path("default_choice_count").asInt();
    }

    private static void assertStandardReportSections(String report, String label) {
        int previous = -1;
        for (String heading : REPORT_HEADINGS) {
            int position = report.indexOf(heading);
            assertTrue(position > previous, label + " is missing or misorders " + heading);
            previous = position;
        }
    }

    private static void assertQualityMetricRows(String report, JsonNode metrics) {
        Map<String, String> expected = new LinkedHashMap<>();
        expected.put("Signs reviewed", metrics.path("signs_reviewed").asText());
        expected.put("Questions reviewed", metrics.path("questions_reviewed").asText());
        expected.put("Lessons reviewed", metrics.path("lessons_reviewed").asText());
        expected.put("Legal sources linked", metrics.path("legal_sources_linked").asText());
        expected.put("Terminology standardized", metrics.path("terminology_standardized").asText());
        expected.put("Cross-language consistency", metrics.path("cross_language_consistency").asText());
        expected.put("Future-law items", metrics.path("future_law_items").asText());
        expected.put("Human review required", metrics.path("human_review_required").asText());
        expected.put("Risk level", metrics.path("risk_level").asText());
        expected.forEach((name, value) -> assertTrue(report.contains("| " + name + " | " + value + " |"),
                "Missing Quality Metrics row: " + name));
    }

    private static Map<String, Integer> integerMap(JsonNode object) {
        Map<String, Integer> values = new HashMap<>();
        object.properties().forEach(entry -> values.put(entry.getKey(), entry.getValue().asInt()));
        return values;
    }

    private static Set<String> fieldSet(JsonNode object) {
        Set<String> fields = new TreeSet<>();
        object.fieldNames().forEachRemaining(fields::add);
        return fields;
    }

    private static Set<String> textSet(JsonNode array) {
        Set<String> values = new LinkedHashSet<>();
        for (JsonNode item : array) {
            values.add(item.asText());
        }
        return values;
    }

    private static Path resolvePublicImage(String imagePath) {
        assertTrue(imagePath.startsWith("/images/"), "Image path must be public-root relative: " + imagePath);
        return resolveProjectPath("public/" + imagePath.substring(1));
    }

    private static Path canonicalFile(String canonicalPath) {
        String filePath = canonicalPath.split("#", 2)[0];
        return resolveProjectPath(filePath);
    }

    private static Path resolveGovernancePath(String relativePath) {
        Path resolved = GOVERNANCE_ROOT.resolve(relativePath).normalize();
        assertTrue(resolved.startsWith(GOVERNANCE_ROOT), "Governance path escapes its root: " + relativePath);
        return resolved;
    }

    private static Path resolveProjectPath(String relativePath) {
        Path resolved = ROOT.resolve(relativePath).normalize();
        assertTrue(resolved.startsWith(ROOT), "Path escapes the project root: " + relativePath);
        return resolved;
    }

    private static void assertText(JsonNode object, String field, String label) {
        String value = object.path(field).asText().trim();
        assertFalse(value.isBlank(), label + " is missing");
        String normalized = value.toLowerCase(Locale.ROOT);
        assertFalse(normalized.contains("todo")
                        || normalized.contains("placeholder")
                        || normalized.contains("lorem ipsum")
                        || normalized.contains("description for"),
                label + " contains placeholder text");
    }

    private static String requiredText(JsonNode object, String field, String label) {
        String value = object.path(field).asText().trim();
        assertFalse(value.isBlank(), label + " is missing");
        return value;
    }

    private static void assertDate(String value, String label) {
        try {
            LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            fail(label + " must be an ISO-8601 date: " + value);
        }
    }

    private static JsonNode read(Path path) throws IOException {
        return MAPPER.readTree(path.toFile());
    }

    private record CatalogCounts(int signs, int questions, int exams) {
        CatalogCounts plus(CatalogCounts other) {
            return new CatalogCounts(signs + other.signs, questions + other.questions, exams + other.exams);
        }
    }
}
