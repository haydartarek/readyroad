package com.readyroad.readyroadbackend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.readyroad.readyroadbackend.dto.TheoryQuestionCoverageResponse;
import com.readyroad.readyroadbackend.dto.TheoryTimeoutAnalysisResponse;
import com.readyroad.readyroadbackend.service.ProgressService;
import com.readyroad.readyroadbackend.service.StudentIntelligenceService;
import com.readyroad.readyroadbackend.service.TheoryQuestionCoverageService;
import com.readyroad.readyroadbackend.service.TheoryTimeoutAnalysisService;
import com.readyroad.readyroadbackend.util.AuthenticationUtil;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class ProgressControllerCoverageTest {

    @Mock private ProgressService progressService;
    @Mock private StudentIntelligenceService studentIntelligenceService;
    @Mock private TheoryQuestionCoverageService coverageService;
    @Mock private TheoryTimeoutAnalysisService timeoutAnalysisService;
    @Mock private AuthenticationUtil authenticationUtil;
    @Mock private Authentication authentication;

    private ProgressController controller;

    @BeforeEach
    void setUp() {
        controller = new ProgressController(
                progressService,
                studentIntelligenceService,
                coverageService,
                timeoutAnalysisService,
                authenticationUtil);
    }

    @Test
    void rejectsUnauthenticatedCoverageRequests() {
        when(authenticationUtil.extractUserId(authentication)).thenReturn(null);

        ResponseEntity<TheoryQuestionCoverageResponse> response =
                controller.getTheoryQuestionCoverage(authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verifyNoInteractions(coverageService);
    }

    @Test
    void returnsCoverageForTheAuthenticatedUserOnly() {
        TheoryQuestionCoverageResponse coverage = TheoryQuestionCoverageResponse.builder()
                .languageCode("fr")
                .eligibleQuestions(10L)
                .uniqueQuestionsSeen(2L)
                .unseenQuestions(8L)
                .categories(List.of())
                .build();
        when(authenticationUtil.extractUserId(authentication)).thenReturn(42L);
        when(coverageService.getCoverage(42L)).thenReturn(coverage);

        ResponseEntity<TheoryQuestionCoverageResponse> response =
                controller.getTheoryQuestionCoverage(authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(coverage);
        verify(coverageService).getCoverage(42L);
    }

    @Test
    void returnsCompletedTheoryTimeoutsForTheAuthenticatedUserOnly() {
        TheoryTimeoutAnalysisResponse analysis = new TheoryTimeoutAnalysisResponse(1, List.of());
        when(authenticationUtil.extractUserId(authentication)).thenReturn(42L);
        when(timeoutAnalysisService.getAnalysis(42L, 5)).thenReturn(analysis);

        ResponseEntity<TheoryTimeoutAnalysisResponse> response =
                controller.getTheoryTimeoutAnalysis(authentication, 5);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(analysis);
        verify(timeoutAnalysisService).getAnalysis(42L, 5);
    }
}
