package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.enums.CategoryContentScope;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.repository.*;
import com.readyroad.readyroadbackend.dto.practice.SubmitPracticeAnswerRequest;
import com.readyroad.readyroadbackend.payment.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TheoryCategoryExamAccessTest {
    private static final long USER_ID = 42L;
    private static final Instant NOW = Instant.parse("2026-10-06T10:00:00Z");
    private final UserEntitlementRepository entitlements = mock(UserEntitlementRepository.class);
    private final UserRepository users = mock(UserRepository.class);
    private final CategoryRepository categories = mock(CategoryRepository.class);
    private final QuizQuestionRepository questions = mock(QuizQuestionRepository.class);
    private final TheoryQuestionCoverageStore coverage = mock(TheoryQuestionCoverageStore.class);
    private final QuizService quiz = mock(QuizService.class);
    private final PracticeService practice = mock(PracticeService.class);
    private final Clock clock = mock(Clock.class);
    private TheoryCategoryPracticeService service;
    private QuizQuestion question;

    @BeforeEach
    void setUp() {
        Category category = new Category();
        category.setId(101L);
        category.setCode("TH01");
        category.setIsActive(true);
        category.setContentScope(CategoryContentScope.THEORETICAL_EXAM);
        question = new QuizQuestion();
        question.setId(501L);
        question.setCategory(category);
        when(clock.instant()).thenReturn(NOW);
        when(categories.findByCode("TH01")).thenReturn(Optional.of(category));
        when(coverage.findEligibleQuestionIds(101L, "en")).thenReturn(List.of(501L));
        when(questions.findAllByIdWithOptionsAndCategory(List.of(501L))).thenReturn(List.of(question));
        when(questions.findById(501L)).thenReturn(Optional.of(question));
        service = new TheoryCategoryPracticeService(categories, questions, quiz, practice,
                new TheoryExamAccessService(entitlements, users, clock), coverage);
    }

    @ParameterizedTest
    @EnumSource(PaymentPlan.class)
    void everyPlanStopsBothDeliveryAndAnswersExactlyAtItsPurchasedExpiry(PaymentPlan plan) {
        User user = new User(); user.setRole(Role.USER);
        when(users.findById(USER_ID)).thenReturn(Optional.of(user));
        Instant expiry = NOW.plus(plan.days(), ChronoUnit.DAYS);
        UserEntitlement entitlement = new UserEntitlement();
        entitlement.setUserId(USER_ID); entitlement.setStatus(EntitlementStatus.ACTIVE);
        entitlement.setExpiresAt(expiry);
        when(entitlements.findById(USER_ID)).thenReturn(Optional.of(entitlement));
        when(clock.instant()).thenReturn(expiry.minusNanos(1));
        assertThat(service.getExamQuestions(USER_ID, "TH01", "en")).hasSize(1);
        service.submitAnswer(USER_ID, "TH01", 501L, new SubmitPracticeAnswerRequest(1L, 1));
        clearInvocations(practice, coverage, questions);
        when(clock.instant()).thenReturn(expiry);
        denied(() -> service.getExamQuestions(USER_ID, "TH01", "en"));
        denied(() -> service.submitAnswer(USER_ID, "TH01", 501L, new SubmitPracticeAnswerRequest(1L, 1)));
        verifyNoInteractions(practice, coverage, questions);
    }

    @ParameterizedTest
    @EnumSource(value = Role.class, names = {"USER", "STUDENT"})
    void regularMembersWithoutAnEntitlementCannotGetOrAnswerQuestions(Role role) {
        User user = new User(); user.setRole(role);
        when(users.findById(USER_ID)).thenReturn(Optional.of(user));
        when(entitlements.findById(USER_ID)).thenReturn(Optional.empty());
        denied(() -> service.getExamQuestions(USER_ID, "TH01", "en"));
        denied(() -> service.submitAnswer(USER_ID, "TH01", 501L, new SubmitPracticeAnswerRequest(1L, 1)));
        verifyNoInteractions(coverage, questions, practice);
    }

    @ParameterizedTest
    @EnumSource(value = Role.class, names = {"ADMIN", "MODERATOR"})
    void staffCanGetAndAnswerWithoutAnEntitlement(Role role) {
        User user = new User(); user.setRole(role);
        when(users.findById(USER_ID)).thenReturn(Optional.of(user));
        assertThat(service.getExamQuestions(USER_ID, "TH01", "en")).hasSize(1);
        service.submitAnswer(USER_ID, "TH01", 501L, new SubmitPracticeAnswerRequest(1L, 1));
        verifyNoInteractions(entitlements);
        verify(practice).submitPracticeAnswer(eq(USER_ID), eq(501L), any());
    }

    @Test
    void allQuestionsAreDeliveredWithoutTheLegacyFiftyQuestionCap() {
        User user = new User(); user.setRole(Role.ADMIN);
        when(users.findById(USER_ID)).thenReturn(Optional.of(user));
        List<Long> ids = LongStream.rangeClosed(1, 67).boxed().toList();
        when(coverage.findEligibleQuestionIds(101L, "en")).thenReturn(ids);
        List<QuizQuestion> bank = ids.stream().map(id -> {
            QuizQuestion q = new QuizQuestion(); q.setId(id); return q;
        }).toList();
        when(questions.findAllByIdWithOptionsAndCategory(ids)).thenReturn(bank);
        assertThat(service.getExamQuestions(USER_ID, "TH01", "en"))
                .extracting(QuizQuestion::getId).containsExactlyInAnyOrderElementsOf(ids);
        verifyNoInteractions(quiz);
    }

    private void denied(Runnable action) {
        assertThatThrownBy(action::run).isInstanceOfSatisfying(ResponseStatusException.class,
                error -> assertThat(error.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN));
    }
}
