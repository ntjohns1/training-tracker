package com.noslen.training_tracker.repository.day;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noslen.training_tracker.model.day.DayMuscleGroup;

import java.util.List;

public interface DayMuscleGroupRepo extends JpaRepository<DayMuscleGroup, Long> {
    List<DayMuscleGroup> findByDay_Id(Long dayId);
}
