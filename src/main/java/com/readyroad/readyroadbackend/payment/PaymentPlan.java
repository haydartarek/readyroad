package com.readyroad.readyroadbackend.payment;

public enum PaymentPlan {
    RIJVIA_3_DAYS(3), RIJVIA_1_WEEK(7), RIJVIA_4_WEEKS(28);

    private final int days;
    PaymentPlan(int days) { this.days = days; }
    public int days() { return days; }
}
