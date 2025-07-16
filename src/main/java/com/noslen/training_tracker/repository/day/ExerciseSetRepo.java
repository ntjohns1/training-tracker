package com.noslen.training_tracker.repository.day;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noslen.training_tracker.model.day.ExerciseSet;

public interface ExerciseSetRepo extends JpaRepository<ExerciseSet, Long> {
    List<ExerciseSet> findByDayExerciseId(Long dayExerciseId);
    void calculateWeightTarget(Long dayExerciseId);
    void calculateRepsTarget(Long dayExerciseId);
}
