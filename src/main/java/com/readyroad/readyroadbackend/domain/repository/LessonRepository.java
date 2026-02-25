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

    @Query("SELECT l FROM Lesson l WHERE l.isActive = true AND " +
            "(LOWER(l.titleNl) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(l.titleEn) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(l.titleFr) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(l.titleAr) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(l.descriptionNl) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(l.descriptionEn) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(l.descriptionFr) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            " LOWER(l.descriptionAr) LIKE LOWER(CONCAT('%', :q, '%')))" +
            " ORDER BY l.displayOrder ASC")
    List<Lesson> searchLessons(@Param("q") String query);

    long countByIsActiveTrue();
}
