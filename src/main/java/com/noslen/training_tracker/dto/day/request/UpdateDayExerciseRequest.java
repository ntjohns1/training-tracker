package com.noslen.training_tracker.dto.day.request;

import java.time.Instant;
import java.util.List;

public record UpdateDayExerciseRequest(
        Long id,
        Long dayId,
        Long exerciseId,
        Integer position,
        Integer jointPain,
        Instant updatedAt,
        Long sourceDayExerciseId,
        Long muscleGroupId,
        String status
) {
}
