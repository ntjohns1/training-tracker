package com.noslen.training_tracker.dto.muscle_group;

import java.time.Instant;

public record MuscleGroupPayload(Long id, String name, Instant createdAt, Instant updatedAt) {}
