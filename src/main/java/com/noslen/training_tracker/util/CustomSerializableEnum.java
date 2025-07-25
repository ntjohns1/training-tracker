package com.noslen.training_tracker.util;

/**
 * Interface for enums that have custom string serialization logic.
 * This provides a clean alternative to reflection-based detection of @JsonValue methods.
 */
public interface CustomSerializableEnum {
    
    /**
     * Returns the custom string representation of this enum value.
     * This is typically used for JSON serialization or DTO conversion.
     * 
     * @return the custom string representation
     */
    String getSerializedValue();
}
