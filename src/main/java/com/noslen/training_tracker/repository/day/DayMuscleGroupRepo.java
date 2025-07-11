package com.noslen.training_tracker.repository.day;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noslen.training_tracker.model.day.DayMuscleGroup;

public interface DayMuscleGroupRepo extends JpaRepository<DayMuscleGroup, Long> {

}
