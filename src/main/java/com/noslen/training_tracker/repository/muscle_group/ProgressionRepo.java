package com.noslen.training_tracker.repository.muscle_group;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noslen.training_tracker.model.muscle_group.Progression;

public interface ProgressionRepo extends JpaRepository<Progression, Long> {

}
