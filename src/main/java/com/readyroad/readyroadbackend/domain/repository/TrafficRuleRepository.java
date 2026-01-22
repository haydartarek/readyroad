package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.TrafficRule;
import com.readyroad.readyroadbackend.domain.entity.TrafficRule.ImportanceLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrafficRuleRepository extends JpaRepository<TrafficRule, Long> {

    Optional<TrafficRule> findByRuleCode(String ruleCode);

    List<TrafficRule> findAllByIsActiveTrue();

    List<TrafficRule> findAllByCategoryAndIsActiveTrue(String category);

    List<TrafficRule> findAllByImportanceLevelAndIsActiveTrue(ImportanceLevel importanceLevel);

    List<TrafficRule> findAllByAppliesToAndIsActiveTrue(String appliesTo);

    @Query("SELECT DISTINCT r.category FROM TrafficRule r WHERE r.isActive = true ORDER BY r.category")
    List<String> findAllCategories();

    @Query("SELECT r FROM TrafficRule r WHERE r.isActive = true AND " +
           "(LOWER(r.titleEn) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.titleNl) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.contentEn) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<TrafficRule> searchRules(@Param("keyword") String keyword);

    long countByIsActiveTrue();

    long countByCategoryAndIsActiveTrue(String category);
}
