package com.noslen.training_tracker.dto.exercise.request;

/**
 * Request DTO for adding a note to an exercise (optionally scoped to a specific day-exercise).
 */
public record CreateExerciseNoteRequest(
        Long exerciseId,
        Long dayExerciseId,
        Long noteId,
        String text
) {
}
