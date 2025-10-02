package com.noslen.training_tracker.dto.day.request;

public record CreateDayMuscleGroupRequest(
        Long dayId,
        Long muscleGroupId
) {
}
