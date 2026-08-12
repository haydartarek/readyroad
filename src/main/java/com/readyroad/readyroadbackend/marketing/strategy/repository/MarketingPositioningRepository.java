package com.readyroad.readyroadbackend.marketing.strategy.repository;

import com.readyroad.readyroadbackend.marketing.strategy.domain.MarketingPositioning;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketingPositioningRepository extends JpaRepository<MarketingPositioning, Long> {
    Optional<MarketingPositioning> findFirstByActiveTrueOrderByIdAsc();

    List<MarketingPositioning> findAllByOrderByIdAsc();
}
