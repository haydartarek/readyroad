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
@Table(name = "marketing_usp")
@Getter
@Setter
@NoArgsConstructor
public class MarketingUsp extends StrategyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @Column(name = "evidence_type", nullable = false, length = 128)
    private String evidenceType;

    @Column(name = "evidence_reference", nullable = false, length = 255)
    private String evidenceReference;

    @Column(nullable = false)
    private short priority = 1;
}
