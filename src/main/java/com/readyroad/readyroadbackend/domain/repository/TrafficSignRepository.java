package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.TrafficSign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrafficSignRepository extends JpaRepository<TrafficSign, Long> {

    List<TrafficSign> findAllByIsActiveTrue();

    List<TrafficSign> findAllByCategoryIdAndIsActiveTrue(Long categoryId);

    Optional<TrafficSign> findBySignCode(String signCode);

    boolean existsBySignCode(String signCode);

    @Query("SELECT t FROM TrafficSign t WHERE t.isActive = true AND " +
            "(LOWER(t.signCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(t.nameAr) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(t.nameEn) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(t.nameNl) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(t.nameFr) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(t.descriptionAr) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(t.descriptionEn) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(t.descriptionNl) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(t.descriptionFr) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<TrafficSign> searchTrafficSigns(@Param("searchTerm") String searchTerm);
}

