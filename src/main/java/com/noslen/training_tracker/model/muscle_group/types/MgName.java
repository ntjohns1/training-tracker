package com.noslen.training_tracker.model.muscle_group.types;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MgName {
    CHEST, BACK, TRICEPS, BICEPS, SHOULDERS, QUADS, GLUTES, HAMSTRINGS, CALVES, TRAPS, FOREARMS, ABS;

    @JsonValue
    public String getValue() {
        // Convert enum name to proper capitalized format
        String name = this.name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
