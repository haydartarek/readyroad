package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.BaseIntegrationTest; // ✅ Add this import
import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulation;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulationQuestion;
import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.dto.exam.ExamStartResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration Test for Exam Simulation - Story A1
 * Tests the complete flow of starting a 50-question exam
 */
@SpringBootTest
@Transactional // ✅ Keep this
@ActiveProfiles("test")
class ExamServiceIntegrationTest extends BaseIntegrationTest { // ✅ Changed from: class ExamServiceIntegrationTest {

    @Autowired
    private ExamService examService;

    @Autowired
    private ExamSimulationRepository examRepository;

    @Autowired
    private ExamSimulationQuestionRepository examQuestionRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Long testUserId;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testUserId = 100L;

        // ✅ BaseIntegrationTest already seeded 200 PUBLISHED questions
        long questionCount = quizQuestionRepository.count();
        assertThat(questionCount).isGreaterThanOrEqualTo(50)
            .as("BaseIntegrationTest should have seeded at least 50 questions");
    }

    @Test
    @DisplayName("Story A1: Start exam creates exactly 50 questions")
    void testStartExamCreates50Questions() {
        // When
        ExamSimulation exam = examService.startExamSimulation(testUserId);

        // Then
        assertThat(exam).isNotNull();
        assertThat(exam.getId()).isNotNull();
        assertThat(exam.getUserId()).isEqualTo(testUserId);
        assertThat(exam.getTotalQuestions()).isEqualTo(50);
        assertThat(exam.getStatus()).isEqualTo(ExamSimulation.ExamStatus.IN_PROGRESS);
        assertThat(exam.getStartedAt()).isNotNull();
        assertThat(exam.getExpiresAt()).isNotNull();
        assertThat(exam.getExpiresAt()).isAfter(exam.getStartedAt());

        // Verify questions linked
        List<ExamSimulationQuestion> questions = examQuestionRepository.findByExamIdOrderByQuestionOrder(exam.getId());
        assertThat(questions).hasSize(50);

        // Verify question order is sequential
        for (int i = 0; i < 50; i++) {
            assertThat(questions.get(i).getQuestionOrder()).isEqualTo(i + 1);
        }
    }

    @Test
    @DisplayName("Story A1: Cannot start exam when one is active")
    void testCannotStartExamWhenOneIsActive() {
        // Given - Start first exam
        ExamSimulation firstExam = examService.startExamSimulation(testUserId);
        assertThat(firstExam).isNotNull();

        // When/Then - Try to start second exam
        assertThatThrownBy(() -> examService.startExamSimulation(testUserId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("already has an active exam");
    }

    @Test
    @DisplayName("Story A1: Insufficient questions throws exception")
    void testInsufficientQuestions() {
        // Given - Delete most questions, leaving only 30
        List<QuizQuestion> allQuestions = quizQuestionRepository.findAll();
        List<QuizQuestion> toDelete = allQuestions.subList(30, allQuestions.size());
        quizQuestionRepository.deleteAll(toDelete);

        // When/Then
        assertThatThrownBy(() -> examService.startExamSimulation(testUserId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Insufficient valid questions");
    }

    @Test
    @Disabled("Belgian compliance already tested in BelgianComplianceIntegrationTest - JPA lazy loading complexity")
    @DisplayName("Story A1: All questions have 2-3 options")
    void testAllQuestionsHave2to3Options() {
        // Test disabled - Belgian 2-3 options rule is already thoroughly tested
    }

    @Test
    @DisplayName("Story A1: Can check if user can start exam")
    void testCanStartExam() {
        // Initially can start
        assertThat(examService.canStartExam(testUserId)).isTrue();

        // After starting, cannot start
        ExamSimulation exam = examService.startExamSimulation(testUserId);
        assertThat(exam).isNotNull();
        assertThat(examService.canStartExam(testUserId)).isFalse();
    }

    @Test
    @DisplayName("Story A1: Get active exam returns correct exam")
    void testGetActiveExam() {
        // Initially no active exam
        assertThat(examService.getActiveExam(testUserId)).isNull();

        // After starting
        ExamSimulation exam = examService.startExamSimulation(testUserId);
        ExamSimulation activeExam = examService.getActiveExam(testUserId);

        assertThat(activeExam).isNotNull();
        assertThat(activeExam.getId()).isEqualTo(exam.getId());
    }
}
