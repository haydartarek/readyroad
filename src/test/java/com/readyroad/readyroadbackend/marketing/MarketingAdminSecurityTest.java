package com.readyroad.readyroadbackend.marketing;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
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
class MarketingAdminSecurityTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;

    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUpUsers() throws Exception {
        userToken = createUserAndLogin("marketing-user", "marketing-user@test.local", Role.USER);
        adminToken = createUserAndLogin("marketing-admin", "marketing-admin@test.local", Role.ADMIN);
    }

    @Test
    void rejectsUnauthenticatedAccess() throws Exception {
        mockMvc.perform(get("/api/admin/marketing/infrastructure"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsNonAdminAccess() throws Exception {
        mockMvc.perform(get("/api/admin/marketing/infrastructure")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void allowsAdminAccessWithoutExposingPayloads() throws Exception {
        mockMvc.perform(get("/api/admin/marketing/infrastructure")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.batchSize").value(10))
                .andExpect(jsonPath("$.lockTtlSeconds").value(600))
                .andExpect(jsonPath("$.tasksByStatus.PENDING").value(0));
    }

    private String createUserAndLogin(String username, String email, Role role) throws Exception {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode("local-test-password"));
        user.setFullName(username);
        user.setRole(role);
        user.setIsActive(true);
        user.setIsLocked(false);
        userRepository.saveAndFlush(user);

        String body = objectMapper.writeValueAsString(new LoginRequest(username, "local-test-password"));
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }

    private record LoginRequest(String username, String password) {}
}
