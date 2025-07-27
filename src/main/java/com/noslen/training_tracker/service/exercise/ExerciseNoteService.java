package com.noslen.training_tracker.service.exercise;

import com.noslen.training_tracker.dto.exercise.response.ExerciseNoteResponse;

public interface ExerciseNoteService {
    ExerciseNoteResponse createExerciseNote(ExerciseNoteResponse exerciseNoteResponse);
    ExerciseNoteResponse updateExerciseNote(Long exerciseNoteId, ExerciseNoteResponse exerciseNoteResponse);
    void deleteExerciseNote(Long exerciseNoteId);
    ExerciseNoteResponse getExerciseNote(Long exerciseNoteId);
}
