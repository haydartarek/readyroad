package com.readyroad.readyroadbackend.payment;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_entitlement")
@Getter @Setter @NoArgsConstructor
public class UserEntitlement {
    @Id private Long userId;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16)
    private EntitlementStatus status = EntitlementStatus.FREE;
    private Instant expiresAt;
}
