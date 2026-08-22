package com.readyroad.readyroadbackend.marketing.editorial;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.readyroad.readyroadbackend.marketing.approval.ApprovalService;
import com.readyroad.readyroadbackend.marketing.domain.AgentTask;
import com.readyroad.readyroadbackend.marketing.domain.TaskStatus;
import com.readyroad.readyroadbackend.marketing.repository.AgentApprovalRepository;
import com.readyroad.readyroadbackend.marketing.repository.AgentTaskRepository;
import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest
@ActiveProfiles("postgresql")
@Testcontainers
class EditorialArticleApprovalPostgreSqlIntegrationTest {

    @Container
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.flyway.url", POSTGRES::getJdbcUrl);
        registry.add("spring.flyway.user", POSTGRES::getUsername);
        registry.add("spring.flyway.password", POSTGRES::getPassword);
        registry.add("readyroad.marketing.enabled", () -> "false");
        registry.add("jwt.secret-key",
                () -> "ZWRpdG9yaWFsLWFwcHJvdmFsLXRlc3Qta2V5LW5vdC1mb3ItcHJvZHVjdGlvbg==");
        registry.add("readyroad.admin.default-password", () -> "Approval-Test-Only-2026!");
    }

    @Autowired DataSource dataSource;
    @Autowired EditorialArticleApprovalService approvalRequestService;
    @Autowired EditorialArticleApprovalTaskHandler approvalTaskHandler;
    @Autowired EditorialEditorService editorService;
    @Autowired ApprovalService approvalService;
    @Autowired AgentTaskRepository taskRepository;
    @Autowired AgentApprovalRepository approvalRepository;

    private JdbcTemplate jdbc;

    @BeforeEach
    void resetApprovalData() {
        jdbc = new JdbcTemplate(dataSource);
        jdbc.update("DELETE FROM audit_logs WHERE event_type IN "
                + "('EDITORIAL_ARTICLE_STATE_CHANGED', 'EDITORIAL_ARTICLE_DRAFT_SAVED')");
        jdbc.update("DELETE FROM agent_approvals WHERE task_id IN "
                + "(SELECT id FROM agent_tasks WHERE task_type = 'ARTICLE_APPROVAL')");
        jdbc.update("DELETE FROM agent_tasks WHERE task_type = 'ARTICLE_APPROVAL'");
        jdbc.execute("TRUNCATE article_versions, article_briefs, articles RESTART IDENTITY");
    }

    @Test
    void bindsApprovalToCurrentVersionsAndCompletesIdempotentlyAfterHumanApproval() {
        long articleId = eligibleArticle(1);
        var request = request(allGates(), "All article quality gates were reviewed");

        var first = approvalRequestService.request(articleId, request, "owner");
        var duplicate = approvalRequestService.request(articleId, request, "owner");

        assertThat(duplicate.id()).isEqualTo(first.id());
        AgentTask waiting = taskRepository.findById(first.id()).orElseThrow();
        assertThat(waiting.getStatus()).isEqualTo(TaskStatus.WAITING_APPROVAL);
        assertThat(waiting.isRequiresApproval()).isTrue();
        assertThat(waiting.getPayload().path("articleId").asLong()).isEqualTo(articleId);
        assertThat(waiting.getPayload().path("versions")).hasSize(4);
        assertThat(waiting.getPayload().has("body")).isFalse();
        assertThat(state(articleId)).isEqualTo(EditorialArticleState.WAITING_APPROVAL);
        assertThat(taskCount()).isOne();

        approvalService.approve(first.id(), "owner", "Approved exact article version snapshot");
        AgentTask approved = taskRepository.findById(first.id()).orElseThrow();
        assertThat(approved.getStatus()).isEqualTo(TaskStatus.APPROVED);
        assertThat(state(articleId)).isEqualTo(EditorialArticleState.WAITING_APPROVAL);

        approvalTaskHandler.execute(claimed(approved));
        approvalTaskHandler.execute(claimed(approved));

        assertThat(state(articleId)).isEqualTo(EditorialArticleState.APPROVED);
        assertThat(articleStateAuditCount(articleId)).isEqualTo(2);
        var recorded = approvalRepository.findByTaskIdAndPayloadVersion(first.id(), 1).orElseThrow();
        assertThat(recorded.getDecision().name()).isEqualTo("APPROVED");
        assertThat(recorded.getPayloadSnapshot()).isEqualTo(waiting.getPayload());
    }

    @Test
    void rejectionMovesTheExactWaitingArticleToRejectedWithoutWorkerExecution() {
        long articleId = eligibleArticle(2);
        var response = approvalRequestService.request(
                articleId, request(allGates(), "Ready for owner decision"), "editor");

        approvalService.reject(response.id(), "owner", "Legal wording needs revision");

        assertThat(taskRepository.findById(response.id()).orElseThrow().getStatus())
                .isEqualTo(TaskStatus.REJECTED);
        assertThat(state(articleId)).isEqualTo(EditorialArticleState.REJECTED);
        assertThat(articleStateAuditCount(articleId)).isEqualTo(2);
    }

    @Test
    void refusesMissingQualityEvidenceBeforeCreatingAnApprovalTask() {
        long articleId = eligibleArticle(3);
        EnumSet<EditorialArticleQualityGate> incomplete = allGates();
        incomplete.remove(EditorialArticleQualityGate.NO_UNSUPPORTED_LEGAL_CLAIMS);

        assertThatThrownBy(() -> approvalRequestService.request(
                articleId, request(incomplete, "Incomplete evidence"), "owner"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("quality gates");

        assertThat(state(articleId)).isEqualTo(EditorialArticleState.IMAGE_REQUIRED);
        assertThat(taskCount()).isZero();
    }

    @Test
    void staleCurrentVersionPreventsApprovalAndKeepsTheTaskPending() {
        long articleId = eligibleArticle(4);
        var response = approvalRequestService.request(
                articleId, request(allGates(), "Version snapshot ready"), "editor");
        replaceCurrentVersion(articleId, "EN");

        assertThatThrownBy(() -> approvalService.approve(
                response.id(), "owner", "Attempt to approve stale content"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("current article versions changed");

        assertThat(taskRepository.findById(response.id()).orElseThrow().getStatus())
                .isEqualTo(TaskStatus.WAITING_APPROVAL);
        assertThat(state(articleId)).isEqualTo(EditorialArticleState.WAITING_APPROVAL);
    }

    @Test
    void preventsEditorChangesWhileAnExactVersionSnapshotAwaitsApproval() {
        long articleId = eligibleArticle(5);
        approvalRequestService.request(
                articleId, request(allGates(), "Lock this version for review"), "editor");

        assertThatThrownBy(() -> editorService.save(
                topicId(articleId), "EN",
                new EditorialEditorDtos.SaveRequest(
                        "Changed title", "changed", "Changed", "Changed body", 1),
                "editor"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409 CONFLICT");
    }

    @Test
    void concurrentRepeatedRequestsResolveToOneApprovalTask() throws Exception {
        long articleId = eligibleArticle(6);
        var request = request(allGates(), "Concurrent owner review");
        CountDownLatch start = new CountDownLatch(1);

        try (var executor = Executors.newFixedThreadPool(2)) {
            var first = executor.submit(() -> {
                start.await();
                return approvalRequestService.request(articleId, request, "owner-a");
            });
            var second = executor.submit(() -> {
                start.await();
                return approvalRequestService.request(articleId, request, "owner-b");
            });
            start.countDown();

            assertThat(List.of(first.get().id(), second.get().id())).hasSize(2).containsOnly(first.get().id());
        }

        assertThat(taskCount()).isOne();
        assertThat(state(articleId)).isEqualTo(EditorialArticleState.WAITING_APPROVAL);
    }

    private EditorialArticleApprovalDtos.Request request(
            EnumSet<EditorialArticleQualityGate> gates,
            String reason) {
        return new EditorialArticleApprovalDtos.Request(gates, reason);
    }

    private EnumSet<EditorialArticleQualityGate> allGates() {
        return EnumSet.allOf(EditorialArticleQualityGate.class);
    }

    private long eligibleArticle(int backlogOrder) {
        Long articleId = jdbc.queryForObject("""
                INSERT INTO articles (
                    article_topic_id, canonical_key, lifecycle_state, canonical_language
                ) VALUES ((SELECT id FROM article_topics WHERE official_backlog_order = ?), ?, 'IMAGE_REQUIRED', 'EN')
                RETURNING id
                """, Long.class, backlogOrder, "approval-" + backlogOrder);
        for (String language : List.of("AR", "NL", "FR", "EN")) {
            jdbc.update("""
                    INSERT INTO article_versions (
                        article_id, version_number, language, title, slug, summary, body,
                        status, is_current, created_by
                    ) VALUES (?, 1, ?, ?, ?, ?, ?, 'DRAFT_READY', TRUE, 'editor')
                    """, articleId, language, language + " title", "approval-" + backlogOrder + "-" + language,
                    language + " summary", language + " body");
        }
        return articleId;
    }

    private void replaceCurrentVersion(long articleId, String language) {
        jdbc.update("UPDATE article_versions SET is_current = FALSE WHERE article_id = ? AND language = ?",
                articleId, language);
        jdbc.update("""
                INSERT INTO article_versions (
                    article_id, version_number, language, title, body, status, is_current, created_by
                ) VALUES (?, 2, ?, ?, ?, 'DRAFT_READY', TRUE, 'another-editor')
                """, articleId, language, language + " changed", language + " changed body");
    }

    private EditorialArticleState state(long articleId) {
        return EditorialArticleState.valueOf(jdbc.queryForObject(
                "SELECT lifecycle_state FROM articles WHERE id = ?", String.class, articleId));
    }

    private long topicId(long articleId) {
        return jdbc.queryForObject(
                "SELECT article_topic_id FROM articles WHERE id = ?", Long.class, articleId);
    }

    private int taskCount() {
        return jdbc.queryForObject(
                "SELECT count(*) FROM agent_tasks WHERE task_type = 'ARTICLE_APPROVAL'", Integer.class);
    }

    private int articleStateAuditCount(long articleId) {
        return jdbc.queryForObject("""
                SELECT count(*) FROM audit_logs
                WHERE event_type = 'EDITORIAL_ARTICLE_STATE_CHANGED'
                  AND entity_type = 'EDITORIAL_ARTICLE'
                  AND entity_id = ?
                """, Integer.class, String.valueOf(articleId));
    }

    private ClaimedTask claimed(AgentTask task) {
        return new ClaimedTask(
                task.getId(), task.getAgentType(), task.getTaskType(), task.getPayload(),
                task.getPayloadVersion(), task.getPriority(), 1, task.getCorrelationId());
    }
}
