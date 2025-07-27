package com.noslen.training_tracker.dto.mesocycle.response;

import java.time.Instant;

public record MesoNoteResponse(
        Long id,
        Long mesoId,
        Long noteId,
        Instant createdAt,
        Instant updatedAt,
        String text) {
}
