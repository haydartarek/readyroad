package com.readyroad.readyroadbackend.marketing.youtube;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("postgresql")
@Testcontainers
class MarketingYouTubePostgreSqlIntegrationTest {

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
                () -> "eW91dHViZS10ZXN0LWp3dC1zZWNyZXQtbm90LWZvci1wcm9kdWN0aW9u");
        registry.add("readyroad.admin.default-password", () -> "YouTube-Test-Only-2026!");
    }

    @Autowired DataSource dataSource;
    @Autowired YouTubeStore store;
    @Autowired YouTubeContentPackageService packageService;
    @Autowired MockMvc mockMvc;

    private JdbcTemplate jdbc;

    @BeforeEach
    void cleanAndPrepareStrategy() {
        jdbc = new JdbcTemplate(dataSource);
        jdbc.update("TRUNCATE content_items, youtube_videos RESTART IDENTITY CASCADE");
        jdbc.update("DELETE FROM marketing_conversion_goals WHERE approved_by = 'TEST'");
        jdbc.update("DELETE FROM marketing_usp WHERE approved_by = 'TEST'");
        jdbc.update("""
                INSERT INTO marketing_usp (
                    title, description, evidence_type, evidence_reference, active, priority, approved_by)
                VALUES ('Verified ReadyRoad USP', 'Verified production capabilities',
                        'READYROAD_PRODUCTION_FEATURES', 'https://readyroad.be/', true, 1, 'TEST')
                ON CONFLICT DO NOTHING
                """);
        Long education = jdbc.queryForObject(
                "SELECT id FROM marketing_funnel_stages WHERE stage_key = 'EDUCATION'", Long.class);
        jdbc.update("""
                INSERT INTO marketing_conversion_goals (
                    goal_key, name, primary_cta, funnel_stage_id, active, approved_by)
                VALUES ('CONTINUE_TOPIC_LEARNING', 'Continue learning', 'تعلّم القاعدة بالتفصيل على ReadyRoad',
                        ?, true, 'TEST')
                ON CONFLICT (goal_key) DO NOTHING
                """, education);
    }

    @Test
    void migrationCreatesOnlyTheYouTubePhaseTablesAndDisabledDailySchedule() {
        List<String> tables = jdbc.queryForList("""
                SELECT table_name FROM information_schema.tables
                WHERE table_schema = 'public' AND table_name IN ('youtube_videos', 'content_items')
                """, String.class);
        assertThat(new HashSet<>(tables)).containsExactlyInAnyOrderElementsOf(
                Set.of("youtube_videos", "content_items"));
        assertThat(jdbc.queryForObject("""
                SELECT count(*) FROM agent_schedules
                WHERE agent_type = 'YOUTUBE' AND interval_days = 1 AND enabled = false
                """, Integer.class)).isEqualTo(1);
        assertThat(jdbc.queryForObject("""
                SELECT setting_value->>'intervalHours' FROM agent_settings
                WHERE agent_type = 'YOUTUBE' AND setting_key = 'youtube.monitoring'
                """, String.class)).isEqualTo("24");
    }

    @Test
    void videoAndContentHandoffsRemainIdempotent() throws Exception {
        YouTubeModels.Video video = video();
        store.saveChannel(channel());
        assertThat(store.upsertVideo(video, YouTubeSyncService.sourceHash(video), null)).isTrue();
        assertThat(store.upsertVideo(video, YouTubeSyncService.sourceHash(video), null)).isFalse();

        var first = packageService.createFor(video, null);
        var second = packageService.createFor(video, null);

        assertThat(first.packages()).isEqualTo(1);
        assertThat(first.drafts()).isEqualTo(4);
        assertThat(second.packages()).isZero();
        assertThat(second.drafts()).isZero();
        assertThat(store.videoCount()).isEqualTo(1);
        assertThat(store.contentPackageCount()).isEqualTo(1);
        assertThat(store.socialDraftCount()).isEqualTo(4);
        mockMvc.perform(get("/api/youtube/videos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.videos[0].videoId").value(video.videoId()));
        assertThat(jdbc.queryForList(
                "SELECT body FROM content_items WHERE item_type = 'YOUTUBE_SOCIAL_DRAFT'", String.class))
                .allMatch(body -> !body.contains("⚠"));
    }

    private static YouTubeModels.Video video() {
        return new YouTubeModels.Video(
                "m5iWWwzaaG0", "UCs_IDQXCz6zADuHIdfS2C2w", "ReadyRoad",
                "العلامات الإرشادية 2026", "شرح تعليمي واضح", Instant.parse("2026-08-10T12:00:00Z"),
                "https://i.ytimg.com/vi/m5iWWwzaaG0/hqdefault.jpg",
                "https://www.youtube.com/watch?v=m5iWWwzaaG0",
                "https://www.youtube-nocookie.com/embed/m5iWWwzaaG0", 0, "AR", "27", 120,
                100L, 10L, 2L);
    }

    private static YouTubeModels.Channel channel() {
        return new YouTubeModels.Channel(
                "UCs_IDQXCz6zADuHIdfS2C2w", "@RijBewijsBe", "ReadyRoad", "ReadyRoad channel",
                "https://yt3.ggpht.com/test", "UUs_IDQXCz6zADuHIdfS2C2w",
                "https://www.youtube.com/@RijBewijsBe/featured", 1000L, 100L, 13L);
    }
}
