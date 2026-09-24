package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.LessonVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonVersionRepository
        extends JpaRepository<LessonVersion, Long> {

    List<LessonVersion> findAllByLesson_IdOrderByVersionNumberDesc(Long lessonId);

    Optional<LessonVersion> findTopByLesson_IdOrderByVersionNumberDesc(Long lessonId);

    Optional<LessonVersion> findByLesson_IdAndVersionNumber(
            Long lessonId,
            int versionNumber);

    long countByLesson_Id(Long lessonId);
}