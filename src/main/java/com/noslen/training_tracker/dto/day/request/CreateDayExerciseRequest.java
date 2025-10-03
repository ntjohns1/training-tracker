package com.noslen.training_tracker.dto.day.request;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CreateDayExerciseRequest(
        @NotNull Long dayId,
        @NotNull Long exerciseId,
        Integer position,
        Integer jointPain,
        Instant createdAt,
        Instant updatedAt,
        Long sourceDayExerciseId,
        @NotNull Long muscleGroupId
) {
}
