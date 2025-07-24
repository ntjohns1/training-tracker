package com.noslen.training_tracker.mapper.muscle_group;

import com.noslen.training_tracker.dto.muscle_group.MuscleGroupPayload;
import com.noslen.training_tracker.model.muscle_group.MuscleGroup;
import com.noslen.training_tracker.model.muscle_group.types.MgName;
import com.noslen.training_tracker.util.EnumConverter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MuscleGroupMapper {

    /**
     * Converts MuscleGroupPayload to MuscleGroup entity
     */
    public MuscleGroup toEntity(MuscleGroupPayload payload) {
        if (payload == null) {
            return null;
        }

        MuscleGroup entity = new MuscleGroup();
        entity.setId(payload.id());
        entity.setName(stringToMgName(payload.name()));
        entity.setCreatedAt(payload.createdAt() != null ? payload.createdAt() : Instant.now());
        entity.setUpdatedAt(payload.updatedAt() != null ? payload.updatedAt() : Instant.now());

        return entity;
    }

    /**
     * Converts MuscleGroup entity to MuscleGroupPayload
     */
    public MuscleGroupPayload toPayload(MuscleGroup entity) {
        if (entity == null) {
            return null;
        }

        return new MuscleGroupPayload(
                entity.getId(),
                mgNameToString(entity.getName()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    /**
     * Updates an existing MuscleGroup entity with data from MuscleGroupPayload
     */
    public void updateEntity(MuscleGroup existing, MuscleGroupPayload payload) {
        if (existing == null || payload == null) {
            return;
        }

        // Update mutable fields
        if (payload.name() != null) {
            existing.setName(stringToMgName(payload.name()));
        }
        if (payload.createdAt() != null) {
            existing.setCreatedAt(payload.createdAt());
        }
        
        // Always update the updatedAt timestamp
        existing.setUpdatedAt(Instant.now());
    }

    /**
     * Creates a new MuscleGroup entity by merging existing entity with payload data
     * This is useful when you need to update all fields
     */
    public MuscleGroup mergeEntity(MuscleGroup existing, MuscleGroupPayload payload) {
        if (existing == null) {
            return toEntity(payload);
        }
        if (payload == null) {
            return existing;
        }

        MuscleGroup merged = new MuscleGroup();
        merged.setId(existing.getId()); // Keep existing ID
        merged.setName(payload.name() != null ? stringToMgName(payload.name()) : existing.getName());
        merged.setCreatedAt(existing.getCreatedAt()); // Keep original creation time
        merged.setUpdatedAt(Instant.now()); // Update timestamp

        return merged;
    }

    /**
     * Converts a list of MuscleGroup entities to MuscleGroupPayloads
     */
    public List<MuscleGroupPayload> toPayloadList(List<MuscleGroup> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toPayload)
                .collect(Collectors.toList());
    }

    /**
     * Converts String name to MgName enum
     * Handles capitalized format (e.g., "Chest", "Quads") from enum.getValue()
     */
    private MgName stringToMgName(String name) {
        if (name == null) {
            return null;
        }
        
        // Try direct enum lookup first (for uppercase values like "CHEST")
        try {
            return EnumConverter.stringToEnum(MgName.class, name);
        } catch (IllegalArgumentException e) {
            // If that fails, try to find by the enum's getValue() method (capitalized)
            for (MgName mgName : MgName.values()) {
                if (mgName.getValue().equals(name)) {
                    return mgName;
                }
            }
            throw new IllegalArgumentException("No MgName found for value: " + name);
        }
    }
    
    /**
     * Converts MgName enum to String using getValue() method
     * Returns capitalized format (e.g., "Chest", "Quads")
     */
    private String mgNameToString(MgName mgName) {
        if (mgName == null) {
            return null;
        }
        return EnumConverter.enumToSerializedValue(mgName);
    }
}
