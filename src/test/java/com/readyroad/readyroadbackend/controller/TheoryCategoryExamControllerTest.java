package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.service.TheoryCategoryPracticeService;
import com.readyroad.readyroadbackend.mapper.QuizQuestionMapper;
import com.readyroad.readyroadbackend.util.AuthenticationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;
import com.readyroad.readyroadbackend.domain.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TheoryCategoryExamControllerTest {
    private final TheoryCategoryPracticeService service = mock(TheoryCategoryPracticeService.class);
    private final QuizQuestionMapper mapper = mock(QuizQuestionMapper.class);
    private final AuthenticationUtil authentication = spy(new AuthenticationUtil());
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(
                new TheoryCategoryPracticeController(service, mapper, authentication)).build();
    }

    @Test
    void anonymousRequestsReceiveNoQuestionContent() throws Exception {
        mvc.perform(get("/api/practice/theory/categories/TH01/exam"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(service, mapper);
    }

    @Test
    void deniedEntitlementReturns403WithoutMappingAnyQuestions() throws Exception {
        when(service.getExamQuestions(42L, "TH01", "en"))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "PAID_ACCESS_REQUIRED"));
        User user = new User(); user.setId(42L);
        mvc.perform(get("/api/practice/theory/categories/TH01/exam")
                .principal(new UsernamePasswordAuthenticationToken(user, null, List.of())))
                .andExpect(status().isForbidden());
        verifyNoInteractions(mapper);
    }

    @Test
    void anonymousTokensCannotUseADevelopmentFallbackUser() throws Exception {
        mvc.perform(get("/api/practice/theory/categories/TH01/exam")
                .principal(new AnonymousAuthenticationToken("test", "anonymousUser",
                        List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS")))))
                .andExpect(status().isUnauthorized());
        verify(authentication, never()).extractUserId(any());
        verifyNoInteractions(service, mapper);
    }

    @Test
    void anonymousRequestsCannotSubmitCategoryAnswers() throws Exception {
        mvc.perform(post("/api/practice/theory/categories/TH01/questions/501/answer")
                .contentType("application/json").content("{\"selectedOptionId\": 1}"))
                .andExpect(status().isUnauthorized());
        verify(authentication, never()).extractUserId(any());
        verifyNoInteractions(service, mapper);
    }
}
