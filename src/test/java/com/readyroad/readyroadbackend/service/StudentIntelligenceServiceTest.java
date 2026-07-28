package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.readyroad.readyroadbackend.domain.entity.ExamSimulation;
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
}
