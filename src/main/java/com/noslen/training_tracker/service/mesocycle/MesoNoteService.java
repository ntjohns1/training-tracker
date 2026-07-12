package com.noslen.training_tracker.service.mesocycle;

import com.noslen.training_tracker.dto.mesocycle.response.MesoNoteResponse;

import java.util.List;

/**
 * Service interface for MesoNote operations
 */
public interface MesoNoteService {

    /**
     * Create a new MesoNote
     */
    MesoNoteResponse createMesoNote(MesoNoteResponse mesoNoteResponse);

    /**
     * Get MesoNote by ID
     */
    MesoNoteResponse getMesoNote(Long id);

    /**
     * Get all MesoNotes for a specific mesocycle
     */
    List<MesoNoteResponse> getMesoNotesByMesoId(Long mesoId);

    /**
     * Update an existing MesoNote
     */
    MesoNoteResponse updateMesoNote(Long id, MesoNoteResponse mesoNoteResponse);

    /**
     * Delete a MesoNote
     */
    void deleteMesoNote(Long id);

    /**
     * Get all MesoNotes
     */
    List<MesoNoteResponse> getAllMesoNotes();
}
