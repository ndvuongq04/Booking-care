package com.Booking_care.util.error;

public class ResourceNotFoundException extends RuntimeException {
    // Constructor that accepts a message
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

