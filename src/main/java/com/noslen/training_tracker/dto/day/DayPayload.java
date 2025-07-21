package com.noslen.training_tracker.dto.day;

import java.time.Instant;
import java.util.List;

public record DayPayload(
        Long id,
        Long mesoId,
        Long week,
        Long position,
        Instant createdAt,
        Instant updatedAt,
        Integer bodyweight,
        Instant bodyweightAt,
        String unit,
        Instant finishedAt,
        String label,
        List<DayNotePayload> notes,
        List<DayExercisePayload> exercises,
        List<DayMuscleGroupPayload> muscleGroups,
        String status) {
}
