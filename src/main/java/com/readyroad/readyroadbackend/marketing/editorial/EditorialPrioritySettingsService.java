package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.repository.AgentSettingRepository;
import com.readyroad.readyroadbackend.marketing.settings.AgentSettingsService;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EditorialPrioritySettingsService {

    public static final String AGENT_TYPE = "EDITORIAL";
    public static final String SETTING_KEY = "priority.scoring";

    private final AgentSettingRepository repository;
    private final AgentSettingsService settingsService;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public JsonNode raw() {
        return repository.findByAgentTypeAndSettingKey(AGENT_TYPE, SETTING_KEY)
                .<JsonNode>map(setting -> setting.getSettingValue().deepCopy())
                .orElseGet(() -> toJson(EditorialPriorityConfig.defaults()));
    }

    @Transactional(readOnly = true)
    public EditorialPriorityConfig current() {
        return parse(raw());
    }

    @Transactional
    public EditorialPriorityConfig update(JsonNode value, String actor) {
        EditorialPriorityConfig config = parse(value);
        settingsService.saveNonSecret(AGENT_TYPE, SETTING_KEY, value, actor);
        return config;
    }

    EditorialPriorityConfig parse(JsonNode value) {
        if (value == null || !value.isObject()) {
            throw new IllegalArgumentException("Editorial priority settings must be an object");
        }
        JsonNode weightsNode = value.path("weights");
        JsonNode thresholdsNode = value.path("thresholds");
        Map<String, BigDecimal> weights = new LinkedHashMap<>();
        for (String key : EditorialPriorityConfig.defaults().weights().keySet()) {
            weights.put(key, decimal(weightsNode, key));
        }
        BigDecimal total = weights.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        if (total.compareTo(BigDecimal.valueOf(100)) != 0
                || weights.values().stream().anyMatch(weight -> weight.signum() < 0)) {
            throw new IllegalArgumentException("Editorial priority weights must be non-negative and total 100");
        }
        EditorialPriorityConfig config = new EditorialPriorityConfig(
                Map.copyOf(weights),
                decimal(thresholdsNode, "p0"),
                decimal(thresholdsNode, "p1"),
                decimal(thresholdsNode, "p2"),
                decimal(thresholdsNode, "p3"),
                decimal(value, "missingSearchConsolePercent"));
        validate(config);
        return config;
    }

    private JsonNode toJson(EditorialPriorityConfig config) {
        var root = objectMapper.createObjectNode();
        root.set("weights", objectMapper.valueToTree(config.weights()));
        root.set("thresholds", objectMapper.createObjectNode()
                .put("p0", config.p0()).put("p1", config.p1())
                .put("p2", config.p2()).put("p3", config.p3()));
        root.put("missingSearchConsolePercent", config.missingSearchConsolePercent());
        return root;
    }

    private static void validate(EditorialPriorityConfig config) {
        if (config.p0().compareTo(BigDecimal.valueOf(100)) > 0
                || config.p0().compareTo(config.p1()) <= 0
                || config.p1().compareTo(config.p2()) <= 0
                || config.p2().compareTo(config.p3()) <= 0
                || config.p3().compareTo(BigDecimal.ZERO) != 0
                || config.missingSearchConsolePercent().compareTo(BigDecimal.ZERO) < 0
                || config.missingSearchConsolePercent().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Editorial priority thresholds are invalid");
        }
    }

    private static BigDecimal decimal(JsonNode node, String key) {
        JsonNode value = node == null ? null : node.get(key);
        if (value == null || !value.isNumber()) {
            throw new IllegalArgumentException(key + " must be numeric");
        }
        return value.decimalValue();
    }
}
