package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.AdminSystemSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminSystemSettingsRepository extends JpaRepository<AdminSystemSettings, Long> {
    Optional<AdminSystemSettings> findFirstByOrderByIdAsc();
}
