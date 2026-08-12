package com.readyroad.readyroadbackend.marketing.analytics;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.repository.AgentSettingRepository;
import com.readyroad.readyroadbackend.marketing.settings.AgentSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnalyticsSettingsService {

    public static final String AGENT_TYPE = "ANALYTICS";
    public static final String POLICY_KEY = "sync.policy";
    public static final String THRESHOLDS_KEY = "opportunity.thresholds";

    private final AgentSettingRepository repository;
    private final AgentSettingsService settingsService;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public AnalyticsSettings current() {
        AnalyticsSettings defaults = AnalyticsSettings.defaults();
        JsonNode policy = value(POLICY_KEY);
        JsonNode thresholds = value(THRESHOLDS_KEY);
        return new AnalyticsSettings(
                integer(policy, "initialBackfillDays", defaults.initialBackfillDays()),
                integer(policy, "intervalDays", defaults.intervalDays()),
                integer(policy, "noDataDays", defaults.noDataDays()),
                integer(policy, "sourceFailureHours", defaults.sourceFailureHours()),
                integer(thresholds, "windowDays", defaults.windowDays()),
                decimal(thresholds, "emergingImpressions", defaults.emergingImpressions()),
                decimal(thresholds, "emergingPositionMin", defaults.emergingPositionMin()),
                decimal(thresholds, "emergingPositionMax", defaults.emergingPositionMax()),
                decimal(thresholds, "opportunityImpressions", defaults.opportunityImpressions()),
                decimal(thresholds, "opportunityPositionMin", defaults.opportunityPositionMin()),
                decimal(thresholds, "opportunityPositionMax", defaults.opportunityPositionMax()),
                decimal(thresholds, "establishedPositionMax", defaults.establishedPositionMax()),
                decimal(thresholds, "establishedClicks", defaults.establishedClicks()),
                decimal(thresholds, "positionDecline", defaults.positionDecline()),
                decimal(thresholds, "clicksDeclinePercent", defaults.clicksDeclinePercent()),
                decimal(thresholds, "ctrDeclinePercent", defaults.ctrDeclinePercent()),
                integer(thresholds, "stableWindows", defaults.stableWindows()));
    }

    @Transactional
    public void update(JsonNode policy, JsonNode thresholds, String actor) {
        AnalyticsSettings candidate = map(policy, thresholds);
        validate(candidate);
        settingsService.saveNonSecret(AGENT_TYPE, POLICY_KEY, policy, actor);
        settingsService.saveNonSecret(AGENT_TYPE, THRESHOLDS_KEY, thresholds, actor);
    }

    private AnalyticsSettings map(JsonNode policy, JsonNode thresholds) {
        AnalyticsSettings defaults = AnalyticsSettings.defaults();
        return new AnalyticsSettings(
                integer(policy, "initialBackfillDays", defaults.initialBackfillDays()),
                integer(policy, "intervalDays", defaults.intervalDays()),
                integer(policy, "noDataDays", defaults.noDataDays()),
                integer(policy, "sourceFailureHours", defaults.sourceFailureHours()),
                integer(thresholds, "windowDays", defaults.windowDays()),
                decimal(thresholds, "emergingImpressions", defaults.emergingImpressions()),
                decimal(thresholds, "emergingPositionMin", defaults.emergingPositionMin()),
                decimal(thresholds, "emergingPositionMax", defaults.emergingPositionMax()),
                decimal(thresholds, "opportunityImpressions", defaults.opportunityImpressions()),
                decimal(thresholds, "opportunityPositionMin", defaults.opportunityPositionMin()),
                decimal(thresholds, "opportunityPositionMax", defaults.opportunityPositionMax()),
                decimal(thresholds, "establishedPositionMax", defaults.establishedPositionMax()),
                decimal(thresholds, "establishedClicks", defaults.establishedClicks()),
                decimal(thresholds, "positionDecline", defaults.positionDecline()),
                decimal(thresholds, "clicksDeclinePercent", defaults.clicksDeclinePercent()),
                decimal(thresholds, "ctrDeclinePercent", defaults.ctrDeclinePercent()),
                integer(thresholds, "stableWindows", defaults.stableWindows()));
    }

    private JsonNode value(String key) {
        return repository.findByAgentTypeAndSettingKey(AGENT_TYPE, key)
                .<JsonNode>map(setting -> setting.getSettingValue().deepCopy())
                .orElseGet(objectMapper::createObjectNode);
    }

    private static int integer(JsonNode node, String key, int fallback) {
        return node != null && node.path(key).canConvertToInt() ? node.path(key).asInt() : fallback;
    }

    private static double decimal(JsonNode node, String key, double fallback) {
        return node != null && node.path(key).isNumber() ? node.path(key).asDouble() : fallback;
    }

    private static void validate(AnalyticsSettings value) {
        if (value.initialBackfillDays() < 1 || value.initialBackfillDays() > 480
                || value.intervalDays() < 1 || value.intervalDays() > 30
                || value.noDataDays() < 1 || value.noDataDays() > 30
                || value.sourceFailureHours() < 1 || value.sourceFailureHours() > 72
                || value.windowDays() < 7 || value.windowDays() > 90
                || value.emergingImpressions() < 0 || value.opportunityImpressions() < 0
                || value.establishedClicks() < 0 || value.stableWindows() < 2) {
            throw new IllegalArgumentException("Analytics settings are outside the allowed range");
        }
        if (value.emergingPositionMin() > value.emergingPositionMax()
                || value.opportunityPositionMin() > value.opportunityPositionMax()
                || value.positionDecline() <= 0
                || value.clicksDeclinePercent() <= 0
                || value.ctrDeclinePercent() <= 0) {
            throw new IllegalArgumentException("Analytics threshold ranges are invalid");
        }
    }
}
