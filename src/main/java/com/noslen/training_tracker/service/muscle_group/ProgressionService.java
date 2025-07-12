package com.noslen.training_tracker.service.muscle_group;

import com.noslen.training_tracker.model.muscle_group.Progression;

public interface ProgressionService {
    Progression getProgression(Long progressionId);
    void updateProgression(Progression progression);
}
