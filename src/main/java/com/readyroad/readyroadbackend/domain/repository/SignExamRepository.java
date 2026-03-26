package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.SignExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SignExamRepository extends JpaRepository<SignExam, Long> {

    List<SignExam> findAllBySignIdOrderByExamNumberAsc(Long signId);

    List<SignExam> findAllBySignIdAndIsActiveTrueOrderByExamNumberAsc(Long signId);

    Optional<SignExam> findBySignIdAndExamNumber(Long signId, Integer examNumber);

    Optional<SignExam> findBySignIdAndExamNumberAndIsActiveTrue(Long signId, Integer examNumber);

    void deleteAllBySignId(Long signId);
}
