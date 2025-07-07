package com.noslen.training_tracker.repository.muscle_group;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noslen.training_tracker.model.muscle_group.MuscleGroup;

public interface MuscleGroupRepo extends JpaRepository<MuscleGroup, Long> {

}
