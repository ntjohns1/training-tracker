package com.noslen.training_tracker.mapper.muscle_group;

import com.noslen.training_tracker.dto.muscle_group.ProgressionPayload;
import com.noslen.training_tracker.model.muscle_group.Progression;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProgressionMapper {

    /**
     * Converts ProgressionPayload to Progression entity
     */
    public Progression toEntity(ProgressionPayload payload) {
        if (payload == null) {
            return null;
        }

        // Use constructor since most fields don't have setters
        Progression entity = new Progression(
                payload.id(),
                payload.muscleGroupId(),
                payload.mgProgressionType(),
                null // mesocycle - would need to be handled separately if needed
        );

        return entity;
    }

    /**
     * Converts Progression entity to ProgressionPayload
     */
    public ProgressionPayload toPayload(Progression entity) {
        if (entity == null) {
            return null;
        }

        return new ProgressionPayload(
                entity.getId(),
                entity.getMuscleGroupId(),
                entity.getMgProgressionType()
        );
    }

    /**
     * Updates an existing Progression entity with data from ProgressionPayload
     * Note: Since Progression is mostly immutable, this method only updates the one mutable field
     */
    public void updateEntity(Progression existing, ProgressionPayload payload) {
        if (existing == null || payload == null) {
            return;
        }

        // Only update the mutable field that has a setter
        if (payload.mgProgressionType() != null) {
            existing.setMgProgressionType(payload.mgProgressionType());
        }

        // Note: muscleGroupId and id cannot be updated because they don't have setters.
        // If these need to be updated, a new entity should be created using mergeEntity method.
    }

    /**
     * Creates a new Progression entity by merging existing entity with payload data
     * This is useful when you need to update immutable fields
     */
    public Progression mergeEntity(Progression existing, ProgressionPayload payload) {
        if (existing == null) {
            return toEntity(payload);
        }
        if (payload == null) {
            return existing;
        }

        // Create new entity using constructor since fields don't have setters
        return new Progression(
                existing.getId(), // Keep existing ID
                payload.muscleGroupId() != 0 ? payload.muscleGroupId() : existing.getMuscleGroupId(),
                payload.mgProgressionType() != null ? payload.mgProgressionType() : existing.getMgProgressionType(),
                existing.getMesocycle() // Keep existing mesocycle relationship
        );
    }

    /**
     * Converts a list of Progression entities to ProgressionPayloads
     */
    public List<ProgressionPayload> toPayloadList(List<Progression> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toPayload)
                .collect(Collectors.toList());
    }
}
