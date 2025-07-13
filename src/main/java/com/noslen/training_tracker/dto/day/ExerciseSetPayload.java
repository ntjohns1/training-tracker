package com.noslen.training_tracker.dto.day;

import java.time.Instant;

public record ExerciseSetPayload(Long id, Long dayExerciseId, Integer position, String setType, Float weight,
        Float weightTarget, Float weightTargetMin, Float weightTargetMax, Integer reps, Integer repsTarget,
        Float bodyweight, String unit, Instant createdAt, Instant finishedAt, String status) {
}
