package com.readyroad.readyroadbackend.domain.entity;

import com.readyroad.readyroadbackend.domain.enums.AuthProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "auth_identities",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"provider", "provider_user_id"}),
                @UniqueConstraint(columnNames = {"user_id", "provider"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AuthIdentity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider provider;

    @Column(name = "provider_user_id", nullable = false, length = 191)
    private String providerUserId;

    @Column(name = "provider_email", nullable = false, length = 100)
    private String providerEmail;

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;
}
