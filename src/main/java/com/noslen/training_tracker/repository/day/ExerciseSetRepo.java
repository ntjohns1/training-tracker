package com.noslen.training_tracker.repository.day;

import com.noslen.training_tracker.model.day.ExerciseSet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseSetRepo extends JpaRepository<ExerciseSet, Long> {
    List<ExerciseSet> findByDayExercise_Id(Long dayExerciseId);

    Integer countByDayExercise_Day_IdAndDayExercise_MuscleGroup_Id(Long dayId, Long muscleGroupId);
}
