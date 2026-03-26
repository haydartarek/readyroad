package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.enums.SignCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoadSignRepository extends JpaRepository<RoadSign, Long> {

    Optional<RoadSign> findBySignCode(String signCode);

    Optional<RoadSign> findByNormalizedSignCode(String normalizedSignCode);

    Optional<RoadSign> findFirstByNormalizedSignCodeAndIsActiveTrueOrderByIdAsc(String normalizedSignCode);

    Optional<RoadSign> findFirstBySignCodeOrderByIdAsc(String signCode);

    Optional<RoadSign> findFirstBySignCodeAndIsActiveTrueOrderByIdAsc(String signCode);

    boolean existsBySignCode(String signCode);

    boolean existsByNormalizedSignCode(String normalizedSignCode);

    boolean existsBySignCodeAndIdNot(String signCode, Long id);

    List<RoadSign> findAllByIsActiveTrue();

    List<RoadSign> findAllByIsActiveTrueOrderBySignCodeAsc();

    List<RoadSign> findAllByCategoryAndIsActiveTrue(SignCategory category);

    long countByCategoryAndIsActiveTrue(SignCategory category);

    long countByIsActiveTrue();

    @Query("SELECT r FROM RoadSign r WHERE r.isActive = true AND " +
            "(LOWER(r.signCode) LIKE LOWER(CONCAT('%', :st, '%')) OR " +
            "LOWER(r.nameNl) LIKE LOWER(CONCAT('%', :st, '%')) OR " +
            "LOWER(r.nameEn) LIKE LOWER(CONCAT('%', :st, '%')) OR " +
            "LOWER(r.nameAr) LIKE LOWER(CONCAT('%', :st, '%')) OR " +
            "LOWER(r.nameFr) LIKE LOWER(CONCAT('%', :st, '%')) OR " +
            "LOWER(r.descriptionNl) LIKE LOWER(CONCAT('%', :st, '%')) OR " +
            "LOWER(r.descriptionEn) LIKE LOWER(CONCAT('%', :st, '%')))")
    List<RoadSign> searchRoadSigns(@Param("st") String searchTerm);

    @Query("SELECT r FROM RoadSign r WHERE " +
            "(:cat IS NULL OR r.category = :cat) AND " +
            "(:q IS NULL OR " +
            " LOWER(r.signCode) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(r.nameNl) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(r.nameEn) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(r.nameAr) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(r.nameFr) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<RoadSign> findAdminSigns(
            @Param("cat") SignCategory cat,
            @Param("q") String q,
            Pageable pageable);
}
