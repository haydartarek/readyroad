package com.readyroad.readyroadbackend.marketing.strategy.repository;

import com.readyroad.readyroadbackend.marketing.strategy.domain.MarketingIcp;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketingIcpRepository extends JpaRepository<MarketingIcp, String> {
    Optional<MarketingIcp> findByIdAndActiveTrue(String id);

    List<MarketingIcp> findAllByOrderByIdAsc();
}
