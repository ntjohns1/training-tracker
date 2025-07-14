package com.noslen.training_tracker.dto.mesocycle;

import java.time.Instant;
import java.util.List;

public record MesocyclePayload(
        Long id,
        String key,
        Long userId,
        String name,
        Integer days,
        String unit,
        Long sourceTemplateId,
        Long sourceMesoId,
        Long microRirs,
        Instant createdAt,
        Instant updatedAt,
        Instant finishedAt,
        Instant deletedAt,
        Instant firstMicroCompletedAt,
        Instant firstWorkoutCompletedAt,
        Instant firstExerciseCompletedAt,
        Instant firstSetCompletedAt,
        Instant lastMicroFinishedAt,
        Instant lastSetCompletedAt,
        Instant lastSetSkippedAt,
        Instant lastWorkoutCompletedAt,
        Instant lastWorkoutFinishedAt,
        Instant lastWorkoutSkippedAt,
        Instant lastWorkoutPartialedAt,
        Integer weeks,
        List<MesoNotePayload> notes) {
}
