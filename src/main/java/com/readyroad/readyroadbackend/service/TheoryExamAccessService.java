package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.payment.EntitlementStatus;
import com.readyroad.readyroadbackend.payment.UserEntitlement;
import com.readyroad.readyroadbackend.payment.UserEntitlementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

/**
 * Central access decision for the paid theory exam.
 *
 * Full access requires BOTH:
 * - entitlement status ACTIVE
 * - expiresAt strictly after the current instant
 *
 * Missing, FREE, EXPIRED, null-expiry, and stale ACTIVE entitlements
 * are treated as free-preview access.
 */
@Service
public class TheoryExamAccessService {

    private final UserEntitlementRepository entitlementRepository;
    private final Clock clock;

    @Autowired
    public TheoryExamAccessService(UserEntitlementRepository entitlementRepository) {
        this(entitlementRepository, Clock.systemUTC());
    }

    TheoryExamAccessService(
            UserEntitlementRepository entitlementRepository,
            Clock clock) {
        this.entitlementRepository = entitlementRepository;
        this.clock = clock;
    }

    public boolean hasFullAccess(Long userId) {
        if (userId == null) {
            return false;
        }

        Instant now = clock.instant();

        return entitlementRepository.findById(userId)
                .filter(entitlement -> entitlement.getStatus() == EntitlementStatus.ACTIVE)
                .map(UserEntitlement::getExpiresAt)
                .filter(expiresAt -> expiresAt.isAfter(now))
                .isPresent();
    }
}