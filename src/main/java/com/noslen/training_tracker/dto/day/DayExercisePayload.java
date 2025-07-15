package com.noslen.training_tracker.dto.day;

import java.time.Instant;
import java.util.List;

public record DayExercisePayload(Long id, Long dayId, Long exerciseId, Integer position, Integer jointPain,
        Instant createdAt, Instant updatedAt, Long sourceDayExerciseId, Long muscleGroupId,
        List<ExerciseSetPayload> sets, String status) {
}
