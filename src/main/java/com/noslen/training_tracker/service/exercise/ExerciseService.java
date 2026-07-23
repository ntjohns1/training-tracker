package com.noslen.training_tracker.service.exercise;

import java.util.List;

import com.noslen.training_tracker.dto.exercise.response.ExerciseResponse;

public interface ExerciseService {
    ExerciseResponse getExercise(Long exerciseId);
    List<ExerciseResponse> getAllExercises();
}
