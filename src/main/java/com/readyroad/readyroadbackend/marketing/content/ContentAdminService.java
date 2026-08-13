package com.readyroad.readyroadbackend.marketing.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.admin.MarketingTaskLifecycleResponse;
import com.readyroad.readyroadbackend.marketing.approval.ApprovalMetadata;
import com.readyroad.readyroadbackend.marketing.config.MarketingProperties;
import com.readyroad.readyroadbackend.marketing.domain.TaskPriority;
import com.readyroad.readyroadbackend.marketing.task.CreateMarketingTaskCommand;
import com.readyroad.readyroadbackend.marketing.task.TaskCreationService;
import java.sql.Timestamp;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ContentAdminService {

    private static final int MAX_PACKAGES = 100;

    private final MarketingProperties properties;
    private final TaskCreationService taskCreationService;
    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbc;

    @Transactional
    public MarketingTaskLifecycleResponse requestGeneration(
            ContentAdminDtos.GenerateRequest request,
            String actor) {
        if (!configured()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "OpenAI API key is not configured");
        }
        if (request.sourceType() != ContentSourceType.YOUTUBE && request.strategyContext() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Strategy context is required for this source");
        }
        var payload = objectMapper.valueToTree(new ContentTaskPayload(
                request.sourceType(), request.sourceId().trim(), request.strategyContext()));
        var result = taskCreationService.create(new CreateMarketingTaskCommand(
                ContentTaskHandler.AGENT_TYPE,
                ContentTaskHandler.GENERATE_PACKAGE,
                payload,
                TaskPriority.NORMAL,
                null,
                actor,
                request.idempotencyKey(),
                null,
                null,
                request.sourceType().name(),
                request.sourceId().trim(),
                ApprovalMetadata.standingOwnerAuthorization()));
        return MarketingTaskLifecycleResponse.from(result.task());
    }

    @Transactional(readOnly = true)
    public ContentAdminDtos.Status status() {
        Long packages = jdbc.queryForObject(
                "SELECT count(*) FROM content_items WHERE item_type = 'CONTENT_PACKAGE'", Long.class);
        Long variants = jdbc.queryForObject(
                "SELECT count(*) FROM content_items WHERE item_type = 'CONTENT_VARIANT'", Long.class);
        return new ContentAdminDtos.Status(
                configured(), "OPENAI", "RESPONSES",
                properties.getContent().getPrimaryModel(), properties.getContent().getReviewModel(),
                properties.getContent().getReasoningEffort(),
                ContentLocale.SUPPORTED.stream().map(Enum::name).toList(),
                packages == null ? 0 : packages,
                variants == null ? 0 : variants);
    }

    @Transactional(readOnly = true)
    public List<ContentAdminDtos.PackageItem> packages(Integer requestedLimit) {
        int limit = requestedLimit == null ? 25 : Math.max(1, Math.min(requestedLimit, MAX_PACKAGES));
        return jdbc.query("""
                SELECT p.id, p.source_type, p.source_id, p.title, p.status, p.created_at,
                       count(v.id) AS variant_count
                FROM content_items p
                LEFT JOIN content_items v ON v.parent_item_id = p.id AND v.item_type = 'CONTENT_VARIANT'
                WHERE p.item_type = 'CONTENT_PACKAGE'
                GROUP BY p.id, p.source_type, p.source_id, p.title, p.status, p.created_at
                ORDER BY p.created_at DESC, p.id DESC
                LIMIT ?
                """, (rs, row) -> new ContentAdminDtos.PackageItem(
                rs.getLong("id"), rs.getString("source_type"), rs.getString("source_id"),
                rs.getString("title"), rs.getString("status"), rs.getInt("variant_count"),
                timestamp(rs.getTimestamp("created_at"))), limit);
    }

    private boolean configured() {
        return properties.getContent().getApiKey() != null && !properties.getContent().getApiKey().isBlank();
    }

    private static java.time.Instant timestamp(Timestamp value) {
        return value == null ? null : value.toInstant();
    }
}
