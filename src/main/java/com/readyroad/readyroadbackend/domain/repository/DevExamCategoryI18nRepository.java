package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.DevExamCategoryI18n;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DevExamCategoryI18nRepository extends JpaRepository<DevExamCategoryI18n, Long> {

    List<DevExamCategoryI18n> findByCategory_Id(Long categoryId);

    Optional<DevExamCategoryI18n> findByCategory_IdAndLang(Long categoryId, String lang);
}
