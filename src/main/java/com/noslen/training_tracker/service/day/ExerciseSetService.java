package com.noslen.training_tracker.service.day;

import java.util.List;

import com.noslen.training_tracker.model.day.ExerciseSet;

public interface ExerciseSetService {
    ExerciseSet createExerciseSet(ExerciseSet exerciseSet);
    ExerciseSet updateExerciseSet(Long id, ExerciseSet exerciseSet);
    ExerciseSet getExerciseSet(Long id);
    void deleteExerciseSet(Long id);
    List<ExerciseSet> getExerciseSetsByDayExerciseId(Long dayExerciseId);
}
