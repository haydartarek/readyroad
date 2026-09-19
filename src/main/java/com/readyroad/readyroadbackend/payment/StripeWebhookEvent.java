package com.readyroad.readyroadbackend.payment;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stripe_webhook_events")
@Getter @NoArgsConstructor
public class StripeWebhookEvent {
    @Id private String stripeEventId;
    @Column(nullable = false) private String eventType;
    @Column(nullable = false) private Instant processedAt;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16) private Status status;
    public enum Status { PROCESSED, IGNORED }
}
