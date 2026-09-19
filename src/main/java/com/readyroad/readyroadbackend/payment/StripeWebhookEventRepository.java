package com.readyroad.readyroadbackend.payment;

import java.time.Instant;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface StripeWebhookEventRepository extends JpaRepository<StripeWebhookEvent, String> {
    @Modifying
    @Query(value = """
            INSERT INTO stripe_webhook_events (stripe_event_id, event_type, processed_at, status)
            VALUES (:id, :type, :now, :status) ON CONFLICT (stripe_event_id) DO NOTHING
            """, nativeQuery = true)
    int claim(@Param("id") String id, @Param("type") String type,
            @Param("now") Instant now, @Param("status") String status);
}
