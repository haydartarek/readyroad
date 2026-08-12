package com.readyroad.readyroadbackend.marketing.audit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.domain.AgentTask;
import com.readyroad.readyroadbackend.marketing.domain.MarketingAuditLog;
import com.readyroad.readyroadbackend.marketing.repository.MarketingAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketingAuditService {

    private final MarketingAuditLogRepository repository;
    private final ObjectMapper objectMapper;

    public MarketingAuditLog recordTaskEvent(AgentTask task, String eventType, String actor, JsonNode safeDetails) {
        return recordEntityEvent(
                eventType,
                actor,
                "AGENT_TASK",
                String.valueOf(task.getId()),
                task.getId(),
                task.getCorrelationId(),
                safeDetails);
    }

    public MarketingAuditLog recordEntityEvent(
            String eventType,
            String actor,
            String entityType,
            String entityId,
            Long taskId,
            String correlationId) {
        return recordEntityEvent(
                eventType, actor, entityType, entityId, taskId, correlationId, objectMapper.createObjectNode());
    }

    public MarketingAuditLog recordEntityEvent(
            String eventType,
            String actor,
            String entityType,
            String entityId,
            Long taskId,
            String correlationId,
            JsonNode safeDetails) {
        MarketingAuditLog audit = new MarketingAuditLog();
        audit.setTaskId(taskId);
        audit.setEventType(eventType);
        audit.setActor(actor);
        audit.setEntityType(entityType);
        audit.setEntityId(entityId);
        audit.setCorrelationId(correlationId);
        audit.setSafeDetails(safeDetails == null ? objectMapper.createObjectNode() : safeDetails.deepCopy());
        return repository.save(audit);
    }

    public JsonNode detail(String key, Object value) {
        return objectMapper.createObjectNode().putPOJO(key, value);
    }
}
