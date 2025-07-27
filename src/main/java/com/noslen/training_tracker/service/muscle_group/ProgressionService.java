package com.noslen.training_tracker.service.muscle_group;

import com.noslen.training_tracker.dto.muscle_group.response.ProgressionResponse;

public interface ProgressionService {
    ProgressionResponse createProgression(ProgressionResponse progressionResponse);
    ProgressionResponse updateProgression(Long progressionId, ProgressionResponse progressionResponse);
    void deleteProgression(Long progressionId);
    ProgressionResponse getProgression(Long progressionId);
}
