package com.readyroad.readyroadbackend.marketing.strategy.repository;

import com.readyroad.readyroadbackend.marketing.strategy.domain.MarketingConversionGoal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketingConversionGoalRepository extends JpaRepository<MarketingConversionGoal, Long> {
    Optional<MarketingConversionGoal> findByIdAndActiveTrue(Long id);

    List<MarketingConversionGoal> findAllByOrderByIdAsc();
}
