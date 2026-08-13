package com.readyroad.readyroadbackend.marketing.youtube;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.strategy.BlockedStrategyContextException;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class YouTubeContentPackageService {

    private static final List<String> PLATFORMS =
            List.of("FACEBOOK", "INSTAGRAM", "TIKTOK", "YOUTUBE_COMMUNITY");

    private final JdbcTemplate jdbc;
    private final ObjectMapper objectMapper;

    @Transactional
    public PackageResult createFor(YouTubeModels.Video video, Long taskId) {
        StrategyIds strategy = strategy(video);
        String context = json(Map.of(
                "uspId", strategy.uspId(),
                "icpId", strategy.icpId(),
                "contentPillarId", strategy.pillarId(),
                "funnelStageId", strategy.funnelId(),
                "conversionGoalId", strategy.goalId(),
                "primaryCta", strategy.primaryCta(),
                "source", "YOUTUBE_VIDEO"));
        String packageKey = "youtube-package:" + video.videoId();
        List<Long> packageRows = jdbc.queryForList("""
                INSERT INTO content_items (
                    item_key, item_type, source_type, source_id, language, status, title, body,
                    usp_id, icp_id, content_pillar_id, funnel_stage_id, conversion_goal_id,
                    primary_cta, strategy_context, metadata, task_id)
                VALUES (?, 'YOUTUBE_CONTENT_PACKAGE', 'YOUTUBE_VIDEO', ?, ?, 'READY_FOR_CONTENT_AGENT',
                        ?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb, ?::jsonb, ?)
                ON CONFLICT (item_key) DO NOTHING RETURNING id
                """, Long.class, packageKey, video.videoId(), video.sourceLanguage(), video.title(),
                video.description(), strategy.uspId(), strategy.icpId(), strategy.pillarId(),
                strategy.funnelId(), strategy.goalId(), strategy.primaryCta(), context,
                json(Map.of("watchUrl", video.watchUrl(), "publishedAt", video.publishedAt().toString())), taskId);
        Long packageId = packageRows.isEmpty()
                ? jdbc.queryForObject("SELECT id FROM content_items WHERE item_key = ?", Long.class, packageKey)
                : packageRows.getFirst();
        int drafts = 0;
        for (String platform : PLATFORMS) {
            drafts += jdbc.update("""
                    INSERT INTO content_items (
                        item_key, item_type, source_type, source_id, parent_item_id, language, platform,
                        status, title, body, usp_id, icp_id, content_pillar_id, funnel_stage_id,
                        conversion_goal_id, primary_cta, strategy_context, metadata, task_id)
                    VALUES (?, 'YOUTUBE_SOCIAL_DRAFT', 'YOUTUBE_VIDEO', ?, ?, ?, ?, 'DRAFT',
                            ?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb, ?::jsonb, ?)
                    ON CONFLICT (item_key) DO NOTHING
                    """, "youtube-social:" + video.videoId() + ":" + platform.toLowerCase(Locale.ROOT),
                    video.videoId(), packageId, video.sourceLanguage(), platform,
                    clean(video.title()), draftBody(platform, video), strategy.uspId(), strategy.icpId(),
                    strategy.pillarId(), strategy.funnelId(), strategy.goalId(), strategy.primaryCta(),
                    context, json(Map.of("platform", platform, "sourceUrl", video.watchUrl())), taskId);
        }
        return new PackageResult(packageRows.isEmpty() ? 0 : 1, drafts);
    }

    StrategyIds strategy(YouTubeModels.Video video) {
        long uspId = requiredLong("SELECT id FROM marketing_usp WHERE active ORDER BY priority DESC, id LIMIT 1", "USP");
        String icpId = switch (video.sourceLanguage()) {
            case "AR" -> "ICP-AR-BEGINNER";
            case "NL" -> "ICP-NL-PRACTICE";
            case "FR" -> "ICP-FR-THEORY";
            default -> throw new BlockedStrategyContextException("YOUTUBE_ICP_FOR_SOURCE_LANGUAGE");
        };
        Integer icpExists = jdbc.queryForObject(
                "SELECT count(*) FROM marketing_icp WHERE id = ? AND active", Integer.class, icpId);
        if (icpExists == null || icpExists == 0) {
            throw new BlockedStrategyContextException("ICP");
        }
        String pillarKey = classifyPillar(video.title() + " " + video.description());
        long pillarId = requiredLong(
                "SELECT id FROM marketing_content_pillars WHERE active AND pillar_key = ?", "CONTENT_PILLAR", pillarKey);
        long funnelId = requiredLong(
                "SELECT id FROM marketing_funnel_stages WHERE active AND stage_key = 'EDUCATION'", "FUNNEL_STAGE");
        List<Map<String, Object>> goals = jdbc.queryForList("""
                SELECT id, primary_cta FROM marketing_conversion_goals
                WHERE active AND goal_key = 'CONTINUE_TOPIC_LEARNING' AND funnel_stage_id = ?
                """, funnelId);
        if (goals.size() != 1) {
            throw new BlockedStrategyContextException("CONVERSION_GOAL");
        }
        return new StrategyIds(
                uspId, icpId, pillarId, funnelId,
                ((Number) goals.getFirst().get("id")).longValue(),
                String.valueOf(goals.getFirst().get("primary_cta")));
    }

    static String classifyPillar(String source) {
        String text = Normalizer.normalize(source == null ? "" : source, Normalizer.Form.NFKC)
                .toLowerCase(Locale.ROOT);
        if (contains(text, "أولو", "voorrang", "priorité", "priority")) return "PRIORITY_INTERSECTIONS";
        if (contains(text, "وقوف", "توقف", "سرعة", "parkeren", "stationnement", "speed")) {
            return "SPEED_PARKING_STOPPING";
        }
        if (contains(text, "علام", "إشار", "verkeersbord", "panneau", "traffic sign")) return "TRAFFIC_SIGNS";
        if (contains(text, "خطأ", "fout", "erreur", "mistake")) return "COMMON_EXAM_ERRORS";
        if (contains(text, "تحضير", "voorbereid", "prépar", "prepare")) return "PREPARATION_TIPS";
        if (contains(text, "تدريب", "اختبار", "oefen", "quiz", "practice")) return "TRAINING_TESTS";
        if (contains(text, "عملي", "praktijk", "pratique", "practical")) return "PRACTICAL_EXAM";
        if (contains(text, "امتحان", "examen", "exam")) return "THEORY_EXAM";
        return "READYROAD_EDUCATIONAL_VIDEOS";
    }

    static String clean(String value) {
        if (value == null) return "";
        return value.replaceAll("[\\p{So}\\x{FE0F}]", " ").replaceAll("\\s+", " ").trim();
    }

    private static String draftBody(String platform, YouTubeModels.Video video) {
        String title = clean(video.title());
        String description = clean(video.description());
        int max = switch (platform) {
            case "FACEBOOK" -> 280;
            case "INSTAGRAM" -> 180;
            case "TIKTOK" -> 100;
            default -> 160;
        };
        String summary = description.length() <= max ? description : description.substring(0, max).stripTrailing();
        return switch (platform) {
            case "FACEBOOK" -> join(title, summary, video.watchUrl());
            case "INSTAGRAM" -> join(title, summary);
            case "TIKTOK" -> title;
            case "YOUTUBE_COMMUNITY" -> join(title, video.watchUrl());
            default -> title;
        };
    }

    private long requiredLong(String sql, String context, Object... args) {
        List<Long> values = jdbc.queryForList(sql, Long.class, args);
        if (values.size() != 1) {
            throw new BlockedStrategyContextException(context);
        }
        return values.getFirst();
    }

    private String json(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException error) {
            throw new IllegalStateException("Unable to serialize YouTube content package", error);
        }
    }

    private static boolean contains(String source, String... values) {
        for (String value : values) if (source.contains(value)) return true;
        return false;
    }

    private static String join(String... values) {
        return java.util.Arrays.stream(values)
                .filter(value -> value != null && !value.isBlank())
                .collect(java.util.stream.Collectors.joining("\n\n"));
    }

    record StrategyIds(long uspId, String icpId, long pillarId, long funnelId, long goalId, String primaryCta) {}

    public record PackageResult(int packages, int drafts) {}
}
