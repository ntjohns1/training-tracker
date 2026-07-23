package com.noslen.training_tracker.mapper.exercise;

import com.noslen.training_tracker.dto.exercise.response.ExerciseResponse;
import com.noslen.training_tracker.enums.ExerciseType;
import com.noslen.training_tracker.enums.MgSubType;
import com.noslen.training_tracker.model.exercise.Exercise;
import com.noslen.training_tracker.util.EnumConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExerciseMapper {

    private final ExerciseNoteMapper exerciseNoteMapper;

    public ExerciseMapper(ExerciseNoteMapper exerciseNoteMapper) {
        this.exerciseNoteMapper = exerciseNoteMapper;
    }

    /**
     * Convert Exercise entity to ExerciseResponse
     */
    public ExerciseResponse toPayload(Exercise entity) {
        if (entity == null) {
            return null;
        }

        return new ExerciseResponse(
                entity.getId(),
                entity.getName(),
                entity.getMuscleGroupId(),
                entity.getYoutubeId(),
                exerciseTypeToString(entity.getExerciseType()),
                entity.getUserId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt(),
                mgSubTypeToString(entity.getMgSubType()),
                // Convert nested ExerciseNote entities to ExerciseNotePayloads
                exerciseNoteMapper.toPayloadList(entity.getNotes())
        );
    }

    /**
     * Convert list of Exercise entities to list of ExercisePayloads
     */
    public List<ExerciseResponse> toPayloadList(List<Exercise> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toPayload)
                .collect(Collectors.toList());
    }

    private String exerciseTypeToString(ExerciseType exerciseType) {
        if (exerciseType == null) {
            return null;
        }
        return EnumConverter.enumToSerializedValue(exerciseType);
    }

    private String mgSubTypeToString(MgSubType mgSubType) {
        if (mgSubType == null) {
            return null;
        }
        return EnumConverter.enumToSerializedValue(mgSubType);
    }
}
