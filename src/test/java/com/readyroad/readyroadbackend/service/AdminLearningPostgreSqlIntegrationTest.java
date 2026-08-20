package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
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
class AdminLearningPostgreSqlIntegrationTest {

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
        registry.add("jwt.secret-key", () -> "bGVhcm5pbmctdGVzdC1qd3Qtc2VjcmV0LW5vdC1mb3ItcHJvZHVjdGlvbg==");
        registry.add("readyroad.admin.default-password", () -> "Learning-Test-Only-2026!");
    }

    @Autowired JdbcTemplate jdbc;
    @Autowired AdminLearningStore store;

    private long studentId;
    private long studentWithOneExamId;
    private long studentWithNoExamsId;

    @BeforeEach
    void setUp() {
        jdbc.update("TRUNCATE users RESTART IDENTITY CASCADE");
        studentId = insertUser("student-many", "student-many@test.local", "USER");
        studentWithOneExamId = insertUser("student-one", "student-one@test.local", "USER");
        studentWithNoExamsId = insertUser("student-none", "student-none@test.local", "USER");
        long adminId = insertUser("admin-audit", "admin@test.local", "ADMIN");
        long moderatorId = insertUser("moderator-audit", "moderator@test.local", "MODERATOR");
        insertCompletedExam(studentId, 42, 84);
        insertCompletedExam(adminId, 50, 100);
        insertCompletedExam(moderatorId, 49, 98);
        insertCompletedExam(studentWithOneExamId, 35, 70);
        insertIncompleteExam(studentId);
        insertCompletedRandomExam(studentId, 40, 80);
        insertCompletedSignExam(studentId, 7, 70);
        insertTheoryQuestionActivity(studentWithNoExamsId);
    }

    @Test
    void v29CreatesHistoricalLocaleAndIdempotencyColumns() {
        assertThat(jdbc.queryForList("""
                SELECT column_name FROM information_schema.columns
                WHERE table_schema = 'public' AND table_name = 'sign_exam_results'
                  AND column_name IN ('language_code', 'submission_key')
                """, String.class)).containsExactlyInAnyOrder("language_code", "submission_key");

        assertThat(jdbc.queryForObject("""
                SELECT COUNT(*) FROM pg_constraint
                WHERE conname = 'uq_sign_exam_result_submission' AND contype = 'u'
                """, Integer.class)).isEqualTo(1);
    }

    @Test
    void studentQueriesExcludeAdminModeratorAndIncompleteAttempts() {
        assertThat(store.countStudents("")).isEqualTo(3);
        var students = store.findStudents("", 20, 0);
        assertThat(students).extracting(student -> student.userId())
                .containsExactlyInAnyOrder(studentId, studentWithOneExamId, studentWithNoExamsId);
        assertThat(students).filteredOn(student -> student.userId().equals(studentId)).singleElement()
                .satisfies(student -> {
                    assertThat(student.totalCompletedExams()).isEqualTo(3);
                    assertThat(student.averageExamScore()).isEqualTo(78);
                });
        assertThat(students).filteredOn(student -> student.userId().equals(studentWithOneExamId)).singleElement()
                .satisfies(student -> assertThat(student.totalCompletedExams()).isEqualTo(1));
        assertThat(students).filteredOn(student -> student.userId().equals(studentWithNoExamsId)).singleElement()
                .satisfies(student -> {
                    assertThat(student.totalCompletedExams()).isZero();
                    assertThat(student.averageExamScore()).isNull();
                    assertThat(student.lastActiveAt()).isNotNull();
                    assertThat(student.lastActivityType()).isEqualTo("THEORY_QUESTION");
                });
        assertThat(store.countExams(null)).isEqualTo(4);
        assertThat(store.findExams(null, 20, 0))
                .allSatisfy(exam -> assertThat(exam.userId()).isIn(studentId, studentWithOneExamId))
                .extracting(exam -> exam.examType())
                .containsExactly("TRAFFIC_SIGN_EXAM", "RANDOM_EXAM", "THEORY_EXAM", "THEORY_EXAM");
        assertThat(store.findExams(null, 1, 0)).singleElement()
                .satisfies(exam -> assertThat(exam.examType()).isEqualTo("TRAFFIC_SIGN_EXAM"));
        assertThat(store.findExams(null, 1, 1)).singleElement()
                .satisfies(exam -> assertThat(exam.examType()).isEqualTo("RANDOM_EXAM"));
    }

    private long insertUser(String username, String email, String role) {
        return jdbc.queryForObject("""
                INSERT INTO users (username, email, full_name, password_hash, role)
                VALUES (?, ?, ?, 'not-a-real-secret', ?) RETURNING id
                """, Long.class, username, email, username, role);
    }

    private void insertCompletedExam(long userId, int correct, double score) {
        jdbc.update("""
                INSERT INTO exam_simulations
                    (user_id, started_at, completed_at, expires_at, total_questions,
                     correct_answers, score_percentage, time_taken_seconds, status, language_code)
                VALUES (?, ?, ?, ?, 50, ?, ?, 900, 'COMPLETED', 'en')
                """, userId, LocalDateTime.now().minusMinutes(20), LocalDateTime.now().minusMinutes(5),
                LocalDateTime.now().plusMinutes(10), correct, score);
    }

    private void insertIncompleteExam(long userId) {
        jdbc.update("""
                INSERT INTO exam_simulations
                    (user_id, started_at, expires_at, total_questions, status)
                VALUES (?, ?, ?, 50, 'IN_PROGRESS')
                """, userId, LocalDateTime.now(), LocalDateTime.now().plusMinutes(30));
    }

    private void insertCompletedRandomExam(long userId, int correct, double score) {
        jdbc.update("""
                INSERT INTO sign_random_practice_sessions
                    (user_id, total_questions, answered_count, correct_count, passing_score,
                     score_pct, passed, status, started_at, expires_at, completed_at, language_code)
                VALUES (?, 50, 50, ?, 41, ?, false, 'COMPLETED', ?, ?, ?, 'nl')
                """, userId, correct, score, LocalDateTime.now().minusMinutes(18),
                LocalDateTime.now().plusMinutes(12), LocalDateTime.now().minusMinutes(3));
    }

    private void insertCompletedSignExam(long userId, int correct, double score) {
        Long signId = jdbc.queryForObject("SELECT id FROM road_signs ORDER BY id LIMIT 1", Long.class);
        jdbc.update("""
                INSERT INTO sign_exam_results
                    (user_id, sign_id, sign_code, exam_number, total_questions, answered_count,
                     correct_count, required_to_pass, score_pct, passed, question_results_json,
                     completed_at, language_code)
                VALUES (?, ?, 'A11', 1, 10, 10, ?, 8, ?, false, '[]', ?, 'fr')
                """, userId, signId, correct, score, LocalDateTime.now().minusMinutes(1));
    }

    private void insertTheoryQuestionActivity(long userId) {
        jdbc.update("""
                INSERT INTO user_question_history
                    (user_id, question_ref_id, question_type, last_presented_at,
                     answered_at, times_presented, times_shown)
                VALUES (?, 900001, 'THEORY', ?, ?, 1, 1)
                """, userId, LocalDateTime.now().minusMinutes(2), LocalDateTime.now().minusMinutes(1));
    }
}
