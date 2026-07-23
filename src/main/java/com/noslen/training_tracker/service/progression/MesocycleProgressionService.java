package com.noslen.training_tracker.service.progression;

import com.noslen.training_tracker.dto.day.request.FinishDayRequest;

public interface MesocycleProgressionService {

    void processCompletedDayandProgramNext(FinishDayRequest finishDayRequest);


    int calculateRecommendedSets(Long dayMuscleGroupId);
}
