package com.noslen.training_tracker.model.day.types;

import com.fasterxml.jackson.annotation.JsonValue;
import com.noslen.training_tracker.util.CustomSerializableEnum;

public enum SetType implements CustomSerializableEnum {
    REGULAR, MYOREP, MYOREP_MATCH;

    @JsonValue
    public String getValue() {
        return switch (this) {  
            case MYOREP_MATCH -> "myorep-match";
            default -> this.name().toLowerCase();
        };
    }

    @Override
    public String getSerializedValue() {
        return getValue();
    }
}
