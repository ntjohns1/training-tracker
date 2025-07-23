package com.noslen.training_tracker.model.day.types;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SetType {
    REGULAR, MYO_REP, MYO_REP_MATCH;

    @JsonValue
    public String getValue() {
        return this.name().toLowerCase();
    }
}
