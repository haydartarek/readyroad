package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulation;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulationAnswer;
import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.entity.UserCategoryProgress;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulationQuestion;
import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
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
import com.readyroad.readyroadbackend.dto.exam.TheoryExamQuestionSnapshot;
import com.readyroad.readyroadbackend.dto.exam.TheoryExamQuestionSnapshot.CategorySnapshot;
import com.readyroad.readyroadbackend.dto.exam.TheoryExamQuestionSnapshot.LocalizedText;
import com.readyroad.readyroadbackend.dto.exam.TheoryExamQuestionSnapshot.OptionSnapshot;
import com.readyroad.readyroadbackend.mapper.ExamMapper;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExamServiceLifecycleTest {

    @Mock ExamSimulationRepository examRepository;
    @Mock CategoryRepository categoryRepository;
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
    void completedExamAttributesProgressToSnapshotCategoryWhenQuestionMovesMidExam() {
        ExamSimulation exam = activeExam();
        exam.setTotalQuestions(1);

        Category originalCategory =
                category(1L, "TH01", "Renamed priority");

        Category movedCategory =
                category(2L, "TH02", "Current moved category");

        QuizQuestion question =
                question(10L, movedCategory);

        ExamSimulationAnswer answer =
                answered(exam, question, true);

        ExamSimulationQuestion examQuestion =
                examQuestion(question);

        when(examRepository.findByIdForUpdate(42L))
                .thenReturn(Optional.of(exam));

        when(answerRepository.findByExamId(42L))
                .thenReturn(List.of(answer));

        when(examQuestionRepository.findByExamIdOrderByQuestionOrder(42L))
                .thenReturn(List.of(examQuestion));

        when(questionSnapshotService.read(examQuestion))
                .thenReturn(snapshot(
                        10L,
                        1L,
                        "TH01",
                        "Historical priority"));

        when(categoryRepository.findAll())
                .thenReturn(List.of(originalCategory, movedCategory));

        when(progressRepository.findByUserIdAndCategoryId(7L, 1L))
                .thenReturn(Optional.empty());

        service.completeExam(42L, 7L);

        verify(progressRepository)
                .findByUserIdAndCategoryId(7L, 1L);

        verify(progressRepository, never())
                .findByUserIdAndCategoryId(7L, 2L);

        ArgumentCaptor<UserCategoryProgress> captor =
                ArgumentCaptor.forClass(UserCategoryProgress.class);

        verify(progressRepository).save(captor.capture());

        assertThat(captor.getValue().getCategoryId())
                .isEqualTo(1L);
    }

    @Test
    void completedResultsKeepSnapshotIdentityButShowCurrentRenamedCategoryName() {
        ExamSimulation exam = activeExam();
        exam.setTotalQuestions(1);
        exam.setStatus(ExamSimulation.ExamStatus.COMPLETED);

        Instant completedAt = Instant.now();
        exam.setStartedAt(completedAt.minusSeconds(5));
        exam.setCompletedAt(completedAt);
        exam.setCorrectAnswers(1);
        exam.setScorePercentage(100.0);

        Category renamedCategory =
                category(1L, "TH01", "Renamed priority");

        Category movedCategory =
                category(2L, "TH02", "Current moved category");

        QuizQuestion question =
                question(10L, movedCategory);

        QuizAnswerOption correctOption =
                option(101L, true);

        ExamSimulationAnswer answer =
                answered(
                        exam,
                        question,
                        true,
                        correctOption,
                        correctOption);

        ExamSimulationQuestion examQuestion =
                examQuestion(question);

        when(examRepository.findById(42L))
                .thenReturn(Optional.of(exam));

        when(answerRepository.findByExamId(42L))
                .thenReturn(List.of(answer));

        when(examQuestionRepository.findByExamIdOrderByQuestionOrder(42L))
                .thenReturn(List.of(examQuestion));

        when(questionSnapshotService.read(examQuestion))
                .thenReturn(snapshot(
                        10L,
                        1L,
                        "TH01",
                        "Historical priority"));

        when(categoryRepository.findAll())
                .thenReturn(List.of(renamedCategory, movedCategory));

        var result =
                service.getExamResults(42L, 7L);

        assertThat(result.getCategoryBreakdown())
                .singleElement()
                .satisfies(category -> {
                    assertThat(category.getCategoryId())
                            .isEqualTo(1L);
                    assertThat(category.getCategoryCode())
                            .isEqualTo("TH01");
                    assertThat(category.getCategoryNameEn())
                            .isEqualTo("Renamed priority");
                });

        assertThat(result.getAllAnswers())
                .singleElement()
                .satisfies(questionResult -> {
                    assertThat(questionResult.getCategoryCode())
                            .isEqualTo("TH01");
                    assertThat(questionResult.getCategoryNameEn())
                            .isEqualTo("Renamed priority");
                });
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

    private static ExamSimulationQuestion examQuestion(
            QuizQuestion question) {

        ExamSimulationQuestion examQuestion =
                new ExamSimulationQuestion();

        examQuestion.setQuestion(question);
        examQuestion.setQuestionId(question.getId());
        examQuestion.setQuestionOrder(1);

        return examQuestion;
    }

    private static Category category(
            long id,
            String code,
            String name) {

        Category category = new Category();
        category.setId(id);
        category.setCode(code);
        category.setNameEn(name);
        category.setNameNl(name);
        category.setNameFr(name);
        category.setNameAr(name);
        category.setIsActive(true);
        return category;
    }

    private static QuizQuestion question(
            long id,
            Category category) {

        QuizQuestion question = new QuizQuestion();
        question.setId(id);
        question.setCategory(category);
        question.setQuestionEn("Current edited question");
        question.setQuestionNl("Current edited question");
        question.setQuestionFr("Current edited question");
        question.setQuestionAr("Current edited question");
        return question;
    }

    private static ExamSimulationAnswer answered(
            ExamSimulation exam,
            QuizQuestion question,
            boolean correct) {

        return answered(
                exam,
                question,
                correct,
                null,
                null);
    }

    private static ExamSimulationAnswer answered(
            ExamSimulation exam,
            QuizQuestion question,
            boolean correct,
            QuizAnswerOption selected,
            QuizAnswerOption correctOption) {

        return ExamSimulationAnswer.builder()
                .exam(exam)
                .question(question)
                .selectedOption(selected)
                .correctOption(correctOption)
                .isCorrect(correct)
                .timeTakenSeconds(5)
                .answeredAt(Instant.now())
                .answerState(ExamSimulationAnswer.AnswerState.ANSWERED)
                .build();
    }

    private static QuizAnswerOption option(
            long id,
            boolean correct) {

        QuizAnswerOption option = new QuizAnswerOption();
        option.setId(id);
        option.setIsCorrect(correct);
        option.setDisplayOrder(0);
        option.setOptionTextEn("Current option");
        option.setOptionTextNl("Current option");
        option.setOptionTextFr("Current option");
        option.setOptionTextAr("Current option");
        return option;
    }

    private static TheoryExamQuestionSnapshot snapshot(
            long questionId,
            long categoryId,
            String categoryCode,
            String historicalCategoryName) {

        return new TheoryExamQuestionSnapshot(
                (short) 1,
                questionId,
                text("Historical question"),
                text("Historical explanation"),
                "/historical.webp",
                new CategorySnapshot(
                        categoryId,
                        categoryCode,
                        text(historicalCategoryName)),
                "MEDIUM",
                List.of(
                        new OptionSnapshot(
                                101L,
                                text("Historical correct"),
                                true,
                                0)));
    }

    private static LocalizedText text(String value) {
        return new LocalizedText(
                value,
                value,
                value,
                value);
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
