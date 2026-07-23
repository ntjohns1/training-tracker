package com.noslen.training_tracker.service.exercise;

import com.noslen.training_tracker.dto.exercise.request.CreateExerciseNoteRequest;
import com.noslen.training_tracker.dto.exercise.request.UpdateExerciseNoteRequest;
import com.noslen.training_tracker.dto.exercise.response.ExerciseNoteResponse;

public interface ExerciseNoteService {
    ExerciseNoteResponse createExerciseNote(CreateExerciseNoteRequest request);
    ExerciseNoteResponse updateExerciseNote(Long exerciseNoteId, UpdateExerciseNoteRequest request);
    void deleteExerciseNote(Long exerciseNoteId);
    ExerciseNoteResponse getExerciseNote(Long exerciseNoteId);
}
