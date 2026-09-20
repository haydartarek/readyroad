package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.payment.EntitlementStatus;
import com.readyroad.readyroadbackend.payment.UserEntitlement;
import com.readyroad.readyroadbackend.payment.UserEntitlementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TheoryExamAccessServiceTest {

    private static final Long USER_ID = 42L;
    private static final Instant NOW = Instant.parse("2026-09-20T00:00:00Z");

    @Mock
    private UserEntitlementRepository entitlementRepository;

    private TheoryExamAccessService service;

    @BeforeEach
    void setUp() {
        service = new TheoryExamAccessService(
                entitlementRepository,
                Clock.fixed(NOW, ZoneOffset.UTC));
    }

    @Test
    void missingEntitlementDoesNotGrantFullAccess() {
        when(entitlementRepository.findById(USER_ID))
                .thenReturn(Optional.empty());

        assertFalse(service.hasFullAccess(USER_ID));
    }

    @Test
    void freeEntitlementDoesNotGrantFullAccess() {
        when(entitlementRepository.findById(USER_ID))
                .thenReturn(Optional.of(entitlement(
                        EntitlementStatus.FREE,
                        NOW.plusSeconds(3600))));

        assertFalse(service.hasFullAccess(USER_ID));
    }

    @Test
    void expiredStatusDoesNotGrantFullAccessEvenWithFutureExpiry() {
        when(entitlementRepository.findById(USER_ID))
                .thenReturn(Optional.of(entitlement(
                        EntitlementStatus.EXPIRED,
                        NOW.plusSeconds(3600))));

        assertFalse(service.hasFullAccess(USER_ID));
    }

    @Test
    void activeEntitlementWithoutExpiryDoesNotGrantFullAccess() {
        when(entitlementRepository.findById(USER_ID))
                .thenReturn(Optional.of(entitlement(
                        EntitlementStatus.ACTIVE,
                        null)));

        assertFalse(service.hasFullAccess(USER_ID));
    }

    @Test
    void activeEntitlementExpiringNowDoesNotGrantFullAccess() {
        when(entitlementRepository.findById(USER_ID))
                .thenReturn(Optional.of(entitlement(
                        EntitlementStatus.ACTIVE,
                        NOW)));

        assertFalse(service.hasFullAccess(USER_ID));
    }

    @Test
    void staleActiveEntitlementDoesNotGrantFullAccess() {
        when(entitlementRepository.findById(USER_ID))
                .thenReturn(Optional.of(entitlement(
                        EntitlementStatus.ACTIVE,
                        NOW.minusSeconds(1))));

        assertFalse(service.hasFullAccess(USER_ID));
    }

    @Test
    void activeFutureEntitlementGrantsFullAccess() {
        when(entitlementRepository.findById(USER_ID))
                .thenReturn(Optional.of(entitlement(
                        EntitlementStatus.ACTIVE,
                        NOW.plusSeconds(1))));

        assertTrue(service.hasFullAccess(USER_ID));
    }

    @Test
    void nullUserNeverHasFullAccess() {
        assertFalse(service.hasFullAccess(null));
    }

    private static UserEntitlement entitlement(
            EntitlementStatus status,
            Instant expiresAt) {
        UserEntitlement entitlement = new UserEntitlement();
        entitlement.setUserId(USER_ID);
        entitlement.setStatus(status);
        entitlement.setExpiresAt(expiresAt);
        return entitlement;
    }
}