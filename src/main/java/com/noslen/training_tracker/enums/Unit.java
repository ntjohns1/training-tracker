package com.noslen.training_tracker.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Unit {
    KGS, LBS;

    @JsonValue
    public String toValue() {
        return name().toLowerCase();
    }
}
