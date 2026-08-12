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
        MarketingAuditLog audit = new MarketingAuditLog();
        audit.setTaskId(task.getId());
        audit.setEventType(eventType);
        audit.setActor(actor);
        audit.setEntityType("AGENT_TASK");
        audit.setEntityId(String.valueOf(task.getId()));
        audit.setCorrelationId(task.getCorrelationId());
        audit.setSafeDetails(safeDetails == null ? objectMapper.createObjectNode() : safeDetails.deepCopy());
        return repository.save(audit);
    }

    public JsonNode detail(String key, Object value) {
        return objectMapper.createObjectNode().putPOJO(key, value);
    }
}
