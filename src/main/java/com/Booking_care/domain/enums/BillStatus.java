package com.Booking_care.domain.enums;

public enum BillStatus {
    UNPAID("Chưa thanh toán"),
    PAID("Đã thanh toán"),
    CANCELLED("Đã hủy");

    private final String label;

    BillStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
