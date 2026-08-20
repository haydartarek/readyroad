package com.readyroad.readyroadbackend.repository;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.DevExamCategory;
import com.readyroad.readyroadbackend.domain.entity.DevExamDifficulty;
import com.readyroad.readyroadbackend.domain.entity.DevExamQuestion;
import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.DevExamCategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.DevExamQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import com.readyroad.readyroadbackend.domain.repository.UserQuestionHistoryRepository;
import com.readyroad.readyroadbackend.domain.repository.UserErrorPatternRepository;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.domain.repository.UserWeakAreaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
abstract class AbstractDatabasePortableRepositoryIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private RoadSignRepository roadSignRepository;

    @Autowired
    private DevExamCategoryRepository devExamCategoryRepository;

    @Autowired
    private DevExamQuestionRepository devExamQuestionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserQuestionHistoryRepository userQuestionHistoryRepository;

    @Autowired
    private UserErrorPatternRepository userErrorPatternRepository;

    @Autowired
    private UserWeakAreaRepository userWeakAreaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    private Category primaryCategory;
    private Category secondaryCategory;
    private DevExamCategory devExamCategory;
    private User user;
    private final Set<Long> eligibleQuestionIds = new HashSet<>();

    @BeforeEach
    void seedPortableQueryFixtures() {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        primaryCategory = categoryRepository.saveAndFlush(category("P" + suffix.substring(0, 4), 901));
        secondaryCategory = categoryRepository.saveAndFlush(category("S" + suffix.substring(0, 4), 902));

        eligibleQuestionIds.add(saveQuestion(primaryCategory, QuizQuestion.DifficultyLevel.EASY, true,
                QuizQuestion.QuestionStatus.PUBLISHED, "primary-easy-1").getId());
        eligibleQuestionIds.add(saveQuestion(primaryCategory, QuizQuestion.DifficultyLevel.EASY, true,
                QuizQuestion.QuestionStatus.PUBLISHED, "primary-easy-2").getId());
        eligibleQuestionIds.add(saveQuestion(primaryCategory, QuizQuestion.DifficultyLevel.MEDIUM, true,
                QuizQuestion.QuestionStatus.PUBLISHED, "primary-medium").getId());
        eligibleQuestionIds.add(saveQuestion(primaryCategory, QuizQuestion.DifficultyLevel.HARD, true,
                QuizQuestion.QuestionStatus.PUBLISHED, "primary-hard").getId());
        eligibleQuestionIds.add(saveQuestion(secondaryCategory, QuizQuestion.DifficultyLevel.EASY, true,
                QuizQuestion.QuestionStatus.PUBLISHED, "secondary-easy").getId());
        saveQuestion(primaryCategory, QuizQuestion.DifficultyLevel.EASY, true,
                QuizQuestion.QuestionStatus.DRAFT, "draft");
        saveQuestion(primaryCategory, QuizQuestion.DifficultyLevel.EASY, false,
                QuizQuestion.QuestionStatus.PUBLISHED, "inactive");

        devExamCategory = new DevExamCategory();
        devExamCategory.setSlug("portable-" + suffix);
        devExamCategory.setIcon("test");
        devExamCategory.setSortOrder(903);
        devExamCategory.setIsActive(true);
        devExamCategory = devExamCategoryRepository.saveAndFlush(devExamCategory);

        for (int index = 0; index < 4; index++) {
            saveDevExamQuestion(devExamCategory, DevExamDifficulty.BEGINNER, true, "beginner-" + index);
        }
        saveDevExamQuestion(devExamCategory, DevExamDifficulty.BEGINNER, false, "inactive");
        saveDevExamQuestion(devExamCategory, DevExamDifficulty.ADVANCED, true, "advanced");

        user = new User();
        user.setUsername("portable_" + suffix);
        user.setEmail("portable_" + suffix + "@readyroad.test");
        user.setFullName("Database Portability Test");
        user.setPasswordHash("not-used");
        user.setRole(Role.USER);
        user.setIsActive(true);
        user.setIsLocked(false);
        user = userRepository.saveAndFlush(user);

        entityManager.clear();
    }

    @Test
    void adminCatalogQueriesAcceptMissingOptionalFilters() {
        assertThat(roadSignRepository.findAdminSigns(null, null, PageRequest.of(0, 20)))
                .isNotNull();

        assertThat(quizQuestionRepository.findAdminQuestions(
                null, null, null, null, PageRequest.of(0, 20)))
                .isNotEmpty();
    }

    @Test
    void signErrorPatternInsertPopulatesRequiredPolymorphicReference() {
        Long questionId = eligibleQuestionIds.iterator().next();

        userErrorPatternRepository.insertSignError(
                user.getId(), "SIGN_CONFUSION", questionId, "PORTABLE");

        assertThat(userErrorPatternRepository.findAllByUserIdOrderByOccurredAtDesc(user.getId()))
                .singleElement()
                .satisfies(pattern -> {
                    assertThat(pattern.getQuestionType()).isEqualTo("PRACTICE");
                    assertThat(pattern.getQuestionRefType()).isEqualTo("SIGN");
                    assertThat(pattern.getQuestionRefId()).isEqualTo(questionId);
                    assertThat(pattern.getTrafficSignCode()).isEqualTo("PORTABLE");
                });
    }

    @Test
    void randomQuizQueriesPreserveFiltersLimitsAndUniqueness() {
        List<QuizQuestion> allRandom = quizQuestionRepository.findRandomQuestions();
        assertThat(allRandom)
                .isNotEmpty()
                .allSatisfy(this::assertPublishedAndActive);
        assertUniqueQuestionIds(allRandom);

        List<Long> limitedIds = quizQuestionRepository.findRandomQuestionIds(3);
        assertThat(limitedIds).hasSize(3).doesNotHaveDuplicates();
        quizQuestionRepository.findAllById(limitedIds).forEach(this::assertPublishedAndActive);

        List<QuizQuestion> categoryQuestions = quizQuestionRepository
                .findRandomQuestionsByCategory(primaryCategory.getId());
        assertThat(categoryQuestions).hasSize(4).allSatisfy(question -> {
            assertPublishedAndActive(question);
            assertThat(question.getCategory().getId()).isEqualTo(primaryCategory.getId());
        });
        assertUniqueQuestionIds(categoryQuestions);

        List<Long> categoryIds = quizQuestionRepository.findRandomQuestionIdsByCategory(primaryCategory.getId(), 2);
        assertThat(categoryIds).hasSize(2).doesNotHaveDuplicates();
        quizQuestionRepository.findAllById(categoryIds).forEach(question -> {
            assertPublishedAndActive(question);
            assertThat(question.getCategory().getId()).isEqualTo(primaryCategory.getId());
        });

        List<QuizQuestion> easyQuestions = quizQuestionRepository
                .findRandomQuestionsByDifficulty(QuizQuestion.DifficultyLevel.EASY);
        assertThat(easyQuestions).isNotEmpty().allSatisfy(question -> {
            assertPublishedAndActive(question);
            assertThat(question.getDifficultyLevel()).isEqualTo(QuizQuestion.DifficultyLevel.EASY);
        });
        assertUniqueQuestionIds(easyQuestions);

        List<Long> easyIds = quizQuestionRepository.findRandomQuestionIdsByDifficulty("EASY", 2);
        assertThat(easyIds).hasSize(2).doesNotHaveDuplicates();
        quizQuestionRepository.findAllById(easyIds).forEach(question -> {
            assertPublishedAndActive(question);
            assertThat(question.getDifficultyLevel()).isEqualTo(QuizQuestion.DifficultyLevel.EASY);
        });
    }

    @Test
    void devExamRandomQueryPreservesCategoryDifficultyLimitAndUniqueness() {
        List<DevExamQuestion> questions = devExamQuestionRepository.findRandomByCategoryAndDifficulty(
                devExamCategory.getId(), DevExamDifficulty.BEGINNER, PageRequest.of(0, 2));

        assertThat(questions).hasSize(2);
        assertThat(questions).extracting(DevExamQuestion::getId).doesNotHaveDuplicates();
        assertThat(questions).allSatisfy(question -> {
            assertThat(question.getCategory().getId()).isEqualTo(devExamCategory.getId());
            assertThat(question.getDifficulty()).isEqualTo(DevExamDifficulty.BEGINNER);
            assertThat(question.getIsActive()).isTrue();
        });
    }

    @Test
    void questionHistoryUpsertsInsertUpdateCountersAndAvoidDuplicates() {
        Long questionId = eligibleQuestionIds.iterator().next();
        LocalDateTime firstShownAt = LocalDateTime.now().minusMinutes(5);
        LocalDateTime secondShownAt = LocalDateTime.now().minusMinutes(4);

        userQuestionHistoryRepository.upsertQuestionPresented(
                user.getId(), questionId, firstShownAt, "RANDOM");
        userQuestionHistoryRepository.upsertQuestionPresented(
                user.getId(), questionId, secondShownAt, "CATEGORY");

        assertThat(historyCount(questionId)).isEqualTo(1);
        HistoryStats shown = historyStats(questionId);
        assertThat(shown.timesShown()).isEqualTo(2);
        assertThat(shown.timesPresented()).isEqualTo(2);
        assertThat(shown.timesCorrect()).isZero();
        assertThat(shown.timesWrong()).isZero();
        assertThat(shown.lastShownType()).isEqualTo("CATEGORY");

        userQuestionHistoryRepository.upsertQuestionAnswered(
                user.getId(), questionId, LocalDateTime.now().minusMinutes(2), true, 12);
        userQuestionHistoryRepository.upsertQuestionAnswered(
                user.getId(), questionId, LocalDateTime.now().minusMinutes(1), false, 18);

        assertThat(historyCount(questionId)).isEqualTo(1);
        HistoryStats answered = historyStats(questionId);
        assertThat(answered.timesShown()).isEqualTo(2);
        assertThat(answered.timesPresented()).isEqualTo(2);
        assertThat(answered.timesCorrect()).isEqualTo(1);
        assertThat(answered.timesWrong()).isEqualTo(1);
        assertThat(answered.lastAnswerCorrect()).isFalse();
        assertThat(answered.timeTakenSeconds()).isEqualTo(18);
    }

    @Test
    void weakAreaUpsertsInsertUpdateAccuracyAndAvoidDuplicates() {
        userWeakAreaRepository.upsertBySignCode(user.getId(), "PORTABLE-SIGN", 1, 1, 0);
        userWeakAreaRepository.upsertBySignCode(user.getId(), "PORTABLE-SIGN", 1, 0, 1);

        WeakAreaStats signStats = weakAreaStats("traffic_sign_code", "PORTABLE-SIGN");
        assertThat(signStats.rows()).isEqualTo(1);
        assertThat(signStats.total()).isEqualTo(2);
        assertThat(signStats.correct()).isEqualTo(1);
        assertThat(signStats.wrong()).isEqualTo(1);
        assertThat(signStats.accuracy()).isEqualTo(50.0);

        userWeakAreaRepository.upsertByCategoryName(user.getId(), "PORTABLE-CATEGORY", 2, 1, 1);
        userWeakAreaRepository.upsertByCategoryName(user.getId(), "PORTABLE-CATEGORY", 1, 1, 0);

        WeakAreaStats categoryStats = weakAreaStats("category", "PORTABLE-CATEGORY");
        assertThat(categoryStats.rows()).isEqualTo(1);
        assertThat(categoryStats.total()).isEqualTo(3);
        assertThat(categoryStats.correct()).isEqualTo(2);
        assertThat(categoryStats.wrong()).isEqualTo(1);
        assertThat(categoryStats.accuracy()).isCloseTo(66.666666, org.assertj.core.data.Offset.offset(0.0001));
    }

    private Category category(String code, int displayOrder) {
        Category category = new Category();
        category.setCode(code);
        category.setNameAr("Portable " + code);
        category.setNameEn("Portable " + code);
        category.setNameNl("Portable " + code);
        category.setNameFr("Portable " + code);
        category.setDisplayOrder(displayOrder);
        category.setIsActive(true);
        return category;
    }

    private QuizQuestion saveQuestion(Category category, QuizQuestion.DifficultyLevel difficulty,
            boolean active, QuizQuestion.QuestionStatus status, String label) {
        QuizQuestion question = new QuizQuestion();
        question.setQuestionAr(label);
        question.setQuestionEn(label);
        question.setQuestionNl(label);
        question.setQuestionFr(label);
        question.setQuestionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE);
        question.setDifficultyLevel(difficulty);
        question.setCategory(category);
        question.setIsActive(active);
        question.setStatus(status);
        int optionCount = difficulty == QuizQuestion.DifficultyLevel.HARD ? 2 : 3;
        for (int index = 0; index < optionCount; index++) {
            QuizAnswerOption option = new QuizAnswerOption();
            option.setOptionTextAr(label + " option " + index);
            option.setOptionTextEn(label + " option " + index);
            option.setOptionTextNl(label + " option " + index);
            option.setOptionTextFr(label + " option " + index);
            option.setIsCorrect(index == 0);
            option.setDisplayOrder(index);
            question.addOption(option);
        }
        return quizQuestionRepository.saveAndFlush(question);
    }

    private void saveDevExamQuestion(DevExamCategory category, DevExamDifficulty difficulty,
            boolean active, String label) {
        DevExamQuestion question = new DevExamQuestion();
        question.setCategory(category);
        question.setDifficulty(difficulty);
        question.setQuestionEn(label);
        question.setQuestionAr(label);
        question.setQuestionNl(label);
        question.setQuestionFr(label);
        question.setIsActive(active);
        devExamQuestionRepository.saveAndFlush(question);
    }

    private void assertPublishedAndActive(QuizQuestion question) {
        assertThat(question.getIsActive()).isTrue();
        assertThat(question.getStatus()).isEqualTo(QuizQuestion.QuestionStatus.PUBLISHED);
    }

    private void assertUniqueQuestionIds(List<QuizQuestion> questions) {
        assertThat(questions).extracting(QuizQuestion::getId).doesNotHaveDuplicates();
    }

    private int historyCount(Long questionId) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_question_history WHERE user_id = ? AND question_ref_id = ?",
                Integer.class, user.getId(), questionId);
    }

    private HistoryStats historyStats(Long questionId) {
        return jdbcTemplate.queryForObject(
                "SELECT times_shown, times_presented, times_correct, times_wrong, last_shown_type, "
                        + "last_answer_correct, time_taken_seconds "
                        + "FROM user_question_history WHERE user_id = ? AND question_ref_id = ?",
                (rs, rowNum) -> new HistoryStats(
                        rs.getInt("times_shown"),
                        rs.getInt("times_presented"),
                        rs.getInt("times_correct"),
                        rs.getInt("times_wrong"),
                        rs.getString("last_shown_type"),
                        nullableBoolean(rs, "last_answer_correct"),
                        rs.getInt("time_taken_seconds")),
                user.getId(), questionId);
    }

    private Boolean nullableBoolean(ResultSet resultSet, String column) throws SQLException {
        boolean value = resultSet.getBoolean(column);
        return resultSet.wasNull() ? null : value;
    }

    private WeakAreaStats weakAreaStats(String keyColumn, String keyValue) {
        String sql = "SELECT COUNT(*) OVER () AS row_count, total_questions, correct_answers, "
                + "wrong_answers, accuracy_percentage FROM user_weak_areas WHERE user_id = ? AND "
                + keyColumn + " = ?";
        return jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> new WeakAreaStats(
                        rs.getInt("row_count"),
                        rs.getInt("total_questions"),
                        rs.getInt("correct_answers"),
                        rs.getInt("wrong_answers"),
                        rs.getDouble("accuracy_percentage")),
                user.getId(), keyValue);
    }

    private record HistoryStats(
            int timesShown,
            int timesPresented,
            int timesCorrect,
            int timesWrong,
            String lastShownType,
            Boolean lastAnswerCorrect,
            int timeTakenSeconds) {
    }

    private record WeakAreaStats(int rows, int total, int correct, int wrong, double accuracy) {
    }
}
