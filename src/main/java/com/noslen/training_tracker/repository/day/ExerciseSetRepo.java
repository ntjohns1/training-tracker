package com.noslen.training_tracker.repository.day;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noslen.training_tracker.model.day.ExerciseSet;

public interface ExerciseSetRepo extends JpaRepository<ExerciseSet, Long> {
    List<ExerciseSet> findByDayExercise_Id(Long dayExerciseId);
}
