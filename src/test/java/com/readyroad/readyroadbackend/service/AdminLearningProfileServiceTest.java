package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.dto.TheoryQuestionCoverageResponse;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AdminLearningProfileServiceTest {

    @Mock AdminLearningStore store;
    @Mock UserRepository userRepository;
    @Mock SignQuizService signQuizService;
    @Mock TheoryQuestionCoverageService coverageService;
    @Mock AdminTheoryExamHistoryService theoryExamHistoryService;
    @InjectMocks AdminLearningService service;

    @Test
    void delegatesCoverageForRealLearnerOnly() {
        User learner = user(Role.USER);
        TheoryQuestionCoverageResponse coverage = TheoryQuestionCoverageResponse.builder()
                .languageCode("nl")
                .build();
        when(userRepository.findById(8L)).thenReturn(Optional.of(learner));
        when(coverageService.getCoverage(8L)).thenReturn(coverage);

        assertThat(service.coverage(8L)).isSameAs(coverage);
        verify(coverageService).getCoverage(8L);
    }

    @Test
    void excludesAdminAndModeratorFromLearnerAnalytics() {
        when(userRepository.findById(9L)).thenReturn(Optional.of(user(Role.ADMIN)));
        when(userRepository.findById(10L)).thenReturn(Optional.of(user(Role.MODERATOR)));

        assertNotFound(() -> service.coverage(9L));
        assertNotFound(() -> service.difficulty(10L));
        verifyNoInteractions(coverageService, store);
    }

    private static User user(Role role) {
        User user = new User();
        user.setRole(role);
        return user;
    }

    private static void assertNotFound(Runnable action) {
        assertThatThrownBy(action::run)
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        exception -> assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }
}
