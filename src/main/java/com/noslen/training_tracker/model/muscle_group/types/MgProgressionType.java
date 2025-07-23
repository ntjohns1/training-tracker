package com.noslen.training_tracker.model.muscle_group.types;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MgProgressionType {
    REGULAR, SECONDARY;

    @JsonValue
    public String getValue() {
        return this.name().toLowerCase();
    }
}
