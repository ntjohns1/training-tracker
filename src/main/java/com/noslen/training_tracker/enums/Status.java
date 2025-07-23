package com.noslen.training_tracker.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {
    COMPLETED,
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
}
