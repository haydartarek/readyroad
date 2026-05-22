package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.Category;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Override
    @Cacheable("categories")
    List<Category> findAll();

    Optional<Category> findByCode(String code);

    List<Category> findAllByIsActiveTrueOrderByDisplayOrderAsc();

    boolean existsByCode(String code);
}
