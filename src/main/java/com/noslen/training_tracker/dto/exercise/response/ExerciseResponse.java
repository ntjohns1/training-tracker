package com.noslen.training_tracker.dto.exercise.response;

import java.time.Instant;
import java.util.List;

public record ExerciseResponse(
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
        List<ExerciseNoteResponse> notes) {
}
