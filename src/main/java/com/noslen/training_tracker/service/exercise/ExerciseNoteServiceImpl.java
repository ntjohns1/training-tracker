package com.noslen.training_tracker.service.exercise;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.noslen.training_tracker.model.exercise.ExerciseNote;
import com.noslen.training_tracker.repository.exercise.ExerciseNoteRepo;

@Service
public class ExerciseNoteServiceImpl implements ExerciseNoteService {
    private final ExerciseNoteRepo repo;

    public ExerciseNoteServiceImpl(ExerciseNoteRepo exerciseNoteRepo) {
        this.repo = exerciseNoteRepo;
    }

    @Override
    public ExerciseNote createExerciseNote(ExerciseNote exerciseNote) {
        return this.repo.save(exerciseNote);
    }

    @Override
    public ExerciseNote updateExerciseNote(Long exerciseNoteId, ExerciseNote exerciseNote) {
        Optional<ExerciseNote> exerciseNoteToUpdate = this.repo.findById(exerciseNoteId);
        if (exerciseNoteToUpdate.isPresent()) {
            exerciseNoteToUpdate.get().setText(exerciseNote.getText());
            exerciseNoteToUpdate.get().setUpdatedAt(Instant.now());
            this.repo.save(exerciseNoteToUpdate.get());
        }
        return exerciseNoteToUpdate.orElse(null);
    }

    @Override
    public void deleteExerciseNote(Long exerciseNoteId) {
        this.repo.deleteById(exerciseNoteId);
    }

    @Override
    public ExerciseNote getExerciseNote(Long exerciseNoteId) {
        Optional<ExerciseNote> exerciseNote = this.repo.findById(exerciseNoteId);
        return exerciseNote.orElse(null);
    }
}
