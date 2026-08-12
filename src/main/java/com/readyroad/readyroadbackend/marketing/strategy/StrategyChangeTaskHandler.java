package com.readyroad.readyroadbackend.marketing.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.audit.MarketingAuditService;
import com.readyroad.readyroadbackend.marketing.domain.AgentTask;
import com.readyroad.readyroadbackend.marketing.repository.AgentTaskRepository;
import com.readyroad.readyroadbackend.marketing.strategy.domain.MarketingContentPillar;
import com.readyroad.readyroadbackend.marketing.strategy.domain.MarketingConversionGoal;
import com.readyroad.readyroadbackend.marketing.strategy.domain.MarketingFunnelStage;
import com.readyroad.readyroadbackend.marketing.strategy.domain.MarketingIcp;
import com.readyroad.readyroadbackend.marketing.strategy.domain.MarketingPositioning;
import com.readyroad.readyroadbackend.marketing.strategy.domain.MarketingUsp;
import com.readyroad.readyroadbackend.marketing.strategy.domain.SocialProofItem;
import com.readyroad.readyroadbackend.marketing.strategy.repository.MarketingContentPillarRepository;
import com.readyroad.readyroadbackend.marketing.strategy.repository.MarketingConversionGoalRepository;
import com.readyroad.readyroadbackend.marketing.strategy.repository.MarketingFunnelStageRepository;
import com.readyroad.readyroadbackend.marketing.strategy.repository.MarketingIcpRepository;
import com.readyroad.readyroadbackend.marketing.strategy.repository.MarketingPositioningRepository;
import com.readyroad.readyroadbackend.marketing.strategy.repository.MarketingUspRepository;
import com.readyroad.readyroadbackend.marketing.strategy.repository.SocialProofItemRepository;
import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import com.readyroad.readyroadbackend.marketing.worker.MarketingTaskHandler;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class StrategyChangeTaskHandler implements MarketingTaskHandler {

    private final AgentTaskRepository taskRepository;
    private final MarketingUspRepository uspRepository;
    private final MarketingIcpRepository icpRepository;
    private final MarketingPositioningRepository positioningRepository;
    private final MarketingContentPillarRepository pillarRepository;
    private final MarketingFunnelStageRepository funnelRepository;
    private final MarketingConversionGoalRepository conversionGoalRepository;
    private final SocialProofItemRepository socialProofRepository;
    private final StrategyChangeValidator validator;
    private final MarketingAuditService auditService;
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(String agentType, String taskType) {
        return MarketingStrategyChangeService.AGENT_TYPE.equals(agentType)
                && MarketingStrategyChangeService.TASK_TYPE.equals(taskType);
    }

    @Override
    @Transactional
    public void execute(ClaimedTask claimedTask) {
        JsonNode payload = claimedTask.payload();
        StrategyResourceType type = resourceType(payload);
        String resourceId = textOrNull(payload, "resourceId");
        JsonNode data = payload.get("data");
        validator.validate(type, resourceId, data);

        AgentTask task = taskRepository.findById(claimedTask.taskId())
                .orElseThrow(() -> StrategyChangeValidator.invalid("Strategy task not found"));
        String approvedBy = task.getApprovedBy();
        if (approvedBy == null || approvedBy.isBlank()) {
            throw StrategyChangeValidator.invalid("Approved strategy actor is required");
        }

        String savedId = switch (type) {
            case USP -> saveUsp(resourceId, data, approvedBy);
            case ICP -> saveIcp(resourceId, data, approvedBy);
            case POSITIONING -> savePositioning(resourceId, data, approvedBy);
            case CONTENT_PILLAR -> savePillar(resourceId, data, approvedBy);
            case FUNNEL_STAGE -> saveFunnelStage(resourceId, data, approvedBy);
            case CONVERSION_GOAL -> saveConversionGoal(resourceId, data, approvedBy);
            case SOCIAL_PROOF -> saveSocialProof(resourceId, data, approvedBy);
        };

        auditService.recordTaskEvent(task, "STRATEGY_UPDATED", approvedBy,
                objectMapper.createObjectNode()
                        .put("resourceType", type.name())
                        .put("resourceId", savedId));
    }

    private String saveUsp(String resourceId, JsonNode data, String actor) {
        MarketingUsp entity = entity(resourceId, uspRepository::findById, MarketingUsp::new);
        entity.setTitle(text(data, "title"));
        entity.setDescription(text(data, "description"));
        entity.setEvidenceType(text(data, "evidenceType"));
        entity.setEvidenceReference(text(data, "evidenceReference"));
        entity.setPriority((short) data.path("priority").asInt());
        applyApproval(entity, data, actor);
        return String.valueOf(uspRepository.save(entity).getId());
    }

    private String saveIcp(String resourceId, JsonNode data, String actor) {
        MarketingIcp entity = icpRepository.findById(resourceId).orElseGet(MarketingIcp::new);
        entity.setId(resourceId);
        entity.setName(text(data, "name"));
        entity.setLanguage(textOrNull(data, "language"));
        entity.setCountry(textOrNull(data, "country"));
        entity.setRegion(textOrNull(data, "region"));
        entity.setPrimaryGoal(textOrNull(data, "primaryGoal"));
        entity.setMainProblem(textOrNull(data, "mainProblem"));
        entity.setSearchIntent(textOrNull(data, "searchIntent"));
        entity.setPreferredContentType(textOrNull(data, "preferredContentType"));
        entity.setPreferredChannel(textOrNull(data, "preferredChannel"));
        entity.setMainObjections(data.has("mainObjections") && data.get("mainObjections").isArray()
                ? data.get("mainObjections").deepCopy()
                : objectMapper.createArrayNode());
        entity.setFunnelStage(textOrNull(data, "funnelStage"));
        entity.setConversionGoal(textOrNull(data, "conversionGoal"));
        applyApproval(entity, data, actor);
        return icpRepository.save(entity).getId();
    }

    private String savePositioning(String resourceId, JsonNode data, String actor) {
        MarketingPositioning entity = entity(
                resourceId, positioningRepository::findById, MarketingPositioning::new);
        entity.setStatement(text(data, "statement"));
        entity.setBrandIdentity(data.get("brandIdentity").deepCopy());
        entity.setBrandVoice(data.get("brandVoice").deepCopy());
        applyApproval(entity, data, actor);
        return String.valueOf(positioningRepository.save(entity).getId());
    }

    private String savePillar(String resourceId, JsonNode data, String actor) {
        MarketingContentPillar entity = entity(
                resourceId, pillarRepository::findById, MarketingContentPillar::new);
        entity.setPillarKey(text(data, "pillarKey"));
        entity.setName(text(data, "name"));
        entity.setPriority((short) data.path("priority").asInt());
        applyApproval(entity, data, actor);
        return String.valueOf(pillarRepository.save(entity).getId());
    }

    private String saveFunnelStage(String resourceId, JsonNode data, String actor) {
        MarketingFunnelStage entity = entity(
                resourceId, funnelRepository::findById, MarketingFunnelStage::new);
        entity.setStageKey(text(data, "stageKey"));
        entity.setSequenceNumber((short) data.path("sequenceNumber").asInt());
        applyApproval(entity, data, actor);
        return String.valueOf(funnelRepository.save(entity).getId());
    }

    private String saveConversionGoal(String resourceId, JsonNode data, String actor) {
        Long funnelStageId = data.path("funnelStageId").asLong();
        if (funnelRepository.findByIdAndActiveTrue(funnelStageId).isEmpty()) {
            throw new BlockedStrategyContextException("FUNNEL_STAGE");
        }
        MarketingConversionGoal entity = entity(
                resourceId, conversionGoalRepository::findById, MarketingConversionGoal::new);
        entity.setGoalKey(text(data, "goalKey"));
        entity.setName(text(data, "name"));
        entity.setDescription(textOrNull(data, "description"));
        entity.setPrimaryCta(text(data, "primaryCta"));
        entity.setFunnelStageId(funnelStageId);
        applyApproval(entity, data, actor);
        return String.valueOf(conversionGoalRepository.save(entity).getId());
    }

    private String saveSocialProof(String resourceId, JsonNode data, String actor) {
        SocialProofItem entity = entity(resourceId, socialProofRepository::findById, SocialProofItem::new);
        entity.setProofType(text(data, "proofType"));
        entity.setClaim(text(data, "claim"));
        entity.setEvidenceReference(text(data, "evidenceReference"));
        applyApproval(entity, data, actor);
        return String.valueOf(socialProofRepository.save(entity).getId());
    }

    private static StrategyResourceType resourceType(JsonNode payload) {
        try {
            return StrategyResourceType.valueOf(text(payload, "resourceType"));
        } catch (IllegalArgumentException error) {
            throw StrategyChangeValidator.invalid("Unsupported strategy resource type");
        }
    }

    private static String text(JsonNode data, String field) {
        String value = textOrNull(data, field);
        if (value == null) {
            throw StrategyChangeValidator.invalid(field + " is required");
        }
        return value;
    }

    private static String textOrNull(JsonNode data, String field) {
        JsonNode value = data == null ? null : data.get(field);
        return value == null || value.isNull() || !value.isTextual() || value.asText().isBlank()
                ? null
                : value.asText().trim();
    }

    private static <T> T entity(
            String resourceId,
            java.util.function.Function<Long, java.util.Optional<T>> finder,
            Supplier<T> factory) {
        return resourceId == null || resourceId.isBlank()
                ? factory.get()
                : finder.apply(StrategyChangeValidator.parseId(resourceId))
                        .orElseThrow(() -> StrategyChangeValidator.invalid("Strategy resource not found"));
    }

    private static void applyApproval(
            com.readyroad.readyroadbackend.marketing.strategy.domain.StrategyRecord entity,
            JsonNode data,
            String actor) {
        if (data.has("active")) {
            entity.setActive(data.get("active").asBoolean());
        }
        entity.setApprovedBy(actor);
    }
}
