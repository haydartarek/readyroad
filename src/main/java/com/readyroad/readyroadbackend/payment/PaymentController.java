package com.readyroad.readyroadbackend.payment;

import com.readyroad.readyroadbackend.domain.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class PaymentController {
    private final CheckoutService checkout;
    private final PurchaseRepository purchases;
    private final UserEntitlementRepository entitlements;
    public PaymentController(CheckoutService checkout, PurchaseRepository purchases, UserEntitlementRepository entitlements) {
        this.checkout = checkout; this.purchases = purchases; this.entitlements = entitlements;
    }

    @PostMapping("/checkout")
    public CheckoutService.CheckoutResult checkout(@AuthenticationPrincipal User user, @Valid @RequestBody CheckoutRequest body,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String locale) {
        Long authenticatedUserId = userId(user);
        String customerEmail = user.getEmail();

        if (customerEmail != null && !customerEmail.isBlank()) {
            return checkout.checkout(
                    authenticatedUserId,
                    body.plan(),
                    body.clientRequestId(),
                    locale,
                    customerEmail);
        }

        return checkout.checkout(
                authenticatedUserId,
                body.plan(),
                body.clientRequestId(),
                locale);
    }

    @PostMapping("/purchases/{id}/resume")
    public CheckoutService.CheckoutResult resumeCheckout(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        return checkout.resumeCheckout(userId(user), id);
    }

    @GetMapping("/purchases/{id}/status")
    public PurchaseResult status(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        Purchase purchase = purchases.findByIdAndUserId(id, userId(user)).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase not found"));
        OffsetDateTime expiry = purchase.getStatus() == PurchaseStatus.PAID
                ? entitlements.findById(user.getId()).map(UserEntitlement::getExpiresAt)
                        .map(value -> value.atZone(ZoneId.of("Europe/Brussels")).toOffsetDateTime()).orElse(null)
                : null;
        return new PurchaseResult(id, purchase.getStatus(), purchase.getPlan(), expiry);
    }

    private static Long userId(User user) {
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        return user.getId();
    }
    public record CheckoutRequest(@NotNull PaymentPlan plan, @NotBlank @Size(max = 64) String clientRequestId) {}
    public record PurchaseResult(UUID purchaseId, PurchaseStatus status, PaymentPlan plan, OffsetDateTime expiresAt) {}
}
