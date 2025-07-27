package com.noslen.training_tracker.dto.muscle_group;

import com.noslen.training_tracker.enums.MgProgressionType;

public record ProgressionResponse(long id, long muscleGroupId, MgProgressionType mgProgressionType) {
}
