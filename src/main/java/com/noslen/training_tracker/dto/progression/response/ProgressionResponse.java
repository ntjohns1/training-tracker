package com.noslen.training_tracker.dto.progression.response;

import com.noslen.training_tracker.enums.MgProgressionType;

public record ProgressionResponse(long id, long muscleGroupId, MgProgressionType mgProgressionType) {
}
