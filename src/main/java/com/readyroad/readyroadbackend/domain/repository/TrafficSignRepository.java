package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.TrafficSign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrafficSignRepository extends JpaRepository<TrafficSign, Long> {

    List<TrafficSign> findAllByIsActiveTrue();

    List<TrafficSign> findAllByCategoryIdAndIsActiveTrue(Long categoryId);

    Optional<TrafficSign> findBySignCode(String signCode);

    boolean existsBySignCode(String signCode);
}

