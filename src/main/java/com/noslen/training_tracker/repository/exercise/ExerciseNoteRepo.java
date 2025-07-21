package com.noslen.training_tracker.repository.exercise;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noslen.training_tracker.model.exercise.ExerciseNote;

public interface ExerciseNoteRepo extends JpaRepository<ExerciseNote, Long> {

}
