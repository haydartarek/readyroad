package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.DevExamChoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DevExamChoiceRepository extends JpaRepository<DevExamChoice, Long> {

    List<DevExamChoice> findByQuestion_IdOrderBySortOrder(Long questionId);

    Optional<DevExamChoice> findByIdAndQuestion_Id(Long id, Long questionId);
}
