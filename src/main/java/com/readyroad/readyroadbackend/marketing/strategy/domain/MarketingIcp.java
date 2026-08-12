package com.readyroad.readyroadbackend.marketing.strategy.domain;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "marketing_icp")
@Getter
@Setter
@NoArgsConstructor
public class MarketingIcp extends StrategyRecord {

    @Id
    @Column(length = 64)
    private String id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 8)
    private String language;

    @Column(length = 80)
    private String country;

    @Column(length = 160)
    private String region;

    @Column(name = "primary_goal", columnDefinition = "text")
    private String primaryGoal;

    @Column(name = "main_problem", columnDefinition = "text")
    private String mainProblem;

    @Column(name = "search_intent", length = 160)
    private String searchIntent;

    @Column(name = "preferred_content_type", length = 128)
    private String preferredContentType;

    @Column(name = "preferred_channel", length = 128)
    private String preferredChannel;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "main_objections", nullable = false)
    private JsonNode mainObjections;

    @Column(name = "funnel_stage", length = 64)
    private String funnelStage;

    @Column(name = "conversion_goal", length = 128)
    private String conversionGoal;
}
