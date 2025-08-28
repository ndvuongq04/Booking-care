package com.Booking_care.domain.enums;

public enum BookingStatus {
    PENDING("Chờ xác nhận"),
    CONFIRMED("Đã xác nhận"),
    CANCELLED("Đã hủy"),
    COMPLETED("Hoàn thành");

    private final String label;

    BookingStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
