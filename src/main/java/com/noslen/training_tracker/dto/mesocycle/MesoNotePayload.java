package com.noslen.training_tracker.dto.mesocycle;

import java.time.Instant;

public record MesoNotePayload(
        Long id,
        Long mesoId,
        Long noteId,
        Instant createdAt,
        Instant updatedAt,
        String text) {
}
