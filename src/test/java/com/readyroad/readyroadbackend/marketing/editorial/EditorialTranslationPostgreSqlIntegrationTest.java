package com.readyroad.readyroadbackend.marketing.editorial;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.readyroad.readyroadbackend.marketing.content.ContentLocale;
import com.readyroad.readyroadbackend.marketing.content.OpenAIContentGenerationException;
import com.readyroad.readyroadbackend.marketing.repository.AgentTaskRepository;
import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
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
@Import(EditorialTranslationPostgreSqlIntegrationTest.TranslationConfiguration.class)
class EditorialTranslationPostgreSqlIntegrationTest {

    @Container
    static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer("postgres:17-alpine");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);

        registry.add("spring.flyway.url", POSTGRES::getJdbcUrl);
        registry.add("spring.flyway.user", POSTGRES::getUsername);
        registry.add("spring.flyway.password", POSTGRES::getPassword);

        registry.add("readyroad.marketing.enabled", () -> "false");
        registry.add(
                "readyroad.marketing.content.api-key",
                () -> "editorial-translation-integration-test-key");

        registry.add(
                "jwt.secret-key",
                () -> "ZWRpdG9yaWFsLXRyYW5zbGF0aW9uLXRlc3Qta2V5LW5vdC1mb3ItcHJvZHVjdGlvbg==");

        registry.add(
                "readyroad.admin.default-password",
                () -> "Editorial-Translation-Test-2026!");
    }

    @Autowired
    DataSource dataSource;

    @Autowired
    EditorialTranslationService translationService;

    @Autowired
    EditorialTranslationStore translationStore;

    @Autowired
    EditorialTranslationTaskHandler translationHandler;

    @Autowired
    AgentTaskRepository taskRepository;

    @Autowired
    TranslationStub translationStub;

    private JdbcTemplate jdbc;

    @BeforeEach
    void reset() {
        jdbc = new JdbcTemplate(dataSource);

        jdbc.execute("""
                TRUNCATE article_refresh_recommendations,
                         article_performance_snapshots,
                         article_publications,
                         article_image_localizations,
                         article_image_variants,
                         article_image_assets,
                         article_versions,
                         article_briefs,
                         articles
                RESTART IDENTITY
                """);

        jdbc.update("""
                DELETE FROM audit_logs
                WHERE event_type IN (
                    'EDITORIAL_ARTICLE_TRANSLATIONS_CREATED',
                    'EDITORIAL_ARTICLE_STATE_CHANGED'
                )
                """);

        jdbc.update("""
                DELETE FROM agent_tasks
                WHERE task_type = 'ARTICLE_TRANSLATION_ADAPT'
                """);

        translationStub.reset();
    }

    @Test
    void createsOnlyMissingTranslationsPreservesHumanVersionAndIsIdempotent() {
        ArticleFixture fixture = createArticle("AR");

        insertHumanVersion(fixture.articleId(), "NL");

        var first = translationService.request(
                fixture.articleId(),
                new EditorialTranslationDtos.CreateRequest("translate-article-1"),
                "editorial-admin");

        var duplicate = translationService.request(
                fixture.articleId(),
                new EditorialTranslationDtos.CreateRequest("translate-article-1"),
                "editorial-admin");

        assertThat(duplicate.id()).isEqualTo(first.id());
        assertThat(first.taskType()).isEqualTo("ARTICLE_TRANSLATION_ADAPT");
        assertThat(first.requiresApproval()).isFalse();
        assertThat(first.approvalMode())
                .isEqualTo("STANDING_OWNER_AUTHORIZATION");

        var task = taskRepository.findById(first.id()).orElseThrow();
        ClaimedTask claimed = claimed(task);

        translationHandler.execute(claimed);
        translationHandler.execute(claimed);

        assertThat(translationStub.calls()).isEqualTo(3);
        assertThat(translationStub.targets())
                .containsExactlyInAnyOrder(
                        ContentLocale.NL,
                        ContentLocale.FR,
                        ContentLocale.EN);
        assertThat(translationStub.targets())
                .doesNotContain(ContentLocale.AR);

        assertThat(currentLanguages(fixture.articleId()))
                .containsExactlyInAnyOrder("AR", "NL", "FR", "EN");

        assertThat(articleState(fixture.articleId()))
                .isEqualTo("IMAGE_REQUIRED");

        assertThat(jdbc.queryForObject("""
                SELECT count(*)
                FROM article_versions
                WHERE article_id = ?
                  AND generation_metadata ->> 'translationTaskId' = ?
                """,
                Integer.class,
                fixture.articleId(),
                String.valueOf(first.id())))
                .isEqualTo(2);

        assertThat(jdbc.queryForObject("""
                SELECT count(*)
                FROM article_versions
                WHERE article_id = ?
                  AND generation_metadata ->> 'translationTaskId' = ?
                  AND metadata ->> 'focusKeyword' = language || ' driving theory exam'
                  AND slug = lower(language) || '-driving-theory-exam'
                """, Integer.class, fixture.articleId(), String.valueOf(first.id())))
                .isEqualTo(2);

        assertThat(jdbc.queryForObject("""
                SELECT count(*)
                FROM article_versions
                WHERE article_id = ?
                  AND generation_metadata ->> 'translationTaskId' = ?
                  AND (generation_metadata ->> 'sourceVersionId')::bigint = ?
                  AND generation_metadata ->> 'sourceLanguage' = 'AR'
                  AND generation_metadata ->> 'targetLanguage' = language
                """,
                Integer.class,
                fixture.articleId(),
                String.valueOf(first.id()),
                fixture.sourceVersionId()))
                .isEqualTo(2);

        assertThat(jdbc.queryForObject("""
                SELECT count(*)
                FROM article_versions
                WHERE article_id = ?
                  AND generation_metadata ->> 'translationTaskId' = ?
                  AND jsonb_array_length(metadata -> 'internalLinks') = 0
                """,
                Integer.class,
                fixture.articleId(),
                String.valueOf(first.id())))
                .isEqualTo(2);

        assertThat(jdbc.queryForObject("""
                SELECT count(*)
                FROM article_versions
                WHERE article_id = ?
                  AND generation_metadata ->> 'translationTaskId' = ?
                  AND generated_by_task_id IS NOT NULL
                """,
                Integer.class,
                fixture.articleId(),
                String.valueOf(first.id())))
                .isZero();

        assertThat(jdbc.queryForObject("""
                SELECT title
                FROM article_versions
                WHERE article_id = ?
                  AND language = 'NL'
                  AND is_current
                """,
                String.class,
                fixture.articleId()))
                .isEqualTo("Human Dutch version");

        assertThat(jdbc.queryForObject("""
                SELECT created_by
                FROM article_versions
                WHERE article_id = ?
                  AND language = 'NL'
                  AND is_current
                """,
                String.class,
                fixture.articleId()))
                .isEqualTo("EDITORIAL_WORKER");

        assertThat(jdbc.queryForObject("""
                SELECT body
                FROM article_versions
                WHERE article_id = ?
                  AND language = 'NL'
                  AND is_current
                """,
                String.class,
                fixture.articleId()))
                .isEqualTo(humanBody());

        assertThat(jdbc.queryForObject("""
                SELECT count(*)
                FROM article_versions
                WHERE article_id = ?
                  AND language = 'NL'
                  AND created_by = 'human-editor'
                  AND NOT is_current
                """,
                Integer.class,
                fixture.articleId()))
                .isOne();

        assertThat(jdbc.queryForObject("""
                SELECT count(*)
                FROM article_versions
                WHERE article_id = ?
                  AND language = 'NL'
                  AND is_current
                  AND metadata ->> 'focusKeyword' = 'NL driving theory exam'
                  AND slug = 'nl-driving-theory-exam'
                  AND generation_metadata ->> 'seoMetadataRepairTaskId' = ?
                """,
                Integer.class,
                fixture.articleId(),
                String.valueOf(first.id())))
                .isOne();

        assertThat(jdbc.queryForObject("""
                SELECT count(*)
                FROM article_versions
                WHERE article_id = ?
                """,
                Integer.class,
                fixture.articleId()))
                .isEqualTo(5);
    }

    @ParameterizedTest
    @ValueSource(strings = {"DRAFT_READY", "FACT_CHECK_REQUIRED", "LEGAL_REVIEW_REQUIRED"})
    void translatesSavedDraftsWithoutSkippingReviewAndReplaysWithoutDuplicates(String state) {
        ArticleFixture fixture = createArticle("AR");
        jdbc.update("UPDATE articles SET lifecycle_state = ? WHERE id = ?", state, fixture.articleId());
        var response = translationService.request(fixture.articleId(),
                new EditorialTranslationDtos.CreateRequest("draft-translation"), "editorial-admin");
        var task = claimed(taskRepository.findById(response.id()).orElseThrow());

        translationHandler.execute(task);
        translationHandler.execute(task);

        assertThat(articleState(fixture.articleId())).isEqualTo(state);
        assertThat(currentLanguages(fixture.articleId())).containsExactlyInAnyOrder("AR", "NL", "FR", "EN");
        assertThat(translationStub.calls()).isEqualTo(3);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM article_versions WHERE article_id = ?",
                Integer.class, fixture.articleId())).isEqualTo(4);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM article_publications", Integer.class)).isZero();
        assertThat(jdbc.queryForObject("SELECT count(*) FROM audit_logs WHERE event_type = ?",
                Integer.class, EditorialTranslationService.AUDIT_EVENT)).isOne();
    }

    @ParameterizedTest
    @ValueSource(strings = {"WAITING_APPROVAL", "APPROVED", "SCHEDULED", "PUBLISHED", "ARCHIVED", "REJECTED"})
    void doesNotTranslateLockedArticles(String state) {
        ArticleFixture fixture = createArticle("AR");
        jdbc.update("UPDATE articles SET lifecycle_state = ? WHERE id = ?", state, fixture.articleId());
        assertThatThrownBy(() -> translationService.request(fixture.articleId(),
                new EditorialTranslationDtos.CreateRequest("locked-translation"), "editorial-admin"))
                .isInstanceOf(IllegalStateException.class);
        assertThat(translationStub.calls()).isZero();
        assertThat(currentLanguages(fixture.articleId())).containsExactly("AR");
    }

    @Test
    void preservesAHumanLocalizedVersionThatAppearsDuringGeneration() {
        ArticleFixture fixture = createArticle("AR");

        translationStub.beforeFirstCall(
                () -> insertHumanVersion(fixture.articleId(), "NL"));

        var response = translationService.request(
                fixture.articleId(),
                new EditorialTranslationDtos.CreateRequest("translation-race"),
                "editorial-admin");

        var task = taskRepository.findById(response.id()).orElseThrow();

        translationHandler.execute(claimed(task));

        /*
         * NL was missing during prepare(), so the model was asked for NL.
         * A human NL version then appeared before persistence.
         * Persistence must keep the human version and skip the generated NL.
         */
        assertThat(translationStub.calls()).isEqualTo(3);
        assertThat(translationStub.targets())
                .containsExactlyInAnyOrder(
                        ContentLocale.NL,
                        ContentLocale.FR,
                        ContentLocale.EN);

        assertThat(jdbc.queryForObject("""
                SELECT title
                FROM article_versions
                WHERE article_id = ?
                  AND language = 'NL'
                  AND is_current
                """,
                String.class,
                fixture.articleId()))
                .isEqualTo("Human Dutch version");

        assertThat(jdbc.queryForObject("""
                SELECT body
                FROM article_versions
                WHERE article_id = ?
                  AND language = 'NL'
                  AND is_current
                """,
                String.class,
                fixture.articleId()))
                .isEqualTo(humanBody());

        assertThat(jdbc.queryForObject("""
                SELECT count(*)
                FROM article_versions
                WHERE article_id = ?
                  AND generation_metadata ->> 'translationTaskId' = ?
                """,
                Integer.class,
                fixture.articleId(),
                String.valueOf(response.id())))
                .isEqualTo(2);

        assertThat(jdbc.queryForObject("""
                SELECT count(*)
                FROM article_versions
                WHERE article_id = ?
                  AND language = 'NL'
                  AND generation_metadata ->> 'seoMetadataRepairTaskId' = ?
                  AND metadata ->> 'focusKeyword' = 'NL driving theory exam'
                """,
                Integer.class,
                fixture.articleId(),
                String.valueOf(response.id())))
                .isOne();

        assertThat(currentLanguages(fixture.articleId()))
                .containsExactlyInAnyOrder("AR", "NL", "FR", "EN");

        assertThat(articleState(fixture.articleId()))
                .isEqualTo("IMAGE_REQUIRED");
    }

    @Test
    void rejectsATaskWhenTheCanonicalSourceVersionChangedAfterRequest() {
        ArticleFixture fixture = createArticle("AR");

        var response = translationService.request(
                fixture.articleId(),
                new EditorialTranslationDtos.CreateRequest("stale-source"),
                "editorial-admin");

        replaceCanonicalVersion(fixture);

        var task = taskRepository.findById(response.id()).orElseThrow();

        assertThatThrownBy(
                () -> translationHandler.execute(claimed(task)))
                .isInstanceOf(MarketingTaskExecutionException.class)
                .extracting(error ->
                        ((MarketingTaskExecutionException) error).errorCode())
                .isEqualTo("ARTICLE_TRANSLATION_VALIDATION_FAILED");

        assertThat(translationStub.calls()).isZero();

        assertThat(currentLanguages(fixture.articleId()))
                .containsExactly("AR");

        assertThat(articleState(fixture.articleId()))
                .isEqualTo("TRANSLATION_REQUIRED");

        assertThat(jdbc.queryForObject("""
                SELECT count(*)
                FROM article_versions
                WHERE article_id = ?
                  AND language <> 'AR'
                """,
                Integer.class,
                fixture.articleId()))
                .isZero();
    }

    @Test
    void preservesPaidTranslationsAndRetriesOnlyMissingLanguages() {
        ArticleFixture fixture = createArticle("AR");

        translationStub.failOnCall(2);

        var response = translationService.request(
                fixture.articleId(),
                new EditorialTranslationDtos.CreateRequest("generation-failure"),
                "editorial-admin");

        var task = taskRepository.findById(response.id()).orElseThrow();

        assertThatThrownBy(
                () -> translationHandler.execute(claimed(task)))
                .isInstanceOf(MarketingTaskExecutionException.class)
                .extracting(error ->
                        ((MarketingTaskExecutionException) error).errorCode())
                .isEqualTo("TEST_TRANSLATION_FAILURE");

        assertThat(translationStub.calls()).isEqualTo(2);
        var completedLanguage = translationStub.targets().get(0);
        var failedLanguage = translationStub.targets().get(1);
        assertThat(currentLanguages(fixture.articleId()))
                .containsExactlyInAnyOrder("AR", completedLanguage.name());

        assertThat(jdbc.queryForObject("""
                SELECT count(*)
                FROM article_versions
                WHERE article_id = ?
                """,
                Integer.class,
                fixture.articleId()))
                .isEqualTo(2);

        assertThat(articleState(fixture.articleId()))
                .isEqualTo("TRANSLATION_REQUIRED");

        translationHandler.execute(claimed(task));
        translationHandler.execute(claimed(task));
        assertThat(translationStub.calls()).isEqualTo(4);
        assertThat(translationStub.targets().stream().filter(completedLanguage::equals).count()).isEqualTo(1);
        assertThat(translationStub.targets().stream().filter(failedLanguage::equals).count()).isEqualTo(2);
        assertThat(translationStub.targets()).contains(ContentLocale.NL, ContentLocale.FR, ContentLocale.EN);
        assertThat(currentLanguages(fixture.articleId()))
                .containsExactlyInAnyOrder("AR", "NL", "FR", "EN");
        assertThat(articleState(fixture.articleId())).isEqualTo("IMAGE_REQUIRED");
        assertThat(jdbc.queryForObject("""
                SELECT count(*) FROM article_versions WHERE article_id = ?
                """, Integer.class, fixture.articleId())).isEqualTo(4);
    }

    @Test
    void usesTheArticlesCanonicalLanguageInsteadOfAssumingArabic() {
        ArticleFixture fixture = createArticle("NL");

        var response = translationService.request(
                fixture.articleId(),
                new EditorialTranslationDtos.CreateRequest("canonical-nl"),
                "editorial-admin");

        var task = taskRepository.findById(response.id()).orElseThrow();

        translationHandler.execute(claimed(task));

        assertThat(translationStub.calls()).isEqualTo(3);

        assertThat(translationStub.targets())
                .containsExactlyInAnyOrder(
                        ContentLocale.AR,
                        ContentLocale.FR,
                        ContentLocale.EN);

        assertThat(translationStub.targets())
                .doesNotContain(ContentLocale.NL);

        assertThat(currentLanguages(fixture.articleId()))
                .containsExactlyInAnyOrder("AR", "NL", "FR", "EN");

        assertThat(jdbc.queryForObject("""
                SELECT count(*)
                FROM article_versions
                WHERE article_id = ?
                  AND generation_metadata ->> 'translationTaskId' = ?
                  AND generation_metadata ->> 'sourceLanguage' = 'NL'
                """,
                Integer.class,
                fixture.articleId(),
                String.valueOf(response.id())))
                .isEqualTo(3);

        assertThat(articleState(fixture.articleId()))
                .isEqualTo("IMAGE_REQUIRED");
    }

    @Test
    void repairsMissingLocalizedSeoMetadataWithoutReplacingExistingArticleContent() {
        ArticleFixture fixture = createArticle("AR");
        insertHumanVersion(fixture.articleId(), "NL");
        insertHumanVersion(fixture.articleId(), "FR");
        insertHumanVersion(fixture.articleId(), "EN");
        jdbc.update("""
                UPDATE articles
                SET lifecycle_state = 'IMAGE_REQUIRED'
                WHERE id = ?
                """, fixture.articleId());

        var response = translationService.request(
                fixture.articleId(),
                new EditorialTranslationDtos.CreateRequest("repair-localized-seo"),
                "editorial-admin");
        var task = taskRepository.findById(response.id()).orElseThrow();

        translationHandler.execute(claimed(task));
        translationHandler.execute(claimed(task));

        assertThat(translationStub.calls()).isEqualTo(3);
        assertThat(translationStub.targets()).containsExactlyInAnyOrder(
                ContentLocale.NL,
                ContentLocale.FR,
                ContentLocale.EN);
        assertThat(articleState(fixture.articleId())).isEqualTo("IMAGE_REQUIRED");

        assertThat(jdbc.queryForObject("""
                SELECT count(*)
                FROM article_versions
                WHERE article_id = ?
                  AND language IN ('NL', 'FR', 'EN')
                  AND is_current
                  AND version_number = 2
                  AND body = ?
                  AND metadata ->> 'focusKeyword' = language || ' driving theory exam'
                  AND slug = lower(language) || '-driving-theory-exam'
                  AND generation_metadata ->> 'seoMetadataRepairTaskId' = ?
                """,
                Integer.class,
                fixture.articleId(),
                humanBody(),
                String.valueOf(response.id())))
                .isEqualTo(3);

        assertThat(jdbc.queryForObject("""
                SELECT count(*)
                FROM article_versions
                WHERE article_id = ?
                  AND language IN ('NL', 'FR', 'EN')
                  AND version_number = 1
                  AND created_by = 'human-editor'
                  AND NOT is_current
                """,
                Integer.class,
                fixture.articleId()))
                .isEqualTo(3);
    }

    @Test
    void usesTheLatestApprovedSameLanguageBriefWhenCanonicalMetadataHasNoFocusKeyword() {
        ArticleFixture fixture = createArticle("AR");
        replaceCanonicalVersionWithoutFocusKeyword(fixture);
        jdbc.update("""
                INSERT INTO article_briefs (
                    article_topic_id, target_language, search_intent, working_title,
                    purpose, target_queries, primary_cta, legal_review_required, status
                ) VALUES (
                    2, 'AR', 'INFORMATIONAL', 'Translation focus keyword test',
                    'Verify the approved brief fallback',
                    '["امتحان السياقة النظري"]'::jsonb,
                    'Continue', FALSE, 'APPROVED'
                )
                """);

        assertThat(translationStore.context(fixture.articleId()).focusKeyword())
                .isEqualTo("امتحان السياقة النظري");
        assertThat(translationService.request(
                fixture.articleId(),
                new EditorialTranslationDtos.CreateRequest("approved-brief-focus-keyword"),
                "editorial-admin").taskType())
                .isEqualTo("ARTICLE_TRANSLATION_ADAPT");
    }

    private ArticleFixture createArticle(String canonicalLanguage) {
        long uspId = jdbc.queryForObject("""
                SELECT id
                FROM marketing_usp
                WHERE title = 'RijVia learning platform'
                """, Long.class);

        long pillarId = jdbc.queryForObject("""
                SELECT id
                FROM marketing_content_pillars
                WHERE pillar_key = 'THEORY_EXAM'
                """, Long.class);

        long funnelId = jdbc.queryForObject("""
                SELECT id
                FROM marketing_funnel_stages
                WHERE stage_key = 'EDUCATION'
                """, Long.class);

        long goalId = jdbc.queryForObject("""
                SELECT id
                FROM marketing_conversion_goals
                WHERE goal_key = 'CONTINUE_TOPIC_LEARNING'
                """, Long.class);

        long articleId = jdbc.queryForObject("""
                INSERT INTO articles (
                    article_topic_id,
                    canonical_key,
                    lifecycle_state,
                    canonical_language,
                    usp_id,
                    icp_id,
                    content_pillar_id,
                    funnel_stage_id,
                    conversion_goal_id
                )
                VALUES (
                    2,
                    ?,
                    'TRANSLATION_REQUIRED',
                    ?,
                    ?,
                    'ICP-AR-BEGINNER',
                    ?,
                    ?,
                    ?
                )
                RETURNING id
                """,
                Long.class,
                "translation-test-" + UUID.randomUUID(),
                canonicalLanguage,
                uspId,
                pillarId,
                funnelId,
                goalId);

        String metadata = """
                {
                  "metaTitle": "Canonical meta title",
                  "metaDescription": "Canonical meta description for translation testing",
                  "focusKeyword": "Belgian driving theory exam",
                  "primaryCta": "Continue learning with RijVia",
                  "internalLinks": []
                }
                """;

        String generationMetadata = """
                {
                  "provider": "TEST",
                  "sourceReference": "ARTICLE_BRIEF:translation-test:verified"
                }
                """;

        long sourceVersionId = jdbc.queryForObject("""
                INSERT INTO article_versions (
                    article_id,
                    version_number,
                    language,
                    title,
                    slug,
                    summary,
                    body,
                    metadata,
                    generation_metadata,
                    status,
                    is_current,
                    created_by
                )
                VALUES (
                    ?,
                    1,
                    ?,
                    'Canonical source article',
                    'canonical-source-article',
                    'Canonical source summary',
                    ?,
                    ?::jsonb,
                    ?::jsonb,
                    'DRAFT_READY',
                    TRUE,
                    'integration-test'
                )
                RETURNING id
                """,
                Long.class,
                articleId,
                canonicalLanguage,
                canonicalBody(),
                metadata,
                generationMetadata);

        return new ArticleFixture(
                articleId,
                sourceVersionId,
                canonicalLanguage);
    }

    private void insertHumanVersion(long articleId, String language) {
        jdbc.update("""
                INSERT INTO article_versions (
                    article_id,
                    version_number,
                    language,
                    title,
                    slug,
                    summary,
                    body,
                    metadata,
                    generation_metadata,
                    status,
                    is_current,
                    created_by
                )
                VALUES (
                    ?,
                    1,
                    ?,
                    'Human Dutch version',
                    'human-dutch-version',
                    'Human localized summary',
                    ?,
                    ?::jsonb,
                    '{"editor":"human"}'::jsonb,
                    'DRAFT',
                    TRUE,
                    'human-editor'
                )
                """,
                articleId,
                language,
                humanBody(),
                """
                {
                  "metaTitle": "Human Dutch meta title",
                  "metaDescription": "Human Dutch meta description",
                  "primaryCta": "Human CTA",
                  "internalLinks": []
                }
                """);
    }

    private void replaceCanonicalVersion(ArticleFixture fixture) {
        jdbc.update("""
                UPDATE article_versions
                SET is_current = FALSE
                WHERE id = ?
                """,
                fixture.sourceVersionId());

        jdbc.update("""
                INSERT INTO article_versions (
                    article_id,
                    version_number,
                    language,
                    title,
                    slug,
                    summary,
                    body,
                    metadata,
                    generation_metadata,
                    status,
                    is_current,
                    created_by
                )
                VALUES (
                    ?,
                    2,
                    ?,
                    'Changed canonical source',
                    'changed-canonical-source',
                    'Changed canonical summary',
                    ?,
                    ?::jsonb,
                    '{"sourceReference":"ARTICLE_BRIEF:changed"}'::jsonb,
                    'DRAFT',
                    TRUE,
                    'human-editor'
                )
                """,
                fixture.articleId(),
                fixture.canonicalLanguage(),
                changedCanonicalBody(),
                """
                {
                  "metaTitle": "Changed canonical meta title",
                  "metaDescription": "Changed canonical meta description",
                  "focusKeyword": "Changed Belgian driving theory exam",
                  "primaryCta": "Changed CTA",
                  "internalLinks": []
                }
                """);
    }

    private void replaceCanonicalVersionWithoutFocusKeyword(ArticleFixture fixture) {
        jdbc.update("""
                UPDATE article_versions
                SET is_current = FALSE
                WHERE id = ?
                """, fixture.sourceVersionId());
        jdbc.update("""
                INSERT INTO article_versions (
                    article_id, version_number, language, title, slug, summary, body,
                    metadata, generation_metadata, status, is_current, created_by
                ) VALUES (
                    ?, 2, ?, 'Canonical source without stored focus keyword',
                    'canonical-source-without-focus-keyword', 'Canonical source summary',
                    ?, ?::jsonb, '{"sourceReference":"ARTICLE_BRIEF:approved"}'::jsonb,
                    'DRAFT_READY', TRUE, 'human-editor'
                )
                """,
                fixture.articleId(),
                fixture.canonicalLanguage(),
                canonicalBody(),
                """
                {
                  "metaTitle": "Canonical meta title",
                  "metaDescription": "Canonical meta description for translation testing",
                  "primaryCta": "Continue learning with RijVia",
                  "internalLinks": []
                }
                """);
    }

    private List<String> currentLanguages(long articleId) {
        return jdbc.queryForList("""
                SELECT language
                FROM article_versions
                WHERE article_id = ?
                  AND is_current
                ORDER BY language
                """,
                String.class,
                articleId);
    }

    private String articleState(long articleId) {
        return jdbc.queryForObject("""
                SELECT lifecycle_state
                FROM articles
                WHERE id = ?
                """,
                String.class,
                articleId);
    }

    private static ClaimedTask claimed(
            com.readyroad.readyroadbackend.marketing.domain.AgentTask task) {

        return new ClaimedTask(
                task.getId(),
                task.getAgentType(),
                task.getTaskType(),
                task.getPayload(),
                task.getPayloadVersion(),
                task.getPriority(),
                1,
                task.getCorrelationId());
    }

    private static String canonicalBody() {
        return "Canonical verified driving theory article content for translation. "
                .repeat(20);
    }

    private static String changedCanonicalBody() {
        return "Changed canonical driving theory article content after task creation. "
                .repeat(20);
    }

    private static String humanBody() {
        return "Human written localized article content that must never be overwritten. "
                .repeat(20);
    }

    record ArticleFixture(
            long articleId,
            long sourceVersionId,
            String canonicalLanguage) {
    }

    static final class TranslationStub
            implements EditorialTranslationClient {

        private final AtomicInteger calls =
                new AtomicInteger();

        private final List<ContentLocale> targets =
                new CopyOnWriteArrayList<>();

        private volatile int failOnCall;
        private volatile Runnable beforeFirstCall;

        @Override
        public AdaptedContent adapt(AdaptRequest request) {
            int call = calls.incrementAndGet();

            targets.add(request.targetLocale());

            Runnable hook = beforeFirstCall;

            if (call == 1 && hook != null) {
                beforeFirstCall = null;
                hook.run();
            }

            if (failOnCall == call) {
                throw new OpenAIContentGenerationException(
                        "TEST_TRANSLATION_FAILURE",
                        "Simulated translation generation failure");
            }

            String target =
                    request.targetLocale().name();

            return new AdaptedContent(
                    request.sourceLocale().name(),
                    target,
                    request.sourceVersionId(),
                    target + " localized article",
                    target.toLowerCase(Locale.ROOT)
                            + "-localized-article",
                    "Localized summary for " + target,
                    localizedBody(target),
                    target + " driving theory exam",
                    target + " localized meta title | RijVia",
                    "Localized meta description for "
                            + target
                            + " article testing.",
                    "Continue learning in " + target,
                    "translation-test-model",
                    500,
                    800,
                    "SUCCEEDED");
        }

        int calls() {
            return calls.get();
        }

        List<ContentLocale> targets() {
            return List.copyOf(targets);
        }

        void failOnCall(int call) {
            failOnCall = call;
        }

        void beforeFirstCall(Runnable action) {
            beforeFirstCall = action;
        }

        void reset() {
            calls.set(0);
            targets.clear();
            failOnCall = 0;
            beforeFirstCall = null;
        }

        private static String localizedBody(String target) {
            return ("Localized "
                    + target
                    + " educational driving theory content for Belgium. ")
                    .repeat(20);
        }
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class TranslationConfiguration {

        @Bean
        TranslationStub translationStub() {
            return new TranslationStub();
        }

        @Bean
        @Primary
        EditorialTranslationClient editorialTranslationClient(
                TranslationStub stub) {
            return stub;
        }
    }
}
