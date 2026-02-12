package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.TrafficSign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrafficSignRepository extends JpaRepository<TrafficSign, Long>,
        JpaSpecificationExecutor<TrafficSign> {

    List<TrafficSign> findAllByIsActiveTrue();

    List<TrafficSign> findAllByCategoryIdAndIsActiveTrue(Long categoryId);

    Optional<TrafficSign> findBySignCode(String signCode);

    boolean existsBySignCode(String signCode);

    /**
     * Check if a sign code exists for a DIFFERENT sign (used during update).
     */
    boolean existsBySignCodeAndIdNot(String signCode, Long id);

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

    /**
     * Paginated admin search — searches across signCode + all 4 language names.
     * Optionally filters by category code.
     */
    @Query("SELECT t FROM TrafficSign t JOIN FETCH t.category c WHERE " +
            "(:categoryCode IS NULL OR c.code = :categoryCode) AND " +
            "(:q IS NULL OR " +
            " LOWER(t.signCode) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(t.nameEn) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(t.nameAr) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(t.nameNl) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(t.nameFr) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<TrafficSign> findAdminSigns(
            @Param("categoryCode") String categoryCode,
            @Param("q") String q,
            Pageable pageable);

    /**
     * Count query for the paginated admin search (avoids N+1 on count).
     */
    @Query("SELECT COUNT(t) FROM TrafficSign t JOIN t.category c WHERE " +
            "(:categoryCode IS NULL OR c.code = :categoryCode) AND " +
            "(:q IS NULL OR " +
            " LOWER(t.signCode) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(t.nameEn) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(t.nameAr) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(t.nameNl) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(t.nameFr) LIKE LOWER(CONCAT('%', :q, '%')))")
    long countAdminSigns(
            @Param("categoryCode") String categoryCode,
            @Param("q") String q);
}
