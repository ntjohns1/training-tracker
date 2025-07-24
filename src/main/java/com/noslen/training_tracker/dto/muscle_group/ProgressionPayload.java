package com.noslen.training_tracker.dto.muscle_group;

import com.noslen.training_tracker.enums.MgProgressionType;

public record ProgressionPayload(long id, long muscleGroupId, MgProgressionType mgProgressionType) {
}
