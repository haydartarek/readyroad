package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findAllByIsActiveTrueOrderByDisplayOrderAsc();

    Optional<Lesson> findByLessonCode(String lessonCode);

    boolean existsByLessonCode(String lessonCode);

    @Query("SELECT DISTINCT l FROM Lesson l LEFT JOIN l.pages p WHERE l.isActive = true AND " +
            "(LOWER(l.titleNl) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(l.titleEn) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(l.titleFr) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(l.titleAr) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(l.descriptionNl) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(l.descriptionEn) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(l.descriptionFr) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(l.descriptionAr) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(p.titleNl) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(p.titleEn) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(p.titleFr) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(p.titleAr) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(p.contentNl) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(p.contentEn) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(p.contentFr) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(p.contentAr) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(COALESCE(p.bulletPointsNl, '')) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(COALESCE(p.bulletPointsEn, '')) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(COALESCE(p.bulletPointsFr, '')) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(COALESCE(p.bulletPointsAr, '')) LIKE LOWER(CONCAT('%', :q, '%')))" +
            " ORDER BY l.displayOrder ASC")
    List<Lesson> searchLessons(@Param("q") String query);

    long countByIsActiveTrue();
}
