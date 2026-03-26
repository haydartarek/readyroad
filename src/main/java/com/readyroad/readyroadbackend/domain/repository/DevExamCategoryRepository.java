package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.DevExamCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DevExamCategoryRepository extends JpaRepository<DevExamCategory, Long> {

    List<DevExamCategory> findByIsActiveTrue();

    Optional<DevExamCategory> findBySlug(String slug);

    @Query("SELECT c FROM DevExamCategory c WHERE c.isActive = true AND c.slug = :slug")
    Optional<DevExamCategory> findActiveBySlug(@Param("slug") String slug);
}
