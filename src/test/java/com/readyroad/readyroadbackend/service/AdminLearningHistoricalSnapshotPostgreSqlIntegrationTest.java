package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.dto.exam.TheoryExamQuestionSnapshot;
import com.readyroad.readyroadbackend.dto.exam.TheoryExamQuestionSnapshot.CategorySnapshot;
import com.readyroad.readyroadbackend.dto.exam.TheoryExamQuestionSnapshot.LocalizedText;
import com.readyroad.readyroadbackend.dto.exam.TheoryExamQuestionSnapshot.OptionSnapshot;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("postgresql")
@Testcontainers
class AdminLearningHistoricalSnapshotPostgreSqlIntegrationTest {

    @Container
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.flyway.url", POSTGRES::getJdbcUrl);
        registry.add("spring.flyway.user", POSTGRES::getUsername);
        registry.add("spring.flyway.password", POSTGRES::getPassword);
        registry.add("readyroad.marketing.enabled", () -> "false");
        registry.add("jwt.secret-key", () -> "aGlzdG9yaWNhbC10ZXN0LWp3dC1zZWNyZXQtbm90LXByb2R1Y3Rpb24=");
        registry.add("readyroad.admin.default-password", () -> "Historical-Test-Only-2026!");
    }

    @Autowired JdbcTemplate jdbc;
    @Autowired ObjectMapper objectMapper;
    @Autowired AdminLearningStore store;
    @Autowired AdminTheoryExamHistoryService historyService;

    private long userId;
    private long categoryId;
    private long questionId;
    private long correctOptionId;
    private long selectedOptionId;

    @BeforeEach
    void setUp() {
        jdbc.update("""
                TRUNCATE exam_simulation_answers, exam_simulation_questions, exam_simulations,
                         quiz_answer_options, quiz_questions, users
                RESTART IDENTITY CASCADE
                """);
        categoryId = jdbc.queryForObject("""
                SELECT id FROM categories
                WHERE content_scope IN ('THEORETICAL_EXAM', 'BOTH')
                ORDER BY id LIMIT 1
                """, Long.class);
        jdbc.update("""
                UPDATE categories SET name_en = 'Current category', name_nl = 'Huidige categorie',
                    name_fr = 'Categorie actuelle', name_ar = 'الفئة الحالية'
                WHERE id = ?
                """, categoryId);
        userId = jdbc.queryForObject("""
                INSERT INTO users (username, email, full_name, password_hash, role, preferred_language)
                VALUES ('history-user', 'history@test.local', 'History User', 'not-a-secret', 'USER', 'en')
                RETURNING id
                """, Long.class);
        questionId = jdbc.queryForObject("""
                INSERT INTO quiz_questions
                    (question_ar, question_en, question_nl, question_fr, question_type,
                     difficulty_level, category_id, is_active, status, published_at)
                VALUES ('حالي', 'Current question', 'Huidige vraag', 'Question actuelle',
                        'MULTIPLE_CHOICE', 'HARD', ?, true, 'PUBLISHED', CURRENT_TIMESTAMP)
                RETURNING id
                """, Long.class, categoryId);
        correctOptionId = insertOption("Current correct", true, 0);
        selectedOptionId = insertOption("Current selected", false, 1);
    }

    @Test
    void v39AddsNullableForwardOnlySnapshotColumnsWithoutBackfill() {
        assertThat(jdbc.queryForList("""
                SELECT column_name FROM information_schema.columns
                WHERE table_schema = 'public' AND table_name = 'exam_simulation_questions'
                  AND column_name IN ('historical_snapshot_version', 'historical_snapshot_json')
                """, String.class)).containsExactlyInAnyOrder(
                        "historical_snapshot_version", "historical_snapshot_json");

        long legacyExamId = insertExam();
        insertExamQuestion(legacyExamId, null);
        assertThat(jdbc.queryForObject("""
                SELECT historical_snapshot_version IS NULL AND historical_snapshot_json IS NULL
                FROM exam_simulation_questions WHERE exam_id = ?
                """, Boolean.class, legacyExamId)).isTrue();
    }

    @Test
    void databaseRejectsASnapshotForAnotherQuestion() throws Exception {
        long examId = insertExam();

        assertThatThrownBy(() -> insertExamQuestion(examId, snapshotJson(questionId + 999)))
                .hasMessageContaining("chk_exam_question_historical_snapshot");
    }

    @Test
    void adminHistoryKeepsSnapshotContentButUsesCurrentCategoryNameByStableId() throws Exception {
        long examId = insertExam();
        insertExamQuestion(examId, snapshotJson());
        insertAnswer(examId);

        jdbc.update("UPDATE quiz_questions SET question_en = 'Edited later', difficulty_level = 'EASY' WHERE id = ?", questionId);
        jdbc.update("UPDATE quiz_answer_options SET option_text_en = 'Edited option later' WHERE question_id = ?", questionId);
        jdbc.update("UPDATE categories SET name_en = 'Edited category later' WHERE id = ?", categoryId);

        var history = historyService.load(examId);

        assertThat(history.status()).isEqualTo(AdminTheoryExamHistoryService.SNAPSHOT_COMPLETE);
        assertThat(history.result().questions()).singleElement().satisfies(question -> {
            assertThat(question.questionTextEn()).isEqualTo("Historical question");
            assertThat(question.selectedOptionTextEn()).isEqualTo("Historical selected");
            assertThat(question.correctOptionTextEn()).isEqualTo("Historical correct");
            assertThat(question.categoryNameEn()).isEqualTo("Edited category later");
            assertThat(question.difficulty()).isEqualTo("MEDIUM");
            assertThat(question.isCorrect()).isFalse();
        });
    }

    @Test
    void difficultyAnalyticsExcludeLegacyAnswersInsteadOfReclassifyingThem() throws Exception {
        long snapshotExam = insertExam();
        insertExamQuestion(snapshotExam, snapshotJson());
        insertAnswer(snapshotExam);
        long legacyExam = insertExam();
        insertExamQuestion(legacyExam, null);
        insertAnswer(legacyExam);

        var performance = store.findDifficultyPerformance(userId);

        assertThat(performance.items()).singleElement().satisfies(row -> {
            assertThat(row.difficulty()).isEqualTo("MEDIUM");
            assertThat(row.answeredQuestions()).isEqualTo(1);
            assertThat(row.correctAnswers()).isZero();
        });
        assertThat(performance.snapshotBackedAnswers()).isEqualTo(1);
        assertThat(performance.legacyAnswersExcluded()).isEqualTo(1);
        assertThat(performance.evidenceStatus()).isEqualTo("SNAPSHOT_PARTIAL");
    }

    private long insertOption(String text, boolean correct, int displayOrder) {
        return jdbc.queryForObject("""
                INSERT INTO quiz_answer_options
                    (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr,
                     is_correct, display_order, is_active)
                VALUES (?, ?, ?, ?, ?, ?, ?, true)
                RETURNING id
                """, Long.class, questionId, text, text, text, text, correct, displayOrder);
    }

    private long insertExam() {
        return jdbc.queryForObject("""
                INSERT INTO exam_simulations
                    (user_id, started_at, completed_at, expires_at, total_questions,
                     correct_answers, score_percentage, time_taken_seconds, status, language_code)
                VALUES (?, ?, ?, ?, 50, 0, 0, 120, 'COMPLETED', 'en')
                RETURNING id
                """, Long.class, userId, LocalDateTime.now().minusMinutes(3),
                LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(27));
    }

    private void insertExamQuestion(long examId, String snapshotJson) {
        jdbc.update("""
                INSERT INTO exam_simulation_questions
                    (exam_id, question_id, question_order, historical_snapshot_version, historical_snapshot_json)
                VALUES (?, ?, 1, ?, ?)
                """, examId, questionId, snapshotJson == null ? null : (short) 1, snapshotJson);
    }

    private void insertAnswer(long examId) {
        jdbc.update("""
                INSERT INTO exam_simulation_answers
                    (exam_id, question_id, selected_option_id, correct_option_id,
                     is_correct, time_taken_seconds, answered_at)
                VALUES (?, ?, ?, ?, false, 15, CURRENT_TIMESTAMP)
                """, examId, questionId, selectedOptionId, correctOptionId);
    }

    private String snapshotJson() throws Exception {
        return snapshotJson(questionId);
    }

    private String snapshotJson(long snapshotQuestionId) throws Exception {
        return objectMapper.writeValueAsString(new TheoryExamQuestionSnapshot(
                (short) 1,
                snapshotQuestionId,
                text("Historical question"),
                text("Historical explanation"),
                "/historical.webp",
                new CategorySnapshot(categoryId, "A", text("Historical category")),
                "MEDIUM",
                List.of(
                        new OptionSnapshot(correctOptionId, text("Historical correct"), true, 0),
                        new OptionSnapshot(selectedOptionId, text("Historical selected"), false, 1))));
    }

    private static LocalizedText text(String value) {
        return new LocalizedText(value, value, value, value);
    }
}
