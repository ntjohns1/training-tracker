package com.noslen.training_tracker.service.exercise;

import com.noslen.training_tracker.dto.exercise.ExerciseNotePayload;

public interface ExerciseNoteService {
    ExerciseNotePayload createExerciseNote(ExerciseNotePayload exerciseNotePayload);
    ExerciseNotePayload updateExerciseNote(Long exerciseNoteId, ExerciseNotePayload exerciseNotePayload);
    void deleteExerciseNote(Long exerciseNoteId);
    ExerciseNotePayload getExerciseNote(Long exerciseNoteId);
}
