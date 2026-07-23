package com.noslen.training_tracker.dto.day.request;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CreateExerciseSetRequest(
        @NotNull Long dayExerciseId,
        Integer position,
        String setType,
        Float weightTarget,
        Float weightTargetMin,
        Float weightTargetMax,
        Integer repsTarget,
        Instant createdAt
) {
}
