package com.readyroad.readyroadbackend.marketing.strategy.repository;

import com.readyroad.readyroadbackend.marketing.strategy.domain.MarketingContentPillar;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketingContentPillarRepository extends JpaRepository<MarketingContentPillar, Long> {
    Optional<MarketingContentPillar> findByIdAndActiveTrue(Long id);

    List<MarketingContentPillar> findAllByOrderByPriorityDescIdAsc();
}
