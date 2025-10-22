package com.Booking_care.util.error;

public class IdInvalidException extends RuntimeException {
    // Constructor that accepts a message
    public IdInvalidException(String message) {
        super(message);
    }
}
