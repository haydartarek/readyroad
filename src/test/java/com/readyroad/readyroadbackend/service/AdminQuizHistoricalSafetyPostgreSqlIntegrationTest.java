package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.dto.AdminQuizQuestionRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("postgresql")
@Testcontainers
class AdminQuizHistoricalSafetyPostgreSqlIntegrationTest {

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
        registry.add("jwt.secret-key", () -> "cGFydC01MS10ZXN0LW9ubHktand0LXNlY3JldC1ub3QtZm9yLXByb2R1Y3Rpb24=");
        registry.add("readyroad.admin.default-password", () -> "Part-51-Test-Only-2026!");
    }

    @Autowired JdbcTemplate jdbc;
    @Autowired AdminQuizService service;
    @Autowired UserRepository userRepository;

    private long adminId;
    private long questionId;
    private long selectedOptionId;
    private long historicalCorrectOptionId;
    private long futureCorrectOptionId;
    private long examId;
    private String replacementCategoryCode;

    @BeforeEach
    void setUp() {
        jdbc.execute("""
                TRUNCATE exam_simulation_answers, exam_simulation_questions, exam_simulations,
                         quiz_answer_options, quiz_questions, users, audit_logs
                RESTART IDENTITY CASCADE
                """);

        List<Map<String, Object>> categories = jdbc.queryForList("""
                SELECT id, code
                FROM categories
                WHERE is_active = true
                  AND content_scope IN ('THEORETICAL_EXAM', 'BOTH')
                ORDER BY id
                LIMIT 2
                """);
        assertThat(categories).hasSize(2);
        long originalCategoryId = ((Number) categories.get(0).get("id")).longValue();
        replacementCategoryCode = String.valueOf(categories.get(1).get("code"));

        adminId = jdbc.queryForObject("""
                INSERT INTO users (username, email, full_name, password_hash, role)
                VALUES ('part51-admin', 'part51-admin@test.local', 'PART 51 Admin', 'not-a-real-secret', 'ADMIN')
                RETURNING id
                """, Long.class);
        long learnerId = jdbc.queryForObject("""
                INSERT INTO users (username, email, full_name, password_hash, role)
                VALUES ('part51-learner', 'part51-learner@test.local', 'PART 51 Learner', 'not-a-real-secret', 'USER')
                RETURNING id
                """, Long.class);

        questionId = jdbc.queryForObject("""
                INSERT INTO quiz_questions
                    (question_ar, question_en, question_nl, question_fr, question_type,
                     difficulty_level, category_id, content_image_url,
                     explanation_ar, explanation_en, explanation_nl, explanation_fr,
                     is_active, status, published_at)
                VALUES
                    ('سؤال قديم', 'Old question', 'Oude vraag', 'Ancienne question', 'MULTIPLE_CHOICE',
                     'EASY', ?, '/images/quiz/old.png',
                     'شرح قديم', 'Old explanation', 'Oude uitleg', 'Ancienne explication',
                     true, 'PUBLISHED', CURRENT_TIMESTAMP)
                RETURNING id
                """, Long.class, originalCategoryId);

        futureCorrectOptionId = insertOption("Future answer", false, 1);
        historicalCorrectOptionId = insertOption("Historical correct", true, 2);
        selectedOptionId = insertOption("Historical selected", false, 3);

        examId = jdbc.queryForObject("""
                INSERT INTO exam_simulations
                    (user_id, started_at, completed_at, expires_at, total_questions,
                     correct_answers, score_percentage, time_taken_seconds, status, language_code)
                VALUES (?, ?, ?, ?, 50, 0, 0, 120, 'COMPLETED', 'en')
                RETURNING id
                """, Long.class, learnerId, LocalDateTime.now().minusMinutes(10),
                LocalDateTime.now().minusMinutes(5), LocalDateTime.now().plusMinutes(20));
        jdbc.update("""
                INSERT INTO exam_simulation_questions (exam_id, question_id, question_order)
                VALUES (?, ?, 1)
                """, examId, questionId);
        jdbc.update("""
                INSERT INTO exam_simulation_answers
                    (exam_id, question_id, selected_option_id, correct_option_id,
                     is_correct, time_taken_seconds, answered_at)
                VALUES (?, ?, ?, ?, false, 12, ?)
                """, examId, questionId, selectedOptionId, historicalCorrectOptionId,
                LocalDateTime.now().minusMinutes(8));

        User admin = userRepository.findById(adminId).orElseThrow();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities()));
    }

    @AfterEach
    void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void editsFutureQuestionAndArchivesOptionWithoutChangingHistoricalExam() {
        var before = service.getQuestionById(questionId);
        AdminQuizQuestionRequest request = updatedRequest(before.version());

        var updated = service.updateQuestion(questionId, request);

        Map<String, Object> historicalAnswer = jdbc.queryForMap("""
                SELECT selected_option_id, correct_option_id, is_correct
                FROM exam_simulation_answers
                WHERE exam_id = ? AND question_id = ?
                """, examId, questionId);
        assertThat(((Number) historicalAnswer.get("selected_option_id")).longValue()).isEqualTo(selectedOptionId);
        assertThat(((Number) historicalAnswer.get("correct_option_id")).longValue()).isEqualTo(historicalCorrectOptionId);
        assertThat(historicalAnswer.get("is_correct")).isEqualTo(false);
        assertThat(jdbc.queryForObject(
                "SELECT score_percentage FROM exam_simulations WHERE id = ?", BigDecimal.class, examId))
                .isEqualByComparingTo("0.00");

        assertThat(jdbc.queryForObject(
                "SELECT is_active FROM quiz_answer_options WHERE id = ?", Boolean.class, selectedOptionId))
                .isFalse();
        assertThat(jdbc.queryForObject(
                "SELECT option_text_en FROM quiz_answer_options WHERE id = ?", String.class, selectedOptionId))
                .isEqualTo("Historical selected");
        assertThat(jdbc.queryForObject(
                "SELECT is_correct FROM quiz_answer_options WHERE id = ?", Boolean.class, futureCorrectOptionId))
                .isTrue();
        assertThat(updated.version()).isEqualTo(before.version() + 1);
        assertThat(updated.difficultyLevel()).isEqualTo("HARD");
        assertThat(updated.options()).extracting(option -> option.id())
                .containsExactly(futureCorrectOptionId, historicalCorrectOptionId);

        Map<String, Object> audit = jdbc.queryForMap("""
                SELECT actor, entity_id, safe_details,
                       (safe_details ->> 'adminId')::bigint AS admin_id
                FROM audit_logs
                WHERE event_type = 'ADMIN_QUIZ_UPDATED' AND entity_id = ?
                """, String.valueOf(questionId));
        assertThat(audit.get("actor")).isEqualTo("part51-admin");
        assertThat(((Number) audit.get("admin_id")).longValue()).isEqualTo(adminId);
        String safeDetails = String.valueOf(audit.get("safe_details"));
        assertThat(safeDetails)
                .contains("oldValueSummary")
                .contains("newValueSummary")
                .contains("optionsArchived")
                .doesNotContain("old.png")
                .doesNotContain("replacement.png");
    }

    @Test
    void rejectsAStaleVersionWithoutChangingTheLatestQuestion() {
        long originalVersion = service.getQuestionById(questionId).version();
        service.updateQuestion(questionId, updatedRequest(originalVersion));

        AdminQuizQuestionRequest stale = updatedRequest(originalVersion);
        stale.setQuestionEn("Stale overwrite");

        assertThatThrownBy(() -> service.updateQuestion(questionId, stale))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("updated by another administrator");
        assertThat(jdbc.queryForObject(
                "SELECT question_en FROM quiz_questions WHERE id = ?", String.class, questionId))
                .isEqualTo("Updated question");
        assertThat(jdbc.queryForObject(
                "SELECT COUNT(*) FROM audit_logs WHERE event_type = 'ADMIN_QUIZ_UPDATED'", Integer.class))
                .isEqualTo(1);
    }

    @Test
    void migrationAddsTheOptimisticVersionColumn() {
        assertThat(jdbc.queryForObject("""
                SELECT COUNT(*)
                FROM information_schema.columns
                WHERE table_schema = 'public'
                  AND table_name = 'quiz_questions'
                  AND column_name = 'version'
                  AND is_nullable = 'NO'
                """, Integer.class)).isEqualTo(1);
    }

    @Test
    void databaseRejectsOptionsOwnedByAnotherQuestion() {
        long foreignQuestionId = insertQuestion("Foreign question");
        long foreignOptionId = insertOption(foreignQuestionId, "Foreign option", true, 1);

        assertThatThrownBy(() -> jdbc.update("""
                UPDATE exam_simulation_answers
                SET selected_option_id = ?
                WHERE exam_id = ? AND question_id = ?
                """, foreignOptionId, examId, questionId))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> jdbc.update("""
                UPDATE exam_simulation_answers
                SET correct_option_id = ?
                WHERE exam_id = ? AND question_id = ?
                """, foreignOptionId, examId, questionId))
                .isInstanceOf(DataIntegrityViolationException.class);

        Map<String, Object> answer = jdbc.queryForMap("""
                SELECT selected_option_id, correct_option_id
                FROM exam_simulation_answers
                WHERE exam_id = ? AND question_id = ?
                """, examId, questionId);
        assertThat(((Number) answer.get("selected_option_id")).longValue()).isEqualTo(selectedOptionId);
        assertThat(((Number) answer.get("correct_option_id")).longValue()).isEqualTo(historicalCorrectOptionId);
    }

    @Test
    void databasePreservesHistoricallyReferencedQuestionsAndOptions() {
        assertThatThrownBy(() -> jdbc.update(
                "DELETE FROM quiz_answer_options WHERE id = ?", selectedOptionId))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> jdbc.update(
                "DELETE FROM quiz_questions WHERE id = ?", questionId))
                .isInstanceOf(DataIntegrityViolationException.class);

        assertThat(jdbc.queryForObject(
                "SELECT COUNT(*) FROM exam_simulation_answers WHERE exam_id = ?", Integer.class, examId))
                .isEqualTo(1);
    }

    @Test
    void databaseAllowsOnlyOneActiveCorrectOptionPerQuestion() {
        assertThatThrownBy(() -> jdbc.update(
                "UPDATE quiz_answer_options SET is_correct = true WHERE id = ?", futureCorrectOptionId))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private long insertOption(String english, boolean correct, int order) {
        return insertOption(questionId, english, correct, order);
    }

    private long insertOption(long ownerQuestionId, String english, boolean correct, int order) {
        return jdbc.queryForObject("""
                INSERT INTO quiz_answer_options
                    (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr,
                     is_correct, display_order, is_active)
                VALUES (?, ?, ?, ?, ?, ?, ?, true)
                RETURNING id
                """, Long.class, ownerQuestionId, english + " AR", english, english + " NL", english + " FR",
                correct, order);
    }

    private long insertQuestion(String english) {
        Long categoryId = jdbc.queryForObject(
                "SELECT category_id FROM quiz_questions WHERE id = ?", Long.class, questionId);
        return jdbc.queryForObject("""
                INSERT INTO quiz_questions
                    (question_ar, question_en, question_nl, question_fr, question_type,
                     difficulty_level, category_id, is_active, status, published_at)
                VALUES (?, ?, ?, ?, 'MULTIPLE_CHOICE', 'EASY', ?, true, 'PUBLISHED', CURRENT_TIMESTAMP)
                RETURNING id
                """, Long.class, english + " AR", english, english + " NL", english + " FR", categoryId);
    }

    private AdminQuizQuestionRequest updatedRequest(long version) {
        AdminQuizQuestionRequest request = new AdminQuizQuestionRequest();
        request.setVersion(version);
        request.setCategoryCode(replacementCategoryCode);
        request.setDifficultyLevel("HARD");
        request.setQuestionType("MULTIPLE_CHOICE");
        request.setQuestionEn("Updated question");
        request.setQuestionAr("سؤال محدّث");
        request.setQuestionNl("Bijgewerkte vraag");
        request.setQuestionFr("Question mise à jour");
        request.setExplanationEn("Updated explanation");
        request.setExplanationAr("شرح محدّث");
        request.setExplanationNl("Bijgewerkte uitleg");
        request.setExplanationFr("Explication mise à jour");
        request.setContentImageUrl("/images/quiz/replacement.png");
        request.setIsActive(true);
        request.setOptions(List.of(
                option(futureCorrectOptionId, "Future answer updated", true, 1),
                option(historicalCorrectOptionId, "Historical correct updated", false, 2)));
        return request;
    }

    private AdminQuizQuestionRequest.OptionDTO option(long id, String english, boolean correct, int order) {
        return new AdminQuizQuestionRequest.OptionDTO(
                id, english, english + " AR", english + " NL", english + " FR", correct, order);
    }
}
