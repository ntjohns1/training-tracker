package com.noslen.training_tracker.dto.mesocycle.request;

/**
 * Request DTO for editing a mesocycle note's text.
 */
public record UpdateMesoNoteRequest(
        Long id,
        String text
) {
}
