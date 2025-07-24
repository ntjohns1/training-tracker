package com.noslen.training_tracker.mapper.exercise;

import com.noslen.training_tracker.dto.exercise.ExercisePayload;
import com.noslen.training_tracker.enums.ExerciseType;
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
     * Convert ExercisePayload to Exercise entity
     */
    public Exercise toEntity(ExercisePayload payload) {
        if (payload == null) {
            return null;
        }

        return Exercise.builder()
                .id(payload.id())
                .name(payload.name())
                .muscleGroupId(payload.muscleGroupId())
                .youtubeId(payload.youtubeId())
                .exerciseType(stringToExerciseType(payload.exerciseType()))
                .userId(payload.userId())
                .createdAt(payload.createdAt())
                .updatedAt(payload.updatedAt())
                .deletedAt(payload.deletedAt())
                .mgSubType(payload.mgSubType())
                // Convert nested ExerciseNotePayloads to ExerciseNote entities
                .notes(payload.notes() != null ?
                               payload.notes()
                                       .stream()
                                       .map(exerciseNoteMapper::toEntity)
                                       .collect(Collectors.toList()) : null)
                .build();
    }

    /**
     * Convert Exercise entity to ExercisePayload
     */
    public ExercisePayload toPayload(Exercise entity) {
        if (entity == null) {
            return null;
        }

        return new ExercisePayload(
                entity.getId(),
                entity.getName(),
                entity.getMuscleGroupId(),
                entity.getYoutubeId(),
                exerciseTypeToString(entity.getExerciseType()),
                entity.getUserId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt(),
                entity.getMgSubType(),
                // Convert nested ExerciseNote entities to ExerciseNotePayloads
                exerciseNoteMapper.toPayloadList(entity.getNotes())
        );
    }

    /**
     * Update existing Exercise entity with data from ExercisePayload
     * Only updates mutable fields that have setters
     */
    public void updateEntity(Exercise existing, ExercisePayload payload) {
        if (existing == null || payload == null) {
            return;
        }

        // Update fields that have setters (mutable fields)
        if (payload.name() != null) {
            existing.setName(payload.name());
        }
        if (payload.muscleGroupId() != null && !payload.muscleGroupId()
                .equals(0L)) {
            existing.setMuscleGroupId(payload.muscleGroupId());
        }
        if (payload.youtubeId() != null) {
            existing.setYoutubeId(payload.youtubeId());
        }
        if (payload.exerciseType() != null) {
            existing.setExerciseType(stringToExerciseType(payload.exerciseType()));
        }
        if (payload.userId() != null && !payload.userId()
                .equals(0L)) {
            existing.setUserId(payload.userId());
        }
        if (payload.mgSubType() != null) {
            existing.setMgSubType(payload.mgSubType());
        }
        if (payload.createdAt() != null) {
            existing.setCreatedAt(payload.createdAt());
        }
        if (payload.updatedAt() != null) {
            existing.setUpdatedAt(payload.updatedAt());
        }
        if (payload.deletedAt() != null) {
            existing.setDeletedAt(payload.deletedAt());
        }
        if (payload.notes() != null) {
            existing.setNotes(payload.notes()
                                      .stream()
                                      .map(exerciseNoteMapper::toEntity)
                                      .collect(Collectors.toList()));
        }
    }

    /**
     * Create a new Exercise entity by merging existing entity with payload data
     * This handles immutable fields by creating a new entity
     */
    public Exercise mergeEntity(Exercise existing, ExercisePayload payload) {
        if (payload == null) {
            return existing;
        }
        if (existing == null) {
            return toEntity(payload);
        }

        return Exercise.builder()
                .id(existing.getId()) // Keep existing ID
                .name(payload.name() != null ? payload.name() : existing.getName())
                .muscleGroupId(payload.muscleGroupId() != null && !payload.muscleGroupId()
                        .equals(0L) ?
                                       payload.muscleGroupId() : existing.getMuscleGroupId())
                .youtubeId(payload.youtubeId() != null ? payload.youtubeId() : existing.getYoutubeId())
                .exerciseType(payload.exerciseType() != null ? stringToExerciseType(payload.exerciseType()) :
                                      existing.getExerciseType())
                .userId(payload.userId() != null && !payload.userId()
                        .equals(0L) ?
                                payload.userId() : existing.getUserId())
                .createdAt(existing.getCreatedAt()) // Keep existing creation time
                .updatedAt(payload.updatedAt() != null ? payload.updatedAt() : existing.getUpdatedAt())
                .deletedAt(payload.deletedAt() != null ? payload.deletedAt() : existing.getDeletedAt())
                .mgSubType(payload.mgSubType() != null ? payload.mgSubType() : existing.getMgSubType())
                .notes(payload.notes() != null ?
                               payload.notes()
                                       .stream()
                                       .map(exerciseNoteMapper::toEntity)
                                       .collect(Collectors.toList()) : existing.getNotes())
                .build();
    }

    /**
     * Convert list of Exercise entities to list of ExercisePayloads
     */
    public List<ExercisePayload> toPayloadList(List<Exercise> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toPayload)
                .collect(Collectors.toList());
    }

    private ExerciseType stringToExerciseType(String exerciseType) {
        if (exerciseType == null) {
            return null;
        }
        try {
            return EnumConverter.stringToEnum(ExerciseType.class,
                                              exerciseType);
        } catch (IllegalArgumentException e) {
            // If that fails, try to find by the enum's getValue() method
            for (ExerciseType et : ExerciseType.values()) {
                if (et.getValue()
                        .equals(exerciseType)) {
                    return et;
                }
            }
            throw new IllegalArgumentException(e);
        }
    }

    private String exerciseTypeToString(ExerciseType exerciseType) {
        if (exerciseType == null) {
            return null;
        }
        return EnumConverter.enumToSerializedValue(exerciseType);
    }
}
