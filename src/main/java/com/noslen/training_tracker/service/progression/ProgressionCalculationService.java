package com.noslen.training_tracker.service.progression;

import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.day.DayMuscleGroup;
import com.noslen.training_tracker.model.day.ExerciseSet;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;


public interface ProgressionCalculationService {

    void calculateRecommendedSets(Long mesoId);
    void calculateTargets(Long mesoId);

}
/*
*
*
*
*
*
* */