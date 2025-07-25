package com.noslen.training_tracker.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.noslen.training_tracker.util.CustomSerializableEnum;

public enum mgSubType implements CustomSerializableEnum {
    COMPOUND,
    CURL,
    HEAVY_AXIAL,
    HINGE,
    HORIZONTAL,
    INCLINE,
    NON_HEAVY_AXIAL,
    NON_INCLINE,
    NON_OVERHEAD,
    OVERHEAD,
    RAISE,
    VERTICAL;

    @JsonValue
    public String getValue() {
        return this.name().toLowerCase();
    }

    @Override
    public String getSerializedValue() {
        return getValue();
    }
}
