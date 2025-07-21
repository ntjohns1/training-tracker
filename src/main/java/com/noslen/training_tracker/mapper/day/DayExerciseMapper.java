package com.noslen.training_tracker.mapper.day;

import com.noslen.training_tracker.dto.day.DayExercisePayload;
import com.noslen.training_tracker.dto.day.ExerciseSetPayload;
import com.noslen.training_tracker.model.day.DayExercise;
import com.noslen.training_tracker.model.day.ExerciseSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DayExerciseMapper {

    @Autowired
    private ExerciseSetMapper exerciseSetMapper;

    /**
     * Converts DayExercisePayload to DayExercise entity
     */
    public DayExercise toEntity(DayExercisePayload payload) {
        if (payload == null) {
            return null;
        }

        DayExercise.DayExerciseBuilder builder = DayExercise.builder()
                .id(payload.id())
                .position(payload.position())
                .jointPain(payload.jointPain())
                .createdAt(payload.createdAt() != null ? payload.createdAt() : Instant.now())
                .updatedAt(payload.updatedAt() != null ? payload.updatedAt() : Instant.now())
                .sourceDayExerciseId(payload.sourceDayExerciseId())
                .status(payload.status());

        // Handle entity relationships by creating stub entities with IDs
        if (payload.dayId() != null) {
            builder.day(com.noslen.training_tracker.model.day.Day.builder().id(payload.dayId()).build());
        }
        if (payload.exerciseId() != null) {
            builder.exercise(com.noslen.training_tracker.model.exercise.Exercise.builder().id(payload.exerciseId()).build());
        }
        if (payload.muscleGroupId() != null) {
            builder.muscleGroup(com.noslen.training_tracker.model.day.DayMuscleGroup.builder().id(payload.muscleGroupId()).build());
        }

        DayExercise entity = builder.build();

        // Handle sets mapping
        if (payload.sets() != null) {
            List<ExerciseSet> sets = payload.sets().stream()
                    .map(setPayload -> {
                        ExerciseSet set = exerciseSetMapper.toEntity(setPayload);
                        if (set != null) {
                            set.setDayExercise(entity);
                        }
                        return set;
                    })
                    .filter(set -> set != null) // Filter out any null sets
                    .collect(Collectors.toList());
            entity.getSets().addAll(sets);
        }

        return entity;
    }

    /**
     * Converts DayExercise entity to DayExercisePayload
     */
    public DayExercisePayload toPayload(DayExercise entity) {
        if (entity == null) {
            return null;
        }

        List<ExerciseSetPayload> setPayloads = null;
        if (entity.getSets() != null) {
            setPayloads = entity.getSets().stream()
                    .map(exerciseSetMapper::toPayload)
                    .collect(Collectors.toList());
        }

        return new DayExercisePayload(
                entity.getId(),
                entity.getDayId(),
                entity.getExerciseId(),
                entity.getPosition(),
                entity.getJointPain(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getSourceDayExerciseId(),
                entity.getMuscleGroupId(),
                setPayloads,
                entity.getStatus()
        );
    }

    /**
     * Updates an existing DayExercise entity with data from DayExercisePayload
     * Note: Since DayExercise is mostly immutable, this method only updates the mutable fields
     */
    public void updateEntity(DayExercise existing, DayExercisePayload payload) {
        if (existing == null || payload == null) {
            return;
        }

        // Only update the mutable fields that have setters
        existing.setUpdatedAt(Instant.now());

        // Update entity relationships (these have setter methods)
        if (payload.dayId() != null) {
            existing.setDay(com.noslen.training_tracker.model.day.Day.builder().id(payload.dayId()).build());
        }
        if (payload.exerciseId() != null) {
            existing.setExercise(com.noslen.training_tracker.model.exercise.Exercise.builder().id(payload.exerciseId()).build());
        }
        if (payload.muscleGroupId() != null) {
            existing.setMuscleGroup(com.noslen.training_tracker.model.day.DayMuscleGroup.builder().id(payload.muscleGroupId()).build());
        }

        // Note: Fields like position, jointPain, sourceDayExerciseId, and status cannot be updated
        // because they don't have setters. If these need to be updated, a new entity should be created.
        // Note: sets are not updated here as they should be managed separately
    }

    /**
     * Creates a new DayExercise entity by merging existing entity with payload data
     * This is useful when you need to update immutable fields
     */
    public DayExercise mergeEntity(DayExercise existing, DayExercisePayload payload) {
        if (existing == null) {
            return toEntity(payload);
        }
        if (payload == null) {
            return existing;
        }

        return DayExercise.builder()
                .id(existing.getId()) // Keep existing ID
                .day(payload.dayId() != null ? 
                    com.noslen.training_tracker.model.day.Day.builder().id(payload.dayId()).build() : 
                    existing.getDay())
                .exercise(payload.exerciseId() != null ? 
                    com.noslen.training_tracker.model.exercise.Exercise.builder().id(payload.exerciseId()).build() : 
                    existing.getExercise())
                .muscleGroup(payload.muscleGroupId() != null ? 
                    com.noslen.training_tracker.model.day.DayMuscleGroup.builder().id(payload.muscleGroupId()).build() : 
                    existing.getMuscleGroup())
                .position(payload.position() != null ? payload.position() : existing.getPosition())
                .jointPain(payload.jointPain() != null ? payload.jointPain() : existing.getJointPain())
                .sourceDayExerciseId(payload.sourceDayExerciseId() != null ? payload.sourceDayExerciseId() : existing.getSourceDayExerciseId())
                .status(payload.status() != null ? payload.status() : existing.getStatus())
                .createdAt(existing.getCreatedAt()) // Keep original creation time
                .updatedAt(Instant.now()) // Update timestamp
                .sets(existing.getSets()) // Keep existing sets
                .build();
    }

    /**
     * Converts a list of DayExercise entities to DayExercisePayloads
     */
    public List<DayExercisePayload> toPayloadList(List<DayExercise> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toPayload)
                .collect(Collectors.toList());
    }
}
