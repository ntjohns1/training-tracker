package com.noslen.training_tracker.dto.day.request;

import java.time.Instant;

/**
 * Request DTO for updating ExerciseSet weight or reps during workout.
 * Supports partial updates - either weight or reps can be updated independently.
 */
public record UpdateExerciseSetRequest(
        Float weight,
        Integer reps,
        Instant updatedAt
) {}
