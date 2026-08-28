package com.readyroad.readyroadbackend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
class TheoryQuestionCooldownEligibilityPostgreSqlIntegrationTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 8, 15, 12, 0);
    private static final LocalDateTime CUTOFF = NOW.minusHours(8);

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
                () -> "cGhhc2UtM2ItdGVzdC1vbmx5LWp3dC1zZWNyZXQtbm90LWZvci1wcm9kdWN0aW9u");
        registry.add("readyroad.admin.default-password", () -> "Phase-3B-Test-Only-2026!");
    }

    @Autowired JdbcTemplate jdbc;
    @Autowired QuizQuestionRepository questionRepository;

    private long categoryId;
    private long userA;
    private long userB;

    @BeforeEach
    void setUp() {
        jdbc.execute("""
                TRUNCATE exam_simulation_answers, exam_simulation_questions, exam_simulations,
                         user_question_history, quiz_answer_options, quiz_questions, users
                RESTART IDENTITY CASCADE
                """);

        categoryId = jdbc.queryForObject("""
                SELECT id
                FROM categories
                WHERE is_active = true
                  AND content_scope IN ('THEORETICAL_EXAM', 'BOTH')
                ORDER BY id
                LIMIT 1
                """, Long.class);
        userA = insertUser("cooldown-a", "cooldown-a@test.local", "ar");
        userB = insertUser("cooldown-b", "cooldown-b@test.local", "en");
    }

    @Test
    void cooldownIsUserSpecificAndUsesTheExactEightHourBoundary() {
        long recent = insertQuestion("Recent at 7h59m");
        long exactBoundary = insertQuestion("Exactly eight hours old");
        long unseen = insertQuestion("Never presented");

        insertHistory(userA, recent, "THEORY", NOW.minusHours(7).minusMinutes(59), NOW.minusHours(7).minusMinutes(59));
        insertHistory(userA, exactBoundary, "THEORY", CUTOFF, CUTOFF);

        assertThat(eligibleIds(userA))
                .containsExactlyInAnyOrder(exactBoundary, unseen)
                .doesNotContain(recent);
        assertThat(eligibleIds(userB))
                .containsExactlyInAnyOrder(recent, exactBoundary, unseen);
    }

    @Test
    void unknownLegacyPresentationRemainsEligibleWithoutInventingATimestamp() {
        long legacy = insertQuestion("Legacy unknown presentation");
        insertHistory(userA, legacy, "PRACTICE", null, NOW.minusMinutes(5));

        assertThat(eligibleIds(userA)).containsExactly(legacy);
        assertThat(jdbc.queryForObject("""
                SELECT last_presented_at
                FROM user_question_history
                WHERE user_id = ? AND question_ref_id = ?
                """, LocalDateTime.class, userA, legacy)).isNull();
    }

    @Test
    void languageChangeDoesNotBypassOrAlterTheCooldown() {
        long recent = insertQuestion("Language independent cooldown");
        insertHistory(userA, recent, "THEORY", NOW.minusHours(1), NOW.minusHours(1));

        assertThat(eligibleIds(userA)).doesNotContain(recent);
        jdbc.update("UPDATE users SET preferred_language = 'fr' WHERE id = ?", userA);
        assertThat(eligibleIds(userA)).doesNotContain(recent);
    }

    @Test
    void nonTheoryHistoryDoesNotBlockATheoryQuestionWithTheSameNumericId() {
        long questionId = insertQuestion("Traffic-sign history isolation");
        insertHistory(userA, questionId, "SIGN", NOW.minusMinutes(1), NOW.minusMinutes(1));

        assertThat(eligibleIds(userA)).containsExactly(questionId);
    }

    @Test
    void neverPresentedQuestionsSortAheadOfExpiredCooldownQuestions() {
        long expired = insertQuestion("Expired cooldown");
        long unseen = insertQuestion("Unseen priority");
        insertHistory(userA, expired, "THEORY", CUTOFF.minusMinutes(1), CUTOFF.minusMinutes(1));

        List<Long> orderedIds = eligibleQuestionIds(userA);

        assertThat(orderedIds).containsExactly(unseen, expired);
    }

    @Test
    void previouslyPresentedQuestionsSortByOldestExposureBeforeRandomTieBreaking() {
        long newest = insertQuestion("Newest eligible exposure");
        long oldest = insertQuestion("Oldest eligible exposure");
        insertHistory(userA, newest, "THEORY", CUTOFF.minusMinutes(1), CUTOFF.minusMinutes(1));
        insertHistory(userA, oldest, "THEORY", CUTOFF.minusDays(2), CUTOFF.minusDays(2));

        assertThat(eligibleQuestionIds(userA)).containsExactly(oldest, newest);
    }

    @Test
    void rankedFallbackIncludesCoolingQuestionsAndOrdersOldestExposureFirst() {
        long newest = insertQuestion("Newest cooling question");
        long oldest = insertQuestion("Oldest cooling question");

        insertHistory(userA, newest, "THEORY", NOW.minusHours(1), NOW.minusHours(1));
        insertHistory(userA, oldest, "THEORY", NOW.minusHours(7), NOW.minusHours(7));

        assertThat(eligibleQuestionIds(userA))
                .doesNotContain(newest, oldest);

        assertThat(rankedTheoryQuestionIds(userA, "en"))
                .containsExactly(oldest, newest);
    }
    @Test
    void bankCandidatesRemainIndependentFromPerUserCooldownAvailability() {
        long blockedForUser = insertQuestion("Bank eligible but cooling down");
        long availableForUser = insertQuestion("Bank and user eligible");
        insertHistory(userA, blockedForUser, "THEORY", CUTOFF.plusHours(1), CUTOFF.plusHours(1));

        List<Long> bankIds = questionRepository.findTheoryQuestionBankCandidates("en").stream()
                .map(QuizQuestion::getId)
                .toList();
        List<Long> userIds = eligibleQuestionIds(userA);

        assertThat(bankIds).contains(blockedForUser, availableForUser);
        assertThat(userIds).contains(availableForUser).doesNotContain(blockedForUser);
    }

    @Test
    void bankEligibilityUsesTheRequestedLocaleWithoutBorrowingAnotherTranslation() {
        long questionId = insertQuestion("Locale-specific question");
        jdbc.update("UPDATE quiz_questions SET question_en = 'Option A' WHERE id = ?", questionId);

        List<Long> arabicIds = questionRepository.findTheoryQuestionBankCandidates("ar").stream()
                .map(QuizQuestion::getId)
                .toList();
        List<Long> englishIds = questionRepository.findTheoryQuestionBankCandidates("en").stream()
                .map(QuizQuestion::getId)
                .toList();

        assertThat(arabicIds).contains(questionId);
        assertThat(englishIds).doesNotContain(questionId);
    }

    private List<Long> rankedTheoryQuestionIds(long userId, String languageCode) {
        return questionRepository.findRankedTheoryQuestionsForUser(userId, languageCode)
                .stream()
                .map(QuizQuestion::getId)
                .toList();
    }
    private Set<Long> eligibleIds(long userId) {
        return eligibleQuestionIds(userId).stream().collect(Collectors.toSet());
    }

    private List<Long> eligibleQuestionIds(long userId) {
        return questionRepository.findCooldownEligibleQuestionsByDifficulty(
                        userId,
                        QuizQuestion.DifficultyLevel.EASY,
                        CUTOFF)
                .stream()
                .map(QuizQuestion::getId)
                .toList();
    }

    private long insertUser(String username, String email, String language) {
        return jdbc.queryForObject("""
                INSERT INTO users (username, email, full_name, password_hash, role, preferred_language)
                VALUES (?, ?, ?, 'not-a-secret', 'USER', ?)
                RETURNING id
                """, Long.class, username, email, username, language);
    }

    private long insertQuestion(String englishText) {
        long questionId = jdbc.queryForObject("""
                INSERT INTO quiz_questions
                    (question_ar, question_en, question_nl, question_fr, question_type,
                     difficulty_level, category_id, is_active, status, published_at)
                VALUES
                    (?, ?, ?, ?, 'MULTIPLE_CHOICE', 'EASY', ?, true, 'PUBLISHED', CURRENT_TIMESTAMP)
                RETURNING id
                """, Long.class, englishText, englishText, englishText, englishText, categoryId);
        jdbc.update("""
                INSERT INTO quiz_answer_options
                    (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr,
                     is_correct, display_order, is_active)
                VALUES
                    (?, 'Correct', 'Correct', 'Correct', 'Correct', true, 1, true),
                    (?, 'Incorrect', 'Incorrect', 'Incorrect', 'Incorrect', false, 2, true)
                """, questionId, questionId);
        return questionId;
    }

    private void insertHistory(
            long userId,
            long questionId,
            String questionType,
            LocalDateTime lastPresentedAt,
            LocalDateTime legacyLastShownAt) {
        jdbc.update("""
                INSERT INTO user_question_history
                    (user_id, question_id, question_type, question_ref_id,
                     last_presented_at, times_presented,
                     last_shown_at, last_shown_type, times_shown,
                     times_correct, times_wrong)
                VALUES (?, ?, ?, ?, ?, ?, ?, 'EXAM', ?, 0, 0)
                """,
                userId,
                questionId,
                questionType,
                questionId,
                lastPresentedAt,
                lastPresentedAt == null ? 0 : 1,
                legacyLastShownAt,
                legacyLastShownAt == null ? 0 : 1);
    }
}
