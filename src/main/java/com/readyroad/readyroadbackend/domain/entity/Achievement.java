package com.readyroad.readyroadbackend.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Achievement Entity
 *
 * Represents a one-time badge/milestone earned by a user.
 * The UNIQUE constraint on (user_id, type) ensures each achievement
 * is awarded at most once per user.
 */
@Entity
@Table(
    name = "achievements",
    indexes = {
        @Index(name = "idx_achievement_user_id",     columnList = "user_id"),
        @Index(name = "idx_achievement_achieved_at", columnList = "achieved_at")
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user who earned this achievement */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** The type of achievement (unique per user) */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private AchievementType type;

    /** Short title shown in the notification */
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    /** Detailed description of what was achieved */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** When the achievement was earned */
    @Column(name = "achieved_at", nullable = false)
    @Builder.Default
    private Instant achievedAt = Instant.now();

    /** Optional JSON metadata (e.g. examId, score) */
    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;
}
