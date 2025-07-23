package com.noslen.training_tracker.model.exercise.types;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ExerciseType {
    BARBELL, BODYWEIGHT, BODYWEIGHT_LOADABLE, CABLE, DUMBBELL, FREEMOTION, MACHINE, MACHINE_ASSISTANCE, SMITH_MACHINE;

    @JsonValue
    public String getValue() {
        return this.name().toLowerCase();
    }
}
