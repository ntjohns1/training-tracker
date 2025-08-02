package com.noslen.training_tracker.dto.day.request;

/**
 * Request DTO for updating ExerciseSet weight or reps during workout.
 * Supports partial updates - either weight or reps can be updated independently.
 */
public record UpdateExerciseSetRequest(
        Float weight,
        Integer reps
) {}
