package com.readyroad.readyroadbackend.payment;

import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StripeWebhookService {
    public static final Set<String> HANDLED = Set.of("checkout.session.completed",
            "checkout.session.async_payment_succeeded", "checkout.session.async_payment_failed", "checkout.session.expired");
    private final StripeWebhookEventRepository events;
    private final PurchaseRepository purchases;
    private final UserRepository users;
    private final UserEntitlementRepository entitlements;
    private final Clock clock;

    public StripeWebhookService(StripeWebhookEventRepository events, PurchaseRepository purchases,
            UserRepository users, UserEntitlementRepository entitlements, @Qualifier("paymentClock") Clock clock) {
        this.events = events; this.purchases = purchases; this.users = users; this.entitlements = entitlements; this.clock = clock;
    }

    @Transactional
    public void process(CheckoutEvent event) {
        boolean handled = HANDLED.contains(event.type());
        Instant now = clock.instant();
        if (events.claim(event.id(), event.type(), now, handled ? "PROCESSED" : "IGNORED") == 0 || !handled) return;
        Purchase purchase = purchases.lockBySessionId(event.sessionId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Checkout is not available yet; retry webhook"));
        if (!purchase.getId().toString().equals(event.purchaseId())
                || !purchase.getUserId().toString().equals(event.userId())
                || !purchase.getPlan().name().equals(event.plan())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Checkout ownership metadata mismatch");
        }
        // REFUNDED is reserved: V1 never transitions into or out of it.
        if (purchase.getStatus() == PurchaseStatus.REFUNDED) return;
        if (event.paymentIntentId() != null) {
            if (purchase.getPaymentIntentId() != null && !purchase.getPaymentIntentId().equals(event.paymentIntentId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Payment reference mismatch");
            }
            purchase.setPaymentIntentId(event.paymentIntentId());
        }
        if (purchase.getStatus() == PurchaseStatus.PAID) return;
        switch (event.type()) {
            case "checkout.session.completed" -> { if (event.paid()) activate(purchase, now); }
            case "checkout.session.async_payment_succeeded" -> activate(purchase, now);
            case "checkout.session.async_payment_failed", "checkout.session.expired" -> {
                if (purchase.getStatus() == PurchaseStatus.PENDING) purchase.setStatus(PurchaseStatus.FAILED);
            }
            default -> { }
        }
        purchase.setUpdatedAt(now);
        purchases.saveAndFlush(purchase);
    }

    private void activate(Purchase purchase, Instant now) {
        // Always lock the existing user before looking for an entitlement that may not yet exist.
        users.findByIdForUpdate(purchase.getUserId()).orElseThrow();
        UserEntitlement entitlement = entitlements.lockByUserId(purchase.getUserId()).orElseGet(() -> {
            UserEntitlement created = new UserEntitlement();
            created.setUserId(purchase.getUserId());
            return created;
        });
        Instant base = entitlement.getExpiresAt() != null && entitlement.getExpiresAt().isAfter(now)
                ? entitlement.getExpiresAt() : now;
        entitlement.setExpiresAt(base.plus(purchase.getPlan().days(), ChronoUnit.DAYS));
        entitlement.setStatus(EntitlementStatus.ACTIVE);
        entitlements.saveAndFlush(entitlement);
        purchase.setStatus(PurchaseStatus.PAID);
    }

    public record CheckoutEvent(String id, String type, String sessionId, String paymentIntentId, boolean paid,
            String purchaseId, String userId, String plan) {}
}
