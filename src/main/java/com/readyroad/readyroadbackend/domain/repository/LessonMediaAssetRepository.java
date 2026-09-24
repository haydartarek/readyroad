package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.LessonMediaAsset;
import com.readyroad.readyroadbackend.domain.enums.LessonMediaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonMediaAssetRepository
        extends JpaRepository<LessonMediaAsset, Long> {

    List<LessonMediaAsset> findAllByLesson_IdOrderByIdDesc(Long lessonId);

    List<LessonMediaAsset> findAllByLesson_IdAndStatusOrderByIdDesc(
            Long lessonId,
            LessonMediaStatus status);

    Optional<LessonMediaAsset> findByStorageKey(String storageKey);

    boolean existsByStorageKey(String storageKey);

    long countByLesson_Id(Long lessonId);

    long countByLesson_IdAndStatus(
            Long lessonId,
            LessonMediaStatus status);
}