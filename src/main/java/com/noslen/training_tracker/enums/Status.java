package com.noslen.training_tracker.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.noslen.training_tracker.util.CustomSerializableEnum;

public enum Status implements CustomSerializableEnum {
    COMPLETE,
    EMPTY,
    PARTIAL,
    PENDING,
    PENDING_FEEDBACK,
    PENDING_WEIGHT,
    PROGRAMMED,
    READY,
    SKIPPED,
    UNPROGRAMMED;

    @JsonValue
    public String toValue() {
        return switch (this) {
            case PENDING_FEEDBACK -> "pendingFeedback";
            case PENDING_WEIGHT -> "pendingWeight";
            default -> name().toLowerCase();
        };
    }

    @Override
    public String getSerializedValue() {
        return toValue();
    }
}
