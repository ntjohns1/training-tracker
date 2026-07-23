package com.noslen.training_tracker.mapper.day;

import com.noslen.training_tracker.dto.day.response.ExerciseSetResponse;
import com.noslen.training_tracker.enums.Unit;
import com.noslen.training_tracker.model.day.ExerciseSet;
import com.noslen.training_tracker.util.EnumConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExerciseSetMapper {

    /**
     * Converts ExerciseSet entity to ExerciseSetResponse
     */
    public ExerciseSetResponse toPayload(ExerciseSet entity) {
        if (entity == null) {
            return null;
        }

        return new ExerciseSetResponse(
                entity.getId(),
                entity.getDayExerciseId(),
                entity.getPosition(),
                EnumConverter.enumToString(entity.getSetType()),
                entity.getWeight(),
                entity.getWeightTarget(),
                entity.getWeightTargetMin(),
                entity.getWeightTargetMax(),
                entity.getReps(),
                entity.getRepsTarget(),
                entity.getBodyweight(),
                unitToString(entity.getUnit()),
                entity.getCreatedAt(),
                entity.getFinishedAt(),
                EnumConverter.enumToString(entity.getStatus())
        );
    }

    /**
     * Converts a list of ExerciseSet entities to ExerciseSetPayloads
     */
    public List<ExerciseSetResponse> toPayloadList(List<ExerciseSet> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toPayload)
                .collect(Collectors.toList());
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
