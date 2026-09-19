package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.readyroad.readyroadbackend.payment.EntitlementStatus;
import com.readyroad.readyroadbackend.payment.UserEntitlement;
import com.readyroad.readyroadbackend.payment.UserEntitlementRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class TheoryExamAccessServiceTest {

    @Mock
    UserEntitlementRepository entitlements;

    @Test
    void freeUserSeesOnlyTenQuestionsAndCannotAccessQuestionEleven() {
        when(entitlements.findById(7L)).thenReturn(Optional.empty());
        TheoryExamAccessService service = new TheoryExamAccessService(entitlements);

        assertThat(service.hasFullAccess(7L)).isFalse();
        assertThat(service.visibleQuestionLimit(7L, 50)).isEqualTo(10);
        assertThatThrownBy(() -> service.requireQuestionAccess(7L, 11))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex ->
                        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.PAYMENT_REQUIRED));
    }

    @Test
    void activeUnexpiredEntitlementUnlocksFullExam() {
        UserEntitlement entitlement = new UserEntitlement();
        entitlement.setUserId(7L);
        entitlement.setStatus(EntitlementStatus.ACTIVE);
        entitlement.setExpiresAt(Instant.now().plus(1, ChronoUnit.DAYS));
        when(entitlements.findById(7L)).thenReturn(Optional.of(entitlement));
        TheoryExamAccessService service = new TheoryExamAccessService(entitlements);

        assertThat(service.hasFullAccess(7L)).isTrue();
        assertThat(service.visibleQuestionLimit(7L, 50)).isEqualTo(50);
        service.requireQuestionAccess(7L, 50);
    }

    @Test
    void expiredEntitlementFallsBackToFreePreview() {
        UserEntitlement entitlement = new UserEntitlement();
        entitlement.setUserId(7L);
        entitlement.setStatus(EntitlementStatus.ACTIVE);
        entitlement.setExpiresAt(Instant.now().minus(1, ChronoUnit.MINUTES));
        when(entitlements.findById(7L)).thenReturn(Optional.of(entitlement));
        TheoryExamAccessService service = new TheoryExamAccessService(entitlements);

        assertThat(service.hasFullAccess(7L)).isFalse();
        assertThat(service.visibleQuestionLimit(7L, 50)).isEqualTo(10);
    }
}
