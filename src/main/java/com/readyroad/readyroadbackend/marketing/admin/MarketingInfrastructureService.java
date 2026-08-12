package com.readyroad.readyroadbackend.marketing.admin;

import com.readyroad.readyroadbackend.marketing.config.MarketingProperties;
import com.readyroad.readyroadbackend.marketing.domain.TaskStatus;
import com.readyroad.readyroadbackend.marketing.repository.AgentTaskRepository;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketingInfrastructureService {

    private final AgentTaskRepository taskRepository;
    private final MarketingProperties properties;

    public MarketingInfrastructureResponse status() {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (TaskStatus status : TaskStatus.values()) {
            counts.put(status.name(), taskRepository.countByStatus(status));
        }
        return new MarketingInfrastructureResponse(
                properties.isEnabled(),
                properties.getWorker().getPollIntervalMs(),
                properties.getWorker().getBatchSize(),
                properties.getWorker().getLockTtl().toSeconds(),
                Map.copyOf(counts));
    }
}
