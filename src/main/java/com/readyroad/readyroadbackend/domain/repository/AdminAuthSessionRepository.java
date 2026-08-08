package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.AdminAuthSession;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminAuthSessionRepository extends JpaRepository<AdminAuthSession, Long> {

    Optional<AdminAuthSession> findBySessionIdAndUserUsername(UUID sessionId, String username);
}
