package com.noslen.training_tracker.dto.muscle_group.response;

import java.time.Instant;

public record MuscleGroupResponse(Long id, String name, Instant createdAt, Instant updatedAt) {}
