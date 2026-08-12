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
@Table(name = "social_proof_items")
@Getter
@Setter
@NoArgsConstructor
public class SocialProofItem extends StrategyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "proof_type", nullable = false, length = 96)
    private String proofType;

    @Column(nullable = false, columnDefinition = "text")
    private String claim;

    @Column(name = "evidence_reference", nullable = false, length = 255)
    private String evidenceReference;
}
