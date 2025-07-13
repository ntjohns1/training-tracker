package com.noslen.training_tracker.dto.exercise;

import java.time.Instant;

public record ExerciseNotePayload(
        Long id,
        Long exerciseId,
        Long userId,
        Long noteId,
        Long dayExerciseId,
        Instant createdAt,
        Instant updatedAt,
        String text) {
}
