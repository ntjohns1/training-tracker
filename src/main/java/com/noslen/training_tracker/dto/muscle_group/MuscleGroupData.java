package com.noslen.training_tracker.dto.muscle_group;

import java.time.Instant;

public record MuscleGroupData(Long id, String name, Instant createdAt, Instant updatedAt) {}
