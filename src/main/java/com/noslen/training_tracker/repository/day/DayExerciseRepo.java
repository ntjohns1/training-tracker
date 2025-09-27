package com.noslen.training_tracker.repository.day;

import com.noslen.training_tracker.model.day.DayExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DayExerciseRepo extends JpaRepository<DayExercise, Long> {
    List<DayExercise> findByDay_Id(Long dayId);
    Optional<DayExercise> findByDay_IdAndExercise_Id(Long dayId, Long exerciseId);

    // Returns just the maximum jointPain value for a given day (null if none)
    @Query("select max(de.jointPain) from DayExercise de where de.day.id = :dayId and de.muscleGroup.id = :muscleGroupId")
    Integer findMaxJointPainByDayId(@Param("dayId") Long dayId, @Param("muscleGroupId") Long muscleGroupId);

    @Query("select count(de) from DayExercise de where de.day.id = :dayId and de.muscleGroup.id = :muscleGroupId")
    Integer countByDayIdAndMuscleGroupId(@Param("dayId") Long dayId, @Param("muscleGroupId") Long muscleGroupId);
}
