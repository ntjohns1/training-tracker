package com.noslen.training_tracker.dto.day.request;

import java.time.Instant;

public record UpdateDayNoteRequest(
        Long id,
        Long dayId,
        String content,
        Instant updatedAt
) {
}
