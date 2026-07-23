package com.noslen.training_tracker.dto.exercise.request;

/**
 * Request DTO for editing an exercise note's text.
 */
public record UpdateExerciseNoteRequest(
        Long id,
        String text
) {
}
