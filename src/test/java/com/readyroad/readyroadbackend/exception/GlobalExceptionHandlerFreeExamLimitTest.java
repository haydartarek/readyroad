package com.readyroad.readyroadbackend.exception;

import com.readyroad.readyroadbackend.service.BackendMessageService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class GlobalExceptionHandlerFreeExamLimitTest {

    @Test
    void freeLimitResponseIsMachineReadableForbidden() {
        BackendMessageService messages = mock(BackendMessageService.class);
        GlobalExceptionHandler handler = new GlobalExceptionHandler(messages);

        ResponseEntity<Map<String, Object>> response =
                handler.handleFreeExamLimitReached(
                        new FreeExamLimitReachedException(55L, 999L));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, Object> body = response.getBody();

        assertEquals(
                "FreeExamLimitReachedException",
                body.get("error"));

        assertEquals(
                "FREE_LIMIT_REACHED",
                body.get("code"));

        assertEquals(55L, body.get("examId"));
        assertEquals(999L, body.get("questionId"));

        assertEquals(
                "Free theory exam preview limit reached",
                body.get("message"));
    }

    @Test
    void freeLimitResponseCanRepresentPaywallWithoutQuestionId() {
        BackendMessageService messages = mock(BackendMessageService.class);
        GlobalExceptionHandler handler = new GlobalExceptionHandler(messages);

        ResponseEntity<Map<String, Object>> response =
                handler.handleFreeExamLimitReached(
                        new FreeExamLimitReachedException(55L, null));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("FREE_LIMIT_REACHED", response.getBody().get("code"));
        assertEquals(false, response.getBody().containsKey("questionId"));
    }
}