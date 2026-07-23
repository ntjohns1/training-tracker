package com.noslen.training_tracker.dto.day.request;

import java.time.Instant;

public record CreateDayRequest(
        Long mesoId,
        Integer week,
        Integer position,
        Instant createdAt,
        Instant updatedAt,
        String unit,
        String label
) {
}
