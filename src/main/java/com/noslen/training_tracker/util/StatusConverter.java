package com.noslen.training_tracker.util;

import com.noslen.training_tracker.enums.Status;

/**
 * Utility class for converting between Status enum and String representations.
 * Centralizes conversion logic to avoid duplication across mappers.
 * Now uses the generic EnumConverter for consistency.
 */
public final class StatusConverter {
    
    private StatusConverter() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Converts a string to Status enum.
     * Handles null values and case-insensitive conversion.
     * 
     * @param status the string representation of the status
     * @return the Status enum value, or null if input is null
     * @throws IllegalArgumentException if the string doesn't match any Status enum value
     */
    public static Status stringToStatus(String status) {
        return EnumConverter.stringToEnum(Status.class, status);
    }
    
    /**
     * Converts a Status enum to its string representation.
     * Uses the enum's name() method for consistency.
     * 
     * @param status the Status enum value
     * @return the string representation, or null if input is null
     */
    public static String statusToString(Status status) {
        return EnumConverter.enumToString(status);
    }
}
