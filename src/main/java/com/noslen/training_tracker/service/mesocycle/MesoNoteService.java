package com.noslen.training_tracker.service.mesocycle;

import com.noslen.training_tracker.dto.mesocycle.MesoNotePayload;

import java.util.List;

/**
 * Service interface for MesoNote operations
 */
public interface MesoNoteService {

    /**
     * Create a new MesoNote
     */
    MesoNotePayload createMesoNote(MesoNotePayload mesoNotePayload);

    /**
     * Get MesoNote by ID
     */
    MesoNotePayload getMesoNote(Long id);

    /**
     * Get all MesoNotes for a specific mesocycle
     */
    List<MesoNotePayload> getMesoNotesByMesoId(Long mesoId);

    /**
     * Update an existing MesoNote
     */
    MesoNotePayload updateMesoNote(Long id, MesoNotePayload mesoNotePayload);

    /**
     * Delete a MesoNote
     */
    void deleteMesoNote(Long id);

    /**
     * Get all MesoNotes
     */
    List<MesoNotePayload> getAllMesoNotes();
}
