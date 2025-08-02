package com.noslen.training_tracker.dto.day.request;

import java.time.Instant;
import java.util.List;

/**
 * Request DTO for finishing a complete workout day.
 * This is the same structure as UpdateDayRequest but semantically represents
 * the final completion of a workout day with all feedback provided.
 * Status transitions from "pendingFeedback" to "pendingConfirmation".
 */
public record FinishDayRequest(
        Long id,
        Long mesoId,
        Integer week,
        Integer position,
        Instant createdAt,
        Instant updatedAt,
        Float bodyweight,
        Instant bodyweightAt,
        String unit,
        Instant finishedAt,
        String label,
        List<Object> notes,
        List<DayExerciseFinishRequest> exercises,
        List<DayMuscleGroupFinishRequest> muscleGroups,
        String status
) {
    
    /**
     * Nested DTO for day exercises in finish day request.
     */
    public record DayExerciseFinishRequest(
            Long id,
            Long dayId,
            Long exerciseId,
            Integer position,
            Integer jointPain,
            Instant createdAt,
            Instant updatedAt,
            Long sourceDayExerciseId,
            Long muscleGroupId,
            List<ExerciseSetFinishRequest> sets,
            String status
    ) {}
    
    /**
     * Nested DTO for exercise sets in finish day request.
     */
    public record ExerciseSetFinishRequest(
            Long id,
            Long dayExerciseId,
            Integer position,
            String setType,
            Float weight,
            Float weightTarget,
            Float weightTargetMin,
            Float weightTargetMax,
            Integer reps,
            Integer repsTarget,
            Float bodyweight,
            String unit,
            Instant createdAt,
            Instant finishedAt,
            String status
    ) {}
    
    /**
     * Nested DTO for day muscle groups in finish day request.
     * Contains final feedback: pump, soreness, workload.
     */
    public record DayMuscleGroupFinishRequest(
            Long id,
            Long dayId,
            Long muscleGroupId,
            Integer pump,
            Integer soreness,
            Integer workload,
            Instant createdAt,
            Instant updatedAt,
            Integer recommendedSets,
            String status
    ) {}
}
