package com.noslen.training_tracker.service.exercise;

import java.util.List;

import com.noslen.training_tracker.model.exercise.Exercise;
import com.noslen.training_tracker.model.exercise.ExerciseNote;

public interface ExerciseService {
    Exercise createExercise(Exercise exercise);
    Exercise updateExercise(Long exerciseId, Exercise exercise);
    void deleteExercise(Long exerciseId);
    Exercise getExercise(Long exerciseId);
    List<Exercise> getAllExercises();

    void addExerciseNote(Long exerciseId, ExerciseNote exerciseNote);
}
