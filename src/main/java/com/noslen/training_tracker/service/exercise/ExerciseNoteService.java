package com.noslen.training_tracker.service.exercise;

import com.noslen.training_tracker.model.exercise.ExerciseNote;

public interface ExerciseNoteService {
    ExerciseNote createExerciseNote(ExerciseNote exerciseNote);
    ExerciseNote updateExerciseNote(Long exerciseNoteId, ExerciseNote exerciseNote);
    void deleteExerciseNote(Long exerciseNoteId);
    ExerciseNote getExerciseNote(Long exerciseNoteId);
}
