package com.noslen.training_tracker.mapper.day;

import com.noslen.training_tracker.dto.day.DayMuscleGroupPayload;
import com.noslen.training_tracker.enums.Status;
import com.noslen.training_tracker.model.day.DayMuscleGroup;
import com.noslen.training_tracker.util.EnumConverter;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * POJO Mapper for converting between DayMuscleGroup entity and DayMuscleGroupPayload DTO.
 * Handles manual mapping logic and supports immutable model fields.
 */
@Component
public class DayMuscleGroupMapper {

    /**
     * Converts DayMuscleGroupPayload DTO to DayMuscleGroup entity.
     * Note: Day and MuscleGroup relationships are not set by this mapper and should be handled by the service layer.
     *
     * @param payload the DayMuscleGroupPayload to convert
     * @return the converted DayMuscleGroup entity, or null if payload is null
     */
    public DayMuscleGroup toEntity(DayMuscleGroupPayload payload) {
        if (payload == null) {
            return null;
        }

        return DayMuscleGroup.builder()
                .id(payload.id())
                .pump(payload.pump())
                .soreness(payload.soreness())
                .workload(payload.workload())
                .recommendedSets(payload.recommendedSets())
                .status(EnumConverter.stringToEnum(Status.class, payload.status()))
                .createdAt(payload.createdAt())
                .updatedAt(payload.updatedAt())
                .build();
    }

    /**
     * Converts DayMuscleGroup entity to DayMuscleGroupPayload DTO.
     *
     * @param entity the DayMuscleGroup entity to convert
     * @return the converted DayMuscleGroupPayload DTO, or null if entity is null
     */
    public DayMuscleGroupPayload toPayload(DayMuscleGroup entity) {
        if (entity == null) {
            return null;
        }

        return new DayMuscleGroupPayload(
                entity.getId(),
                entity.getDayId(), // Uses the derived property from entity
                entity.getMuscleGroupId(), // Uses the derived property from entity
                entity.getPump(),
                entity.getSoreness(),
                entity.getWorkload(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getRecommendedSets(),
                EnumConverter.enumToString(entity.getStatus())
        );
    }

    /**
     * Updates mutable fields of an existing DayMuscleGroup entity from DayMuscleGroupPayload.
     * Only updates fields that can be modified after creation.
     *
     * @param entity  the existing DayMuscleGroup entity to update
     * @param payload the DayMuscleGroupPayload containing update data
     */
    public void updateEntity(DayMuscleGroup entity, DayMuscleGroupPayload payload) {
        if (entity == null || payload == null) {
            return;
        }

        // Note: DayMuscleGroup appears to be mostly immutable based on the entity structure
        // Only updating fields that might be mutable
        // Since the entity doesn't have setters, this method may need to be implemented
        // differently or the entity may need setter methods for mutable fields
    }

    /**
     * Creates a new DayMuscleGroup entity by merging existing entity with payload updates.
     * Used when immutable fields need to be changed.
     *
     * @param existing the existing DayMuscleGroup entity
     * @param payload  the DayMuscleGroupPayload containing update data
     * @return a new DayMuscleGroup entity with merged data
     */
    public DayMuscleGroup mergeEntity(DayMuscleGroup existing, DayMuscleGroupPayload payload) {
        if (existing == null) {
            return toEntity(payload);
        }
        if (payload == null) {
            return existing;
        }

        return DayMuscleGroup.builder()
                .id(existing.getId()) // Always preserve existing ID
                .day(existing.getDay()) // Preserve relationship
                .muscleGroup(existing.getMuscleGroup()) // Preserve relationship
                .pump(payload.pump() != null ? payload.pump() : existing.getPump())
                .soreness(payload.soreness() != null ? payload.soreness() : existing.getSoreness())
                .workload(payload.workload() != null ? payload.workload() : existing.getWorkload())
                .recommendedSets(payload.recommendedSets() != null ? payload.recommendedSets() : existing.getRecommendedSets())
                .status(payload.status() != null ? EnumConverter.stringToEnum(Status.class, payload.status()) : existing.getStatus())
                .createdAt(existing.getCreatedAt()) // Preserve creation timestamp
                .updatedAt(payload.updatedAt() != null ? payload.updatedAt() : existing.getUpdatedAt())
                .build();
    }

    /**
     * Converts a list of DayMuscleGroup entities to a list of DayMuscleGroupPayload DTOs.
     *
     * @param entities the list of DayMuscleGroup entities to convert
     * @return the converted list of DayMuscleGroupPayload DTOs, or null if input is null
     */
    public List<DayMuscleGroupPayload> toPayloadList(List<DayMuscleGroup> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toPayload)
                .collect(Collectors.toList());
    }

    /**
     * Converts a list of DayMuscleGroupPayload DTOs to a list of DayMuscleGroup entities.
     *
     * @param payloads the list of DayMuscleGroupPayload DTOs to convert
     * @return the converted list of DayMuscleGroup entities, or null if input is null
     */
    public List<DayMuscleGroup> toEntityList(List<DayMuscleGroupPayload> payloads) {
        if (payloads == null) {
            return null;
        }

        return payloads.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
