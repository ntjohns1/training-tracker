package com.noslen.training_tracker.dto;

import com.noslen.training_tracker.dto.exercise.response.ExerciseResponse;
import com.noslen.training_tracker.dto.mesocycle.response.MesocycleResponse;
import com.noslen.training_tracker.dto.progression.response.MuscleGroupResponse;

import java.util.List;

/**
 * Aggregate payload returned to the client on startup: the current user's mesocycles plus the
 * reference data (exercise catalog + muscle groups) the builder and workout screens need.
 */
public record BootstrapResponse(
        List<MesocycleResponse> mesocycles,
        List<ExerciseResponse> exercises,
        List<MuscleGroupResponse> muscleGroups
) {
}
