package com.readyroad.readyroadbackend.payment;

import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface PurchaseRepository extends JpaRepository<Purchase, UUID> {
    Optional<Purchase> findByClientRequestId(String clientRequestId);
    Optional<Purchase> findByIdAndUserId(UUID id, Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Purchase p where p.id = :id")
    Optional<Purchase> lockById(@Param("id") UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Purchase p where p.checkoutSessionId = :sessionId")
    Optional<Purchase> lockBySessionId(@Param("sessionId") String sessionId);

    @Modifying
    @Query(value = """
            INSERT INTO purchases (id, user_id, plan, client_request_id, status, checkout_locale, created_at, updated_at)
            VALUES (:id, :userId, :plan, :requestId, 'PENDING', :locale, :now, :now)
            ON CONFLICT (client_request_id) DO NOTHING
            """, nativeQuery = true)
    int insertIfAbsent(@Param("id") UUID id, @Param("userId") Long userId,
            @Param("plan") String plan, @Param("requestId") String requestId,
            @Param("locale") String locale, @Param("now") Instant now);
}
