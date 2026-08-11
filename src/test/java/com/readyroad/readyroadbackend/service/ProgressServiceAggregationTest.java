package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulation;
import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.entity.SignPracticeSession;
import com.readyroad.readyroadbackend.domain.entity.SignRandomPracticeSession;
import com.readyroad.readyroadbackend.domain.entity.UserCategoryProgress;
import com.readyroad.readyroadbackend.domain.entity.UserLessonProgress;
import com.readyroad.readyroadbackend.domain.entity.UserWeakArea;
import com.readyroad.readyroadbackend.domain.enums.CategoryContentScope;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import com.readyroad.readyroadbackend.domain.repository.SignExamResultRepository;
import com.readyroad.readyroadbackend.domain.repository.SignPracticeSessionRepository;
import com.readyroad.readyroadbackend.domain.repository.SignRandomPracticeSessionRepository;
import com.readyroad.readyroadbackend.domain.repository.UserCategoryProgressRepository;
import com.readyroad.readyroadbackend.domain.repository.UserLessonProgressRepository;
import com.readyroad.readyroadbackend.domain.repository.UserQuestionHistoryRepository;
import com.readyroad.readyroadbackend.domain.repository.UserWeakAreaRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ProgressServiceAggregationTest {

    private static final long USER_ID = 17L;

    @Mock
    private UserCategoryProgressRepository progressRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private QuizQuestionRepository questionRepository;
    @Mock
    private UserQuestionHistoryRepository historyRepository;
    @Mock
    private UserLessonProgressRepository lessonProgressRepository;
    @Mock
    private UserWeakAreaRepository weakAreaRepository;
    @Mock
    private RoadSignRepository roadSignRepository;
    @Mock
    private ExamSimulationRepository examSimulationRepository;
    @Mock
    private SignPracticeSessionRepository signPracticeSessionRepository;
    @Mock
    private SignExamResultRepository signExamResultRepository;
    @Mock
    private SignRandomPracticeSessionRepository signRandomPracticeSessionRepository;

    @InjectMocks
    private ProgressService progressService;

    @Test
    void returnsExactCountsForMixedPersistedActivity() {
        UserCategoryProgress categoryProgress = new UserCategoryProgress();
        categoryProgress.setUserId(USER_ID);
        categoryProgress.setCategoryId(3L);
        categoryProgress.setQuestionsAttempted(20);
        categoryProgress.setCorrectAnswers(15);
        categoryProgress.setAccuracyRate(BigDecimal.valueOf(75));

        Category category = new Category();
        category.setId(3L);
        category.setCode("TH01");
        category.setNameEn("Priority and intersections");
        category.setIsActive(true);
        category.setContentScope(CategoryContentScope.THEORETICAL_EXAM);
        categoryProgress.setCategory(category);

        Category signCategory = new Category();
        signCategory.setId(4L);
        signCategory.setCode("B");
        signCategory.setNameEn("Priority signs");
        signCategory.setIsActive(true);
        signCategory.setContentScope(CategoryContentScope.TRAFFIC_SIGN);

        UserCategoryProgress signProgress = new UserCategoryProgress();
        signProgress.setUserId(USER_ID);
        signProgress.setCategoryId(4L);
        signProgress.setQuestionsAttempted(9);
        signProgress.setCorrectAnswers(8);
        signProgress.setAccuracyRate(BigDecimal.valueOf(88.89));
        signProgress.setCategory(signCategory);

        List<UserLessonProgress> lessons = List.of(
                completedLesson(1L),
                completedLesson(2L),
                completedLesson(3L),
                completedLesson(4L));

        when(progressRepository.findByUserId(USER_ID)).thenReturn(List.of(categoryProgress, signProgress));
        when(categoryRepository.findAll()).thenReturn(List.of(category, signCategory));
        when(lessonProgressRepository.findAllByUserId(USER_ID)).thenReturn(lessons);
        when(historyRepository.findDistinctAnswerDatesByUserId(USER_ID)).thenReturn(List.of());
        when(signPracticeSessionRepository.findAllByUserIdOrderByStartedAtDesc(USER_ID)).thenReturn(List.of());
        when(signRandomPracticeSessionRepository.findAllByUserIdOrderByStartedAtDesc(USER_ID)).thenReturn(List.of());
        when(examSimulationRepository.findByUserIdOrderByStartedAtDesc(USER_ID)).thenReturn(List.of());
        when(weakAreaRepository.findAllByUserId(USER_ID)).thenReturn(List.of());

        when(examSimulationRepository.countByUserIdAndStatus(
                USER_ID, ExamSimulation.ExamStatus.COMPLETED)).thenReturn(3L);
        when(examSimulationRepository.countByUserIdAndStatusAndScorePercentageGreaterThanEqual(
                USER_ID, ExamSimulation.ExamStatus.COMPLETED, 82.0)).thenReturn(2L);
        when(examSimulationRepository.countByUserIdAndStatus(
                USER_ID, ExamSimulation.ExamStatus.IN_PROGRESS)).thenReturn(1L);

        when(signPracticeSessionRepository.countByUserIdAndStatus(
                USER_ID, SignPracticeSession.SessionStatus.COMPLETED)).thenReturn(2L);
        when(signPracticeSessionRepository.countByUserIdAndStatus(
                USER_ID, SignPracticeSession.SessionStatus.IN_PROGRESS)).thenReturn(1L);

        when(signExamResultRepository.countByUserId(USER_ID)).thenReturn(2L);
        when(signExamResultRepository.countDistinctSignsWithPassedExam(USER_ID)).thenReturn(1L);

        when(signRandomPracticeSessionRepository.countByUser_IdAndStatus(
                USER_ID, SignRandomPracticeSession.SessionStatus.COMPLETED)).thenReturn(1L);
        when(signRandomPracticeSessionRepository.countByUserIdAndPassedTrue(USER_ID)).thenReturn(1L);
        when(signRandomPracticeSessionRepository.countByUser_IdAndStatus(
                USER_ID, SignRandomPracticeSession.SessionStatus.IN_PROGRESS)).thenReturn(1L);

        var progress = progressService.getOverallProgress(USER_ID);

        assertThat(progress.getTotalAttempted()).isEqualTo(20);
        assertThat(progress.getTotalExamsTaken()).isEqualTo(3);
        assertThat(progress.getPassedExams()).isEqualTo(2);
        assertThat(progress.getFailedExams()).isEqualTo(1);
        assertThat(progress.getPassRate()).isEqualByComparingTo("66.67");
        assertThat(progress.getSignPracticeCount()).isEqualTo(2);
        assertThat(progress.getSignExamCount()).isEqualTo(2);
        assertThat(progress.getSignPassedCount()).isEqualTo(1);
        assertThat(progress.getSignRandomExamCount()).isEqualTo(1);
        assertThat(progress.getSignRandomExamPassedCount()).isEqualTo(1);
        assertThat(progress.getLessonsStartedCount()).isEqualTo(4);
        assertThat(progress.getLessonsCompletedCount()).isEqualTo(4);
        assertThat(progress.getActiveTheoryExamCount()).isEqualTo(1);
        assertThat(progress.getIncompleteSignPracticeCount()).isEqualTo(1);
        assertThat(progress.getActiveRandomSignExamCount()).isEqualTo(1);
        assertThat(progress.getIncompleteActivitiesCount()).isEqualTo(3);
    }

    @Test
    void weakSignsExcludeZeroAccuracyButKeepNonZeroValues() {
        UserWeakArea zeroAccuracy = weakSign("A1", 0.0);
        UserWeakArea nonZeroAccuracy = weakSign("F9", 50.0);
        RoadSign sign = new RoadSign();
        sign.setSignCode("F9");
        sign.setNameEn("Motorway");

        when(weakAreaRepository.findAllByUserId(USER_ID))
                .thenReturn(List.of(zeroAccuracy, nonZeroAccuracy));
        when(roadSignRepository.findBySignCodeIn(Set.of("F9")))
                .thenReturn(List.of(sign));

        @SuppressWarnings("unchecked")
        List<Object> weakSigns = ReflectionTestUtils.invokeMethod(
                progressService,
                "identifyWeakSigns",
                USER_ID);

        assertThat(weakSigns).hasSize(1);
        assertThat(ReflectionTestUtils.getField(weakSigns.get(0), "signCode"))
                .isEqualTo("F9");
    }

    private static UserWeakArea weakSign(String code, double accuracy) {
        UserWeakArea area = new UserWeakArea();
        area.setTrafficSignCode(code);
        area.setTotalQuestions(2);
        area.setAccuracyPercentage(accuracy);
        return area;
    }

    private static UserLessonProgress completedLesson(long lessonId) {
        UserLessonProgress progress = new UserLessonProgress(USER_ID, lessonId);
        progress.setPagesRead(3);
        progress.setStatus("COMPLETED");
        return progress;
    }
}
