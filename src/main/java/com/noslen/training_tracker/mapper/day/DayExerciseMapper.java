package com.noslen.training_tracker.mapper.day;

import com.noslen.training_tracker.dto.day.response.DayExerciseResponse;
import com.noslen.training_tracker.dto.day.response.ExerciseSetResponse;
import com.noslen.training_tracker.model.day.DayExercise;
import com.noslen.training_tracker.util.EnumConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DayExerciseMapper {

    @Autowired
    private ExerciseSetMapper exerciseSetMapper;

    /**
     * Converts DayExercise entity to DayExerciseResponse
     */
    public DayExerciseResponse toPayload(DayExercise entity) {
        if (entity == null) {
            return null;
        }

        List<ExerciseSetResponse> setPayloads = null;
        if (entity.getSets() != null) {
            setPayloads = entity.getSets().stream()
                    .map(exerciseSetMapper::toPayload)
                    .collect(Collectors.toList());
        }

        return new DayExerciseResponse(
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
                EnumConverter.enumToString(entity.getStatus())
        );
    }

    /**
     * Converts a list of DayExercise entities to DayExercisePayloads
     */
    public List<DayExerciseResponse> toPayloadList(List<DayExercise> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toPayload)
                .collect(Collectors.toList());
    }
}
