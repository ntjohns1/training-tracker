package com.noslen.training_tracker.dto.muscle_group;

import com.noslen.training_tracker.util.MgProgressionType;

public record ProgressionPayload(long id, long muscleGroupId, MgProgressionType mgProgressionType) {
}
