package com.noslen.training_tracker.service.muscle_group;

import com.noslen.training_tracker.dto.muscle_group.ProgressionPayload;

public interface ProgressionService {
    ProgressionPayload createProgression(ProgressionPayload progressionPayload);
    ProgressionPayload updateProgression(Long progressionId, ProgressionPayload progressionPayload);
    void deleteProgression(Long progressionId);
    ProgressionPayload getProgression(Long progressionId);
}
