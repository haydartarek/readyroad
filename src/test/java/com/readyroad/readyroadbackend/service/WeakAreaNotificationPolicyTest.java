package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class WeakAreaNotificationPolicyTest {
    @Test
    void requiresFiveCumulativeAnswersAndAccuracyStrictlyBelowSixtyPercent() {
        assertThat(WeakAreaNotificationPolicy.isWeak(0, 0)).isFalse();
        assertThat(WeakAreaNotificationPolicy.isWeak(4, 0)).isFalse();
        assertThat(WeakAreaNotificationPolicy.isWeak(5, 2)).isTrue();
        assertThat(WeakAreaNotificationPolicy.isWeak(5, 3)).isFalse();
        assertThat(WeakAreaNotificationPolicy.isWeak(20, 11)).isTrue();
        assertThat(WeakAreaNotificationPolicy.isWeak(20, 12)).isFalse();
        assertThat(WeakAreaNotificationPolicy.isWeak(5, -1)).isFalse();
        assertThat(WeakAreaNotificationPolicy.isWeak(5, 6)).isFalse();
    }
}
