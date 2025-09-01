package com.Booking_care.domain.enums;

public enum BillStatusEnum {
    UNPAID("Chưa thanh toán"),
    PAID("Đã thanh toán"),
    CANCELLED("Đã hủy");

    private final String label;

    BillStatusEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
