package com.noslen.training_tracker.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.noslen.training_tracker.util.CustomSerializableEnum;

public enum Unit implements CustomSerializableEnum {
    KGS, LBS;

    @JsonValue
    public String toValue() {
        return name().toLowerCase();
    }

    @Override
    public String getSerializedValue() {
        return toValue();
    }
}
