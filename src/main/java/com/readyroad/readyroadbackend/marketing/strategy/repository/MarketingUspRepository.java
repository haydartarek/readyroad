package com.readyroad.readyroadbackend.marketing.strategy.repository;

import com.readyroad.readyroadbackend.marketing.strategy.domain.MarketingUsp;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketingUspRepository extends JpaRepository<MarketingUsp, Long> {
    Optional<MarketingUsp> findByIdAndActiveTrue(Long id);

    List<MarketingUsp> findAllByOrderByPriorityDescIdAsc();
}
