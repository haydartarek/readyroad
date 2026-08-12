package com.readyroad.readyroadbackend.marketing.strategy.domain;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "marketing_positioning")
@Getter
@Setter
@NoArgsConstructor
public class MarketingPositioning extends StrategyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "text")
    private String statement;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "brand_identity", nullable = false)
    private JsonNode brandIdentity;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "brand_voice", nullable = false)
    private JsonNode brandVoice;
}
