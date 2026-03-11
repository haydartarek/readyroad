package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.UserLessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserLessonProgressRepository extends JpaRepository<UserLessonProgress, Long> {

    Optional<UserLessonProgress> findByUserIdAndLessonId(Long userId, Long lessonId);

    List<UserLessonProgress> findAllByUserId(Long userId);
}
