package com.noslen.training_tracker.dto.muscle_group;

import com.noslen.training_tracker.model.muscle_group.types.MgProgressionType;

public record ProgressionPayload(long id, long muscleGroupId, MgProgressionType mgProgressionType) {
}
