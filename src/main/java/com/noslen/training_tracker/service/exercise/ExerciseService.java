package com.noslen.training_tracker.service.exercise;

import java.util.List;

import com.noslen.training_tracker.dto.exercise.ExerciseResponse;
import com.noslen.training_tracker.dto.exercise.ExerciseNoteResponse;

public interface ExerciseService {
    ExerciseResponse createExercise(ExerciseResponse exerciseResponse);
    ExerciseResponse updateExercise(Long exerciseId, ExerciseResponse exerciseResponse);
    void deleteExercise(Long exerciseId);
    ExerciseResponse getExercise(Long exerciseId);
    List<ExerciseResponse> getAllExercises();

    void addExerciseNote(Long exerciseId, ExerciseNoteResponse exerciseNoteResponse);
}
