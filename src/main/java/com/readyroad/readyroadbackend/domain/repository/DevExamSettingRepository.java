package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.DevExamSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DevExamSettingRepository extends JpaRepository<DevExamSetting, Long> {

    Optional<DevExamSetting> findByCategory_Id(Long categoryId);
}
