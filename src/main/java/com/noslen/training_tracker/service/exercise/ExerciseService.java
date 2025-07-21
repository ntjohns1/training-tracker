package com.noslen.training_tracker.service.exercise;

import java.util.List;

import com.noslen.training_tracker.dto.exercise.ExercisePayload;
import com.noslen.training_tracker.dto.exercise.ExerciseNotePayload;

public interface ExerciseService {
    ExercisePayload createExercise(ExercisePayload exercisePayload);
    ExercisePayload updateExercise(Long exerciseId, ExercisePayload exercisePayload);
    void deleteExercise(Long exerciseId);
    ExercisePayload getExercise(Long exerciseId);
    List<ExercisePayload> getAllExercises();

    void addExerciseNote(Long exerciseId, ExerciseNotePayload exerciseNotePayload);
}
