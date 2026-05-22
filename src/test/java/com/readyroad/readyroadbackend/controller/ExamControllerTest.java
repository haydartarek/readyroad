package com.readyroad.readyroadbackend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.readyroad.readyroadbackend.dto.exam.ExamStartResponse;
import com.readyroad.readyroadbackend.service.BackendMessageService;
import com.readyroad.readyroadbackend.service.ExamService;
import com.readyroad.readyroadbackend.util.AuthenticationUtil;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ExamControllerTest {

    @Mock
    private ExamService examService;

    @Mock
    private AuthenticationUtil authenticationUtil;

    @Mock
    private BackendMessageService messages;

    @InjectMocks
    private ExamController examController;

    @Test
    void startExamDelegatesResponseConstructionToService() {
        ExamStartResponse exam = ExamStartResponse.builder()
                .examId(42L)
                .status("IN_PROGRESS")
                .totalQuestions(50)
                .questions(List.of())
                .build();

        when(authenticationUtil.getCurrentUserId()).thenReturn(7L);
        when(examService.startExamResponse(7L)).thenReturn(exam);

        ResponseEntity<?> response = examController.startExam();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(exam);
        verify(examService).startExamResponse(7L);
    }

    @Test
    void getActiveExamReturnsServiceBackedResponseWhenPresent() {
        ExamStartResponse activeExam = ExamStartResponse.builder()
                .examId(84L)
                .status("IN_PROGRESS")
                .questions(List.of())
                .build();

        when(authenticationUtil.getCurrentUserId()).thenReturn(9L);
        when(examService.getActiveExamResponse(9L)).thenReturn(activeExam);

        ResponseEntity<Map<String, Object>> response = examController.getActiveExam();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("hasActiveExam", true);
        assertThat(response.getBody()).containsEntry("activeExam", activeExam);
        verify(examService).getActiveExamResponse(9L);
    }
}