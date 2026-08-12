package com.readyroad.readyroadbackend.marketing.strategy;

import com.readyroad.readyroadbackend.marketing.strategy.domain.MarketingContentPillar;
import com.readyroad.readyroadbackend.marketing.strategy.domain.MarketingConversionGoal;
import com.readyroad.readyroadbackend.marketing.strategy.domain.MarketingFunnelStage;
import com.readyroad.readyroadbackend.marketing.strategy.domain.MarketingIcp;
import com.readyroad.readyroadbackend.marketing.strategy.domain.MarketingPositioning;
import com.readyroad.readyroadbackend.marketing.strategy.domain.MarketingUsp;
import com.readyroad.readyroadbackend.marketing.strategy.domain.SocialProofItem;

public final class StrategyDtos {

    private StrategyDtos() {}

    public record Usp(
            Long id,
            String title,
            String description,
            String evidenceType,
            String evidenceReference,
            boolean active,
            short priority,
            String approvedBy) {
        static Usp from(MarketingUsp entity) {
            return new Usp(
                    entity.getId(), entity.getTitle(), entity.getDescription(), entity.getEvidenceType(),
                    entity.getEvidenceReference(), entity.isActive(), entity.getPriority(), entity.getApprovedBy());
        }
    }

    public record Icp(
            String id,
            String name,
            String language,
            String country,
            String region,
            String primaryGoal,
            String mainProblem,
            String searchIntent,
            String preferredContentType,
            String preferredChannel,
            Object mainObjections,
            String funnelStage,
            String conversionGoal,
            boolean active,
            String approvedBy) {
        static Icp from(MarketingIcp entity) {
            return new Icp(
                    entity.getId(), entity.getName(), entity.getLanguage(), entity.getCountry(), entity.getRegion(),
                    entity.getPrimaryGoal(), entity.getMainProblem(), entity.getSearchIntent(),
                    entity.getPreferredContentType(), entity.getPreferredChannel(), copy(entity.getMainObjections()),
                    entity.getFunnelStage(), entity.getConversionGoal(), entity.isActive(), entity.getApprovedBy());
        }
    }

    public record Positioning(
            Long id,
            String statement,
            Object brandIdentity,
            Object brandVoice,
            boolean active,
            String approvedBy) {
        static Positioning from(MarketingPositioning entity) {
            return new Positioning(
                    entity.getId(), entity.getStatement(), copy(entity.getBrandIdentity()), copy(entity.getBrandVoice()),
                    entity.isActive(), entity.getApprovedBy());
        }
    }

    public record ContentPillar(
            Long id,
            String pillarKey,
            String name,
            boolean active,
            short priority,
            String approvedBy) {
        static ContentPillar from(MarketingContentPillar entity) {
            return new ContentPillar(
                    entity.getId(), entity.getPillarKey(), entity.getName(), entity.isActive(),
                    entity.getPriority(), entity.getApprovedBy());
        }
    }

    public record FunnelStage(
            Long id,
            String stageKey,
            short sequenceNumber,
            boolean active,
            String approvedBy) {
        static FunnelStage from(MarketingFunnelStage entity) {
            return new FunnelStage(
                    entity.getId(), entity.getStageKey(), entity.getSequenceNumber(), entity.isActive(),
                    entity.getApprovedBy());
        }
    }

    public record ConversionGoal(
            Long id,
            String goalKey,
            String name,
            String description,
            String primaryCta,
            Long funnelStageId,
            boolean active,
            String approvedBy) {
        static ConversionGoal from(MarketingConversionGoal entity) {
            return new ConversionGoal(
                    entity.getId(), entity.getGoalKey(), entity.getName(), entity.getDescription(),
                    entity.getPrimaryCta(), entity.getFunnelStageId(), entity.isActive(), entity.getApprovedBy());
        }
    }

    public record SocialProof(
            Long id,
            String proofType,
            String claim,
            String evidenceReference,
            boolean active,
            String approvedBy) {
        static SocialProof from(SocialProofItem entity) {
            return new SocialProof(
                    entity.getId(), entity.getProofType(), entity.getClaim(), entity.getEvidenceReference(),
                    entity.isActive(), entity.getApprovedBy());
        }
    }

    private static Object copy(com.fasterxml.jackson.databind.JsonNode value) {
        if (value == null || value.isNull()) {
            return null;
        }
        if (value.isArray()) {
            java.util.List<Object> items = new java.util.ArrayList<>();
            value.forEach(item -> items.add(copy(item)));
            return java.util.List.copyOf(items);
        }
        if (value.isObject()) {
            java.util.Map<String, Object> fields = new java.util.LinkedHashMap<>();
            value.properties().forEach(entry -> fields.put(entry.getKey(), copy(entry.getValue())));
            return java.util.Collections.unmodifiableMap(fields);
        }
        if (value.isBoolean()) {
            return value.booleanValue();
        }
        if (value.isIntegralNumber()) {
            return value.longValue();
        }
        if (value.isFloatingPointNumber()) {
            return value.doubleValue();
        }
        return value.asText();
    }
}
