package com.noslen.training_tracker.dto.mesocycle.request;

/**
 * Request DTO for adding a note to a mesocycle.
 */
public record CreateMesoNoteRequest(
        Long mesoId,
        Long noteId,
        String text
) {
}
