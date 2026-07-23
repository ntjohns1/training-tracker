package com.noslen.training_tracker.dto.day.request;

import java.time.Instant;

public record CreateDayMuscleGroupRequest(
        Long dayId,
        Long muscleGroupId,
        Instant createdAt,
        Instant updatedAt,
        Integer recommendedSets
) {
}
