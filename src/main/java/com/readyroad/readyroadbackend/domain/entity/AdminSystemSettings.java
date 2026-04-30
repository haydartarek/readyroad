package com.readyroad.readyroadbackend.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Singleton admin-controlled system settings.
 * A single row is kept in the database and mirrored in the admin settings page.
 */
@Entity
@Table(name = "admin_system_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AdminSystemSettings extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String siteName = "ReadyRoad";

    @Column(nullable = false, length = 5)
    private String defaultLanguage = "en";

    @Column(nullable = false)
    private Boolean maintenanceMode = false;

    @Column(nullable = false)
    private Boolean allowRegistrations = true;

    @Column(nullable = false)
    private Integer examQuestions = 50;

    @Column(nullable = false)
    private Integer examDurationMinutes = 30;

    @Column(nullable = false)
    private Integer passingScorePercent = 82;
}
