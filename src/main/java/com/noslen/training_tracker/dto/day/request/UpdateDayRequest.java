package com.noslen.training_tracker.dto.day.request;

import java.time.Instant;
import java.util.List;

/**
 * Request DTO for updating a Day with complete nested structure.
 * Used for finishing day exercises and providing workout feedback.
 * Based on real API structure from finish_day_exercise and finish_day endpoints.
 */
public record UpdateDayRequest(
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
        List<Object> notes,  // Simplified for now
        List<DayExerciseUpdateRequest> exercises,
        List<DayMuscleGroupUpdateRequest> muscleGroups,
        String status
) {
    
    /**
     * Nested DTO for updating day exercises within a day update.
     */
    public record DayExerciseUpdateRequest(
            Long id,
            Long dayId,
            Long exerciseId,
            Integer position,
            Integer jointPain,
            Instant createdAt,
            Instant updatedAt,
            Long sourceDayExerciseId,
            Long muscleGroupId,
            List<ExerciseSetUpdateRequest> sets,
            String status
    ) {}
    
    /**
     * Nested DTO for updating exercise sets within a day exercise update.
     */
    public record ExerciseSetUpdateRequest(
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
     * Nested DTO for updating day muscle groups within a day update.
     */
    public record DayMuscleGroupUpdateRequest(
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
