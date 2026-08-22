package com.readyroad.readyroadbackend.marketing.editorial;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.readyroad.readyroadbackend.marketing.approval.ApprovalMetadata;
import com.readyroad.readyroadbackend.marketing.approval.ApprovalService;
import com.readyroad.readyroadbackend.marketing.domain.AgentTask;
import com.readyroad.readyroadbackend.marketing.domain.TaskPriority;
import com.readyroad.readyroadbackend.marketing.repository.AgentTaskRepository;
import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import com.readyroad.readyroadbackend.marketing.task.CreateMarketingTaskCommand;
import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import com.readyroad.readyroadbackend.marketing.task.TaskCreationService;
import com.readyroad.readyroadbackend.marketing.worker.MarketingTaskDispatcher;
import java.util.EnumSet;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("postgresql")
@Testcontainers
class EditorialArticlePublicationPostgreSqlIntegrationTest {

    private static final String PUBLICATION_TASK = "ARTICLE_PUBLISH";

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
                () -> "ZWRpdG9yaWFsLXB1YmxpY2F0aW9uLXRlc3Qta2V5LW5vdC1mb3ItcHJvZA==");
        registry.add("readyroad.admin.default-password", () -> "Publication-Test-Only-2026!");
    }

    @Autowired DataSource dataSource;
    @Autowired EditorialArticleApprovalService approvalRequestService;
    @Autowired EditorialArticleApprovalTaskHandler approvalTaskHandler;
    @Autowired ApprovalService approvalService;
    @Autowired AgentTaskRepository taskRepository;
    @Autowired TaskCreationService taskCreationService;
    @Autowired MarketingTaskDispatcher dispatcher;
    @Autowired ObjectMapper objectMapper;
    @Autowired WebApplicationContext webApplicationContext;

    private JdbcTemplate jdbc;
    private MockMvc mockMvc;

    @BeforeEach
    void resetPublicationData() {
        jdbc = new JdbcTemplate(dataSource);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        Boolean publicationTableExists = jdbc.queryForObject(
                "SELECT to_regclass('article_publications') IS NOT NULL", Boolean.class);
        if (Boolean.TRUE.equals(publicationTableExists)) {
            jdbc.update("DELETE FROM article_publications");
        }
        jdbc.update("DELETE FROM audit_logs WHERE entity_type = 'EDITORIAL_ARTICLE'");
        jdbc.update("DELETE FROM agent_approvals WHERE task_id IN "
                + "(SELECT id FROM agent_tasks WHERE task_type IN ('ARTICLE_APPROVAL', 'ARTICLE_PUBLISH'))");
        jdbc.update("DELETE FROM agent_tasks WHERE task_type IN ('ARTICLE_APPROVAL', 'ARTICLE_PUBLISH')");
        jdbc.execute("TRUNCATE article_publications, article_versions, article_briefs, articles RESTART IDENTITY");
    }

    @Test
    void approvedSnapshotSchedulesAndPublishesExactlyFourLanguages() {
        long articleId = eligibleArticle(1);
        AgentTask approval = approvedArticle(articleId);

        approvalTaskHandler.execute(claimed(approval));

        assertThat(state(articleId)).isEqualTo(EditorialArticleState.SCHEDULED);
        AgentTask publication = publicationTask(articleId);
        assertThat(publication.getParentTaskId()).isEqualTo(approval.getId());
        assertThat(publication.getPayload().path("versions")).hasSize(4);
        assertThat(publication.isRequiresApproval()).isFalse();

        dispatcher.dispatch(claimed(publication));

        assertThat(state(articleId)).isEqualTo(EditorialArticleState.PUBLISHED);
        assertThat(publicationRows(articleId)).isEqualTo(4);
        assertThat(publicationLanguages(articleId)).containsExactlyInAnyOrder("AR", "NL", "FR", "EN");
        assertThat(jdbc.queryForObject("""
                SELECT count(*) FROM article_publications publication
                JOIN article_versions version ON version.id = publication.article_version_id
                WHERE publication.article_id = ?
                  AND version.article_id = publication.article_id
                  AND version.language = publication.language
                """, Integer.class, articleId)).isEqualTo(4);
    }

    @Test
    void repeatedApprovalAndPublicationExecutionRemainIdempotent() {
        long articleId = eligibleArticle(2);
        AgentTask approval = approvedArticle(articleId);

        approvalTaskHandler.execute(claimed(approval));
        approvalTaskHandler.execute(claimed(approval));
        AgentTask publication = publicationTask(articleId);
        dispatcher.dispatch(claimed(publication));
        dispatcher.dispatch(claimed(publication));

        assertThat(publicationTaskCount(articleId)).isOne();
        assertThat(publicationRows(articleId)).isEqualTo(4);
        assertThat(state(articleId)).isEqualTo(EditorialArticleState.PUBLISHED);
        assertThat(publicationAuditCount(articleId)).isOne();
    }

    @Test
    void staleVersionSnapshotCannotBePublished() {
        long articleId = eligibleArticle(3);
        AgentTask approval = approvedArticle(articleId);
        approvalTaskHandler.execute(claimed(approval));
        AgentTask publication = publicationTask(articleId);
        replaceCurrentVersion(articleId, "FR");

        assertThatThrownBy(() -> dispatcher.dispatch(claimed(publication)))
                .isInstanceOf(MarketingTaskExecutionException.class)
                .extracting(failure -> ((MarketingTaskExecutionException) failure).errorCode())
                .isEqualTo("ARTICLE_PUBLICATION_STALE");

        assertThat(state(articleId)).isEqualTo(EditorialArticleState.SCHEDULED);
        assertThat(publicationRows(articleId)).isZero();
    }

    @Test
    void publicationTaskWithoutApprovedParentIsRejected() {
        long articleId = eligibleArticle(4);
        ObjectNode payload = objectMapper.createObjectNode().put("articleId", articleId);
        AgentTask publication = taskCreationService.create(new CreateMarketingTaskCommand(
                "EDITORIAL", PUBLICATION_TASK, payload, TaskPriority.CRITICAL, null,
                "SYSTEM", "unauthorized-publication:" + articleId, null, null,
                "ARTICLE", String.valueOf(articleId), ApprovalMetadata.standingOwnerAuthorization()))
                .task();

        assertThatThrownBy(() -> dispatcher.dispatch(claimed(publication)))
                .isInstanceOf(MarketingTaskExecutionException.class)
                .extracting(failure -> ((MarketingTaskExecutionException) failure).errorCode())
                .isEqualTo("ARTICLE_PUBLICATION_NOT_APPROVED");

        assertThat(state(articleId)).isEqualTo(EditorialArticleState.IMAGE_REQUIRED);
        assertThat(publicationRows(articleId)).isZero();
    }

    @Test
    void publicationRecordsCannotDuplicateTheSameImmutableVersion() {
        long articleId = eligibleArticle(5);
        AgentTask approval = approvedArticle(articleId);
        approvalTaskHandler.execute(claimed(approval));
        AgentTask publication = publicationTask(articleId);
        dispatcher.dispatch(claimed(publication));

        Long versionId = jdbc.queryForObject("""
                SELECT article_version_id FROM article_publications
                WHERE article_id = ? AND language = 'EN'
                """, Long.class, articleId);
        assertThatThrownBy(() -> jdbc.update("""
                INSERT INTO article_publications (
                    article_id, article_version_id, language, approval_task_id,
                    publication_task_id, status, published_at
                ) VALUES (?, ?, 'EN', ?, ?, 'PUBLISHED', CURRENT_TIMESTAMP)
                """, articleId, versionId, approval.getId(), publication.getId()))
                .hasMessageContaining("uq_article_publications_version");
    }

    @Test
    void publicBlogRoutesExposeOnlyThePublishedLocalizedSnapshot() throws Exception {
        long articleId = eligibleArticle(6);
        AgentTask approval = approvedArticle(articleId);
        approvalTaskHandler.execute(claimed(approval));
        dispatcher.dispatch(claimed(publicationTask(articleId)));

        mockMvc.perform(get("/api/articles").param("language", "AR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].language").value("AR"))
                .andExpect(jsonPath("$[0].slug").value("publication-6-AR"))
                .andExpect(jsonPath("$[0].title").value("AR title"))
                .andExpect(jsonPath("$[0].body").doesNotExist());

        mockMvc.perform(get("/api/articles/{slug}", "publication-6-AR")
                        .param("language", "NL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.language").value("NL"))
                .andExpect(jsonPath("$.slug").value("publication-6-NL"))
                .andExpect(jsonPath("$.body").value("NL body"))
                .andExpect(jsonPath("$.alternateSlugs.AR").value("publication-6-AR"))
                .andExpect(jsonPath("$.alternateSlugs.EN").value("publication-6-EN"));
    }

    private AgentTask approvedArticle(long articleId) {
        var response = approvalRequestService.request(
                articleId,
                new EditorialArticleApprovalDtos.Request(
                        EnumSet.allOf(EditorialArticleQualityGate.class),
                        "All publication gates passed"),
                "editor");
        approvalService.approve(response.id(), "owner", "Approved exact article snapshot for publication");
        return taskRepository.findById(response.id()).orElseThrow();
    }

    private long eligibleArticle(int backlogOrder) {
        Long articleId = jdbc.queryForObject("""
                INSERT INTO articles (
                    article_topic_id, canonical_key, lifecycle_state, canonical_language
                ) VALUES ((SELECT id FROM article_topics WHERE official_backlog_order = ?), ?, 'IMAGE_REQUIRED', 'EN')
                RETURNING id
                """, Long.class, backlogOrder, "publication-" + backlogOrder);
        for (String language : List.of("AR", "NL", "FR", "EN")) {
            jdbc.update("""
                    INSERT INTO article_versions (
                        article_id, version_number, language, title, slug, summary, body,
                        status, is_current, created_by
                    ) VALUES (?, 1, ?, ?, ?, ?, ?, 'DRAFT_READY', TRUE, 'editor')
                    """, articleId, language, language + " title", "publication-" + backlogOrder + "-" + language,
                    language + " summary", language + " body");
        }
        return articleId;
    }

    private void replaceCurrentVersion(long articleId, String language) {
        jdbc.update("UPDATE article_versions SET is_current = FALSE WHERE article_id = ? AND language = ?",
                articleId, language);
        jdbc.update("""
                INSERT INTO article_versions (
                    article_id, version_number, language, title, slug, body,
                    status, is_current, created_by
                ) VALUES (?, 2, ?, ?, ?, ?, 'DRAFT_READY', TRUE, 'another-editor')
                """, articleId, language, language + " changed", "changed-" + articleId + "-" + language,
                language + " changed body");
    }

    private AgentTask publicationTask(long articleId) {
        return taskRepository.findByAgentTypeAndTaskTypeAndIdempotencyKey(
                        "EDITORIAL", PUBLICATION_TASK, publicationIdempotencyKey(articleId))
                .orElseThrow();
    }

    private String publicationIdempotencyKey(long articleId) {
        String versionIds = jdbc.queryForList("""
                SELECT id::text FROM article_versions
                WHERE article_id = ? AND is_current
                ORDER BY language
                """, String.class, articleId).stream().reduce((left, right) -> left + "-" + right).orElseThrow();
        return "article-publication:" + articleId + ":" + versionIds;
    }

    private EditorialArticleState state(long articleId) {
        return EditorialArticleState.valueOf(jdbc.queryForObject(
                "SELECT lifecycle_state FROM articles WHERE id = ?", String.class, articleId));
    }

    private int publicationRows(long articleId) {
        Boolean tableExists = jdbc.queryForObject(
                "SELECT to_regclass('article_publications') IS NOT NULL", Boolean.class);
        if (!Boolean.TRUE.equals(tableExists)) {
            return 0;
        }
        return jdbc.queryForObject(
                "SELECT count(*) FROM article_publications WHERE article_id = ?", Integer.class, articleId);
    }

    private List<String> publicationLanguages(long articleId) {
        return jdbc.queryForList("""
                SELECT language FROM article_publications
                WHERE article_id = ? ORDER BY language
                """, String.class, articleId);
    }

    private int publicationTaskCount(long articleId) {
        return jdbc.queryForObject("""
                SELECT count(*) FROM agent_tasks
                WHERE task_type = 'ARTICLE_PUBLISH' AND source_type = 'ARTICLE' AND source_id = ?
                """, Integer.class, String.valueOf(articleId));
    }

    private int publicationAuditCount(long articleId) {
        return jdbc.queryForObject("""
                SELECT count(*) FROM audit_logs
                WHERE event_type = 'ARTICLE_PUBLISHED'
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
