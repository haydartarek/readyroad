package com.readyroad.readyroadbackend.payment;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.readyroad.readyroadbackend.domain.entity.User;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class PaymentControllerTest {
    final CheckoutService checkout = mock(CheckoutService.class);
    final PurchaseRepository purchases = mock(PurchaseRepository.class);
    final UserEntitlementRepository entitlements = mock(UserEntitlementRepository.class);
    final PaymentController controller = new PaymentController(checkout, purchases, entitlements);

    @Test void scopesStatusToAuthenticatedOwner() {
        User user = new User(); user.setId(42L);
        UUID id = UUID.randomUUID();
        when(purchases.findByIdAndUserId(id, 42L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> controller.status(user, id)).isInstanceOfSatisfying(ResponseStatusException.class,
                ex -> assertThat(ex.getStatusCode().value()).isEqualTo(404));
        verifyNoInteractions(entitlements);
    }

    @Test void unauthenticatedRequestsNeverAccessPurchases() {
        assertThatThrownBy(() -> controller.status(null, UUID.randomUUID())).isInstanceOf(ResponseStatusException.class);
        assertThatThrownBy(() -> controller.checkout(null, new PaymentController.CheckoutRequest(PaymentPlan.RIJVIA_3_DAYS, "req"), "en"))
                .isInstanceOf(ResponseStatusException.class);
        verifyNoInteractions(checkout, purchases, entitlements);
    }

    @Test void paidExpiryIsPresentedInBrusselsAndPendingExpiryIsNull() {
        User user = new User(); user.setId(42L);
        Purchase purchase = new Purchase(); purchase.setId(UUID.randomUUID()); purchase.setStatus(PurchaseStatus.PAID);
        purchase.setPlan(PaymentPlan.RIJVIA_3_DAYS);
        UserEntitlement entitlement = new UserEntitlement(); entitlement.setExpiresAt(Instant.parse("2026-03-31T12:00:00Z"));
        when(purchases.findByIdAndUserId(purchase.getId(), 42L)).thenReturn(Optional.of(purchase));
        when(entitlements.findById(42L)).thenReturn(Optional.of(entitlement));
        assertThat(controller.status(user, purchase.getId()).expiresAt().toString()).isEqualTo("2026-03-31T14:00+02:00");
        purchase.setStatus(PurchaseStatus.PENDING);
        assertThat(controller.status(user, purchase.getId()).expiresAt()).isNull();
        purchase.setStatus(PurchaseStatus.FAILED);
        assertThat(controller.status(user, purchase.getId()).expiresAt()).isNull();
    }
}
