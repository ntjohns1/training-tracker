package com.noslen.training_tracker.repository.progression;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noslen.training_tracker.model.progression.MuscleGroup;

public interface MuscleGroupRepo extends JpaRepository<MuscleGroup, Long> {

}
