package com.noslen.training_tracker.repository.mesocycle;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noslen.training_tracker.model.mesocycle.MesoNote;
import java.util.List;

public interface MesoNoteRepo extends JpaRepository<MesoNote, Long> {

    /**
     * Find all MesoNotes by mesocycle ID
     */
    List<MesoNote> findByMesocycle_Id(Long mesocycleId);
}
