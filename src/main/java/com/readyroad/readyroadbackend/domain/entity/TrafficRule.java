package com.readyroad.readyroadbackend.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Traffic Rule Entity
 * قاعدة المرور
 */
@Entity
@Table(name = "traffic_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TrafficRule extends BaseEntity {

    @Column(nullable = false, unique = true, length = 20)
    private String ruleCode;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String titleAr;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String titleEn;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String titleNl;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String titleFr;

    @Column(columnDefinition = "TEXT")
    private String contentAr;

    @Column(columnDefinition = "TEXT")
    private String contentEn;

    @Column(columnDefinition = "TEXT")
    private String contentNl;

    @Column(columnDefinition = "TEXT")
    private String contentFr;

    @Column(length = 50)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private ImportanceLevel importanceLevel = ImportanceLevel.MEDIUM;

    @Column(length = 100)
    private String appliesTo = "ALL"; // ALL, CAR, MOTORCYCLE, BICYCLE, etc.

    @Column(columnDefinition = "TEXT")
    private String penaltyInfoAr;

    @Column(columnDefinition = "TEXT")
    private String penaltyInfoEn;

    @Column(columnDefinition = "TEXT")
    private String penaltyInfoNl;

    @Column(columnDefinition = "TEXT")
    private String penaltyInfoFr;

    @Column(nullable = false)
    private Boolean isActive = true;

    // Enum
    public enum ImportanceLevel {
        HIGH,
        MEDIUM,
        LOW
    }
}
