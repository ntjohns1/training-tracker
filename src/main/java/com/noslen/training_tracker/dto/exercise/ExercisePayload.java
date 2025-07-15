package com.noslen.training_tracker.dto.exercise;

import java.time.Instant;
import java.util.List;

public record ExercisePayload(
        Long id,
        String name,
        Long muscleGroupId,
        String youtubeId,
        String exerciseType,
        Long userId,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt,
        String mgSubType,
        List<ExerciseNotePayload> notes) {
}
