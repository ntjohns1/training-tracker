package com.noslen.training_tracker.repository.day;

import com.noslen.training_tracker.model.day.DayExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DayExerciseRepo extends JpaRepository<DayExercise, Long> {
    List<DayExercise> findByDay_Id(Long dayId);
    Optional<DayExercise> findByDay_IdAndExercise_Id(Long dayId, Long exerciseId);
}
