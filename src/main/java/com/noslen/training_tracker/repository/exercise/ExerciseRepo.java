package com.noslen.training_tracker.repository.exercise;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noslen.training_tracker.model.exercise.Exercise;

public interface ExerciseRepo extends JpaRepository<Exercise, Long> {

}
