package com.noslen.training_tracker.dto.day.request;

/**
 * Request DTO for editing a day note (text and/or pin state).
 */
public record UpdateDayNoteRequest(
        Long id,
        Boolean pinned,
        String text
) {
}
