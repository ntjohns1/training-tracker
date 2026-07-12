package com.noslen.training_tracker.dto.day.request;

import java.time.Instant;

public record CreateDayNoteRequest(
        Long dayId,
        String content,
        Instant createdAt,
        Instant updatedAt,
        String status
) {
}
