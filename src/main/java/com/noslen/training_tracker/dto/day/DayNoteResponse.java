package com.noslen.training_tracker.dto.day;

import java.time.Instant;

public record DayNoteResponse(
        Long id,
        Long dayId,
        Long noteId,
        Boolean pinned,
        Instant createdAt,
        Instant updatedAt,
        String text) {
}
