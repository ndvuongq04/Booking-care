package com.Booking_care.util.error;

public class BusinessException extends RuntimeException {
    // Constructor that accepts a message
    public BusinessException(String message) {
        super(message);
    }
}
