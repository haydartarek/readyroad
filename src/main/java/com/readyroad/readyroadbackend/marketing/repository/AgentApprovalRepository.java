package com.readyroad.readyroadbackend.marketing.repository;

import com.readyroad.readyroadbackend.marketing.domain.AgentApproval;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentApprovalRepository extends JpaRepository<AgentApproval, Long> {
    Optional<AgentApproval> findByTaskIdAndPayloadVersion(Long taskId, int payloadVersion);
}
