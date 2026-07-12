package com.noslen.training_tracker.dto.day.request;

import java.time.Instant;

/**
 * Request DTO for finishing an ExerciseSet.
 * First set of an exercise requires soreness feedback, subsequent sets do not.
 */
public record FinishExerciseSetRequest(
        Instant finishedAt,
        Integer reps,
        Float weight,
        Integer soreness  // Optional: only required for first set of exercise
) {}
