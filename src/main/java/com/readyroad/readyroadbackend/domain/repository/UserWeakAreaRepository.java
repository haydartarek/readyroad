package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.UserWeakArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserWeakAreaRepository extends JpaRepository<UserWeakArea, Long> {

    Optional<UserWeakArea> findByUserIdAndCategoryId(Long userId, Long categoryId);

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
        @Param("threshold") Double threshold
    );

    List<UserWeakArea> findAllByUserId(Long userId);

    /**
     * Upsert a sign-specific weak-area record using the raw traffic_sign_code column.
     * We bypass the JPA entity because UserWeakArea maps category_id/traffic_sign_id FKs,
     * but the actual DB column (from V11) is traffic_sign_code VARCHAR.
     */
    @Modifying
    @Query(nativeQuery = true, value = """
            INSERT INTO user_weak_areas
                (user_id, traffic_sign_code, total_questions, correct_answers, wrong_answers,
                 accuracy_percentage, last_updated)
            VALUES (:userId, :signCode, :totalQ, :correct, :wrong,
                CASE WHEN :totalQ > 0 THEN (:correct * 100.0 / :totalQ) ELSE 0.0 END,
                NOW())
            ON DUPLICATE KEY UPDATE
                total_questions     = total_questions     + :totalQ,
                correct_answers     = correct_answers     + :correct,
                wrong_answers       = wrong_answers       + :wrong,
                accuracy_percentage = (correct_answers + :correct) * 100.0
                                      / (total_questions + :totalQ),
                last_updated        = NOW()
            """)
    void upsertBySignCode(@Param("userId")  Long   userId,
                          @Param("signCode") String signCode,
                          @Param("totalQ")   int    totalQ,
                          @Param("correct")  int    correct,
                          @Param("wrong")    int    wrong);
}
