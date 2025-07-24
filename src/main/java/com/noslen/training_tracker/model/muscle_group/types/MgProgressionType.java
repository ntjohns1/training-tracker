package com.noslen.training_tracker.model.muscle_group.types;

import com.fasterxml.jackson.annotation.JsonValue;
import com.noslen.training_tracker.util.CustomSerializableEnum;

public enum MgProgressionType implements CustomSerializableEnum {
    REGULAR, SECONDARY;

    @JsonValue
    public String getValue() {
        return this.name().toLowerCase();
    }

    @Override
    public String getSerializedValue() {
        return getValue();
    }
}
