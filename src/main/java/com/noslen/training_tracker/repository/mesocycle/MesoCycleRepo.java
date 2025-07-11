package com.noslen.training_tracker.repository.mesocycle;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noslen.training_tracker.model.mesocycle.Mesocycle;

public interface MesoCycleRepo extends JpaRepository<Mesocycle, Long> {

}
