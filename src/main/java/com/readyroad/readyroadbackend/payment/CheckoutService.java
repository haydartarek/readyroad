package com.readyroad.readyroadbackend.payment;

import com.stripe.exception.StripeException;
import java.time.Clock;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

@ConditionalOnProperty(name = "rijvia.payments.enabled", havingValue = "true")
@Service
public class CheckoutService {
    private static final Set<String> LOCALES = Set.of("ar", "nl", "fr", "en");
    private final PurchaseRepository purchases;
    private final StripeCheckoutGateway stripe;
    private final Clock clock;
    private final TransactionTemplate transactions;

    public CheckoutService(PurchaseRepository purchases, StripeCheckoutGateway stripe,
            @Qualifier("paymentClock") Clock clock, PlatformTransactionManager transactionManager) {
        this.purchases = purchases; this.stripe = stripe; this.clock = clock;
        this.transactions = new TransactionTemplate(transactionManager);
    }

    public CheckoutResult checkout(Long userId, PaymentPlan plan, String requestId, String locale) {
        return checkout(userId, plan, requestId, locale, null);
    }

    public CheckoutResult checkout(
            Long userId,
            PaymentPlan plan,
            String requestId,
            String locale,
            String customerEmail) {
        // Locale is supplied from the existing frontend language context and validated at this boundary.
        if (!LOCALES.contains(locale)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported checkout locale");
        UUID id = transactions.execute(tx -> {
            purchases.insertIfAbsent(UUID.randomUUID(), userId, plan.name(), requestId, locale, clock.instant());
            Purchase purchase = purchases.findByClientRequestId(requestId).orElseThrow();
            if (!purchase.getUserId().equals(userId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase not found");
            if (purchase.getPlan() != plan) throw new ResponseStatusException(HttpStatus.CONFLICT, "Request already belongs to a different plan");
            return purchase.getId();
        });
        // The purchase is committed BEFORE contacting Stripe. A network failure cannot lose its
        // UUID, original locale or idempotency key. Retries serialize on this persisted row.
        return transactions.execute(tx -> {
            Purchase purchase = purchases.lockById(id).orElseThrow();
            Instant now = clock.instant();
            if (purchase.getStatus() == PurchaseStatus.FAILED || purchase.getStatus() == PurchaseStatus.REFUNDED
                    || (purchase.getCheckoutExpiresAt() != null && !purchase.getCheckoutExpiresAt().isAfter(now))) {
                throw new ResponseStatusException(HttpStatus.GONE, "Checkout link expired; start a new purchase");
            }
            if (purchase.getCheckoutSessionId() == null) {
                // Stripe may discard idempotency keys after 24h. Never create another session
                // for an old ambiguous request after that retention window.
                if (!purchase.getCreatedAt().plusSeconds(23 * 3600).isAfter(now)) {
                    throw new ResponseStatusException(HttpStatus.GONE, "Checkout request expired; start again");
                }
                try {
                    var session = customerEmail == null || customerEmail.isBlank()
                            ? stripe.create(purchase)
                            : stripe.create(purchase, customerEmail);
                    if (session.getId() == null || session.getUrl() == null) {
                        throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Checkout is unavailable");
                    }
                    purchase.setCheckoutSessionId(session.getId());
                    purchase.setCheckoutUrl(session.getUrl());
                    purchase.setCheckoutExpiresAt(session.getExpiresAt() == null ? null : Instant.ofEpochSecond(session.getExpiresAt()));
                    purchase.setUpdatedAt(now);
                    purchases.saveAndFlush(purchase);
                } catch (StripeException ex) {
                    // Do not expose provider responses or credentials to callers/logs.
                    throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Unable to open checkout; retry this request");
                }
            }
            return new CheckoutResult(id, purchase.getCheckoutUrl());
        });
    }

    public CheckoutResult resumeCheckout(Long userId, UUID purchaseId) {
        return transactions.execute(tx -> {
            Purchase purchase = purchases.lockById(purchaseId).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase not found"));

            if (!purchase.getUserId().equals(userId)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase not found");
            }

            if (purchase.getStatus() != PurchaseStatus.PENDING) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Purchase is not pending");
            }

            Instant now = clock.instant();

            if (purchase.getCheckoutSessionId() == null
                    || purchase.getCheckoutUrl() == null
                    || purchase.getCheckoutExpiresAt() == null
                    || !purchase.getCheckoutExpiresAt().isAfter(now)) {
                throw new ResponseStatusException(HttpStatus.GONE, "Checkout link expired; start a new purchase");
            }

            return new CheckoutResult(purchase.getId(), purchase.getCheckoutUrl());
        });
    }

    public record CheckoutResult(UUID purchaseId, String checkoutUrl) {}
}
