package com.readyroad.readyroadbackend.marketing.repository;

import com.readyroad.readyroadbackend.marketing.domain.AgentDefinition;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentDefinitionRepository extends JpaRepository<AgentDefinition, Long> {
    Optional<AgentDefinition> findByAgentType(String agentType);

    List<AgentDefinition> findAllByOrderByAgentTypeAsc();

    long countByEnabledTrue();
}
