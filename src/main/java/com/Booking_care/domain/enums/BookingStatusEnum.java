package com.Booking_care.domain.enums;

public enum BookingStatusEnum {
    PENDING("Chờ xác nhận"),
    CONFIRMED("Đã xác nhận"),
    CANCELLED("Đã hủy"),
    COMPLETED("Hoàn thành");

    private final String label;

    BookingStatusEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
