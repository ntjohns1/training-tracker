package com.noslen.training_tracker.dto.mesocycle;

import java.time.Instant;

public record MesoTemplatePayload(
        Long id,
        String key,
        String name,
        String emphasis,
        String sex,
        Long userId,
        Long sourceTemplateId,
        Long sourceMesoId,
        Long prevTemplateId,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt,
        Integer frequency) {
}
