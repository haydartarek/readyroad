package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.payment.EntitlementStatus;
import com.readyroad.readyroadbackend.payment.UserEntitlement;
import com.readyroad.readyroadbackend.payment.UserEntitlementRepository;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

/**
 * Central access decision for the paid theory exam.
 *
 * Regular users require BOTH:
 * - entitlement status ACTIVE
 * - expiresAt strictly after the current instant
 *
 * ADMIN and MODERATOR users always have full access.
 *
 * Missing, FREE, EXPIRED, null-expiry, and stale ACTIVE entitlements
 * are treated as free-preview access.
 */
@Service
public class TheoryExamAccessService {

    private final UserEntitlementRepository entitlementRepository;
    private final UserRepository userRepository;
    private final Clock clock;

    @Autowired
    public TheoryExamAccessService(
            UserEntitlementRepository entitlementRepository,
            UserRepository userRepository) {
        this(entitlementRepository, userRepository, Clock.systemUTC());
    }

    TheoryExamAccessService(
            UserEntitlementRepository entitlementRepository,
            Clock clock) {
        this(entitlementRepository, null, clock);
    }

    TheoryExamAccessService(
            UserEntitlementRepository entitlementRepository,
            UserRepository userRepository,
            Clock clock) {
        this.entitlementRepository = entitlementRepository;
        this.userRepository = userRepository;
        this.clock = clock;
    }

    public boolean hasFullAccess(Long userId) {
        if (userId == null) {
            return false;
        }

        if (userRepository != null
                && userRepository.findById(userId)
                        .map(user -> user.getRole() == Role.ADMIN
                                || user.getRole() == Role.MODERATOR)
                        .orElse(false)) {
            return true;
        }

        Instant now = clock.instant();

        return entitlementRepository.findById(userId)
                .filter(entitlement -> entitlement.getStatus() == EntitlementStatus.ACTIVE)
                .map(UserEntitlement::getExpiresAt)
                .filter(expiresAt -> expiresAt.isAfter(now))
                .isPresent();
    }
}
