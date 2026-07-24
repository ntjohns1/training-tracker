package com.noslen.training_tracker.dto.day.request;

import java.time.Instant;

/**
 * Request DTO for updating an ExerciseSet during a workout (partial update).
 *
 * <p>{@code weight}/{@code reps} update the logged values. {@code status} drives the
 * "logged" state used by the LOG checkbox: {@code "complete"} stamps {@code finishedAt}
 * (using the supplied {@code finishedAt}, or now if absent); any other status clears it.</p>
 */
public record UpdateExerciseSetRequest(
        Float weight,
        Integer reps,
        Instant finishedAt,
        String status
) {}
