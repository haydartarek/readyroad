package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import com.readyroad.readyroadbackend.dto.exam.ExamResultsDTO;
import org.junit.jupiter.api.AfterEach;
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
class ExamCompletionConcurrencyPostgreSqlIntegrationTest {

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
                () -> "cGhhc2UtNy10ZXN0LW9ubHktand0LXNlY3JldC1ub3QtZm9yLXByb2R1Y3Rpb24=");
        registry.add("readyroad.admin.default-password", () -> "Phase-7-Test-Only-2026!");
    }

    @Autowired JdbcTemplate jdbc;
    @Autowired ExamService examService;
    @Autowired AdminLearningStore adminLearningStore;
    @Autowired TheoryTimeoutAnalysisService timeoutAnalysisService;

    private ExecutorService executor;
    private long userId;
    private long categoryId;
    private long questionId;
    private long examId;

    @BeforeEach
    void setUp() {
        executor = Executors.newFixedThreadPool(2);
        jdbc.execute("TRUNCATE users RESTART IDENTITY CASCADE");

        categoryId = jdbc.queryForObject("""
                SELECT id FROM categories
                WHERE is_active = true
                  AND content_scope IN ('THEORETICAL_EXAM', 'BOTH')
                ORDER BY id LIMIT 1
                """, Long.class);
        userId = jdbc.queryForObject("""
                INSERT INTO users (username, email, full_name, password_hash, role, preferred_language)
                VALUES ('phase7-user', 'phase7@test.local', 'Phase 7 User', 'not-a-secret', 'USER', 'en')
                RETURNING id
                """, Long.class);
        examId = jdbc.queryForObject("""
                INSERT INTO exam_simulations
                    (user_id, started_at, expires_at, total_questions, status)
                VALUES (?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '750 seconds', 50, 'IN_PROGRESS')
                RETURNING id
                """, Long.class, userId);
        for (int order = 1; order <= 50; order++) {
            long currentQuestionId = jdbc.queryForObject("""
                    INSERT INTO quiz_questions
                        (question_ar, question_en, question_nl, question_fr, question_type,
                         difficulty_level, category_id, is_active, status, published_at)
                    VALUES (?, ?, ?, ?, 'MULTIPLE_CHOICE',
                            'EASY', ?, true, 'PUBLISHED', CURRENT_TIMESTAMP)
                    RETURNING id
                    """, Long.class,
                    "سؤال " + order, "Question " + order, "Vraag " + order,
                    "Question " + order, categoryId);
            if (order == 1) {
                questionId = currentQuestionId;
            }
            long selectedOptionId = insertOption(currentQuestionId, "Correct", true, 1);
            insertOption(currentQuestionId, "Wrong", false, 2);
            jdbc.update("""
                    INSERT INTO exam_simulation_questions (exam_id, question_id, question_order)
                    VALUES (?, ?, ?)
                    """, examId, currentQuestionId, order);
            jdbc.update("""
                    INSERT INTO exam_simulation_answers
                        (exam_id, question_id, selected_option_id, correct_option_id,
                         is_correct, time_taken_seconds, answered_at)
                    VALUES (?, ?, ?, ?, true, 5, CURRENT_TIMESTAMP)
                    """, examId, currentQuestionId, selectedOptionId, selectedOptionId);
        }
    }

    @AfterEach
    void shutDownExecutor() {
        executor.shutdownNow();
    }

    @Test
    void concurrentCompletionRequestsConvergeOnOnePersistedCompletion() throws Exception {
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        Future<?> first = executor.submit(() -> completeAfterBarrier(ready, start));
        Future<?> second = executor.submit(() -> completeAfterBarrier(ready, start));
        ready.await();
        start.countDown();

        first.get();
        second.get();

        assertThat(jdbc.queryForObject(
                "SELECT status FROM exam_simulations WHERE id = ?", String.class, examId))
                .isEqualTo("COMPLETED");
        assertThat(jdbc.queryForObject(
                "SELECT COUNT(*) FROM exam_simulations WHERE id = ? AND status = 'COMPLETED'",
                Integer.class, examId)).isEqualTo(1);
        assertThat(jdbc.queryForObject(
                "SELECT COUNT(*) FROM exam_simulation_answers WHERE exam_id = ?",
                Integer.class, examId)).isEqualTo(50);
        assertThat(jdbc.queryForObject("""
                SELECT questions_attempted FROM user_category_progress
                WHERE user_id = ? AND category_id = ?
                """, Integer.class, userId, categoryId)).isEqualTo(50);
        assertThat(jdbc.queryForObject("""
                SELECT times_correct FROM user_question_history
                WHERE user_id = ? AND question_ref_id = ?
                """, Integer.class, userId, questionId)).isEqualTo(1);
    }

    @Test
    void timedOutQuestionRemainsUnansweredAndDoesNotPolluteAccuracy() {
        jdbc.update("DELETE FROM exam_simulation_answers WHERE exam_id = ? AND question_id = ?", examId, questionId);

        examService.recordQuestionTimeout(examId, questionId, userId);
        examService.recordQuestionTimeout(examId, questionId, userId);
        assertThat(timeoutAnalysisService.getAnalysis(userId, 10).totalTimeouts()).isZero();
        examService.completeExam(examId, userId);

        ExamResultsDTO results = examService.getExamResults(examId, userId);
        assertThat(results.getTotalQuestions()).isEqualTo(50);
        assertThat(results.getCorrectAnswers()).isEqualTo(49);
        assertThat(results.getWrongAnswers()).isZero();
        assertThat(results.getAnsweredCount()).isEqualTo(49);
        assertThat(results.getUnansweredCount()).isEqualTo(1);
        assertThat(results.getAllAnswers()).hasSize(50);
        assertThat(results.getAllAnswers()).filteredOn(answer -> Boolean.TRUE.equals(answer.getWasTimeout()))
                .singleElement()
                .satisfies(answer -> {
                    assertThat(answer.getSelectedOptionId()).isNull();
                    assertThat(answer.getCorrectOptionId()).isNotNull();
                    assertThat(answer.getQuestionId()).isEqualTo(questionId);
                });
        assertThat(timeoutAnalysisService.getAnalysis(userId, 10).totalTimeouts()).isEqualTo(1);
        assertThat(timeoutAnalysisService.getAnalysis(userId, 10).items())
                .singleElement()
                .satisfies(item -> {
                    assertThat(item.examId()).isEqualTo(examId);
                    assertThat(item.questionId()).isEqualTo(questionId);
                    assertThat(item.reviewPath()).isEqualTo("/exam/results/" + examId);
                });
        var adminSummary = adminLearningStore.findExam(userId, "THEORY_EXAM", examId);
        assertThat(adminSummary.answeredQuestions()).isEqualTo(49);
        assertThat(adminSummary.correctAnswers()).isEqualTo(49);
        assertThat(adminSummary.incorrectAnswers()).isZero();
        assertThat(adminSummary.unansweredAnswers()).isEqualTo(1);

        assertThat(jdbc.queryForObject("""
                SELECT COUNT(*) FROM exam_simulation_answers
                WHERE exam_id = ? AND answer_state = 'TIMED_OUT' AND selected_option_id IS NULL
                """, Integer.class, examId)).isEqualTo(1);
        assertThat(jdbc.queryForObject("""
                SELECT questions_attempted FROM user_category_progress
                WHERE user_id = ? AND category_id = ?
                """, Integer.class, userId, categoryId)).isEqualTo(49);
        assertThat(jdbc.queryForObject("""
                SELECT times_presented FROM user_question_history
                WHERE user_id = ? AND question_ref_id = ?
                """, Integer.class, userId, questionId)).isEqualTo(1);
        assertThat(jdbc.queryForObject("""
                SELECT times_correct + times_wrong FROM user_question_history
                WHERE user_id = ? AND question_ref_id = ?
                """, Integer.class, userId, questionId)).isZero();
    }

    @Test
    void severalTimedOutQuestionsPreserveTheResultInvariant() {
        var timedOutQuestionIds = jdbc.queryForList("""
                SELECT question_id FROM exam_simulation_questions
                WHERE exam_id = ? ORDER BY question_order LIMIT 3
                """, Long.class, examId);
        timedOutQuestionIds.forEach(timedOutQuestionId -> {
            jdbc.update("DELETE FROM exam_simulation_answers WHERE exam_id = ? AND question_id = ?",
                    examId, timedOutQuestionId);
            examService.recordQuestionTimeout(examId, timedOutQuestionId, userId);
        });

        examService.completeExam(examId, userId);

        ExamResultsDTO results = examService.getExamResults(examId, userId);
        assertThat(results.getCorrectAnswers()).isEqualTo(47);
        assertThat(results.getWrongAnswers()).isZero();
        assertThat(results.getAnsweredCount()).isEqualTo(47);
        assertThat(results.getUnansweredCount()).isEqualTo(3);
        assertThat(results.getCorrectAnswers() + results.getWrongAnswers()
                + results.getUnansweredCount()).isEqualTo(results.getTotalQuestions());
        assertThat(results.getAllAnswers())
                .filteredOn(answer -> Boolean.TRUE.equals(answer.getWasTimeout()))
                .hasSize(3);
        assertThat(timeoutAnalysisService.getAnalysis(userId, 10).totalTimeouts()).isEqualTo(3);
    }

    @Test
    void abandonedAttemptRetainsTimeoutEvidenceButStaysOutOfCompletedAnalytics() {
        jdbc.update("DELETE FROM exam_simulation_answers WHERE exam_id = ? AND question_id = ?", examId, questionId);
        examService.recordQuestionTimeout(examId, questionId, userId);

        examService.cancelExam(examId, userId);

        assertThat(jdbc.queryForObject(
                "SELECT status FROM exam_simulations WHERE id = ?", String.class, examId))
                .isEqualTo("ABANDONED");
        assertThat(jdbc.queryForObject("""
                SELECT COUNT(*) FROM exam_simulation_answers
                WHERE exam_id = ? AND answer_state = 'TIMED_OUT'
                """, Integer.class, examId)).isEqualTo(1);
        assertThat(timeoutAnalysisService.getAnalysis(userId, 10).totalTimeouts()).isZero();
        assertThat(adminLearningStore.findExam(userId, "THEORY_EXAM", examId)).isNull();
    }

    private void completeAfterBarrier(CountDownLatch ready, CountDownLatch start) {
        ready.countDown();
        try {
            start.await();
            examService.completeExam(examId, userId);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(exception);
        }
    }

    private long insertOption(long optionQuestionId, String text, boolean correct, int displayOrder) {
        return jdbc.queryForObject("""
                INSERT INTO quiz_answer_options
                    (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr,
                     is_correct, display_order, is_active)
                VALUES (?, ?, ?, ?, ?, ?, ?, true)
                RETURNING id
                """, Long.class, optionQuestionId, text, text, text, text, correct, displayOrder);
    }
}
