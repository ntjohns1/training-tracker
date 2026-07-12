package com.noslen.training_tracker.mapper.day;

import com.noslen.training_tracker.dto.day.response.DayMuscleGroupResponse;
import com.noslen.training_tracker.model.day.DayMuscleGroup;
import com.noslen.training_tracker.util.EnumConverter;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * POJO Mapper for converting between DayMuscleGroup entity and DayMuscleGroupResponse DTO.
 * Handles manual mapping logic and supports immutable model fields.
 */
@Component
public class DayMuscleGroupMapper {

    /**
     * Converts DayMuscleGroup entity to DayMuscleGroupResponse DTO.
     *
     * @param entity the DayMuscleGroup entity to convert
     * @return the converted DayMuscleGroupResponse DTO, or null if entity is null
     */
    public DayMuscleGroupResponse toPayload(DayMuscleGroup entity) {
        if (entity == null) {
            return null;
        }

        return new DayMuscleGroupResponse(
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
     * Converts a list of DayMuscleGroup entities to a list of DayMuscleGroupResponse DTOs.
     *
     * @param entities the list of DayMuscleGroup entities to convert
     * @return the converted list of DayMuscleGroupResponse DTOs, or null if input is null
     */
    public List<DayMuscleGroupResponse> toPayloadList(List<DayMuscleGroup> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toPayload)
                .collect(Collectors.toList());
    }
}
