package com.noslen.training_tracker.model.exercise.types;

import com.fasterxml.jackson.annotation.JsonValue;
import com.noslen.training_tracker.util.CustomSerializableEnum;

public enum ExerciseType implements CustomSerializableEnum {
    BARBELL, BODYWEIGHT_ONLY, BODYWEIGHT_LOADABLE, CABLE, DUMBBELL, FREEMOTION, MACHINE, MACHINE_ASSISTANCE, SMITH_MACHINE;

    @JsonValue
    public String getValue() {
        return switch (this) {
            case BODYWEIGHT_ONLY -> "bodyweight-only";
            case BODYWEIGHT_LOADABLE -> "bodyweight-loadable";
            case MACHINE_ASSISTANCE -> "machine-assistance";
            case SMITH_MACHINE -> "smith-machine";
            default -> this.name().toLowerCase();
        };
    }

    @Override
    public String getSerializedValue() {
        return getValue();
    }
}
