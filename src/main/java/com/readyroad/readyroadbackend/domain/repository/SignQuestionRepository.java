package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.SignQuestion;
import com.readyroad.readyroadbackend.domain.enums.SignDifficulty;
import com.readyroad.readyroadbackend.domain.enums.SignQuestionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SignQuestionRepository extends JpaRepository<SignQuestion, Long> {

    Optional<SignQuestion> findByQuestionRef(String questionRef);

    boolean existsByQuestionRef(String questionRef);

    List<SignQuestion> findAllBySignId(Long signId);

    List<SignQuestion> findAllBySignIdAndIsActiveTrue(Long signId);

    List<SignQuestion> findAllBySignIdAndDifficultyAndIsActiveTrue(
            Long signId, SignDifficulty difficulty);

    List<SignQuestion> findAllBySignIdAndQuestionTypeAndIsActiveTrue(
            Long signId, SignQuestionType questionType);

    List<SignQuestion> findAllByIsActiveTrueAndDifficulty(SignDifficulty difficulty);

    @Query("SELECT q FROM SignQuestion q WHERE q.isActive = true AND q.difficulty = :difficulty AND q.sign.isActive = true")
    List<SignQuestion> findAllActiveForActiveSignsByDifficulty(@Param("difficulty") SignDifficulty difficulty);

    long countBySignId(Long signId);
}
