package com.readyroad.readyroadbackend.marketing.strategy.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "marketing_conversion_goals")
@Getter
@Setter
@NoArgsConstructor
public class MarketingConversionGoal extends StrategyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "goal_key", nullable = false, unique = true, length = 96)
    private String goalKey;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "primary_cta", nullable = false, length = 200)
    private String primaryCta;

    @Column(name = "funnel_stage_id", nullable = false)
    private Long funnelStageId;
}
