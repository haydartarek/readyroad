package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.UserErrorPattern;
import com.readyroad.readyroadbackend.domain.entity.UserErrorPattern.ErrorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UserErrorPatternRepository extends JpaRepository<UserErrorPattern, Long> {

    List<UserErrorPattern> findAllByUserIdOrderByOccurredAtDesc(Long userId);

    @Query("SELECT e.errorType, COUNT(e) FROM UserErrorPattern e " +
           "WHERE e.user.id = :userId " +
           "GROUP BY e.errorType " +
           "ORDER BY COUNT(e) DESC")
    List<Object[]> findMostCommonErrorTypes(@Param("userId") Long userId);

    @Query("SELECT COUNT(e) FROM UserErrorPattern e " +
           "WHERE e.user.id = :userId AND e.errorType = :errorType")
    Long countByUserIdAndErrorType(
        @Param("userId") Long userId,
        @Param("errorType") ErrorType errorType
    );

    @Query("SELECT e FROM UserErrorPattern e " +
           "WHERE e.user.id = :userId AND e.category.id = :categoryId " +
           "ORDER BY e.occurredAt DESC")
    List<UserErrorPattern> findByCategoryForUser(
        @Param("userId") Long userId,
        @Param("categoryId") Long categoryId
    );

    /**
     * Insert a sign-specific error pattern using raw native SQL.
     * question_type='PRACTICE' is a fixed literal (matches the DB ENUM).
     * question_ref_id holds the sign_questions.id.
     * traffic_sign_id and category_id are intentionally left NULL
     * (sign-quiz bypasses the legacy FK columns).
     */
    @Modifying
    @Query(nativeQuery = true, value = """
            INSERT INTO user_error_patterns
                (user_id, error_type, question_type, question_ref_id, occurred_at)
            VALUES (:userId, :errorType, 'PRACTICE', :questionId, NOW())
            """)
    void insertSignError(@Param("userId")     Long   userId,
                         @Param("errorType")  String errorType,
                         @Param("questionId") Long   questionId,
                         @Param("signCode")   String signCode);
}
