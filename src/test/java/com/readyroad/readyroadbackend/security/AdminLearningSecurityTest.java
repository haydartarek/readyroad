package com.readyroad.readyroadbackend.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminLearningSecurityTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;

    private long studentId;
    private String userToken;
    private String moderatorToken;
    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        studentId = createUser("learning-student", "student-learning@test.local", Role.USER).getId();
        userToken = login("learning-student");
        createUser("learning-moderator", "moderator-learning@test.local", Role.MODERATOR);
        moderatorToken = login("learning-moderator");
        createUser("learning-admin", "admin-learning@test.local", Role.ADMIN);
        adminToken = login("learning-admin");
    }

    @Test
    void learningEndpointsAreAdminOnly() throws Exception {
        String endpoint = "/api/admin/learning/users/" + studentId + "/activity-availability";
        mockMvc.perform(get(endpoint)).andExpect(status().isUnauthorized());
        mockMvc.perform(get(endpoint).header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
        mockMvc.perform(get(endpoint).header("Authorization", "Bearer " + moderatorToken))
                .andExpect(status().isForbidden());
        mockMvc.perform(get(endpoint).header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        String bankHealth = "/api/admin/quiz/bank-health";
        mockMvc.perform(get(bankHealth)).andExpect(status().isUnauthorized());
        mockMvc.perform(get(bankHealth).header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
        mockMvc.perform(get(bankHealth).header("Authorization", "Bearer " + moderatorToken))
                .andExpect(status().isForbidden());
        mockMvc.perform(get(bankHealth).header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        for (String protectedEndpoint : new String[] {
                "/api/admin/learning/users/" + studentId + "/coverage",
                "/api/admin/learning/users/" + studentId + "/difficulty"
        }) {
            mockMvc.perform(get(protectedEndpoint)).andExpect(status().isUnauthorized());
            mockMvc.perform(get(protectedEndpoint).header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isForbidden());
            mockMvc.perform(get(protectedEndpoint).header("Authorization", "Bearer " + moderatorToken))
                    .andExpect(status().isForbidden());
        }

        for (var request : List.of(
                post("/api/admin/quiz/categories"),
                put("/api/admin/quiz/categories/1"))) {
            mockMvc.perform(request.contentType(MediaType.APPLICATION_JSON).content("{}"))
                    .andExpect(status().isUnauthorized());
            mockMvc.perform(request
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}")
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isForbidden());
            mockMvc.perform(request
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}")
                            .header("Authorization", "Bearer " + moderatorToken))
                    .andExpect(status().isForbidden());
        }
    }

    private User createUser(String username, String email, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFullName(username);
        user.setPasswordHash(passwordEncoder.encode("local-test-password"));
        user.setRole(role);
        user.setIsActive(true);
        user.setIsLocked(false);
        return userRepository.saveAndFlush(user);
    }

    private String login(String username) throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(username, "local-test-password"))))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }

    private record LoginRequest(String username, String password) {
    }
}
