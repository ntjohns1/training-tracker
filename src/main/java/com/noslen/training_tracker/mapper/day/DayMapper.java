package com.noslen.training_tracker.mapper.day;

import com.noslen.training_tracker.dto.day.DayPayload;
import com.noslen.training_tracker.model.day.Day;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * POJO Mapper for converting between Day entity and DayPayload DTO.
 * Handles manual mapping logic including nested relationships and supports immutable model fields.
 */
@Component
public class DayMapper {

    private final DayNoteMapper dayNoteMapper;
    private final DayExerciseMapper dayExerciseMapper;
    private final DayMuscleGroupMapper dayMuscleGroupMapper;

    public DayMapper(DayNoteMapper dayNoteMapper, DayExerciseMapper dayExerciseMapper, 
                     DayMuscleGroupMapper dayMuscleGroupMapper) {
        this.dayNoteMapper = dayNoteMapper;
        this.dayExerciseMapper = dayExerciseMapper;
        this.dayMuscleGroupMapper = dayMuscleGroupMapper;
    }

    /**
     * Converts DayPayload DTO to Day entity.
     * Note: Mesocycle relationship and nested collections are not set by this mapper 
     * and should be handled by the service layer.
     *
     * @param payload the DayPayload to convert
     * @return the converted Day entity, or null if payload is null
     */
    public Day toEntity(DayPayload payload) {
        if (payload == null) {
            return null;
        }

        return Day.builder()
                .id(payload.id())
                .week(payload.week() != null ? payload.week().intValue() : null) // Convert Long to Integer
                .position(payload.position() != null ? payload.position().intValue() : null) // Convert Long to Integer
                .createdAt(payload.createdAt())
                .updatedAt(payload.updatedAt())
                .bodyweight(payload.bodyweight() != null ? payload.bodyweight().doubleValue() : null) // Convert Integer to Double
                .bodyweightAt(payload.bodyweightAt())
                .unit(payload.unit())
                .finishedAt(payload.finishedAt())
                .label(payload.label())
                // Note: Nested collections (notes, exercises, muscleGroups) are handled by service layer
                // to avoid circular dependencies and maintain proper entity relationships
                .build();
    }

    /**
     * Converts Day entity to DayPayload DTO including nested collections.
     *
     * @param entity the Day entity to convert
     * @return the converted DayPayload DTO, or null if entity is null
     */
    public DayPayload toPayload(Day entity) {
        if (entity == null) {
            return null;
        }

        return new DayPayload(
                entity.getId(),
                entity.getMesoId(), // Uses the derived property from entity
                entity.getWeek() != null ? entity.getWeek().longValue() : null, // Convert Integer to Long
                entity.getPosition() != null ? entity.getPosition().longValue() : null, // Convert Integer to Long
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getBodyweight() != null ? entity.getBodyweight().intValue() : null, // Convert Double to Integer
                entity.getBodyweightAt(),
                entity.getUnit(),
                entity.getFinishedAt(),
                entity.getLabel(),
                dayNoteMapper.toPayloadList(entity.getNotes()),
                dayExerciseMapper.toPayloadList(entity.getExercises()),
                dayMuscleGroupMapper.toPayloadList(entity.getMuscleGroups()),
                determineStatus(entity) // Calculate status based on entity state
        );
    }

    /**
     * Updates mutable fields of an existing Day entity from DayPayload.
     * Only updates fields that can be modified after creation.
     *
     * @param entity  the existing Day entity to update
     * @param payload the DayPayload containing update data
     */
    public void updateEntity(Day entity, DayPayload payload) {
        if (entity == null || payload == null) {
            return;
        }

        // Update mutable timestamp fields using available setters
        if (payload.updatedAt() != null) {
            entity.setUpdatedAt(payload.updatedAt());
        }
        
        // Note: Other fields are immutable and would require using mergeEntity method
        // or handling at the service layer with entity rebuilding
    }

    /**
     * Creates a new Day entity by merging existing entity with payload updates.
     * Used when immutable fields need to be changed.
     *
     * @param existing the existing Day entity
     * @param payload  the DayPayload containing update data
     * @return a new Day entity with merged data
     */
    public Day mergeEntity(Day existing, DayPayload payload) {
        if (existing == null) {
            return toEntity(payload);
        }
        if (payload == null) {
            return existing;
        }

        return Day.builder()
                .id(existing.getId()) // Always preserve existing ID
                .mesocycle(existing.getMesocycle()) // Preserve relationship
                .week(payload.week() != null ? payload.week().intValue() : existing.getWeek())
                .position(payload.position() != null ? payload.position().intValue() : existing.getPosition())
                .createdAt(existing.getCreatedAt()) // Preserve creation timestamp
                .updatedAt(payload.updatedAt() != null ? payload.updatedAt() : existing.getUpdatedAt())
                .bodyweight(payload.bodyweight() != null ? payload.bodyweight().doubleValue() : existing.getBodyweight())
                .bodyweightAt(payload.bodyweightAt() != null ? payload.bodyweightAt() : existing.getBodyweightAt())
                .unit(payload.unit() != null ? payload.unit() : existing.getUnit())
                .finishedAt(payload.finishedAt() != null ? payload.finishedAt() : existing.getFinishedAt())
                .label(payload.label() != null ? payload.label() : existing.getLabel())
                .notes(existing.getNotes()) // Preserve existing collections
                .exercises(existing.getExercises()) // Preserve existing collections
                .muscleGroups(existing.getMuscleGroups()) // Preserve existing collections
                .build();
    }

    /**
     * Converts a list of Day entities to a list of DayPayload DTOs.
     *
     * @param entities the list of Day entities to convert
     * @return the converted list of DayPayload DTOs, or null if input is null
     */
    public List<DayPayload> toPayloadList(List<Day> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toPayload)
                .collect(Collectors.toList());
    }

    /**
     * Converts a list of DayPayload DTOs to a list of Day entities.
     *
     * @param payloads the list of DayPayload DTOs to convert
     * @return the converted list of Day entities, or null if input is null
     */
    public List<Day> toEntityList(List<DayPayload> payloads) {
        if (payloads == null) {
            return null;
        }

        return payloads.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * Determines the status of a Day based on its current state.
     * This is a business logic method that calculates status from entity properties.
     *
     * @param entity the Day entity
     * @return the calculated status string
     */
    private String determineStatus(Day entity) {
        if (entity == null) {
            return null;
        }
        
        if (entity.getFinishedAt() != null) {
            return "completed";
        } else if (entity.getExercises() != null && !entity.getExercises().isEmpty()) {
            return "in_progress";
        } else {
            return "planned";
        }
    }
}
