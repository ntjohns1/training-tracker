package com.noslen.training_tracker.util;

/**
 * Generic utility class for converting between Enum types and String representations.
 * Provides type-safe conversion methods that can be used with any enum type.
 * Centralizes enum conversion logic to avoid duplication across mappers.
 */
public final class EnumConverter {
    
    private EnumConverter() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Converts a string to an enum of the specified type.
     * Handles null values and case-insensitive conversion.
     * 
     * @param <T> the enum type
     * @param enumClass the Class object of the enum type
     * @param value the string representation of the enum value
     * @return the enum value, or null if input is null
     * @throws IllegalArgumentException if the string doesn't match any enum value
     */
    public static <T extends Enum<T>> T stringToEnum(Class<T> enumClass, String value) {
        if (value == null) {
            return null;
        }
        return Enum.valueOf(enumClass, value.toUpperCase());
    }
    
    /**
     * Converts an enum to its string representation.
     * Uses the enum's getSerializedValue() method if it implements CustomSerializableEnum,
     * otherwise uses the enum's name() method.
     * 
     * @param enumValue the enum value
     * @return the string representation, or null if input is null
     */
    public static String enumToString(Enum<?> enumValue) {
        if (enumValue == null) {
            return null;
        }
        
        // Use custom serialization if available
        if (enumValue instanceof CustomSerializableEnum customEnum) {
            return customEnum.getSerializedValue();
        }
        
        // Fallback to enum name
        return enumValue.name();
    }
    
    /**
     * Converts an enum to its custom serialized value representation.
     * For enums that implement CustomSerializableEnum, uses their getSerializedValue() method.
     * Falls back to name() for standard enums.
     * 
     * @param enumValue the enum value
     * @return the serialized string representation, or null if input is null
     */
    public static String enumToSerializedValue(Enum<?> enumValue) {
        if (enumValue == null) {
            return null;
        }
        
        // Check if the enum implements CustomSerializableEnum
        if (enumValue instanceof CustomSerializableEnum customEnum) {
            return customEnum.getSerializedValue();
        }
        
        // Fall back to standard name() for regular enums
        return enumValue.name();
    }
}
