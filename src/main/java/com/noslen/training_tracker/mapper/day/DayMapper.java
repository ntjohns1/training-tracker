package com.noslen.training_tracker.mapper.day;

import com.noslen.training_tracker.dto.day.response.DayResponse;
import com.noslen.training_tracker.enums.Unit;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.util.EnumConverter;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * POJO Mapper for converting between Day entity and DayResponse DTO.
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
     * Converts DayResponse DTO to Day entity.
     * Note: Mesocycle relationship and nested collections are not set by this mapper 
     * and should be handled by the service layer.
     *
     * @param payload the DayResponse to convert
     * @return the converted Day entity, or null if payload is null
     */
    public Day toEntity(DayResponse payload) {
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
                .unit(stringToUnit(payload.unit()))
                .finishedAt(payload.finishedAt())
                .label(payload.label())
                // Note: Nested collections (notes, exercises, muscleGroups) are handled by service layer
                // to avoid circular dependencies and maintain proper entity relationships
                .build();
    }

    /**
     * Converts Day entity to DayResponse DTO including nested collections.
     *
     * @param entity the Day entity to convert
     * @return the converted DayResponse DTO, or null if entity is null
     */
    public DayResponse toPayload(Day entity) {
        if (entity == null) {
            return null;
        }

        return new DayResponse(
                entity.getId(),
                entity.getMesoId(), // Uses the derived property from entity
                entity.getWeek() != null ? entity.getWeek().longValue() : null, // Convert Integer to Long
                entity.getPosition() != null ? entity.getPosition().longValue() : null, // Convert Integer to Long
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getBodyweight() != null ? entity.getBodyweight().intValue() : null, // Convert Double to Integer
                entity.getBodyweightAt(),
                unitToString(entity.getUnit()),
                entity.getFinishedAt(),
                entity.getLabel(),
                dayNoteMapper.toPayloadList(entity.getNotes()),
                dayExerciseMapper.toPayloadList(entity.getExercises()),
                dayMuscleGroupMapper.toPayloadList(entity.getMuscleGroups()),
                determineStatus(entity) // Calculate status based on entity state
        );
    }

    /**
     * Converts a list of Day entities to a list of DayResponse DTOs.
     *
     * @param entities the list of Day entities to convert
     * @return the converted list of DayResponse DTOs, or null if input is null
     */
    public List<DayResponse> toPayloadList(List<Day> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toPayload)
                .collect(Collectors.toList());
    }

    /**
     * Converts a list of DayResponse DTOs to a list of Day entities.
     *
     * @param payloads the list of DayResponse DTOs to convert
     * @return the converted list of Day entities, or null if input is null
     */


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
            return "complete";
        } else if (entity.getExercises() != null && !entity.getExercises().isEmpty()) {
            return "in_progress";
        } else {
            return "planned";
        }
    }


    public Unit stringToUnit(String unit) {
        if (unit == null) {
            return null;
        }
        try {
            return EnumConverter.stringToEnum(Unit.class, unit);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid unit string: " + unit);
        }
    }

    public String unitToString(Unit unit) {
        if (unit == null) {
            return null;
        }
        return EnumConverter.enumToSerializedValue(unit);
    }   
    
}
