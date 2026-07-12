package com.noslen.training_tracker.dto.day.response;

import java.time.Instant;
import java.util.List;

public record DayResponse(
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
        List<DayNoteResponse> notes,
        List<DayExerciseResponse> exercises,
        List<DayMuscleGroupResponse> muscleGroups,
        String status) {
}
