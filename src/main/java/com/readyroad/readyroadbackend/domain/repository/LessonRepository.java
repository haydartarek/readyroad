package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByCategoryIdAndIsActiveTrue(Long categoryId);
    List<Lesson> findByIsActiveTrueOrderByDisplayOrderAsc();
    List<Lesson> findByCategoryIdOrderByDisplayOrderAsc(Long categoryId);

    @Query("SELECT l FROM Lesson l WHERE l.isActive = true AND " +
            "(LOWER(l.titleAr) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(l.titleEn) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(l.titleNl) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(l.titleFr) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Lesson> searchLessons(@Param("searchTerm") String searchTerm);
}


