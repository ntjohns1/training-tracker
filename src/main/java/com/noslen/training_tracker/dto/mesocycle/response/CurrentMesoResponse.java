package com.noslen.training_tracker.dto.mesocycle.response;

import com.noslen.training_tracker.dto.muscle_group.response.ProgressionResponse;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record CurrentMesoResponse(
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
        List<Week> weeks,
        Set<MesoNoteResponse> notes,
        String status,
        String generatedFrom,
        Map<Long, ProgressionResponse> progressions) {
}
