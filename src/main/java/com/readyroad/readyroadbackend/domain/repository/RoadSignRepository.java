package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.enums.SignCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoadSignRepository extends JpaRepository<RoadSign, Long> {

    Optional<RoadSign> findBySignCode(String signCode);

    Optional<RoadSign> findByNormalizedSignCode(String normalizedSignCode);

    boolean existsBySignCode(String signCode);

    boolean existsByNormalizedSignCode(String normalizedSignCode);

    List<RoadSign> findAllByIsActiveTrueOrderBySignCodeAsc();

    List<RoadSign> findAllByCategoryAndIsActiveTrue(SignCategory category);

    long countByIsActiveTrue();
}
