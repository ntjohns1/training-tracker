package com.noslen.training_tracker.dto.progression.response;

import java.time.Instant;

public record MuscleGroupResponse(Long id, String name, Instant createdAt, Instant updatedAt) {}
