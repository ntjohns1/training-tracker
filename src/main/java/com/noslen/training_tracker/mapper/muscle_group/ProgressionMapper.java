package com.noslen.training_tracker.mapper.muscle_group;

import com.noslen.training_tracker.dto.muscle_group.ProgressionResponse;
import com.noslen.training_tracker.model.muscle_group.Progression;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProgressionMapper {

    /**
     * Converts ProgressionResponse to Progression entity
     * Note: MuscleGroup entity must be set separately in the service layer
     */
    public Progression toEntity(ProgressionResponse payload) {
        if (payload == null) {
            return null;
        }

        // Create entity with null MuscleGroup - service layer will set this
        Progression entity = new Progression(
                payload.id(),
                null, // MuscleGroup will be set by service layer
                payload.mgProgressionType(),
                null // mesocycle - would need to be handled separately if needed
        );

        return entity;
    }

    /**
     * Converts Progression entity to ProgressionResponse
     */
    public ProgressionResponse toPayload(Progression entity) {
        if (entity == null) {
            return null;
        }

        return new ProgressionResponse(
                entity.getId(),
                entity.getMuscleGroupId(),
                entity.getMgProgressionType()
        );
    }

    /**
     * Updates an existing Progression entity with data from ProgressionResponse
     * Note: MuscleGroup changes must be handled in the service layer
     */
    public Progression updateEntity(Progression existing, ProgressionResponse payload) {
        if (existing == null || payload == null) {
            return existing;
        }

        // Create new entity using constructor since fields don't have setters
        return new Progression(
                existing.getId(), // Keep existing ID
                existing.getMuscleGroup(), // Keep existing MuscleGroup - service handles changes
                payload.mgProgressionType() != null ? payload.mgProgressionType() : existing.getMgProgressionType(),
                existing.getMesocycle() // Keep existing mesocycle relationship
        );
    }

    /**
     * Converts a list of Progression entities to ProgressionPayloads
     */
    public List<ProgressionResponse> toPayloadList(List<Progression> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toPayload)
                .collect(Collectors.toList());
    }
}
