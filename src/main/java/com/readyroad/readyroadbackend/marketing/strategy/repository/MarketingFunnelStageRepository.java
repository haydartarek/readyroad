package com.readyroad.readyroadbackend.marketing.strategy.repository;

import com.readyroad.readyroadbackend.marketing.strategy.domain.MarketingFunnelStage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketingFunnelStageRepository extends JpaRepository<MarketingFunnelStage, Long> {
    Optional<MarketingFunnelStage> findByIdAndActiveTrue(Long id);

    List<MarketingFunnelStage> findAllByOrderBySequenceNumberAsc();
}
