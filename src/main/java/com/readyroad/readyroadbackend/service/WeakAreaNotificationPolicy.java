package com.readyroad.readyroadbackend.service;

final class WeakAreaNotificationPolicy {
    // Match the existing learning-profile minimum evidence threshold.
    private static final int MIN_ATTEMPTS = 5;
    private static final int MAX_ACCURACY_PERCENT = 60;

    private WeakAreaNotificationPolicy() {}

    static boolean isWeak(int attempted, int correct) {
        return attempted >= MIN_ATTEMPTS && correct >= 0 && correct <= attempted
                && (long) correct * 100 < (long) attempted * MAX_ACCURACY_PERCENT;
    }
}
