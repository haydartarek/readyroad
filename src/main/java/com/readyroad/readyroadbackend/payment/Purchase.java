package com.readyroad.readyroadbackend.payment;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "purchases")
@Getter @Setter @NoArgsConstructor
public class Purchase {
    @Id private UUID id;
    @Column(nullable = false) private Long userId;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 24) private PaymentPlan plan;
    @Column(nullable = false, unique = true, length = 64) private String clientRequestId;
    @Column(unique = true) private String checkoutSessionId;
    @Column(unique = true) private String paymentIntentId;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16) private PurchaseStatus status;
    @Column(nullable = false, length = 2) private String checkoutLocale;
    @Column(columnDefinition = "text") private String checkoutUrl;
    private Instant checkoutExpiresAt;
    @Column(nullable = false) private Instant createdAt;
    @Column(nullable = false) private Instant updatedAt;
}
