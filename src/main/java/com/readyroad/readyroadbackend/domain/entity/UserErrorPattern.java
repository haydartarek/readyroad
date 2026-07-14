package com.readyroad.readyroadbackend.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ═══════════════════════════════════════════════════════════════════
 * Error Patterns (User Error Patterns)
 * ═══════════════════════════════════════════════════════════════════
 * 
 * Core of Law Four: General Statistics
 * 
 * This Entity records error patterns, not the errors themselves:
 * 
 * • ErrorType -> General classification (SIGN_CONFUSION,
 * PRIORITY_MISUNDERSTANDING...)
 * • No field for "sign name" or "law number"
 * • The pattern is applicable to any content
 * 
 * Application example:
 * - In signs: SIGN_CONFUSION = confusion between two signs
 * - In medicine: SIGN_CONFUSION = confusion between symptoms
 * - In mathematics: SIGN_CONFUSION = confusion between formulas
 * 
 * Rule: The pattern is general, the content is variable
 * 
 * This is intentional ignorance (Law Five)
 * 
 * @see SYSTEM_LAWS.md - Laws Four and Five
 */
@Entity
@Table(name = "user_error_patterns")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserErrorPattern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "error_type", nullable = false)
    private ErrorType errorType;

    @Column(name = "question_type")
    private String questionType;

    @Column(name = "question_ref_type")
    private String questionRefType;

    @Column(name = "question_ref_id")
    private Long questionRefId;

    @Column(name = "traffic_sign_code", length = 10)
    private String trafficSignCode;

    @Column(name = "rule_category", length = 50)
    private String ruleCategory;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public enum ErrorType {
        SIGN_CONFUSION, // Confusion between similar signs
        SUPPLEMENTARY_IGNORED, // Ignoring a supplementary panel
        PRIORITY_MISUNDERSTANDING, // Misunderstanding of priority
        SPEED_LIMIT_ERROR, // Error in speed limits
        ZONE_CONFUSION, // Confusion between zones
        RULE_OVERGENERALIZATION, // Overgeneralizing a rule in the wrong context
        OTHER
    }

    @PrePersist
    protected void onCreate() {
        if (occurredAt == null) {
            occurredAt = LocalDateTime.now();
        }
    }
}
