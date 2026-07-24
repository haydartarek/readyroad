package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.SignExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SignExamRepository extends JpaRepository<SignExam, Long> {

    List<SignExam> findAllBySignIdOrderByExamNumberAsc(Long signId);

    List<SignExam> findAllBySignIdAndIsActiveTrueOrderByExamNumberAsc(Long signId);

    Optional<SignExam> findBySignIdAndExamNumber(Long signId, Integer examNumber);

    Optional<SignExam> findBySignIdAndExamNumberAndIsActiveTrue(Long signId, Integer examNumber);

    /**
     * Lightweight exam-one configuration used by the all-sign progress endpoint.
     *
     * Columns: sign_id, total_questions, passing_score.
     */
    @Query("""
            SELECT e.sign.id, e.totalQuestions, e.passingScore
            FROM SignExam e
            WHERE e.examNumber = 1 AND e.isActive = true
            """)
    List<Object[]> findActiveExamOneProgressConfigs();

    void deleteAllBySignId(Long signId);
}
