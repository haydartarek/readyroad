package com.readyroad.readyroadbackend.marketing.audit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.domain.AgentExecutionLog;
import com.readyroad.readyroadbackend.marketing.domain.ExecutionLogLevel;
import com.readyroad.readyroadbackend.marketing.repository.AgentExecutionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExecutionLogService {

    private final AgentExecutionLogRepository repository;
    private final ObjectMapper objectMapper;

    public AgentExecutionLog record(
            Long taskId,
            Long attemptId,
            ExecutionLogLevel level,
            String eventCode,
            String message,
            JsonNode safeContext) {
        AgentExecutionLog log = new AgentExecutionLog();
        log.setTaskId(taskId);
        log.setAttemptId(attemptId);
        log.setLevel(level);
        log.setEventCode(eventCode);
        log.setMessage(message);
        log.setSafeContext(safeContext == null ? objectMapper.createObjectNode() : safeContext.deepCopy());
        return repository.save(log);
    }
}
