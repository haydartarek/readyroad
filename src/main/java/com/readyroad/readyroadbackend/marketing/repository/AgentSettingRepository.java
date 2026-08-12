package com.readyroad.readyroadbackend.marketing.repository;

import com.readyroad.readyroadbackend.marketing.domain.AgentSetting;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentSettingRepository extends JpaRepository<AgentSetting, Long> {
    Optional<AgentSetting> findByAgentTypeAndSettingKey(String agentType, String settingKey);

    List<AgentSetting> findAllByOrderByAgentTypeAscSettingKeyAsc();
}
