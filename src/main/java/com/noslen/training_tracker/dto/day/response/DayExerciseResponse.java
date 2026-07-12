package com.noslen.training_tracker.dto.day.response;

import java.time.Instant;
import java.util.List;

public record DayExerciseResponse(
        Long id,
        Long dayId,
        Long exerciseId,
        Integer position,
        Integer jointPain,
        Instant createdAt,
        Instant updatedAt,
        Long sourceDayExerciseId,
        Long muscleGroupId,
        List<ExerciseSetResponse> sets,
        String status) {
}
