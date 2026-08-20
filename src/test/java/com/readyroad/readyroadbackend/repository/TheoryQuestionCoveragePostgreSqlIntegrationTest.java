package com.readyroad.readyroadbackend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.readyroad.readyroadbackend.domain.repository.TheoryQuestionCoverageStore;
import com.readyroad.readyroadbackend.domain.repository.TheoryQuestionCoverageStore.CoverageRow;
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
class TheoryQuestionCoveragePostgreSqlIntegrationTest {

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
        registry.add("jwt.secret-key",
                () -> "cGhhc2UtNS10ZXN0LW9ubHktand0LXNlY3JldC1ub3QtZm9yLXByb2R1Y3Rpb24=");
        registry.add("readyroad.admin.default-password", () -> "Phase-5-Test-Only-2026!");
    }

    @Autowired JdbcTemplate jdbc;
    @Autowired TheoryQuestionCoverageStore coverageStore;

    private long categoryId;
    private long userId;

    @BeforeEach
    void setUp() {
        jdbc.execute("""
                TRUNCATE exam_simulation_answers, exam_simulation_questions, exam_simulations,
                         user_question_history, quiz_answer_options, quiz_questions, users
                RESTART IDENTITY CASCADE
                """);
        categoryId = jdbc.queryForObject("""
                SELECT id FROM categories
                WHERE content_scope IN ('THEORETICAL_EXAM', 'BOTH')
                ORDER BY id LIMIT 1
                """, Long.class);
        jdbc.update("""
                UPDATE categories
                SET is_active = true,
                    name_en = 'Priority', name_nl = 'Voorrang',
                    name_fr = 'Priorite', name_ar = 'الأولوية'
                WHERE id = ?
                """, categoryId);
        userId = insertUser();
    }

    @Test
    void calculatesCurrentCoverageFromPresentedAndAnsweredHistorySeparately() {
        long answered = insertQuestion("Answered", true, "PUBLISHED", "Vraag", "Question", "Question", "سؤال");
        long abandoned = insertQuestion("Abandoned", true, "PUBLISHED", "Vraag 2", "Question 2", "Question 2", "سؤال 2");
        insertQuestion("Unseen", true, "PUBLISHED", "Vraag 3", "Question 3", "Question 3", "سؤال 3");
        insertQuestion("Draft", true, "DRAFT", "Concept", "Draft", "Brouillon", "مسودة");
        insertQuestion("Inactive", false, "PUBLISHED", "Inactief", "Inactive", "Inactive", "غير نشط");

        insertHistory(answered, "THEORY", 2, 1, 1, 2);
        insertHistory(abandoned, "THEORY", 1, 0, 0, 0);

        CoverageRow row = rowFor("en");

        assertThat(row.eligibleQuestions()).isEqualTo(3);
        assertThat(row.uniqueQuestionsSeen()).isEqualTo(2);
        assertThat(row.timesPresented()).isEqualTo(3);
        assertThat(row.timesAnswered()).isEqualTo(2);
        assertThat(row.timesCorrect()).isEqualTo(1);
        assertThat(row.timesIncorrect()).isEqualTo(1);
    }

    @Test
    void derivesTheEligibleDenominatorIndependentlyForArNlEnAndFr() {
        insertQuestion("Complete", true, "PUBLISHED", "Volledig", "Complete", "Complet", "مكتمل");
        insertQuestion("Missing French", true, "PUBLISHED", "Nederlands", "English", "", "عربي");
        long invalidDutchOption = insertQuestion(
                "Dutch option placeholder", true, "PUBLISHED", "Nederlandse optie", "English option", "Option francaise", "خيار عربي");
        jdbc.update("""
                UPDATE quiz_answer_options
                SET option_text_nl = 'Optie A'
                WHERE question_id = ? AND display_order = 0
                """, invalidDutchOption);

        assertThat(rowFor("en").eligibleQuestions()).isEqualTo(3);
        assertThat(rowFor("ar").eligibleQuestions()).isEqualTo(3);
        assertThat(rowFor("fr").eligibleQuestions()).isEqualTo(2);
        assertThat(rowFor("nl").eligibleQuestions()).isEqualTo(2);
        assertThat(rowFor("ar").categoryName()).isEqualTo("الأولوية");
        assertThat(rowFor("nl").categoryName()).isEqualTo("Voorrang");
    }

    @Test
    void ignoresNonTheoryAndLegacyShownOnlyHistoryWithoutFabricatingExposure() {
        long signHistory = insertQuestion("Sign history", true, "PUBLISHED", "Teken", "Sign", "Signe", "علامة");
        long legacy = insertQuestion("Legacy shown", true, "PUBLISHED", "Oud", "Legacy", "Ancien", "قديم");
        insertHistory(signHistory, "SIGN", 5, 4, 1, 5);
        insertHistory(legacy, "THEORY", 0, 1, 0, 4);

        CoverageRow row = rowFor("en");

        assertThat(row.eligibleQuestions()).isEqualTo(2);
        assertThat(row.uniqueQuestionsSeen()).isZero();
        assertThat(row.timesPresented()).isZero();
        assertThat(row.timesAnswered()).isEqualTo(1);
        assertThat(row.timesCorrect()).isEqualTo(1);
    }

    private CoverageRow rowFor(String language) {
        List<CoverageRow> rows = coverageStore.findCoverage(userId, language);
        return rows.stream()
                .filter(row -> row.categoryId() == categoryId)
                .findFirst()
                .orElseThrow();
    }

    private long insertUser() {
        return jdbc.queryForObject("""
                INSERT INTO users (username, email, full_name, password_hash, role, preferred_language)
                VALUES ('coverage-user', 'coverage@test.local', 'Coverage User', 'not-a-secret', 'USER', 'en')
                RETURNING id
                """, Long.class);
    }

    private long insertQuestion(
            String label,
            boolean active,
            String status,
            String nl,
            String en,
            String fr,
            String ar) {
        long questionId = jdbc.queryForObject("""
                INSERT INTO quiz_questions
                    (question_ar, question_en, question_nl, question_fr, question_type,
                     difficulty_level, category_id, is_active, status, published_at)
                VALUES (?, ?, ?, ?, 'MULTIPLE_CHOICE', 'EASY', ?, ?, ?, CURRENT_TIMESTAMP)
                RETURNING id
                """, Long.class, ar, en, nl, fr, categoryId, active, status);
        insertOptions(questionId, label + " NL", label + " EN", label + " FR", label + " AR", true);
        return questionId;
    }

    private void insertOptions(
            long questionId,
            String nl,
            String en,
            String fr,
            String ar,
            boolean firstCorrect) {
        jdbc.update("""
                INSERT INTO quiz_answer_options
                    (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr,
                     is_correct, display_order, is_active)
                VALUES (?, ?, ?, ?, ?, ?, 0, true),
                       (?, 'بديل', 'Alternative', 'Alternatief', 'Alternative', false, 1, true)
                """,
                questionId, ar, en, nl, fr, firstCorrect,
                questionId);
    }

    private void insertHistory(
            long questionId,
            String questionType,
            int presented,
            int correct,
            int incorrect,
            int legacyShown) {
        jdbc.update("""
                INSERT INTO user_question_history
                    (user_id, question_id, question_ref_id, question_type,
                     times_presented, times_shown, times_correct, times_wrong,
                     last_presented_at, last_shown_at, last_shown_type)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?,
                        CASE WHEN ? > 0 THEN CURRENT_TIMESTAMP ELSE NULL END,
                        CASE WHEN ? > 0 THEN CURRENT_TIMESTAMP ELSE NULL END,
                        'EXAM')
                """,
                userId, questionId, questionId, questionType,
                presented, legacyShown, correct, incorrect,
                presented, legacyShown);
    }
}
