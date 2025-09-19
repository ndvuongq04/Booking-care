package com.Booking_care.util.error;

public class StorageException extends Exception {
    // Constructor that accepts a message
    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
