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
import java.util.ArrayList;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.dao.DataIntegrityViolationException;
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
    @Autowired EditorialContentGraphService contentGraphService;
    @Autowired EditorialEditorService editorService;
    @Autowired EditorialArticleUpdateService updateService;
    @Autowired EditorialArticleWorkflowService workflowService;
    @Autowired EditorialArticlePublicationStore publicationStore;
    @Autowired EditorialArticleApprovalStore approvalStore;
    @Autowired PlatformTransactionManager transactionManager;
    @Autowired EditorialInternalLinkStore internalLinkStore;
    @Autowired EditorialPerformanceStore performanceStore;
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
        jdbc.execute("""
                TRUNCATE article_refresh_recommendations, article_performance_snapshots,
                         article_publications, article_image_localizations,
                         article_image_variants, article_image_assets, article_versions, article_briefs, articles
                RESTART IDENTITY
                """);
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
                    publication_task_id, status, published_at, published_slug
                ) VALUES (?, ?, 'EN', ?, ?, 'PUBLISHED', CURRENT_TIMESTAMP, 'publication-5-EN')
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
                .andExpect(jsonPath("$[0].image.heroUrl").value(org.hamcrest.Matchers.startsWith("/images/articles/")))
                .andExpect(jsonPath("$[0].image.altText").value("AR approved article image"))
                .andExpect(jsonPath("$[0].alternateSlugs.AR").value("publication-6-AR"))
                .andExpect(jsonPath("$[0].alternateSlugs.NL").value("publication-6-NL"))
                .andExpect(jsonPath("$[0].alternateSlugs.FR").value("publication-6-FR"))
                .andExpect(jsonPath("$[0].alternateSlugs.EN").value("publication-6-EN"))
                .andExpect(jsonPath("$[0].body").doesNotExist());

        mockMvc.perform(get("/api/articles/{slug}", "publication-6-AR")
                        .param("language", "NL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.language").value("NL"))
                .andExpect(jsonPath("$.slug").value("publication-6-NL"))
                .andExpect(jsonPath("$.body").value("NL body"))
                .andExpect(jsonPath("$.metaTitle").value("NL meta title"))
                .andExpect(jsonPath("$.metaDescription").value("NL meta description"))
                .andExpect(jsonPath("$.image.altText").value("NL approved article image"))
                .andExpect(jsonPath("$.internalLinks[0].type").value("EXAM"))
                .andExpect(jsonPath("$.internalLinks[0].targetPath").value("/nl/exam"))
                .andExpect(jsonPath("$.internalLinks[0].anchorText").value("NL exam"))
                .andExpect(jsonPath("$.alternateSlugs.AR").value("publication-6-AR"))
                .andExpect(jsonPath("$.alternateSlugs.EN").value("publication-6-EN"));

        mockMvc.perform(get("/api/articles/related")
                        .param("language", "NL")
                        .param("targetPath", "/nl/exam"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("publication-6-NL"))
                .andExpect(jsonPath("$[0].title").value("NL title"));
    }

    @Test
    void updateSessionKeepsThePublishedSnapshotPublicInEveryLanguage() throws Exception {
        long articleId = eligibleArticle(6);
        AgentTask approval = approvedArticle(articleId);
        approvalTaskHandler.execute(claimed(approval));
        dispatcher.dispatch(claimed(publicationTask(articleId)));

        updateService.start(articleId, "editor");
        assertThat(state(articleId)).isEqualTo(EditorialArticleState.DRAFTING);
        for (String language : List.of("AR", "NL", "FR", "EN")) {
            replaceCurrentVersion(articleId, language);
            String publishedSlug = "publication-6-" + language;
            String examPath = "EN".equals(language) ? "/exam" : "/" + language.toLowerCase() + "/exam";

            mockMvc.perform(get("/api/articles").param("language", language))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].slug").value(publishedSlug))
                    .andExpect(jsonPath("$[0].title").value(language + " title"))
                    .andExpect(jsonPath("$[0].alternateSlugs.EN").value("publication-6-EN"));
            mockMvc.perform(get("/api/articles/{slug}", publishedSlug).param("language", language))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.body").value(language + " body"));
            mockMvc.perform(get("/api/articles/{slug}", "publication-6-AR").param("language", language))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.slug").value(publishedSlug));
            mockMvc.perform(get("/api/articles/related").param("language", language)
                            .param("targetPath", examPath))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].slug").value(publishedSlug));
            mockMvc.perform(get("/api/articles/{slug}", "changed-" + articleId + "-" + language)
                            .param("language", language))
                    .andExpect(status().isNotFound());
            assertThat(internalLinkStore.publishedArticleId(language, publishedSlug)).contains(articleId);
        }
        assertThat(contentGraphService.graph().nodes())
                .filteredOn(node -> node.id().startsWith("ARTICLE:" + articleId + ":"))
                .hasSize(4)
                .allSatisfy(node -> assertThat(node.published()).isTrue());
        assertThat(performanceStore.publishedArticleIds()).contains(articleId);
        assertThat(publicationRows(articleId)).isEqualTo(4);
        assertThat(publicationAuditCount(articleId)).isOne();
    }

    @Test
    void pendingUpdateReviewDoesNotWithdrawThePreviousPublication() throws Exception {
        long articleId = eligibleArticle(6);
        AgentTask approval = approvedArticle(articleId);
        approvalTaskHandler.execute(claimed(approval));
        dispatcher.dispatch(claimed(publicationTask(articleId)));

        for (EditorialArticleState pending : List.of(
                EditorialArticleState.DRAFT_READY, EditorialArticleState.FACT_CHECK_REQUIRED,
                EditorialArticleState.LEGAL_REVIEW_REQUIRED, EditorialArticleState.TRANSLATION_REQUIRED,
                EditorialArticleState.IMAGE_REQUIRED, EditorialArticleState.WAITING_APPROVAL,
                EditorialArticleState.APPROVED, EditorialArticleState.SCHEDULED,
                EditorialArticleState.REJECTED)) {
            jdbc.update("UPDATE articles SET lifecycle_state = ? WHERE id = ?", pending.name(), articleId);
            mockMvc.perform(get("/api/articles/{slug}", "publication-6-EN").param("language", "EN"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.body").value("EN body"));
        }
    }

    @Test
    void adminCanEditAndRepublishOnTheSameRoutesWithoutReplacingHistory() throws Exception {
        long articleId = eligibleArticle(6);
        assignCompleteStrategyContext(6);
        AgentTask firstApproval = approvedArticle(articleId);
        approvalTaskHandler.execute(claimed(firstApproval));
        dispatcher.dispatch(claimed(publicationTask(articleId)));

        assertThat(updateService.start(articleId, "editor").changed()).isTrue();
        assertThat(updateService.start(articleId, "editor").changed()).isFalse();
        assertThat(jdbc.queryForObject("SELECT count(*) FROM article_versions WHERE article_id = ?",
                Integer.class, articleId)).isEqualTo(8);
        long topicId = jdbc.queryForObject("SELECT article_topic_id FROM articles WHERE id = ?",
                Long.class, articleId);
        var version = editorService.versions(articleId, "EN").getFirst();
        var saved = editorService.save(topicId, "EN", new EditorialEditorDtos.SaveRequest(
                "Revised title", version.slug(), version.summary(), "Revised body",
                version.metaTitle(), version.metaDescription(), version.internalLinks().stream()
                        .map(link -> new EditorialInternalLinkDtos.Input(link.targetPath(), link.anchorText())).toList(),
                version.typography(), version.versionNumber()), "editor");
        assertThat(saved.created()).isTrue();
        mockMvc.perform(get("/api/articles/publication-6-EN").param("language", "EN"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.body").value("EN body"));

        assertThat(workflowService.advanceFromEditor(articleId, "editor", "Draft reviewed").state())
                .isEqualTo(EditorialArticleState.DRAFT_READY);
        workflowService.advanceFromEditor(articleId, "editor", "Begin fact check");
        jdbc.update("""
                INSERT INTO article_briefs (article_topic_id, target_language, target_queries,
                    search_intent, working_title, purpose, primary_cta, legal_review_required, status)
                VALUES (?, 'EN', '["driving theory"]'::jsonb,
                    'INFORMATIONAL', 'Test title', 'Test goal', 'Start learning', FALSE, 'APPROVED')
                """, topicId);
        workflowService.advanceFromEditor(articleId, "editor", "Facts verified");
        workflowService.advanceFromEditor(articleId, "editor", "Languages reviewed");
        AgentTask secondApproval = approvedArticle(articleId);
        approvalTaskHandler.execute(claimed(secondApproval));
        var secondPublication = claimed(publicationTask(articleId));
        dispatcher.dispatch(secondPublication);
        dispatcher.dispatch(secondPublication);
        for (String language : List.of("AR", "NL", "FR", "EN")) {
            mockMvc.perform(get("/api/articles/publication-6-" + language).param("language", language))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.body").value("EN".equals(language) ? "Revised body" : language + " body"));
        }
        assertThat(publicationRows(articleId)).isEqualTo(8);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM article_publications WHERE article_id = ? AND status = 'PUBLISHED'",
                Integer.class, articleId)).isEqualTo(4);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM article_publications WHERE article_id = ? AND status = 'SUPERSEDED'",
                Integer.class, articleId)).isEqualTo(4);
        assertThat(publicationAuditCount(articleId)).isEqualTo(2);
    }

    @Test
    void failedRepublishingRestoresEveryPreviouslyPublishedLanguage() throws Exception {
        long articleId = eligibleArticle(6);
        var approval = approvedArticle(articleId);
        approvalTaskHandler.execute(claimed(approval));
        var firstPublication = publicationTask(articleId);
        dispatcher.dispatch(claimed(firstPublication));
        updateService.start(articleId, "editor");
        var snapshot = new ArrayList<>(approvalStore.currentVersions(articleId));
        var last = snapshot.getLast();
        snapshot.set(snapshot.size() - 1,
                new EditorialArticleApprovalStore.VersionSnapshot(Long.MAX_VALUE, last.language(), last.versionNumber()));
        long imageId = jdbc.queryForObject("SELECT id FROM article_image_assets WHERE article_id = ? AND status = 'APPROVED'",
                Long.class, articleId);
        assertThatThrownBy(() -> new TransactionTemplate(transactionManager).executeWithoutResult(status ->
                publicationStore.publish(articleId, approval.getId(), approval.getId(), imageId, snapshot)))
                .isInstanceOf(org.springframework.dao.InvalidDataAccessApiUsageException.class)
                .hasRootCauseInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Published article versions do not match the approved snapshot");
        assertThat(publicationRows(articleId)).isEqualTo(4);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM article_publications WHERE article_id = ? AND status = 'PUBLISHED'",
                Integer.class, articleId)).isEqualTo(4);
        for (String language : List.of("AR", "NL", "FR", "EN")) {
            mockMvc.perform(get("/api/articles/publication-6-" + language).param("language", language))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.body").value(language + " body"));
        }
    }

    @Test
    void publicBlogRoutesHideUnpublishedArticlesAndRejectInvalidRoutes() throws Exception {
        eligibleArticle(7);

        mockMvc.perform(get("/api/articles").param("language", "EN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
        mockMvc.perform(get("/api/articles/{slug}", "publication-7-EN")
                        .param("language", "EN"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/articles").param("language", "DE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void contentGraphStopsResolvingAnArchivedArticleRoute() throws Exception {
        long targetArticleId = eligibleArticle(10);
        AgentTask targetApproval = approvedArticle(targetArticleId);
        approvalTaskHandler.execute(claimed(targetApproval));
        dispatcher.dispatch(claimed(publicationTask(targetArticleId)));

        assignCompleteStrategyContext(11);
        long sourceArticleId = editorService.save(11, "EN", new EditorialEditorDtos.SaveRequest(
                "Source article",
                "source-article",
                "Source summary",
                "Source body",
                "Source article | RijVia",
                "Source article description",
                List.of(new EditorialInternalLinkDtos.Input(
                        "/blog/publication-10-EN",
                        "Published target article")),
                null), "editor").articleId();

        jdbc.update("UPDATE articles SET lifecycle_state = 'ARCHIVED' WHERE id = ?", targetArticleId);

        mockMvc.perform(get("/api/articles").param("language", "EN"))
                .andExpect(status().isOk()).andExpect(jsonPath("$").isEmpty());
        mockMvc.perform(get("/api/articles/{slug}", "publication-10-EN").param("language", "EN"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/articles/related").param("language", "EN")
                        .param("targetPath", "/exam"))
                .andExpect(status().isOk()).andExpect(jsonPath("$").isEmpty());
        assertThat(internalLinkStore.publishedArticleId("EN", "publication-10-EN")).isEmpty();
        assertThat(performanceStore.publishedArticleIds()).doesNotContain(targetArticleId);

        var graph = contentGraphService.graph();
        String archivedNodeId = "ARTICLE:" + targetArticleId + ":EN";
        assertThat(graph.nodes())
                .filteredOn(node -> node.id().equals(archivedNodeId))
                .singleElement()
                .satisfies(node -> {
                    assertThat(node.published()).isFalse();
                    assertThat(node.path()).isNull();
                });
        assertThat(graph.nodes())
                .anySatisfy(node -> {
                    assertThat(node.type()).isEqualTo("UNRESOLVED_ARTICLE");
                    assertThat(node.path()).isEqualTo("/blog/publication-10-EN");
                    assertThat(node.published()).isFalse();
                });
        assertThat(graph.edges())
                .filteredOn(edge -> edge.sourceId().equals("ARTICLE:" + sourceArticleId + ":EN"))
                .singleElement()
                .satisfies(edge -> assertThat(edge.targetId())
                        .isEqualTo("UNRESOLVED_ARTICLE:/blog/publication-10-EN"));
    }

    @Test
    void publicationRejectsSnapshotsWithoutAUsableLocalizedSlug() {
        long articleId = eligibleArticle(8, "FR");
        AgentTask approval = approvedArticle(articleId);

        assertThatThrownBy(() -> approvalTaskHandler.execute(claimed(approval)))
                .isInstanceOf(MarketingTaskExecutionException.class)
                .extracting(failure -> ((MarketingTaskExecutionException) failure).errorCode())
                .isEqualTo("ARTICLE_PUBLICATION_ROUTE_INVALID");
        assertThat(publicationRows(articleId)).isZero();
    }

    @Test
    void persistedPublicationSlugsAreUniqueWithinEachLanguage() {
        long articleId = eligibleArticle(9);
        AgentTask approval = approvedArticle(articleId);
        approvalTaskHandler.execute(claimed(approval));
        AgentTask publication = publicationTask(articleId);
        dispatcher.dispatch(claimed(publication));

        assertThat(jdbc.queryForObject("""
                SELECT published_slug FROM article_publications
                WHERE article_id = ? AND language = 'EN'
                """, String.class, articleId)).isEqualTo("publication-9-EN");
        assertThatThrownBy(() -> jdbc.update("""
                UPDATE article_publications
                SET published_slug = lower(published_slug)
                WHERE article_id = ? AND language = 'AR'
                """, articleId))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("Article publication routes are immutable");
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

    private void assignCompleteStrategyContext(int backlogOrder) {
        jdbc.update("""
                WITH selected_goal AS (
                    SELECT id, funnel_stage_id
                    FROM marketing_conversion_goals
                    WHERE active
                    ORDER BY id
                    LIMIT 1
                )
                UPDATE article_topics topic
                SET primary_language = 'EN',
                    usp_id = (SELECT id FROM marketing_usp WHERE active ORDER BY priority DESC, id LIMIT 1),
                    icp_id = (SELECT id FROM marketing_icp WHERE active ORDER BY id LIMIT 1),
                    content_pillar_id = (
                        SELECT id FROM marketing_content_pillars
                        WHERE active ORDER BY priority DESC, id LIMIT 1
                    ),
                    funnel_stage_id = selected_goal.funnel_stage_id,
                    conversion_goal_id = selected_goal.id
                FROM selected_goal
                WHERE topic.official_backlog_order = ?
                """, backlogOrder);
    }

    private long eligibleArticle(int backlogOrder) {
        return eligibleArticle(backlogOrder, null);
    }

    private long eligibleArticle(int backlogOrder, String languageWithoutSlug) {
        Long articleId = jdbc.queryForObject("""
                INSERT INTO articles (
                    article_topic_id, canonical_key, lifecycle_state, canonical_language
                ) VALUES ((SELECT id FROM article_topics WHERE official_backlog_order = ?), ?, 'IMAGE_REQUIRED', 'EN')
                RETURNING id
                """, Long.class, backlogOrder, "publication-" + backlogOrder);
        for (String language : List.of("AR", "NL", "FR", "EN")) {
            String slug = language.equals(languageWithoutSlug)
                    ? null
                    : "publication-" + backlogOrder + "-" + language;
            String examPath = language.equals("EN") ? "/exam" : "/" + language.toLowerCase() + "/exam";
            jdbc.update("""
                    INSERT INTO article_versions (
                        article_id, version_number, language, title, slug, summary, body,
                        metadata, status, is_current, created_by
                    ) VALUES (?, 1, ?, ?, ?, ?, ?,
                              jsonb_build_object(
                                  'metaTitle', ?,
                                  'metaDescription', ?,
                                  'internalLinks', jsonb_build_array(jsonb_build_object(
                                      'type', 'EXAM',
                                      'targetPath', ?,
                                      'anchorText', ?
                                  ))
                              ),
                              'DRAFT_READY', TRUE, 'editor')
                    """, articleId, language, language + " title", slug,
                    language + " summary", language + " body",
                    language + " meta title", language + " meta description",
                    examPath, language + " exam");
        }
        EditorialArticleImageTestData.seedApprovedImage(jdbc, articleId);
        return articleId;
    }

    private void replaceCurrentVersion(long articleId, String language) {
        jdbc.update("UPDATE article_versions SET is_current = FALSE WHERE article_id = ? AND language = ?",
                articleId, language);
        jdbc.update("""
                INSERT INTO article_versions (
                    article_id, version_number, language, title, slug, body,
                    status, is_current, created_by
                ) VALUES (?, (SELECT COALESCE(max(version_number), 0) + 1 FROM article_versions WHERE article_id = ? AND language = ?),
                          ?, ?, ?, ?, 'DRAFT_READY', TRUE, 'another-editor')
                """, articleId, articleId, language, language, language + " changed", "changed-" + articleId + "-" + language,
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
        Long imageAssetId = jdbc.queryForObject("""
                SELECT id FROM article_image_assets
                WHERE article_id = ? AND status = 'APPROVED'
                """, Long.class, articleId);
        return "article-publication:" + articleId + ":" + versionIds + ":image-" + imageAssetId;
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
