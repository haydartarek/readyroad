package com.readyroad.readyroadbackend.marketing.strategy.repository;

import com.readyroad.readyroadbackend.marketing.strategy.domain.SocialProofItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialProofItemRepository extends JpaRepository<SocialProofItem, Long> {
    List<SocialProofItem> findAllByOrderByIdAsc();
}
