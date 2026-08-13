package com.readyroad.readyroadbackend.marketing;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.marketing.domain.AgentDefinition;
import com.readyroad.readyroadbackend.marketing.repository.AgentDefinitionRepository;
import com.readyroad.readyroadbackend.marketing.strategy.MarketingStrategyChangeService;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MarketingAdminSecurityTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired AgentDefinitionRepository agentDefinitionRepository;
    @Autowired PlatformTransactionManager transactionManager;

    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUpUsers() throws Exception {
        TransactionTemplate committedSetup = new TransactionTemplate(transactionManager);
        committedSetup.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        committedSetup.executeWithoutResult(status -> {
            AgentDefinition strategy = agentDefinitionRepository
                    .findByAgentType(MarketingStrategyChangeService.AGENT_TYPE)
                    .orElseGet(() -> new AgentDefinition(
                            MarketingStrategyChangeService.AGENT_TYPE,
                            "ReadyRoad Marketing Strategy Engine",
                            true));
            strategy.setEnabled(true);
            agentDefinitionRepository.saveAndFlush(strategy);
            AgentDefinition adminPlatform = agentDefinitionRepository
                    .findByAgentType("ADMIN_PLATFORM")
                    .orElseGet(() -> new AgentDefinition(
                            "ADMIN_PLATFORM",
                            "ReadyRoad Marketing Admin Platform",
                            true));
            adminPlatform.setEnabled(true);
            agentDefinitionRepository.saveAndFlush(adminPlatform);
        });
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
    void protectsAnalyticsAdminContractsWithTheExistingAdminBoundary() throws Exception {
        mockMvc.perform(get("/api/admin/marketing/analytics/status"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/admin/marketing/analytics/status")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void protectsYouTubeAdminContractsWithTheExistingAdminBoundary() throws Exception {
        mockMvc.perform(get("/api/admin/marketing/youtube/status"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/admin/marketing/youtube/status")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void protectsContentAdminContractsWithTheExistingAdminBoundary() throws Exception {
        mockMvc.perform(get("/api/admin/marketing/content/status"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/admin/marketing/content/status")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void protectsEditorialBacklogWithTheExistingAdminBoundary() throws Exception {
        mockMvc.perform(get("/api/admin/marketing/editorial/backlog"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/admin/marketing/editorial/backlog")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());

        for (String path : new String[] {
                "/api/admin/marketing/editorial/priorities",
                "/api/admin/marketing/editorial/priority-settings"
        }) {
            mockMvc.perform(get(path)).andExpect(status().isUnauthorized());
            mockMvc.perform(get(path).header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isForbidden());
        }

        mockMvc.perform(get("/api/admin/marketing/editorial/priority-settings")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weights.searchDemand").value(20))
                .andExpect(jsonPath("$.thresholds.p0").value(80));

        mockMvc.perform(post("/api/admin/marketing/editorial/priorities/recalculate")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idempotencyKey\":\"security-editorial-priority\"}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/admin/marketing/editorial/priority-settings")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"settings\":{},\"idempotencyKey\":\"security-editorial-settings\"}"))
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

    @Test
    void protectsAndExposesTheAdminBasicPlatformContracts() throws Exception {
        mockMvc.perform(get("/api/admin/marketing/overview"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/admin/marketing/overview")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());

        for (String path : new String[] {
                "/api/admin/marketing/overview",
                "/api/admin/marketing/agents",
                "/api/admin/marketing/tasks",
                "/api/admin/marketing/errors",
                "/api/admin/marketing/audit",
                "/api/admin/marketing/settings",
                "/api/admin/marketing/worker-health"
        }) {
            mockMvc.perform(get(path).header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(put("/api/admin/marketing/agents/STRATEGY/enabled")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"enabled\":false,\"idempotencyKey\":\"security-agent-control\"}"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.agentType").value("ADMIN_PLATFORM"))
                .andExpect(jsonPath("$.taskType").value("AGENT_ENABLED_CHANGE"))
                .andExpect(jsonPath("$.status").value("WAITING_APPROVAL"));
    }

    @Test
    void protectsTheStrategySnapshotWithTheExistingAdminRole() throws Exception {
        mockMvc.perform(get("/api/admin/marketing/strategy"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/admin/marketing/strategy")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/admin/marketing/strategy")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usps").isArray())
                .andExpect(jsonPath("$.conversionGoals").isArray());
    }

    @Test
    void createsAnApprovalBoundStrategyTaskWithoutApplyingTheChange() throws Exception {
        mockMvc.perform(post("/api/admin/marketing/strategy/change-requests")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "resourceType": "USP",
                                  "data": {
                                    "title": "Four-language learning",
                                    "description": "ReadyRoad supports four languages.",
                                    "evidenceType": "READYROAD_FEATURE",
                                    "evidenceReference": "SUPPORTED_LANGUAGES",
                                    "active": true,
                                    "priority": 2
                                  },
                                  "idempotencyKey": "security-test-strategy-usp"
                                }
                                """))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.agentType").value("STRATEGY"))
                .andExpect(jsonPath("$.taskType").value("STRATEGY_CHANGE"))
                .andExpect(jsonPath("$.status").value("WAITING_APPROVAL"))
                .andExpect(jsonPath("$.requiresApproval").value(true));
    }

    @Test
    void returnsBlockedStrategyContextInsteadOfInventingMissingValues() throws Exception {
        mockMvc.perform(post("/api/admin/marketing/strategy/context/resolve")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "uspId": 999,
                                  "icpId": "ICP-AR-BEGINNER",
                                  "contentPillarId": 1,
                                  "funnelStageId": 1,
                                  "conversionGoalId": 1
                                }
                                """))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.errorCode").value("BLOCKED_STRATEGY_CONTEXT"));
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
