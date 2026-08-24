package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.readyroad.readyroadbackend.domain.entity.ExamSimulation;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulationQuestion;
import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationAnswerRepository;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationRepository;
import com.readyroad.readyroadbackend.domain.repository.NotificationRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizAnswerOptionRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.UserCategoryProgressRepository;
import com.readyroad.readyroadbackend.domain.repository.UserQuestionHistoryRepository;
import com.readyroad.readyroadbackend.domain.repository.UserWeakAreaRepository;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.exception.ExamNotActiveException;
import com.readyroad.readyroadbackend.exception.ExamQuestionPoolUnavailableException;
import com.readyroad.readyroadbackend.exception.UnauthorizedException;
import com.readyroad.readyroadbackend.mapper.ExamMapper;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExamServiceLifecycleTest {

    @Mock ExamSimulationRepository examRepository;
    @Mock ExamSimulationQuestionRepository examQuestionRepository;
    @Mock ExamSimulationAnswerRepository answerRepository;
    @Mock QuizAnswerOptionRepository optionRepository;
    @Mock QuizQuestionRepository questionRepository;
    @Mock NotificationService notificationService;
    @Mock NotificationRepository notificationRepository;
    @Mock AchievementService achievementService;
    @Mock UserCategoryProgressRepository progressRepository;
    @Mock UserQuestionHistoryRepository historyRepository;
    @Mock StreakService streakService;
    @Mock UserWeakAreaRepository weakAreaRepository;
    @Mock RoadSignReferenceTextResolver roadSignReferenceTextResolver;
    @Mock ExamMapper examMapper;
    @Mock BackendMessageService messages;
    @Mock TheoryExamQuestionAllocator questionAllocator;
    @Mock TheoryExamQuestionSnapshotService questionSnapshotService;
    @Mock UserRepository userRepository;

    @InjectMocks ExamService service;

    @Test
    void learnerHistoryContainsCompletedAttemptsOnly() {
        service.getCompletedExams(7L);

        verify(examRepository).findByUserIdAndStatusOrderByCompletedAtDesc(
                7L, ExamSimulation.ExamStatus.COMPLETED);
    }

    @Test
    void incompleteExitCreatesNoScoreAndIsIdempotent() {
        ExamSimulation exam = activeExam();
        when(examRepository.findByIdForUpdate(42L)).thenReturn(Optional.of(exam));

        service.cancelExam(42L, 7L);

        assertThat(exam.getStatus()).isEqualTo(ExamSimulation.ExamStatus.ABANDONED);
        assertThat(exam.getCompletedAt()).isNull();
        assertThat(exam.getCorrectAnswers()).isNull();
        assertThat(exam.getScorePercentage()).isNull();
        verify(examRepository).save(exam);
        verify(notificationService, never()).createExamAbandonedNotification(any(), any());

        service.cancelExam(42L, 7L);
        verify(examRepository).save(exam);
    }

    @Test
    void incompleteExamCannotBecomeCompletedResult() {
        ExamSimulation exam = activeExam();
        when(examRepository.findByIdForUpdate(42L)).thenReturn(Optional.of(exam));
        when(answerRepository.findByExamId(42L)).thenReturn(List.of());

        assertThatThrownBy(() -> service.completeExam(42L, 7L))
                .isInstanceOf(ExamNotActiveException.class);

        assertThat(exam.getStatus()).isEqualTo(ExamSimulation.ExamStatus.IN_PROGRESS);
        assertThat(exam.getScorePercentage()).isNull();
        verify(examRepository, never()).save(exam);
        verifyNoInteractions(progressRepository, weakAreaRepository);
    }

    @Test
    void repeatedCompletedRequestResolvesTheExistingResultWithoutSideEffects() {
        ExamSimulation exam = activeExam();
        exam.setStatus(ExamSimulation.ExamStatus.COMPLETED);
        exam.setCompletedAt(Instant.now());
        exam.setCorrectAnswers(41);
        exam.setScorePercentage(82.0);
        when(examRepository.findByIdForUpdate(42L)).thenReturn(Optional.of(exam));

        service.completeExam(42L, 7L);

        verifyNoInteractions(answerRepository, notificationService, progressRepository,
                weakAreaRepository, historyRepository, streakService);
        verify(examRepository, never()).save(exam);
    }

    @Test
    void abandonedAttemptIsNotReportedAsAHarmlessRepeatedCompletion() {
        ExamSimulation exam = activeExam();
        exam.setStatus(ExamSimulation.ExamStatus.ABANDONED);
        when(examRepository.findByIdForUpdate(42L)).thenReturn(Optional.of(exam));

        assertThatThrownBy(() -> service.completeExam(42L, 7L))
                .isInstanceOf(ExamNotActiveException.class);

        verifyNoInteractions(answerRepository, notificationService, progressRepository,
                weakAreaRepository, historyRepository, streakService);
    }

    @Test
    void expiredAttemptIsNotReportedAsAHarmlessRepeatedCompletion() {
        ExamSimulation exam = activeExam();
        exam.setStatus(ExamSimulation.ExamStatus.EXPIRED);
        when(examRepository.findByIdForUpdate(42L)).thenReturn(Optional.of(exam));

        assertThatThrownBy(() -> service.completeExam(42L, 7L))
                .isInstanceOf(ExamNotActiveException.class);

        verifyNoInteractions(answerRepository, notificationService, progressRepository,
                weakAreaRepository, historyRepository, streakService);
    }

    @Test
    void presentedQuestionIsRecordedOnlyOncePerPersistedExamQuestion() {
        ExamSimulation exam = activeExam();
        ExamSimulationQuestion examQuestion = new ExamSimulationQuestion();
        examQuestion.setExam(exam);
        examQuestion.setQuestionId(17L);
        examQuestion.setQuestionOrder(1);

        when(examRepository.findById(42L)).thenReturn(Optional.of(exam));
        when(examQuestionRepository.findByExamIdAndQuestionId(42L, 17L))
                .thenReturn(Optional.of(examQuestion));
        when(examQuestionRepository.markPresentedIfAbsent(
                eq(42L), eq(17L), any(LocalDateTime.class)))
                .thenReturn(1, 0);

        service.recordQuestionPresented(42L, 17L, 7L);
        service.recordQuestionPresented(42L, 17L, 7L);

        verify(historyRepository).upsertQuestionPresented(
                eq(7L), eq(17L), any(LocalDateTime.class), eq("EXAM"));
    }

    @Test
    void presentedQuestionRejectsAUserWhoDoesNotOwnTheExam() {
        ExamSimulation exam = activeExam();
        when(examRepository.findById(42L)).thenReturn(Optional.of(exam));

        assertThatThrownBy(() -> service.recordQuestionPresented(42L, 17L, 99L))
                .isInstanceOf(UnauthorizedException.class);

        verifyNoInteractions(examQuestionRepository, historyRepository);
    }

    @Test
    void failedAllocationPreflightDoesNotPersistAnExam() {
        User user = new User();
        user.setPreferredLanguage("en");
        when(userRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(user));
        when(examRepository.findByUserIdAndStatus(7L, ExamSimulation.ExamStatus.IN_PROGRESS))
                .thenReturn(Optional.empty());
        when(questionAllocator.allocate(eq(7L), eq("en"), any(LocalDateTime.class)))
                .thenThrow(new ExamQuestionPoolUnavailableException("Unavailable", 50, 42));

        assertThatThrownBy(() -> service.startExamSimulation(7L))
                .isInstanceOf(ExamQuestionPoolUnavailableException.class);

        verify(examRepository, never()).save(any());
        verifyNoInteractions(examQuestionRepository);
    }

    private ExamSimulation activeExam() {
        ExamSimulation exam = new ExamSimulation();
        exam.setId(42L);
        exam.setUserId(7L);
        exam.setStartedAt(Instant.now());
        exam.setExpiresAt(Instant.now().plusSeconds(1800));
        exam.setTotalQuestions(50);
        exam.setStatus(ExamSimulation.ExamStatus.IN_PROGRESS);
        return exam;
    }
}
