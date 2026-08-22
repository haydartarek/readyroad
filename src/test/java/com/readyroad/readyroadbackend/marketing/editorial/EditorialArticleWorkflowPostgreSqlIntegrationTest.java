package com.readyroad.readyroadbackend.marketing.editorial;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.approval.ApprovalMetadata;
import com.readyroad.readyroadbackend.marketing.domain.TaskPriority;
import com.readyroad.readyroadbackend.marketing.task.CreateMarketingTaskCommand;
import com.readyroad.readyroadbackend.marketing.task.TaskCreationService;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
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
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest
@ActiveProfiles("postgresql")
@Testcontainers
class EditorialArticleWorkflowPostgreSqlIntegrationTest {

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
                () -> "ZWRpdG9yaWFsLXdvcmtmbG93LXRlc3Qta2V5LW5vdC1mb3ItcHJvZHVjdGlvbg==");
        registry.add("readyroad.admin.default-password", () -> "Workflow-Test-Only-2026!");
    }

    @Autowired DataSource dataSource;
    @Autowired EditorialArticleWorkflowService workflow;
    @Autowired TaskCreationService taskCreationService;
    @Autowired ObjectMapper objectMapper;

    private JdbcTemplate jdbc;

    @BeforeEach
    void resetWorkflowData() {
        jdbc = new JdbcTemplate(dataSource);
        jdbc.execute("TRUNCATE article_publications, article_versions, article_briefs, articles RESTART IDENTITY");
        jdbc.update("DELETE FROM audit_logs WHERE event_type = 'EDITORIAL_ARTICLE_STATE_CHANGED'");
    }

    @Test
    void followsTheApprovedWorkflowAndAuditsEachRealTransitionOnce() {
        long articleId = insertArticle(1, EditorialArticleState.IDEA);
        insertApprovedBrief(articleId, true);

        assertChanged(articleId, EditorialArticleState.PLANNED);
        assertChanged(articleId, EditorialArticleState.BRIEF_READY);
        assertChanged(articleId, EditorialArticleState.DRAFTING);
        insertCurrentVersion(articleId, "EN");
        assertChanged(articleId, EditorialArticleState.DRAFT_READY);
        assertChanged(articleId, EditorialArticleState.FACT_CHECK_REQUIRED);
        assertChanged(articleId, EditorialArticleState.LEGAL_REVIEW_REQUIRED);
        assertChanged(articleId, EditorialArticleState.TRANSLATION_REQUIRED);
        for (String language : List.of("AR", "NL", "FR")) {
            insertCurrentVersion(articleId, language);
        }
        assertChanged(articleId, EditorialArticleState.IMAGE_REQUIRED);

        assertThatThrownBy(() -> transition(
                articleId,
                EditorialArticleState.WAITING_APPROVAL,
                EnumSet.complementOf(EnumSet.of(EditorialArticleQualityGate.NO_UNSUPPORTED_LEGAL_CLAIMS))))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("quality gates");

        var waiting = transition(
                articleId,
                EditorialArticleState.WAITING_APPROVAL,
                EnumSet.allOf(EditorialArticleQualityGate.class));
        var repeated = transition(
                articleId,
                EditorialArticleState.WAITING_APPROVAL,
                EnumSet.allOf(EditorialArticleQualityGate.class));

        assertThat(waiting.changed()).isTrue();
        assertThat(repeated.changed()).isFalse();
        assertThat(repeated.state()).isEqualTo(EditorialArticleState.WAITING_APPROVAL);
        assertThat(auditCount(articleId)).isEqualTo(9);
        assertThat(jdbc.queryForObject("""
                SELECT count(*) FROM audit_logs
                WHERE event_type = 'EDITORIAL_ARTICLE_STATE_CHANGED'
                  AND entity_id = ?
                  AND task_id IS NOT NULL
                  AND correlation_id IS NOT NULL
                """, Integer.class, String.valueOf(articleId))).isEqualTo(9);
    }

    @Test
    void choosesTheLegalBranchOnlyWhenTheApprovedBriefRequiresIt() {
        long legalArticle = insertArticle(2, EditorialArticleState.FACT_CHECK_REQUIRED);
        insertApprovedBrief(legalArticle, true);
        long ordinaryArticle = insertArticle(3, EditorialArticleState.FACT_CHECK_REQUIRED);
        insertApprovedBrief(ordinaryArticle, false);

        assertThatThrownBy(() -> transition(
                legalArticle, EditorialArticleState.TRANSLATION_REQUIRED, EnumSet.noneOf(EditorialArticleQualityGate.class)))
                .isInstanceOf(InvalidEditorialArticleStateTransitionException.class)
                .hasMessageContaining("LEGAL_REVIEW_REQUIRED");
        assertChanged(legalArticle, EditorialArticleState.LEGAL_REVIEW_REQUIRED);

        assertThatThrownBy(() -> transition(
                ordinaryArticle, EditorialArticleState.LEGAL_REVIEW_REQUIRED,
                EnumSet.noneOf(EditorialArticleQualityGate.class)))
                .isInstanceOf(InvalidEditorialArticleStateTransitionException.class)
                .hasMessageContaining("TRANSLATION_REQUIRED");
        assertChanged(ordinaryArticle, EditorialArticleState.TRANSLATION_REQUIRED);
    }

    @Test
    void blocksMissingPrerequisitesAndIllegalStateSkipping() {
        long articleId = insertArticle(4, EditorialArticleState.PLANNED);

        assertThatThrownBy(() -> transition(
                articleId, EditorialArticleState.BRIEF_READY, EnumSet.noneOf(EditorialArticleQualityGate.class)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("approved brief");

        setState(articleId, EditorialArticleState.DRAFTING);
        assertThatThrownBy(() -> transition(
                articleId, EditorialArticleState.DRAFT_READY, EnumSet.noneOf(EditorialArticleQualityGate.class)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("canonical current version");

        setState(articleId, EditorialArticleState.TRANSLATION_REQUIRED);
        insertCurrentVersion(articleId, "EN");
        assertThatThrownBy(() -> transition(
                articleId, EditorialArticleState.IMAGE_REQUIRED, EnumSet.noneOf(EditorialArticleQualityGate.class)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("AR, NL, FR and EN");

        setState(articleId, EditorialArticleState.IDEA);
        assertThatThrownBy(() -> transition(
                articleId, EditorialArticleState.PUBLISHED, EnumSet.noneOf(EditorialArticleQualityGate.class)))
                .isInstanceOf(InvalidEditorialArticleStateTransitionException.class);
        assertThat(auditCount(articleId)).isZero();
    }

    @Test
    void serializesConcurrentDuplicateTransitionsWithoutDuplicateAudit() throws Exception {
        long articleId = insertArticle(5, EditorialArticleState.IDEA);
        CountDownLatch start = new CountDownLatch(1);

        try (var executor = Executors.newFixedThreadPool(2)) {
            var first = executor.submit(() -> {
                start.await();
                return transition(
                        articleId, EditorialArticleState.PLANNED,
                        EnumSet.noneOf(EditorialArticleQualityGate.class));
            });
            var second = executor.submit(() -> {
                start.await();
                return transition(
                        articleId, EditorialArticleState.PLANNED,
                        EnumSet.noneOf(EditorialArticleQualityGate.class));
            });
            start.countDown();

            assertThat(List.of(first.get().changed(), second.get().changed()))
                    .containsExactlyInAnyOrder(true, false);
        }

        assertThat(state(articleId)).isEqualTo(EditorialArticleState.PLANNED);
        assertThat(auditCount(articleId)).isOne();
    }

    @Test
    void requiresANewCanonicalDraftForARecommendedUpdateCycle() {
        long articleId = insertArticle(8, EditorialArticleState.UPDATE_RECOMMENDED);
        insertCurrentVersion(articleId, "EN");

        assertChanged(articleId, EditorialArticleState.DRAFTING);
        assertThatThrownBy(() -> transition(
                articleId, EditorialArticleState.DRAFT_READY,
                EnumSet.noneOf(EditorialArticleQualityGate.class)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("fresh canonical current version");

        jdbc.update("UPDATE article_versions SET is_current = FALSE WHERE article_id = ?", articleId);
        insertCurrentVersion(articleId, "EN");
        assertChanged(articleId, EditorialArticleState.DRAFT_READY);
    }

    @Test
    void keepsRejectedAndArchivedArticlesTerminalAndRequiresAReason() {
        long rejected = insertArticle(6, EditorialArticleState.WAITING_APPROVAL);
        long update = insertArticle(7, EditorialArticleState.UPDATE_RECOMMENDED);

        assertThatThrownBy(() -> workflow.transition(new EditorialArticleWorkflowDtos.TransitionRequest(
                rejected, EditorialArticleState.REJECTED, task(rejected).id(), "workflow-terminal",
                "owner", " ", EnumSet.noneOf(EditorialArticleQualityGate.class))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("reason");
        assertChanged(rejected, EditorialArticleState.REJECTED);
        assertChanged(update, EditorialArticleState.ARCHIVED);

        assertThatThrownBy(() -> transition(
                rejected, EditorialArticleState.PLANNED, EnumSet.noneOf(EditorialArticleQualityGate.class)))
                .isInstanceOf(InvalidEditorialArticleStateTransitionException.class);
        assertThatThrownBy(() -> transition(
                update, EditorialArticleState.DRAFTING, EnumSet.noneOf(EditorialArticleQualityGate.class)))
                .isInstanceOf(InvalidEditorialArticleStateTransitionException.class);
    }

    @Test
    void rejectsATaskContextThatBelongsToAnotherArticle() {
        long first = insertArticle(9, EditorialArticleState.IDEA);
        long second = insertArticle(10, EditorialArticleState.IDEA);
        TaskContext wrongTask = task(first);

        assertThatThrownBy(() -> workflow.transition(new EditorialArticleWorkflowDtos.TransitionRequest(
                second, EditorialArticleState.PLANNED, wrongTask.id(), wrongTask.correlationId(),
                "workflow-test", "Mismatched task context",
                EnumSet.noneOf(EditorialArticleQualityGate.class))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not match");
        assertThat(state(second)).isEqualTo(EditorialArticleState.IDEA);
        assertThat(auditCount(second)).isZero();
    }

    private void assertChanged(long articleId, EditorialArticleState target) {
        assertThat(transition(articleId, target, EnumSet.noneOf(EditorialArticleQualityGate.class)).changed()).isTrue();
        assertThat(state(articleId)).isEqualTo(target);
    }

    private EditorialArticleWorkflowDtos.TransitionResult transition(
            long articleId,
            EditorialArticleState target,
            EnumSet<EditorialArticleQualityGate> gates) {
        String reason = target == EditorialArticleState.REJECTED || target == EditorialArticleState.ARCHIVED
                ? "Owner-reviewed terminal decision"
                : "Targeted workflow integration test";
        TaskContext task = task(articleId);
        return workflow.transition(new EditorialArticleWorkflowDtos.TransitionRequest(
                articleId, target, task.id(), task.correlationId(), "workflow-test", reason, gates));
    }

    private TaskContext task(long articleId) {
        String unique = UUID.randomUUID().toString();
        var result = taskCreationService.create(new CreateMarketingTaskCommand(
                "EDITORIAL",
                "ARTICLE_DRAFT_CREATE",
                objectMapper.createObjectNode()
                        .put("articleId", articleId),
                TaskPriority.NORMAL,
                null,
                "workflow-test",
                "workflow-" + unique,
                "workflow-" + unique,
                null,
                "ARTICLE",
                String.valueOf(articleId),
                ApprovalMetadata.standingOwnerAuthorization()));
        return new TaskContext(result.task().getId(), result.task().getCorrelationId());
    }

    private long insertArticle(int backlogOrder, EditorialArticleState state) {
        return jdbc.queryForObject("""
                INSERT INTO articles (
                    article_topic_id, canonical_key, lifecycle_state, canonical_language
                ) VALUES ((SELECT id FROM article_topics WHERE official_backlog_order = ?), ?, ?, 'EN')
                RETURNING id
                """, Long.class, backlogOrder, "workflow-" + backlogOrder, state.name());
    }

    private void insertApprovedBrief(long articleId, boolean legalReviewRequired) {
        jdbc.update("""
                INSERT INTO article_briefs (
                    article_topic_id, target_language, search_intent, working_title, purpose,
                    primary_cta, legal_review_required, status
                ) SELECT article_topic_id, 'EN', 'INFORMATIONAL', 'Workflow title',
                         'Verify the editorial workflow', 'Start learning', ?, 'APPROVED'
                  FROM articles WHERE id = ?
                """, legalReviewRequired, articleId);
    }

    private void insertCurrentVersion(long articleId, String language) {
        Integer next = jdbc.queryForObject("""
                SELECT COALESCE(max(version_number), 0) + 1
                FROM article_versions WHERE article_id = ? AND language = ?
                """, Integer.class, articleId, language);
        jdbc.update("""
                INSERT INTO article_versions (
                    article_id, version_number, language, title, body, status, is_current, created_by
                ) VALUES (?, ?, ?, ?, ?, 'DRAFT_READY', TRUE, 'workflow-test')
                """, articleId, next, language, language + " workflow title", language + " workflow body");
    }

    private void setState(long articleId, EditorialArticleState state) {
        jdbc.update("UPDATE articles SET lifecycle_state = ? WHERE id = ?", state.name(), articleId);
    }

    private EditorialArticleState state(long articleId) {
        return EditorialArticleState.valueOf(jdbc.queryForObject(
                "SELECT lifecycle_state FROM articles WHERE id = ?", String.class, articleId));
    }

    private int auditCount(long articleId) {
        return jdbc.queryForObject("""
                SELECT count(*) FROM audit_logs
                WHERE event_type = 'EDITORIAL_ARTICLE_STATE_CHANGED'
                  AND entity_type = 'EDITORIAL_ARTICLE'
                  AND entity_id = ?
                """, Integer.class, String.valueOf(articleId));
    }

    private record TaskContext(long id, String correlationId) {}
}
