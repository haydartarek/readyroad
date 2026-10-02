package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.dto.response.LessonDetailResponse;
import com.readyroad.readyroadbackend.dto.response.LessonPageResponse;
import com.readyroad.readyroadbackend.service.LessonProgressService;
import com.readyroad.readyroadbackend.service.LessonService;
import com.readyroad.readyroadbackend.util.AuthenticationUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LessonControllerApiContractTest {

    @Mock private LessonService lessonService;
    @Mock private LessonProgressService progressService;
    @Mock private AuthenticationUtil authenticationUtil;
    @Mock private Authentication authentication;

    @Test
    void progressUsesServerPageCountInsteadOfClientTotal() {
        LessonController controller = new LessonController(lessonService, progressService, authenticationUtil);
        LessonDetailResponse lesson = new LessonDetailResponse(
                7L, "les-30", "car", "nl", "en", "fr", "ar",
                null, null, null, null, 30, 10,
                List.of(page(1), page(2), page(3)));
        when(authenticationUtil.extractUserId(authentication)).thenReturn(42L);
        when(lessonService.getLessonByIdOrCode("les-30")).thenReturn(lesson);

        controller.markPageRead("les-30", Map.of("totalPages", 1, "pageNumber", 1), authentication);

        verify(progressService).markPageRead(eq(42L), eq(7L), eq(3), eq(1));
    }

    private static LessonPageResponse page(int number) {
        return new LessonPageResponse(number * 1L, number, null, null, null, null,
                null, null, null, null, null);
    }
}
