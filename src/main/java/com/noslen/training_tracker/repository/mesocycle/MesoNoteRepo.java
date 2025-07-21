package com.noslen.training_tracker.repository.mesocycle;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noslen.training_tracker.model.mesocycle.MesoNote;

public interface MesoNoteRepo extends JpaRepository<MesoNote, Long> {

}
