package com.readyroad.readyroadbackend.marketing.settings;

import com.fasterxml.jackson.databind.JsonNode;
import com.readyroad.readyroadbackend.marketing.domain.AgentSetting;
import com.readyroad.readyroadbackend.marketing.repository.AgentDefinitionRepository;
import com.readyroad.readyroadbackend.marketing.repository.AgentSettingRepository;
import java.util.Locale;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgentSettingsService {

    private static final Set<String> FORBIDDEN_KEY_PARTS = Set.of(
            "PASSWORD", "SECRET", "TOKEN", "API_KEY", "PRIVATE_KEY", "CREDENTIAL");

    private final AgentDefinitionRepository definitionRepository;
    private final AgentSettingRepository settingRepository;

    @Transactional
    public AgentSetting saveNonSecret(String agentType, String key, JsonNode value, String actor) {
        requireText(agentType, "agentType");
        requireText(key, "key");
        requireText(actor, "actor");
        if (value == null) {
            throw new IllegalArgumentException("value is required");
        }
        String normalizedKey = key.trim().toUpperCase(Locale.ROOT);
        if (containsForbiddenKey(normalizedKey) || containsForbiddenJsonField(value)) {
            throw new IllegalArgumentException("Secrets must be stored outside agent_settings");
        }
        if (definitionRepository.findByAgentType(agentType).isEmpty()) {
            throw new IllegalArgumentException("Unknown marketing agent: " + agentType);
        }

        AgentSetting setting = settingRepository.findByAgentTypeAndSettingKey(agentType, key.trim())
                .orElseGet(AgentSetting::new);
        setting.setAgentType(agentType);
        setting.setSettingKey(key.trim());
        setting.setSettingValue(value.deepCopy());
        setting.setUpdatedBy(actor);
        return settingRepository.save(setting);
    }

    private static boolean containsForbiddenJsonField(JsonNode value) {
        if (value.isObject()) {
            for (var field : value.properties()) {
                if (containsForbiddenKey(field.getKey().toUpperCase(Locale.ROOT))
                        || containsForbiddenJsonField(field.getValue())) {
                    return true;
                }
            }
        } else if (value.isArray()) {
            for (JsonNode item : value) {
                if (containsForbiddenJsonField(item)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean containsForbiddenKey(String normalizedKey) {
        return FORBIDDEN_KEY_PARTS.stream().anyMatch(normalizedKey::contains);
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
    }
}
