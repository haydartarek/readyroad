package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.readyroad.readyroadbackend.domain.repository.UserQuestionHistoryRepository;
import java.time.LocalDateTime;
import java.util.Map;
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
class ExamQuestionExposurePostgreSqlIntegrationTest {

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
                () -> "cGhhc2UtM2EtdGVzdC1vbmx5LWp3dC1zZWNyZXQtbm90LWZvci1wcm9kdWN0aW9u");
        registry.add("readyroad.admin.default-password", () -> "Phase-3A-Test-Only-2026!");
    }

    @Autowired JdbcTemplate jdbc;
    @Autowired ExamService examService;
    @Autowired UserQuestionHistoryRepository historyRepository;

    private long userId;
    private long questionId;
    private long examId;

    @BeforeEach
    void setUp() {
        jdbc.execute("""
                TRUNCATE exam_simulation_answers, exam_simulation_questions, exam_simulations,
                         user_question_history, quiz_answer_options, quiz_questions, users
                RESTART IDENTITY CASCADE
                """);

        long categoryId = jdbc.queryForObject("""
                SELECT id
                FROM categories
                WHERE is_active = true
                  AND content_scope IN ('THEORETICAL_EXAM', 'BOTH')
                ORDER BY id
                LIMIT 1
                """, Long.class);
        userId = jdbc.queryForObject("""
                INSERT INTO users (username, email, full_name, password_hash, role)
                VALUES ('phase3a-user', 'phase3a@test.local', 'Phase 3A User', 'not-a-secret', 'USER')
                RETURNING id
                """, Long.class);
        questionId = jdbc.queryForObject("""
                INSERT INTO quiz_questions
                    (question_ar, question_en, question_nl, question_fr, question_type,
                     difficulty_level, category_id, is_active, status, published_at)
                VALUES
                    ('سؤال العرض', 'Presented question', 'Getoonde vraag', 'Question presentee',
                     'MULTIPLE_CHOICE', 'EASY', ?, true, 'PUBLISHED', CURRENT_TIMESTAMP)
                RETURNING id
                """, Long.class, categoryId);
        examId = jdbc.queryForObject("""
                INSERT INTO exam_simulations
                    (user_id, started_at, expires_at, total_questions, status)
                VALUES (?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '750 seconds', 50, 'IN_PROGRESS')
                RETURNING id
                """, Long.class, userId);
        jdbc.update("""
                INSERT INTO exam_simulation_questions (exam_id, question_id, question_order)
                VALUES (?, ?, 1)
                """, examId, questionId);
    }

    @Test
    void abandonedExamKeepsPresentationWithoutInventingAnAnswer() {
        examService.recordQuestionPresented(examId, questionId, userId);
        examService.recordQuestionPresented(examId, questionId, userId);
        examService.cancelExam(examId, userId);

        Map<String, Object> history = historyRow();
        assertThat(history.get("question_type")).isEqualTo("THEORY");
        assertThat(history.get("last_shown_type")).isEqualTo("EXAM");
        assertThat(history.get("last_shown_at")).isNotNull();
        assertThat(((Number) history.get("times_shown")).intValue()).isEqualTo(1);
        assertThat(history.get("last_presented_at")).isNotNull();
        assertThat(((Number) history.get("times_presented")).intValue()).isEqualTo(1);
        assertThat(history.get("answered_at")).isNull();
        assertThat(((Number) history.get("times_correct")).intValue()).isZero();
        assertThat(((Number) history.get("times_wrong")).intValue()).isZero();
        assertThat(jdbc.queryForObject("""
                SELECT presented_at IS NOT NULL
                FROM exam_simulation_questions
                WHERE exam_id = ? AND question_id = ?
                """, Boolean.class, examId, questionId)).isTrue();
    }

    @Test
    void answeredEventDoesNotFabricatePresentation() {
        historyRepository.upsertQuestionAnswered(
                userId, questionId, LocalDateTime.now(), true, 12);

        Map<String, Object> history = historyRow();
        assertThat(history.get("question_type")).isEqualTo("THEORY");
        assertThat(history.get("last_shown_type")).isNull();
        assertThat(history.get("last_shown_at")).isNull();
        assertThat(((Number) history.get("times_shown")).intValue()).isZero();
        assertThat(history.get("last_presented_at")).isNull();
        assertThat(((Number) history.get("times_presented")).intValue()).isZero();
        assertThat(history.get("answered_at")).isNotNull();
        assertThat(((Number) history.get("times_correct")).intValue()).isEqualTo(1);
        assertThat(((Number) history.get("times_wrong")).intValue()).isZero();
    }

    @Test
    void answerPreservesTheExistingExamPresentationSemantics() {
        examService.recordQuestionPresented(examId, questionId, userId);
        LocalDateTime presentedAt = jdbc.queryForObject("""
                SELECT last_shown_at
                FROM user_question_history
                WHERE user_id = ? AND question_ref_id = ?
                """, LocalDateTime.class, userId, questionId);

        historyRepository.upsertQuestionAnswered(
                userId, questionId, LocalDateTime.now(), false, 15);

        Map<String, Object> history = historyRow();
        assertThat(history.get("last_shown_type")).isEqualTo("EXAM");
        assertThat(jdbc.queryForObject("""
                SELECT last_shown_at
                FROM user_question_history
                WHERE user_id = ? AND question_ref_id = ?
                """, LocalDateTime.class, userId, questionId)).isEqualTo(presentedAt);
        assertThat(((Number) history.get("times_shown")).intValue()).isEqualTo(1);
        assertThat(history.get("last_presented_at")).isNotNull();
        assertThat(((Number) history.get("times_presented")).intValue()).isEqualTo(1);
        assertThat(history.get("answered_at")).isNotNull();
        assertThat(((Number) history.get("times_correct")).intValue()).isZero();
        assertThat(((Number) history.get("times_wrong")).intValue()).isEqualTo(1);
    }

    private Map<String, Object> historyRow() {
        return jdbc.queryForMap("""
                SELECT question_type, last_shown_type, last_shown_at, times_shown,
                       last_presented_at, times_presented,
                       answered_at, times_correct, times_wrong
                FROM user_question_history
                WHERE user_id = ? AND question_ref_id = ?
                """, userId, questionId);
    }
}
