package com.noslen.training_tracker.service.muscle_group;

import com.noslen.training_tracker.model.muscle_group.Progression;

public interface ProgressionService {
    Progression createProgression(Progression progression);
    Progression updateProgression(Long progressionId, Progression progression);
    void deleteProgression(Long progressionId);
    Progression getProgression(Long progressionId);
}
