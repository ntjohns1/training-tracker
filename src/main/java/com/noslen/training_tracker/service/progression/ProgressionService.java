package com.noslen.training_tracker.service.progression;

import com.noslen.training_tracker.dto.progression.response.ProgressionResponse;

public interface ProgressionService {
    ProgressionResponse createProgression(ProgressionResponse progressionResponse);
    ProgressionResponse updateProgression(Long progressionId, ProgressionResponse progressionResponse);
    void deleteProgression(Long progressionId);
    ProgressionResponse getProgression(Long progressionId);
}
