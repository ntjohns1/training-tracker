package com.noslen.training_tracker.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.noslen.training_tracker.util.CustomSerializableEnum;

public enum MgSubType implements CustomSerializableEnum {
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

        return switch(this) {
            case HEAVY_AXIAL -> "heavy-axial";
            case NON_HEAVY_AXIAL -> "non-heavy-axial";
            case NON_INCLINE -> "non-incline";
            case NON_OVERHEAD -> "non-overhead";
            default -> this.name().toLowerCase();
        };
    }

    @Override
    public String getSerializedValue() {
        return getValue();
    }
}
