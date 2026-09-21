package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.readyroad.readyroadbackend.dto.exam.ExamAccessMode;
import com.readyroad.readyroadbackend.dto.exam.ExamAccessState;
import com.readyroad.readyroadbackend.dto.exam.ExamQuestionDTO;
import com.readyroad.readyroadbackend.dto.exam.ExamStartResponse;
import com.readyroad.readyroadbackend.exception.FreeExamLimitReachedException;
import com.readyroad.readyroadbackend.payment.EntitlementStatus;
import com.readyroad.readyroadbackend.payment.UserEntitlement;
import com.readyroad.readyroadbackend.payment.UserEntitlementRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
class FreeExamPreviewPostgreSqlIntegrationTest {

    private static final List<Long> FIXED_PREVIEW_IDS =
            List.of(
                    127L,
                    60L,
                    62L,
                    364L,
                    220L,
                    20L,
                    136L,
                    41L,
                    428L,
                    65L);

    /**
     * PostgreSQL migration fixtures currently expose TH01..TH08.
     *
     * The production preview IDs remain exactly the approved ten IDs.
     * Category assignment here is test-fixture inventory only; this
     * integration test verifies preview ordering/access/paywall lifecycle,
     * not production taxonomy classification.
     *
     * TH01 and TH02 intentionally receive two preview IDs so every
     * migration-backed category still has enough inventory for the
     * 50-question allocator.
     */
    private static final List<PreviewSeed> PREVIEW_SEEDS =
            List.of(
                    new PreviewSeed("TH01", 127L),
                    new PreviewSeed("TH02", 60L),
                    new PreviewSeed("TH03", 62L),
                    new PreviewSeed("TH01", 364L),
                    new PreviewSeed("TH04", 220L),
                    new PreviewSeed("TH05", 20L),
                    new PreviewSeed("TH06", 136L),
                    new PreviewSeed("TH07", 41L),
                    new PreviewSeed("TH02", 428L),
                    new PreviewSeed("TH08", 65L));

    @Container
    static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer("postgres:17-alpine");

    @DynamicPropertySource
    static void properties(
            DynamicPropertyRegistry registry) {

        registry.add(
                "spring.datasource.url",
                POSTGRES::getJdbcUrl);

        registry.add(
                "spring.datasource.username",
                POSTGRES::getUsername);

        registry.add(
                "spring.datasource.password",
                POSTGRES::getPassword);

        registry.add(
                "spring.flyway.url",
                POSTGRES::getJdbcUrl);

        registry.add(
                "spring.flyway.user",
                POSTGRES::getUsername);

        registry.add(
                "spring.flyway.password",
                POSTGRES::getPassword);

        registry.add(
                "readyroad.marketing.enabled",
                () -> "false");

        registry.add(
                "jwt.secret-key",
                () -> "cGhhc2UtM2EtdGVzdC1vbmx5LWp3dC1zZWNyZXQtbm90LWZvci1wcm9kdWN0aW9u");

        registry.add(
                "readyroad.admin.default-password",
                () -> "Phase-3A-Test-Only-2026!");
    }

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    ExamService examService;

    @Autowired
    UserEntitlementRepository entitlementRepository;

    private long userId;

    @BeforeEach
    void setUp() {

        jdbc.execute("""
                TRUNCATE
                    user_entitlement,
                    exam_simulation_answers,
                    exam_simulation_questions,
                    exam_simulations,
                    user_question_history,
                    quiz_answer_options,
                    quiz_questions,
                    users
                RESTART IDENTITY CASCADE
                """);

        userId =
                jdbc.queryForObject("""
                        INSERT INTO users
                            (
                                username,
                                email,
                                full_name,
                                password_hash,
                                role,
                                preferred_language
                            )
                        VALUES
                            (
                                'free-preview-user',
                                'free-preview@test.local',
                                'Free Preview User',
                                'offline-test-password',
                                'USER',
                                'en'
                            )
                        RETURNING id
                        """,
                        Long.class);

        seedTheoryQuestionBank();
    }

    @Test
    void migration67IsAppliedWithPreviewTimingColumns() {

        assertThat(
                jdbc.queryForObject("""
                        SELECT COUNT(*)
                        FROM flyway_schema_history
                        WHERE version = '67'
                          AND success = true
                        """,
                        Integer.class))
                .isOne();

        assertThat(
                jdbc.queryForObject("""
                        SELECT COUNT(*)
                        FROM information_schema.columns
                        WHERE table_schema = current_schema()
                          AND table_name = 'exam_simulations'
                          AND column_name IN (
                              'preview_attempt',
                              'paywall_reached_at',
                              'full_access_resumed_at',
                              'paywall_paused_seconds'
                          )
                        """,
                        Integer.class))
                .isEqualTo(4);
    }

    @Test
    void freePreviewUpgradesAndResumesSameExamAtQuestionEleven() {

        // ========================================================
        // FREE start
        // ========================================================

        ExamStartResponse started =
                examService.startExamResponse(userId);

        assertThat(started.getExamId())
                .isNotNull();

        Long examId =
                started.getExamId();

        assertThat(started.getTotalQuestions())
                .isEqualTo(50);

        assertThat(started.getAccessMode())
                .isEqualTo(
                        ExamAccessMode.PREVIEW);

        assertThat(started.getAccessState())
                .isEqualTo(
                        ExamAccessState.PREVIEW_ACTIVE);

        assertThat(started.getFreeQuestionLimit())
                .isEqualTo(10);

        assertThat(started.getResumeQuestionOrder())
                .isEqualTo(1);

        assertThat(started.getQuestions())
                .hasSize(10);

        assertThat(
                started.getQuestions()
                        .stream()
                        .map(ExamQuestionDTO::getQuestionId)
                        .toList())
                .containsExactlyElementsOf(
                        FIXED_PREVIEW_IDS);

        assertThat(
                jdbc.queryForObject("""
                        SELECT COUNT(*)
                        FROM exam_simulation_questions
                        WHERE exam_id = ?
                        """,
                        Integer.class,
                        examId))
                .isEqualTo(50);

        assertThat(
                jdbc.queryForObject("""
                        SELECT preview_attempt
                        FROM exam_simulations
                        WHERE id = ?
                        """,
                        Boolean.class,
                        examId))
                .isTrue();


        // ========================================================
        // Finalize Q1-Q10 using timeout path
        // ========================================================

        for (Long questionId : FIXED_PREVIEW_IDS) {
            examService.recordQuestionTimeout(
                    examId,
                    questionId,
                    userId);
        }

        ExamStartResponse paywall =
                examService.getActiveExamResponse(
                        userId);

        assertThat(paywall)
                .isNotNull();

        assertThat(paywall.getExamId())
                .isEqualTo(examId);

        assertThat(paywall.getAccessMode())
                .isEqualTo(
                        ExamAccessMode.PREVIEW);

        assertThat(paywall.getAccessState())
                .isEqualTo(
                        ExamAccessState.FREE_LIMIT_REACHED);

        assertThat(paywall.getResumeQuestionOrder())
                .isEqualTo(11);

        assertThat(paywall.getQuestions())
                .hasSize(10);

        assertThat(paywall.getFinalizedQuestionIds())
                .containsExactlyElementsOf(
                        FIXED_PREVIEW_IDS);

        Map<String, Object> pausedState =
                jdbc.queryForMap("""
                        SELECT
                            status,
                            preview_attempt,
                            paywall_reached_at,
                            full_access_resumed_at
                        FROM exam_simulations
                        WHERE id = ?
                        """,
                        examId);

        assertThat(pausedState.get("status"))
                .isEqualTo("IN_PROGRESS");

        assertThat(pausedState.get("preview_attempt"))
                .isEqualTo(true);

        assertThat(pausedState.get("paywall_reached_at"))
                .isNotNull();

        assertThat(pausedState.get("full_access_resumed_at"))
                .isNull();


        // ========================================================
        // Q11 cannot leak to FREE learner
        // ========================================================

        Long questionElevenId =
                jdbc.queryForObject("""
                        SELECT question_id
                        FROM exam_simulation_questions
                        WHERE exam_id = ?
                          AND question_order = 11
                        """,
                        Long.class,
                        examId);

        assertThat(questionElevenId)
                .isNotNull();

        assertThat(
                FIXED_PREVIEW_IDS)
                .doesNotContain(
                        questionElevenId);

        assertThatThrownBy(
                () -> examService.recordQuestionPresented(
                        examId,
                        questionElevenId,
                        userId))
                .isInstanceOf(
                        FreeExamLimitReachedException.class);


        // ========================================================
        // Simulate checkout taking longer than the original timer
        // ========================================================

        jdbc.update("""
                UPDATE exam_simulations
                SET paywall_reached_at =
                    paywall_reached_at - INTERVAL '15 minutes',
                    expires_at =
                        expires_at - INTERVAL '15 minutes'
                WHERE id = ?
                """,
                examId);

        ExamStartResponse stillPaused =
                examService.getActiveExamResponse(
                        userId);

        assertThat(stillPaused)
                .isNotNull();

        assertThat(stillPaused.getExamId())
                .isEqualTo(examId);

        assertThat(stillPaused.getAccessState())
                .isEqualTo(
                        ExamAccessState.FREE_LIMIT_REACHED);

        assertThat(
                jdbc.queryForObject("""
                        SELECT status
                        FROM exam_simulations
                        WHERE id = ?
                        """,
                        String.class,
                        examId))
                .isEqualTo("IN_PROGRESS");


        // ========================================================
        // Simulate successful Stripe entitlement activation
        // ========================================================

        UserEntitlement entitlement =
                new UserEntitlement();

        entitlement.setUserId(userId);
        entitlement.setStatus(
                EntitlementStatus.ACTIVE);

        entitlement.setExpiresAt(
                Instant.now()
                        .plus(
                                3,
                                ChronoUnit.DAYS));

        entitlementRepository.saveAndFlush(
                entitlement);


        // ========================================================
        // First paid resume: SAME exam, Q11, 600 seconds
        // ========================================================

        Instant beforeResume =
                Instant.now();

        ExamStartResponse paid =
                examService.getActiveExamResponse(
                        userId);

        Instant afterResume =
                Instant.now();

        assertThat(paid)
                .isNotNull();

        assertThat(paid.getExamId())
                .isEqualTo(examId);

        assertThat(paid.getAccessMode())
                .isEqualTo(
                        ExamAccessMode.FULL);

        assertThat(paid.getAccessState())
                .isEqualTo(
                        ExamAccessState.FULL_ACTIVE);

        assertThat(paid.getTotalQuestions())
                .isEqualTo(50);

        assertThat(paid.getQuestions())
                .hasSize(50);

        assertThat(paid.getResumeQuestionOrder())
                .isEqualTo(11);

        assertThat(paid.getFinalizedQuestionIds())
                .containsExactlyElementsOf(
                        FIXED_PREVIEW_IDS);

        assertThat(paid.getExpiresAt())
                .isAfterOrEqualTo(
                        beforeResume.plusSeconds(599));

        assertThat(paid.getExpiresAt())
                .isBeforeOrEqualTo(
                        afterResume.plusSeconds(600));

        Instant firstPaidExpiry =
                paid.getExpiresAt();


        // ========================================================
        // Refresh cannot grant another 600 seconds
        // ========================================================

        ExamStartResponse refreshed =
                examService.getActiveExamResponse(
                        userId);

        assertThat(refreshed)
                .isNotNull();

        assertThat(refreshed.getExamId())
                .isEqualTo(examId);

        assertThat(refreshed.getExpiresAt())
                .isEqualTo(
                        firstPaidExpiry);

        Map<String, Object> resumedState =
                jdbc.queryForMap("""
                        SELECT
                            status,
                            preview_attempt,
                            paywall_reached_at,
                            full_access_resumed_at
                        FROM exam_simulations
                        WHERE id = ?
                        """,
                        examId);

        assertThat(resumedState.get("status"))
                .isEqualTo("IN_PROGRESS");

        assertThat(resumedState.get("preview_attempt"))
                .isEqualTo(true);

        assertThat(resumedState.get("paywall_reached_at"))
                .isNotNull();

        assertThat(resumedState.get("full_access_resumed_at"))
                .isNotNull();
    }

    private void seedTheoryQuestionBank() {

        int generatedId =
                1000;

        for (PreviewSeed seed : PREVIEW_SEEDS) {

            Long categoryId =
                    jdbc.queryForObject("""
                            SELECT id
                            FROM categories
                            WHERE code = ?
                              AND is_active = true
                              AND content_scope IN (
                                  'THEORETICAL_EXAM',
                                  'BOTH'
                              )
                            """,
                            Long.class,
                            seed.categoryCode());

            for (int slot = 0; slot < 6; slot++) {

                long questionId;

                if (slot == 0) {
                    questionId =
                            seed.questionId();
                } else {
                    questionId =
                            generatedId++;
                }

                String difficulty =
                        switch (slot % 3) {
                            case 0 -> "EASY";
                            case 1 -> "MEDIUM";
                            default -> "HARD";
                        };

                insertQuestion(
                        questionId,
                        categoryId,
                        difficulty);
            }
        }

        assertThat(
                jdbc.queryForObject("""
                        SELECT COUNT(*)
                        FROM quiz_questions
                        WHERE is_active = true
                          AND status = 'PUBLISHED'
                        """,
                        Integer.class))
                .isEqualTo(60);
    }

    private void insertQuestion(
            long questionId,
            long categoryId,
            String difficulty) {

        jdbc.update("""
                INSERT INTO quiz_questions
                    (
                        id,
                        question_ar,
                        question_en,
                        question_nl,
                        question_fr,
                        question_type,
                        difficulty_level,
                        category_id,
                        is_active,
                        status,
                        published_at
                    )
                VALUES
                    (
                        ?,
                        ?,
                        ?,
                        ?,
                        ?,
                        'MULTIPLE_CHOICE',
                        ?,
                        ?,
                        true,
                        'PUBLISHED',
                        CURRENT_TIMESTAMP
                    )
                """,
                questionId,
                "سؤال " + questionId,
                "Driving question " + questionId,
                "Verkeersvraag " + questionId,
                "Question de circulation " + questionId,
                difficulty,
                categoryId);

        jdbc.update("""
                INSERT INTO quiz_answer_options
                    (
                        question_id,
                        display_order,
                        option_text_ar,
                        option_text_en,
                        option_text_nl,
                        option_text_fr,
                        is_correct,
                        is_active,
                        created_at
                    )
                VALUES
                    (
                        ?,
                        1,
                        'الإجابة الصحيحة',
                        'Correct answer',
                        'Correct antwoord',
                        'Bonne réponse',
                        true,
                        true,
                        CURRENT_TIMESTAMP
                    ),
                    (
                        ?,
                        2,
                        'إجابة أخرى',
                        'Another answer',
                        'Ander antwoord',
                        'Autre réponse',
                        false,
                        true,
                        CURRENT_TIMESTAMP
                    )
                """,
                questionId,
                questionId);
    }

    private record PreviewSeed(
            String categoryCode,
            long questionId) {
    }
}
