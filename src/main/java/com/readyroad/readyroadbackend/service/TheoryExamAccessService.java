package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.payment.EntitlementStatus;
import com.readyroad.readyroadbackend.payment.UserEntitlement;
import com.readyroad.readyroadbackend.payment.UserEntitlementRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Resolves access to the full theory exam.
 *
 * Free authenticated users can use the first ten questions as a preview.
 * Active paid entitlements unlock the complete exam.
 */
@Service
@RequiredArgsConstructor
public class TheoryExamAccessService {

    public static final int FREE_PREVIEW_QUESTION_LIMIT = 10;

    private final UserEntitlementRepository entitlements;

    public boolean hasFullAccess(Long userId) {
        Instant now = Instant.now();
        return entitlements.findById(userId)
                .filter(entitlement -> entitlement.getStatus() == EntitlementStatus.ACTIVE)
                .map(UserEntitlement::getExpiresAt)
                .filter(expiresAt -> expiresAt.isAfter(now))
                .isPresent();
    }

    public int visibleQuestionLimit(Long userId, int fullQuestionCount) {
        return hasFullAccess(userId)
                ? fullQuestionCount
                : Math.min(FREE_PREVIEW_QUESTION_LIMIT, fullQuestionCount);
    }

    public void requireQuestionAccess(Long userId, int questionOrder) {
        if (questionOrder <= FREE_PREVIEW_QUESTION_LIMIT || hasFullAccess(userId)) {
            return;
        }
        throw new ResponseStatusException(
                HttpStatus.PAYMENT_REQUIRED,
                "Full exam access required");
    }
}
