package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulation;
import com.readyroad.readyroadbackend.domain.entity.UserCategoryProgress;
import com.readyroad.readyroadbackend.domain.enums.CategoryContentScope;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationAnswerRepository;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationRepository;
import com.readyroad.readyroadbackend.domain.repository.SignExamResultRepository;
import com.readyroad.readyroadbackend.domain.repository.SignPracticeSessionRepository;
import com.readyroad.readyroadbackend.domain.repository.SignRandomPracticeSessionRepository;
import com.readyroad.readyroadbackend.domain.repository.UserCategoryProgressRepository;
import com.readyroad.readyroadbackend.domain.repository.UserLessonProgressRepository;
import com.readyroad.readyroadbackend.domain.repository.UserQuestionHistoryRepository;
import com.readyroad.readyroadbackend.dto.StudentIntelligenceResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudentIntelligenceServiceTest {

    @Mock
    private ExamSimulationRepository examRepository;
    @Mock
    private ExamSimulationAnswerRepository examAnswerRepository;
    @Mock
    private SignPracticeSessionRepository signPracticeRepository;
    @Mock
    private SignRandomPracticeSessionRepository randomPracticeRepository;
    @Mock
    private SignExamResultRepository signExamRepository;
    @Mock
    private UserCategoryProgressRepository categoryProgressRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserQuestionHistoryRepository questionHistoryRepository;
    @Mock
    private UserLessonProgressRepository lessonProgressRepository;

    private StudentIntelligenceService service;

    @BeforeEach
    void setUp() {
        service = new StudentIntelligenceService(
                new StudentIntelligenceEngine(),
                examRepository,
                examAnswerRepository,
                signPracticeRepository,
                randomPracticeRepository,
                signExamRepository,
                categoryProgressRepository,
                categoryRepository,
                questionHistoryRepository,
                lessonProgressRepository);
    }

    @Test
    void returnsExplicitUnavailableMetricsWhenNoHistoricalEvidenceExists() {
        long userId = 42L;
        when(examRepository.findByUserIdOrderByStartedAtDesc(userId)).thenReturn(List.of());
        when(signPracticeRepository.findAllByUserIdOrderByStartedAtDesc(userId)).thenReturn(List.of());
        when(randomPracticeRepository.findAllByUserIdOrderByStartedAtDesc(userId)).thenReturn(List.of());
        when(signExamRepository.findByUserIdOrderByCompletedAtDesc(userId)).thenReturn(List.of());
        when(categoryProgressRepository.findByUserId(userId)).thenReturn(List.of());
        when(categoryRepository.findAll()).thenReturn(List.of());
        when(questionHistoryRepository.findByUserId(userId)).thenReturn(List.of());
        when(lessonProgressRepository.findAllByUserId(userId)).thenReturn(List.of());
        when(examAnswerRepository.findHistoryForUser(userId, ExamSimulation.ExamStatus.COMPLETED))
                .thenReturn(List.of());

        StudentIntelligenceResponse result = service.getStudentIntelligence(userId);

        assertThat(result.getDataStatus()).isEqualTo("NO_DATA");
        assertThat(result.getExamReadinessScore()).isNull();
        assertThat(result.getConfidenceScore()).isNull();
        assertThat(result.getTimingAnalytics().getAverageAnswerTimeSeconds()).isNull();
        assertThat(result.getTimingAnalytics().getCategoryTimings()).isEmpty();
        assertThat(result.getProgressJourney().getLessonRevisitCount()).isNull();
        assertThat(result.getProgressJourney().getCompletedPracticeSessions()).isZero();
        assertThat(result.getStrongestCategories()).isEmpty();
    }

    @Test
    void excludesTrafficSignCategoriesFromTheoreticalIntelligence() {
        long userId = 42L;
        Category theory = category(101L, "TH01", CategoryContentScope.THEORETICAL_EXAM);
        Category sign = category(1L, "A", CategoryContentScope.TRAFFIC_SIGN);
        UserCategoryProgress theoryProgress = progress(userId, theory, 20, 8);
        UserCategoryProgress signProgress = progress(userId, sign, 20, 19);

        when(examRepository.findByUserIdOrderByStartedAtDesc(userId)).thenReturn(List.of());
        when(signPracticeRepository.findAllByUserIdOrderByStartedAtDesc(userId)).thenReturn(List.of());
        when(randomPracticeRepository.findAllByUserIdOrderByStartedAtDesc(userId)).thenReturn(List.of());
        when(signExamRepository.findByUserIdOrderByCompletedAtDesc(userId)).thenReturn(List.of());
        when(categoryProgressRepository.findByUserId(userId))
                .thenReturn(List.of(theoryProgress, signProgress));
        when(categoryRepository.findAll()).thenReturn(List.of(theory, sign));
        when(questionHistoryRepository.findByUserId(userId)).thenReturn(List.of());
        when(lessonProgressRepository.findAllByUserId(userId)).thenReturn(List.of());
        when(examAnswerRepository.findHistoryForUser(userId, ExamSimulation.ExamStatus.COMPLETED))
                .thenReturn(List.of());

        StudentIntelligenceResponse result = service.getStudentIntelligence(userId);

        assertThat(result.getLearningPriorities())
                .extracting(StudentIntelligenceResponse.LearningPriority::getCategoryCode)
                .containsExactly("TH01");
        assertThat(result.getStrongestCategories())
                .extracting(StudentIntelligenceResponse.LearningPriority::getCategoryCode)
                .doesNotContain("A");
    }

    private static Category category(Long id, String code, CategoryContentScope scope) {
        Category category = new Category();
        category.setId(id);
        category.setCode(code);
        category.setNameEn(code);
        category.setNameNl(code);
        category.setNameFr(code);
        category.setNameAr(code);
        category.setIsActive(true);
        category.setContentScope(scope);
        return category;
    }

    private static UserCategoryProgress progress(
            long userId,
            Category category,
            int attempted,
            int correct) {
        return new UserCategoryProgress(
                userId,
                category.getId(),
                attempted,
                correct,
                BigDecimal.valueOf(correct * 100.0 / attempted),
                LocalDateTime.now(),
                UserCategoryProgress.MasteryLevel.BEGINNER,
                category);
    }
}
