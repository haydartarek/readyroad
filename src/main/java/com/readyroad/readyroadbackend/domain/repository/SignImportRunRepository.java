package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.SignImportRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SignImportRunRepository extends JpaRepository<SignImportRun, Long> {

    /** Fetches the most recent import run record. */
    Optional<SignImportRun> findTopByOrderByCreatedAtDesc();
}
