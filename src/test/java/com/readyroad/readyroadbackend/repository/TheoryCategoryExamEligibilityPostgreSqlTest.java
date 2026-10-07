package com.readyroad.readyroadbackend.repository;

import com.readyroad.readyroadbackend.domain.repository.TheoryQuestionCoverageStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import static org.assertj.core.api.Assertions.assertThat;

/** Real PostgreSQL eligibility/count tests in an isolated container; no application migrations. */
@Testcontainers
class TheoryCategoryExamEligibilityPostgreSqlTest {
    @Container static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine");
    private JdbcTemplate jdbc;
    private TheoryQuestionCoverageStore store;

    @BeforeEach
    void setUp() {
        jdbc = new JdbcTemplate(new DriverManagerDataSource(
                POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword()));
        jdbc.execute("DROP TABLE IF EXISTS user_question_history, quiz_answer_options, quiz_questions, categories");
        jdbc.execute("""
            CREATE TABLE categories (id BIGINT PRIMARY KEY, code TEXT, is_active BOOLEAN,
              content_scope TEXT, display_order INT, name_en TEXT, name_nl TEXT, name_fr TEXT, name_ar TEXT);
            CREATE TABLE quiz_questions (id BIGINT PRIMARY KEY, category_id BIGINT,
              is_active BOOLEAN, status TEXT, question_en TEXT, question_nl TEXT, question_fr TEXT, question_ar TEXT);
            CREATE TABLE quiz_answer_options (id BIGINT PRIMARY KEY, question_id BIGINT,
              is_active BOOLEAN, is_correct BOOLEAN, option_text_en TEXT, option_text_nl TEXT,
              option_text_fr TEXT, option_text_ar TEXT);
            CREATE TABLE user_question_history (user_id BIGINT, question_ref_id BIGINT,
              question_type TEXT, times_presented INT, times_correct INT, times_wrong INT);
            INSERT INTO categories VALUES (101, 'TH01', true, 'THEORETICAL_EXAM', 1,
              'Priority', 'Voorrang', 'Priorité', 'الأولوية'),
              (102, 'TH02', true, 'THEORETICAL_EXAM', 2, 'Speed', 'Snelheid', 'Vitesse', 'السرعة');
            """);
        store = new TheoryQuestionCoverageStore(jdbc);
    }

    @Test
    void deliveryCountersAndDashboardUseTheSameLiveCategoryMembership() {
        question(1, 101, true, "PUBLISHED", "Published question", "Question publiée");
        question(2, 101, true, "DRAFT", "Draft question", "Brouillon");
        question(3, 101, false, "PUBLISHED", "Inactive question", "Question inactive");
        question(4, 102, true, "PUBLISHED", "Other category question", "Autre question");
        assertThat(store.findEligibleQuestionIds(101, "en")).containsExactly(1L);
        assertThat(store.countEligibleQuestionsByCategory("en").get(101L)).isEqualTo(1L);
        assertThat(store.findCoverage(42, "en").stream().filter(row -> row.categoryId() == 101)
                .findFirst().orElseThrow().eligibleQuestions()).isEqualTo(1);
        question(5, 101, true, "PUBLISHED", "New question", "Nouvelle question");
        assertThat(store.findEligibleQuestionIds(101, "en")).containsExactly(1L, 5L);
        assertThat(store.countEligibleQuestionsByCategory("en").get(101L)).isEqualTo(2L);
        jdbc.update("DELETE FROM quiz_answer_options WHERE question_id = 1");
        jdbc.update("DELETE FROM quiz_questions WHERE id = 1");
        assertThat(store.findEligibleQuestionIds(101, "en")).containsExactly(5L);
        assertThat(store.countEligibleQuestionsByCategory("en").get(101L)).isEqualTo(1L);
    }

    @Test
    void unavailableTranslationsAndInvalidOptionsNeverInflateTheCounter() {
        question(1, 101, true, "PUBLISHED", "Question", "");
        question(2, 101, true, "PUBLISHED", "Other question", "Autre question");
        jdbc.update("UPDATE quiz_answer_options SET is_correct = false WHERE question_id = 2");
        question(3, 101, true, "PUBLISHED", "Third question", "Troisième question");
        jdbc.update("UPDATE quiz_answer_options SET option_text_en = 'Option A' WHERE id = 31");
        assertThat(store.findEligibleQuestionIds(101, "en")).containsExactly(1L);
        assertThat(store.findEligibleQuestionIds(101, "fr")).containsExactly(3L);
        assertThat(store.countEligibleQuestionsByCategory("en").get(101L)).isEqualTo(1L);
        jdbc.update("UPDATE categories SET is_active = false WHERE id = 101");
        assertThat(store.findEligibleQuestionIds(101, "en")).isEmpty();
    }

    private void question(long id, long categoryId, boolean active, String status, String en, String fr) {
        jdbc.update("INSERT INTO quiz_questions VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                id, categoryId, active, status, en, en, fr, en);
        for (int option = 1; option <= 2; option++) {
            jdbc.update("INSERT INTO quiz_answer_options VALUES (?, ?, true, ?, ?, ?, ?, ?)",
                    id * 10 + option, id, option == 1, "Answer " + option, "Antwoord " + option,
                    "Réponse " + option, "إجابة " + option);
        }
    }
}
