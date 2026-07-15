package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.UserWeakArea;
import com.readyroad.readyroadbackend.domain.repository.custom.UserWeakAreaUpsertRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserWeakAreaRepository extends JpaRepository<UserWeakArea, Long>, UserWeakAreaUpsertRepository {

    Optional<UserWeakArea> findByUserIdAndCategory(Long userId, String category);

    Optional<UserWeakArea> findByUserIdAndTrafficSignCode(Long userId, String trafficSignCode);

    @Query("SELECT w FROM UserWeakArea w " +
            "WHERE w.user.id = :userId " +
            "ORDER BY w.accuracyPercentage ASC, w.totalQuestions DESC")
    List<UserWeakArea> findWeakestAreasForUser(@Param("userId") Long userId);

    @Query("SELECT w FROM UserWeakArea w " +
            "WHERE w.user.id = :userId " +
            "AND w.accuracyPercentage < :threshold " +
            "ORDER BY w.accuracyPercentage ASC")
    List<UserWeakArea> findAreasUnderThreshold(
            @Param("userId") Long userId,
            @Param("threshold") Double threshold);

    List<UserWeakArea> findAllByUserId(Long userId);

}
