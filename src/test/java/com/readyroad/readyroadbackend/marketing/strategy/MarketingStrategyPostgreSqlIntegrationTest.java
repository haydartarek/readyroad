package com.readyroad.readyroadbackend.marketing.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import com.readyroad.readyroadbackend.marketing.approval.ApprovalService;
import com.readyroad.readyroadbackend.marketing.domain.AgentDefinition;
import com.readyroad.readyroadbackend.marketing.domain.AgentTask;
import com.readyroad.readyroadbackend.marketing.domain.TaskStatus;
import com.readyroad.readyroadbackend.marketing.repository.AgentDefinitionRepository;
import com.readyroad.readyroadbackend.marketing.repository.AgentTaskRepository;
import com.readyroad.readyroadbackend.marketing.repository.MarketingAuditLogRepository;
import com.readyroad.readyroadbackend.marketing.strategy.domain.MarketingUsp;
import com.readyroad.readyroadbackend.marketing.strategy.repository.MarketingUspRepository;
import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import com.readyroad.readyroadbackend.marketing.task.TaskClaimService;
import com.readyroad.readyroadbackend.marketing.task.TaskExecutionService;
import com.readyroad.readyroadbackend.marketing.worker.MarketingTaskDispatcher;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("postgresql")
@Testcontainers
class MarketingStrategyPostgreSqlIntegrationTest {

    private static final Set<String> EXPECTED_TABLES = Set.of(
            "marketing_usp",
            "marketing_icp",
            "marketing_positioning",
            "marketing_content_pillars",
            "marketing_funnel_stages",
            "marketing_conversion_goals",
            "social_proof_items");

    @Container
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.flyway.url", POSTGRES::getJdbcUrl);
        registry.add("spring.flyway.user", POSTGRES::getUsername);
        registry.add("spring.flyway.password", POSTGRES::getPassword);
        registry.add("readyroad.marketing.enabled", () -> "false");
        registry.add("jwt.secret-key",
                () -> "bWFya2V0aW5nLXN0cmF0ZWd5LXRlc3Qtc2VjcmV0LTIwMjYtcmVhZHlyb2Fk");
        registry.add("readyroad.admin.default-password", () -> "Marketing-Strategy-Test-2026!");
    }

    @Autowired DataSource dataSource;
    @Autowired AgentDefinitionRepository definitionRepository;
    @Autowired AgentTaskRepository taskRepository;
    @Autowired MarketingAuditLogRepository auditRepository;
    @Autowired MarketingUspRepository uspRepository;
    @Autowired MarketingStrategyChangeService changeService;
    @Autowired ApprovalService approvalService;
    @Autowired TaskClaimService claimService;
    @Autowired MarketingTaskDispatcher dispatcher;
    @Autowired TaskExecutionService executionService;

    @BeforeEach
    void ensureStrategyAgentEnabled() {
        AgentDefinition definition = definitionRepository.findByAgentType(MarketingStrategyChangeService.AGENT_TYPE)
                .orElseGet(() -> new AgentDefinition(
                        MarketingStrategyChangeService.AGENT_TYPE,
                        "RijVia Marketing Strategy Engine",
                        true));
        definition.setEnabled(true);
        definitionRepository.saveAndFlush(definition);
    }

    @Test
    void migrationCreatesTheSevenStrategyTablesAndApprovedReferenceData() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        List<String> tables = jdbc.queryForList("""
                SELECT table_name
                FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND (table_name LIKE 'marketing_%' OR table_name = 'social_proof_items')
                """, String.class);

        assertThat(new HashSet<>(tables)).containsAll(EXPECTED_TABLES);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM marketing_icp", Integer.class)).isEqualTo(6);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM marketing_content_pillars", Integer.class)).isEqualTo(12);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM marketing_funnel_stages", Integer.class)).isEqualTo(9);
        assertThat(jdbc.queryForObject(
                "SELECT COUNT(*) FROM marketing_conversion_goals WHERE active", Integer.class)).isEqualTo(8);
        assertThat(jdbc.queryForObject("""
                SELECT COUNT(*)
                FROM marketing_conversion_goals goal
                JOIN marketing_funnel_stages stage ON stage.id = goal.funnel_stage_id
                WHERE goal.active AND stage.stage_key = 'PAID_CONVERSION'
                """, Integer.class)).isZero();
        assertThat(jdbc.queryForList("""
                SELECT stage.stage_key
                FROM marketing_conversion_goals goal
                JOIN marketing_funnel_stages stage ON stage.id = goal.funnel_stage_id
                WHERE goal.active
                ORDER BY stage.sequence_number
                """, String.class)).containsExactly(
                        "AWARENESS", "DISCOVERY", "EDUCATION", "PRACTICE",
                        "ACCOUNT_CONVERSION", "EXAM_USAGE", "RETENTION", "ADVOCACY");
        assertThat(jdbc.queryForObject("""
                SELECT COUNT(*) FROM marketing_usp
                WHERE active AND title = 'RijVia learning platform'
                  AND evidence_reference = 'https://rijvia.be'
                """, Integer.class)).isOne();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM social_proof_items", Integer.class)).isZero();
    }

    @Test
    void rebrandMigrationUpdatesTheActiveProductIdentity() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);

        assertThat(jdbc.queryForObject(
                "SELECT site_name FROM admin_system_settings ORDER BY id LIMIT 1", String.class))
                .isEqualTo("RijVia");
        assertThat(jdbc.queryForObject(
                "SELECT statement FROM marketing_positioning WHERE active = TRUE", String.class))
                .contains("RijVia")
                .doesNotContain("ReadyRoad");
        assertThat(jdbc.queryForObject(
                "SELECT name FROM marketing_content_pillars WHERE pillar_key = 'RIJVIA_EDUCATIONAL_VIDEOS'",
                String.class))
                .isEqualTo("فيديوهات RijVia التعليمية");
        assertThat(jdbc.queryForObject(
                "SELECT COUNT(*) FROM agent_definitions WHERE display_name LIKE '%ReadyRoad%'",
                Integer.class))
                .isZero();
        assertThat(jdbc.queryForObject("""
                SELECT COUNT(*) FROM marketing_usp
                WHERE active = TRUE
                  AND concat_ws(' ', title, description, evidence_type, evidence_reference) ~* 'ReadyRoad'
                """, Integer.class))
                .isZero();
        assertThat(jdbc.queryForObject("""
                SELECT COUNT(*) FROM marketing_conversion_goals
                WHERE active = TRUE
                  AND concat_ws(' ', name, description, primary_cta) ~* 'ReadyRoad'
                """, Integer.class))
                .isZero();
        assertThat(jdbc.queryForObject("""
                SELECT COUNT(*) FROM audit_logs
                WHERE event_type = 'RIJVIA_MARKETING_BRAND_IDENTITY_COMPLETED'
                """, Integer.class))
                .isOne();
    }

    @Test
    void approvedStrategyChangeRunsThroughTheExistingTaskWorkerAndAuditFlow() {
        long uspCountBefore = uspRepository.count();
        long auditCountBefore = auditRepository.countByEventType("STRATEGY_UPDATED");
        String idempotencyKey = "strategy-usp-integration-" + System.nanoTime();
        StrategyChangeRequest request = new StrategyChangeRequest(
                StrategyResourceType.USP,
                null,
                Map.of(
                        "title", "Four-language learning",
                        "description", "ReadyRoad supports Arabic, Dutch, French and English.",
                        "evidenceType", "READYROAD_FEATURE",
                        "evidenceReference", "SUPPORTED_LANGUAGES",
                        "active", true,
                        "priority", 2),
                idempotencyKey);

        AgentTask task = changeService.requestChange(request, "marketing-admin").task();

        assertThat(task.getStatus()).isEqualTo(TaskStatus.WAITING_APPROVAL);
        assertThat(uspRepository.count()).isEqualTo(uspCountBefore);

        approvalService.approve(task.getId(), "marketing-admin", "Approved strategy evidence");
        ClaimedTask claimed = claimService.claimNextBatch("strategy-test-worker").stream()
                .filter(candidate -> candidate.taskId().equals(task.getId()))
                .findFirst()
                .orElseThrow();
        dispatcher.dispatch(claimed);
        executionService.complete(task.getId(), "strategy-test-worker");

        assertThat(taskRepository.findById(task.getId()).orElseThrow().getStatus())
                .isEqualTo(TaskStatus.COMPLETED);
        assertThat(uspRepository.findAll().stream()
                        .filter(usp -> "Four-language learning".equals(usp.getTitle()))
                        .toList())
                .singleElement()
                .satisfies(usp -> {
                    assertThat(usp.getTitle()).isEqualTo("Four-language learning");
                    assertThat(usp.getApprovedBy()).isEqualTo("marketing-admin");
                });
        assertThat(uspRepository.count()).isEqualTo(uspCountBefore + 1);
        assertThat(auditRepository.countByEventType("STRATEGY_UPDATED")).isEqualTo(auditCountBefore + 1);
    }

    @Test
    void updateWithoutActiveFieldPreservesTheExistingActivationState() {
        MarketingUsp existing = new MarketingUsp();
        existing.setTitle("Existing evidence");
        existing.setDescription("Verified existing evidence.");
        existing.setEvidenceType("READYROAD_FEATURE");
        existing.setEvidenceReference("EXISTING_REFERENCE");
        existing.setPriority((short) 1);
        existing.setActive(false);
        existing.setApprovedBy("marketing-admin");
        existing = uspRepository.saveAndFlush(existing);

        StrategyChangeRequest request = new StrategyChangeRequest(
                StrategyResourceType.USP,
                String.valueOf(existing.getId()),
                Map.of(
                        "title", "Updated evidence",
                        "description", "Verified updated evidence.",
                        "evidenceType", "READYROAD_FEATURE",
                        "evidenceReference", "UPDATED_REFERENCE",
                        "priority", 2),
                "strategy-usp-preserve-active-" + System.nanoTime());

        AgentTask task = changeService.requestChange(request, "marketing-admin").task();
        approvalService.approve(task.getId(), "marketing-admin", "Approved evidence update");
        ClaimedTask claimed = claimService.claimNextBatch("strategy-active-test-worker").stream()
                .filter(candidate -> candidate.taskId().equals(task.getId()))
                .findFirst()
                .orElseThrow();
        dispatcher.dispatch(claimed);
        executionService.complete(task.getId(), "strategy-active-test-worker");

        MarketingUsp updated = uspRepository.findById(existing.getId()).orElseThrow();
        assertThat(updated.getTitle()).isEqualTo("Updated evidence");
        assertThat(updated.isActive()).isFalse();
    }
}
