package com.noslen.training_tracker.dto.day;

import java.time.Instant;

public record DayMuscleGroupPayload(
        Long id,
        Long dayId,
        Long muscleGroupId,
        Integer pump,
        Integer soreness,
        Integer workload,
        Instant createdAt,
        Instant updatedAt,
        Integer recommendedSets,
        String status) {

}
