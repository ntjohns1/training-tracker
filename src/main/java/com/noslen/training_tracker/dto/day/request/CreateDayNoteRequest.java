package com.noslen.training_tracker.dto.day.request;

/**
 * Request DTO for adding a note to a day.
 */
public record CreateDayNoteRequest(
        Long dayId,
        Long noteId,
        Boolean pinned,
        String text
) {
}
