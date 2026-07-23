package com.noslen.training_tracker.dto.mesocycle.request;

/**
 * Request DTO for updating a mesocycle's editable metadata (rename, unit, status).
 * Nested structure (weeks/days/exercises) is not edited through this endpoint.
 */
public record UpdateMesocycleRequest(
        Long id,
        String name,
        String unit,
        String status
) {
}
